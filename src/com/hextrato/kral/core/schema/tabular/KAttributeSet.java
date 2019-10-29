package com.hextrato.kral.core.schema.tabular;

import java.io.BufferedWriter;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

public class KAttributeSet extends AMetaNamedObjectSet {
	
	public KAttributeSet(KTabular tabular) throws KException {
		if (tabular == null) throw new KException("Invalid null tabular");
		this._tabular = tabular;
		this.setMetaType("Attribute");
	}
	
	private KTabular _tabular = null;
	public KTabular getTabular() { return this._tabular; }

	public void create (String name,String datatype) throws KException {
		if (datatype.trim().equals("")) datatype = "String";
		this.create(name, new KAttribute(getTabular(), datatype));	
	}

	public KAttribute getAttribute() {
		return getAttribute(this.getCurrent());
	}
	public KAttribute getAttribute(String uid_OR_name) {
		return (KAttribute)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getAttribute(name).hextract(bf);
	}

}
