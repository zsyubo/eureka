package com.netflix.eureka.registry;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author David Liu
 */
public interface ResponseCache {

    void invalidate(String appName, @Nullable String vipAddress, @Nullable String secureVipAddress);

    AtomicLong getVersionDelta();

    AtomicLong getVersionDeltaWithRegions();

    /**
     * 获取关于应用程序的缓存信息。
     * 如果缓存的信息不可用，它将在第一次请求时生成。在第一次请求之后，<p>该信息再由一个后台线程定期更新。</p>
     *
     * Get the cached information about applications.
     *
     * <p>
     * If the cached information is not available it is generated on the first
     * request. After the first request, the information is then updated
     * periodically by a background thread.
     * </p>
     *
     * @param key the key for which the cached information needs to be obtained.
     * @return payload which contains information about the applications.
     */
     String get(Key key);

    /**
     * 获得有关应用程序的压缩信息。
     *
     * Get the compressed information about the applications.
     *
     * @param key the key for which the compressed cached information needs to be obtained.
     * @return compressed payload which contains information about the applications.
     */
    byte[] getGZIP(Key key);

    /**
     * 通过停止内部线程和取消注册Servo监控器来执行该缓存的关闭。
     *
     * Performs a shutdown of this cache by stopping internal threads and unregistering
     * Servo monitors.
     */
    void stop();
}
