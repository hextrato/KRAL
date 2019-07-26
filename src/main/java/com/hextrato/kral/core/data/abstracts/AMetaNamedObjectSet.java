package com.hextrato.kral.core.data.abstracts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hextrato.kral.core.data.type.TUid;
import com.hextrato.kral.core.util.exception.KException;

public abstract class AMetaNamedObjectSet extends AMetaUIDObjectSet {

	//private String 				_metaType = "Something";
	//protected Properties 		_sizes = new Properties();

	//protected void setMetaType(String metaType) { this._metaType = metaType; }
	
	// private Map<String,AbstractMetaNamedObject> _metaSet = new LinkedHashMap<String,AbstractMetaNamedObject>(); // HashMap,TreeMap 
	private Map<String,String> _nameSet = new LinkedHashMap<String,String>(); // HashMap,TreeMap 
	
	// public Map<String,AbstractMetaNamedObject> theList () { return _metaSet; }
	public Map<String,String> theNames () { return _nameSet; }
	
	public void create (String name, AMetaNamedObject metaObject) throws KException { // throws Exception {
		create (TUid.random(), name, metaObject);
		
	}
	public void create (String uid, String name, AMetaNamedObject metaObject) throws KException { // throws Exception {
		if (name.trim().equals("")) {
			throw new KException("Invalid NULL "+_metaType+" name");
		} 
		if (_nameSet.containsKey(uid) || _uidsSet.containsKey(uid) || _nameSet.containsKey(name) || _uidsSet.containsKey(name)) {
			throw new KException(""+_metaType+" uid '"+uid+"' or name '"+name+"' already exists");
		}
		super.create(uid,metaObject);
		metaObject.inSet(this);
		metaObject.setProperty("_uid_", uid);
		if (metaObject.getProperty("_name_").equals("")) {
			metaObject.setProperty("_name_", name);
			// _metaSet.put( name, metaObject );
		} else {
			name = metaObject.getProperty("_name_");
		}
		_nameSet.put( name, uid );
		setCurrent(name);
	}

	public void itDoesNotExist(String uid) throws KException {
		throw new KException(""+_metaType+" uid or name '"+uid+"' does NOT exists");
	}
	public void uidDoesNotExist(String uid) throws KException {
		throw new KException(""+_metaType+" uid '"+uid+"' does NOT exists");
	}
	public void nameDoesNotExist(String name) throws KException {
		throw new KException(""+_metaType+" name '"+name+"' does NOT exists");
	}
	
	public void delete (String uid_OR_name) throws KException {
		if (_nameSet.containsKey(uid_OR_name)) {
			String uid = _nameSet.get(uid_OR_name);
			_uidsSet.remove(uid);
			_nameSet.remove(uid_OR_name);
		} else {
			if (_uidsSet.containsKey(uid_OR_name)) {
				AMetaNamedObject _object = (AMetaNamedObject)_uidsSet.get(uid_OR_name);
				String uid = _object.getProperty("_uid_");
				String name = _object.getProperty("_name_");
				_uidsSet.remove(uid);
				_nameSet.remove(name);
			} else {
				itDoesNotExist(uid_OR_name);
			}
		}
		if (!_nameSet.containsKey(_currentMeta)) _currentMeta = "";
	}

	public boolean hasUID (String uid) { 
		return _uidsSet.containsKey(uid);
	}
	public boolean hasName (String name) { 
		return _nameSet.containsKey(name);
	}
	public boolean exists (String uid_OR_name) { 
		return ( _uidsSet.containsKey(uid_OR_name) || _nameSet.containsKey(uid_OR_name) );
	}

	private String _currentMeta = "";

	public boolean setCurrent(String uid_OR_name) throws KException {
		if (uid_OR_name.equals("")) {
			_currentMeta = uid_OR_name;
		} else 
		if (_nameSet.containsKey(uid_OR_name)) {
			_currentMeta = uid_OR_name;
		} else {
			if (_uidsSet.containsKey(uid_OR_name)) {
				_currentMeta = _uidsSet.get(uid_OR_name).getProperty("_name_");
			} else { 
				_currentMeta = "";
				itDoesNotExist(uid_OR_name);
			}
		}
		return (!_currentMeta.equals(""));
	}
	
	public String getCurrent() {
		return _currentMeta;
	}
	public String getCurrentName() throws KException {
		if (_nameSet.containsKey(_currentMeta))
			return _currentMeta;
		else
			return "";
		//return _nameSet.getOrDefault(_currentMeta,"");
	}
	
	public AMetaNamedObject get(String uid_OR_name) {
		if (uid_OR_name.equals("")) uid_OR_name = this.getCurrent();
		if (this.theList().containsKey(uid_OR_name))
			return (AMetaNamedObject)( this.theList().get(uid_OR_name) );
		else
			if (this.theNames().containsKey(uid_OR_name))
				return (AMetaNamedObject)( this.theList().get( this.theNames().get(uid_OR_name)) );
			else
				return null;
	}

	/*
	protected void updateSizes(String property, String value) {
		if (this._sizes.containsKey(property)) {
			this._sizes.replace(property, (int) Math.max( (int)this._sizes.get(property), Math.max( property.length(), value.length())) );
		} else {
			this._sizes.put(property, (int) Math.max( property.length(), value.length()) );
		}
	}
	public int getPropertySize(String property) {
		return (int)this._sizes.getOrDefault(property, property.length());
	}
	*/

}
