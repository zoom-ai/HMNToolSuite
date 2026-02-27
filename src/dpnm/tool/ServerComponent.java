package dpnm.tool;

import java.awt.*;

import dpnm.server.*;
import dpnm.tool.data.ServerInfo;
import dpnm.tool.comp.TextComponent;

public class ServerComponent extends Component {
	protected int WIDTH = 48;
	protected int HEIGHT = 48;
	
	protected ServerInfo serverInfo = null;
	
	protected Image iconImage = null;
	
	protected TextComponent nameComp = null;
	
	public ServerComponent() {
	}
	

	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	
	public void setServerInfo(ServerInfo nodeInfo) {
		this.serverInfo = nodeInfo;
		
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
		setLocation(serverInfo.getXpos()-WIDTH/2, serverInfo.getYpos()-HEIGHT/2);
		super.paint(g);
		g.setColor(Color.black);
		
		g.drawImage(iconImage,
				0, 0, WIDTH, HEIGHT, this);
		
		if (nameComp != null) {
    		nameComp.setText(serverInfo.getServer().getId());
    		nameComp.draw((Graphics2D)g);
		}
	}
	
	public boolean isIn(int x, int y) {
		setLocation(serverInfo.getXpos()-WIDTH/2, serverInfo.getYpos()-HEIGHT/2);
		Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight()-20);
		return r.contains(x,y);
	}
}
