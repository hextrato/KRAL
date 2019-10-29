package com.hextrato.kral.console.exec.meta.hyper.space.probscore;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Target implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("class"); }

	public String[] getValidTokenSet () { return new String[] {"labels"}; }

}

