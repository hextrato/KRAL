package com.hextrato.kral.core.schema.neural;

import java.io.BufferedWriter;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.data.abstracts.AMetaObjectSet;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.neural.KNeural;
import com.hextrato.kral.core.util.exception.KException;

public class KNeuralSet extends AMetaNamedObjectSet {
	
	public KNeuralSet() throws KException {
		this(KRAL.schemata().getCurrent());
	}
	public KNeuralSet(String schemaName) throws KException {
		this((KSchema)KRAL.schemata().theList().get(schemaName));
	}
	public KNeuralSet(KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this.setMetaType("Split");
		this._schema = schema;
	}

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public void create (String name) throws KException { 
		this.create(name, new KNeural(this._schema));	
	}
	
	public KNeural getNeural() {
		return getNeural(this.getCurrent());
	}
	public KNeural getNeural(String uid_OR_name) {
		return (KNeural)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getNeural(name).hextract(bf);
	}
/*
	public HextraNeuralSet(HextraSchema schema) throws HXException {
		if (schema == null) throw new HXException("Invalid null schema");
		this._schema = schema;
		this.setMetaType("Neural");
	}

	private HextraSchema _schema = null;
	public HextraSchema getSchema() { return this._schema; }

	public void create (String name) throws HXException {
		this.create(name, new HextraNeural(getSchema(), name));	
	}
	
	public boolean setCurrent(String name) throws HXException {
		boolean isSet = super.setCurrent(name);
		if (isSet) {
			this.theList().get(name).layers().setCurrent( this.theList().get(name).layers().getCurrent() );
		}
		return isSet;
	}

	public HextraNeural getNeural() {
		return getNeural(this.getCurrent());
	}
	public HextraNeural getNeural(String name) {
		if (name.equals("")) name = this.getCurrent();
		if (this.theList().containsKey(name))
			return this.theList().get(name);
		else
			return null;
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws HXException {
		for (String name : this.theList().keySet()) this.getNeural(name).hextract(bf);
	}
*/
	
}
