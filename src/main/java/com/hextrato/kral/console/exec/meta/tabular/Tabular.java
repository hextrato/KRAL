package com.hextrato.kral.console.exec.meta.tabular;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Tabular implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("tabular"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save", "import", "export", "hextract"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("tabular").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String tabularName = KCFinder.which(clmd, "tabular");
		if (!schema.tabulars().exists(tabularName) && KConsole.isMetadataAutocreate())
			return (new TabularCreate()).exec(clmd);
		else
			return (new TabularSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String tabularName = KCFinder.which(clmd, "tabular");
		schema.tabulars().create(tabularName);
		KConsole.feedback("Tabular '"+tabularName+"' created");
		KConsole.metadata("Tabular", tabularName);
		return true;
	}
	
	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String tabularName = KCFinder.which(clmd, "tabular");
		schema.tabulars().delete(tabularName);
		KConsole.feedback("Tabular '"+tabularName+"' deleted");
		KConsole.metadata("Tabular", tabularName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KConsole.println("schema.name = " + tabular.getSchema().getName());
		KConsole.println("tabular.name = " + tabular.getName());
		KConsole.metadata("Tabular", tabular.getName());
		return true;
	}

	public static boolean doExport(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String csvRelFileName = KCFinder.which(clmd, "csv");
		
		String partialPath = "";
		try { partialPath = Paths.get(csvRelFileName).getParent().toString(); } catch (NullPointerException e) {}

		String nextWorkDir = KConsole.config().get("hextrato.current.dir"); 
		if (partialPath.startsWith("/") || partialPath.startsWith("\\") || (partialPath.length()>1 && partialPath.charAt(1) == ':') ) {
			nextWorkDir = partialPath;
		} else {
			if (!nextWorkDir.isEmpty() && !partialPath.isEmpty()) nextWorkDir = nextWorkDir + File.separator;
			nextWorkDir = nextWorkDir + partialPath;
		}
		
		File file = new File(Paths.get(nextWorkDir).toString());
		String csvPath = "";
		try { csvPath = file.getCanonicalPath().toString(); } catch (IOException e) {}
		String csvFile = Paths.get(csvRelFileName).getFileName().toString();
		
		tabular.exportToCSV(csvPath,csvFile);
		KConsole.feedback("Tabular '"+tabular.getName()+"' exported");
		KConsole.metadata("Tabular", tabular.getName(), csvRelFileName);
		return true;
	}
	
	public static boolean doImport(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String csvRelFileName = KCFinder.which(clmd, "csv");
		
		String partialPath = "";
		try { partialPath = Paths.get(csvRelFileName).getParent().toString(); } catch (NullPointerException e) {}
		
		String nextWorkDir = KConsole.config().get("hextrato.current.dir"); 
		if (partialPath.startsWith("/") || partialPath.startsWith("\\") || (partialPath.length()>1 && partialPath.charAt(1) == ':') ) {
			nextWorkDir = partialPath;
		} else {
			if (!nextWorkDir.isEmpty() && !partialPath.isEmpty()) nextWorkDir = nextWorkDir + File.separator;
			nextWorkDir = nextWorkDir + partialPath;
		}
		
		File file = new File(Paths.get(nextWorkDir).toString());
		String csvPath = "";
		try { csvPath = file.getCanonicalPath().toString(); } catch (IOException e) {}
		String csvFile = Paths.get(csvRelFileName).getFileName().toString();
		
		tabular.importFromCSV(csvPath,csvFile);
		KConsole.feedback("Tabular '"+tabular.getName()+"' imported");
		KConsole.metadata("Tabular", tabular.getName(), csvRelFileName);
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchTabularName = clmd.getVar("tabular");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String tabularName : schema.tabulars().theList().keySet()) {
			KTabular tabular = schema.tabulars().getTabular(tabularName);
			if (("["+tabular.getName()+"]").contains(searchTabularName)) {
				schema.tabulars().setCurrent(tabularName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No tabulars found");
		return true;
	}
	
	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		tabular.hextract(fileName);
		KConsole.feedback("Tabular '"+tabular.getName()+"' hextracted");
		KConsole.metadata("Tabular", tabular.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchTabularName = clmd.getVar("tabular");
		for (String tabularName : schema.tabulars().theList().keySet()) {
			KTabular tabular = schema.tabulars().getTabular(tabularName);
			if (("["+tabular.getName()+"]").contains(searchTabularName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.tabulars().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.tabulars().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.tabulars().getPropertySize("_name_")+"s", "_name_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_schema_")+"s", tabular.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_uid_")+"s", tabular.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_name_")+"s", tabular.getProperty("_name_"));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No tabulars found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = tabular.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String tabularName = KCFinder.which(clmd, "tabular");
		schema.tabulars().setCurrent(tabularName);
		KConsole.feedback("Tabular '"+tabularName+"' selected");
		KConsole.metadata("Tabular", tabularName);
		return true;
	}

}

