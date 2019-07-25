package com.hextrato.kral.core.schema.neural.layer.type;

import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.util.exception.KException;

public class NLSoftmax extends NLBase {

	public String getType() { return "softmax"; };

	public NLSoftmax() throws KException {
		super();
	}

	public NLBase setInputSize(int size) throws KException {
		super.setInputSize(size);
		super.setOutputSize(size);
		return this;
	}

	public NLBase setOutputSize(int size) throws KException {
		super.setOutputSize(size);
		super.setInputSize(size);
		return this;
	}

	//
	// feedForward
	//
	
	private double _expSum = 0;
	
	public NLBase feedForward(DVector input) throws KException { 
		// test input.cardinality
		if (input.length() != this.getInputSize())
			throw new KException ("Input vector size ["+input.length()+"] does not match input layer size["+this.getInputSize()+"]");
		this._iValues = input;
		// -- feed 
		_expSum = 0;
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			this._pResult.setValue(ox, this._iValues.getValue(ox));
			double expVal = Math.exp(this._iValues.getValue(ox));
			this._oValues.setValue(ox, expVal);
			_expSum += expVal;
		}
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			this._oValues.setValue(ox, this._oValues.getValue(ox) / _expSum );
		}
		return this;
	}

	//
	// learn
	//
	protected NLBase calcInputError() throws KException {
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			this._iErrors.setValue(ox, this._nErrors.getValue(ox) );
		}
		return this;
	}

	protected NLBase calcNetworkError() throws KException {
		double error;
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			double exps = Math.exp(this._pResult.getValue(ox));
			double diff = _expSum - exps;
			double deriv = 1 / ( 2 + (exps/diff) + (diff/exps) );
			error = this._oErrors.getValue(ox) * deriv;
			this._nErrors.setValue(ox, error);
		}
		return this;
	}

	public NLBase calcErrorByTarget(DVector target) throws KException {
		double error;
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			error = (target.getValue(ox) - this._oValues.getValue(ox));
			this._oErrors.setValue(ox, error);
		}
		calcNetworkError();
		calcInputError();
		return this;
	}

	public NLBase calcErrorByPropagation(NLBase layer) throws KException {
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			this._oErrors.setValue(ox, layer.theGradientIn().getValue(ox) );
		}
		calcNetworkError();
		calcInputError();
		return this;
	}
	
	public NLBase backPropagation() throws KException { 
		// DO Nothing !!!
		return this;
	}

	public double calcError(DVector target) throws KException {
		int maxTargetIndex = 0; double maxTarget = target.getValue(0);
		int maxOutputIndex = 0; double maxOutput = this._oValues.getValue(0);
		for (int ox=1; ox<this.getOutputSize(); ox++) {
			if (target.getValue(ox) > maxTarget) {
				maxTarget = target.getValue(ox);
				maxTargetIndex = ox;
			}
			if (this._oValues.getValue(ox) > maxOutput) {
				maxOutput = this._oValues.getValue(ox);
				maxOutputIndex = ox;
			}
		}
		// System.out.println("["+((maxTargetIndex==maxOutputIndex) ? "OK" : "No")+"] "+target.toString() + " X " + this._oValues.toString());
		return (maxTargetIndex==maxOutputIndex) ? 0 : 1;
	}

}
