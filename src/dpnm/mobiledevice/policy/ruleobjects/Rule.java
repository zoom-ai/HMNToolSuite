package dpnm.mobiledevice.policy.ruleobjects;

import java.util.ArrayList;
import java.util.Iterator;

// A rule object that contains name, event, conditions, and action
public class Rule {
	private String name;
	private int event;
	private ArrayList<Atom> conditions;
	private Action action;
	
	public Rule() {
		conditions = new ArrayList<Atom>();
	}
	public Rule(String name, int event, ArrayList<Atom> conditions,	Action action) {
		this.name = name;
		this.event = event;
		this.conditions = conditions;
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}
	
	
	public String getEventString() {
		return Event.EVENT_STR[this.event];
//		switch(this.event) {
//		case Event.VOICECALL: return "voice_call";
//		case Event.VIDEOCALL: return "video_call";
//		case Event.FTP: return "ftp";
//		case Event.STREAMING: return "streaming";
//		case Event.WEBBROWSER: return "web_browser";
//		case Event.SMS: return "sms";
//		}
//		return null;
	}
	
	public void setEvent(String event) {
		for (int i = 0; i < Event.EVENT_STR.length; i++) {
			if (event.intern() == Event.EVENT_STR[i].intern()) {
				this.event = i;
			}
		}
	}
	
	public ArrayList<Atom> getConditions() {
		return conditions;
	}
	public void setConditions(ArrayList<Atom> conditions) {
		this.conditions = conditions;
	}
	public void addCondition(Atom condition) {
		conditions.add(condition);
	}
	public void removeCondition(String var) {
		Atom atom;
		Iterator<Atom> it = this.conditions.iterator();
		while (it.hasNext()) {
			atom = it.next();
			if (var.equals(atom.getVar()) == true) {
				this.conditions.remove(atom);				
			}
		}
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getName() + ": ");
		buffer.append("Event: " + getEventString()+", ");
		buffer.append("Condition: ");
		if (conditions != null) {
			for (int i = 0; i < conditions.size(); i++) {
				buffer.append(conditions.get(i));
			}
		} else {
			buffer.append("NONE");
		}
		buffer.append(", ");
		buffer.append("Action: " + action);
		return buffer.toString();
	}
	
	public String toFileName() {
		return new String(action.getDecisionAlgorithmString()+"_"+action.getUserProfile().toFileName());
	}
}
