package dpnm.network.device;

import dpnm.comm.*;
import dpnm.Conf;
import dpnm.network.INetwork;
import dpnm.network.NetworkFactory;
import dpnm.network.event.*;
import dpnm.util.*;

import java.util.*;

public abstract class NetworkDevice extends CommunicationDevice implements UpdateListener {
	public static final int BASE_STATION 			= 0x00;
	public static final int ACCESS_POINT 			= 0x01;
	public static final int RADIO_ACCESS_STATION 	= 0x02;
	
	public static final long DELAY = 1000L;
	
	protected String name = "";
	protected int type = NetworkDevice.BASE_STATION;
	private String networkStr = null;
	
	protected Vector<NetworkEventListener> listeners = null;
	
	protected Timer timer = null;
	protected NetworkEventGenerator eventGen = null;
	
	protected Vector<ConnectedMobileNode> connectedMobileNodes = null;
	
	public NetworkDevice() {
		timer = dpnm.util.UpdateTimer.getInstance().getTimer();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}

	public String getNetworkStr() {
		return networkStr;
	}

	public void setNetworkStr(String networkStr) {
		this.networkStr = networkStr;
	}

	public INetwork getNetwork() {
		return NetworkFactory.getInstance().getNetwork(networkStr);
	}

	public void addNetworkEventListener(NetworkEventListener listener) {
		if (listeners == null) {
    		listeners = new Vector<NetworkEventListener>();
		}
		listeners.addElement(listener);
	}
	
	public void removeNetworkEventListener(NetworkEventListener listener) {
		if (listeners != null) {
			listeners.removeElement(listener);
			if (listeners.size() == 0) {
				listeners = null;
			}
		}
	}

	public String getDeviceInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MACAddr: " + getMacAddress() + "\n");
		buffer.append("IPAddr: " + getIpAddress() + "\n");
		buffer.append("Name: " + getName() + "\n");
		buffer.append("Type: " + getType() + "\n");
		buffer.append("Network: " + getNetwork() + "\n");
    	return buffer.toString();
	}
	
	public void start() {
		eventGen = new NetworkEventGenerator();
		try {
			timer.schedule(eventGen, 0, DELAY);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		UpdateTimer.getInstance().addUpdateListener(this);
	}
	
	public void stop() {
		if (timer == null || eventGen == null) {
			return;
		}
		try {
			eventGen.cancel();
			eventGen = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		UpdateTimer.getInstance().removeUpdateListener(this);
	}
	
	void fireNetworkEvent() {
		NetworkEvent evt = new NetworkEvent(this);
		evt.setType(networkStr);
		
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			NetworkEventListener listener = listeners.elementAt(i);
			listener.receivedNetworkSignal(evt);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NetworkDevice) {
			return getName().intern() == ((NetworkDevice)obj).getName().intern();
		}
		return false;
	}
	
	@Override
	public synchronized void send(Packet p) {
		p.getDst().receive(p);
	}
	
	@Override
	public synchronized void receive(Packet p) {
		if (p instanceof ConnectPacket) {
			ConnectedMobileNode cn = new ConnectedMobileNode(((ConnectPacket) p).getHost(), 
    						(CommunicationDevice)p.getSrc());
			if(((ConnectPacket)p).isConnect()) { //	connection
    			cn.setTimeStamp(System.currentTimeMillis());
        		addConnectedMobileNode(cn);
			} else {	// disconnection
    			removeConnectedMobileNode(getConnectedMobileNode(cn));
			}
		}
	}
	
	public abstract void sendSignal(CommunicationDevice dst, double strength);
	
	public synchronized Vector<ConnectedMobileNode> getConnectedMobileNodes() {
		return connectedMobileNodes;
	}
	
	public synchronized ConnectedMobileNode getConnectedMobileNode(Host host, CommunicationDevice device) {
		for (int i = 0; connectedMobileNodes != null && i < connectedMobileNodes.size(); i++) {
			if (connectedMobileNodes.elementAt(i).equals(new ConnectedMobileNode(host, device))) {
				return connectedMobileNodes.elementAt(i);
			}
		}
		return null;
	}
	
	public synchronized ConnectedMobileNode getConnectedMobileNode(ConnectedMobileNode node) {
		for (int i = 0; connectedMobileNodes != null && i < connectedMobileNodes.size(); i++) {
			if (connectedMobileNodes.elementAt(i).equals(node)) {
				return connectedMobileNodes.elementAt(i);
			}
		}
		return null;
	}
	

	private synchronized void addConnectedMobileNode(ConnectedMobileNode node) {
		if (connectedMobileNodes == null) {
			connectedMobileNodes = new Vector<ConnectedMobileNode>();
		}
		/*
		 * 1. Check if device is already existed in list
		 * 3. if not, add
		 */
		
		ConnectedMobileNode cn = getConnectedMobileNode(node);
		
		if (cn != null) {
			cn.setTimeStamp(System.currentTimeMillis());
		} else {
    		connectedMobileNodes.addElement(node);
		}
	}
	
	private synchronized void removeConnectedMobileNode(ConnectedMobileNode node) {
		if (connectedMobileNodes != null) {
			connectedMobileNodes.removeElement(node);
			if (connectedMobileNodes.isEmpty()) {
				connectedMobileNodes = null;
			}
		}
	}	
	
	public synchronized void updateInfo() {
		//	update connected mobile node list
		//	1. check timeout stamp;
		if (connectedMobileNodes != null) {
			for (int i = 0; connectedMobileNodes != null && i < connectedMobileNodes.size(); i++) {
    			if (System.currentTimeMillis() - connectedMobileNodes.elementAt(i).getTimeStamp() > Conf.UPDATE_TIMEOUT) { // remove
    				removeConnectedMobileNode(connectedMobileNodes.elementAt(i));
    			}
				/*
    			long diff = System.currentTimeMillis() - connectedMobileNodes.elementAt(i).getTimeStamp();
    			if (diff > Conf.UPDATE_TIMEOUT) { // remove
    				removeConnectedMobileNode(connectedMobileNodes.elementAt(i));
    			}
    			*/
    		}
		}
	}
	
	class NetworkEventGenerator extends TimerTask {
		@Override
		public void run() {
			//	fire event
			fireNetworkEvent();
		}
	}
	
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append(getName()+" (");
		sb.append(getNetwork().getName()+")\r\n");
		sb.append("Connected Mobile Nodes:\r\n");
		
		Vector<ConnectedMobileNode> nodes = getConnectedMobileNodes();
		if (nodes != null) {
			for (int j = 0; j < nodes.size(); j++) {
    			ConnectedMobileNode node = nodes.elementAt(j);
    			sb.append(node.getHost().getId() + " (");
    			sb.append(node.getDevice().getIpAddress());
    			sb.append(")\r\n");
    		}
		}	
		return sb.toString();
	}
}
