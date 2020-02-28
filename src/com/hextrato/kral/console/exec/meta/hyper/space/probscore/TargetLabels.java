package com.hextrato.kral.console.exec.meta.hyper.space.probscore;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class TargetLabels implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("labelspace"); }

	public String[] getValidTokenSet () { return new String[] {"arcdist","l2norm"}; }

}

