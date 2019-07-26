package com.hextrato.kral.core.schema;

import java.io.BufferedWriter;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

public class KSplitSet extends AMetaNamedObjectSet {

	public KSplitSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KSplitSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KSplitSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("Split");
		this._schema = schema;
	}
	
	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name) throws KException { 
		this.create(name, new KSplit(this._schema));	
	}

	public KSplit getSplit() {
		return getSplit(this.getCurrent());
	}
	public KSplit getSplit(String uid_OR_name) {
		return (KSplit)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getSplit(name).hextract(bf);
	}

}
