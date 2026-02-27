package dpnm.tool;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;

import dpnm.mobilenode.*;
import dpnm.tool.comp.ZFileFilter;

class MobileNodeInfoPane extends JPanel implements MouseListener, ActionListener {
	private JTabbedPane mainPane;
	private JPopupMenu popupMenu;
	private JMenuItem addMobileNode;
	private JMenuItem modifyMobileNode;
	private JMenuItem removeMobileNode;
	
	private JTextField colorFld;
	private JTextField iconFld;
	
	String currentMobileNode = null;
	
	public MobileNodeInfoPane() {
		setLayout(new BorderLayout());
		mainPane = new JTabbedPane();
		mainPane.setTabPlacement(JTabbedPane.LEFT);
		add(mainPane, BorderLayout.CENTER);
		mainPane.addMouseListener(this);
		
		createPopupMenu();
		loadNetworkInformation();
	}
	
    private void createPopupMenu() {
    	popupMenu = new JPopupMenu();
    	
    	addMobileNode = new JMenuItem("Add New Mobile Node");
    	addMobileNode.setActionCommand("AddMobileNode");
    	addMobileNode.addActionListener(this);
    	
    	popupMenu.add(addMobileNode);
    	
    	modifyMobileNode = new JMenuItem("Modify Mobile Node");
    	modifyMobileNode.setActionCommand("ModifyMobileNode");
    	modifyMobileNode.addActionListener(this);
    	
    	popupMenu.add(modifyMobileNode);
 
    	removeMobileNode = new JMenuItem("Delete Mobile Node");
    	removeMobileNode.setActionCommand("DeleteMobileNode");
    	removeMobileNode.addActionListener(this);
    	
    	popupMenu.add(removeMobileNode);
    }
    
	public void loadNetworkInformation() {
		IMobileNode mobileNodes[] = MobileNodeFactory.getInstance().getMobileNodes();
		for (int i = 0; i < mobileNodes.length; i++) {
    		mainPane.addTab(mobileNodes[i].getName(), new MobileNodeView(mobileNodes[i]));
		}
	}
	
	public void reload() {
		mainPane.removeAll();
		loadNetworkInformation();
		repaint();
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
		// TODO Auto-generated method stub
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			currentMobileNode = mainPane.getTitleAt(mainPane.getSelectedIndex());
			
			modifyMobileNode.setText("Modify Mobile Node ("+currentMobileNode+")");
			removeMobileNode.setText("Remove Mobile Node ("+currentMobileNode+")");
            popupMenu.show(mainPane, e.getX(), e.getY()); 
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		String menuItemString = e.getActionCommand();
		
		if (menuItemString.intern() == "AddMobileNode") {
			createMobileNode();
		} else if (menuItemString.intern() == "ModifyMobileNode") {
			modifyMobileNode();
			currentMobileNode = null;
		} else if (menuItemString.intern() == "DeleteMobileNode") {
			deleteMobileNode();
			currentMobileNode = null;
		}
	}
	
	class MobileNodeView extends JPanel {
    	String M_PARAM[] = {
			"Icon",
			"Color",
			"Max Velocity (km/h)",
			"Min Velocity (km/h)"
    	};
    	JComponent comp[] = new JComponent[4];
    	
		public MobileNodeView(IMobileNode mobileNode) {
			setLayout(null);
			JLabel label = new JLabel("Icon:");
			label.setBounds(10, 24, 100, 20);
			add(label);
			
			label = new JLabel(HMNEmulatorGUI.rf.getIcon(Resources.MOBILENODE_DIR+mobileNode.getIconStr()));
			label.setBounds(120, 10, 48, 48);
			add(label);
			
			label = new JLabel("Color:");
			label.setBounds(10, 60, 100, 20);
			add(label);
			
			label = new JLabel("  ");
			label.setOpaque(true);
			label.setBackground(new Color(mobileNode.getColor()));
			label.setBounds(120, 60, 40, 20);
			add(label);
	
			label = new JLabel("Max Velocity:");
			label.setBounds(10, 80, 100, 20);
			add(label);
			
			label = new JLabel(String.valueOf(mobileNode.getMaxVelocity()));
			label.setBounds(120, 80, 40, 20);
			add(label);
	
			label = new JLabel("Min Velocity:");
			label.setBounds(10, 100, 100, 20);
			add(label);
			
			label = new JLabel(String.valueOf(mobileNode.getMinVelocity()));
			label.setBounds(120, 100, 40, 20);
			add(label);
		}
	}
	
