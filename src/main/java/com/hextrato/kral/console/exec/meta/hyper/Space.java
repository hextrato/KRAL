package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.hyper.KSpace;
import com.hextrato.kral.core.util.exception.KException;

public class Space implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("space"); }

	public String[] getValidTokenSet () { return new String[] {"create", "list", "select", "desc", "foreach", "save", "hextract"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("space").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String spaceName = KCFinder.which(clmd, "space");
		if (!schema.hyperspace().exists(spaceName) && KConsole.isMetadataAutocreate())
			return (new SpaceCreate()).exec(clmd);
		else
			return (new SpaceSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String spaceName = KCFinder.which(clmd, "space");
		String spaceDims = KCFinder.which(clmd, "dimensionality");
		schema.hyperspace().create(spaceName,Integer.valueOf(spaceDims));
		KConsole.feedback("Space '"+spaceName+"' created");
		KConsole.metadata("Space", spaceName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KConsole.println("schema.name = " + space.getSchema().getName());
		KConsole.println("space.name = " + space.getName());
		KConsole.println("space.dimensionality = " + space.getDims());
		KConsole.metadata("Space", space.getName());
		return true;
	}

	public static boolean doForEach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchSpaceName = clmd.getVar("space");
		String searchSpaceDims = clmd.getParameter("dimensionality");
		if (searchSpaceDims.equals("")) searchSpaceDims = "0";
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String spaceName : schema.hyperspace().theList().keySet()) {
			KSpace space = schema.hyperspace().getSpace(spaceName);
			if (	("["+space.getName()+"]").contains(searchSpaceName)
					&&
					(space.getDims() == Integer.valueOf(searchSpaceDims) || searchSpaceDims.equals("0"))
					) {
				schema.hyperspace().setCurrent(spaceName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No spaces found");
		return true;
	}

	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		space.hextract(fileName);
		KConsole.feedback("Space '"+space.getName()+"' hextracted");
		KConsole.metadata("Space", space.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchSpaceName = clmd.getVar("space");
		String searchSpaceDims = clmd.getParameter("dimensionality");
		if (searchSpaceDims.equals("")) searchSpaceDims = "0";
		for (String spaceName : schema.hyperspace().theList().keySet()) {
			KSpace space = schema.hyperspace().getSpace(spaceName);
			if (	("["+space.getName()+"]").contains(searchSpaceName)
					&&
					(space.getDims() == Integer.valueOf(searchSpaceDims) || searchSpaceDims.equals("0"))
					) {
				if (!found) {
					// HXConsole.output ( String.format("%-20s %-20s %-20s", "schema","space","dimensionality") );
					String output = "";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize("_dims_")+"s", "_dims_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize("_schema_")+"s", space.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize("_uid_")+"s", space.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize("_name_")+"s", space.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize("_dims_")+"s", space.getProperty("_uid_"));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No spaces found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = space.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String spaceName = KCFinder.which(clmd, "space");
		schema.hyperspace().setCurrent(spaceName);
		KConsole.feedback("Space '"+spaceName+"' selected");
		KConsole.metadata("Space", spaceName);
		return true;
	}

}

