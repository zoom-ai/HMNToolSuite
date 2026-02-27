package dpnm.network;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dpnm.tool.data.NetworkDeviceInfo;
import dpnm.tool.data.NetworkMobileNodeInfo;

public class NetworkImpl implements INetwork {
    public static final String NETWORK     		= "network";
    public static final String NAME        		= "name";
    public static final String DEVICE        	= "device";
    public static final String COVERAGE        	= "coverage";
    public static final String BANDWIDTH       	= "bandwidth";
    public static final String DELAY       		= "delay";
    public static final String JITTER       	= "jitter";
    public static final String BER       		= "bitErrorRate";
    public static final String THROUGHPUT      	= "throughput";
    public static final String BURSTERR       	= "burstErr";
    public static final String PLR       		= "packetLossRatio";
    public static final String COSTRATE       	= "costRate";
    public static final String TXPOWER       	= "txPower";
    public static final String RXPOWER       	= "rxPower";
    public static final String IDLEPOWER       	= "idlePower";
    public static final String MINVELOCITY     	= "minVelocity";
    public static final String MAXVELOCITY     	= "maxVelocity";
    public static final String COLOR       		= "color";
    
	//	name
	private String name = null;

	//
	private String deviceType = null;

	//	coverage
	private int coverage = 0;
	
	//	quality
	private int bandwidth = 0;
	private int delay = 0;
	private int jitter = 0;
	private double ber = 0.0;
	private double throughput = 0.0;
	private double burstErr =0.0;
	private double plr = 0.0;
	
	//	cost
	private double costRate = 0.0;
	
	//	power consumption rate
	private double txPower = 0.0;
	private double rxPower = 0.0;
	private double idlePower = 0.0;
	
	//	velocity
	private int minVelocity = 0;
	private int maxVelocity = 0;
	
	//	color
	private int color = 0;
	
	public NetworkImpl() {
		
	}
	
	public NetworkImpl(Node networkNode) {
		NodeList nodes = networkNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == NAME.intern()) {
            	this.name = value;
			}
            else if(name.intern() == DEVICE.intern()) {
            	this.deviceType = value;
			}
            else if(name.intern() == COVERAGE.intern()) {
            	this.coverage = Integer.parseInt(value);
			}
            else if(name.intern() == BANDWIDTH.intern()) {
            	this.bandwidth = Integer.parseInt(value);
			}
            else if(name.intern() == DELAY.intern()) {
            	this.delay = Integer.parseInt(value);
			}
            else if(name.intern() == JITTER.intern()) {
            	this.jitter = Integer.parseInt(value);
			}
            else if(name.intern() == BER.intern()) {
            	this.ber = Double.parseDouble(value);
			}
            else if(name.intern() == THROUGHPUT.intern()) {
            	this.throughput = Double.parseDouble(value);
			}
            else if(name.intern() == BURSTERR.intern()) {
            	this.burstErr = Double.parseDouble(value);
			}
            else if(name.intern() == PLR.intern()) {
            	this.plr = Double.parseDouble(value);
			}
            else if(name.intern() == COSTRATE.intern()) {
            	this.costRate = Double.parseDouble(value);
			}
            else if(name.intern() == TXPOWER.intern()) {
            	this.txPower = Double.parseDouble(value);
			}
            else if(name.intern() == RXPOWER.intern()) {
            	this.rxPower = Double.parseDouble(value);
			}
            else if(name.intern() == IDLEPOWER.intern()) {
            	this.idlePower = Double.parseDouble(value);
			}
            else if(name.intern() == MINVELOCITY.intern()) {
            	this.minVelocity = Integer.parseInt(value);
			}
            else if(name.intern() == MAXVELOCITY.intern()) {
            	this.maxVelocity = Integer.parseInt(value);
			}
            else if(name.intern() == COLOR.intern()) {
            	this.color = Integer.parseInt(value, 16);
			}
		}
	}
	
	public void appendXml(Element root) {
        Document doc = root.getOwnerDocument();
		Element networkElem = doc.createElement(NETWORK);
		root.appendChild(networkElem);
		
		String values[][] = {
				{NAME, name},
				{DEVICE, deviceType},
				{COVERAGE, String.valueOf(coverage)},
				{BANDWIDTH, String.valueOf(bandwidth)},
				{DELAY, String.valueOf(delay)},
				{JITTER, String.valueOf(jitter)},
				{BER, String.valueOf(ber)},
				{THROUGHPUT, String.valueOf(throughput)},
				{BURSTERR, String.valueOf(burstErr)},
				{PLR, String.valueOf(plr)},
				{COSTRATE, String.valueOf(costRate)},
				{TXPOWER, String.valueOf(txPower)},
				{RXPOWER, String.valueOf(rxPower)},
				{IDLEPOWER, String.valueOf(idlePower)},
				{MINVELOCITY, String.valueOf(minVelocity)},
				{MAXVELOCITY, String.valueOf(maxVelocity)},
				{COLOR, Integer.toHexString(color)}
		};
 	
		for (int i = 0; i < values.length; i++) {
    		Element elem = doc.createElement(values[i][0]);
    		elem.appendChild(doc.createTextNode(values[i][1]));
    		networkElem.appendChild(elem);
		}
	}
	
	
	public String getName() {
		return name;
	}

	public String getDeviceType() {
		return deviceType;
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
		return color;
	}

	double getBer() {
		return ber;
	}

	void setBer(double ber) {
		this.ber = ber;
	}

	double getBurstErr() {
		return burstErr;
	}

	void setBurstErr(double burstErr) {
		this.burstErr = burstErr;
	}

	double getPlr() {
		return plr;
	}

	void setPlr(double plr) {
		this.plr = plr;
	}

	void setName(String name) {
		this.name = name;
	}

	void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	void setCoverage(int coverage) {
		this.coverage = coverage;
	}

	void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	void setDelay(int delay) {
		this.delay = delay;
	}

	void setJitter(int jitter) {
		this.jitter = jitter;
	}

	void setThroughput(double throughput) {
		this.throughput = throughput;
	}

	void setCostRate(double costRate) {
		this.costRate = costRate;
	}

	void setTxPower(double txPower) {
		this.txPower = txPower;
	}

	void setRxPower(double rxPower) {
		this.rxPower = rxPower;
	}

	void setIdlePower(double idlePower) {
		this.idlePower = idlePower;
	}

	void setMinVelocity(int minVelocity) {
		this.minVelocity = minVelocity;
	}

	void setMaxVelocity(int maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	void setColor(int color) {
		this.color = color;
	}
}
