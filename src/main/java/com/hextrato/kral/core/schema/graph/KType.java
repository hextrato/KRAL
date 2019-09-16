package com.hextrato.kral.core.schema.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KType extends AMetaNamedObject {

	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }

	private double _min = Double.MIN_VALUE;
	private double _max = Double.MAX_VALUE;
	private KContinuous _kont = null;
	
	public KType (KGraph graph) throws KException {
		if (graph == null) throw new KException("Invalid null graph");
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", graph.getSchema().getName());
		this._graph = graph;
		this.properties().declare("_graph_", "String");
		this.properties().set("_graph_", graph.getName());

		this._split = graph.getSchema().splits().getSplit();
		if (this._split == null) throw new KException("Invalid split");
		this.properties().declare("_split_", "String");
		this.properties().set("_split_", this._split.getName());

		this.properties().declare("_original_", "String" );
		this.properties().set("_original_", "" );
		this.properties().declare("disjoint", "Boolean" );
		this.properties().set("disjoint", "false" );
		this.properties().declare("isolated", "Boolean" );
		this.properties().set("isolated", "false" );
		this.properties().declare("continuous", "Boolean" );
		this.properties().set("continuous", "false" );
	}

	// private String _originalName = "";
	public boolean hasOriginalName() throws KException { return !(this.properties().get("_original_").equals("")); }   
	public String getOriginalName() throws KException { return this.properties().get("_original_"); }   
	public void setOriginalName( String originalName ) throws KException { this.properties().set("_original_",originalName); }   

	public boolean isDisjoint() throws KException { return this.properties().get("disjoint").equals("true"); }
	public boolean isIsolated() throws KException { return this.properties().get("isolated").equals("true"); }
	public boolean isContinuous() throws KException { return this.properties().get("continuous").equals("true"); }
	public void setDisjoint(boolean flag) throws KException { this.properties().set("disjoint",flag?"true":"false"); }
	public void setIsolated(boolean flag) throws KException { this.properties().set("isolated",flag?"true":"false"); }
	public void setContinuous(boolean flag) throws KException { 
		this.properties().set("continuous",flag?"true":"false");
		if (flag = true && this._kont == null) {
			this._kont = new KContinuous();
			this._kont.setMin(this._min);
			this._kont.setMax(this._max);
		}
	}
	public DVector getContinuousVector(double value, int dimensionalty) throws KException {
		if (this._kont == null) return null;
		else
			return this._kont.getVector(value, dimensionalty); // default magnitude sqrt(2)/2
	}
	public double getContinuousValue(DVector vector) throws KException {
		if (this._kont == null) return 0;
		else
			return this._kont.getValue(vector); // default magnitude sqrt(2)/2
	}

	// continuous limit
	public double getMin() { return this._min; }
	public double getMax() { return this._max; }
	public void setMin(double min) throws KException { this._min = min; if (this._kont != null) this._kont.setMin(this._min); }
	public void setMax(double max) throws KException { this._max = max; if (this._kont != null) this._kont.setMax(this._max); }

	// stats
	private long _statUsedAs = 0;
	public void computeUsedAs() { this._statUsedAs++; }
	public void computeUsedAs(long times) { this._statUsedAs+=times; }
	public long usedAs() { return this._statUsedAs; }
	
	List<String> _typedEntitySet = new ArrayList<String>();
	
	public List<String> theEntitySet() { return _typedEntitySet; } 
	public void registerEntity(String typedName) {
		if (!_typedEntitySet.contains(typedName))
		_typedEntitySet.add(typedName);
	}

	Iterator<String> _randomEntityIterator = null; 
	int _randomIteratorReseter = 0;
	
	private void resetRandomListOfEntities() {
		Collections.shuffle(_typedEntitySet);
		_randomEntityIterator = _typedEntitySet.iterator();
		_randomIteratorReseter = 0;
	}
	
	public String getRandomEntity() {
		if (_randomEntityIterator == null || _randomIteratorReseter > 10) 
			resetRandomListOfEntities();
		if (!_randomEntityIterator.hasNext())
			resetRandomListOfEntities();
		_randomIteratorReseter++;
		return _randomEntityIterator.next();
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("type %s create", this.getName()) );
			bf.newLine();
			// ? HOW TO hextract continuous
			//for (String property : this.properties().keySet()) {
			//	bf.write( String.format("graph %s property %s %s", this.getName(), property, this.properties().get(property)) );
			//	bf.newLine();
			//}
			/*
			bf.write( String.format("graph %s property typed %s", this.getName(), this.properties().get("typed")) );
			bf.newLine();
			bf.write( String.format("graph %s property autocreate %s", this.getName(), this.properties().get("autocreate")) );
			bf.newLine();
			bf.write( String.format("graph %s property hypergraph %s", this.getName(), this.properties().get("hypergraph")) );
			bf.newLine();
			*/
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
	
}
