/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.discovery;

import java.util.List;

import javax.annotation.Nullable;

import com.google.inject.ImplementedBy;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;

/**
 * eureka客户端向Eureka服务器注册一个实例所需的配置信息。
 * 大部分所需信息由默认配置DefaultEurekaClientConfig提供。用户只需要提供Eureka服务器服务的URL。Eureka服务器服务的URL可以通过2种机制进行配置 1）通过在DNS中注册信息。2）通过在配置中指定它。
 * 一旦客户端被注册，用户就可以根据虚拟主机名（也叫VIPAddress）从EurekaClient中查找信息，这是最常见的方式，或者通过其他方式来获得与其他在Eureka注册的实例对话所需的信息。
 * 请注意，所有的配置在运行时都是无效的，除非和另外指定。
 *
 * Configuration information required by the eureka clients to register an
 * instance with <em>Eureka</em> server.
 *
 * <p>
 * Most of the required information is provided by the default configuration
 * {@link DefaultEurekaClientConfig}. The users just need to provide the eureka
 * server service urls. The Eureka server service urls can be configured by 2
 * mechanisms
 *
 * 1) By registering the information in the DNS. 2) By specifying it in the
 * configuration.
 * </p>
 *
 *
 * Once the client is registered, users can look up information from
 * {@link EurekaClient} based on <em>virtual hostname</em> (also called
 * VIPAddress), the most common way of doing it or by other means to get the
 * information necessary to talk to other instances registered with
 * <em>Eureka</em>.
 *
 * <p>
 * Note that all configurations are not effective at runtime unless and
 * otherwise specified.
 * </p>
 *
 * @author Karthik Ranganathan
 *
 */
@ImplementedBy(DefaultEurekaClientConfig.class)
public interface EurekaClientConfig {

    /**
     * 从eureka服务器获取注册表信息的频率（以秒为单位）。
     *
     * Indicates how often(in seconds) to fetch the registry information from
     * the eureka server.
     *
     * @return the fetch interval in seconds.
     */
    int getRegistryFetchIntervalSeconds();

    /**
     * 指示复制实例变化到eureka服务器的频率（以秒为单位）。默认30秒
     *
     * Indicates how often(in seconds) to replicate instance changes to be
     * replicated to the eureka server.
     *
     * @return the instance replication interval in seconds.
     */
    int getInstanceInfoReplicationIntervalSeconds();

    /**
     * 指示最初多久（以秒为单位）将实例信息复制到eureka服务器。默认40秒
     *
     * Indicates how long initially (in seconds) to replicate instance info
     * to the eureka server
     */
    int getInitialInstanceInfoReplicationIntervalSeconds();

    /**
     * 表示多长时间轮询一次eureka服务器信息的变化（以秒为单位）。Eureka服务器可以被添加或删除，这个设置控制了eureka客户端应该多快知道这个信息。
     *
     * Indicates how often(in seconds) to poll for changes to eureka server
     * information.
     *
     * <p>
     * Eureka servers could be added or removed and this setting controls how
     * soon the eureka clients should know about it.
     * </p>
     *
     * @return the interval to poll for eureka service url changes.
     */
    int getEurekaServiceUrlPollIntervalSeconds();

    /**
     * Gets the proxy host to eureka server if any.
     *
     * @return the proxy host.
     */
    String getProxyHost();

    /**
     * Gets the proxy port to eureka server if any.
     *
     * @return the proxy port.
     */
    String getProxyPort();

    /**
     * Gets the proxy user name if any.
     *
     * @return the proxy user name.
     */
    String getProxyUserName();

    /**
     * Gets the proxy password if any.
     *
     * @return the proxy password.
     */
    String getProxyPassword();

    /**
     * Indicates whether the content fetched from eureka server has to be
     * compressed whenever it is supported by the server. The registry
     * information from the eureka server is compressed for optimum network
     * traffic.
     *
     * @return true, if the content need to be compressed, false otherwise.
     * @deprecated gzip content encoding will be always enforced in the next minor Eureka release (see com.netflix.eureka.GzipEncodingEnforcingFilter).
     */
    boolean shouldGZipContent();

