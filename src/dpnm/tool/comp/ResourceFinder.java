/*
 * @(#)ResourceFinder.java
 * 
 * Created on 2005. 5. 29
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.tool.comp;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;

import dpnm.tool.Resources;

import java.awt.Image;
import java.awt.image.*;
import javax.imageio.ImageIO;

/**
 * Class Instruction
 *
 * @author Eric Kang
 */
public class ResourceFinder {
	public ImageIcon getIcon(String iconName) {
		return new ImageIcon(Resources.HOME+File.separator+iconName);
	}
	
	public Image getImage(String iconName) {
		ImageIcon imageIcon = getIcon(iconName);
		return imageIcon.getImage();
	}
	
	public URL getURL(String iconName) {
		File f = new File(Resources.HOME+File.separator+iconName);
		URL url = null;
		try {
			url = f.toURL();
		} catch (Exception ex) {
		}
		return url;
	}
	
	public BufferedImage getBufferedImage(String iconName) {
		try {
    		File f = new File(Resources.HOME+File.separator+iconName);
    		BufferedImage img = ImageIO.read(f);
    		return img;
		} catch (Exception e){
			return null;
		}
	}
}
