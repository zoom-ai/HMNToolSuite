package dpnm.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;

import dpnm.tool.comp.SignalComponent;
import dpnm.tool.data.*;
import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.MobileDevice;
import dpnm.mobiledevice.NetworkInterface;
import dpnm.mobiledevice.NetworkProperty;
import dpnm.mobiledevice.StateManager;
import dpnm.mobilenode.MobileNodeFactory;
import dpnm.network.*;
import dpnm.network.device.*;
import dpnm.util.*;

public class MobileNodeMonitorView extends JPanel {
	private static final int CANDIDATE_NETWORK_HEIGHT = 300;
	private static final String MOBILENODE_HEADERS[] = {
	"Device", "Type", "Available Network(s)", "Selected Network(s)"
	};
	
    private NetworkMobileNodeInfo mNodeInfo[];
	
	private JTabbedPane mainPane;
	private JPanel mNodeView;
	private JTable mNodeTable;
	private JTextArea mNodeArea;
	
	private JPanel deviceView[];
	
	
	public MobileNodeMonitorView() {
		this(null);
	}
	
	public MobileNodeMonitorView(NetworkMobileNodeInfo mNodeInfo[]) {
		mainPane = new JTabbedPane();
        mainPane.setPreferredSize(new Dimension(Env.MONITOR_WIDTH/2-20, Env.MONITOR_HEIGHT-60));
		add(mainPane);
		setMobileNodeInfo(mNodeInfo);
	}
	
	public void setMobileNodeInfo(NetworkMobileNodeInfo mNodeInfo[]) {
		this.mNodeInfo = mNodeInfo;
		if (mNodeInfo != null) {
			createUI();
		}
    	showCurrentMobileDeviceStatus();
	}
	
	private synchronized void createUI() {
		mNodeView = new JPanel();
        mNodeArea = new JTextArea();
        mNodeArea.setEditable(false);
        mNodeArea.setFont(Resources.DIALOG_12);
    	JScrollPane scrollPane = new JScrollPane(mNodeArea);
        mainPane.addTab("Mobile Node(s)", scrollPane);
		
        deviceView = new JPanel[mNodeInfo.length];
        for (int i = 0; i < mNodeInfo.length; i++) {
        	deviceView[i] = new MobileDeviceView(mNodeInfo[i].getDevice());
        	mainPane.addTab(mNodeInfo[i].getDevice().getId(), deviceView[i]);
        	mainPane.setToolTipTextAt(i+1, mNodeInfo[i].getDevice().getId() + "("+
        			mNodeInfo[i].getMobileNode().getName()+")");
        	mainPane.setIconAt(i+1, 
   					HMNEmulatorGUI.getResizedImage(
        					Resources.MOBILENODE_DIR+File.separator+
        							mNodeInfo[i].getMobileNode().getIconStr(), 16, 16));
        }
	}
	
