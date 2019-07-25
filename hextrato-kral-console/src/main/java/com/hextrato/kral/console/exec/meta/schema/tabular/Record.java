package com.hextrato.kral.console.exec.meta.schema.tabular;

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

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "show", "attribute", "foreach", "save"}; }
	
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

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		String searchRecordSplit = clmd.getParameter("_split_");
		String searchRecordUID = clmd.getVar("_uid_");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String recordUID : tabular.records().theList().keySet()) {
			KRecord record = tabular.records().getRecord(recordUID);
			if (	("["+record.getUID()+"]").contains(searchRecordUID)
					&&
					("["+record.getSplit().getName()+"]").contains(searchRecordSplit)
			) {
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
		String searchRecordSplit = clmd.getParameter("_split_");
		String searchRecordUID = clmd.getVar("_uid_");
		for (String recordUID : tabular.records().theList().keySet()) {
			KRecord record = tabular.records().getRecord(recordUID);
			if (	("["+record.getUID()+"]").contains(searchRecordUID)
					&&
					("["+record.getSplit().getName()+"]").contains(searchRecordSplit)
			) {
				if (count==0) {
					String output = "";
					output = output + String.format("%-"+tabular.records().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize("_tabular_")+"s", "_tabular_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize("_split_")+"s", "_split_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.records().getPropertySize("_uid_")+"s", "_uid_");
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
				output = output + String.format("%-"+tabular.records().getPropertySize("_schema_")+"s", record.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.records().getPropertySize("_tabular_")+"s", record.getProperty("_tabular_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.records().getPropertySize("_split_")+"s", record.getProperty("_split_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.records().getPropertySize("_uid_")+"s", record.getProperty("_uid_"));
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
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = "";
		switch (property) {
		case "schema":	value = record.getTabular().getSchema().getName();	break;
		case "tabular":	value = record.getTabular().getName();				break;
		case "split": 	value = record.getSplit().getName();				break;
		case "uid":		value = record.getUID();							break;
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
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String recordUID = KCFinder.which(clmd, "uid");
		tabular.records().setCurrent(recordUID);
		KConsole.feedback("Record '"+recordUID+"' selected");
		KConsole.metadata("Record", recordUID);
		return true;
	}

	public static boolean doAttributeSave(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		// String attribute = CLFinder.which(clmd, "attribute");
		String variable = KCFinder.which(clmd, "variable");
		KConsole.vars().set(variable, record.getAttributeValue(attribute.getName()));
		KConsole.feedback("Attribute '"+attribute.getName()+"' saved	");
		KConsole.metadata("Attribute", attribute.getName(), record.getAttributeValue(attribute.getName()) );
		return true;
	}

	public static boolean doAttributeSet(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		String attribute = KCFinder.which(clmd, "attribute");
		String value = KCFinder.which(clmd, "value");
		record.setAttributeValue(attribute, value);
		KConsole.feedback("Attribute '"+attribute+"' set");
		KConsole.metadata("Attribute", attribute, value);
		return true;
	}
	
	public static boolean doAttributeUnset(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		String attribute = KCFinder.which(clmd, "attribute");
		record.unsetAttributeValue(attribute);
		KConsole.metadata("Attribute", attribute);
		return true;
	}


}

