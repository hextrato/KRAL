package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Info implements KCParser {

	public boolean exec(KCMetadata clmd) throws KException {
		KConsole.output("hextrato+ console version: 2.0.1.9");
		return true;
	}
	
}
