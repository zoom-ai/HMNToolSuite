package dpnm.mobiledevice.policy.rulemanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import dpnm.mobiledevice.policy.ruleobjects.*;

public class RuleCompiler extends DefaultHandler {
	private RuleBase rulebase;
	private Rule newRule;
	private Atom newAtom;
	
	private boolean in_rulebasetag = false;
	private boolean in_ruletag = false;
	private boolean in_nametag = false;
	private boolean in_eventtag = false;
	private boolean in_conditiontag = false;
	private boolean in_atomtag = false;
	private boolean in_vartag = false;
	private boolean in_valuetag = false;
	private boolean in_actiontag = false;
	
	// Constructors
	public RuleCompiler() {
	}	

	public RuleBase buildRuleBase(String filename) {
		InputStream is = null;
		rulebase = new RuleBase(); // create new rulebase
		if (!(new File(filename)).exists()) {
			return rulebase;
		}
		try {
			is = new FileInputStream(filename);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			SAXParserFactory parserModel = SAXParserFactory.newInstance();
			SAXParser concreteParser = parserModel.newSAXParser();
			XMLReader myReader = concreteParser.getXMLReader();
			myReader.setContentHandler(this);
			myReader.parse(new InputSource(is));			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return rulebase;
	}
	
	// Process start of each tag
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		if (qName.equalsIgnoreCase("rulebase")) {
			in_rulebasetag = true;			
		} else if (qName.equalsIgnoreCase("rule")) {
			in_ruletag = true;
			newRule = new Rule();
		} else if (qName.equalsIgnoreCase("name")) {
			in_nametag = true;
		} else if (qName.equalsIgnoreCase("event")) {
			in_eventtag = true;
		} else if (qName.equalsIgnoreCase("condition")) {
			in_conditiontag = true;			
		} else if (qName.equalsIgnoreCase("atom")) {
			in_atomtag = true;
			newAtom = new Atom();
		} else if (qName.equalsIgnoreCase("var")) {
			in_vartag = true;
		} else if (qName.equalsIgnoreCase("value")) {
			in_valuetag = true;
		} else if (qName.equalsIgnoreCase("action")) {
			in_actiontag = true;
		}		
	}
	
	// Process end of each tag
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("rulebase")) {
			in_rulebasetag = false;
		} else if (qName.equalsIgnoreCase("rule")) {
			in_ruletag = false;
			rulebase.addRule(newRule);
		} else if (qName.equalsIgnoreCase("name")) {
			in_nametag = false;
		} else if (qName.equalsIgnoreCase("event")) {
			in_eventtag = false;
		} else if (qName.equalsIgnoreCase("condition")) {
			in_conditiontag = false;			
		} else if (qName.equalsIgnoreCase("atom")) {
			in_atomtag = false;
			newRule.addCondition(newAtom);
		} else if (qName.equalsIgnoreCase("var")) {
			in_vartag = false;
		} else if (qName.equalsIgnoreCase("value")) {
			in_valuetag = false;
		} else if (qName.equalsIgnoreCase("action")) {
			in_actiontag = false;
		}
	}
	
	// Process each value in each tag
	public void characters(char[] chars, int start, int length) {
		if (in_rulebasetag) {
			if (in_ruletag) {
				if (in_nametag) {
					newRule.setName(new String(chars, start, length));
				} else if (in_eventtag) {
					String event = new String(chars, start, length);
					newRule.setEvent(event);
				} else if (in_conditiontag) { 
					if (in_atomtag) {
						if (in_vartag) {
							newAtom.setVar(new String(chars, start, length));
						} else if (in_valuetag) {
							newAtom.addValue(new String(chars, start, length));
						}
					}
				} else if (in_actiontag) {
					newRule.setAction(new Action(new String(chars, start, length)));
				}
			}
		}
	}
}
