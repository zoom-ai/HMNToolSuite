package dpnm.mobiledevice.device;

import dpnm.mobiledevice.NetworkInterface;

public class WLAN extends NetworkInterface {
	public static final String NAME = "WLAN";

	public WLAN() {
		super.setName(NAME);
	}
}
