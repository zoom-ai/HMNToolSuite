package test;

import java.util.*;

import dpnm.Conf;
import dpnm.tool.data.NetworkDeviceInfo;

public class CLISimulator {

	boolean t1end = false;
	boolean t2end = false;
	boolean t3end = false;
	
	public CLISimulator() {
		Task1 t1 = new Task1();
		Task2 t2 = new Task2();
		Task3 t3 = new Task3();
		
		Timer t = new Timer();
		
		try {
			t.schedule(t1, 0, 1000);
			t.schedule(t2, 0, 2000);
			t.schedule(t3, 0, 3000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		while(!t1end) 
		{
			synchronized(this) {
				try {
					Thread.sleep(500);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		try {
			t1.cancel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CLISimulator();
	}
	public class Task1 extends TimerTask {
		int i = 0;
		public synchronized void run() {
    		System.out.println("task1");
    		i++;
    		if (i == 3)
    			t1end = true;
    	}
	}
	public class Task2 extends TimerTask {
    	public synchronized void run() {
    		System.out.println("task2");
    	}
	}
	public class Task3 extends TimerTask {
    	public synchronized void run() {
    		System.out.println("task3");
    	}
	}
	
}
