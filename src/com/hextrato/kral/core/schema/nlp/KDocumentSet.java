package com.hextrato.kral.core.schema.nlp;

import java.io.BufferedWriter;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

public class KDocumentSet extends AMetaNamedObjectSet {
	
	public KDocumentSet(KCorpus corpus) throws KException {
		if (corpus == null) throw new KException("Invalid null corpus");
		this._corpus = corpus;
		this.setMetaType("Document");
	}
	
	private KCorpus _corpus = null;
	public KCorpus getCorpus() { return this._corpus; }

	public void create (String name) throws KException {
		// if (datadocument.trim().equals("")) datadocument = "String";
		this.create(name, new KDocument(getCorpus()));	
	}

	public KDocument getDocument() {
		return getDocument(this.getCurrent());
	}
	public KDocument getDocument(String uid_OR_name) {
		return (KDocument)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getDocument(name).hextract(bf);
	}

}
