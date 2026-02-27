package dpnm.mobiledevice.app;

import dpnm.mobiledevice.MobileApplication;

public class WebBrowser implements MobileApplication {
	public static final String name = "WebBrowser";

	private boolean isRunning = false;
	
	public WebBrowser() {
		
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
