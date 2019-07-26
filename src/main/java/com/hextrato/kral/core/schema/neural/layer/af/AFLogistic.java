package com.hextrato.kral.core.schema.neural.layer.af;

public class AFLogistic extends AFunction {

	public double function (double x) {
		return ( x / ( 1 + Math.abs(x) ) );
	}

	public double derivative (double x) {
		// return ( 1 / ( 1 + Math.abs(x) ) );
		return ( 1 / Math.pow( 1 + Math.abs(x) , 2 ) );
	}

}
