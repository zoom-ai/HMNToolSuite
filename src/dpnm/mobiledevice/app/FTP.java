package dpnm.mobiledevice.app;
import dpnm.mobiledevice.MobileApplication;

public class FTP implements MobileApplication {
	public static final String name = "FTP";
	
	private boolean isRunning = false;

	public FTP() {
		
	}
	
	public void destroy() {
		isRunning = false;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void pause() {
		// TODO Auto-generated method stub

	}

	public void start() {
		isRunning = true;
	}

	public boolean isRunning() {
		// TODO Auto-generated method stub
		return isRunning;
	}

	public String getName() {
		return name;
	}
	
	public void setNetworkInterfaceManager() {
	}
	
	public String toString() {
		return getName();
	}
}
