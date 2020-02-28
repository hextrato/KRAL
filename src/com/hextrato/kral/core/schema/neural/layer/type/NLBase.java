package com.hextrato.kral.core.schema.neural.layer.type;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.hyper.KVectorSet;
import com.hextrato.kral.core.schema.neural.layer.af.AFunction;
import com.hextrato.kral.core.util.exception.KException;

public abstract class NLBase {

	//
	// vector and matrices 
	//
	// input / output / weights
	//

	public String getType() { return "base"; };
	
	protected DVector _iValues = null; // do NOT need initialisation (works as pointer/reference)
	protected DVector _pResult = null; // pre RESULT (before ACTIVATION)
	protected DVector _oValues = null; // out VALUES (after  ACTIVATION)
	protected DVector _oErrors = null; // calc output  gradient errors (after ACTIVATION)
	protected DVector _nErrors = null; // calc network gradient errors (before ACTIVATION)
	protected DVector _iErrors = null; // calc input gradient errors (weight based)

	public DVector theInputValues() 	{ 	return this._iValues;	}
	public DVector theOutputValues()	{	return this._oValues;	}
	public DVector theGradientOut() 	{	return this._oErrors;	}
	public DVector theGradientNet() 	{	return this._nErrors;	}
	public DVector theGradientIn() 	{	return this._iErrors;	}

	//
	// prev & next
	//
	
	private NLBase _prevLayer = null;
	private NLBase _nextLayer = null;
	public void setPrevLayer (NLBase prevLayer) throws KException { this._prevLayer = prevLayer; }
	public void setNextLayer (NLBase nextLayer) throws KException { this._nextLayer = nextLayer; }
	public NLBase getPrevLayer () { return this._prevLayer; }
	public NLBase getNextLayer () { return this._nextLayer; }
	
	//
	// dimensionality
	//
	
	private int _iSize = 0;	
	private int _oSize = 0;	

	public NLBase setInputSize(int size) throws KException {
		if (size < 1) throw new KException("Invalid layer input size ["+size+"]");
		if (this._iSize > 0) throw new KException("Layer input size already set ["+this._iSize+"]");
		this._iSize = size;
		if (this._iSize > 0 && this._oSize > 0) this.init();
		return this;
	}
	public int getInputSize() { 
		return this._iSize;
	}
	public NLBase setOutputSize(int size) throws KException {
		if (size < 1) KRAL.error("Invalid layer output size ["+size+"]");
		if (this._oSize > 0) throw new KException("Layer output size already set ["+this._oSize+"]");
		this._oSize = size;
		if (this._iSize > 0 && this._oSize > 0) this.init();
		return this;
	}
	public int getOutputSize() { 
		return this._oSize;
	}
	
	//
	// initialization
	//
	public NLBase init() throws KException {
		this._pResult = new DVector(this.getOutputSize());
		this._oValues = new DVector(this.getOutputSize());
		this._oErrors = new DVector(this.getOutputSize());
		this._nErrors = new DVector(this.getOutputSize());
		this._iErrors = new DVector(this.getInputSize());
		return this;
	}

	//
	// activation function
	//
	public NLBase setActivationFunction(AFunction activationFunction) throws KException {
		throw new KException ("'Activation Function' is not implemented in this layer oper");
	}
	public AFunction getActivationFunction() throws KException {
		throw new KException ("'Activation Function' is not implemented in this layer oper");
	}

	//
	// learning rate
	//
	public NLBase setLearningRate(double learningRate) throws KException { 
		throw new KException ("'Learning Rate' is not implemented in this layer oper");
	}
	public double getLearningRate() throws KException { 
		throw new KException ("'Learning Rate' is not implemented in this layer oper");
	}

	//
	// probabilistic leaning factor
	//
	public NLBase setMisslearningFactor(double probMissLearningFactor) throws KException { 
		throw new KException ("'Learning Factor' is not implemented in this layer oper");
	}
	public double getMisslearningFactor() throws KException { 
		throw new KException ("'Learning Factor' is not implemented in this layer oper");
	}
	
	//
	// weights & biases
	//
	
	public DMatrix theWeights() throws KException 	{	throw new KException ("'Weight Matrix' is not implemented in this layer oper");	}
	public DVector theBiases() throws KException 		{	throw new KException ("'Bias Vector' is not implemented in this layer oper");		}

	public NLBase setWeights(DMatrix weights) throws KException { 
		throw new KException ("'Weight Matrix' is not implemented in this layer oper");
	}
	public NLBase setWeightsNormal() throws KException { 
		throw new KException ("'Weight Matrix' is not implemented in this layer oper");
	}
	public NLBase setWeightsRandom() throws KException { 
		throw new KException ("'Weight Matrix' is not implemented in this layer oper");
	}
	public NLBase setWeightsNull() throws KException { 
		throw new KException ("'Weight Matrix' is not implemented in this layer oper");
	}
	public DMatrix getWeights() throws KException { 
		throw new KException ("'Weight Matrix' is not implemented in this layer oper");
	}

