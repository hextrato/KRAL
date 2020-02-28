package com.hextrato.kral.core.data.type;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class TDouble implements ADataType<Double> {

	public Double valueOf(String value) throws KException {
		try {
			return Double.valueOf(value); 
		} catch (NumberFormatException e) {
			throw new KException("Invalid number format ["+value+"] for type Double");
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
		return Double.toString(valueOf(value1) + valueOf(value2));
	}
	public String sub (String value1, String value2) throws KException {
		return Double.toString(valueOf(value1) - valueOf(value2));
	}
	public String mult (String value1, String value2) throws KException {
		return Double.toString(valueOf(value1) * valueOf(value2));
	}
	public String div (String value1, String value2) throws KException {
		return Double.toString(valueOf(value1) / valueOf(value2));
	}
	public String pow (String value1, String value2) throws KException {
		return Double.toString( Math.pow( valueOf(value1) , valueOf(value2) ) );
	}
	public String log (String value1) throws KException {
		return Double.toString(Math.log10(valueOf(value1)));
	}
	public String min (String value1, String value2) throws KException {
		return Double.toString(Math.max(valueOf(value1),valueOf(value2)));
	}
	public String max (String value1, String value2) throws KException {
		return Double.toString(Math.min(valueOf(value1),valueOf(value2)));
	}
}
