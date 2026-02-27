package dpnm.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import dpnm.mobilenode.MobileNodeFactory;
import dpnm.network.NetworkFactory;
import dpnm.tool.comp.DesComponent;
import dpnm.tool.comp.HoverButton;
import dpnm.tool.comp.StatusBar;
import dpnm.tool.data.*;

public class PathEditor extends JDialog 
	implements ActionListener, MouseListener, MouseMotionListener, ChangeListener {
	private static final int P_SIZE = 6;
	private static final int SELECT = 0x00;
	private static final int MOVE = 0x01;
	private static final int ADDPOINT = 0x02;
	
	private int state = SELECT;
	
	private NetworkMobileNodeInfo nodeInfo = null;
	
	private HMNEmulatorGUI owner = null;
	private Vector<NetworkComponent> networks;
	private MobileNodeComponent mnodeComponent;
	
	private JPopupMenu popupMenu = null;
	private JMenuItem deleteMenu = null;
	private JMenuItem propertyEditorMenu = null;
	
	/*	View */
	private JPanel view = null;
	private StatusBar statusBar = new StatusBar(false);
	
	private Vector<NodePoint> points;
	private NodePoint selectedPoint = null;
	
	/*	Velocity */
	private JTextField velocityFld[] = null;
    private JSlider velocity[] = null;
	private JTextField velocityFld_ = null;
	private JTextField stayFld = null;
	
	private DesComponent desComp = new DesComponent();
	
	/*	Select network */
	private JCheckBox networkCheckBox[] = null;
	private JToggleButton networkBtn = null;
	
	   /* toolbar button */
	private static final String toolbarBtnStr[][] = {
    			{"Save", "Save", "Save"},
    			{"Reset", "Reset", "Reset"},
    			{"Select", "Select", "Select"},
    			{"Add Point", "AddPoint", "AddPoint"},
    			{"Set All Properties", "SetAllProperties", "SetAllProperties"},
    			/*
    			{"Show Network", "ShowNetwork", "ShowNetwork"},
    			{"Hide Network", "HideNetwork", "HideNetwork"}
    			*/
	};

	public PathEditor(
			HMNEmulatorGUI owner,
			NetworkMapInfo mapInfo, NetworkMobileNodeInfo nodeInfo, 
			String filename) {
		super(owner, false);
		this.owner = owner;
		this.nodeInfo = nodeInfo;
		setTitle("PathEditor <"+nodeInfo.getDevice().getId()+">");
		
		setIconImage(getToolkit().getImage(HMNEmulatorGUI.rf.getURL(Resources.LOGO_IMG_STR)));
		
		networks = new Vector<NetworkComponent>();
		points = new Vector<NodePoint>();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(Env.PLAYER_WIDTH, Env.PLAYER_HEIGHT);
		setBounds((screenSize.width/2) - (Env.PLAYER_WIDTH/2),
				(screenSize.height/2) - (Env.PLAYER_HEIGHT/2),
				Env.PLAYER_WIDTH, Env.PLAYER_HEIGHT);
		
		createUI();
		createView();
		
		loadNetworkMapInfo(mapInfo, nodeInfo);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				save();
			}
    		}
    	);
		
		statusBar.setSimState("");
	}
	
	private void createUI() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		contentPane.add(statusBar, BorderLayout.SOUTH);
		contentPane.add(createComponentToolBar(), BorderLayout.WEST);
		setContentPane(contentPane);
		createPopupMenu();
	}
	
    private JToolBar createToolBar()
    {
        JToolBar toolBar = new JToolBar() ;
        for (int j = 0; j < toolbarBtnStr.length; j++) {
            JButton button = new HoverButton(toolbarBtnStr[j][0]);
            button.setActionCommand(toolbarBtnStr[j][1]);
            button.setToolTipText(toolbarBtnStr[j][2]);
            button.addActionListener(this);
            toolBar.add(button);
        }
       
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
    	return toolBar;
    	
    }
    private void createPopupMenu() {
    	popupMenu = new JPopupMenu();
    	
       	deleteMenu = new JMenuItem("Delete");
    	deleteMenu.setActionCommand("Delete");
    	deleteMenu.addActionListener(this);
    	popupMenu.add(deleteMenu);
    	
    	propertyEditorMenu = new JMenuItem("Edit Property");
    	propertyEditorMenu.setActionCommand("EditProperty");
    	propertyEditorMenu.addActionListener(this);
    	popupMenu.add(propertyEditorMenu);
    }
   
	private void createView() {
    	view = new PointView();
		view.setBackground(Color.white);
		view.setLayout(null);

        view.setPreferredSize(new Dimension(Env.VIEW_WIDTH, Env.VIEW_HEIGHT));
        JScrollPane js = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        getContentPane().add(js, BorderLayout.CENTER);
	}
	
	void loadNetworkMapInfo(NetworkMapInfo mapInfo, NetworkMobileNodeInfo nodeInfo) {
		if (mapInfo == null) {
			return;
		}
        view.setPreferredSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
       
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            JLabel label = new JLabel();
            label.setLocation(0,0);
            label.setIcon(HMNEmulatorGUI.getResizedImage(
						Resources.MAP_DIR+mapInfo.getBackground(),
						mapInfo.getWidth(), mapInfo.getHeight()));
            label.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
            view.add(label,0);
    	}
    	NetworkDeviceInfo[] info = mapInfo.getDevices();
    	if (info != null) {
        	for (int i = 0; i < info.length; i++) {
        		NetworkComponent comp = new NetworkComponent();
        		comp.setDeviceInfo(info[i]);
                comp.setIcon(owner.deviceImg[info[i].getIcon()], info[i].getIcon());
        		view.add(comp,0);
                networks.add(comp);
        	}
    	}
   	
		mnodeComponent = new MobileNodeComponent();
		mnodeComponent.setNetworkMobileNodeInfo(nodeInfo);
        mnodeComponent.setIcon(nodeInfo.getMobileNode().getIcon());
		view.add(mnodeComponent,0);
		
		//	add or load current points
		int path[][] = nodeInfo.getPath();
		for (int i = 0; i < path.length; i++) {
    		NodePoint p = new NodePoint(
    			path[i][NetworkMobileNodeInfo.PATH_XPOS],
    			path[i][NetworkMobileNodeInfo.PATH_YPOS],
    			path[i][NetworkMobileNodeInfo.PATH_VELOCITY],
    			path[i][NetworkMobileNodeInfo.PATH_STAY]);
			points.add(p);
			view.add(p, 0);
		}
		addDesComponent();
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
	
	private void setDesComp(int x, int y, String text, Color background, boolean isVisible) {
        desComp.setLocation(x,y);
        desComp.setText(text);
        desComp.setBackground(background);
        desComp.setVisible(isVisible);
    }
	
	public void actionPerformed(ActionEvent e) {
		String menuItemString = e.getActionCommand();
		
		if (menuItemString.intern() == "Save") {
			save();
    	} else if (menuItemString.intern() == "Reset") {
			NodePoint p = points.elementAt(0);
			for (int i = 0; i < points.size(); i++) {
				view.remove(points.elementAt(i));
			}
			points.removeAllElements();
			points.addElement(p);
			view.add(p);
			repaint();
		} else if (menuItemString.intern() == "Select") {
			setState(SELECT);
		} else if (menuItemString.intern() == "Delete") {
			points.removeElement(selectedPoint);
			view.remove(selectedPoint);
			selectedPoint = null;
			repaint();
		} else if (menuItemString.intern() == "EditProperty") {
			editProperty();
		} else if (menuItemString.intern() == "AddPoint") {
			setState(ADDPOINT);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if (menuItemString.intern() == "ShowNetwork") {
			for (int i = 0; i < networks.size(); i++) {
				networks.elementAt(i).setVisible(true);
			}
    		repaint();
		} else if (menuItemString.intern() == "HideNetwork") {
			for (int i = 0; i < networks.size(); i++) {
				networks.elementAt(i).setVisible(false);
			}
    		repaint();
		} else if (menuItemString.intern() == "SetAllProperties") {
			setAllProperties();
		}
	
	}
	
	private void editProperty() {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

       
        JLabel label = new JLabel("Velocity");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
        
        velocityFld_ = new JTextField(4);
        velocityFld_.setEditable(false);
        velocityFld_.setText(String.valueOf(selectedPoint.getVelocity()));
        velocityFld_.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(velocityFld_,c);
        p.add(velocityFld_);
        
        JSlider velocity = new JSlider(JSlider.HORIZONTAL, 
        		nodeInfo.getMobileNode().getMinVelocity(), 
        		nodeInfo.getMobileNode().getMaxVelocity(), 
        		selectedPoint.getVelocity());
        velocity.setMajorTickSpacing(5);
        velocity.setMinorTickSpacing(1);
        velocity.setPaintTicks(true);
        velocity.setPaintLabels(true);
        velocity.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
        	    if (!source.getValueIsAdjusting()) {
        	        velocityFld_.setText(String.valueOf((int)source.getValue()));
        	    }
        	}
        });
       
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(velocity,c);
        p.add(velocity);
        
        label = new JLabel("Stay Time");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
        
        stayFld = new JTextField(10);
        stayFld.setText(String.valueOf(selectedPoint.getStay()));
        stayFld.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(stayFld,c);
        p.add(stayFld);

        message[0] = "Edit Property of Point ("+selectedPoint.getXpos()+","+selectedPoint.getYpos()+")";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Edit Velocity",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
            if ((velocityFld_.getText() == null || velocityFld_.getText().intern() == "".intern()) &&
            (velocityFld_.getText() == null || velocityFld_.getText().intern() == "".intern())) {
                JOptionPane.showMessageDialog(this, "Please, insert velocity",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setState(SELECT);
                return;
            }
            selectedPoint.setVelocity(Integer.parseInt(velocityFld_.getText()));
            selectedPoint.setStay(Integer.parseInt(stayFld.getText()));
        }		
        setState(SELECT);
        repaint();
		
	}
	
	private void setAllProperties() {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);
        
        JLabel label = new JLabel ("Point");
        c.weightx = 1.0;
        c.gridwidth = 1;
        gridbag.setConstraints(label,c);
        p.add(label);
        
        label = new JLabel ("Stay Time");
        gridbag.setConstraints(label,c);
        p.add(label);
        
        label = new JLabel ("Velocity");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(label,c);
        p.add(label);
 
        velocityFld = new JTextField[points.size()];
        velocity = new JSlider[points.size()];
        JTextField stayField[] = new JTextField[points.size()];
        for (int i = 0; i < points.size(); i++) {
            label = new JLabel(i + ":("+
            		points.elementAt(i).getXpos() + "," +
            		points.elementAt(i).getYpos()+")");
            c.weightx = 1.0;
            c.gridwidth = 1;
            gridbag.setConstraints(label,c);
            p.add(label);
            
            stayField[i] = new JTextField(10);
            stayField[i].setText(String.valueOf(points.elementAt(i).getStay()));
            stayField[i].setForeground(Color.blue);
            gridbag.setConstraints(stayField[i],c);
            p.add(stayField[i]);
 
            label = new JLabel(" (ms) ");
            gridbag.setConstraints(label,c);
            p.add(label);
 
            velocityFld[i] = new JTextField(4);
//            velocityFld[i].setEditable(false);
            velocityFld[i].setText(String.valueOf(points.elementAt(i).getVelocity()));
            velocityFld[i].setForeground(Color.blue);
            velocityFld[i].addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            	    JTextField source = (JTextField)e.getSource();
            	    try {
        	    		int value = Integer.parseInt(source.getText());
        	    		
        	    		if ( value < nodeInfo.getMobileNode().getMinVelocity() || 
        	    				value > nodeInfo.getMobileNode().getMaxVelocity()) {
                             JOptionPane.showMessageDialog(null, 
                            		 "Value must be from " + 
                            		 nodeInfo.getMobileNode().getMinVelocity() + " to " + 
        	    				nodeInfo.getMobileNode().getMaxVelocity(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                     	    for (int j = 0; velocityFld != null && j < velocity.length; j++) {
                    	    	if (source == velocityFld[j]) {
                    	    		velocityFld[j].setText(String.valueOf(velocity[j].getValue()));
                    	    		return;
                    	    	}
                     	    }
        	    		}
                	    for (int j = 0; velocityFld != null && j < velocity.length; j++) {
                	    	if (source == velocityFld[j]) {
                	    		System.out.println(velocityFld[j].getText());
                	    		velocity[j].setValue(Integer.parseInt(velocityFld[j].getText()));
                	    	}
                    	}
            	    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please, input number format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                  	    for (int j = 0; velocityFld != null && j < velocity.length; j++) {
                	    	if (source == velocityFld[j]) {
                	    		velocityFld[j].setText(String.valueOf(velocity[j].getValue()));
                	    		return;
                	    	}
                 	    }
                        return;
            	    }            	}
            });
            gridbag.setConstraints(velocityFld[i],c);
            p.add(velocityFld[i]);
            
            velocity[i] = new JSlider(JSlider.HORIZONTAL, 
            		nodeInfo.getMobileNode().getMinVelocity(), 
            		nodeInfo.getMobileNode().getMaxVelocity(), 
            		points.elementAt(i).getVelocity());
            velocity[i].setMajorTickSpacing(5);
            velocity[i].setMinorTickSpacing(1);
            velocity[i].setPaintTicks(true);
            velocity[i].setPaintLabels(true);
            velocity[i].addChangeListener(this);
           
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(velocity[i],c);
            p.add(velocity[i]);
        }
        
        p.setSize(p.getWidth(), 768);
        
        message[0] = "Edit Properties of Mobile Node ("+nodeInfo.getDevice().getId()+")";
        message[1] = new JScrollPane(p);
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Edit Properties",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
        	for (int i = 0; i < points.size(); i++) {
        		points.elementAt(i).setStay(Integer.parseInt(stayField[i].getText()));
        	}
        }		
        setState(SELECT);
        repaint();
		
	}
	
	private void setAllVelocity() {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        velocityFld = new JTextField[points.size()];
        velocity = new JSlider[points.size()];
        for (int i = 0; i < points.size(); i++) {
            JLabel label = new JLabel(i + ":("+
            		points.elementAt(i).getXpos() + "," +
            		points.elementAt(i).getYpos()+")");
            c.weightx = 1.0;
            c.gridwidth = 1;
            gridbag.setConstraints(label,c);
            p.add(label);
            
            velocityFld[i] = new JTextField(4);
            velocityFld[i].setEditable(false);
            velocityFld[i].setText(String.valueOf(points.elementAt(i).getVelocity()));
            velocityFld[i].setForeground(Color.blue);
            gridbag.setConstraints(velocityFld[i],c);
            p.add(velocityFld[i]);
            
            velocity[i] = new JSlider(JSlider.HORIZONTAL, 
            		nodeInfo.getMobileNode().getMinVelocity(), 
            		nodeInfo.getMobileNode().getMaxVelocity(), 
            		points.elementAt(i).getVelocity());
            velocity[i].setMajorTickSpacing(1);
            velocity[i].setPaintTicks(true);
            velocity[i].addChangeListener(this);
           
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(velocity[i],c);
            p.add(velocity[i]);
        }

        message[0] = "Edit Velocity of Mobile Node ("+nodeInfo.getDevice().getId()+")";
        message[1] = p;
        
        int result;

        result = JOptionPane.showOptionDialog(null, message, 
        		"Edit Velocity",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if ( result == JOptionPane.OK_OPTION ) {
        	for (int i = 0; i < points.size(); i++) {
        		points.elementAt(i).setVelocity(Integer.parseInt(velocityFld[i].getText()));
        	}
        }		
        setState(SELECT);
        repaint();
		
	}
	
	public void setState(int state) {
		this.state = state;
		statusBar.setState(getStateText(state));
		if (state == SELECT) {
			view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/*
	private boolean load() {
		String filename = (new File(currentFile)).getName();
		filename = filename.substring(0, filename.length()-4);
		try {
			File cFile = new File(Resources.DATA_DIR+File.separator+filename+".npk");
			if (cFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(cFile));
				String line = null;
				while((line = reader.readLine()) != null) {
					//	find device
					if (line.intern() == nodeInfo.getDevice().getId().intern()) {
						//	read points
						line = reader.readLine();
						StringTokenizer st = new StringTokenizer(line, "|");
						while(st.hasMoreElements()) {
							String p = st.nextToken();
    						StringTokenizer st2 = new StringTokenizer(p, ",");
    						NodePoint np = new NodePoint(
    								Integer.parseInt(st2.nextToken()),
    								Integer.parseInt(st2.nextToken()));
    						points.addElement(np);
    						view.add(np,0);
						}
						return true;
					}
				}
				reader.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	private void save() {
		String filename = (new File(currentFile)).getName();
		filename = filename.substring(0, filename.length()-4);
		try {
			File cFile = new File(Resources.DATA_DIR+File.separator+filename+".npk");
			if (cFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(cFile));
				
				String line = null;
				ArrayList<String> str = new ArrayList<String>();
				boolean isFound = false;
				while((line = reader.readLine()) != null) {
					//	find device
					if (line.intern() == nodeInfo.getDevice().getId().intern()) {
						str.add(line);
						line = reader.readLine();
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < points.size(); i++) {
             				sb.append(points.elementAt(i).getXpos()+","+points.elementAt(i).getY());
             				sb.append("|");
						}
						str.add(sb.toString());
						isFound = true;
						continue;
					}
					str.add(line);
				}
				reader.close();
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(cFile));
				for (int i = 0; i < str.size(); i++) {
					writer.write(str.get(i)+"\n");
				}
				if (!isFound) {
    				writer.write(nodeInfo.getDevice().getId()+"\n");
	     			for (int i = 0; i < points.size(); i++) {
         				writer.write(points.elementAt(i).getXpos()+","+points.elementAt(i).getY());
         				writer.write("|");
	     			}
				}
				writer.flush();
				writer.close();
			} else {
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(cFile));
				writer.write(nodeInfo.getDevice().getId()+"\n");
     			for (int i = 0; i < points.size(); i++) {
     				writer.write(points.elementAt(i).getXpos()+","+points.elementAt(i).getY());
     				writer.write("|");
    			}
     			writer.flush();
     			writer.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
	*/
	
	private void save() {
		int p[][] = new int[points.size()][NetworkMobileNodeInfo.PATH_COUNT];
		for (int i = 0; i < points.size(); i++) {
			p[i][NetworkMobileNodeInfo.PATH_XPOS] = points.elementAt(i).getXpos();
			p[i][NetworkMobileNodeInfo.PATH_YPOS] = points.elementAt(i).getYpos();
			p[i][NetworkMobileNodeInfo.PATH_VELOCITY] = points.elementAt(i).getVelocity();
			p[i][NetworkMobileNodeInfo.PATH_STAY] = points.elementAt(i).getStay();
		}
		nodeInfo.setPath(p);
	}
	
    private String getStateText(int state) {
        switch(state) {
        case SELECT:
            return "SELECT";
        case MOVE:
            return "MOVE";
        case ADDPOINT:
            return "ADD POINT";
        }
        return "NONE";
    }
 
	class NodePoint extends Component {
		int x = 0;
		int y = 0;
		int v = 0;
		int s = 0;
		boolean isSelect = false;
		
		public NodePoint(int x, int y) {
			this(x, y, 0, 0);
		}
		public NodePoint(int x, int y, int v, int s) {
			this.x = x;
			this.y = y;
			this.v = v;
			this.s = s;
			
			setLocation(x-P_SIZE/2, y-P_SIZE/2);
			setSize(P_SIZE,P_SIZE);
		}

		public int getXpos() {
			return x;
		}

		public void setXpos(int x) {
			setLocation(x-P_SIZE/2, y-P_SIZE/2);
			this.x = x;
		}

		public int getYpos() {
			return y;
		}

		public void setYpos(int y) {
			setLocation(x-P_SIZE/2, y-P_SIZE/2);
			this.y = y;
		}
		
		public void setVelocity(int v) {
			this.v = v;
		}
		
		public int getVelocity() {
			return v;
		}
		
		public void setStay(int s) {
			this.s = s;
		}
		
		public int getStay() {
			return s;
		}
	
		public boolean isSelect() {
			return isSelect;
		}

		public void setSelect(boolean isSelect) {
			this.isSelect = isSelect;
		}
		
		public boolean contains(int x, int y) {
			Rectangle r = new Rectangle(getXpos()-P_SIZE/2, getYpos()-P_SIZE/2,
			P_SIZE,P_SIZE);
			return r.contains(x, y);
		}

		public void paint(Graphics g) {
			if (isSelect) {
				g.setColor(Color.blue);
			} else {
    			g.setColor(Color.red);
			}
			g.fillRect(0, 0, P_SIZE, P_SIZE);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("   ");
			sb.append(getXpos() + "," + getYpos());
			sb.append("   \n");
			sb.append("   ");
			sb.append("V = " + getVelocity());
			sb.append("   ");
			return sb.toString();
		}
	}
	
	class PointView extends JPanel {
		public PointView() {
			super();
		}
		
		public void paint(Graphics g) {
			super.paint(g);
 			g.setColor(Color.red);
 			//	draw line among points
			int x = points.elementAt(0).getXpos();
			int y = points.elementAt(0).getYpos();
			for (int i = 1 ; i < points.size(); i++) {
				g.drawLine(x, y, points.elementAt(i).getX(), points.elementAt(i).getY());
				x = points.elementAt(i).getX();
				y = points.elementAt(i).getY();
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && state == ADDPOINT) {
			//	add or load current points
			NodePoint p = new NodePoint(e.getX(), e.getY(), nodeInfo.getCurrentVelocity(), 0);
			points.add(p);
			view.add(p, 0);
			addDesComponent();
	        repaint();
		} else if (e.getModifiers() == MouseEvent.BUTTON1_MASK && state == SELECT) {
			for (int i = 0; i < points.size(); i++) {
				NodePoint p = points.elementAt(i);
				if (p.contains(e.getX(), e.getY())) {
					p.setSelect(true);
					selectedPoint = p;
					setState(MOVE);
					repaint();
					return;
				}
			}
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			for (int i = 0; i < points.size(); i++) {
				NodePoint p = points.elementAt(i);
				if (p.contains(e.getX(), e.getY())) {
                    selectedPoint = p;
                    popupMenu.show(view, e.getX(), e.getY()); 
                    return;
				}
			}
		}
 
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if (state == MOVE && selectedPoint != null) {
    		selectedPoint.setXpos(e.getX());
    		selectedPoint.setYpos(e.getY());
    		selectedPoint.setSelect(false);
    		selectedPoint = null;
    		setState(SELECT);
    		repaint();
		}
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (state == MOVE && selectedPoint != null) {
			selectedPoint.setXpos(e.getX());
			selectedPoint.setYpos(e.getY());
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (state == SELECT) {
			for (int i = 0; i < points.size(); i++) {
				NodePoint p = points.elementAt(i);
				if (p.contains(e.getX(), e.getY())) {
		  			setDesComp(e.getX(), e.getY(), 
		  					p.toString(), Resources.DES_COLOR_MOBILENODE, true);
	                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            		view.repaint();
					return;
				}
			}
	        view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        desComp.setVisible(false);
		}
		statusBar.setPosition(e.getX(), e.getY());
	}

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    for (int i = 0; velocity != null && i < velocity.length; i++) {
	    	if (source == velocity[i]) {
        	    if (!source.getValueIsAdjusting()) {
        	        velocityFld[i].setText(String.valueOf((int)source.getValue()));
        	    }
	    	}
    	}
	}
}
