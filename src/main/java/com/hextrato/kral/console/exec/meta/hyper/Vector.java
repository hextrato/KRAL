package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.hyper.KSpace;
import com.hextrato.kral.core.schema.hyper.KVector;
import com.hextrato.kral.core.util.exception.KException;

public class Vector implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("vector"); }

	public String[] getValidTokenSet () { return new String[] {"create", "list", "select", "desc", "foreach", "save"}; }

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
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KVector vector = KCFinder.findVector(space, clmd);
		KConsole.println("schema.name = " + vector.getSpace().getSchema().getName());
		KConsole.println("space.name = " + vector.getSpace().getName());
		KConsole.println("vector.uid = " + vector.getUID());
		KConsole.println("vector.name = " + vector.getName());
		KConsole.println("vector.dimensionality = " + vector.getDims());
		KConsole.println("vector.values = " + vector.getValues().toString());
		KConsole.metadata("Vector", vector.getUID());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String searchVectorUID = clmd.getVar("uid");
		String searchVectorDims = clmd.getParameter("dimensionality");
		if (searchVectorDims.equals("")) searchVectorDims = "0";
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String vectorName : space.vectors().theList().keySet()) {
			KVector vector = space.vectors().getVector(vectorName);
			if (	("["+vector.getUID()+"]").contains(searchVectorUID)
					&&
					(vector.getDims() == Integer.valueOf(searchVectorDims) || searchVectorDims.equals("0"))
					) {
				space.vectors().setCurrent(vectorName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No vectors found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		long count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String searchVectorUID = clmd.getVar("uid");
		String searchVectorDims = clmd.getParameter("dimensionality");
		if (searchVectorDims.equals("")) searchVectorDims = "0";
		for (String vectorName : space.vectors().theList().keySet()) {
			KVector vector = space.vectors().getVector(vectorName);
			if (	("["+vector.getUID()+"]").contains(searchVectorUID)
					&&
					(vector.getDims() == Integer.valueOf(searchVectorDims) || searchVectorDims.equals("0"))
					) {
				if (count==0) {
					String output = "";
					output = output + String.format("%-"+space.vectors().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize("_space_")+"s", "_space_");
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+space.vectors().getPropertySize("_values_")+"s", "_values_");
					KConsole.output(output);
				}
				/*
				HXConsole.output ( String.format("%-20s %-20s %-40s %-20s %s"
						, vector.getSpace().getSchema().getName()
						, vector.getSpace().getName()
						, vector.getUID()
						, Integer.toString(vector.getDims())
						, vector.getValues().toString()
						));
				*/
				String output = "";
				output = output + String.format("%-"+space.vectors().getPropertySize("_schema_")+"s", vector.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize("_space_")+"s", vector.getProperty("_space_"));
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize("_uid_")+"s", vector.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize("_name_")+"s", vector.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+space.vectors().getPropertySize("_values_")+"s", vector.getProperty("_values_"));
				KConsole.output(output);
				count++;
			}
		}
		if (count==0)
			KConsole.feedback("No vectors found");
		else
			KConsole.feedback(count+" found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KVector vector = KCFinder.findVector(space, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = vector.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String vectorName = KCFinder.which(clmd, "vector");
		space.vectors().setCurrent(vectorName);
		KConsole.feedback("Vector '"+vectorName+"' selected");
		KConsole.metadata("Vector", vectorName);
		return true;
	}

}

