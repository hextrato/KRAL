package com.hextrato.kral.console.exec.meta.neural;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class LayerCreate implements KCParser {
	
	public void setContext (KCMetadata clmd) { clmd.setContext("oper"); }

	public String[] getValidTokenSet () { return new String[] {"after"}; }
	
	public boolean exec(KCMetadata clmd) throws KException {
		return Layer.doCreate(clmd);
	}
}

