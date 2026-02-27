package dpnm.mobiledevice;

import dpnm.Conf;
import dpnm.util.Logger;

public class ApplicationManager {
	private MobileDevice owner = null;
	private MobileApplication applications[] = null;
	
	private MobileApplication currentApp = null;
	
	public ApplicationManager(MobileDevice owner) {
		this.owner = owner;
	}
	
	public void setApplications(MobileApplication apps[]) {
		this.applications = apps;
	}
	
	public boolean isApplication(String name) {
		return applications != null && getApplication(name) != null;
    }
	
	private MobileApplication getApplication(String name) {
		if (applications != null) {
			for (int i = 0; i < applications.length; i++) {
    			if (applications[i].getName().intern() == name.intern()) {
    				return applications[i];
    			}
			}
		}
		return null;
	}
	
	public void startApplication(String name) {
		MobileApplication app = getApplication(name);
		if (app != null) {
			if (Conf.DEBUG) {
				Logger.getInstance().logDevice("ApplicationManager", "start "+app.getName());
			}
			app.init();
			currentApp = app;
			owner.getNetworkInterfaceManager().startSelectionTask(currentApp);
			
			app.start();
		}
	}
	
	public void stopApplication(String name) {
    	MobileApplication app = getApplication(name);
		if (app != null) {
			if (Conf.DEBUG) {
				Logger.getInstance().logDevice("ApplicationManager", "stop "+app.getName());
			}
			app.destroy();
			currentApp = null;
			owner.getNetworkInterfaceManager().stopSelectionTask();
		}
	}
	
	public MobileApplication getCurrentApplication() {
		return currentApp;
	}
	
	public void stop() {
		if (currentApp != null) {
			stopApplication(currentApp.getName());
		}
	}
}