    /**
     * Indicates how long to wait (in seconds) before a read from eureka server
     * needs to timeout.
     *
     * @return time in seconds before the read should timeout.
     */
    int getEurekaServerReadTimeoutSeconds();

    /**
     * Indicates how long to wait (in seconds) before a connection to eureka
     * server needs to timeout.
     *
     * <p>
     * Note that the connections in the client are pooled by
     * {@link org.apache.http.client.HttpClient} and this setting affects the actual
     * connection creation and also the wait time to get the connection from the
     * pool.
     * </p>
     *
     * @return time in seconds before the connections should timeout.
     */
    int getEurekaServerConnectTimeoutSeconds();

    /**
     * Gets the name of the implementation which implements
     * {@link BackupRegistry} to fetch the registry information as a fall back
     * option for only the first time when the eureka client starts.
     *
     * <p>
     * This may be needed for applications which needs additional resiliency for
     * registry information without which it cannot operate.
     * </p>
     *
     * @return the class name which implements {@link BackupRegistry}.
     */
    String getBackupRegistryImpl();

    /**
     * Gets the total number of connections that is allowed from eureka client
     * to all eureka servers.
     *
     * @return total number of allowed connections from eureka client to all
     *         eureka servers.
     */
    int getEurekaServerTotalConnections();

    /**
     * Gets the total number of connections that is allowed from eureka client
     * to a eureka server host.
     *
     * @return total number of allowed connections from eureka client to a
     *         eureka server.
     */
    int getEurekaServerTotalConnectionsPerHost();

    /**
     * Gets the URL context to be used to construct the <em>service url</em> to
     * contact eureka server when the list of eureka servers come from the
     * DNS.This information is not required if the contract returns the service
     * urls by implementing {@link #getEurekaServerServiceUrls(String)}.
     *
     * <p>
     * The DNS mechanism is used when
     * {@link #shouldUseDnsForFetchingServiceUrls()} is set to <em>true</em> and
     * the eureka client expects the DNS to configured a certain way so that it
     * can fetch changing eureka servers dynamically.
     * </p>
     *
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the string indicating the context {@link java.net.URI} of the eureka
     *         server.
     */
    String getEurekaServerURLContext();

    /**
     * Gets the port to be used to construct the <em>service url</em> to contact
     * eureka server when the list of eureka servers come from the DNS.This
     * information is not required if the contract returns the service urls by
     * implementing {@link #getEurekaServerServiceUrls(String)}.
     *
     * <p>
     * The DNS mechanism is used when
     * {@link #shouldUseDnsForFetchingServiceUrls()} is set to <em>true</em> and
     * the eureka client expects the DNS to configured a certain way so that it
     * can fetch changing eureka servers dynamically.
     * </p>
     *
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the string indicating the port where the eureka server is
     *         listening.
     */
    String getEurekaServerPort();

    /**
     * Gets the DNS name to be queried to get the list of eureka servers.This
     * information is not required if the contract returns the service urls by
     * implementing {@link #getEurekaServerServiceUrls(String)}.
     *
     * <p>
     * The DNS mechanism is used when
     * {@link #shouldUseDnsForFetchingServiceUrls()} is set to <em>true</em> and
     * the eureka client expects the DNS to configured a certain way so that it
     * can fetch changing eureka servers dynamically.
     * </p>
     *
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the string indicating the DNS name to be queried for eureka
     *         servers.
     */
    String getEurekaServerDNSName();

    /**
     * 指示eureka客户端是否应使用DNS机制来获取要交谈的eureka服务器的列表。当DNS名称被更新为有额外的服务器时，在eureka客户端按照getEurekaServiceUrlPollIntervalSeconds()中的规定对该信息进行轮询后，立即使用该信息。
     * 另外，服务Url可以返回getEurekaServerServiceUrls(String)，但用户应该实现自己的机制，在发生变化时返回更新的列表。
     * 这些变化在运行时是有效的。
     *
     * Indicates whether the eureka client should use the DNS mechanism to fetch
     * a list of eureka servers to talk to. When the DNS name is updated to have
     * additional servers, that information is used immediately after the eureka
     * client polls for that information as specified in
     * {@link #getEurekaServiceUrlPollIntervalSeconds()}.
     *
     * <p>
     * Alternatively, the service urls can be returned
     * {@link #getEurekaServerServiceUrls(String)}, but the users should implement
     * their own mechanism to return the updated list in case of changes.
     * </p>
     *
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return true if the DNS mechanism should be used for fetching urls, false otherwise.
     */
    boolean shouldUseDnsForFetchingServiceUrls();

