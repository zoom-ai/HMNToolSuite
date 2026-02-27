package dpnm.mobiledevice;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jFuzzyLogic.FIS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dpnm.comm.CommunicationDevice;
import dpnm.comm.Host;
import dpnm.featuremodel.StaticFeatureModel;
import dpnm.mobiledevice.policy.ruleobjects.Event;
import dpnm.mobiledevice.policy.ruleobjects.Rule;
import dpnm.network.device.NetworkDevice;
import dpnm.server.*;
import dpnm.tool.Resources;

/**
 * This MobileDevice is based on MVC (Model-View-Controller)
 * 
 * @author Eliot Kang
 * @since 2009-03-10
 */
public class MobileDevice implements Host {
	public static final String APP_NONE = "AppNone";
	public static final String NI_NONE = "NetworkInterfaceNone";
	private static final String FUZZY_RSS = Resources.HOME+File.separator+Resources.FUZZY_RULE_DIR+"RSS.fcl";
	private static final String FUZZY_COST = Resources.HOME+File.separator+Resources.FUZZY_RULE_DIR+"Cost.fcl";
	private static final String FUZZY_QUALITY = Resources.HOME+File.separator+Resources.FUZZY_RULE_DIR+"Quality.fcl";
	private static final String FUZZY_LIFETIME = Resources.HOME+File.separator+Resources.FUZZY_RULE_DIR+"Lifetime.fcl";

	/*
	 * DATA members (Model)
	 */
	private String id = "MD";
	private MobileApplication applications[];
	private NetworkInterface networkInterfaces[];
	
	/*
	 * Controller
	 */
	StateManager stateManager = null;
	
	/*
	 * GUI
	 */
	MobileDeviceFrame gui = null;
	
	/*
	 * Manager
	 */
	ApplicationManager appManager 		= null;
	NetworkInterfaceManager niManager 	= null;
	PolicyManager policyManager = null;
	
	/*
	 * Context Server
	 */
	ContextServer contextServer = null;
	
	int currentVelocity = 0;


	public MobileDevice(String id) {
		this(id, null, null);
	}
	
	public MobileDevice(String id, MobileApplication apps[], NetworkInterface nis[]) {
		this.id = id;
		applications = apps;
		networkInterfaces = nis;
		
		stateManager = new StateManager(this);
		
		appManager = new ApplicationManager(this);
		appManager.setApplications(apps);
		
		niManager = new NetworkInterfaceManager(this);
		niManager.setNetworkInterfaces(nis);
		
		policyManager = new PolicyManager();


		powerOn();
	}
	
	public void showGUI() {
		if (gui == null) {
    		gui = new MobileDeviceFrame(id, applications, networkInterfaces, stateManager, policyManager);
		}
		gui.setVisible(true);
	}
	
	public void hideGUI() {
		gui.setVisible(false);
		gui = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void powerOn() {
		if (gui != null) {
//    		gui.powerOn();
		}
//		niManager.startSelectionTask();
	}
	
	public void powerOff() {
		if (gui != null) {
//    		gui.powerOff();
		}
//		niManager.stopSelectionTask();
	}
	
	public MobileApplication[] getApplications() {
		return applications;
	}

	public void setApplications(MobileApplication[] apps) {
		this.applications = apps;
		if (gui != null) {
    		gui.setApplications(apps);
		}
		appManager.setApplications(apps);
	}

	public NetworkInterface[] getNetworkInterfaces() {
		return networkInterfaces;
	}
	
	public NetworkInterface getNetworkInterface(String name) {
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			if (networkInterfaces[i].getName().intern() == name.intern())
				return networkInterfaces[i];
		}
		return null;
	}

	public void setNetworkInterfaces(NetworkInterface[] nis) {
		this.networkInterfaces = nis;
		if (gui != null) {
    		gui.setNetworkInterfaces(networkInterfaces);
		}
		niManager.setNetworkInterfaces(nis);
	}

	public ApplicationManager getApplicationManager() {
		return appManager;
	}

	public NetworkInterfaceManager getNetworkInterfaceManager() {
		return niManager;
	}

	public PolicyManager getPolicyManager() {
		return policyManager;
	}

	public Rule[] getPolicies() {
		return (Rule [])policyManager.getPolicies().toArray(new Rule[0]);
	}
	public StateManager getStateManager() {
		return stateManager;
	}
	
	public void updateApplication() {
		if (gui != null) {
    		gui.updateApplication();
		}
	}
	
	public void updateNetworkInterface() {
		if (gui != null) {
    		gui.updateNetworkInterface();
		}
	}
	
