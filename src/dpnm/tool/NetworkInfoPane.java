package dpnm.tool;

import javax.swing.JButton;
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import dpnm.network.*;
import dpnm.tool.comp.ZFileFilter;
import dpnm.tool.data.NetworkDeviceInfo;

class NetworkInfoPane extends JPanel implements MouseListener, ActionListener {

	private JTabbedPane mainPane;
	private JPopupMenu popupMenu;
	private JMenuItem addNetwork;
	private JMenuItem modifyNetwork;
	private JMenuItem removeNetwork;
	
	private JTextField colorFld;
	
	String currentNetwork = null;
	
	public NetworkInfoPane() {
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
    	
    	addNetwork = new JMenuItem("Add New Network");
    	addNetwork.setActionCommand("AddNetwork");
    	addNetwork.addActionListener(this);
    	
    	popupMenu.add(addNetwork);
    	
    	modifyNetwork = new JMenuItem("Modify Network");
    	modifyNetwork.setActionCommand("ModifyNetwork");
    	modifyNetwork.addActionListener(this);
    	
    	popupMenu.add(modifyNetwork);
 
    	removeNetwork = new JMenuItem("Delete Network");
    	removeNetwork.setActionCommand("DeleteNetwork");
    	removeNetwork.addActionListener(this);
    	
    	popupMenu.add(removeNetwork);
    }
    
