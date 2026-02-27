package dpnm.mobiledevice;

import java.io.File;

import dpnm.mobiledevice.policy.ruleobjects.Action;
import dpnm.tool.Resources;
import net.sourceforge.jFuzzyLogic.FIS;

//	1. Collect all information
//	2. Calculate APAV (Access Point Acceptance Value) for each AP
//	3. Calculate APSV (Access Point Satisfaction Value) for each AP
public class NetworkProperty {
	/* RSS context */
	private double signalStrength = 0.0;
	/* Cost context */
	private double costRate = 0.0;
	/* Quality context */
	private int bandwidth = 0;
	private int delay =0;
	private int jitter =0;
	private double ber =0.0;
	private double throughput=0.0;
	private double burstErr=0.0;
	private double plr=0.0;
	/* Lifetime context */
	private double txPower =0.0;
	private double rxPower=0.0;
	private double idlePower=0.0;
	
	private double apavRSS = 0.0;
	private double apavCost = 0.0;
	private double apavQuality = 0.0;
	private double apavLifetime = 0.0;
	
	private double apsv = 0.0;

	private long timeStamp = 0;
	
	private Object lock = new Object();
	
	public NetworkProperty() {
		
	}

	public double getSignalStrength() {
		return signalStrength;
	}

	void setSignalStrength(double signalStrength) {
		this.signalStrength = signalStrength;
	}
	
	public double getCostRate() {
		return costRate;
	}

