package dpnm.mobiledevice;

import java.io.File;
import java.util.*;

import net.sourceforge.jFuzzyLogic.FIS;

import dpnm.comm.Host;
import dpnm.comm.Packet;
import dpnm.comm.SignalPacket;
import dpnm.comm.ConnectPacket;
import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.event.NetworkHandoverEvent;
import dpnm.mobiledevice.policy.ruleobjects.Action;
import dpnm.network.NetworkFactory;
import dpnm.server.ContextServer;
import dpnm.tool.Resources;
import dpnm.util.UpdateTimer;
import dpnm.util.UpdateListener;

public abstract class NetworkInterface extends CommunicationDevice implements UpdateListener {
	protected boolean isSelected = false;
	protected boolean isEnabled = false;
	protected boolean isSpeedSupport = true;
	protected boolean isSLASupport = true;
	
	protected String name = null;
	
	protected Hashtable<CommunicationDevice, NetworkProperty> candidateNetworkDevices = null;
	
	private CommunicationDevice selectedDevice = null;
	
	private CommunicationDevice candidateDevice = null;
	
	private long numHandover = 0;
	
	private long duration = 0;
	
	private long prevTime = 0;
	
	private NetworkInterfaceManager manager = null;
	
	private ContextServer contextServer = null;

	private FIS qualityFIS = null;
	private FIS lifetimeFIS = null;

	private Action currentAction = null;

	public void start() {
		setEnabled(true);
		UpdateTimer.getInstance().addUpdateListener(this);
	}
	
	public void stop() {
		setEnabled(false);
		UpdateTimer.getInstance().removeUpdateListener(this);
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isSelected() {
		return isSelected;
	}

	//
	//	measure duration from selected to unselected
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if (isSelected) {
			selectedDevice = candidateDevice;
			prevTime = System.currentTimeMillis();
		} else {
			/*
			if (prevTime != 0) {
    			duration += System.currentTimeMillis() - prevTime;
			}
			*/
			prevTime = 0;
			selectedDevice = null;
		}
	}
	
	public long getDuration() {
		return duration;
	}
	                           
	
	public void setManager(NetworkInterfaceManager manager) {
		this.manager = manager;
	}
	
	public synchronized Enumeration<CommunicationDevice> getCandidateNetworkDevices() {
		if (candidateNetworkDevices != null) {
			return candidateNetworkDevices.keys();
		}
		return null;
	}
	
	public synchronized NetworkProperty getNetworkProperty(CommunicationDevice device) {
		if (candidateNetworkDevices == null)
			return null;
		synchronized(candidateNetworkDevices) {
			if (candidateNetworkDevices != null && device != null) {

				NetworkProperty p = candidateNetworkDevices.get(device);

				
				return p == null ? p : p.clone();
			}
		}
		return null;
	}
	
	private synchronized void addCandidateNetworkDevice(CommunicationDevice device, NetworkProperty prop) {
		if (candidateNetworkDevices == null) {
			candidateNetworkDevices = new Hashtable<CommunicationDevice, NetworkProperty>();
		}
		/*
		 * 1. Check if device is already existed in list
		 * 2. if exist, update property information
		 * 3. if not, add
		 */
		//	1
		synchronized(candidateNetworkDevices) {
    		if (candidateNetworkDevices.containsKey(device)) {
    			NetworkProperty oldProp = candidateNetworkDevices.get(device);
    			oldProp.update(prop);		// update all values and calculate
    		} else {
        		candidateNetworkDevices.put(device, prop);
    		}
		}
	}
	
	private synchronized void removeCandidateNetworkDevice(CommunicationDevice device) {
		if (candidateNetworkDevices != null) {
			synchronized(candidateNetworkDevices) {
    			candidateNetworkDevices.remove(device);
				if (candidateDevice != null && device.getMacAddress().intern() == candidateDevice.getMacAddress().intern()) {
					candidateDevice = null;
				}
    			if (candidateNetworkDevices.isEmpty()) {
    				candidateNetworkDevices = null;
    				candidateDevice = null;
    			}
			}
		}
	}
	@Override
	public synchronized void receive(Packet p) {
		if (p instanceof SignalPacket) {

//    		if (currentApplication == null || currentAction == null || contextServer == null)
//    			return;
/*
			 * TODO: update context information to the network property
			 */
    		CommunicationDevice src = (CommunicationDevice)p.getSrc();

			NetworkProperty prop = new NetworkProperty();
    		//	set all context information
    		prop.setSignalStrength(((SignalPacket)p).getSingalStrength());

    		if (contextServer != null && qualityFIS != null && lifetimeFIS != null && currentAction != null) {
    			prop.setCostRate(contextServer.getNetworkData(src.getMacAddress()).getCostRate());
    			prop.setBandwidth(contextServer.getNetworkData(src.getMacAddress()).getBandwidth());
    			prop.setDelay(contextServer.getNetworkData(src.getMacAddress()).getDelay());
    			prop.setJitter(contextServer.getNetworkData(src.getMacAddress()).getJitter());
    			prop.setBer(contextServer.getNetworkData(src.getMacAddress()).getBER());
    			prop.setThroughput(contextServer.getNetworkData(src.getMacAddress()).getThroughput());
    			prop.setBurstErr(contextServer.getNetworkData(src.getMacAddress()).getBurstError());
    			prop.setPlr(contextServer.getNetworkData(src.getMacAddress()).getPacketLossRatio());
    			prop.setTxPower(contextServer.getNetworkData(src.getMacAddress()).getTxPower());
    			prop.setRxPower(contextServer.getNetworkData(src.getMacAddress()).getRxPower());
    			prop.setIdlePower(contextServer.getNetworkData(src.getMacAddress()).getIdlePower());
    			prop.calculateAllValues(qualityFIS, lifetimeFIS, currentAction.getUserProfile());		// update all values and calculate

    		}    		
    		prop.setTimeStamp(System.currentTimeMillis());
   			addCandidateNetworkDevice(src, prop);
		}
	}

