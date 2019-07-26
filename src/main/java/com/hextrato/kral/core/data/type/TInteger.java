package com.hextrato.kral.core.data.type;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class TInteger implements ADataType<Integer> {

	public Integer valueOf(String value) throws KException {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			throw new KException("Invalid number format ["+value+"] for type Integer");
		}
	}

	public boolean GT (String value1, String value2) throws KException {
		return (valueOf(value1) > valueOf(value2));
	}
	public boolean GTE (String value1, String value2) throws KException {
		return (valueOf(value1) >= valueOf(value2));
	}
	public boolean LT (String value1, String value2) throws KException {
		return (valueOf(value1) < valueOf(value2));
	}
	public boolean LTE (String value1, String value2) throws KException {
		return (valueOf(value1) <= valueOf(value2));
	}

	public String add (String value1, String value2) throws KException {
		return Long.toString(valueOf(value1) + valueOf(value2));
	}
	public String sub (String value1, String value2) throws KException {
		return Long.toString(valueOf(value1) - valueOf(value2));
	}
	public String mult (String value1, String value2) throws KException {
		return Long.toString(valueOf(value1) * valueOf(value2));
	}
	public String div (String value1, String value2) throws KException {
		return Long.toString(valueOf(value1) / valueOf(value2));
	}
	public String pow (String value1, String value2) throws KException {
		return Long.toString( (long) (Math.pow( valueOf(value1) , valueOf(value2) )) );
	}
	
}
