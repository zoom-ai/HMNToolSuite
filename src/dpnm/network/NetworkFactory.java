package dpnm.network;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dpnm.tool.Resources;
import dpnm.tool.comp.ZFileFilter;

public class NetworkFactory {
	
	private static NetworkFactory _instance = null;
	
	public static NetworkFactory getInstance() {
		if (_instance == null) {
			_instance = new NetworkFactory();
		}
		return _instance;
	}
	
	String [] networkName = null;
	INetwork networks[] = null;
	
	public NetworkFactory() {
		openNetwork(); 
	}
	
	public synchronized void openNetwork() {
		try {
			File dNetwork = new File(Resources.HOME+ File.separator + Resources.NETWORK_DIR);
			
			ZFileFilter filter = new ZFileFilter();
			filter.addExtension("xml");
			File networkFile[] = dNetwork.listFiles(filter);
			
			networkName = new String[networkFile.length];
			networks = new INetwork[networkFile.length];
			
			for (int i = 0; i < networkFile.length; i++) {
				networkName[i] = networkFile[i].getName().substring(0, networkFile[i].getName().length()-4);
    			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    			factory.setValidating(false);
    			factory.setNamespaceAware(false);
    			DocumentBuilder builder = factory.newDocumentBuilder();
    			Document doc = builder.parse(networkFile[i]);
    			Element root = doc.getDocumentElement();
    			NodeList nodes = root.getElementsByTagName("network");
    			networks[i] = new NetworkImpl(nodes.item(0));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}	}
	
	public synchronized void createNetwork(
			String name, String deviceType, int coverage, int bandwidth,  int delay, int jitter, 
			double ber, double throughput, double burstErr, double plr, double costRate,
			double txPower, double rxPower, double idlePower, int minVelocity, int maxVelocity, int color) {
		NetworkImpl newNetwork = new NetworkImpl();
		newNetwork.setName(name);
		newNetwork.setDeviceType(deviceType);
		newNetwork.setCoverage(coverage);
		newNetwork.setBandwidth(bandwidth);
		newNetwork.setDelay(delay);
		newNetwork.setJitter(jitter);
		newNetwork.setBer(ber);
		newNetwork.setThroughput(throughput);
		newNetwork.setBurstErr(burstErr);
		newNetwork.setPlr(plr);
		newNetwork.setCostRate(costRate);
		newNetwork.setTxPower(txPower);
		newNetwork.setRxPower(rxPower);
		newNetwork.setIdlePower(idlePower);
		newNetwork.setMinVelocity(minVelocity);
		newNetwork.setMaxVelocity(maxVelocity);
		newNetwork.setColor(color);
		saveNetwork(newNetwork);
	}
	
	public synchronized void modifyNetwork(
			String name, String deviceType, int coverage, int bandwidth,  int delay, int jitter, 
			double ber, double throughput, double burstErr, double plr, double costRate,
			double txPower, double rxPower, double idlePower, int minVelocity, int maxVelocity, int color) {
		NetworkImpl newNetwork = (NetworkImpl)getNetwork(name);
		newNetwork.setName(name);
		newNetwork.setDeviceType(deviceType);
		newNetwork.setCoverage(coverage);
		newNetwork.setBandwidth(bandwidth);
		newNetwork.setDelay(delay);
		newNetwork.setJitter(jitter);
		newNetwork.setBer(ber);
		newNetwork.setThroughput(throughput);
		newNetwork.setBurstErr(burstErr);
		newNetwork.setPlr(plr);
		newNetwork.setCostRate(costRate);
		newNetwork.setTxPower(txPower);
		newNetwork.setRxPower(rxPower);
		newNetwork.setIdlePower(idlePower);
		newNetwork.setMinVelocity(minVelocity);
		newNetwork.setMaxVelocity(maxVelocity);
		newNetwork.setColor(color);
		saveNetwork(newNetwork);
	}
	
	public synchronized void deleteNetwork(String network) {
		try {
    		File dNetwork = new File(Resources.HOME+ File.separator + Resources.NETWORK_DIR+File.separator+network+".xml");
    		if (dNetwork.exists()) {
    			dNetwork.delete();
    		}
		} catch(Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	public synchronized void saveNetwork(NetworkImpl network) {
		try {
			File file = new File(Resources.HOME+File.separator+Resources.NETWORK_DIR+File.separator+
					network.getName()+".xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("emulator");
			doc.appendChild(root);
			network.appendXml(root);
			
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			DOMSource source = new DOMSource(root);
			FileOutputStream destination = new FileOutputStream(file);
			StreamResult result = new StreamResult(destination);
			transformer.transform(source, result);
			destination.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public INetwork getNetwork(String nName) {
		for (int i = 0; i < networkName.length; i++) {
			if (networkName[i].intern() == nName.intern()) {
				return networks[i];
			}
		}
		return null;
	}

	public String[] getNetworkNames() {
		return networkName;
	}
	
	public INetwork[] getNetworks() {
		return networks;
	}
	
	public int getNetworkCount() {
		return networks == null ? 0 : networks.length;
	}
	
	public String getNetworkNameAt(int i) {
		return networkName == null ? null : networkName[i];
	}

	public INetwork getNetworkAt(int i) {
		return networks == null ? null : networks[i];
	}
	
	/**
	 * get index of network
	 * @param network name
	 * @return index of given network name, otherwise -1
	 */
	public int getNetworkIndex(String name) {
		for (int i = 0; networkName != null && i < networkName.length; i++) {
			if (networkName[i].intern() == name.intern())
				return i;
		}
		return -1;
	}
	public static void main(String args[]) {
			NetworkFactory f = new NetworkFactory();
	}
}
