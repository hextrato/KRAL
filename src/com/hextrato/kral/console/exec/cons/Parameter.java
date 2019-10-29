package com.hextrato.kral.console.exec.cons;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Parameter implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("parameter"); }
	
	public String[] getValidTokenSet () {
		return new String[] {"show", "set", "list"}; 
	}

	public static boolean doShow(KCMetadata clmd) throws KException {
		String parameter = KCFinder.which(clmd, "parameter");
		if (KConsole.config().exits(parameter)) {
			String value = KConsole.config().get(clmd.getVar("parameter"));
			KConsole.output(parameter+" = "+value);
			KConsole.metadata("parameter", parameter, value);
		} else { 
			throw new KException("invalid parameter ["+parameter+"]");
		}
		return true; 
	}
	
	public static boolean doSet(KCMetadata clmd) throws KException {
		String parameter = KCFinder.which(clmd, "parameter");
		String value = KCFinder.which(clmd, "value");
		KConsole.config().set(parameter, value);
		KConsole.feedback("Parameter '"+parameter+"' set");
		KConsole.metadata("parameter", parameter, value);
		return true; 
	}
	
	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		for (String parameter : KConsole.config().keySet()) {
			String value = KConsole.config().get(parameter);
			if (parameter.indexOf(clmd.getVar("parameter")) >= 0) {
				KConsole.println("<"+KConsole.config().getDatatype(parameter)+"> "+parameter+" = "+value);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No parameters found");
		return true; 

	}
}