    /**
     * 表示该实例是否应向eureka服务器注册其信息以便被其他人发现。
     * 在某些情况下，你不希望你的实例被发现，而你只想发现其他实例。
     *
     * Indicates whether or not this instance should register its information
     * with eureka server for discovery by others.
     *
     * <p>
     * In some cases, you do not want your instances to be discovered whereas
     * you just want do discover other instances.
     * </p>
     *
     * @return true if this instance should register with eureka, false
     *         otherwise
     */
    boolean shouldRegisterWithEureka();

    /**
     * 表示在客户端关闭时，客户端是否应明确地从远程服务器上取消注册。
     *
     * Indicates whether the client should explicitly unregister itself from the remote server
     * on client shutdown.
     *
     * @return true if this instance should unregister with eureka on client shutdown, false otherwise
     */
    default boolean shouldUnregisterOnShutdown() {
        return true;
    }

    /**
     * Indicates whether or not this instance should try to use the eureka
     * server in the same zone for latency and/or other reason.
     *
     * <p>
     * Ideally eureka clients are configured to talk to servers in the same zone
     * </p>
     *
     * <p>
     * <em>The changes are effective at runtime at the next registry fetch cycle as specified by
     * {@link #getRegistryFetchIntervalSeconds()}</em>
     * </p>
     *
     * @return true if the eureka client should prefer the server in the same
     *         zone, false otherwise.
     */
    boolean shouldPreferSameZoneEureka();

    /**
     * Indicates whether server can redirect a client request to a backup server/cluster.
     * If set to false, the server will handle the request directly, If set to true, it may
     * send HTTP redirect to the client, with a new server location.
     *
     * @return true if HTTP redirects are allowed
     */
    boolean allowRedirects();

    /**
     * Indicates whether to log differences between the eureka server and the
     * eureka client in terms of registry information.
     *
     * <p>
     * Eureka client tries to retrieve only delta changes from eureka server to
     * minimize network traffic. After receiving the deltas, eureka client
     * reconciles the information from the server to verify it has not missed
     * out some information. Reconciliation failures could happen when the
     * client has had network issues communicating to server.If the
     * reconciliation fails, eureka client gets the full registry information.
     * </p>
     *
     * <p>
     * While getting the full registry information, the eureka client can log
     * the differences between the client and the server and this setting
     * controls that.
     * </p>
     * <p>
     * <em>The changes are effective at runtime at the next registry fetch cycle as specified by
     * {@link #getRegistryFetchIntervalSeconds()}</em>
     * </p>
     *
     * @return true if the eureka client should log delta differences in the
     *         case of reconciliation failure.
     */
    boolean shouldLogDeltaDiff();

    /**
     * delta在这里可以认为是增量同步
     * 指示eureka客户端是否应禁用delta的获取，而应诉诸于获取完整的注册表信息。
     * 注意，delta获取可以极大地减少流量，因为eureka服务器的变化率通常比获取率低得多。
     * 这些变化在运行时在getRegistryFetchIntervalSeconds()指定的下一个注册表获取周期生效。
     *
     * Indicates whether the eureka client should disable fetching of delta and
     * should rather resort to getting the full registry information.
     *
     * <p>
     * Note that the delta fetches can reduce the traffic tremendously, because
     * the rate of change with the eureka server is normally much lower than the
     * rate of fetches.
     * </p>
     * <p>
     * <em>The changes are effective at runtime at the next registry fetch cycle as specified by
     * {@link #getRegistryFetchIntervalSeconds()}</em>
     * </p>
     *
     * @return true to enable fetching delta information for registry, false to
     *         get the full registry.
     */
    boolean shouldDisableDelta();

