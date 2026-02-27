/*
 * @(#)HMNEmulatorMain.java
 * 
 * Created on 2008. 02. 21
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eliot Kang.
 *
 *	Contact: Eliot Kang at eliot@postech.edu
 */

package dpnm.tool;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import dpnm.tool.comp.*;
import dpnm.util.Logger;

/**
 * This is a main window.
 * This has many internal windows and uses them.
 * 	 
 * @author eliot
 */
public class HMNEmulatorMain implements ActionListener, InternalFrameListener {
	/**
	* root frame of the application
	*/
	private static JFrame _frame ;
    /**
    * The number of windows.
    */
    public static int _numWindow = 0 ;
    public static int getNumWindow() {
        return _numWindow;
    }
    public static void increaseNumWindow() {
        _numWindow++;
        if (_numWindow == 10) {
            _numWindow = 0;
        }
    }
	/**
	* DesktopPane of SWindow which manipulates all internal window.
	*/
	private JDesktopPane _sDesktopPane ;

	// Current ui
	public String currentUI = "Metal" ;
	
	static ResourceFinder rf = new ResourceFinder();
	
	private static HMNEmulatorMain _instance = null;

	public synchronized static HMNEmulatorMain getInstance(String title) {
		if (_instance == null) {
			_instance = new HMNEmulatorMain(title);
		}
		return _instance;
	}
	
	public synchronized static HMNEmulatorMain getInstance() {
		return _instance;
	}

	/**
	* Initialize window.
	*
	* @param title the window title
	*/
	public HMNEmulatorMain(String title)
	{
		// instance define
		_instance = this ;

		_frame = new JFrame(title) ;
		
		_frame.setIconImage(
                _frame.getToolkit().getImage(rf.getURL(Resources.LOGO_IMG_STR)));
		_sDesktopPane = new JDesktopPane() ;

		// setting menuBar
		_frame.setJMenuBar(createMenuBar()) ;

		JPanel contentPane = new JPanel() ;
		contentPane.setLayout(new BorderLayout()) ;
		contentPane.add(createToolBar(), BorderLayout.NORTH) ;

		int inset = 10;
		//Provide a preferred size for the split pane line by xhiloh on 07. 16.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		contentPane.add(_sDesktopPane, BorderLayout.CENTER) ;

		inset = 50;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_frame.setBounds ( inset, inset, screenSize.width - inset*4, 
                screenSize.height - inset*4 );
		_frame.setContentPane(contentPane) ;

		_frame.addWindowListener(new WindowAdapter()
			{
				public void windowActivated(WindowEvent e) {
				}
				public void windowClosing(WindowEvent e)
				{
					System.exit(0) ;
				}
				public void windowOpened(WindowEvent e) {
				}
			}
		);
		
		init();
	}
	
	private void init() {
	}
	/**
	* Manipulate the event which is relative with menu.
	*
	* @param e the menu event
	*/
	public void actionPerformed(ActionEvent e)
	{
		String sMenuItemStr = e.getActionCommand() ;
		if(sMenuItemStr.intern() == "CreateNetworks".intern()) {
			if (Env.DEBUG) {
				Logger.getInstance().logEmulator("Main", sMenuItemStr + " is pressed");
			}
			launchNetworkEditor();
		} else if(sMenuItemStr.intern() == "PlayNetworks".intern()) {
			if (Env.DEBUG) {
				Logger.getInstance().logEmulator("Main", sMenuItemStr + " is pressed");
			}
			launchNetworkPlayer();
		}
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar sMenuBar ;

		sMenuBar = new JMenuBar() ;
		sMenuBar.add(buildConnectMenu()) ;
		sMenuBar.add(buildHelpMenu()) ;

		return sMenuBar ;
	}

	protected JMenu buildConnectMenu()
	{
		JMenu file = new JMenu("Emulator") ;
		
		JMenuItem menuItem;
		
		/*
        menuItem = new JMenuItem("Create Networks", 
                rf.getIcon(Resources.CREATEMAP_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
				ActionEvent.ALT_MASK));
        menuItem.setActionCommand("CreateNetworks");
        menuItem.addActionListener(this);
        file.add(menuItem);

        menuItem = new JMenuItem("Play Networks", 
                rf.getIcon(Resources.DISPLAYMAP_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 
				ActionEvent.ALT_MASK));
        menuItem.setActionCommand("PlayNetworks");
        menuItem.addActionListener(this);
        file.add(menuItem);
   */     
        file.addSeparator();

		menuItem = new JMenuItem("Exit", rf.getIcon(
                    Resources.EXIT_IMG_STR));
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    exit();
					System.exit(1);;
				}
			}
		);
        file.add(menuItem);
		return file ;
	}

	/**
	* build Help menu
	*
	* @return help menu
	*/
	protected JMenu buildHelpMenu() {
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About HMNEmulator...");

		about.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			showAboutBox();
		    }
		});

		help.add(about);
		return help;
	}
		/**
	* show about dialog
	*/
	public void showAboutBox() {
		JOptionPane.showMessageDialog(null, Env.ABOUT_MSG);
	}

	/**
	* Create Tool Bar
	*
	* @return toolBar
	*/
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar() ;
		return toolBar ;
	}
	
	public void exit() {
	
	}

    public void insertFrame(JInternalFrame frame) {
        _sDesktopPane.add(frame);
    }
    
    JFrame getFrame() {
        return _frame;
    }

	/**
	* Start application.
	*
	* @param args ignored
	*/
	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(Env.DEFAULT_LOOKANDFEEL);
		} catch (Exception e) {}

        HMNEmulatorMain.getInstance(Env.TITLE + " " +Env.VERSION);
		_frame.setLocation(100,0);

		_frame.setVisible(true) ;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void launchNetworkEditor() {
		//	launch editor
		NetworkEditor editor = new NetworkEditor();
		_sDesktopPane.add(editor);
		editor.setVisible(true);
	}
	
	private void launchNetworkPlayer() {
		//	launch player
//		NetworkPlayer player = new NetworkPlayer();
	}
}
