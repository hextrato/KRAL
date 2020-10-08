package com.hextrato.kral.core.schema.ker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KRelation;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.schema.graph.KTripleSet;
import com.hextrato.kral.core.util.exception.KException;

public class KCluster extends AMetaNamedObject {

	KGraph _graph;
	KEmbedSet _embeds;
	int _dimensions = 0; 
	String _targetType = "";
	String _targetSplit = "";
	
	int _numEmbeds = 0;
	int _numClusters = 0;
	int _numFeatVals = 0;
	
	Properties cCounts;
	Properties cStats;
	
	Map<String,DVector> theFeatureValueStats; 
	Map<String,DVector> theClusters; 
	Map<String,DVector> theEntities; 
	
	public KCluster(KGraph graph, KEmbedSet embeds, int dimensions, String targetSplit, String targetType) throws KException {
		super();
		this._graph = graph;
		this._embeds = embeds;
		this._dimensions = dimensions;
		this._targetSplit = targetSplit;
		this._targetType = targetType;

		theFeatureValueStats = new HashMap<String,DVector>();
		theClusters = new HashMap<String,DVector>();
		theEntities = new HashMap<String,DVector>();
		
		this.properties().declare("avg_radius", "Double");
		this.properties().set("avg_radius", "0.0");
		this.properties().declare("max_radius", "Double");
		this.properties().set("max_radius", "0.0");
		this.properties().declare("avg_distance", "Double");
		this.properties().set("avg_distance", "0.0");
		this.properties().declare("min_distance", "Double");
		this.properties().set("min_distance", "0.0");
		this.properties().declare("max_distance", "Double");
		this.properties().set("max_distance", "0.0");
		
		KTripleSet tripleSet = this._graph.triples();
		for (String tripleKey : tripleSet.theList().keySet()) {
			KTriple triple = tripleSet.getTriple(tripleKey);
			if (triple.getPola()) {
				KEntity head = triple.getHead();
				KRelation rela = triple.getRela();
				KEntity tail = triple.getTail();
				String feature = "";
				String value = "";
				if (head.getType().equals(targetType)  && ( head.getSplit().equals(this._targetSplit) || this._targetSplit.equals("*") ) ) {
					feature = rela.getName();
					value = tail.getName();
					this.addEmbed(head.getName(), this._embeds.getEmbed(head.getName()).representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0) );
				} 
				else if (tail.getType().equals(targetType) && ( tail.getSplit().equals(this._targetSplit) || this._targetSplit.equals("*") ) ) {
					feature = rela.getName();
					value = head.getName();
					this.addEmbed(tail.getName(), this._embeds.getEmbed(tail.getName()).representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0) );
				}
				if (!feature.equals("")) {
					this.countFeatureValue(feature, value);
				}
			}
		}
	}

	public void countFeatureValue(String feature, String value) throws KException {
		String fv = "("+feature+","+value+")";
		if (!theFeatureValueStats.containsKey(fv)) {
			DVector fvStat = new DVector(1).setValue(0, 1.0);
			this._numFeatVals = theFeatureValueStats.size();
			theFeatureValueStats.put(fv, fvStat);
		} else {
			DVector fvStat = theFeatureValueStats.get(fv);
			fvStat.setValue(0, fvStat.getValue(0) + 1 );
		}
	}

	public void addEmbed(String embedName, DVector evecEmbed) throws KException {
		String ev = embedName;
		if (!this.theEntities.containsKey(ev)) {
			this.theEntities.put(ev,new DVector(this._dimensions ));
			this.theEntities.get(ev).setValues(evecEmbed.toString());
			this._numEmbeds++;
			this._embeds.getEmbed(embedName).setProperty(__INTERNAL_PROPERTY_CLUSTER__, "");
		} 

	}

	public void dumpStats() throws KException {
		for (String fv : theFeatureValueStats.keySet()) {
			KConsole.message(fv + " = " + Double.toString(theFeatureValueStats.get(fv).getValue(0)) );
		}
	}
	public void dumpClust() throws KException {
		for (String f : this.theClusters.keySet()) {
			DVector v = this.theClusters.get(f);
			KConsole.message(f + " = " + v);
		}
	}
	public void dumpEmbed() throws KException {
		for (String p : this.theEntities.keySet()) {
			DVector entity = this.theEntities.get(p);
			String currCluster = this._embeds.getEmbed(p).getProperty(__INTERNAL_PROPERTY_CLUSTER__);
			KConsole.message(p +  " => " + currCluster + " : " + entity);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void knn(int K) throws KException {
		// how many clusters Ks ?
		if (K > this._numEmbeds / 2) K = this._numEmbeds / 2;
		if (K < 2) throw new KException("Invalud number of clusters K = "+K);
		this._numClusters = K;
		
		DVector _HYPERZERO_ = new DVector(this._dimensions).fillWith(0.0);

		DVector aCluster = new DVector(this._dimensions);
		DVector bCluster = new DVector(this._dimensions);
		DVector anEntity = new DVector(this._dimensions);

		//
		// CREATE CLUSTERS
		//
		/* KEEP RANDOM ONLY
		int vectorPos = 0;
		int vectorSub = 1;
		double vectorVal = 1.0;
		*/
		KConsole.message("KNN: Initializing "+K+" clusters...");
		
		Iterator<String> nextEntity = this.theEntities.keySet().iterator();
		for (int i = 0; i < K; i++) {
			
			// DVector center = new DVector(this._dimensions);
			
			/*
			for (int pos=0; pos < this._dimensions ; pos++) {
				// aCluster.setValue(pos, Math.random()*0.2 - 0.1);
				aCluster.setValue(pos, Math.random()*2.0 - 1.0);
			}
			*/
			anEntity = this.theEntities.get( nextEntity.next() );
			
			/* KEEP RANDOM ONLY
			for (int pos=vectorPos; pos < vectorPos+vectorSub ; pos++) {
				center.setValue(pos, vectorVal);
				vectorVal *= -1.0;
			}
			*/
			String c = "Cluster."+i;
			// this.properties().declare(c, "Vector");
			// this.properties().set(c, aCluster.toString());
			this.theClusters.put(c, new DVector(this._dimensions).setValues( anEntity.toString() ));

			/* KEEP RANDOM ONLY
			vectorPos++; if (vectorPos >= this._dimensions-vectorSub+1) {
				vectorVal *= 0.707;
				vectorSub++;
			}
			*/
		}
		
		//
		// DEFINE EMBED CLUSTER
		// 

		// this.dumpClust();

		int minStillLearning = -1;
		boolean stillLearning = true;
		int maxResetRounds = 5;
		int maxRounds = 50;
		double maxRoundRadius = 0.0;
		double avgRoundRadius = 0.0;
		double cntRoundRadius = 0.0;

		// KConsole.message("Clustering...");
		KConsole.message("KNN: Clustering: up to "+maxRounds+" steps...");

		while (stillLearning && maxRounds-- > 0) {
			
			maxResetRounds--;
			
			System.out.print(".");
		
			stillLearning = false;
			int countStillLearning = 0;

			maxRoundRadius = 0.0;
			avgRoundRadius = 0.0;
			cntRoundRadius = 0.0;
		
			// Assign embed cluster
			System.out.print("A");

			// for (String p : this.properties().keySet()) if (p.startsWith("embed>")) {
			for (String embedName : this.theEntities.keySet()) {
				// String embedName = p.replace("embed>", "");
				// DVector embedVect = (new DVector(this._dimensions)).setValues( this.properties().get(p) );
				// anEntity.setValues( this.properties().get(p) );
				anEntity = this.theEntities.get(embedName);
				
				double minDist = -1;
				String clusterAssigned = "";
	
				// for (String clustName : this.properties().keySet()) if (clustName.startsWith("clust>")) {
				for (String clustName : this.theClusters.keySet()) {
					
					// DVector clustVect = (new DVector(this._dimensions)).setValues( this.properties().get(clustName) );
					// aCluster.setValues(this.properties().get(clustName));
					aCluster = this.theClusters.get(clustName);
					
					if (aCluster.distance(_HYPERZERO_) > 0) {
						// KConsole.message(clustVect + " vs " + embedVect);
						
						// double dist = clustVect.distance(embedVect);
						double dist = aCluster.distance(anEntity);
						
						
						if (dist < minDist || minDist == -1) {
							minDist = dist;
							clusterAssigned = clustName;
						}
						if (dist > maxRoundRadius) maxRoundRadius = dist;
						avgRoundRadius += dist;
						cntRoundRadius += 1.0;
						
					} else if (maxResetRounds >= 0) {
						for (int pos=0; pos < this._dimensions ; pos++) {
							aCluster.setValue(pos, Math.random()*0.2 - 0.1);
						}
						// this.properties().set(clustName,aCluster.toString());
					}
					
				}
				String currCluster = this._embeds.getEmbed(embedName).getProperty(__INTERNAL_PROPERTY_CLUSTER__);
				if (!currCluster.equals(clusterAssigned)) {
					this._embeds.getEmbed(embedName).setProperty(__INTERNAL_PROPERTY_CLUSTER__, clusterAssigned);
					stillLearning = true;
					countStillLearning++;
				}
				// KConsole.message(embedName + " => " + clusterAssigned);
				
			} // embed

			int countActiveClusters = 0;
			// for (String clustName : this.properties().keySet()) if (clustName.startsWith("clust>")) {
			for (String clustName : this.theClusters.keySet()) {
				// DVector clustVect = (new DVector(this._dimensions)).setValues( this.properties().get(clustName) );
				// aCluster.setValues(this.properties().get(clustName));
				aCluster = this.theClusters.get(clustName);
				if (aCluster.distance(_HYPERZERO_) > 0) {
					countActiveClusters++;
				}
			}

			System.out.print("("+countStillLearning+"|"+countActiveClusters+")");
			
			avgRoundRadius /= cntRoundRadius;

			if (minStillLearning == -1 || countStillLearning < minStillLearning) {
				minStillLearning = countStillLearning;
			
				// Ajust cluster center
				System.out.print("R");
	
				cCounts = new Properties();
				
				// for (String clusterName : this.properties().keySet()) if (clusterName.startsWith("clust>")) {
				for (String clusterName : this.theClusters.keySet()) {
					// KConsole.message("clusterName = "+clusterName);
					// DVector newCenter = new DVector(this._dimensions);
					aCluster = this.theClusters.get(clusterName);
					aCluster.fillWith(0.0);
					double howManyEmbeds = 0.0;
					// for (String p : this.properties().keySet()) if (p.startsWith("embed>")) {
					for (String embedName : this.theEntities.keySet()) {
						// String embedName = p.replace("embed>", "");
						// KConsole.message("embedName = "+embedName);
						if (this._embeds.getEmbed(embedName).getProperty(__INTERNAL_PROPERTY_CLUSTER__).equals(clusterName)) {
							howManyEmbeds = howManyEmbeds + 1.0;
							// DVector embedVect = (new DVector(this._dimensions)).setValues( this.properties().get(p) );
							// anEntity.setValues( this.properties().get(p) );
							anEntity = this.theEntities.get(embedName);
							for (int i = 0; i < this._dimensions; i++)
								aCluster.setValue(i, aCluster.getValue(i) + anEntity.getValue(i) );
							
							// cluster count 
						    String statProp = clusterName;
							if (!cCounts.containsKey(statProp))
								cCounts.setProperty(statProp, "0.0");
							cCounts.setProperty(statProp, Double.toString( Double.valueOf(cCounts.getProperty(statProp))+1.0 ) );
	
						}
					}
					if (howManyEmbeds > 0.0) 
						for (int i = 0; i < this._dimensions; i++)
							aCluster.setValue(i, aCluster.getValue(i) / howManyEmbeds );
					// this.properties().set(clusterName,aCluster.toString());
					
				} // cluster

			} else {
				
				// stillLearning = false;
			}

			// this.dumpClust();
		} // while 

		System.out.println("");
		// KConsole.message("lastRound.maxRadius = "+avgRoundRadius);
		this.properties().set("avg_radius", Double.toString(avgRoundRadius));
		this.properties().set("max_radius", Double.toString(maxRoundRadius));

		double avgIntraDistance = 0.0;
		double cntIntraDistance = 0.0;
		double minIntraDistance = -1.0;
		double maxIntraDistance = -1.0;

		// for (String clustNameA : this.properties().keySet()) if (clustNameA.startsWith("clust>")) {
		for (String clustNameA : this.theClusters.keySet()) {
			// DVector clustVect = (new DVector(this._dimensions)).setValues( this.properties().get(clustName) );
			// for (String clustNameB : this.properties().keySet()) if (clustNameB.startsWith("clust>") && clustNameB.compareTo(clustNameA) > 0) {
			for (String clustNameB : this.theClusters.keySet()) if (clustNameB.compareTo(clustNameA) > 0) {
				// aCluster.setValues(this.properties().get(clustNameA));
				// bCluster.setValues(this.properties().get(clustNameB));
				aCluster = this.theClusters.get(clustNameA);
				bCluster = this.theClusters.get(clustNameB);
				double distance = aCluster.distance(bCluster);
				if (minIntraDistance == -1.0 || distance < minIntraDistance) minIntraDistance = distance;
				if (maxIntraDistance == -1.0 || distance > maxIntraDistance) maxIntraDistance = distance;
				avgIntraDistance += distance;
				cntIntraDistance += 1.0;
			}
		}
		if (cntIntraDistance > 0) avgIntraDistance /= cntIntraDistance; else avgIntraDistance = -1.0; 
		
		this.properties().set("avg_distance", Double.toString(avgIntraDistance));
		this.properties().set("min_distance", Double.toString(minIntraDistance));
		this.properties().set("max_distance", Double.toString(maxIntraDistance));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	double clusteracy = 0.0;
	
	double cfvContribSum = 0.0;
	double cfvContribNum = 0.0;
	double cFinalScoreSum = 0.0;
	double cFinalScoreLog = 0.0;

	// public double evaluate(int minCount) throws KException {
	public double evaluate() throws KException {

		// KConsole.message("Evaluating...");
		DVector aCluster = new DVector(this._dimensions);
		DVector anEntity = new DVector(this._dimensions);

		double cFinalScore = 0.0;
		// double cFinalScore_norm = 0.0;

		// double cNormFinalScoreSum = 0.0;
		// double cNormFinalScoreLog = 0.0;

		// double max_radius = Double.valueOf( this.properties().get("max_radius") );
		double avg_distance = Double.valueOf( this.properties().get("avg_distance") );
		
		Properties cfvProps = new Properties();
		Properties cfvCount = new Properties();

		// for each triple
		KTripleSet tripleSet = this._graph.triples();
		for (String tripleKey : tripleSet.theList().keySet()) {
			KTriple triple = tripleSet.getTriple(tripleKey);
			if (triple.getPola()) {
				KEntity head = triple.getHead();
				KRelation rela = triple.getRela();
				KEntity tail = triple.getTail();
				String entity = "";
				String feature = "";
				String value = "";
				if (head.getType().equals(this._targetType) && ( head.getSplit().equals(this._targetSplit) || this._targetSplit.equals("*") ) ) {
					entity = head.getName();
					feature = rela.getName();
					value = tail.getName();
				} 
				else if (tail.getType().equals(this._targetType) && ( tail.getSplit().equals(this._targetSplit) || this._targetSplit.equals("*") )) {
					entity = tail.getName();
					feature = rela.getName();
					value = head.getName();
				}
				if (!entity.equals("")) {
					// DVector embedVect = (new DVector(this._dimensions)).setValues( this.properties().get("embed>"+entity) );
					// anEntity.setValues( this.properties().get("embed>"+entity) );
					anEntity = this.theEntities.get(entity);
					String clusterName = this._embeds.getEmbed(entity).getProperty(__INTERNAL_PROPERTY_CLUSTER__);
					// DVector clustCenter = (new DVector(this._dimensions)).setValues( this.properties().get(clusterName) );
					// aCluster.setValues( this.properties().get(clusterName) );
					
				    Double dist = aCluster.distance(anEntity);
				    // if (dist > max_radius) dist = max_radius;  
				    if (dist > avg_distance) dist = avg_distance;

				    String statProp = clusterName+">stats("+feature+","+value+")";
				    
					if (!cfvProps.containsKey(statProp))
						cfvProps.setProperty(statProp, "0.0");
					// cfvProps.setProperty(statProp, Double.toString( Double.valueOf(cfvProps.getProperty(statProp))+(max_radius-dist)/max_radius ) );
					//if (weight)
					//   cfvProps.setProperty(statProp, Double.toString( Double.valueOf(cfvProps.getProperty(statProp)) + (avg_distance-dist)/avg_distance ) );
					//else 
					cfvProps.setProperty(statProp, Double.toString( Double.valueOf(cfvProps.getProperty(statProp)) + 1.0 ) );
					if (!cfvCount.containsKey(statProp))
						cfvCount.setProperty(statProp, "0.0");
					cfvCount.setProperty(statProp, Double.toString( Double.valueOf(cfvCount.getProperty(statProp)) + 1.0 ) );
				}
				
			} // polarity = pos

		} // triples

		/*
		cCounts.forEach( (k, v) -> {
			System.out.println(k.toString() + " = " + v.toString());
		} );

		cfvProps.forEach( (k, v) -> {
			System.out.println(k.toString() + " = " + v.toString());
		} );

		cfvCount.forEach( (k, v) -> {
			if (Double.valueOf(v.toString()) > minCount)
				System.out.println(k.toString() + " = " + v.toString());
		} );

		*/
		
		//////////
		
		// System.out.println ("numEmbeds = " + Double.toString(this._numEmbeds));

		cStats = new Properties();

		cCounts.forEach( (ck, cv) -> {

			cfvContribSum = 0.0;
			cfvContribNum = 0.0;

			double cCount = Double.valueOf(cv.toString());

			if ( cCount > 0 ) cfvProps.forEach( (fk, fv) -> {
				if (fk.toString().startsWith(ck.toString()+">")) {
					
					// System.out.println( fk.toString() + " >> " +cfvCount.getProperty(fk.toString()) );
					// if (Double.valueOf(cfvCount.getProperty(fk.toString())) >= minCount) {
					
						double fvPerc = Double.valueOf(fv.toString()) / cCount;
						double fCountPerc = 0.0;
						/*
						for (String p : this.properties().keySet()) if (p.startsWith("stats(")) {
							if (fk.toString().endsWith(">"+p)) {
								fCountPerc = Double.valueOf(this.properties().get(p)); // / (double)this._numEmbeds;
							}
						}
						*/
						for (String fv2 : theFeatureValueStats.keySet()) {
							if (fk.toString().endsWith(">"+fv2)) {
								// fCountPerc = Double.valueOf(this.properties().get(fv2)); // / (double)this._numEmbeds;
								fCountPerc = Double.valueOf(this.theFeatureValueStats.get(fv2).getValue(0)); // / (double)this._numEmbeds;
							}
						}
	
						// / 2.0 because of norm Radius
						//if (!weight)
						//	fCountPerc = fCountPerc / (double)this._numEmbeds;
						//else
							fCountPerc = fCountPerc / (double)this._numEmbeds / 2.0;
						
						if (fvPerc > fCountPerc) {
							// cfvContribSum = cfvContribSum + (fvPerc - fCountPerc / 2.0);
							cfvContribSum = cfvContribSum + (fvPerc - fCountPerc);
							cfvContribNum = cfvContribNum + 1.0; 
							// System.out.println (fk.toString() + " > " + Double.toString(fvPerc)  + " comp " + Double.toString(fCountPerc));
						}

					// }
				}
			} );
			
			double cScore = cfvContribSum / cfvContribNum;
			// System.out.println (ck.toString() + " >score> " + Double.toString(cScore));

			cFinalScoreSum += cScore*Math.log10(cCount);
			cFinalScoreLog += Math.log10(cCount);

			//cFinalScoreSum_norm += ;
			//cFinalScoreLog_norm += ;

		} );
		
		cFinalScore = cFinalScoreSum / cFinalScoreLog;
		//cFinalScore_norm = cFinalScoreSum_norm / cFinalScoreLog_norm;
		// System.out.println ("clusteracy = " + Double.toString(cFinalScore));
		// KConsole.lastDouble(cFinalScore);
		this.clusteracy = cFinalScore; 
		//this.clusteracy_norm = cFinalScore_norm; 
		return cFinalScore;
	}

}
