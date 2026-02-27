package dpnm.mobiledevice.event;

import java.util.EventObject;

import dpnm.comm.Host;
import dpnm.comm.CommunicationDevice;

public class NetworkHandoverEvent {
	public static final int HORIZONTAL_HANDOVER = 0x00;
	public static final int VERTICAL_HANDOVER = 0x01;
	
	private int type = HORIZONTAL_HANDOVER;
	
	private Host host = null;
	private CommunicationDevice prevNetwork = null;
	private CommunicationDevice nextNetwork = null;
	
	public NetworkHandoverEvent(Host host, CommunicationDevice prev, CommunicationDevice next, int type) {
		this.host = host;
		this.prevNetwork = prev;
		this.nextNetwork = next;
		this.type = type;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return this.host;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}

	public CommunicationDevice getPrevNetwork() {
		return prevNetwork;
	}

	public void setPrevNetwork(CommunicationDevice prevNetwork) {
		this.prevNetwork = prevNetwork;
	}

	public CommunicationDevice getNextNetwork() {
		return nextNetwork;
	}

	public void setNextNetwork(CommunicationDevice nextNetwork) {
		this.nextNetwork = nextNetwork;
	}
	
}
