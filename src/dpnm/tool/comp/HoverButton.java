/*
 /* @(#)HoverButton.java
 * 
 * Copyright (c) 2004 Jun Kang(eliot@postech.edu)
 * All right reserved. http://eliot.plus.or.kr
 * 
 */
package dpnm.tool.comp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class HoverButton extends JButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tipText = null;
    //private HButtonGroup buttonGroup = null;

    public HoverButton( String label ) {
        this( label, null );
    }
    
    public HoverButton( Icon icon ) {
        this( null, icon );
    }

    public HoverButton(Action action) {
        this(null, null);
        setAction(action);
    }

    public HoverButton( String label, Icon icon ) {
        super( icon );
        //setSelected( false );
        if ( label != null && label != "" )
            label += " ";
        super.setText( label );
        setOpaque( true );
        setMargin( new Insets( -1, 1, -1, 0 ));
        setHorizontalAlignment( JLabel.CENTER );
        setRequestFocusEnabled( false );
        setVerticalTextPosition( JButton.BOTTOM );
        addMouseListener( new MouseAdapter() {
            public void mouseEntered( MouseEvent e ) {
                if (( e.getModifiers() & InputEvent.BUTTON1_MASK ) 
                        == InputEvent.BUTTON1_MASK )
                    return;
                if ( isEnabled() ) {
                    setBorderPainted( true );
                    getParent().setCursor( new Cursor( Cursor.HAND_CURSOR ));
                }
                if ( tipText != null ) {
                    ToolTip.setTipText( tipText );
                    if ( !isEnabled() )
                        ToolTip.setAttribute(0, new Color(248, 74, 56), null);
                    ToolTip.setVisible( true );
                    ToolTip.setLocation( HoverButton.this, e.getX(), e.getY() );
                }
            }

            public void mouseExited( MouseEvent e ) {
                if ( !isSelected() )
                    setBorderPainted( false );
                getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ));
                ToolTip.setVisible( false );
            }
        } );

        /*addMouseMotionListener( new MouseInputAdapter() {
            public void mouseMoved( MouseEvent e ) {
                if ( tipText != null && ToolTip.isVisible() )
                    ToolTip.setLocation( HoverButton.this, e.getX(), e.getY() );
            }
        } );

        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                refresh();
            }
        });*/
        refresh();
    }

    /**
     * Updates button look as the selection state.
     */
    private void refresh() {
        if (isSelected()) {
            setBorder( new StaticBorder( 1 ));
            setBackground( UIManager.getColor( "controlShadow" ));
            setBorderPainted( true );
        } else {
            setBorder( BorderFactory.createEtchedBorder() );
            setBackground( UIManager.getColor( "control" ));
            setBorderPainted( false );
        }
    }

    /*protected void fireActionPerformed( ActionEvent e ) {
        super.fireActionPerformed( e );
        if ( buttonGroup != null )
            buttonGroup.buttonClicked( this );
    }

    public void setGroup( HButtonGroup group ) {
        buttonGroup = group;
    }
     */

    public void setToolTipText( String text ) {
        tipText = text;
    }

    public void setText( String label ) {
        if ( label != null && label != "" )
            label += " ";
        super.setText( label );
    }
// commented to aviod IOException by Park, Jung Jun
/*
    public Icon getDisabledIcon() {
        Icon defaultIcon = super.getIcon();

        ImageProducer source = (( ImageIcon )defaultIcon ).getImage().getSource();
        GrayFilter filter = new GrayFilter();
        ImageProducer prod = new FilteredImageSource( source, filter );
        ImageIcon disabledIcon = new ImageIcon( Toolkit.getDefaultToolkit().createImage( prod ));
        return disabledIcon;
    }
*/
}
