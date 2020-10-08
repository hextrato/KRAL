package com.hextrato.kral.core.schema.tabular;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hextrato.kral.core.data.abstracts.ADataType;
import com.hextrato.kral.core.data.abstracts.AMetaUIDObject;
import com.hextrato.kral.core.data.struct.DVariable;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.util.exception.KException;

public class KRecord extends AMetaUIDObject {
		
	private KSplit _split = null;
	public KSplit getSplit() { return this._split; }

	private KTabular _tabular = null;
	public KTabular getTabular() { return this._tabular; }

	public KRecord (KTabular tabular) throws KException {
		if (tabular == null) throw new KException("Invalid null tabular");
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, tabular.getSchema().getName());
		this._tabular = tabular;
		this.properties().declare(__INTERNAL_PROPERTY_TABULAR__, "String");
		this.properties().set(__INTERNAL_PROPERTY_TABULAR__, tabular.getName());
		this._split = tabular.getSchema().splits().getSplit();
		if (this._split == null) throw new KException("Invalid split");
		this.properties().declare(__INTERNAL_PROPERTY_SPLIT__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SPLIT__, this._split.getName());
	}
	
	
	private Map<String,String> _attributeValues = new HashMap<String,String>();
	public Map<String,String> values() { return _attributeValues; }

	public void unsetAttributeValue (String attribute) throws KException {
		if (_tabular.attributes().exists(attribute))
			_attributeValues.remove(attribute);
		else
			throw new KException("Invalid attribute '"+attribute+"' in tabular "+_tabular.getName());
	}
	public void setAttributeValue (String attribute, String value) throws KException {
		if (_tabular.attributes().exists(attribute)) {
			ADataType<?> _datatype = DVariable.getDatatype(_tabular.attributes().get(attribute).getProperty(__INTERNAL_PROPERTY_DATATYPE__));
			String parsedValue = _datatype.valueOf(value).toString();
			if (_attributeValues.containsKey(attribute))
				_attributeValues.replace(attribute, parsedValue);
			else
				_attributeValues.put(attribute, parsedValue);
			this._set.updateSizes(attribute, parsedValue);
		} else {
			throw new KException("Invalid attribute '"+attribute+"' in tabular "+_tabular.getName());
		}
	}

	public String getAttributeValue (String attribute) throws KException {
		if (_tabular.attributes().exists(attribute)) {
			if (_attributeValues.containsKey(attribute))
				return _attributeValues.get(attribute);
			else
				return "";
		} else {
			throw new KException("Invalid attribute '"+attribute+"' in tabular "+_tabular.getName());
		}
	}
	
	public boolean exitsAttributeValue (String attribute) throws KException {
		if (_tabular.attributes().exists(attribute)) 
			return _attributeValues.containsKey(attribute);
		else
			throw new KException("Invalid attribute '"+attribute+"' in tabular "+_tabular.getName());
	}
	
	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("record %s create", this.getUID() ) );
			bf.newLine();
			for (String attrName : _attributeValues.keySet()) {
				String attrValue = _attributeValues.get(attrName);
				bf.write( String.format("record %s attribute %s set %s", this.getUID(),attrName,attrValue ) );
				bf.newLine();
			}
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
