package com.hextrato.kral.core.schema.neural;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedWriter;
import java.lang.reflect.Constructor;

public class KLayerSet extends AMetaNamedObjectSet {
	
	public KLayerSet(KNeural neural) throws KException {
		if (neural == null) throw new KException("Invalid null neural");
		this._neural = neural;
		this.setMetaType("Layer");
	}
	
	private KNeural _neural = null;
	public KNeural getNeural() { return this._neural; }

	public void create (String name, String oper) throws KException {
		//if (oper.trim().equals("")) throw new HXException("Invalid null oper");
		//this.create(name, new HextraLayer(getNeural(), oper));	
		if (oper.trim().equals("")) oper = "Linear";
		oper = oper.toLowerCase();
		String className = "info.hextrato.schema.neural.HextraLayerOper"+oper.substring(0,1).toUpperCase() + oper.substring(1);
		KLayer lo;
		try {
			Constructor<?> c = Class.forName(className).getConstructor(KNeural.class, String.class);
			lo = (KLayer)c.newInstance(getNeural(),oper);
		} catch (Exception e) {
			KRAL.error("Invalid layer oper '"+className+"'");
			throw new KException(e.getMessage()); 
		}
		this.create(name, lo);	
	}

	public KLayer getLayer() {
		return getLayer(this.getCurrent());
	}
	public KLayer getLayer(String uid_OR_name) {
		return (KLayer)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) if (this.getLayer(name).getPrevLayer() == null) this.getLayer(name).hextract(bf);
	}

}
