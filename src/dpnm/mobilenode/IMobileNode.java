package dpnm.mobilenode;

import java.awt.*;

public interface IMobileNode {
	public String getName();
	public Image getIcon();
	public String getIconStr();
	public int getColor();
	public int getMaxVelocity();
	public int getMinVelocity();
}
