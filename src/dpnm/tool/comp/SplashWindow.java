/*
 *	@(#)SplashWindow.java	$Revision: 1.2 $ $Date: 2001/09/12 06:50:56 $
 *
 *	Copyright (c) 2001 Alticast Corp.
 *	All rights reserved. http://www.alticast.com/
 *
 *	This software is the confidential and proprietary informatioon of
 *	Alticast Corp. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Alticast.
 *
 *	Contact: Jun Kang at eliot@alticast.com
 */
package dpnm.tool.comp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *	A window class that shows the splash window
 *
 *	@author Jun Kang
 *	@since	2001/09/03
 *	@version $Id: SplashWindow.java,v 1.2 2001/09/12 06:50:56 eliot Exp $
 */
public class SplashWindow extends JWindow {
	public SplashWindow(ImageIcon icon) {
		super();

		JLabel l;

		l = new JLabel(icon);

		Dimension labelSize = l.getPreferredSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		l.setBounds(0, 0, labelSize.width, labelSize.height);
		getContentPane().setLayout(null);
		getContentPane().add(l);
		setBounds(screenSize.width/2 - (labelSize.width/2),
				screenSize.height/2 - (labelSize.height/2),
				labelSize.width, labelSize.height);
		setVisible(true);
	}
}
