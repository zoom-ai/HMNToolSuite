package dpnm.network;

public interface INetwork {
	//	name
	/**
	 * get name of network
	 *
	 * @return name of network
	 */
	public String 	getName();

	//	type
	/**
	 * get name of network
	 *
	 * @return name of network
	 */
	public String 	getDeviceType();

	//	coverage
	/**
	 * get coverage of network (meter)
	 * 
	 * @return network coverage
	 */
	public int 		getCoverage();		//	radius (meters)
	
	//	quality
	/**
	 * get bandwidth of network (ms)
	 * 
	 * @return network bandwidth
	 */
	public int 		getBandwidth();		//	bandwidth (kbyte)
	
	/**
	 * get delay of network (ms)
	 * 
	 * @return network delay
	 */
	public int 		getDelay();			// ms
	
	/**
	 * get jitter of network (ms)
	 * 
	 * @return network jitter
	 */
	public int 		getJitter();
	
	/**
	 * get BER (bit error rate) (dB)
	 * 
	 * @return BER of network
	 */
	public double 	getBER();
	
	/**
	 * get throughput of network (Mbyte/s)
	 * 
	 * @return network throughput
	 */
	public double 	getThroughput();
	
	/**
	 * get burst error of network
	 * 
	 * @return network burst error
	 */
	public double 	getBurstError();
	
	/**
	 * get packet loss ratio
	 * 
	 * @return network PLR
	 */
	public double 	getPacketLossRatio();
	
	//	cost
	/**
	 * get cost rate ($)
	 * 
	 * @return cost rate of network
	 */
	public double 	getCostRate();
	
	//	power
	/**
	 * get transmission power consumption rate (W)
	 * 
	 * @return tx power
	 */
	public double 	getTxPower();
	
	/**
	 * get receive power consumption rate (W)
	 * 
	 * @return rx power
	 */
	public double 	getRxPower();
	
	/**
	 * get idle power consumption rate (W)
	 * 
	 * @return idle power
	 */
	public double 	getIdlePower();
	
	//	velocity
	/**
	 * get minimum velocity bound (km/s)
	 * 
	 * @return minimum velocity
	 */
	public int 		getMinVelocity();
	
	/**
	 * get maximum velocity bound (km/s)
	 * 
	 * @return maximum velocity
	 */
	public int		getMaxVelocity();
	
	/**
	 * get the color
	 * 
	 * @return color
	 */
	public int 		getColor();
}
