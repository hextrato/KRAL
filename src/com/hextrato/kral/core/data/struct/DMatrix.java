package com.hextrato.kral.core.data.struct;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.util.UFiller;
import com.hextrato.kral.core.util.exception.KException;

public class DMatrix {

	/*
	 * TODO:
	 * - CopyValuesFrom (with initial offset/position [x,y]) - is it possible?
	 */

	// private static final double[][] _emptyMatrix = new double[][] {};
	private static final DVector[] _emptyMatrix = new DVector[] { };
	
	// private double[][]	_matValues = null;
	private DVector[]  _rows = null;
	private int 		_numRows = 0;
	private int 		_numCols = 0;
	private double 		_dimensionality = 0;

	private void setDimensions (int rows, int cols) {
		this._numRows = rows;
		this._numCols = cols;
		this._dimensionality = Math.sqrt(Math.min(rows , cols));
	}
	
	public int rows() {return this._numRows;}
	public int cols() {return this._numCols;}

	public DVector[] theValues() {
		if (this._rows == null)
			return _emptyMatrix;
		else
			return this._rows;
	}

	//
	// constructor
	//

	public DMatrix() throws KException {
		//this._numRows = 0;
		//this._numCols = 0;
		this.setDimensions(0, 0);
		// this._matValues = null;
		this._rows = null;
	}

	public DMatrix(int rows, int cols) throws KException {
		if (rows < 1 || cols < 1)
			KRAL.error("Invalid matrix size [rows:"+rows+",cols:"+cols+"]");
		// this._numRows = rows;
		// this._numCols = cols;
		this.setDimensions(rows, cols);
		// this._matValues = new double[rows][cols];
		this._rows = new DVector[rows];
		for (int r=0; r<rows; r++) this._rows[r] = new DVector(cols);
	}
	
	/*
	public HXMatrix(double[][] array) throws HXException {
		// this._numRows = array.length;
		// this._numCols = array[0].length;
		this.setDimensions(array.length, array[0].length);
		// this._matValues = array;
	}
	*/

	//
	// copy / set
	//
	
	public DMatrix copyValuesFrom(DVector[] values) throws KException {
		if (values.length != this.rows() || values[0].length() != this.cols())
			throw new KException ("HEXRR: input matrix size [rows:"+values.length+",cols:"+(values[0].length())+"] does not match matrix dimensions ["+this.rows()+","+this.cols()+"]");
		for (int row = 0; row < this.rows(); row++)
			for (int col = 0; col < this.cols(); col++)
				this.setValue(row, col, values[row].getValue(col));
		return this;
	}

	public DMatrix copyValuesFrom(DMatrix matrix) throws KException {
		return this.copyValuesFrom(matrix.theValues());
	}

	public DMatrix setValue(int row, int col, double value) {
		// this._matValues[row][col] = value;
		this._rows[row].setValue(col, value);
		return this;
	}
	public DMatrix setValue(int pos, double value) throws KException {
		if (this.rows() == 1) {
			// this._matValues[0][pos] = value;
			this._rows[0].setValue(pos, value);
		} else {
			throw new KException ("Not a unidimensional matrix");
		}
		return this;
	}

	public double getValue(int row, int col) {
		// return this._matValues[row][col];
		return this._rows[row].getValue(col);
	}
	public double getValue(int pos) throws KException {
		if (this.rows() == 1) {
			// return this._matValues[0][pos];
			return this._rows[0].getValue(pos);
		} else {
			throw new KException ("Not a unidimensional matrix");
		}
	}

	public DVector getRow(int row) {
		// return this._matValues[row];
		return this._rows[row];
	}

	public DMatrix setValues(DVector[] values) throws KException {
		if (values == null) {
			this._rows = null;
			// this._numRows = 0;
			// this._numCols = 0;
			this.setDimensions(0, 0);
		} else {
			this._rows = values;
			// this._numRows = matrix.length;
			// this._numCols = matrix[0].length;
			this.setDimensions(values.length, values[0].length());
		}
		return this;
	}

	public DMatrix setValues(DMatrix matrix) throws KException {
		return this.setValues(matrix.theValues());
	}

