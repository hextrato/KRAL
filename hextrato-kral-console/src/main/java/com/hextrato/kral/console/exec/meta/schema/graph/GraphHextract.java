package com.hextrato.kral.console.exec.meta.schema.graph;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class GraphHextract implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("file"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Graph.doHextract(clmd);
	}
}