	void setCostRate(double costRate) {
		this.costRate = costRate;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	public int getDelay() {
		return delay;
	}

	void setDelay(int delay) {
		this.delay = delay;
	}

	public int getJitter() {
		return jitter;
	}

	void setJitter(int jitter) {
		this.jitter = jitter;
	}

	public double getBer() {
		return ber;
	}

	void setBer(double ber) {
		this.ber = ber;
	}

	public double getThroughput() {
		return throughput;
	}

	void setThroughput(double throughput) {
		this.throughput = throughput;
	}

	public double getBurstErr() {
		return burstErr;
	}

	void setBurstErr(double burstErr) {
		this.burstErr = burstErr;
	}

	public double getPlr() {
		return plr;
	}

	void setPlr(double plr) {
		this.plr = plr;
	}

	public double getTxPower() {
		return txPower;
	}

	void setTxPower(double txPower) {
		this.txPower = txPower;
	}

	public double getRxPower() {
		return rxPower;
	}

	void setRxPower(double rxPower) {
		this.rxPower = rxPower;
	}

	public double getIdlePower() {
		return idlePower;
	}

	void setIdlePower(double idlePower) {
		this.idlePower = idlePower;
	}

	public synchronized double getApavRSS() {
		return apavRSS;
	}

	public synchronized double getApavCost() {
		return apavCost;
	}

	public synchronized double getApavQuality() {
		return apavQuality;
	}

	public synchronized double getApavLifetime() {
		return apavLifetime;
	}

	public synchronized double getApsv() {
		return apsv;
	}

	private void setApavRSS(double apavRSS) {
		this.apavRSS = apavRSS;
	}

	private void setApavCost(double apavCost) {
		this.apavCost = apavCost;
	}

	private void setApavQuality(double apavQuality) {
		this.apavQuality = apavQuality;
	}

	private void setApavLifetime(double apavLifetime) {
		this.apavLifetime = apavLifetime;
	}

	private void setApsv(double apsv) {
		this.apsv = apsv;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public void update(NetworkProperty prop) {
		synchronized(lock) {
			setSignalStrength(prop.getSignalStrength());
			setCostRate(prop.getCostRate());
			setBandwidth(prop.getBandwidth());
			setDelay(prop.getDelay());
			setJitter(prop.getJitter());
			setBer(prop.getBer());
			setThroughput(prop.getThroughput());
			setBurstErr(prop.getBurstErr());
			setPlr(prop.getPlr());
			setTxPower(prop.getTxPower());
			setRxPower(prop.getRxPower());
			setIdlePower(prop.getIdlePower());
			setTimeStamp(prop.getTimeStamp());
			setApavRSS(prop.getApavRSS());
			setApavCost(prop.getApavCost());
			setApavQuality(prop.getApavQuality());
			setApavLifetime(prop.getApavLifetime());
			setApsv(prop.getApsv());
		}
	}
	
	public synchronized void calculateAllValues(FIS qualityFIS, FIS lifetimeFIS, UserProfile up) {
		synchronized(lock) {
			calculateApavRSS();
			calculateApavCost();
			calculateApavQuality(qualityFIS);
			calculateApavLifetime(lifetimeFIS);
			calculateApsv(up);
		}
	}

	public synchronized boolean compare(NetworkProperty dp, int decision) {
		synchronized(lock) {
			if (decision == Action.RSS) {
				return getApavRSS() < dp.getApavRSS();
			} else if (decision == Action.COST){
				if (getApavCost() == dp.getApavCost()) {
					return getSignalStrength() < dp.getSignalStrength();
				}
				return getApavCost() < dp.getApavCost();
			} else if (decision == Action.QUALITY){
				if (getApavQuality() == dp.getApavQuality()) {
					return getSignalStrength() < dp.getSignalStrength();
				}
				return getApavQuality() < dp.getApavQuality();
			} else if (decision == Action.LIFETIME){
				if (getApavLifetime() == dp.getApavLifetime()) {
					return getSignalStrength() < dp.getSignalStrength();
				}
				return getApavLifetime() < dp.getApavLifetime();
			} else if (decision == Action.AUHO) {
				if (getApsv() == dp.getApsv()) {
					return getSignalStrength() < dp.getSignalStrength();
				}
				return getApsv() < dp.getApsv();
			}
			return false;
		}
	}

	/* ------------------------------------------------------------------
	 * Calculate APAV_RSS
	 * ------------------------------------------------------------------ */
	private void calculateApavRSS() {
        // Set inputs
//        MobileDevice.fuzzyRSS.setVariable("RSS", getSignalStrength()*100);
//        // Evaluate
//        MobileDevice.fuzzyRSS.evaluate();
//        apavRSS = MobileDevice.fuzzyRSS.getVariable("APAV").getValue();
		apavRSS = getSignalStrength();
	}

	/* ------------------------------------------------------------------
	 * Calculate APAV_Cost
	 * ------------------------------------------------------------------ */
	private void calculateApavCost() {
//        // Set inputs
//        MobileDevice.fuzzyCost.setVariable("COST", getCostRate());
//        // Evaluate
//        MobileDevice.fuzzyCost.evaluate();
//        apavCost = MobileDevice.fuzzyCost.getVariable("APAV").getValue();
		//	As Cost is lower, APAV is higher
		apavCost = 1.0 - getCostRate();
	}
	/* ------------------------------------------------------------------
	 * Calculate APAV_Quality
	 * ------------------------------------------------------------------ */
	private void calculateApavQuality(FIS fis) {
		fis.setVariable("bandwidth", getBandwidth());
        fis.setVariable("delay", getDelay());
        fis.setVariable("jitter", getJitter());
        fis.setVariable("ber", getBer());
        fis.setVariable("throughput", getThroughput());
        fis.setVariable("bursterror", getBurstErr());
        fis.setVariable("packetlossratio", getPlr());
        // Evaluate
        fis.evaluate();
        apavQuality = fis.getVariable("APAV").getLatestDefuzzifiedValue();
        
        if (apavQuality == 0) {
            System.out.println(fis.getVariable("APAV").getLatestDefuzzifiedValue());
        	System.out.println("apavQuality == 0");
        }
	}
	
	/* ------------------------------------------------------------------
	 * Calculate APAV_Lifetime
	 * ------------------------------------------------------------------ */
	private void calculateApavLifetime(FIS fis) {
		// Set inputs
        fis.setVariable("tx", getTxPower());
        fis.setVariable("rx", getRxPower());
        fis.setVariable("idle", getIdlePower());
        // Evaluate
        fis.evaluate();
        apavLifetime = fis.getVariable("APAV").getLatestDefuzzifiedValue();
        if (apavLifetime == 0) {
        	System.out.println(getTxPower());
        	System.out.println(getRxPower());
        	System.out.println(getIdlePower());
        	System.out.println(fis.getVariable("APAV").getLatestDefuzzifiedValue());
        	System.out.println("apavLifetime == 0");
        }
	}
	
	private void calculateApsv(UserProfile up) {
		if (up != null) {
			apsv = apavRSS * up.getRss() + apavCost * up.getCost() +
				apavQuality * up.getQuality() + apavLifetime * up.getLifetime();
		}
	}
	
	public synchronized NetworkProperty clone() {
		synchronized(lock) {
			NetworkProperty p = new NetworkProperty();
			p.update(this);
			return p;
		}
	}
}
