package com.hextrato.kral.console.exec.meta.schema;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class Split implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("split"); }

	public String[] getValidTokenSet () { return new String[] {"create", "desc", "foreach", "hextract", "list", "save", "select"}; }

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
		KSchema schema = KCFinder.findSchema(clmd);
		String splitName = KCFinder.which(clmd, "split");
		schema.splits().create(splitName);
		KConsole.feedback("Split '"+splitName+"' created");
		KConsole.metadata("Split", splitName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSplit split = KCFinder.findSplit(schema, clmd);
		KConsole.println("schema.name = " + split.getSchema().getName());
		KConsole.println("split.name = " + split.getName());
		KConsole.metadata("Split", split.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchSplitName = clmd.getVar("split");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String splitName : schema.splits().theList().keySet()) {
			KSplit split = schema.splits().getSplit(splitName);
			if (("["+split.getName()+"]").contains(searchSplitName)) {
				schema.splits().setCurrent(splitName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No splits found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchSplitName = clmd.getVar("split");
		for (String splitName : schema.splits().theList().keySet()) {
			KSplit split = schema.splits().getSplit(splitName);
			if (("["+split.getName()+"]").contains(searchSplitName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.splits().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.splits().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.splits().getPropertySize("_name_")+"s", "_name_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.splits().getPropertySize("_schema_")+"s", split.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.splits().getPropertySize("_uid_")+"s", split.getUID());
				output = output + "\t";
				output = output + String.format("%-"+schema.splits().getPropertySize("_name_")+"s", split.getName());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No splits found");
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
		KSchema schema = KCFinder.findSchema(clmd);
		KSplit split = KCFinder.findSplit(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = split.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String splitName = KCFinder.which(clmd, "split");
		schema.splits().setCurrent(splitName);
		KConsole.feedback("Split '"+splitName+"' selected");
		KConsole.metadata("Split", splitName);
		return true;
	}

}

