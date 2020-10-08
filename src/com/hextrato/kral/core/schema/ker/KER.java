package com.hextrato.kral.core.schema.ker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DVariableSet;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KRelation;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.schema.graph.KTripleSet;
import com.hextrato.kral.core.schema.graph.KType;
import com.hextrato.kral.core.schema.graph.KTypeSet;
import com.hextrato.kral.core.util.exception.KException;

public class KER extends AMetaNamedObject {

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }
	public void setGraph(KGraph graph) throws KException { 
		this._graph = graph;
		if (graph == null) 
			throw new KException("Invalid null graph");
		else
			this.properties().set(__INTERNAL_PROPERTY_GRAPH__, graph.getName());
	}
	
	public KER (KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		// this._name = name;
		this._schema = schema;
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, schema.getName());
		this.properties().declare(__INTERNAL_PROPERTY_GRAPH__, "String");
		this.properties().set(__INTERNAL_PROPERTY_GRAPH__, "");
		this.properties().declare("dimensions", "Integer");
		this.properties().set("dimensions", Integer.toString(this.getDimensions()));
		this.properties().declare("learning_rate", "Double");
		this.properties().set("learning_rate", Double.toString(this.getLearningRate()));
		this.properties().declare("learning_margin", "Double");
		this.properties().set("learning_margin", Double.toString(this.getLearningMargin()));
		this.properties().declare("disjoint_factor", "Double");
		this.properties().set("disjoint_factor", Double.toString(this.getDisjointFactor()));
		this.properties().declare("disjoint_margin", "Double");
		this.properties().set("disjoint_margin", Double.toString(this.getDisjointMargin()));
		this.properties().declare("random_factor", "Double");
		this.properties().set("random_factor", Double.toString(this.getRandomFactor()));
		this.properties().declare("regularization_type", "String");
		this.properties().set("regularization_type", this.getRegularizationType());
		this.properties().declare("regularization_factor", "Double");
		this.properties().set("regularization_factor", Double.toString(this.getRegularizationFactor()));
		this.properties().declare("regularization_margin", "Double");
		this.properties().set("regularization_margin", Double.toString(this.getRegularizationMargin()));
		this.properties().declare("projection_matrices", "Boolean");
		this.properties().set("projection_matrices", Boolean.toString(this.isProjectionMatrixActive()));
		this.properties().declare("inverse_relations", "Boolean");
		this.properties().set("inverse_relations", Boolean.toString(this.isInverseRelationActive()));
		this.properties().declare("ignore_types", "Boolean");
		this.properties().set("ignore_types", Boolean.toString(this.isIgnoreTypesActive()));
		this.properties().declare("latent_constraint", "Double");
		this.properties().set("latent_constraint", Double.toString(this.getLatentConstraint()));
		this.properties().declare("enforced_cycles", "Integer");
		this.properties().set("enforced_cycles", Integer.toString(this.getEnforcedLearningCycles()));
		this.properties().declare("current_cycles", "Integer");
		this.properties().set("current_cycles", Integer.toString(this.getCurrentCycles()));
		this.properties().declare("functional_negative_rate", "Double");
		this.properties().set("functional_negative_rate", Double.toString(this.getFunctionalNegativeRate()));
		this.properties().declare("functional_negative_max", "Integer");
		this.properties().set("functional_negative_max", Integer.toString(this.getFunctionalNegativeMax()));
		this.properties().declare("cluster.clusteracy", "Double");
		//this.properties().declare("cluster.clusteracy_norm", "Double");
		this.properties().declare("cluster.max_radius", "Double");
		this.properties().declare("cluster.avg_radius", "Double");
		this.properties().declare("cluster.min_distance", "Double");
		this.properties().declare("cluster.max_distance", "Double");
		this.properties().declare("cluster.avg_distance", "Double");
	}

	private KEmbedSet _embedSet = new KEmbedSet(this);
	public KEmbedSet embeds() { return _embedSet; }

	// dimensionality
	public static final int 		MIN_K = 2;
	public static final int 		MAX_K = 1024;
	private 	int 				_dimensions = 0;
	public int getDimensions() { return this._dimensions; }
	public void setDimensions(int k) throws KException { 
		if (this._dimensions != 0) throw new KException("KER k already set ["+this._dimensions+"]");
		if (k < MIN_K || k > MAX_K) throw new KException("Invalid k ["+k+"]");
		this._dimensions = k;
		this.properties().set("dimensions", Integer.toString(this._dimensions));
		this.initialize();
	}

	//
	// CONFIG
	//
	
	private double _learningRate = 0.1;
	private double _learningMargin = 1.0;
	
	public void setLearningRate(double learningRate) throws KException {
		this.enforceLearningCycles();
		this._learningRate = learningRate;
		//for (Map.Entry<String,HXRelationEmbed> relation : this.theRelationEmbeds().entrySet()) {
		//	relation.getValue().setLearningRate(learningRate);
		//}
		for (String embedKey : this.embeds().theList().keySet()) {
			KEmbed embed = this.embeds().getEmbed(embedKey);
			if (embed.getType().contentEquals(KEmbed.RELATION)) {
				if (embed.nnLayerDir != null) embed.nnLayerDir.setLearningRate(learningRate);
				if (embed.nnLayerInv != null) embed.nnLayerDir.setLearningRate(learningRate);
			}
		}
		this.properties().set("learning_rate", Double.toString(this.getLearningRate()));
	}
	public double getLearningRate() { return this._learningRate; }
	
	public void setLearningMargin(double learningMargin) throws KException {
		this.enforceLearningCycles();
		this._learningMargin = learningMargin;
		this.properties().set("learning_margin", Double.toString(this.getLearningMargin()));
	}
	public double getLearningMargin() { return this._learningMargin; }

	private double _disjointFactor = 0.1;
	private double _disjointMargin = 0.25;
	
	public void setDisjointFactor(double factor) throws KException {
		this.enforceLearningCycles();
		this._disjointFactor = factor; 
		this.properties().set("disjoint_factor", Double.toString(this.getDisjointFactor()));
	}
	public void setDisjointMargin(double margin) throws KException {
		this.enforceLearningCycles();
		this._disjointMargin = margin; 
		this.properties().set("disjoint_margin", Double.toString(this.getDisjointMargin()));
	}
	
	public double getDisjointFactor() { return this._disjointFactor; }
	public double getDisjointMargin() { return this._disjointMargin; }

	protected 	double	_randomFactor = 1.0;
	public double getRandomFactor() { return this._randomFactor; }
	public void setRandomFactor(double factor) throws KException { 
		this._randomFactor = factor;
		this.properties().set("random_factor", Double.toString(this.getRandomFactor()));
	}
	
	protected 	String	_regularizationType = "SPACE";
	protected 	double	_regularizationFactor = 1.0;
	protected 	double	_regularizationMargin = Math.sqrt(2)/2;
	public String getRegularizationType() { return this._regularizationType; }
	public double getRegularizationFactor() { return this._regularizationFactor; }
	public double getRegularizationMargin() { return this._regularizationMargin; }
	public void setRegularizationType(String type) throws KException {
		this.enforceLearningCycles();
		switch (type) {
		case "SPACE": this._regularizationType = type; break;
		case "SURFACE": this._regularizationType = type; break;
		case "RANGE": this._regularizationType = type; break;
		default: throw new KException("Invalid regularization type ["+type+"], SPACE/SURFACE/RANGE expected");
		}
		this.properties().set("regularization_type", this.getRegularizationType());
	}
	public void setRegularizationFactor(double factor) throws KException {
		this.enforceLearningCycles();
		this._regularizationFactor = factor; 
		this.properties().set("regularization_factor", Double.toString(this.getRegularizationFactor()));
	}
	public void setRegularizationMargin(double factor) throws KException {
		this.enforceLearningCycles();
		this._regularizationMargin = factor; 
		this.properties().set("regularization_margin", Double.toString(this.getRegularizationMargin()));
	}

	private void initialize() throws KException {
		// randomization_factor
		this._randomFactor = Math.log(this._dimensions)*2/Math.sqrt(this._dimensions);
		if (this._randomFactor > 1) this._randomFactor = 1;
		this._randomFactor /= 2.0;
		this.setRandomFactor(this._randomFactor); // to update property RF 
		// normalization_factor
		double nfactor = Math.max( 1 , Math.sqrt(this.getDimensions())/2.0 );
		this.setRegularizationFactor(nfactor);
		// disjoint_margin
		// this.setDisjointMargin( nfactor / 4 );
		// this.setDisjointMargin( nfactor / 1.0 );
		
		// this.setLearningMargin( Math.sqrt(this._dimensions * 2) / 10 );
		this.setLearningMargin( Math.log10(this._dimensions) / 4 );
		this.setDisjointMargin( Math.log10(this._dimensions) );
	}

	private boolean _learnProjectionMatrix = false;
	private boolean _learnInverseRelation = false;
	
	public void setProjectionMatrix(boolean flag) throws KException {
		this.enforceLearningCycles();
		this._learnProjectionMatrix = flag;
		for (String embedKey : this.embeds().theList().keySet()) {
			KEmbed embed = this.embeds().getEmbed(embedKey);
			if (embed.getType().contentEquals(KEmbed.RELATION)) 
				embed.setProjectionMatrix(flag);
		}
		this.properties().set("projection_matrices", Boolean.toString(this.isProjectionMatrixActive()));
	}
	public void setInverseRelation(boolean flag) throws KException {
		this.enforceLearningCycles();
		this._learnInverseRelation = flag;
		for (String embedKey : this.embeds().theList().keySet()) {
			KEmbed embed = this.embeds().getEmbed(embedKey);
			if (embed.getType().contentEquals(KEmbed.RELATION)) 
				embed.setInverseRelation(flag);
		}
		this.properties().set("inverse_relations", Boolean.toString(this.isInverseRelationActive()));
	}
	public boolean isProjectionMatrixActive() { return this._learnProjectionMatrix; }
	public boolean isInverseRelationActive() { return this._learnInverseRelation; }

	private boolean _ignore_types = false;
	public void setIgnoreTypes(boolean flag) throws KException {
		this.enforceLearningCycles();
		this._ignore_types = flag; 
		this.properties().set("ignore_types", Boolean.toString(this.isIgnoreTypesActive()));
	}
	public boolean isIgnoreTypesActive() { return this._ignore_types; }

	private double _latentConstraint = 0.99;
	
	public void setLatentConstraint(double constraint) throws KException {
		this.enforceLearningCycles();
		this._latentConstraint = Math.abs(constraint); 
		this.properties().set("latent_constraint", Double.toString(this.getLatentConstraint()));
	}
	public double getLatentConstraint() { return this._latentConstraint; }

	// FUNCTIONAL RELATIONS
	
	private double _functionalNegativeRate = 0.1;
	private int _functionalNegativeMax = -1;
	
	public void setFunctionalNegativeRate(double rate) throws KException {
		this.enforceLearningCycles();
		this._functionalNegativeRate = Math.abs(rate); 
		this.properties().set("functional_negative_rate", Double.toString(this.getFunctionalNegativeRate()));
	}
	public double getFunctionalNegativeRate() { return this._functionalNegativeRate; }

	public void setFunctionalNegativeMax(int max) throws KException {
		this.enforceLearningCycles();
		this._functionalNegativeMax = max; 
		this.properties().set("functional_negative_max", Integer.toString(this.getFunctionalNegativeMax()));
	}
	public int getFunctionalNegativeMax() { return this._functionalNegativeMax; }

	
	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("ker %s create", this.getName()) );
			KGraph graph = this.getGraph();
			if (graph != null) {
				bf.newLine();
				bf.write( String.format("ker %s config graph %s", this.getName(), graph.getName()) );
			}
			if (this.getDimensions() > 0) {
				bf.newLine();
				bf.write( String.format("ker %s config dimensions %d", this.getName(), this.getDimensions()) );
			}
			bf.newLine();
			bf.write( String.format("ker %s config learning_rate %f", this.getName(), this.getLearningRate()) );
			bf.newLine();
			bf.write( String.format("ker %s config learning_margin %f", this.getName(), this.getLearningMargin()) );
			bf.newLine();
			bf.write( String.format("ker %s config disjoint_factor %f", this.getName(), this.getDisjointFactor()) );
			bf.newLine();
			bf.write( String.format("ker %s config disjoint_margin %f", this.getName(), this.getDisjointMargin()) );
			bf.newLine();
			bf.write( String.format("ker %s config random_factor %f", this.getName(), this.getRandomFactor()) );
			bf.newLine();
			bf.write( String.format("ker %s config regularization_type %s", this.getName(), this.getRegularizationType()) );
			bf.newLine();
			bf.write( String.format("ker %s config regularization_factor %f", this.getName(), this.getRegularizationFactor()) );
			bf.newLine();
			bf.write( String.format("ker %s config regularization_margin %f", this.getName(), this.getRegularizationMargin()) );
			bf.newLine();
			bf.write( String.format("ker %s config projection_matrices %s", this.getName(), Boolean.toString(this.isProjectionMatrixActive())) );
			bf.newLine();
			bf.write( String.format("ker %s config inverse_relations %s", this.getName(), Boolean.toString(this.isInverseRelationActive())) );
			bf.newLine();
			bf.write( String.format("ker %s config ignore_types %s", this.getName(), Boolean.toString(this.isIgnoreTypesActive())) );
			bf.newLine();
			bf.write( String.format("ker %s config latent_constraint %f", this.getName(), this.getLatentConstraint()) );
			bf.newLine();
			bf.write( String.format("ker %s config enforced_cycles %d", this.getName(), this.getEnforcedLearningCycles()) );
			bf.newLine();
			bf.write( String.format("ker %s config current_cycles %d", this.getName(), this.getCurrentCycles()) );
			bf.newLine();
			bf.write( String.format("ker %s config functional_negative_rate %f", this.getName(), this.getFunctionalNegativeRate()) );
			bf.newLine();
			bf.write( String.format("ker %s config functional_negative_max %d", this.getName(), this.getFunctionalNegativeMax()) );
			bf.newLine();
			this.embeds().hextract(bf);
			for (String metric : this.scores().keySet()) {
				bf.write( String.format("ker %s score %s set %s", this.getName(), metric, this.getScore(metric)) );
				bf.newLine();
			}
			
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}
	
	//
	// LEARN
	//
	private int _enforcedLearningCycles = 0;
	public int getEnforcedLearningCycles() { return this._enforcedLearningCycles; } 
	public void setEnforcedLearningCycles(int cycles) throws KException { 
		this._enforcedLearningCycles = cycles;
		this.properties().set("enforced_cycles", Integer.toString(this.getEnforcedLearningCycles()));
	} 
	
	private int _currentCycles = 0;
	public int getCurrentCycles() { return this._currentCycles; } 
	public void setCurrentCycles(int cycles) throws KException { 
		this._currentCycles = cycles;
		this.properties().set("current_cycles", Integer.toString(this.getCurrentCycles()));
	} 

	int _currentEnforcedLearningCycles = -1; 
	public void enforceLearningCycles() { this._currentEnforcedLearningCycles = _enforcedLearningCycles; }
	public void checkLearningCycleEnforced() { 
		if (this._currentEnforcedLearningCycles >= 0) this._currentEnforcedLearningCycles--;
	}	
	public boolean isLearningCycleEnforced() { 
		return (this._currentEnforcedLearningCycles>=0);
	}	
	
	public void validateConstituents(KTriple triple) throws KException {
		// String headEntity = triple.getHead().getType() + ":" + triple.getHead().getName();
		String headEntity = triple.getHead().getName();
		if (this.embeds().getEmbed(headEntity) == null) {
			this.embeds().create(headEntity, "ENTITY");
			this.embeds().getEmbed(headEntity).normalize();
		}
		// String tailEntity = triple.getTail().getType() + ":" + triple.getTail().getName();
		String tailEntity = triple.getTail().getName();
		if (this.embeds().getEmbed(tailEntity) == null) {
			this.embeds().create(tailEntity, "ENTITY");
			this.embeds().getEmbed(tailEntity).normalize();
		}
		String relation = triple.getRela().getName();
		if (this.embeds().getEmbed(relation) == null) {
			this.embeds().create(relation, "RELATION");
			this.embeds().getEmbed(relation).normalize();
		}
	}

	public void learn (KSplit split) throws KException {
		this.learn(split, 1);
	}
	
	public void debug (String message)  {
		KRAL.debug(message);
	}
	
	public void learn (KSplit split, int repeatTimes) throws KException { 
		if (this._graph == null) throw new KException("Source graph not defined yet");
		if (this.getDimensions() == 0) throw new KException("Dimensionality not defined yet");
		KTypeSet types = this._graph.types();
		
		List<String> tripleKeys = new ArrayList<String>(this._graph.triples().theList().keySet());
		KTriple triple = null;

		// check embed initialization for all triples' constituents
		for (String tripleUID : tripleKeys) {
			triple = this._graph.triples().getTriple(tripleUID);
			validateConstituents(triple);
		}

		int tripleCount = 0;
		for (int cycle = 0; cycle < repeatTimes; cycle++) {
	
			this.setCurrentCycles( this.getCurrentCycles() + 1 );

			this.checkLearningCycleEnforced();
			
			// debug("KER.cycle = "+cycle);
			
			//
			// disjoint-type-based learn
			// 
			for (String typeEntry : types.theList().keySet()) {
				KType type = types.getType(typeEntry);
				if (type.isDisjoint()) {
					//debug("disjoint type = "+type.getName());
					for (String entityEntry_A : _graph.entities().theList().keySet()) {
						// get Entity A ?
						KEntity entity_A = _graph.entities().getEntity(entityEntry_A);
						// check Entity A type and split ?
						if (entity_A.getType().equals(type.getName()) && entity_A.getSplit().getName().equals(split.getName())) {
							// debug("A = " + entity_A.getName());
							// get Entity B ?
							for (String entityEntry_B : _graph.entities().theList().keySet()) {
								KEntity entity_B = _graph.entities().getEntity(entityEntry_B);
								// check Entity B != entity A AND B type only (split later) ?
								if ( entity_B.getType().equals(type.getName()) && entity_B.getName().compareTo(entity_A.getName()) > 0 ) {
									// debug("  B = " + entity_A.getName());
									// do A and B have embed ? 
									if (this.embeds().getEmbed(entity_A.getName()) != null && this.embeds().getEmbed(entity_B.getName()) != null ) {
										KEmbed aEmb = this.embeds().getEmbed(entity_A.getName());
										KEmbed bEmb = this.embeds().getEmbed(entity_B.getName());
										DVector aVec = aEmb.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0);
										DVector bVec = bEmb.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0);
										
										// debug("------");
										// debug("AVec   = " + aVec.toString() + " => "+aVec.magnitude());
										// debug("BVec   = " + bVec.toString() + " => "+bVec.magnitude());
										aVec.moveAwayFrom(bVec, this.getDisjointFactor(), this.getDisjointMargin());
										
										// debug("AVec bn= " + aVec.toString() + " => "+aVec.magnitude());
										// KEEP TO THE END OF EACH DISJOINT TYPE CYCLE ? NO ?// 
										aEmb.normalize();
										// check Entity B split ?
										if ( entity_B.getSplit().getName().equals(split.getName()) ) {
											bVec.moveAwayFrom(aVec, this.getDisjointFactor(), this.getDisjointMargin());
											// debug("BVec bn= " + bVec.toString() + " => "+bVec.magnitude());
											// KEEP TO THE END OF EACH DISJOINT TYPE CYCLE ? NO ?//  
											bEmb.normalize();
										}
										// debug("AVec n = " + aVec.toString() + " => "+aVec.magnitude());
										// debug("BVec n = " + bVec.toString() + " => "+bVec.magnitude());
									}
								}
							}
						}
					}
					for (String entityEntry_A : _graph.entities().theList().keySet()) {
						KEntity entity_A = _graph.entities().getEntity(entityEntry_A);
						if (entity_A.getType().equals(type.getName()) && entity_A.getSplit().getName().equals(split.getName())) {
							KEmbed aEmb = this.embeds().getEmbed(entity_A.getName());
							aEmb.normalize();
						}
					}
				}
			}

			// if (1==1) return;
			
			// ...
			// triple-based learn 
			Collections.shuffle(tripleKeys);
			for (String tripleUID : tripleKeys) {
				triple = this._graph.triples().getTriple(tripleUID);
				// check triple split ?
				if (triple.getSplit().getName().equals(split.getName())) {
					if (cycle==0) tripleCount++;
					// debug("learning triple ... "+tripleUID);
					// if (cycle == 0) validateConstituents(triple); (DONE BEFORE !!!)
					this.learn(triple);
				}
			}
			
			// ...
			// regularization
			/*
			for (String entityEntry : _graph.entities().theList().keySet()) {
				// get Entity
				KEntity entity = _graph.entities().getEntity(entityEntry);
				// check Entity split
				if (entity.getSplit().getName().equals(split.getName())) {
					KEmbed entityEmbed = this.embeds().getEmbed(entity.getName());
					entityEmbed.normalize();
				}
			}
			*/
			
		}
		KRAL.message("Split "+split.getName()+" repeated "+repeatTimes+" learning cycle(s) for "+tripleCount+" triples");
	}
		
	//
	// _LEARN_TRIPLE_
	//
	private void learn (KTriple triple) throws KException {
		if (triple.getPola()) {
			this.learn_POS(triple);
		} else {
			this.learn_NEG(triple);
		}
	}

	private void tripleValidation(KTriple triple) throws KException {
		String headName = triple.getHead().getName();
		//String headType = triple.getHead().getType();
		String relaName = triple.getRela().getName();
		String tailName = triple.getTail().getName();
		//String tailType = triple.getTail().getType();
		
		// HextraEmbed head = this.embeds().getEmbed(headType+":"+headName);
		KEmbed head = this.embeds().getEmbed(headName);
		KEmbed	rela = this.embeds().getEmbed(relaName);
		// HextraEmbed tail = this.embeds().getEmbed(tailType+":"+tailName);
		KEmbed tail = this.embeds().getEmbed(tailName);
		
		if (!head.getType().equals(KEmbed.ENTITY)) throw new KException("Invalid head embed type ["+head.getType()+"], ENTITY expected");
		if (!tail.getType().equals(KEmbed.ENTITY)) throw new KException("Invalid tail embed type ["+head.getType()+"], ENTITY expected");
		if (!rela.getType().equals(KEmbed.RELATION)) throw new KException("Invalid relation embed type ["+head.getType()+"], RELATION expected");
	}

	/*
	// random navigation through entities;
	List<String> _randomEntityList = null;
	Iterator<String> _randomEntityIteractor = null; 
	int _randomEntityReseter = 0; 
	private void resetRandomListOfEntities() {
		_randomEntityList = new ArrayList<String>(this._graph.entities().theList().keySet());
		Collections.shuffle(_randomEntityList);
		_randomEntityIteractor = _randomEntityList.iterator();
		_randomEntityReseter = 0;
	}
	private String getNextRandomEntity() {
		if (_randomEntityList == null || _randomEntityIteractor == null) //  || Math.random() < 0.001) 
			resetRandomListOfEntities();
		if (_randomEntityIteractor.hasNext()) return _randomEntityIteractor.next();
		_randomEntityIteractor = _randomEntityList.iterator();
		_randomEntityReseter++;
		if (_randomEntityReseter > 100) resetRandomListOfEntities();
		return _randomEntityIteractor.next();
	}
	*/
	
	private void learn_POS (KTriple triple) throws KException {
		tripleValidation(triple);
		String headName = triple.getHead().getName();
		//String headType = triple.getHead().getType();
		String relaName = triple.getRela().getName();
		String tailName = triple.getTail().getName();
		//String tailType = triple.getTail().getType();
		
		KRelation rela = triple.getRela();

		KEmbed	relaEmbed = this.embeds().getEmbed(relaName);
		//HextraEntity posHead = this._graph.entities().getEntity(headType+":"+headName);
		//HextraEntity posTail = this._graph.entities().getEntity(tailType+":"+tailName);
		KEntity posHead = this._graph.entities().getEntity(headName);
		KEntity posTail = this._graph.entities().getEntity(tailName);
		// ...
		// find negative head and tail entities
		
		// List<String> entities = new ArrayList<String>(this._graph.entities().theList().keySet());
		// Collections.shuffle(entities);
		KEmbed negHead = null;
		KEmbed negTail = null;
		if (!triple.getCorruptedHead().isEmpty())
			negHead = this.embeds().getEmbed(triple.getCorruptedHead());
		if (!triple.getCorruptedTail().isEmpty())
			negTail = this.embeds().getEmbed(triple.getCorruptedTail());

		double probabilisticChange = Math.min(1.0, 100.0/(this._graph.entities().theList().size() + this._graph.triples().theList().size()) );
		
		if (negHead == null || Math.random() <= probabilisticChange) {
			// for (int i = 0; i < this._graph.entities().theList().keySet().size(); i++) {
			// Collections.shuffle(this._graph.types().getType(posHead.getType()).theEntitySet());
			// for (String entityName : this._graph.types().getType(posHead.getType()).theEntitySet()) {
			String entityName = this._graph.types().getType(posHead.getType()).getRandomEntity();
				// count++;
				// String entityName = getNextRandomEntity();
				KEntity candidate = this._graph.entities().getEntity(entityName);
				if (candidate.getType().equals(posHead.getType()) || this.isIgnoreTypesActive() ) {
					if (candidate != posHead && candidate != posTail)
						// negHead = this.embeds().getEmbed(candidate.getType()+":"+candidate.getName());
						// postitive triple does not exists
						if (!this.getGraph().triples().containsTriple(candidate.getName(), relaName, tailName, true)) {
							negHead = this.embeds().getEmbed(candidate.getName());
							triple.setCorruptedHead(candidate.getName());
						}
				}
				// if (negHead != null) break;
			// }
		}
		if (negTail == null || Math.random() <= probabilisticChange) {
			// for (int i = 0; i < this._graph.entities().theList().keySet().size(); i++) {
			// Collections.shuffle(this._graph.types().getType(posTail.getType()).theEntitySet());
			// for (String entityName : this._graph.types().getType(posTail.getType()).theEntitySet()) {
			String entityName = this._graph.types().getType(posTail.getType()).getRandomEntity();
				// count++;
				/// String entityName = getNextRandomEntity();
				KEntity candidate = this._graph.entities().getEntity(entityName);
				if (candidate.getType().equals(posTail.getType()) || this.isIgnoreTypesActive() ) {
					if (candidate != posHead && candidate != posTail)
						// negTail = this.embeds().getEmbed(candidate.getType()+":"+candidate.getName());
						if (!this.getGraph().triples().containsTriple(headName, relaName, candidate.getName(), true)) {
							negTail = this.embeds().getEmbed(candidate.getName());
							triple.setCorruptedTail(candidate.getName());
						}
				}
				// if (negTail != null) break;
			// }
		}

		// if (1==1) return;

		//debug ("pos head = " + posHead.getName());
		//debug ("pos tail = " + posTail.getName());
		//if (negHead != null) debug ("neg head = " + negHead.getName());
		//if (negTail != null) debug ("neg tail = " + negTail.getName());
		relaEmbed.learnPositiveRelation(triple, negHead, negTail); // + negTail, +negHead
		// additional functional negative learning

		if (rela.isFunctional()) {
			int countFunctionalNegative = 0;
			// Collections.shuffle(this._graph.types().getType(posTail.getType()).theEntitySet());
			for (String entityName : this._graph.types().getType(posTail.getType()).theEntitySet()) {
			// for (String entityName : entities) {
			//for (int i = 0; i < this._graph.entities().theList().keySet().size(); i++) {
				//String entityName = getNextRandomEntity();
				KEmbed negFunctionalTail = null;
				KEntity candidate = this._graph.entities().getEntity(entityName);
				if (candidate.getType().equals(posTail.getType()) || this.isIgnoreTypesActive() ) {
					if (candidate != posTail && (negTail == null || candidate.getName() != negTail.getName()) && Math.random() <= this.getFunctionalNegativeRate() )
						if (!this.getGraph().triples().containsTriple(headName, relaName, candidate.getName(), true))
							negFunctionalTail = this.embeds().getEmbed(candidate.getName());
				}
				if (negFunctionalTail != null) {
					relaEmbed.learnPositiveRelation(triple, null, negFunctionalTail);
					countFunctionalNegative++;
					if (countFunctionalNegative > this.getFunctionalNegativeMax() && this.getFunctionalNegativeMax() >= 0) 
						break;
				}
			}
		}
	}
	
	private void learn_NEG (KTriple triple) throws KException {
		tripleValidation(triple);
		String relaName = triple.getRela().getName();
		KEmbed	rela = this.embeds().getEmbed(relaName);
		rela.setPositiveMarginError(0);
		rela.setInverseMarginError(0);
		rela.learnNegativeRelation(triple);
	}

	//
	// GUI
	//
	KERVisual guiContainer = null;
	public void draw() throws KException {
		if (guiContainer == null) guiContainer = new KERVisual(this);
		else { 
			//guiContainer.setVisible(true);
			guiContainer.repaint();
		}
	}

	//
	// SCORE
	//
	
	private DVariableSet _scores = new DVariableSet();
	public DVariableSet scores() { return _scores; } 
	public void setScore(String metric, String value) throws KException {
		if (!this.scores().keySet().contains(metric))
			this.scores().declare(metric, "Double");
		this.scores().set(metric, value);
	}
	public String getScore(String metric) throws KException {
		if (!this.scores().keySet().contains(metric))
			return "";
		else
			return this.scores().get(metric);
	}
	
	private String 			_scoringRoot = "";
	private String 			_scoringSplit = "";
	private KGraph 	_scoringGraph = null;
	List<String> 			_scoringTriples = null;

	private static final int[] HITS_AT = new int[] {1,3,5,10};

	private int _setScoreCountTriple;
	private int _relScoreCountTriple;

	private double _scoreRank;
	
	// MRR
	private double _setScoreMRR;
	private double _relScoreMRR;

	// MRank
	private double _setScoreMRank;
	private double _relScoreMRank;

	// Hits
	private double[] _setScoreHits = new double[HITS_AT.length];
	private double[] _relScoreHits = new double[HITS_AT.length];
	
	private void evaluate(KTriple triple) throws KException {
		String headName = triple.getHead().getName();
		String relaName = triple.getRela().getName();
		// String relaName_Orig = relaName;

		//if ( !_scoringGraph.relations().getRelation(relaName).getOriginalName().equals("") ) {
		//	relaName_Orig = _scoringGraph.relations().getRelation(relaName).getOriginalName();	
		//}
		String tailName = triple.getTail().getName();

		// HextraEntity head = _scoringGraph.entities().getEntity(headName);
		KEntity tail = _scoringGraph.entities().getEntity(tailName);

		KEmbed	headEmbed = this.embeds().getEmbed(headName);
		// HXVector headVector = headEmbed.representation().getSLR(HextraEmbed.ENTITY_VECTOR_SLR).getRow(0);
		KEmbed	relaEmbed = this.embeds().getEmbed(relaName);
		KEmbed	tailEmbed = this.embeds().getEmbed(tailName);
		DVector tailVector = tailEmbed.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0);

		relaEmbed.feed(headEmbed,tailEmbed);
		DVector headResult = relaEmbed.nnLayerDir.theOutputValues();
		//HXVector tailResult = null;
		//if (rela.nnLayerInv != null) tailResult = rela.nnLayerInv.theOutputValues();
		
		// dist
		double tripleDistance = headResult.distance(tailVector);
		_scoreRank = 1;

		// for (String falseEntityKey : this._scoringGraph.entities().theList().keySet()) {
		for (String falseEntityKey : this._graph.types().getType(tail.getType()).theEntitySet()) {
			KEntity falseEntity = this._scoringGraph.entities().getEntity(falseEntityKey);
			String falseEntityName = falseEntity.getName();
			if (!falseEntityName.equals(headName) && !falseEntityName.equals(tailName)) {
				
				boolean isValidFalseTail = true;
				// boolean isValidFalseHead = true;

				if (_scoringGraph.isTyped() && !this.isIgnoreTypesActive()) {
					isValidFalseTail = false;
					if (falseEntity.getType().equals(tail.getType())) {
						isValidFalseTail = true;
					}
					//isValidFalseHead = false;
					//if (falseEntity.getType().equals(head.getType())) {
					//	isValidFalseHead = true;
					//}
				}
				
				// replace TAIL
				if (isValidFalseTail) {
					
					double falseTailDistance = -1;
					
					KEmbed falseTailEmbed = this.embeds().getEmbed(falseEntityName);
					
					if (falseTailEmbed != null) {

						DVector falseTailVector = falseTailEmbed.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0);
						
						if (falseTailVector != null) {

							falseTailDistance = headResult.distance(falseTailVector);
							if (falseTailDistance < tripleDistance)
								_scoreRank++;								
							
						} // falseTailVector != null
						
					} // falseTailEmbed != null
					
				} // isValidFalseTail

				// REPLACE HEAD
				// (REMOVED)

			}

		} // for negTail
			
		// mean rank
		_relScoreMRank += _scoreRank;

		// mrr
		_relScoreMRR += 1.0/_scoreRank;

		// hits
		for (int hh=0; hh<HITS_AT.length; hh++) {
			if (_scoreRank <= HITS_AT[hh]) _relScoreHits[hh] ++;
		}
		 
	}
	
	private int evaluate(KRelation relation) throws KException {
		this._relScoreCountTriple = 0;
		_relScoreCountTriple = 0;
		_relScoreMRR = 0;
		_relScoreMRank = 0;
		for (int hh=0; hh<HITS_AT.length; hh++) {
			_relScoreHits[hh] = 0;
		}
		for (String tripleUID : _scoringTriples) {
			KTriple triple = _scoringGraph.triples().getTriple(tripleUID);
			if ( ( triple.getRela().getName().equals(relation.getName()) || relation.getName().equals("*") || relation.getName().equals("%") )
					&& triple.getSplit().getName().equals(_scoringSplit) && triple.getPola() == true) {
				if (triple.getHead().getSplit().getName().equals("_FIX_") || triple.getTail().getSplit().getName().equals("_FIX_")) {
					// Nothing
				} else {
					this._relScoreCountTriple ++;
					this.evaluate(triple);
				}
			}
		}

		if (_relScoreCountTriple > 0) {
			_setScoreCountTriple += _relScoreCountTriple;
			_setScoreMRR += _relScoreMRR;
			_setScoreMRank += _relScoreMRank;
			for (int hh=0; hh<HITS_AT.length; hh++) {
				_setScoreHits[hh] += _relScoreHits[hh];
			}
	
			_relScoreMRR /= _relScoreCountTriple;
			_relScoreMRank /= _relScoreCountTriple;
			for (int hh=0; hh<HITS_AT.length; hh++) {
				_relScoreHits[hh] /= _relScoreCountTriple;
			}
	
			this.setScore(_scoringRoot+"rel."+relation.getName()+".triple_count", Double.toString(this._relScoreCountTriple));
			this.setScore(_scoringRoot+"rel."+relation.getName()+".mrr", Double.toString(_relScoreMRR));
			this.setScore(_scoringRoot+"rel."+relation.getName()+".mrank", Double.toString(_relScoreMRank));
			for (int hh=0; hh<HITS_AT.length; hh++) {
				this.setScore(_scoringRoot+"rel."+relation.getName()+".hits@"+Integer.toString(HITS_AT[hh]), Double.toString(_relScoreHits[hh]));
			}
		}
		return _relScoreCountTriple;
	}
	
	public void evaluate(KSplit split) throws KException {
		if (split == null) throw new KException("Invalid null split");
		_scoringSplit = split.getName();
		KRAL.message("Scoring split "+_scoringSplit);
		_scoringGraph = this.getGraph();
		_scoringTriples = new ArrayList<String>(this._graph.triples().theList().keySet());
		
		_scoringRoot = ""+_scoringSplit+".";

		_setScoreCountTriple = 0;
		_setScoreMRR = 0;
		_setScoreMRank = 0;
		for (int hh=0; hh<HITS_AT.length; hh++) {
			_setScoreHits[hh] = 0;
		}

		for (String relationName : _scoringGraph.relations().theList().keySet()) {
			KRelation relation =  _scoringGraph.relations().getRelation(relationName);
			int tripleCount = this.evaluate(relation);
			KRAL.message("Relation "+relation.getName()+ " scored for "+tripleCount+" triples");
		}

		_setScoreMRR /= _setScoreCountTriple;
		_setScoreMRank /= _setScoreCountTriple;
		for (int hh=0; hh<HITS_AT.length; hh++) {
			_setScoreHits[hh] /= _setScoreCountTriple;
		}
		
		this.setScore(_scoringRoot+"set"+".triple_count", Double.toString(_setScoreCountTriple));
		this.setScore(_scoringRoot+"set"+".mrr", Double.toString(_setScoreMRR));
		KConsole.message(_scoringRoot+"set"+".mrr" + " = " + Double.toString(_setScoreMRR));
		this.setScore(_scoringRoot+"set"+".mrank", Double.toString(_setScoreMRank));
		KConsole.message(_scoringRoot+"set"+".mrank" + " = " + Double.toString(_setScoreMRank));
		for (int hh=0; hh<HITS_AT.length; hh++) {
			this.setScore(_scoringRoot+"set"+".hits@"+Integer.toString(HITS_AT[hh]), Double.toString(_setScoreHits[hh]));
			KConsole.message(_scoringRoot+"set"+".hits@"+Integer.toString(HITS_AT[hh]) + " = " + Double.toString(_setScoreHits[hh]));
		}
	}

	// public void cluster(int kClusters, String targetType, int minCount) throws KException {
	public void cluster(String targetSplit, int kClusters, String targetType) throws KException {
		//System.out.println("k="+k);
		//System.out.println("min="+min);
		//System.out.println("type="+type);
		if (this.getGraph() == null) {
			KConsole.error("Undefined graph for ker ["+this.getName()+"]");
		}
		if (targetSplit == null) targetSplit = "";
		if (targetSplit.equals("")) targetSplit = "*";
		//
		// create list of type entities with embedding values
		// AND
		// create list of (feature,values) to count
		//

		double clusteracy = 0.0;
		
		for (int i = 0; i < 1; i++) {
			KCluster cluster = new KCluster(this.getGraph(), this.embeds(), this.getDimensions(), targetSplit, targetType);
			//cluster.dumpStats();
			//cluster.dumpEmbed();
			cluster.knn(kClusters); 
			double score = cluster.evaluate(); // (minCount);
			if (score > clusteracy) clusteracy = score;
			//cluster.dumpClust();

			this.properties().set("cluster.clusteracy", Double.toString(clusteracy));
			// this.properties().set("cluster.clusteracy_norm", Double.toString(cluster.getNormClusteracy()));
			this.properties().set("cluster.max_radius", cluster.properties().get("max_radius"));
			this.properties().set("cluster.avg_radius", cluster.properties().get("avg_radius"));
			this.properties().set("cluster.min_distance", cluster.properties().get("min_distance"));
			this.properties().set("cluster.max_distance", cluster.properties().get("max_distance"));
			this.properties().set("cluster.avg_distance", cluster.properties().get("avg_distance"));
			this.setScore(targetSplit+".cluster.clusteracy."+kClusters, Double.toString(clusteracy));
			// this.setScore(targetSplit+".cluster.clusteracy_norm."+kClusters, Double.toString(cluster.getNormClusteracy()));
			this.setScore(targetSplit+".cluster.max_radius."+kClusters, cluster.properties().get("max_radius"));
			this.setScore(targetSplit+".cluster.avg_radius."+kClusters, cluster.properties().get("avg_radius"));
			this.setScore(targetSplit+".cluster.min_distance."+kClusters, cluster.properties().get("min_distance"));
			this.setScore(targetSplit+".cluster.max_distance."+kClusters, cluster.properties().get("max_distance"));
			this.setScore(targetSplit+".cluster.avg_distance."+kClusters, cluster.properties().get("avg_distance"));
			KConsole.message( "cluster.MAX_RADIUS = " + cluster.properties().get("max_radius"));
			KConsole.message( "cluster.AVG_RADIUS = " + cluster.properties().get("avg_radius"));
			KConsole.message( "cluster.MIN_DISTANCE = " + cluster.properties().get("min_distance"));
			KConsole.message( "cluster.MAX_DISTANCE = " + cluster.properties().get("max_distance"));
			KConsole.message( "cluster.AVG_DISTANCE = " + cluster.properties().get("avg_distance"));
			//cluster.dumpEmbed();
			cluster.dumpClust();
			//cluster.dumpStats();
			KConsole.message( "cluster.CLUSTERACY = " + clusteracy);
			KConsole.lastDouble(clusteracy);
		}

	}
	
	
}
