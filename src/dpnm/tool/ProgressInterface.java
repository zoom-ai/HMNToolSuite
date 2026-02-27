package dpnm.tool;

public interface ProgressInterface {
	public void setNetworkStatus(int status);
	public void setMobileNodeStatus(int status);
	public void start();
	public void stop();
	public void appendText(String str);
}
