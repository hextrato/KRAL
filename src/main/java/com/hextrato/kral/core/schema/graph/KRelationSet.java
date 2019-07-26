package com.hextrato.kral.core.schema.graph;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObjectSet;
import com.hextrato.kral.core.util.exception.KException;

public class KRelationSet extends AMetaNamedObjectSet {

	public KRelationSet(KGraph graph) throws KException {
		if (graph == null) throw new KException("Invalid null graph");
		this._graph = graph;
		this.setMetaType("Relation");
	}
	
	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	public void create (String name) throws KException {
		// if (datatype.trim().equals("")) datatype = "String";
		this.create(name, new KRelation(getGraph()));	
	}

	public KRelation getRelation() {
		return getRelation(this.getCurrent());
	}
	public KRelation getRelation(String uid_OR_name) {
		return (KRelation)this.get(uid_OR_name);
	}

	// redefine delete in order to manage stats
	public void delete (String name, boolean cascade) throws KException {
		if (this.theList().containsKey(name)) {
			KRelation relation = this.getRelation(name);
			if (relation.usedAs() == 0 || cascade) {
				// delete triples
				List<String> tripleUIDs = new ArrayList<String>();
				for (String tripleUID : this.getGraph().triples().theList().keySet()) {
					KTriple triple = _graph.triples().getTriple(tripleUID);
					if (triple.getRela() == relation) {
						tripleUIDs.add(tripleUID);
					}
				}
				for (String tripleUID : tripleUIDs) _graph.triples().delete(tripleUID);
				super.delete(name);
			} else {
				throw new KException("Relation already used ("+relation.usedAs()+"), use CASCADE option");
			}
		}
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		for (String name : this.theList().keySet()) this.getRelation(name).hextract(bf);
	}
}
