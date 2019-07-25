package com.hextrato.kral.core.data.type;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class TString implements ADataType<String> {

	public String valueOf(String value) throws KException {
		// if (value == null) return null;
		return value; // even NULL anyway
	}

	public String concat (String value1, String value2) throws KException {
		return value1 + value2;
	}
	
}
