package com.hextrato.kral.core.schema.neural;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.hyper.KVectorSet;
import com.hextrato.kral.core.schema.neural.layer.af.AFunction;
import com.hextrato.kral.core.schema.neural.layer.type.NLBase;
import com.hextrato.kral.core.util.exception.KException;

public abstract class KLayer extends AMetaNamedObject {
	
	private KNeural _neural = null;
	public KNeural getNeural() { return this._neural; }

	// private String _name;
	private String _oper;

	// public String getName() { return this._name; }
	public String getOper() { return this._oper; }
	
	public KLayer (KNeural neural, String oper) throws KException {
		if (neural == null) throw new KException("Invalid null neural");
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", neural.getSchema().getName());
		this._neural = neural;
		this.properties().declare("_neural_", "String");
		this.properties().set("_neural_", neural.getName());
		this._oper = oper.toLowerCase();
		this.properties().declare("_oper_", "String");
		this.properties().set("_oper_", this._oper);
	}

	/*
	public HextraLayer (HextraNeural neural, String name,String oper) throws HXException {
		if (neural == null) throw new HXException("Invalid null neural");
		this._neural = neural;
		// this._name = name;
		this._oper = oper.toLowerCase();
	}
	*/
	
	private KLayer _prevLayer = null;
	private KLayer _nextLayer = null;

	static final KLayer nullLayer = null;

	public void setPrevLayer (KLayer prevLayer) throws KException {
		KLayer formerPrevLayer = this._prevLayer;
		if (formerPrevLayer != null) formerPrevLayer.setNextLayer(nullLayer);
		this._prevLayer = prevLayer;
		if (prevLayer != null) {
			KLayer formerNext = prevLayer.getNextLayer();
			if (formerNext != null) formerNext.setPrevLayer(nullLayer);
			prevLayer.setNextLayer(this);
			this.setInputSize(prevLayer.getOutputSize());
		}
		// NLBase
		if (prevLayer == null) this.getBLay().setPrevLayer(null); else this.getBLay().setPrevLayer(prevLayer.getBLay()); 
	}
	
	public void setPrevLayer (String prevLayerName) throws KException {
		if (prevLayerName.equals("null")) this.setPrevLayer(nullLayer);
		else
		if (this.getNeural().layers().exists(prevLayerName)) {
			KLayer prevLayer = this.getNeural().layers().getLayer(prevLayerName);
			this.setPrevLayer(prevLayer);
		} else {
			throw new KException("Invalid layer name '"+prevLayerName+"'");
		}
	}
	
	public KLayer getPrevLayer () { return this._prevLayer; }

	private void setNextLayer (KLayer nextLayer) throws KException {
		this._nextLayer = nextLayer;
		// NLBase
		if (nextLayer == null) this.getBLay().setNextLayer(null); else this.getBLay().setNextLayer(nextLayer.getBLay()); 
		
	}

	public KLayer getNextLayer () { return this._nextLayer; }

	// 
	// general layer interface
	//

	protected NLBase _blay = null;
	
	private NLBase getBLay() throws KException { if (this._blay == null) throw new KException("Invalid null neural layer instance for layer '"+getName()+"'"); return this._blay; }
	public void setInputSize(int size) throws KException { this.getBLay().setInputSize(size); } 
	public void setOutputSize(int size) throws KException { this.getBLay().setOutputSize(size); } 
	public void setActivationFunction(String afName) throws KException { this.getBLay().setActivationFunction(AFunction.getActivationFunction(afName)); }
	public void setsetLearningRate(double learningRate) throws KException { this.getBLay().setLearningRate(learningRate); }
	public void setMisslearningFactor(double probMissLearningFactor) throws KException { this.getBLay().setMisslearningFactor(probMissLearningFactor); }

	public int getInputSize() throws KException { return this.getBLay().getInputSize(); } 
	public int getOutputSize() throws KException { return this.getBLay().getOutputSize(); } 
	public double getLearningRate() throws KException { return this.getBLay().getLearningRate(); }
	public double getMisslearningFactor() throws KException { return this.getBLay().getMisslearningFactor(); }
	public String getActivationFunction() throws KException { return AFunction.getName(this.getBLay().getActivationFunction()); }

