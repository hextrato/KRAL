package com.hextrato.kral.console.parser;

import java.util.Properties;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.core.util.exception.KException;

public class KCMetadata {

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =  
	// MAIN
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	private String[] _tokens;

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public KCMetadata(String cmdlin) throws KException {
		String stmt;
		String opts;
		if (cmdlin.lastIndexOf('?') > 0) {
			stmt = cmdlin.substring(0,cmdlin.lastIndexOf('?')).trim();
			opts = cmdlin.substring(cmdlin.lastIndexOf('?')+1).trim();
		} else { 
			stmt = cmdlin.trim();
			opts = "";
		}

		// bloks
		if (stmt.indexOf('{') > 0 && stmt.lastIndexOf('}') > stmt.indexOf('{')) {
			_blok = stmt.substring(stmt.indexOf('{')+1,stmt.lastIndexOf('}')).trim();
			stmt = stmt.substring(0,cmdlin.indexOf('{')).trim() + " " + stmt.substring(stmt.lastIndexOf('}')+1).trim();
		}

		// variable replace on stmt
		for (String var : KConsole.vars().keySet()) {
			stmt = stmt.replace("%"+var+"%", KConsole.vars().get(var));
		}
		for (String last : KConsole.last().keySet()) {
			stmt = stmt.replace("%"+last+"%", KConsole.last().get(last));
		}

		// tokens
		_tokens = stmt.split(" ");
		
		// vars
		String[] vars = opts.split("&");
		for (String var : vars) {
			String param;
			String value;
			if (var.indexOf('=') > 0) {
				param = var.substring(0,var.indexOf('='));
				value = var.substring(var.indexOf('=')+1);
			} else {
				param = var;
				value = "";
			}
			
			// variable replace (param)
			if (param.startsWith("%") && param.endsWith("%")) {
				String variable = param.substring(1, param.length()-1);
				if (KConsole.vars().exits(variable) && !KConsole.vars().get(variable).equals(""))
					param = KConsole.vars().get(variable);
			}
			// variable replace (value)
			if (value.startsWith("%") && value.endsWith("%")) {
				String variable = value.substring(1, value.length()-1);
				if (KConsole.vars().exits(variable) && !KConsole.vars().get(variable).equals(""))
					value = KConsole.vars().get(variable);
			}
			
			this.setParameter(param,value);
		}
		
	}
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =  
	// ?
	// UNDER REVIEW 
	// ?
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	//
	// PARSED
	//
	private String _parsed = "";
	public void addParsed(String token) {
		//if (this._parsed.length() > 0) this._parsed = this._parsed+".";
		//this._parsed = this._parsed+"."+token;
		if (!this._parsed.isEmpty()) this._parsed = this._parsed + ".";
		this._parsed = this._parsed + token.toLowerCase();
		/*
		if (token.endsWith(".")) {
			this._parsed = this._parsed + token;
		} else {
			this._parsed = this._parsed + token.substring(0,1).toUpperCase() + token.substring(1).toLowerCase();
		}
		*/
	}
	public void setParsedRoot() {
		this._parsed = "";
	}
	/*
	public void setParsed(String token) {
		// this._parsed = "."+token.substring(0,1).toUpperCase() + token.substring(1).toLowerCase();
		this._parsed = token.substring(0,1).toUpperCase() + token.substring(1).toLowerCase();
	}
	*/
	public String getParsed() {
		return this._parsed;
	}
	
	public String find (String what) {
		if (!getVar(what).equals(""))
			return getVar(what);
		else {
			if (!getParameter(what).equals("") && !this.hasVar(what) ) {
				setVar(what, getParameter(what));
				removeParameter(what);
				return getVar(what);
			}
		}
		return "";
	}
	
	private Properties _vars = new Properties();		// internal
	private Properties _parameters = new Properties();	// from cmdlin
	private String _context = "";


	public String getContext () {
		return this._context;
	}
	public void setContext (String context) {
		this._context = context;
		if (!this.hasVar(context)) {
			this.setVar(context, this.getParameter(context));
			this.removeParameter(context);
		}
		
	}
	public void setVar (String var, String value) {
		String key = var.toUpperCase();
		_vars.setProperty(key, value);
	}
	public String getVar (String var) {
		String key = var.toUpperCase();
		if (_vars.containsKey(key))
			return _vars.getProperty(key);
		else
			return "";
	}
	public boolean hasVar (String var) {
		String key = var.toUpperCase();
		return _vars.containsKey(key);
	}

	public boolean hasParameter (String param) {
		String key = param.toUpperCase();
		return _parameters.containsKey(key);
	}
	public void removeParameter (String param) {
		String key = param.toUpperCase();
		if (_parameters.containsKey(key))
			_parameters.remove(key);
	}
	public int countParameters () {
		return _parameters.size();
	}
	private void setParameter (String param, String value) {
		String key = param.toUpperCase();
		_parameters.setProperty(key, value);
	}
	public String getParameter (String param) {
		String key = param.toUpperCase();
		if (_parameters.containsKey(key))
			return _parameters.getProperty(key);
		else
			return "";
	}

	private int _currentTokenIndex = -1;
	public String firstToken() {
		if (_tokens.length > 0) return _tokens[0]; else return "";
	}
	public String prevToken() {
		this._currentTokenIndex--;
		return _tokens[this._currentTokenIndex];
	}
	public String nextToken() {
		while (this._currentTokenIndex < _tokens.length-1) {
			this._currentTokenIndex++;
			if (!_tokens[this._currentTokenIndex].trim().equals("")) 
				return _tokens[this._currentTokenIndex];
		}
		return null;
	}
	public boolean lastToken() {
		return !(this._currentTokenIndex < _tokens.length-1);
	}

	private String _blok = "";
	public String getBlok() { return _blok; }
	

}
