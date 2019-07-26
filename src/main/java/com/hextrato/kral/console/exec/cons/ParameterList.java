package com.hextrato.kral.console.exec.cons;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class ParameterList implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Parameter.doList(clmd);
	}
	
}

