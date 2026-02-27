package dpnm.tool;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class NetworkMapView extends JPanel {
	/**
	* The memory of the canvas image for double buffering.
	*/
	private BufferedImage _offImage = null ;

	Dimension offDimension = null;
	BufferedImage offImage;
	Graphics offGraphics;
	
	private double zoom = 1.0;

	public NetworkMapView() {
		
	}
	
	double getZoom() {
		return zoom;
	}

	void setZoom(double zoom) {
		this.zoom = zoom;
	}

	@Override
	public void paint(Graphics g) {
		
	    Dimension d = getSize();
	    
	    // Create the offscreen graphics context
	    if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
	        offDimension = d;
	        offImage = (BufferedImage)createImage(d.width, d.height);
	        offGraphics = offImage.getGraphics();
	    }
	    
	    // Erase the previous image
	    offGraphics.setColor(getBackground());
	    offGraphics.fillRect(0,0, d.width, d.height);
	    
	    Graphics2D offg2 = (Graphics2D)offImage.getGraphics();
		
	    offg2.scale(zoom, zoom);
		super.paint(offg2);
		
	    g.drawImage(offImage, 0, 0, this);	
	}
}