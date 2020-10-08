package com.hextrato.kral.core.schema.graph;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KEntity extends AMetaNamedObject {

	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }
	
	public String getType() throws KException { return this.properties().get(__INTERNAL_PROPERTY_TYPE__); }
	public String getNick() throws KException { return this.properties().get(__INTERNAL_PROPERTY_NICK__); }
	
	// public HextraEntity (HextraGraph graph, String typedName) throws HXException {
	public KEntity (KGraph graph, String typedName) throws KException {
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, graph.getSchema().getName());
		this._graph = graph;
		this.properties().declare(__INTERNAL_PROPERTY_GRAPH__, "String");
		this.properties().set(__INTERNAL_PROPERTY_GRAPH__, graph.getName());

		this._split = graph.getSchema().splits().getSplit();
		if (this._split == null) throw new KException("Invalid split");
		this.properties().declare(__INTERNAL_PROPERTY_SPLIT__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SPLIT__, this._split.getName());

		// Entity Type/Name
		
		this.setProperty(__INTERNAL_PROPERTY_NAME__, typedName );
		
		this.properties().declare(__INTERNAL_PROPERTY_TYPE__, "String");
		this.setProperty(__INTERNAL_PROPERTY_TYPE__, "*");
		this.properties().declare(__INTERNAL_PROPERTY_NICK__, "String");
		this.setProperty(__INTERNAL_PROPERTY_NICK__, "");
		if (graph.isTyped()) {
			// this._name = extractNameFrom(typedName);
			// this._type = extractTypeFrom(typedName);
			this.setProperty(__INTERNAL_PROPERTY_TYPE__, extractTypeFrom(this.getName()));
			if (this._graph.types().getType( this.getType() ).isContinuous()) {
				if (!graph.getSchema().splits().theNames().containsKey(KGraph.CONTINUOUS_SPLIT_NAME)) {
					graph.getSchema().splits().create(KGraph.CONTINUOUS_SPLIT_NAME);
					graph.getSchema().splits().setCurrent(this._split.getName());
				}
				this._split = graph.getSchema().splits().getSplit(KGraph.CONTINUOUS_SPLIT_NAME);
				this.properties().set(__INTERNAL_PROPERTY_SPLIT__, this._split.getName());
			}
			this.setProperty(__INTERNAL_PROPERTY_NICK__, extractNameFrom(this.getName()));
			this.setProperty(__INTERNAL_PROPERTY_NAME__, this.getProperty(__INTERNAL_PROPERTY_TYPE__)+":"+this.getProperty(__INTERNAL_PROPERTY_NICK__));
		} else {
			this.setProperty(__INTERNAL_PROPERTY_NICK__, this.getName() ); // .replace(":", "_") );
			this.setProperty(__INTERNAL_PROPERTY_NAME__, this.getName() ); // .replace(":", "_") );
			// this._type = "*";
		}
		if (!getGraph().types().exists(this.getType())) {
			if (graph.isAutocreate())
				getGraph().types().create(this.getType());
			else
				throw new KException("Graph "+graph.getName()+" is set autocreate OFF for type "+this.getType());
		}
		this._graph.types().getType(this.getType()).computeUsedAs();
		this._graph.types().getType(this.getType()).registerEntity(this.getName());
	}

	public static String extractTypeFrom(String fullName) {
		if (fullName.contains(":"))
			return fullName.substring(0,fullName.lastIndexOf(":"));
		else
			return "*";
	}
	public static String extractNameFrom(String fullName) {
		if (fullName.contains(":"))
			return fullName.substring(fullName.lastIndexOf(":")+1);
		else
			return fullName;
	}
	public boolean isType(String type) throws Exception {
		return type.equals(this.getType());
	}
	
	// stats
	private long _statUsedAs = 0;
	private long _statUsedAsHead = 0;
	private long _statUsedAsTail = 0;
	private long _statUsedAsActor = 0;
	public void computeUsedAsHead() { this._statUsedAsHead++; this._statUsedAs++; }
	public void computeUsedAsTail() { this._statUsedAsTail++; this._statUsedAs++; }
	public void computeUsedAsActor() { this._statUsedAsActor++; this._statUsedAs++; }
	public void computeUsedAsHead(long times) { this._statUsedAsHead+=times; this._statUsedAs+=times; }
	public void computeUsedAsTail(long times) { this._statUsedAsTail+=times; this._statUsedAs+=times; }
	public void computeUsedAsActor(long times) { this._statUsedAsActor+=times; this._statUsedAs+=times; }
	public long usedAs() { return this._statUsedAs; }
	public long usedAsHead() { return this._statUsedAsHead; }
	public long usedAsTail() { return this._statUsedAsTail; }
	public long usedAsActor() { return this._statUsedAsActor; }
}
