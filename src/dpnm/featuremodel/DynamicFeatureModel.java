package dpnm.featuremodel;

public class DynamicFeatureModel {
	private String[] apps = null;
	private String[] nis = null;
	private String name = null;
	
	public DynamicFeatureModel(String name) {
		this.name = name;
	}
	
	public String[] getFeatures(String name) {
		if (name.intern() == "Application".intern()) {
			return apps;
		} else {
			return nis;
		}
	}
	
	public void setApplication(String[] apps) {
		this.apps = apps;
	}
	
	public void setNetworkInterface(String[] nis) {
		this.nis = nis;
	}
}
