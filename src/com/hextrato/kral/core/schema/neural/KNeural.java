package com.hextrato.kral.core.schema.neural;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

public class KNeural extends AMetaNamedObject {

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }
	
	public KNeural (KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		// this._name = name;
		this._schema = schema;
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", schema.getName());
	}
	
	private KLayerSet _layersSet = new KLayerSet(this);
	public KLayerSet layers() { return _layersSet; }

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("neural %s create", this.getName()) );
			bf.newLine();
			for (String comment : this.comments().keySet()) {
				bf.write( String.format("# %s : %s", comment , this.getComment(comment))  );
				bf.newLine();
			}
			this.layers().hextract(bf);
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
	
}
