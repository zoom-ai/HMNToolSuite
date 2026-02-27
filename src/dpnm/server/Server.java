package dpnm.server;

import dpnm.comm.CommunicationDevice;
import dpnm.comm.ConnectPacket;
import dpnm.comm.Packet;
import dpnm.network.device.ConnectedMobileNode;

import dpnm.tool.data.NetworkDeviceInfo;

public abstract class Server extends CommunicationDevice {
	public static final int OSS 		= 0x00;
	public static final int CONTEXT 	= 0x01;
	public static final int LOCATION 	= 0X02;
	public static final int APPLICATION = 0x03;
	
	protected NetworkDeviceInfo[] deviceInfo;
	
	public static final String[] TYPE = {
		"OSS", "ContextServer", "LocationServer", "ApplicationServer"
	};
	protected String id;
	protected int type;
	
	public Server() {
		
	}
	
	public Server(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static int getType(String name) {
		for (int i = 0; i < TYPE.length; i++) {
			if (name.intern() == TYPE[i].intern())
				return i;
		}
		return -1;
	}
	public String getServerInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("IPAddr: " + getIpAddress() + "\n");
		buffer.append("MACAddr: " + getMacAddress() + "\n");
		buffer.append("Id: " + getId() + "\n");
		buffer.append("Type: " + getType() + "\n");
    	return buffer.toString();
	}
	
	public void start(NetworkDeviceInfo[] deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	@Override
	public synchronized void send(Packet p) {
		p.getDst().receive(p);
	}
	
	@Override
	public synchronized void receive(Packet p) {
		if (p instanceof ConnectPacket) {
		}
	}

}
