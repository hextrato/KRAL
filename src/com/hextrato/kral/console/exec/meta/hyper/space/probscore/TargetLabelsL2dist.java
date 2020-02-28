package com.hextrato.kral.console.exec.meta.hyper.space.probscore;

import com.hextrato.kral.console.exec.meta.hyper.Space;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TargetLabelsL2dist implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("dist"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Space.doProbscoreArcdist(clmd);
	}
}

