package dpnm.mobiledevice;

import java.util.*;

import dpnm.Conf;
import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.event.*;
import dpnm.mobiledevice.handoverdecision.*;
import dpnm.network.device.NetworkDevice;
import dpnm.network.event.NetworkEvent;
import dpnm.network.event.NetworkEventListener;
import dpnm.util.*;
import dpnm.mobiledevice.policy.ruleobjects.*;
import dpnm.server.ContextServer;


public class NetworkInterfaceManager {
	private MobileDevice owner = null;
	private NetworkInterface networkInterfaces[] = null;
	
	private NetworkInterface currentNi = null;;
	
	private Timer timer = null;
	private NetworkSelectionTask task = null;
	
	private long numVertical = 0;
	
	private MobileApplication currentApp = null;
	
	protected Vector<NetworkHandoverListener> listeners = null;
	
	private ContextServer contextServer = null;

	private HandoverDecisionMaker handoverDecisionMaker = null;
	
	public NetworkInterfaceManager(MobileDevice owner) {
		this.owner = owner;
		handoverDecisionMaker = new AutonomicHandoverDecisionMaker();
		timer = dpnm.util.UpdateTimer.getInstance().getTimer();
	}
	
	public void setNetworkInterfaces(NetworkInterface nis[]) {
		this.networkInterfaces = nis;
		handoverDecisionMaker.setNetworkInterfaces(networkInterfaces);
		
		startNetworkInterfaces();
	}
	
