package dpnm.network.device;

import dpnm.comm.CommunicationDevice;
import dpnm.comm.Host;

public class ConnectedMobileNode {
	Host host = null;
	CommunicationDevice device = null;
	long timeStamp = 0;
	
	public ConnectedMobileNode(Host host, CommunicationDevice device) {
		this.host = host;
		this.device = device;
	}
	
	public Host getHost() {
		return host;
	}

	public CommunicationDevice getDevice() {
		return device;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConnectedMobileNode) {
			ConnectedMobileNode n = (ConnectedMobileNode)obj;
    		return host.getId().intern() == n.getHost().getId().intern() &&
    			device.getMacAddress().intern() == n.getDevice().getMacAddress().intern();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return host.getId() + "("+device.getIpAddress()+")";
	}
}
