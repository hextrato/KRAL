package com.hextrato.kral.console.exec.meta.nlp;

import java.io.File;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.nlp.KCorpus;
import com.hextrato.kral.core.util.exception.KException;

public class Corpus implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("corpus"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save", "hextract", "load"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("corpus").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String corpusName = KCFinder.which(clmd, "corpus");
		if (!schema.corpora().exists(corpusName) && KConsole.isMetadataAutocreate())
			return (new CorpusCreate()).exec(clmd);
		else
			return (new CorpusSelect()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String corpusName = KCFinder.which(clmd, "corpus");
		schema.corpora().create(corpusName);
		KConsole.feedback("Corpus '"+corpusName+"' created");
		KConsole.metadata("Corpus", corpusName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String corpusName = KCFinder.which(clmd, "corpus");
		schema.corpora().delete(corpusName);
		KConsole.feedback("Corpus '"+corpusName+"' deleted");
		KConsole.metadata("Corpus", corpusName);
		return true;
	}
	
	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		KConsole.println("schema.name = " + corpus.getSchema().getName());
		KConsole.println("corpus.name = " + corpus.getName());
		for (String property : corpus.properties().keySet()) if (!property.endsWith("_")) {
			KConsole.println("property: "+property+" = " + corpus.properties().get(property));
		}
		KConsole.metadata("Corpus", corpus.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchCorpusName = clmd.getVar("corpus");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String corpusName : schema.corpora().theList().keySet()) {
			KCorpus corpus = schema.corpora().getCorpus(corpusName);
			if (("["+corpus.getName()+"]").contains(searchCorpusName)) {
				schema.corpora().setCurrent(corpusName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No corpuss found");
		return true;
	}
	
	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		corpus.hextract(fileName);
		KConsole.feedback("Corpus '"+corpus.getName()+"' hextracted");
		KConsole.metadata("Corpus", corpus.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchCorpusName = clmd.getVar("corpus");
		for (String corpusName : schema.corpora().theList().keySet()) {
			KCorpus corpus = schema.corpora().getCorpus(corpusName);
			if (("["+corpus.getName()+"]").contains(searchCorpusName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.corpora().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.corpora().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.corpora().getPropertySize("_name_")+"s", "_name_");
					for (String property : corpus.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+schema.corpora().getPropertySize(property)+"s", property);
					}
					/*
					String output = String.format("%-20s %-20s", "schema","corpus");
					for (String k : corpus.properties().keySet().toArray(new String[0])) {
						output = output + String.format(" %-20s", k);
					}
					*/
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_schema_")+"s", corpus.properties().get("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_uid_")+"s", corpus.properties().get("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_name_")+"s", corpus.properties().get("_name_"));
				for (String property : corpus.properties().keySet()) if (!property.endsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+schema.tabulars().getPropertySize(property)+"s", corpus.properties().get(property));
				}
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No corpuss found");
		return true;
	}

	/*
	public static boolean doProperty(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String property = KCFinder.which(clmd, "property");
		String value = KCFinder.which(clmd, "value");
		if (!property.endsWith("_") && !property.startsWith("_")) {
			switch(property) {
			case "typed":
				corpus.setTyped(value.toUpperCase().equals("TRUE"));
				break;
			case "autocreate":
				corpus.setAutocreate(value.toUpperCase().equals("TRUE"));
				break;
//			case "hypercorpus":
//				corpus.setHypercorpus(value.toUpperCase().equals("TRUE"));
//				break;
			default:
				// corpus.setProperty(property, value);
				throw new KException("Invalid property ["+property+"]");
			}
		} else {
			throw new KException("Invalid property ["+property+"]");
		}
		return true;
	}
	*/

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = corpus.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String corpusName = KCFinder.which(clmd, "corpus");
		schema.corpora().setCurrent(corpusName);
		KConsole.feedback("Corpus '"+corpusName+"' selected");
		KConsole.metadata("Corpus", corpusName);
		return true;
	}

	public static boolean doLoad(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String dir = clmd.getVar("dir");
		//
		File folder = new File(dir);
		if (!folder.isDirectory()) {
			throw new KException("Invalid directory ["+dir+"]");
		}
		corpus.loadFrom(folder);
		//
		KConsole.feedback("Corpus '"+corpus.getName()+"' loaded");
		KConsole.metadata("Corpus", corpus.getName(), dir);
		return true;
	}

}