	@Override
	public synchronized void send(Packet p) {
		p.getDst().receive(p);
	}

	public synchronized void updateInfo() {
		//	update candidate network list
		//	1. check timeout stamp;
		if (candidateNetworkDevices != null) {
    		synchronized(candidateNetworkDevices) {
        		Enumeration<CommunicationDevice> devices = candidateNetworkDevices.keys();
        		while(devices != null && devices.hasMoreElements()) {
        			CommunicationDevice d = devices.nextElement();
        			NetworkProperty p = getNetworkProperty(d);
        			if (System.currentTimeMillis() - p.getTimeStamp() > dpnm.Conf.UPDATE_TIMEOUT) { // remove
        				removeCandidateNetworkDevice(d);
        			}
        		}
    		}
		}
		
		//	check connected duration
		if (isSelected) {
			if (prevTime != 0) {
				long cTime = System.currentTimeMillis();
    			duration += cTime - prevTime;
    			prevTime = cTime;
			}
		}
	}
	
	public long getNumHandover() {
		return numHandover;
	}

	//	abstract public void connect(CommunicationDevice dst);
	public void connect(Host host) {
//		System.out.printn("Send from " + host.getId()+"("+getIpAddress()+")" + " to " + ((dpnm.network.device.NetworkDevice)selectedDevice).getName());
		if (selectedDevice != null) {
    		send(new ConnectPacket(this, selectedDevice, host, true));
		}
	}
	
	public void disconnect(Host host) {
		if (selectedDevice != null) {
    		send(new ConnectPacket(this, selectedDevice, host, false));
		}
	}

	public synchronized void networkSelection(Action action) {
		this.currentAction 		= action;
		if (candidateNetworkDevices != null) {
        	synchronized(candidateNetworkDevices) {
        		Enumeration<CommunicationDevice> devices = candidateNetworkDevices.keys();
        		if (devices == null) {
        			return;
        		}
        		CommunicationDevice currentDevice = null;
        		NetworkProperty currentProperty = null;
        		if (candidateDevice != null) {
        			currentDevice = candidateDevice;
        			currentProperty = getNetworkProperty(currentDevice);
        		} else {
            		if (devices.hasMoreElements()) {
            			currentDevice = devices.nextElement();
            			currentProperty = getNetworkProperty(currentDevice);
            		}
        		}
        		//	check random or not
        		if (action.getDecisionAlgorithm() != Action.RANDOM) {
	        		while(devices != null && devices.hasMoreElements()) {
	        			CommunicationDevice d = devices.nextElement();
	        			NetworkProperty p = getNetworkProperty(d);
	            			
	        			if (currentDevice.getMacAddress() != d.getMacAddress() && currentProperty.compare(p,
	        					action.getDecisionAlgorithm())) {
	        				currentDevice = d;
	        				currentProperty = p;
	        			}
	        		}
        		} else {
        			int can = new Random(System.currentTimeMillis()).nextInt(candidateNetworkDevices.size());
        			int count = 0;
        			while(devices != null && devices.hasMoreElements()) {
        				if (count == can) {
        					currentDevice = devices.nextElement();
        					break;
        				}
        				currentDevice = devices.nextElement();
        				count++;
        			}
        		}
        		candidateDevice = currentDevice;
    		}
    	}
	}
	
	synchronized public boolean doHorizontalHandover(Host host) {
		if (selectedDevice == null || candidateDevice == null) {
			return false;
		}
		if (checkHandover(selectedDevice, candidateDevice)) {
			disconnect(host);
			numHandover++;
			manager.fireNetworkHandoverEvent(selectedDevice, candidateDevice, NetworkHandoverEvent.HORIZONTAL_HANDOVER);
    		selectedDevice = candidateDevice;
    		return true;
		}
		return false;
	}
	
	synchronized boolean checkHandover(CommunicationDevice c, CommunicationDevice d) {
		return c.getMacAddress().intern() != d.getMacAddress().intern();
	}

	public CommunicationDevice getSelectedDevice() {
		return selectedDevice;
	}
	
	public CommunicationDevice getCandidateDevice() {
		return candidateDevice;
	}

	public boolean isSpeedSupport() {
		return isSpeedSupport;
	}

	void setSpeedSupport(int currentVelocity) {
		this.isSpeedSupport = 
			currentVelocity >= NetworkFactory.getInstance().getNetwork(getName()).getMinVelocity()
			&& currentVelocity <= NetworkFactory.getInstance().getNetwork(getName()).getMaxVelocity()
			;
	}

	public boolean isSLASupport() {
		return isSLASupport;
	}

	void setSLASupport(MobileApplication app) {
		//	check if this network interface support current application SLA
		this.isSLASupport = true;
	}
	
	void setContextServer(ContextServer server) {
		this.contextServer = server;
	}

	public void setApplication(MobileApplication app) {
		qualityFIS = FIS.load(Resources.HOME+File.separator+Resources.FUZZY_RULE_DIR+"Quality_"+app.getName()+".fcl");
		lifetimeFIS = FIS.load(Resources.HOME+File.separator+Resources.FUZZY_RULE_DIR+"Power_"+app.getName()+".fcl");
	}
}
