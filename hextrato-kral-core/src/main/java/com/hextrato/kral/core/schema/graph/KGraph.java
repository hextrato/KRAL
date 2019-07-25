package com.hextrato.kral.core.schema.graph;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class KGraph extends AMetaNamedObject {
	
	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public KGraph (KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		// this._name = name;
		this._schema = schema;
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", schema.getName());
		this.properties().declare("typed", "Boolean");
		this.properties().set("typed", "true");
		this.properties().declare("autocreate", "Boolean");
		this.properties().set("autocreate", "true");
		this.properties().declare("hypergraph", "Boolean");
		this.properties().set("hypergraph", "false");
	}
	
	public boolean isTyped() throws KException { return this.properties().get("typed").equals("true"); }
	public boolean isAutocreate() throws KException { return this.properties().get("autocreate").equals("true"); }
	public boolean isHypergraph() throws KException { return this.properties().get("hypergraph").equals("true"); }
	
	public void setTyped(boolean flag) throws KException { 
		int qtGeneric = 0;
		if (this.types().theList().containsKey("*")) qtGeneric = 1;
		if (!flag && this.types().theList().size() > qtGeneric) throw new KException("Remove types before setting graph as untyped");
		this.properties().set("typed",flag?"true":"false"); 
		if (!flag && qtGeneric==0) this.types().create("*");
	}
	public void setAutocreate(boolean flag) throws KException { this.properties().set("autocreate",flag?"true":"false"); }
	public void setHypergraph(boolean flag) throws KException { this.properties().set("hypergraph",flag?"true":"false"); }

	//
	// TripleSet Graphs 
	//
	
	private KTypeSet _typeSet = new KTypeSet(this);
	public KTypeSet types() { return _typeSet; }

	private KEntitySet _entitySet = new KEntitySet(this);
	public KEntitySet entities() { return _entitySet; }

	private KRelationSet _relationSet = new KRelationSet(this);
	public KRelationSet relations() { return _relationSet; }
	
	private KTripleSet _tripleSet = new KTripleSet(this);
	public KTripleSet triples() { return _tripleSet; }
	
	//
	// Hyper Graphs 
	//

	/*
	private HextraHyperSet _hyperSet = new HextraHyperSet(this);
	public HextraHyperSet hypers() { return _hyperSet; }

	private HextraRoleSet _roleSet = new HextraRoleSet(this);
	public HextraRoleSet roles() { return _roleSet; }

	private HextraTupleSet _tupleSet = new HextraTupleSet(this);
	public HextraTupleSet tuples() { return _tupleSet; }
	*/
	
	//
	// EXPORT
	//
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

}
