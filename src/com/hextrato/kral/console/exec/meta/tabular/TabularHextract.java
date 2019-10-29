package com.hextrato.kral.console.exec.meta.tabular;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TabularHextract implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("file"); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Tabular.doHextract(clmd);
	}
}

