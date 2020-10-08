package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.hyper.KSpace;
import com.hextrato.kral.core.schema.hyper.KVector;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Vector implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("vector"); }

	public String[] getValidTokenSet () { return new String[] {"create", "list", "select", "desc", "foreach", "count", "find", "save"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("vector").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String vectorUID = KCFinder.which(clmd, "uid");
		if (!space.vectors().exists(vectorUID) && KConsole.isMetadataAutocreate())
			return (new VectorCreate()).exec(clmd);
		else
			return (new VectorSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String vectorName = KCFinder.which(clmd, "vector");
		String vectorVals = KCFinder.which(clmd, "values");
		space.vectors().create(vectorName,vectorVals);
		if (!vectorVals.equals("[]")) {
			space.vectors().getVector(vectorName).getValues().setValues(vectorVals);
		}
		KConsole.feedback("Vector '"+vectorName+"' created");
		KConsole.metadata("Vector", vectorName);
		KConsole.lastString(vectorName); // ** NEW ** //
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KVector vector = KCFinder.findVector(space, clmd);
		KConsole.println("_.schema = " + vector.getSpace().getSchema().getName());
		KConsole.println("_.space = " + vector.getSpace().getName());
		KConsole.println("_.uid = " + vector.getUID());
		KConsole.println("_.name = " + vector.getName());
		KConsole.println("_.dimensionality = " + vector.getDims());
		KConsole.println("_.values = " + vector.getValues().toString());
		KConsole.metadata("Vector", vector.getUID());
		KConsole.lastString(vector.getName()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KVector vector, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KVector.__INTERNAL_PROPERTY_SCHEMA__);
		String searchSpace = clmd.getParameter(KVector.__INTERNAL_PROPERTY_TABULAR__);
		String searchUID = clmd.getParameter(KVector.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KVector.__INTERNAL_PROPERTY_NAME__);
		String searchValues = clmd.getParameter(KSpace.__INTERNAL_PROPERTY_VALUES__);
		String searchDims = clmd.getParameter(KSpace.__INTERNAL_PROPERTY_DIMENSIONALITY__);
		if (searchDims.equals("")) searchDims = "0";
		boolean match = false;
		if ( true
				&& ("["+vector.getSpace().getSchema()+"]").contains(searchSchema)
				&& ("["+vector.getSpace()+"]").contains(searchSpace)
				&& ("["+vector.getUID()+"]").contains(searchUID)
				&& ("["+vector.getName()+"]").contains(searchName)
				&& ("["+vector.getValues()+"]").contains(searchValues)
				&&
				(vector.getDims() == Integer.valueOf(searchDims) || searchDims.equals("0"))
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema,clmd);
		for (String vectorUID : space.vectors().theList().keySet()) {
			KVector vector = space.vectors().getVector(vectorUID);
			if (Vector.matches(vector,clmd)) {
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
		KSpace space = KCFinder.findSpace(schema,clmd);
		for (String vectorUID : space.vectors().theList().keySet()) {
			KVector vector = space.vectors().getVector(vectorUID);
			if (Vector.matches(vector,clmd)) {
				space.vectors().setCurrent(vectorUID);
				KConsole.feedback("Found: " + vectorUID);
				KConsole.lastFound(vectorUID); // ** NEW ** //
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
		KSpace space = KCFinder.findSpace(schema,clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String vectorUID : space.vectors().theList().keySet()) {
			KVector vector = space.vectors().getVector(vectorUID);
			if (Vector.matches(vector,clmd)) {
				space.vectors().setCurrent(vectorUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No vectors found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema,clmd);
		for (String vectorUID : space.vectors().theList().keySet()) {
			KVector vector = space.vectors().getVector(vectorUID);
			if (Vector.matches(vector,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+space.vectors().getPropertySize(KVector.__INTERNAL_PROPERTY_SCHEMA__)+"s", KVector.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize(KVector.__INTERNAL_PROPERTY_NEURAL__)+"s", KVector.__INTERNAL_PROPERTY_NEURAL__);
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize(KVector.__INTERNAL_PROPERTY_UID__)+"s", KVector.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize(KVector.__INTERNAL_PROPERTY_NAME__)+"s", KVector.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize(KVector.__INTERNAL_PROPERTY_DIMENSIONALITY__)+"s", KVector.__INTERNAL_PROPERTY_DIMENSIONALITY__);
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize(KVector.__INTERNAL_PROPERTY_VALUES__)+"s", KVector.__INTERNAL_PROPERTY_VALUES__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+space.vectors().getPropertySize(KTabular.__INTERNAL_PROPERTY_SCHEMA__)+"s", vector.getSpace().getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize(KTabular.__INTERNAL_PROPERTY_TABULAR__)+"s", vector.getSpace().getName());
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize(KTabular.__INTERNAL_PROPERTY_UID__)+"s", vector.getUID());
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize(KTabular.__INTERNAL_PROPERTY_NAME__)+"s", vector.getName());
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize(KTabular.__INTERNAL_PROPERTY_DIMENSIONALITY__)+"s", vector.getDims());
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize(KTabular.__INTERNAL_PROPERTY_VALUES__)+"s", vector.getValues());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No vectors found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KVector vector = KCFinder.findVector(space, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = vector.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String vectorName = KCFinder.which(clmd, "vector");
		space.vectors().setCurrent(vectorName);
		KConsole.feedback("Vector '"+vectorName+"' selected");
		KConsole.metadata("Vector", vectorName);
		KConsole.lastString(vectorName); // ** NEW ** //
		return true;
	}

}

