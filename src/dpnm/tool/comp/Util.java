/*
 * @(#)Util.java
 * 
 * Created on 2005. 12. 15
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.tool.comp;

import javax.swing.*;
import java.awt.*;

public class Util {

	public static JPanel getPanelFor(JComponent comp1, JComponent comp2) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(comp1);
		panel.add(comp2);
		return panel;
	}

	public static JPanel getPanelFor(JComponent comp1, JComponent comp2, JComponent comp3) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(comp1);
		panel.add(comp2);
		panel.add(comp3);
		return panel;
	}

	public static void error(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void warn(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static void info(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void centerOnScreen(Window window) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension screenSize = t.getScreenSize();
		Dimension frameSize = window.getPreferredSize();
		double x = (screenSize.getWidth() - frameSize.getWidth()) / 2;
		double y = (screenSize.getHeight() - frameSize.getHeight()) / 2;
		window.setLocation((int) x, (int) y);
	}

	static final JButton PLACEHOLDER_BUTTON = new JButton("123456789012");
	static final Dimension BUTTON_SIZE = PLACEHOLDER_BUTTON.getPreferredSize();

	public static JButton standardButton(String caption) {
		JButton button = new JButton(caption);
		button.setPreferredSize(BUTTON_SIZE);
		button.setMinimumSize(BUTTON_SIZE);
		button.setMaximumSize(BUTTON_SIZE);
		return button;
	}

	static final JButton BIG_PLACEHOLDER_BUTTON = new JButton("12345678901234567");
	static final Dimension BIG_BUTTON_SIZE = BIG_PLACEHOLDER_BUTTON.getPreferredSize();

	public static JButton largeButton(String caption) {
		JButton button = new JButton(caption);
		button.setPreferredSize(BIG_BUTTON_SIZE);
		button.setMinimumSize(BIG_BUTTON_SIZE);
		button.setMaximumSize(BIG_BUTTON_SIZE);
		return button;
	}

	static final JLabel PLACEHOLDER_LABEL = new JLabel("nnnnnnnnnnnnnnn");
	static final Dimension LABEL_SIZE = PLACEHOLDER_LABEL.getPreferredSize();

	public static JLabel standardLabel(String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(LABEL_SIZE);
		return label;
	}

	public static JLabel standardLabel() {
		return standardLabel("");
	}

	static final int INPUT_FIELD_SIZE = 20;

	public static JTextField standardTextField() {
		JTextField textField = new JTextField();
		textField.setColumns(INPUT_FIELD_SIZE);
		return textField;
	}

	static final int SCROLL_PANE_HEIGHT = 30;

	public static JScrollPane standardScrollPane(JComponent component) {
		JTextField placeholder = Util.standardTextField();
		int width = (int)placeholder.getPreferredSize().getWidth();
		JScrollPane pane = new JScrollPane(component);
		pane.setPreferredSize(new Dimension(width, 150));
		return pane;
	}
	
	public static String getDoubleString(double value) {
        StringBuffer sb = new StringBuffer();
        int v = (int)value;
        sb.append(v);
        sb.append(".");
        value = value - v;
        v = (int)((value+0.005) * 100);
        sb.append(v);
        return sb.toString();
    }
}
