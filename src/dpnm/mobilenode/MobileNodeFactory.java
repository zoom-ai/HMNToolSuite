package dpnm.mobilenode;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.ImageIcon;
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

import dpnm.network.INetwork;
import dpnm.tool.Resources;
import dpnm.tool.comp.ZFileFilter;
public class MobileNodeFactory {
	public static final int MAX_VELOCITY = 100;
	private static MobileNodeFactory _instance = null;
	
	public static MobileNodeFactory getInstance() {
		if (_instance == null) {
			_instance = new MobileNodeFactory();
		}
		return _instance;
	}
	
	String [] mNodeName = null;
	IMobileNode mNodes[] = null;
	
	public MobileNodeFactory() {
		openMobileNode();
	}
	
	public synchronized void openMobileNode() {
		try {
			File dNode = new File(Resources.HOME+ File.separator + Resources.MOBILENODE_DIR);
			
			ZFileFilter filter = new ZFileFilter();
			filter.addExtension("xml");
			filter.ignoreDirectory(true);
			File mNodeFile[] = dNode.listFiles(filter);
			
			mNodeName = new String[mNodeFile.length];
			mNodes = new IMobileNode[mNodeFile.length];
			
			for (int i = 0; i < mNodeFile.length; i++) {
				if (!mNodeFile[i].isFile()) {
					continue;
				}
				mNodeName[i] = mNodeFile[i].getName().substring(0, mNodeFile[i].getName().length()-4);
    			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    			factory.setValidating(false);
    			factory.setNamespaceAware(false);
    			DocumentBuilder builder = factory.newDocumentBuilder();
    			Document doc = builder.parse(mNodeFile[i]);
    			Element root = doc.getDocumentElement();
    			NodeList nodes = root.getElementsByTagName("mobilenode");
    			MobileNodeImpl mNode = new MobileNodeImpl(nodes.item(0));
    			mNode.setIcon(getImage(Resources.MOBILENODE_DIR + mNode.getIconStr()));
    			mNodes[i] = mNode;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public synchronized void createMobileNode(
			String name, String iconStr, int color, int maxVelocity, int minVelocity) {
		MobileNodeImpl newMobileNode = new MobileNodeImpl();
		newMobileNode.setName(name);
		newMobileNode.setIconStr(iconStr);
    	newMobileNode.setMinVelocity(minVelocity);
		newMobileNode.setMaxVelocity(maxVelocity);
		newMobileNode.setColor(color);
		saveMobileNode(newMobileNode);
	}
	
	public synchronized void modifyMobileNode(
			String name, String iconStr, int color, int maxVelocity, int minVelocity) {
		MobileNodeImpl newMobileNode = (MobileNodeImpl)getMobileNode(name);
		newMobileNode.setName(name);
		newMobileNode.setIconStr(iconStr);
    	newMobileNode.setMinVelocity(minVelocity);
		newMobileNode.setMaxVelocity(maxVelocity);
		newMobileNode.setColor(color);
		saveMobileNode(newMobileNode);
	}
	
	public synchronized void deleteNetwork(String mobileNode) {
		try {
    		File dNetwork = new File(Resources.HOME+ File.separator + Resources.MOBILENODE_DIR+File.separator+mobileNode+".xml");
    		if (dNetwork.exists()) {
    			dNetwork.delete();
    		}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	
	}
	
	public synchronized void saveMobileNode(MobileNodeImpl mobileNode) {
		try {
			File file = new File(Resources.HOME+File.separator+Resources.MOBILENODE_DIR+File.separator+
					mobileNode.getName()+".xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("emulator");
			doc.appendChild(root);
			mobileNode.appendXml(root);
			
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
	
	private ImageIcon getIcon(String iconName) {
		return new ImageIcon(Resources.HOME+File.separator+iconName);
	}
	
	private Image getImage(String iconName) {
		ImageIcon imageIcon = getIcon(iconName);
		return imageIcon.getImage();
	}
	
	public IMobileNode getMobileNode(String mName) {
		for (int i = 0; mNodeName != null && i < mNodeName.length; i++) {
			if (mNodeName[i].intern() == mName.intern()) {
				return mNodes[i];
			}
		}
		return null;
	}

	public IMobileNode[] getMobileNodes() {
		return mNodes;
	}

	public String[] getMobileNodeNames() {
		return mNodeName;
	}
	
	public int getMobileNodeCount() {
		return mNodes == null ? 0 : mNodes.length;
	}
	
	public String getMobileNodeNameAt(int i) {
		return mNodeName == null ? null : mNodeName[i];
	}

	public IMobileNode getMobileNodeAt(int i) {
		return mNodes == null ? null : mNodes[i];
	}
	
	/**
	 * get index of mobile node
	 * @param mobile node name
	 * @return index of given mobile node name, otherwise -1
	 */
	public int getMobileNodeIndex(String name) {
		for (int i = 0; mNodeName != null && i < mNodeName.length; i++) {
			if (mNodeName[i].intern() == name.intern())
				return i;
		}
		return -1;
	}

	public static void main(String args[]) {
			MobileNodeFactory f = MobileNodeFactory.getInstance();
	}
}
