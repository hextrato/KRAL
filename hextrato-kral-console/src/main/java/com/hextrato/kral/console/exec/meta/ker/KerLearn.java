package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class KerLearn implements KCParser {
	
	public void setContext (KCMetadata clmd) { clmd.setContext("split"); }

	public String[] getValidTokenSet () { return new String[] {"repeat"}; }

}

