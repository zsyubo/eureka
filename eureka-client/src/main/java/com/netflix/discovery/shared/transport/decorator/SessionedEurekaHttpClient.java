/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.discovery.shared.transport.decorator;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpClientFactory;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.TransportUtils;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.netflix.discovery.EurekaClientNames.METRIC_TRANSPORT_PREFIX;

/**
 * 看了下代码，这个HttpClient
 *
 * SessionedEurekaHttpClient在一个固定的时间间隔（会话）强制执行完全的重新连接，防止客户端永远停留在一个特定的Eureka服务器实例。这反过来又保证了在集群拓扑结构发生变化时负载的均匀分布。
 *
 * {@link SessionedEurekaHttpClient} enforces full reconnect at a regular interval (a session), preventing
 * a client to sticking to a particular Eureka server instance forever. This in turn guarantees even
 * load distribution in case of cluster topology change.
 *
 * @author Tomasz Bak
 */
public class SessionedEurekaHttpClient extends EurekaHttpClientDecorator {
    private static final Logger logger = LoggerFactory.getLogger(SessionedEurekaHttpClient.class);

    private final Random random = new Random();

    private final String name;
    private final EurekaHttpClientFactory clientFactory; // 还嵌套一个
    private final long sessionDurationMs;  // session过期时间配置，默认是1200秒
    private volatile long currentSessionDurationMs; // 当前session过期时间

    private volatile long lastReconnectTimeStamp = -1;
    private final AtomicReference<EurekaHttpClient> eurekaHttpClientRef = new AtomicReference<>();

    public SessionedEurekaHttpClient(String name, EurekaHttpClientFactory clientFactory, long sessionDurationMs) {
        this.name = name;
        this.clientFactory = clientFactory;// RetryableEurekaHttpClient.createFactory
        this.sessionDurationMs = sessionDurationMs;// 1200*1000
        this.currentSessionDurationMs = randomizeSessionDuration(sessionDurationMs);// 计算一个随机值？ 难道是session过期时间？
        Monitors.registerObject(name, this);
    }

    @Override
    protected <R> EurekaHttpResponse<R> execute(RequestExecutor<R> requestExecutor) {
        long now = System.currentTimeMillis();
        long delay = now - lastReconnectTimeStamp; // 计算时间间隔
        if (delay >= currentSessionDurationMs) { // 如果大于session过期时间
            logger.debug("Ending a session and starting anew");
            lastReconnectTimeStamp = now;
            currentSessionDurationMs = randomizeSessionDuration(sessionDurationMs); // 起算下一次session过期时间
            TransportUtils.shutdown(eurekaHttpClientRef.getAndSet(null));
        }

        EurekaHttpClient eurekaHttpClient = eurekaHttpClientRef.get();  // RetryableEurekaHttpClient
        if (eurekaHttpClient == null) {
            eurekaHttpClient = TransportUtils.getOrSetAnotherClient(eurekaHttpClientRef, clientFactory.newClient());  // RetryableEurekaHttpClient
        }
        return requestExecutor.execute(eurekaHttpClient);
    }

    @Override
    public void shutdown() {
        if(Monitors.isObjectRegistered(name, this)) {
            Monitors.unregisterObject(name, this);
        }
        TransportUtils.shutdown(eurekaHttpClientRef.getAndSet(null));
    }

    /**
     * 一个随机的sessionDuration，以ms为单位，在[0, sessionDurationMs/2]中计算+/-一个额外的量。
     * @return a randomized sessionDuration in ms calculated as +/- an additional amount in [0, sessionDurationMs/2]
     */
    protected long randomizeSessionDuration(long sessionDurationMs) {
        long delta = (long) (sessionDurationMs * (random.nextDouble() - 0.5));
        return sessionDurationMs + delta;
    }

    @Monitor(name = METRIC_TRANSPORT_PREFIX + "currentSessionDuration",
            description = "Duration of the current session", type = DataSourceType.GAUGE)
    public long getCurrentSessionDuration() {
        return lastReconnectTimeStamp < 0 ? 0 : System.currentTimeMillis() - lastReconnectTimeStamp;
    }
}
