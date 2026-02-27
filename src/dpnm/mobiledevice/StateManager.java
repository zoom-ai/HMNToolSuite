package dpnm.mobiledevice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is for managing states of a mobile device
 * 
 * @author eliot
 *
 */

public class StateManager implements ActionListener {
	//	current state management
	private int mainState = State.POWER_OFF;
	
	private MobileDevice device = null;
	public StateManager(MobileDevice device) {
		this.device = device;
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		//	power
		if (cmd.intern() == "Power") {
			if (mainState == State.POWER_OFF) {
				device.powerOn();
    			mainState = State.POWER_ON;
			} else {
				device.powerOff();
    			mainState = State.POWER_OFF;
			}
		}
		
		//	Application
		if (cmd.intern() == MobileDevice.APP_NONE.intern()) {
			MobileApplication app = device.getApplicationManager().getCurrentApplication();
			if (app != null) {
				device.getApplicationManager().stopApplication(app.getName());
			}
		} else {
			if (device.getApplicationManager().isApplication(cmd)) {
				MobileApplication app = device.getApplicationManager().getCurrentApplication();
				if (app != null) {
					device.getApplicationManager().stopApplication(app.getName());
				}
				device.getApplicationManager().startApplication(cmd);
    		}
		}

		//	Network	
		if (cmd.intern() == MobileDevice.NI_NONE.intern()) {
			NetworkInterface ni = device.getNetworkInterfaceManager().getCurrentNetworkInterface();
			if (ni != null) {
				ni.setEnabled(false);
				device.getNetworkInterfaceManager().stopNetworkInterface(ni.getName());
			}
		} else {
    		if (device.getNetworkInterfaceManager().isNetworkInterface(cmd)) {
    			device.getNetworkInterfaceManager().startNetworkInterface(cmd);
    		}
		}
    }
}
