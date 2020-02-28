package com.hextrato.kral.core.schema.neural.layer.type;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.data.util.URandom;
import com.hextrato.kral.core.util.exception.KException;

public class NLLinear extends NLBase {

	public String getType() { return "linear"; };

	public static final double	DEFAULT_LEARNING_RATE = 0.1;
	public static final double	DEFAULT_MISSLEARNING_FACTOR = 0; // 0.95;

	public NLLinear() throws KException {
		super();
	}

	//
	// initialization
	//
	public NLBase init() throws KException {
		super.init();
		return this;
	}

	//
	// learning rate
	//
	private double _learningRate = DEFAULT_LEARNING_RATE;
	public NLBase setLearningRate(double learningRate) { 
		this._learningRate = learningRate;
		return this;
	}
	public double getLearningRate() { 
		return this._learningRate;
	}

	//
	// probabilistic leaning factor
	//
	private double _missLearningFactor = DEFAULT_MISSLEARNING_FACTOR;
	public NLBase setMisslearningFactor(double probMissLearningFactor) { 
		this._missLearningFactor = probMissLearningFactor;
		return this;
	}
	public double getMisslearningFactor() { 
		return this._missLearningFactor;
	}
	
	protected DMatrix _pWeight = null;
	protected DVector _pBiases = null;
	public DMatrix theWeights() throws KException	{	return this._pWeight;	}
	public DVector theBiases() throws KException	{	return this._pBiases;	}

	//
	// weights & biases
	//
	public NLBase setWeights(DMatrix weights) throws KException {
		if (weights != null) {
			if (weights.rows() != this.getInputSize() && weights.cols() != this.getOutputSize())
				KRAL.error("Weight matrix size ["+weights.rows()+","+weights.cols()+"] does not match layer input/output size ["+this.getInputSize()+","+this.getOutputSize()+"]");
		}
		this._pWeight = weights;
		return this;
	}
	public NLBase setWeightsIdentity() throws KException { 
		if (this._pWeight == null) this._pWeight = new DMatrix(this.getInputSize(),this.getOutputSize());
		// this._pWeight.randomizeAsNormal(); // randomize(-2,2);
		this._pWeight.fillWithIdentity();
		return this;
	}
	public NLBase setWeightsNormal() throws KException { 
		if (this._pWeight == null) this._pWeight = new DMatrix(this.getInputSize(),this.getOutputSize());
		// this._pWeight.randomizeAsNormal(); // randomize(-2,2);
		this._pWeight.randomizeAsNormal();
		return this;
	}
	public NLBase setWeightsRandom() throws KException { 
		if (this._pWeight == null) this._pWeight = new DMatrix(this.getInputSize(),this.getOutputSize());
		// this._pWeight.randomizeAsNormal(); // randomize(-2,2);
		this._pWeight.randomize(-2,2);
		return this;
	}
	public NLBase setWeightsNull() throws KException { 
		this._pWeight = null;
		return this;
	}
	public DMatrix getWeights() throws KException { 
		return this._pWeight;
	}

	public NLBase setWeights(String weights) throws KException {
		if (this._pWeight == null) this._pWeight = new DMatrix(this.getInputSize(),this.getOutputSize());
		this._pWeight.setValues(weights);
		return this; 
	}
	public NLBase setBiases(String biases) throws KException {
		return this.setBiases(new DVector(this.getOutputSize()).setValues(biases));
	}
	
	public NLBase setBiases(DVector biases) throws KException { 
		if (biases != null) {
			if (biases.size() != this.getOutputSize())
				KRAL.error("Bias vector size ["+biases.size()+"] does not match layer output size ["+this.getOutputSize()+"]");
		}
		this._pBiases = biases;
		return this;
	}
	public NLBase setBiasesNormal() throws KException { 
		if (this._pBiases == null) this._pBiases = new DVector(this.getOutputSize());
		// this._pBiases.randomizeAsNormal(); 
		this._pBiases.randomizeAsNormal();
		return this;
	}
	public NLBase setBiasesRandom() throws KException { 
		if (this._pBiases == null) this._pBiases = new DVector(this.getOutputSize());
		// this._pBiases.randomizeAsNormal(); 
		this._pBiases.randomize(-2,2);
		return this;
	}
	public NLBase setBiasesNull() throws KException { 
		this._pBiases = null;
		return this;
	}
	public DVector getBiases() throws KException { 
		return this._pBiases;
	}

	//
	// feedForward
	//
	public NLBase feedForward(DVector input) throws KException { 
		// test input.cardinality
		if (input.length() != this.getInputSize())
			KRAL.error("Input vector size ["+input.length()+"] does not match input layer size["+this.getInputSize()+"]");
		this._iValues = input;
		// -- feed
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			double oValue = 0;
			if (this._pWeight != null) {
				for (int ix = 0; ix < this.getInputSize(); ix++) {
					oValue += this._iValues.getValue(ix) * this._pWeight.getValue(ix,ox);
				}
			} else {
				oValue = (ox < this.getInputSize())?this._iValues.getValue(ox):0;
			}
			if (this._pBiases != null) oValue += this._pBiases.getValue(ox);
			this._pResult.setValue( ox, oValue );
			this._oValues.setValue( ox, oValue );
		}
		return this;
	}

	//
	// learn
	//
	protected NLBase calcInputError() throws KException {
		double error;
		for (int ix = 0; ix < this.getInputSize(); ix++) {
			if (this._pWeight != null) {
				error = 0;
				for (int ox = 0; ox < this.getOutputSize(); ox++) {
					error += this._nErrors.getValue(ox) * this._pWeight.getValue(ix, ox);
				}
				this._iErrors.setValue(ix, error );
			} else {
				this._iErrors.setValue(ix, (ix < this.getOutputSize())?this._nErrors.getValue(ix):0 );
			}
		}
		return this;
	}

	protected NLBase calcNetworkError() throws KException {
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			this._nErrors.setValue(ox, this._oErrors.getValue(ox) );
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
		for (int o=0; o<this.getOutputSize(); o++) {
			// weights
			if (this._pWeight != null) {
				for (int i=0; i<this.getInputSize(); i++) {
					boolean updateByLearningFactor = (this._missLearningFactor <= 0.0);
					if (this._missLearningFactor > 0.0) updateByLearningFactor = (URandom.nextRandom() > this._missLearningFactor);
					if (updateByLearningFactor) { // || true) {
						double w = this._pWeight.getValue(i,o);
						w +=  ( this.getLearningRate() * this._nErrors.getValue(o) * this._iValues.getValue(i) );
						this._pWeight.setValue(i,o,w);
					}
				}
			}
			// biases
			if (this._pBiases != null) {
				boolean updateByLearningFactor = (this._missLearningFactor <= 0.0);
				if (this._missLearningFactor > 0.0) updateByLearningFactor = (URandom.nextRandom() > this._missLearningFactor);
				if (updateByLearningFactor) { // || true) {
					double b = this.theBiases().getValue(o);
					b += ( this.getLearningRate() * this._nErrors.getValue(o) );
					this.theBiases().setValue(o, b);
				}
			}
		}
		return this;
	}

	public double calcError(DVector target) throws KException {
		double distance = 0;
		for (int ox=0; ox<this.getOutputSize(); ox++) {
			double diff = target.getValue(ox) - this._oValues.getValue(ox);
			distance += (diff * diff);
		}
		// System.out.println("["+String.format("%1.6f", Math.sqrt(distance))+"] "+target.toString() + " X " + this._oValues.toString());
		return Math.sqrt(distance);
	}

}
