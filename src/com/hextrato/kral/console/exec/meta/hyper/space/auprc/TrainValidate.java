package com.hextrato.kral.console.exec.meta.hyper.space.auprc;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TrainValidate implements KCParser {

	public String[] getValidTokenSet () { return new String[] {"target"}; }

	// public void setContext (KCMetadata clmd) { clmd.setContext("validspace"); }
	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "validspace"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("validspace"); 
		else
			clmd.setContext("validlabel"); 
	}

}

