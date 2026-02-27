package test;

import dpnm.tool.HMNEmulator;

public class TestRandom {
	public static void main(String args[]) {
    	HMNEmulator emulator = new HMNEmulator();
    	emulator.simulate("data"+java.io.File.separator+args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), 
    			"", Integer.parseInt(args[4]), Integer.parseInt(args[5]));
	}
}
