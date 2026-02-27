package dpnm.tool.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.util.StringTokenizer;

/**
 * Class Instruction
 *
 * @author Eliot Kang
 */
public class TextComponent extends Component {
    /**
	 * 
	 */
	private static final long serialVersionUID = -158449849483676306L;
	
	///////////////////////////////////////////////////////////////////////////
	// FONT
	///////////////////////////////////////////////////////////////////////////
	public static final Font DIALOG_10 = new Font("Dialog", Font.PLAIN, 10);
    public static final Font DIALOG_10B = new Font("Dialog", Font.BOLD, 10);
	public static final Font DIALOG_12 = new Font("Dialog", Font.PLAIN, 12);
    public static final Font DIALOG_12B = new Font("Dialog", Font.BOLD, 12);
    public static final Font DIALOG_14 = new Font("Dialog", Font.BOLD, 14);
	public static final Font ARIAL_10 = new Font("Arial", Font.BOLD, 10);
	public static final Font ARIAL_12 = new Font("Arial", Font.BOLD, 12);
	public static final Font ARIAL_14 = new Font("Arial", Font.BOLD, 14);
	public static final Font ARIAL_23 = new Font("Arial", Font.BOLD, 23);
	public static final Font ARIAL_50 = new Font("Arial", Font.BOLD, 50);
	
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;

    private static final String omitStr = "...";

    private String text = null;
    private Insets insets = null;

    private int gap     = 10;   // gap, between image and text  
    private int align = ALIGN_CENTER;
    private boolean omit = true; // use '...'

    private boolean opaque = false;
    private boolean icon = false;
    
    private boolean isEditing = false;
    
    public TextComponent() {}

    /**
     * Creates a KLabel with the given Strig, Font and Color.
     */
    public TextComponent(String text) {
        this.text = text;
    }

    /**
     * Sets the String.
     */
    public void setText(String text){
        this.text = text;
    }
    
    public void setEditing(boolean editing) {
        isEditing = editing;
    }
    
    public String getText() {
        return text;
    }
    
    public void setInsets(Insets i) {
        insets = i;
    }

    /**
     * Sets alignment.
     * @param align - the alignment value
     */
    public void setAlignment(int align) {
        this.align = align;
    }

    /**
     * If this Component bounds is smaller than text's width, 
     * then use omission expression('...').
     * @param omit - use omission expression or not.
     */
    public void setOmission(boolean omit) {
        this.omit = omit;
    }
    
    /** 
     * Set the gap between image and text.
     */
    public void setGap(int i) {
        gap = i;
    }
    
    public int getTextWidth(Graphics2D g) {
        if (text != null) {
	        FontMetrics fm = g.getFontMetrics();
	        int width = 0;
	        StringTokenizer st = new StringTokenizer(text, "\n");
	        while(st.hasMoreElements()) {
	            int t_width = fm.stringWidth(st.nextToken());
	            if (insets != null) {
	                t_width=t_width+insets.left+insets.right;
	            }
	            if (t_width > width) {
	                width = t_width;
	            }
	        }
	        return width;
        }
        return 0;
    }
    
    public int getTextHeight(Graphics2D g) {
        FontMetrics fm = g.getFontMetrics();
        int th = (int) (fm.getMaxAscent() - fm.getMaxDescent()) / 2 
        + fm.getMaxAscent();
        
        return th;
    }
    
