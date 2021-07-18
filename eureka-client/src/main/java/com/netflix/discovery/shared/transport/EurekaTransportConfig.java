package com.netflix.discovery.shared.transport;

/**
 * 管理与传输层有关的配置的配置类
 *
 * Config class that governs configurations relevant to the transport layer
 *
 * @author David Liu
 */
public interface EurekaTransportConfig {

    /**
     * 用于会话客户的重新连接的时间间隔，  默认20*60
     *
     * @return the reconnect inverval to use for sessioned clients
     */
    int getSessionedClientReconnectIntervalSeconds();

    /**
     * 在[0, 1.0]的范围内，超过该隔离集被清除的全部端点集的百分比。 0.66
     *
     * @return the percentage of the full endpoints set above which the quarantine set is cleared in the range [0, 1.0]
     */
    double getRetryableClientQuarantineRefreshPercentage();

    /**
     * @return the max staleness threshold tolerated by the applications resolver
     */
    int getApplicationsResolverDataStalenessThresholdSeconds();

    /**
     * By default, the applications resolver extracts the public hostname from internal InstanceInfos for resolutions.
     * Set this to true to change this behaviour to use ip addresses instead (private ip if ip type can be determined).
     *
     * @return false by default
     */
    boolean applicationsResolverUseIp();

    /**
     * 轮询异步解析器的时间间隔。  默认5分钟
     *
     * @return the interval to poll for the async resolver.
     */
    int getAsyncResolverRefreshIntervalMs();

    /**
     * @return the async refresh timeout threshold in ms.
     */
    int getAsyncResolverWarmUpTimeoutMs();

    /**
     * @return the max threadpool size for the async resolver's executor
     */
    int getAsyncExecutorThreadPoolSize();

    /**
     * The remote vipAddress of the primary eureka cluster to register with.
     *
     * @return the vipAddress for the write cluster to register with
     */
    String getWriteClusterVip();

    /**
     * The remote vipAddress of the eureka cluster (either the primaries or a readonly replica) to fetch registry
     * data from.
     *
     * @return the vipAddress for the readonly cluster to redirect to, if applicable (can be the same as the bootstrap)
     */
    String getReadClusterVip();

    /**
     * 实际Debug发现为空
     * 可以用来指定不同的引导解决策略。目前支持的策略是。
     * - default 默认（如果没有匹配）：从dns txt记录或静态配置主机名引导
     * - composite 复合：如果数据可用且warm(见getApplicationsResolverDataStalenessThresholdSeconds())，从本地注册表引导，否则返回到后备默认值
     *
     * Can be used to specify different bootstrap resolve strategies. Current supported strategies are:
     *  - default (if no match): bootstrap from dns txt records or static config hostnames
     *  - composite: bootstrap from local registry if data is available
     *    and warm (see {@link #getApplicationsResolverDataStalenessThresholdSeconds()}, otherwise
     *    fall back to a backing default
     *
     * @return null for the default strategy, by default
     */
    String getBootstrapResolverStrategy();

    /**
     * By default, the transport uses the same (bootstrap) resolver for queries.
     *
     * Set this property to false to use an indirect resolver to resolve query targets
     * via {@link #getReadClusterVip()}. This indirect resolver may or may not return the same
     * targets as the bootstrap servers depending on how servers are setup.
     *
     * @return true by default.
     */
    boolean useBootstrapResolverForQuery();
}
