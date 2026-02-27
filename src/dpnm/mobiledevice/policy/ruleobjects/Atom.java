package dpnm.mobiledevice.policy.ruleobjects;

import java.util.ArrayList;

// An atom object that contains variable(condition type) and value;
public class Atom {
	private String var;
	private ArrayList<String> values;
	
	public Atom() {
		values = new ArrayList<String>();
	}
	
	public String getVar() {
		return var;
	}
	public void setVar(String var) {
		this.var = var;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	public void addValue(String value) {
		this.values.add(value);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(var+"=");
		for (int i = 0; i < values.size(); i++) {
			buffer.append(values.get(i)+"|");
		}
		return buffer.toString();
	}
}
