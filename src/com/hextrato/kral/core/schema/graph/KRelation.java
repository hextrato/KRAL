package com.hextrato.kral.core.schema.graph;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KRelation extends AMetaNamedObject {
	
	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }

	public KRelation (KGraph graph) throws KException {
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
		
		this.properties().declare("_original_", "String" );
		this.properties().set("_original_", "" );
		this.properties().declare("functional", "Boolean" );
		this.properties().set("functional", "false" );
		this.properties().declare("isolated", "Boolean" );
		this.properties().set("isolated", "false" );
		this.properties().declare("head_type_norm", "Boolean" );
		this.properties().set("head_type_norm", "false" );
		this.properties().declare("tail_type_norm", "Boolean" );
		this.properties().set("tail_type_norm", "false" );
	}

	// private String _originalName = "";
	public boolean hasOriginalName() throws KException { return !(this.properties().get("_original_").equals("")); }   
	public String getOriginalName() throws KException { return this.properties().get("_original_"); }   
	public void setOriginalName( String originalName ) throws KException { this.properties().set("_original_",originalName); }   

	public boolean isFunctional() throws KException { return this.properties().get("functional").equals("true"); }
	public boolean isIsolated() throws KException { return this.properties().get("isolated").equals("true"); }
	public boolean isHeadTypeNorm() throws KException { return this.properties().get("head_type_norm").equals("true"); }
	public boolean isTailTypeNorm() throws KException { return this.properties().get("tail_type_norm").equals("true"); }
	
	public void setFunctional(boolean flag) throws KException { this.properties().set("functional",flag?"true":"false"); }
	public void setIsolated(boolean flag) throws KException { this.properties().set("isolated",flag?"true":"false"); }
	public void setHeadTypeNorm(boolean flag) throws KException { this.properties().set("head_type_norm",flag?"true":"false"); }
	public void setTailTypeNorm(boolean flag) throws KException { this.properties().set("tail_type_norm",flag?"true":"false"); }

	// stats
	private long _statUsedAs = 0;
	public void computeUsedAs() { this._statUsedAs++; }
	public void computeUsedAs(long times) { this._statUsedAs+=times; }
	public long usedAs() { return this._statUsedAs; }
	
}