	public NLBase setWeights(String weights) throws KException { 
		throw new KException ("'Weight Matrix' is not implemented in this layer oper");
	}
	public NLBase setBiases(String biases) throws KException { 
		throw new KException ("'Bias Vector' is not implemented in this layer oper");
	}

	public NLBase setBiases(DVector biases) throws KException { 
		throw new KException ("'Bias Vector' is not implemented in this layer oper");
	}
	public NLBase setBiasesNormal() throws KException { 
		throw new KException ("'Bias Vector' is not implemented in this layer oper");
	}
	public NLBase setBiasesRandom() throws KException { 
		throw new KException ("'Bias Vector' is not implemented in this layer oper");
	}
	public NLBase setBiasesNull() throws KException { 
		throw new KException ("'Bias Vector' is not implemented in this layer oper");
	}
	public DVector getBiases() throws KException { 
		throw new KException ("'Bias Vector' is not implemented in this layer oper");
	}

	//
	// feedForward
	//
	public NLBase feed(DVector input) throws KException {
		NLBase layer,nextLayer;
		layer = this;
		// System.out.println(">>>"+input.toString());
		layer.feedForward(input);
		while ( (nextLayer = layer.getNextLayer()) != null) {
			// System.out.println(">>>"+layer.theOutputValues().toString());
			nextLayer.feedForward(layer.theOutputValues());
			layer = nextLayer;
		}
		return layer;
	}

	public NLBase feedForward(DVector input) throws KException { 
		throw new KException ("'Feed Forward' is not implemented in this layer oper");
	}
	
	//
	// learn :: backPropagation
	//
	public NLBase learn(DVector input, DVector target) throws KException {
		NLBase endPoint = this.feed(input);
		endPoint.back(target);
		return this;
	}
	
	public NLBase back(DVector target) throws KException { 
		NLBase layer,prevLayer;
		// calcErrors
		layer = this;
		layer.calcErrorByTarget(target);
		// System.out.println("calcErrorByTarget");
		// skip softmax
		while ( (prevLayer = layer.getPrevLayer()) != null && layer.getType().equals("softmax")) {
			layer = prevLayer;
			layer.calcErrorByTarget(target);
			// System.out.println("calcErrorByTarget");
		}
		// propagate errors
		while ( (prevLayer = layer.getPrevLayer()) != null) {
			prevLayer.calcErrorByPropagation(layer); // GradientIn?  
			layer = prevLayer;
			// System.out.println("calcErrorByPropagation");
		}
		// System.exit(0);
		// backPropagation
		layer = this;
		layer.backPropagation();
		while ( (prevLayer = layer.getPrevLayer()) != null) {
			prevLayer.backPropagation(); 
			layer = prevLayer;
		}
		return this;
		// return this;
	}

	public NLBase backPropagation() throws KException { 
		throw new KException ("'Back Propagation' is not implemented in this layer oper");
	}

	//
	// learn
	//
	public NLBase calcErrorByTarget(DVector target) throws KException {
		throw new KException ("'Calc Error' is not implemented in this layer oper");
	}
	public NLBase calcErrorByPropagation(NLBase layer) throws KException {
		throw new KException ("'Calc Error' is not implemented in this layer oper");
	}
	public double calcError(DVector target) throws KException {
		throw new KException ("'Calc Accuracy' is not implemented in this layer oper");
	}
	

	//
	// test 
	//
	protected double _lastError = 999999;

	public double getLastError() throws KException { return this._lastError; }
	
	public void test(KVectorSet inputVectors,KVectorSet outputVectors) throws KException {
		double error = 0.0;
		long   cases = 0;
		for (String iVectorName : inputVectors.theNames().keySet()) {
			if (outputVectors.exists(iVectorName)) {
				DVector iVector = inputVectors.getVector(iVectorName).getValues();
				DVector oVector = outputVectors.getVector(iVectorName).getValues();
				error += this.test(iVector,oVector);
				cases ++;
			}
		}
		if (cases > 0) this._lastError = error / (double)cases;
	}

	public double test(DVector iVector,DVector oVector) throws KException {
		double error = 0;
		NLBase endPoint = this.feed(iVector);
		error = endPoint.calcError(oVector);
		return error;
	}

	//
	// accuracy 
	//
	protected double _accuracy = 0.0;
	protected double _max_expected_DIFF = 0.25;

	public double getLastAccuracy() throws KException { return this._accuracy; }
	
	public void accuracy(KVectorSet inputVectors,KVectorSet outputVectors) throws KException {
		this._accuracy = 0.0;
		long   cases = 0;
		for (String iVectorName : inputVectors.theNames().keySet()) {
			if (outputVectors.exists(iVectorName)) {
				DVector iVector = inputVectors.getVector(iVectorName).getValues();
				DVector oVector = outputVectors.getVector(iVectorName).getValues();
				this._accuracy += this.accuracy(iVector,oVector);
				cases += oVector.size();
			}
		}
		if (cases > 0) this._accuracy /= (double)cases;
	}

