package dpnm.tool.data;

import dpnm.server.*;
import dpnm.tool.data.NetworkDeviceInfo.DeviceInfo;

import org.w3c.dom.*;

import java.util.*;

public class ServerInfo {
    /** device */
    public static final String SERVERNODE = "servernode";
    public static final String SERVER     = "server";
    public static final String ID         = "id";
    public static final String TYPE       = "type";
    public static final String XPOS       = "xpos";
    public static final String YPOS       = "ypos";
    
    private Server server = null;
	private int xpos = 0;
	private int ypos = 0;
	private int type = 0;
	
	public ServerInfo() {
		
	}
	
    public ServerInfo(String id, int type, int xpos, int ypos) {
    	setServer(id, type);
    	setType(type);
    	setXpos(xpos);
        setYpos(ypos);
    }
    
    
	public ServerInfo(Node serverNode) {
		NodeList nodes = serverNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == SERVER.intern()) {
				setServer(node);
			}
			else if(name.intern() == XPOS.intern()) {
				setXpos(Integer.parseInt(value));
			}
			else if(name.intern() == YPOS.intern()) {
				setYpos(Integer.parseInt(value));
			}
			else if(name.intern() == TYPE.intern()) {
				setType(Integer.parseInt(value));
			}
    	}
	}

	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}

	private void setServer(Node serverNode) {
		NodeList nodes = serverNode.getChildNodes();
		String id = null;
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == ID.intern()) {
            	id = value;
            }
			else if(name.intern() == TYPE.intern()) {
				setType(Integer.parseInt(value));
			}
    	}
		setServer(id, type);
	}

	private void setServer(String id, int type) {
		switch(type) {
		case Server.APPLICATION:
			server = new ApplicationServer(id);
			 break;
		case Server.CONTEXT:
			server = new ContextServer(id);
			 break;
		case Server.OSS:
			server = new OSS(id);
			 break;
		case Server.LOCATION:
			server = new LocationServer(id);
			 break;
		}
	}

	public void setServer(Server server) {
		this.server = server;
	}
	
	public Server getServer() {
		return server;
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
	
	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Server: " + server.getServerInfo() + "\n");
		buffer.append("xpos: " + getXpos() + "\n");
		buffer.append("ypos: " + getYpos() + "\n");

		return buffer.toString();
	}
	
	public String getComment() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name: " + server.getId() + "\n");
		buffer.append("IP: " + server.getIpAddress()+"\n");
		buffer.append("MAC: " + server.getMacAddress()+"\n");
		buffer.append("Location: ("+getXpos()+","+getYpos()+")\n");
		
		//	context server
		if ( server.getType() == Server.CONTEXT) {
			ContextServer cs = (ContextServer)server;
			if (cs.getManagedNetworkDevices() != null) {
				buffer.append("Managed Devices: ");
				for (int i = 0; i < cs.getManagedNetworkDevices().length; i++) {
					buffer.append(cs.getManagedNetworkDevices()[i].getDevice().getName()+" ");
				}
			}
		}
		return buffer.toString();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof ServerInfo) {
			ServerInfo info = (ServerInfo)obj;
			return server.getMacAddress().intern() == info.getServer().getMacAddress().intern();
		}
		return false;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(server.getId());
		return buffer.toString();
	}
	
	public void appendXml(Element root) {
		Document doc = root.getOwnerDocument();
		Element serverNodeElem = doc.createElement(SERVERNODE);
		root.appendChild(serverNodeElem);

		Element serverElem = doc.createElement(SERVER);
		serverNodeElem.appendChild(serverElem);

		Element idElem = doc.createElement(ID);
		idElem.appendChild(doc.createTextNode(server.getId()));
		serverElem.appendChild(idElem);
		Element typeElem = doc.createElement(TYPE);
 		typeElem.appendChild(doc.createTextNode(String.valueOf(type)));
 		serverElem.appendChild(typeElem);
 		
		Element xposElem = doc.createElement(XPOS);
		xposElem.appendChild(doc.createTextNode(String.valueOf(xpos)));
		serverNodeElem.appendChild(xposElem);
		Element yposElem = doc.createElement(YPOS);
		yposElem.appendChild(doc.createTextNode(String.valueOf(ypos)));
		serverNodeElem.appendChild(yposElem);
	}
}
