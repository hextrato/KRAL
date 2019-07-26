package com.hextrato.kral.console.exec.oper;

import java.io.File;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.struct.DOperators;
import com.hextrato.kral.core.util.exception.KException;

//import info.hextrato.metadata.handlers.HXOperators;

public class WhenDo implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public boolean exec(KCMetadata clmd) throws KException {
		String var1 = clmd.getVar("var1");
		String oper = clmd.getVar("oper");
		String var2 = clmd.getVar("var2");
		File f;
		// Hextrato.debug(var1 + " " + oper + " " + var2 + "?");
		boolean _true_or_false_ = false; 
		switch (oper.toUpperCase()) {
		case "EQ":  
			_true_or_false_ = DOperators.EQ(KConsole.vars().getVariable(var1) , KConsole.vars().getVariable(var2)); 
			break;
		case "!EQ":  
		case "NEQ":  
			// _true_or_false_ = ! (HXConsole.vars().EQ(var1,var2) );
			_true_or_false_ = ! ( DOperators.EQ(KConsole.vars().getVariable(var1) , KConsole.vars().getVariable(var2)) ); 
			break;
		case "IS":
			// _true_or_false_ = HXConsole.vars().IS(var1,var2);
			_true_or_false_ = DOperators.IS(KConsole.vars().getVariable(var1) , var2); 
			break;
		case "!IS":
		case "ISNOT":
			// _true_or_false_ = ! (HXConsole.vars().IS(var1,var2) );
			_true_or_false_ = ! ( DOperators.IS(KConsole.vars().getVariable(var1) , var2) ); 
			break;
		case "!LTE":  
		case "GT":  
			// _true_or_false_ = HXConsole.vars().GT(var1,var2);
			_true_or_false_ = DOperators.GT(KConsole.vars().getVariable(var1) , KConsole.vars().getVariable(var2)); 
			break;
		case "!LT":  
		case "GTE":  
			// _true_or_false_ = HXConsole.vars().GTE(var1,var2);
			_true_or_false_ = DOperators.GTE(KConsole.vars().getVariable(var1) , KConsole.vars().getVariable(var2)); 
			break;
		case "!GTE":  
		case "LT":  
			// _true_or_false_ = HXConsole.vars().LT(var1,var2);
			_true_or_false_ = DOperators.LT(KConsole.vars().getVariable(var1) , KConsole.vars().getVariable(var2)); 
			break;
		case "!GT":  
		case "LTE":  
			// _true_or_false_ = HXConsole.vars().LTE(var1,var2);
			_true_or_false_ = DOperators.LTE(KConsole.vars().getVariable(var1) , KConsole.vars().getVariable(var2)); 
			break;
		case "FILE_EXISTS":  
			// _true_or_false_ = HXConsole.vars().LTE(var1,var2);
			f = new File(var1);
			_true_or_false_ = f.exists();  
			break;
		case "!FILE_EXISTS":  
			// _true_or_false_ = HXConsole.vars().LTE(var1,var2);
			f = new File(var1);
			_true_or_false_ = !f.exists();  
			break;
		default: 
			KConsole.error("Invalid logical operation ["+oper+"]");
		}
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined repeat blok");
		if (_true_or_false_) 
			if (!KConsole.runLine(blok)) return true;
		return true;
	}
}

