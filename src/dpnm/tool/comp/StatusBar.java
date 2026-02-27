package dpnm.tool.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

/**
 * Class Instruction
 *
 * @author Eliot Kang
 */
public class StatusBar extends JPanel {
	private JLabel _msg = new JLabel("State: ");
	private JLabel _msg2 = new JLabel("Simulation State: ");
	private JLabel _msg3 = new JLabel("(,)");
	private JLabel _msg4 = new JLabel("");
	
	public StatusBar()
	{
		this(true);
	}
		
	public StatusBar(boolean isSim)
	{
		setBorder(new LineBorder(Color.black)) ;
		_msg.setBorder(new EtchedBorder());
		_msg2.setBorder(new EtchedBorder());
		_msg3.setBorder(new EtchedBorder());
    	_msg4.setBorder(new EtchedBorder());
    	
    	_msg3.setPreferredSize(new Dimension(90, 25));
    	_msg3.setMinimumSize(new Dimension(90,25));
    	_msg4.setPreferredSize(new Dimension(90,25));
    	_msg4.setMinimumSize(new Dimension(90,25));

		GridBagLayout gridBag = new GridBagLayout() ;
		GridBagConstraints gridBagConst = new GridBagConstraints();
		setLayout(gridBag) ;

		gridBagConst.fill = GridBagConstraints.BOTH ;
		gridBagConst.weightx = 0.5;
		gridBag.setConstraints(_msg, gridBagConst);
		add(_msg) ;
		
	
		if (isSim) {
    		gridBag.setConstraints(_msg2, gridBagConst);
    		add(_msg2) ;
		}
		
		add(_msg4) ;
		add(_msg3) ;
	}

	public void setState(String message)
	{
		_msg.setText("State : " + message) ;
	}
	
	public void setSimState(String message) 
	{
		_msg2.setText("Simulation State: " + message);
	}
	
	public void setTime(String message)
	{
		_msg2.setText("Simulation Duration: " + message);
	}
	
	public void setPosition(int x, int y)
	{
		_msg3.setText(x + ", " + y);
	}
	
	public void setZoom(double zoom) {
		
		_msg4.setText("Zoom: " + getDoubleStr(zoom));
	}
	
	private String getDoubleStr(double n) {
		int f = (int)n;
		int r = ((int)(n*10))%10;
		return f+"."+r;
	}
}