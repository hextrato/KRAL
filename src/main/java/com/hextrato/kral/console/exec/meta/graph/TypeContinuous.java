package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.parser.KCParser;

public class TypeContinuous implements KCParser {

	public String[] getValidTokenSet () { return new String[] {"min","max"}; }

}

