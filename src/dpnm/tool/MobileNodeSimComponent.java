package dpnm.tool;

import java.awt.*;

import dpnm.network.*;
import dpnm.mobilenode.*;
import dpnm.tool.data.NetworkMobileNodeInfo;
import dpnm.tool.comp.TextComponent;

/**
 * This component is for displaying mobile node
 * This component is static (if network mobility is not possible)
 * 
 * Network component (icon)
 * Coverage (Circle)
 * Network (Color)
 * 
 * 
 * @author Eliot Kang
 *
 */
public class MobileNodeSimComponent extends MobileNodeComponent {
	private boolean isIconMode = false;
	
	public MobileNodeSimComponent() {
		WIDTH = 8;
		HEIGHT = 8;
	}
	
	public MobileNodeSimComponent(boolean isIconMode) {
		this.isIconMode = isIconMode;
		if (!isIconMode) {
    		WIDTH = 8;
    		HEIGHT = 8;
		}
	}
	

	/*
	public NetworkMobileNodeInfo getDeviceInfo() {
		return nodeInfo;
	}
	*/
	
	public void setNetworkMobileNodeInfo(NetworkMobileNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
		
		setBounds(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2, WIDTH, HEIGHT);
    }

	public boolean isIn(int x, int y) {
		setLocation(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2);
		Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
		return r.contains(x,y);
	}
	
	
	public void paint(Graphics g) {
		if (isIconMode) {
			super.paint(g);
		} else {
    		setLocation(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2);
    		g.setColor(new Color(nodeInfo.getMobileNode().getColor()));
    		g.fillRect(0,0,WIDTH, HEIGHT);
		}
	}
}
