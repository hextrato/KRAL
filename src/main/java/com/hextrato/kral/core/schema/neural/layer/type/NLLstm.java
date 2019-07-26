package com.hextrato.kral.core.schema.neural.layer.type;

import com.hextrato.kral.core.util.exception.KException;

public class NLLstm extends NLBase {

	public String getType() { return "lstm"; };

	public static final double	DEFAULT_LEARNING_RATE = 0.05;
	public static final double	DEFAULT_MISSLEARNING_FACTOR = 0.95;

	public NLLstm() throws KException {
		super();
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
	

}
