package dpnm.comm;

import java.io.*;
import java.util.Vector;

public final class AddressManager {
	private static final String MAC_SEPARATOR = "-";
	private static final String MAC_PREFIX = "7D";
	private static final String IP_PREFIX = "2001:0:0:0:";
	private static final String IP_SEPARATOR = ":";
	/*
	private static AddressManager _instance = null;
	
	public static AddressManager getInstance() {
		if (_instance == null) {
			_instance = new AddressManager();
		}
		return _instance;
	}
	*/
	
	/*
	private Vector<String> macAddressList = null;
	private Vector<String> ipAddressList = null; 
	*/
	
	private static long mac = 0;
	private static long ip = 0;
	
	AddressManager() {
	}
	
	public static String getMacAddress() {
		StringBuffer sb = new StringBuffer();
		sb.append(MAC_PREFIX);
		sb.append(MAC_SEPARATOR);
		for (int i = 0 ; i < 5; i++) {
    		sb.append(String.format("%02X", (mac >>> (8*(4-i)))&0xFF));
    		if (i < 4) {
        		sb.append(MAC_SEPARATOR);
    		}
		}
    	mac++;
		return sb.toString();
	}
	
	public static String getIpAddress() {
		StringBuffer sb = new StringBuffer();
		sb.append(IP_PREFIX);
		for (int i = 0 ; i < 4; i++) {
    		sb.append(String.format("%X", (ip >>> (16*(3-i)))&0xFFFF));
    		if (i < 3) {
        		sb.append(IP_SEPARATOR);
    		}
		}
		ip++;
		return sb.toString();
	}
	
	public static void main(String args[]) {
		for (int i = 0; i < 1000000; i++) {
       		AddressManager.getMacAddress();
			if (i > 500000) {
        		System.out.println(i+"th: " + AddressManager.getMacAddress());
			}
		}
	}
}
