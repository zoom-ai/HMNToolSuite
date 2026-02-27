package dpnm.tool.data;

public class LogGraph {
	private long timestamp = 0;
	private double apavRSS = 0.0;
	private double apavCost = 0.0;
	private double apavQuality = 0.0;
	private double apavLifetime = 0.0;
	private double apsv = 0.0;
	public double getApavRSS() {
		return apavRSS;
	}
	public void setApavRSS(double apavRSS) {
		this.apavRSS = apavRSS;
	}
	public double getApavCost() {
		return apavCost;
	}
	public void setApavCost(double apavCost) {
		this.apavCost = apavCost;
	}
	public double getApavQuality() {
		return apavQuality;
	}
	public void setApavQuality(double apavQuality) {
		this.apavQuality = apavQuality;
	}
	public double getApavLifetime() {
		return apavLifetime;
	}
	public void setApavLifetime(double apavLifetime) {
		this.apavLifetime = apavLifetime;
	}
	public double getApsv() {
		return apsv;
	}
	public void setApsv(double apsv) {
		this.apsv = apsv;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
