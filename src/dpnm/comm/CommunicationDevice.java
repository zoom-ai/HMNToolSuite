package dpnm.comm;

public abstract class CommunicationDevice {
	private String macAddress = null;
	private String ipAddress = null;

	public CommunicationDevice() {
		macAddress = AddressManager.getMacAddress();
		ipAddress = AddressManager.getIpAddress();
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public abstract void send(Packet p);
	
	public abstract void receive(Packet p);
	
	/*
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CommunicationDevice) {
    		return getMacAddress().intern() == ((CommunicationDevice)obj).getMacAddress().intern();
		}
		return false;
	}
	*/}
