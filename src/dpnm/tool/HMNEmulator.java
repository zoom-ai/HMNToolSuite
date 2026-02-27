package dpnm.tool;

import dpnm.Conf;
import dpnm.featuremodel.StaticFeatureModel;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobilenode.MobileNodeFactory;
import dpnm.network.NetworkFactory;
import dpnm.tool.data.*;
import dpnm.util.Log;
import dpnm.util.Logger;

import java.io.*;
import java.util.Random;
import java.util.Vector;


import org.w3c.dom.*;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

/**
 * This class is main class for heterogeneous mobile network emulator
 *
 * @author Eliot Kang
 * @since 2009/03/13
 */
public final class HMNEmulator {
	private static final int SIM_RUNNING = 0x00;
	private static final int SIM_STOPPED = 0x01;

	private int simState = SIM_STOPPED;

	private NetworkMapInfo mapInfo = null;
	private String currentFile = null;
	
	

	/*	GUI component */
	private HMNEmulatorGUI gui = null;
	/*	Network simulator */
	private NetworkPlayer player = null;
	/*	Network Simulator */
	private HMNNetworkSimulator simulator = null;
	/*	Network Simulator CLI */
	private HMNNetworkSimulatorCLI simulatorCLI = null;
	
	private ProgressInterface progress = null;

	/* logging */
	private File log[] = null;
	private File logTable[] = null;
	private File logGraph[] = null;
	private File logGraphcsv[] = null;

	public HMNEmulator() {
	}
	
	public void initGUI() {
		gui = new HMNEmulatorGUI(this, Env.TITLE +" " + Env.VERSION);
	}
	
	public void initSimulator() {
		simulator = new HMNNetworkSimulator(this, "HMNNetwork Simulator");
	}

	public void loadNetwork() {
		if (mapInfo == null) {
			return;
		}
		//	load all components on GUI frame
		gui.loadNetworkMapInfo(mapInfo);
		gui.setViewTitle(mapInfo.getName(), currentFile == null ? currentFile : new File(currentFile).getName());
		//	load all devices on player
		player = new NetworkPlayer();
		player.loadNetworkMapInfo(mapInfo);
	}
	
	public void launchSimulatorCLI() {
		simulatorCLI = new HMNNetworkSimulatorCLI(this);
	}
	

	public void launchSimulator() {
		launchSimulator(false);
	}
	
	public void launchSimulator(boolean file) {
		if (!file) {
			initSimulator();
			simulator.reset();
			simulator.setVisible(true);
		} else
			launchSimulator(0, currentFile);
	}
	
	public void launchSimulator(long timeinterval) {
		launchSimulator(timeinterval, currentFile);
	}

	public void launchSimulator(long timeinterval, String file) {
		NetworkMapInfo info = getNetworkMapInfo(file);
		if (info == null) {
			return;
		}
		
		if (simulator == null) {
			initSimulator();
		}
		stopPlayer();
		simulator.reset();
		simulator.setIconMode(true);
		simulator.loadNetworkMapInfo(info);
		simulator.setTimeInterval(timeinterval);
		simulator.setViewTitle(info.getName());
		simulator.setVisible(true);
	}
	
	public void openSimulator(long timeinterval, String file) {
		NetworkMapInfo info = getNetworkMapInfo(file);
		if (info == null) {
			return;
		}
		simulator.reset();
		simulator.loadNetworkMapInfo(info);
		simulator.setTimeInterval(timeinterval);
		simulator.setViewTitle(info.getName());
		simulator.setVisible(true);
	}
	
	void exitSimulator() {
		stopSimulator();
		simulator.reset();
		simulator.setVisible(false);
		if (gui == null) {
			exit();
		}
	}

	public void startSimulator() {
		if (simState == SIM_RUNNING) {
    		simulator.loadNetworkMapInfo(mapInfo);
    		simulator.setViewTitle(mapInfo.getName());
		}
	}

	public void stopSimulator() {
		if (simState == SIM_RUNNING) {
		}
	}

