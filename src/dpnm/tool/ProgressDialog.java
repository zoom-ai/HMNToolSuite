package dpnm.tool;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;

import dpnm.tool.comp.ZFileFilter;

public class ProgressDialog extends JDialog implements ProgressInterface {
	private JProgressBar networkBar;
	private JProgressBar mobileNodeBar;
	private JTextArea output;
	private JButton okBtn;
	
	private JPanel mainPane = null;
	
	public ProgressDialog(Frame owner) {
		super(owner, "Random Generation");
		
		setIconImage(getToolkit().getImage(HMNEmulatorGUI.rf.getURL(
				Resources.ICON_DIR+File.separator+Resources.SIMULATOR_ICON)));
		createUI();
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width/2) - (getWidth()/2),
				(screenSize.height/2) - (getHeight()/2)-150);
	
	}
	public void createUI() {
		mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		setContentPane(mainPane);
		mainPane.setOpaque(true);
		
		networkBar = new JProgressBar(0, 100);
		networkBar.setValue(0);
		networkBar.setStringPainted(true);
		
		mobileNodeBar = new JProgressBar(0, 100);
		mobileNodeBar.setValue(0);
		mobileNodeBar.setStringPainted(true);
		
		output = new JTextArea(8,30);
		output.setMargin(new Insets(5,5,5,5));
		output.setEditable(false);
		
		JPanel panel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
	    panel.setLayout(gridbag);
		
		JLabel label = new JLabel("Network Creation");
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(label,c);
		panel.add(label);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(networkBar, c);
		panel.add(networkBar);
		
		label = new JLabel("Mobile Node Creation");
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10,0,0,0);
		gridbag.setConstraints(label,c);
		panel.add(label);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10,0,0,0);
		gridbag.setConstraints(mobileNodeBar, c);
		panel.add(mobileNodeBar);
		
		mainPane.add(panel, BorderLayout.PAGE_START);
		
		add(new JScrollPane(output), BorderLayout.CENTER);
		mainPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
        okBtn = new JButton("OK");
        okBtn.setEnabled(false);
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	setVisible(false);
            	reset();
            }
        });
//		mainPane.add(okBtn, BorderLayout.PAGE_END);
        
		pack();
	}
	
	public void reset() {
		setNetworkStatus(0);
		setMobileNodeStatus(0);
		output.setText("");
	}
	
	public void setNetworkStatus(int status) {
		if (status == networkBar.getValue())
			return;
		networkBar.setValue(status);
		appendText(
				String.format("Completed %d%% of Creating Networks.", status));
		repaint();
	}
	
	public void setMobileNodeStatus(int status) {
		if (status == mobileNodeBar.getValue())
			return;
		mobileNodeBar.setValue(status);
		appendText(
				String.format("Completed %d%% of Creating Mobile Nodes.", status));
		if (status == 100) {
			okBtn.setEnabled(true);
		}
		repaint();
	}
	
	public void appendText(String text) {
		output.append(text+"\n");
		output.getCaret().setDot(output.getText().length());
	}
	
	public static void main(String args[]) {
		ProgressDialog pd = new ProgressDialog(null);
		pd.setNetworkStatus(20);
		pd.setMobileNodeStatus(100);
		pd.setVisible(true);
	}
	public void start() {
		setVisible(true);
	}
	public void stop() {
		setVisible(false);
		reset();
	}
}
