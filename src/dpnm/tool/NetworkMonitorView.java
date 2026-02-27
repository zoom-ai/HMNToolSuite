package dpnm.tool;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import dpnm.tool.data.*;
import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.MobileDevice;
import dpnm.mobiledevice.NetworkProperty;
import dpnm.network.*;
import dpnm.network.device.*;

public class NetworkMonitorView extends JPanel {
	private static final String NETWORK_HEADERS[] = {
	"Device", "Type", "Connected Mobile Nodes"};
	
	private static final String NETWORK_PARAMETERS[] = {
			"Coverage (meter)",
			"Bandwidth (kbyte)",
			"Delay (ms)",
			"Jitter (ms)",
			"BER (dB)",
			"Throughput (Mbyte/s)",
			"Burst Error",
			"Packet Loss Ratio",
			"Cost Rate ($/min)",
			"Power Tx (W)",
			"Power Rx (W)",
			"Power Idle (W)",
			"Max Velocity (km/h)",
			"Min Velocity (km/h)"
	};

    private NetworkDeviceInfo deviceInfo[];
	
	private JTabbedPane mainPane;
	private JPanel networkView;
	private JTable networkTable;
	private JTextArea networkArea;
	private NetworkModel networkModel;
	
	private JPanel deviceView[];
	
	
	public NetworkMonitorView() {
		this(null);
	}
	
	public NetworkMonitorView(NetworkDeviceInfo deviceInfo[]) {
		mainPane = new JTabbedPane();
        mainPane.setPreferredSize(new Dimension(Env.MONITOR_WIDTH/2-20, Env.MONITOR_HEIGHT-60));
        mainPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        mainPane.setTabPlacement(JTabbedPane.LEFT);
		add(mainPane);
		setDeviceInfo(deviceInfo);
	}
	
	public void setDeviceInfo(NetworkDeviceInfo deviceInfo[]) {
		this.deviceInfo = deviceInfo;
		if (deviceInfo != null)
			createUI();
		
		showCurrentNetworkStatus();
	}
	
	private void createUI() {
		networkView = new JPanel();
        networkModel = new NetworkModel();
        networkModel.setNetworkInfo(deviceInfo);
        networkTable = new JTable(networkModel);
        networkArea = new JTextArea();
        networkArea.setEditable(false);
        networkArea.setFont(Resources.DIALOG_12);
		networkTable.setGridColor(Color.black);
		JScrollPane scrollPane = new JScrollPane(networkArea);
//        scrollPane.setPreferredSize(new Dimension(Env.MONITOR_WIDTH/2-20, Env.MONITOR_HEIGHT-60));
//		scrollPane.setBounds(20,160,400,250);
        mainPane.addTab("Network(s)", scrollPane);
		
        deviceView = new DeviceView[deviceInfo.length];
        for (int i = 0; i < deviceInfo.length; i++) {
        	deviceView[i] = new DeviceView(deviceInfo[i]);
        	mainPane.addTab(deviceInfo[i].getDevice().getName(), deviceView[i]);
        	mainPane.setBackgroundAt(i+1, new Color(deviceInfo[i].getDevice().getNetwork().getColor()));
        	mainPane.setToolTipTextAt(i+1, deviceInfo[i].getDevice().getNetworkStr());
        }
	}
	
