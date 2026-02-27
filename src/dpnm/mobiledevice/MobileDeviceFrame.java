package dpnm.mobiledevice;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import dpnm.tool.Env;
import dpnm.tool.MobileNodeComponent;
import dpnm.tool.NetworkComponent;
import dpnm.tool.data.NetworkDeviceInfo;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import dpnm.mobiledevice.policy.ruleobjects.*;
import dpnm.network.device.NetworkDevice;

class MobileDeviceFrame extends JFrame implements MouseListener, ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static final String PHONE_IMG_STR = "phone.png";
	

	private MobileApplication applications[] = null;
	private NetworkInterface networkInterfaces[] = null;
	private StateManager stateManager = null;
	private PolicyManager policyManager = null;
	
	/*
	 * GUI members
	 */
	private static final int FRAME_WIDTH=640;
	private static final int FRAME_HEIGHT=480;
	
	//	status bar
	JTextField  status			= null;
	
	JPanel 		contentPane 	= null;
	JTabbedPane mainPane 		= null;
	JPanel 		networkPane 	= null;
	JPanel 		appPane 		= null;
	
	JButton		powerBtn		= null;
	
	JRadioButton appBtn[]		= null;
	ButtonGroup appGrp			= null;
	
	JRadioButton networkBtn[]	= null;
	ButtonGroup networkGrp		= null;
	
	JTextArea log 				= null;
	
	//	policy
	private JList policyList 					= null;
	private DefaultListModel policyListModel 	= null;
	private JPopupMenu popupMenu = null;
	private Rule selectedRule	= null;
	
	private JPanel auhoMainPane = null;
	private JPanel basicPrefPane = null;
	private JPanel advancedPrefPane = null;
	
	MobileDeviceFrame(String id, 
			MobileApplication apps[], 
			NetworkInterface nis[], 
			StateManager stateManager,
			PolicyManager policyManager) {
		super.setTitle("Mobile Device ("+id+")");
		this.stateManager = stateManager;
		this.policyManager = policyManager;
		applications = apps;
		networkInterfaces = nis;
		
		setIconImage(getToolkit().getImage("res/phone.png"));
	
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width/2) - (FRAME_WIDTH/2),
				(screenSize.height/2) - (FRAME_HEIGHT/2),
				FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		
		//	create content panel
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
//		contentPane.add(createPowerPane(), BorderLayout.NORTH);
		contentPane.add(createStatusPane(), BorderLayout.NORTH);
		contentPane.add(createMainPane(), BorderLayout.CENTER);
		setContentPane(contentPane);
		createPopupMenu();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
    		}
    	);
		
		updatePolicy();	}
	
	protected JPanel createStatusPane() {
		JPanel statusPane = new JPanel();

		status = new JTextField(20);
		status.setText("Connected Network: -(-)");
		status.setEnabled(false);
		
		statusPane.add(status);
		return statusPane;
	}
	
	protected JTabbedPane createMainPane() {
		mainPane = new JTabbedPane();
		
	       //	add network info view
        JPanel  appPane = new JPanel();
        appPane.setLayout(new GridLayout(3,1));
        appPane.add(createAppPane());
	  	appPane.add(createNetworkPane());
        log = new JTextArea();
        log.setEditable(false);
        appPane.add(new JScrollPane(log));

        mainPane.addTab("App & Network", appPane);
        
        JPanel policyPane = new JPanel();
        mainPane.addTab("Policy", policyPane);
        
        policyPane.setLayout(new BorderLayout());
        policyListModel = new DefaultListModel();
        policyList = new JList(policyListModel);
        policyList.addMouseListener(this);
        policyPane.add(new JScrollPane(policyList), BorderLayout.CENTER);
	  	
        JPanel bPane = new JPanel();
        JButton btn = new JButton("Add Policy");
        btn.setActionCommand("AddPolicy");
        btn.addActionListener(this);
        bPane.add(btn);
        policyPane.add(bPane, BorderLayout.SOUTH);
        return mainPane;
	}
	
	protected JPanel createAppPane() {
		appPane = new JPanel();
		appPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		((FlowLayout)appPane.getLayout()).setAlignment(FlowLayout.LEFT);
		((FlowLayout)appPane.getLayout()).setHgap(20);
		((FlowLayout)appPane.getLayout()).setVgap(5);
		appPane.add(new JLabel("      Application      "));
		
		if (applications != null) {
    		appGrp = new ButtonGroup();
    		appBtn = new JRadioButton[applications.length + 1];
    		for (int i = 0; i < applications.length+1; i++) {
    			if (i == 0) {	//	NONE
    				appBtn[i] = new JRadioButton("NONE");
    				appBtn[i].setActionCommand("AppNone");
    				appBtn[i].setSelected(true);
    			} else {
        			appBtn[i] = new JRadioButton(applications[i-1].getName());
        			appBtn[i].setActionCommand(applications[i-1].getName());
    			}
    			appBtn[i].addActionListener(stateManager);
    			appGrp.add(appBtn[i]);
    			appPane.add(appBtn[i]);
    		}
        	for (int i = 0; i < applications.length; i++) {
        		if (applications[i].isRunning()) {
        			appBtn[i+1].setSelected(true);
        			break;
        		}
        	}
 
		}
		return appPane;	
	}
	
	protected JPanel createNetworkPane() {
		networkPane = new JPanel();
		networkPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		((FlowLayout)networkPane.getLayout()).setAlignment(FlowLayout.LEFT);
		((FlowLayout)networkPane.getLayout()).setHgap(20);
		((FlowLayout)networkPane.getLayout()).setVgap(5);
		networkPane.add(new JLabel("      Network Interface      "));
    	
    	if (networkInterfaces != null) {
        	networkGrp = new ButtonGroup();
        	networkBtn = new JRadioButton[networkInterfaces.length+1];
        	for (int i = 0; i < networkInterfaces.length+1; i++) {
        		if (i == 0) {
            		networkBtn[i] = new JRadioButton("NONE");
    				networkBtn[i].setActionCommand("NetworkInterfaceNone");
    				networkBtn[i].setSelected(true);
        		} else {
            		networkBtn[i] = new JRadioButton(networkInterfaces[i-1].getName());
            		networkBtn[i].setActionCommand(networkInterfaces[i-1].getName());
        		}
        		networkBtn[i].addActionListener(stateManager);
        		networkBtn[i].setEnabled(false);
        		networkGrp.add(networkBtn[i]);
        		networkPane.add(networkBtn[i]);
        	}
        	for (int i = 0; i < networkInterfaces.length; i++) {
        		if (networkInterfaces[i].isSelected()) {
        			networkBtn[i+1].setSelected(true);
        			break;
        		}
        	}
    	}
		return networkPane;
	}
	
	public void updatePolicy() {
		if (!policyListModel.isEmpty()) {
			policyListModel.removeAllElements();
		}
		ArrayList<Rule> rules = policyManager.getPolicies();
		
		if (rules == null || rules.size() == 0) {
			policyListModel.addElement("No Policy");
		} else {
			for (int i = 0; i < rules.size(); i++) {
				policyListModel.addElement(rules.get(i));
			}
		}
	}
	
    private void createPopupMenu() {
    	popupMenu = new JPopupMenu();

    	JMenuItem item = new JMenuItem("Modify");
    	item.setActionCommand("ModifyPolicy");
    	item.addActionListener(this);
    	
    	popupMenu.add(item);
    	
       	item = new JMenuItem("Delete");
    	item.setActionCommand("DeletePolicy");
    	item.addActionListener(this);
    	
    	popupMenu.add(item);
   	}
    
	void setApplications(MobileApplication apps[]) {
		this.applications = apps;
		if (appGrp != null) {
			appGrp = null;
		}
		if (appBtn != null) {
			for (int i = 0; i < appBtn.length; i++) {
				appPane.remove(appBtn[i]);
			}
			appBtn = null;
		}
	
		if (applications != null) {
    		appGrp = new ButtonGroup();
    		appBtn = new JRadioButton[applications.length + 1];
    		for (int i = 0; i < applications.length+1; i++) {
    			if (i == 0) {	//	NONE
    				appBtn[i] = new JRadioButton("NONE");
    			} else {
        			appBtn[i] = new JRadioButton(applications[i-1].getName());
    			}
    			appBtn[i].addActionListener(stateManager);
    			appGrp.add(appBtn[i]);
    			appPane.add(appBtn[i]);
    		}
		}	}
	
	void setNetworkInterfaces(NetworkInterface nis[]) {
		this.networkInterfaces = nis;
		if (networkGrp != null) {
			networkGrp = null;
		}
		if (networkBtn != null) {
			for (int i = 0; i < networkBtn.length; i++) {
				networkPane.remove(networkBtn[i]);
			}
			networkBtn = null;
		}
    	if (networkInterfaces != null) {
        	networkGrp = new ButtonGroup();
        	networkBtn = new JRadioButton[networkInterfaces.length+1];
        	for (int i = 0; i < networkInterfaces.length+1; i++) {
        		if (i == 0) {
            		networkBtn[i] = new JRadioButton("NONE");
        		} else {
            		networkBtn[i] = new JRadioButton(networkInterfaces[i-1].getName());
        		}
        		networkBtn[i].addActionListener(stateManager);
        		networkGrp.add(networkBtn[i]);
        		networkPane.add(networkBtn[i]);
        	}
    	}
	}
	
	void updateApplication() {
		boolean isApplication = false;
		for (int i = 0; applications != null && i < applications.length; i++) {
			if (applications[i].isRunning()) {
				appBtn[i+1].setSelected(true);
				isApplication = true;
			} else {
				appBtn[i+1].setSelected(false);
			}
		}
		appBtn[0].setSelected(!isApplication);
		appPane.repaint();
	}
	
	void updateNetworkInterface() {
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			if (networkInterfaces[i].isEnabled() && networkInterfaces[i].isSelected()) {
				networkBtn[i+1].setSelected(true);
				status.setText("Connected Network: "+networkInterfaces[i].getName()+"("+((NetworkDevice)networkInterfaces[i].getSelectedDevice()).getName()+")");
				networkPane.repaint();
				return;
			}
		}
		status.setText("Connected Network: -(-)");
		networkBtn[0].setSelected(true);
		networkPane.repaint();
		//		boolean isNI = false;
