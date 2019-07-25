package com.hextrato.kral.console.exec.meta.schema.tabular.record;

import com.hextrato.kral.console.exec.meta.schema.tabular.Record;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class AttributeSet implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("value"); }

	// public String[] getValidTokenSet () { return new String[] {}; }
	
	// public boolean partial(CLMetadata clmd) // NO //

	public boolean exec(KCMetadata clmd) throws KException {
		return Record.doAttributeSet(clmd);
	}
	
}