    /**
     * 逗号分隔的区域列表，将为其获取eureka注册表信息。必须为这些区域中的每一个定义可用性区域，正如getAvailabilityZones（String）所返回的那样。如果不这样做，将导致发现客户端启动失败。
     *
     * Comma separated list of regions for which the eureka registry information will be fetched. It is mandatory to
     * define the availability zones for each of these regions as returned by {@link #getAvailabilityZones(String)}.
     * Failing to do so, will result in failure of discovery client startup.
     *
     * @return Comma separated list of regions for which the eureka registry information will be fetched.
     * <code>null</code> if no remote region has to be fetched.
     */
    @Nullable
    String fetchRegistryForRemoteRegions();

    /**
     * Gets the region (used in AWS datacenters) where this instance resides.
     * us-east-1
     * @return AWS region where this instance resides.
     */
    String getRegion();

    /**
     * 获取该实例所在区域的可用性区域（用于AWS数据中心）的列表。这些变化在运行时在getRegistryFetchIntervalSeconds()指定的下一个注册表获取周期生效。
     *
     * Gets the list of availability zones (used in AWS data centers) for the
     * region in which this instance resides.
     *
     * <p>
     * <em>The changes are effective at runtime at the next registry fetch cycle as specified by
     * {@link #getRegistryFetchIntervalSeconds()}</em>
     * </p>
     * @param region the region where this instance is deployed.
     *
     * @return the list of available zones accessible by this instance.
     */
    String[] getAvailabilityZones(String region);

    /**
     * 获取完全合格的java.net.URLs列表，以便与eureka服务器通信。
     * 通常，eureka服务器的java.net.URLs带有协议、主机、端口、上下文和版本信息（如果有）。Example: http://ec2-256-156-243-129.compute-1.amazonaws.com:7001/eureka/v2/
     * 这些变化在运行时，在getEurekaServiceUrlPollIntervalSeconds()指定的下一个服务URL刷新周期生效。
     *
     * Gets the list of fully qualified {@link java.net.URL}s to communicate with eureka
     * server.
     *
     * <p>
     * Typically the eureka server {@link java.net.URL}s carry protocol,host,port,context
     * and version information if any.
     * <code>Example: http://ec2-256-156-243-129.compute-1.amazonaws.com:7001/eureka/v2/</code>
     * <p>
     *
     * <p>
     * <em>The changes are effective at runtime at the next service url refresh cycle as specified by
     * {@link #getEurekaServiceUrlPollIntervalSeconds()}</em>
     * </p>
     * @param myZone the zone in which the instance is deployed.
     *
     * @return the list of eureka server service urls for eureka clients to talk
     *         to.
     */
    List<String> getEurekaServerServiceUrls(String myZone);

    /**
     * Indicates whether to get the <em>applications</em> after filtering the
     * applications for instances with only {@link com.netflix.appinfo.InstanceInfo.InstanceStatus#UP} states.
     *
     * <p>
     * <em>The changes are effective at runtime at the next registry fetch cycle as specified by
     * {@link #getRegistryFetchIntervalSeconds()}</em>
     * </p>
     *
     * @return true to filter, false otherwise.
     */
    boolean shouldFilterOnlyUpInstances();

    /**
     * Indicates how much time (in seconds) that the HTTP connections to eureka
     * server can stay idle before it can be closed.
     *
     * <p>
     * In the AWS environment, it is recommended that the values is 30 seconds
     * or less, since the firewall cleans up the connection information after a
     * few mins leaving the connection hanging in limbo
     * </p>
     *
     * @return time in seconds the connections to eureka can stay idle before it
     *         can be closed.
     */
    int getEurekaConnectionIdleTimeoutSeconds();

    /**
     * 表示该客户端是否应从eureka服务器获取eureka注册表信息。
     *
     * Indicates whether this client should fetch eureka registry information from eureka server.
     *
     * @return {@code true} if registry information has to be fetched, {@code false} otherwise.
     */
    boolean shouldFetchRegistry();

    /**
     * If set to true, the {@link EurekaClient} initialization should throw an exception at constructor time
     * if the initial fetch of eureka registry information from the remote servers is unsuccessful.
     *
     * Note that if {@link #shouldFetchRegistry()} is set to false, then this config is a no-op.
     *
     * @return true or false for whether the client initialization should enforce an initial fetch.
     */
    default boolean shouldEnforceFetchRegistryAtInit() {
        return false;
    }

