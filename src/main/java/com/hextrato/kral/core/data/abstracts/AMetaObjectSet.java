package com.hextrato.kral.core.data.abstracts;

// import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hextrato.kral.core.util.exception.KException;

public abstract class AMetaObjectSet<Meta> {

	private String _metaType = "Something";

	protected void setMetaType(String metaType) { this._metaType = metaType; }
	
	private Map<String,Meta> _metaSet = new LinkedHashMap<String,Meta>(); // HashMap,TreeMap 

	public Map<String,Meta> theList () { return _metaSet; }

	/*
	private boolean _hasError = false;
	private String  _message = "";
	
	private void setError (String error) throws HXException {
		_hasError = true;
		_message = error;
		throw new HXException(_message);
	}
	private void setMessage (String message) {
		_hasError = false;
		_message = message;
	}
	private void clearMessage () {
		_hasError = false;
		_message = "";
	}

	public boolean hasError () { return _hasError; }
	public String getMessage () { return _message; }
	*/
	
	public void create (String name, Meta metaObject) throws KException { // throws Exception {
		// clearMessage ();
		if (name.trim().equals("")) {
			throw new KException("Invalid NULL "+_metaType+" name");
		} else
		if (_metaSet.containsKey(name)) {
			throw new KException(""+_metaType+" '"+name+"' already exists");
		} else {
			_metaSet.put( name, metaObject ); 
			setCurrent(name);
			// setMessage(""+_metaType+" '"+name+"' created");
		}
	}

	public void itDoesNotExist(String name) throws KException {
		throw new KException(""+_metaType+" '"+name+"' does NOT exists");
	}
	
	public void delete (String name) throws KException {
		//clearMessage ();
		if (_metaSet.containsKey(name)) {
			_metaSet.remove(name);
			if (name.equals(getCurrent())) setCurrent("");
			if (name.equals(getCurrent())) setCurrent("");
			// setMessage(""+_metaType+" '"+name+"' deleted");
		} else {
			itDoesNotExist(name);
		}
	}

	public boolean exists (String name) { // throws Exception {
		return _metaSet.containsKey(name);
	}

	private String _currentMetaName = "";
	public boolean setCurrent(String name) throws KException{
		// clearMessage ();
		if (name.equals("")) {
			_currentMetaName = name;
		} else 
		if (_metaSet.containsKey(name)) {
			_currentMetaName = name;
			// InitializationParameterSet.set("[meta."+_metaType+".current]", _currentMetaName);
			// setMessage(""+_metaType+" '"+name+"' selected");
		} else { 
			_currentMetaName = "";
			itDoesNotExist(name);
		}
		// InitializationParameterSet.set("[meta."+_metaType+".current]".toLowerCase(), _currentMetaName);
		return (!_currentMetaName.equals(""));
	}
	
	public String getCurrent(){
		return _currentMetaName;
	}
}
