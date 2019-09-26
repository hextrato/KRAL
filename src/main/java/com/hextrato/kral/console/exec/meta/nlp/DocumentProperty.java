package com.hextrato.kral.console.exec.meta.nlp;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class DocumentProperty implements KCParser {
	
	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "property"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("property"); 
		else
			clmd.setContext("value"); 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Document.doProperty(clmd);
	}
}

