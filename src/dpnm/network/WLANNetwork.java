package dpnm.network;

public class WLANNetwork implements INetwork {
	//	name
	private String name = "WLAN (IEEE 802.11b)";

	//	coverage
	private int coverage = 200;
	
	//	quality
	private int bandwidth = 11000;
	private int delay = 45;
	private int jitter = 10;
	private double ber = 0.00001;
	private double throughput = 25;
	private double burstErr =0.2;
	private double plr = 0.04;
	
	//	cost
	private double costRate = 0.2;
	
	//	power consumption rate
	private double txPower = 2.8;
	private double rxPower = 0.495;
	private double idlePower = 0.082;
	
	//	velocity
	private int minVelocity = 0;
	private int maxVelocity = 4;
	
	public String getName() {
		return name;
	}

	public double getBER() {
		return ber;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public double getBurstError() {
		return burstErr;
	}

	public double getCostRate() {
		return costRate;
	}

	public int getCoverage() {
		return coverage;
	}

	public int getDelay() {
		return delay;
	}

	public double getIdlePower() {
		return idlePower;
	}

	public int getJitter() {
		return jitter;
	}

	public int getMaxVelocity() {
		return maxVelocity;
	}

	public int getMinVelocity() {
		return minVelocity;
	}

	public double getPacketLossRatio() {
		return plr;
	}

	public double getRxPower() {
		return rxPower;
	}

	public double getThroughput() {
		return throughput;
	}

	public double getTxPower() {
		return txPower;
	}

	public int getColor() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getDeviceType() {
		// TODO Auto-generated method stub
		return null;
	}
}
