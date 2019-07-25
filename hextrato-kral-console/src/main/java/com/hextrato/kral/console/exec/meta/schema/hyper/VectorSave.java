package com.hextrato.kral.console.exec.meta.schema.hyper;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class VectorSave implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("property"); }

	public String[] getValidTokenSet () { return new String[] {"var"}; }

}

