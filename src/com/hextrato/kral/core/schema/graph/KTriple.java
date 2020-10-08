package com.hextrato.kral.core.schema.graph;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaUIDObject;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KTriple extends AMetaUIDObject {
	
	// private String _uid;
	// public String getUID() { return this._uid; }

	KEntity _head = null;
	KEntity _tail = null;
	KRelation _rela = null;
	boolean _pola = true;
	
	
	// public HextraTriple (HextraGraph graph, String uid, String head, String rela, String tail) throws HXException {
	public KTriple (KGraph graph, String head, String rela, String tail) throws KException {
		this(graph, head, rela, tail, true);
	}
	// public HextraTriple (HextraGraph graph, String uid, String head, String rela, String tail, boolean pola) throws HXException {
	public KTriple (KGraph graph, String head, String rela, String tail, boolean pola) throws KException {
		if (graph == null) throw new KException("Invalid null graph");
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, graph.getSchema().getName());
		this._graph = graph;
		this.properties().declare(__INTERNAL_PROPERTY_GRAPH__, "String");
		this.properties().set(__INTERNAL_PROPERTY_GRAPH__, graph.getName());
		this._split = graph.getSchema().splits().getSplit();
		if (this._split == null) throw new KException("Invalid split");
		this.properties().declare(__INTERNAL_PROPERTY_SPLIT__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SPLIT__, this._split.getName());
		// this._uid = uid;

		this._head = graph.entities().getEntity(head);
		if (this._head == null) throw new KException("Invalid head entity name: "+head);
		this.properties().declare(__INTERNAL_PROPERTY_HEAD__, "String");
		this.properties().set(__INTERNAL_PROPERTY_HEAD__, head);

		this._rela = graph.relations().getRelation(rela);
		if (this._rela == null) throw new KException("Invalid relation name: "+rela);
		this.properties().declare(__INTERNAL_PROPERTY_RELA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_RELA__, rela);

		this._tail = graph.entities().getEntity(tail);
		if (this._tail == null) throw new KException("Invalid tail entity name: "+tail);
		this.properties().declare(__INTERNAL_PROPERTY_TAIL__, "String");
		this.properties().set(__INTERNAL_PROPERTY_TAIL__, tail);

		this._pola = pola;
		this.properties().declare(__INTERNAL_PROPERTY_POLA__, "Boolean");
		this.properties().set(__INTERNAL_PROPERTY_POLA__, (this._pola)?"true":"false");
		
		this._head.computeUsedAsHead();
		this._tail.computeUsedAsTail();
		this._rela.computeUsedAs();
	}

	// buffered corrupted constituents used along training
	private String _corruptedHead = "";
	private String _corruptedTail = "";
	public String getCorruptedHead () { return _corruptedHead; }
	public String getCorruptedTail () { return _corruptedTail; }
	public void setCorruptedHead (String entityName) { _corruptedHead = entityName; }
	public void setCorruptedTail (String entityName) { _corruptedTail = entityName; }
	
	public KEntity getHead() { return this._head; }
	public KEntity getTail() { return this._tail; }
	public KRelation getRela() { return this._rela; }
	public boolean getPola() { return this._pola; }
	
	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }

	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("triple %s create", "?" ) );
			bf.newLine();
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
	
}
