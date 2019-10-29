package com.hextrato.kral.core.schema.neural.layer.af;

public class AFTanh extends AFunction {

	public double function (double x) {
		return (Math.tanh(x));
		// return (-1 + 2 / ( 1 + Math.pow(Math.E,(-2*x)) ) );
	}

	public double derivative (double x) {
		//double coshx = Math.cosh(x);
		//double denom = (Math.cosh(2*x) + 1);
		//return 4 * coshx * coshx / (denom * denom);
		double fx = function(x);
		fx = fx * fx;
		// return 1-Math.pow(function(x),2);
		return 1-fx;
	}

}
