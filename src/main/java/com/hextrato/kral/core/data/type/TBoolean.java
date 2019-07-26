package com.hextrato.kral.core.data.type;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class TBoolean implements ADataType<Boolean> {

	public int integerOf(String value) {
		return valueOf(value)?1:0;
	}

	public Boolean valueOf(String value) {
		if (value == null) return null;
		if (value.equals("")) return null;
		switch (value.toUpperCase()) {
		case "TRUE":
		case "T":
		case "YES":
		case "Y":
		case "1":
			return true;
		case "FALSE":
		case "F":
		case "NO":
		case "N":
		case "0":
			return false;
		}
		return false;
	}

	public String toString(Boolean value) {
		return (value.booleanValue()?"True":"False"); 
	}

	public boolean GT (String value1, String value2) throws KException {
		return (integerOf(value1) > integerOf(value2));
	}
	public boolean GTE (String value1, String value2) throws KException {
		return (integerOf(value1) >= integerOf(value2));
	}
	public boolean LT (String value1, String value2) throws KException {
		return (integerOf(value1) < integerOf(value2));
	}
	public boolean LTE (String value1, String value2) throws KException {
		return (integerOf(value1) <= integerOf(value2));
	}
	
}
