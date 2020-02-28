package com.hextrato.kral.console.exec.cons;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class VarPos implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("pos"); }

	public String[] getValidTokenSet () {
		return new String[] {"get", "set"}; 
	}
}