	void showCurrentNetworkStatus() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; deviceInfo != null && i < deviceInfo.length; i++) {
			sb.append(deviceInfo[i].getStatus());
			sb.append("---------------------------------------------\n\n");
    	}
		networkArea.setText(sb.toString());
	}		
	class NetworkModel extends AbstractTableModel {

		NetworkDeviceInfo[] info = null;
		
		public NetworkModel() {
			this(null);
		}
		
		public NetworkModel(NetworkDeviceInfo[] info) {
			setNetworkInfo(info);
		}
		
		public void setNetworkInfo(NetworkDeviceInfo[] info) {
			this.info = info;
		}

		public int getColumnCount() {
			// TODO Auto-generated method stub
			return NETWORK_HEADERS.length;
		}

		public int getRowCount() {
			// TODO Auto-generated method stub
			return info.length;
		}
		
		public String getColumnName(int col) {
			return NETWORK_HEADERS[col];
		}

		public Object getValueAt(int row, int col) {
			// TODO Auto-generated method stub
			if (info == null) {
				return "";
			}
			switch(col) {
			case 0: return info[row].getDevice().getName();
			case 1: return info[row].getDevice().getNetwork().getName(); 
			case 2: return "..";
			}
			return "";
		}
	}
	/*
	 * DeviceView
	 * 
	 * Base station position
	 * Type
	 * IPAddr
	 * Name
	 * Network information
	 */
	class DeviceView extends JPanel {
		private NetworkDeviceInfo deviceInfo;
		
		private JTextField nameFld = null;
		private JTextField positionFld = null;
		private JTextField typeFld = null;
		private JTextField ipAddrFld = null;
		private JTable networkInfoTable = null;
		private JList connectedNodeInfoList = null;
		private NetworkInfoModel networkInfoModel = null;
		private DefaultListModel connectedListModel = null;
		
		public DeviceView(NetworkDeviceInfo deviceInfo) {
			this.deviceInfo = deviceInfo;
			createUI();
			updateData();
		}
		
		private void createUI() {
			setLayout(null);
			
			//	name
			JLabel label = new JLabel("Name: ");
			label.setFont(Resources.ARIAL_12);
			label.setBounds(10, 10, 100, 20);
			add(label);
			
			nameFld = new JTextField(20);
			nameFld.setBounds(100, 10, 200, 20);
			nameFld.setEditable(false);
			add(nameFld);
			
			//positionFld
			label = new JLabel("Location: ");
			label.setFont(Resources.ARIAL_12);
			label.setBounds(10, 40, 100, 20);
			add(label);
			
			positionFld = new JTextField(20);
			positionFld.setBounds(100, 40, 200, 20);
			positionFld.setEditable(false);
			add(positionFld);
			
			//typeFld
			label = new JLabel("Type: ");
			label.setFont(Resources.ARIAL_12);
			label.setBounds(10, 70, 100, 20);
			add(label);
			
			typeFld = new JTextField(20);
			typeFld.setBounds(100, 70, 200, 20);
			typeFld.setEditable(false);
			add(typeFld);
			
			//ipAddrFld
			label = new JLabel("IP Address: ");
			label.setFont(Resources.ARIAL_12);
			label.setBounds(10, 100, 100, 20);
			add(label);
			
			ipAddrFld = new JTextField(20);
			ipAddrFld.setBounds(100, 100, 200, 20);
			ipAddrFld.setEditable(false);
			add(ipAddrFld);
	
			//network information
			label = new JLabel("Network Information: ");
			label.setFont(Resources.ARIAL_12);
			label.setBounds(10, 130, 140, 20);
			add(label);
	
			networkInfoModel = new NetworkInfoModel();
			networkInfoTable = new JTable(networkInfoModel);
			networkInfoTable.setGridColor(Color.black);
			JScrollPane scrollPane = new JScrollPane(networkInfoTable);
			scrollPane.setBounds(20,160,350,250);
			add(scrollPane);
			
			//network information
			label = new JLabel("Connected Mobile Nodes: ");
			label.setFont(Resources.ARIAL_12);
			label.setBounds(10, 420, 200, 20);
			add(label);
			
			connectedListModel = new DefaultListModel();
			connectedNodeInfoList = new JList(connectedListModel);
			connectedNodeInfoList.setBounds(20, 450, 200, 60);
			scrollPane = new JScrollPane(connectedNodeInfoList);
			scrollPane.setBounds(20, 450, 200, 60);
			
			/*
			NetworkMobileNodeInfo mnodes[] = new NetworkMobileNodeInfo[test.TestPlayer.MOBILENODES.length];
			for (int i = 0; i < mnodes.length; i++) {
		  		NetworkMobileNodeInfo mInfo = new NetworkMobileNodeInfo();
		  		char id = (char) ('A'+((char)i));
		  		MobileDevice device = new MobileDevice(Character.toString(id));
		  		mInfo.setType(test.TestPlayer.MOBILENODES[i][0]);
		  		mInfo.setXpos(test.TestPlayer.MOBILENODES[i][1]+test.TestPlayer.X_GAP);
		  		mInfo.setYpos(test.TestPlayer.MOBILENODES[i][2]+test.TestPlayer.Y_GAP);
		  		mInfo.setDevice(device);
		  		mnodes[i] = mInfo;
			}
			nodeInfoModel.setMobileNodeInfo(mnodes);
			*/
			
			add(scrollPane);
			
		}
		
		void updateConnectedList() {
			connectedListModel.removeAllElements();
    		Vector<ConnectedMobileNode> nodes = deviceInfo.getDevice().getConnectedMobileNodes();
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					connectedListModel.addElement(nodes.elementAt(i));
	    		}
    		}	
			connectedNodeInfoList.repaint();
		}
		
		private void updateData() {
			if (deviceInfo == null)
				return;
			
			nameFld.setText(deviceInfo.getDevice().getName());
			positionFld.setText(deviceInfo.getXpos() + " , " + deviceInfo.getYpos());
			typeFld.setText(deviceInfo.getDevice().getNetwork().getName());
			ipAddrFld.setText(deviceInfo.getDevice().getIpAddress());
//			networkInfoModel.setNetworkInfo(deviceInfo.getDevice().getNetwork());
			networkInfoModel.setNetworkInfo(deviceInfo.getData());
		}
		

		
		class NetworkInfoModel extends AbstractTableModel {
		
			INetwork network;
			
			public NetworkInfoModel() {
				this(null);
			}
			
			public NetworkInfoModel(INetwork network) {
				setNetworkInfo(network);
			}
			
			public void setNetworkInfo(INetwork network) {
				this.network = network;
			}

			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 2;
			}

			public int getRowCount() {
				// TODO Auto-generated method stub
				return NETWORK_PARAMETERS.length;
			}
			
			public String getColumnName(int col) {
				switch(col) {
				case 0:
					return "Parameter";
				case 1:
					return "Value";
				}
				return null;
			}

			public Object getValueAt(int row, int col) {
				// TODO Auto-generated method stub
		    	if (col == 0) {
		    		return NETWORK_PARAMETERS[row];
		    	}
		    	if (col == 1) {
		    		return getNetworkValue(row);
		    	}
		    	return null;
    		}
			
		    /*
		     * Don't need to implement this method unless your table's
		     * editable.
		     */
		    public boolean isCellEditable(int row, int col) {
		    	return false;
		    }
		    
		    private String getNetworkValue(int v) {
		    	if (network == null)
		    		return "";
		    	
		    	switch(v) {
		    	case 0: return Integer.toString(network.getCoverage());
		    	case 1: return Integer.toString(network.getBandwidth());
		    	case 2: return Integer.toString(network.getDelay());
		    	case 3: return Integer.toString(network.getJitter());
		    	case 4: return Double.toString(network.getBER());
		    	case 5: return Double.toString(network.getThroughput());
		    	case 6: return Double.toString(network.getBurstError());
		    	case 7: return Double.toString(network.getPacketLossRatio());
		    	case 8: return Double.toString(network.getCostRate());
		    	case 9: return Double.toString(network.getTxPower());
		    	case 10: return Double.toString(network.getRxPower());
		    	case 11: return Double.toString(network.getRxPower());
		    	case 12: return Double.toString(network.getIdlePower());
		    	case 13: return Integer.toString(network.getMaxVelocity());
		    	case 14: return Integer.toString(network.getMinVelocity());
		    	}
		    	return "";
		    }
		    
		}
	}
	
	public synchronized void updateInfo() {
        for (int i = 0; i < deviceView.length; i++) {
        	((DeviceView)deviceView[i]).updateConnectedList();
        }
		showCurrentNetworkStatus();
	}
}
