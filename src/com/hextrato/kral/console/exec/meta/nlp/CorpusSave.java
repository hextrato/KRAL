package com.hextrato.kral.console.exec.meta.nlp;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;

public class CorpusSave implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("component"); }

	public String[] getValidTokenSet () { return new String[] {"var"}; }

}

