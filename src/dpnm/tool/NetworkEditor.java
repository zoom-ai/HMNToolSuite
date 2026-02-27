package dpnm.tool;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Network Editor.
 * This class is based on Model-View-Controller pattern.
 *
 * @author Eliot Kang
 */
public class NetworkEditor extends JInternalFrame {

	//	DEFINE state
	private static final int NONE = -1;
	private static final int SELECT = 0x00;
	private static final int CREATE_NETWORK = 0x01;

	private int state = NONE;

	/**
	* The offset pixels of the internal windows.
	*/
	private static final int _windowOffset = 30 ;

	JPanel view;
	JScrollPane viewPane;

	JLabel backgroundLabel = new JLabel();
		
//	StatusBar statusBar = new StatusBar();

	public NetworkEditor() {
		this("NoName", true, true, true, true, 640, 480);
	}

	public NetworkEditor(String title, int width, int height) {
		this(title, true, true, true, true, width, height);
	}

    /**
     * Constructor
     */
    public NetworkEditor(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, 
    		int width, int height) {
        super(title, resizable, closable, maximizable, iconifiable);
        
        setSize(width, height);
    }
}

