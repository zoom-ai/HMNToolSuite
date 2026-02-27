package dpnm.tool;

import javax.swing.table.AbstractTableModel;

import dpnm.network.INetwork;

public class NetworkInfoModel extends AbstractTableModel {
	private static final String NETWORK_PARAMETERS[] = {
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
			"Min Velocity (km/h)",
			"Max Velocity (km/h)"
	};
	INetwork network;
	
	public NetworkInfoModel(INetwork network) {
		setNetworkInfo(network);
	}
	
	public void setNetworkInfo(INetwork network) {
		this.network = network;
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return NETWORK_PARAMETERS.length;
	}
	
	public String getColumnName(int col) {
		switch(col) {
		case 0:
			return "Parameter";
		case 1:
			return "Value";
		}
		return null;
	}

	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
    	if (col == 0) {
    		return NETWORK_PARAMETERS[row];
    	}
    	if (col == 1) {
    		return getNetworkValue(row);
    	}
    	return null;
	}
	
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
    	return false;
    }
    
    private String getNetworkValue(int v) {
    	if (network == null)
    		return "";
    	
    	switch(v) {
    	case 0: return Integer.toString(network.getCoverage());
    	case 1: return Integer.toString(network.getBandwidth());
    	case 2: return Integer.toString(network.getDelay());
    	case 3: return Integer.toString(network.getJitter());
    	case 4: return Double.toString(network.getBER());
    	case 5: return Double.toString(network.getThroughput());
    	case 6: return Double.toString(network.getBurstError());
    	case 7: return Double.toString(network.getPacketLossRatio());
    	case 8: return Double.toString(network.getCostRate());
    	case 9: return Double.toString(network.getTxPower());
    	case 10: return Double.toString(network.getRxPower());
    	case 11: return Double.toString(network.getIdlePower());
    	case 12: return Integer.toString(network.getMinVelocity());
    	case 13: return Integer.toString(network.getMaxVelocity());
    	}
    	return "";
    }
}
