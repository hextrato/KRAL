package com.hextrato.kral.core.data.struct;

import com.hextrato.kral.core.util.exception.KException;

public class DCommentSet extends DVariableSet {

	//
	// Metadata
	//

	public DCommentSet () {
		_metadata = "comment";
	}
	
	public void setValue (String variable, String value) throws KException {
		if (!this._variableSet.containsKey(variable)) {
			this.declare(variable, "String");
		}
		this._variableSet.get(variable).setValue(value);
	}

	public DVariable getComment(String variable) throws KException { return this.getVariable(variable); }

}
