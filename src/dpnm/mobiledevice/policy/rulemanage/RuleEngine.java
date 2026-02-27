package dpnm.mobiledevice.policy.rulemanage;

import java.util.ArrayList;
import java.util.Iterator;

import dpnm.mobiledevice.policy.ruleobjects.*;
import dpnm.mobiledevice.policy.contextobjects.*;

public class RuleEngine {
	private static final int STATUS = 0;
	private static final int CALLER = 1;
	private static final int LOCATION = 2;
	private static final int NUMBER = 3;
	
	public RuleEngine() {
		
	}
	
	public Action run(Event ev, RuleBase rb, ContextFacts cf) {
		boolean condSatisfied;
		Rule rule;
		Atom condition;
		Iterator<Rule> itRules = rb.getRules().iterator();
		Iterator<Atom> itCondition;
		while (itRules.hasNext()) {
			rule = itRules.next();
			// compare rules that correspond to ev
			if (ev.getType() == rule.getEvent()) {
				// compare condition and facts
				condSatisfied = true;
//				if (rule.getConditions() != null) {
//					itCondition = rule.getConditions().iterator();
//					while (itCondition.hasNext()) {
//						condition = itCondition.next();
//						
//						if (condition.getVar().equals("status")) {
//							if (compareContext(condition.getValues(), cf, STATUS) == true) {
//								condSatisfied = true;
//							} else {
//								condSatisfied = false;
//								break;
//							}
//						} else if (condition.getVar().equals("caller")) {
//							if (compareContext(condition.getValues(), cf, CALLER) == true) {
//								condSatisfied = true;
//							} else {
//								condSatisfied = false;
//								break;
//							}
//						} else if (condition.getVar().equals("phone_number")) {
//							if (compareContext(condition.getValues(), cf, NUMBER) == true) {
//								condSatisfied = true;
//							} else {
//								condSatisfied = false;
//								break;
//							}
//						} else if (condition.getVar().equals("location")) {
//							if (compareContext(condition.getValues(), cf, LOCATION) == true) {
//								condSatisfied = true;
//							} else {
//								condSatisfied = false;
//								break;
//							}
//						}
//					}
//				} else {
//					condSatisfied = true;
//				}
//				
				// When the condition is satisfied
				if (condSatisfied == true) {
					return rule.getAction();					
				}	
			}
		}		
		return null; // If there is no matched rule 
	}
	
	private boolean compareContext(ArrayList<String> values, ContextFacts cf, int contextType) {
		boolean flag = false;
		String currentContext;
		String ruleCondition;
		
		switch (contextType) {
		case STATUS:
			currentContext = cf.getSchedule().getStatus();
			break;
		case CALLER:
			currentContext = cf.getCaller().getName();
			break;
		case NUMBER:
			currentContext = cf.getCaller().getPhoneNumber();
			break;
		case LOCATION:
			currentContext = cf.getLocation().getName();
			break;
		default:
			return false;	
		}		
		
		Iterator<String> it = values.iterator();
		while (it.hasNext()) {
			ruleCondition = it.next();
			if (ruleCondition.equals(currentContext)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
}
