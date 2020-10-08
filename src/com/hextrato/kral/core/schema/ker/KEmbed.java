package com.hextrato.kral.core.schema.ker;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DMatrix;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.schema.neural.layer.type.NLLinear;
import com.hextrato.kral.core.util.exception.KException;

public class KEmbed extends AMetaNamedObject {

	public final static String ENTITY = "ENTITY";
	public final static String RELATION = "RELATION";

	public final static String ENTITY_VECTOR_SLR = "evs";
	public final static String RELATION_VECTOR_SLR = "rvs";
	public final static String RELATION_MATRIX_SLR = "rpm";
	public final static String RELATION_VECTOR_SLR_INVERSE = "rvsi";
	public final static String RELATION_MATRIX_SLR_INVERSE = "rpmi";
	
	private KER _ker = null;
	public KER getKER() { return this._ker; }

	private SLR _slr = new SLR(this);
	public SLR representation() { return this._slr; }

	private String _type;
	public String getType() { return this._type; }
	
	NLLinear nnLayerDir = null; // directional relation
	NLLinear nnLayerInv = null; // inverse relation

	private DVector auxVec = null;
	
	public KEmbed (KER ker, String type, String constituentTypedName) throws KException {
		if (ker == null) throw new KException("Invalid null KER");
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, ker.getSchema().getName());
		this._ker = ker;
		this.properties().declare(__INTERNAL_PROPERTY_KER__, "String");
		this.properties().set(__INTERNAL_PROPERTY_KER__, ker.getName());
		if (type == null) throw new KException("Invalid null embed type");
		this._type = type.toUpperCase().trim();
		this.properties().declare(__INTERNAL_PROPERTY_TYPE__, "String");
		this.properties().set(__INTERNAL_PROPERTY_TYPE__, this.getType());

		this.properties().declare(__INTERNAL_PROPERTY_CLUSTER__, "String");
		this.properties().set(__INTERNAL_PROPERTY_CLUSTER__, "");

