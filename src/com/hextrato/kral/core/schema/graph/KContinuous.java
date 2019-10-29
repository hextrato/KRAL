package com.hextrato.kral.core.schema.graph;

import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.util.exception.KException;

public class KContinuous {

	// private static final double MARGIN_FACTOR = 0.1;
	
	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	private double _min;
	private double _max;
	
	public KContinuous () throws KException {
		/* 
		if (graph == null) throw new KException("Invalid null graph");
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", graph.getSchema().getName());
		this._graph = graph;
		this.properties().declare("_graph_", "String");
		this.properties().set("_graph_", graph.getName());
		*/
		
		// this._min = Double.MAX_VALUE;
		// this._max = Double.MIN_VALUE;

		this._min = Double.MIN_VALUE;
		this._max = Double.MAX_VALUE;
		
		/*
		this.properties().declare("min", "Double" );
		this.properties().set("min", Double.toString(this._min) );
		this.properties().declare("max", "Double" );
		this.properties().set("max", Double.toString(this._max) );
		*/
	}

	public double getMin() throws KException {
		return this._min;
	}

	public double getMax() throws KException {
		return this._max;
	}

	public void setMin(double value) throws KException {
		// if (value < this._min) {
			this._min = value;
			// this.properties().set("min", Double.toString(this._min) );
		// }
		System.out.println("set minVal = " + this._min);
	}

	public void setMax(double value) throws KException {
		// if (value > this._max) {
			this._max = value;
			// this.properties().set("max", Double.toString(this._max) );
		// }
		System.out.println("set maxVal = " + this._max);
	}
	
	public double getValue (DVector vector) throws KException {
		return getValue (vector, Math.sqrt(2)/2.0);
	}
	public double getValue (DVector vector, double magnitude) throws KException {
		double minVal = this._min;
		double maxVal = this._max;
		double segSiz = (maxVal - minVal) / (vector.size()-1);
		double segBeg = minVal;
		// double segEnd = minVal + segSiz;
		DVector ref = new DVector(vector.size());
		double minDist = Double.MAX_VALUE;
		// int theSeg = -1;
		double theValue = 0;
		for (int i=0; i < vector.size()-1; i++) {
			ref.setValue(i, magnitude);
			double distA = vector.distance(ref);
			ref.setValue(i, 0);
			ref.setValue(i+1, magnitude);
			double distB = vector.distance(ref);
			if (distA + distB < minDist) {
				minDist = distA + distB;
				// theSeg = i;
				theValue = segBeg + distA * segSiz / (distA + distB);
			}
			segBeg += segSiz;
			// segEnd += segSiz;
		}
		return theValue;
	}

	public DVector getVector (double value, int dimensionality) throws KException {
		return getVector (value,dimensionality,Math.sqrt(2)/2.0);
	}
	public DVector getVector (double value, int dimensionality, double magnitude) throws KException {
		DVector v = new DVector(dimensionality);
		double minVal = this._min;
		double maxVal = this._max;
		if (value < minVal || value > maxVal) 
			return v; // as it is
		double segSiz = (maxVal - minVal) / ((double)dimensionality-1);
		double segBeg = minVal;
		double segEnd = minVal + segSiz;
		int vecPos = 0;
		while (value > segEnd && vecPos < dimensionality-1) {
			segBeg += segSiz;
			segEnd += segSiz;
			vecPos ++;
		}
		double segProp = (value - segBeg) / (segEnd - segBeg);
		double a = Math.cos(segProp*(Math.PI/2));
		double b = Math.sin(segProp*(Math.PI/2));
		v.setValue(vecPos, a*magnitude);
		v.setValue(vecPos+1, b*magnitude);
		
		//System.out.println("value = " + value);
		//System.out.println("minVal = " + minVal);
		//System.out.println("maxVal = " + maxVal);
		//System.out.println("return = " + v.toString());
		//if (1==1) System.exit(0);
		return v;
	}
	
}
