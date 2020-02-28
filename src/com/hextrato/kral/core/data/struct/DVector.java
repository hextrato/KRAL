package com.hextrato.kral.core.data.struct;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.util.UFiller;
import com.hextrato.kral.core.util.exception.KException;

public class DVector {

	private static final double[] _emptyVector = new double[] {};
	
	private double[] 	_vecValues = null;
	private int 		_vecLength = 0;

	// SIZE === LENGTH (equivalents)
	public int size() {return this._vecLength;}
	public int length() {return this._vecLength;}
	
	public double[] theValues() {
		if (this._vecValues == null)
			// the empty vector avoids null pointers being assigned
			return _emptyVector;
		else
			return this._vecValues;
	}

	//
	// constructor
	//
	
	public DVector() throws KException {
		this._vecLength = 0;
		this._vecValues = null;
	}
	
	public DVector(int size) throws KException {
		if (size < 1)
			KRAL.error ("Invalid vector dimension ["+size+"]");
		this._vecLength = size;
		this._vecValues = new double[this._vecLength];
	}
	
	public DVector(double[] array) throws KException {
		// it expects a hard-coded array to be passed as a parameter
		this._vecLength = array.length;
		this._vecValues = array;
	}

	//
	// copy / set
	//
	
	public DVector copyValuesFromTo(DVector vector, int offset, int position) throws KException {
		for (int fromPosition = offset, toPosition = position; fromPosition < vector.size() && toPosition < this.size(); fromPosition++,toPosition++)
			this.setValue(toPosition, vector.getValue(fromPosition) );
		return this;
	}

	public DVector copyValuesFrom(double[] array) throws KException {
		if (array.length != this.size())
			throw new KException ("HEXRR: input array size ["+array.length+"] does not match vector size ["+this.size()+"]");
		for (int pos = 0; pos < this.size(); pos++)
			this.setValue(pos, array[pos]);
		return this;
	}

	public DVector copyValuesFrom(DVector vector) throws KException {
		return this.copyValuesFrom(vector.theValues());
	}

	public DVector setValue(int pos, double value) {
		this._vecValues[pos] = value;
		return this;
	}

	public double getValue(int pos) {
		return this._vecValues[pos];
	}

	public DVector setValues(double [] values) throws KException {
		if (values == null) {
			this._vecLength = 0;
			this._vecValues = null;
		} else {
			this._vecLength = values.length;
			this._vecValues = values;	
		}
		return this;
	}
	
	public DVector setValues(DVector values) throws KException {
		return this.setValues(values.theValues());
	}

	public DVector setValues(String valueList) throws KException {
		valueList = valueList.trim();
		if (valueList.startsWith("[") && valueList.endsWith("]")) {
			valueList = valueList.substring(1, valueList.length()-1);
			String[] values = valueList.split(",");
			if (values.length != this.length()) throw new KException ("Incompatible dimensionality ["+this.length()+"] in: ["+valueList+"]");
			for (int i = 0; i < values.length; i++) this.setValue(i, Double.valueOf(values[i]));
			return this;
		} else {
			throw new KException ("Invalid vector format in: "+valueList);
		}
	}
	
	//
	// properties
	//
	
	public double magnitude () {
		double r = 0.0;
		for (int ii=0; ii<this.length(); ii++)
			r += this.getValue(ii) * this.getValue(ii);
		return Math.sqrt(r);
	}

	public double distance (DVector b) {
		DVector a = this;
		double _L_Distance = 0;
		for (int ii=0; ii<a.size(); ii++) {
			_L_Distance += (a.getValue(ii)-b.getValue(ii))*(a.getValue(ii)-b.getValue(ii));
		}
		_L_Distance = Math.sqrt(_L_Distance );
		return _L_Distance;
	}

	//
	// random
	//
	
	public DVector randomize () throws KException {
		UFiller.randomize(this,-1,+1);
		return this;
	}
	public DVector randomize (double b, double e) throws KException {
		UFiller.randomize(this,b,e);
		return this;
	}
	public DVector randomizeAsNormal () throws KException {
		if (this.size() > 36)
			UFiller.randomizeAsNormal(this,-6/Math.sqrt(this.size()),+6/Math.sqrt(this.size()));
		else 
			UFiller.randomizeAsNormal(this,-2/Math.sqrt(this.size()),+2/Math.sqrt(this.size()));
		return this;
	}
	public DVector randomizeAsNormal (double b, double e) throws KException {
		UFiller.randomizeAsNormal(this,b,e);
		return this;
	}

	//
	// hyper-position
	//
	
	public DVector moveCloserTo(DVector target, double learningRate) throws KException {
		if (target.size() != this.size())
			KRAL.error("target vector size ["+target.size()+"] does not match this vector size ["+this.size()+"]");
		for (int pos = 0; pos < this.size(); pos++) {
			double thisValue = this.getValue(pos);
			double targetValue = target.getValue(pos);
			this.setValue(pos, thisValue + (targetValue-thisValue)*learningRate );
		}
		return this;
	}

