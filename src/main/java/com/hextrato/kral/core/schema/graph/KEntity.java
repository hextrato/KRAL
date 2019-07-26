package com.hextrato.kral.core.schema.graph;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KEntity extends AMetaNamedObject {

	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }
	
	public String getType() throws KException { return this.properties().get("_type_"); }
	public String getNick() throws KException { return this.properties().get("_nick_"); }
	
	// public HextraEntity (HextraGraph graph, String typedName) throws HXException {
	public KEntity (KGraph graph, String typedName) throws KException {
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", graph.getSchema().getName());
		this._graph = graph;
		this.properties().declare("_graph_", "String");
		this.properties().set("_graph_", graph.getName());

		this._split = graph.getSchema().splits().getSplit();
		if (this._split == null) throw new KException("Invalid split");
		this.properties().declare("_split_", "String");
		this.properties().set("_split_", this._split.getName());

		// Entity Type/Name
		
		this.setProperty("_name_", typedName );
		
		this.properties().declare("_type_", "String");
		this.setProperty("_type_", "*");
		this.properties().declare("_nick_", "String");
		this.setProperty("_nick_", "");
		if (graph.isTyped()) {
			// this._name = extractNameFrom(typedName);
			// this._type = extractTypeFrom(typedName);
			this.setProperty("_type_", extractTypeFrom(this.getName()));
			this.setProperty("_nick_", extractNameFrom(this.getName()));
			this.setProperty("_name_", this.getProperty("_type_")+":"+this.getProperty("_nick_"));
		} else {
			this.setProperty("_nick_", this.getName() ); // .replace(":", "_") );
			this.setProperty("_name_", this.getName() ); // .replace(":", "_") );
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
