package com.hextrato.kral.console.exec.meta.neural;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class LayerLearnRepeat implements KCParser {
	
	public void setContext (KCMetadata clmd) { 
		clmd.setContext("times");
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Layer.doLearnRepeat(clmd);
	}
}

