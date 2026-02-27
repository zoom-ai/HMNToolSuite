package dpnm.tool;

public class ProgressCLI implements ProgressInterface {
	
	int networkStatus = 0;
	int mobileNodeStatus = 0;
	
	public ProgressCLI() {
		
	}
	
	public void reset() {
		networkStatus = 0;
    	mobileNodeStatus = 0;
	}
	
	public void setNetworkStatus(int status) {
		if (status == networkStatus)
			return;
		networkStatus = status;
    	appendText(String.format("Completed %d%% of Creating Networks.", status));
	}
	
	public void setMobileNodeStatus(int status) {
		if (status == mobileNodeStatus)
			return;
		mobileNodeStatus = status;
		appendText(String.format("Completed %d%% of Creating Mobile Nodes.", status));
	}
	
	public void start() {
	}
	
	public void stop() {
		reset();
	}
	
	public void appendText(String str) {
		System.out.println(str);
	}
}
