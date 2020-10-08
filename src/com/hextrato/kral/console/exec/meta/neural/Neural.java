package com.hextrato.kral.console.exec.meta.neural;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.neural.KLayer;
import com.hextrato.kral.core.schema.neural.KNeural;
import com.hextrato.kral.core.util.exception.KException;

public class Neural implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("neural"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "count", "find", "save", "hextract", "comment"}; }

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
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KConsole.println("_.schema = " + neural.getSchema().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + neural.getUID()); // ** NEW ** //
		KConsole.println("_.name = " + neural.getName()); // ** NEW ** //
		KConsole.metadata("Neural", neural.getName());
		KConsole.lastString(neural.getName()); // ** NEW ** //
		return true;
	}
	
	private static boolean matches(KNeural neural, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KNeural.__INTERNAL_PROPERTY_SCHEMA__);
		String searchUID = clmd.getParameter(KNeural.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KNeural.__INTERNAL_PROPERTY_NAME__);
		boolean match = false;
		if ( true
				&& ("["+neural.getSchema()+"]").contains(searchSchema)
				&& ("["+neural.getUID()+"]").contains(searchUID)
				&& ("["+neural.getName()+"]").contains(searchName)
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String neuralName : schema.neuronal().theList().keySet()) {
			KNeural neural = schema.neuronal().getNeural(neuralName);
			if (Neural.matches(neural,clmd)) {
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
		for (String neuralUID : schema.neuronal().theList().keySet()) {
			KNeural neural = schema.neuronal().getNeural(neuralUID);
			if (Neural.matches(neural,clmd)) {
				schema.neuronal().setCurrent(neuralUID);
				KConsole.feedback("Found: " + neuralUID);
				KConsole.lastFound(neuralUID); // ** NEW ** //
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
		for (String neuralUID : schema.neuronal().theList().keySet()) {
			KNeural neural = schema.neuronal().getNeural(neuralUID);
			if (Neural.matches(neural,clmd)) {
				schema.neuronal().setCurrent(neuralUID);
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
		for (String neuralUID : schema.neuronal().theList().keySet()) {
			KNeural neural = schema.neuronal().getNeural(neuralUID);
			if (Neural.matches(neural,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.neuronal().getPropertySize(KNeural.__INTERNAL_PROPERTY_SCHEMA__)+"s", KNeural.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+schema.neuronal().getPropertySize(KNeural.__INTERNAL_PROPERTY_UID__)+"s", KNeural.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+schema.neuronal().getPropertySize(KNeural.__INTERNAL_PROPERTY_NAME__)+"s", KNeural.__INTERNAL_PROPERTY_NAME__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.neuronal().getPropertySize(KNeural.__INTERNAL_PROPERTY_SCHEMA__)+"s", neural.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.neuronal().getPropertySize(KNeural.__INTERNAL_PROPERTY_UID__)+"s", neural.getUID());
				output = output + "\t";
				output = output + String.format("%-"+schema.neuronal().getPropertySize(KNeural.__INTERNAL_PROPERTY_NAME__)+"s", neural.getName());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No neurals found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = neural.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		String neuralName = KCFinder.which(clmd, "neural");
		schema.neuronal().setCurrent(neuralName);
		KConsole.feedback("Neural '"+neuralName+"' selected");
		KConsole.metadata("Neural", neuralName);
		KConsole.lastString(neuralName); // ** NEW ** //
		return true;
	}

	public static boolean doComment(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String comment = KCFinder.which(clmd, "comment");
		String value = KCFinder.which(clmd, "value");
		neural.comments().set(comment, value);
		return true;
	}
	

}

