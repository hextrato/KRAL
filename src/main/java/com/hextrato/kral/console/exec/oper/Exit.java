package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Exit implements KCParser {

	public boolean exec(KCMetadata clmd) throws KException {
		KConsole.feedback("Goodbye!");
		System.exit(0);
		return true;
	}
	
}
