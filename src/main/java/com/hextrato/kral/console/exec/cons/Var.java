package com.hextrato.kral.console.exec.cons;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.struct.DOperators;
import com.hextrato.kral.core.util.exception.KException;

public class Var implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("var"); }
	
	public String[] getValidTokenSet () {
		return new String[] {"show", "set", "list", "declare", "equals", "add", "sub", "mult", "div", "pow", "concat"}; 
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		// for (String var : HXConsole.vars().datatypes().keySet()) {
		for (String var : KConsole.vars().keySet()) {
			//if (var.indexOf(clmd.getVar("var")) >= 0) {
			if (("["+var+"]").contains(clmd.getVar("var"))) {
				KConsole.println("<"+KConsole.vars().getDatatype(var)+"> "+var+" = "+KConsole.vars().get(var));
				// CLParser.out(var+" = "+HextraVariableSet.mappings().get(var));
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No vars found");
		return true; 
	}

	public static boolean doShow(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		if (KConsole.vars().exits(var)) {
			String value = KConsole.vars().get(var);
			KConsole.output("<"+KConsole.vars().getDatatype(var)+"> "+var+" = "+value);
			KConsole.metadata("var", var, value);
		} else { 
			throw new KException("undefined var ["+var+"]");
		}
		return true; 
	}

	public static boolean doSet(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		KConsole.vars().set(var, value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}

	public static boolean doDeclare(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String type = KCFinder.which(clmd, "datatype");
		String value = "";
		try { value = KCFinder.which(clmd, "value"); } catch (KException e) {} finally {}
		KConsole.vars().declare(var, type);
		if (!value.isEmpty()) KConsole.vars().set(var, value);
		KConsole.feedback("Var '"+var+"' declared");
		KConsole.metadata("var", var);
		return true; 
	}

	public static boolean doAdd(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		// HXConsole.vars().add(var, value);
		DOperators.add(KConsole.vars().getVariable(var), value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}
	
	public static boolean doSub(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		// HXConsole.vars().sub(var, value);
		DOperators.sub(KConsole.vars().getVariable(var), value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}

	public static boolean doMult(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		// HXConsole.vars().mult(var, value);
		DOperators.mult(KConsole.vars().getVariable(var), value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}

	public static boolean doDiv(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		// HXConsole.vars().div(var, value);
		DOperators.div(KConsole.vars().getVariable(var), value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}

	public static boolean doPow(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		// HXConsole.vars().pow(var, value);
		DOperators.pow(KConsole.vars().getVariable(var), value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}
	
	public static boolean doConcat(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String value = KCFinder.which(clmd, "value");
		// HXConsole.vars().concat(var, value);
		DOperators.concat(KConsole.vars().getVariable(var), value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("var", var, value);
		return true; 
	}

	public static boolean doEquals(KCMetadata clmd) throws KException {
		String var = KCFinder.which(clmd, "var");
		String varFrom = clmd.getVar("varFrom");
		String value = KConsole.vars().get(varFrom);
		KConsole.vars().set(var,value);
		KConsole.feedback("Var '"+var+"' set");
		KConsole.metadata("Var", var, value);
		return true;
	}



}

