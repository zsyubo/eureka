package com.netflix.discovery.shared.transport;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

/**
 * Low level Eureka HTTP client API.
 *
 * @author Tomasz Bak
 */
public interface EurekaHttpClient {

    /**
     * 注册服务
     * @param info
     * @return
     */
    EurekaHttpResponse<Void> register(InstanceInfo info);

    /**
     * 注销服务
     * @param appName
     * @param id
     * @return
     */
    EurekaHttpResponse<Void> cancel(String appName, String id);

    /**
     * 服务续约
     * @param appName
     * @param id
     * @param info
     * @param overriddenStatus
     * @return
     */
    EurekaHttpResponse<InstanceInfo> sendHeartBeat(String appName, String id, InstanceInfo info, InstanceStatus overriddenStatus);

    /**
     * 状态变更
     * @param appName
     * @param id
     * @param newStatus
     * @param info
     * @return
     */
    EurekaHttpResponse<Void> statusUpdate(String appName, String id, InstanceStatus newStatus, InstanceInfo info);

    EurekaHttpResponse<Void> deleteStatusOverride(String appName, String id, InstanceInfo info);

    EurekaHttpResponse<Applications> getApplications(String... regions);

    EurekaHttpResponse<Applications> getDelta(String... regions);

    EurekaHttpResponse<Applications> getVip(String vipAddress, String... regions);

    EurekaHttpResponse<Applications> getSecureVip(String secureVipAddress, String... regions);

    EurekaHttpResponse<Application> getApplication(String appName);

    EurekaHttpResponse<InstanceInfo> getInstance(String appName, String id);

    EurekaHttpResponse<InstanceInfo> getInstance(String id);

    void shutdown();
}
