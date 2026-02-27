package dpnm.tool;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.swing.*;

import java.util.*;

import dpnm.tool.comp.*;
import dpnm.tool.data.*;
import dpnm.util.*;

public class MonitorView extends JFrame implements UpdateListener {
	private NetworkDeviceInfo deviceInfo[];
	private NetworkMobileNodeInfo mobileNodeInfo[];

	NetworkMonitorView networkView = null;
	MobileNodeMonitorView mNodeView = null;
	
	public MonitorView(String name) {
		super(Env.MONITOR_TITLE+" [" + name + "]");
		setIconImage(getToolkit().getImage(HMNEmulatorGUI.rf.getURL(Resources.ICON_DIR+Resources.MONITOR_ICON)));
		setSize(Env.MONITOR_WIDTH, Env.MONITOR_HEIGHT);
		setBounds(50,50,
				Env.MONITOR_WIDTH, Env.MONITOR_HEIGHT);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
    		}
    	);	}
	
	public void setTitle(String name) {
		super.setTitle("Monitor View [" + name + "]");
	}
	
	public synchronized void setMapInfo(NetworkMapInfo mapInfo) {
		deviceInfo = mapInfo.getDevices();
		mobileNodeInfo = mapInfo.getMnodes();
		
		createUI();
		UpdateTimer.getInstance().addUpdateListener(this);
	}
	
	private void createUI() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1,2));
		setContentPane(contentPane);
		createView();
	}
	
	private void createView() {
		networkView = new NetworkMonitorView(deviceInfo);
        networkView.setPreferredSize(new Dimension(Env.MONITOR_WIDTH/2, Env.MONITOR_HEIGHT));
		
		/*
        networkView.setPreferredSize(new Dimension(Env.MONITOR_WIDTH/2, Env.MONITOR_HEIGHT));
        JScrollPane js = new JScrollPane(networkView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JTabbedPane tp = new JTabbedPane();
        tp.addTab("Networks", js);
        
        for (int i = 0; i < deviceInfo.length; i++) {
        	JPanel p = new JPanel();
        	tp.addTab(deviceInfo[i].getDevice().getName(), p);
        }
        */
        getContentPane().add(networkView);
        
		mNodeView = new MobileNodeMonitorView(mobileNodeInfo);
        mNodeView.setPreferredSize(new Dimension(Env.MONITOR_WIDTH/2, Env.MONITOR_HEIGHT));
        /*
        JScrollPane js2 = new JScrollPane(mNodeView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JTabbedPane tp2 = new JTabbedPane();
        tp2.addTab("Mobile Nodes", js2);
        for (int i = 0; i < mobileNodeInfo.length; i++) {
        	JPanel p = new JPanel();
        	tp2.addTab(mobileNodeInfo[i].getDevice().getID(), p);
        }
        */
        getContentPane().add(mNodeView);
	}
	
	private void exit() {
		UpdateTimer.getInstance().removeUpdateListener(this);
		mNodeView.exit();
		setVisible(false);
	}

	public synchronized void updateInfo() {
		// TODO Auto-generated method stub
		networkView.updateInfo();
		mNodeView.updateInfo();
	}
}