	public void startNetworkInterfaces() {
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			networkInterfaces[i].setManager(this);
			networkInterfaces[i].start();
		}
	}
	
	public boolean isNetworkInterface(String name) {
		return networkInterfaces != null && getNetworkInterface(name) != null;
    }
	
	private NetworkInterface getNetworkInterface(String name) {
		if (networkInterfaces != null) {
			for (int i = 0; i < networkInterfaces.length; i++) {
    			if (networkInterfaces[i].getName().intern() == name.intern()) {
    				return networkInterfaces[i];
    			}
			}
		}
		return null;
	}
	
	public void addNetworkHandoverListener(NetworkHandoverListener listener) {
		if (listeners == null) {
    		listeners = new Vector<NetworkHandoverListener>();
		}
		listeners.addElement(listener);
	}
	
	public void removeNetworkHandoverListener(NetworkHandoverListener listener) {
		if (listeners != null) {
			listeners.removeElement(listener);
			if (listeners.size() == 0) {
				listeners = null;
			}
		}
	}
	
	void fireNetworkHandoverEvent(CommunicationDevice prev, CommunicationDevice next, int type) {
		NetworkHandoverEvent evt = new NetworkHandoverEvent(owner, prev, next, type);
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			NetworkHandoverListener listener = listeners.elementAt(i);
			listener.handover(evt);
		}
	}
	
	public void startNetworkInterface(String name) {
		NetworkInterface ni = getNetworkInterface(name);
		if (ni != null) {
			if (Conf.DEBUG) {
				Logger.getInstance().logDevice("NIManager", "enable "+ni.getName());
			}
	
			ni.setEnabled(false);
			currentNi = ni;
		}
	}
	
	public void stopNetworkInterface(String name) {
    	NetworkInterface ni = getNetworkInterface(name);
		if (ni != null) {
			if (Conf.DEBUG) {
				Logger.getInstance().logDevice("NIManager", "disable "+ni.getName());
			}
			ni.setEnabled(false);
			currentNi = null;
		}
	}
	
	public void startSelectionTask(MobileApplication app) {
		this.currentApp = app;
		if (handoverDecisionMaker != null) {
			handoverDecisionMaker.setApplication(currentApp);
		}
		task = new NetworkSelectionTask();
		try {
			timer.schedule(task, 0, Conf.NETWORK_SELECTION_TIMEOUT);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void stopSelectionTask() {
		if (timer == null || task == null) {
			return;
		}
		try {
			task.cancel();
			task = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (currentNi != null) {
			currentNi.disconnect(owner);
			currentNi.setSelected(false);
    		currentNi = null;
		}
		currentApp = null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * CORE PART OF NETWORK SELECTION
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	signal strength based selection
	private synchronized void networkSelection() {
		if (networkInterfaces == null || networkInterfaces.length == 0) {
			return;
		}
		
		//	1. Load policy (by current application and context
		Action action = owner.getPolicyManager().getAction(Event.createEvent(currentApp.getName())); 
		
		//1. Apply Speed Filter
		//2. Apply SLA Filter
		for (int i = 0; i < networkInterfaces.length; i++) {
			networkInterfaces[i].setSpeedSupport(owner.getCurrentVelocity());
			networkInterfaces[i].setSLASupport(currentApp);
		}
		
		NetworkInterface bestNI = handoverDecisionMaker.getBestAccessNetwork(action);

		if (bestNI != null) {	// 	we have the best access network for the current application
			//	check number of vertical handover
			if (currentNi == null || checkVerticalHandover(currentNi, bestNI)) {
				doVerticalHandover(bestNI);
			} else {
				if (currentNi != null) {
					// check horizontal handover
					currentNi.doHorizontalHandover(owner);
				}
			}
		} else {
			if (currentNi != null) {
				currentNi.disconnect(owner);
				currentNi.setSelected(false);
				currentNi = null;
			}
		}
		owner.updateNetworkInterface();
		//		int maxNi = 0;
//		NetworkDevice maxDevice = null;
//		NetworkProperty maxProp = null;
//		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
//			networkInterfaces[i].networkSelection(owner.getPolicyManager().getCurrentPolicy());
//			NetworkDevice cDevice = (NetworkDevice)networkInterfaces[i].getCandidateDevice();
//			maxDevice = (NetworkDevice)networkInterfaces[maxNi].getCandidateDevice();
//			
//			NetworkProperty cProp = networkInterfaces[i].getNetworkProperty(cDevice);
//			maxProp = networkInterfaces[maxNi].getNetworkProperty(maxDevice);
//			
//			if (maxProp != null && cProp != null && maxProp.compare(cProp, owner.getPolicyManager().getCurrentPolicy())) {
//				maxDevice = cDevice;
//				maxProp = cProp;
//				maxNi = i;
//			} else if (maxProp == null) {
//				maxProp = cProp;
//				maxNi = i;
//			}
//		}
//		if (maxProp != null) {
//    		//	check number of vertical handover
//			if (currentNi == null || checkVerticalHandover(currentNi, networkInterfaces[maxNi])) {
//				doVerticalHandover(networkInterfaces[maxNi]);
//			} else {
//				if (currentNi != null) {
//    				// check horizontal handover
//					currentNi.doHorizontalHandover(owner);
//				}
//			}
//		} else {
//			if (currentNi != null) {
//				currentNi.disconnect(owner);
//				currentNi.setSelected(false);
//				currentNi = null;
//			}
//		}
	}
	
	private void doVerticalHandover(NetworkInterface newNi) {
		// Increase Number of Vertical Handovers
		//	"Current Network interface is null" means initial selection
		if (currentNi != null) { // vertical handover
    		numVertical++;
    		fireNetworkHandoverEvent(currentNi.getSelectedDevice(), newNi.getCandidateDevice(),
    				NetworkHandoverEvent.VERTICAL_HANDOVER);
			currentNi.disconnect(owner);
			currentNi.setSelected(false);
		}
		currentNi = newNi;
		currentNi.setSelected(true);
		currentNi.doHorizontalHandover(owner);
	}
	
	private boolean checkVerticalHandover(NetworkInterface ci, NetworkInterface ni) {
		return (ci.getName().intern() != ni.getName().intern());
	}
	
	void connect() {
		if (currentNi != null) {
			currentNi.connect(owner);
		}
	}
	
	public NetworkInterface getCurrentNetworkInterface() {
		return currentNi;
	}
	
	public long getNumHandover() {
		return numVertical+getNumHorizontalHandover();
	}

	public long getNumVerticalHandover() {
		return numVertical;
	}

	public long getNumHorizontalHandover() {
		int numHorizontal = 0;
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			numHorizontal += networkInterfaces[i].getNumHandover();
		}
		return numHorizontal;
	}
	
	public void setContextServer(ContextServer contextServer) {
//		this.contextServer = contextServer;
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			networkInterfaces[i].setContextServer(contextServer);
		}
	}

	class NetworkSelectionTask extends TimerTask {

		@Override
		public void run() {
			//	first network selection
			networkSelection();
			//	send connect singal to selected network
			connect();
		}
	}
}
