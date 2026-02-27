/*
 * @(#)Env.java
 * 
 * Created on 2005. 12. 14
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.tool;

import java.awt.Color;
/**
 * This class is Environment configuration classes.
 *
 * @author Eric Kang
 */
public final class Env {
	static final boolean DEBUG = true;

	static final String TITLE = "HMNEmulator (Heterogeneous Mobile Network Emulator)";
	static final String VERSION = "0.2 (2009-2010) - Copyright (J.M. Kang)";
	static final String TAB_TITLE = "Network Map";
	static final String NETWROK_TAB_TITLE = "Network Information";
	static final String MOBILE_NODE_TAB_TITLE = "Mobile Node Information";
	static final String HANDOVER_TAB_TITLE = "Hanodver Information";
	static final String MONITOR_TITLE = "Monitor View";
	
	static final String DEFAULT_LOOKANDFEEL = 
		"swing.addon.plaf.threeD.ThreeDLookAndFeel";
	
	static final String ABOUT_MSG =
		"HMNEmulator (Heterogeneous Mobile Network Emulator\n" +
		"This emulator is maintained by Eliot J.M. Kang(eliot@postech.edu)\n" +
		"(2009 - 2010)";
	
	/**
	 * constants for player
	 */
	static final int PLAYER_WIDTH = 1164;
	static final int PLAYER_HEIGHT = 768;
	
	static final int SIMULATOR_WIDTH = 1164;
	static final int SIMULATOR_HEIGHT = 768;
	
	static final int MONITOR_WIDTH = 1000;
	static final int MONITOR_HEIGHT = 900;
	
	static final int VIEW_WIDTH = 1024;
	static final int VIEW_HEIGHT = 668;
	
	/*
	 * coverage unit
	 */
	public static final int ZOOM = 4;

	static final String HANDLER_NAME = "emulator";

	static final int RANDOM_MAX_PATH = 100;
	static final int RANDOM_MAX_STAY = 5000;
}
