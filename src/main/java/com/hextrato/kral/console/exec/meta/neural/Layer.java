package com.hextrato.kral.console.exec.meta.neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.hyper.KSpace;
import com.hextrato.kral.core.schema.neural.KLayer;
import com.hextrato.kral.core.schema.neural.KNeural;
import com.hextrato.kral.core.util.exception.KException;

public class Layer implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("layer"); }

	public String[] getValidTokenSet () { return new String[] {"create", "list", "select", "desc", "foreach", "save", "set", "weights", "biases", "feed", "back", "learn", "validate", "test", "accuracy"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("layer").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String layerName = KCFinder.which(clmd, "layer");
		if (!neural.layers().exists(layerName) && KConsole.isMetadataAutocreate())
			return (new LayerCreate()).exec(clmd);
		else
			return (new LayerSelect()).exec(clmd);
	}
	
	//
	// Basic Methods
	//
	
	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String layerName = KCFinder.which(clmd, "layer");
		String layerOper = KCFinder.which(clmd, "oper");
		neural.layers().create(layerName,layerOper);
		KConsole.feedback("Layer '"+layerName+"' created");
		KConsole.metadata("Layer", layerName);
		return true;
	}
	
	public static boolean doCreateAfter(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String layerName = KCFinder.which(clmd, "layer");
		String layerOper = KCFinder.which(clmd, "oper");
		String layerAfter = KCFinder.which(clmd, "after");
		neural.layers().create(layerName,layerOper);
		neural.layers().getLayer(layerName).setPrevLayer(layerAfter);
		KConsole.feedback("Layer '"+layerName+"' created");
		KConsole.metadata("Layer", layerName);
		return true;
	}
	
	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		KConsole.println("neural.name = " + layer.getNeural().getName());
		KConsole.println("layer.name = " + layer.getName());
		KConsole.println("layer.oper = " + layer.getOper());
		KConsole.println("layer.input.size = " + layer.getInputSize());
		KConsole.println("layer.output.size = " + layer.getOutputSize());
		try { KConsole.println("layer.learn.rate = " + layer.getLearningRate()); } catch (KException e) {} finally {};
		try { KConsole.println("layer.misslearn.factor = " + layer.getMisslearningFactor()); } catch (KException e) {} finally {};
		try { KConsole.println("layer.activation.function = " + layer.getActivationFunction()); } catch (KException e) {} finally {};
		try { KConsole.println("layer.weight.matrix = \n" + layer.theWeights().toString()); } catch (Exception e) {} finally {};
		try { KConsole.println("layer.bias.vector = \n" + layer.theBiases().toString()); } catch (Exception e) {} finally {};
		try { KConsole.println("layer.gradient.out = \n" + layer.theGradientOut().toString()); } catch (Exception e) {} finally {};
		try { KConsole.println("layer.gradient.net = \n" + layer.theGradientNet().toString()); } catch (Exception e) {} finally {};
		try { KConsole.println("layer.gradient.in = \n" + layer.theGradientIn().toString()); } catch (Exception e) {} finally {};
		String chain = ""; 
		KLayer chainLayer = layer;
		while (chainLayer != null && chain.length() <= 1000) {
			chain = chain + " => " + chainLayer.getName();
			chainLayer = chainLayer.getNextLayer();
		}
		KConsole.println("layer.chain" + chain);
		KConsole.metadata("Layer", layer.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String searchLayerName = clmd.getVar("layer");
		String searchLayerOper = clmd.getParameter("oper");
		String searchLayerAfter = clmd.getParameter("after");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String layerName : neural.layers().theList().keySet()) {
			KLayer layer = neural.layers().getLayer(layerName);
			if (	("["+layer.getName()+"]").contains(searchLayerName)
					&&
					("["+layer.getOper()+"]").contains(searchLayerOper)
					&&
					( searchLayerOper.isEmpty() || (layer.getPrevLayer() != null && ("["+layer.getPrevLayer().getName()+"]").contains(searchLayerAfter)) )
					) {
				neural.layers().setCurrent(layerName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No layers found");
		return true;
	}
	
	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String searchLayerName = clmd.getVar("layer");
		String searchLayerOper = clmd.getParameter("oper");
		String searchLayerPrev = clmd.getParameter("prev");
		String searchLayerNext = clmd.getParameter("next");
		for (String layerName : neural.layers().theList().keySet()) {
			KLayer layer = neural.layers().getLayer(layerName);
			if (	("["+layer.getName()+"]").contains(searchLayerName)
					&&
					("["+layer.getOper()+"]").contains(searchLayerOper)
					&&
					( searchLayerOper.isEmpty() || (layer.getPrevLayer() != null && ("["+layer.getPrevLayer().getName()+"]").contains(searchLayerPrev)) )
					&&
					( searchLayerOper.isEmpty() || (layer.getNextLayer() != null && ("["+layer.getNextLayer().getName()+"]").contains(searchLayerNext)) )
					) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+neural.layers().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+neural.layers().getPropertySize("_neural_")+"s", "_neural_");
					output = output + "\t";
					output = output + String.format("%-"+neural.layers().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+neural.layers().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+neural.layers().getPropertySize("_oper_")+"s", "_oper_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+neural.layers().getPropertySize("_schema_")+"s", layer.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+neural.layers().getPropertySize("_neural_")+"s", layer.getProperty("_neural_"));
				output = output + "\t";
				output = output + String.format("%-"+neural.layers().getPropertySize("_uid_")+"s", layer.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+neural.layers().getPropertySize("_name_")+"s", layer.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+neural.layers().getPropertySize("_oper_")+"s", layer.getProperty("_oper_"));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No layers found");
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = layer.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		String layerName = KCFinder.which(clmd, "layer");
		neural.layers().setCurrent(layerName);
		KConsole.feedback("Layer '"+layerName+"' selected");
		KConsole.metadata("Layer", layerName);
		return true;
	}

	//
	// Setup Methods
	//

	public static boolean doSet(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String hyperparam = KCFinder.which(clmd, "hyperparam").toLowerCase();
		String hypervalue = KCFinder.which(clmd, "hypervalue").toLowerCase();
		switch (hyperparam) {
		
		case "input.size":
		case "input_size":
		case "input":
		case "is":
			layer.setInputSize(Integer.valueOf(hypervalue));
			break;

		case "output.size":
		case "output_size":
		case "output":
		case "os":
			layer.setOutputSize(Integer.valueOf(hypervalue));
			break;

		case "activation.function":
		case "activation_function":
		case "function":
		case "af":
			layer.setActivationFunction(hypervalue);
			break;

		case "learning.rate":
		case "learning_rate":
		case "learn.rate":
		case "learn_rate":
		case "lr":
			layer.setsetLearningRate(Double.valueOf(hypervalue));
			break;

		case "misslearning.factor":
		case "misslearning_factor":
		case "misslearn.factor":
		case "misslearn_factor":
		case "mlf":
			layer.setMisslearningFactor(Double.valueOf(hypervalue));
			break;

		case "biases.vector":
		case "bias.vector":
		case "biases":
		case "bias":
			layer.setBiases(hypervalue);
			break;

		case "weights.matrix":
		case "weight.matrix":
		case "weights":
		case "weight":
			layer.setWeights(hypervalue);
			break;

		default:
			throw new KException ("Invalid hyperparam '"+hyperparam+"'");
		}
		return true;
	}
	
	public static boolean doBiases(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String action = KCFinder.which(clmd, "action");
		switch (action.toLowerCase()) {
		case "normal":
			layer.setBiasesNormal();
			break;
		case "random":
			layer.setBiasesRandom();
			break;
		case "null":
			layer.setBiasesNull();
			break;
		default:
			throw new KException("Invalid action '"+action+"'");
		}
		KConsole.metadata("Layer", layer.getName());
		return true;
	}
	
	public static boolean doWeights(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String action = KCFinder.which(clmd, "action");
		switch (action.toLowerCase()) {
		case "normal":
			layer.setWeightsNormal();
			break;
		case "random":
			layer.setWeightsRandom();
			break;
		case "null":
			layer.setWeightsNull();
			break;
		default:
			throw new KException("Invalid action '"+action+"'");
		}
		KConsole.metadata("Layer", layer.getName());
		return true;
	}

	//
	// Run/Learning Methods
	//

	public static boolean doFeed(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String inputValues = KCFinder.which(clmd, "values").trim();
		
		// HXConsole.feedback("Layer '"+layer.getName()+"' fed");
		KConsole.metadata("Layer", layer.getName());

		if (inputValues.startsWith("[") && inputValues.endsWith("]")) {
			DVector inputVector = new DVector(layer.getInputSize()); 
			inputVector.setValues(inputValues);
			layer.feed(inputVector);
			String feedback = layer.theInputValues().toString();
			// HXConsole.feedback("layer."+layer.getName()+".input  = "+layer.theInputValues().toString());
			KLayer nextLayer;
			while ( (nextLayer = layer.getNextLayer()) != null) {
				//HXConsole.feedback("layer."+nextLayer.getName()+".input  = ");
				//HXConsole.feedback(nextLayer.theInputValues().toString());
				layer = nextLayer;
			}
			feedback = feedback + " => " + layer.theOutputValues().toString();
			//HXConsole.feedback("layer."+layer.getName()+".output = "+layer.theOutputValues().toString()); 
			KConsole.feedback(feedback);
		} else {
			throw new KException("Invalid values format in: "+inputValues);
		}
		return true;
	}
	
	public static boolean doBack(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String targetValues = KCFinder.which(clmd, "values").trim();
		// HXConsole.feedback("Layer '"+layer.getName()+"' fed");
		KConsole.metadata("Layer", layer.getName());
		if (targetValues.startsWith("[") && targetValues.endsWith("]")) {
			DVector targetVector = new DVector(layer.getOutputSize()); 
			targetVector.setValues(targetValues);
			layer.back(targetVector);
		} else {
			throw new KException("Invalid target values format in: "+targetValues);
		}
		return true;
	}

	public static boolean doLearnRepeat(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String times = KCFinder.which(clmd, "times").trim();
		String input = KCFinder.which(clmd, "input").trim();
		String output = KCFinder.which(clmd, "output").trim();
		
		int repeatTimes = 0;
		try { repeatTimes = Integer.valueOf(times); }
		catch (Exception e) {
			throw new KException("Invalid number of times to repeat");
		}

		// HextraSchema schema = CLFinder.findSchema(clmd);
		KSpace iSpace = schema.hyperspace().getSpace(input);
		if (iSpace == null) throw new KException("Invalid input space ["+input+"]");
		KSpace oSpace = schema.hyperspace().getSpace(output);
		if (oSpace == null) throw new KException("Invalid output space ["+output+"]");
		
		// HXConsole.feedback("Layer '"+layer.getName()+"' fed");
		KConsole.metadata("Layer", layer.getName());

		List<String> vectorKeys = new ArrayList<String>(iSpace.vectors().theNames().keySet());
		for (int i = 0; i < repeatTimes; i++) {
			Collections.shuffle(vectorKeys);
			for (String iVectorName : vectorKeys) { // iSpace.vectors().theList().keySet()) {
				// Hextrato.message(iVectorUID);
				if (oSpace.vectors().exists(iVectorName)) {
					DVector iVector = iSpace.vectors().getVector(iVectorName).getValues();
					DVector oVector = oSpace.vectors().getVector(iVectorName).getValues();
					// Hextrato.message("i = "+iVector.toString() + " o = "+oVector.toString());
					layer.learn(iVector,oVector);
				}
			}
		}
		return true;
	}
	
	public static boolean doTest(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String input = KCFinder.which(clmd, "input").trim();
		String output = KCFinder.which(clmd, "output").trim();
		
		// HextraSchema schema = CLFinder.findSchema(clmd);
		KSpace iSpace = schema.hyperspace().getSpace(input);
		KSpace oSpace = schema.hyperspace().getSpace(output);
		
		// HXConsole.feedback("Layer '"+layer.getName()+"' fed");
		KConsole.metadata("Layer", layer.getName());
		layer.test(iSpace.vectors(),oSpace.vectors());
		KConsole.feedback("Layer error = " + layer.getLastError());
		
		return true;
	}

	public static boolean doAccuracy(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KNeural neural = KCFinder.findNeural(schema, clmd);
		KLayer layer = KCFinder.findLayer(neural, clmd);
		String input = KCFinder.which(clmd, "input").trim();
		String output = KCFinder.which(clmd, "output").trim();
		
		// HextraSchema schema = CLFinder.findSchema(clmd);
		KSpace iSpace = schema.hyperspace().getSpace(input);
		KSpace oSpace = schema.hyperspace().getSpace(output);
		
		// HXConsole.feedback("Layer '"+layer.getName()+"' fed");
		KConsole.metadata("Layer", layer.getName());
		layer.accuracy(iSpace.vectors(),oSpace.vectors());
		KConsole.feedback("Layer accuracy = " + layer.getLastAccuracy());
		
		return true;
	}
}