    /**
     * Indicates whether the client is only interested in the registry information for a single VIP.
     *
     * @return the address of the VIP (name:port).
     * <code>null</code> if single VIP interest is not present.
     */
    @Nullable
    String getRegistryRefreshSingleVipAddress();

    /**
     * 用来初始化心跳执行器的线程池大小。
     *
     * The thread pool size for the heartbeatExecutor to initialise with
     *
     * @return the heartbeatExecutor thread pool size
     */
    int getHeartbeatExecutorThreadPoolSize();

    /**
     * 心跳执行器的指数回退相关属性。它是重试延迟的最大乘数值，在发生一系列超时的情况下。默认10
     *
     * Heartbeat executor exponential back off related property.
     * It is a maximum multiplier value for retry delay, in case where a sequence of timeouts
     * occurred.
     *
     * @return maximum multiplier value for retry delay
     */
    int getHeartbeatExecutorExponentialBackOffBound();

    /**
     * 用来初始化cacheRefreshExecutor的线程池大小。
     *
     * The thread pool size for the cacheRefreshExecutor to initialise with
     *
     * @return the cacheRefreshExecutor thread pool size
     */
    int getCacheRefreshExecutorThreadPoolSize();

    /**
     * 缓存刷新执行器的指数回退相关属性。它是重试延迟的最大乘数值，在发生一系列超时的情况下。默认值10次
     *
     * Cache refresh executor exponential back off related property.
     * It is a maximum multiplier value for retry delay, in case where a sequence of timeouts
     * occurred.
     *
     * @return maximum multiplier value for retry delay
     */
    int getCacheRefreshExecutorExponentialBackOffBound();

    /**
     * Get a replacement string for Dollar sign <code>$</code> during serializing/deserializing information in eureka server.
     *
     * @return Replacement string for Dollar sign <code>$</code>.
     */
    String getDollarReplacement();

    /**
     * Get a replacement string for underscore sign <code>_</code> during serializing/deserializing information in eureka server.
     *
     * @return Replacement string for underscore sign <code>_</code>.
     */
    String getEscapeCharReplacement();

    /**
     *  默认为true
     * 如果设置为true，通过com.netflix.appinfo.ApplicationInfoManager.setInstanceStatus(com.netflix.appinfo.InstanceInfo.InstanceStatus)的本地状态更新将触发对远程eureka服务器的按需（但速率有限）注册/更新
     *
     * If set to true, local status updates via
     * {@link com.netflix.appinfo.ApplicationInfoManager#setInstanceStatus(com.netflix.appinfo.InstanceInfo.InstanceStatus)}
     * will trigger on-demand (but rate limited) register/updates to remote eureka servers
     *
     * @return true or false for whether local status updates should be updated to remote servers on-demand
     */
    boolean shouldOnDemandUpdateStatusChange();

    /**
     * If set to true, the {@link EurekaClient} initialization should throw an exception at constructor time
     * if an initial registration to the remote servers is unsuccessful.
     *
     * Note that if {@link #shouldRegisterWithEureka()} is set to false, then this config is a no-op
     *
     * @return true or false for whether the client initialization should enforce an initial registration
     */
    default boolean shouldEnforceRegistrationAtInit() {
        return false;
    }

    /**
     * This is a transient config and once the latest codecs are stable, can be removed (as there will only be one)
     *
     * @return the class name of the encoding codec to use for the client. If none set a default codec will be used
     */
    String getEncoderName();

    /**
     * This is a transient config and once the latest codecs are stable, can be removed (as there will only be one)
     *
     * @return the class name of the decoding codec to use for the client. If none set a default codec will be used
     */
    String getDecoderName();

    /**
     * @return {@link com.netflix.appinfo.EurekaAccept#name()} for client data accept
     */
    String getClientDataAccept();

    /**
     * To avoid configuration API pollution when trying new/experimental or features or for the migration process,
     * the corresponding configuration can be put into experimental configuration section. Config format is:
     * eureka.experimental.freeFormConfigString
     *
     * @return a property of experimental feature
     */
    String getExperimental(String name);

    /**
     * For compatibility, return the transport layer config class
     *
     * @return an instance of {@link EurekaTransportConfig}
     */
    EurekaTransportConfig getTransportConfig();
}
