package dpnm.server;

import dpnm.tool.data.NetworkDeviceInfo;
import dpnm.network.NetworkData;

/**
 * This class is for Context Server.
 * Context Server can collect all of context information from OSSs and location database.
 * 
 * @author Administrator
 *
 */
public class ContextServer extends Server {
	public ContextServer(String id) {
		super(id);
		setType(CONTEXT);
	}

	public NetworkDeviceInfo[] getManagedNetworkDevices() {
		return deviceInfo;
	}
	
	public NetworkData getNetworkData(String macAddress) {
		if (deviceInfo == null) {
			return null;
		}
		for (int i = 0; i < deviceInfo.length; i++) {
			if (deviceInfo[i].getDevice().getMacAddress().intern() == macAddress.intern()) {
				return deviceInfo[i].getData();
			}
		}
		return null;
	}
}
