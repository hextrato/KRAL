package com.hextrato.kral.core.data.struct;

import java.util.HashMap;
import java.util.Map;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.util.exception.KException;

public class DVariable {

	//
	// pre instantiated datatypes 
	//
	private static String datatypeRootPath = KRAL.DATATYPE_PACKAGE + ".T";

	private static Map<String,ADataType<?>> _datatypeSet = new HashMap<String,ADataType<?>>();

	public static String getNormalisedDatatype(String datatype) {
		datatype = datatype.substring(0,1).toUpperCase() + datatype.substring(1).toLowerCase();
		return datatype;
	}
	
	public static ADataType<?> getDatatype(String datatype) throws KException {
		String typeFullName = datatypeRootPath + getNormalisedDatatype(datatype);
		if (_datatypeSet.containsKey(typeFullName))
			return _datatypeSet.get(typeFullName);
		else {
			ADataType<?> dtype = null;
			try {
				dtype = (ADataType<?>)(Class.forName(typeFullName).newInstance());
				_datatypeSet.put(typeFullName, dtype);
				return dtype;
			} catch (Exception e) {
				KRAL.error("Var type ["+datatype+"] not found: class ["+typeFullName+"] does not exist.");
				return null;
			} 
		}
	}
	
	//
	// variable 
	//

	private ADataType<?> _datatype;
	private String 				_value = "";
	private String[] 			_validValues = null;
	private boolean				_caseSensitive = false;
	
	public DVariable () throws KException { this("String"); }
	public DVariable (String datatype) throws KException {
		this._datatype = getDatatype(datatype);
	}
	public DVariable (String datatype, String value) throws KException {
		this(datatype);
		this.setValue(value);
	}

	public DVariable setValue (String value) throws KException {
		if (value.startsWith("\"") && value.endsWith("\"")) value = value.substring(1, value.length()-1);
		ADataType<?> dtype = this._datatype;
		this._value = dtype.valueOf(value).toString();
		return this;
	}

	public DVariable setValidValues (String[] validValues) throws KException {
		this._validValues = validValues;
		return this;
	}

	public boolean isValidValue (String value) {
		if (this._validValues == null) {
			return true;
		} else { 
			for (String validValue : this._validValues) {
				if (value.equals(validValue) || (value.equalsIgnoreCase(validValue) && !this._caseSensitive) )
					return true;
			}
		}
		return false;
	}
	public DVariable setCaseSensitive (boolean flag) throws KException {
		this._caseSensitive = flag;
		return this;
	}

	public String getValue () {
		return this._value;
	}

	public String getDatatype () {
		return this._datatype.getClass().getName().replace(datatypeRootPath,"");
	}
}
