package dpnm.comm;

public class ConnectPacket extends Packet {
	private Host host = null;
	private boolean isConnect = false;
	
	public ConnectPacket(CommunicationDevice src, CommunicationDevice dst) {
		this(src, dst, null, true);
	}
	
	public ConnectPacket(CommunicationDevice src, CommunicationDevice dst, Host host, boolean isConnect) {
		super(src, dst);
		this.host = host;
		this.isConnect = isConnect;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return this.host;
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}
}
