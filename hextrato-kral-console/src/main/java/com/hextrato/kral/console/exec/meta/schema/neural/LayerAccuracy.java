package com.hextrato.kral.console.exec.meta.schema.neural;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class LayerAccuracy implements KCParser {
	
	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "input"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("input");
		else
			clmd.setContext("output");
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Layer.doAccuracy(clmd);
	}
}

