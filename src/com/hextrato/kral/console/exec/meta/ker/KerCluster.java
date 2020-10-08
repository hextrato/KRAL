package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class KerCluster implements KCParser {

	public void setContext (KCMetadata clmd) { 
	/*
	if (clmd.getVar("eslit").equals("") )
			clmd.setContext("esplit");
		else
	}
	*/
		clmd.setContext("k");
	}

	public String[] getValidTokenSet () { return new String[] {"type"}; }

}

