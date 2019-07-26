package com.hextrato.kral.core.schema.neural.layer.type;

import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.neural.layer.af.AFunction;
import com.hextrato.kral.core.util.exception.KException;

public class NLLogistic extends NLLinear {

	public String getType() { return "logistic"; };

	public static final AFunction DEFAULT_LOGISTIC_ACTIVATION_FUNCTION = AFunction.AF_LOGISTIC; 

	public NLLogistic() throws KException {
		super();
	}

	//
	// initialization
	//
	public NLBase init() throws KException {
		super.init();
		this._pWeight = new DMatrix(this.getInputSize(),this.getOutputSize());
		this._pBiases = new DVector(this.getOutputSize());
		return this;
	}

	//
	// activation function (it cannot be NULL, otherwise it will be a Linear Layer)
	//
	private AFunction _activationFunction = DEFAULT_LOGISTIC_ACTIVATION_FUNCTION;
	
	public NLBase setActivationFunction(AFunction activationFunction) throws KException {
		if (activationFunction == null) throw new KException("Invalid null activation functon");
		this._activationFunction = activationFunction;
		return this;
	}
	public AFunction getActivationFunction() {
		if (this._activationFunction == null) this._activationFunction = AFunction.AF_LINEAR;
		return this._activationFunction;
	}

	//
	// weights & biases (ALREADY IMPLEMENTED IN NLLinear
	//
	/*
	public NLBase setWeightsNull() throws KException { 
		this._pWeight.fillWithIdentity();
		return this;
	}
	public NLBase setBiasesNull() throws KException { 
		this._pBiases.fillWith(0);
		return this;

	}
	*/
	
	//
	// feedForward
	//
	public NLBase feedForward(DVector input) throws KException { 
		super.feedForward(input);
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			this._oValues.setValue( ox, this.getActivationFunction().function( this._oValues.getValue(ox) ) );
		}
		return this;
	}

	//
	// learn
	//

	protected NLBase calcInputError() throws KException {
		return super.calcInputError();
	}

	protected NLBase calcNetworkError() throws KException {
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			double error = this._oErrors.getValue(ox) * this.getActivationFunction().derivative(this._pResult.getValue(ox));
			this._nErrors.setValue(ox, error);
		}
		return this;
	}
	
	public NLBase calcErrorByTarget(DVector target) throws KException {
		/*
		double error;
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			error = (target.getValue(ox) - this._oValues.getValue(ox));
			this._oErrors.setValue(ox, error);
		}
		calcNetworkError();
		calcInputError();
		return this;
		*/ 
		return super.calcErrorByTarget(target);
	}

	public NLBase calcErrorByPropagation(NLBase layer) throws KException {
		return super.calcErrorByPropagation(layer);
	}

	public NLBase backPropagation() throws KException { 
		return super.backPropagation();
	}

}
