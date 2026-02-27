/*
 * @(#)DesComponent.java
 * 
 * Created on 2005. 12. 17
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

import java.awt.*;
import java.awt.image.BufferedImage;


public class DesComponent extends Component {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4786969555808972241L;

	private TextComponent textComponent;
	/**
	* The memory of the canvas image for double buffering.
	*/
	private BufferedImage _offImage = null ;

	Dimension offDimension = null;
	BufferedImage offImage;
	Graphics offGraphics;
	
    Polygon polygon;
    
    public DesComponent() {
        textComponent = new TextComponent();
        setSize(200,100);
        setForeground(new Color(0,0,0,192));
        setBackground(new Color(224,224,255,192));
        polygon = new Polygon();
        polygon.addPoint(0,0);
        polygon.addPoint(20,20);
        polygon.addPoint(40,20);
    }
    
    public void setText(String text) {
        textComponent.setText(text);
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        AlphaComposite blend = AlphaComposite.getInstance(
           AlphaComposite.SRC_OVER, 0.7f);
            g2.setComposite(blend);
        setSize(textComponent.getTextWidth(g2), 
        		textComponent.getTextHeight(g2)*textComponent.getLines()+20);
        g2.setColor(getBackground());
        g2.fillPolygon(polygon);
        g2.fillRect(0,20,getWidth(), getHeight());
        g2.fillRect(0, 20, getWidth() - 3, getHeight() - 3);
        g2.setColor(new Color(100,100,100,192));

        if (textComponent != null) {
            textComponent.setBounds(0, 20, (int)getWidth(), (int)getHeight());
            textComponent.setInsets(new Insets(0,2,0,2));
            textComponent.setForeground(getForeground());
            textComponent.setAlignment(TextComponent.ALIGN_LEFT);
            textComponent.draw(g2);
        }
        g2.fillRect(2, getHeight() - 3, getWidth() - 2, 3);
        g2.fillRect(getWidth() - 3, 22, 3, getHeight() - 25);
    }
}
