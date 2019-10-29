package com.hextrato.kral.console.exec.meta.hyper.space.probscore;

import com.hextrato.kral.console.exec.meta.hyper.Space;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TargetLabels implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("labelspace"); }

	public String[] getValidTokenSet () { return new String[] {"distance"}; }

}

