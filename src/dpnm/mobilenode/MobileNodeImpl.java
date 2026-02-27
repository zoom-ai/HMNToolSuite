package dpnm.mobilenode;

import java.awt.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MobileNodeImpl implements IMobileNode {
    public static final String MOBILENODE     	= "mobilenode";
    public static final String NAME        		= "name";
    public static final String ICON        		= "icon";
    public static final String MINVELOCITY     	= "minVelocity";
    public static final String MAXVELOCITY     	= "maxVelocity";
    public static final String COLOR       		= "color";
    
	private String name = null;
	private Image icon = null;
	private String iconStr = null;
	private int color = 0;
	private int maxVelocity = 0;
	private int minVelocity = 0;
	
	public MobileNodeImpl() {
	}
	
	public MobileNodeImpl(Node mNode) {
		NodeList nodes = mNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == NAME.intern()) {
            	this.name = value;
			}
            else if(name.intern() == ICON.intern()) {
            	this.iconStr = value;
			}
            else if(name.intern() == COLOR.intern()) {
            	this.color = Integer.parseInt(value, 16);
			}
            else if(name.intern() == MINVELOCITY.intern()) {
            	this.minVelocity = Integer.parseInt(value);
			}
            else if(name.intern() == MAXVELOCITY.intern()) {
            	this.maxVelocity = Integer.parseInt(value);
			}
		}
	}
	
	public void appendXml(Element root) {
        Document doc = root.getOwnerDocument();
		Element mobileNodeElem = doc.createElement(MOBILENODE);
		root.appendChild(mobileNodeElem);
		
		String values[][] = {
				{NAME, name},
				{ICON, iconStr},
    			{MINVELOCITY, String.valueOf(minVelocity)},
				{MAXVELOCITY, String.valueOf(maxVelocity)},
				{COLOR, Integer.toHexString(color)}
		};
 	
		for (int i = 0; i < values.length; i++) {
    		Element elem = doc.createElement(values[i][0]);
    		elem.appendChild(doc.createTextNode(values[i][1]));
    		mobileNodeElem.appendChild(elem);
		}
	}
	public int getColor() {
		return color;
	}

	public Image getIcon() {
		return icon;
	}

	public String getIconStr() {
		return iconStr;
	}
	
	public int getMaxVelocity() {
		return maxVelocity;
	}

	public int getMinVelocity() {
		return minVelocity;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public void setIconStr(String iconStr) {
		this.iconStr = iconStr;
	}
	public void setColor(int color) {
		this.color = color;
	}

	public void setMaxVelocity(int maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public void setMinVelocity(int minVelocity) {
		this.minVelocity = minVelocity;
	}
	
	public String toString() {
		return name;
	}
}
