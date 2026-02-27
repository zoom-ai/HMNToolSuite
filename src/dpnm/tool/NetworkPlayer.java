package dpnm.tool;

import java.util.Vector;

import dpnm.tool.data.*;
import dpnm.mobiledevice.MobileDevice;
import dpnm.network.*;
import dpnm.network.device.*;
import dpnm.network.event.*;
import dpnm.server.ContextServer;
import dpnm.server.Server;

import java.awt.geom.*;

/**
 * This class is for emulating real network with networks and mobile nodes
 * 
 * @author eliot
 *
 */
class NetworkPlayer implements NetworkEventListener {
	private Vector<NetworkDeviceInfo> networkInfo = null;
	private Vector<NetworkMobileNodeInfo> nodeInfo = null;
	private Vector<ServerInfo> serverInfo = null;
	
	boolean isRunning = false;
	
	NetworkPlayer() {
		networkInfo = new Vector<NetworkDeviceInfo>();
		nodeInfo = new Vector<NetworkMobileNodeInfo>();
		serverInfo = new Vector<ServerInfo>();
	}
	
	void loadNetworkMapInfo(NetworkMapInfo mapInfo) {
    	NetworkDeviceInfo[] info = mapInfo.getDevices();
    	if (info != null) {
        	for (int i = 0; i < info.length; i++) {
                addNetwork(info[i]);
        	}
    	}
    	NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
    	if (nodeInfo != null) {
        	for (int i = 0; i < nodeInfo.length; i++) {
                addMobileNode(nodeInfo[i]);
			}
		}
    	ServerInfo[] serverInfo = mapInfo.getServers();
    	if (serverInfo != null) {
        	for (int i = 0; i < serverInfo.length; i++) {
                addServer(serverInfo[i]);
			}
		}
	}
	
	void resetData() {
		networkInfo.removeAllElements();
		nodeInfo.removeAllElements();
		serverInfo.removeAllElements();
	}
	public NetworkDeviceInfo[] getNetworkDevices() {
		return (NetworkDeviceInfo[])networkInfo.toArray(new NetworkDeviceInfo[0]);
	}

	public ServerInfo[] getServers() {
		return (ServerInfo[])serverInfo.toArray(new ServerInfo[0]);
	}

	public ContextServer getContextServer() {
		for (int i = 0; i < serverInfo.size(); i++) {
			if (serverInfo.elementAt(i).getServer().getType() == Server.CONTEXT) {
				return (ContextServer)serverInfo.elementAt(i).getServer();
			}
		}
		return null;
	}

	void addNetwork(NetworkDeviceInfo info) {
		networkInfo.addElement(info);
		info.getDevice().addNetworkEventListener(this);
		if (isRunning) {
    		info.getDevice().start();
		}
	}
	
	void addMobileNode(NetworkMobileNodeInfo info) {
		nodeInfo.addElement(info);
	}
	
	void removeNetwork(NetworkDeviceInfo info) {
		networkInfo.removeElement(info);
		info.getDevice().removeNetworkEventListener(this);
	}
	
	void removeMobileNode(NetworkMobileNodeInfo info) {
		nodeInfo.removeElement(info);
	}
	
	void addServer(ServerInfo info) {
		serverInfo.addElement(info);
	}
	
	void removeServer(ServerInfo info) {
		serverInfo.removeElement(info);
	}

	void start() {
		isRunning = true;
		for (int i = 0; i < networkInfo.size(); i++) {
			networkInfo.elementAt(i).getDevice().start();
		}
		for (int i = 0 ; i < serverInfo.size(); i++) {
			serverInfo.elementAt(i).getServer().start(getNetworkDevices());
		}
		for (int i = 0 ; i < nodeInfo.size(); i++) {
			nodeInfo.elementAt(i).getDevice().setContextServer(getContextServer());
		}
		dpnm.util.UpdateTimer.getInstance().start();
	}
	
	void stop() {
		isRunning = false;
		for (int i = 0; i < networkInfo.size(); i++) {
			networkInfo.elementAt(i).getDevice().stop();
		}
		dpnm.util.UpdateTimer.getInstance().stop();
	}

	public void receivedNetworkSignal(NetworkEvent evt) {
		// TODO Auto-generated method stub
		NetworkDevice device = (NetworkDevice)evt.getSource();
		NetworkDeviceInfo info = findNetworkDeviceInfo(device.getName());
		
		transmitNetworkEvents(info);
	}
	
	private NetworkDeviceInfo findNetworkDeviceInfo(String name) {
		for (int i = 0; i < networkInfo.size(); i++) {
			if (networkInfo.elementAt(i).getDevice().getName().intern() == name.intern()) {
				return networkInfo.elementAt(i);
			}
		}
		return null;
	}
	
	private void transmitNetworkEvents(NetworkDeviceInfo info) {
		//	1. find mobile node is in radius
		//	2. check same module
		//	3. transmit event
		
		for (int i = 0; i < nodeInfo.size(); i++) {
			NetworkMobileNodeInfo nInfo = nodeInfo.elementAt(i);
    		double radius = (double)info.getDevice().getNetwork().getCoverage() / Env.ZOOM;
    		//	nodeRadius^2 = (device.x - network.x)^2 + (device.y - network.y)^2
    		double nodeRadius = Point2D.distance(
    				(double)info.getXpos(), 
    				(double)info.getYpos(), 
    				(double)nInfo.getXpos(), 
    				(double)nInfo.getYpos());
    		if (nodeRadius <= radius) {
    			if (nInfo.getDevice().hasNetworkInterface(info.getDevice().getNetwork().getName())) {
        			info.getDevice().sendSignal(
        					nInfo.getDevice().getNetworkInterface(info.getDevice().getNetwork().getName()),
        					1.0 - nodeRadius/radius);
    			}
    		}
		}
	}
}
