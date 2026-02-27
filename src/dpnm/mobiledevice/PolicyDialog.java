package dpnm.mobiledevice;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class PolicyDialog extends JDialog implements ActionListener {
	private JPanel auhoMainPane = null;
	private JPanel basicPrefPane = null;
	private JPanel advancedPrefPane = null;
	public PolicyDialog() {
		create();
		setTitle("Add New Policy");
	}
	
	public void create() {
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        JLabel label = new JLabel("Name");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        JTextField name = new JTextField(20);
        name.setForeground(Color.blue);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(name,c);

        p.add(name);

        label = new JLabel("Event");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        String eventTypes[] = dpnm.mobiledevice.policy.ruleobjects.Event.EVENT_STR;
        JComboBox eventCombo = new JComboBox(eventTypes);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(eventCombo,c);

        p.add(eventCombo);

        label = new JLabel("Condition");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);
            
        label = new JLabel("TBD");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(label,c);
        p.add(label);

        label = new JLabel("Action (Decision Algorithm)");
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,c);
        p.add(label);

        JPanel decisionPane = new JPanel();

		String decisionAlgorithms[] = dpnm.mobiledevice.policy.ruleobjects.Action.DECISION_ALGORITHM_STR;
		ButtonGroup decisionGrp = new ButtonGroup();
   		JRadioButton[] decisionBtn = new JRadioButton[decisionAlgorithms.length];
   		for (int i = 0; i < decisionAlgorithms.length; i++) {
   			decisionBtn[i] = new JRadioButton(decisionAlgorithms[i]);
   			decisionBtn[i].addActionListener(this);
   			decisionBtn[i].setActionCommand(decisionAlgorithms[i]);
   			decisionGrp.add(decisionBtn[i]);
   			decisionPane.add(decisionBtn[i]);
		}

   		
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(decisionPane,c);
   		p.add(decisionPane);

   		auhoMainPane = new JPanel();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
   		auhoMainPane.setLayout(gridbag2);
   		
   		c.fill = GridBagConstraints.HORIZONTAL;
   		gridbag.setConstraints(auhoMainPane, c);
   		p.add(auhoMainPane);
   		
   		label = new JLabel("AUHO");
        c2.gridwidth = GridBagConstraints.RELATIVE;
        gridbag2.setConstraints(label,c2);
        auhoMainPane.add(label);

        JPanel prefPane = new JPanel();

		String auhoPref[] = {"Basic Mode", "Advanced Mode"};
		ButtonGroup auhoGrp = new ButtonGroup();
   		JRadioButton[] auhoBtn = new JRadioButton[auhoPref.length];
   		for (int i = 0; i < auhoPref.length; i++) {
   			auhoBtn[i] = new JRadioButton(auhoPref[i]);
   			auhoBtn[i].addActionListener(this);
   			auhoBtn[i].setActionCommand(auhoPref[i]);
   			auhoGrp.add(auhoBtn[i]);
   			prefPane.add(auhoBtn[i]);
		}
        c2.gridwidth = GridBagConstraints.REMAINDER;
        gridbag2.setConstraints(prefPane,c2);
   		auhoMainPane.add(prefPane);

        basicPrefPane = new JPanel();

   		JCheckBox[] prefBtn = new JCheckBox[decisionAlgorithms.length-2];
   		for (int i = 1; i < decisionAlgorithms.length-1; i++) {
   			prefBtn[i-1] = new JCheckBox(decisionAlgorithms[i]);
   			basicPrefPane.add(prefBtn[i-1]);
		}
        c2.fill = GridBagConstraints.HORIZONTAL;
        gridbag2.setConstraints(basicPrefPane,c2);
   		auhoMainPane.add(basicPrefPane);
   		basicPrefPane.setVisible(false);

        advancedPrefPane = new JPanel();

   		JTextField[] prefField = new JTextField[decisionAlgorithms.length-2];
   		for (int i = 1; i < decisionAlgorithms.length-1; i++) {
   			JLabel l = new JLabel(decisionAlgorithms[i]+": ");
   			advancedPrefPane.add(l);
   			prefField[i-1] = new JTextField(3);
   			advancedPrefPane.add(prefField[i-1]);
		}
        c2.fill = GridBagConstraints.HORIZONTAL;
        gridbag2.setConstraints(advancedPrefPane,c2);
   		auhoMainPane.add(advancedPrefPane);
   		advancedPrefPane.setVisible(false);
   		
   		auhoMainPane.setVisible(false);

   	
   		getContentPane().add(p);
   		pack();
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		
		if (cmd.intern() == "AUHO") {
			if (auhoMainPane != null) {
				auhoMainPane.setVisible(true);
			}
			pack();
		} else if (cmd.intern() == "RANDOM" || cmd.intern() == "RSS" || cmd.intern() == "COST" 
			|| cmd.intern() == "LIFETIME" || cmd.intern() == "QUALITY") {
			if (auhoMainPane != null) {
				auhoMainPane.setVisible(false);
				pack();
			}
		} else if (cmd.intern() == "Basic Mode") {
			if (advancedPrefPane != null) {
				advancedPrefPane.setVisible(false);
				pack();
			}
			if (basicPrefPane != null) {
				basicPrefPane.setVisible(true);
			}
			pack();
		} else if (cmd.intern() == "Advanced Mode") {
			if (basicPrefPane != null) {
				basicPrefPane.setVisible(false);
			}
			if (advancedPrefPane != null) {
				advancedPrefPane.setVisible(true);
			}
			pack();
		}
		
	}
	
	public static void main(String args[]) {
		PolicyDialog pd = new PolicyDialog();
		pd.setVisible(true);
	}
}
