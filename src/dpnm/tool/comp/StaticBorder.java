package dpnm.tool.comp;

import javax.swing.border.*;
import java.awt.*;
import java.util.*;

public class StaticBorder implements Border {
    private int borderWidth = 3;

    public StaticBorder( int width ) {
        borderWidth = width;
    }

    public void paintBorder( Component comp, Graphics g, 
            int x, int y, int width, int height ) {
        Color baseColor = comp.getBackground();
        float[] HSB = Color.RGBtoHSB( baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null );
        HSB[1] *= 0.6;
        if ( HSB[1] < 0 )
            HSB[1] = ( float )0;
        HSB[2] += 0.3;
        if ( HSB[2] > 1 )
            HSB[2] = ( float )1;
        Color brightColor = Color.getHSBColor( HSB[0], HSB[1], HSB[2] );
        HSB = Color.RGBtoHSB( baseColor.getRed(), baseColor.getGreen(), 
            baseColor.getBlue(), null );
        HSB[2] -= 0.3;
        if ( HSB[2] < 0 )
            HSB[2] = ( float )0;
        Color darkColor = Color.getHSBColor( HSB[0], HSB[1], HSB[2] );

        for( int i = 0; i < borderWidth; i++ ) {
            g.setColor( darkColor );
            g.drawLine( x + i, y, x + i, y + height - i - 2 );
            g.drawLine( x, y + i, x + width - i - 1, y + i );
            g.setColor( brightColor );
            g.drawLine( x + i, y + height - i - 1, x + width - 1, 
                y + height - i - 1 );
            g.drawLine( x + width - i - 1, y + i, x + width - i - 1, 
                y + height - 1 );
        }
    }

    public boolean isBorderOpaque() { return false; }
    public Insets getBorderInsets( Component comp ) {
        return new Insets( borderWidth + 1, borderWidth + 1, borderWidth + 1, 
            borderWidth + 1 );
    }
}
