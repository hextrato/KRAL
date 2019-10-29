package com.hextrato.kral.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.hextrato.kral.core.data.struct.DVariableSet;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.KSchemaSet;
import com.hextrato.kral.core.util.exception.KException;

public abstract class KRAL {
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// Constant values
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	public static final String DATATYPE_PACKAGE = "com.hextrato.kral.core.data.type";
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// init
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	public static void init() throws KException {
		resetVars();
		resetLast();
		resetConfig();
		resetSchemata();
	}
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// output
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	public static boolean TRACE_MODE = true;
	public static boolean DEBUG_MODE = true;
	public static boolean STATS_MODE = true;
	public static boolean ALERT_MODE = true;
	public static boolean ERROR_MODE = true;
	
	public static void message (String info) throws KException {
		System.out.println(info);
	}
	public static void print (String info) throws KException {
		System.out.println (info);
	}
	public static void error (String info) throws KException {
		if (ERROR_MODE) System.out.println(info);
		throw new KException (info);
	}
	public static void alert (String info) {
		if (ALERT_MODE) System.out.println(info);
	}
	public static void stats (String info) {
		if (STATS_MODE) System.out.println(info);
	}
	public static void debug (String info) {
		if (DEBUG_MODE) System.out.println(info);
	}
	public static void trace (String info) {
		if (DEBUG_MODE) System.out.println(info);
	}
	public static void setDebugMode (boolean status) {
		DEBUG_MODE = status;
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// variables
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	private static DVariableSet _vars = null;
	public static DVariableSet vars() { return _vars; }
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void resetVars () throws KException {
		_vars = new DVariableSet();
		_vars.setMetadata("variable");
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// last debug
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	private static DVariableSet _last = null;
	public static DVariableSet last() { return _last; }
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void resetLast () throws KException {
		_last = new DVariableSet();
		_last.setMetadata("last");
		last().declare("_last.feedback","String");
		last().declare("_last.echo","String");
		last().declare("_last.error","String");
		last().declare("_last.output","String");
		last().declare("_last.meta.type","String");
		last().declare("_last.meta.name","String");
		last().declare("_last.meta.info","String");
		last().declare("_last.double","Double");
		last().declare("_last.integer","Integer");
		last().declare("_last.vector","Vector");
		last().declare("_last.string","String");
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void metadata(String type, String name) throws KException {
		metadata(type, name, "");
	}
	public static void metadata(String type, String name, String info) throws KException {
		last().set("_last.meta.type",type);
		last().set("_last.meta.name",name);
		last().set("_last.meta.info",info);
	}
	public static void lastDouble(double value) throws KException {
		last().set("_last.double",Double.toString(value));
	}

	public static void lastInteger(int value) throws KException {
		last().set("_last.integer",Integer.toString(value));
	}

	public static void lastString(String value) throws KException {
		last().set("_last.string",value);
	}

	public static void lastVector(DVector value) throws KException {
		last().set("_last.vector",value.toString());
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// io
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	public static String getFileFullPath(String fileName) throws KException {
		String currWorkDir = config().get("hextrato.current.dir");

		String partialPath = "";
		try { partialPath = Paths.get(fileName).getParent().toString(); } catch (NullPointerException e) {}

		if (partialPath.startsWith("/") || partialPath.startsWith("\\") || (partialPath.length()>1 && partialPath.charAt(1) == ':') ) {
			currWorkDir = partialPath;
		} else {
			if (!currWorkDir.isEmpty() && !partialPath.isEmpty()) currWorkDir = currWorkDir + File.separator;
			currWorkDir = currWorkDir + partialPath;
		}
		File file = new File(Paths.get(currWorkDir).toString());
		String scriptPath = "";
		try { scriptPath = file.getCanonicalPath().toString(); } catch (IOException e) {}
		String scriptFile = Paths.get(fileName).getFileName().toString();
	
		return scriptPath + File.separator + scriptFile;
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// config
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	private static DVariableSet _config = null;
	public static DVariableSet config() { return _config; }
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void resetConfig () throws KException {
		_config = new DVariableSet();
		_config.setMetadata("parameter");
		
		// hextrato
		config().declare("hextrato.home.dir", "String").setValue("/");
		config().declare("hextrato.current.dir", "String").setValue("");
		config().declare("hextrato.work.dir", "String").setValue(System.getProperty("user.dir"));
		// console
		config().declare("console.feedback", "String").setValidValues(new String[] {"ON","OFF"}).setValue("ON");
		config().declare("console.echo", "String").setValidValues(new String[] {"ON","OFF"}).setValue("ON");
		// metadata
		config().declare("metadata.autocreate", "String").setValidValues(new String[] {"FALSE","TRUE"}).setValue("FALSE");
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static boolean isMetadataAutocreate() throws KException {
		return config().get("metadata.autocreate").equals("TRUE");
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// Schemata
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	private static KSchemaSet _schemaSet = null;
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static KSchemaSet schemata() { return _schemaSet; } 
	protected static void resetSchemata() { _schemaSet = new KSchemaSet(); }


	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// MAIN
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void main (String args[]) throws Exception {
		init();
	}

}

