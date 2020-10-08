package com.hextrato.kral.console.exec.meta;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class Split implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("split"); }

	public String[] getValidTokenSet () { return new String[] {"create", "desc", "foreach", "list", "count", "find", "hextract", "save", "select"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("split").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String splitName = KCFinder.which(clmd, "split");
		if (!schema.splits().exists(splitName) && KConsole.isMetadataAutocreate())
			return (new SplitCreate()).exec(clmd);
		else
			return (new SplitSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		String splitName = KCFinder.which(clmd, "split");
		schema.splits().create(splitName);
		KConsole.feedback("Split '"+splitName+"' created");
		KConsole.metadata("Split", splitName);
		KConsole.lastString(splitName); // ** NEW ** //
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSplit split = KCFinder.findSplit(schema, clmd);
		KConsole.println("_.schema = " + split.getSchema().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + split.getUID()); // ** NEW ** //
		KConsole.println("_.name = " + split.getName()); // ** NEW ** //
		KConsole.metadata("Split", split.getName());
		KConsole.lastString(split.getName()); // ** NEW ** //
		return true;
	}

	
	private static boolean matches(KSplit split, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KSplit.__INTERNAL_PROPERTY_SCHEMA__);
		String searchUID = clmd.getParameter(KSplit.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KSplit.__INTERNAL_PROPERTY_NAME__);
		boolean match = false;
		if ( true
				&& ("["+split.getSchema()+"]").contains(searchSchema)
				&& ("["+split.getUID()+"]").contains(searchUID)
				&& ("["+split.getName()+"]").contains(searchName)
				) {
			match = true;
		}
		return match;
	}
	
	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String splitName : schema.splits().theList().keySet()) {
			KSplit split = schema.splits().getSplit(splitName);
			if (Split.matches(split,clmd)) {
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
		for (String splitUID : schema.splits().theList().keySet()) {
			KSplit split = schema.splits().getSplit(splitUID);
			if (Split.matches(split,clmd)) {
				schema.splits().setCurrent(splitUID);
				KConsole.feedback("Found: " + splitUID);
				KConsole.lastFound(splitUID); // ** NEW ** //
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
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String splitUID : schema.splits().theList().keySet()) {
			KSplit split = schema.splits().getSplit(splitUID);
			if (Split.matches(split,clmd)) {
				schema.splits().setCurrent(splitUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("Not found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String splitUID : schema.splits().theList().keySet()) {
			KSplit split = schema.splits().getSplit(splitUID);
			if (Split.matches(split,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.splits().getPropertySize(KSplit.__INTERNAL_PROPERTY_SCHEMA__)+"s", KSplit.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+schema.splits().getPropertySize(KSplit.__INTERNAL_PROPERTY_UID__)+"s", KSplit.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+schema.splits().getPropertySize(KSplit.__INTERNAL_PROPERTY_NAME__)+"s", KSplit.__INTERNAL_PROPERTY_NAME__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.splits().getPropertySize(KSplit.__INTERNAL_PROPERTY_SCHEMA__)+"s", split.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.splits().getPropertySize(KSplit.__INTERNAL_PROPERTY_UID__)+"s", split.getUID());
				output = output + "\t";
				output = output + String.format("%-"+schema.splits().getPropertySize(KSplit.__INTERNAL_PROPERTY_NAME__)+"s", split.getName());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("Not found");
		return true;
	}

	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSplit split = KCFinder.findSplit(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		split.hextract(fileName);
		KConsole.feedback("Split '"+split.getName()+"' hextracted");
		KConsole.metadata("Split", split.getName(), fileName);
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSplit split = KCFinder.findSplit(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = split.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		String splitName = KCFinder.which(clmd, "split");
		schema.splits().setCurrent(splitName);
		KConsole.feedback("Split '"+splitName+"' selected");
		KConsole.metadata("Split", splitName);
		KConsole.lastString(splitName); // ** NEW ** //
		return true;
	}

}

