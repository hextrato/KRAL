package com.hextrato.kral.core.data.struct;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class DOperators {

	//
	// Logical Operations
	//
	
	public static boolean EQ (DVariable var1, DVariable var2) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var1.getDatatype());
		return dtype.EQ(var1.getValue(), var2.getValue() );
	}

	public static boolean IS (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		return dtype.EQ( var.getValue(), dtype.valueOf(value).toString() );
	}

	public static boolean GT (DVariable var1, DVariable var2) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var1.getDatatype());
		return dtype.GT( var1.getValue(), var2.getValue() );
	}
	public static boolean GTE (DVariable var1, DVariable var2) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var1.getDatatype());
		return dtype.GTE( var1.getValue(), var2.getValue() );
	}
	public static boolean LT (DVariable var1, DVariable var2) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var1.getDatatype());
		return dtype.LT( var1.getValue(), var2.getValue() );
	}
	public static boolean LTE (DVariable var1, DVariable var2) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var1.getDatatype());
		return dtype.LTE( var1.getValue(), var2.getValue() );
	}

	public static void concat (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.concat( var.getValue(), value ) );
	}
	public static void add (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.add( var.getValue(), value ) );
	}
	public static void sub (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.sub( var.getValue(), value ) );
	}
	public static void mult (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.mult( var.getValue(), value ) );
	}
	public static void div (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.div( var.getValue(), value ) );
	}
	public static void pow (DVariable var, String value) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.pow( var.getValue(), value ) );
	}
	public static void log (DVariable var) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.log( var.getValue() ) );
	}
	public static void min (DVariable var, String minValue) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.min( var.getValue(), minValue ) );
	}
	public static void max (DVariable var, String maxValue) throws KException {
		ADataType<?> dtype = DVariable.getDatatype(var.getDatatype());
		var.setValue( dtype.max( var.getValue(), maxValue ) );
	}

}
