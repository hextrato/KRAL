package com.hextrato.kral.core.schema.hyper;

import java.io.BufferedWriter;
import java.io.IOException;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.data.util.UFiller;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

public class KSpace extends AMetaNamedObject {
	
	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public KSpace (KSchema schema, int dims) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		if (dims < 1) throw new KException("Invalid dimensionality ["+dims+"]");
		this._schema = schema;
		this.properties().declare("_schema_", "String");
		this.properties().set("_schema_", schema.getName());
		this.properties().declare("_dims_", "Integer");
		this.properties().set("_dims_", Integer.toString(dims));
	}
	
	public int getDims() throws NumberFormatException, KException { 
		return Integer.valueOf(this.properties().get("_dims_")); 
	}
	
	private KVectorSet _vectorSet = new KVectorSet(this);
	public KVectorSet vectors() { return _vectorSet; }
	
	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("space %s create %d", this.getName(),this.getDims()) );
			bf.newLine();
			this.vectors().hextract(bf);
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

	//
	// Probabilistic Scores
	//
	static double similarityFactor4TPlabel = Math.sqrt(2)/4; 
	
	public DVector probScoreArcDist (DVector vector, DVector target, KSpace labels, DVector distances) throws KException {
		return probScore (vector, target, labels, distances, true);
	}

	public DVector probScoreL2Norm (DVector vector, DVector target, KSpace labels, DVector distances) throws KException {
		return probScore (vector, target, labels, distances, false);
	}

	public DVector probScore (DVector vector, DVector target, KSpace labels, DVector distances, boolean isArcDistance) throws KException {
	
		if (distances == null || distances.size() == 0)
			return (new DVector (1).setValue(0,-1) );
		DVector wDistanceTN = new DVector(distances.size());
		UFiller.fillWith(wDistanceTN, 0);
		DVector wDistanceTP = new DVector(distances.size());
		UFiller.fillWith(wDistanceTP, 0);
		DVector wSupport = new DVector(distances.size());
		UFiller.fillWith(wSupport, 0);

		for (String vectorID : this.vectors().theNames().keySet()) {
			DVector spaceVector = this.vectors().getVector(vectorID).getValues();
			DVector labelVector = labels.vectors().getVector(vectorID).getValues();
			
			// double thisDistance = spaceVector.distance(vector);
			double thisVecDistance;
			if (isArcDistance)
				thisVecDistance = spaceVector.arc(vector);
			else
				thisVecDistance = spaceVector.distance(vector);
			
			//if (thisVecDistance > Math.acos(0)) 
			//	System.out.println(vector.toString()+ " : "+thisVecDistance);
			
			for (int i = 0; i < distances.size(); i++) {
				double maxdistance = distances.getValue(i);
				if (thisVecDistance <= maxdistance) {
					double invdistance = maxdistance - thisVecDistance;
					double labeldistance = labelVector.distance(target);
					double labelTPfactor = (1 - labeldistance);
					if (labeldistance < similarityFactor4TPlabel) {
						//wDistanceTP += invdistance * labelTPfactor;
						wDistanceTP.setValue(i , wDistanceTP.getValue(i) + invdistance * labelTPfactor);
					} else {
						//wDistanceTN += invdistance;
						wDistanceTN.setValue(i , wDistanceTN.getValue(i) + invdistance);
					}
					wSupport.setValue(i, wSupport.getValue(i)+1);
				}
			} // for
		}
		DVector scores = new DVector(distances.size()*2);
		double distanceTP; 
		double distanceTN; 
		for (int i = 0; i < distances.size(); i++) {
			distanceTP = wDistanceTP.getValue(i);
			distanceTN = wDistanceTN.getValue(i);
			if (distanceTP + distanceTN <= 0) {
				scores.setValue(i, -1);
			} else {
				scores.setValue(i, distanceTP / (distanceTP + distanceTN) );
			}
			scores.setValue(distances.size()+i, wSupport.getValue(i));
		}
		// double score = wDistanceTP / (wDistanceTP + wDistanceTN);
        return scores;
	}

	//
	// Stats
	//
	public void stats (String varPrefix, int dimensionality) throws KException {
		DVector minPos = new DVector(dimensionality); minPos.fillWith(-1);
		DVector maxPos = new DVector(dimensionality); maxPos.fillWith(-1);
		DVector avgPos = new DVector(dimensionality); avgPos.fillWith(0);
		DVector cntPos = new DVector(dimensionality); cntPos.fillWith(0);
		DVector minNeg = new DVector(dimensionality); minNeg.fillWith(+1);
		DVector maxNeg = new DVector(dimensionality); maxNeg.fillWith(+1);
		DVector avgNeg = new DVector(dimensionality); avgNeg.fillWith(0);
		DVector cntNeg = new DVector(dimensionality); cntNeg.fillWith(0);

		for (String vectorName : this.vectors().theNames().keySet()) {
			KVector vector = this.vectors().getVector(vectorName);
			for (int pos = 0; pos < dimensionality; pos++) {
				double value = vector.getValues().getValue(pos);
				if (value >= 0) {
					// POS
					if (value < minPos.getValue(pos) || minPos.getValue(pos) < 0) minPos.setValue(pos,value);  
					if (value > maxPos.getValue(pos)) maxPos.setValue(pos,value);
					cntPos.setValue(pos, cntPos.getValue(pos)+1);
					avgPos.setValue(pos, avgPos.getValue(pos)+value);
				} else {
					// NEG
					if (value < minNeg.getValue(pos)) minNeg.setValue(pos,value);  
					if (value > maxNeg.getValue(pos) || maxNeg.getValue(pos) > 0) maxNeg.setValue(pos,value);  
					cntNeg.setValue(pos, cntNeg.getValue(pos)+1);
					avgNeg.setValue(pos, avgNeg.getValue(pos)+value);
				}
			}			
		}
		for (int pos = 0; pos < dimensionality; pos++) {
			if (avgPos.getValue(pos) > 0) 
				avgPos.setValue(pos, avgPos.getValue(pos) / cntPos.getValue(pos));
			else 
				avgPos.setValue(pos, -1);
			if (avgNeg.getValue(pos) < 0) 
				avgNeg.setValue(pos, avgNeg.getValue(pos) / cntNeg.getValue(pos));
			else 
				avgNeg.setValue(pos, +1);
		}
		String minPosVar = varPrefix+"_"+"MINPOS";
		String maxPosVar = varPrefix+"_"+"MAXPOS";
		String avgPosVar = varPrefix+"_"+"AVGPOS";
		String minNegVar = varPrefix+"_"+"MINNEG";
		String maxNegVar = varPrefix+"_"+"MAXNEG";
		String avgNegVar = varPrefix+"_"+"AVGNEG";
		if (!KConsole.vars().exits(minPosVar)) KConsole.vars().declare(minPosVar, "Vector");
		if (!KConsole.vars().exits(maxPosVar)) KConsole.vars().declare(maxPosVar, "Vector");
		if (!KConsole.vars().exits(avgPosVar)) KConsole.vars().declare(avgPosVar, "Vector");
		if (!KConsole.vars().exits(minNegVar)) KConsole.vars().declare(minNegVar, "Vector");
		if (!KConsole.vars().exits(maxNegVar)) KConsole.vars().declare(maxNegVar, "Vector");
		if (!KConsole.vars().exits(avgNegVar)) KConsole.vars().declare(avgNegVar, "Vector");
		KConsole.vars().set(minPosVar, minPos.toString());
		KConsole.vars().set(maxPosVar, maxPos.toString());
		KConsole.vars().set(avgPosVar, avgPos.toString());
		KConsole.vars().set(minNegVar, minNeg.toString());
		KConsole.vars().set(maxNegVar, maxNeg.toString());
		KConsole.vars().set(avgNegVar, avgNeg.toString());
	}

	//
	// Area Under PR Curve 
	//

	private final static double THRESHOLD_SPLIT_FACTOR = 25.0;

	// TESTING ...
	public void auprcTest (double targetValue, int labelPosition, KSpace testSpace, KSpace testLabel) throws KException {
		if (this.getDims() != testSpace.getDims()/2 ) {
			throw new KException("Incompatible dimensions with test space");
		}
		
		String varPrefix = testSpace.getName(); 
		testSpace.stats(varPrefix,testSpace.getDims());
		String minPosVar = varPrefix+"_"+"MINPOS";
		String maxPosVar = varPrefix+"_"+"MAXPOS";
		String avgPosVar = varPrefix+"_"+"AVGPOS";
		//String minNegVar = varPrefix+"_"+"MINNEG";
		//String maxNegVar = varPrefix+"_"+"MAXNEG";
		//String avgNegVar = varPrefix+"_"+"AVGNEG";
		DVector minPos = new DVector(testSpace.getDims()).setValues(KConsole.vars().get(minPosVar));
		DVector maxPos = new DVector(testSpace.getDims()).setValues(KConsole.vars().get(maxPosVar));
		DVector avgPos = new DVector(testSpace.getDims()).setValues(KConsole.vars().get(avgPosVar));
		DVector medPos = new DVector(this.getDims());
		//DVector minNeg = new DVector(this.getDims()).setValues(KConsole.vars().get(minNegVar));
		//DVector maxNeg = new DVector(this.getDims()).setValues(KConsole.vars().get(maxNegVar));
		//DVector avgNeg = new DVector(this.getDims()).setValues(KConsole.vars().get(avgNegVar));

		// if (1==1) return;
		
		DVector splitFactors = new DVector(this.getDims());
		DVector currentThresholds = new DVector(this.getDims());
		// DVector nextThresholds = new DVector(this.getDims());

		DVector TP = new DVector(this.getDims());
		DVector FP = new DVector(this.getDims());
		DVector FN = new DVector(this.getDims());
		DVector P = new DVector(this.getDims());
		DVector R = new DVector(this.getDims());
		// DVector F5 = new DVector(this.getDims());
		// DVector F1 = new DVector(this.getDims());
		// DVector F2 = new DVector(this.getDims());

		DVector aucLastR = new DVector(this.getDims()).fillWith(1);
		DVector aucLastP = new DVector(this.getDims()).fillWith(0);
		DVector aucSumP = new DVector(this.getDims()).fillWith(0);
		DVector aucSumR = new DVector(this.getDims()).fillWith(0);
		DVector aucCount = new DVector(this.getDims()).fillWith(0);
		DVector auc = new DVector(this.getDims()).fillWith(0);

		DVector tF5 = new DVector(this.getDims());
		DVector tF1 = new DVector(this.getDims());
		DVector tF2 = new DVector(this.getDims());
		DVector tF5threshold = new DVector(this.getDims()).setValues( this.vectors().getVector("T-0.5").getValues() );
		DVector tF1threshold = new DVector(this.getDims()).setValues( this.vectors().getVector("T-1.0").getValues() );
		DVector tF2threshold = new DVector(this.getDims()).setValues( this.vectors().getVector("T-2.0").getValues() );

		for (int pos = 0; pos < this.getDims(); pos ++) {
			splitFactors.setValue(pos, Math.min(maxPos.getValue(pos)-avgPos.getValue(pos), avgPos.getValue(pos)-minPos.getValue(pos)) / THRESHOLD_SPLIT_FACTOR );
			if (maxPos.getValue(pos) >= 0) {
				currentThresholds.setValue(pos, minPos.getValue(pos)+splitFactors.getValue(pos));
				medPos.setValue(pos, (minPos.getValue(pos)+maxPos.getValue(pos))/2);
			}
		}

		/////System.out.println("splitFactors      = "+splitFactors.toString());
		/////System.out.println("initialThresholds = "+currentThresholds.toString());

		//KConsole.println("splitFactors="+splitFactors);
		//KConsole.println("currentThresholds="+currentThresholds);
		//KConsole.println("medPos="+medPos);

		// F0.5-score
		TP.fillWith(0);
		FP.fillWith(0);
		FN.fillWith(0);
		for (String vectorName : testSpace.vectors().theNames().keySet()) {
			DVector scores = testSpace.vectors().getVector(vectorName).getValues();
			double label = testLabel.vectors().getVector(vectorName).getValues().getValue(labelPosition);
			for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && tF5threshold.getValue(pos) >= 0) {
				double score = scores.getValue(pos);
				// ? if (score >= 0) {
					if (targetValue > 0) { // tF5threshold.getValue(pos)) {
						if (label >= tF5threshold.getValue(pos) && score >= tF5threshold.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
						if (label >= tF5threshold.getValue(pos) && score < tF5threshold.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
						if (label < tF5threshold.getValue(pos) && score >= tF5threshold.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
					} else {
						if (label <= tF5threshold.getValue(pos) && score <= tF5threshold.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
						if (label <= tF5threshold.getValue(pos) && score > tF5threshold.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
						if (label > tF5threshold.getValue(pos) && score <= tF5threshold.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
					}
				// ? } else FN.setValue(pos, FN.getValue(pos)+1 );
			}
		}
		for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && tF5threshold.getValue(pos) >= 0) {
			// Precision and Recall
			if (TP.getValue(pos) + FP.getValue(pos) > 0) P.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); else P.setValue(pos, 0); 
			if (TP.getValue(pos) + FN.getValue(pos) > 0) R.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); else R.setValue(pos, 1);
			if (P.getValue(pos) + R.getValue(pos) > 0) tF5.setValue(pos, (1.0+0.5*0.5) * (P.getValue(pos) * R.getValue(pos) ) / (0.5*0.5+P.getValue(pos))*R.getValue(pos) ); else tF5.setValue(pos, 0);
		}
		this.vectors().create( testSpace.getName()+":"+"F-0.5:PRECIS" , P.toString() );
		this.vectors().create( testSpace.getName()+":"+"F-0.5:RECALL" , R.toString() );

		// F1.0-score
		TP.fillWith(0);
		FP.fillWith(0);
		FN.fillWith(0);
		for (String vectorName : testSpace.vectors().theNames().keySet()) {
			DVector scores = testSpace.vectors().getVector(vectorName).getValues();
			double label = testLabel.vectors().getVector(vectorName).getValues().getValue(labelPosition);
			for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && tF1threshold.getValue(pos) >= 0) {
				double score = scores.getValue(pos);
				// ? if (score >= 0) {
					if (targetValue > 0) { // tF1threshold.getValue(pos)) {
						if (label >= tF1threshold.getValue(pos) && score >= tF1threshold.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
						if (label >= tF1threshold.getValue(pos) && score < tF1threshold.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
						if (label < tF1threshold.getValue(pos) && score >= tF1threshold.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
					} else {
						if (label <= tF1threshold.getValue(pos) && score <= tF1threshold.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
						if (label <= tF1threshold.getValue(pos) && score > tF1threshold.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
						if (label > tF1threshold.getValue(pos) && score <= tF1threshold.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
					}
				// ? } else FN.setValue(pos, FN.getValue(pos)+1 );
			}
		}
		for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && tF5threshold.getValue(pos) >= 0) {
			// Precision and Recall
			if (TP.getValue(pos) + FP.getValue(pos) > 0) P.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); else P.setValue(pos, 0); 
			if (TP.getValue(pos) + FN.getValue(pos) > 0) R.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); else R.setValue(pos, 1);
			if (P.getValue(pos) + R.getValue(pos) > 0) tF1.setValue(pos, (1.0+1.0*1.0) * (P.getValue(pos) * R.getValue(pos) ) / (1.0*1.0+P.getValue(pos))*R.getValue(pos) ); else tF1.setValue(pos, 0);
		}
		this.vectors().create( testSpace.getName()+":"+"F-1.0:PRECIS" , P.toString() );
		this.vectors().create( testSpace.getName()+":"+"F-1.0:RECALL" , R.toString() );

		// F2.0-score
		TP.fillWith(0);
		FP.fillWith(0);
		FN.fillWith(0);
		for (String vectorName : testSpace.vectors().theNames().keySet()) {
			DVector scores = testSpace.vectors().getVector(vectorName).getValues();
			double label = testLabel.vectors().getVector(vectorName).getValues().getValue(labelPosition);
			for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && tF2threshold.getValue(pos) >= 0) {
				double score = scores.getValue(pos);
				// ? if (score >= 0) {
					if (targetValue > 0) { // tF2threshold.getValue(pos)) {
						if (label >= tF2threshold.getValue(pos) && score >= tF2threshold.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
						if (label >= tF2threshold.getValue(pos) && score < tF2threshold.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
						if (label < tF2threshold.getValue(pos) && score >= tF2threshold.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
					} else {
						if (label <= tF2threshold.getValue(pos) && score <= tF2threshold.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
						if (label <= tF2threshold.getValue(pos) && score > tF2threshold.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
						if (label > tF2threshold.getValue(pos) && score <= tF2threshold.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
					}
				// ? } else FN.setValue(pos, FN.getValue(pos)+1 );
			}
		}
		for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && tF5threshold.getValue(pos) >= 0) {
			// Precision and Recall
			if (TP.getValue(pos) + FP.getValue(pos) > 0) P.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); else P.setValue(pos, 0); 
			if (TP.getValue(pos) + FN.getValue(pos) > 0) R.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); else R.setValue(pos, 1);
			if (P.getValue(pos) + R.getValue(pos) > 0) tF2.setValue(pos, (1.0+2.0*2.0) * (P.getValue(pos) * R.getValue(pos) ) / (2.0*2.0+P.getValue(pos))*R.getValue(pos) ); else tF2.setValue(pos, 0);
		}
		this.vectors().create( testSpace.getName()+":"+"F-2.0:PRECIS" , P.toString() );
		this.vectors().create( testSpace.getName()+":"+"F-2.0:RECALL" , R.toString() );

		// auroc
		int countSplit = 0;
		boolean any = true;
		aucLastP.fillWith(0);
		aucLastR.fillWith(1);
		while (any && countSplit < 100*THRESHOLD_SPLIT_FACTOR) 
		{
			countSplit++; any=false;
			//KConsole.message("--------------------------------"+countSplit);
			//KConsole.message("currentThresholds = "+ currentThresholds.toString());
			//KConsole.message("--------------------------------");
			// ---
			// TST
			// ---
			TP.fillWith(0);
			FP.fillWith(0);
			FN.fillWith(0);
			for (String vectorName : testSpace.vectors().theNames().keySet()) {
				DVector scores = testSpace.vectors().getVector(vectorName).getValues();
				double label = testLabel.vectors().getVector(vectorName).getValues().getValue(labelPosition);
				for (int pos = 0; pos < this.getDims(); pos ++) if (splitFactors.getValue(pos) > 0 && maxPos.getValue(pos) >= 0 && currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
					any = true;
					double score = scores.getValue(pos);
					// ? if (score >= 0) {
						if (targetValue > 0) { // currentThresholds.getValue(pos)) {
							if (label >= currentThresholds.getValue(pos) && score >= currentThresholds.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
							if (label >= currentThresholds.getValue(pos) && score < currentThresholds.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
							if (label < currentThresholds.getValue(pos) && score >= currentThresholds.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
						} else {
							if (label <= currentThresholds.getValue(pos) && score <= currentThresholds.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
							if (label <= currentThresholds.getValue(pos) && score > currentThresholds.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
							if (label > currentThresholds.getValue(pos) && score <= currentThresholds.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
						}
					// ? }  else FN.setValue(pos, FN.getValue(pos)+1 );
				}
			}
			//KConsole.message("aucLastP: "+ aucLastP.toString());
			//KConsole.message("aucLastR: "+ aucLastR.toString());
			for (int pos = 0; pos < this.getDims(); pos ++) if (maxPos.getValue(pos) >= 0 && currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
				if (TP.getValue(pos) > 0 && FP.getValue(pos) > 0 && FN.getValue(pos) > 0) {
					aucCount.setValue(pos, aucCount.getValue(pos)+1);
					// Precision and Recall
					// if (FP.getValue(pos) > 0) P.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); else P.setValue(pos, 1); 
					// if (FN.getValue(pos) > 0) R.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); else R.setValue(pos, 1);
					P.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); 
					R.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); 
					
					//if (P.getValue(pos) + R.getValue(pos) > 0) F5.setValue(pos, (1.0+0.5*0.5) * (P.getValue(pos) * R.getValue(pos) ) / (0.5*0.5+P.getValue(pos))*R.getValue(pos) ); else F5.setValue(pos, 0);
					//if (P.getValue(pos) + R.getValue(pos) > 0) F1.setValue(pos, (1.0+1.0*1.0) * (P.getValue(pos) * R.getValue(pos) ) / (1.0*1.0+P.getValue(pos))*R.getValue(pos) ); else F1.setValue(pos, 0);
					//if (P.getValue(pos) + R.getValue(pos) > 0) F2.setValue(pos, (1.0+2.0*2.0) * (P.getValue(pos) * R.getValue(pos) ) / (2.0*2.0+P.getValue(pos))*R.getValue(pos) ); else F2.setValue(pos, 0);
					
					// AUC parameters
					// if (P.getValue(pos) > aucLastP.getValue(pos)) {
					//if (pos == 2) {
					//	KConsole.message("*** P[2]: "+ P.getValue(pos));
					//	KConsole.message("*** R[2]: "+ R.getValue(pos));
					//}
					if (P.getValue(pos) != aucLastP.getValue(pos) || R.getValue(pos) > aucLastR.getValue(pos)) {
						aucSumP.setValue ( pos, aucSumP.getValue(pos) + Math.abs( P.getValue(pos) - aucLastP.getValue(pos) ) );
						aucSumR.setValue ( pos, aucSumR.getValue(pos) + Math.abs( (R.getValue(pos) + aucLastR.getValue(pos)) * (P.getValue(pos) - aucLastP.getValue(pos)) / 2 ) );
						//if (aucSumP.getValue(pos) == 0) auc.setValue(pos, 0); 
						//else auc.setValue(pos, aucSumR.getValue(pos) / aucSumP.getValue(pos));
						// auc.setValue ( pos, auc.getValue(pos) + Math.abs( (R.getValue(pos) + aucLastR.getValue(pos)) * (P.getValue(pos) - aucLastP.getValue(pos)) / 2 ) );				
						aucLastP.setValue(pos, P.getValue(pos));
						aucLastR.setValue(pos, R.getValue(pos));
					}
					
					//if (pos == 14) {
					//	System.out.println("Th\t"+currentThresholds.getValue(pos)+"\tTP\t"+TP.getValue(pos)+"\tFP\t"+FP.getValue(pos)+"\tFN\t"+FN.getValue(pos)+"\tP\t"+P.getValue(pos)+"\tR\t"+R.getValue(pos)+"\n");
					//}
				}
			}
			//KConsole.message("currentP: "+ P.toString());
			//KConsole.message("currentR: "+ R.toString());
			//KConsole.message("auc: "+ auc.toString());
			//KConsole.message("prec   = "+ P.toString());
			//KConsole.message("reca   = "+ R.toString());
			//KConsole.message("F1     = "+ F1.toString());
			//KConsole.message("F1*    = "+ bestF1.toString());
			//KConsole.message("TH (F1)= "+ currentThresholds.toString());
			for (int pos = 0; pos < this.getDims(); pos ++) {
				currentThresholds.setValue(pos, currentThresholds.getValue(pos) + splitFactors.getValue(pos) );
			}
		}
		P.fillWith(1);
		R.fillWith(0);
		for (int pos = 0; pos < this.getDims(); pos ++) {
			aucSumP.setValue ( pos, aucSumP.getValue(pos) + Math.abs( P.getValue(pos) - aucLastP.getValue(pos) ) );
			aucSumR.setValue ( pos, aucSumR.getValue(pos) + Math.abs( (R.getValue(pos) + aucLastR.getValue(pos)) * (P.getValue(pos) - aucLastP.getValue(pos)) / 2 ) );
			if (aucSumP.getValue(pos) == 0 || aucCount.getValue(pos) < 3) auc.setValue(pos, 0); 
			else auc.setValue(pos, aucSumR.getValue(pos) / aucSumP.getValue(pos));
		}
		
		/////System.out.println("finalThresholds = "+currentThresholds.toString());
		
		
		KConsole.message("countSplit="+countSplit);

		//KConsole.message("================================");
		//KConsole.message("F0.5   = "+ tF5.toString());
		//KConsole.message("F1     = "+ tF1.toString());
		//KConsole.message("F2     = "+ tF2.toString());
		//KConsole.message("auc    = "+ auc.toString());

		/*
		this.vectors().create( "F-0.5" , bestF5.toString() );
		this.vectors().create( "F-1.0" , bestF1.toString() );
		this.vectors().create( "F-2.0" , bestF2.toString() );
		this.vectors().create( "T-0.5" , bestF5threshold.toString() );
		this.vectors().create( "T-1.0" , bestF1threshold.toString() );
		this.vectors().create( "T-2.0" , bestF2threshold.toString() );
		*/
		this.vectors().create( testSpace.getName()+":"+"F-0.5" , tF5.toString() );
		this.vectors().create( testSpace.getName()+":"+"F-1.0" , tF1.toString() );
		this.vectors().create( testSpace.getName()+":"+"F-2.0" , tF2.toString() );
		this.vectors().create( testSpace.getName()+":"+"AUPRC" , auc.toString() );
		KConsole.lastVector(auc);
	}
	
	// TRAINING ...
	public void auprcTrain (double targetValue, int labelPosition, KSpace trainSpace, KSpace trainLabel, KSpace validSpace, KSpace validLabel) throws KException {
		if (!this.vectors().theList().isEmpty()) {
			throw new KException("Space is not empty; clear vectors...");
		}
		if (this.getDims() != trainSpace.getDims()/2 ) {
			throw new KException("Incompatible dimensions with training space");
		}
		if (this.getDims() != validSpace.getDims()/2 ) {
			throw new KException("Incompatible dimensions with validation space");
		}

		String valPrefix = validSpace.getName(); 
		validSpace.stats(valPrefix,validSpace.getDims());
		String minVLDPosVar = valPrefix+"_"+"MINPOS";
		String maxVLDPosVar = valPrefix+"_"+"MAXPOS";
		String avgVLDPosVar = valPrefix+"_"+"AVGPOS";
		DVector minVLDPos = new DVector(validSpace.getDims()).setValues(KConsole.vars().get(minVLDPosVar));
		DVector maxVLDPos = new DVector(validSpace.getDims()).setValues(KConsole.vars().get(maxVLDPosVar));
		DVector avgVLDPos = new DVector(validSpace.getDims()).setValues(KConsole.vars().get(avgVLDPosVar));
		DVector minVLDPosSco = new DVector(this.getDims()).copyValuesFromTo(minVLDPos, 0, 0);
		DVector maxVLDPosSco = new DVector(this.getDims()).copyValuesFromTo(maxVLDPos, 0, 0);
		DVector avgVLDPosSco = new DVector(this.getDims()).copyValuesFromTo(avgVLDPos, 0, 0);
		DVector minVLDPosSup = new DVector(this.getDims()).copyValuesFromTo(minVLDPos, this.getDims(), 0);
		DVector maxVLDPosSup = new DVector(this.getDims()).copyValuesFromTo(maxVLDPos, this.getDims(), 0);
		DVector avgVLDPosSup = new DVector(this.getDims()).copyValuesFromTo(avgVLDPos, this.getDims(), 0);
		this.vectors().create( validSpace.getName()+":"+"minPosSco" , minVLDPosSco.toString() );
		this.vectors().create( validSpace.getName()+":"+"maxPosSco" , maxVLDPosSco.toString() );
		this.vectors().create( validSpace.getName()+":"+"avgPosSco" , avgVLDPosSco.toString() );
		this.vectors().create( validSpace.getName()+":"+"minPosSup" , minVLDPosSup.toString() );
		this.vectors().create( validSpace.getName()+":"+"maxPosSup" , maxVLDPosSup.toString() );
		this.vectors().create( validSpace.getName()+":"+"avgPosSup" , avgVLDPosSup.toString() );

		String varPrefix = trainSpace.getName(); 
		trainSpace.stats(varPrefix,trainSpace.getDims());
		String minPosVar = varPrefix+"_"+"MINPOS";
		String maxPosVar = varPrefix+"_"+"MAXPOS";
		String avgPosVar = varPrefix+"_"+"AVGPOS";
		//String minNegVar = varPrefix+"_"+"MINNEG";
		//String maxNegVar = varPrefix+"_"+"MAXNEG";
		//String avgNegVar = varPrefix+"_"+"AVGNEG";
		DVector minPos = new DVector(trainSpace.getDims()).setValues(KConsole.vars().get(minPosVar));
		DVector maxPos = new DVector(trainSpace.getDims()).setValues(KConsole.vars().get(maxPosVar));
		DVector avgPos = new DVector(trainSpace.getDims()).setValues(KConsole.vars().get(avgPosVar));
		DVector medPos = new DVector(this.getDims());
		//DVector minNeg = new DVector(this.getDims()).setValues(KConsole.vars().get(minNegVar));
		//DVector maxNeg = new DVector(this.getDims()).setValues(KConsole.vars().get(maxNegVar));
		//DVector avgNeg = new DVector(this.getDims()).setValues(KConsole.vars().get(avgNegVar));

		DVector minLRNPosSco = new DVector(this.getDims()).copyValuesFromTo(minPos, 0, 0);
		DVector maxLRNPosSco = new DVector(this.getDims()).copyValuesFromTo(maxPos, 0, 0);
		DVector avgLRNPosSco = new DVector(this.getDims()).copyValuesFromTo(avgPos, 0, 0);
		DVector minLRNPosSup = new DVector(this.getDims()).copyValuesFromTo(minPos, this.getDims(), 0);
		DVector maxLRNPosSup = new DVector(this.getDims()).copyValuesFromTo(maxPos, this.getDims(), 0);
		DVector avgLRNPosSup = new DVector(this.getDims()).copyValuesFromTo(avgPos, this.getDims(), 0);
		this.vectors().create( trainSpace.getName()+":"+"minPosSco" , minLRNPosSco.toString() );
		this.vectors().create( trainSpace.getName()+":"+"maxPosSco" , maxLRNPosSco.toString() );
		this.vectors().create( trainSpace.getName()+":"+"avgPosSco" , avgLRNPosSco.toString() );
		this.vectors().create( trainSpace.getName()+":"+"minPosSup" , minLRNPosSup.toString() );
		this.vectors().create( trainSpace.getName()+":"+"maxPosSup" , maxLRNPosSup.toString() );
		this.vectors().create( trainSpace.getName()+":"+"avgPosSup" , avgLRNPosSup.toString() );

		// KConsole.runLine("var list");

		DVector splitFactors = new DVector(this.getDims());
		DVector currentThresholds = new DVector(this.getDims());
		// DVector nextThresholds = new DVector(this.getDims());

		DVector TP = new DVector(this.getDims());
		DVector FP = new DVector(this.getDims());
		DVector FN = new DVector(this.getDims());
		DVector P = new DVector(this.getDims());
		DVector R = new DVector(this.getDims());
		DVector F5 = new DVector(this.getDims());
		DVector F1 = new DVector(this.getDims());
		DVector F2 = new DVector(this.getDims());

		DVector vldP = new DVector(this.getDims());
		DVector vldR = new DVector(this.getDims());
		DVector vldF5 = new DVector(this.getDims());
		DVector vldF1 = new DVector(this.getDims());
		DVector vldF2 = new DVector(this.getDims());

		DVector aucLastR = new DVector(this.getDims()).fillWith(1);
		DVector aucLastP = new DVector(this.getDims()).fillWith(0);
		DVector aucSumP = new DVector(this.getDims()).fillWith(0);
		DVector aucSumR = new DVector(this.getDims()).fillWith(0);
		DVector auc = new DVector(this.getDims()).fillWith(0);

		DVector bestF5 = new DVector(this.getDims());
		DVector bestF1 = new DVector(this.getDims());
		DVector bestF2 = new DVector(this.getDims());
		DVector bestF5threshold = new DVector(this.getDims());
		DVector bestF1threshold = new DVector(this.getDims());
		DVector bestF2threshold = new DVector(this.getDims());

		for (int pos = 0; pos < this.getDims(); pos ++) {
			splitFactors.setValue(pos, Math.min(maxPos.getValue(pos)-avgPos.getValue(pos), avgPos.getValue(pos)-minPos.getValue(pos)) / THRESHOLD_SPLIT_FACTOR );
			currentThresholds.setValue(pos, minPos.getValue(pos)+splitFactors.getValue(pos));
			// nextThresholds.setValue(pos, -1);
			medPos.setValue(pos, (minPos.getValue(pos)+maxPos.getValue(pos))/2);
		}

		int countSplit = 0;
		boolean any = true;
		while (any && countSplit < 100*THRESHOLD_SPLIT_FACTOR) 
		{
			countSplit++; any=false;
			//KConsole.message("--------------------------------"+countSplit);
			//KConsole.message("currentThresholds = "+ currentThresholds.toString());
			//KConsole.message("--------------------------------");
			// ---
			// LRN
			// ---
			TP.fillWith(0);
			FP.fillWith(0);
			FN.fillWith(0);
			for (String vectorName : trainSpace.vectors().theNames().keySet()) {
				DVector scores = trainSpace.vectors().getVector(vectorName).getValues();
				double label = trainLabel.vectors().getVector(vectorName).getValues().getValue(labelPosition);
				for (int pos = 0; pos < this.getDims(); pos ++) if (splitFactors.getValue(pos) > 0 && currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
					any = true;
					double score = scores.getValue(pos);
					// ? if (score >= 0) {
						if (targetValue > 0) { // currentThresholds.getValue(pos)) {
							if (label >= currentThresholds.getValue(pos) && score >= currentThresholds.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
							if (label >= currentThresholds.getValue(pos) && score < currentThresholds.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
							if (label < currentThresholds.getValue(pos) && score >= currentThresholds.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
						} else {
							if (label <= currentThresholds.getValue(pos) && score <= currentThresholds.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
							if (label <= currentThresholds.getValue(pos) && score > currentThresholds.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
							if (label > currentThresholds.getValue(pos) && score <= currentThresholds.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
						}
					// ? } else FN.setValue(pos, FN.getValue(pos)+1 );
				}
			}
			// Precision and Recall
			for (int pos = 0; pos < this.getDims(); pos ++) if (currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
				if (TP.getValue(pos) + FP.getValue(pos) > 0) P.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); else P.setValue(pos, 0); 
				if (TP.getValue(pos) + FN.getValue(pos) > 0) R.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); else R.setValue(pos, 1);
				if (P.getValue(pos) + R.getValue(pos) > 0) F5.setValue(pos, (1.0+0.5*0.5) * (P.getValue(pos) * R.getValue(pos) ) / (0.5*0.5+P.getValue(pos))*R.getValue(pos) ); else F5.setValue(pos, 0);
				if (P.getValue(pos) + R.getValue(pos) > 0) F1.setValue(pos, (1.0+1.0*1.0) * (P.getValue(pos) * R.getValue(pos) ) / (1.0*1.0+P.getValue(pos))*R.getValue(pos) ); else F1.setValue(pos, 0);
				if (P.getValue(pos) + R.getValue(pos) > 0) F2.setValue(pos, (1.0+2.0*2.0) * (P.getValue(pos) * R.getValue(pos) ) / (2.0*2.0+P.getValue(pos))*R.getValue(pos) ); else F2.setValue(pos, 0);
			}
			// ---
			// VLD
			// ---
			TP.fillWith(0);
			FP.fillWith(0);
			FN.fillWith(0);
			for (String vectorName : validSpace.vectors().theNames().keySet()) {
				DVector scores = validSpace.vectors().getVector(vectorName).getValues();
				double label = validLabel.vectors().getVector(vectorName).getValues().getValue(labelPosition);
				for (int pos = 0; pos < this.getDims(); pos ++) if (currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
					any = true;
					double score = scores.getValue(pos);
					// ? if (score >= 0) {
						if (targetValue > 0) { // currentThresholds.getValue(pos)) {
							if (label >= currentThresholds.getValue(pos) && score >= currentThresholds.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
							if (label >= currentThresholds.getValue(pos) && score < currentThresholds.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
							if (label < currentThresholds.getValue(pos) && score >= currentThresholds.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
						} else {
							if (label <= currentThresholds.getValue(pos) && score <= currentThresholds.getValue(pos) ) TP.setValue(pos, TP.getValue(pos)+1 );
							if (label <= currentThresholds.getValue(pos) && score > currentThresholds.getValue(pos) ) FN.setValue(pos, FN.getValue(pos)+1 );
							if (label > currentThresholds.getValue(pos) && score <= currentThresholds.getValue(pos) ) FP.setValue(pos, FP.getValue(pos)+1 );
						}
					// ? } else FN.setValue(pos, FN.getValue(pos)+1 );
				}
			}
			//KConsole.message("aucLastP: "+ aucLastP.toString());
			//KConsole.message("aucLastR: "+ aucLastR.toString());
			for (int pos = 0; pos < this.getDims(); pos ++) if (currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
				// Precision and Recall
				if (TP.getValue(pos) + FP.getValue(pos) > 0) vldP.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FP.getValue(pos))); else vldP.setValue(pos, 0); 
				if (TP.getValue(pos) + FN.getValue(pos) > 0) vldR.setValue(pos, TP.getValue(pos) / (TP.getValue(pos) + FN.getValue(pos))); else vldR.setValue(pos, 1);
				if (vldP.getValue(pos) + vldR.getValue(pos) > 0) vldF5.setValue(pos, (1.0+0.5*0.5) * (vldP.getValue(pos) * vldR.getValue(pos) ) / (0.5*0.5+vldP.getValue(pos))*vldR.getValue(pos) ); else vldF5.setValue(pos, 0);
				if (vldP.getValue(pos) + vldR.getValue(pos) > 0) vldF1.setValue(pos, (1.0+1.0*1.0) * (vldP.getValue(pos) * vldR.getValue(pos) ) / (1.0*1.0+vldP.getValue(pos))*vldR.getValue(pos) ); else vldF1.setValue(pos, 0);
				if (vldP.getValue(pos) + vldR.getValue(pos) > 0) vldF2.setValue(pos, (1.0+2.0*2.0) * (vldP.getValue(pos) * vldR.getValue(pos) ) / (2.0*2.0+vldP.getValue(pos))*vldR.getValue(pos) ); else vldF2.setValue(pos, 0);
				// AUC parameters
				// if (P.getValue(pos) > aucLastP.getValue(pos)) {
				//if (pos == 0) {
				//	KConsole.message("*** P["+pos+"]: "+ vldP);
				//	KConsole.message("*** R["+pos+"]: "+ vldR);
				//}
				if (vldP.getValue(pos) != aucLastP.getValue(pos)) {
					aucSumP.setValue ( pos, aucSumP.getValue(pos) + Math.abs( vldP.getValue(pos) - aucLastP.getValue(pos) ) );
					aucSumR.setValue ( pos, aucSumR.getValue(pos) + Math.abs( (vldR.getValue(pos) + aucLastR.getValue(pos)) * (vldP.getValue(pos) - aucLastP.getValue(pos)) / 2 ) );
					if (aucSumP.getValue(pos) == 0) auc.setValue(pos, 0); 
					else auc.setValue(pos, aucSumR.getValue(pos) / aucSumP.getValue(pos));
					// auc.setValue ( pos, auc.getValue(pos) + Math.abs( (R.getValue(pos) + aucLastR.getValue(pos)) * (P.getValue(pos) - aucLastP.getValue(pos)) / 2 ) );				
					aucLastP.setValue(pos, vldP.getValue(pos));
					aucLastR.setValue(pos, vldR.getValue(pos));
				}
			}
			//KConsole.message("currentP: "+ vldP.toString());
			//KConsole.message("currentR: "+ vldR.toString());
			//KConsole.message("auc: "+ auc.toString());
			
			// Best F-scores
			for (int pos = 0; pos < this.getDims(); pos ++) if (currentThresholds.getValue(pos) <= maxPos.getValue(pos) - splitFactors.getValue(pos)) {
				// F-1.0
				if (vldF1.getValue(pos) >= bestF1.getValue(pos)) {
				// if (F1.getValue(pos) >= bestF1.getValue(pos) && vldF1.getValue(pos) >= bestF1.getValue(pos)) {
				// if ( (F1.getValue(pos) > bestF1.getValue(pos) || vldF1.getValue(pos) > bestF1.getValue(pos)) ) {
					if ( (vldF1.getValue(pos) > bestF1.getValue(pos)) || currentThresholds.getValue(pos) <= medPos.getValue(pos) ) {
					// if ( (F1.getValue(pos) > bestF1.getValue(pos) && vldF1.getValue(pos) > bestF1.getValue(pos)) || currentThresholds.getValue(pos) <= medPos.getValue(pos) ) {
						bestF1.setValue(pos, vldF1.getValue(pos));
						bestF1threshold.setValue(pos, currentThresholds.getValue(pos));
					}
				}

				//}
				// F-0.5
				if (vldF5.getValue(pos) >= bestF5.getValue(pos)) {
				// if (F5.getValue(pos) >= bestF5.getValue(pos) && vldF5.getValue(pos) >= bestF5.getValue(pos)) {
				// if ( (F5.getValue(pos) > bestF5.getValue(pos) || vldF5.getValue(pos) > bestF5.getValue(pos)) ) {
					if ( (vldF5.getValue(pos) > bestF5.getValue(pos)) || currentThresholds.getValue(pos) <= 1.0 * medPos.getValue(pos) / 2.0 ) {
					//if ( (F5.getValue(pos) > bestF5.getValue(pos) && vldF5.getValue(pos) > bestF5.getValue(pos)) || currentThresholds.getValue(pos) <= 1.0 * medPos.getValue(pos) / 2.0 ) {
						bestF5.setValue(pos, vldF5.getValue(pos));
						bestF5threshold.setValue(pos, currentThresholds.getValue(pos));
					}
				}
				//}
				// F-2.0
				if (vldF2.getValue(pos) >= bestF2.getValue(pos)) {
				// if (F2.getValue(pos) >= bestF2.getValue(pos) && vldF2.getValue(pos) >= bestF2.getValue(pos)) {
				// if ( (F2.getValue(pos) > bestF2.getValue(pos) || vldF2.getValue(pos) > bestF2.getValue(pos)) ) {
					if ( (vldF2.getValue(pos) > bestF2.getValue(pos)) || currentThresholds.getValue(pos) <= 3.0 * medPos.getValue(pos) / 2.0 ) {
					// if ( (F2.getValue(pos) > bestF2.getValue(pos) && vldF2.getValue(pos) > bestF2.getValue(pos)) || currentThresholds.getValue(pos) <= 3.0 * medPos.getValue(pos) / 2.0 ) {
						bestF2.setValue(pos, vldF2.getValue(pos));
						bestF2threshold.setValue(pos, currentThresholds.getValue(pos));
					}
				}
				//}
			}
			
			
			//KConsole.message("prec   = "+ P.toString());
			//KConsole.message("reca   = "+ R.toString());
			//KConsole.message("F0.5(L)= "+ F5.toString());
			//KConsole.message("F0.5(V)= "+ vldF5.toString());
			//KConsole.message("F1  (L)= "+ F1.toString());
			//KConsole.message("F1  (V)= "+ vldF1.toString());
			//KConsole.message("F1*    = "+ bestF1.toString());
			//KConsole.message("F2  (L)= "+ F2.toString());
			//KConsole.message("F2  (V)= "+ vldF2.toString());
			//KConsole.message("TH (F1)= "+ currentThresholds.toString());
			//KConsole.message("TH*(F1)= "+ bestF1threshold.toString());

			// next thresholds
			for (int pos = 0; pos < this.getDims(); pos ++) {
				currentThresholds.setValue(pos, currentThresholds.getValue(pos) + splitFactors.getValue(pos) );
			}
		}
		KConsole.message("countSplit="+countSplit);

		//KConsole.message("================================");
		//KConsole.message("bestF0.5   = "+ bestF5.toString());
		//KConsole.message("thresholds = "+ bestF5threshold.toString());
		//KConsole.message("bestF1     = "+ bestF1.toString());
		//KConsole.message("thresholds = "+ bestF1threshold.toString());
		//KConsole.message("bestF2     = "+ bestF2.toString());
		//KConsole.message("thresholds = "+ bestF2threshold.toString());
		//KConsole.message("auc        = "+ auc.toString());
	
		//this.vectors().create( trainSpace.getName()+":"+"minNeg" , minNeg.toString() );
		//this.vectors().create( trainSpace.getName()+":"+"maxNeg" , maxNeg.toString() );
		//this.vectors().create( trainSpace.getName()+":"+"avgNeg" , avgNeg.toString() );
		this.vectors().create( validSpace.getName()+":"+"F-0.5" , bestF5.toString() );
		this.vectors().create( validSpace.getName()+":"+"F-1.0" , bestF1.toString() );
		this.vectors().create( validSpace.getName()+":"+"F-2.0" , bestF2.toString() );
		this.vectors().create( "T-0.5" , bestF5threshold.toString() );
		this.vectors().create( "T-1.0" , bestF1threshold.toString() );
		this.vectors().create( "T-2.0" , bestF2threshold.toString() );
		this.vectors().create( validSpace.getName()+":"+"AUPRC" , auc.toString() );
		KConsole.lastVector(auc);
	}

}
