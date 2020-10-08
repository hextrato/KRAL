package com.hextrato.kral.core.schema;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.graph.KGraphSet;
import com.hextrato.kral.core.schema.hyper.KSpaceSet;
import com.hextrato.kral.core.schema.ker.KERSet;
import com.hextrato.kral.core.schema.neural.KNeuralSet;
import com.hextrato.kral.core.schema.tabular.KTabularSet;
import com.hextrato.kral.core.util.exception.KException;

public class KSchema extends AMetaNamedObject {

	public KSchema() throws KException {
		super();
		// TODO Auto-generated constructor stub
	}

	private KSplitSet _splitSet = new KSplitSet(this);
	public KSplitSet splits() { return _splitSet; }

	private KTabularSet _tabularSet = new KTabularSet(this);
	public KTabularSet tabulars() { return _tabularSet; }
	
	private KGraphSet _graphSet = new KGraphSet(this);
	public KGraphSet graphs() { return _graphSet; }

	private KSpaceSet _spaceSet = new KSpaceSet(this);
	public KSpaceSet hyperspace() { return _spaceSet; }
	
	private KNeuralSet _neuralSet = new KNeuralSet(this);
	public KNeuralSet neuronal() { return _neuralSet; }

	private KERSet _kerSet = new KERSet(this);
	public KERSet kers() { return _kerSet; }

	private String _currentSplit = "";
	public void setCurrentSplit(String split) throws KException {
		if (_splitSet.theList().containsKey(split))
			this._currentSplit = split;
		else
			throw new KException("Invalid split '"+split+"'");
	}
	public String getCurrentSplit() {
		return this._currentSplit;
	}
	
	// private HextraOntologySet _ontologySet = new HextraOntologySet(this);
	// public HextraOntologySet ontologies() { return _ontologySet; }

	//
	// EXPORT
	// 
	
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("schema %s create", this.getName()) );
			bf.newLine();
			this.splits().hextract(bf);			// OK
			this.tabulars().hextract(bf);		// OK
			this.graphs().hextract(bf);			// ?
			this.hyperspace().hextract(bf);		// ?
			this.neuronal().hextract(bf);		// ?
			this.kers().hextract(bf);		// ?
			// this.ontologies().hextract(bf);		// ?
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
}