	private void createMobileNode() {
	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		String nameStr[] = {"Name", 
    		"Max Velocity (km/h) (int)",
			"Min Velocity (km/h) (int)"
    	};	
		JTextField valueFld[] = new JTextField[nameStr.length];

		for (int i = 0; i < nameStr.length; i++) {
			JLabel label = new JLabel(nameStr[i]);
			c.gridwidth = GridBagConstraints.RELATIVE;
			gridbag.setConstraints(label,c);
			p.add(label);
				
			valueFld[i] = new JTextField(10);
			valueFld[i].setForeground(Color.blue);
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(valueFld[i],c);
			p.add(valueFld[i]);
		}
        
        JLabel label = new JLabel("Icon");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
        
        JPanel bPane = new JPanel();
        iconFld = new JTextField(10);
        iconFld.setText("");
        iconFld.setForeground(Color.blue);
        bPane.add(iconFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        JButton btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
    			JFileChooser jf = new JFileChooser(new File(Resources.HOME+File.separator+Resources.MOBILENODE_DIR));

    			jf.setDialogTitle("Choose a icon file");
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
    				iconFld.setText(file.getName());
    			}
            }
        });
        bPane.add(btn);
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
        
        label = new JLabel("Color (FFFFFF)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        bPane = new JPanel();
        colorFld = new JTextField(6);
        colorFld.setForeground(Color.blue);
        bPane.add(colorFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	Color initColor = null;
            	try {
            		initColor = new Color(Integer.parseInt(colorFld.getText(), 16));
            	} catch (Exception ex) {
            		initColor = null;
            	}
            	Color c = JColorChooser.showDialog(null, "Pick a color",  initColor);
            	if (c != null) {
            		/*
                	colorFld.setText(Integer.toHexString(c.getRed()).toUpperCase()+
                			Integer.toHexString(c.getGreen()).toUpperCase()+
                			Integer.toHexString(c.getBlue()).toUpperCase());
                			*/
                	colorFld.setText(Integer.toHexString(c.getRGB()).toUpperCase().substring(2, 8));
            	}
            }
        });
        bPane.add(btn);
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
       
	    message[0] = "Create New Mobile Node";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Create New Mobile Node",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.YES_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert mobile node name",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            MobileNodeFactory.getInstance().createMobileNode(
            		valueFld[0].getText(), 						// name
            		iconFld.getText(),// icon
            		Integer.parseInt(colorFld.getText(), 16),	// color
            		Integer.parseInt(valueFld[1].getText()),	// minVelocity
            		Integer.parseInt(valueFld[2].getText())	// maxVelocity
            		);
            MobileNodeFactory.getInstance().openMobileNode();
            reload();
		}
    }
 	private void modifyMobileNode() {
	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		String nameStr[] = {"Name", 
			"Max Velocity (km/h) (int)",
			"Min Velocity (km/h) (int)"
    	};	
		IMobileNode mobileNode = MobileNodeFactory.getInstance().getMobileNode(currentMobileNode);
		String valueStr[] = {
				mobileNode.getName(),
				String.valueOf(mobileNode.getMaxVelocity()),
				String.valueOf(mobileNode.getMinVelocity())};
		JTextField valueFld[] = new JTextField[nameStr.length];

		for (int i = 0; i < nameStr.length; i++) {
			JLabel label = new JLabel(nameStr[i]);
			c.gridwidth = GridBagConstraints.RELATIVE;
			gridbag.setConstraints(label,c);
			p.add(label);
				
			valueFld[i] = new JTextField(10);
			valueFld[i].setText(valueStr[i]);
			valueFld[i].setForeground(Color.blue);
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(valueFld[i],c);
			p.add(valueFld[i]);
		}
		
        JLabel label = new JLabel("Icon");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
        
        JPanel bPane = new JPanel();
        iconFld = new JTextField(10);
        iconFld.setText(mobileNode.getIconStr());
        iconFld.setForeground(Color.blue);
        bPane.add(iconFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        JButton btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
    			JFileChooser jf = new JFileChooser(new File(Resources.HOME+File.separator+Resources.MOBILENODE_DIR));

    			jf.setDialogTitle("Choose a icon file");
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
    				iconFld.setText(file.getName());
    			}
            }
        });
        bPane.add(btn);
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
        
        label = new JLabel("Color (FFFFFF)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        bPane = new JPanel();
        colorFld = new JTextField(6);
        colorFld.setText(Integer.toHexString(mobileNode.getColor()));
        colorFld.setForeground(Color.blue);
        bPane.add(colorFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        btn = new JButton("...");
        btn.setToolTipText("Browsing...");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	Color initColor = null;
            	try {
            		initColor = new Color(Integer.parseInt(colorFld.getText(), 16));
            	} catch (Exception ex) {
            		initColor = null;
            	}
            	Color c = JColorChooser.showDialog(null, "Pick a color",  initColor);
            	if (c != null) {
                	colorFld.setText(Integer.toHexString(c.getRed()).toUpperCase()+
                			Integer.toHexString(c.getGreen()).toUpperCase()+
                			Integer.toHexString(c.getBlue()).toUpperCase());
            	}
            }
        });
        bPane.add(btn);
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
 
	    message[0] = "Modify Mobile Node ("+currentMobileNode+")";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Modify Mobile Node ("+currentMobileNode+")",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.OK_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert map name",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            MobileNodeFactory.getInstance().modifyMobileNode(
            		valueFld[0].getText(), 						// name
            		iconFld.getText(),// icon
            		Integer.parseInt(colorFld.getText(), 16),	// color
            		Integer.parseInt(valueFld[1].getText()),	// minVelocity
            		Integer.parseInt(valueFld[2].getText())	// maxVelocity
            		);
            MobileNodeFactory.getInstance().openMobileNode();
 
           reload();
		}
    }
    
 	private void deleteMobileNode() {
 		if (currentMobileNode.intern() == "BIKE" || currentMobileNode.intern() == "CAR" || 
 				currentMobileNode.intern() == "TAXI" || currentMobileNode.intern() == "BUS" ||
 				currentMobileNode.intern() == "WALK" || currentMobileNode.intern() == "TRAIN") {
 					JOptionPane.showMessageDialog(null,
 							"\""+currentMobileNode+"\" cannot be deleted.",
 							"Warning",
 							JOptionPane.WARNING_MESSAGE);
 					return;
 		}
 		int result = JOptionPane.showConfirmDialog(null, "Do you want to delete \""+currentMobileNode+"\"?",
 				"Delete Mobile Node",
 				JOptionPane.YES_NO_OPTION,
 				JOptionPane.WARNING_MESSAGE
 				);
 		if (result == JOptionPane.YES_OPTION) {
 			MobileNodeFactory.getInstance().deleteNetwork(currentMobileNode);
 			MobileNodeFactory.getInstance().openMobileNode();
 			reload();
 		}
 	}
}