	public DVector moveAwayFrom(DVector target, double learningRate, double targetDistance) throws KException {
		if (target.size() != this.size())
			KRAL.error("target vector size ["+target.size()+"] does not match this vector size ["+this.size()+"]");
		if (targetDistance <= 0)
			return this; // just ignore
		/* PREVIOUS VERSION
		double distFactor = this.distance(target)/targetDistance;
		if (distFactor < 1) {
			for (int pos = 0; pos < this.size(); pos++) {
				double thisValue = this.getValue(pos);
				double targetValue = target.getValue(pos);
				this.setValue(pos, thisValue - (targetValue-thisValue)*(1+distFactor)*learningRate; // + (Math.random()*1-0.5)*targetDistance*learningRate);
			}
		}
		*/
		double distFactor = targetDistance / this.distance(target);
		if (distFactor > 1) {
			for (int pos = 0; pos < this.size(); pos++) {
				double thisValue = this.getValue(pos);
				double targetValue = target.getValue(pos);
				this.setValue(pos, thisValue - (targetValue-thisValue)*distFactor*learningRate); // + (Math.random()*1-0.5)*targetDistance*learningRate);
			}
		}
		return this;
	}
	
	//
	// normalize
	//
	
	public DVector normalizeByFixedMagnitude (double magnitude) {
		return normalizeByFixedMagnitude (magnitude, 0); 
	}
	public DVector normalizeByFixedMagnitude (double magnitude, double latentConstraint) {
		double d = this.magnitude();
		if (d > 0) { 
			double factor = magnitude / d; // (1/d)
			for (int p = 0; p < this.size(); p++) {
				double newValue = this.getValue(p) * factor;
				if (latentConstraint > 0 && newValue > 0 && newValue > latentConstraint) newValue = latentConstraint; 
				if (latentConstraint > 0 && newValue < 0 && newValue < -latentConstraint) newValue = -latentConstraint; 
				this.setValue(p,newValue);
			}
		}
		return this;
	}
	
	public DVector normalizeByMaxMagnitude (double magnitude) {
		return normalizeByMaxMagnitude (magnitude, 0);
	}
	public DVector normalizeByMaxMagnitude (double magnitude, double latentConstraint) {
		double d = this.magnitude();
		if (magnitude > 0 && d > magnitude) {
			this.normalizeByFixedMagnitude (magnitude, latentConstraint);
		}
		return this;
	}

	//
	// fillWith
	//
	public DVector fillWith (double value) {
		double[] array = this.theValues();
		for (int ii=0; ii<array.length; ii++)
			array[ii] = value;
		return this;
	}

	//
	// cosine and arc
	//
	public double cosine (DVector vector) throws KException {
		if (this.length() != vector.length()) {
			throw new KException ("Vector sizes do not match: ["+this.length()+","+vector.length()+"]");
		}
		double c = 0;
		for (int pos = 0; pos < this.length(); pos++) {
			c += (this.getValue(pos) * vector.getValue(pos));
		}
		c /= (this.magnitude() * vector.magnitude());
		if (c > 1.0) c = 1.0;
		if (c < -1.0) c = -1.0;
		return c;
	}

	public double arc (DVector vector) throws KException {
		return Math.acos( this.cosine(vector) );
	}

	//
	// correlation
	//

	public double PearsonsCorrelation(DVector ys) {
	    //TODO: check here that arrays are not null, of the same length etc
		DVector xs = this; 
	    //double mx = 0.0;
	    //double my = 0.0;
		
	    double sx = 0.0;
	    double sy = 0.0;
	    double sxx = 0.0;
	    double syy = 0.0;
	    double sxy = 0.0;

	    double n = xs.size();

	    /*
	    for(int i = 0; i < n; i++) {
	      double x = xs.getValue(i);
	      double y = xs.getValue(i);
	      mx += x;
	      my += y;
	    }
	    mx /= n;
	    my /= n;

	    // cov
	    double cov = 0.0;
	    for(int i = 0; i < n; i++) {
		      double x = xs.getValue(i);
		      double y = xs.getValue(i);
		      cov += (x-mx)*(y-my);
	    }
	    
	    // stdev
	    double sdx = 0.0;
	    double sdy = 0.0;
	    for(int i = 0; i < n; i++) {
		      double x = xs.getValue(i);
		      double y = xs.getValue(i);
		      sdx += (x-mx)*(x-mx);
		      sdy += (y-my)*(y-my);
	    }
	    sdx = Math.sqrt( sdx * 1.0/(n-1.0) );
	    sdy = Math.sqrt( sdy * 1.0/(n-1.0) );
	    
	    return cov / (sdx * sdy );
*/
	    for(int i = 0; i < n; i++) {
	      double x = xs.getValue(i);
	      double y = ys.getValue(i);
	      sx += x;
	      sy += y;
	      sxx += x * x;
	      syy += y * y;
	      sxy += x * y;
	    }

	    // covariation
	    double cov = sxy / n - sx * sy / n / n;
	    // standard error of x
	    double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
	    // standard error of y
	    double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);
	    // correlation is just a normalized covariation
	    return cov / sigmax / sigmay;
	  }

	//
	// toString
	//

	public String toString() {
		String s = "[";
		for (int pos = 0; pos < this.size(); pos++) {
			if (pos > 0) s = s + ",";
			//s = s + String.format("%1.32f", this.getValue(pos));
			//while (s.contains(".") && s.endsWith("0")) s = s.substring(0, s.length()-1);
			//while (s.endsWith(".")) s = s.substring(0, s.length()-1);
			String v = String.format("%1.32f", this.getValue(pos));
			while (v.contains(".") && v.endsWith("0")) v = v.substring(0, v.length()-1);
			while (v.endsWith(".")) v = v.substring(0, v.length()-1);
			s = s + v;
		}
		s = s + "]";
		return s;
	}
	
}
