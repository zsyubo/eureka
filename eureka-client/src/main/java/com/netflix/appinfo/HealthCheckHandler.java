package com.netflix.appinfo;

/**
 * 这提供了一个比现有的HealthCheckCallback更细化的健康检查合同。
 *
 * This provides a more granular healthcheck contract than the existing {@link HealthCheckCallback}
 *
 * @author Nitesh Kant
 */
public interface HealthCheckHandler {

    InstanceInfo.InstanceStatus getStatus(InstanceInfo.InstanceStatus currentStatus);

}
