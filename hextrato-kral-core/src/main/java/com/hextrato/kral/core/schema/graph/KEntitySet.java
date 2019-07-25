package com.hextrato.kral.core.schema.graph;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.util.exception.KException;

public class KEntitySet extends AMetaNamedObjectSet {
	
	public KEntitySet(KGraph graph) throws KException {
		if (graph == null) throw new KException("Invalid null graph");
		this._graph = graph;
		this.setMetaType("Entity");
	}
	
	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	public void create (String name) throws KException {
		// if (datatype.trim().equals("")) datatype = "String";
		this.create(name, new KEntity(getGraph(), name));	
	}

	public KEntity getEntity() {
		return getEntity(this.getCurrent());
	}
	public KEntity getEntity(String uid_OR_name) {
		return (KEntity)this.get(uid_OR_name);
	}

	// redefine delete in order to manage stats
	public void delete (String name, boolean cascade) throws KException {
		if (this.theList().containsKey(name)) {
			KEntity entity = this.getEntity(name);
			if (entity.usedAs() == 0 || cascade) {
				// delete triples
				List<String> tripleUIDs = new ArrayList<String>();
				for (String tripleUID : this.getGraph().triples().theList().keySet()) {
					KTriple triple = _graph.triples().getTriple(tripleUID);
					if (triple.getHead() == entity || triple.getTail() == entity) {
						// _graph.triples().delete(tripleUID);
						tripleUIDs.add(tripleUID);
					}
				}
				for (String tripleUID : tripleUIDs) _graph.triples().delete(tripleUID);
				this.delete(name);
			} else {
				throw new KException("Entity already used as head("+entity.usedAsHead()+") or tail("+entity.usedAsTail()+"), use CASCADE option");
			}
		}
	}
	
	// redefine delete in order to manage stats
	public void delete (String name) throws KException {
		if (this.theList().containsKey(name)) {
			KEntity entity = this.getEntity(name);
			this.getGraph().types().getType(entity.getType()).computeUsedAs(-1);
		}
		super.delete(name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getEntity(name).hextract(bf);
	}

}

