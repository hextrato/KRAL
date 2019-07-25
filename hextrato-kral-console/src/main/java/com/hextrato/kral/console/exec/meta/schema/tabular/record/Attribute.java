package com.hextrato.kral.console.exec.meta.schema.tabular.record;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Attribute implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("attribute"); }

	public String[] getValidTokenSet () { return new String[] {"set", "unset", "save"}; }
	
	// public boolean partial(CLMetadata clmd) // NO //

	public boolean exec(KCMetadata clmd) throws KException {
		return false;
	}
	
}

