package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class KerScore implements KCParser {

	public String[] getValidTokenSet () { return new String[] {"set","copyto"}; }

	public void setContext (KCMetadata clmd) { 
		clmd.setContext("score");
		/*
		String property = "";
		try { property = CLFinder.which(clmd, "metric"); } catch (HXException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("score"); 
		else
			clmd.setContext("value");
		*/ 
	}

}

