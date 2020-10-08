package com.hextrato.kral.core.data.abstracts;

import java.util.Properties;

import com.hextrato.kral.core.util.exception.KException;

public abstract class AMetaObject {

	protected Properties _properties;
	protected Properties _ranges;
	
	abstract public void setUID(String uid) throws KException;
	abstract public String getUID() throws KException;
	abstract public void setName(String name) throws KException;
	abstract public String getName() throws KException;
	
	public AMetaObject () {
	}

	public Properties properties() throws KException {
		if (this._properties == null) throw new KException("Object has no property set");
		return this._properties;
	}
	public void initProperties() {
		this._properties = new Properties();
		this._ranges = new Properties();
	}
	public void defineProperty (String property, String[] range) throws KException {
		if (range == null || range.length < 1) throw new KException("Null or empty range set definition for property '"+property+"'");
		defineProperty(property, range, range[0]);
	}
	public void defineProperty (String property, String value) throws KException {
		defineProperty(property, null, value);
	}
	public void defineProperty (String property, String[] range, String value) throws KException {
		if (this._properties == null) throw new KException("Object has no property set");
		if (this._properties.containsKey(property)) throw new KException("Property '"+property+"' already defined");
		this._properties.setProperty(property,value);
		if ( range !=null ) this._ranges.put(property, range);
	}
	public void setProperty (String property, String value) throws KException {
		if (this._properties == null) throw new KException("Object has no property set");
		if (!this._properties.containsKey(property)) throw new KException("Invalid property '"+property+"'");
		if (this._ranges.containsKey(property)) {
			boolean validValue = false;
			String valueList = "";
			for (String possibleValue : (String[])this._ranges.get(property) ) {
				valueList = valueList + (valueList.equals("")?"(":",") + possibleValue;
				if (possibleValue.equals(value)) validValue = true;
			}
			valueList = valueList + ")";
			if (!validValue) throw new KException("Invalid value '"+value+"' for property '"+property+"' "+valueList);
		}
		this._properties.setProperty(property, value);
	}
	public String getProperty (String property) throws KException {
		if (this._properties == null) throw new KException("Object has no property set");
		if (!this._properties.containsKey(property)) throw new KException("Invalid property '"+property+"'");
		return this._properties.getProperty(property);
	}

	public boolean exits (String property) {
		return this._properties.containsKey(property);
	}

}
