package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TypeSaveVar implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("var"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Type.doSaveVar(clmd);
	}
}