	public void setWeightsNormal() throws KException { 
		this.getBLay().setWeightsNormal();
	}
	public void setWeightsRandom() throws KException { 
		this.getBLay().setWeightsRandom();
	}
	public void setWeightsNull() throws KException { 
		this.getBLay().setWeightsNull();
	}
	public void setWeights(String values) throws KException { 
		this.getBLay().setWeights(values);
	}
	public void setBiases(String values) throws KException { 
		this.getBLay().setBiases(values);
	}
	public void setBiasesNormal() throws KException { 
		this.getBLay().setBiasesRandom();
	}
	public void setBiasesRandom() throws KException { 
		this.getBLay().setBiasesRandom();
	}
	public void setBiasesNull() throws KException { 
		this.getBLay().setBiasesNull();
	}
	public DMatrix theWeights() throws KException 	{ return this.getBLay().theWeights(); }
	public DVector theBiases() throws KException 		{ return this.getBLay().theBiases(); }
	public DVector theInputValues() throws KException	{ 	return this.getBLay().theInputValues();	}
	public DVector theOutputValues() throws KException{	return this.getBLay().theOutputValues();	}
	public DVector theGradientOut() throws KException	{	return this.getBLay().theGradientOut();	}
	public DVector theGradientNet() throws KException	{	return this.getBLay().theGradientNet();	}
	public DVector theGradientIn() throws KException	{	return this.getBLay().theGradientIn();	}

	public void feed(DVector input) throws KException 		{ this.getBLay().feed(input); }
	public void back(DVector target) throws KException 		{ this.getBLay().back(target); }
	public void learn(DVector input,DVector target) throws KException 		{ this.getBLay().learn(input,target); }
	
	public void test(KVectorSet inputVectors,KVectorSet outputVectors) throws KException { this.getBLay().test(inputVectors, outputVectors); }
	public void accuracy(KVectorSet inputVectors,KVectorSet outputVectors) throws KException { this.getBLay().accuracy(inputVectors, outputVectors); }
	public double getLastError() throws KException { return this.getBLay().getLastError(); }
	public double getLastAccuracy() throws KException { return this.getBLay().getLastAccuracy(); }

	//
	// EXPORT
	//

	public void hextract (BufferedWriter bf) throws KException {
		KLayer prevLayer = this.getPrevLayer();
        try {
        	if (prevLayer == null) {
        		bf.write( String.format("layer %s create %s", this.getName(), this.getOper()) );
    			bf.newLine();
        		bf.write( String.format("layer %s config input_size %d", this.getName(), this.getInputSize()) );
    			bf.newLine();
        	} else {
        		bf.write( String.format("layer %s create %s after %s", this.getName(), this.getOper(), prevLayer.getName()) );
    			bf.newLine();
        	}
			// properties
        	if (!this.getOper().equals("softmax")) {
        		bf.write( String.format("layer %s config output_size %d", this.getName(), this.getOutputSize()) );
        		bf.newLine();
        	}
			try {  
	    		bf.write( String.format("layer %s config learn_rate %f", this.getName(), this.getLearningRate()) );
				bf.newLine();
			} catch (KException e) {} finally {};
			try {  
	    		bf.write( String.format("layer %s config misslearn_factor %f", this.getName(), this.getMisslearningFactor()) );
				bf.newLine();
			} catch (KException e) {} finally {};
			try {  
	    		bf.write( String.format("layer %s config activation_function %s", this.getName(), this.getActivationFunction()) );
				bf.newLine();
			} catch (KException e) {} finally {};
			try {
	    		bf.write( String.format("layer %s config biases %s", this.getName(), this.theBiases().toString()) );
				bf.newLine();
			} catch (KException e) {} finally {};
			try {
				DMatrix weights = this.theWeights();
				DVector rowValues = new DVector(weights.cols()); 
				if (weights != null) {
					for (int row=0; row<weights.rows(); row++) {
						rowValues.copyValuesFrom(weights.getRow(row));
			    		bf.write( String.format("layer %s config weights %d:%s", this.getName(), row, rowValues.toString()) );
						bf.newLine();
					}
				}
			} catch (KException e) {} finally {};
			// next Layer
			if (this.getNextLayer() != null) this.getNextLayer().hextract(bf);
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
}
