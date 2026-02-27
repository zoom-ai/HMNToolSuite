package test;

import dpnm.mobiledevice.MobileDevice;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.NetworkInterface;
import dpnm.mobiledevice.app.*;
import dpnm.mobiledevice.device.*;

public class TestProg {
	public static void main(String args[]) {
		MobileApplication apps[] = new MobileApplication[4];
		apps[0] = new VoiceCall();
		apps[1] = new VideoCall();
		apps[2] = new SMS();
		apps[3] = new Streaming();
		
		NetworkInterface nis[] = new NetworkInterface[4];
		nis[0] = new CDMA();
		nis[1] = new HSDPA();
		nis[2] = new WiBro();
		nis[3] = new WLAN();
		
		MobileDevice md = new MobileDevice("IMEI:00000001", apps, nis);
		md.showGUI();
	}
}