//		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
//			if (networkInterfaces[i].isEnabled()) {
//				networkBtn[i+1].setSelected(true);
//				isNI = true;
//			} else {
//				appBtn[i+1].setSelected(false);
//			}
//		}
//		appBtn[0].setSelected(!isNI);
//		
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

	public void mousePressed(MouseEvent e) {

		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			//	at the view
			if (e.getSource() == policyList) {
				if (policyManager.getPolicies() == null || policyManager.getPolicies().size() == 0) {
					return;
				}
				int index = policyList.getSelectedIndex();
				if (index != -1) {
					selectedRule = (Rule)policyList.getSelectedValue();
                    popupMenu.show(policyList, e.getX(), e.getY()); 
                    return;
				}
			} 
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		
		if (cmd.intern() == "AddPolicy") {
			addPolicy();
		} else if (cmd.intern() == "ModifyPolicy") {
			modifyPolicy();
		} else if (cmd.intern() == "DeletePolicy") {
			if (selectedRule != null) {
				policyManager.deletePolicy(selectedRule.getName());
				selectedRule = null;
				updatePolicy();
				policyList.repaint();
			}
//		} else if (cmd.intern() == "AUHO") {
//			if (auhoMainPane != null) {
//				auhoMainPane.setVisible(true);
//			}
//		} else if (cmd.intern() == "RANDOM" || cmd.intern() == "RSS" || cmd.intern() == "COST" || cmd.intern() == "QUALITY") {
//			if (auhoMainPane != null) {
//				auhoMainPane.setVisible(false);
//			}
//		} else if (cmd.intern() == "Basic Mode") {
//			if (advancedPrefPane != null) {
//				advancedPrefPane.setVisible(false);
//			}
//			if (basicPrefPane != null) {
//				basicPrefPane.setVisible(true);
//			}
//		} else if (cmd.intern() == "Advanced Mode") {
//			if (basicPrefPane != null) {
//				basicPrefPane.setVisible(false);
//			}
//			if (advancedPrefPane != null) {
//				advancedPrefPane.setVisible(true);
//			}
		}
		
	}
	
	private void addPolicy() {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("Name");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField name = new JTextField(20);
        name.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(name,c);

        p.add(name);

        label = new JLabel("Event");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String eventTypes[] = dpnm.mobiledevice.policy.ruleobjects.Event.EVENT_STR;
        JComboBox eventCombo = new JComboBox(eventTypes);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(eventCombo,c);

        p.add(eventCombo);

        label = new JLabel("Condition");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        label = new JLabel("TBD");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(label,c);
        p.add(label);

        label = new JLabel("Action (Decision Algorithm)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);

        JPanel decisionPane = new JPanel();

		String decisionAlgorithms[] = dpnm.mobiledevice.policy.ruleobjects.Action.DECISION_ALGORITHM_STR;
		ButtonGroup decisionGrp = new ButtonGroup();
   		JRadioButton[] decisionBtn = new JRadioButton[decisionAlgorithms.length];
   		for (int i = 0; i < decisionAlgorithms.length; i++) {
   			decisionBtn[i] = new JRadioButton(decisionAlgorithms[i]);
   			decisionBtn[i].addActionListener(this);
   			decisionBtn[i].setActionCommand(decisionAlgorithms[i]);
   			decisionGrp.add(decisionBtn[i]);
   			decisionPane.add(decisionBtn[i]);
		}

   		
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(decisionPane,c);
   		p.add(decisionPane);

   		auhoMainPane = new JPanel();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
   		auhoMainPane.setLayout(gridbag2);
   		
   		c.fill = GridBagConstraints.HORIZONTAL;
   		gridbag.setConstraints(auhoMainPane, c);
   		p.add(auhoMainPane);
   		
   		label = new JLabel("AUHO");
        c2.gridwidth = GridBagConstraints.RELATIVE;
        gridbag2.setConstraints(label,c2);
        auhoMainPane.add(label);

        JPanel prefPane = new JPanel();

		String auhoPref[] = {"Basic Mode", "Advanced Mode"};
		ButtonGroup auhoGrp = new ButtonGroup();
   		JRadioButton[] auhoBtn = new JRadioButton[auhoPref.length];
   		for (int i = 0; i < auhoPref.length; i++) {
   			auhoBtn[i] = new JRadioButton(auhoPref[i]);
   			auhoBtn[i].addActionListener(this);
   			auhoBtn[i].setActionCommand(auhoPref[i]);
   			auhoGrp.add(auhoBtn[i]);
   			prefPane.add(auhoBtn[i]);
		}
        c2.gridwidth = GridBagConstraints.REMAINDER;
        gridbag2.setConstraints(prefPane,c2);
   		auhoMainPane.add(prefPane);

        basicPrefPane = new JPanel();

   		JCheckBox[] prefBtn = new JCheckBox[decisionAlgorithms.length-2];
   		for (int i = 1; i < decisionAlgorithms.length-1; i++) {
   			prefBtn[i-1] = new JCheckBox(decisionAlgorithms[i]);
   			basicPrefPane.add(prefBtn[i-1]);
		}
        c2.fill = GridBagConstraints.HORIZONTAL;
        gridbag2.setConstraints(basicPrefPane,c2);
   		auhoMainPane.add(basicPrefPane);

        advancedPrefPane = new JPanel();

   		JTextField[] prefField = new JTextField[decisionAlgorithms.length-2];
   		for (int i = 1; i < decisionAlgorithms.length-1; i++) {
   			JLabel l = new JLabel(decisionAlgorithms[i]+": ");
   			advancedPrefPane.add(l);
   			prefField[i-1] = new JTextField(3);
   			prefField[i-1].setText("0.0");
   			advancedPrefPane.add(prefField[i-1]);
		}
        c2.fill = GridBagConstraints.HORIZONTAL;
        gridbag2.setConstraints(advancedPrefPane,c2);
   		auhoMainPane.add(advancedPrefPane);
   		
   		
        message[0] = "Add New Policy";
        message[1] = p;
        
        int result;

        
        result = JOptionPane.showOptionDialog(null, message, "Add New Policy",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((name.getText() == null || name.getText().intern() == "".intern()) &&
            (name.getText() == null || name.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert policy name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dpnm.mobiledevice.policy.ruleobjects.Action action = 
            	new dpnm.mobiledevice.policy.ruleobjects.Action();
            
            //	Set decision algorithm
            for (int i = 0; i < decisionAlgorithms.length;i++) {
            	if (decisionBtn[i].isSelected()) {
            		action.setDecisionAlgorithm(i);
            		break;
            	}
            }
            
//            if (action.getDecisionAlgorithm() == dpnm.mobiledevice.policy.ruleobjects.Action.AUHO) {
            	if (auhoBtn[0].isSelected()) {	// Basic mode
            		UserPreference pref = new UserPreference();
            		if (prefBtn[0].isSelected()) pref.setPreference(UserPreference.RSS);
            		if (prefBtn[1].isSelected()) pref.setPreference(UserPreference.COST);
            		if (prefBtn[2].isSelected()) pref.setPreference(UserPreference.QUALITY);
            		if (prefBtn[3].isSelected()) pref.setPreference(UserPreference.LIFETIME);
            		
            		UserProfile up = new UserProfile();
            		up.setUserPreference(pref);
            		action.setUserProfile(up);
            	} else {	// advanced mode
            		UserProfile up = new UserProfile(
            				Double.parseDouble(prefField[0].getText()),
            				Double.parseDouble(prefField[1].getText()),
            				Double.parseDouble(prefField[2].getText()),
            				Double.parseDouble(prefField[3].getText()));
            		action.setUserProfile(up);
            	}
//            }
            
            policyManager.addPolicy(name.getText(), eventCombo.getSelectedIndex(), null, action);
        }		
        updatePolicy();
        policyList.repaint();
	}
	
	private void modifyPolicy() {
		if (selectedRule == null) {
			return;
		}
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("Name");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField name = new JTextField(20);
        name.setText(selectedRule.getName());
        name.setEnabled(false);
        name.setEditable(false);
        name.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(name,c);

        p.add(name);

        label = new JLabel("Event");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String eventTypes[] = dpnm.mobiledevice.policy.ruleobjects.Event.EVENT_STR;
        JComboBox eventCombo = new JComboBox(eventTypes);
        eventCombo.setSelectedIndex(selectedRule.getEvent());
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(eventCombo,c);

        p.add(eventCombo);

        label = new JLabel("Condition");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        label = new JLabel("TBD");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(label,c);
        p.add(label);

        label = new JLabel("Action (Decision Algorithm)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);

        JPanel decisionPane = new JPanel();

		String decisionAlgorithms[] = dpnm.mobiledevice.policy.ruleobjects.Action.DECISION_ALGORITHM_STR;
		ButtonGroup decisionGrp = new ButtonGroup();
   		JRadioButton[] decisionBtn = new JRadioButton[decisionAlgorithms.length];
   		for (int i = 0; i < decisionAlgorithms.length; i++) {
   			decisionBtn[i] = new JRadioButton(decisionAlgorithms[i]);
   			if (selectedRule.getAction().getDecisionAlgorithm() == i) {
   				decisionBtn[i].setSelected(true);
   			}
   			decisionBtn[i].addActionListener(this);
   			decisionBtn[i].setActionCommand(decisionAlgorithms[i]);
   			decisionGrp.add(decisionBtn[i]);
   			decisionPane.add(decisionBtn[i]);
		}

   		
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(decisionPane,c);
   		p.add(decisionPane);

   		auhoMainPane = new JPanel();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
   		auhoMainPane.setLayout(gridbag2);
   		
   		c.fill = GridBagConstraints.HORIZONTAL;
   		gridbag.setConstraints(auhoMainPane, c);
   		p.add(auhoMainPane);
   		
   		label = new JLabel("AUHO");
        c2.gridwidth = GridBagConstraints.RELATIVE;
        gridbag2.setConstraints(label,c2);
        auhoMainPane.add(label);

        JPanel prefPane = new JPanel();

		String auhoPref[] = {"Basic Mode", "Advanced Mode"};
		ButtonGroup auhoGrp = new ButtonGroup();
   		JRadioButton[] auhoBtn = new JRadioButton[auhoPref.length];
   		for (int i = 0; i < auhoPref.length; i++) {
   			auhoBtn[i] = new JRadioButton(auhoPref[i]);
   			auhoBtn[i].addActionListener(this);
   			auhoBtn[i].setActionCommand(auhoPref[i]);
   			auhoGrp.add(auhoBtn[i]);
   			prefPane.add(auhoBtn[i]);
		}
        c2.gridwidth = GridBagConstraints.REMAINDER;
        gridbag2.setConstraints(prefPane,c2);
   		auhoMainPane.add(prefPane);

        basicPrefPane = new JPanel();

   		JCheckBox[] prefBtn = new JCheckBox[decisionAlgorithms.length-2];
   		for (int i = 1; i < decisionAlgorithms.length-1; i++) {
   			prefBtn[i-1] = new JCheckBox(decisionAlgorithms[i]);
   			basicPrefPane.add(prefBtn[i-1]);
		}
        c2.fill = GridBagConstraints.HORIZONTAL;
        gridbag2.setConstraints(basicPrefPane,c2);
   		auhoMainPane.add(basicPrefPane);

        advancedPrefPane = new JPanel();

   		JTextField[] prefField = new JTextField[decisionAlgorithms.length-2];
   		for (int i = 1; i < decisionAlgorithms.length-1; i++) {
   			JLabel l = new JLabel(decisionAlgorithms[i]+": ");
   			advancedPrefPane.add(l);
   			prefField[i-1] = new JTextField(3);
   			prefField[i-1].setText("0.0");
   			advancedPrefPane.add(prefField[i-1]);
		}
        c2.fill = GridBagConstraints.HORIZONTAL;
        gridbag2.setConstraints(advancedPrefPane,c2);
   		auhoMainPane.add(advancedPrefPane);
   		
//   		if (selectedRule.getAction().getDecisionAlgorithm() == dpnm.mobiledevice.policy.ruleobjects.Action.AUHO) {
   		if (selectedRule.getAction().getUserProfile() != null) {
   			if (selectedRule.getAction().getUserProfile().getUserPreference() != null) {
   				auhoBtn[0].setSelected(true);
   				UserPreference pref = selectedRule.getAction().getUserProfile().getUserPreference();
   				prefBtn[0].setSelected(pref.isOn(UserPreference.RSS));
   				prefBtn[1].setSelected(pref.isOn(UserPreference.COST));
   				prefBtn[2].setSelected(pref.isOn(UserPreference.QUALITY));
   				prefBtn[3].setSelected(pref.isOn(UserPreference.LIFETIME));
   			} else {
   				auhoBtn[1].setSelected(true);
	   			prefField[0].setText(String.valueOf(selectedRule.getAction().getUserProfile().getRss()));
	   			prefField[1].setText(String.valueOf(selectedRule.getAction().getUserProfile().getCost()));
	   			prefField[2].setText(String.valueOf(selectedRule.getAction().getUserProfile().getQuality()));
	   			prefField[3].setText(String.valueOf(selectedRule.getAction().getUserProfile().getLifetime()));
   			}
   		}
//   		}

   		

        message[0] = "Modify Policy ("+selectedRule.getName()+")";
        message[1] = p;
        
        int result;

        
        result = JOptionPane.showOptionDialog(null, message, "Modify Policy (" + selectedRule.getName()+")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((name.getText() == null || name.getText().intern() == "".intern()) &&
            (name.getText() == null || name.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert policy name",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dpnm.mobiledevice.policy.ruleobjects.Action action = 
            	new dpnm.mobiledevice.policy.ruleobjects.Action();
            
            //	Set decision algorithm
            for (int i = 0; i < decisionAlgorithms.length;i++) {
            	if (decisionBtn[i].isSelected()) {
            		action.setDecisionAlgorithm(i);
            		break;
            	}
            }
            
//            if (action.getDecisionAlgorithm() == dpnm.mobiledevice.policy.ruleobjects.Action.AUHO) {
            	if (auhoBtn[0].isSelected()) {	// Basic mode
            		UserPreference pref = new UserPreference();
            		if (prefBtn[0].isSelected()) pref.setPreference(UserPreference.RSS);
            		if (prefBtn[1].isSelected()) pref.setPreference(UserPreference.COST);
            		if (prefBtn[2].isSelected()) pref.setPreference(UserPreference.QUALITY);
            		if (prefBtn[3].isSelected()) pref.setPreference(UserPreference.LIFETIME);
            		
            		UserProfile up = new UserProfile();
            		up.setUserPreference(pref);
            		action.setUserProfile(up);
            	} else {	// advanced mode
            		UserProfile up = new UserProfile(
            				Double.parseDouble(prefField[0].getText()),
            				Double.parseDouble(prefField[1].getText()),
            				Double.parseDouble(prefField[2].getText()),
            				Double.parseDouble(prefField[3].getText()));
            		action.setUserProfile(up);
            	}
//            }
            
            policyManager.modifyPolicy(name.getText(), eventCombo.getSelectedIndex(), null, action);
        }		
        updatePolicy();
        policyList.repaint();
	}
}