	void showCurrentMobileDeviceStatus() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; mNodeInfo != null && i < mNodeInfo.length; i++) {
			sb.append(mNodeInfo[i].getStatus());
			sb.append("---------------------------------------------\n\n");
		}
		mNodeArea.setText(sb.toString());
	}	
	
	public void exit() {
	}
	

	class MobileDeviceView extends JPanel {
		private MobileDevice device = null;
		
		JPanel 		contentPane 	= null;
		JPanel 		mainPane 		= null;
		JPanel 		networkPane 	= null;
		JPanel 		appPane 		= null;
		
		JButton		powerBtn		= null;
		
		JRadioButton appBtn[]		= null;
		ButtonGroup appGrp			= null;
		
		JRadioButton networkBtn[]	= null;
		ButtonGroup networkGrp		= null;
		
		JPanel		candidateNetworkPane[] = null;
		
		
		public MobileDeviceView(MobileDevice device) {
			this.device = device;
			
			//	create content panel
			setLayout(new BorderLayout());
//			add(createPowerPane(), BorderLayout.NORTH);
			add(createMainPane(), BorderLayout.CENTER);
			powerOn();
		}
		
		public void powerOff() {
//			mainPane.setVisible(false);
			
			for (int i = 0; appBtn != null && i < appBtn.length; i++) {
				appBtn[i].setSelected(false);
			}
			
			for (int i = 0; networkBtn != null && i < networkBtn.length; i++) {
				networkBtn[i].setSelected(false);
			}
		}
		
		public void powerOn() {
//			mainPane.setVisible(true);
			if (appBtn != null) {
				appBtn[0].setSelected(true);
			}
			if (networkBtn != null) {
				networkBtn[0].setSelected(true);
			}
		}
		
    	protected JPanel createMainPane() {
			mainPane = new JPanel();
//			mainPane.setLayout(new GridLayout(1,2));
			mainPane.setLayout(new BorderLayout());
			
		  	mainPane.add(createAppPane(), BorderLayout.PAGE_START);
		  	mainPane.add(createNetworkPane(), BorderLayout.CENTER);
		  	
			return mainPane;
		}
		
		protected JPanel createAppPane() {
			appPane = new JPanel();
//			appPane.setBounds(10,0,Env.SIMULATOR_WIDTH/2-40,80);
			appPane.setPreferredSize(new Dimension(Env.SIMULATOR_WIDTH/2-40,90));
			appPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//			appPane.setLayout(new BorderLayout());
			appPane.setLayout(null);
			/*
			((FlowLayout)appPane.getLayout()).setAlignment(FlowLayout.LEFT);
			((FlowLayout)appPane.getLayout()).setHgap(20);
			((FlowLayout)appPane.getLayout()).setVgap(5);
			*/
			JLabel label = new JLabel("Application");
			label.setBounds(100,0,100,20);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			appPane.add(label);
			
			JPanel cPane = new JPanel(new FlowLayout());
			cPane.setBounds(10, 20, 400, 60);
//			cPane.setBounds(20,20,Env.SIMULATOR_WIDTH/2-40,60);
			((FlowLayout)cPane.getLayout()).setAlignment(FlowLayout.LEFT);
			if (device.getApplications() != null) {
				MobileApplication applications[] = device.getApplications();
	    		appGrp = new ButtonGroup();
	    		appBtn = new JRadioButton[applications.length + 1];
	    		for (int i = 0; i < applications.length+1; i++) {
	    			if (i == 0) {	//	NONE
	    				appBtn[i] = new JRadioButton("NONE");
	    				appBtn[i].setActionCommand("AppNone");
	    			} else {
	        			appBtn[i] = new JRadioButton(applications[i-1].getName());
	        			appBtn[i].setActionCommand(applications[i-1].getName());
	    			}
//	    			appBtn[i].addActionListener(this);
	    			appBtn[i].setEnabled(false);
	    			appGrp.add(appBtn[i]);
	    			cPane.add(appBtn[i]);
	    		}
			}
			appPane.add(cPane);
			return appPane;	
		}
		
		protected JPanel createNetworkPane() {
			networkPane = new JPanel();
			networkPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			JPanel titlePane = new JPanel();
			networkPane.setLayout(new BorderLayout());
			/*
			((FlowLayout)networkPane.getLayout()).setAlignment(FlowLayout.LEFT);
			((FlowLayout)networkPane.getLayout()).setHgap(20);
			((FlowLayout)networkPane.getLayout()).setVgap(5);
			*/
			titlePane.add(new JLabel("Network Interface"));
			networkPane.add(titlePane, BorderLayout.NORTH);
	    	
			JPanel cPane = new JPanel();
			/*
			((FlowLayout)cPane.getLayout()).setAlignment(FlowLayout.LEFT);
			((FlowLayout)cPane.getLayout()).setHgap(40);
			((FlowLayout)cPane.getLayout()).setVgap(5);
			*/
			
	    	if (device.getNetworkInterfaces() != null) {
	    		NetworkInterface networkInterfaces[] = device.getNetworkInterfaces();
	        	networkGrp = new ButtonGroup();
	        	networkBtn = new JRadioButton[networkInterfaces.length+1];
	        	for (int i = 0; i < networkInterfaces.length+1; i++) {
	        		if (i == 0) {
	            		networkBtn[i] = new JRadioButton("NONE");
	    				networkBtn[i].setActionCommand("NetworkInterfaceNone");
	        		} else {
	            		networkBtn[i] = new JRadioButton(networkInterfaces[i-1].getName());
	            		networkBtn[i].setActionCommand(networkInterfaces[i-1].getName());
	        		}
//	        		networkBtn[i].addActionListener(this);
	        		networkBtn[i].setEnabled(false);
	        		networkGrp.add(networkBtn[i]);
	        		cPane.add(networkBtn[i]);
	        	}
	    	}
        	networkPane.add(cPane, BorderLayout.NORTH);
        	createCandidateNetworkPane();
			return networkPane;
		}
		
		public void createCandidateNetworkPane() {
			JPanel pane = new JPanel();
			pane.setLayout(null);
	    	if (device.getNetworkInterfaces() != null) {
	    		NetworkInterface networkInterfaces[] = device.getNetworkInterfaces();
	    		candidateNetworkPane = new JPanel[networkInterfaces.length];
	        	for (int i = 0; i < networkInterfaces.length; i++) {
        	    	JTabbedPane mPane = new JTabbedPane();
        	    	if (i%2 == 0) {
            	    	mPane.setBounds(0, (i/2)*CANDIDATE_NETWORK_HEIGHT, 230,CANDIDATE_NETWORK_HEIGHT);
        	    	} else {
            	    	mPane.setBounds(232, (i/2)*CANDIDATE_NETWORK_HEIGHT, 230,CANDIDATE_NETWORK_HEIGHT);
        	    	}
	        		candidateNetworkPane[i] = new JPanel();
	        		candidateNetworkPane[i].setLayout(null);
//        			((FlowLayout)candidateNetworkPane[i].getLayout()).setAlignment(FlowLayout.LEFT);
//        			((FlowLayout)candidateNetworkPane[i].getLayout()).setHgap(20);
//        			((FlowLayout)candidateNetworkPane[i].getLayout()).setVgap(5);
	        		mPane.addTab(networkInterfaces[i].getName(), 
	        				new JScrollPane(candidateNetworkPane[i]));
    	        	pane.add(mPane);
	        	}
    	    	int height = CANDIDATE_NETWORK_HEIGHT * (int)(((double)networkInterfaces.length/2)+ 0.5);
    	    	pane.setPreferredSize(new Dimension(460, height));
	    	}
	    	
	    	networkPane.add(new JScrollPane(pane), BorderLayout.CENTER);
		}
		
		public void updateApplicationPane() {
			if (device.getApplications() != null) {
				MobileApplication applications[] = device.getApplications();
				boolean isRunning = false;
				for (int i = 0; applications != null && i < applications.length; i++) {
					if (applications[i].isRunning()) {
						appBtn[i+1].setSelected(true);
						isRunning = true;
					}
				}
				if (!isRunning) {
					appBtn[0].setSelected(true);
				}
			}
		}
		
		public synchronized void updateCandidateNetworkPane() {
			if (candidateNetworkPane == null) {
				return;
			}
	    	if (device.getNetworkInterfaces() != null) {
	    		for (int i = 0; i < candidateNetworkPane.length; i++) {
	    			if (candidateNetworkPane[i] != null) {
    	    			candidateNetworkPane[i].removeAll();
	    			}
	    		}
	    		NetworkInterface networkInterfaces[] = device.getNetworkInterfaces();
	    		String selectedNi = null;
	        	for (int i = 0; i < networkInterfaces.length; i++) {
	        		ButtonGroup candidateNetworkGroup = new ButtonGroup();
	        		Enumeration<CommunicationDevice> networkDevices = networkInterfaces[i].getCandidateNetworkDevices();
	    			if (networkDevices != null) {
	    				int j = 0;
	    				String selectedDevice = null;
	    				if (networkInterfaces[i].isSelected()) {
	    					selectedNi = networkInterfaces[i].getName();
	    					selectedDevice = networkInterfaces[i].getSelectedDevice().getMacAddress();
	    				}
	    	    		while(networkDevices.hasMoreElements()) {
	    	    			NetworkDevice d = (NetworkDevice)networkDevices.nextElement();
	    	    			NetworkProperty p = device.getNetworkInterfaces()[i].getNetworkProperty(d);
	    	    			/*
	    	    			JRadioButton btn = new JRadioButton(d.getName() + "("+
	    	    					String.valueOf((int)(p.getSignalStrength()*100))+")");
	    	    					*/
	    	    			JRadioButton btn = new JRadioButton(d.getName());
	    	    			btn.setBounds(5, j*25, 90, 20);
	    	    			btn.setActionCommand(d.getName());
	    	    			btn.setEnabled(false);
	    	    			
	    	    			//	check selected
	    	    			if (selectedDevice != null && selectedDevice.intern() == d.getMacAddress().intern()) {
	    	    				btn.setSelected(true);
	    	    			}
	    	    			candidateNetworkGroup.add(btn);
	    	    			candidateNetworkPane[i].add(btn);
	    	    			SignalComponent sc = new SignalComponent((int)(p.getSignalStrength()*100));
	    	    			sc.setLocation(110, j*25);
	    	    			candidateNetworkPane[i].add(sc);
	    	    			j++;
	    	    		}
    	    			candidateNetworkPane[i].setPreferredSize(new Dimension(220, j*25));
	    			}	
	        	}
	        	if (selectedNi != null) { 
	        		for (int i = 1; i < networkBtn.length; i++) {
	        			if (networkBtn[i].getText().intern() == selectedNi.intern()) {
	        				networkBtn[i].setSelected(true);
	        				break;
	        			}
	        		}
	        	} else {
	        		networkBtn[0].setSelected(true);
	        	}
	    	}
	    	repaint();
		}
		
		public void setDevice(MobileDevice device) {
			this.device = device;
			setApplications(device.getApplications());
			setNetworkInterfaces(device.getNetworkInterfaces());
		}
		
		public void setApplications(MobileApplication apps[]) {
			if (appGrp != null) {
				appGrp = null;
			}
			if (appBtn != null) {
				for (int i = 0; i < appBtn.length; i++) {
					appPane.remove(appBtn[i]);
				}
				appBtn = null;
			}
		
			if (apps != null) {
	    		appGrp = new ButtonGroup();
	    		appBtn = new JRadioButton[apps.length + 1];
	    		for (int i = 0; i < apps.length+1; i++) {
	    			if (i == 0) {	//	NONE
	    				appBtn[i] = new JRadioButton("NONE");
	    			} else {
	        			appBtn[i] = new JRadioButton(apps[i-1].getName());
	    			}
	    			appBtn[i].setEnabled(false);
//	    			appBtn[i].addActionListener(this);
	    			appGrp.add(appBtn[i]);
	    			appPane.add(appBtn[i]);
	    		}
			}
		}
		
		public void setNetworkInterfaces(NetworkInterface nis[]) {
			if (networkGrp != null) {
				networkGrp = null;
			}
			if (networkBtn != null) {
				for (int i = 0; i < networkBtn.length; i++) {
					networkPane.remove(networkBtn[i]);
				}
				networkBtn = null;
			}
	    	if (nis != null) {
	        	networkGrp = new ButtonGroup();
	        	networkBtn = new JRadioButton[nis.length+1];
	        	for (int i = 0; i < nis.length+1; i++) {
	        		if (i == 0) {
	            		networkBtn[i] = new JRadioButton("NONE");
	        		} else {
	            		networkBtn[i] = new JRadioButton(nis[i-1].getName());
	        		}
//	        		networkBtn[i].addActionListener(this);
	        		networkGrp.add(networkBtn[i]);
	        		networkPane.add(networkBtn[i]);
	        	}
	    	}
		}		
		
		/*
    	public void actionPerformed(ActionEvent e) {
    		String cmd = e.getActionCommand();
    
    		//	Application
    		if (cmd.intern() == MobileDevice.APP_NONE.intern()) {
    			MobileApplication app = device.getApplicationManager().getCurrentApplication();
    			if (app != null) {
    				device.getApplicationManager().stopApplication(app.getName());
    				device.updateApplication();
    			}
    		} else {
        		if (device.getApplicationManager().isApplication(cmd)) {
        			device.getApplicationManager().startApplication(cmd);
        			device.updateApplication();
        		}
    		}
    
    		//	Network	
    		if (cmd.intern() == MobileDevice.NI_NONE.intern()) {
    			NetworkInterface ni = device.getNetworkInterfaceManager().getCurrentNetworkInterface();
    			if (ni != null) {
    				ni.setEnabled(false);
    				device.getNetworkInterfaceManager().stopNetworkInterface(ni.getName());
    				device.updateNetworkInterface();
    			}
    		} else {
        		if (device.getNetworkInterfaceManager().isNetworkInterface(cmd)) {
        			device.getNetworkInterfaceManager().startNetworkInterface(cmd);
    				device.updateNetworkInterface();
        		}
    		}
    	}
    	*/
	}
	
	public synchronized void updateInfo() {
        for (int i = 0; i < deviceView.length; i++) {
        	((MobileDeviceView)deviceView[i]).updateApplicationPane();
        	((MobileDeviceView)deviceView[i]).updateCandidateNetworkPane();
        }
        showCurrentMobileDeviceStatus();
	}
}