	public boolean hasNetworkInterface(String name) {
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			if (networkInterfaces[i].getName().intern() == name.intern())
				return true;
		}
		return false;
	}
	
	public String[] getIpAddress() {
		String str [] = new String[networkInterfaces.length];
		for (int i = 0; i < networkInterfaces.length; i++) {
			str[i] = networkInterfaces[i].getIpAddress();
		}
		return str;
	}
	
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		//	show the number of handovers
		sb.append("# of Handovers: "+ getNetworkInterfaceManager().getNumHandover());
		sb.append(" (Horizontal: "+ getNetworkInterfaceManager().getNumHorizontalHandover()+", ");
		sb.append("Vertical: "+ getNetworkInterfaceManager().getNumVerticalHandover()+")\r\n");
		
		NetworkInterface networkInterfaces[] = getNetworkInterfaces();
		//	show the number of each horizontal handovers
		sb.append("[");
		for (int j = 0; networkInterfaces != null && j < networkInterfaces.length; j++) {
			sb.append(networkInterfaces[j].getName()+"("+
					networkInterfaces[j].getNumHandover()+", "+
					networkInterfaces[j].getDuration()+") ");
		}
		sb.append("]\r\n");
		sb.append("Current Action: ");
		if (appManager.getCurrentApplication() != null) {
			sb.append(policyManager.getPolicy(appManager.getCurrentApplication().getName())+"\r\n");
		} else {
			sb.append(" No policy\r\n");
		}
		sb.append("Availale Network(s):\r\n");
		for (int j = 0; networkInterfaces != null && j < networkInterfaces.length; j++) {
    		Enumeration<CommunicationDevice> networkDevices = networkInterfaces[j].getCandidateNetworkDevices();
			if (networkDevices != null) {
	    		while(networkDevices.hasMoreElements()) {
	    			NetworkDevice d = (NetworkDevice)networkDevices.nextElement();
	    			NetworkProperty p = networkInterfaces[j].getNetworkProperty((CommunicationDevice)d);
    				sb.append(d.getNetwork().getName() + "("+d.getName()+",");
	    			if (p != null) {
		    			sb.append(getSignalStrengthStr(p.getSignalStrength())+",");
	    				sb.append("R:"+getSignalStrengthStr(p.getApavRSS())+",");
	    				sb.append("C:"+getSignalStrengthStr(p.getApavCost())+",");
	    				sb.append("Q:"+getSignalStrengthStr(p.getApavQuality())+",");
	    				sb.append("L:"+getSignalStrengthStr(p.getApavLifetime())+",");
	    				sb.append("S:"+getSignalStrengthStr(p.getApsv())+")");
	    			} else {
	    				sb.append("0");
	    			}
	    			sb.append(")\r\n");
	    		}
			}	
		}
		sb.append("\r\n");
		sb.append("Connected Network(s)\r\n");
		NetworkInterface ni = getNetworkInterfaceManager().getCurrentNetworkInterface();
		if (ni != null) {
			sb.append(ni.getName());
			sb.append("("+((NetworkDevice)ni.getSelectedDevice()).getName()+")\r\n");
		}
		return sb.toString();
	}
	
	private String getSignalStrengthStr(double d) {
		return String.valueOf((int)(d*100));
	}
	
	public void setContextServer(ContextServer contextServer) {
		this.contextServer = contextServer;
		niManager.setContextServer(contextServer);
	}
	
	
	private static final String PACKAGE_APP = "dpnm.mobiledevice.app";
	private static final String PACKAGE_NI = "dpnm.mobiledevice.device";
	
	//	test
	public static void main(String args[]) {
		String[] a = {"VoiceCall", "VideoCall", "Streaming", "WebBrowser", "SMS"};
		String[] n = {"CDMA", "GSM", "WiBro"};
		MobileApplication apps[] = getApplication(a);
		NetworkInterface nis[] = getNetworkInterface(n);
		
  		MobileDevice device = new MobileDevice("Test", apps, nis);
  		device.showGUI();
	}
	
	public static MobileApplication[] getApplication(String[] name) {
		MobileApplication[] apps = new MobileApplication[name.length];
		for (int i = 0; i < name.length; i++) {
			try {
    			Class<? extends MobileApplication> c = 
    				Class.forName(PACKAGE_APP+"."+name[i]).asSubclass(MobileApplication.class);
    			apps[i] = c.newInstance();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			}
		}
		return apps;
	}
	
	public static NetworkInterface[] getNetworkInterface(String[] name) {
		NetworkInterface[] nis = new NetworkInterface[name.length];
		for (int i = 0; i < name.length; i++) {
			try {
    			Class<? extends NetworkInterface> c = 
    				Class.forName(PACKAGE_NI+"."+name[i]).asSubclass(NetworkInterface.class);
    			nis[i] = c.newInstance();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			}
		}
		return nis;
	}
	
	public void savePolicy(String name) {
		policyManager.save(name, getId());
	}
	
	public void loadPolicy(String name) {
		policyManager.load(name, getId());
	}

	public void destroy() {
		policyManager.destroy();
	}
	
	public int getCurrentVelocity() {
		return currentVelocity;
	}
	public void setCurrentVelocity(int currentVelocity) {
		this.currentVelocity = currentVelocity;
	}
	
	
}