package com.hextrato.kral.core.data.struct;

import com.hextrato.kral.core.util.exception.KException;

public class DAxis {

	private int _dimensionality = 0;
	private double _axisRangeMIN = -1.0;
	private double _axisRangeMAX = +1.0;
	private DVector _axisValues = null;
	private DVector[] _vectorial = null;
	private static final double ARC_UNIT = Math.acos(0);

	private int _discreteValuesCount = 0;
	private DVector _discreteValues = null;

	private void calcDiscreteValues() throws KException {
		this._discreteValuesCount = this.getDimensionality()*2;
		this._discreteValues = new DVector(this._discreteValuesCount);
		double step = (this._axisRangeMAX - this._axisRangeMIN) / (this._discreteValuesCount-1);
		double value = this._axisRangeMIN;
		for (int i=0; i<this._discreteValuesCount; i++) {
			this._discreteValues.setValue(i, value);
			value += step;
		}
	}
	
	public DAxis(int dimensionality) throws KException {
		if (dimensionality < 2) throw new KException ("Invalid embedding axis dimensionality ["+dimensionality+"]");
		this._dimensionality = dimensionality;
		this._axisValues = new DVector(dimensionality*2);
		this._vectorial = new DVector[dimensionality*2];
		for (int i=0; i<dimensionality; i++) {
			this._axisValues.setValue(i, 1);
			this._vectorial[i] = new DVector(dimensionality).setValue(i, 1);
		}
		for (int i=dimensionality; i<dimensionality*2; i++) {
			this._axisValues.setValue(i, -1);
			this._vectorial[i] = new DVector(dimensionality).setValue(i-dimensionality, -1);
		}
		this.setBoundaries(-1.0, +1.0);
	}
	
	public DAxis setBoundaries(double min, double max) throws KException {
		if (min >= max) throw new KException ("Invalid embedding axis boundaries ["+String.format("%f,%f", min,max)+"]");
		this._axisRangeMIN = min;
		this._axisRangeMAX = max;
		this.calcDiscreteValues();
		return this;
	}
	
	public int getDimensionality() { return this._dimensionality; }
	
	private int getReferenceIndex(DVector vector) throws KException {
		double minDist = -1;
		int minDistIndex = -1;
		for (int i=1; i < this._discreteValuesCount; i++) {
			DVector dv1 = this._vectorial[i-1];
			DVector dv2 = this._vectorial[i];
			double dist1 = vector.distance(dv1);
			double dist2 = vector.distance(dv2);
			double dist = dist1 + dist2;
			if (dist < minDist || minDist < 0) { 
				minDist = dist;
				minDistIndex = i;
			}
		}
		if (minDistIndex-1 < 0) throw new KException("Embedding is out of range when calculating AxisEmbedding value");
		return minDistIndex-1;
	}
	public double calcAccuracy(DVector vector) throws KException  {
		if (vector.size() != this.getDimensionality()) throw new KException("Vector size ["+vector.size()+"] and Continuous dimensionality ["+this.getDimensionality()+"] mismatch");
		double fullMagnitude = vector.magnitude();
		if (fullMagnitude == 0) throw new KException("Invalid vector ZERO magnitude");
		int refIndex = getReferenceIndex(vector);
		double accuracy = 0;
		double sumVec = 0;
		double sumRef = 0;
		for (int i=0; i < vector.length(); i++) {
			sumVec += vector.getValue(i);
			if (refIndex <= i && i <= refIndex+1) sumRef += vector.getValue(i);
		}
		accuracy = sumRef / sumVec;
		if (fullMagnitude < 1) accuracy *= fullMagnitude;
		/*
		if (refIndex >= this.getDimensionality()) refIndex -= this.getDimensionality();
		double v1 = vector.getValue(refIndex);
		double v2 = vector.getValue(refIndex+1);
		double refMagnitude = Math.sqrt(v1*v1 + v2*v2);
				if (fullMagnitude > 1) {
			refMagnitude /= fullMagnitude;
			fullMagnitude = 1;
			accuracy = refMagnitude;
		} else {
			accuracy = refMagnitude * refMagnitude / fullMagnitude;
		}
		*/
		return accuracy;
	}
	public double calcValue(DVector vector) throws Exception  {
		int refIndex = getReferenceIndex(vector);
		double v1 = this._discreteValues.getValue(refIndex);
		double v2 = this._discreteValues.getValue(refIndex+1);

		double value = 0;

		DVector normVector = new DVector(2);
		int vIndex1 = refIndex+0; if (vIndex1 >= this.getDimensionality()) vIndex1 -= this.getDimensionality();
		int vIndex2 = refIndex+1; if (vIndex2 >= this.getDimensionality()) vIndex2 -= this.getDimensionality();
		normVector.setValue(0,vector.getValue(vIndex1));
		normVector.setValue(1,vector.getValue(vIndex2));
		normVector.normalizeByFixedMagnitude(1);

		double ratioArc = Math.acos(normVector.getValue(0)) / ARC_UNIT;
		value = v1 + (v2-v1)*ratioArc;
		return value;
	}

	public DVector calcVector(double value) throws KException  {
		if (value < this._axisRangeMIN || value > this._axisRangeMAX) throw new KException("Value "+value+" out of boundaries ["+String.format("%f,%f", this._axisRangeMIN,this._axisRangeMAX)+"]");
		DVector vector = new DVector(this.getDimensionality());
		for (int i=1; i < this._discreteValuesCount; i++) {
			double v1 = this._discreteValues.getValue(i-1);
			double v2 = this._discreteValues.getValue(i);
			int refIndex1 = i-1; if (refIndex1 >= this.getDimensionality()) refIndex1 -= this.getDimensionality();
			int refIndex2 = i+0; if (refIndex2 >= this.getDimensionality()) refIndex2 -= this.getDimensionality();
			if (value >= v1 && value <= v2) {
				double arc = ARC_UNIT * (value - v1) / (v2-v1);
				//vector.setValue(refIndex1, Math.cos(arc) * this._vectorial[i-1].getValue(refIndex1) );
				//vector.setValue(refIndex2, Math.sin(arc) * this._vectorial[i+0].getValue(refIndex2) );
				vector.setValue(refIndex1, Math.cos(arc) * this._axisValues.getValue(i-1) );
				vector.setValue(refIndex2, Math.sin(arc) * this._axisValues.getValue(i+0) );
				//for (int d=0; d<this.getDimensionality(); d++) {
				//	vector.setValue(d, this._vectorial[i-1].getValue(d) + (value-v1) * (this._vectorial[i].getValue(d)-this._vectorial[i-1].getValue(d)) / (v2-v1) );
				//}
				//vector.normalizeByFixedMagnitude(1);
				return vector;
			}
		}
		throw new KException("Value "+value+" out of boundaries ["+String.format("%f,%f", this._axisRangeMIN,this._axisRangeMAX)+"]");
	}

}
