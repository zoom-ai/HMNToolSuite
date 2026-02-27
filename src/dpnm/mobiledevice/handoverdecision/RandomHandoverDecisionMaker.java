package dpnm.mobiledevice.handoverdecision;

import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.NetworkInterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

public class RandomHandoverDecisionMaker {

	public CommunicationDevice getBestAccessPoint(MobileApplication app, NetworkInterface[] nis) {
		/**
		 * 1. get the number of all candidate access network
		 * 2. define one value randomly
		 * 3. return the AP
		 */
		ArrayList<CommunicationDevice> devices = new ArrayList<CommunicationDevice>();
		for (int i = 0; i < nis.length; i++) {
			if (nis[i] != null) {
				Enumeration<CommunicationDevice> cn = nis[i].getCandidateNetworkDevices();
				while(cn.hasMoreElements()) {
					devices.add(cn.nextElement());
				}
			}
		}
		int size = devices.size();
		
		Random rand = new Random(System.currentTimeMillis());
		
		return devices.get(rand.nextInt(size));
	}

	public NetworkInterface getBestAccessNetwork(NetworkInterface[] nis) {
		// TODO Auto-generated method stub
		return null;
	}

	public CommunicationDevice getBestAccessPoint() {
		// TODO Auto-generated method stub
		return null;
	}

}
