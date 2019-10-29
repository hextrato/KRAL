package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Last implements KCParser {

	public boolean exec(KCMetadata clmd) throws KException {
		for (String last : KConsole.last().keySet()) {
				KConsole.last().show(last);
				// HXConsole.output(("<"+HXConsole.vars().datatypes().get(var)+"> "+var+" = "+HXConsole.vars().values().get(var));
		}
		return true;
	}
	
}
