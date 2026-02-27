package test;

import java.awt.*;

import dpnm.mobiledevice.MobileApplication;
import dpnm.tool.data.NetworkMobileNodeInfo;

public class UnitTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
    	int height = 300 * (int)(((double)6/2)+ 0.5);
    	System.out.println(height);
		int r = (int)((double)255 * 100)/100;
		
		System.out.println(r);
		Color c = Color.pink;
		
		System.out.printf("%6x\n", c.getRGB());
		
		c = Color.orange;
		System.out.printf("%6x\n", c.getRGB());
		
		c = Color.cyan;
		System.out.printf("%6x\n", c.getRGB());
		
		c = Color.magenta;
		System.out.printf("%6x\n", c.getRGB());
		
		c = Color.gray;
		System.out.printf("%6x\n", c.getRGB());
//		8e34a4
	}
}
