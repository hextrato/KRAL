package com.hextrato.kral.core.schema.hyper;

import java.io.BufferedWriter;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

public class KVectorSet extends AMetaNamedObjectSet {
	
	public KVectorSet(KSpace space) throws KException {
		if (space == null) throw new KException("Invalid null space");
		this._space = space;
		this.setMetaType("Vector");
	}
	
	private KSpace _space = null;
	public KSpace getSpace() { return this._space; }

	public void create (String name,String datatype) throws KException {
		if (datatype.trim().equals("")) datatype = "String";
		this.create(name, new KVector(getSpace(), datatype));	
	}

	public KVector getVector() {
		return getVector(this.getCurrent());
	}
	public KVector getVector(String uid_OR_name) {
		return (KVector)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getVector(name).hextract(bf);
	}


}