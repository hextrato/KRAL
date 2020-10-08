package com.hextrato.kral.console.exec.meta.tabular;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.tabular.KAttribute;
import com.hextrato.kral.core.schema.tabular.KRecord;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Record implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("uid"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "show", "attribute", "foreach", "count", "find", "save"}; }
	
	// 

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String recordUID = KCFinder.which(clmd, "uid");
		if (!tabular.records().exists(recordUID) && KConsole.isMetadataAutocreate())
			return (new RecordCreate()).exec(clmd);
		else
			return (new RecordSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String recordUID = clmd.find("uid"); // === which, but no Exception
		if (recordUID.trim().equals("")) {
			tabular.records().create();
		} else {
			tabular.records().create(recordUID);
		}
		KConsole.feedback("Record '"+recordUID+"' created");
		KConsole.metadata("Record", recordUID);
		KConsole.lastString(recordUID); // ** NEW ** //
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String recordUID = clmd.getVar("uid");
		if (recordUID.equals("")) {
			KConsole.error("which record?");
		} else {
			tabular.records().delete(recordUID);
			KConsole.feedback("Record '"+recordUID+"' deleted");
			KConsole.metadata("Record", recordUID);
		}
		return true;
	}

	public static boolean doShow(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		KConsole.println("_.schema = " + record.getTabular().getSchema().getName()); // ** NEW ** //
		KConsole.println("_.tabular = " + record.getTabular().getName()); // ** NEW ** //
		KConsole.println("_.split = " + record.getSplit().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + record.getUID()); // ** NEW ** //
		for (String attributeUID : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeUID);
			String attributeName = attribute.getName();
			KConsole.println("attribute."+attributeName+" = " + record.getAttributeValue(attributeName)); // ** NEW ** //
		}
		KConsole.metadata("Record", record.getUID());
		KConsole.lastString(record.getUID()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KRecord record, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KRecord.__INTERNAL_PROPERTY_SCHEMA__);
		String searchTable = clmd.getParameter(KRecord.__INTERNAL_PROPERTY_TABULAR__);
		String searchSplit = clmd.getParameter(KRecord.__INTERNAL_PROPERTY_SPLIT__);
		String searchUID = clmd.getParameter(KRecord.__INTERNAL_PROPERTY_UID__);
		boolean match = false;
		if ( true
				&& ("["+record.getTabular().getSchema()+"]").contains(searchSchema)
				&& ("["+record.getTabular()+"]").contains(searchTable)
				&& ("["+record.getSplit()+"]").contains(searchSplit)
				&& ("["+record.getUID()+"]").contains(searchUID)
				) {
			match = true;
			KTabular tabular = record.getTabular();
			for (String attributeUID : tabular.attributes().theList().keySet()) {
				KAttribute attribute = tabular.attributes().getAttribute(attributeUID);
				String attributeName = attribute.getName();
				String attributeSearch = clmd.getParameter(attributeName);
				match = match & ("["+record.getAttributeValue(attributeName)+"]").contains(attributeSearch);
			}
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		for (String recordUID : tabular.records().theList().keySet()) {
			KRecord record = tabular.records().getRecord(recordUID);
			if (Record.matches(record,clmd)) {
				count++;
			}
		}
		KConsole.feedback("Count = " + count); // ** NEW ** //
		KConsole.lastInteger(count); // ** NEW ** //
		return true;
	}

	public static boolean doFind(KCMetadata clmd) throws KException {
		KConsole.lastFound(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		for (String recordUID : tabular.records().theList().keySet()) {
			KRecord record = tabular.records().getRecord(recordUID);
			if (Record.matches(record,clmd)) {
				tabular.records().setCurrent(recordUID);
				KConsole.feedback("Found: " + recordUID);
				KConsole.lastFound(recordUID); // ** NEW ** //
				return true;
			}
		}
		KConsole.feedback("Not found");
		KConsole.lastFound(""); // ** NEW ** //
		return true;
	}
	
	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String recordUID : tabular.records().theList().keySet()) {
			KRecord record = tabular.records().getRecord(recordUID);
			if (Record.matches(record,clmd)) {
				tabular.records().setCurrent(recordUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No records found");
		return true;
	}
	
	public static boolean doList(KCMetadata clmd) throws KException {
		long count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		for (String recordUID : tabular.records().theList().keySet()) {
			KRecord record = tabular.records().getRecord(recordUID);
			if (Record.matches(record,clmd)) {
				if (count==0) {
					String output = "";
					output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_SCHEMA__)+"s", KRecord.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_TABULAR__)+"s", KRecord.__INTERNAL_PROPERTY_TABULAR__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_SPLIT__)+"s", KRecord.__INTERNAL_PROPERTY_SPLIT__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_UID__)+"s", KRecord.__INTERNAL_PROPERTY_UID__);
					// for (String attribute : record.values().keySet()) {
					for (String attribute : tabular.attributes().theNames().keySet()) {
						//if (attribute.length() > 20) attribute = attribute.substring(0, 20);
						//output = output + String.format(" %-20s",attribute);
						output = output + "\t";
						output = output + String.format("%-"+tabular.records().getPropertySize(attribute)+"s", attribute);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_SCHEMA__)+"s", record.getTabular().getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_TABULAR__)+"s", record.getTabular().getName());
				output = output + "\t";
				output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_SPLIT__)+"s", record.getSplit().getName());
				output = output + "\t";
				output = output + String.format("%-"+tabular.records().getPropertySize(KRecord.__INTERNAL_PROPERTY_UID__)+"s", record.getUID());
				// for (String attribute : record.values().keySet()) {
				for (String attribute : tabular.attributes().theNames().keySet()) {
					String value = record.getAttributeValue(attribute);
					//if (value.length() > 20) value = value.substring(0, 20);
					//output = output + String.format(" %-20s",value);
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize(attribute)+"s", value);
				}
				KConsole.output(output);
				count++;
			}
		}
		if (count==0)
			KConsole.feedback("No records found");
		else
			KConsole.feedback(count+" found");
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = "";
		switch (property) {
		case "_.schema":	value = record.getTabular().getSchema().getName();	break;
		case "_.tabular":	value = record.getTabular().getName();				break;
		case "_.split": 	value = record.getSplit().getName();				break;
		case "_.uid":		value = record.getUID();							break;
		default:
			if (record.getTabular().attributes().exists(property)) {
				value = record.getAttributeValue(property);
			} else {
				throw new KException ("Invalid property '"+property+"'");
			}
		}
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String recordUID = KCFinder.which(clmd, "uid");
		tabular.records().setCurrent(recordUID);
		KConsole.feedback("Record '"+recordUID+"' selected");
		KConsole.metadata("Record", recordUID);
		KConsole.lastString(recordUID); // ** NEW ** //
		return true;
	}

	public static boolean doAttributeSave(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		// String attribute = CLFinder.which(clmd, "attribute");
		String variable = KCFinder.which(clmd, "variable");
		KConsole.vars().set(variable, record.getAttributeValue(attribute.getName()));
		KConsole.feedback("Attribute '"+attribute.getName()+"' saved	");
		KConsole.metadata("Attribute", attribute.getName(), record.getAttributeValue(attribute.getName()) );
		KConsole.lastString(record.getAttributeValue(attribute.getName())); // ** NEW ** //
		return true;
	}

	public static boolean doAttributeSet(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		String attribute = KCFinder.which(clmd, "attribute");
		String value = KCFinder.which(clmd, "value");
		record.setAttributeValue(attribute, value);
		KConsole.feedback("Attribute '"+attribute+"' set");
		KConsole.metadata("Attribute", attribute, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}
	
	public static boolean doAttributeUnset(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		String attribute = KCFinder.which(clmd, "attribute");
		record.unsetAttributeValue(attribute);
		KConsole.metadata("Attribute", attribute);
		return true;
	}


}

