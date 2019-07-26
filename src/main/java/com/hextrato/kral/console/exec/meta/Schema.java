package com.hextrato.kral.console.exec.meta;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

public class Schema implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("schema"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "desc", "foreach", "hextract", "list", "save", "select"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("schema").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		String schemaName = KCFinder.which(clmd, "schema");
		if (!KConsole.schemata().exists(schemaName) && KConsole.isMetadataAutocreate())
			return (new SchemaCreate()).exec(clmd);
		else
			return (new SchemaSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		String schemaName = KCFinder.which(clmd, "schema");
		KConsole.schemata().create(schemaName);
		KConsole.feedback("Schema '"+schemaName+"' created");
		KConsole.metadata("Schema", schemaName);
		return true;
	}
	
	public static boolean doDelete(KCMetadata clmd) throws KException {
		String schemaName = KCFinder.which(clmd, "schema");
		KConsole.schemata().delete(schemaName);
		KConsole.feedback("Schema '"+schemaName+"' deleted");
		KConsole.metadata("Schema", schemaName);
		return true;
	}
	
	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KConsole.println("schema._uid_ = " + schema.getUID());
		KConsole.println("schema._name_ = " + schema.getName());

		String splitList = "{";
		for (String splitName : schema.splits().theList().keySet() ) {
			splitList = splitList + (splitList.equals("{")?"":",") + (splitName.equals(schema.splits().getCurrent())?"*":"") + splitName;
		}
		splitList = splitList + "}";
		KConsole.println("schema.splits = " + splitList);

		String graphList = "{";
		for (String graphName : schema.graphs().theList().keySet() ) {
			graphList = graphList + (graphList.equals("{")?"":",") + (graphName.equals(schema.graphs().getCurrent())?"*":"") + graphName;
		}
		graphList = graphList + "}";
		KConsole.println("schema.graphs = " + graphList);
		
		String tabularList = "{";
		for (String tabularName : schema.tabulars().theList().keySet() ) {
			tabularList = tabularList + (tabularList.equals("{")?"":",") + (tabularName.equals(schema.tabulars().getCurrent())?"*":"") + tabularName;
		}
		tabularList = tabularList + "}";
		KConsole.println("schema.tabulars = " + tabularList);
		KConsole.metadata("Schema", schema.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		String searchSchemaName = clmd.getVar("schema");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String schemaName : KConsole.schemata().theList().keySet()) {
			KSchema schema = KConsole.schemata().getSchema(schemaName);
			if (("["+schema.getName()+"]").contains(searchSchemaName)) {
				KConsole.schemata().setCurrent(schemaName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No schemas found");
		return true;
	}
	
	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String fileName = KCFinder.which(clmd, "file");
		schema.hextract(fileName);
		KConsole.feedback("Schema '"+schema.getName()+"' hextracted");
		KConsole.metadata("Schema", schema.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		String searchSchemaName = clmd.getVar("schema");
		for (String schemaName : KConsole.schemata().theList().keySet()) {
			KSchema schema = KConsole.schemata().getSchema(schemaName);
			if (("["+schema.getName()+"]").contains(searchSchemaName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+KConsole.schemata().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+KConsole.schemata().getPropertySize("_name_")+"s", "_name_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+KConsole.schemata().getPropertySize("_uid_")+"s", schema.getUID());
				output = output + "\t";
				output = output + String.format("%-"+KConsole.schemata().getPropertySize("_name_")+"s", schema.getName());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No schemas found");
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = schema.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		String schemaName = KCFinder.which(clmd, "schema");
		KConsole.schemata().setCurrent(schemaName);
		KConsole.feedback("Schema '"+schemaName+"' selected");
		KConsole.metadata("Schema", schemaName);
		return true;
	}

}

