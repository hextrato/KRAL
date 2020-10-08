package com.hextrato.kral.core.data.abstracts;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.struct.DCommentSet;
import com.hextrato.kral.core.data.struct.DVariableSet;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public abstract class AMetaUIDObject {

	final static public String __INTERNAL_PROPERTY_UID__ = "_.uid";  
	final static public String __INTERNAL_PROPERTY_NAME__ = "_.name";  
	final static public String __INTERNAL_PROPERTY_ORIGINAL__ = "_.original";  
	final static public String __INTERNAL_PROPERTY_SCHEMA__ = "_.schema";  
	final static public String __INTERNAL_PROPERTY_SPLIT__ = "_.split";  
	final static public String __INTERNAL_PROPERTY_TABULAR__ = "_.tabular";  
	final static public String __INTERNAL_PROPERTY_DATATYPE__ = "_.datatype";  
	final static public String __INTERNAL_PROPERTY_GRAPH__ = "_.graph";  
	final static public String __INTERNAL_PROPERTY_SPACE__ = "_.space";  
	final static public String __INTERNAL_PROPERTY_VALUES__ = "_.values";  
	final static public String __INTERNAL_PROPERTY_KER__ = "_.ker";  
	final static public String __INTERNAL_PROPERTY_TYPE__ = "_.type";  
	final static public String __INTERNAL_PROPERTY_NEURAL__ = "_.neural";  
	final static public String __INTERNAL_PROPERTY_OPER__ = "_.oper";  
	final static public String __INTERNAL_PROPERTY_NICK__ = "_.nick";  
	final static public String __INTERNAL_PROPERTY_CLUSTER__ = "_.cluster";  
	final static public String __INTERNAL_PROPERTY_DIMENSIONALITY__ = "_.dimensionality";  

	final static public String __INTERNAL_PROPERTY_HEAD__ = "_.head";  
	final static public String __INTERNAL_PROPERTY_TAIL__ = "_.tail";  
	final static public String __INTERNAL_PROPERTY_RELA__ = "_.rela";  
	final static public String __INTERNAL_PROPERTY_POLA__ = "_.pola";  
		
	protected DCommentSet			 	_comments;
	protected DVariableSet			 	_properties;
	protected Properties 				_ranges;
	protected AMetaUIDObjectSet	_set = null;
	
	public AMetaUIDObject () throws KException {
		this._comments = new DCommentSet();
		this._properties = new DVariableSet();
		this._ranges = new Properties();
		this._properties.declare(__INTERNAL_PROPERTY_UID__, "Uid");
	}

	public void setUID(String uid) throws KException { 
		this.setProperty(__INTERNAL_PROPERTY_UID__,uid); 
	}

	public String getUID() throws KException { return this.getProperty(__INTERNAL_PROPERTY_UID__); }
	public String getName() throws KException { return ""; }


	public void inSet (AMetaUIDObjectSet set) throws KException {
		this._set = set;
		for (String property : this.properties().keySet()) {
			this._set.updateSizes(property, this.getProperty(property));
		}
	}
	
	public DVariableSet properties() throws KException {
		if (this._properties == null) throw new KException("Object has no property set");
		return this._properties;
	}

	public DVariableSet comments() throws KException {
		if (this._comments == null) throw new KException("Object has no comment set");
		return this._comments;
	}

	public void declareProperty (String property, String datatype) throws KException {
		declareProperty(property, datatype, null, null);
	}
	public void declareProperty (String property, String datatype, String[] range) throws KException {
		// if (range == null || range.length < 1) throw new HXException("Null or empty range set definition for property '"+property+"'");
		declareProperty(property, datatype, range, null);
	}
	public void declareProperty (String property, String datatype, String[] range, String value) throws KException {
		// if (this._properties == null) throw new HXException("Object has no property set initialized");
		if (this.hasProperty(property)) throw new KException("Property '"+property+"' already declared");
		this._properties.declare(property, datatype);
		if ( range != null ) this._ranges.put(property, range);
		if ( value != null ) this.setProperty(property, value);
	}
	
	public void setProperty (String property, String value) throws KException {
		// if (this._properties == null) throw new HXException("Object has no property set initialized");
		if (!this.hasProperty(property)) throw new KException("Invalid property '"+property+"'");
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
		this._properties.setValue(property, value);
		if (_set != null) this._set.updateSizes(property, value);
	}

	public String getProperty (String property) throws KException {
		// if (this._properties == null) throw new HXException("Object has no property set");
		if (!this.hasProperty(property)) throw new KException("Invalid property '"+property+"'");
		return this._properties.getValue(property);
	}

	public String getComment (String comment) throws KException {
		if (!this.hasComment(comment)) throw new KException("Invalid comment '"+comment+"'");
		return this._comments.getValue(comment);
	}

	public boolean hasProperty (String property) {
		return this._properties.exits(property);
	}

	public boolean hasComment (String comment) {
		return this._comments.exits(comment);
	}

	public void hextract (BufferedWriter bf) throws KException {
		KRAL.error("hextract not implemented for his object");
	}
	public void hextract (String fileName) throws KException { 
        try {
        	FileWriter fw = new FileWriter(KRAL.getFileFullPath(fileName));
            BufferedWriter bf = new BufferedWriter(fw);
            this.hextract(bf);
            bf.close();
    		fw.close();
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
