package dpnm.tool;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Vector;

import dpnm.network.*;
import dpnm.network.device.ConnectedMobileNode;
import dpnm.tool.data.NetworkDeviceInfo;
import dpnm.tool.comp.TextComponent;

/**
 * This component is for displaying network area.
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
public class NetworkComponent extends Component {
	/* Constants */
	private static final BasicStroke N_WIDTH = new BasicStroke(3.0f);
	private static final BasicStroke C_WIDTH = new BasicStroke(2.0f);
	
	protected NetworkDeviceInfo deviceInfo = null;
	
	protected int radius = 0;
	
	protected int icon = 0;
	protected Image iconImage = null;
	
	protected TextComponent nameComp = null;
	protected TextComponent descComp = null;
	
	protected boolean isSim = false;
	
	private MobileNodeComponentManager comManager = null;;
	
	public NetworkComponent() {
		this(null);
	}
	
	public NetworkComponent(MobileNodeComponentManager comManager) {
		this.comManager = comManager;
	}

	public NetworkDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	
	public void setSim(boolean isSim) {
		this.isSim = isSim;
	}

	public void setDeviceInfo(NetworkDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
				
		radius = deviceInfo.getDevice().getNetwork().getCoverage() / Env.ZOOM;
		setBounds(deviceInfo.getXpos()-radius, deviceInfo.getYpos()-radius, 2 * radius+1, 2* radius+1);
		
		nameComp = new TextComponent();
        nameComp.setSize(radius*2, 40);
	    nameComp.setLocation(0,radius+40);
	    nameComp.setInsets(new Insets(0,2,0,2));
	    nameComp.setFont(Resources.ARIAL_14);
	    nameComp.setAlignment(TextComponent.ALIGN_CENTER);
	    
		descComp = new TextComponent();
        descComp.setSize(radius*2, 40);
	    descComp.setLocation(0,radius+60);
	    descComp.setInsets(new Insets(0,2,0,2));
	    descComp.setFont(Resources.ARIAL_12);
	    descComp.setAlignment(TextComponent.ALIGN_CENTER);
	}
	
	public void setIcon(Image image, int icon) {
		this.icon = icon;
		this.iconImage = image;
	}
	
	/*
	public void setLocation(int x, int y) {
		deviceInfo.setXpos(x);
		deviceInfo.setYpos(y);
		super.setLocation(deviceInfo.getXpos()-radius, deviceInfo.getYpos()-radius);
	}
	*/
	
	public void paint(Graphics g) {
		setLocation(deviceInfo.getXpos()-radius, deviceInfo.getYpos()-radius);
		super.paint(g);
		g.setColor(new Color(deviceInfo.getDevice().getNetwork().getColor()));
		
		//	draw image
		if (icon == 1) {
    		g.drawImage(iconImage, radius-12, radius-30, 24, 59, this);
		} else {
    		g.drawImage(iconImage, radius-15, radius-31, 30, 62, this);
		}
		nameComp.setText(deviceInfo.getDevice().getName());
//		nameComp.setForeground(Color.black);
		nameComp.draw((Graphics2D)g);
		
		descComp.setText(deviceInfo.getDevice().getNetwork().getName());
//		descComp.setForeground(Color.black);
		descComp.draw((Graphics2D)g);
		
		Graphics2D g2 = (Graphics2D)g;
		if (!isSim) {
    		g2.setStroke(N_WIDTH);
		}
		g.drawOval(0, 0, radius*2, radius*2);
		
		//	draw connected line
		if (!isSim) {
    		g2.setStroke(C_WIDTH);
		}
		if (comManager != null) {
    		Vector<ConnectedMobileNode> nodes = deviceInfo.getDevice().getConnectedMobileNodes();
    		if (nodes != null) {
    			for (int j = 0; j < nodes.size(); j++) {
        			ConnectedMobileNode node = nodes.elementAt(j);
        			MobileNodeComponent com = comManager.getMobileNodeComponent(node.getHost().getId());
        			if (com != null) {
        				//	1. check distance between network device and mobile device
        				//	2. if it is more than radius, do not draw
        				double d = Point2D.distance(radius, radius, 
        						com.getDeviceInfo().getXpos()-getX(), com.getDeviceInfo().getYpos() - getY());
        				if (d <= radius) {
            				g2.drawLine(radius, radius,
            						com.getDeviceInfo().getXpos() - getX(), com.getDeviceInfo().getYpos() - getY());
        				}
        			}
        		}
    		}		
		}
	}

    public boolean isIn(int x, int y) {
		setLocation(deviceInfo.getXpos()-radius, deviceInfo.getYpos()-radius);
    	Rectangle r = null;
    	if (icon == 1) {
            r = new Rectangle(getX()+radius-15, getY()+radius-31, 30, 62);
		} else {
            r = new Rectangle(getX()+radius-12, getY()+radius-30, 24, 59);
		}
        return r.contains(x, y);
    }
}
