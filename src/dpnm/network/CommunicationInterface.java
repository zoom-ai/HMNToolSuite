package dpnm.network;

import java.util.Hashtable;

import dpnm.comm.Packet;
import dpnm.mobiledevice.NetworkProperty;
import dpnm.network.device.NetworkDevice;

public interface CommunicationInterface {
	public void send(Packet p);
	public void receive(Packet p);
}
