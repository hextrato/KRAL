package com.hextrato.kral.core.schema.graph;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedWriter;

public class KGraphSet extends AMetaNamedObjectSet {
	
	public KGraphSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KGraphSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KGraphSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("Graph");
		this._schema = schema;
	}

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name) throws KException { 
		this.create(name, new KGraph(this._schema));	
	}
	
	public KGraph getGraph() {
		return getGraph(this.getCurrent());
	}
	public KGraph getGraph(String uid_OR_name) {
		return (KGraph)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getGraph(name).hextract(bf);
	}

}
