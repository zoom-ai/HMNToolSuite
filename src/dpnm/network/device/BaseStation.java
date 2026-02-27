package dpnm.network.device;

import dpnm.comm.CommunicationDevice;
import dpnm.comm.SignalPacket;

import dpnm.network.event.*;

public class BaseStation extends NetworkDevice {
	public BaseStation() {
		type = NetworkDevice.BASE_STATION;
	}

	@Override
	public void sendSignal(CommunicationDevice dst, double strength) {
		SignalPacket p = new SignalPacket(this, dst);
		p.setSingnalStrength(strength);
		super.send(p);
		
	}
}
