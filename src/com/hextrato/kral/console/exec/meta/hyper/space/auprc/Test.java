package com.hextrato.kral.console.exec.meta.hyper.space.auprc;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Test implements KCParser {

	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "testspace"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("testspace"); 
		else
			clmd.setContext("testlabel"); 
	}

	public String[] getValidTokenSet () { return new String[] {"target"}; }

}

