package com.hextrato.kral.console.exec.meta;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class SchemaDelete implements KCParser {

	public boolean exec(KCMetadata clmd) throws KException {
		return Schema.doDelete(clmd);
	}
}

