package com.hextrato.kral.console.exec.meta;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class SplitList implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public boolean exec(KCMetadata clmd) throws KException {
		return Split.doList(clmd);
		/*
		boolean found = false;
		HextraSchema schema = CLFinder.findSchema(clmd);
		String searchSplitName = clmd.getVar("split");
		for (String splitName : schema.splits().theList().keySet()) {
			HextraSplit split = schema.splits().getSplit(splitName);
			if (("["+split.getName()+"]").contains(searchSplitName)) {
				if (!found) {
					HXConsole.output ( String.format("%-20s %-20s", "schema","split") );
				}
				HXConsole.output ( String.format("%-20s %-20s"
						, split.getSchema().getName()
						, split.getName()
						));
				found = true;
			}
		}
		if (!found)
			HXConsole.feedback("No splits found");
		return true;
		*/
	}
}

