package com.hextrato.kral.core.data.util;

import java.util.Random;

import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.data.struct.DVector;

public class UFiller {
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// random()
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

	// HXVector
	
	public static void randomize (DVector vector, double b, double e) {
		Random r = new Random();
		double[] array = vector.theValues();
		for (int ii=0; ii<array.length; ii++)
			// array[ii] = Math.random()*(e-b)+b;
			array[ii] = r.nextDouble() * (e-b) + b;
	}
	public static void randomize (DVector vector) {
		randomize(vector,-1,+1);
	}

	// HXMatrix
	public static void randomize (DMatrix matrix, double b, double e) {
		Random r = new Random();
		DVector[] values = matrix.theValues();
		for (int ii=0; ii<values.length; ii++)
			for (int jj=0; jj<values[ii].length(); jj++)
				// array[ii][jj] = Math.random()*(e-b)+b;
				values[ii].setValue(jj, r.nextDouble() * (e-b)+b);
	}
	public static void randomize (DMatrix matrix) {
		randomize(matrix,-1,+1);
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// randomNormal()
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

	// HXVector
	
	public static void randomizeAsNormal (DVector vector, double b, double e) {
		Random r = new Random();
		double[] array = vector.theValues();
		for (int ii=0; ii<array.length; ii++) {
			double r1 = r.nextDouble(); //Math.random();
			double r2 = r.nextDouble(); //Math.random();
			double r3 = (r.nextDouble() > 0.5) ? +1.0 : -1.0;
			// array[ii] = r1 * r2 * (e-b) + b;
			array[ii] = r1 * r2 * r3 + (e+b)/2.0;
		}
	}
	public static void randomNormal (DVector vector) {
		randomizeAsNormal(vector,-1,+1);
	}

	// HXMatrix
	
	public static void randomizeAsNormal (DMatrix matrix, double b, double e) {
		Random r = new Random();
		DVector[] values = matrix.theValues();
		for (int ii=0; ii<values.length; ii++)
			for (int jj=0; jj<values[ii].length(); jj++) {
				double r1 = r.nextDouble(); //Math.random();
				double r2 = r.nextDouble(); //Math.random();
				double r3 = (r.nextDouble() > 0.5) ? +1 : -1;
				// values[ii].setValue(jj, r1 * r2 * (e-b) + b);
				values[ii].setValue(jj, r1 * r2 * r3 + (e+b)/2.0);
			}
	}
	public static void randomizeAsNormal (DMatrix matrix) {
		randomizeAsNormal(matrix,-1,+1);
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// fillWith()
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// double[]
	public static void fillWith (DVector vector, double value) {
		double[] array = vector.theValues();
		for (int ii=0; ii<array.length; ii++)
			array[ii] = value;
	}
	// double[][]
	public static void fillWith (DMatrix matrix, double value) {
		DVector[] values = matrix.theValues();
		for (int ii=0; ii<values.length; ii++)
			for (int jj=0; jj<values[ii].length(); jj++)
				values[ii].setValue(jj, value);
	}	
	
	
	/*
	HXVector
	public static void fillWith (HXVector vector, double value) {
		fillWith(vector.theValues(),value);
	}
	public static void fillWithZero (HXVector vector) {
		fillWith(vector.theValues(),0);
	}
	public static void fillWithOne (HXVector vector) {
		fillWith(vector.theValues(),1);
	}
	// HXMatrix
	public static void fillWith (HXMatrix matrix, double value) {
		fillWith(matrix.theValues(),value);
	}
	public static void fillWithZero (HXMatrix matrix) {
		fillWith(matrix.theValues(),0);
	}
	public static void fillWithOne (HXMatrix matrix) {
		fillWith(matrix.theValues(),1);
	}

	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// normalize()
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// double[]
	public static void normalize (HXVector vector, double magnitude) {
		double d = HXDatasetter.magnitude(vector);
		// adjust to magnitude = exact(magnitude)
		if (d > 0) { 
			double factor = magnitude / d; // (1/d)
			for (int p = 0; p < vector.size(); p++) {
				vector.setValue(p,vector.getValue(p) * factor);
			}
		}
	}
	public static void normalize (HXVector vector, double maxMagnitude, double maxThreshold) {
		double d = HXDatasetter.magnitude(vector);
		// adjust to magnitude = max (maxMagnitude)
		if (maxMagnitude > 0 && d > maxMagnitude) {
			double factor = maxMagnitude / d; // (1 / d)
			for (int p = 0; p < vector.size(); p++) {
				vector.setValue(p,vector.getValue(p) * factor);
			}
		}
		// adjust to maxThreshold
		for (int p = 0; p < vector.size(); p++) {
			double value = vector.getValue(p);
			if (maxThreshold > 0 && value > maxThreshold) {
				value = maxThreshold;
				vector.setValue(p,maxThreshold);
			}
			if (maxThreshold > 0 && value < -maxThreshold) {
				value = -maxThreshold;
				vector.setValue(p,-maxThreshold);
			}
		}
	}
	*/
	
}
