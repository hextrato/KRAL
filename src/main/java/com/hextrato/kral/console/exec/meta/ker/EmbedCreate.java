package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class EmbedCreate implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("type"); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Embed.doCreate(clmd);
	}
}

