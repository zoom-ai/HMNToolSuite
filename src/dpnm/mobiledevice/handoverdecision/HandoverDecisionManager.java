package dpnm.mobiledevice.handoverdecision;

import dpnm.mobiledevice.policy.ruleobjects.Action;

public class HandoverDecisionManager {
	public static HandoverDecisionMaker createHandoverDecisionMaker(Action action) {
		switch(action.getDecisionAlgorithm()) {
//		case Action.RANDOM: return new RandomHandoverDecisionMaker();
//		case Action.RSS: return new RSSHandoverDecisionMaker();
//		case Action.COST: return new CostHandoverDecisionMaker();
//		case Action.QUALITY: return new QualityHandoverDecisionMaker();
//		case Action.LIFETIME: return new LifetimeHandoverDecisionMaker();
//		case Action.AUHO: return new AutonomicHandoverDecisionMaker();
		}
		return null;
	}
}
