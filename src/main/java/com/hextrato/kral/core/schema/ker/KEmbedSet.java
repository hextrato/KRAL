package com.hextrato.kral.core.schema.ker;

import java.io.BufferedWriter;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.ker.KER;
import com.hextrato.kral.core.util.exception.KException;

public class KEmbedSet extends AMetaNamedObjectSet {
	
	public KEmbedSet(KER ker) throws KException {
		if (ker == null) throw new KException("Invalid null ker");
		this._ker = ker;
		this.setMetaType("Embed");
	}
	
	private KER _ker = null;
	public KER getKER() { return this._ker; }

	public void create (String name, String type) throws KException {
		// if (datatype.trim().equals("")) datatype = "String";
		this.create(name, new KEmbed(getKER(), type));	
	}

	public KEmbed getEmbed() {
		return getEmbed(this.getCurrent());
	}
	public KEmbed getEmbed(String uid_OR_name) {
		return (KEmbed)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getEmbed(name).hextract(bf);
	}

}
