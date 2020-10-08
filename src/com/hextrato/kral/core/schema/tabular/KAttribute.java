package com.hextrato.kral.core.schema.tabular;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.util.exception.KException;

public class KAttribute extends AMetaNamedObject {

	private KTabular _tabular = null;
	public KTabular getTabular() { return this._tabular; }
	
	public KAttribute (KTabular tabular,String datatype) throws KException {
		if (tabular == null) throw new KException("Invalid null tabular");
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, tabular.getSchema().getName());
		this._tabular = tabular;
		this.properties().declare(__INTERNAL_PROPERTY_TABULAR__, "String");
		this.properties().set(__INTERNAL_PROPERTY_TABULAR__, tabular.getName());
		//this._datatype = datatype;
		this.properties().declare(__INTERNAL_PROPERTY_DATATYPE__, "String");
		this.properties().set(__INTERNAL_PROPERTY_DATATYPE__, datatype);
	}
	
	public String getDatatype() throws KException { return this.properties().get(__INTERNAL_PROPERTY_DATATYPE__); }

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("attribute %s create %s", this.getName(),this.getDatatype()) );
			bf.newLine();
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}

