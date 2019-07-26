package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class EmbedConfig implements KCParser {

	public void setContext (KCMetadata clmd) { 
		String property = "";
		try { property = KCFinder.which(clmd, "hyperparam"); } catch (KException e) {} finally {}
		if (property.equals(""))
			clmd.setContext("hyperparam"); 
		else
			clmd.setContext("hypervalue"); 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return Embed.doConfig(clmd);
	}

}