	public void startPlayer() {
		if (player != null) {
			if (simState == SIM_STOPPED) {
				player.start();
				simState = SIM_RUNNING;
			}
		}
	}

	public void stopPlayer() {
		if (player != null) {
			if (simState == SIM_RUNNING) {
				player.stop();
				simState = SIM_STOPPED;
			}
		}
	}

	public NetworkMapInfo getNetworkMapInfo() {
		return mapInfo;
	}

	public String getCurrentFile() {
		return currentFile;
	}

	public void createNetworkMap(String title, String descr, int width, int height, String background) {
		currentFile = null;
		this.mapInfo = new NetworkMapInfo(title, descr, width, height, background);
		if (gui != null) {
    		gui.resetView();
    		loadNetwork();
		}
	}

	public void modifyNetworkMap(String title, String descr, int width, int height, String background) {
		if (mapInfo != null) {
			mapInfo.setName(title);
			mapInfo.setDescr(descr);
			mapInfo.setWidth(width);
			mapInfo.setHeight(height);
			mapInfo.setBackground(background);
			gui.resetView();
			loadNetwork();
		}
	}

	public synchronized void openNetworkMap(String mapFile) {
		openNetworkMap(new File(mapFile));
	}
	
	public synchronized void openNetworkMap(File mapFile) {
		currentFile = mapFile.getAbsolutePath();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(mapFile);
			Element root = doc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(NetworkMapInfo.MAP);
			this.mapInfo = new NetworkMapInfo(nodes.item(0));
    		gui.resetView();
			loadNetwork();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//	load policies of Mobile Node
		NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
		for (int i = 0; i < nodeInfo.length; i++) {
			nodeInfo[i].getDevice().loadPolicy(mapFile.getName().substring(0,mapFile.getName().length()-4));
		}

	}
	
	public synchronized NetworkMapInfo getNetworkMapInfo(String filename) {
		return getNetworkMapInfo(new File(filename));
	}
	
