package com.hextrato.kral.console.exec.meta.schema.ker;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class KerDelete implements KCParser {

	public boolean exec(KCMetadata clmd) throws KException {
		return Ker.doDelete(clmd);
	}
}

