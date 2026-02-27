package dpnm.mobiledevice;

public class UserProfile {
	//	predefined table for user preferences
	private static final double UP_TABLE[][] ={
		//	RSS, COST, QUALITY, LIFETIME
		{0.0, 0.0, 0.0, 0.0},	//	NONE
		{0.1, 0.1, 0.1, 0.7},	//	RSS,							0001
		{0.1, 0.1, 0.7, 0.1}, 	//	COST, 							0010
		{0.1, 0.1, 0.4, 0.4}, 	//  RSS & COST						0011
		{0.1, 0.7, 0.1, 0.1},	// 	QUALITY							0100
		{0.1, 0.4, 0.1, 0.4},	//  RSS & QUALITY					0101	
		{0.1, 0.4, 0.4, 0.1}, 	//  COST & QUALITY					0110
		{0.1, 0.3, 0.3, 0.3}, 	//  RSS & COST & QUALITY			0111
		{0.7, 0.1, 0.1, 0.1}, 	//  LIFETIME						1000
		{0.4, 0.1, 0.1, 0.4}, 	//  RSS & LIFETIME					1001
		{0.4, 0.1, 0.4, 0.1}, 	//  COST & LIFETIME					1010
		{0.3, 0.1, 0.3, 0.3}, 	//  RSS & COST & LIFETIME			1011
		{0.4, 0.4, 0.1, 0.1}, 	//  QUALITY & LIFETIME				1100
		{0.3, 0.3, 0.1, 0.3}, 	//  RSS & QUALITY & LIFETIME		1101
		{0.3, 0.3, 0.3, 0.1}, 	//  COST & QUALITY &LIFETIME			1110
		{0.25, 0.25, 0.25, 0.25}, 	//  RSS & COST & QUALITY & LIFETIME	1111
	};
	
	double rss = 0.0;
	double cost = 0.0;
	double lifetime = 0.0;
	double quality = 0.0;
	
	
	private UserPreference preference = null;
	
	public UserProfile() {
		
	}
	
	public UserProfile(double rss, double cost, double quality, double lifetime) {
		this.rss = rss;
		this.quality = quality;
		this.cost = cost;
		this.lifetime = lifetime;
	}

	public void setUserPreference(UserPreference uPref) {
		this.preference = uPref;
		setRss(UP_TABLE[uPref.getPreference()][3]);
		setCost(UP_TABLE[uPref.getPreference()][2]);
		setQuality(UP_TABLE[uPref.getPreference()][1]);
		setLifetime(UP_TABLE[uPref.getPreference()][0]);
	}
	
	public UserPreference getUserPreference() {
		return preference;
	}
	
	public double getRss() {
		return rss;
	}

	public void setRss(double rss) {
		this.rss = rss;
	}

	public double getQuality() {
		return quality;
	}

	public void setQuality(double quality) {
		this.quality = quality;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getLifetime() {
		return lifetime;
	}

	public void setLifetime(double lifetime) {
		this.lifetime = lifetime;
	}
	
	public String toFileName() {
		if (preference != null) {
			return new String("Basic"+preference.toFileName());
		} else {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Advanced_");
			buffer.append("(R-"+getRss()+"_");
			buffer.append("C-"+getCost()+"_");
			buffer.append("Q-"+getQuality()+"_");
			buffer.append("L-"+getLifetime()+")");
			return buffer.toString();
		}
	}
}