	public synchronized NetworkMapInfo getNetworkMapInfo(File mapFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(mapFile);
			Element root = doc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(NetworkMapInfo.MAP);
			NetworkMapInfo mapInfo = new NetworkMapInfo(nodes.item(0));
			//	load policies of Mobile Node
			NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
			for (int i = 0; i < nodeInfo.length; i++) {
				nodeInfo[i].getDevice().loadPolicy(mapFile.getName().substring(0,mapFile.getName().length()-4));
			}
			return mapInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void saveNetworkMap(String mapFile) {
		saveNetworkMap(new File(mapFile));
	}
	
	public synchronized void saveNetworkMap(File mapFile) {
		currentFile = mapFile.getAbsolutePath();
		saveNetworkMap(this.mapInfo, mapFile);
		if (gui != null) {
    		gui.setViewTitle(mapInfo.getName(), currentFile == null ? currentFile : new File(currentFile).getName());
		}
	}
	
	public synchronized void saveNetworkMap(NetworkMapInfo info, String mapFile) {
		saveNetworkMap(info, new File(mapFile));
	}
	
	public synchronized void saveNetworkMap(NetworkMapInfo info, File mapFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement(Env.HANDLER_NAME);
			doc.appendChild(root);
			info.appendXml(root);
			
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			DOMSource source = new DOMSource(root);
			FileOutputStream destination = new FileOutputStream(mapFile);
			StreamResult result = new StreamResult(destination);
			transformer.transform(source, result);
			destination.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//	save policies of Mobile Node
		NetworkMobileNodeInfo[] nodeInfo = info.getMnodes();
		for (int i = 0; i < nodeInfo.length; i++) {
			nodeInfo[i].getDevice().savePolicy(mapFile.getName().substring(0,mapFile.getName().length()-4));
		}
	}

	public synchronized NetworkDeviceInfo addNetwork(String name, String type, int x, int y) {
		NetworkDeviceInfo info = new NetworkDeviceInfo(name, type, x, y);
		mapInfo.addNetworkDeviceInfo(info);
		//	add network info to player
		if (player != null) {
    		player.addNetwork(info);
		}
		return info;
	}

	public synchronized void modifyNetwork(String macAddr, String name, int x, int y) {
		NetworkDeviceInfo[] deviceInfos = mapInfo.getDevices();
		for (int i = 0; i < deviceInfos.length; i++) {
			if (deviceInfos[i].getDevice().getMacAddress().intern() == macAddr.intern()) {
				deviceInfos[i].getDevice().setName(name);
				deviceInfos[i].setXpos(x);
				deviceInfos[i].setYpos(y);
				break;
			}
		}
	}

	public synchronized void deleteNetwork(String macAddr) {
		NetworkDeviceInfo[] deviceInfos = mapInfo.getDevices();
		for (int i = 0; i < deviceInfos.length; i++) {
			if (deviceInfos[i].getDevice().getMacAddress().intern() == macAddr.intern()) {
				if (player != null) {
					player.removeNetwork(deviceInfos[i]);
				}
				mapInfo.removeNetworkDeviceInfo(deviceInfos[i]);
				break;
			}
		}
	}

	public synchronized NetworkMobileNodeInfo createMobileNode(String id, Vector<String> appList, Vector<String> niList, int x, int y, String type) {
		NetworkMobileNodeInfo info = new NetworkMobileNodeInfo(id, appList, niList, x, y, type);
		mapInfo.addNetworkMobileNodeInfo(info);
		//	add info to player
		if (player != null) {
    		player.addMobileNode(info);
		}
		return info;
	}

	public synchronized void modifyMobileNode(String id, 
			Vector<String> appList, Vector<String> niList, int x, int y, String type) {
		NetworkMobileNodeInfo[] nodeInfos = mapInfo.getMnodes();
		for (int i = 0; i < nodeInfos.length; i++) {
			if (nodeInfos[i].getDevice().getId().intern() == id.intern()) {
				nodeInfos[i].getDevice().setApplications(
						nodeInfos[i].getApplication(appList));
				nodeInfos[i].getDevice().setNetworkInterfaces(
						nodeInfos[i].getNetworkInterface(niList));
				nodeInfos[i].setXpos(x);
				nodeInfos[i].setYpos(y);
				nodeInfos[i].setType(type);
				break;
			}
		}
	}

	public synchronized void deleteMobileNode(String id) {
		NetworkMobileNodeInfo[] nodeInfos = mapInfo.getMnodes();
		for (int i = 0; i < nodeInfos.length; i++) {
			if (nodeInfos[i].getDevice().getId().intern() == id.intern()) {
				if (player != null) {
					player.removeMobileNode(nodeInfos[i]);
				}
				mapInfo.removeNetworkMobileNodeInfo(nodeInfos[i]);
				break;
			}
		}
	}

	public synchronized ServerInfo createServer(String id, int type, int x, int y) {
		ServerInfo info = new ServerInfo(id, type, x, y);
		mapInfo.addServerInfo(info);
		//	add network info to player
		if (player != null) {
    		player.addServer(info);
		}
		return info;
	}

	public synchronized void modifyServer(String id, int x, int y) {
		ServerInfo[] serverInfos = mapInfo.getServers();
		for (int i = 0; i < serverInfos.length; i++) {
			if (serverInfos[i].getServer().getId().intern() == id.intern()) {
				serverInfos[i].setXpos(x);
				serverInfos[i].setYpos(y);
				break;
			}
		}
	}

	public synchronized void deleteServer(String id) {
		ServerInfo[] serverInfos = mapInfo.getServers();
		for (int i = 0; i < serverInfos.length; i++) {
			if (serverInfos[i].getServer().getId().intern() == id.intern()) {
				if (player != null) {
					player.removeServer(serverInfos[i]);
				}
				mapInfo.removeServerInfo(serverInfos[i]);
				break;
			}
		}
	}

    public int[] getEachNetworkCount() {
    	return getEachNetworkCount(getNetworkMapInfo().getDevices());
    }
    
    public int[] getEachNetworkCount(NetworkDeviceInfo[] info) {
    	// # of each network
    	int count[] = new int[NetworkFactory.getInstance().getNetworkCount()];
    	for (int i = 0; i < count.length; i++) {
    		count[i] = 0;
    	}
    	if (info != null) {
        	for (int i = 0; i < info.length; i++) {
        		int index = NetworkFactory.getInstance().getNetworkIndex(info[i].getDevice().getNetworkStr());
        		if (index != -1) {
            		count[index]++;
        		}
        	}
    	}
    	return count;
    }
    
    public int[] getEachMobileNodeCount() {
    	return getEachMobileNodeCount(getNetworkMapInfo().getMnodes());
    }
    
    public int[] getEachMobileNodeCount(NetworkMobileNodeInfo[] nodeInfo) {
    	// # of each mobile node
    	int count[] = new int[MobileNodeFactory.getInstance().getMobileNodeCount()];
    	for (int i = 0; i < count.length; i++) {
    		count[i] = 0;
    	}
    	if (nodeInfo != null) {
        	for (int i = 0; i < nodeInfo.length; i++) {
        		int index = MobileNodeFactory.getInstance().getMobileNodeIndex(nodeInfo[i].getMobileNode().getName());
        		if (index != -1) {
            		count[index]++;
        		}
        	}
    	}
    	return count;
    }
    
	public void showGUI() {
		gui.setVisible(true);
	}

	void exit() {
		stopPlayer();
		System.exit(1);
	}
	
	public synchronized void simulate(String filename, String title, int width, int height, String background, 
			int numNetworks, int numMobileNodes) {
		simulate(filename, title, width, height, background,  numNetworks, numMobileNodes,
				Env.RANDOM_MAX_PATH, Env.RANDOM_MAX_STAY);
	}
	
	public synchronized void simulate(String filename, String title, int width, int height, String background, 
			int numNetworks, int numMobileNodes, int maxPath, int maxStay) {
		SimulationThread st = new SimulationThread(
				filename, title, width, height, background,  numNetworks, numMobileNodes, maxPath, maxStay);
		st.start();
	}
	
	
	public synchronized void simulateWork(String filename, String title, int width, int height, String background, 
			int numNetworks, int numMobileNodes, int maxPath, int maxStay) {
		long pTime = System.currentTimeMillis();
		
		//	start progress Dialog
		if (progress == null) {
			if (gui != null) {
    			progress = new ProgressDialog(gui);
			} else if (simulator != null) {
				progress = new ProgressDialog(simulator);
			} else {
				progress = new ProgressCLI();
			}
		}
		progress.start();
		
		if (!filename.endsWith(".xml")) {
			filename = filename + ".xml";
		}
		//
		String descr = "Generated Randomly (Networks:"+numNetworks+", Mobile Nodes: " + numMobileNodes+")";
//		createNetworkMap(title, descr, width, height, background);
		NetworkMapInfo sMapInfo = new NetworkMapInfo(title, descr, width, height, background);
	
		int bs = 1;
		int ap = 1;
		int ras = 1;
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < numNetworks; i++) {
//			String network = dpnm.featuremodel.StaticFeatureModel.getInstance().getFeatures("NetworkInterface")[rand.nextInt(4)];
			String network = NetworkFactory.getInstance().getNetworkNames()[rand.nextInt(
					NetworkFactory.getInstance().getNetworkCount())];
			String deviceType = NetworkFactory.getInstance().getNetwork(network).getDeviceType();
			String name = "N";
			if (deviceType.intern() == "BaseStation") {
				name = "BS"+(bs++);
			} else if (deviceType.intern() == "AccessPoint") {
				name = "AP"+(ap++);
			} else if (deviceType.intern() == "RadioAccessStation") {
				name = "RAS"+(ras++);
			}
    		NetworkDeviceInfo info = new NetworkDeviceInfo(name, network, rand.nextInt(width), rand.nextInt(height));
    		sMapInfo.addNetworkDeviceInfo(info);
			//	progress report
			progress.setNetworkStatus((int)((double)(i+1)/numNetworks*100));
		}
		progress.appendText(numNetworks + " were created.");
		if (Conf.DEBUG) {
			Logger.getInstance().logEmulator("HMNEmulator (Simulation)",
					numNetworks + " were created.");
		}
		for (int i = 0 ; i < numMobileNodes; i++) {
			String name = "MN"+(i+1);
    		Vector<String> apps = new Vector<String>();
    		Vector<String> nis = new Vector<String>();
			int appSize = 1+rand.nextInt(StaticFeatureModel.getInstance().getFeatureCount(StaticFeatureModel.APPLICATION)-1);
			int niSize = 1+rand.nextInt(StaticFeatureModel.getInstance().getFeatureCount(StaticFeatureModel.NETWORKINTERFACE)-1);
			int applist[] = new int[appSize];
			for (int j = 0; j < appSize; j++) {
				applist[j] = -1;
			}
			for (int j = 0; j < appSize; j++) {
				while(true) {
    				int kind = rand.nextInt(StaticFeatureModel.getInstance().getFeatureCount(StaticFeatureModel.APPLICATION));
    				boolean same = false;
    				for (int k = 0; k < j; k++) {
    					if (applist[k] == kind) {
    						same = true;
    					}
    				}
    				if (!same) {
    					applist[j] = kind;
    					break;
    				}
				}
				apps.add(StaticFeatureModel.getInstance().getFeatures(StaticFeatureModel.APPLICATION)[applist[j]]);
			}
			
			/*
			System.out.print(i+"th app: ");
			for (int j = 0; j < appSize; j++) {
				System.out.print(applist[j]+" ");
			}
			System.out.print("\n");
			*/
			int nilist[] = new int[niSize];
			for (int j = 0; j < niSize; j++) {
				nilist[j] = -1;
			}
			for (int j = 0; j < niSize; j++) {
				while(true) {
    				int kind = rand.nextInt(StaticFeatureModel.getInstance().getFeatureCount(StaticFeatureModel.NETWORKINTERFACE));
    				boolean same = false;
    				for (int k = 0; k < j; k++) {
    					if (nilist[k] == kind) {
    						same = true;
    					}
    				}
    				if (!same) {
    					nilist[j] = kind;
    					break;
    				}
				}
				nis.add(StaticFeatureModel.getInstance().getFeatures(StaticFeatureModel.NETWORKINTERFACE)[nilist[j]]);
			}

			String type = MobileNodeFactory.getInstance().getMobileNodeNames()[rand.nextInt(
					MobileNodeFactory.getInstance().getMobileNodeCount())];
    		NetworkMobileNodeInfo nodeInfo = new NetworkMobileNodeInfo(
    				name, apps, nis, rand.nextInt(width), rand.nextInt(height), type);
    		sMapInfo.addNetworkMobileNodeInfo(nodeInfo);
			int numPath = rand.nextInt(maxPath)+2;
			int path[][] = new int[numPath][NetworkMobileNodeInfo.PATH_COUNT];
			path[0][NetworkMobileNodeInfo.PATH_XPOS] = nodeInfo.getXpos();
			path[0][NetworkMobileNodeInfo.PATH_YPOS] = nodeInfo.getYpos();
			//	velocity 는 0이 되어서는 안된다.
			path[0][NetworkMobileNodeInfo.PATH_VELOCITY] = rand.nextInt(
					nodeInfo.getMobileNode().getMaxVelocity() - 
					nodeInfo.getMobileNode().getMinVelocity() 
					) + nodeInfo.getMobileNode().getMinVelocity();
			path[0][NetworkMobileNodeInfo.PATH_VELOCITY] = 
				path[0][NetworkMobileNodeInfo.PATH_VELOCITY] == 0 ? 1 : 
					path[0][NetworkMobileNodeInfo.PATH_VELOCITY];
			path[0][NetworkMobileNodeInfo.PATH_STAY] =
				rand.nextInt(maxStay);
			//	velocity
			for (int j = 1; j < numPath; j++) {
				path[j][NetworkMobileNodeInfo.PATH_XPOS] = rand.nextInt(width); 
				path[j][NetworkMobileNodeInfo.PATH_YPOS] = rand.nextInt(height); 
				path[j][NetworkMobileNodeInfo.PATH_VELOCITY] = rand.nextInt(
					nodeInfo.getMobileNode().getMaxVelocity() - 
					nodeInfo.getMobileNode().getMinVelocity()
					) + nodeInfo.getMobileNode().getMinVelocity();
    			path[j][NetworkMobileNodeInfo.PATH_VELOCITY] = 
    				path[j][NetworkMobileNodeInfo.PATH_VELOCITY] == 0 ? 1 : 
    					path[j][NetworkMobileNodeInfo.PATH_VELOCITY];
    			path[j][NetworkMobileNodeInfo.PATH_STAY] =
    				rand.nextInt(maxStay);
			//	velocity
			}
			nodeInfo.setPath(path);
			
			progress.setMobileNodeStatus((int)((double)(i+1)/numMobileNodes*100));
		}
		progress.appendText(numMobileNodes + " were created.");
		if (Conf.DEBUG) {
			Logger.getInstance().logEmulator("HMNEmulator (Simulation)",
        		numMobileNodes + " were created.");
		}
		saveNetworkMap(sMapInfo, filename);
		if (Conf.DEBUG) {
			Logger.getInstance().logEmulator("HMNEmulator (Simulation)",
    		filename + " was created");
		}
		
		String msg = 
        	title + " ("+numNetworks+" Network(s), "+numMobileNodes+" Mobile Node(s)) was created successfully.\n"+
        	"File name: " + filename+"\n"+
        	"Total Time: " + (System.currentTimeMillis()-pTime) + "ms";
		progress.appendText(msg);
		progress.appendText("Done!");
		if (gui != null || simulator != null) {
            JOptionPane.showMessageDialog(null,
            		msg,
    			"Creation Result",
    			JOptionPane.INFORMATION_MESSAGE);
		}
        progress.stop();
	}
	
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(Env.DEFAULT_LOOKANDFEEL);
		} catch (Exception e) {}
		HMNEmulator emulator = new HMNEmulator();
		
		if (args.length == 1 && args[0].intern() == "-s") {
			emulator.launchSimulator();
		} else if (args.length == 1 && args[0].intern() == "-c") {
			emulator.launchSimulatorCLI();
		} else if (args.length == 1) {
    		emulator.initGUI();
    		emulator.openNetworkMap("data/"+args[0]);
    		emulator.showGUI();
		} else {
    		emulator.initGUI();
//    		emulator.openNetworkMap("data/test.xml");
    		emulator.showGUI();
//		emulator.simulate("data/test_random.xml", "Test Map", "Test", 1600, 1200, "ut_austin.png", 10, 10);
		}
	}
	
	public class SimulationThread extends Thread {
		
		String filename; String title; int width; int height; String background; int numNetworks; int numMobileNodes;
		int maxPath; int maxStay;
		public SimulationThread(String filename, String title, int width, int height, String background, 
			int numNetworks, int numMobileNodes, int maxPath, int maxStay) {
			this.filename = filename;
			this.title = title;
			this.width = width;
			this.height = height;
			this.background = background;
			this.numNetworks = numNetworks;
			this.numMobileNodes = numMobileNodes;
			this.maxPath = maxPath;
			this.maxStay = maxStay;
		}
		public void run() {
			simulateWork(filename, title, width, height, background,  numNetworks, numMobileNodes,
					maxPath, maxStay);
		}
	}
	
	void initLogging(NetworkMapInfo mapInfo, String appName) {
		try {
    		File dFile = new File(Resources.LOG);
    		if (!dFile.exists()) {
    			dFile.mkdir();
    		}
			String name = mapInfo.getName();
			File mdFile = new File(Resources.LOG+File.separator+name);
			if (!mdFile.exists()) {
				mdFile.mkdir();
			}
			
			log = new File[mapInfo.getMnodes().length];
			logTable = new File[mapInfo.getMnodes().length];
			logGraph = new File[mapInfo.getMnodes().length];
			logGraphcsv = new File[mapInfo.getMnodes().length];
			NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
			for (int i = 0; i < log.length; i++) {
				File nodeDir = new File(
						mdFile.getAbsolutePath()+File.separator+nodeInfo[i].getDevice().getId());
				if (!nodeDir.exists()) {
					nodeDir.mkdir();
				}
				String logStr = nodeDir.getAbsolutePath()+File.separator+appName+"_"+
						nodeInfo[i].getDevice().getPolicyManager().getPolicy(appName).toFileName();
				log[i] = new File(logStr+".txt");
				if (log[i].exists()) {
					log[i].delete();
				}
				logTable[i] = new File(logStr+"_Table.txt");
				if (logTable[i].exists()) {
					logTable[i].delete();
				}
				logGraph[i] = new File(logStr+"_Graph.m");
				if (logGraph[i].exists()) {
					logGraph[i].delete();
				}
				logGraphcsv[i] = new File(logStr+"_Graph.csv");
				if (logGraphcsv[i].exists()) {
					logGraphcsv[i].delete();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	//	LOG DATA for ANALYSIS
	//
	//	Timestamp | MN_x | MN_y | Speed | CN List | SCN List filtered by Speed Filter
	//	SSCN List filtered by SLA filter
	//	APAV_RSS of SSCN List | APAV_Cost of SSCN List | APAV_Quality of SSCN List
	//	APAV_Lifetime of SSCN List
	//	APSV of SSCN List
	//	Selected best AP
	void logging(NetworkMapInfo mapInfo, long timestamp) {
		try {
			NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
			for (int i = 0; i < log.length; i++) {
				FileWriter writer = new FileWriter(log[i], true);
				writer.write(timestamp+"|");					// timestamp
				writer.write(nodeInfo[i].getLog(timestamp)+"\r\n");
				writer.close();
			}
		}
		catch(Exception e) { 
			e.printStackTrace();
		}
	}
	
	public void loggingTable(NetworkMapInfo mapInfo) {
		try {
			NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
			for (int i = 0; i < log.length; i++) {
				MobileApplication apps = nodeInfo[i].getDevice().getApplicationManager().getCurrentApplication();
				if (apps != null) {
					FileWriter writer = new FileWriter(logTable[i]);
						writer.write(apps.getName()+"|"+
						nodeInfo[i].getDevice().getPolicyManager().getPolicy(apps.getName()).toFileName()+"\r\n");
						writer.write(nodeInfo[i].loggingTable());
						writer.close();
						writer = null;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public File[] loggingGraph(NetworkMapInfo mapInfo) {
		try {
			NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
			for (int i = 0; i < logGraphcsv.length; i++) {
				MobileApplication apps = nodeInfo[i].getDevice().getApplicationManager().getCurrentApplication();
				if (apps == null)
					continue;
				FileWriter writer = new FileWriter(logGraph[i]);
				FileWriter writer2 = new FileWriter(logGraphcsv[i]);
				String name = apps.getName()+"_"+
				nodeInfo[i].getDevice().getPolicyManager().getPolicy(
						apps.getName()).getAction().getDecisionAlgorithmString();
//					System.out.println(nodeInfo[i].loggingGraph(name));
				writer.write(nodeInfo[i].loggingGraph(name));
				nodeInfo[i].loggingGraphcsv(name, writer2);
				writer.close();
				writer2.close();
				writer = null;
				writer2 = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return logGraphcsv;
	}
}
