package dpnm.tool;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dpnm.Conf;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.event.NetworkHandoverEvent;
import dpnm.mobiledevice.event.NetworkHandoverListener;
import dpnm.mobiledevice.policy.ruleobjects.Rule;
import dpnm.network.device.NetworkDevice;
import dpnm.tool.HMNNetworkSimulator.SimulationTask;
import dpnm.tool.comp.ZFileFilter;
import dpnm.tool.data.NetworkDeviceInfo;
import dpnm.tool.data.NetworkMapInfo;
import dpnm.tool.data.NetworkMobileNodeInfo;
import dpnm.util.Log;
import dpnm.util.UpdateTimer;

public class HMNNetworkSimulatorCLI implements NetworkHandoverListener {

	private HMNEmulator owner = null;
	private NetworkMapInfo mapInfo = null;
	private File mapFile = null;
	
	private long interval = 0;
	private double speedRate = 1.0;
	
	private NetworkPlayer player = null;
	
	private long duration = 0;
	private long end = 0;
	
	/*	Timer */
	private Timer timer = null;
	private TimerTask task = null;
	
	private Object comnetTask = new Object();
	private boolean taskEnd = false;
	
	private File handoverLog = null;
	private SimpleDateFormat sdf = null;
	
	public HMNNetworkSimulatorCLI(HMNEmulator owner) {
		this.owner = owner;
		
		player = new NetworkPlayer();
		sdf = new SimpleDateFormat("yyMMdd HHmmss.SSS");
		timer = dpnm.util.UpdateTimer.getInstance().getTimer();
		startCLI();
	}
	
	void startCLI() {
		printMainMenu();
		int cmd = getUserInput();
		while(cmd != 1 && cmd != 2 && cmd != 3 && cmd != 4) {
			System.out.println("Invalid Input");
			printMainMenu();
			cmd = getUserInput();
		}
		if (cmd == 1) {
			selectNetworkMap();
		} else if (cmd == 2) {
			randomGenerate();
			startCLI();
		} else if (cmd == 3) {
			comnet();
		} else if (cmd == 4) {
			System.out.println("Bye~");
			System.exit(1);
		}
	}
	
	private void printMainMenu() {
		System.out.println("--------------------------");
		System.out.println(" HMNNetwork Simulator CLI");
		System.out.println("--------------------------");
		System.out.println("1. Open Network Map");
		System.out.println("2. Random Generation");
		System.out.println("3. ComNet Experiment");
		System.out.println("4. Exit");
	}
	
	private void printSubMenu() {
		System.out.println("--------------------------");
		System.out.println(" HMNNetwork Simulator CLI");
		System.out.println("--------------------------");
		System.out.println("1. Run Network");
		System.out.println("2. Stop Network");
		System.out.println("3. Print Status");
		System.out.println("4. Save Satatus");
		System.out.println("5. Change Speed Rate");
		System.out.println("6. Back to Main Menu");
	}
	
	private int getUserInput() {
		return getUserInput("Select: ");
	}
	
