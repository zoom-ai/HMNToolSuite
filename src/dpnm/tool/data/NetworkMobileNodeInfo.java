package dpnm.tool.data;

import dpnm.comm.CommunicationDevice;
import dpnm.mobiledevice.*;
import dpnm.mobilenode.*;
import dpnm.network.device.*;

import org.w3c.dom.*;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class NetworkMobileNodeInfo {
    /** device */
    public static final String MOBILENODE = "mobilenode";
    public static final String DEVICE     = "device";
    public static final String ID         = "id";
    public static final String APP		  = "application";
    public static final String NI		  = "networkinterface";
    public static final String XPOS       = "xpos";
    public static final String YPOS       = "ypos";
    public static final String TYPE       = "type";
    public static final String PATH		  = "path";
    
    public static final int PATH_XPOS 		= 0;
    public static final int PATH_YPOS 		= 1;
    public static final int PATH_VELOCITY 	= 2;
    public static final int PATH_STAY 		= 3;
    public static final int PATH_COUNT 		= PATH_STAY+1;
    
	private static final String PACKAGE_APP = "dpnm.mobiledevice.app";
	private static final String PACKAGE_NI = "dpnm.mobiledevice.device";
	
	private MobileDevice device;
	private int xpos = 0;
	private int ypos = 0;
	private int path[][] = null;
	private int currentVelocity = 0;
	private long duration = 0;
	private String type = null;
	
	//	logging
	private LogTable logs[] = null;
	private ArrayList<LogGraph> graphs = null;
	
	//	moving mobile node
	/*
	private int sX = 0;
	private int sY = 0;
	private int dX = 0;
	private int dY = 0;
	private int cX = 0;
	private int cY = 0;
	private int count = 0;
	private int timeCount = 0;
	*/
	
	//	new moving
	private int pathIndex = 0;
	private double rX = 0.0;
	private double rY = 0.0;
	private int stay = 0;
	
	public NetworkMobileNodeInfo() {
	}
	
    public NetworkMobileNodeInfo(String id, Vector<String> apps, Vector<String> nis, int xpos, int ypos, String type) {
    	setDevice(new DeviceInfo(id, apps, nis));
        setXpos(xpos);
        setYpos(ypos);
        setType(type);
		setCurrentVelocity(getMobileNode().getMaxVelocity());
        setPath(xpos, ypos);
    }
 
	public NetworkMobileNodeInfo(Node mNode) {
		NodeList nodes = mNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == DEVICE.intern()) {
				setDevice(new DeviceInfo(node));
			}
			else if(name.intern() == XPOS.intern()) {
				setXpos(Integer.parseInt(value));
			}
			else if(name.intern() == YPOS.intern()) {
				setYpos(Integer.parseInt(value));
			}
			else if(name.intern() == TYPE.intern()) {
				setType(value);
        		setCurrentVelocity(getMobileNode().getMaxVelocity());			}
			else if(name.intern() == PATH.intern()) {
				setPath(value);
			}
       	}
		if (path == null) {
			path = new int[1][PATH_COUNT];
			path[0][PATH_XPOS] = getXpos();
			path[0][PATH_YPOS] = getYpos();
			path[0][PATH_VELOCITY] = getCurrentVelocity();
			path[0][PATH_STAY] = 0;
			logs = new LogTable[1];
		}
	}

	public void setDevice(DeviceInfo info) {
		MobileApplication apps[] = getApplication(info.getApplications());
		NetworkInterface nis[] = getNetworkInterface(info.getNetworkInterfaces());
		
  		device = new MobileDevice(info.getId(), apps, nis);
	}
	
	
	public void setPath(int x, int y) {
		setPath(x, y, true);
	}
	
    public void setPath(int x, int y, boolean isInit) {
        if (path == null) {
        	path = new int[1][PATH_COUNT];
        	logs = new LogTable[1];
        }
		path[0][PATH_XPOS] = x;
		path[0][PATH_YPOS] = y;
		if (isInit) {
    		path[0][PATH_VELOCITY] = getCurrentVelocity();
    		path[0][PATH_STAY] = 0;
		}
    }
    
	public void setPath(int[][] path) {
		this.path = path;
		this.logs = new LogTable[path.length];
	}
	
	public int[][] getPath() {
		return path;
	}
	
	private void setPath(String pathStr) {
		StringTokenizer st = new StringTokenizer(pathStr, "|");
		path = new int[st.countTokens()][PATH_COUNT];
		this.logs = new LogTable[path.length];

		int i = 0;
		while(st.hasMoreElements()) {
			String p = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(p, ",");
			path[i][PATH_XPOS] = Integer.parseInt(st2.nextToken());
			path[i][PATH_YPOS] = Integer.parseInt(st2.nextToken());
			if (st2.hasMoreElements()) {
    			path[i][PATH_VELOCITY] = Integer.parseInt(st2.nextToken());
			} else {
				path[i][PATH_VELOCITY] = getCurrentVelocity();
			}
			if (st2.hasMoreElements()) {
    			path[i][PATH_STAY] = Integer.parseInt(st2.nextToken());
			} else {
				path[i][PATH_STAY] = 0;
			}
			i++;
		}
	}
	
	private String getPathStr() {
		if (path == null) {
			path = new int[1][PATH_COUNT];
			path[0][PATH_XPOS] = getXpos();
			path[0][PATH_YPOS] = getYpos();
			path[0][PATH_VELOCITY] = getCurrentVelocity();
			path[0][PATH_STAY] = 0;
		}
		StringBuffer sb = new StringBuffer();
		
		if (path != null) {
    		for (int i = 0; i < path.length; i++) {
    			sb.append(String.valueOf(path[i][PATH_XPOS]));
    			sb.append(",");
    			sb.append(String.valueOf(path[i][PATH_YPOS]));
    			sb.append(",");
    			sb.append(String.valueOf(path[i][PATH_VELOCITY]));
    			sb.append(",");
    			sb.append(String.valueOf(path[i][PATH_STAY]));
        		sb.append("|");
    		}
		}
		return sb.toString();
	}

	public MobileApplication[] getApplication(Vector<String> name) {
		MobileApplication[] apps = new MobileApplication[name.size()];
		for (int i = 0; i < name.size(); i++) {
			try {
    			Class<? extends MobileApplication> c = 
    				Class.forName(PACKAGE_APP+"."+name.elementAt(i)).asSubclass(MobileApplication.class);
    			apps[i] = c.newInstance();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			}
		}
		return apps;
	}
	
	public NetworkInterface[] getNetworkInterface(Vector<String> name) {
		NetworkInterface[] nis = new NetworkInterface[name.size()];
		for (int i = 0; i < name.size(); i++) {
			try {
    			Class<? extends NetworkInterface> c = 
    				Class.forName(PACKAGE_NI+"."+name.elementAt(i)).asSubclass(NetworkInterface.class);
    			nis[i] = c.newInstance();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			}
		}
		return nis;
	}

	public DeviceInfo getDeviceInfo() {
		Vector<String> apps = new Vector<String>();
		Vector<String> nis = new Vector<String>();
		MobileApplication[] m = device.getApplications();
		if (m != null) {
			for (int i = 0; i < m.length; i++) {
				apps.addElement(m[i].getName());
			}
		}
		NetworkInterface[] n = device.getNetworkInterfaces();
		if (n != null) {
			for (int i = 0; i < n.length; i++) {
				nis.addElement(n[i].getName());
			}
		}
		return new DeviceInfo(device.getId(), apps, nis);
	}
	
	public MobileDevice getDevice() {
		return device;
	}
	public void setDevice(MobileDevice device) {
		this.device = device;
	}
	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
		setPath(xpos, ypos);
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
		setPath(xpos, ypos);
	}
	
	public void setLocation(int xpos, int ypos) {
		setXpos(xpos);
		setYpos(ypos);
		setPath(xpos, ypos, false);
	}
	public IMobileNode getMobileNode() {
		return MobileNodeFactory.getInstance().getMobileNode(type);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getCurrentVelocity() {
		return currentVelocity;
	}

	public void setCurrentVelocity(int currentVelocity) {
		this.currentVelocity = currentVelocity;
		device.setCurrentVelocity(currentVelocity);
	}

	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ID: " + device.getId() + "\n");
		buffer.append("IP: ");
		for (int i =0; i < device.getIpAddress().length;i++) {
			buffer.append(device.getIpAddress()[i]+" ");
		}
		buffer.append("\n");
		buffer.append("Location: ("+getXpos()+","+getYpos()+")\n");
		buffer.append("Application: ");
		for (int i = 0; device.getApplications() != null && i < device.getApplications().length; i++) {
			buffer.append("("+device.getApplications()[i]+")");
		}
		for (int i = 0; device.getNetworkInterfaces() != null && i < device.getNetworkInterfaces().length; i++) {
			buffer.append("("+device.getNetworkInterfaces()[i]+")");
		}
		return buffer.toString();
	}

	public String getComment() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ID: " + device.getId() + "\n");
		buffer.append("NodeType: " + getType() + "\n");
		buffer.append("IP: ");
		for (int i =0; i < device.getIpAddress().length;i++) {
			buffer.append(device.getIpAddress()[i]+"\n");
		}
		buffer.append("\n");
		buffer.append("Location: ("+getXpos()+","+getYpos()+")\n");
		buffer.append("Velocity: ("+getCurrentVelocity()+")\n");
		buffer.append("Application:\n");
		for (int i = 0; device.getApplications() != null && i < device.getApplications().length; i++) {
			buffer.append("("+device.getApplications()[i].getName());
			if (device.getApplications()[i].isRunning()) {
				buffer.append("*");
			}
			buffer.append(")");
		}
		buffer.append("\n");
		buffer.append("Network Interfaces:\n");
		for (int i = 0; device.getNetworkInterfaces() != null && i < device.getNetworkInterfaces().length; i++) {
    		Enumeration<CommunicationDevice> networkDevices = device.getNetworkInterfaces()[i].getCandidateNetworkDevices();
			buffer.append(device.getNetworkInterfaces()[i].getName());
			if (networkDevices != null) {
				buffer.append(" - ");
	    		while(networkDevices.hasMoreElements()) {
	    			CommunicationDevice d = networkDevices.nextElement();
	    			NetworkProperty p = device.getNetworkInterfaces()[i].getNetworkProperty(d);
	    			
	    			buffer.append(((NetworkDevice)d).getName());
	    			buffer.append("("+getSignalStrengthStr(p.getSignalStrength())+",");
	    			buffer.append("R:"+getSignalStrengthStr(p.getApavRSS())+",");
	    			buffer.append("C:"+getSignalStrengthStr(p.getApavCost())+",");
	    			buffer.append("Q:"+getSignalStrengthStr(p.getApavQuality())+",");
	    			buffer.append("L:"+getSignalStrengthStr(p.getApavLifetime())+",");
	    			buffer.append("S:"+getSignalStrengthStr(p.getApsv())+")");
	    		}
			}	
			if (device.getNetworkInterfaces()[i].isSelected()) {
				buffer.append("*");
			}
    		buffer.append("\n");
		}
		//	candidate networks
		
		return buffer.toString();
	}
	
	private String getSignalStrengthStr(double d) {
		return String.valueOf((int)(d*100));
	}
	
	public void appendXml(Element root) {
		Document doc = root.getOwnerDocument();
		Element mnodeElem = doc.createElement(MOBILENODE);
		root.appendChild(mnodeElem);

		getDeviceInfo().appendXml(mnodeElem);

		Element xposElem = doc.createElement(XPOS);
		xposElem.appendChild(doc.createTextNode(String.valueOf(xpos)));
		mnodeElem.appendChild(xposElem);
		
		Element yposElem = doc.createElement(YPOS);
		yposElem.appendChild(doc.createTextNode(String.valueOf(ypos)));
		mnodeElem.appendChild(yposElem);
		Element typeElem = doc.createElement(TYPE);
		typeElem.appendChild(doc.createTextNode(getType()));
		mnodeElem.appendChild(typeElem);
		
		String pathStr = getPathStr();
		Element pathElem = doc.createElement(PATH);
		pathElem.appendChild(doc.createTextNode(pathStr));
		mnodeElem.appendChild(pathElem);
    }

	public boolean equals(Object obj) {
		if (obj instanceof NetworkMobileNodeInfo) {
			NetworkMobileNodeInfo info = (NetworkMobileNodeInfo)obj;
			return device.getId().intern() == info.getDevice().getId().intern();
		}
		return false;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(device.getId());
		buffer.append(" (" +type+")");
		return buffer.toString();
	}
	class DeviceInfo {
		String id = null;
		Vector<String> apps;
		Vector<String> nis;
		
		public DeviceInfo() {
		}
		
	    public DeviceInfo(String id, Vector<String> apps, Vector<String> nis) {
	    	setId(id);
			this.apps = apps;
			this.nis = nis;
	    }
	
		public DeviceInfo(Node deviceNode) {
			apps = new Vector<String>();
			nis = new Vector<String>();
			NodeList nodes = deviceNode.getChildNodes();
			for(int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				String name = node.getNodeName();
				Node firstChild = node.getFirstChild();
				String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
	            if(name.intern() == ID.intern()) {
					setId(value);
				}
    			else if (name.intern() == APP.intern()) {
    				apps.addElement(value);
    			}
    			else if (name.intern() == NI.intern()) {
    				nis.addElement(value);
    			}
	    	}
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setApplications(Vector<String> apps) {
			this.apps = apps;
		}
		
		public Vector<String> getApplications() {
			return apps;
		}
		
		public void setNetworkInterfaces(Vector<String> nis) {
			this.nis = nis;
		}
		
		public Vector<String> getNetworkInterfaces() {
			return nis;
		}

		public void appendXml(Element root) {
			Document doc = root.getOwnerDocument();
			Element deviceElem = doc.createElement(DEVICE);
			root.appendChild(deviceElem);

			Element idElem = doc.createElement(ID);
			idElem.appendChild(doc.createTextNode(id));
			deviceElem.appendChild(idElem);

			for (int i = 0; i < apps.size(); i++) {
    			Element appElem = doc.createElement(APP);
    			appElem.appendChild(doc.createTextNode(apps.elementAt(i)));
    			deviceElem.appendChild(appElem);
			}
			for (int i = 0; i < nis.size(); i++) {
    			Element niElem = doc.createElement(NI);
    			niElem.appendChild(doc.createTextNode(nis.elementAt(i)));
    			deviceElem.appendChild(niElem);
			}
		}
	}
	
	/**
	 * Moving
	 */
	
	public void initializeMove() {
		/*
		sX = dX = cX = getPath()[0][0];
		sY = dY = cY = getPath()[0][1];
		*/
		rX = getPath()[0][PATH_XPOS];
		rY = getPath()[0][PATH_YPOS];
		currentVelocity = getPath()[0][PATH_VELOCITY];
		stay = getPath()[0][PATH_STAY];
		pathIndex = 1;
	}
	
	public void move(long durationTime) {
		move(durationTime, 1.0);
	}
	
	public synchronized void move(long durationTime, double speedRate) {
		if (getPath().length == 1) {
			//	no move
			return;
		}
		if (stay > 0) {	//	stay
			stay = stay - (int)durationTime < 0 ? 0 : stay-(int)durationTime;
			return;
		}
		this.duration = durationTime;
		int tX = getPath()[pathIndex][0];
		int tY = getPath()[pathIndex][1];
//		int velocity = getPath()[pathIndex][2];
		
		double distance = java.awt.geom.Point2D.distance(rX, rY, tX, tY) * dpnm.tool.Env.ZOOM;
		double moveDistance = (double)getCurrentVelocity()/3600*speedRate * durationTime;	// m/ms
		if (moveDistance >= distance) { // change next path
			
			if (dpnm.Conf.LOG){
//				loggingPath(pathIndex);
			}
			stay = getPath()[pathIndex][PATH_STAY];
			pathIndex = (pathIndex+1)%getPath().length;
			rX = tX; rY = tY;
			setCurrentVelocity(getPath()[pathIndex][PATH_VELOCITY]);
		} else {
    		rX += (double)(tX-rX) * moveDistance/distance;
    		rY += (double)(tY-rY) * moveDistance/distance;
		}
		setLocation((int)rX, (int)rY);
	}
	
	/*
	public void move2() {
		if (getPath().length == 1) {
			//	no move
			return;
		}
		int c = 0;
		while(true) {
    		if (cX == dX && cY == dY) {
    			pathIndex = (pathIndex+1)%getPath().length;
    			sX = cX = dX;
    			sY = cY = dY;
        		dX = getPath()[pathIndex][0];
        		dY = getPath()[pathIndex][1];
        		c++;
    			count = 0;
    		} else if (c == getPath().length) {
    			return;
    		} else {
    			break;
    		}
		}
		
		timeCount = (timeCount+1)%(MobileNodeFactory.MAX_VELOCITY/getCurrentVelocity());
//		timeCount = 0;
		if (timeCount == 0) {
			//	move current point -> destination point
			//	1. check direction
			count++;
    		//	move current point -> destination point
    		//	1. check direction
    		if (sX == dX) { // vertical
    			if (sY >= dY) {
    				sY--;
    			} else {
    				sY++;
    			}
    		} else if (sY == dY) { //	horizontal
    			if (sX >= dX) {
    				sX--;
    			} else {
    				sX++;
    			}
    		} else if (sX < dX && sY < dY) {	//	1
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX+count;
    				cY = sY+count;
    			} else if (dX - sX > dY - sY) {		//	1-1
    				cX = sX+count;
    				cY = sY+(int)((double)(dY-sY)/(dX-sX)*count);
    			} else {
    				cY = sY+count;
    				cX = sX+(int)((double)(dX-sX)/(dY-sY)*count);
    			}
    		} else if (sX > dX && sY < dY) {	//  2
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX-count;
    				cY = sY+count;
    			} else if (sX - dX > dY - sY) {		//	2-1
    				cX = sX-count;
    				cY = sY+(int)((double)(dY-sY)/(sX-dX)*count);
    			} else {
    				cY = sY+count;
    				cX = sX-(int)((double)(sX-dX)/(dY-sY)*count);
    			}
    		} else if (sX > dX && sY > dY) {	//  3
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX-count;
    				cY = sY-count;
    			} else if (sX - dX > sY - dY) {		//	1-1
    				cX = sX-count;
    				cY = sY-(int)((double)(sY-dY)/(sX-dX)*count);
    			} else {
    				cY = sY-count;
    				cX = sX-(int)((double)(sX-dX)/(sY-dY)*count);
    			}
    		} else if (sX < dX && sY > dY) {	//  4
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX+count;
    				cY = sY-count;
    			} else if (dX - sX > sY - dY) {		//	1-1
    				cX = sX+count;
    				cY = sY-(int)((double)(sY-dY)/(dX-sX)*count);
    			} else {
    				cY = sY-count;
    				cX = sX+(int)((double)(dX-sX)/(sY-dY)*count);
    			}
    		}
			setLocation(cX, cY);
		}
	}
		*/
	
	
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDevice().getId()+" (");
		sb.append(getMobileNode().getName()+")\r\n");
		sb.append("Current Velocity: " + getCurrentVelocity()+"\r\n");
		sb.append("Current Stay Time: " + stay+"\r\n");
		sb.append(getDevice().getStatus());
		return sb.toString();
	}
	
	public void destroy() {
		if (device != null) {
			device.destroy();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//	LOG DATA for ANALYSIS
	//
	//	Timestamp | MN_x | MN_y | Speed | CN List | SCN List filtered by Speed Filter
	//	SSCN List filtered by SLA filter
	//	APAV_RSS of SSCN List | APAV_Cost of SSCN List | APAV_Quality of SSCN List
	//	APAV_Lifetime of SSCN List
	//	APSV of SSCN List
	//	Selected best AP
	public String getLog(long timestamp) {
		if (graphs == null) {
			graphs = new ArrayList<LogGraph>();
		}
		StringBuffer sb = new StringBuffer();
		sb.append(getXpos()+"|");
		sb.append(getYpos()+"|");
		sb.append(getCurrentVelocity()+"|");
	
		StringBuffer speedBuffer = new StringBuffer();
		StringBuffer slaBuffer = new StringBuffer();
		StringBuffer apav[] = new StringBuffer[4];
		for (int i = 0; i < apav.length; i++) {
			apav[i] = new StringBuffer();
		}
		StringBuffer apsv = new StringBuffer();
		String bestAP = null;
		for (int i = 0; device.getNetworkInterfaces() != null && i < device.getNetworkInterfaces().length; i++) {
    		Enumeration<CommunicationDevice> networkDevices = 
    			device.getNetworkInterfaces()[i].getCandidateNetworkDevices();
			if (networkDevices != null) {
				sb.append(device.getNetworkInterfaces()[i].getName());
				boolean flagSpeed = true;
				boolean flagSla = true;
				if (device.getNetworkInterfaces()[i].isSpeedSupport()) {
					speedBuffer.append(device.getNetworkInterfaces()[i].getName());
				} else flagSpeed = false;
				if (device.getNetworkInterfaces()[i].isSpeedSupport()) {
					slaBuffer.append(device.getNetworkInterfaces()[i].getName());
				} else flagSla = false;
				sb.append("(");
				speedBuffer.append("(");
				slaBuffer.append("(");
	    		while(networkDevices.hasMoreElements()) {
	    			CommunicationDevice d = networkDevices.nextElement();
	    			NetworkProperty p = device.getNetworkInterfaces()[i].getNetworkProperty(d);
	    			sb.append(((NetworkDevice)d).getName());
	    			if (flagSpeed)
	    				speedBuffer.append(((NetworkDevice)d).getName()+",");
	    			if (flagSla)
	    				slaBuffer.append(((NetworkDevice)d).getName()+",");

	    			if (flagSpeed && flagSla) {
		    			apav[0].append(String.format("%.3f", p.getApavRSS())+",");
		    			apav[1].append(String.format("%.3f", p.getApavCost())+",");
		    			apav[2].append(String.format("%.3f", p.getApavQuality())+",");
		    			apav[3].append(String.format("%.3f", p.getApavLifetime())+",");
		    			apsv.append(String.format("%.3f", p.getApsv())+",");
	    			}
	    		}
	    		if (sb.charAt(sb.length()-1) == ',')
	    			sb.deleteCharAt(sb.length()-1);
	    		sb.append(") ");
	    		if (flagSpeed && speedBuffer.charAt(speedBuffer.length()-1) == ',')  {
	    			speedBuffer.deleteCharAt(speedBuffer.length()-1);
	    			speedBuffer.append(") ");
	    		}
	    		if (flagSla && slaBuffer.charAt(slaBuffer.length()-1) == ',') {
	    			slaBuffer.deleteCharAt(slaBuffer.length()-1);
	    			slaBuffer.append(") ");
	    		}
    			if (flagSpeed && flagSla) {
    				for (int j = 0; j < 4; j++) {
    					apav[j].deleteCharAt(apav[j].length()-1);
    				}
					apsv.deleteCharAt(apsv.length()-1);
    			}

	    		if (device.getNetworkInterfaces()[i].isSelected()) {
	    			bestAP = ((NetworkDevice)device.getNetworkInterfaces()[i].getSelectedDevice()).getName();
	    			NetworkProperty p = device.getNetworkInterfaces()[i].getNetworkProperty(
	    					device.getNetworkInterfaces()[i].getSelectedDevice());
	    			LogGraph lg = new LogGraph();
	    			lg.setTimestamp(timestamp);
	    			if (p != null) {
//		    			lg.setApavRSS(p.getApavRSS());
//		    			lg.setApavCost(p.getApavCost());
//		    			lg.setApavQuality(p.getApavQuality());
//		    			lg.setApavLifetime(p.getApavLifetime());
		    			lg.setApsv(p.getApsv());
  			
	    			} else {
	    				lg.setApsv(0);
	    			}
	    			graphs.add(lg);

	    		}
			}
		}
		if (sb.charAt(sb.length()-1) == ' ')
			sb.deleteCharAt(sb.length()-1);
		sb.append("|");
		sb.append(speedBuffer.toString().trim()+"|");
		sb.append(slaBuffer.toString().trim()+"|");
		for (int i = 0; i < apav.length; i++)
			sb.append(apav[i].toString()+"|");
		sb.append(apsv.toString()+"|");
		sb.append(bestAP);
		return sb.toString();
	}

	private void loggingPath(int path) {
		if (logs[path] == null) {
			logs[path] = new LogTable();
		}
		logs[path].setSpeed(getCurrentVelocity());

		StringBuffer sb = new StringBuffer();
		StringBuffer speedBuffer = new StringBuffer();
		StringBuffer slaBuffer = new StringBuffer();
		StringBuffer apav[] = new StringBuffer[4];
		for (int i = 0; i < apav.length; i++) {
			apav[i] = new StringBuffer();
		}
		StringBuffer apsv = new StringBuffer();
		String bestAP = null;
		for (int i = 0; device.getNetworkInterfaces() != null && i < device.getNetworkInterfaces().length; i++) {
    		Enumeration<CommunicationDevice> networkDevices = 
    			device.getNetworkInterfaces()[i].getCandidateNetworkDevices();
			if (networkDevices != null) {
				sb.append(device.getNetworkInterfaces()[i].getName());
				boolean flagSpeed = true;
				boolean flagSla = true;
				if (device.getNetworkInterfaces()[i].isSpeedSupport()) {
					speedBuffer.append(device.getNetworkInterfaces()[i].getName());
				} else flagSpeed = false;
				if (device.getNetworkInterfaces()[i].isSpeedSupport()) {
					slaBuffer.append(device.getNetworkInterfaces()[i].getName());
				} else flagSla = false;
				sb.append("(");
				speedBuffer.append("(");
				slaBuffer.append("(");
	    		while(networkDevices.hasMoreElements()) {
	    			CommunicationDevice d = networkDevices.nextElement();
	    			NetworkProperty p = device.getNetworkInterfaces()[i].getNetworkProperty(d);
	    			String name = ((NetworkDevice)d).getName();
	    			sb.append(name+",");
	    			if (flagSpeed)
	    				speedBuffer.append(name+",");
	    			if (flagSla)
	    				slaBuffer.append(name+",");

	    			if (flagSpeed && flagSla) {
		    			apav[0].append(name+"("+String.format("%.3f", p.getApavRSS())+"),");
		    			apav[1].append(name+"("+String.format("%.3f", p.getApavCost())+"),");
		    			apav[2].append(name+"("+String.format("%.3f", p.getApavQuality())+"),");
		    			apav[3].append(name+"("+String.format("%.3f", p.getApavLifetime())+"),");
		    			apsv.append(name+"("+String.format("%.3f", p.getApsv())+"),");
	    			}
	    		}
	    		if (sb.charAt(sb.length()-1) == ',')
	    			sb.deleteCharAt(sb.length()-1);
	    		sb.append(") ");
	    		if (flagSpeed && speedBuffer.charAt(speedBuffer.length()-1) == ',')  {
	    			speedBuffer.deleteCharAt(speedBuffer.length()-1);
	    			speedBuffer.append(") ");
	    		}
	    		if (flagSla && slaBuffer.charAt(slaBuffer.length()-1) == ',') {
	    			slaBuffer.deleteCharAt(slaBuffer.length()-1);
	    			slaBuffer.append(") ");
	    		}
    			if (flagSpeed && flagSla) {
    				for (int j = 0; j < 4; j++) {
    					apav[j].deleteCharAt(apav[j].length()-1);
    				}
					apsv.deleteCharAt(apsv.length()-1);
    			}

	    		if (device.getNetworkInterfaces()[i].isSelected()) {
	    			bestAP = ((NetworkDevice)device.getNetworkInterfaces()[i].getSelectedDevice()).getName();
	    		}
			}
		}
		if (sb.length() > 1 && sb.charAt(sb.length()-1) == ' ')
			sb.deleteCharAt(sb.length()-1);
		logs[path].setAp(sb.toString());
		logs[path].setSpeedAp(speedBuffer.toString().trim());
		logs[path].setSlaAp(slaBuffer.toString().trim());
		String apavS[] = new String[apav.length];
		for (int i = 0; i < apav.length; i++)
			apavS[i] = apav[i].toString();
		logs[path].setApav(apavS);
		logs[path].setApsv(apsv.toString());
		logs[path].setBestAp(bestAP);
		logs[path].setLocation(path);
		logs[path].setX(getPath()[path][0]);
		logs[path].setY(getPath()[path][1]);
		logs[path].setDuration(duration);
	}
	
	public String loggingTable() {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < logs.length; i++) {
			if (logs[i] == null)
				continue;
			sb.append(logs[i].getLocation()+"|");
			sb.append(logs[i].getSpeed()+"|");
			sb.append(logs[i].getDuration()+"|");
			sb.append(logs[i].getX()+","+logs[i].getY()+"|");
			sb.append(logs[i].getAp()+"|");
			sb.append(logs[i].getSpeedAp()+"|");
			sb.append(logs[i].getSlaAp()+"|");
			for (int j = 0; j < 4; j++)
				sb.append(logs[i].getApav()[j]+"|");
			sb.append(logs[i].getApsv()+"|");
			sb.append(logs[i].getBestAp()+"\r\n");
		}
		return sb.toString();
	}
	public String loggingTableWiki() {
		StringBuffer sb = new StringBuffer();
		sb.append("{| style=\"border-color: #000; border-style: solid; border-width: 1px 1px 1px 1px; border-spacing: 0; border-collapse: collapse; margin: 0; padding: 4px;\"\r\n");
		String title[] = {
				"Location in the Map",
				"Speed of "+ getDevice().getId(),
				"Available Access Networks (AP)",
				"Speed Filtering (AP)",						
				"SLA Filtering (AP)",					
				"AP (APAV_RSS)",				
				"AP (APAV_COST)",					
				"AP (APAV_QUALITY)",						
				"AP (APAV_LIFETIME)",						
				"AP (APSV)",						
				"RSS (best AP)",						
				"Cost (best AP)",						
				"Quality (best AP)",						
				"Lifetime (best AP)",						
				"AUHO (best AP)"
		};
		for (int i = 0; i < title.length; i++) {
			sb.append("!style=\"border-width: 0px 1px 1px 1px; border-style: solid;\"|");
		}
		return sb.toString();
	}
	
	public String loggingGraph(String name) {
		StringBuffer sb[] = new StringBuffer[6];

		for (int i = 0; i < 2; i++) {
			sb[i] = new StringBuffer();
		}
		sb[0].append(name+"_X = [");
//		sb[1].append(name+"_APAV_RSS = [");
//		sb[2].append(name+"_APAV_Cost = [");
//		sb[3].append(name+"_APAV_Quality = [");
//		sb[4].append(name+"_APAV_Lifetime = [");
		sb[1].append(name+"_APSV = [");

		for (int i = 0; i < graphs.size(); i++) {
			LogGraph lg = graphs.get(i);
			sb[0].append(lg.getTimestamp()+" ");
//			sb[1].append(lg.getApavRSS()+" ");
//			sb[2].append(lg.getApavCost()+" ");
//			sb[3].append(lg.getApavQuality()+" ");
//			sb[4].append(lg.getApavLifetime()+" ");
			sb[1].append(lg.getApsv()+" ");
		}
		for (int i = 0; i < 2; i++) {
			sb[i].append("];\r\n");
		}
		
		for (int i = 1; i < 2; i++) {
			sb[0].append(sb[i].toString());
		}
		return sb[0].toString();
	}
	
	public void loggingGraphcsv(String name, FileWriter writer) {
		loggingGraphcsv(name, writer, 1);
	}
	public void loggingGraphcsv(String name, FileWriter writer, int filter) {
		try {
//			writer.write("Index,");
			writer.write(name+"_X,");
//			writer.write(name+"_APAV_RSS,");
//			writer.write(name+"_APAV_Cost,");
//			writer.write(name+"_APAV_Quality,");
//			writer.write(name+"_APAV_Lifetime,");
			writer.write(name+"_APSV\r\n");

			for (int i = 0; i < graphs.size(); i++) {
				LogGraph lg = graphs.get(i);
				if ((lg.getTimestamp()/1000) % filter == 0) {
//					writer.write((i)+",");
					writer.write((lg.getTimestamp()/1000)+",");
//					writer.write(lg.getApavRSS()+",");
//					writer.write(lg.getApavCost()+",");
//					writer.write(lg.getApavQuality()+",");
//					writer.write(lg.getApavLifetime()+",");
					writer.write(lg.getApsv()+"\r\n");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void resetData() {
		initializeMove();
		logs = new LogTable[1];
		graphs = new ArrayList<LogGraph>();
		getDevice().getApplicationManager().stop();
	}
}
