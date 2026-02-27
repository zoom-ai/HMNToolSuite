package dpnm.mobiledevice;

public class UserPreference {
	public static final int RSS = 0x01;
	public static final int COST = 0x02;
	public static final int QUALITY = 0x04;
	public static final int LIFETIME = 0x08;

	public int preference = 0x00;
	
	public UserPreference() {
		
	}
	
	public UserPreference(int pref) {
		this.preference = pref;
	}
	
	public void setPreference(int pref) {
		this.preference |= pref;
	}
	
	public int getPreference() {
		return this.preference;
	}
	
	public boolean isOn(int pref) {
		return (this.preference & pref) > 0;
	}
	
	public boolean isOff(int pref) {
		return (this.preference & pref) == 0;
	}
	
	public int getNumberOfPreferences() {
		int count = 0;
		int pref = 1;
		for (int i = 0; i < 4; i++) {
			if (isOn(pref)) {
				count++;
			}
			pref *= 2;
		}
		return count;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (isOn(RSS)) buffer.append("[RSS]");
		if (isOn(COST)) buffer.append("[COST]");
		if (isOn(QUALITY)) buffer.append("[QUALITY]");
		if (isOn(LIFETIME)) buffer.append("[LIFETIME]");
		return buffer.toString();
	}

	public String toFileName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("_");
		if (isOn(RSS)) buffer.append("R");
		if (isOn(COST)) buffer.append("C");
		if (isOn(QUALITY)) buffer.append("Q");
		if (isOn(LIFETIME)) buffer.append("L");
		return buffer.toString();
	}

	public static void main(String args[]) {
		UserPreference up = new UserPreference();
		up.setPreference(UserPreference.RSS);
		System.out.println("RSS: " + up.getNumberOfPreferences());
		up.setPreference(UserPreference.QUALITY);
		System.out.println("RSS: " + up.getNumberOfPreferences());

	}
}
