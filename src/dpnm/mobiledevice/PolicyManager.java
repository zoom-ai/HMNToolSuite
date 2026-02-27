package dpnm.mobiledevice;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import dpnm.mobiledevice.policy.ruleobjects.*;
import dpnm.mobiledevice.policy.rulemanage.*;
import dpnm.tool.Resources;
import dpnm.tool.comp.ZFileFilter;

public class PolicyManager {
	private static final Action DEFAULT_ACTION = new Action(Action.RSS);
	private static final String RULE_FILE_NAME = "rule.xml";
	private RuleBase rulebase = null;
	private RuleEngine ruleEngine = null;

	private String dName = null;
	private String fName = null;
	
	//	for simulation
	private Rule currentRule = null;
	
	public PolicyManager() {
		rulebase = new RuleBase();
		ruleEngine = new RuleEngine();
	}
	
	public Action getAction(Event evt) {

		if (currentRule != null) {
			return currentRule.getAction();
		}
		Action action = ruleEngine.run(evt, rulebase, null);
		
		return action == null ? DEFAULT_ACTION : action;
	}

	public void addPolicy(String name, int event, ArrayList<Atom> conditions, Action action) {
		rulebase.addRule(name, event, conditions, action);
	}
	
	public void deletePolicy(String name) {
		rulebase.removeRule(name);
	}
	
	public void modifyPolicy(String name, int event, ArrayList<Atom> conditions, Action action) {
		Rule rule = rulebase.getRuleByName(name);
		rule.setEvent(event);
		rule.setConditions(conditions);
		rule.setAction(action);
	}
	
	public ArrayList<Rule> getPolicies() {
		return rulebase.getRules();
	}
	
	public Rule getPolicy(String eventStr) {
		return currentRule != null ? currentRule :
			rulebase.getRuleByEvent(Event.getEvent(eventStr));
	}

	public void load(String name, String id) {
		dName = name;
		fName = id;
		String filename = Resources.DATA_DIR+File.separator+name+File.separator+id+File.separator+RULE_FILE_NAME;
		rulebase = new RuleCompiler().buildRuleBase(filename);
	}

	public void save(String name, String id) {
		dName = name;
		fName = id;
		File d = new File(Resources.DATA_DIR+File.separator+name);
		if (!d.exists()) {
			d.mkdir();
		}
		File dnode = new File(Resources.DATA_DIR+File.separator+name+File.separator+id);
		if (!dnode.exists()) {
			dnode.mkdir();
		}
		File f = new File(Resources.DATA_DIR+File.separator+name+File.separator+id+File.separator+RULE_FILE_NAME);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			rulebase.writeRule(fos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void destroy() {
		String dname = Resources.DATA_DIR+File.separator+dName+File.separator+fName;;
		String filename = dname+File.separator+RULE_FILE_NAME;
		try {
			File f = new File(filename);
			if (f.exists()) {
				f.delete();
			}
			File d = new File(dname);
			if (d.exists()) {
				d.delete();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setCurrentPolicy(Rule rule) {
		this.currentRule = rule;
	}
}
