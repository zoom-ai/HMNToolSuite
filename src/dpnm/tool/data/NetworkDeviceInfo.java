package dpnm.tool.data;

import java.util.Vector;

import dpnm.network.*;
import dpnm.network.device.*;

import org.w3c.dom.*;

public class NetworkDeviceInfo {
    /** device */
    public static final String DEVICE     = "device";
    public static final String NETWORK    = "network";
    public static final String NAME       = "name";
    public static final String TYPE       = "type";
    public static final String NTYPE      = "networktype";
    public static final String XPOS       = "xpos";
    public static final String YPOS       = "ypos";
    public static final String DATA		  = "data";

	private static final String PACKAGE_DEVICE = "dpnm.network.device";

	private NetworkDevice device;
	private int xpos;
	private int ypos;
	private int icon;
	
	//	data for Network Device
	private NetworkData data;
	
	public NetworkDeviceInfo() {
		
	}
	
    public NetworkDeviceInfo(String name, String ntype, int xpos, int ypos) {
    	setDevice(new DeviceInfo(name, ntype));
        setXpos(xpos);
        setYpos(ypos);
        setIcon(device.getType());
        data = new NetworkData(getDevice().getNetwork());
    }
    
    
	public NetworkDeviceInfo(Node deviceNode) {
		NodeList nodes = deviceNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == NETWORK.intern()) {
				setDevice(new DeviceInfo(node));
			}
			else if(name.intern() == XPOS.intern()) {
				setXpos(Integer.parseInt(value));
			}
			else if(name.intern() == YPOS.intern()) {
				setYpos(Integer.parseInt(value));
			} 
			else if (name.intern() == DATA.intern()) {
				data = new NetworkData(node);
				data.setNetwork(getDevice().getNetwork());
			}
    	}
		if (data == null) {
	        data = new NetworkData(getDevice().getNetwork());
		}
		setIcon(getDevice().getType());
	}

	public NetworkDevice getDevice() {
		return device;
	}

	public void setDevice(NetworkDevice device) {
		this.device = device;
	}

	public void setDevice(DeviceInfo info) {
		INetwork network = NetworkFactory.getInstance().getNetwork(info.getNtype());
		try {
			Class<? extends NetworkDevice> c = 
				Class.forName(PACKAGE_DEVICE+"."+
						network.getDeviceType()).asSubclass(NetworkDevice.class);
			device = c.newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		device.setName(info.getName());
		device.setNetworkStr(info.getNtype());
	}

	public DeviceInfo getDeviceInfo() {
		return new DeviceInfo(device.getName(), getNtype());
	}
	
	/*
	private int getType() {
		if (device instanceof BaseStation) return NetworkDevice.BASE_STATION;
		if (device instanceof AccessPoint) return NetworkDevice.ACCESS_POINT;
		if (device instanceof RadioAccessStation) return NetworkDevice.RADIO_ACCESS_STATION;
		return 0;
	}
	*/

	private String getNtype() {
		return device.getNetwork().getName();
	}

	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	
	public void setLocation(int xpos, int ypos) {
		setXpos(xpos);
		setYpos(ypos);
	}
	
	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public NetworkData getData() {
		return data;
	}
	
	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DeviceInfo: " + device.getDeviceInfo() + "\n");
		buffer.append("xpos: " + getXpos() + "\n");
		buffer.append("ypos: " + getYpos() + "\n");
		buffer.append(data.getInfo());
		return buffer.toString();
	}
	
	public String getComment() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name: " + device.getName() + "\n");
		buffer.append("MAC: " + device.getMacAddress()+"\n");
		buffer.append("IP: " + device.getIpAddress()+"\n");
		buffer.append("Location: ("+getXpos()+","+getYpos()+")\n");
		buffer.append(data.getInfo());
		buffer.append("Connected Nodes: ");
		Vector<ConnectedMobileNode> nodes = getDevice().getConnectedMobileNodes();
		if (nodes != null) {
			buffer.append("\n");
			for (int j = 0; j < nodes.size(); j++) {
    			ConnectedMobileNode node = nodes.elementAt(j);
    			buffer.append(node.getHost().getId() + " (");
    			buffer.append(node.getDevice().getIpAddress());
    			buffer.append(")\n");
    		}
		}	
		return buffer.toString();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof NetworkDeviceInfo) {
			NetworkDeviceInfo info = (NetworkDeviceInfo)obj;
			return device.getMacAddress().intern() == info.getDevice().getMacAddress().intern();
		}
		return false;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(device.getName());
		buffer.append(" (" + device.getNetwork().getName()+")");
		return buffer.toString();
	}
	
	public void appendXml(Element root) {
		Document doc = root.getOwnerDocument();
		Element deviceElem = doc.createElement(DEVICE);
		root.appendChild(deviceElem);

		getDeviceInfo().appendXml(deviceElem);

		Element xposElem = doc.createElement(XPOS);
		xposElem.appendChild(doc.createTextNode(String.valueOf(xpos)));
		deviceElem.appendChild(xposElem);
		Element yposElem = doc.createElement(YPOS);
		yposElem.appendChild(doc.createTextNode(String.valueOf(ypos)));
		deviceElem.appendChild(yposElem);
		Element dataElem = doc.createElement(DATA);
		data.appendXml(dataElem);
		deviceElem.appendChild(dataElem);
	}
	
	class DeviceInfo {
		String name = null;
		String ntype = null;
		
		public DeviceInfo() {
		}
		
	    public DeviceInfo(String name, String ntype) {
	    	setName(name);
	    	setNtype(ntype);
	    }
	    
		public DeviceInfo(Node deviceNode) {
			NodeList nodes = deviceNode.getChildNodes();
			for(int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				String name = node.getNodeName();
				Node firstChild = node.getFirstChild();
				String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
				if(name.intern() == NAME.intern()) {
					setName(value);
				}
				else if(name.intern() == NTYPE.intern()) {
					setNtype(value);
				}
	    	}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNtype() {
			return ntype;
		}

		public void setNtype(String ntype) {
			this.ntype = ntype;
		}

		public void appendXml(Element root) {
			Document doc = root.getOwnerDocument();
			Element networkElem = doc.createElement(NETWORK);
			root.appendChild(networkElem);
    		Element nameElem = doc.createElement(NAME);
			nameElem.appendChild(doc.createTextNode(name));
			networkElem.appendChild(nameElem);
			Element ntypeElem = doc.createElement(NTYPE);
			ntypeElem.appendChild(doc.createTextNode(ntype));
			networkElem.appendChild(ntypeElem);
		}
	}
	
	public String getStatus() {
		return getDevice().getStatus();
	}
	
	public void resetData() {
		getData().setCurrentTime(0);
	}
}
