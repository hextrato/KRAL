package com.hextrato.kral.console.exec.meta.schema.ker;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class KerSave implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("property"); }

	public String[] getValidTokenSet () { return new String[] {"var"}; }

}

