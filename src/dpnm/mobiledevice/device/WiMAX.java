package dpnm.mobiledevice.device;

import dpnm.mobiledevice.NetworkInterface;

public class WiMAX extends NetworkInterface {
	public static final String NAME = "WiMAX";
	
	public WiMAX() {
		super.setName(NAME);
	}
}
