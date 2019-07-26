package com.hextrato.kral.core.schema.tabular;

import java.io.BufferedWriter;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

public class KTabularSet extends AMetaNamedObjectSet {
	
	public KTabularSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KTabularSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KTabularSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("Tabular");
		this._schema = schema;
	}

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name) throws KException { 
		this.create(name, new KTabular(this._schema));	
	}
	
	public KTabular getTabular() {
		return getTabular(this.getCurrent());
	}
	public KTabular getTabular(String uid_OR_name) {
		return (KTabular)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getTabular(name).hextract(bf);
	}

}
