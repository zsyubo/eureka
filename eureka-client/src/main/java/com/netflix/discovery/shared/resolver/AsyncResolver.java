package com.netflix.discovery.shared.resolver;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.discovery.TimedSupervisorTask;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.netflix.discovery.EurekaClientNames.METRIC_RESOLVER_PREFIX;

/**
 * 一个异步解析器，它为获取保持一个缓存版本的端点列表值，并在不同的线程中定期更新这个缓存。
 *
 * An async resolver that keeps a cached version of the endpoint list value for gets, and updates this cache
 * periodically in a different thread.
 *
 * @author David Liu
 */
public class AsyncResolver<T extends EurekaEndpoint> implements ClosableResolver<T> {
    private static final Logger logger = LoggerFactory.getLogger(AsyncResolver.class);

    // Note that warm up is best effort. If the resolver is accessed by multiple threads pre warmup,
    // only the first thread will block for the warmup (up to the configurable timeout).
    private final AtomicBoolean warmedUp = new AtomicBoolean(false);
    private final AtomicBoolean scheduled = new AtomicBoolean(false);

    private final String name;    // a name for metric purposes
    private final ClusterResolver<T> delegate;
    private final ScheduledExecutorService executorService;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final TimedSupervisorTask backgroundTask;
    private final AtomicReference<List<T>> resultsRef;

    private final int refreshIntervalMs; // 默认5分钟
    private final int warmUpTimeoutMs;

    // Metric timestamp, tracking last time when data were effectively changed.
    private volatile long lastLoadTimestamp = -1;

    /**
     * Create an async resolver with an empty initial value. When this resolver is called for the first time,
     * an initial warm up will be executed before scheduling the periodic update task.
     */
    public AsyncResolver(String name,
                         ClusterResolver<T> delegate,
                         int executorThreadPoolSize,
                         int refreshIntervalMs,
                         int warmUpTimeoutMs) {
        this(
                name,
                delegate,
                Collections.<T>emptyList(),
                executorThreadPoolSize,
                refreshIntervalMs,
                warmUpTimeoutMs
        );
    }

    /**
     * 创建一个具有预设初始值的异步解析器。当这个解析器第一次被调用时，将不会有预热，初始值将被返回。定期更新任务将在第一次调用getClusterEndpoints后才会被安排。
     *
     * Create an async resolver with a preset initial value. WHen this resolver is called for the first time,
     * there will be no warm up and the initial value will be returned. The periodic update task will not be
     * scheduled until after the first time getClusterEndpoints call.
     */
    public AsyncResolver(String name,// bootstrap
                         ClusterResolver<T> delegate, // ZoneAffinityClusterResolver
                         List<T> initialValues, //  集群eureka server地址
                         int executorThreadPoolSize, // 1
                         int refreshIntervalMs) {
        this(
                name,
                delegate,
                initialValues,
                executorThreadPoolSize,
                refreshIntervalMs,
                0
        );

        warmedUp.set(true);
    }

    /**
     * 这里面有好几个task
     * @param delegate the delegate resolver to async resolve from
     * @param initialValue the initial value to use
     * @param executorThreadPoolSize the max number of threads for the threadpool
     * @param refreshIntervalMs the async refresh interval
     * @param warmUpTimeoutMs the time to wait for the initial warm up
     */
    AsyncResolver(String name,
                  ClusterResolver<T> delegate,
                  List<T> initialValue,
                  int executorThreadPoolSize,
                  int refreshIntervalMs,
                  int warmUpTimeoutMs) {
        this.name = name;
        this.delegate = delegate;
        this.refreshIntervalMs = refreshIntervalMs;
        this.warmUpTimeoutMs = warmUpTimeoutMs;

        this.executorService = Executors.newScheduledThreadPool(1,
                new ThreadFactoryBuilder()
                        .setNameFormat("AsyncResolver-" + name + "-%d")
                        .setDaemon(true)
                        .build());

        this.threadPoolExecutor = new ThreadPoolExecutor(
                1, executorThreadPoolSize, 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),  // use direct handoff
                new ThreadFactoryBuilder()
                        .setNameFormat("AsyncResolver-" + name + "-executor-%d")
                        .setDaemon(true)
                        .build()
        );

        // 后台线程
        this.backgroundTask = new TimedSupervisorTask(
                this.getClass().getSimpleName(),
                executorService,
                threadPoolExecutor,
                refreshIntervalMs,
                TimeUnit.MILLISECONDS,
                5,
                updateTask
        );

        this.resultsRef = new AtomicReference<>(initialValue);
        Monitors.registerObject(name, this);
    }

    @Override
    public void shutdown() {
        if(Monitors.isObjectRegistered(name, this)) {
            Monitors.unregisterObject(name, this);
        }
        executorService.shutdownNow();
        threadPoolExecutor.shutdownNow();
        backgroundTask.cancel();
    }


    @Override
    public String getRegion() {
        return delegate.getRegion();
    }

    @Override
    public List<T> getClusterEndpoints() {
        long delay = refreshIntervalMs;
        // warmedUp 在构造方法实例化时就设置为true了
        if (warmedUp.compareAndSet(false, true)) {
            if (!doWarmUp()) {
                delay = 0;
            }
        }
        if (scheduled.compareAndSet(false, true)) {
            scheduleTask(delay); // 属实疑惑，不知道为撒这样写
        }
        return resultsRef.get();
    }


    /* visible for testing */ boolean doWarmUp() {
        Future future = null;
        try {
            future = threadPoolExecutor.submit(updateTask);
            future.get(warmUpTimeoutMs, TimeUnit.MILLISECONDS);  // block until done or timeout
            return true;
        } catch (Exception e) {
            logger.warn("Best effort warm up failed", e);
        } finally {
            if (future != null) {
                future.cancel(true);
            }
        }
        return false;
    }

    /* visible for testing */ void scheduleTask(long delay) {
        executorService.schedule(
                backgroundTask, delay, TimeUnit.MILLISECONDS);
    }

    @Monitor(name = METRIC_RESOLVER_PREFIX + "lastLoadTimestamp",
            description = "How much time has passed from last successful async load", type = DataSourceType.GAUGE)
    public long getLastLoadTimestamp() {
        return lastLoadTimestamp < 0 ? 0 : System.currentTimeMillis() - lastLoadTimestamp;
    }

    @Monitor(name = METRIC_RESOLVER_PREFIX + "endpointsSize",
            description = "How many records are the in the endpoints ref", type = DataSourceType.GAUGE)
    public long getEndpointsSize() {
        return resultsRef.get().size();  // return directly from the ref and not the method so as to not trigger warming
    }

    /**
     * 一次性task？
     */
    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            try {
                // 获取eureka server列表
                List<T> newList = delegate.getClusterEndpoints();
                if (newList != null) {
                    resultsRef.getAndSet(newList);
                    lastLoadTimestamp = System.currentTimeMillis();
                } else {
                    logger.warn("Delegate returned null list of cluster endpoints");
                }
                logger.debug("Resolved to {}", newList);
            } catch (Exception e) {
                logger.warn("Failed to retrieve cluster endpoints from the delegate", e);
            }
        }
    };
}
