package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class KerEvaluate implements KCParser {

	public void setContext (KCMetadata clmd) { 
			clmd.setContext("split"); 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Ker.doEvaluate(clmd);
	}

}

