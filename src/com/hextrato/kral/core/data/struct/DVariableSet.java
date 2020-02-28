package com.hextrato.kral.core.data.struct;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.util.exception.KException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DVariableSet {

	//
	// Metadata
	//

	protected String _metadata = "variable";

	public void setMetadata (String metadata) { _metadata = metadata; }
	
	//
	// Variable Set
	//
	
	protected Map<String,DVariable> _variableSet = new HashMap<String,DVariable>();
	
	public Set<String> keySet() {
		return _variableSet.keySet();
	}
	
	public DVariable declare (String variable, String datatype) throws KException {
		if (this._variableSet.containsKey(variable)) {
			throw new KException(_metadata+" "+"'"+variable+"'"+" "+"already declared");
		} else {
			DVariable v = new DVariable(datatype);
			this._variableSet.put(variable, v);
			return v;
		}
	}

	public void set(String variable, String value) throws KException { this.setValue(variable, value); }
	
	public void setValue (String variable, String value) throws KException {
		if (this._variableSet.containsKey(variable)) {
			this._variableSet.get(variable).setValue(value);
		} else {
			KRAL.error(_metadata+" "+"'"+variable+"'"+" "+"not declared");
		}
	}

	public DVariable getVariable(String variable) throws KException { return _variableSet.get(variable); }
	public String get(String variable) throws KException { return this.getValue(variable); }

	public String getValue (String variable) throws KException {
		if (this._variableSet.containsKey(variable))
			return this._variableSet.get(variable).getValue();
		else
			KRAL.error(_metadata+" "+"'"+variable+"'"+" "+"not declared");
		return "";
	}

	public String getDatatype (String variable) throws KException {
		if (this._variableSet.containsKey(variable))
			return this._variableSet.get(variable).getDatatype();
		else
			KRAL.error(_metadata+" "+"'"+variable+"'"+" "+"not declared");
		return "";
	}

	public boolean exits (String variable) {
		return this._variableSet.containsKey(variable);
	}

	public void show(String variable) throws KException {
		KRAL.print(this.getDatatype(variable)+" "+variable+" = "+getValue(variable));
	}
	
}
