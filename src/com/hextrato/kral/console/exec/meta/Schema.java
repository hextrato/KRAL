package com.hextrato.kral.console.exec.meta;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Schema implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("schema"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "desc", "foreach", "list", "count", "find", "hextract", "save", "select"}; }

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
	
	private static boolean matches(KSchema schema, KCMetadata clmd) throws KException {
		String searchUID = clmd.getParameter(KSchema.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KSchema.__INTERNAL_PROPERTY_NAME__);
		boolean match = false;
		if ( true
				&& ("["+schema.getUID()+"]").contains(searchUID)
				&& ("["+schema.getName()+"]").contains(searchName)
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		for (String schemaUID : KConsole.schemata().theList().keySet()) {
			KSchema schema = KConsole.schemata().getSchema(schemaUID);
			if (Schema.matches(schema,clmd)) {
				count++;
			}
		}
		KConsole.feedback("Count = " + count); // ** NEW ** //
		KConsole.lastInteger(count); // ** NEW ** //
		return true;
	}

	public static boolean doFind(KCMetadata clmd) throws KException {
		KConsole.lastFound(""); // ** NEW ** //
		for (String schemaUID : KConsole.schemata().theList().keySet()) {
			KSchema schema = KConsole.schemata().getSchema(schemaUID);
			if (Schema.matches(schema,clmd)) {
				KConsole.schemata().setCurrent(schemaUID);
				KConsole.feedback("Found: " + schemaUID);
				KConsole.lastFound(schemaUID); // ** NEW ** //
				return true;
			}
		}
		KConsole.feedback("Not found");
		KConsole.lastFound(""); // ** NEW ** //
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
		for (String splitUID : schema.splits().theList().keySet() ) {
			KSplit split = schema.splits().getSplit(splitUID); 
			String splitName = split.getName();
			splitList = splitList + (splitList.equals("{")?"":",") + (splitName.equals(schema.splits().getCurrent())?"*":"") + splitName;
		}
		splitList = splitList + "}";
		KConsole.println("schema.splits = " + splitList);

		String tabularList = "{";
		for (String tabularUID : schema.tabulars().theList().keySet() ) {
			KTabular tabular = schema.tabulars().getTabular(tabularUID); 
			String tabularName = tabular.getName();
			tabularList = tabularList + (tabularList.equals("{")?"":",") + (tabularName.equals(schema.tabulars().getCurrent())?"*":"") + tabularName;
		}
		tabularList = tabularList + "}";
		KConsole.println("schema.tabulars = " + tabularList);

		String graphList = "{";
		for (String graphUID : schema.graphs().theList().keySet() ) {
			KGraph graph = schema.graphs().getGraph(graphUID); 
			String graphName = graph.getName();
			graphList = graphList + (graphList.equals("{")?"":",") + (graphName.equals(schema.graphs().getCurrent())?"*":"") + graphName;
		}
		graphList = graphList + "}";
		KConsole.println("schema.graphs = " + graphList);
		
		KConsole.metadata("Schema", schema.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String schemaUID : KConsole.schemata().theList().keySet()) {
			KSchema schema = KConsole.schemata().getSchema(schemaUID);
			if (Schema.matches(schema,clmd)) {
				KConsole.schemata().setCurrent(schemaUID);
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
		for (String schemaUID : KConsole.schemata().theList().keySet()) {
			KSchema schema = KConsole.schemata().getSchema(schemaUID);
			if (Schema.matches(schema,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+KConsole.schemata().getPropertySize(KSchema.__INTERNAL_PROPERTY_UID__)+"s", KSchema.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+KConsole.schemata().getPropertySize(KSchema.__INTERNAL_PROPERTY_NAME__)+"s", KSchema.__INTERNAL_PROPERTY_NAME__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+KConsole.schemata().getPropertySize(KSchema.__INTERNAL_PROPERTY_UID__)+"s", schema.getUID());
				output = output + "\t";
				output = output + String.format("%-"+KConsole.schemata().getPropertySize(KSchema.__INTERNAL_PROPERTY_NAME__)+"s", schema.getName());
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

