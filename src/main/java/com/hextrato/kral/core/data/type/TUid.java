package com.hextrato.kral.core.data.type;

import java.util.UUID;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class TUid implements ADataType<String> {

	public String valueOf(String value) throws KException {
		return value;
	}

	public static String random() throws KException {
		String uid = UUID.randomUUID().toString();
		uid = uid.substring(0,8) + uid.substring(24);
		return uid;
	}
}
