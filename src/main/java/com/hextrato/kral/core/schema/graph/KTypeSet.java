package com.hextrato.kral.core.schema.graph;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KType;
import com.hextrato.kral.core.util.exception.KException;

public class KTypeSet extends AMetaNamedObjectSet {
	
	public KTypeSet(KGraph graph) throws KException {
		if (graph == null) throw new KException("Invalid null graph");
		this._graph = graph;
		this.setMetaType("Type");
	}
	
	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	public void create (String name) throws KException {
		// if (datatype.trim().equals("")) datatype = "String";
		this.create(name, new KType(getGraph()));	
	}

	public KType getType() {
		return getType(this.getCurrent());
	}
	public KType getType(String uid_OR_name) {
		return (KType)this.get(uid_OR_name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getType(name).hextract(bf);
	}
	
	// redefine delete in order to manage stats
	public void delete (String name, boolean cascade) throws KException {
		if (this.theList().containsKey(name)) {
			KType type = this.getType(name);
			if (type.usedAs() == 0 || cascade) {
				// delete entities
				List<String> entityUIDs = new ArrayList<String>();
				for (String entityUID : this.getGraph().entities().theList().keySet()) {
					KEntity entity = _graph.entities().getEntity(entityUID);
					if (entity.getType().equals(name)) {
						entityUIDs.add(entityUID);
					}
				}
				for (String entityUID : entityUIDs) _graph.entities().delete(entityUID,cascade);
				this.delete(name);
			} else {
				throw new KException("Type already used ("+type.usedAs()+"), use CASCADE option");
			}
		}
	}

}
