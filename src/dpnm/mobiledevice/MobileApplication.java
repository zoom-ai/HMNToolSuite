package dpnm.mobiledevice;

public interface MobileApplication {
	public String getName();
	public void init();
	public void start();
	public void pause();
	public void destroy();
	public boolean isRunning();
}
