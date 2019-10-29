package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class SpaceProbscore implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("vector"); }

	public String[] getValidTokenSet () { return new String[] {"target"}; }
	
}

