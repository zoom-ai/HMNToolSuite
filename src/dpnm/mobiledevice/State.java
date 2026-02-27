package dpnm.mobiledevice;

/**
 * This class is for defining State
 * 
 * @author eliot
 *
 */
public final class State {
	//	main state
	static final int POWER_OFF = 0x10;
	static final int POWER_ON  = 0x11;
	
	//	application state
	static final int APP_IDLE = 0x20;
	static final int APP_RUNNING = 0x21;
	
	//	network state
	static final int NETWORK_IDLE = 0x30;
	static final int NETWORK_RUNNING = 0x31;
	
	
	static String getStateStr(int state) {
		switch(state) {
		case POWER_ON: return "POWER ON";
		case POWER_OFF: return "POWER OFF";
		
		//	application
		case APP_IDLE: return "IDLE";
		case APP_RUNNING: return "APP RUNNING";
		
		//	network
		case NETWORK_IDLE: return "IDLE";
		case NETWORK_RUNNING: return "NETWORK RUNNING";
		
		default: return "NONE";
		}
	}
}
