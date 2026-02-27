package dpnm.network.device;

import dpnm.comm.CommunicationDevice;
import dpnm.comm.SignalPacket;

public class AccessPoint extends NetworkDevice {
	public AccessPoint() {
		type = NetworkDevice.ACCESS_POINT;
	}


	@Override
	public void sendSignal(CommunicationDevice dst, double strength) {
		SignalPacket p = new SignalPacket(this, dst);
		p.setSingnalStrength(strength);
		super.send(p);
		
	}
}
