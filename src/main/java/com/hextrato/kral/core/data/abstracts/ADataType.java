package com.hextrato.kral.core.data.abstracts;

import com.hextrato.kral.core.util.exception.KException;

public interface ADataType<Meta> {

	public Meta valueOf(String value) throws KException;

	public default String toString(Meta value) throws KException {
		return value.toString(); 
	}
	
	public default boolean EQ (String value1, String value2) throws KException {
		return value1.equals(value2);
	}
	public default boolean GT (String value1, String value2) throws KException {
		return (value1.compareTo(value2) > 0);
	}
	public default boolean GTE (String value1, String value2) throws KException {
		return (value1.compareTo(value2) >= 0);
	}
	public default boolean LT (String value1, String value2) throws KException {
		return (value1.compareTo(value2) < 0);
	}
	public default boolean LTE (String value1, String value2) throws KException {
		return (value1.compareTo(value2) <= 0);
	}

	public default String concat (String value1, String value2) throws KException {
		throw new KException("Not implemented for given datatype");
	}
	public default String add (String value1, String value2) throws KException {
		throw new KException("Not implemented for given datatype");
	}
	public default String sub (String value1, String value2) throws KException {
		throw new KException("Not implemented for given datatype");
	}
	public default String mult (String value1, String value2) throws KException {
		throw new KException("Not implemented for given datatype");
	}
	public default String div (String value1, String value2) throws KException {
		throw new KException("Not implemented for given datatype");
	}
	public default String pow (String value1, String value2) throws KException {
		throw new KException("Not implemented for given datatype");
	}

}
