package dpnm.context;

public class PowerConsumptionContext {
	//	power consumption rate
	private double txPower;
	private double rxPower;
	private double idlePower;

	public double getTxPower() {
		return txPower;
	}

	public void setTxPower(double txPower) {
		this.txPower = txPower;
	}
	public double getRxPower() {
		return rxPower;
	}
	public void setRxPower(double rxPower) {
		this.rxPower = rxPower;
	}
	public double getIdlePower() {
		return idlePower;
	}
	public void setIdlePower(double idlePower) {
		this.idlePower = idlePower;
	}
}