    public int getLines() {
        if (text != null) {
	        int count = 1;
	        for (int i = 0; i < text.length(); i++) {
	            if (text.charAt(i) == '\n') count++;
	        }
	        return count;
        }
        return 1;
    }
    public void draw(Graphics2D g) {

        int lineNumber = 0;
        int x = getLocation().x;
        String t = null;
        if (isEditing) {
            g.setColor(new Color(235,235,235));
            g.drawRect(getLocation().x+2, getLocation().y+2, getSize().width-2, getSize().height-2);
        }

        if (text != null) {
	        StringTokenizer st = new StringTokenizer(text, "\n");
	
	        while(st.hasMoreElements()) {
	            t = st.nextToken();
	            x = drawLine(g, t, lineNumber++);
	        }
        }
        FontMetrics fm = g.getFontMetrics();
        Point p = getLocation();
        int w = getSize().width;
        int h = getTextHeight(g)*(getLines()-1);
//        if (isEditing) {
//            g.setColor(Color.black);
//            g.setStroke(new BasicStroke(1.2f));
//            if (text != null) {
//                if (text.endsWith("\n")) {
//                    t = null;
//                    x = getLocation().x;
//                }
//            }
//            if (t != null && t.length() != 0) {
//	            int textWidth = fm.stringWidth(t)+1;
//	            int textHeight = (int) (fm.getMaxAscent() - fm.getMaxDescent()) / 2 
//	            + fm.getMaxAscent() - 2;
//	            g.drawLine(x+textWidth, (int)p.getY()+2+h, x+textWidth, (int)p.getY()+h+textHeight);
//            } else {
//	            int textHeight = (int) (fm.getMaxAscent() - fm.getMaxDescent()) / 2 +
//	            fm.getMaxAscent() -2;
//                g.drawLine(x+w/2,(int)p.getY()+2+h,x+w/2,(int)p.getY()+h+textHeight);
//            }
//        }

    }
    public int drawLine(Graphics2D g, String text, int lineNumber) {
        Dimension d = getSize();
        Point p = getLocation();

        FontMetrics fm = g.getFontMetrics();
        int th = (int) (fm.getMaxAscent() - fm.getMaxDescent()) / 2 
        + fm.getMaxAscent();

        int x = (int)p.getX();
        int y = (int)p.getY()+th*lineNumber;
        int w = d.width;
        int h = th;
        
        if (insets != null) {
            x += insets.left;
            y += insets.top;
            w -= insets.left + insets.right;
            h -= insets.top + insets.bottom;
        }

        if (text != null) { 
            g.setColor(getForeground());
            y = y + (int) (h - fm.getMaxAscent() - fm.getMaxDescent()) / 2 
                            + fm.getMaxAscent();
            //System.out.println("base : " + fm.getMaxAscent());

            // set alignment. (default - left alignment)
            if (align == ALIGN_CENTER) {
                int textWidth = fm.stringWidth(text);
                int writableSpace = d.width;
                if ((writableSpace - textWidth) > 0) {
                    x = x + (d.width - textWidth) / 2;
                }
            } else if (align == ALIGN_RIGHT) {
                int textWidth = fm.stringWidth(text);
                int writableSpace = d.width;
                if ((writableSpace - textWidth) > 0) {
                    x = x + d.width - fm.stringWidth(text);
                }
            }

            // use omission expression.
            if (omit) {
                int writableSpace = d.width;
                int textWidth = fm.stringWidth(text);
                if(writableSpace < textWidth) {
                    int omitStrWidth = fm.stringWidth(omitStr);
                    int usedSpace = omitStrWidth;
                    int length = text.length();
                    for (int i = 0; i < length; i++) {
                        usedSpace += fm.charWidth(text.charAt(i));
                        if (usedSpace > writableSpace) {
                            text = text.substring(0, i) + omitStr;
                            //System.out.println("omit text : " + text);
                            break;
                        }
                    } 
                }
            }
//            g.setColor(Color.black);
            g.setFont(getFont());
            g.setColor(getForeground());
            g.drawString(text, x, y);

        }
        
        return x;
    }

    public void append(char c) {
        StringBuffer sb = text == null ? new StringBuffer() : new StringBuffer(text);
        sb.append(c);
        text = sb.toString();
    }
    
    public void back() {
        if (text != null) {
            if (text.length() != 0) {
                text = text.substring(0, text.length()-1);
            }
        }
    }
    
    public void enter() {
        if (text != null) {
            append('\n');
        }
    }
}
