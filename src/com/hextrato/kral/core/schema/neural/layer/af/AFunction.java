package com.hextrato.kral.core.schema.neural.layer.af;

import java.util.Map;

import com.hextrato.kral.core.util.exception.KException;

import java.util.HashMap;

public abstract class AFunction {

	public final static AFunction AF_RELU = new AFRelu(); 
	public final static AFunction AF_LEAKRELU = new AFReluLeak(); 
	public final static AFunction AF_LINEAR = new AFLinear(); 
	public final static AFunction AF_SIGMOID = new AFSigmoid(); 
	public final static AFunction AF_TANH = new AFTanh(); 
	public final static AFunction AF_LOGISTIC = new AFLogistic(); 

	public double function (double x) { 
		return x; 
	};
	public double derivative (double x) {
		return (this.function(x+0.0001)-this.function(x-0.0001))/0.0002;
	}
	
	static Map<String,AFunction> _activationFunctions = null;

	public static AFunction getActivationFunction(String afName) throws KException {
		if (_activationFunctions == null) {
			_activationFunctions = new HashMap<String,AFunction>();
			_activationFunctions.put("relu", AF_RELU);
			_activationFunctions.put("leakrelu", AF_LEAKRELU);
			_activationFunctions.put("linear", AF_LINEAR);
			_activationFunctions.put("sigmoid", AF_SIGMOID);
			_activationFunctions.put("tanh", AF_TANH);
			_activationFunctions.put("logistic", AF_LOGISTIC);
		}
		if (!_activationFunctions.containsKey(afName)) throw new KException("Invalid activation function '"+afName+"'");
		return _activationFunctions.get(afName);
	}
	
	public static String getName(AFunction af) throws KException {
		if (af == null) return "NULL";
		if (af == AF_RELU) return "relu";
		if (af == AF_LEAKRELU) return "leakrelu";
		if (af == AF_LINEAR) return "linear";
		if (af == AF_SIGMOID) return "sigmoid";
		if (af == AF_TANH) return "tanh";
		if (af == AF_LOGISTIC) return "logistic";
		throw new KException("Unrecognized activation function type");
	}
}
