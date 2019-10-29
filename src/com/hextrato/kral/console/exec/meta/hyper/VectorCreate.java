package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class VectorCreate implements KCParser {
	
	public void setContext (KCMetadata clmd) { clmd.setContext("values"); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Vector.doCreate(clmd);
	}
}

