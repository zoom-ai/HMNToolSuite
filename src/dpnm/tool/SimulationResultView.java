package dpnm.tool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

import java.io.*;

import dpnm.tool.comp.HoverButton;
import dpnm.tool.data.NetworkMobileNodeInfo;

public class SimulationResultView extends JPanel {
	private static final int NUM_OF_METRIC = 3;
	private static final int GRAPH_WIDTH = 500;
	private static final int GRAPH_HEIGHT = 300;
	
	private JPanel mainPane = null;
	private JButton updateBtn = null;
	NetworkMobileNodeInfo nodeInfo[] = null;
	XYSeries horizontalHandover[] = null;
	XYSeries verticalHandover[] = null;
	XYSeries totalHandover[] = null;
	XYSeriesCollection handoverCollection[] = null;
	JFreeChart handoverChart[] = null;
	
	XYSeries velocity[] = null;
	XYSeriesCollection velocityCollection[] = null;
	JFreeChart velocityChart[] = null;
	
	XYSeries stay[][] = null;
	XYSeriesCollection stayCollection[] = null;
	JFreeChart stayChart[] = null;
	
	JLabel handoverGraph[] = null;
	JLabel velocityGraph[] = null;
	JLabel stayGraph[] = null;
	
	public SimulationResultView() {
		setLayout(new BorderLayout());
		mainPane = new JPanel();
		add(mainPane, BorderLayout.CENTER);
		
		updateBtn = new HoverButton("Update Graph");
		updateBtn.setToolTipText("Update Graph");
		updateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateGraph();
			}
		});
		add(updateBtn, BorderLayout.NORTH);
    }
	
	public void setMobileNodeInfo(NetworkMobileNodeInfo[] nodeInfo) {
		this.nodeInfo = nodeInfo;
		
		mainPane.setLayout(new GridLayout(nodeInfo.length+1,NUM_OF_METRIC));
		horizontalHandover = new XYSeries[nodeInfo.length+1];
		verticalHandover = new XYSeries[nodeInfo.length+1];
		totalHandover = new XYSeries[nodeInfo.length+1];
		handoverCollection = new XYSeriesCollection[nodeInfo.length+1];
		handoverGraph = new JLabel[nodeInfo.length+1];
		handoverChart = new JFreeChart[nodeInfo.length+1];
		

		for (int i = 0; i < nodeInfo.length+1; i++) {
    		horizontalHandover[i] = new XYSeries("# of Horizontal Handover", false);
//    		horizontalHandover[i].add(0, 0);
    		verticalHandover[i] = new XYSeries("# of Vertical Handover", false);
//    		verticalHandover[i].add(0, 0);
    		totalHandover[i] = new XYSeries("# of Total Handover", false);
//    		totalHandover[i].add(0, 0);
    		handoverCollection[i] = new XYSeriesCollection();
    		handoverCollection[i].addSeries(horizontalHandover[i]);
    		handoverCollection[i].addSeries(verticalHandover[i]);
//    		handoverCollection[i].addSeries(totalHandover[i]);
    		handoverGraph[i] = new JLabel();
    		handoverGraph[i].setSize(new Dimension(GRAPH_WIDTH,GRAPH_HEIGHT));
    		
    		String name = null;
    		if (i == 0) {
    			name = "Handover # of All Mobile Nodes";
    		} else {
    			name = "Handover # of Mobile Node " + nodeInfo[i-1].getDevice().getId();
    		}
			handoverChart[i] = ChartFactory.createXYLineChart(
				name,
                "# of Handovers",           
                "Time (sec)",           
                (XYDataset)handoverCollection[i],
                PlotOrientation.HORIZONTAL,
                true, true, true);
		}
		//	velocity
		
		velocity = new XYSeries[nodeInfo.length];
		velocityCollection = new XYSeriesCollection[nodeInfo.length+1];
		velocityGraph = new JLabel[nodeInfo.length+1];
		velocityChart = new JFreeChart[nodeInfo.length+1];

		velocityCollection[0] = new XYSeriesCollection();
		velocityGraph[0] = new JLabel();
		velocityGraph[0].setSize(new Dimension(GRAPH_WIDTH,GRAPH_HEIGHT));
		velocityChart[0] = ChartFactory.createXYLineChart(
				"Velocity of All Mobile Nodes",
                "Velocity (km/h)",           
                "Time (sec)",           
                (XYDataset)velocityCollection[0],
                PlotOrientation.HORIZONTAL,
                true, true, true);
 
		for (int i = 0; i < nodeInfo.length; i++) {
			velocity[i] = new XYSeries(nodeInfo[i].getDevice().getId(), false);
//    		velocity[i].add(0, 0);
			velocityCollection[0].addSeries(velocity[i]);
    		velocityCollection[i+1] = new XYSeriesCollection();
			velocityCollection[i+1].addSeries(velocity[i]);
			velocityGraph[i+1] = new JLabel();
    		velocityGraph[i+1].setSize(new Dimension(GRAPH_WIDTH,GRAPH_HEIGHT));
    		
			velocityChart[i+1] = ChartFactory.createXYLineChart(
					"Velocity of Mobile Node " + nodeInfo[i].getDevice().getId(),
                    "Velocity (km/h)",           
                    "Time (sec)",           
                    (XYDataset)velocityCollection[i+1],
                    PlotOrientation.HORIZONTAL,
                    true, true, true);
		}
		
		//	stay
		stay = new XYSeries[nodeInfo.length+1][];
		stayCollection = new XYSeriesCollection[nodeInfo.length+1];
		stayGraph = new JLabel[nodeInfo.length+1];
		stayChart = new JFreeChart[nodeInfo.length+1];

		stay[0] = new XYSeries[nodeInfo.length];
		stayCollection[0] = new XYSeriesCollection();
		stayGraph[0] = new JLabel();
		stayGraph[0].setSize(new Dimension(GRAPH_WIDTH,GRAPH_HEIGHT));
		stayChart[0] = ChartFactory.createXYLineChart(
				"Connected Time of All Mobile Nodes",
                "Connected Time (msec)",           
                "Time (sec)",           
                (XYDataset)stayCollection[0],
                PlotOrientation.HORIZONTAL,
                true, true, true);
 
		for (int i = 0; i < nodeInfo.length; i++) {
			stay[0][i] = new XYSeries(nodeInfo[i].getDevice().getId(), false);
//    		stay[0][i].add(0, 0);
			stay[i+1] = new XYSeries[nodeInfo[i].getDevice().getNetworkInterfaces().length];
			stayCollection[0].addSeries(stay[0][i]);
			stayCollection[i+1] = new XYSeriesCollection();
			for (int j = 0; j < stay[i+1].length; j++) {
				stay[i+1][j] = new XYSeries(nodeInfo[i].getDevice().getNetworkInterfaces()[j].getName(), false);
//        		stay[i+1][j].add(0, 0);
				stayCollection[i+1].addSeries(stay[i+1][j]);
			}
			stayGraph[i+1] = new JLabel();
        	stayGraph[i+1].setSize(new Dimension(GRAPH_WIDTH,GRAPH_HEIGHT));
        	
			stayChart[i+1] = ChartFactory.createXYLineChart(
					"Connected Time of Each Network of Mobile Node " + nodeInfo[i].getDevice().getId(),
                    "Connected Time (msec)",           
                    "Time (sec)",           
                    (XYDataset)stayCollection[i+1],
                    PlotOrientation.HORIZONTAL,
                    true, true, true);
		}
   
		for (int i = 0; i < nodeInfo.length+1; i++) {
			mainPane.add(handoverGraph[i]);
			mainPane.add(velocityGraph[i]);
			mainPane.add(stayGraph[i]);
		}
	}
	
	public void reset() {
		for (int i = 0; handoverCollection != null && 
			velocityCollection != null &&
			stayCollection != null 
			&& i < handoverCollection.length; i++) {
			handoverCollection[i].removeAllSeries();
			velocityCollection[i].removeAllSeries();
			stayCollection[i].removeAllSeries();
			remove(handoverGraph[i]);
			remove(velocityGraph[i]);
			remove(stayGraph[i]);
		}
		horizontalHandover = null;
		verticalHandover = null;
		totalHandover = null;
		velocity = null;
		stay = null;
		handoverCollection = null;
		velocityCollection = null;
		stayCollection = null;
	}
	
	public synchronized void updateMobileNode(long time) {
		double t = (double)time/1000;
		updateHandover(t);
		updateVelocity(t);
		updateStayTime(t);
	}
	
	public synchronized void updateGraph() {
   		BufferedImage image = null;
		for (int i = 0; i < nodeInfo.length; i++) {
       		image = handoverChart[i+1].createBufferedImage(GRAPH_WIDTH,GRAPH_HEIGHT);
    		handoverGraph[i+1].setIcon(new ImageIcon(image));
    		
    		image = velocityChart[i+1].createBufferedImage(GRAPH_WIDTH,GRAPH_HEIGHT);
    		velocityGraph[i+1].setIcon(new ImageIcon(image));
    		
    		image = stayChart[i+1].createBufferedImage(GRAPH_WIDTH,GRAPH_HEIGHT);
    		stayGraph[i+1].setIcon(new ImageIcon(image));
		}
    	image = handoverChart[0].createBufferedImage(GRAPH_WIDTH,GRAPH_HEIGHT);
		handoverGraph[0].setIcon(new ImageIcon(image));
   		
		image = velocityChart[0].createBufferedImage(GRAPH_WIDTH,GRAPH_HEIGHT);
		velocityGraph[0].setIcon(new ImageIcon(image));
		
		image = stayChart[0].createBufferedImage(GRAPH_WIDTH,GRAPH_HEIGHT);
		stayGraph[0].setIcon(new ImageIcon(image));
		
		mainPane.repaint();
	}
	
	private synchronized void updateHandover(double time) {
		long hh = 0;
		long vh = 0;
		long th = 0;
		for (int i = 0; i < nodeInfo.length; i++) {
			long ihh = nodeInfo[i].getDevice().getNetworkInterfaceManager(
					).getNumHorizontalHandover();
			long ivh = nodeInfo[i].getDevice().getNetworkInterfaceManager(
					).getNumVerticalHandover();
			hh += ihh;
			vh += ivh;
			th += ihh+ivh;
    		horizontalHandover[i+1].add(hh, time);
    		verticalHandover[i+1].add(vh, time);
    		totalHandover[i+1].add(th, time);
		}
		horizontalHandover[0].add(hh, time);
		verticalHandover[0].add(vh, time);
		totalHandover[0].add(th, time);
	}
	
	private synchronized void updateVelocity(double time) {
		for (int i = 0; i < nodeInfo.length; i++) {
			int v = nodeInfo[i].getCurrentVelocity();
    		velocity[i].add(v, time);
		}
	}
	
	
	private synchronized void updateStayTime(double time) {
		for (int i = 0; i < nodeInfo.length; i++) {
			long duration = 0;
			for (int j = 0; j < stay[i+1].length; j++) {
				long d =  nodeInfo[i].getDevice().getNetworkInterfaces()[j].getDuration();
				duration += d;
				stay[i+1][j].add(d, time);
			}
			stay[0][i].add(duration, time);
		}
	}
	
	public void exportAllGraph(File dir) {
		if (nodeInfo == null)
			return;
		if (!dir.exists()) {
			dir.mkdir();
		}
		for (int i = 0; i < nodeInfo.length+1; i++) {
			// handover
			String filename = dir.getAbsolutePath()+File.separator;
			if (i == 0) {
				filename = filename + "handover_"+i+"_all.png";
			} else {
				filename = filename + "handover_"+i+"_"+nodeInfo[i-1].getDevice().getId()+".png";
			}

			try {
        		ChartUtilities.saveChartAsPNG(
    				new File(filename), handoverChart[i], GRAPH_WIDTH, GRAPH_HEIGHT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
    		
			filename = dir.getAbsolutePath()+File.separator;
			if (i == 0) {
				filename = filename + "velocity_"+i+"_all.png";
			} else {
				filename = filename + "velocity_"+i+"_"+nodeInfo[i-1].getDevice().getId()+".png";
			}

			try {
        		ChartUtilities.saveChartAsPNG(
    				new File(filename), velocityChart[i], GRAPH_WIDTH, GRAPH_HEIGHT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			filename = dir.getAbsolutePath()+File.separator;
			if (i == 0) {
				filename = filename + "stay_"+i+"_all.png";
			} else {
				filename = filename + "stay_"+i+"_"+nodeInfo[i-1].getDevice().getId()+".png";
			}

			try {
        		ChartUtilities.saveChartAsPNG(
    				new File(filename), stayChart[i], GRAPH_WIDTH, GRAPH_HEIGHT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
