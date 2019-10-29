package com.hextrato.kral.console.exec.meta.nlp;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.nlp.KCorpus;
import com.hextrato.kral.core.schema.nlp.KDocument;
import com.hextrato.kral.core.util.exception.KException;

public class Document implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("document"); }

	public String[] getValidTokenSet () {
		return new String[] {"create", "delete", "list", "select", "desc", "foreach", "property", "save"}; 
	}

	public boolean partial(KCMetadata clmd) {
		return !(clmd.getVar("document").equals(""));
	}

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String documentName = KCFinder.which(clmd, "document");
		if (!corpus.documents().exists(documentName) && KConsole.isMetadataAutocreate())
			return (new DocumentCreate()).exec(clmd);
		else
			return (new DocumentSelect()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String documentName = KCFinder.which(clmd, "document");
		corpus.documents().create(documentName);
		KConsole.feedback("Document '"+documentName+"' created");
		KConsole.metadata("Document", documentName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String documentUID = clmd.getVar("document");
		if (documentUID.equals("")) {
			KConsole.error("which document?");
		} else {
			corpus.documents().delete(documentUID);
			KConsole.feedback("Document '"+documentUID+"' deleted");
			KConsole.metadata("Document", documentUID);
		}
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		KDocument document = KCFinder.findDocument(corpus, clmd);
		KConsole.println("schema.name = " + document.getCorpus().getSchema().getName());
		KConsole.println("corpus.name = " + document.getCorpus().getName());
		KConsole.println("document.name = " + document.getName());
		for (String property : document.properties().keySet()) if (!property.endsWith("_")) {
			KConsole.println("property: "+property+" = " + document.properties().get(property));
		}
		KConsole.metadata("Document", document.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema,clmd);
		String searchDocumentName = clmd.getVar("document");
		String searchDocumentText = clmd.getVar("text");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String documentName : corpus.documents().theList().keySet()) {
			KDocument document = corpus.documents().getDocument(documentName);
			if (	("["+document.getName()+"]").contains(searchDocumentName) 
					&&
					("["+document.getProperty("text")+"]").contains(searchDocumentText) ) 
			{
				corpus.documents().setCurrent(documentName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No documents found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String searchDocumentName = clmd.getVar("document");
		String searchDocumentSplit = clmd.getVar("split");
		String searchDocumentText = clmd.getVar("text");
		for (String documentName : corpus.documents().theList().keySet()) {
			KDocument document = corpus.documents().getDocument(documentName);
			if (	("["+document.getName()+"]").contains(searchDocumentName) 
					&&
					("["+document.getSplit().getName()+"]").contains(searchDocumentSplit)
					&&
					("["+document.getProperty("text")+"]").contains(searchDocumentText) ) 
			{
				if (!found) {
					String output = "";
					output = output + String.format("%-"+corpus.documents().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+corpus.documents().getPropertySize("_corpus_")+"s", "_corpus_");
					output = output + "\t";
					output = output + String.format("%-"+corpus.documents().getPropertySize("_split_")+"s", "_split_");
					output = output + "\t";
					output = output + String.format("%-"+corpus.documents().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+corpus.documents().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+Math.min(50,corpus.documents().getPropertySize("text"))+"s", "text");
					/*
					for (String property : document.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+corpus.documents().getPropertySize(property)+"s", property);
					}
					*/
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+corpus.documents().getPropertySize("_schema_")+"s", document.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+corpus.documents().getPropertySize("_corpus_")+"s", document.getProperty("_corpus_"));
				output = output + "\t";
				output = output + String.format("%-"+corpus.documents().getPropertySize("_split_")+"s", document.getProperty("_split_"));
				output = output + "\t";
				output = output + String.format("%-"+corpus.documents().getPropertySize("_uid_")+"s", document.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+corpus.documents().getPropertySize("_name_")+"s", document.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+Math.min(50,corpus.documents().getPropertySize("text"))+"s", document.getProperty("text").substring(0,Math.min(50,document.getProperty("text").length())).replace('\n', '\\') );
				/*
				for (String property : document.properties().keySet()) if (!property.endsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+corpus.documents().getPropertySize(property)+"s", document.getProperty(property));
				}
				*/
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No documents found");
		return true;
	}

	public static boolean doProperty(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		KDocument document = KCFinder.findDocument(corpus, clmd);
		String property = KCFinder.which(clmd, "property");
		String value = KCFinder.which(clmd, "value");
		if (!property.endsWith("_") && !property.startsWith("_")) {
			switch(property) {
			case "text":
				document.setProperty("text",value);
				break;
			default:
				throw new KException("Invalid property ["+property+"]");
			}
		} else {
			throw new KException("Invalid property ["+property+"]");
		}
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		KDocument document = KCFinder.findDocument(corpus, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = document.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KCorpus corpus = KCFinder.findCorpus(schema, clmd);
		String documentName = KCFinder.which(clmd, "document");
		corpus.documents().setCurrent(documentName);
		KConsole.feedback("Document '"+documentName+"' selected");
		KConsole.metadata("Document", documentName);
		return true;
	}

}

