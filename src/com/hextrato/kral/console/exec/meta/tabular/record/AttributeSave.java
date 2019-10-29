package com.hextrato.kral.console.exec.meta.tabular.record;

import com.hextrato.kral.console.exec.meta.tabular.Record;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class AttributeSave implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("variable"); }

	// public String[] getValidTokenSet () { return new String[] {}; }
	
	// public boolean partial(CLMetadata clmd) // NO //

	public boolean exec(KCMetadata clmd) throws KException {
		return Record.doAttributeSave(clmd);
	}
	
}

