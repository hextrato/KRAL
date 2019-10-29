package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Repeat implements KCParser {

	public void setContext (KCMetadata clmd) { 
		String times = "";
		try { times = KCFinder.which(clmd, "times"); } catch (KException e) {} finally {}
		if (times.equals(""))
			clmd.setContext("times"); 
		else
			clmd.setContext("var"); 
	}

	public String[] getValidTokenSet () {
		return new String[] {"do"}; 
	}

}

