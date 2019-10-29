package com.hextrato.kral.core.schema.nlp;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class KCorpus extends AMetaNamedObject {

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public KCorpus (KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		this._schema = schema;
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", schema.getName());
	}
	
	//
	// Documents 
	//
	
	private KDocumentSet _documentSet = new KDocumentSet(this);
	public KDocumentSet documents() { return _documentSet; }

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("graph %s create", this.getName()) );
			bf.newLine();
			for (String property : this.properties().keySet()) {
				bf.write( String.format("graph %s property %s %s", this.getName(), property, this.properties().get(property)) );
				bf.newLine();
			}
			/*
			bf.write( String.format("graph %s property typed %s", this.getName(), this.properties().get("typed")) );
			bf.newLine();
			bf.write( String.format("graph %s property autocreate %s", this.getName(), this.properties().get("autocreate")) );
			bf.newLine();
			bf.write( String.format("graph %s property hypergraph %s", this.getName(), this.properties().get("hypergraph")) );
			bf.newLine();
			*/
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

	//
	// load documents
	//
	public void loadDocument (File doc) throws KException {
		//KConsole.debug("reading "+doc.getName());
		String documentName = doc.getName();
		int version = 2;
		while (this.documents().theNames().containsKey(documentName)) {
			documentName = doc.getName()+"_"+version;
			version++;
		}
		this.documents().create(documentName);
		//
		// read content
		//
		String content = "";
		BufferedReader in;
		try {
			in = new BufferedReader( new InputStreamReader( new FileInputStream(doc), "UTF8" ) );
			String str;
			while ((str = in.readLine()) != null) {
			    content = content + str + "\n"; 
			}
			in.close();		
		} catch (UnsupportedEncodingException e1) {
			throw new KException("Error reading file "+doc.getName()+"; [UnsupportedEncoding]; "+e1.getMessage());
		} catch (FileNotFoundException e2) {
			throw new KException("Error reading file "+doc.getName()+"; [FileNotFound]; "+e2.getMessage());
		} catch (IOException e3) {
			throw new KException("Error reading file "+doc.getName()+"; [IO]; "+e3.getMessage());
		}
		this.documents().getDocument(documentName).setProperty("text", content);
	}
	
	public void loadFrom (File folder) throws KException {
		for (final File entry : folder.listFiles()) {
	        if (entry.isDirectory()) {
	            loadFrom(entry);
	        } else {
	            loadDocument(entry);
	        }
	    }		
		
	}
}
