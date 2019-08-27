package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.ker.KER;
import com.hextrato.kral.core.util.exception.KException;

public class Ker implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("ker"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save", "hextract", "config", "learn", "draw", "evaluate", "score"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("ker").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String kerName = KCFinder.which(clmd, "ker");
		if (!schema.kers().exists(kerName) && KConsole.isMetadataAutocreate())
			return (new KerCreate()).exec(clmd);
		else
			return (new KerSelect()).exec(clmd);
	}

	public static boolean doConfig(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String hyperparam = KCFinder.which(clmd, "hyperparam").toLowerCase();
		String hypervalue = KCFinder.which(clmd, "hypervalue");
		switch (hyperparam) {
		
		// case "kg":
		case "graph":
			// System.out.println(hypervalue);
			KGraph graph = schema.graphs().getGraph(hypervalue);
			// System.out.println(graph.getName());
			ker.setGraph(graph);
			break;

		// case "k":
		case "dimensions":
			ker.setDimensions(Integer.valueOf(hypervalue));
			break;

		// case "lr":
		case "learning_rate":
			ker.setLearningRate(Double.valueOf(hypervalue));
			break;

		// case "lm":
		case "learning_margin":
			ker.setLearningMargin(Double.valueOf(hypervalue));
			break;

		// case "df":
		case "disjoint_factor":
			ker.setDisjointFactor(Double.valueOf(hypervalue));
			break;

		// case "dm":
		case "disjoint_margin":
			ker.setDisjointMargin(Double.valueOf(hypervalue));
			break;

		// case "rf":
		case "random_factor":
			ker.setRandomFactor(Double.valueOf(hypervalue));
			break;

		// case "nt":
		case "regularization_type":
			switch (hypervalue.toUpperCase()) {
			case "SPACE": ker.setRegularizationType("SPACE"); break;
			case "SURFACE": ker.setRegularizationType("SURFACE"); break;
			case "RANGE": ker.setRegularizationType("RANGE"); break;
			default: throw new KException("Invalid normalization type ["+hypervalue+"], SPACE/SURFACE/RANGE expected");
			}
			break;

		//case "nf":
		case "regularization_factor":
			ker.setRegularizationFactor(Double.valueOf(hypervalue));
			break;

		// case "nm":
		case "regularization_margin":
			ker.setRegularizationMargin(Double.valueOf(hypervalue));
			break;

		// case "pm":
		case "projection_matrices":
			switch (hypervalue.toUpperCase()) {
			case "TRUE": ker.setProjectionMatrix(true); break;
			case "FALSE": ker.setProjectionMatrix(false); break;
			default: throw new KException ("Invalid hypervalue '"+hypervalue+"', TRUE/FALSE expected");
			}
			break;

		// case "ir":
		case "inverse_relations":
			switch (hypervalue.toUpperCase()) {
			case "TRUE": ker.setInverseRelation(true); break;
			case "FALSE": ker.setInverseRelation(false); break;
			default: throw new KException ("Invalid hypervalue '"+hypervalue+"', TRUE/FALSE expected");
			}
			break;

		// case "it":
		case "ignore_types":
			switch (hypervalue.toUpperCase()) {
			case "TRUE": ker.setIgnoreTypes(true); break;
			case "FALSE": ker.setIgnoreTypes(false); break;
			default: throw new KException ("Invalid hypervalue '"+hypervalue+"', TRUE/FALSE expected");
			}
			break;

		// case "lc":
		case "latent_constraint":
			ker.setLatentConstraint(Double.valueOf(hypervalue));
			break;

		// case "ec":
		case "enforced_cycles":
			ker.setEnforcedLearningCycles(Integer.valueOf(hypervalue));
			break;

		// case "cc":
		case "current_cycles":
			ker.setCurrentCycles(Integer.valueOf(hypervalue));
			break;

		// case "fr":
		case "functional_negative_rate":
			ker.setFunctionalNegativeRate(Double.valueOf(hypervalue));
			break;

		// case "fm":
		case "functional_negative_max":
			ker.setFunctionalNegativeMax(Integer.valueOf(hypervalue));
			break;

		default:
			throw new KException ("Invalid hyperparam '"+hyperparam+"'");
		}
		return true;
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String kerName = KCFinder.which(clmd, "ker");
		schema.kers().create(kerName);
		KConsole.feedback("KER '"+kerName+"' created");
		KConsole.metadata("KER", kerName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String kerName = KCFinder.which(clmd, "ker");
		schema.kers().delete(kerName);
		KConsole.feedback("KER '"+kerName+"' deleted");
		KConsole.metadata("KER", kerName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		KConsole.println("ker.name = " + ker.getName());
		KGraph graph = ker.getGraph();
		KConsole.println("ker.graph = " + ((graph == null)?"_UNDEFINED_":graph.getName()));
		KConsole.println("ker.dimensions = " + ((ker.getDimensions() == 0)?"_UNDEFINED_":Integer.toString(ker.getDimensions())));
		KConsole.println("ker.learning_rate = " + ker.getLearningRate());
		KConsole.println("ker.learning_margin = " + ker.getLearningMargin());
		KConsole.println("ker.disjoint_factor = " + ker.getDisjointFactor());
		KConsole.println("ker.disjoint_margin = " + ker.getDisjointMargin());
		KConsole.println("ker.random_factor = " + ker.getRandomFactor());
		KConsole.println("ker.regularization_type = " + ker.getRegularizationType());
		KConsole.println("ker.regularization_factor = " + ker.getRegularizationFactor());
		KConsole.println("ker.regularization_margin [RANGE only] = " + ker.getRegularizationMargin());
		KConsole.println("ker.projection_matrices = " + Boolean.toString(ker.isProjectionMatrixActive()));
		KConsole.println("ker.inverse_relations = " + Boolean.toString(ker.isInverseRelationActive()));
		KConsole.println("ker.ignore_types = " + Boolean.toString(ker.isIgnoreTypesActive()));
		KConsole.println("ker.latent_constraint = " + ker.getLatentConstraint());
		KConsole.println("ker.enforced_cycles = " + ker.getEnforcedLearningCycles());
		KConsole.println("ker.current_cycles = " + ker.getCurrentCycles());
		KConsole.println("ker.functional_negative_rate = " + ker.getFunctionalNegativeRate());
		KConsole.println("ker.functional_negative_max = " + ker.getFunctionalNegativeMax());

		KConsole.metadata("KER", ker.getName());
		for (String metric : ker.scores().keySet()) {
			KConsole.println("score: "+metric+" = " + ker.getScore(metric));
		}
		
		return true;
	}

	public static boolean doDraw(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		ker.draw();
		KConsole.metadata("KER", ker.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchKERName = clmd.getVar("ker");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String kerName : schema.kers().theList().keySet()) {
			KER ker = schema.kers().getKER(kerName);
			if (("["+ker.getName()+"]").contains(searchKERName)) {
				schema.kers().setCurrent(kerName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No kers found");
		return true;
	}

	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		ker.hextract(fileName);
		KConsole.feedback("KER '"+ker.getName()+"' hextracted");
		KConsole.metadata("KER", ker.getName(), fileName);
		return true;
	}

	public static boolean doLearnRepeat(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String splitName = KCFinder.which(clmd, "split").trim();
		KSplit split = schema.splits().getSplit(splitName);
		if (split == null) throw new KException("Invalid split name ["+splitName+"]");
		String times = KCFinder.which(clmd, "times").trim();
		
		int repeatTimes = 0;
		try { repeatTimes = Integer.valueOf(times); }
		catch (Exception e) {
			throw new KException("Invalid number of times to repeat");
		}
		KConsole.metadata("KER", ker.getName());
		ker.learn(split,repeatTimes);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchKERName = clmd.getVar("ker");
		for (String kerName : schema.kers().theList().keySet()) {
			KER ker = schema.kers().getKER(kerName);
			if (("["+ker.getName()+"]").contains(searchKERName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.kers().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.kers().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.kers().getPropertySize("_name_")+"s", "_name_");
					for (String property : ker.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+schema.kers().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.kers().getPropertySize("_schema_")+"s", ker.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.kers().getPropertySize("_uid_")+"s", ker.getUID());
				output = output + "\t";
				output = output + String.format("%-"+schema.kers().getPropertySize("_name_")+"s", ker.getName());
				for (String property : ker.properties().keySet()) if (!property.endsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+schema.kers().getPropertySize(property)+"s", ker.getProperty(property));
				}
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No KERs found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = ker.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String kerName = KCFinder.which(clmd, "ker");
		schema.kers().setCurrent(kerName);
		KConsole.feedback("KER '"+kerName+"' selected");
		KConsole.metadata("KER", kerName);
		return true;
	}

	public static boolean doEvaluate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String splitName = clmd.getVar("split");
		if (splitName.trim().contentEquals("")) throw new KException("missing split");
		KSplit split = schema.splits().getSplit(splitName); 
		ker.evaluate(split);
		KConsole.feedback("KER '"+ker.getName()+"' evaluated");
		KConsole.metadata("KER", ker.getName());
		return true;
	}

	public static boolean doScoreSet(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String score = clmd.getVar("score");
		String value = clmd.getVar("value");
		ker.setScore(score, value);
		KConsole.feedback("KER '"+ker.getName()+"' scored");
		KConsole.metadata("KER", ker.getName());
		return true;
	}

	public static boolean doScoreCopyto(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String score = clmd.getVar("score");
		String var = clmd.getVar("var");
		String value = ker.getScore(score);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}


}

