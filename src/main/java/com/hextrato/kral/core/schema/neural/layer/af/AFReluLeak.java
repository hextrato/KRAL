package com.hextrato.kral.core.schema.neural.layer.af;

public class AFReluLeak extends AFunction {
	public double function (double x) {
		return ((x<0)?x*0.01:x);
	}
	public double derivative (double x) {
		return ((x<0)?0.01:1);
	}
}
