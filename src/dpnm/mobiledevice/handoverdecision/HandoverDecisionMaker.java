package dpnm.mobiledevice.handoverdecision;

import dpnm.mobiledevice.*;
import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.policy.ruleobjects.Action;

/**
 * HandoverDecisionManager get network interfaces, service, context, and policy
 * Its output is best AP
 * 
 * @author Administrator
 *
 */

public interface HandoverDecisionMaker {
	public void setApplication(MobileApplication app);
	public void setNetworkInterfaces(NetworkInterface[] nis);
	public NetworkInterface getBestAccessNetwork(Action action);
}
