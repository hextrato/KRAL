package com.hextrato.kral.console.exec.cons;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class ParameterShow implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("parameter"); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Parameter.doShow(clmd);
	}
	
}

