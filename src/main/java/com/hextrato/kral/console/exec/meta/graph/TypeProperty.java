package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TypeProperty implements KCParser {
	
	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "property"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("property"); 
		else
			clmd.setContext("value"); 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Type.doProperty(clmd);
	}
}

