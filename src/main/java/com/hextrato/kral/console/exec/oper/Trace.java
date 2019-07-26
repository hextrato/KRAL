package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class Trace implements KCParser {

	public boolean exec(KCMetadata clmd) {
		KConsole.println(KConsole.trace());
		return true;
	}
	
}
