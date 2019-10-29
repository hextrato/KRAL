package com.hextrato.kral.core.schema.ker;

import java.io.BufferedWriter;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

public class KERSet extends AMetaNamedObjectSet {
	
	public KERSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KERSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KERSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("KER");
		this._schema = schema;
	}
	
	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name) throws KException { 
		this.create(name, new KER(this._schema));	
	}

	public KER getKER() {
		return getKER(this.getCurrent());
	}
	public KER getKER(String uid_OR_name) {
		return (KER)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getKER(name).hextract(bf);
	}

}
