package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class SpaceStats implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("varPrefix"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Space.doStats(clmd);
	}
}

