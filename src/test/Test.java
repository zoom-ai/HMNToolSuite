package test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dpnm.mobiledevice.MobileDevice;
import dpnm.network.*;
import dpnm.network.device.*;
import dpnm.tool.*;
import dpnm.tool.data.*;

import java.awt.Color;

public class Test {

	
	public static void main(String args[]) {
//		NetworkMapInfo mapInfo = createNetworkMapInfo();
/*		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		DocumentBuilder builder;
		NetworkMapInfo mapInfo = null;
		try {
			builder = factory.newDocumentBuilder();
    		Document doc = builder.parse(new File("data/ut_austin.xml"));
    		Element root = doc.getDocumentElement();
    		NodeList nodes = root.getElementsByTagName(NetworkMapInfo.MAP);
    		mapInfo = new NetworkMapInfo(nodes.item(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/	
		/*
		HMNEmulator emulator = new HMNEmulator("Heterogeneous Mobile Network Emulator - Austin");
		emulator.openNetworkMap("data/ut_austin.xml");
		emulator.setVisible(true);
		*/
//		emulator.openMonitorView();
		
		String test = "a|b|c|d|";
		java.util.StringTokenizer st = new java.util.StringTokenizer(test, "|");
		System.out.println(st.countTokens());
		
		int sX = 10;
		int sY = 10;
		int cX = sX;
		int cY = sY;
		int dX = 5;
		int dY = 5;
		
		int count = 0;
		
		while(!(cX == dX && cY == dY)) {
			count++;
    		//	move current point -> destination point
    		//	1. check direction
    		if (sX == dX) { // vertical
    			if (sY >= dY) {
    				sY--;
    			} else {
    				sY++;
    			}
    		} else if (sY == dY) { //	horizontal
    			if (sX >= dX) {
    				sX--;
    			} else {
    				sX++;
    			}
    		} else if (sX < dX && sY < dY) {	//	1
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX+count;
    				cY = sY+count;
    			} else if (dX - sX > dY - sY) {		//	1-1
    				cX = sX+count;
    				cY = sY+(int)((double)(dY-sY)/(dX-sX)*count);
    			} else {
    				cY = sY+count;
    				cX = sX+(int)((double)(dX-sX)/(dY-sY)*count);
    			}
    		} else if (sX > dX && sY < dY) {	//  2
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX-count;
    				cY = sY+count;
    			} else if (sX - dX > dY - sY) {		//	2-1
    				cX = sX-count;
    				cY = sY+(int)((double)(dY-sY)/(sX-dX)*count);
    			} else {
    				cY = sY+count;
    				cX = sX-(int)((double)(sX-dX)/(dY-sY)*count);
    			}
    		} else if (sX > dX && sY > dY) {	//  3
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX-count;
    				cY = sY-count;
    			} else if (sX - dX > sY - dY) {		//	1-1
    				cX = sX-count;
    				cY = sY-(int)((double)(sY-dY)/(sX-dX)*count);
    			} else {
    				cY = sY-count;
    				cX = sX-(int)((double)(sX-dX)/(sY-dY)*count);
    			}
    		} else if (sX < dX && sY > dY) {	//  4
    			if (dX - sX == dY - sY) {		//	1-1
    				cX = sX+count;
    				cY = sY-count;
    			} else if (dX - sX > sY - dY) {		//	1-1
    				cX = sX+count;
    				cY = sY-(int)((double)(sY-dY)/(dX-sX)*count);
    			} else {
    				cY = sY-count;
    				cX = sX+(int)((double)(dX-sX)/(sY-dY)*count);
    			}
    		}
    		
    		System.out.println(cX+","+cY);
		}
	}
}
