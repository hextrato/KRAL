package com.hextrato.kral.core.schema.tabular;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaUIDObjectSet;
import com.hextrato.kral.core.data.type.TUid;
import com.hextrato.kral.core.util.exception.KException;

public class KRecordSet extends AMetaUIDObjectSet {
	
	public KRecordSet(KTabular tabular) throws KException {
		if (tabular == null) throw new KException("Invalid null tabular");
		this._tabular = tabular;
		this.setMetaType("Record");
	}

	private KTabular _tabular = null;
	public KTabular getTabular() { return this._tabular; }

	public void create () throws KException {
		String uid = TUid.random(); // UUID.randomUUID().toString();
		this.create(uid);
	}

	public void create (String uid) throws KException { 
		this.create(uid, new KRecord(getTabular()));	
	}

	public KRecord getRecord() {
		return getRecord(this.getCurrent());
	}
	public KRecord getRecord(String uid) {
		return (KRecord)this.get(uid);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		String currSplit = "";
		for (String uid : this.theList().keySet()) {
			KRecord r = this.getRecord(uid); 
			if (!r.getSplit().getName().equals(currSplit)) {
				currSplit = r.getSplit().getName();
		        try {
		        	bf.write( String.format("split %s select", currSplit) );
		        	bf.newLine();
		        } catch (IOException e) {
		        	throw new KException(e.getMessage());
		        }
			}
			r.hextract(bf);
		}
	}


}
