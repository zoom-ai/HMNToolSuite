package dpnm.tool.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dpnm.network.device.ConnectedMobileNode;

import java.io.IOException;

import java.util.Vector;

public class NetworkMapInfo {
    /*
     * constant information
     */
    public static final String MAP        = "map";
    public static final String NAME       = "name";
    public static final String DESCR      = "description";
    public static final String WIDTH      = "width";
    public static final String HEIGHT     = "height";
    public static final String BACKGROUND = "background";
    public static final String DEVICE     = "device";
    public static final String MOBILENODE = "mobilenode";
    public static final String SERVER	  = "servernode";

	private String name = "";
	private String descr = "";
	private int width;
	private int height;
	private String background ="";
	
	private Vector<NetworkDeviceInfo> devices;
	private Vector<NetworkMobileNodeInfo> mnodes;
	private Vector<ServerInfo> snodes;
	
	public NetworkMapInfo() {
		devices = new Vector<NetworkDeviceInfo>();
		mnodes = new Vector<NetworkMobileNodeInfo>();
		snodes = new Vector<ServerInfo>();
	}
	
	public NetworkMapInfo(String name, String descr, int width, int height, String background) {
		this();
		setName(name);
		setDescr(descr);
		setWidth(width);
		setHeight(height);
		setBackground(background);
	}
	
	public NetworkMapInfo(Node mapNode) {
		this();
		NodeList nodes = mapNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == NAME.intern()) {
				setName(value);
			}
            else if(name.intern() == DESCR.intern()) {
				setDescr(value);
			}
            else if(name.intern() == WIDTH.intern()) {
				setWidth(Integer.parseInt(value));
			}
            else if(name.intern() == HEIGHT.intern()) {
				setHeight(Integer.parseInt(value));
			}
            else if(name.intern() == BACKGROUND.intern()) {
				setBackground(value == null ? "" : value);
			}
            else if(name.intern() == DEVICE.intern()) {
				devices.addElement(new NetworkDeviceInfo(node));
			}
            else if(name.intern() == MOBILENODE.intern()) {
				mnodes.addElement(new NetworkMobileNodeInfo(node));
			}
            else if(name.intern() == SERVER.intern()) {
				snodes.addElement(new ServerInfo(node));
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}
	
	public void addNetworkDeviceInfo(NetworkDeviceInfo device) {
		devices.addElement(device);
	}

	public void removeNetworkDeviceInfo(NetworkDeviceInfo device) {
		devices.removeElement(device);
	}

	public void addNetworkMobileNodeInfo(NetworkMobileNodeInfo mnode) {
		mnodes.addElement(mnode);
	}

	public void removeNetworkMobileNodeInfo(NetworkMobileNodeInfo mnode) {
		mnode.destroy();
		mnodes.removeElement(mnode);
	}

	public void addServerInfo(ServerInfo snode) {
		snodes.addElement(snode);
	}

	public void removeServerInfo(ServerInfo snode) {
		snodes.removeElement(snode);
	}

	public NetworkDeviceInfo[] getDevices() {
		return (NetworkDeviceInfo[])devices.toArray(new NetworkDeviceInfo[0]);
	}

	public void setDevices(NetworkDeviceInfo[] devices) {
		this.devices.removeAllElements();
		for (int i = 0; i < devices.length; i++) {
			this.devices.addElement(devices[i]);
		}
	}

	public NetworkMobileNodeInfo[] getMnodes() {
		return (NetworkMobileNodeInfo[])mnodes.toArray(new NetworkMobileNodeInfo[0]);
	}

	public void setMnodes(NetworkMobileNodeInfo[] mnodes) {
		this.mnodes.removeAllElements();
		for (int i = 0; i < mnodes.length; i++) {
			this.mnodes.addElement(mnodes[i]);
		}
	}

	public ServerInfo[] getServers() {
		return (ServerInfo[])snodes.toArray(new ServerInfo[0]);
	}

	public void setServers(NetworkMobileNodeInfo[] snodes) {
		this.mnodes.removeAllElements();
		for (int i = 0; i < snodes.length; i++) {
			this.mnodes.addElement(snodes[i]);
		}
	}
	
	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name: " + getName() + "\n");
		buffer.append("Description: " + getDescr() + "\n");
		buffer.append("width: " + getWidth() + "\n");
		buffer.append("height: " + getHeight() + "\n");
		
		for (int i = 0; i < devices.size(); i++) {
			buffer.append(devices.elementAt(i).getInfo());
		}
		for (int i = 0; i < mnodes.size(); i++) {
			buffer.append(mnodes.elementAt(i).getInfo());
		}
		for (int i = 0; i < snodes.size(); i++) {
			buffer.append(snodes.elementAt(i).getInfo());
		}
		return buffer.toString();
	}
	
	public void appendXml(Element root) {
        Document doc = root.getOwnerDocument();
		Element mapElem = doc.createElement(MAP);
		root.appendChild(mapElem);
		Element nameElem = doc.createElement(NAME);
		nameElem.appendChild(doc.createTextNode(name));
		mapElem.appendChild(nameElem);
		Element descrElem = doc.createElement(DESCR);
		descrElem.appendChild(doc.createTextNode(descr));
		mapElem.appendChild(descrElem);
		Element widthElem = doc.createElement(WIDTH);
		widthElem.appendChild(doc.createTextNode(String.valueOf(width)));
		mapElem.appendChild(widthElem);
		Element heightElem = doc.createElement(HEIGHT);
		heightElem.appendChild(doc.createTextNode(String.valueOf(height)));
		mapElem.appendChild(heightElem);
		Element backgroundElem = doc.createElement(BACKGROUND);
		backgroundElem.appendChild(doc.createTextNode(background));
		mapElem.appendChild(backgroundElem);

		for (int i = 0; i < devices.size(); i++) {
			NetworkDeviceInfo device = devices.elementAt(i);
			device.appendXml(mapElem);
		}
	
		for (int i = 0; i < mnodes.size(); i++) {
			NetworkMobileNodeInfo mnode = (NetworkMobileNodeInfo) mnodes.elementAt(i);
			mnode.appendXml(mapElem);
		}
		for (int i = 0; i < snodes.size(); i++) {
			ServerInfo snode = (ServerInfo) snodes.elementAt(i);
			snode.appendXml(mapElem);
		}
	}
	
	public String getMobleNodeStatus() {
		StringBuffer sb = new StringBuffer();
		NetworkMobileNodeInfo mNodeInfo[] = getMnodes();
		for (int i = 0; mNodeInfo != null && i < mNodeInfo.length; i++) {
			sb.append(mNodeInfo[i].getStatus());
			sb.append("---------------------------------------------\r\n");
		}
		return sb.toString();
	}	
	
	public String getNetworkStatus() {
		StringBuffer sb = new StringBuffer();
		NetworkDeviceInfo deviceInfo[] = getDevices();
		for (int i = 0; deviceInfo != null && i < deviceInfo.length; i++) {
			sb.append(deviceInfo[i].getStatus());
			sb.append("---------------------------------------------\r\n");
    	}
		return sb.toString();
	}	
	
	public void resetData() {
		NetworkDeviceInfo[] deviceInfo = getDevices();
		NetworkMobileNodeInfo[] nodeInfo = getMnodes();
		
		for (int i = 0; i < deviceInfo.length; i++) {
			deviceInfo[i].resetData();
		}
		for (int i = 0 ; i < nodeInfo.length; i++) {
			nodeInfo[i].resetData();
		}
		
	}
}

