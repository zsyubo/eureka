package com.netflix.eureka.registry.rule;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.lease.Lease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 如果实例处于下降或开始状态，该规则就会匹配。
 * <p></p>
 * This rule matches if the instance is DOWN or STARTING.
 *
 * Created by Nikos Michalakis on 7/13/16.
 */
public class DownOrStartingRule implements InstanceStatusOverrideRule {
    private static final Logger logger = LoggerFactory.getLogger(DownOrStartingRule.class);

    @Override
    public StatusOverrideResult apply(InstanceInfo instanceInfo,
                                      Lease<InstanceInfo> existingLease,
                                      boolean isReplication) {
        // ReplicationInstance is DOWN or STARTING - believe that, but when the instance says UP, question that
        // The client instance sends STARTING or DOWN (because of heartbeat failures), then we accept what
        // the client says. The same is the case with replica as well.
        // The OUT_OF_SERVICE from the client or replica needs to be confirmed as well since the service may be
        // currently in SERVICE
        if ((!InstanceInfo.InstanceStatus.UP.equals(instanceInfo.getStatus())) // 如果不为UP状态
                && (!InstanceInfo.InstanceStatus.OUT_OF_SERVICE.equals(instanceInfo.getStatus()))) {  // 不为OUT_OF_SERVICE状态
            logger.debug("Trusting the instance status {} from replica or instance for instance {}",
                    instanceInfo.getStatus(), instanceInfo.getId());
            return StatusOverrideResult.matchingStatus(instanceInfo.getStatus());
        }
        return StatusOverrideResult.NO_MATCH;
    }

    @Override
    public String toString() {
        return DownOrStartingRule.class.getName();
    }
}
