package com.hextrato.kral.console.exec.meta.hyper.space.auprc;

import com.hextrato.kral.console.exec.meta.hyper.Space;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TrainValidateTarget implements KCParser {

	// public void setContext (KCMetadata clmd) { clmd.setContext("targetclass"); }
	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "targetclass"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("targetclass"); 
		else
			clmd.setContext("targetposition"); 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Space.doAuprcTrain(clmd);
	}

}

