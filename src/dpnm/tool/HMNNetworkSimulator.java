package dpnm.tool;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dpnm.mobiledevice.policy.ruleobjects.Rule;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

import dpnm.Conf;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.event.*;
import dpnm.mobilenode.MobileNodeFactory;
import dpnm.network.NetworkFactory;
import dpnm.network.device.NetworkDevice;
import dpnm.server.ContextServer;
import dpnm.server.Server;
import dpnm.tool.comp.*;
import dpnm.tool.data.*;
import dpnm.util.*;

/**
 * This class is for simulating networks
 * 
 * @author eliot
 *
 */
public class HMNNetworkSimulator extends JFrame 
	implements ActionListener, MouseMotionListener, UpdateListener, MouseWheelListener,
		MobileNodeComponentManager, NetworkHandoverListener, ChangeListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4321782005899245361L;


	/*
	 * Owner of This GUI
	 */
	private HMNEmulator owner = null;
	private NetworkMapInfo mapInfo = null;
	private long interval = 0;
	
	private NetworkPlayer player = null;
	
	private JTextField fileFld = null;
	
	private JTextField background = null;
	private JTextField randomFile = null;
	
	private boolean isIconMode = false;
	
	private long duration = 0;
	private long end = 0;
	
    /* toolbar button */
	private static final String toolbarBtnStr[][][] = {
			{
    			{"Open", Resources.OPEN_ICON, "Open", "Open"},
			},
			{
				{"Run", Resources.RUN_ICON, "RunNetwork", "Run Network Emulator"},
				{"Stop", Resources.STOP_ICON, "StopNetwork", "Stop Network Emulator"},
			},
			{
				{"MonitorView", Resources.MONITOR_ICON, "LaunchMonitorView", "Launch Monitor View"}
			},
			{
				{"Export MAP PNG", Resources.EXPORT_ICON, "Export", "Export PNG image..."},
				{"Export Graph PNG", Resources.EXPORT_ICON, "ExportGraph", "Export Graph PNG image..."}
			},
			{
				{"Random Simulation", Resources.SIMULATOR_ICON, "RandomSimulation", "Generate Network Map Randomly"}
			}
	};
	
	//	speed rate
	private JSlider speedRateBar = null;
	private double speedRate = 1.0;
	
	private Vector<NetworkComponent> networks;
	private Vector<MobileNodeSimComponent> mnodes;
	private Vector<ServerComponent> snodes;
	private ContextServer currentContextServer = null;
	
	/*	UI */
	static ResourceFinder rf = new ResourceFinder();
	
	Image deviceImg[] = new Image[Resources.DEVICE_IMG_STR.length];
	Image mobileNodeImg[] = new Image[Resources.MOBILENODE_IMG_STR.length];
	Image serverImg[] = new Image[Resources.SERVER_IMG_STR.length];

	
	private DesComponent desComp = new DesComponent();
	
	/*	View */
	private NetworkMapView view = null;
	private JTabbedPane tp = null;
	private JTabbedPane ntp = null;
	private JTabbedPane mtp = null;
	private JList networkList = null;
	private JList mnodeList = null;
	private JTextArea numberNetwork = null;
	private JTextArea numberMobileNode = null;
	private DefaultListModel networkListModel = null;
	private DefaultListModel mnodeListModel = null;
	
	private StatusBar statusBar = new StatusBar();
	
	private JTextArea mNodeArea = null;
	private JTextArea networkArea = null;
	private JTextArea handoverInfoArea = null;
	
	/*	Monitor View */
	private MonitorView monitorView = null;
	
	/*	Simulation Result View */
	private SimulationResultView resultView = null;
	
	/*	Timer */
	private Timer timer = null;
	private TimerTask task = null;
	
	/*	Select network and mobile node */
	private JCheckBox networkCheckBox[] = null;
	private JCheckBox mobileNodeCheckBox[] = null;
	private JToggleButton networkBtn = null;
	private JToggleButton mNodeBtn = null;
	
	public HMNNetworkSimulator(HMNEmulator owner, String title) {
		super(title);
		this.owner = owner;
		
		setIconImage(getToolkit().getImage(rf.getURL(
				Resources.ICON_DIR+File.separator+Resources.SIMULATOR_ICON)));
		
		player = new NetworkPlayer();
		
		networks = new Vector<NetworkComponent>();
		mnodes = new Vector<MobileNodeSimComponent>();
		snodes = new Vector<ServerComponent>();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(Env.SIMULATOR_WIDTH, Env.SIMULATOR_HEIGHT);
		setBounds((screenSize.width/2) - (Env.SIMULATOR_WIDTH/2),
				(screenSize.height/2) - (Env.SIMULATOR_HEIGHT/2),
				Env.SIMULATOR_WIDTH, Env.SIMULATOR_HEIGHT);
		
		loadImages();
		
		createUI();
		createView();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
    		}
    	);
		setZoom(1.0);
		setSimState("STOPPED");
		setTime(duration);
		
		timer = dpnm.util.UpdateTimer.getInstance().getTimer();
	}

	boolean isIconMode() {
		return isIconMode;
	}

	void setIconMode(boolean isIconMode) {
		this.isIconMode = isIconMode;
	}

	private void loadImages() {
		//	load images
		for (int i = 0; i < Resources.DEVICE_IMG_STR.length; i++) {
			deviceImg[i] = rf.getImage(Resources.DEVICE_DIR + 
					Resources.DEVICE_IMG_STR[i]);
		}
		for (int i = 0; i < Resources.SERVER_IMG_STR.length; i++) {
			serverImg[i] = rf.getImage(Resources.SERVER_DIR + 
					Resources.SERVER_IMG_STR[i]);
		}
	}

	private void createUI() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		contentPane.add(createComponentToolBar(), BorderLayout.WEST);
		contentPane.add(statusBar, BorderLayout.SOUTH);
		setContentPane(contentPane);
	}
	
    private JToolBar createToolBar()
    {
        JToolBar toolBar = new JToolBar() ;
        for (int i = 0; i < toolbarBtnStr.length; i++) {
            for (int j = 0; j < toolbarBtnStr[i].length; j++) {
                JButton button = new HoverButton(toolbarBtnStr[i][j][0],
                rf.getIcon(Resources.ICON_DIR+toolbarBtnStr[i][j][1]));
                button.setActionCommand(toolbarBtnStr[i][j][2]);
                button.setToolTipText(toolbarBtnStr[i][j][3]);
                button.addActionListener(this);
                toolBar.add(button);
            }
            toolBar.addSeparator();
        }
        
        speedRateBar = new JSlider(JSlider.HORIZONTAL, 1, 40, 10);
        speedRateBar.addChangeListener(this);
        speedRateBar.setMajorTickSpacing(1);
        speedRateBar.setPaintTicks(true);
        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 1 ), new JLabel("0.1") );
        labelTable.put( new Integer( 10 ), new JLabel("1.0") );
        labelTable.put( new Integer( 20), new JLabel("2.0") );
        labelTable.put( new Integer( 30), new JLabel("3.0") );
        labelTable.put( new Integer( 40), new JLabel("4.0") );
        speedRateBar.setLabelTable( labelTable );
        speedRateBar.setPaintLabels(true);
        toolBar.add(speedRateBar);
