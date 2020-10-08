package com.hextrato.kral.core.schema.hyper;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.util.exception.KException;

public class KVector extends AMetaNamedObject {

	private KSpace _space = null;
	public KSpace getSpace() { return this._space; }

	private DVector _vector = null;

	public KVector (KSpace space,String values) throws KException {
		if (space == null) throw new KException("Invalid null space");
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, space.getSchema().getName());
		this._space = space;
		this.properties().declare(__INTERNAL_PROPERTY_SPACE__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SPACE__, space.getName());
		//this._datatype = datatype;
		this._vector = new DVector(this._space.getDims());
		this.properties().declare(__INTERNAL_PROPERTY_VALUES__, "Vector");
		this.setValues(values);
	}
	
	public DVector getValues() throws KException { 
		return this._vector;
	}
	public int getDims() throws KException { 
		return (this._vector == null)?0:this._vector.length();
	}
	public void setValues(String values) throws KException { 
		this.properties().set(__INTERNAL_PROPERTY_VALUES__, values);
		this._vector.setValues(values);
		if (this._vector.length() != this._space.getDims()) {
			throw new KException("Invalid vector dim ["+this._vector.length()+"] in space dim ["+this._space.getDims()+"]");
		}
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("vector %s create %s", this.getName(), this.getValues().toString() ) );
			bf.newLine();
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
