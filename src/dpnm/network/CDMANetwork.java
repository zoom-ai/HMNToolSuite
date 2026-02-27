package dpnm.network;

public class CDMANetwork implements INetwork {
	//	name
	private String name = "CDMA";

	//	coverage
	private int coverage = 1000;
	
	//	quality
	private int bandwidth = 1000;
	private int delay = 19;
	private int jitter = 6;
	private double ber = 0.001;
	private double throughput = 1.7;
	private double burstErr =0.5;
	private double plr = 0.07;
	
	//	cost
	private double costRate = 0.9;
	
	//	power consumption rate
	private double txPower = 1.4;
	private double rxPower = 0.925;
	private double idlePower = 0.045;
	
	//	velocity
	private int minVelocity = 0;
	private int maxVelocity = 1000;
	
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
