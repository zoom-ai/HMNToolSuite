package dpnm.featuremodel;

public class StaticFeatureModel {
	public static final String APPLICATION = "Application";
	public static final String NETWORKINTERFACE = "NetworkInterface";
	
	private static final String[] apps = {"VoiceCall", "Streaming", "FTP", "VideoCall", "WebBrowser", "SMS"};
	private static final String[] nis = {"CDMA", "HSDPA", "WiBro", "WLAN", "GSM", "BlueTooth"};
	
	private static StaticFeatureModel _instance = null;
	
	public static StaticFeatureModel getInstance() {
		if (_instance == null) {
			_instance = new StaticFeatureModel("Mobile Device");
		}
		return _instance;
	}
	
	private String name = null;
	
	public StaticFeatureModel(String name) {
		this.name = name;
	}
	
	public String[] getFeatures(String name) {
		if (name.intern() == APPLICATION.intern()) {
			return apps;
		} else if (name.intern() == NETWORKINTERFACE.intern()) {
			return nis;
		}
		return null;
	}
	
	public int getFeatureCount(String name) {
		return getFeatures(name) == null ? 0 : getFeatures(name).length;
	}
}
