package com.hextrato.kral.console.exec.meta.neural;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.neural.KNeural;
import com.hextrato.kral.core.util.exception.KException;

public class Neural implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("neural"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save", "hextract"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("neural").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String neuralName = KCFinder.which(clmd, "neural");
		if (!schema.neuronal().exists(neuralName) && KConsole.isMetadataAutocreate())
			return (new NeuralCreate()).exec(clmd);
		else
			return (new NeuralSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String neuralName = KCFinder.which(clmd, "neural");
		schema.neuronal().create(neuralName);
		KConsole.feedback("Neural '"+neuralName+"' created");
		KConsole.metadata("Neural", neuralName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String neuralName = KCFinder.which(clmd, "neural");
		schema.neuronal().delete(neuralName);
		KConsole.feedback("Neural '"+neuralName+"' deleted");
		KConsole.metadata("Neural", neuralName);
		return true;
	}
	
	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KConsole.println("neural.name = " + neural.getName());
		KConsole.metadata("Neural", neural.getName());
		return true;
	}
	
	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchNeuralName = clmd.getVar("neural");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String neuralName : schema.neuronal().theList().keySet()) {
			KNeural neural = schema.neuronal().getNeural(neuralName);
			if (("["+neural.getName()+"]").contains(searchNeuralName)) {
				schema.neuronal().setCurrent(neuralName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No neurals found");
		return true;
	}
	
	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		neural.hextract(fileName);
		KConsole.feedback("Neural '"+neural.getName()+"' hextracted");
		KConsole.metadata("Neural", neural.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchNeuralName = clmd.getVar("neural");
		for (String neuralName : schema.neuronal().theList().keySet()) {
			KNeural neural = schema.neuronal().getNeural(neuralName);
			if (("["+neural.getName()+"]").contains(searchNeuralName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.neuronal().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.neuronal().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.neuronal().getPropertySize("_name_")+"s", "_name_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.neuronal().getPropertySize("_schema_")+"s", neural.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.neuronal().getPropertySize("_uid_")+"s", neural.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.neuronal().getPropertySize("_name_")+"s", neural.getProperty("_name_"));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No neurals found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = neural.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String neuralName = KCFinder.which(clmd, "neural");
		schema.neuronal().setCurrent(neuralName);
		KConsole.feedback("Neural '"+neuralName+"' selected");
		KConsole.metadata("Neural", neuralName);
		return true;
	}


}