		// if (!this._type.equals(ENTITY) && !this._type.equals("RELATION")) throw new HXException("Invalid ker type ["+type+"]");
		switch (this._type) {
		case ENTITY:
			this.representation().create(ENTITY_VECTOR_SLR, 1, this.getKER().getDimensions() );
			this.representation().getSLR(ENTITY_VECTOR_SLR).randomize(-this.getKER().getRandomFactor(), +this.getKER().getRandomFactor());
			if (this._ker.getGraph().isTyped()) {
				String entityType = KEntity.extractTypeFrom(constituentTypedName);
				if (this._ker.getGraph().types().getType( entityType ).isContinuous()) {
					double entityValue = Double.valueOf(KEntity.extractNameFrom(constituentTypedName));
					// this.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0).copyValuesFrom( this._ker.getGraph().types().getType( entityType ).getContinuousVector(entityValue , this._ker.getDimensions() , this._ker.getRegularizationFactor() ) );
					this.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0).copyValuesFrom( this._ker.getGraph().types().getType( entityType ).getContinuousVector(entityValue , this._ker.getDimensions() ) );
					System.out.println(entityValue + " => " + this.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0).toString() + " in ["+this._ker.getGraph().types().getType( entityType ).getMin()+","+this._ker.getGraph().types().getType( entityType ).getMax()+"]");
				}
			}
			break;
		case RELATION:
			auxVec = new DVector(this._ker.getDimensions());
			// ...
			// Neural Layers
			nnLayerDir = new NLLinear();
			nnLayerDir.setInputSize(ker.getDimensions());
			nnLayerDir.setOutputSize(ker.getDimensions());
			nnLayerDir.setLearningRate(ker.getLearningRate());
			nnLayerInv = new NLLinear();
			nnLayerInv.setInputSize(ker.getDimensions());
			nnLayerInv.setOutputSize(ker.getDimensions());
			nnLayerInv.setLearningRate(ker.getLearningRate());
			// ...
			// config
			this.representation().create(RELATION_VECTOR_SLR, 1, this.getKER().getDimensions() );
			nnLayerDir.setBiases(this.representation().getSLR(RELATION_VECTOR_SLR).getRow(0));
			this.representation().getSLR(RELATION_VECTOR_SLR).randomizeAsNormal(-this.getKER().getRandomFactor(), +this.getKER().getRandomFactor());
			this.setInverseRelation(this.getKER().isInverseRelationActive()); 
			this.setProjectionMatrix(this.getKER().isProjectionMatrixActive()); 
			break;
		default: throw new KException("Invalid ker type ["+type+"]");
		}
	}
	
	private boolean _learnProjectionMatrix = false;
	private boolean _learnInverseRelation = false;

	private void validateInverseAndProjections() throws KException {
		if (this._learnProjectionMatrix) {
			if (!this.representation().theList().containsKey(RELATION_MATRIX_SLR) || this.representation().getSLR(RELATION_MATRIX_SLR) == null) {
				this.representation().create(RELATION_MATRIX_SLR, this.getKER().getDimensions(), this.getKER().getDimensions() );
				// this.representation().getSLR(RELATION_MATRIX_SLR).randomizeAsNormal(-this.getKER().getRandomFactor(), +this.getKER().getRandomFactor());
				this.representation().getSLR(RELATION_MATRIX_SLR).fillWithIdentity();
			}
			nnLayerDir.setWeights(this.representation().getSLR(RELATION_MATRIX_SLR));
		} else {
			if (this.representation().theList().containsKey(RELATION_MATRIX_SLR) && this.representation().getSLR(RELATION_MATRIX_SLR) != null) {
				this.representation().delete(RELATION_MATRIX_SLR);
				this.representation().create(RELATION_MATRIX_SLR, null );
			}
			nnLayerDir.setWeightsNull();
		}
		if (this._learnInverseRelation) {
			if (!this.representation().theList().containsKey(RELATION_VECTOR_SLR_INVERSE) || this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE) == null) {
				this.representation().create(RELATION_VECTOR_SLR_INVERSE, 1, this.getKER().getDimensions() );
				// this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).randomizeAsNormal(-this.getKER().getRandomFactor(), +this.getKER().getRandomFactor());
				this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).copyValuesFrom(this.representation().getSLR(RELATION_VECTOR_SLR));
				for (int i = 0; i < this.getKER().getDimensions(); i++)
					this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).setValue(i, -this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).getValue(i));
			}
			nnLayerInv.setBiases(this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).getRow(0));
		} else {
			if (this.representation().theList().containsKey(RELATION_VECTOR_SLR_INVERSE) && this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE) != null) {
				this.representation().delete(RELATION_VECTOR_SLR_INVERSE);
				this.representation().create(RELATION_VECTOR_SLR_INVERSE, null );
			}
			nnLayerInv.setBiasesNull();
		}
		if (this._learnProjectionMatrix && this._learnInverseRelation) {
			if (!this.representation().theList().containsKey(RELATION_MATRIX_SLR_INVERSE) || this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE) == null) {
				this.representation().create(RELATION_MATRIX_SLR_INVERSE, this.getKER().getDimensions(), this.getKER().getDimensions() );
				// this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE).randomizeAsNormal(-this.getKER().getRandomFactor(), +this.getKER().getRandomFactor());
				this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE).fillWithIdentity();
			}
			nnLayerInv.setWeights(this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE));
		} else {
			if (this.representation().theList().containsKey(RELATION_MATRIX_SLR_INVERSE) && this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE) != null) {
				this.representation().delete(RELATION_MATRIX_SLR_INVERSE);
				this.representation().create(RELATION_MATRIX_SLR_INVERSE, null );
			}
			nnLayerInv.setWeightsNull();
		}
		//nnLayerDir.setBiases( (this.representation().getSLR(RELATION_VECTOR_SLR)==null)?null:this.representation().getSLR(RELATION_VECTOR_SLR).getRow(0) );
		//nnLayerDir.setWeights( this.representation().getSLR(RELATION_MATRIX_SLR) );
		//nnLayerDir.setBiases( (this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE)==null)?null:this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).getRow(0) );
		//nnLayerDir.setWeights( this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE) );
	}
	public void setProjectionMatrix(boolean flag) throws KException {
		this._learnProjectionMatrix = flag;
		this.validateInverseAndProjections();
	}
	public void setInverseRelation(boolean flag) throws KException {
		this._learnInverseRelation = flag;
		this.validateInverseAndProjections();
	}
	public boolean isProjectionMatrixActive() { return this._learnProjectionMatrix; }
	public boolean isInverseRelationActive() { return this._learnInverseRelation; }
	
	public void feed(KEmbed head, KEmbed tail) throws KException {
		this.feed(head.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0), tail.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0) );
	}
	public void feed(DVector head, DVector tail) throws KException {
		if (!this.getType().equals(RELATION))  {
			throw new KException("Cannot feed an entity embed");
		} else {
			nnLayerDir.feed(head);
			if (this.getKER().isInverseRelationActive())
				nnLayerInv.feed(tail);
		}
	}

	private double _positiveMarginError = 0;
	private double _inverseMarginError = 0;
	public void setPositiveMarginError(double distance) { this._positiveMarginError = distance; }
	public void setInverseMarginError(double distance) { this._inverseMarginError = distance; }

	//
	// NEGATIVE (***UNDER REVIEW***)
	//
	public void learnNegativeRelation(KTriple triple) throws KException {
		this.learnNegativeRelation(triple,false);
	}
	public void learnNegativeRelation(KTriple triple, boolean corrupted) throws KException {
		if (!this.getType().equals(RELATION))  {
			throw new KException("Cannot learn negative relations based on an entity embed");
		} else {
			String headName = triple.getHead().getName();
			String tailName = triple.getTail().getName();
			
			KEmbed head = this.getKER().embeds().getEmbed(headName);
			KEmbed tail = this.getKER().embeds().getEmbed(tailName);

			DVector headVec = head.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);
			DVector tailVec = tail.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);

			this.feed(headVec, tailVec);

			double targetMargin;
			double __dirDistTailPOS = nnLayerDir.theOutputValues().distance(tailVec);
			double __invDistHeadPOS = (!this.getKER().isInverseRelationActive())?0:nnLayerInv.theOutputValues().distance(headVec);

			// ---
			// (relation and triple have same split AND not corrupted)
			// ---
			if ( !corrupted && triple.getSplit().getName().equals(triple.getRela().getSplit().getName()) ) {
				// ...
				// Directional Relation Learn Negative
				boolean hasToNormalize = false;
				targetMargin = this.getKER().getLearningMargin() + this._positiveMarginError;
				if ( __dirDistTailPOS < targetMargin && __dirDistTailPOS > 0) {
					auxVec.copyValuesFrom(nnLayerDir.theOutputValues());
					auxVec.moveAwayFrom(tailVec, 1, targetMargin);
					/*
					double increasePropDistance = (targetMargin - __dirDistTailPOS) / __dirDistTailPOS;
					HXVector negativeDirTarget = new HXVector(nnLayerDir.getOutputSize());
					for (int i=0; i < tailVec.length(); i++) {
						negativeDirTarget.setValue(i, nnLayerDir.theOutputValues().getValue(i) - (tailVec.getValue(i) - nnLayerDir.theOutputValues().getValue(i)) * (1+increasePropDistance) );
					}
					*/
					nnLayerDir.back(auxVec);
					hasToNormalize = true;
				}
				// ...
				// Inverse Relation Learn Negative
				if (this.getKER().isInverseRelationActive()) {
					targetMargin = this.getKER().getLearningMargin() + this._inverseMarginError;
					if ( __invDistHeadPOS < targetMargin && __invDistHeadPOS > 0) {
						auxVec.copyValuesFrom(nnLayerInv.theOutputValues());
						auxVec.moveAwayFrom(headVec, 1, targetMargin);
						/*
						double increasePropDistance = (targetMargin - __invDistHeadPOS) / __invDistHeadPOS;
						HXVector negativeDirTarget = new HXVector(nnLayerDir.getOutputSize());
						for (int i=0; i < tailVec.length(); i++) {
							negativeDirTarget.setValue(i, nnLayerInv.theOutputValues().getValue(i) - (headVec.getValue(i) - nnLayerInv.theOutputValues().getValue(i)) * (1+increasePropDistance));
						}
						*/
						nnLayerInv.back(auxVec);
						hasToNormalize = true;
					}
				}
				// KEEP TO THE END OF EACH CYCLE ? NO ?// 
				if ( hasToNormalize ) this.normalize();
			}

			// ...
			// (tail and triple have same split)  
			// ...
			if ( triple.getSplit().getName().equals(triple.getTail().getSplit().getName()) ) {
				targetMargin = this.getKER().getLearningMargin() + this._positiveMarginError;
				if (__dirDistTailPOS < targetMargin && __dirDistTailPOS > 0) {
					/*
					double increasePropDistance = (targetMargin - __dirDistTailPOS) / __dirDistTailPOS;
					for (int i=0; i < tailVec.length(); i++) {
						// ...
						// update tail embed for negative triple
						// tailVec.setValue(i, tailVec.getValue(i) + (tailVec.getValue(i) - nnLayerDir.theOutputValues().getValue(i)) * (1+increasePropDistance) * this.getKER().getLearningRate() );
						tailVec.setValue(i, tailVec.getValue(i) + (tailVec.getValue(i) + nnLayerDir.theOutputValues().getValue(i)) * (1+increasePropDistance) * this.getKER().getLearningRate() );
					}
					*/
					// DEGUB
					//System.out.println(String.format("tail %s)",tailVec));
					tailVec.moveAwayFrom(nnLayerDir.theOutputValues(), this._ker.getLearningRate(), targetMargin);
					//System.out.println(String.format("tail %s)",tailVec));
					// if (!corrupted) {
						// KEEP TO THE END OF EACH CYCLE ? NO ?// 
					    tail.normalize();
						//System.out.println(String.format("tail %s)",tailVec));
					//}
				}
			}
			// ...
			// (head and triple have same split in inverse relation mode)  
			// ...
			if (this.getKER().isInverseRelationActive()) {

				if (triple.getSplit().getName().equals(triple.getHead().getSplit().getName())) {
					targetMargin = this.getKER().getLearningMargin() + this._inverseMarginError;
					if (__invDistHeadPOS < targetMargin && __invDistHeadPOS > 0) {
						/*
						double increasePropDistance =  (targetMargin - __invDistHeadPOS) / __invDistHeadPOS;
						for (int i=0; i < headVec.length(); i++) {
							// ...
							// update head embed for negative triple in inverse relation
							headVec.setValue(i, headVec.getValue(i) + (headVec.getValue(i) + nnLayerInv.theOutputValues().getValue(i)) * (1+increasePropDistance) * this.getKER().getLearningRate() );
						}
						*/
						// DEGUB
						// System.out.println(String.format("head %s)",headVec));
						headVec.moveAwayFrom(nnLayerInv.theOutputValues(), this._ker.getLearningRate(), targetMargin);
						// System.out.println(String.format("head %s)",headVec));
						// if (!corrupted) {
 							// KEEP TO THE END OF EACH CYCLE ? NO ?// 
						    head.normalize();
							// System.out.println(String.format("head %s)",headVec));
						// }
					}
				}
			}
		}
	}
	
	//
	// POSITIVE
	//
	public void learnPositiveRelation(KTriple triple, KEmbed negHead, KEmbed negTail) throws KException {
		if (!this.getType().equals(RELATION))  {
			throw new KException("Cannot learn positive relations based on an entity embed");
		} else {
			String headName = triple.getHead().getName();
			String tailName = triple.getTail().getName();
			
			KEmbed head = this.getKER().embeds().getEmbed(headName);
			KEmbed tail = this.getKER().embeds().getEmbed(tailName);

			DVector headVec = head.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);
			DVector tailVec = tail.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);

			DVector negHeadVec = (negHead==null)?null:negHead.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);
			DVector negTailVec = (negTail==null)?null:negTail.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);

			/*
			double negHeadUsedCount = 1.0; 
			if (negHead != null)
				if (this._ker.getGraph().entities().getEntity(negHead.getName()) != null) 
					negHeadUsedCount = Math.max(1.0, this._ker.getGraph().entities().getEntity(negHead.getName()).usedAsHead());
				
			double negTailUsedCount = 1.0;
			if (negTail != null)
				if (this._ker.getGraph().entities().getEntity(negTail.getName()) != null) 
					negTailUsedCount = Math.max(1.0, this._ker.getGraph().entities().getEntity(negTail.getName()).usedAsTail());
			*/
			
			this.feed(headVec, tailVec);
			
			double __dirDistTailPOS = nnLayerDir.theOutputValues().distance(tailVec);
			double __dirDistTailNEG = (negTailVec==null)?-1:nnLayerDir.theOutputValues().distance(negTailVec);
			if (__dirDistTailNEG > __dirDistTailPOS + this.getKER().getLearningMargin() && !this._ker.isLearningCycleEnforced()) __dirDistTailNEG = -1;

			double __invDistHeadPOS = 0;
			double __invDistHeadNEG = -1;
			
			if (this.getKER().isInverseRelationActive()) {
				__invDistHeadPOS = nnLayerInv.theOutputValues().distance(headVec);
				__invDistHeadNEG = (negHeadVec==null)?-1:nnLayerInv.theOutputValues().distance(negHeadVec);
				if (__invDistHeadNEG > __invDistHeadPOS + this.getKER().getLearningMargin() && !this._ker.isLearningCycleEnforced()) __invDistHeadNEG = -1;
			}
			this.setPositiveMarginError(__dirDistTailPOS);
			this.setInverseMarginError(__invDistHeadPOS);
			
			//Hextrato.debug("__dirDistTailPOS = "+__dirDistTailPOS);
			//Hextrato.debug("__dirDistTailNEG = "+__dirDistTailNEG);
			//Hextrato.debug("__invDistHeadPOS = "+__invDistHeadPOS);
			//Hextrato.debug("__invDistHeadNEG = "+__invDistHeadNEG);
			
			/* DEPRECATED 
			// learn NEGATIVE triples
			if (__dirDistTailNEG > 0) {
				//System.out.println(String.format("negaTail (%s:%s , %s , %s:%s)",headType,headName,this.getName(),tailType,tailName));
				// HextraTriple negativeTriple = new HextraTriple(this.getKER().getGraph(), triple.getHead().getType()+":"+triple.getHead().getName(), triple.getRela().getName(), negTail.getName(), false);
				HextraTriple negativeTriple = new HextraTriple(this.getKER().getGraph(), triple.getHead().getName(), triple.getRela().getName(), negTail.getName(), false);
				this.learnNegativeRelation(negativeTriple,true);
				negTail.normalize();
			}
			if (__invDistHeadNEG > 0) {
				//System.out.println(String.format("negaHead (%s:%s , %s , %s:%s)",headType,headName,this.getName(),tailType,tailName));
				// HextraTriple negativeTriple = new HextraTriple(this.getKER().getGraph(), negHead.getName(), triple.getRela().getName(), triple.getTail().getType()+":"+triple.getTail().getName(), false);
				HextraTriple negativeTriple = new HextraTriple(this.getKER().getGraph(), negHead.getName(), triple.getRela().getName(), triple.getTail().getName(), false);
				this.learnNegativeRelation(negativeTriple,true);
				negHead.normalize();
			}
			*/ 
			
			// ---
			// (relation and triple have same split)
			// ---
			if ( triple.getSplit().getName().equals(triple.getRela().getSplit().getName()) ) {
				if (__dirDistTailNEG > 0) { 
					// (negTail and triple have same split)
					if ( triple.getSplit().getName().equals(this._ker.getGraph().entities().getEntity(negTail.getName()).getSplit().getName()) ) {
						negTailVec.moveAwayFrom(nnLayerDir.theOutputValues(), this._ker.getLearningRate(), __dirDistTailPOS + this.getKER().getLearningMargin() ); //  * (2/(1+Math.log(negTailUsedCount))) );
						// KEEP TO THE END OF EACH CYCLE ? NO ?// 
						negTail.normalize();
					}
					nnLayerDir.back(tailVec);
				}
				if (__invDistHeadNEG > 0) {
					// (negHead and triple have same split)
					if ( triple.getSplit().getName().equals(this._ker.getGraph().entities().getEntity(negHead.getName()).getSplit().getName()) ) {
						// LEARN NEG HEAD WITH LOWER LEARINING RATE 
						negHeadVec.moveAwayFrom(nnLayerInv.theOutputValues(), this._ker.getLearningRate(), __invDistHeadPOS + this.getKER().getLearningMargin() ); // * (1/(1+Math.log(negHeadUsedCount))) );
						// KEEP TO THE END OF EACH CYCLE ? NO ?// 
						negHead.normalize();
					}
					nnLayerInv.back(headVec);
				}
				if (__invDistHeadNEG > 0 || __dirDistTailNEG > 0)
					this.normalize(); // ??? (RELATION) KEEP TO THE END OF EACH CYCLE ??? // 
			} else {
				if (__dirDistTailNEG > 0) nnLayerDir.calcErrorByTarget(tailVec);
				if (__invDistHeadNEG > 0) nnLayerInv.calcErrorByTarget(headVec);
			}

			// --
			// (tail and triple have same split)
			// --
			if ( triple.getSplit().getName().equals(triple.getTail().getSplit().getName()) ) {
				/*
				if (__invDistHeadNEG > 0) {
					for (int i=0; i < tailVec.length(); i++) {
						auxVec.setValue(i, tailVec.getValue(i) + (headVec.getValue(i) - nnLayerInv.theOutputValues().getValue(i)) );
					}					
					tailVec.moveCloserTo(auxVec, this.getKER().getLearningRate());
				}
				*/
				if (__dirDistTailNEG > 0) { 
					tailVec.moveCloserTo(nnLayerDir.theOutputValues(), this.getKER().getLearningRate());
					// KEEP TO THE END OF EACH CYCLE ? NO ?// 
					tail.normalize();
				}
				// if (__invDistHeadNEG > 0 || __dirDistTailNEG > 0) 
				//if (__dirDistTailNEG > 0) 
			}

			// ---
			// (head and triple have same split)
			// ---
			if (triple.getSplit().getName().equals(triple.getHead().getSplit().getName())) {
				if (__dirDistTailNEG > 0) {
					for (int i=0; i < tailVec.length(); i++) {
						//auxVec.setValue(i, headVec.getValue(i) + (tailVec.getValue(i) - nnLayerDir.theOutputValues().getValue(i)) );
						auxVec.setValue(i, headVec.getValue(i) + nnLayerDir.theGradientIn().getValue(i));
					}					
					headVec.moveCloserTo(auxVec, this.getKER().getLearningRate());
					// KEEP TO THE END OF EACH CYCLE ? NO ?// 
					head.normalize();
				}
				/*
				if (__invDistHeadNEG > 0) { 
					headVec.moveCloserTo(nnLayerInv.theOutputValues(), this.getKER().getLearningRate());
				} 
				*/
				//if (__invDistHeadNEG > 0 || __dirDistTailNEG > 0) head.normalize();
				//	head.normalize();
			}
		}
	}
	
	private void normalizeEntityVector(DVector vector) {
		switch (this._ker.getRegularizationType()) {
		case "SPACE": 
			vector.normalizeByMaxMagnitude(this._ker.getRegularizationFactor(),_ker.getLatentConstraint()); 
			break;
		case "SURFACE": 
			vector.normalizeByFixedMagnitude(this._ker.getRegularizationFactor(),_ker.getLatentConstraint()); 
			break;
		case "RANGE": 
			if (vector.magnitude() < this._ker.getRegularizationFactor() * this._ker.getRegularizationMargin())
				vector.normalizeByFixedMagnitude(this._ker.getRegularizationFactor() * this._ker.getRegularizationMargin(),_ker.getLatentConstraint()); 
			else
				vector.normalizeByMaxMagnitude(this._ker.getRegularizationFactor(),_ker.getLatentConstraint()); 
			break;
		}
	}
	private void normalizeRelationVector(DVector vector) {
		double factor = this.getKER().getDimensions(); // 3
		vector.normalizeByMaxMagnitude(this._ker.getRegularizationFactor() * factor); 
	}

	private void normalizeRelationMatrix(DMatrix matrix) {
		// double factor = this.getKER().getDimensions(); // 3
		//vector.normalizeByMaxMagnitude(this._ker.getRegularizationFactor() * factor);
		double factor = this.getKER().getDimensions(); 
		factor *= factor;
		double maxValue = 0.0;
		for (int row=0; row < matrix.rows(); row++)
			for (int col=0; col < matrix.cols(); col++) {
				double value = Math.abs( matrix.getValue(row,col) );
				if (value > maxValue) maxValue = value;
			}
		if (maxValue > factor) {
			factor /= maxValue;
			for (int row=0; row < matrix.rows(); row++)
				for (int col=0; col < matrix.cols(); col++)
					matrix.setValue(row,col, matrix.getValue(row,col) * factor );
		}
	}

	protected void normalize() throws KException {
		if (this.getType().equals(ENTITY))  {
			String entityType = KEntity.extractTypeFrom(this.getName());
			if (!this._ker.getGraph().types().getType( entityType ).isContinuous()) {
				DVector vector = this.representation().getSLR(ENTITY_VECTOR_SLR).getRow(0);
				this.normalizeEntityVector(vector);
			}
		}
		if (this.getType().equals(RELATION))  {
			DVector vector = this.representation().getSLR(RELATION_VECTOR_SLR).getRow(0);
			this.normalizeRelationVector(vector);
			if (this.isProjectionMatrixActive()) {
				DMatrix matrix = this.representation().getSLR(RELATION_MATRIX_SLR);
				this.normalizeRelationMatrix(matrix);
			}
			if (this.isInverseRelationActive()) {
			    vector = this.representation().getSLR(RELATION_VECTOR_SLR_INVERSE).getRow(0);
				this.normalizeRelationVector(vector);
				if (this.isProjectionMatrixActive()) {
					DMatrix matrix = this.representation().getSLR(RELATION_MATRIX_SLR_INVERSE);
					this.normalizeRelationMatrix(matrix);
				}
			}
		}
	}


	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("embed %s create %s", this.getName(), this.getType()) );
			bf.newLine();
			switch (this.getType()) {
			case "ENTITY":
				bf.write( String.format("embed %s config %s 0:%s", this.getName(), "evs", this.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0)) );
				bf.newLine();
				break;
			case "RELATION":
				bf.write( String.format("embed %s config %s 0:%s", this.getName(), "rvs", this.representation().getSLR(KEmbed.RELATION_VECTOR_SLR).getRow(0)) );
				bf.newLine();
				if (this.isProjectionMatrixActive()) {
					for (int r=0; r < this._ker.getDimensions(); r++) {
						bf.write( String.format("embed %s config %s %d:%s", this.getName(), "rpm", r, this.representation().getSLR(KEmbed.RELATION_MATRIX_SLR).getRow(r) ) );
						bf.newLine();
					}
				}
				if (this.isInverseRelationActive()) {
					bf.write( String.format("embed %s config %s 0:%s", this.getName(), "rvsi", this.representation().getSLR(KEmbed.RELATION_VECTOR_SLR_INVERSE).getRow(0)) );
					bf.newLine();
					if (this.isProjectionMatrixActive()) {
						for (int r=0; r < this._ker.getDimensions(); r++) {
							bf.write( String.format("embed %s config %s %d:%s", this.getName(), "rpmi", r, this.representation().getSLR(KEmbed.RELATION_MATRIX_SLR_INVERSE).getRow(r) ) );
							bf.newLine();
						}
					}
				}
				break;
			}
			// bf.write( String.format("embed %s config learning_rate %f", this.getName(), this.getLearningRate()) );
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
