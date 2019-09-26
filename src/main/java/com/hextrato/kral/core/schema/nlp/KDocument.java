package com.hextrato.kral.core.schema.nlp;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KDocument extends AMetaNamedObject {

	private KCorpus _corpus = null;
	public KCorpus getCorpus() { return this._corpus; }

	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }

	public KDocument (KCorpus corpus) throws KException {
		if (corpus == null) throw new KException("Invalid null corpus");
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", corpus.getSchema().getName());
		this._corpus = corpus;
		this.properties().declare("_corpus_", "String");
		this.properties().set("_corpus_", corpus.getName());
		//
		this._split = corpus.getSchema().splits().getSplit();
		if (this._split == null) throw new KException("Invalid split");
		this.properties().declare("_split_", "String");
		this.properties().set("_split_", this._split.getName());
		//
		this.properties().declare("text", "String" );
		this.properties().set("text", "" );
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("document %s create", this.getName()) );
			bf.newLine();
			// ? HOW TO hextract continuous
			//for (String property : this.properties().keySet()) {
			//	bf.write( String.format("corpus %s property %s %s", this.getName(), property, this.properties().get(property)) );
			//	bf.newLine();
			//}
			/*
			bf.write( String.format("corpus %s property typed %s", this.getName(), this.properties().get("typed")) );
			bf.newLine();
			bf.write( String.format("corpus %s property autocreate %s", this.getName(), this.properties().get("autocreate")) );
			bf.newLine();
			bf.write( String.format("corpus %s property hypercorpus %s", this.getName(), this.properties().get("hypercorpus")) );
			bf.newLine();
			*/
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
	
}
