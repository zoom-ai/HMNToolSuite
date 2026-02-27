package dpnm.network;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NetworkData extends NetworkImpl {
	long currentTime = 0;
	public static final String NETWORK_PARAMETERS[] = {
		"Coverage (meter)",
		"Bandwidth (kbyte)",
		"Delay (ms)",
		"Jitter (ms)",
		"BER (dB)",
		"Throughput (Mbyte/s)",
		"Burst Error",
		"Packet Loss Ratio",
		"Cost Rate ($/min)",
		"Power Tx (W)",
		"Power Rx (W)",
		"Power Idle (W)",
		"Max Velocity (km/h)",
		"Min Velocity (km/h)"
	};

	public String getNetworkValue(int v) {
		switch(v) {
		case 0: return Integer.toString(getCoverage());
		case 1: return Integer.toString(getBandwidth());
		case 2: return Integer.toString(getDelay());
		case 3: return Integer.toString(getJitter());
		case 4: return Double.toString(getBER());
		case 5: return Double.toString(getThroughput());
		case 6: return Double.toString(getBurstError());
		case 7: return Double.toString(getPacketLossRatio());
		case 8: return Double.toString(getCostRate());
		case 9: return Double.toString(getTxPower());
		case 10: return Double.toString(getRxPower());
		case 11: return Double.toString(getIdlePower());
		case 12: return Integer.toString(getMaxVelocity());
		case 13: return Integer.toString(getMinVelocity());
		}
		return "";
	}

	public void setNetworkValue(int idx, String v) {
		switch(idx) {
		case 0: setCoverage(Integer.parseInt(v)); break;
		case 1: setBandwidth(Integer.parseInt(v)); break;
		case 2: setDelay(Integer.parseInt(v)); break;
		case 3: setJitter(Integer.parseInt(v)); break;
		case 4: setBer(Double.parseDouble(v)); break;
		case 5: setThroughput(Double.parseDouble(v)); break;
		case 6: setBurstErr(Double.parseDouble(v)); break;
		case 7: setPlr(Double.parseDouble(v)); break;
		case 8: setCostRate(Double.parseDouble(v)); break;
		case 9: setTxPower(Double.parseDouble(v)); break;
		case 10: setRxPower(Double.parseDouble(v)); break;
		case 11: setIdlePower(Double.parseDouble(v)); break;
		case 12: setMaxVelocity(Integer.parseInt(v)); break;	
		case 13: setMinVelocity(Integer.parseInt(v)); break;
		}
	}

	public String getNetworkSeriesValue(int v) {
		switch(v) {
		case 0: return getSeriesDataStr(coverage);
		case 1: return getSeriesDataStr(bandwidth);
		case 2: return getSeriesDataStr(delay);
		case 3: return getSeriesDataStr(jitter);
		case 4: return getSeriesDataStr(ber);
		case 5: return getSeriesDataStr(throughput);
		case 6: return getSeriesDataStr(burstErr);
		case 7: return getSeriesDataStr(plr);
		case 8: return getSeriesDataStr(costRate);
		case 9: return getSeriesDataStr(txPower);
		case 10: return getSeriesDataStr(rxPower);
		case 11: return getSeriesDataStr(idlePower);
		case 12: return getSeriesDataStr(maxVelocity);
		case 13: return getSeriesDataStr(minVelocity);
		}
		return "";
	}

	public void setNetworkSeriesValue(int idx, String v) {
		switch(idx) {
		case 0: setCoverage(getSeriesDataInt(v)); break;
		case 1: setBandwidth(getSeriesDataInt(v)); break;
		case 2: setDelay(getSeriesDataInt(v)); break;
		case 3: setJitter(getSeriesDataInt(v)); break;
		case 4: setBer(getSeriesDataDouble(v)); break;
		case 5: setThroughput(getSeriesDataDouble(v)); break;
		case 6: setBurstErr(getSeriesDataDouble(v)); break;
		case 7: setPlr(getSeriesDataDouble(v)); break;
		case 8: setCostRate(getSeriesDataDouble(v)); break;
		case 9: setTxPower(getSeriesDataDouble(v)); break;
		case 10: setRxPower(getSeriesDataDouble(v)); break;
		case 11: setIdlePower(getSeriesDataDouble(v)); break;
		case 12: setMaxVelocity(getSeriesDataInt(v)); break;
		case 13: setMinVelocity(getSeriesDataInt(v)); break;
		}
	}

	private String getSeriesDataStr(long[][] value) {
		if (value != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < value.length; i++) {
				buffer.append(value[i][0]+","+value[i][1]+"|");
			}
			return buffer.toString();
		}
		return "";
	}
	private String getSeriesDataStr(double[][] value) {
		if (value != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < value.length; i++) {
				buffer.append(value[i][0]+","+value[i][1]+"|");
			}
			return buffer.toString();
		}
		return "";
	}
	
	private long[][] getSeriesDataInt(String v) {
		if (v != null && v.intern() != "".intern()) {
			StringTokenizer st = new StringTokenizer(v, "|");
			Vector<String> value = new Vector<String>();
			while(st.hasMoreTokens()) {
				value.addElement(st.nextToken());
			}
			long data[][] = new long[value.size()][2];
			for (int i = 0; i < value.size(); i++) {
				StringTokenizer dst = new StringTokenizer(value.elementAt(i), ",");
				data[i][0] = Long.parseLong(dst.nextToken());
				data[i][1] = Long.parseLong(dst.nextToken());
			}
			return data;
		}
		return null;
	}
	
	private double[][] getSeriesDataDouble(String v) {
		if (v != null && v.intern() != "".intern()) {
			StringTokenizer st = new StringTokenizer(v, "|");
			Vector<String> value = new Vector<String>();
			while(st.hasMoreTokens()) {
				value.addElement(st.nextToken());
			}
			double data[][] = new double[value.size()][2];
			for (int i = 0; i < value.size(); i++) {
				StringTokenizer dst = new StringTokenizer(value.elementAt(i), ",");
				data[i][0] = Double.parseDouble(dst.nextToken());
				data[i][1] = Double.parseDouble(dst.nextToken());
			}
			return data;
		}
		return null;
	}


	//	coverage
	private long coverage[][];
	
	//	quality
	private long bandwidth[][];
	private long delay[][];
	private long jitter[][];
	private double ber[][];
	private double throughput[][];
	private double burstErr[][];
	private double plr[][];
	
	//	cost
	private double costRate[][];
	
	//	power consumption rate
	private double txPower[][];
	private double rxPower[][];
	private double idlePower[][];
	
	//	velocity
	private long minVelocity[][];
	private long maxVelocity[][];
	
	public NetworkData(INetwork network) {
		setNetwork(network);
		for (int i = 0; i < NETWORK_PARAMETERS.length; i++) {
			setNetworkSeriesValue(i, new String("0," +getNetworkValue(i)));
		}
		setCurrentTime(0);
	}
	
	public void setNetwork(INetwork network) {
		super.setCoverage(network.getCoverage());
		super.setBandwidth(network.getBandwidth());
		super.setDelay(network.getDelay());
		super.setJitter(network.getJitter());
		super.setBer(network.getBER());
		super.setThroughput(network.getThroughput());
		super.setBurstErr(network.getBurstError());
		super.setPlr(network.getPacketLossRatio());
		super.setCostRate(network.getCostRate());
		super.setTxPower(network.getTxPower());
		super.setRxPower(network.getRxPower());
		super.setIdlePower(network.getIdlePower());
		super.setMinVelocity(network.getMinVelocity());
		super.setMaxVelocity(network.getMaxVelocity());
		setCurrentTime(0);
	}

	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\r\n");
		for (int i = 0; i < NETWORK_PARAMETERS.length; i++) {
			buffer.append(NETWORK_PARAMETERS[i] + ": " + getNetworkValue(i)+",");
			if ((i%2)==1) buffer.append("\r\n");
		}
		return buffer.toString();
	}

	public void setCurrentTime(long time) {
		super.setCoverage(getTimedValue(coverage, getCoverage(), time));
		super.setBandwidth(getTimedValue(bandwidth, getBandwidth(), time));
		super.setDelay(getTimedValue(delay, getDelay(), time));
		super.setJitter(getTimedValue(jitter, getJitter(), time));
		super.setBer(getTimedValue(ber, getBer(), time));
		super.setThroughput(getTimedValue(throughput, getThroughput(), time));
		super.setBurstErr(getTimedValue(burstErr, getBurstErr(), time));
		super.setPlr(getTimedValue(plr, getPlr(), time));
		super.setCostRate(getTimedValue(costRate, getCostRate(), time));
		super.setTxPower(getTimedValue(txPower, getTxPower(), time));
		super.setRxPower(getTimedValue(rxPower, getRxPower(), time));
		super.setIdlePower(getTimedValue(idlePower, getIdlePower(), time));
		super.setMinVelocity(getTimedValue(minVelocity, getMinVelocity(), time));
		super.setMaxVelocity(getTimedValue(maxVelocity, getMaxVelocity(), time));
	}

	public long getCurrentTime() {
		return currentTime;
	}
	
	public int getTimedValue(long value[][], int current, long time) {
		if (value != null) {
			int v = current;
			for (int i = 0; i < value.length; i++) {
				if (value[i][0] > time) {
					return v;
				}
				v = (int)value[i][1];
			}
			return v;
		}
		return current;
	}
	
	public double getTimedValue(double value[][], double current, long time) {
		if (value != null) {
			double v = current;
			for (int i = 0; i < value.length; i++) {
				if (value[i][0] > (double)time) {
					return v;
				}
				v = value[i][1];
			}
		}
		return current;
	}
	
	public NetworkData(Node networkNode) {
		NodeList nodes = networkNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == COVERAGE.intern()) {
            	setNetworkSeriesValue(0, value);
            }
            else if(name.intern() == BANDWIDTH.intern()) {
            	setNetworkSeriesValue(1, value);
			}
            else if(name.intern() == DELAY.intern()) {
            	setNetworkSeriesValue(2, value);
			}
            else if(name.intern() == JITTER.intern()) {
            	setNetworkSeriesValue(3, value);
			}
            else if(name.intern() == BER.intern()) {
            	setNetworkSeriesValue(4, value);
			}
            else if(name.intern() == THROUGHPUT.intern()) {
            	setNetworkSeriesValue(5, value);
			}
            else if(name.intern() == BURSTERR.intern()) {
            	setNetworkSeriesValue(6, value);
			}
            else if(name.intern() == PLR.intern()) {
            	setNetworkSeriesValue(7, value);
			}
            else if(name.intern() == COSTRATE.intern()) {
            	setNetworkSeriesValue(8, value);
			}
            else if(name.intern() == TXPOWER.intern()) {
            	setNetworkSeriesValue(9, value);
			}
            else if(name.intern() == RXPOWER.intern()) {
            	setNetworkSeriesValue(10, value);
			}
            else if(name.intern() == IDLEPOWER.intern()) {
            	setNetworkSeriesValue(11, value);
			}
            else if(name.intern() == MINVELOCITY.intern()) {
            	setNetworkSeriesValue(12, value);
			}
            else if(name.intern() == MAXVELOCITY.intern()) {
            	setNetworkSeriesValue(13, value);
			}
		}
	}
	
	public void appendXml(Element root) {
        Document doc = root.getOwnerDocument();
		
		String values[] = {
				COVERAGE, 
				BANDWIDTH, 
				DELAY, 
				JITTER, 
				BER, 
				THROUGHPUT,
				BURSTERR,
				PLR,
				COSTRATE,
				TXPOWER,
				RXPOWER,
				IDLEPOWER,
				MINVELOCITY,
				MAXVELOCITY,
		};
 	
		for (int i = 0; i < values.length; i++) {
    		Element elem = doc.createElement(values[i]);
    		elem.appendChild(doc.createTextNode(getNetworkSeriesValue(i)));
    		root.appendChild(elem);
		}
	}
	public void setCoverage(int coverage) {
		super.setCoverage(coverage);
	}

	public void setBandwidth(int bandwidth) {
		super.setBandwidth(bandwidth);
	}

	public void setDelay(int delay) {
		super.setDelay(delay);
	}

	public void setJitter(int jitter) {
		super.setJitter(jitter);
	}

	public void setThroughput(double throughput) {
		super.setThroughput(throughput);
	}

	public void setCostRate(double costRate) {
		super.setCostRate(costRate);
	}

	public void setTxPower(double txPower) {
		super.setTxPower(txPower);
	}

	public void setRxPower(double rxPower) {
		super.setRxPower(rxPower);
	}

	public void setIdlePower(double idlePower) {
		super.setIdlePower(idlePower);
	}

	public void setMinVelocity(int minVelocity) {
		super.setMinVelocity(minVelocity);
	}

	public void setMaxVelocity(int maxVelocity) {
		super.setMaxVelocity(maxVelocity);
	}

	public void setCoverage(long[][] coverage) {
		this.coverage = coverage;
	}

	public void setBandwidth(long[][] bandwidth) {
		this.bandwidth = bandwidth;
	}

	public void setDelay(long[][] delay) {
		this.delay = delay;
	}

	public void setJitter(long[][] jitter) {
		this.jitter = jitter;
	}

	public void setBer(double[][] ber) {
		this.ber = ber;
	}

	public void setThroughput(double[][] throughput) {
		this.throughput = throughput;
	}

	public void setBurstErr(double[][] burstErr) {
		this.burstErr = burstErr;
	}

	public void setPlr(double[][] plr) {
		this.plr = plr;
	}

	public void setCostRate(double[][] costRate) {
		this.costRate = costRate;
	}

	public void setTxPower(double[][] txPower) {
		this.txPower = txPower;
	}

	public void setRxPower(double[][] rxPower) {
		this.rxPower = rxPower;
	}

	public void setIdlePower(double[][] idlePower) {
		this.idlePower = idlePower;
	}

	public void setMinVelocity(long[][] minVelocity) {
		this.minVelocity = minVelocity;
	}

	public void setMaxVelocity(long[][] maxVelocity) {
		this.maxVelocity = maxVelocity;
	}
	
}
