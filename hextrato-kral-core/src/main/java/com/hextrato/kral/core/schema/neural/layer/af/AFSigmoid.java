package com.hextrato.kral.core.schema.neural.layer.af;

public class AFSigmoid extends AFunction {

	public double function (double x) {
		double e_x = Math.pow(Math.E,(-x));
		return (1.0 / (e_x + 1.0));
		// return (1/( 1 + Math.pow(Math.E,(-x))));
	}

	public double derivative (double x) {
		double f_x = function(x);
		return ( f_x )*( 1 - f_x );
	}

}
