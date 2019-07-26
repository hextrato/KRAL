package com.hextrato.kral.core.schema.hyper;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedWriter;

public class KSpaceSet extends AMetaNamedObjectSet {
	
	public KSpaceSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KSpaceSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KSpaceSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("Space");
		this._schema = schema;
	}


	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name, int dims) throws KException { 
		this.create(name, new KSpace(this._schema, dims));	
	}

	public KSpace getSpace() {
		return getSpace(this.getCurrent());
	}
	public KSpace getSpace(String uid_OR_name) {
		return (KSpace)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getSpace(name).hextract(bf);
	}

}
