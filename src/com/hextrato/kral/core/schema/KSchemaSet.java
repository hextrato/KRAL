package com.hextrato.kral.core.schema;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

public class KSchemaSet extends AMetaNamedObjectSet {

	public KSchemaSet() {
		this.setMetaType("Schema");
	}
	
	public void create (String name) throws KException { 
		this.create(name, new KSchema());	
	}
	
	public boolean setCurrent(String name) throws KException {
		return super.setCurrent(name);
	}
	
	public KSchema getSchema() {
		return getSchema(this.getCurrent());
	}
	public KSchema getSchema(String uid_OR_name) {
		return (KSchema)this.get(uid_OR_name);
	}
}

