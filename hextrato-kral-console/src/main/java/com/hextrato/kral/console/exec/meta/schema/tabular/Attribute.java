package com.hextrato.kral.console.exec.meta.schema.tabular;

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

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save", "hextract"}; }

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
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String attributeName = KCFinder.which(clmd, "attribute");
		String attributeDatatype = KCFinder.which(clmd, "datatype");
		// tabular.attributes().create(attributeName,clmd.getParameter("datatype"));
		tabular.attributes().create(attributeName,attributeDatatype);
		KConsole.feedback("Attribute '"+attributeName+"' created");
		KConsole.metadata("Attribute", attributeName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String attributeName = KCFinder.which(clmd, "attribute");
		tabular.attributes().delete(attributeName);
		KConsole.feedback("Attribute '"+attributeName+"' deleted");
		KConsole.metadata("Attribute", attributeName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		KConsole.println("schema.name = " + attribute.getTabular().getSchema().getName());
		KConsole.println("tabular.name = " + attribute.getTabular().getName());
		KConsole.println("attribute.name = " + attribute.getName());
		KConsole.println("attribute.datatype = " + attribute.getDatatype());
		KConsole.metadata("Attribute", attribute.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema,clmd);
		String searchAttributeName = clmd.getVar("attribute");
		String searchAttributeType = clmd.getParameter("datatype");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String attributeName : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeName);
			if (("["+attribute.getName()+"]").contains(searchAttributeName) && ("["+attribute.getDatatype()+"]").contains(searchAttributeType)) {
				tabular.attributes().setCurrent(attributeName);
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
		String searchAttributeName = clmd.getVar("attribute");
		String searchAttributeType = clmd.getParameter("datatype");
		for (String attributeName : tabular.attributes().theList().keySet()) {
			KAttribute attribute = tabular.attributes().getAttribute(attributeName);
			if (("["+attribute.getName()+"]").contains(searchAttributeName) && ("["+attribute.getDatatype()+"]").contains(searchAttributeType)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+tabular.attributes().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize("_tabular_")+"s", "_tabular_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+tabular.attributes().getPropertySize("_datatype_")+"s", "_datatype_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+tabular.attributes().getPropertySize("_schema_")+"s", attribute.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize("_tabular_")+"s", attribute.getProperty("_tabular_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize("_uid_")+"s", attribute.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize("_name_")+"s", attribute.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+tabular.attributes().getPropertySize("_datatype_")+"s", attribute.getProperty("_datatype_"));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No attributes found");
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KAttribute attribute = KCFinder.findAttribute(tabular, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = attribute.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String attributeName = KCFinder.which(clmd, "attribute");
		tabular.attributes().setCurrent(attributeName);
		KConsole.feedback("Attribute '"+attributeName+"' selected");
		KConsole.metadata("Attribute", attributeName);
		return true;
	}


	
}

