package dpnm.tool;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import java.text.SimpleDateFormat;
import java.util.*;

import dpnm.Conf;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.NetworkInterface;
import dpnm.mobiledevice.event.*;
import dpnm.mobilenode.IMobileNode;
import dpnm.server.Server;
import dpnm.mobilenode.MobileNodeFactory;
import dpnm.network.INetwork;
import dpnm.network.NetworkData;
import dpnm.network.NetworkFactory;
import dpnm.network.device.NetworkDevice;
import dpnm.tool.comp.*;
import dpnm.tool.data.*;
import dpnm.util.Logger;
import dpnm.util.UpdateListener;
import dpnm.util.UpdateTimer;
import dpnm.featuremodel.StaticFeatureModel;

public class HMNEmulatorGUI extends JFrame 
    implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener,
    MobileNodeComponentManager, UpdateListener, NetworkHandoverListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3531571062985895196L;


	/*
	 * Owner of This GUI
	 */
	private HMNEmulator owner = null;


    //  DEFINE state
    private static final int NONE 					= -1;
    private static final int SELECT 				= 0x00;
    private static final int CREATE_NETWORK 		= 0x01;
    private static final int MODIFY_NETWORK 		= 0x02;
    private static final int CREATE_MOBILENODE 		= 0x03;
    private static final int MODIFY_MOBILENODE 		= 0x04;
    private static final int CREATE_SERVER 			= 0x05;
    private static final int MODIFY_SERVER 			= 0x06;
    private static final int MOVE_NETWORK 			= 0x07;
    private static final int MOVE_MOBILENODE 		= 0x08;
    private static final int MOVE_SERVER	 		= 0x09;
    
    private int state = NONE;
    

    /* toolbar button */
	private static final String toolbarBtnStr[][][] = {
			{
    			{"New", Resources.NEW_ICON, "New", "New"},
    			{"Open", Resources.OPEN_ICON, "Open", "Open"},
    			{"Save", Resources.SAVE_ICON, "Save", "Save"},
    			{"Preference", Resources.PREFERENCE_ICON, "Preference", "Preference"}
			},
			{
				{"Add New Network", Resources.ADDNETWORK_ICON, "AddNetwork", "Add New Network"},
				{"Add New Mobile Node", Resources.ADDMN_ICON, "AddMN", "Add New Mobile Node"},
				{"Add New Server", Resources.ADDSERVER_ICON, "AddServer", "Add New Server"},
//				{"Configuration", Resources.CONF_ICON, "Conf", "Configuration"},
			},
			{
				{"MonitorView", Resources.MONITOR_ICON, "LaunchMonitorView", "Launch Monitor View"}
			},
			{
				{"Simulator", Resources.SIMULATOR_ICON, "StartSimulator", "Start Network Simulator"}
			},
			{
				{"Run", Resources.RUN_ICON, "RunNetwork", "Run Network Emulator"},
				{"Stop", Resources.STOP_ICON, "StopNetwork", "Stop Network Emulator"},
			}
	};
	private JButton mapBtn = null;
	private boolean mapVisible = true;

	private static final String menuStr[] = {"Map", "Network", "Emulate", "Simulate", "Zoom", "About"};
	private static final int menuKeys[] = {
		KeyEvent.VK_M, KeyEvent.VK_N, KeyEvent.VK_E, KeyEvent.VK_S, KeyEvent.VK_Z, KeyEvent.VK_A};
	private static final String subMenuStr[][][] = {
		{
			{"New Map", Resources.ICON_DIR+Resources.NEW_ICON, "New"},
			{"Open Map", Resources.ICON_DIR+Resources.OPEN_ICON, "Open"},
			{"Save Map", Resources.ICON_DIR+Resources.SAVE_ICON, "Save"},
			{"Save As Map", Resources.ICON_DIR+Resources.SAVEAS_ICON, "SaveAs"},
			{"Preference", Resources.ICON_DIR+Resources.PREFERENCE_ICON, "Preference"},
			{"Export", Resources.ICON_DIR+Resources.EXPORT_ICON, "Export"},
			null,
			{"Exit", Resources.ICON_DIR+Resources.EXIT_ICON, "Exit"}
		},
		{
			{"Add New Network", Resources.ICON_DIR+Resources.ADDNETWORK_ICON, "AddNetwork"},
			{"Add New Mobile Node", Resources.ICON_DIR+Resources.ADDMN_ICON, "AddMN"},
			{"Add New Server", Resources.ICON_DIR+Resources.ADDSERVER_ICON, "AddServer"},
//			null,
//			{"Configuration", Resources.ICON_DIR+Resources.CONF_ICON, "Conf"},
		},
		{
			{"Run Network Emulator", Resources.ICON_DIR+Resources.RUN_ICON, "RunNetwork"},
			{"Stop Network Emulator", Resources.ICON_DIR+Resources.STOP_ICON, "StopNetwork"},
		},
		{
			{"Random Simulation", Resources.ICON_DIR+Resources.SIMULATOR_ICON, "RandomSimulation"}
		},
		{
			{"X 0.1", Resources.ICON_DIR+Resources.ZOOMOUT_ICON, "X 0.1"},
			{"X 0.2", Resources.ICON_DIR+Resources.ZOOMOUT_ICON, "X 0.2"},
			{"X 0.5", Resources.ICON_DIR+Resources.ZOOMOUT_ICON, "X 0.5"},
			{"X 1.0", Resources.ICON_DIR+Resources.ZOOM_ICON, "X 1.0"},
			{"X 2.0", Resources.ICON_DIR+Resources.ZOOMIN_ICON, "X 2.0"},
			{"X 3.0", Resources.ICON_DIR+Resources.ZOOMIN_ICON, "X 3.0"}
		},
		{
			{"About HMNEmulator...", Resources.LOGO_IMG_STR, "About"}
		}
	};
	private static int subMenuKeys[][][] = {
		{
			{KeyEvent.VK_N, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_O, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_S, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_A, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_P, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_T, ActionEvent.CTRL_MASK},
			null,
			{KeyEvent.VK_X, ActionEvent.CTRL_MASK}
		},
		{
			{KeyEvent.VK_E, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_M, ActionEvent.CTRL_MASK},
			{KeyEvent.VK_V, ActionEvent.CTRL_MASK},
			null,
			{KeyEvent.VK_C, ActionEvent.ALT_MASK}
		},
		{
			{KeyEvent.VK_R, ActionEvent.ALT_MASK},
			{KeyEvent.VK_S, ActionEvent.ALT_MASK}
		},
		{
			{KeyEvent.VK_T, ActionEvent.ALT_MASK}
		},
			null,
		{
			{KeyEvent.VK_H, ActionEvent.CTRL_MASK}
		}
	};
	
	private Vector<NetworkComponent> networks;
	private Vector<MobileNodeComponent> mnodes;
	private Vector<ServerComponent> snodes;
	
	/*	UI */
	static ResourceFinder rf = new ResourceFinder();
	
	private SplashWindow sp;
	
	Image deviceImg[] = new Image[Resources.DEVICE_IMG_STR.length];
	Image serverImg[] = new Image[Resources.SERVER_IMG_STR.length];
	
	private JPopupMenu popupMenu;
	private JMenuItem modifyMenu;
	private JMenuItem deleteMenu;
	private JMenuItem dataEditorMenu;
	private JMenuItem dataSeriesEditorMenu;
	private JMenuItem pathEditorMenu;
	private JMenuItem velocityEditorMenu;
	
	private Component selectedComp = null;
	private DesComponent desComp = new DesComponent();
	
	/*	View */
	private NetworkMapView view = null;
	private JLabel backgroundMap = null;
	private JTabbedPane tp = null;
	private JTabbedPane ntp = null;
	private JTabbedPane mtp = null;
	private JList networkList = null;
	private JList mnodeList = null;
	private JTextArea numberNetwork = null;
	private JTextArea numberMobileNode = null;
	private DefaultListModel networkListModel = null;
	private DefaultListModel mnodeListModel = null;
	private JTextField background = null;
	private JTextField randomFile = null;
	private JTextField velocityFld = null;
	
	//	additional information tab
	private NetworkInfoPane networkInfoPane = null;
	private MobileNodeInfoPane mobileNodeInfoPane = null;
	private JTextArea handoverInfoArea = null;
	
	/*	Select network and mobile node */
	private JCheckBox networkCheckBox[] = null;
	private JCheckBox mobileNodeCheckBox[] = null;
	private JToggleButton networkBtn = null;
	private JToggleButton mNodeBtn = null;
	
	
	private StatusBar statusBar = new StatusBar();
	
	//	points
	int diff_x = 0;
	int diff_y = 0;
	
	/*	Monitor View */
	private MonitorView monitorView = null;
	
	public HMNEmulatorGUI(HMNEmulator owner, String title) {
		super(title);
		this.owner = owner;
		
		sp = new SplashWindow(rf.getIcon(Resources.SPLASH_IMG_STR));
		
		setIconImage(getToolkit().getImage(rf.getURL(Resources.LOGO_IMG_STR)));
		
		networks = new Vector<NetworkComponent>();
		mnodes = new Vector<MobileNodeComponent>();
		snodes = new Vector<ServerComponent>();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(Env.PLAYER_WIDTH, Env.PLAYER_HEIGHT);
		setBounds((screenSize.width/2) - (Env.PLAYER_WIDTH/2),
				(screenSize.height/2) - (Env.PLAYER_HEIGHT/2),
				Env.PLAYER_WIDTH, Env.PLAYER_HEIGHT);
		
		loadImages();
		
		setJMenuBar(createMenuBar());
		createUI();
		createView();
		
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (sp != null) {
    				sp.setVisible(false);
    				sp.dispose();
				}
			}
			public void windowClosing(WindowEvent e) {
				exit();
			}
    		}
    	);
		setZoom(1.0);
		setState(SELECT);
		setSimState("STOPPED");
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

	/*
	 * Create Menu Bar with pre-defined menu string
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		
		for (int i = 0; i < menuStr.length; i++) {
			JMenu menu = new JMenu(menuStr[i]);
			menu.setMnemonic(menuKeys[i]);
			for (int j = 0; j < subMenuStr[i].length; j++) {
				if (subMenuStr[i][j] == null) {
					menu.addSeparator();
					continue;
				}
				JMenuItem subMenu = new JMenuItem(subMenuStr[i][j][0],
						rf.getIcon(subMenuStr[i][j][1]));
				subMenu.setActionCommand(subMenuStr[i][j][2]);
				if (subMenuKeys[i] != null) {
    				subMenu.setMnemonic(subMenuKeys[i][j][0]);
    				subMenu.setAccelerator(KeyStroke.getKeyStroke(
        							subMenuKeys[i][j][0], subMenuKeys[i][j][1]));
				}
				subMenu.addActionListener(this);
				menu.add(subMenu);
			}
			menuBar.add(menu);
		}
		return menuBar;
	}
	
	private void createUI() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		contentPane.add(createComponentToolBar(), BorderLayout.WEST);
		contentPane.add(statusBar, BorderLayout.SOUTH);
		setContentPane(contentPane);
		createPopupMenu();
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
        
//        mapBtn = new JToggleButton("Hide Map",
        mapBtn = new HoverButton("Hide Map",
                rf.getIcon(Resources.ICON_DIR+Resources.MAP_ICON)
        		);
        mapBtn.setToolTipText("Hide Map");
        mapBtn.setSelected(true);
        mapBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                	mapVisible = !mapVisible;
                	if (backgroundMap != null) {
                		backgroundMap.setVisible(mapVisible);
                	}
                	if (mapVisible) {
                    	mapBtn.setText("Hide Map");
                    	mapBtn.setToolTipText("Hide Map");
                	} else {
                    	mapBtn.setText("Show Map");
                    	mapBtn.setToolTipText("Show Map");
                	}
                }
            });
        toolBar.add(mapBtn);
       
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
        					getResizedImage(
        					Resources.MOBILENODE_DIR+File.separator+
        							MobileNodeFactory.getInstance().getMobileNodeAt(i).getIconStr(), 16, 16), 
        					true); 
            mobileNodeCheckBox[i].setSelectedIcon(getCheckedResizedImage(
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
    
    private void createPopupMenu() {
    	popupMenu = new JPopupMenu();
    	
    	modifyMenu = new JMenuItem("Modify Information");
    	modifyMenu.setActionCommand("ModifyInformation");
    	modifyMenu.addActionListener(this);
    	
    	popupMenu.add(modifyMenu);
    	
    	deleteMenu = new JMenuItem("Delete");
    	deleteMenu.setActionCommand("Delete");
    	deleteMenu.addActionListener(this);
    	
    	popupMenu.add(deleteMenu);
    	
    	popupMenu.addSeparator();

    	dataEditorMenu = new JMenuItem("Edit Data");
    	dataEditorMenu.setActionCommand("EditData");
    	dataEditorMenu.addActionListener(this);

    	dataSeriesEditorMenu = new JMenuItem("Edit Series Data");
    	dataSeriesEditorMenu.setActionCommand("EditSeriesData");
    	dataSeriesEditorMenu.addActionListener(this);

    	pathEditorMenu = new JMenuItem("Edit Path");
    	pathEditorMenu.setActionCommand("EditPath");
    	pathEditorMenu.addActionListener(this);
    	
    	velocityEditorMenu = new JMenuItem("Edit Velocity");
    	velocityEditorMenu.setActionCommand("EditVelocity");
    	velocityEditorMenu.addActionListener(this);
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
        
		//	add listeners
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
        view.addMouseWheelListener(this);
        
        //	add network info view
        networkInfoPane = new NetworkInfoPane();
        tp.addTab(Env.NETWROK_TAB_TITLE, networkInfoPane);
        
        mobileNodeInfoPane = new MobileNodeInfoPane();
        tp.addTab(Env.MOBILE_NODE_TAB_TITLE, mobileNodeInfoPane);
        
        handoverInfoArea = new JTextArea();
        handoverInfoArea.setEditable(false);
        tp.addTab(Env.HANDOVER_TAB_TITLE, new JScrollPane(handoverInfoArea));
        
        ntp = new JTabbedPane();
        networkListModel = new DefaultListModel();
        networkList = new JList(networkListModel);
        networkList.addMouseListener(this);
        ntp.addTab("Networks", new JScrollPane(networkList));
        
        numberNetwork = new JTextArea();
        numberNetwork.setEditable(false);
        ntp.addTab("# of Network", new JScrollPane(numberNetwork));
        ntp.setToolTipTextAt(1, "Number of each network");
        
        mtp = new JTabbedPane();
        mnodeListModel = new DefaultListModel();
        mnodeList = new JList(mnodeListModel);
        mnodeList.addMouseListener(this);
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
		splitPane.setDividerLocation(Env.VIEW_WIDTH-150);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 50);
		tp.setMinimumSize(minimumSize);
		leftPane.setMinimumSize(minimumSize);
	
        getContentPane().add(splitPane, BorderLayout.CENTER);
	}
	
	void resetView() {
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
			
		updateNetworkComponentCount();
		updateMobileNodeComponentCount();
		
		setZoom(1.0);
		setState(SELECT);
	}
	
	void setViewTitle(String mapName, String fileName) {
        tp.setTitleAt(0, Env.TAB_TITLE+" [" +
    		(mapName != null ? mapName : "") + "] - " + 
    		(fileName == null ? "nofile" : fileName));
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
        view.setPreferredSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
       
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            backgroundMap = new JLabel();
            backgroundMap.setLocation(0,0);
            backgroundMap.setIcon(getResizedImage(
						Resources.MAP_DIR+mapInfo.getBackground(),
						mapInfo.getWidth(), mapInfo.getHeight()));
            backgroundMap.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
            view.add(backgroundMap,0);
    	}
    	NetworkDeviceInfo[] info = mapInfo.getDevices();
    	if (info != null) {
        	for (int i = 0; i < info.length; i++) {
        		NetworkComponent comp = new NetworkComponent(this);
        		comp.setDeviceInfo(info[i]);
                comp.setIcon(deviceImg[info[i].getIcon()], info[i].getIcon());
        		view.add(comp,0);
                networks.add(comp);
                networkListModel.addElement(info[i]);
        	}
    	}
    	setNetworkTitle(networkListModel.getSize());
		updateNetworkComponentCount();
   	
    	NetworkMobileNodeInfo[] nodeInfo = mapInfo.getMnodes();
    	if (nodeInfo != null) {
        	for (int i = 0; i < nodeInfo.length; i++) {
        		MobileNodeComponent comp = new MobileNodeComponent();
        		comp.setNetworkMobileNodeInfo(nodeInfo[i]);
                comp.setIcon(nodeInfo[i].getMobileNode().getIcon());
        		view.add(comp,0);
                mnodes.add(comp);
                mnodeListModel.addElement(nodeInfo[i]);
                
                //	add network handover listener
                nodeInfo[i].getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        	}
    	}
    	setMnodeTitle(mnodeListModel.getSize());
		updateMobileNodeComponentCount();

    	ServerInfo[] serverInfo = mapInfo.getServers();
    	if (nodeInfo != null) {
        	for (int i = 0; i < serverInfo.length; i++) {
        		ServerComponent comp = new ServerComponent();
        		comp.setServerInfo(serverInfo[i]);
    			comp.setIcon(serverImg[serverInfo[i].getType()]);
        		view.add(comp,0);
                snodes.add(comp);
                
                //	add network server listener
//                nodeInfo[i].getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        	}
    	}

		addDesComponent();
		
		//	update number of each network and each mobile nodes
        repaint();
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
	
	/*
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
    */

    static ImageIcon getResizedImage(String imageStr, int width, int height) {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	try {
    		BufferedImage bi =  rf.getBufferedImage(imageStr);
    		BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g = bi2.createGraphics();
    		g.drawImage(bi, 0, 0, width, height, null);
    		ImageIO.write(bi2, "png", outputStream);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return new ImageIcon(outputStream.toByteArray());
    }

    static ImageIcon getCheckedResizedImage(String imageStr, int width, int height) {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	try {
    		BufferedImage bi =  rf.getBufferedImage(imageStr);
    		BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g = bi2.createGraphics();
    		g.drawImage(bi, 0, 0, width, height, null);
    		g.setColor(Color.blue);
    		g.drawRect(0,0,width-1, height-1);
    		ImageIO.write(bi2, "png", outputStream);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return new ImageIcon(outputStream.toByteArray());
    }
    
	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
		if (getState() == NONE) {
			return;
		}
		int x = (int)(e.getX()/view.getZoom());
		int y = (int)(e.getY()/view.getZoom());
		if (getState() != SELECT) {
    		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && getState() == CREATE_NETWORK) {
    			addNetwork(x, y);
    		} else if (e.getModifiers() == MouseEvent.BUTTON1_MASK && getState() == CREATE_MOBILENODE) {
    			addMobileNode(x, y);
    		} else if (e.getModifiers() == MouseEvent.BUTTON1_MASK && getState() == CREATE_SERVER) {
    			addServer(x, y);
    		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
    			setState(SELECT);
    		}
    		return;
		}
		
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			//	at the view
			if (e.getSource() == view) {
				NetworkComponent comp = findNetworkComponent(x, y);
				if (comp != null) {
                    selectedComp = comp;
                    modifyMenu.setText("Modify Network (" + comp.getDeviceInfo().getDevice().getName()+")");
                    deleteMenu.setText("Delete Network (" + comp.getDeviceInfo().getDevice().getName()+")");
                        
                    popupMenu.remove(pathEditorMenu);
                    popupMenu.remove(velocityEditorMenu);

                    popupMenu.add(dataEditorMenu);
                    dataEditorMenu.setText("Edit Data of (" + comp.getDeviceInfo().getDevice().getName() +")");
                    popupMenu.add(dataSeriesEditorMenu);
                    dataSeriesEditorMenu.setText("Edit Series Data of (" + comp.getDeviceInfo().getDevice().getName() +")");
                    popupMenu.show(view, x, y); 
                    return;
				}
				MobileNodeComponent mComp = findMobileNodeComponent(x, y);
				if (mComp != null) {
                    selectedComp = mComp;
                    modifyMenu.setText("Modify Mobile Node (" + mComp.getDeviceInfo().getDevice().getId()+")");
                    deleteMenu.setText("Delete Mobile Node (" + mComp.getDeviceInfo().getDevice().getId()+")");
                    popupMenu.remove(dataEditorMenu);
                    popupMenu.remove(dataSeriesEditorMenu);
                    popupMenu.add(pathEditorMenu);
                    pathEditorMenu.setText("Edit Path of Mobile Node (" + mComp.getDeviceInfo().getDevice().getId()+")");
                    popupMenu.add(velocityEditorMenu);
                    velocityEditorMenu.setText("Edit Velocity of Mobile Node (" + mComp.getDeviceInfo().getDevice().getId()+")");
                    popupMenu.show(view, x, y); 
                    return;
 				
				}
				/*
                for (int i = 0; i < mnodes.size(); i++) {
                    MobileNodeComponent comp = mnodes.elementAt(i);
                    if (comp.isIn(x, y)) {
                        selectedComp = comp;
                        modifyMenu.setText("Modify Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                        deleteMenu.setText("Delete Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                        popupMenu.add(pathEditorMenu);
                        pathEditorMenu.setText("Edit Path of Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                        popupMenu.add(velocityEditorMenu);
                        velocityEditorMenu.setText("Edit Velocity of Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                        popupMenu.show(view, x, y); 
                        return;
                    }
                }
                */
			} else if (e.getSource() == networkList) {
				int index = networkList.getSelectedIndex();
				if (index != -1) {
                    NetworkComponent comp = networks.elementAt(index);
                    selectedComp = comp;
                    modifyMenu.setText("Modify Network (" + comp.getDeviceInfo().getDevice().getName()+")");
                    deleteMenu.setText("Delete Network (" + comp.getDeviceInfo().getDevice().getName()+")");
                        
                    popupMenu.remove(pathEditorMenu);
                    popupMenu.remove(velocityEditorMenu);
                    popupMenu.add(dataEditorMenu);
                    dataEditorMenu.setText("Edit Data of (" + comp.getDeviceInfo().getDevice().getName() +")");
                    popupMenu.add(dataSeriesEditorMenu);
                    dataSeriesEditorMenu.setText("Edit Series Data of (" + comp.getDeviceInfo().getDevice().getName() +")");
                    popupMenu.show(networkList, x, y); 
                    return;
				}
			} else if (e.getSource() == mnodeList) {
				int index = mnodeList.getSelectedIndex();
				if (index != -1) {
                    MobileNodeComponent comp = mnodes.elementAt(index);
                    selectedComp = comp;
                    modifyMenu.setText("Modify Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                    deleteMenu.setText("Delete Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                    popupMenu.remove(dataEditorMenu);
                    popupMenu.remove(dataSeriesEditorMenu);
                    popupMenu.add(pathEditorMenu);
                    pathEditorMenu.setText("Edit Path of Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                    popupMenu.add(velocityEditorMenu);
                    velocityEditorMenu.setText("Edit Velocity of Mobile Node (" + comp.getDeviceInfo().getDevice().getId()+")");
                    popupMenu.show(mnodeList, x, y); 
				}
			}
		} else if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			if (e.getClickCount() == 2) {
				MobileNodeComponent comp = findMobileNodeComponent(x,y);
				if (comp != null) {
                	comp.getDeviceInfo().getDevice().showGUI();
				}
				/*
                for (int i = 0; i < mnodes.size(); i++) {
                    MobileNodeComponent comp = mnodes.elementAt(i);
                    if (comp.isIn(x, y)) {
                    	comp.getDeviceInfo().getDevice().showGUI();
                    }
                }
                */
			} else {
				NetworkComponent comp = findNetworkComponent(x,y);
				if (comp != null) {
                	diff_x = comp.getDeviceInfo().getXpos() - x;
                	diff_y = comp.getDeviceInfo().getYpos() - y;
                	selectedComp = comp;
                	setState(MOVE_NETWORK);
                    return;
				}
				MobileNodeComponent mComp = findMobileNodeComponent(x,y);
				if (mComp != null) {
                	diff_x = mComp.getDeviceInfo().getXpos() - x;
                	diff_y = mComp.getDeviceInfo().getYpos() - y;
                	selectedComp = mComp;
                	setState(MOVE_MOBILENODE);
                    return;
				}
				ServerComponent sComp = findServerComponent(x,y);
				if (sComp != null) {
                	diff_x = sComp.getServerInfo().getXpos() - x;
                	diff_y = sComp.getServerInfo().getYpos() - y;
                	selectedComp = sComp;
                	setState(MOVE_SERVER);
                    return;
				}
				/*
                for (int i = 0; i < networks.size(); i++) {
                    NetworkComponent comp = networks.elementAt(i);
                    if (comp.isIn(x, y)) {
                    	diff_x = comp.getDeviceInfo().getXpos() - x;
                    	diff_y = comp.getDeviceInfo().getYpos() - y;
                    	selectedComp = comp;
                    	setState(MOVE_NETWORK);
                        return;
                    }
                }
                for (int i = 0; i < mnodes.size(); i++) {
                    MobileNodeComponent comp = mnodes.elementAt(i);
                    if (comp.isIn(x, y)) {
                    	diff_x = comp.getDeviceInfo().getXpos() - x;
                    	diff_y = comp.getDeviceInfo().getYpos() - y;
                    	selectedComp = comp;
                    	setState(MOVE_MOBILENODE);
                        return;
                    }
                }
                */
			}
		}
	}
	
	private NetworkComponent findNetworkComponent(int x, int y) {
        for (int i = 0; i < networks.size(); i++) {
            NetworkComponent comp = networks.elementAt(i);
            if (comp.isIn(x, y) && comp.isVisible()) {
            	return comp;
            }
        }
		return null;
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

	private ServerComponent findServerComponent(int x, int y) {
       for (int i = 0; i < snodes.size(); i++) {
            ServerComponent comp = snodes.elementAt(i);
            if (comp.isIn(x, y) && comp.isVisible()) {
                return comp;
            }
        }
		return null;
	}

	public void mouseReleased(MouseEvent e) {
		int x = (int)(e.getX()/view.getZoom());
		int y = (int)(e.getY()/view.getZoom());
		// TODO Auto-generated method stub
		if (getState() == MOVE_NETWORK) {
    		if (selectedComp != null) {
    			NetworkComponent comp = (NetworkComponent)selectedComp;
//    			comp.setLocation(x+diff_x, y+diff_y);
    			comp.getDeviceInfo().setLocation(x+diff_x, y+diff_y);
    			view.repaint();
    			setState(SELECT);
    			selectedComp = null;
    		}
    	}
		if (getState() == MOVE_MOBILENODE) {
    		if (selectedComp != null) {
    			MobileNodeComponent comp = (MobileNodeComponent)selectedComp;
//    			comp.setLocation(x+diff_x, y+diff_y);
    			comp.getDeviceInfo().setLocation(x+diff_x, y+diff_y);
    			view.repaint();
    			setState(SELECT);
    			selectedComp = null;
    		}
    	}
		if (getState() == MOVE_SERVER) {
    		if (selectedComp != null) {
    			ServerComponent comp = (ServerComponent)selectedComp;
//    			comp.setLocation(x+diff_x, y+diff_y);
    			comp.getServerInfo().setLocation(x+diff_x, y+diff_y);
    			view.repaint();
    			setState(SELECT);
    			selectedComp = null;
    		}
    	}

	}

	private void setDesComp(int x, int y, String text, Color background, boolean isVisible) {
        desComp.setLocation(x,y);
        desComp.setText(text);
        desComp.setBackground(background);
        desComp.setVisible(isVisible);
    }
	
	public void mouseDragged(MouseEvent e) {
		int x = (int)(e.getX()/view.getZoom());
		int y = (int)(e.getY()/view.getZoom());
		if (getState() == MOVE_NETWORK) {
			NetworkComponent comp = (NetworkComponent)selectedComp;
			comp.getDeviceInfo().setLocation(x+diff_x, y+diff_y);
			setDesComp(x, y, comp.getDeviceInfo().getComment(), Resources.DES_COLOR_NETWORK, true);
    		view.repaint();
		} else if (getState() == MOVE_MOBILENODE) {
			MobileNodeComponent comp = (MobileNodeComponent)selectedComp;
			comp.getDeviceInfo().setLocation(x+diff_x, y+diff_y);
			setDesComp(x, y, comp.getDeviceInfo().getComment(), Resources.DES_COLOR_MOBILENODE, true);
			view.repaint();
		} else if (getState() == MOVE_SERVER) {
			ServerComponent comp = (ServerComponent)selectedComp;
			comp.getServerInfo().setLocation(x+diff_x, y+diff_y);
			setDesComp(x, y, comp.getServerInfo().getComment(), Resources.DES_COLOR_SERVER, true);
			view.repaint();
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = (int)(e.getX()/view.getZoom());
		int y = (int)(e.getY()/view.getZoom());
		statusBar.setPosition(x, y);
		if (getState() == SELECT) {
			NetworkComponent comp = findNetworkComponent(x,y);
			if (comp != null) {
    			setDesComp(x, y, comp.getDeviceInfo().getComment(), Resources.DES_COLOR_NETWORK, true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        		view.repaint();
                return;
			
			}
			MobileNodeComponent mComp = findMobileNodeComponent(x,y);
			if (mComp != null) {
    			setDesComp(x, y, mComp.getDeviceInfo().getComment(), Resources.DES_COLOR_MOBILENODE, true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        		view.repaint();
                return;
			}

			ServerComponent sComp = findServerComponent(x,y);
			if (sComp != null) {
    			setDesComp(x, y, sComp.getServerInfo().getComment(), Resources.DES_COLOR_SERVER, true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        		view.repaint();
                return;
			}

			/*
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
	            MobileNodeComponent comp = (MobileNodeComponent)mnodes.elementAt(i);
	            if (comp.isIn(x, y)) {
        			setDesComp(x, y, comp.getDeviceInfo().getComment(), Resources.DES_COLOR_MOBILENODE, true);
	                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            		view.repaint();
	                return;
	            }
	        }
	        */
	        view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        desComp.setVisible(false);
		}
	}

	public void openMonitorView(NetworkMapInfo mapInfo) {
		monitorView = new MonitorView(mapInfo.getName());
    	monitorView.setMapInfo(mapInfo);
    	monitorView.repaint();
		monitorView.setVisible(true);
	}
	
	private void createNetworkMap() {
	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		String nameStr[] = {"Name", "Description", "Width", "Height"};
		String valueStr[] = {"", "", "1024", "768"};
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
        
	    message[0] = "Create Network Map";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Create Network Map",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.OK_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            owner.createNetworkMap(valueFld[0].getText(), 
					valueFld[1].getText(), Integer.parseInt(valueFld[2].getText()),
            		Integer.parseInt(valueFld[3].getText()), background.getText());
		}
	}

	private synchronized void openNetworkMap() {
		JFileChooser jf = new JFileChooser(new File(Resources.DATA_DIR));
		jf.setDialogTitle("Choose a network map file");
		jf.setDialogType(JFileChooser.OPEN_DIALOG);
		jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
    			
		ZFileFilter filter = new ZFileFilter();
		filter.addExtension("xml");
		filter.setDescription("Network Map File");
		jf.setFileFilter(filter);
		jf.setLocale(Locale.US);
    			
		if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			owner.openNetworkMap(jf.getSelectedFile());
		}
	}
	
	private synchronized void saveNetworkMap() {
		JFileChooser jf = new JFileChooser(new File(Resources.DATA_DIR));
		jf.setDialogTitle("Save a network map");
		jf.setDialogType(JFileChooser.SAVE_DIALOG);
		jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
		ZFileFilter filter = new ZFileFilter();
		filter.addExtension("xml");
		filter.setDescription("Network Map File");
		jf.setFileFilter(filter);
		jf.setLocale(Locale.US);
		
		if (jf.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = jf.getSelectedFile();
			if (!file.getName().endsWith(".xml")) {
				file = new File(file.getPath()+".xml");
			}
			owner.saveNetworkMap(file);
    	}
	}

 
    private void modifyNetworkMap(NetworkMapInfo mapInfo) {
		if (mapInfo == null)
			return;

        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

		String nameStr[] = {"Name", "Description", "Width", "Height"};
		String valueStr[] = {mapInfo.getName(), mapInfo.getDescr(), 
			String.valueOf(mapInfo.getWidth()), String.valueOf(mapInfo.getHeight())};
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
        background.setText(mapInfo.getBackground());
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
        
        message[0] = "Modify Network Map";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, "Modify Network Map",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            owner.modifyNetworkMap(valueFld[0].getText(), valueFld[1].getText(), Integer.parseInt(valueFld[2].getText()),
                    Integer.parseInt(valueFld[3].getText()), background.getText());
        }
    }

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String menuItemString = e.getActionCommand();
		
		if (menuItemString.intern() == "New") {
			createNetworkMap();
		} else if (menuItemString.intern() == "Open") {
			openNetworkMap();
		} else if (menuItemString.intern() == "Save") {
			if (owner.getCurrentFile() != null) {
    			owner.saveNetworkMap(owner.getCurrentFile());
			} else {
				saveNetworkMap();
			}
		} else if (menuItemString.intern() == "SaveAs") {
			saveNetworkMap();
		} else if (menuItemString.intern() == "Preference") {
			modifyNetworkMap(owner.getNetworkMapInfo());
		} else if (menuItemString.intern() == "AddNetwork") {
			setState(CREATE_NETWORK);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (menuItemString.intern() == "AddMN") {
			setState(CREATE_MOBILENODE);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if (menuItemString.intern() == "AddServer") {
			setState(CREATE_SERVER);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (menuItemString.intern() == "LaunchMonitorView") {
			openMonitorView(owner.getNetworkMapInfo());
		} else if (menuItemString.intern() == "ModifyInformation") {
			modifyInfo();
		} else if (menuItemString.intern() == "Delete") {
			deleteInfo();
		} else if (menuItemString.intern() == "RunNetwork") {
			owner.startPlayer();
			UpdateTimer.getInstance().addUpdateListener(this);
			setSimState("RUNNING");
		} else if (menuItemString.intern() == "StopNetwork") {
			UpdateTimer.getInstance().removeUpdateListener(this);
			owner.stopPlayer();
			setSimState("STOPPED");
		} else if (menuItemString.intern() == "Export") {
			exportPNGImage();
		} else if (menuItemString.intern() == "Exit") {
			exit();
		} else if (menuItemString.intern() == "About") {
			JOptionPane.showMessageDialog(null, Env.ABOUT_MSG);
		} else if (menuItemString.intern() == "EditPath") {
			openEditorPathDialog(owner.getNetworkMapInfo(), owner.getCurrentFile());
		} else if (menuItemString.intern() == "EditData") {
			editData();
		} else if (menuItemString.intern() == "EditSeriesData") {
			editSeriesData();
		} else if (menuItemString.intern() == "EditVelocity") {
			editVelocity();
		} else if (menuItemString.intern() == "StartSimulator") {
			launchNetworkSimulator();
		} else if (menuItemString.intern() == "RandomSimulation") {
			randomSimulation();
		} else if (menuItemString.intern() == "X 0.1") {
			setZoom(0.1);
		} else if (menuItemString.intern() == "X 0.2") {
			setZoom(0.2);
		} else if (menuItemString.intern() == "X 0.5") {
			setZoom(0.5);
		} else if (menuItemString.intern() == "X 1.0") {
			setZoom(1.0);
		} else if (menuItemString.intern() == "X 2.0") {
			setZoom(2.0);
		} else if (menuItemString.intern() == "X 3.0") {
			setZoom(3.0);
		}
	}
	
	private void editVelocity() {
		NetworkMobileNodeInfo nodeInfo = ((MobileNodeComponent)selectedComp).getDeviceInfo();
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("Velocity");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
        
        velocityFld = new JTextField(4);
        velocityFld.setEditable(false);
        velocityFld.setText(String.valueOf(nodeInfo.getCurrentVelocity()));
        velocityFld.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(velocityFld,c);
        p.add(velocityFld);
        
        JSlider velocity = new JSlider(JSlider.HORIZONTAL, 
        		nodeInfo.getMobileNode().getMinVelocity(), 
        		nodeInfo.getMobileNode().getMaxVelocity(), 
        		nodeInfo.getCurrentVelocity());
        velocity.setMajorTickSpacing(1);
        velocity.setPaintTicks(true);
        /*
        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 1 ), new JLabel("0.1") );
        labelTable.put( new Integer( 10 ), new JLabel("1.0") );
        labelTable.put( new Integer( 20), new JLabel("2.0") );
        labelTable.put( new Integer( 30), new JLabel("3.0") );
        speedRateBar.setLabelTable( labelTable );
        speedRateBar.setPaintLabels(true);
        toolBar.add(speedRateBar);
        toolBar.add(new Label("                    "));
        */
        velocity.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
        	    if (!source.getValueIsAdjusting()) {
        	        velocityFld.setText(String.valueOf((int)source.getValue()));
        	    }
        	}
        });
       
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(velocity,c);
        p.add(velocity);

        message[0] = "Edit Velocity of Mobile Node ("+nodeInfo.getDevice().getId()+")";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Edit Velocity",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((velocityFld.getText() == null || velocityFld.getText().intern() == "".intern()) &&
            (velocityFld.getText() == null || velocityFld.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
            nodeInfo.setCurrentVelocity(Integer.parseInt(velocityFld.getText()));
        }		
        setState(SELECT);
        repaint();
		
	}
	
	private void editData() {
		NetworkDeviceInfo deviceInfo = ((NetworkComponent)selectedComp).getDeviceInfo();
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);
        
		JTextField valueFld[] = new JTextField[NetworkData.NETWORK_PARAMETERS.length];

		for (int i = 0; i < NetworkData.NETWORK_PARAMETERS.length; i++) {
			JLabel label = new JLabel(NetworkData.NETWORK_PARAMETERS[i]);
			c.gridwidth = GridBagConstraints.RELATIVE;
			gridbag.setConstraints(label,c);
			p.add(label);
				
			valueFld[i] = new JTextField(5);
			valueFld[i].setText(String.valueOf(deviceInfo.getData().getNetworkValue(i)));
			valueFld[i].setForeground(Color.blue);
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(valueFld[i],c);
			p.add(valueFld[i]);
		}
		
        message[0] = "Edit Data of Access Point ("+deviceInfo.getDevice().getName()+")";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Edit Data",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < valueFld.length; i++) {
            	deviceInfo.getData().setNetworkValue(i, valueFld[i].getText());
            }
        }		
        setState(SELECT);
        repaint();
	}
	
	private void editSeriesData() {
		NetworkDeviceInfo deviceInfo = ((NetworkComponent)selectedComp).getDeviceInfo();
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);
        
		JTextField valueFld[] = new JTextField[NetworkData.NETWORK_PARAMETERS.length];

		for (int i = 0; i < NetworkData.NETWORK_PARAMETERS.length; i++) {
			JLabel label = new JLabel(NetworkData.NETWORK_PARAMETERS[i]);
			c.gridwidth = GridBagConstraints.RELATIVE;
			gridbag.setConstraints(label,c);
			p.add(label);
				
			valueFld[i] = new JTextField(30);
			valueFld[i].setText(deviceInfo.getData().getNetworkSeriesValue(i));
			valueFld[i].setForeground(Color.blue);
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(valueFld[i],c);
			p.add(valueFld[i]);
		}
		
        message[0] = "Edit Data Series of Access Point ("+deviceInfo.getDevice().getName()+") -  (time, value|....)";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Edit Series Data",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            for (int i = 0; i < valueFld.length; i++) {
            	deviceInfo.getData().setNetworkSeriesValue(i, valueFld[i].getText());
            }
        }		
        setState(SELECT);
        repaint();
	}
	private void modifyInfo() {
		if (selectedComp instanceof NetworkComponent) {
			modifyNetwork((NetworkComponent)selectedComp);
		} else if (selectedComp instanceof MobileNodeComponent) {
			modifyMobileNode((MobileNodeComponent)selectedComp);
		} else {
			modifyServer((ServerComponent)selectedComp);
		}
	}

	private void deleteInfo() {
		if (selectedComp instanceof NetworkComponent) {
			deleteNetwork((NetworkComponent)selectedComp);
		} else {
			deleteMobileNode((MobileNodeComponent)selectedComp);
		}
	}

	private void addNetwork(int x, int y) {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("Name");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField name = new JTextField(30);
        name.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(name,c);

        p.add(name);

        label = new JLabel("Network");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String networkTypes[] = dpnm.network.NetworkFactory.getInstance().getNetworkNames();
        JComboBox networkCombo = new JComboBox(networkTypes);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(networkCombo,c);

        p.add(networkCombo);
 
        message[0] = "Add New Network";
        message[1] = p;
        
        int result;

        
        result = JOptionPane.showOptionDialog(null, message, "Add New Network",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((name.getText() == null || name.getText().intern() == "".intern()) &&
            (name.getText() == null || name.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
			NetworkDeviceInfo info = 
				owner.addNetwork(name.getText(), (String)networkCombo.getSelectedItem(), x, y);
			NetworkComponent comp = new NetworkComponent(this);
			comp.setDeviceInfo(info);
			comp.setIcon(deviceImg[info.getIcon()], info.getIcon());
			view.add(comp,0);
			networks.add(comp);
			networkListModel.addElement(info);
			updateNetworkComponentCount();
        	setNetworkTitle(networkListModel.getSize());
			addDesComponent();
        }		
        setState(SELECT);
        repaint();
	}
	
	private void modifyNetwork(NetworkComponent comp) {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("Name");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField name = new JTextField(30);
        name.setText(comp.getDeviceInfo().getDevice().getName());
        name.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(name,c);

        p.add(name);

        label = new JLabel("Network");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String networkTypes[] = dpnm.network.NetworkFactory.getInstance().getNetworkNames();
        JComboBox networkCombo = new JComboBox(networkTypes);
        int i = 0;
        for (; i < networkTypes.length; i++) {
        	if (networkTypes[i].intern() == comp.getDeviceInfo().getDevice().getNetwork().getName().intern())
        		break;
        }
        networkCombo.setSelectedIndex(i);
        networkCombo.setEnabled(false);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(networkCombo,c);

        p.add(networkCombo);
        
        label = new JLabel("Xpos");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField xPosFld = new JTextField(4);
        xPosFld.setText(String.valueOf(comp.getDeviceInfo().getXpos()));
        xPosFld.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(xPosFld,c);

        p.add(xPosFld);
 
        label = new JLabel("Ypos");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField yPosFld = new JTextField(4);
        yPosFld.setText(String.valueOf(comp.getDeviceInfo().getYpos()));
        yPosFld.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(yPosFld,c);

        p.add(yPosFld);
 
        message[0] = "Modify Network ("+comp.getDeviceInfo().getDevice().getName()+")";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Modify Network ("+comp.getDeviceInfo().getDevice().getName()+")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((name.getText() == null || name.getText().intern() == "".intern()) &&
            (name.getText() == null || name.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
            networkListModel.removeElement(comp.getDeviceInfo());
			owner.modifyNetwork(comp.getDeviceInfo().getDevice().getMacAddress(),
            name.getText(), Integer.parseInt(xPosFld.getText()),
            Integer.parseInt(yPosFld.getText()));
			networkListModel.addElement(comp.getDeviceInfo());
			updateNetworkComponentCount();
			selectedComp = null;
        }		
        setState(SELECT);
        repaint();
	}
	
	private void deleteNetwork(NetworkComponent comp) {
		networkListModel.removeElement(comp.getDeviceInfo());
		updateNetworkComponentCount();
    	setNetworkTitle(networkListModel.getSize());
		owner.deleteNetwork(comp.getDeviceInfo().getDevice().getMacAddress());
		view.remove(comp);
		networks.remove(comp);
		setState(SELECT);
		repaint();
		selectedComp = null;
	}
	
	private void addMobileNode(int x, int y) {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("ID");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField id = new JTextField(30);
        id.setText("Node");
        id.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(id,c);

        p.add(id);

        label = new JLabel("Type");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JComboBox typeCombo = new JComboBox(MobileNodeFactory.getInstance().getMobileNodes());
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        typeCombo.setRenderer(renderer);
        typeCombo.setMaximumRowCount(3);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(typeCombo,c);

        p.add(typeCombo);
        
        label = new JLabel("Application");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String[] apps = StaticFeatureModel.getInstance().getFeatures("Application");
        JCheckBox[] appBox = new JCheckBox[apps.length];
        JPanel checkPane = new JPanel();
        for (int i = 0; i < apps.length; i++) {
        	appBox[i] = new JCheckBox(apps[i]);
        	checkPane.add(appBox[i]);
        }
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(checkPane,c);

        p.add(checkPane);
 
        label = new JLabel("Network Interface");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String[] nis = StaticFeatureModel.getInstance().getFeatures("NetworkInterface");
        JCheckBox[] niBox = new JCheckBox[nis.length];
        checkPane = new JPanel();
        for (int i = 0; i < nis.length; i++) {
        	niBox[i] = new JCheckBox(nis[i]);
        	checkPane.add(niBox[i]);
        }
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(checkPane,c);

        p.add(checkPane);
 
        message[0] = "Create New Mobile Node";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, "Create New Mobile Node",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((id.getText() == null || id.getText().intern() == "".intern()) &&
            (id.getText() == null || id.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
            Vector<String> appList = new Vector<String>();
            for (int i = 0; i < appBox.length; i++) {
            	if (appBox[i].isSelected()) {
            		appList.addElement(apps[i]);
            	}
            }
            Vector<String> niList = new Vector<String>();
            for (int i = 0; i < niBox.length; i++) {
            	if (niBox[i].isSelected()) {
            		niList.addElement(nis[i]);
            	}
            }
            
			NetworkMobileNodeInfo info = owner.createMobileNode(
            		id.getText(), 
            		appList,
            		niList,
            		x, y,
        			MobileNodeFactory.getInstance().getMobileNodeNames()[typeCombo.getSelectedIndex()]
        			                                                     );
            		
    		MobileNodeComponent comp = new MobileNodeComponent();
    		comp.setNetworkMobileNodeInfo(info);
            comp.setIcon(info.getMobileNode().getIcon());
    		view.add(comp,0);
            mnodes.add(comp);
            mnodeListModel.addElement(info);
            updateMobileNodeComponentCount();
        	setMnodeTitle(mnodeListModel.getSize());
            addDesComponent();
            
            //	add handover listener
            info.getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        }		
        setState(SELECT);
        repaint();
	}
	
	private void modifyMobileNode(MobileNodeComponent comp) {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("ID");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField id = new JTextField(30);
        id.setEnabled(false);
        id.setText(comp.getDeviceInfo().getDevice().getId());
        id.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(id,c);

        p.add(id);

        label = new JLabel("Type");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JComboBox typeCombo = new JComboBox(MobileNodeFactory.getInstance().getMobileNodes());
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        typeCombo.setRenderer(renderer);
        typeCombo.setMaximumRowCount(3);
 
        for (int i = 0; i < MobileNodeFactory.getInstance().getMobileNodeNames().length; i++) {
        	if (comp.getDeviceInfo().getMobileNode().getName().intern() == 
        			MobileNodeFactory.getInstance().getMobileNodeNames()[i].intern()) {
            	typeCombo.setSelectedIndex(i);
            	break;
        	}
        }
 
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(typeCombo,c);

        p.add(typeCombo);
        
        label = new JLabel("Application");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String[] apps = StaticFeatureModel.getInstance().getFeatures("Application");
        JCheckBox[] appBox = new JCheckBox[apps.length];
        JPanel checkPane = new JPanel();
        MobileApplication mApp[] = comp.getDeviceInfo().getDevice().getApplications();
        for (int i = 0; i < apps.length; i++) {
        	appBox[i] = new JCheckBox(apps[i]);
        	checkPane.add(appBox[i]);
        	for (int j = 0; j < mApp.length; j++) {
        		if (apps[i].intern() == mApp[j].getName().intern()) {
        			appBox[i].setSelected(true);
        		}
        	}
        }
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(checkPane,c);

        p.add(checkPane);
 
        label = new JLabel("Network Interface");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String[] nis = StaticFeatureModel.getInstance().getFeatures("NetworkInterface");
        JCheckBox[] niBox = new JCheckBox[nis.length];
        checkPane = new JPanel();
        NetworkInterface mNis[] = comp.getDeviceInfo().getDevice().getNetworkInterfaces();
        for (int i = 0; i < nis.length; i++) {
        	niBox[i] = new JCheckBox(nis[i]);
        	checkPane.add(niBox[i]);
        	for (int j = 0; j < mNis.length; j++) {
        		if (nis[i].intern() == mNis[j].getName().intern()) {
            		niBox[i].setSelected(true);
        		}
        	}
        }
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(checkPane,c);

        p.add(checkPane);
 
        label = new JLabel("Xpos");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField xPosFld = new JTextField(4);
        xPosFld.setText(String.valueOf(comp.getDeviceInfo().getXpos()));
        xPosFld.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(xPosFld,c);

        p.add(xPosFld);
 
        label = new JLabel("Ypos");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField yPosFld = new JTextField(4);
        yPosFld.setText(String.valueOf(comp.getDeviceInfo().getYpos()));
        yPosFld.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(yPosFld,c);

        p.add(yPosFld);
 
        message[0] = "Modify Mobile Node ("+comp.getDeviceInfo().getDevice().getId()+")";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Modify Mobile Node ("+comp.getDeviceInfo().getDevice().getId()+")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((id.getText() == null || id.getText().intern() == "".intern()) &&
            (id.getText() == null || id.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
            Vector<String> appList = new Vector<String>();
            for (int i = 0; i < appBox.length; i++) {
            	if (appBox[i].isSelected()) {
            		appList.addElement(apps[i]);
            	}
            }
            Vector<String> niList = new Vector<String>();
            for (int i = 0; i < niBox.length; i++) {
            	if (niBox[i].isSelected()) {
            		niList.addElement(nis[i]);
            	}
            }
            mnodeListModel.removeElement(comp.getDeviceInfo());
			owner.modifyMobileNode(
					id.getText(),
					appList,
					niList,
            Integer.parseInt(xPosFld.getText()),
            Integer.parseInt(yPosFld.getText()),
			MobileNodeFactory.getInstance().getMobileNodeNames()[typeCombo.getSelectedIndex()]
            );
            mnodeListModel.addElement(comp.getDeviceInfo());
            updateMobileNodeComponentCount();
			selectedComp = null;
        }		
        setState(SELECT);
        repaint();
	}
	
	private void deleteMobileNode(MobileNodeComponent comp) {
        mnodeListModel.removeElement(comp.getDeviceInfo());
        comp.getDeviceInfo().getDevice().getNetworkInterfaceManager().removeNetworkHandoverListener(this);
    	setMnodeTitle(mnodeListModel.getSize());
		owner.deleteMobileNode(
				comp.getDeviceInfo().getDevice().getId());
		view.remove(comp);
		mnodes.remove(comp);
		setState(SELECT);
        updateMobileNodeComponentCount();
		repaint();
		selectedComp = null;
	}
	
	private void addServer(int x, int y) {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("ID");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField id = new JTextField(30);
        id.setText("Server");
        id.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(id,c);

        p.add(id);

        label = new JLabel("Type");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        
        JComboBox typeCombo = new JComboBox(Server.TYPE);
        ServerComboBoxRenderer renderer = new ServerComboBoxRenderer();
        typeCombo.setRenderer(renderer);
        typeCombo.setMaximumRowCount(3);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(typeCombo,c);

        p.add(typeCombo);
        
        message[0] = "Create New Server";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, "Create New Server",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((id.getText() == null || id.getText().intern() == "".intern()) &&
            (id.getText() == null || id.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert id",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
            
			ServerInfo info = owner.createServer(
            		id.getText(), 
        			typeCombo.getSelectedIndex(),
            		x, y);
            		
    		ServerComponent comp = new ServerComponent();
    		comp.setServerInfo(info);
			comp.setIcon(serverImg[info.getType()]);

    		view.add(comp,0);
            snodes.add(comp);
            addDesComponent();
            
            //	TODO: add monitoring listener
//            info.getDevice().getNetworkInterfaceManager().addNetworkHandoverListener(this);
        }		
        setState(SELECT);
        repaint();
	}
	
	private void modifyServer(ServerComponent comp) {
	}
	
	private void deleteServer(ServerComponent comp) {
        mnodeListModel.removeElement(comp.getServerInfo());
        //	TODO: remove listeners
//        comp.getServerInfo().getDevice().getNetworkInterfaceManager().removeNetworkHandoverListener(this);
    	owner.deleteServer(
				comp.getServerInfo().getServer().getId());
		view.remove(comp);
		snodes.remove(comp);
		setState(SELECT);
		repaint();
		selectedComp = null;
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
    
    void exportPNGImage(File pngFile) {
    	NetworkMapInfo mapInfo = owner.getNetworkMapInfo();
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		statusBar.setState(getStateText(state));
		if (state == SELECT) {
			view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	void setSimState(String state) {
		statusBar.setSimState(state);
	}
	
    private String getStateText(int state) {
        switch(state) {
        case NONE:
            return "NONE";
        case SELECT:
            return "SELECT";
        case CREATE_NETWORK:
            return "CREATE NETWORK";
        case CREATE_MOBILENODE:
            return "CREATE MOBILE NODE";
        case CREATE_SERVER:
            return "CREATE SERVER";
        case MODIFY_NETWORK:
            return "MODIFY NETWORK";
        case MODIFY_MOBILENODE:
            return "MODIFY MOBILE NODE";
        case MODIFY_SERVER:
            return "MODIFY SERVER";
        case MOVE_NETWORK:
            return "MOVE NETWORK";
        case MOVE_MOBILENODE:
            return "MOVE MOBILE NODE";
        case MOVE_SERVER:
            return "MOVE SERVER";
        }
        return "NOT DEFINED";
    }
    
    private void exit() {
    	owner.exit();
    }
    
    private void openEditorPathDialog(NetworkMapInfo mapInfo, String filename) {
    	PathEditor editor = new PathEditor(this,
    			mapInfo,  ((MobileNodeComponent)selectedComp).getDeviceInfo(),
    			filename);
    	editor.setVisible(true);
    }
    
    private void launchNetworkSimulator() {
        owner.launchSimulator(true);

// 	    Object[] message = new Object[2];
//	    JPanel p = new JPanel();
//        GridBagLayout gridbag = new GridBagLayout();
//        GridBagConstraints c = new GridBagConstraints();
//	    p.setLayout(gridbag);
//
//		JLabel label = new JLabel("Time interval (ms)");
//		c.gridwidth = GridBagConstraints.RELATIVE;
//		gridbag.setConstraints(label,c);
//		p.add(label);
//				
//		JTextField time = new JTextField(6);
//		time.setText("100");
//		time.setForeground(Color.blue);
//		c.gridwidth = GridBagConstraints.REMAINDER;
//		gridbag.setConstraints(time,c);
//		p.add(time);
//
//	    message[0] = "Start Network Simulator";
//	    message[1] = p;
//	    
//	    int result;
//
//		result = JOptionPane.showOptionDialog(null, message, "Start Network Simulator",
//				JOptionPane.OK_CANCEL_OPTION,
//				JOptionPane.INFORMATION_MESSAGE, null, null, null);
//
//		if ( result == JOptionPane.OK_OPTION ) {
//            if (time.getText() == null || time.getText().intern() == "".intern()) {
//                JOptionPane.showMessageDialog(this, "Please, insert time interval",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//            	return;
//            }
//            
//            owner.launchSimulator(Long.parseLong(time.getText()));
//		}
//	   	
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
            		Integer.parseInt(valueFld[6].getText())
            		);
    	}
    }
    
    class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        private Font uhOhFont;
        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }
    
        /*
        * This method finds the image and text corresponding
        * to the selected value and returns the label, set up
        * to display the text and image.
        */
        public Component getListCellRendererComponent(
                            JList list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {
        //Get the selected index. (The index param isn't
        //always valid, so just use the value.)
        IMobileNode selectedNode = (IMobileNode)value;
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        //Set the icon and text.  If icon was null, say so.
        
        setIcon(rf.getIcon(Resources.MOBILENODE_DIR+selectedNode.getIconStr()));
        setText(selectedNode.getName() + ", Velocity["+selectedNode.getMinVelocity()+","+
        		selectedNode.getMaxVelocity()+"]");
        setFont(list.getFont());
        return this;
        }
    }
    
    class ServerComboBoxRenderer extends JLabel implements ListCellRenderer {
        private Font uhOhFont;
        public ServerComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }
    
        /*
        * This method finds the image and text corresponding
        * to the selected value and returns the label, set up
        * to display the text and image.
        */
        public Component getListCellRendererComponent(
                            JList list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {
        //Get the selected index. (The index param isn't
        //always valid, so just use the value.)
        String selectedNode = (String)value;
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        //Set the icon and text.  If icon was null, say so.
        
        setIcon(rf.getIcon(Resources.SERVER_DIR+Resources.SERVER_IMG_STR[Server.getType(selectedNode)]));
        setText(selectedNode);
        setFont(list.getFont());
        return this;
        }
    }
    private void updateNetworkComponentCount() {
    	numberNetwork.setText("");
    	// # of each network
    	int count[] = owner.getEachNetworkCount();
    	for (int i = 0; count != null && i < count.length; i++) {
    		numberNetwork.append(NetworkFactory.getInstance().getNetworkNameAt(i) + ": " + count[i]+"\n");
    	}
    }
    
    private void updateMobileNodeComponentCount() {
    	numberMobileNode.setText("");
    	// # of each mobile node
    	int count[] = owner.getEachMobileNodeCount();
    	for (int i = 0; count != null && i < count.length; i++) {
    		numberMobileNode.append(MobileNodeFactory.getInstance().getMobileNodeNameAt(i) + ": " + count[i]+"\n");
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
    	if (owner.getNetworkMapInfo() != null) {
    		view.setZoom(zoom);
        	int w = owner.getNetworkMapInfo().getWidth();
        	int h = owner.getNetworkMapInfo().getHeight();
        	
        	Dimension dim = new Dimension((int)(w * zoom), (int)(h*zoom));
        	view.setPreferredSize(dim);
        	view.repaint();
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

	public void updateInfo() {
		// TODO Auto-generated method stub
		view.repaint();
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
		
		if (Conf.DEBUG) {
			Logger.getInstance().logEmulator("EmulatorGUI", sb.toString());
		}
		handoverInfoArea.getCaret().setDot(handoverInfoArea.getText().length());
	}
}
