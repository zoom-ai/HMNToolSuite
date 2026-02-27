package dpnm.server;

public interface IServer {
	public static final int OSS 		= 0x01;
	public static final int AAA 		= 0x02;
	public static final int DHCP 		= 0x03;
	public static final int CONTEXT 	= 0x04;
	public static final int LOCATION 	= 0X05;
	public static final int APPLICATION = 0x06;
	
	public String getIPAddr();
	public String getName();
	public int getType();
}
