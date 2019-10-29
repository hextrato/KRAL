package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class RelationDelete implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public String[] getValidTokenSet () { return new String[] {"cascade"}; }

	public boolean exec(KCMetadata clmd) throws KException {
		return Relation.doDelete(clmd);
	}
}

