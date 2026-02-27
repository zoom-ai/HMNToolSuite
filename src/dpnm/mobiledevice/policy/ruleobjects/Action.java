package dpnm.mobiledevice.policy.ruleobjects;

import java.util.StringTokenizer;

import dpnm.mobiledevice.UserProfile;
import dpnm.mobiledevice.UserPreference;

public class Action {
	public static final int RANDOM 		= 0x00;
	public static final int RSS 		= 0x01;
	public static final int COST 		= 0x02;
	public static final int QUALITY 	= 0x03;
	public static final int LIFETIME	= 0x04;
	public static final int AUHO		= 0x05;

	private static final String DTOKEN	= "#";
	private static final String VTOKEN 	= "|";
	
	public static final String DECISION_ALGORITHM_STR[] = {
		"RANDOM", "RSS", "COST", "QUALITY", "LIFETIME", "AUHO"
	};
	public static final String DECISION_ALGORITHM_TITLE[] = {
		"Random Decision", 
		"RSS-based Decision", 
		"COST-based Decision", 
		"QUALITY-based Decision", 
		"LIFETIME-based Decision", 
		"Proposed AUHO Decision"
	};
	
	UserProfile up = null;

	private int decisionAlg = RSS;

	public String getDecisionAlgorithmString() {
		return DECISION_ALGORITHM_STR[decisionAlg];
	}
	public String getDecisionAlgorithmTitle() {
		return DECISION_ALGORITHM_TITLE[decisionAlg];
	}

	public Action(int alg) {
		this.decisionAlg = alg;
	}
	
	public Action(UserProfile up) {
		this.decisionAlg = AUHO;
		this.up = up;
	}
	
	public Action() {
		
	}
	
	public void setDecisionAlgorithm(int decisionAlg) {
		this.decisionAlg = decisionAlg;
	}
	
	public Action(String action) {
		StringTokenizer dst = new StringTokenizer(action, DTOKEN);
		String decision = dst.nextToken();
		for (int i = 0; i < DECISION_ALGORITHM_STR.length; i++) {
			if (decision.intern() == DECISION_ALGORITHM_STR[i].intern()) {
				setDecisionAlgorithm(i);
				break;
			}
		}
//		if (getDecisionAlgorithm() == AUHO) {
			String bMode = dst.nextToken();
			if (bMode.intern() == "BASIC".intern()) {
				UserPreference uPref = new UserPreference(Integer.parseInt(dst.nextToken()));
				up = new UserProfile();
				up.setUserPreference(uPref);
			} else {
				StringTokenizer st = new StringTokenizer(dst.nextToken(), VTOKEN);
				up = new UserProfile();
				while(st.hasMoreTokens()) {
					up.setRss(Double.parseDouble(st.nextToken()));
					up.setCost(Double.parseDouble(st.nextToken()));
					up.setQuality(Double.parseDouble(st.nextToken()));
					up.setLifetime(Double.parseDouble(st.nextToken()));
				}
			}
//		}
	}

	public void setUserProfile(UserProfile up) {
		this.up = up;
	}
	
	public UserProfile getUserProfile() {
		return up;
	}
	
	public String getActionStr() {
		StringBuffer sb = new StringBuffer();
		sb.append(DECISION_ALGORITHM_STR[getDecisionAlgorithm()]+DTOKEN);
		if (getUserProfile().getUserPreference() != null) {	//basic mode
			sb.append("BASIC"+DTOKEN+getUserProfile().getUserPreference().getPreference());
		} else {
			sb.append("ADVANCED"+DTOKEN);
			sb.append(up.getRss()+VTOKEN);
			sb.append(up.getCost()+VTOKEN);
			sb.append(up.getQuality()+VTOKEN);
			sb.append(up.getLifetime()+VTOKEN);
		}
		return sb.toString();
	}
	
	public int getDecisionAlgorithm() {
		return decisionAlg;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getDecisionAlgorithmString());
//		if (getDecisionAlgorithm()==AUHO) {
			if (up.getUserPreference() != null) {
				buffer.append("("+up.getUserPreference().toString()+")");
			} else {
				buffer.append("(RSS:"+up.getRss()+",");
				buffer.append("COST:"+up.getCost()+",");
				buffer.append("QUALITY:"+up.getQuality()+",");
				buffer.append("LIFETIME:"+up.getLifetime()+")");
			}
//		}
		return buffer.toString();
	}
}
