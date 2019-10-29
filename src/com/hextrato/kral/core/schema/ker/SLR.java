package com.hextrato.kral.core.schema.ker;

import com.hextrato.kral.core.data.abstracts.AMetaObjectSet;
import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.util.exception.KException;

public class SLR extends AMetaObjectSet<DMatrix> {

	//
	// SLR = Symbolic Latent Representation (combines Vectors and Matrices to represent each ENTITY / RELATION)
	//
	
	public SLR(KEmbed embedding) throws KException {
		if (embedding == null) throw new KException("Invalid null embedder");
		this._embedding = embedding;
		this.setMetaType("DSR");
	}
	
	private KEmbed _embedding = null;
	public KEmbed getEmbedding() { return this._embedding; }

	public void create (String type, int rows, int cols) throws KException {
		this.create(type, new DMatrix(rows,cols) );	
	}

	public DMatrix getSLR(String type) {
		if (this.theList().containsKey(type))
			return this.theList().get(type);
		else
			return null;
	}
}
