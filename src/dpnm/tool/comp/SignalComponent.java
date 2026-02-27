package dpnm.tool.comp;

import java.awt.*;

import dpnm.tool.Resources;

public class SignalComponent extends Component {
	private TextComponent textComponent;
	
	private static final Color signalColor2[] = {
		new Color(0,235,235),
		new Color(0,163,247),
		new Color(0,0,247),
		new Color(0,255,0),
		new Color(0,199,0),
		new Color(0,143,0),
		new Color(150,150,0),
		new Color(255,255,0),
		new Color(255,143,0),
        new Color(255,0,0),
	};
	private static final Color signalColor[] = {
		new Color(0,0,255),
		new Color(51,102,255),
		new Color(51,204,255),
		new Color(0,255,0),
		new Color(82,255,0),
		new Color(164,255,0),
		new Color(255,255,0),
		new Color(255,164,0),
		new Color(255,82,0),
        new Color(255,0,0),
	};
	
	int signalStrength = 0;
	
    public SignalComponent() {
        setSize(104,20);
        textComponent = new TextComponent();
    	textComponent.setAlignment(TextComponent.ALIGN_CENTER);
        textComponent.setBounds(0, 0, 102, 20);
        textComponent.setFont(Resources.ARIAL_12);
    }
    
    public SignalComponent(int signalStrength) {
    	this();
    	setSignalStrength(signalStrength);
    }
    
    public void setSignalStrength(int signalStrength) {
    	this.signalStrength = signalStrength;
    	textComponent.setText(String.valueOf(signalStrength));
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        /*
        setSize(textComponent.getTextWidth(g2), 
        		textComponent.getTextHeight(g2)*textComponent.getLines()+20);
        g2.setColor(getBackground());
        g2.fillPolygon(polygon);
        g2.fillRect(0,20,getWidth(), getHeight());
        g2.fillRect(0, 20, getWidth() - 3, getHeight() - 3);
        g2.setColor(new Color(100,100,100,192));
        */

//        g2.setColor(Color.green);
//        g2.fill3DRect(1, 3, signalStrength, 14, true);
        drawSignal(g2);
        g2.setColor(Color.black);
        g2.draw3DRect(0, 3, 102, 14, false);
        
        textComponent.setForeground(getForeground());
        textComponent.draw(g2);
        
        /*
		g2.setStroke(new BasicStroke(1));
		g.drawRect(0, 20, getWidth()-3, getHeight()-3);
		*/
    }
    
    private void drawSignal(Graphics2D g2) {
        AlphaComposite blend = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.7f);
        g2.setComposite(blend);
    	for (int i = 0; i < signalStrength; i = i+5) {
    		/*
    		int r = (int)((double)255 * i)/100;
    		int b = 255 - (int)((double)255 * i)/100;
    		g2.setColor(new Color(r, 0, b));
    		*/
    		int h = (int)((double)14 * i)/100;
    		g2.setColor(signalColor[i/10]);
//    		g2.fill3DRect(1+i, 3, 1, 14, true);
    		g2.fillRect(1+i, 17-h, 10, h);
    		g2.setColor(Color.white);
    		g2.drawRect(1+i, 17-h, 10, h);
//    		g2.drawLine(1+i, 17-h, 1+i, 17);
    	}
        blend = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 1.0f);
        g2.setComposite(blend);
    }
}
