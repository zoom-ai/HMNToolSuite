package dpnm.network;

public class WiBroNetwork implements INetwork {
	//	name
	private String name = "WiBro";

	//	coverage
	private int coverage = 800;
	
	//	quality
	private int bandwidth = 2000;
	private int delay = 30;
	private int jitter = 8;
	private double ber = 0.0001;
	private double throughput = 40;
	private double burstErr =0.1;
	private double plr = 0.02;
	
	//	cost
	private double costRate = 0.5;
	
	//	power consumption rate
	private double txPower = 2.0;
	private double rxPower = 0.7;
	private double idlePower = 0.06;
	
	//	velocity
	private int minVelocity = 0;
	private int maxVelocity = 60000;
	
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
