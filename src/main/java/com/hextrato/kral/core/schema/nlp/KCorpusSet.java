package com.hextrato.kral.core.schema.nlp;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedWriter;

public class KCorpusSet extends AMetaNamedObjectSet {
	
	public KCorpusSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KCorpusSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KCorpusSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("Corpus");
		this._schema = schema;
	}

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name) throws KException { 
		this.create(name, new KCorpus(this._schema));	
	}
	
	public KCorpus getCorpus() {
		return getCorpus(this.getCurrent());
	}
	public KCorpus getCorpus(String uid_OR_name) {
		return (KCorpus)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getCorpus(name).hextract(bf);
	}

}
