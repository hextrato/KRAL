package com.hextrato.kral.core.schema;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.util.exception.KException;

public class KSplit extends AMetaNamedObject {

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }
	
	public KSplit (KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this._schema = schema;
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, schema.getName());
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("split %s create", this.getName()) );
			bf.newLine();
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
