package com.hextrato.kral.console.exec.meta.hyper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.type.TVector;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.hyper.KSpace;
import com.hextrato.kral.core.util.exception.KException;

public class Space implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("space"); }

    public String[] getValidTokenSet () { return new String[] {"create", "list", "select", "desc", "foreach", "count", "find", "save", "hextract", "probscore","stats","auprc"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("space").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String spaceName = KCFinder.which(clmd, "space");
		if (!schema.hyperspace().exists(spaceName) && KConsole.isMetadataAutocreate())
			return (new SpaceCreate()).exec(clmd);
		else
			return (new SpaceSelect()).exec(clmd);
	}
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String spaceName = KCFinder.which(clmd, "space");
		String spaceDims = KCFinder.which(clmd, "dimensionality");
		schema.hyperspace().create(spaceName,Integer.valueOf(spaceDims));
		KConsole.feedback("Space '"+spaceName+"' created");
		KConsole.metadata("Space", spaceName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KConsole.println("_.schema = " + space.getSchema().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + space.getUID()); // ** NEW ** //
		KConsole.println("_.name = " + space.getName()); // ** NEW ** //
		KConsole.println("_.dimensionality = " + space.getDims());
		KConsole.metadata("Space", space.getName());
		KConsole.lastString(space.getName()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KSpace space, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KSpace.__INTERNAL_PROPERTY_SCHEMA__);
		String searchUID = clmd.getParameter(KSpace.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KSpace.__INTERNAL_PROPERTY_NAME__);
		String searchDims = clmd.getParameter(KSpace.__INTERNAL_PROPERTY_DIMENSIONALITY__);
		if (searchDims.equals("")) searchDims = "0";
		boolean match = false;
		if ( true
				&& ("["+space.getSchema()+"]").contains(searchSchema)
				&& ("["+space.getUID()+"]").contains(searchUID)
				&& ("["+space.getName()+"]").contains(searchName)
				&&
				(space.getDims() == Integer.valueOf(searchDims) || searchDims.equals("0"))
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String spaceName : schema.hyperspace().theList().keySet()) {
			KSpace space = schema.hyperspace().getSpace(spaceName);
			if (Space.matches(space,clmd)) {
				count++;
			}
		}
		KConsole.feedback("Count = " + count); // ** NEW ** //
		KConsole.lastInteger(count); // ** NEW ** //
		return true;
	}

	public static boolean doFind(KCMetadata clmd) throws KException {
		KConsole.lastFound(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		for (String spaceUID : schema.hyperspace().theList().keySet()) {
			KSpace space = schema.hyperspace().getSpace(spaceUID);
			if (Space.matches(space,clmd)) {
				schema.hyperspace().setCurrent(spaceUID);
				KConsole.feedback("Found: " + spaceUID);
				KConsole.lastFound(spaceUID); // ** NEW ** //
				return true;
			}
		}
		KConsole.feedback("Not found");
		KConsole.lastFound(""); // ** NEW ** //
		return true;
	}

	public static boolean doForEach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String spaceUID : schema.hyperspace().theList().keySet()) {
			KSpace space = schema.hyperspace().getSpace(spaceUID);
			if (Space.matches(space,clmd)) {
				schema.hyperspace().setCurrent(spaceUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No spaces found");
		return true;
	}

	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		space.hextract(fileName);
		KConsole.feedback("Space '"+space.getName()+"' hextracted");
		KConsole.metadata("Space", space.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String spaceUID : schema.hyperspace().theList().keySet()) {
			KSpace space = schema.hyperspace().getSpace(spaceUID);
			if (Space.matches(space,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_SCHEMA__)+"s", KSpace.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_UID__)+"s", KSpace.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_NAME__)+"s", KSpace.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_DIMENSIONALITY__)+"s", KSpace.__INTERNAL_PROPERTY_DIMENSIONALITY__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_SCHEMA__)+"s", space.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_UID__)+"s", space.getUID());
				output = output + "\t";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_NAME__)+"s", space.getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.hyperspace().getPropertySize(KSpace.__INTERNAL_PROPERTY_DIMENSIONALITY__)+"s", space.getDims());
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No spaces found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = space.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}

	public static boolean doStats(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String varPrefix = clmd.getVar("varPrefix");
		space.stats(varPrefix,space.getDims());
		KConsole.feedback("Space '"+space.getName()+"' stats");
		KConsole.metadata("Space", space.getName());
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		String spaceName = KCFinder.which(clmd, "space");
		schema.hyperspace().setCurrent(spaceName);
		KConsole.feedback("Space '"+spaceName+"' selected");
		KConsole.metadata("Space", spaceName);
		KConsole.lastString(spaceName); // ** NEW ** //
		return true;
	}
	
	static TVector typeVector = new TVector();

	public static boolean doProbscoreArcdist(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String pVector = KCFinder.which(clmd, "vector");
		DVector vector = typeVector.valueOf(pVector);
		String pTarget = KCFinder.which(clmd, "class");
		DVector target = typeVector.valueOf(pTarget);
		String labelsName = KCFinder.which(clmd, "labelspace");
		KSpace labels = schema.hyperspace().getSpace(labelsName);
		String dist = KCFinder.which(clmd, "dist");
		DVector distances = typeVector.valueOf(dist); 
		DVector scores = space.probScoreArcDist(vector,target,labels,distances);
		KConsole.feedback("Space '"+space.getName()+"' probscored");
		KConsole.metadata("Space", space.getName());
		KConsole.lastVector(scores);
		return true;
	}

	public static boolean doProbscoreL2norm(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		String pVector = KCFinder.which(clmd, "vector");
		DVector vector = typeVector.valueOf(pVector);
		String pTarget = KCFinder.which(clmd, "class");
		DVector target = typeVector.valueOf(pTarget);
		String labelsName = KCFinder.which(clmd, "labelspace");
		KSpace labels = schema.hyperspace().getSpace(labelsName);
		String dist = KCFinder.which(clmd, "dist");
		DVector distances = typeVector.valueOf(dist); 
		DVector scores = space.probScoreL2Norm(vector,target,labels,distances);
		KConsole.feedback("Space '"+space.getName()+"' probscored");
		KConsole.metadata("Space", space.getName());
		KConsole.lastVector(scores);
		return true;
	}

	public static boolean doAuprcTrain(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);

		String pTargetClass = KCFinder.which(clmd, "targetclass");
		String pTargetPosition = KCFinder.which(clmd, "targetposition");
		double targetClass = Double.valueOf(pTargetClass);
		int targetPosition = Integer.valueOf(pTargetPosition);
		
		String pTrainSpace = KCFinder.which(clmd, "trainspace");
		String pTrainLabel = KCFinder.which(clmd, "trainlabel");
		KSpace trainSpace = schema.hyperspace().getSpace(pTrainSpace);
		KSpace trainLabel = schema.hyperspace().getSpace(pTrainLabel);
		
		String pValidSpace = KCFinder.which(clmd, "validspace");
		String pValidLabel = KCFinder.which(clmd, "validlabel");
		KSpace validSpace = schema.hyperspace().getSpace(pValidSpace); 
		KSpace validLabel = schema.hyperspace().getSpace(pValidLabel);
		
		space.auprcTrain(targetClass,targetPosition,trainSpace,trainLabel,validSpace,validLabel);
		
		KConsole.feedback("Space '"+space.getName()+"' auprc'd");
		KConsole.metadata("Space", space.getName());
		// KConsole.lastVector(scores);
		return true;
	}

	public static boolean doAuprcTest(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);

		String pTargetClass = KCFinder.which(clmd, "targetclass");
		String pTargetPosition = KCFinder.which(clmd, "targetposition");
		double targetClass = Double.valueOf(pTargetClass);
		int targetPosition = Integer.valueOf(pTargetPosition);
		
		String pTestSpace = KCFinder.which(clmd, "testspace");
		String pTestLabel = KCFinder.which(clmd, "testlabel");
		KSpace testSpace = schema.hyperspace().getSpace(pTestSpace);
		KSpace testLabel = schema.hyperspace().getSpace(pTestLabel);
		
		space.auprcTest(targetClass,targetPosition,testSpace,testLabel);
		
		KConsole.feedback("Space '"+space.getName()+"' auprc'd");
		KConsole.metadata("Space", space.getName());
		// KConsole.lastVector(scores);
		return true;
	}

	public static boolean doTest(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KSpace space = KCFinder.findSpace(schema, clmd);
		KConsole.feedback("Space '"+space.getName()+"'");
		String pVector = KCFinder.which(clmd, "vector");
		DVector vector = typeVector.valueOf(pVector);
		KConsole.feedback("Vector '"+vector.toString()+"'");
		String pTarget = KCFinder.which(clmd, "class");
		DVector target = typeVector.valueOf(pTarget);
		KConsole.feedback("Target '"+target.toString()+"'");
		String labelsName = KCFinder.which(clmd, "labelspace");
		KSpace labels = schema.hyperspace().getSpace(labelsName);
		KConsole.feedback("Labels '"+labels.getName()+"'");
		return true;
	}
	
	
}

