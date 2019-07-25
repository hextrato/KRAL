package com.hextrato.kral.console.exec.meta.schema.tabular;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class TabularExport implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("csv"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Tabular.doExport(clmd);
	}
}

