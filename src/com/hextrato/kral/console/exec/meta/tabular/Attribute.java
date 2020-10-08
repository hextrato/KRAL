package com.hextrato.kral.console.exec.meta.tabular;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.tabular.KAttribute;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Attribute implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("attribute"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "count", "find", "save", "hextract"}; }

	//
	
	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String attributeName = KCFinder.which(clmd, "attribute");
		if (!tabular.attributes().exists(attributeName) && KConsole.isMetadataAutocreate())
			return (new AttributeCreate()).exec(clmd);
		else
			return (new AttributeSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String attributeName = KCFinder.which(clmd, "attribute");
		String attributeDatatype = KCFinder.which(clmd, "datatype");
		tabular.attributes().create(attributeName,attributeDatatype);
		KConsole.feedback("Attribute '"+attributeName+"' created");
		KConsole.metadata("Attribute", attributeName);
		KConsole.lastString(attributeName); // ** NEW ** //
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String attributeName = KCFinder.which(clmd, "attribute");
		tabular.attributes().delete(attributeName);
		KConsole.feedback("Attribute '"+attributeName+"' deleted");
		KConsole.metadata("Attribute", attributeName);
		KConsole.lastString(attributeName); // ** NEW ** //
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		KConsole.println("_.schema = " + attribute.getTabular().getSchema().getName()); // ** NEW ** //
		KConsole.println("_.tabular = " + attribute.getTabular().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + attribute.getUID()); // ** NEW ** //
		KConsole.println("_.name = " + tabular.getName()); // ** NEW ** //
		KConsole.println("_.datatype = " + attribute.getDatatype());
		KConsole.metadata("Attribute", attribute.getName());
		KConsole.lastString(attribute.getName()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KAttribute attribute, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KAttribute.__INTERNAL_PROPERTY_SCHEMA__);
		String searchTable = clmd.getParameter(KAttribute.__INTERNAL_PROPERTY_TABULAR__);
		String searchUID = clmd.getParameter(KAttribute.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KAttribute.__INTERNAL_PROPERTY_NAME__);
		String searchDataype = clmd.getParameter(KAttribute.__INTERNAL_PROPERTY_DATATYPE__);
		boolean match = false;
		if ( true
				&& ("["+attribute.getTabular().getSchema()+"]").contains(searchSchema)
				&& ("["+attribute.getTabular()+"]").contains(searchTable)
				&& ("["+attribute.getUID()+"]").contains(searchUID)
				&& ("["+attribute.getName()+"]").contains(searchName)
				&& ("["+attribute.getDatatype()+"]").contains(searchDataype)
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		for (String attributeUID : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeUID);
			if (Attribute.matches(attribute,clmd)) {
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
		for (String attributeUID : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeUID);
			if (Attribute.matches(attribute,clmd)) {
				tabular.attributes().setCurrent(attributeUID);
				KConsole.feedback("Found: " + attributeUID);
				KConsole.lastFound(attributeUID); // ** NEW ** //
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
		for (String attributeUID : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeUID);
			if (Attribute.matches(attribute,clmd)) {
				tabular.attributes().setCurrent(attributeUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No attributes found");
		return true;
	}

	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		String fileName = KCFinder.which(clmd, "file");
		attribute.hextract(fileName);
		KConsole.feedback("Attribute '"+attribute.getName()+"' hextracted");
		KConsole.metadata("Attribute", attribute.getName(), fileName);
	return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		for (String attributeUID : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeUID);
			if (Attribute.matches(attribute,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_SCHEMA__)+"s", KAttribute.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_TABULAR__)+"s", KAttribute.__INTERNAL_PROPERTY_TABULAR__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_UID__)+"s", KAttribute.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_NAME__)+"s", KAttribute.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_DATATYPE__)+"s", KAttribute.__INTERNAL_PROPERTY_DATATYPE__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_SCHEMA__)+"s", attribute.getTabular().getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_TABULAR__)+"s", attribute.getTabular().getName());
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_UID__)+"s", attribute.getUID());
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_NAME__)+"s", attribute.getName());
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize(KTabular.__INTERNAL_PROPERTY_DATATYPE__)+"s", attribute.getDatatype());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No attributes found");
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = attribute.getProperty(property);
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
		String attributeName = KCFinder.which(clmd, "attribute");
		tabular.attributes().setCurrent(attributeName);
		KConsole.feedback("Attribute '"+attributeName+"' selected");
		KConsole.metadata("Attribute", attributeName);
		KConsole.lastString(attributeName); // ** NEW ** //
		return true;
	}


	
}

