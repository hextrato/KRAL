package com.hextrato.kral.core.schema.neural.layer.af;

public class AFRelu extends AFunction {
	public double function (double x) {
		return ((x<0)?0:x);
	}
	public double derivative (double x) {
		return ((x<0)?0:1);
	}
}
