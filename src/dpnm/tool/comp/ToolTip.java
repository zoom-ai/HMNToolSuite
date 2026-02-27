package dpnm.tool.comp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * A fancy tooltip implementation.
 *
 * @version $revision: 1.2 $ $date: 2000/03/27 04:15:17 $
 * @author Kim, DoHyung (based on Park, JungGeun's implementation)
 */
public class ToolTip {

    //////////////////////////////////////////////////////////////////////
    // Constants
    //////////////////////////////////////////////////////////////////////

    /**
     * String instance used as separator. Pass this for tooltip text
     * if a separator should be displayed.
     */
    public static final String SEPARATOR = new String();


    //////////////////////////////////////////////////////////////////////
    // Variables
    //////////////////////////////////////////////////////////////////////

    /** ToolTip component. */
    private static CustomToolTip tip = new CustomToolTip();

    /**
     * JWindow to be used to host tooltip when tooltip goes
     * beyond the bounds of ancestor which is a {@link Window}.
     */
    private static JWindow popup = null;

    /** Current visibility of tooltip. */
    private static boolean visible = false;

    /** Map which caches popup windows. The key is a {@link Window}. */
    private static HashMap popupCache = new HashMap();


    //////////////////////////////////////////////////////////////////////
    // Methods
    //////////////////////////////////////////////////////////////////////

    /** Sets the location of tooltip on given component. */
    public static void setLocation(Component c, int x, int y) {
        Component comp = c;
        JLayeredPane layeredPane;
        Component t;
        int wx, wy;

        while (comp != null && !(comp instanceof Window)) {
            x += comp.getX();
            y += comp.getY();
            comp = comp.getParent();
        }
        if (comp == null) {
            return;
        }
        y += 20;

        wx = x + comp.getX();
        wy = y + comp.getY();

        layeredPane = ((RootPaneContainer) comp).getLayeredPane();
        t = layeredPane.getParent();
        while (t != comp) {
            x -= t.getX();
            y -= t.getY();
            t = t.getParent();
        }

        if (x + tip.getWidth() > layeredPane.getWidth()
                || y + tip.getHeight() > layeredPane.getHeight()) {
            if (popup == null || popup.getOwner() != comp) {
                if (popup != null) {
                    popup.setVisible(false);
                    popup = null;
                }
                if (tip.getParent() != null) {
                    tip.setVisible(false);
                    tip.getParent().remove(tip);
                }
                popup = getPopup((Window) comp);
                if (popup == null) {
                    return;
                }
                tip.setVisible(true);
                popup.getContentPane().add(tip);
                popup.pack();
                popup.setVisible(visible);
            }
            popup.setLocation(wx, wy);
            return;
        }

        if (popup != null) {
            popup.setVisible(false);
            popup.remove(tip);
            popup = null;
        }
        if (tip.getParent() != layeredPane) {
            tip.setVisible(false);
            if (tip.getParent() != null) {
                tip.getParent().remove(tip);
            }
            layeredPane.add(tip, JLayeredPane.POPUP_LAYER);
        }
        tip.setLocation(x, y);
        tip.setVisible(visible);
    }

    /** Sets the location of tooltip on given component. */
    public static void setLocation(Component comp, Point p) {
        setLocation(comp, p.x, p.y);
    }

    /** Sets the visibility of tooltip. */
    public static void setVisible(boolean visible) {
        ToolTip.visible = visible;
        if (popup != null) {
            popup.setVisible(visible);
        } else {
            tip.setVisible(visible);
            if (!visible && tip.getParent() != null) {
                tip.getParent().remove(tip);
            }
        }
    }

    /** Returns <tt>true</tt> if tooltip is being displayed. */
    public static boolean isVisible() {
        return visible;
    }

    /**
     * Sets text to be displayed as tooltip. This method interprets
     * newline characters and divides given text into a list of lines.
     */
    public static void setTipText(String text) {
        tip.setTipText(text);
        if (popup != null) {
            popup.pack();
        }
    }

    /**
     * Sets text to be displayed as tooltip with specific color
     * and font to be used to display it. This method interprets
     * newline characters and divides given text into a list of lines.
     */
    public static void setTipText(String text, Color color, Font font) {
        tip.setTipText(text, color, font);
        if (popup != null) {
            popup.pack();
        }
    }

    /** Replaces a line of text in tooltip text at given index. */
    public static void setTipText(int index, String text) {
        tip.setTipText(index, text);
        if (popup != null) {
            popup.pack();
        }
    }

    /**
     * Replaces a line of text in tooltip text at given index, and also
     * changes its font and color.
     */
    public static void setTipText(int index, String text,
                                  Color color, Font font) {
        tip.setTipText(index, text, color, font);
        if (popup != null) {
            popup.pack();
        }
    }

    /** Sets given text as tooltip. */
    public static void setTipText(String text[]) {
        tip.setTipText(text);
        if (popup != null) {
            popup.pack();
        }
    }

    /**
     * Sets given text as tooltip with specific color and font to be used
     * for displaying it.
     */
    public static void setTipText(String text[], Color color, Font font) {
        tip.setTipText(text, color, font);
        if (popup != null) {
            popup.pack();
        }
    }

    /** Removes a line of text at given index. */
    public static void removeTipText(int index) {
        tip.removeTipText( index );
        if (popup != null) {
            popup.pack();
        }
    }

    /** Sets attributes(font and color) for a line at given index. */
    public static void setAttribute(int index, Color textColor, Font font) {
        tip.setAttribute(index, textColor, font);
        if (popup != null) {
            popup.pack();
        }
    }

    /**
     * Returns a {@link JWindow} instance used to host tooltip. This window
     * is used to display tooltip text when tooltip text is outside given
     * window.
     */
    private static JWindow getPopup(Window window) {
        JWindow popup;

        popup = (JWindow) popupCache.get(window);
        if (popup != null) {
            return popup;
        }
        popup = new JWindow(window);
        popupCache.put(window, popup);

        return popup;
    }
}

/** ToolTip component. */
class CustomToolTip extends JComponent {

    //////////////////////////////////////////////////////////////////////
    // Constants
    //////////////////////////////////////////////////////////////////////

    /** Font leading. */
    private static final int FONT_LEADING = 0;

    /** Background color of tooltip. */
    private static final Color backColor = new Color(193, 226, 241, 220);

    /** Color of tooltip shadow. */
    private static final Color shadowColor = new Color(82, 52, 144, 150);

    /** Default font for tooltip texts. */
    private static final Font defaultFont = new Font("Dialog", Font.BOLD, 12);

    /** Default color for tooltip texts. */
    private static final Color defaultColor = new Color(79, 56, 178);


    //////////////////////////////////////////////////////////////////////
    // Instance variables
    //////////////////////////////////////////////////////////////////////

    /** Represents tooltip text lines. */
    private Line[] lines = null;


    //////////////////////////////////////////////////////////////////////
    // Methods
    //////////////////////////////////////////////////////////////////////

    /** @see ToolTip#setTipText(String) */
    public void setTipText(String text) {
        setTipText(text, defaultColor, defaultFont);
    }

    /** @see ToolTip#setTipText(String,Color,Font) */
    public void setTipText(String text, Color color, Font font) {
        int index, pos;

        lines = null;
        index = 0;

        while (true) {
            pos = text.indexOf('\n');
            if (pos < 0) {
                break;
            }
            _setTipText(index, text.substring(0, pos), color, font);
            text = text.substring(pos + 1, text.length());
            index++;
        }
        _setTipText(index, text, color, font);
        setSize(getPreferredSize());
        repaint();
    }

    /** @see ToolTip#setTipText(int,String) */
    public void setTipText(int index, String text) {
        setTipText(index, text, defaultColor, defaultFont);
    }

    /** @see ToolTip#setTipText(int,String,Color,Font) */
    public void setTipText(int index, String text, Color color, Font font) {
        _setTipText(index, text, color, font);
        setSize(getPreferredSize());
        repaint();
    }

    public void _setTipText(int index, String text, Color color, Font font) {
        Line[] temp;

        if (lines == null) {
            lines = new Line[1];
            lines[0] = new Line();
            index = 0;
        }

        if (lines.length <= index) {
            temp = new Line[lines.length + 1];
            System.arraycopy(lines, 0, temp, 0, lines.length);
            temp[lines.length] = new Line();
            lines = temp;
        }

        lines[index].text = text;
        lines[index].fontColor = color;
        lines[index].font = font;
    }

    /** @see ToolTip#setTipText(String[]) */
    public void setTipText(String text[]) {
        setTipText(text, defaultColor, defaultFont);
    }

    /** @see ToolTip#setTipText(String[],Color,Font) */
    public void setTipText(String text[], Color color, Font font) {
        lines = new Line[text.length];
        for(int i = 0; i < text.length; i++) {
            lines[i] = new Line();
            lines[i].text = text[i];
            lines[i].fontColor = color;
            lines[i].font = font;
        }
        setSize(getPreferredSize());
        repaint();
    }

    /** @see ToolTip#removeTipText(int) */
    public void removeTipText(int index) {
        if (lines.length <= index) {
            return;
        }
        lines[index] = null;
        setSize(getPreferredSize());
        repaint();
    }

    /** @see ToolTip#setAttribute(int,Color,Font) */
    public void setAttribute(int index, Color textColor, Font font) {
        if (lines == null || lines.length <= index) {
            return;
        }
        if (textColor != null) {
            lines[index].fontColor = textColor;
        }
        if (font != null) {
            lines[index].font = font;
        }
        setSize(getPreferredSize());
        repaint();
    }

    /** Overriden to return proper size for tooltip text set. */
    public Dimension getPreferredSize() {
        int width = 0;
        int height = 0;
        FontMetrics fm;
        int fontHeight;

        if (lines == null) {
            return new Dimension();
        }
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) {
                /* delete */
                continue;
            }
            if (lines[i].text == ToolTip.SEPARATOR) {
                height += 4;
                continue;
            }

            fm = getFontMetrics(lines[i].font);
            fontHeight = fm.getAscent() + fm.getDescent() + FONT_LEADING;
            if (fm.stringWidth(lines[i].text) > width) {
                width = fm.stringWidth(lines[i].text);
            }
            height += fontHeight;
        }
        return new Dimension(width + 10, height + 4);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        FontMetrics fm;

        super.paintComponent(g);

        g2.setColor(backColor);
        g2.fillRect(0, 0, getWidth() - 3, getHeight() - 3);
        for (int i = 0, y = 0; i < lines.length; i++) {
            if (lines[i] == null) {
                continue;
            }
            if (lines[i].text == ToolTip.SEPARATOR) {
                g2.setColor(Color.white);
                g2.drawLine(0, y + 3, getWidth() - 4, y + 3);
                y += 4;
                continue;
            }
            g2.setColor(lines[i].fontColor);
            g2.setFont(lines[i].font);
            fm = g2.getFontMetrics();
            y += fm.getAscent() + FONT_LEADING;
            g2.drawString(lines[i].text, 4, y);
            y += fm.getDescent();
        }

        g2.setColor(shadowColor);
        g2.fillRect(2, getHeight() - 3, getWidth() - 2, 3);
        g2.fillRect(getWidth() - 3, 2, 3, getHeight() - 5);
    }


    //////////////////////////////////////////////////////////////////////
    // Innerclasses
    //////////////////////////////////////////////////////////////////////

    /** To hold information on a line of text. */
    private static class Line {
        public String text;
        public Color fontColor;
        public Font font;

        public Line() {
            text = "";
            fontColor = defaultColor;
            font = defaultFont;
        }
    }
}
