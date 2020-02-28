package com.hextrato.kral.core.data.type;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.util.exception.KException;

public class TVector implements ADataType<DVector> {

	public DVector valueOf(String value) throws KException {
		value = value.trim();
		if (value.startsWith("[") && value.endsWith("]")) {
			value = value.substring(1, value.length()-1);
			String[] values = value.split(",");
			DVector v = new DVector(values.length);
			for (int i = 0; i < values.length; i++) v.setValue(i, Double.valueOf(values[i]));
			return v;
		} else {
			throw new KException ("Invalid vector format in: "+value);
		}
	}

	/*
	public boolean GT (String value1, String value2) throws KException {
		return (valueOf(value1).magnitude() > valueOf(value2).magnitude());
	}
	public boolean GTE (String value1, String value2) throws KException {
		return (valueOf(value1).magnitude() >= valueOf(value2).magnitude());
	}
	public boolean LT (String value1, String value2) throws KException {
		return (valueOf(value1).magnitude() < valueOf(value2).magnitude());
	}
	public boolean LTE (String value1, String value2) throws KException {
		return (valueOf(value1).magnitude() <= valueOf(value2).magnitude());
	}
	*/
	
}
