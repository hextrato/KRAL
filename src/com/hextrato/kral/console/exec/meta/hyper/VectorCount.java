package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class VectorCount implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Vector.doCount(clmd);
	}
}

