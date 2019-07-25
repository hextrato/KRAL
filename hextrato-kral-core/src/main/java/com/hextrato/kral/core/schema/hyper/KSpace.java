package com.hextrato.kral.core.schema.hyper;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

public class KSpace extends AMetaNamedObject {
	
	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public KSpace (KSchema schema, int dims) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		if (dims < 1) throw new KException("Invalid dimensionality ["+dims+"]");
		this._schema = schema;
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", schema.getName());
		this.properties().declare("_dims_", "Integer");
		this.properties().set("_dims_", Integer.toString(dims));
	}
	
	public int getDims() throws NumberFormatException, KException { 
		return Integer.valueOf(this.properties().get("_dims_")); 
	}
	
	private KVectorSet _vectorSet = new KVectorSet(this);
	public KVectorSet vectors() { return _vectorSet; }
	
	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("space %s create %d", this.getName(),this.getDims()) );
			bf.newLine();
			this.vectors().hextract(bf);
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