	public DMatrix setValues(String rowValues) throws KException {
		if (this._rows==null) throw new KException ("HXMatrix not yet initialized");
		rowValues = rowValues.trim();
		String[] mainParts = rowValues.split(":");
		if (mainParts.length!=2) throw new KException ("Incompatible matrix format 'row:[values]' in: ["+rowValues+"]");
		int matrixRow = Integer.valueOf(mainParts[0]);
		String valueList = mainParts[1];
		if (valueList.startsWith("[") && valueList.endsWith("]")) {
			valueList = valueList.substring(1, valueList.length()-1);
			String[] values = valueList.split(",");
			if (values.length != this.cols()) throw new KException ("Incompatible column dimensionality in: ["+valueList+"]");
			for (int i = 0; i < values.length; i++) this.setValue(matrixRow, i, Double.valueOf(values[i]));
		} else {
			throw new KException ("Invalid vector format in: "+valueList+" for HXMatrix values in row "+matrixRow);
		}
		return this;
	}

	//
	// random
	//

	public DMatrix randomize () throws KException {
		UFiller.randomize(this,-1,+1);
		return this;
	}
	public DMatrix randomize (double b, double e) throws KException {
		UFiller.randomize(this,b,e);
		return this;
	}
	public DMatrix randomizeAsNormal () throws KException {
		if (this.rows() > 36)
			UFiller.randomizeAsNormal(this,-6/Math.sqrt(this.rows()),+6/Math.sqrt(this.rows()));
		else 
			UFiller.randomizeAsNormal(this,-2/Math.sqrt(this.rows()),+2/Math.sqrt(this.rows()));
		return this;
	}
	public DMatrix randomizeAsNormal (double b, double e) throws KException {
		UFiller.randomizeAsNormal(this,b,e);
		return this;
	}

	//
	// fillWith
	//

	public DMatrix fillWith (double value) {
		DVector[] array = this.theValues();
		for (int ii=0; ii<array.length; ii++)
			for (int jj=0; jj<array[ii].length(); jj++)
				array[ii].setValue(jj, value);
		return this;
	}

	public DMatrix fillWithIdentity () {
		DVector[] values = this.theValues();
		for (int ii = 0; ii < values.length; ii++)
			for (int jj = 0; jj < values[ii].length(); jj++)
				values[ii].setValue(jj, ( (ii==jj) ? 1 : 0 ) );
		return this;
	}

	//
	// normalize Matrix
	//
	private DMatrix normalize (double matrixMagnitude) throws KException {
		DVector[] values = this.theValues();
		double magnitude = 0;
		for (int ii = 0; ii < values.length; ii++)
			for (int jj = 0; jj < values[ii].length(); jj++)
				magnitude += values[ii].getValue(jj) * values[ii].getValue(jj);
		magnitude = Math.sqrt(magnitude);
		double factor = magnitude / matrixMagnitude;
		if (factor == 0)
			KRAL.error("Cannot normalize matrix with magnitude 0");
		for (int ii = 0; ii < values.length; ii++)
			for (int jj = 0; jj < values[ii].length(); jj++)
				values[ii].setValue(jj, values[ii].getValue(jj) / factor );
		return this;
	}
	public DMatrix normalize () throws KException {
		return normalize ( this._dimensionality );
	}

	
	//
	// toString
	//
	public String toString() {
		String s = "[";
		for (int row = 0; row < this.rows(); row++) {
			if (row > 0) s = s + "\n,";
			// if (row > 0) s = s + ","; else s = s + " ";
			s = s + "[";
			for (int col = 0; col < this.cols(); col++) {
				if (col > 0) s = s + ",";
				String v = String.format("%1.32f", this.getValue(row,col));
				while (v.contains(".") && v.endsWith("0")) v = v.substring(0, v.length()-1);
				while (v.endsWith(".")) v = v.substring(0, v.length()-1);
				// s = s + String.format("%1.6f", this.getValue(row,col));
				s = s + v;
			}
			s = s + "]";
		}
		s = s + "]";
		return s;
	}

}
