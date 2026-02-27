/*
 * @(#)Resources.java
 * 
 * Created on 2005. 6. 2
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eliot Kang at eliot@postech.edu
 */
package dpnm.tool;

import java.io.File;
import java.awt.Font;
import java.awt.Color;

/**
 * This class is for definition all constants.
 * And, resources are difined
 *
 * @author Eliot Kang
 * @since 2005/12/15
 */
public final class Resources {
	///////////////////////////////////////////////////////////////////////////
	// GENERAL
	///////////////////////////////////////////////////////////////////////////
    //  resource home path definition
    public static String DATA_DIR = (new File("data")).getAbsolutePath();
    public static String HOME = (new File("res")).getAbsolutePath();
    public static String LOG = (new File("log")).getAbsolutePath();

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

	///////////////////////////////////////////////////////////////////////////
	// COLOR SCHEME
	///////////////////////////////////////////////////////////////////////////
	/**
	 * network color
	 */
	public static final Color CDMA_COLOR = Color.red;
	public static final Color HSDPA_COLOR = Color.blue;
	public static final Color WIBRO_COLOR = Color.black;
	public static final Color WLAN_COLOR = Color.green;

	/*	description color */
    public static final Color DES_COLOR_NETWORK 		= new Color(224,224,255,224);
//    public static final Color DES_COLOR_MOdBILENODE   	= new Color(255,224,224,224);
    public static final Color DES_COLOR_MOBILENODE   	= new Color(224,255,224,224);
//    public static final Color DES_COLOR_MOBILENODE   	= new Color(255,255,224,224);
    public static final Color DES_COLOR_SERVER  	 	= new Color(255,224,224,224);

	
	///////////////////////////////////////////////////////////////////////////
	// IMAGE SCHEME
	///////////////////////////////////////////////////////////////////////////
    public static final String SPLASH_IMG_STR = "splash.png";
    public static final String LOGO_IMG_STR = "logo1.gif";
    public static final String EXIT_IMG_STR = "exit.gif";
    
    public static final String MAP_DIR = "maps"+File.separator;
    public static final String DEVICE_DIR = "device"+File.separator;
    public static final String MOBILENODE_DIR = "mobilenode"+File.separator;
    public static final String SERVER_DIR = "server"+File.separator;
    public static final String ICON_DIR = "icon"+File.separator;
    public static final String NETWORK_DIR = "network"+File.separator;
    public static final String FUZZY_RULE_DIR = "fcl"+File.separator;
   
    public static final String AP_IMG_STR = "ap.png";
    public static final String BS_IMG_STR = "bs.png";
    public static final String RAS_IMG_STR = "bs.png";
    public static final String DEVICE_IMG_STR[] = {BS_IMG_STR, AP_IMG_STR, RAS_IMG_STR};

    public static final String OSS_IMG_STR = "oss.png";
    public static final String APP_SERVER_IMG_STR = "app_server.png";
    public static final String CONTEXT_SERVER_IMG_STR = "context_server.png";
    public static final String LOCATION_SERVER_IMG_STR = "location_server.png";
    public static final String SERVER_IMG_STR[] = {OSS_IMG_STR, CONTEXT_SERVER_IMG_STR, LOCATION_SERVER_IMG_STR, APP_SERVER_IMG_STR};

    public static final String WALK_IMG_STR = "walk.png";
    public static final String CAR_IMG_STR = "car.png";
    public static final String TAXI_IMG_STR = "taxi.png";
    public static final String BUS_IMG_STR = "bus.png";
    public static final String TRAIN_IMG_STR = "train.png";
    
    public static final String MOBILENODE_IMG_STR[] = {WALK_IMG_STR, CAR_IMG_STR, TAXI_IMG_STR, BUS_IMG_STR, TRAIN_IMG_STR};
    
    //ICON
    public static final String NEW_ICON = "new.gif";
    public static final String OPEN_ICON = "open.gif";
    public static final String SAVE_ICON = "save.gif";
    public static final String SAVEAS_ICON = "saveas.gif";
    public static final String EXPORT_ICON = "export.gif";
    public static final String ADDMN_ICON = "addmn.gif";
    public static final String ADDNETWORK_ICON = "addnetwork.png";
    public static final String ADDSERVER_ICON = "addserver.png";
    public static final String MONITOR_ICON = "monitor.png";
    public static final String CONF_ICON = "conf.gif";
    public static final String PREFERENCE_ICON = "preference.gif";
    public static final String EXIT_ICON = "exit.gif";
    public static final String RUN_ICON = "run.gif";
    public static final String STOP_ICON = "stop.gif";
    public static final String SIMULATOR_ICON = "simulator.gif";
    public static final String ZOOM_ICON = "zoom.gif";
    public static final String ZOOMIN_ICON = "zoomin.gif";
    public static final String ZOOMOUT_ICON = "zoomout.gif";
    public static final String MAP_ICON = "map.png";
}