//        toolBar.add(new Label("                    "));
        toolBar.addSeparator(new Dimension(50, 20));
       
        return toolBar ;
    }
    
    private JToolBar createComponentToolBar() {
    	JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
    	
    	String networkNames[] = NetworkFactory.getInstance().getNetworkNames();
    	
    	JLabel label = new JLabel("Networks");
    	toolBar.add(label);
//        JButton button = new HoverButton("Select All");
        networkBtn = new JToggleButton("Select All");
        networkBtn.setToolTipText("Select All Networks");
        networkBtn.setSelected(true);
        networkBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                	for (int i = 0; i < networkCheckBox.length; i++) {
                		networkCheckBox[i].setSelected(networkBtn.isSelected());
                	}
            		for (int j = 0; j < networks.size(); j++) {
        				networks.elementAt(j).setVisible(networkBtn.isSelected());
            		}
                }
            });
        toolBar.add(networkBtn);
        networkCheckBox = new JCheckBox[networkNames.length];
    	for (int i = 0; i < networkNames.length; i++) {
            networkCheckBox[i] = new JCheckBox(networkNames[i], true);
            networkCheckBox[i].setToolTipText("Show/Hide " + networkNames[i] + " Network");
            networkCheckBox[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	//	toggle visible flag;
                	if (e.getSource() instanceof JCheckBox) {
                		boolean isVisible = ((JCheckBox)e.getSource()).isSelected();
                		String name = ((JCheckBox)e.getSource()).getText();
                		for (int j = 0; j < networks.size(); j++) {
                			if (networks.elementAt(j).getDeviceInfo().getDevice().getNetworkStr().intern() ==
                				name.intern()) {
                				networks.elementAt(j).setVisible(isVisible);
                			}
                		}
                		view.repaint();
                	}
                }
            });
            toolBar.add(networkCheckBox[i]);
    	}
    	
    	toolBar.addSeparator();
    	
    	String mNodeNames[] = MobileNodeFactory.getInstance().getMobileNodeNames();
    	
    	label = new JLabel("Mobile Nodes");
    	toolBar.add(label);
        mNodeBtn = new JToggleButton("Select All");
        mNodeBtn.setToolTipText("Select All Mobile Nodes");
        mNodeBtn.setSelected(true);
        mNodeBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                	for (int i = 0; i < mobileNodeCheckBox.length; i++) {
                		mobileNodeCheckBox[i].setSelected(mNodeBtn.isSelected());
                	}
            		for (int j = 0; j < mnodes.size(); j++) {
        				mnodes.elementAt(j).setVisible(mNodeBtn.isSelected());
            		}
 
                }
            });
        toolBar.add(mNodeBtn);
        
        mobileNodeCheckBox = new JCheckBox[mNodeNames.length];
    	for (int i = 0; i < mNodeNames.length; i++) {
            mobileNodeCheckBox[i] = new JCheckBox(mNodeNames[i], 
        					HMNEmulatorGUI.getResizedImage(
        					Resources.MOBILENODE_DIR+File.separator+
        							MobileNodeFactory.getInstance().getMobileNodeAt(i).getIconStr(), 16, 16), 
        					true); 
            mobileNodeCheckBox[i].setSelectedIcon(HMNEmulatorGUI.getCheckedResizedImage(
        					Resources.MOBILENODE_DIR+File.separator+
        							MobileNodeFactory.getInstance().getMobileNodeAt(i).getIconStr(), 16, 16));
            mobileNodeCheckBox[i].setToolTipText("Show/Hide " + mNodeNames[i] + " Mobile Node");
            mobileNodeCheckBox[i].setSelected(true);	
            mobileNodeCheckBox[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	//	toggle visible flag;
                	if (e.getSource() instanceof JCheckBox) {
                		boolean isVisible = ((JCheckBox)e.getSource()).isSelected();
                		String name = ((JCheckBox)e.getSource()).getText();
                		for (int j = 0; j < mnodes.size(); j++) {
                			if (mnodes.elementAt(j).getDeviceInfo().getMobileNode().getName().intern() ==
                				name.intern()) {
                				mnodes.elementAt(j).setVisible(isVisible);
                			}
                		}
                		view.repaint();
                	}
                }
            });
            toolBar.add(mobileNodeCheckBox[i]);
    	}
 
    	return toolBar;
    	
    }
    
	private void createView() {
    	view = new NetworkMapView();
		view.setBackground(Color.white);
		view.setLayout(null);

        view.setPreferredSize(new Dimension(Env.VIEW_WIDTH, Env.VIEW_HEIGHT));
        JScrollPane js = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tp = new JTabbedPane();
        tp.addTab(Env.TAB_TITLE+" []", js);
        
        networkArea = new JTextArea();
        JScrollPane js2 = new JScrollPane(networkArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tp.addTab("Network Status", js2);
        
        mNodeArea = new JTextArea();
        JScrollPane js3 = new JScrollPane(mNodeArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tp.addTab("Mobile Node Status", js3);
 
        handoverInfoArea = new JTextArea();
        JScrollPane js4 = new JScrollPane(handoverInfoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tp.addTab("Handover Status", js4);
        
        resultView = new SimulationResultView();
        tp.addTab("Simulation Performance", new JScrollPane(resultView));
 
	
		//	add listeners
        view.addMouseMotionListener(this);
        view.addMouseWheelListener(this);
        view.addMouseListener(this);
        
        ntp = new JTabbedPane();
        networkListModel = new DefaultListModel();
        networkList = new JList(networkListModel);
        ntp.addTab("Networks", new JScrollPane(networkList));
        
        numberNetwork = new JTextArea();
        numberNetwork.setEditable(false);
        ntp.addTab("# of Network", new JScrollPane(numberNetwork));
        ntp.setToolTipTextAt(1, "Number of each network");
        
        mtp = new JTabbedPane();
        mnodeListModel = new DefaultListModel();
        mnodeList = new JList(mnodeListModel);
        mtp.addTab("Mobile Nodes", new JScrollPane(mnodeList));
        
        numberMobileNode = new JTextArea();
        numberMobileNode.setEditable(false);
        mtp.addTab("# of Mobile Node", new JScrollPane(numberMobileNode));
        mtp.setToolTipTextAt(1, "Number of each mobile node");
        
        JPanel leftPane = new JPanel();
        leftPane.setLayout(new GridLayout(2,1));
        leftPane.add(ntp);
        leftPane.add(mtp);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		                           tp, leftPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(Env.VIEW_WIDTH-100);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 50);
		tp.setMinimumSize(minimumSize);
		leftPane.setMinimumSize(minimumSize);
	
        getContentPane().add(splitPane, BorderLayout.CENTER);
	}
	
	void setViewTitle(String mapName) {
        tp.setTitleAt(0, Env.TAB_TITLE+" [" +
    		(mapName != null ? mapName : "")+" - ("+interval+" ms)");
	}
	
	void setNetworkTitle(int count) {
		ntp.setTitleAt(0, "Networks ("+count+")");
	}
	
	void setMnodeTitle(int count) {
		mtp.setTitleAt(0, "Mobile Nodes ("+count+")");
	}
 
	void loadNetworkMapInfo(NetworkMapInfo mapInfo) {
		if (mapInfo == null) {
			return;
		}
		this.mapInfo = mapInfo;
        view.setPreferredSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
       
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            JLabel label = new JLabel();
            label.setLocation(0,0);
            label.setIcon(getBackgroundImage(
						Resources.MAP_DIR+mapInfo.getBackground(),
						mapInfo.getWidth(), mapInfo.getHeight()));
            label.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
            view.add(label,0);
    	}
    	NetworkDeviceInfo[] info = mapInfo.getDevices();
    	if (info != null) {
        	for (int i = 0; i < info.length; i++) {
        		NetworkComponent comp = new NetworkComponent(this);
        		comp.setDeviceInfo(info[i]);
                comp.setIcon(deviceImg[info[i].getIcon()], info[i].getIcon());
                comp.setSim(true);
        		view.add(comp,0);
                networks.add(comp);
                networkListModel.addElement(info[i]);
        	}
    	}
    	setNetworkTitle(networkListModel.getSize());
    	// # of each network
    	int count[] = owner.getEachNetworkCount(info);
    	for (int i = 0; count != null && i < count.length; i++) {
    		numberNetwork.append(NetworkFactory.getInstance().getNetworkNameAt(i) + ": " + count[i]+"\n");
    	}
    	
    	NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
    	if (nodeInfo != null) {
        	for (int i = 0; i < nodeInfo.length; i++) {
        		MobileNodeSimComponent comp = new MobileNodeSimComponent(isIconMode);
        		comp.setNetworkMobileNodeInfo(nodeInfo[i]);
                comp.setIcon(nodeInfo[i].getMobileNode().getIcon());
        		view.add(comp,0);
                mnodes.add(comp);
                mnodeListModel.addElement(nodeInfo[i]);
                
                nodeInfo[i].initializeMove();
                nodeInfo[i].getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        	}
    	}
    	setMnodeTitle(mnodeListModel.getSize());
    
    	// # of each mobile node
    	count = owner.getEachMobileNodeCount(nodeInfo);
    	for (int i = 0; count != null && i < count.length; i++) {
    		numberMobileNode.append(MobileNodeFactory.getInstance().getMobileNodeNameAt(i) + ": " + count[i]+"\n");
    	}    	player.loadNetworkMapInfo(mapInfo);
    	
    	//	simulation result graph
    	resultView.setMobileNodeInfo(nodeInfo);

    	setMnodeTitle(mnodeListModel.getSize());
    	ServerInfo[] serverInfo = mapInfo.getServers();
    	if (nodeInfo != null) {
        	for (int i = 0; i < serverInfo.length; i++) {
        		ServerComponent comp = new ServerComponent();
        		comp.setServerInfo(serverInfo[i]);
    			comp.setIcon(serverImg[serverInfo[i].getType()]);
        		view.add(comp,0);
                snodes.add(comp);
                
                if (serverInfo[i].getServer().getType() == Server.CONTEXT) {
                	currentContextServer = (ContextServer)serverInfo[i].getServer();
                }
                //	add network server listener
//                nodeInfo[i].getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        	}
    	}


		addDesComponent();
        
		updateInfo();
		
        repaint();
	}
	
	void reset() {
		mapInfo = null;
		if (networks != null)
			networks.removeAllElements();
		if (mnodes != null) {
			for (int i = 0; i < mnodes.size(); i++) {
				mnodes.elementAt(i).getDeviceInfo().getDevice().getNetworkInterfaceManager().removeNetworkHandoverListener(this);
			}
			mnodes.removeAllElements();
		}
		if (view != null)
			view.removeAll();
		if (!networkListModel.isEmpty())
			networkListModel.removeAllElements();
		if (!mnodeListModel.isEmpty())
			mnodeListModel.removeAllElements();
		numberNetwork.setText("");
		numberMobileNode.setText("");
		duration = 0;
		if (resultView != null) {
			resultView.reset();
		}
		setTime(duration);
		setZoom(1.0);
	}
	
	private void addDesComponent() {
		if (desComp != null) {
			view.remove(desComp);
			desComp = null;
		}
		desComp = new DesComponent();
		desComp.setLocation(200, 100);
		desComp.setVisible(false);
		view.add(desComp, 0);
	}
	
    ImageIcon getBackgroundImage(String background, int width, int height) {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	try {
    		BufferedImage bi =  rf.getBufferedImage(background);
    		BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    		Graphics2D g = bi2.createGraphics();
    		g.drawImage(bi, 0, 0, width, height, null);
    		ImageIO.write(bi2, "png", outputStream);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return new ImageIcon(outputStream.toByteArray());
    }

	private void setDesComp(int x, int y, String text, Color background, boolean isVisible) {
        desComp.setLocation(x,y);
        desComp.setText(text);
        desComp.setBackground(background);
        desComp.setVisible(isVisible);
    }
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = (int)(e.getX()/view.getZoom());
		int y = (int)(e.getY()/view.getZoom());
		statusBar.setPosition(x, y);
        for (int i = 0; i < networks.size(); i++) {
            NetworkComponent comp = (NetworkComponent)networks.elementAt(i);
            if (comp.isIn(x, y)) {
    			setDesComp(x, y, comp.getDeviceInfo().getComment(), Resources.DES_COLOR_NETWORK, true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                view.repaint();
                return;
            }
        }
        for (int i = 0; i < mnodes.size(); i++) {
            MobileNodeSimComponent comp = (MobileNodeSimComponent)mnodes.elementAt(i);
            if (comp.isIn(x, y)) {
    			setDesComp(x, y, comp.getDeviceInfo().getComment(), Resources.DES_COLOR_MOBILENODE, true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                view.repaint();
                return;
            }
        }
        for (int i = 0; i < snodes.size(); i++) {
            ServerComponent comp = (ServerComponent)snodes.elementAt(i);
            if (comp.isIn(x, y)) {
    			setDesComp(x, y, comp.getServerInfo().getComment(), Resources.DES_COLOR_SERVER, true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                view.repaint();
                return;
            }
        }

        view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        desComp.setVisible(false);
	}

	public void openMonitorView(NetworkMapInfo mapInfo) {
		if (mapInfo == null) {
			JOptionPane.showMessageDialog(this, "Map Info is Null", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		monitorView = new MonitorView(mapInfo.getName());
    	monitorView.setMapInfo(mapInfo);
    	monitorView.repaint();
		monitorView.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String menuItemString = e.getActionCommand();
		
		if (menuItemString.intern() == "Open") {
			openNetworkMap();
		} else if (menuItemString.intern() == "Save") {
		} else if (menuItemString.intern() == "LaunchMonitorView") {
			openMonitorView(mapInfo);
		} else if (menuItemString.intern() == "RunNetwork") {
			startSimulation();
			setSimState("RUNNING");
		} else if (menuItemString.intern() == "StopNetwork") {
			stopSimulation();
			setSimState("STOPPED");
		} else if (menuItemString.intern() == "Export") {
			exportPNGImage();
		} else if (menuItemString.intern() == "ExportGraph") {
			exportGraphPNGImage();
    	} else if (menuItemString.intern() == "RandomSimulation") {
    		randomSimulation();
    	}
	}
	
	public void startSimulation() {
		//	determine applications for mobile nodes
 	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		JLabel label = new JLabel("Time interval (ms)");
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(label,c);
		p.add(label);
				
		JTextField time = new JTextField(6);
		time.setText("1000");
		time.setForeground(Color.blue);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(time,c);
		p.add(time);
		
		label = new JLabel("End time (ms)");
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(label,c);
		p.add(label);
				
		JTextField endtime = new JTextField(8);
		endtime.setText("600000");
		endtime.setForeground(Color.blue);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(endtime,c);
		p.add(endtime);



    	NetworkMobileNodeInfo nodeInfo[] = mapInfo.getMnodes();
    	JComboBox appCombo[] = new JComboBox[nodeInfo.length];
    	JComboBox policyCombo[] = new JComboBox[nodeInfo.length];
    	for (int i = 0; i < nodeInfo.length; i++) {
	        label = new JLabel(nodeInfo[i].getDevice().getId());
	        c.gridwidth = GridBagConstraints.RELATIVE;
	        gridbag.setConstraints(label,c);
	        p.add(label);

	        JPanel pane = new JPanel();
	        appCombo[i] = new JComboBox(nodeInfo[i].getDevice().getApplications());
	        pane.add(appCombo[i]);

	        policyCombo[i] = new JComboBox(nodeInfo[i].getDevice().getPolicies());
	        pane.add(policyCombo[i]);
	        
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        gridbag.setConstraints(pane,c);
	        p.add(pane);
    	}

		
		message[0] = "Start Network Simulator";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Start Network Simulator",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.OK_OPTION ) {
            if (time.getText() == null || time.getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert time interval",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            
            interval = Long.parseLong(time.getText());
            end = Long.parseLong(endtime.getText());

            String appNames[] = new String[nodeInfo.length];
	    	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
        		nodeInfo[i].getDevice().getPolicyManager().setCurrentPolicy((Rule)policyCombo[i].getSelectedItem());
        		appNames[i] = ((MobileApplication)appCombo[i].getSelectedItem()).getName();
	    	}
			//	initialize log
	    	
			owner.initLogging(mapInfo, appNames[0]);

			task = new SimulationTask();
			player.start();

			try {
				timer.schedule(task, 0, interval);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
	    	UpdateTimer.getInstance().addUpdateListener(this);
	    	updateInfo();
	    	    	
	    	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
        		nodeInfo[i].getDevice().getApplicationManager().startApplication(((MobileApplication)appCombo[i].getSelectedItem()).getName());
        		nodeInfo[i].getDevice().updateApplication();
	    	}
		}
	}
	
	public void stopSimulation() {
	   	//	stop any application 
		if (mapInfo != null) {
        	NetworkMobileNodeInfo nodeInfo[] = mapInfo.getMnodes();
        	for (int i = 0; nodeInfo != null && i < nodeInfo.length; i++) {
    			nodeInfo[i].getDevice().getApplicationManager().stop();
    		}
		}
		if (timer == null || task == null) {
			return;
		}
		try {
			task.cancel();
			task = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		player.stop();
		UpdateTimer.getInstance().removeUpdateListener(this);
		
	}
	
	void setSimState(String state) {
		statusBar.setState(state);
	}
	
	void setTime(long time) {
		if (mapInfo != null) {
			NetworkDeviceInfo deviceInfo[] = mapInfo.getDevices();
			for (int i = 0; i < deviceInfo.length; i++) {
				deviceInfo[i].getData().setCurrentTime(time);
			}
		}
		int ms = (int)(time % 1000);
		int s = (int)((time/1000)%60);
		int m = (int)((time/1000/60)%60);
		int h = (int)(time/1000/60/60);
		
		statusBar.setTime(String.format("%04dh %02dm %02ds %03d", h, m, s, ms));
	}

    private void exit() {
    	stopSimulation();
    	owner.exitSimulator();
    }
    
 	private synchronized void openNetworkMap() {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);


        JLabel label = new JLabel("Map File");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JPanel bPane = new JPanel();
        fileFld = new JTextField(30);
        fileFld.setForeground(Color.blue);
        bPane.add(fileFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        JButton btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
    			JFileChooser jf = new JFileChooser(new File(Resources.DATA_DIR));
    			jf.setDialogTitle("Choose a map file");
    			jf.setDialogType(JFileChooser.OPEN_DIALOG);
    			jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
    			
    			ZFileFilter filter = new ZFileFilter();
    			filter.addExtension("xml");
    			filter.setDescription("Map File");
    			jf.setFileFilter(filter);
    			
    			if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    				File file = jf.getSelectedFile();
					if (!file.getName().endsWith(".xml")) {
        				file = new File(file.getPath()+".xml");
        			}
    				fileFld.setText(file.getAbsolutePath());
    			}
            }
        });
        bPane.add(btn);
        
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
        
		label = new JLabel("Time Interval (ms)");
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(label,c);
		p.add(label);
				
		JTextField interval = new JTextField(8);
		interval.setText("100");
		interval.setForeground(Color.blue);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(interval,c);
		p.add(interval);

        message[0] = "Open Network Map";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, "Open Network Map",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if (fileFld.getText() == null || fileFld.getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            owner.openSimulator(Long.parseLong(interval.getText()), fileFld.getText());
            
            //TODO: open network map and load policy for each mobile node
        }
    }
	   
    public void setTimeInterval(long timeinterval) {
    	interval = timeinterval;
    }
    
	public class SimulationTask extends TimerTask {
    	public void run() {
			setTime(duration);

    		//	fire event
			for (int i = 0; i < mapInfo.getMnodes().length; i++) {
				mapInfo.getMnodes()[i].move(interval, speedRate);
//				mnodes.elementAt(i).move();
				view.repaint();
			}
			if (Conf.LOG){
				owner.logging(mapInfo, duration);
			}
			//	update duration timer
			duration+=interval;
			if (duration > end) {
				if (Conf.LOG){
					owner.loggingTable(mapInfo);
					owner.loggingGraph(mapInfo);
				}
				stopSimulation();
			}
    	}
	}
	
	
	public void updateInfo() {
		if (mapInfo != null) {
			networkArea.setText(mapInfo.getNetworkStatus());
			mNodeArea.setText(mapInfo.getMobleNodeStatus());
		}
		if (resultView != null) {
			resultView.updateMobileNode(duration);
		}
//		view.repaint();
	}
	
    private void exportPNGImage() {
    	File f = new File("png");
    	if (!f.exists()) {
    		f.mkdir();
    	}
        JFileChooser fc = new JFileChooser(f);
        fc.setDialogTitle("Save to PNG file");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File pngFile = fc.getSelectedFile();
            if (!pngFile.getPath().endsWith(".png")) {
            	pngFile = new File(pngFile.getPath()+".png");
            }
            exportPNGImage(pngFile);
        }
    }
 
    private void exportGraphPNGImage() {
    	File f = new File("png");
    	if (!f.exists()) {
    		f.mkdir();
    	}
        JFileChooser fc = new JFileChooser(f);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Save Graph to PNG file(s)");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        	if (resultView != null) {
        		resultView.exportAllGraph(fc.getSelectedFile());
        	}
        }
    }
 
    void exportPNGImage(File pngFile) {
        BufferedImage bi = new BufferedImage(view.getWidth(), view.getHeight()+25,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
//        g.fillRect(0,0, getWidth(), getHeight()+25);
        g.fillRect(0,0, view.getWidth(), view.getHeight()+25);
        g.translate(0, 25);
        view.paint(g);
        g.setFont(Resources.DIALOG_12B);
        g.setColor(Color.black);
        g.translate(0, -25);
        g.drawString("MAP TITLE : " + mapInfo.getName() + " ( " + mapInfo.getDescr()+ " ) - " +
                mapInfo.getWidth() + " x " + mapInfo.getHeight(), 5, 16);

        try {
            ImageIO.write(bi, "png", pngFile);
        } catch ( Exception ex) {
            
        }
    }

    private void randomSimulation() {
	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		String nameStr[] = {"Name", "Width", "Height", "# of Networks", "# of Mobile Nodes",
				"Max Path", "Max Stay time"};
		String valueStr[] = {"", "1024", "768", "40", "100", String.valueOf(Env.RANDOM_MAX_PATH),
				String.valueOf(Env.RANDOM_MAX_STAY)};
	
		JTextField valueFld[] = new JTextField[nameStr.length];

		for (int i = 0; i < nameStr.length; i++) {
			JLabel label = new JLabel(nameStr[i]);
			c.gridwidth = GridBagConstraints.RELATIVE;
			gridbag.setConstraints(label,c);
			p.add(label);
				
			valueFld[i] = new JTextField(30);
			valueFld[i].setText(valueStr[i]);
			valueFld[i].setForeground(Color.blue);
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(valueFld[i],c);
			p.add(valueFld[i]);
		}
        
        JLabel label = new JLabel("Background");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JPanel bPane = new JPanel();
        background = new JTextField(20);
        background.setText("");
        background.setForeground(Color.blue);
        bPane.add(background);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        JButton btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
    			JFileChooser jf = new JFileChooser(new File(Resources.HOME+File.separator+Resources.MAP_DIR));
    			jf.setDialogTitle("Choose a background file");
    			jf.setDialogType(JFileChooser.OPEN_DIALOG);
    			jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
    			
    			ZFileFilter filter = new ZFileFilter();
    			filter.addExtension("png");
    			filter.addExtension("jpg");
    			filter.addExtension("gif");
    			filter.setDescription("Image Files");
    			jf.setFileFilter(filter);
    			
    			if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    				File file = jf.getSelectedFile();
    				background.setText(file.getName());
    			}
            }
        });
        bPane.add(btn);
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
        
        label = new JLabel("File");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
           
        bPane = new JPanel();
        randomFile = new JTextField(20);
        randomFile.setText("");
        randomFile.setForeground(Color.blue);
        bPane.add(randomFile);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
    			JFileChooser jf = new JFileChooser(new File(Resources.DATA_DIR));
    			jf.setDialogTitle("Choose a map file");
    			jf.setDialogType(JFileChooser.SAVE_DIALOG);
    			jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    			
    			ZFileFilter filter = new ZFileFilter();
    			filter.addExtension("xml");
    			filter.setDescription("Map File");
    			jf.setFileFilter(filter);
    			
    			if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    				File file = jf.getSelectedFile();
					if (!file.getName().endsWith(".xml")) {
        				file = new File(file.getPath()+".xml");
        			}
    				randomFile.setText(file.getAbsolutePath());
    			}
            }
        });
        bPane.add(btn);
 
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
        
	    message[0] = "Random Simulation";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Create a new map randomly",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.OK_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            owner.simulate(randomFile.getText(), valueFld[0].getText(), Integer.parseInt(valueFld[1].getText()), 
            		Integer.parseInt(valueFld[2].getText()), background.getText(), 
            		Integer.parseInt(valueFld[3].getText()), 
            		Integer.parseInt(valueFld[4].getText()),
            		Integer.parseInt(valueFld[5].getText()),
            		Integer.parseInt(valueFld[6].getText()));
		}
    }
    
    
    private void changeZoom(double unit) {
    	double currentZoom = view.getZoom();
    	currentZoom += unit;
    	currentZoom = currentZoom < 0.1 ? 0.1 : currentZoom;
    	currentZoom = currentZoom > 3.0 ? 3.0 : currentZoom;
    	setZoom(currentZoom);
    }
    
    private void setZoom(double zoom) {
    	statusBar.setZoom(zoom);
    	if (mapInfo != null) {
    		view.setZoom(zoom);
        	int w = mapInfo.getWidth();
        	int h = mapInfo.getHeight();
        	
        	Dimension dim = new Dimension((int)(w * zoom), (int)(h*zoom));
        	view.setPreferredSize(dim);
        	view.repaint();
        	
        	/*
        	int w1 = view.getWidth();
        	int h1 = view.getHeight();
        	System.out.println(String.format("w: %d, w1: %d, h: %d, h1: %d", w, w1, h, h1));
        	System.out.println(dim);
        	*/
    	}
    }
    
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() > 0) {
			//	zoom in
			changeZoom(-0.1);
		} else {
			//	zoom out
			changeZoom(0.1);
		}
	}
	public MobileNodeComponent getMobileNodeComponent(String name) {
		if (mnodes != null) {
			for (int i = 0; i < mnodes.size(); i++) {
				if (mnodes.elementAt(i).getDeviceInfo().getDevice().getId().intern() == name.intern()) {
					return mnodes.elementAt(i);
				}
			}
		}
		return null;
	}

	public void handover(NetworkHandoverEvent evt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd HHmmss.SSS");
		handoverInfoArea.append(sdf.format(new Date(System.currentTimeMillis())));
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
		sb.append(next.getName() +"("+next.getNetworkStr()+")\n");
		handoverInfoArea.append(sb.toString());
		
		/*
		if (Conf.DEBUG) {
			Logger.getInstance().logEmulator("Simulator", sb.toString());
		}
		*/
		handoverInfoArea.getCaret().setDot(handoverInfoArea.getText().length());
	}

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        speedRate = (double)source.getValue()/10;
	    }
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = (int)(e.getX()/view.getZoom());
		int y = (int)(e.getY()/view.getZoom());
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			if (e.getClickCount() == 2) {
				MobileNodeComponent comp = findMobileNodeComponent(x,y);
				if (comp != null) {
	            	comp.getDeviceInfo().getDevice().showGUI();
				}
			}
		}
	} 
	
	private MobileNodeComponent findMobileNodeComponent(int x, int y) {
       for (int i = 0; i < mnodes.size(); i++) {
            MobileNodeComponent comp = mnodes.elementAt(i);
            if (comp.isIn(x, y) && comp.isVisible()) {
                return comp;
            }
        }
		return null;
	}
	
	public void setEndTime(long end) {
		this.end = end;
	}
	
	public long getEndTime() {
		return end;
	}
}