	public void loadNetworkInformation() {
		INetwork networks[] = NetworkFactory.getInstance().getNetworks();
		for (int i = 0; i < networks.length; i++) {
			NetworkInfoModel model = new NetworkInfoModel(networks[i]);
			JTable table = new JTable(model);
    		table.setGridColor(Color.black);
    		mainPane.addTab(networks[i].getName(), table);
        	mainPane.setBackgroundAt(i, new Color(networks[i].getColor()));
        	mainPane.setToolTipTextAt(i, networks[i].getName());
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
			currentNetwork = mainPane.getTitleAt(mainPane.getSelectedIndex());
			modifyNetwork.setText("Modify Network ("+currentNetwork+")");
			removeNetwork.setText("Remove Network ("+currentNetwork+")");
            popupMenu.show(mainPane, e.getX(), e.getY()); 
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		String menuItemString = e.getActionCommand();
		
		if (menuItemString.intern() == "AddNetwork") {
			createNetwork();
		} else if (menuItemString.intern() == "ModifyNetwork") {
			modifyNetwork();
			currentNetwork = null;
		} else if (menuItemString.intern() == "DeleteNetwork") {
			deleteNetwork();
			currentNetwork = null;
		}
	}
	
	private void createNetwork() {
	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		String nameStr[] = {"Name", 
			"Coverage (meter) (int)",
			"Bandwidth (kbyte) (int)",
			"Delay (ms) (int)",
			"Jitter (ms) (int)",
			"BER (dB) (double)",
			"Throughput (Mbyte/s) (double)",
			"Burst Error (double)",
			"Packet Loss Ratio (double)",
			"Cost Rate ($/min) (double)",
			"Power Tx (W) (double)",
			"Power Rx (W) (double)",
			"Power Idle (W) (double)",
			"Min Velocity (km/h) (int)",
			"Max Velocity (km/h) (int)"
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
		
        JLabel label = new JLabel("Device");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String deviceTypes[] = {"BaseStation", "AccessPoint", "RadioAccessStation"};
        JComboBox deviceCombo = new JComboBox(deviceTypes);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(deviceCombo,c);

        p.add(deviceCombo);
        
        label = new JLabel("Color (FFFFFF)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JPanel bPane = new JPanel();
        colorFld = new JTextField(6);
        colorFld.setForeground(Color.blue);
        bPane.add(colorFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        JButton btn = new JButton("...");
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
                	colorFld.setText(Integer.toHexString(c.getRGB()).toUpperCase().substring(2, 8));
                			
                	/*
                	colorFld.setText(Integer.toHexString(c.getRed()).toUpperCase()+
                			Integer.toHexString(c.getGreen()).toUpperCase()+
                			Integer.toHexString(c.getBlue()).toUpperCase());
                			*/
            	}
            }
        });
        bPane.add(btn);
        gridbag.setConstraints(bPane,c);
        p.add(bPane);
       
	    message[0] = "Create New Network";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Create New Network",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.YES_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert network name",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            NetworkFactory.getInstance().createNetwork(
            		valueFld[0].getText(), 						// name
            		deviceTypes[deviceCombo.getSelectedIndex()],// device type
            		Integer.parseInt(valueFld[1].getText()),	// coverage
            		Integer.parseInt(valueFld[2].getText()),	// bandwidth
            		Integer.parseInt(valueFld[3].getText()),	// delay
            		Integer.parseInt(valueFld[4].getText()),	// jitter
            		Double.parseDouble(valueFld[5].getText()),	// ber
            		Double.parseDouble(valueFld[6].getText()),	// throughput
            		Double.parseDouble(valueFld[7].getText()),	// burstErr
            		Double.parseDouble(valueFld[8].getText()),	// plr
            		Double.parseDouble(valueFld[9].getText()),	// costRate
            		Double.parseDouble(valueFld[10].getText()),	// txPower
            		Double.parseDouble(valueFld[11].getText()),	// rxPower
            		Double.parseDouble(valueFld[12].getText()),	// idlePower
            		Integer.parseInt(valueFld[13].getText()),	// minVelocity
            		Integer.parseInt(valueFld[14].getText()),	// maxVelocity
            		Integer.parseInt(colorFld.getText(), 16)	// color
            		);
            NetworkFactory.getInstance().openNetwork();
            reload();
		}
    }
 	private void modifyNetwork() {
	    Object[] message = new Object[2];
	    JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    p.setLayout(gridbag);

		String nameStr[] = {"Name", 
			"Coverage (meter) (int)",
			"Bandwidth (kbyte) (int)",
			"Delay (ms) (int)",
			"Jitter (ms) (int)",
			"BER (dB) (double)",
			"Throughput (Mbyte/s) (double)",
			"Burst Error (double)",
			"Packet Loss Ratio (double)",
			"Cost Rate ($/min) (double)",
			"Power Tx (W) (double)",
			"Power Rx (W) (double)",
			"Power Idle (W) (double)",
			"Min Velocity (km/h) (int)",
			"Max Velocity (km/h) (int)"
    	};	
		INetwork network = NetworkFactory.getInstance().getNetwork(currentNetwork);
		String valueStr[] = {
				network.getName(),
				String.valueOf(network.getCoverage()),
				String.valueOf(network.getBandwidth()),
				String.valueOf(network.getDelay()),
				String.valueOf(network.getJitter()),
				String.valueOf(network.getBER()),
				String.valueOf(network.getThroughput()),
				String.valueOf(network.getBurstError()),
				String.valueOf(network.getPacketLossRatio()),
				String.valueOf(network.getCostRate()),
				String.valueOf(network.getTxPower()),
				String.valueOf(network.getRxPower()),
				String.valueOf(network.getIdlePower()),
				String.valueOf(network.getMinVelocity()),
				String.valueOf(network.getMaxVelocity())};
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
		
        JLabel label = new JLabel("Device");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String deviceTypes[] = {"BaseStation", "AccessPoint", "RadioAccessStation"};
        JComboBox deviceCombo = new JComboBox(deviceTypes);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(deviceCombo,c);
        
        for (int i = 0; i < deviceTypes.length; i++) {
        	if (deviceTypes[i].intern() == network.getDeviceType().intern()) {
        		deviceCombo.setSelectedIndex(i);
        		break;
        	}
        }

        p.add(deviceCombo);
        
        label = new JLabel("Color (FFFFFF)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JPanel bPane = new JPanel();
        colorFld = new JTextField(6);
        colorFld.setText(Integer.toHexString(network.getColor()));
        colorFld.setForeground(Color.blue);
        bPane.add(colorFld);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        JButton btn = new JButton("...");
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
       
	    message[0] = "Modify Network ("+currentNetwork+")";
	    message[1] = p;
	    
	    int result;

		result = JOptionPane.showOptionDialog(null, message, "Modify Network ("+currentNetwork+")",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if ( result == JOptionPane.OK_OPTION ) {
            if (valueFld[0].getText() == null || valueFld[0].getText().intern() == "".intern()) {
                JOptionPane.showMessageDialog(this, "Please, insert network name",
                        "Error", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            NetworkFactory.getInstance().modifyNetwork(
            		valueFld[0].getText(), 						// name
            		deviceTypes[deviceCombo.getSelectedIndex()],// device type
            		Integer.parseInt(valueFld[1].getText()),	// coverage
            		Integer.parseInt(valueFld[2].getText()),	// bandwidth
            		Integer.parseInt(valueFld[3].getText()),	// delay
            		Integer.parseInt(valueFld[4].getText()),	// jitter
            		Double.parseDouble(valueFld[5].getText()),	// ber
            		Double.parseDouble(valueFld[6].getText()),	// throughput
            		Double.parseDouble(valueFld[7].getText()),	// burstErr
            		Double.parseDouble(valueFld[8].getText()),	// plr
            		Double.parseDouble(valueFld[9].getText()),	// costRate
            		Double.parseDouble(valueFld[10].getText()),	// txPower
            		Double.parseDouble(valueFld[11].getText()),	// rxPower
            		Double.parseDouble(valueFld[12].getText()),	// idlePower
            		Integer.parseInt(valueFld[13].getText()),	// minVelocity
            		Integer.parseInt(valueFld[14].getText()),	// maxVelocity
            		Integer.parseInt(colorFld.getText(), 16)	// color
            		);
            NetworkFactory.getInstance().openNetwork();
            reload();
		}
    }
    
 	private void deleteNetwork() {
 		if (currentNetwork.intern() == "CDMA" || currentNetwork.intern() == "HSDPA" || 
 				currentNetwork.intern() == "WLAN" || currentNetwork.intern() == "WiBro") {
 					JOptionPane.showMessageDialog(null,
 							"\""+currentNetwork+"\" cannot be deleted.",
 							"Warning",
 							JOptionPane.WARNING_MESSAGE);
 					return;
 		}
 		int result = JOptionPane.showConfirmDialog(null, "Do you want to delete \""+currentNetwork+"\"?",
 				"Delete Network",
 				JOptionPane.YES_NO_OPTION,
 				JOptionPane.WARNING_MESSAGE
 				);
 		if (result == JOptionPane.YES_OPTION) {
 			NetworkFactory.getInstance().deleteNetwork(currentNetwork);
 			NetworkFactory.getInstance().openNetwork();
 			reload();
 		}
 	}
}
