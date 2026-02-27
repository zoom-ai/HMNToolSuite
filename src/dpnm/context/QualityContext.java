package dpnm.context;

public class QualityContext {
	//	quality
	private int delay;
	private int jitter;
	private double ber;
	private double throughput;
	private double burstErr;
	private double plr;
	
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getJitter() {
		return jitter;
	}
	public void setJitter(int jitter) {
		this.jitter = jitter;
	}
	public double getBer() {
		return ber;
	}
	public void setBer(double ber) {
		this.ber = ber;
	}
	public double getThroughput() {
		return throughput;
	}
	public void setThroughput(double throughput) {
		this.throughput = throughput;
	}
	public double getBurstErr() {
		return burstErr;
	}
	public void setBurstErr(double burstErr) {
		this.burstErr = burstErr;
	}
	public double getPlr() {
		return plr;
	}
	public void setPlr(double plr) {
		this.plr = plr;
	}	
}
