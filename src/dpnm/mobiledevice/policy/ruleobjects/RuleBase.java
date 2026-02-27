package dpnm.mobiledevice.policy.ruleobjects;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

// Rule-base object
public class RuleBase {
	private ArrayList<Rule> rules;
	
	public RuleBase() {
		rules = new ArrayList<Rule>();
	}

	public void addRule(String name, int event, ArrayList<Atom> conditions, Action action) {
		Rule newRule = new Rule(name, event, conditions, action);
		rules.add(newRule);
	}

	public void addRule(Rule newRule) {
		rules.add(newRule);
	}

	public void removeRule(String name) {
		Rule rule;
		Iterator<Rule> it = this.rules.iterator();
		while (it.hasNext()) {
			rule = it.next();
			if (name.intern() == rule.getName().intern()) {
				this.rules.remove(rule);
				break;
			}
		}
	}

	public ArrayList<Rule> getRules() {
		return rules;
	}
	
	public Rule getRuleByName(String name) {
		Iterator<Rule> itRule = rules.iterator();
		Rule rule = null;
		while (itRule.hasNext()) {
			rule = itRule.next();
			if (rule.getName().intern() == name.intern()) {
				break;
			}
		}
		
		if (rule != null) {
			return rule;
		} else {
			return null;
		}
	}
	
	public Rule getRuleByEvent(int event) {
		Iterator<Rule> itRule = rules.iterator();
		Rule rule = null;
		while (itRule.hasNext()) {
			rule = itRule.next();
			if (rule.getEvent() == event) {
				break;
			}
		}
		return rule;
	}
	
	public void deleteAllRule() {
		rules = new ArrayList<Rule>();		
	}
	
	public void writeRule(FileOutputStream fos) {
		String ruleText = "<?xml version=\"1.0\" encoding=\"utf-8\"?><rulebase>";
		
		Rule rule;
		Atom condition;
		String value;		
				
		Iterator<Rule> itRules = rules.iterator();
		Iterator<Atom> itCondition;
		Iterator<String> itValue;
		
		while (itRules.hasNext()) {
			rule = itRules.next();
			ruleText += "<rule>";
			ruleText += "<name>" + rule.getName() + "</name>";
			ruleText += "<event>" + rule.getEventString() + "</event>";

			ruleText += "<condition>";
			if (rule.getConditions() != null) {
				itCondition = rule.getConditions().iterator();
				while (itCondition.hasNext()) {
					condition = itCondition.next();
					ruleText += "<atom>";
					ruleText += "<var>" + condition.getVar() + "</var>";
					
					itValue = condition.getValues().iterator();
					while (itValue.hasNext()) {
						value = itValue.next();
						ruleText += "<value>" + value + "</value>";
					}
					ruleText += "</atom>";
				}
			}
			ruleText += "</condition>";
			
			ruleText += "<action>" + rule.getAction().getActionStr() + "</action>";
			ruleText += "</rule>";
		}
		
		ruleText += "</rulebase>";
		
		try {
			fos.write(ruleText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