	private int getUserInput(String msg) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean isValidResponse = false;
		int input = 0;
		while(!isValidResponse) {
    		System.out.print(msg);
    		try {
    			String cmd = br.readLine();
    			input = Integer.parseInt(cmd);
    			isValidResponse = true;
    		} catch (Exception ex) {
    			System.out.println("Invalid Input");
    			isValidResponse = false;
    		}
		}
		return input;
	}
	
	private void changeSpeedRate() {
		System.out.println("Change Speed Rate (current="+speedRate+")");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			System.out.print("rate = ");
			String rate = br.readLine();
			speedRate = Double.parseDouble(rate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
		
	private void randomGenerate() {
		System.out.println("Random Generation of Network Map");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
    		String nameStr[] = {"Name", "Width", "Height", "# of Networks", "# of Mobile Nodes",
    				"Max Path", "Max Stay time"};
    		String valueStr[] = {"", "1024", "768", "40", "100", String.valueOf(Env.RANDOM_MAX_PATH),
    				String.valueOf(Env.RANDOM_MAX_STAY)};
			String values[] = new String[nameStr.length];

			for (int i = 0; i < nameStr.length; i++) {
				System.out.println(nameStr[i] +"(default:"+valueStr[i]+")");
				System.out.print("? ");
    			values[i] = br.readLine();
				System.out.println(values[i]);
    			if (values[i].charAt(0) == '\r') {
    				System.out.println("enter");
    			}
			}
			
			System.out.println("Background:");
			ZFileFilter filter = new ZFileFilter();
    		filter.ignoreDirectory(true);
			filter.addExtension("png");
			filter.addExtension("jpg");
			filter.addExtension("gif");
	
			File dNode = new File(Resources.HOME+File.separator+Resources.MAP_DIR);
    		filter.ignoreDirectory(true);
    		File backFiles[] = dNode.listFiles(filter);
    		
    		for (int i = 0; i < backFiles.length; i++) {
    			System.out.println("["+i+"] " + backFiles[i].getName());
    		}
			System.out.print("Select? ");
			String background = backFiles[Integer.parseInt(br.readLine())].getName();
			
			System.out.println("File Name : ");
			String fileName = new File(Resources.DATA_DIR+File.separator+br.readLine()).getAbsolutePath();
			
			if (!fileName.endsWith(".xml")) {
				fileName = fileName + ".xml";
			}
 
			owner.simulate(fileName, values[0], Integer.parseInt(values[1]), 
            		Integer.parseInt(values[2]), background, 
            		Integer.parseInt(values[3]), 
            		Integer.parseInt(values[4]),
            		Integer.parseInt(values[5]),
            		Integer.parseInt(values[6])
            		);	           
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void selectNetworkMap() {
		File dNode = new File(Resources.DATA_DIR);
		
		ZFileFilter filter = new ZFileFilter();
		filter.ignoreDirectory(true);
		filter.addExtension("xml");
		filter.ignoreDirectory(true);
		File mapFiles[] = dNode.listFiles(filter);
		
		System.out.println("Network Map List");
		for (int i = 0; i < mapFiles.length; i++) {
			System.out.println("["+i+"] " + mapFiles[i].getName());
		}
		
		int mapIndex = getUserInput();
		while(mapIndex > mapFiles.length-1) {
			System.out.println("Invalid Input");
			mapIndex = getUserInput();
		}
		
		interval = (long)getUserInput("Time Interval (ms): ");
		
		reset();
		loadNetworkMapInfo(mapFiles[mapIndex]);
		
		while(true) {
			printSubMenu();
			int cmd = getUserInput();
			switch(cmd) {
			case 1: startSimulation(); break;
			case 2: stopSimulation(); break;
			case 3: printStatus(); break;
			case 4: saveStatus(); break;
			case 5: changeSpeedRate(); break;
			case 6: break;
			default:
				System.out.println("Invalid Input");
			}
			if (cmd == 6)
				break;
		}
		startCLI();
	}
	void loadNetworkMapInfo(File mapFile) {
		this.mapFile = mapFile;
		mapInfo = owner.getNetworkMapInfo(mapFile);
    	player.loadNetworkMapInfo(mapInfo);
    	NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
    	if (nodeInfo != null) {
        	for (int i = 0; i < nodeInfo.length; i++) {
                nodeInfo[i].initializeMove();
                nodeInfo[i].getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        	}
    	}
	}
	void reset() {
		if (mapInfo != null) {
			NetworkMobileNodeInfo nodeInfo[] = mapInfo.getMnodes();
			if (nodeInfo != null) {
				for (int i = 0; i < nodeInfo.length; i++) {
					nodeInfo[i].getDevice().getNetworkInterfaceManager().removeNetworkHandoverListener(this);
				}
			}
		}
		player.resetData();
		mapInfo = null;
		duration = 0;
	}
	
	private void printStatus() {
		System.out.println("Current Status");
		System.out.println("Duration: " + duration);
		System.out.println(mapInfo.getNetworkStatus());
		System.out.println(mapInfo.getMobleNodeStatus());
	}
	
	private void saveStatus() {
		try {
    		File dFile = new File(Resources.LOG);
    		if (!dFile.exists()) {
    			dFile.mkdir();
    		}
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String cd = df.format(new Date(System.currentTimeMillis()));
			String name = mapFile.getName().substring(0, mapFile.getName().length()-4);
    		FileWriter fw = new FileWriter(Resources.LOG+File.separator+cd+"_"+name+"_cli_log.txt");
    		fw.write("Current Status ("+ cd+")\r\n");
    		fw.write("Duration: " + duration+"\r\n");
    		fw.write(mapInfo.getNetworkStatus());
        	fw.write(mapInfo.getMobleNodeStatus());
    		fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void startSimulation() {
    	initRecord();
		
		interval = (long)getUserInput("Time Interval (ms): ");
		end = (long)getUserInput("End Time (ms): ");
    	NetworkMobileNodeInfo nodeInfo[] = mapInfo.getMnodes();
		
		System.out.println("-----------------------------------------------");
		System.out.println("Select the starting Applications and its policy");
		System.out.println("-----------------------------------------------");
		
		String apps[] = new String[nodeInfo.length];
		Rule policies[] = new Rule[nodeInfo.length];
		for (int i = 0; i < nodeInfo.length; i++) {
	        System.out.println("Node: " + nodeInfo[i].getDevice().getId());

			for (int j = 0; j < nodeInfo[i].getDevice().getApplications().length; j++) {
				System.out.println("["+j+"] " + nodeInfo[i].getDevice().getApplications()[j].getName());
			}
            apps[i] = nodeInfo[i].getDevice().getApplications()[getUserInput()].getName();

			for (int j = 0; j < nodeInfo[i].getDevice().getPolicies().length; j++) {
				System.out.println("["+j+"] " + nodeInfo[i].getDevice().getPolicies()[j]);
			}
            policies[i] = nodeInfo[i].getDevice().getPolicies()[getUserInput()];
		}

    	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
    		nodeInfo[i].getDevice().getPolicyManager().setCurrentPolicy(policies[i]);
    	}
    	owner.initLogging(mapInfo, apps[0]);

		try {
	   		task = new SimulationTask();
			timer.schedule(task, 0, interval);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		player.start();
		
    	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
    		nodeInfo[i].getDevice().getApplicationManager().startApplication(apps[i]);
    		nodeInfo[i].getDevice().updateApplication();
    	}
	}
	
	public void stopSimulation() {
	   	//	stop any application 
		if (mapInfo != null) {
        	mapInfo.resetData();
		}
		if (timer == null || task == null) {
			return;
		}
		try {
			task.cancel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		player.stop();
//		UpdateTimer.getInstance().removeUpdateListener(this);
	}
	
	public class SimulationTask extends TimerTask {
    	public synchronized void run() {
    		if (mapInfo == null) {
    			return;
    		}
			NetworkDeviceInfo deviceInfo[] = mapInfo.getDevices();
			for (int i = 0; i < deviceInfo.length; i++) {
				deviceInfo[i].getData().setCurrentTime(duration);
			}
    		//	fire event
			for (int i = 0; i < mapInfo.getMnodes().length; i++) {
				mapInfo.getMnodes()[i].move(interval, speedRate);
			}
			if (dpnm.Conf.LOG){
				owner.logging(mapInfo, duration);
			}
			//	update duration timer
			duration+=interval;
			if (duration > end) {
				taskEnd = true;
			}

    	}
	}

	private void initRecord() {
		try {
    		File dFile = new File(Resources.LOG);
    		if (!dFile.exists()) {
    			dFile.mkdir();
    		}
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String cd = df.format(new Date(System.currentTimeMillis()));
			String name = mapFile.getName().substring(0, mapFile.getName().length()-4);
			handoverLog = new File(Resources.LOG+File.separator+cd+"_"+name+"_handover_log.txt");
    		String log = sdf.format(new Date()) + " [HANDOVER LOG STARTED]";
    		FileWriter writer = new FileWriter(handoverLog, false);
    		writer.write(log+"\r\n");
    		writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void addRecord(String str) {
		String log = sdf.format(new Date()) + " : " + str;
		try {

			FileWriter writer = new FileWriter(handoverLog, true);
			writer.write(log+ "\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
	}

	public void handover(NetworkHandoverEvent evt) {
		StringBuffer sb = new StringBuffer();
		if (evt.getType() == NetworkHandoverEvent.HORIZONTAL_HANDOVER) {
    		sb.append(" [H] ");
		} else {
    		sb.append(" [V] ");
		}
		sb.append(" {" + evt.getHost().getId()+"} ");
		NetworkDevice prev = (NetworkDevice)evt.getPrevNetwork();
		NetworkDevice next = (NetworkDevice)evt.getNextNetwork();
		sb.append(prev.getName() +"("+prev.getNetworkStr()+") -> ");
		sb.append(next.getName() +"("+next.getNetworkStr()+")\r\n");
		addRecord(sb.toString());
	}
	
	public void comnet() {
		File dNode = new File(Resources.DATA_DIR);
		
		ZFileFilter filter = new ZFileFilter();
		filter.ignoreDirectory(true);
		filter.addExtension("xml");
		filter.ignoreDirectory(true);
		File mapFiles[] = dNode.listFiles(filter);
		
		System.out.println("Network Map List");
		for (int i = 0; i < mapFiles.length; i++) {
			System.out.println("["+i+"] " + mapFiles[i].getName());
		}
		System.out.println("["+mapFiles.length+"] Next Step");
		
		Vector<File> maps = new Vector<File>();
		int mapIndex = 0;
			while(true) { 
				mapIndex = getUserInput();
				while(mapIndex > mapFiles.length) {
					System.out.println("Invalid Input");
					mapIndex = getUserInput();
				}
				if (mapIndex == mapFiles.length) 	
					break;
				maps.add(mapFiles[mapIndex]);
			}
		
		interval = (long)getUserInput("Time Interval (sec): ") * 1000;
		end = (long)getUserInput("End Time (sec): ") * 1000;

		for (int i = 0; i < maps.size(); i++) {
			System.out.println("Network Simulation: " + maps.elementAt(i).getName());
			loadNetworkMapInfo(maps.elementAt(i));
			startSimulationAll();
		}
		
		while(true) {
			printSubMenu();
			int cmd = getUserInput();
			switch(cmd) {
			case 1: startSimulation(); break;
			case 2: stopSimulation(); break;
			case 3: printStatus(); break;
			case 4: saveStatus(); break;
			case 5: changeSpeedRate(); break;
			case 6: break;
			default:
				System.out.println("Invalid Input");
			}
			if (cmd == 6)
				break;
		}
		startCLI();
	}
	
	public void startSimulationAll() {
    	initRecord();
    	NetworkMobileNodeInfo nodeInfo[] = mapInfo.getMnodes();
		
		String apps[][] = new String[nodeInfo.length][];
		Rule policies[][] = new Rule[nodeInfo.length][];
		for (int i = 0; i < nodeInfo.length; i++) {
			apps[i] = new String[nodeInfo[i].getDevice().getApplications().length];
			for (int j = 0; j < nodeInfo[i].getDevice().getApplications().length; j++) {
				apps[i][j] = nodeInfo[i].getDevice().getApplications()[j].getName();
			}

			policies[i] = nodeInfo[i].getDevice().getPolicies();
		}

		//	do all simulations for apps and policies
		for (int i = 0; i < nodeInfo.length; i++) {
			for (int j = 0; j < apps[i].length; j++) {
				File[][] sum = new File[policies[i].length][];
				for (int k = 0; k < policies[i].length; k++) {
					System.out.println("Simulation Start ("+mapInfo.getName()+ " - " + apps[i][j] + ", " +
							policies[i][k]+")");
					//	app = app[i][j];
					//	policy = policy [i][k];
			    	nodeInfo = mapInfo.getMnodes();
					for (int t = 0; nodeInfo != null && t < nodeInfo.length; t++) {
			    		nodeInfo[t].getDevice().getPolicyManager().setCurrentPolicy(policies[i][k]);
			    	}
		    		taskEnd = false;

			    	owner.initLogging(mapInfo, apps[i][j]);

					task = new SimulationTask();
					try {
						timer.schedule(task, 0, interval);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
			    	for (int t = 0; nodeInfo != null && t < nodeInfo.length; t++) {
			    		nodeInfo[t].getDevice().getApplicationManager().startApplication(apps[i][j]);
			    		nodeInfo[t].getDevice().updateApplication();
			    	}
					player.start();
			    	
			    	try {
			    		Thread.sleep(end);
			    		while(!taskEnd) {
				    		System.out.println("sleep: " + taskEnd);

			    			Thread.sleep(2000);
			    		}
			    		System.out.println("Wake up!!!");
						if (Conf.LOG){
//							owner.loggingTable(mapInfo);
							sum[k] = owner.loggingGraph(mapInfo);
						}
			    		stopSimulation();
			    		duration = 0;
//			    		loadNetworkMapInfo(mapFile);
			    	} catch (Exception ex) {
			    		ex.printStackTrace();
			    	}
				}
				try {
					summary(sum, mapInfo.getMnodes().length, apps[i][j], policies[i], 1);
					summary(sum, mapInfo.getMnodes().length, apps[i][j], policies[i], 5);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	public void comnet2() {
		File dNode = new File(Resources.DATA_DIR);
		
		ZFileFilter filter = new ZFileFilter();
		filter.ignoreDirectory(true);
		filter.addExtension("xml");
		filter.ignoreDirectory(true);
		File mapFiles[] = dNode.listFiles(filter);
		
		System.out.println("Network Map List");
		for (int i = 0; i < mapFiles.length; i++) {
			System.out.println("["+i+"] " + mapFiles[i].getName());
		}
		
		int mapIndex = getUserInput();
		while(mapIndex > mapFiles.length-1) {
			System.out.println("Invalid Input");
			mapIndex = getUserInput();
		}
		
		interval = 1000;
		
		reset();
		loadNetworkMapInfo(mapFiles[mapIndex]);

		end = 652000;
		
    	NetworkMobileNodeInfo nodeInfo[] = mapInfo.getMnodes();

		String apps[] = new String[nodeInfo.length];
		Rule policies[] = new Rule[nodeInfo.length];
		for (int i = 0; i < nodeInfo.length; i++) {
	        System.out.println("Node: " + nodeInfo[i].getDevice().getId());

			for (int j = 0; j < nodeInfo[i].getDevice().getApplications().length; j++) {
				System.out.println("["+j+"] " + nodeInfo[i].getDevice().getApplications()[j].getName());
			}
            apps[i] = nodeInfo[i].getDevice().getApplications()[getUserInput()].getName();

			for (int j = 0; j < nodeInfo[i].getDevice().getPolicies().length; j++) {
				System.out.println("["+j+"] " + nodeInfo[i].getDevice().getPolicies()[j]);
			}
            policies[i] = nodeInfo[i].getDevice().getPolicies()[getUserInput()];
		}

    	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
    		nodeInfo[i].getDevice().getPolicyManager().setCurrentPolicy(policies[i]);
    	}
    	owner.initLogging(mapInfo, apps[0]);

		task = new SimulationTask();
		try {
			timer.schedule(task, 0, interval);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		player.start();
		
    	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
    		nodeInfo[i].getDevice().getApplicationManager().startApplication(apps[i]);
    		nodeInfo[i].getDevice().updateApplication();
    	}
		
		while(true) {
			printSubMenu();
			int cmd = getUserInput();
			switch(cmd) {
			case 1: startSimulation(); break;
			case 2: stopSimulation(); break;
			case 3: printStatus(); break;
			case 4: saveStatus(); break;
			case 5: changeSpeedRate(); break;
			case 6: break;
			default:
				System.out.println("Invalid Input");
			}
			if (cmd == 6)
				break;
		}
		startCLI();
	}
	
	public void summary(File[][] f, int node, String app, Rule[] rule, int sample) throws Exception {
		String[] value = {"APSV"};

		File[][] output = new File[value.length][];
		FileWriter[][] writer = new FileWriter[value.length][];
		for (int j = 0; j < value.length; j++) {
			output[j] = new File[node];
			writer[j] = new FileWriter[node];
			for (int i = 0; i < node; i++) {
				String name = mapInfo.getMnodes()[i].getDevice().getId();
				output[j][i] = new File(f[0][i].getParentFile().getParent()+File.separator+name+"_"+app+"_"+
						rule[0].getAction().getUserProfile().toFileName()+"_"+value[j]+"_"+sample+".csv");
				writer[j][i] = new FileWriter(output[j][i]);
				writer[j][i].write("Time");
				for (int k = 0; k < rule.length; k++) {
					writer[j][i].write(","+rule[k].getAction().getDecisionAlgorithmTitle());
				}
				writer[j][i].write("\r\n");
			}
		}

		for (int n = 0; n < node; n++) {
			Scanner[] scan = new Scanner[f.length];
			for (int j = 0; j < f.length; j++) {
				scan[j] = new Scanner(f[j][n]);
				scan[j].next();
			}
			int count = 0;
			while(scan[0].hasNext()) {

				for (int j = 0; j < scan.length; j++) {
					String s = scan[j].next();
					if (count%sample != 0)
						continue;
					StringTokenizer st = new StringTokenizer(s,",");
					//time
					Long time = Long.parseLong(st.nextToken());
					for (int k = 0; k < writer.length; k++) {
						if (j == 0) {
							writer[k][n].write(time+",");
						}
						writer[k][n].write(st.nextToken()+",");
					}
				}
				if (count%sample == 0)
					for (int k = 0; k < writer.length; k++) {
						writer[k][n].write("\r\n");
					}
				
				count++;
			}
		}
		for (int i = 0; i < writer.length; i++) {
			for (int j = 0; j < writer[i].length; j++) {
				writer[i][j].close();
			}
		}

	}
	
}