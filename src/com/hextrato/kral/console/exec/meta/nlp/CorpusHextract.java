package com.hextrato.kral.console.exec.meta.nlp;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class CorpusHextract implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("file"); } 

	public boolean exec(KCMetadata clmd) throws KException {
		return Corpus.doHextract(clmd);
	}
}