	public double accuracy(DVector iVector,DVector oVector) throws KException {
		double rights = 0;
		NLBase endPoint = this.feed(iVector);
		for (int i=0; i<oVector.size(); i++) {
			if (Math.abs(oVector.getValue(i) - endPoint.theOutputValues().getValue(i)) <= _max_expected_DIFF) {
				rights ++;
			}
		}
		// System.out.println("["+String.format("%5.0f/%d", rights,oVector.size())+"] "+oVector.toString() + " X " + endPoint.theOutputValues().toString());
		return rights;
	}

	
	
	
	//
	// REVIEW FROM HERE
	//
	
	public NLBase resetWeights() throws KException {
		//this.theWeights() = new DMatrix(this._iSize,this._oSize).randomize(-2,+2); // randomizeAsNormal(); // randomize(-0.1,+0.1);
		//this.theBiases() = new DVector(this._oSize).randomize(-2,+2); // randomizeAsNormal(); // randomize(-0.1,+0.1);
		return this;
	}

	public NLBase () throws KException {
		
	}

	/*
	public NLBase (int inputSize, int outputSize) throws KException {
		this
		.setInputSize(inputSize)
		.setOutputSize(outputSize)
		.setLearningRate(HXLayerSetup.DEFAULT_LEARNING_RATE)
		.setProbMissLearningFactor(HXLayerSetup.DEFAULT_PROB_MISSLEARNING_FACTOR)
		.setActivationFunction(HXLayerSetup.DEFAULT_ACTIVATION_FUNCTION)
		;
		this.initializeInternalVectors();
		this.resetWeights();
	}
	
	public NLBase (HXLayerSetup ls) throws KException {
		this
		.setInputSize(ls.getInputSize())
		.setOutputSize(ls.getOutputSize())
		.setLearningRate(ls.getLearningRate())
		.setProbMissLearningFactor(ls.getProbMissLearningFactor())
		.setActivationFunction(ls.getActivationFunction())
		;
		this.initializeInternalVectors();
		this.resetWeights();
	}
	*/


	
	/*

	//
	// feedForward
	//

	public NLBase feedForward(DVector input) throws KException {
		// test input.cardinality
		if (input.length() != this.getInputSize())
			Hextrato.error("Input vector size ["+input.length()+"] does not match input layer size["+this.getInputSize()+"]");
		this._iValues = input;
		
		// ...
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			double oValue = 0;
			if (this._pBiases != null) oValue += this._pBiases.getValue(ox);
			if (this._pWeight != null) {
				for (int ix = 0; ix < this.getInputSize(); ix++) {
					oValue += this._iValues.getValue(ix) * this._pWeight.getValue(ix,ox);
				}
			} else {
				oValue += this._iValues.getValue(ox);
			}
			this._pResult.setValue( ox, oValue );
			oValue = this.getActivationFunction().function(oValue);
			this._oValues.setValue( ox, oValue );
		}
		return this;
	}

	//
	// backpropagation
	//

	private double _layerError = 0.0;

	public void calcLayerErrorByGradient(HXBase_2_Layer layer) throws KException {
		// Hextrato.message(">>> weigths "+(layer.theWeights().rows())+","+(layer.theWeights().cols()));
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			double error = 0;
			for (int nx = 0; nx < layer.getOutputSize(); nx++) {
				// Hextrato.message(">>> weigth "+(ox)+","+(nx));
				error += layer.theGradientNet().getValue(nx) * layer.theWeights().getValue(ox, nx);
			}
			this._oErrors.setValue(ox, error);
			error *= this.getActivationFunction().derivative(this._pResult.getValue(ox));
			this._nErrors.setValue(ox, error);
		}
	}
	
	public void calcLayerErrorByTarget(DVector target) throws KException {
		double error;
		this._layerError = 0;
		for (int ox = 0; ox < this.getOutputSize(); ox++) {
			error = (this._oValues.getValue(ox) - target.getValue(ox));
			this._oErrors.setValue(ox, error);
			this._layerError += error*error;
			error *= this.getActivationFunction().derivative(this._pResult.getValue(ox));
			this._nErrors.setValue(ox, error);
		}
		this._layerError /= 2; // Math.sqrt(this._outputError);
	}
	public double getLayerError() {
		return this._layerError;
	}
	public double getError() {
		return this._layerError;
	}

	public NLBase updateWeightsAndBiases() throws KException {
		//Hextrato.message("W(-) = "+this.theWeights());
		//Hextrato.message("b(-) = "+this.theBiases());
		for (int o=0; o<this.getOutputSize(); o++) {
			double gerror = this._nErrors.getValue(o);
			// weights
			for (int i=0; i<this.getInputSize(); i++) {
				double w = this.theWeights().getValue(i,o);
				w -= ( this.getLearningRate() * gerror * this.theInputValues().getValue(i) );
				this.theWeights().setValue(i,o,w);
			}
			// biases
			double b = this.theBiases().getValue(o);
			b -= ( this.getLearningRate() * gerror );	// ???
			this.theBiases().setValue(o, b);
		}
		//Hextrato.message("W(+) = "+this.theWeights());
		//Hextrato.message("b(+) = "+this.theBiases());
		return this;
	}

	*/
}
