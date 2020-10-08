package com.hextrato.kral.core.data.abstracts;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.hextrato.kral.core.util.exception.KException;

public abstract class AMetaUIDObjectSet {

	protected String 				_metaType = "Something";
	protected Properties 		_sizes = new Properties();

	protected void setMetaType(String metaType) { this._metaType = metaType; }
	
	protected Map<String,AMetaUIDObject> _uidsSet = new LinkedHashMap<String,AMetaUIDObject>(); // HashMap,TreeMap 
	
	public Map<String,AMetaUIDObject> theList () { return _uidsSet; }
	
	public void create (String uid, AMetaUIDObject metaObject) throws KException { // throws Exception {
		if (uid.trim().equals("")) {
			throw new KException("Invalid NULL "+_metaType+" uid");
		} 
		if (_uidsSet.containsKey(uid)) {
			throw new KException(""+_metaType+" uid '"+uid+"' already exists");
		}
		metaObject.inSet(this);
		metaObject.setUID(uid);
		_uidsSet.put( uid, metaObject );
		// _uidsSet.put( uid, name);
		setCurrent(uid);
	}

	public void uidDoesNotExist(String uid) throws KException {
		throw new KException(""+_metaType+" uid '"+uid+"' does NOT exists");
	}
	public void itDoesNotExist(String uid) throws KException {
		throw new KException(""+_metaType+" uid '"+uid+"' does NOT exists");
	}
	
	public void delete (String uid) throws KException {
		if (_uidsSet.containsKey(uid)) {
			AMetaUIDObject _object = (AMetaUIDObject)_uidsSet.get(uid);
			String _uid_ = _object.getUID();
			_uidsSet.remove(_uid_);
			//_uidsSet.remove(uid);
		} else {
			itDoesNotExist(uid);
		}
		if (!_uidsSet.containsKey(_currentMeta)) _currentMeta = "";
	}

	public boolean hasUID (String uid) { 
		return _uidsSet.containsKey(uid);
	}
	public boolean exists (String uid) { 
		return ( _uidsSet.containsKey(uid) );
	}

	private String _currentMeta = "";

	public boolean setCurrent(String uid) throws KException {
		if (uid.equals("")) {
			_currentMeta = uid;
		} else {
			if (_uidsSet.containsKey(uid)) {
				_currentMeta = _uidsSet.get(uid).getUID();
			} else { 
				_currentMeta = "";
				itDoesNotExist(uid);
			}
		}
		return (!_currentMeta.equals(""));
	}
	
	public String getCurrent() {
		return _currentMeta;
	}
	
	public AMetaUIDObject get(String uid) {
		if (uid.equals("")) uid = this.getCurrent();
		if (_uidsSet.containsKey(uid))
			return _uidsSet.get(uid);
		else
			return null;
	}

	public void updateSizes(String property, String value) {
		if (this._sizes.containsKey(property)) {
			this._sizes.replace(property, (int) Math.max( (int)this._sizes.get(property), Math.max( property.length(), value.length())) );
		} else {
			this._sizes.put(property, (int) Math.max( property.length(), value.length()) );
		}
	}
	public int getPropertySize(String property) {
		return (int)this._sizes.getOrDefault(property, property.length());
	}
	

}
