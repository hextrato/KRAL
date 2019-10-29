package com.hextrato.kral.console.exec.meta.hyper.space.auprc;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Train implements KCParser {

	// public void setContext (KCMetadata clmd) { clmd.setContext("labelspace"); }
	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "trainspace"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("trainspace"); 
		else
			clmd.setContext("trainlabel"); 
	}

	public String[] getValidTokenSet () { return new String[] {"validate"}; }

}

