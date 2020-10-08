package com.hextrato.kral.core.data.abstracts;

import com.hextrato.kral.core.util.exception.KException;

public abstract class AMetaNamedObject extends AMetaUIDObject {
	
	public AMetaNamedObject () throws KException {
		super();
		this._properties.declare(__INTERNAL_PROPERTY_NAME__, "String");
	}
	
	public void setName(String name) throws KException { 
		this.setProperty(__INTERNAL_PROPERTY_NAME__,name); 
	}

	public String getName() throws KException { return this.getProperty(__INTERNAL_PROPERTY_NAME__); }

}
