package dpnm.tool;

import java.awt.*;

import dpnm.network.*;
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
public class MobileNodeComponent extends Component {
	protected int WIDTH = 48;
	protected int HEIGHT = 48;
	
	protected NetworkMobileNodeInfo nodeInfo = null;
	
	protected Image iconImage = null;
	
	protected TextComponent nameComp = null;
	
	public MobileNodeComponent() {
	}
	

	public NetworkMobileNodeInfo getDeviceInfo() {
		return nodeInfo;
	}
	
	public void setNetworkMobileNodeInfo(NetworkMobileNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
		
		setBounds(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2, WIDTH, HEIGHT+20);
		
		nameComp = new TextComponent();
        nameComp.setSize(WIDTH, 20);
	    nameComp.setLocation(0, HEIGHT);
	    nameComp.setInsets(new Insets(0,2,0,2));
	    nameComp.setFont(Resources.ARIAL_12);
	    nameComp.setAlignment(TextComponent.ALIGN_CENTER);
    }
	
	public void setIcon(Image image) {
		this.iconImage = image;
	}
	
	/*
	public void setLocation(int x, int y) {
		nodeInfo.setXpos(x);
		nodeInfo.setYpos(y);
		super.setLocation(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2);
	}
	*/
	
	public void paint(Graphics g) {
		setLocation(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2);
		super.paint(g);
		g.setColor(Color.black);
		
		g.drawImage(nodeInfo.getMobileNode().getIcon(),
				0, 0, WIDTH, HEIGHT, this);
		
		if (nameComp != null) {
    		nameComp.setText(nodeInfo.getDevice().getId());
    		nameComp.draw((Graphics2D)g);
		}
	}
	
	public boolean isIn(int x, int y) {
		setLocation(nodeInfo.getXpos()-WIDTH/2, nodeInfo.getYpos()-HEIGHT/2);
		Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight()-20);
		return r.contains(x,y);
	}
}
