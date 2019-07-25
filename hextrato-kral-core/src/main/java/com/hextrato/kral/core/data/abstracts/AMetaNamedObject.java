package com.hextrato.kral.core.data.abstracts;

import com.hextrato.kral.core.util.exception.KException;

public abstract class AMetaNamedObject extends AMetaUIDObject {
	
	public AMetaNamedObject () throws KException {
		super();
		this._properties.declare("_name_", "String");
	}
	
	// protected AbstractMetaNamedObjectSet	_set = null;
	//public void inSet (AbstractMetaNamedObjectSet set) {
	//	super.inSet(set);
	//	this._set = set;
	//}
	
	public String getName() throws KException { return this.getProperty("_name_"); }

}
