package dpnm.mobiledevice.app;

import dpnm.mobiledevice.MobileApplication;

public class VoiceCall implements MobileApplication {
	public static final String name = "VoiceCall";

	private boolean isRunning = false;
	
	public VoiceCall() {
		
	}
	
	public void destroy() {
		isRunning = false;
	}

	public void init() {
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void pause() {
	}

	public void start() {
		isRunning = true;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public String toString() {
		return getName();
	}
}
