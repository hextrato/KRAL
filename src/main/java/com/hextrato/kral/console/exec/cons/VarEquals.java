package com.hextrato.kral.console.exec.cons;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class VarEquals implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("varFrom"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Var.doConcat(clmd); 
	}
}

