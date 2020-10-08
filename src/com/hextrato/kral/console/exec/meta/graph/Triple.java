package com.hextrato.kral.console.exec.meta.graph;


import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.schema.tabular.KRecord;
import com.hextrato.kral.core.util.exception.KException;

public class Triple implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("triple"); }

	public String[] getValidTokenSet () {
		return new String[] {"list", "create", "delete", "select", "show", "foreach", "count", "find", "save"}; 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return (new TripleCreate()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String triple = KCFinder.which(clmd, "triple");
		String uid = graph.triples().create(triple);
		KConsole.feedback("Triple '"+uid+"' created");
		KConsole.metadata("Triple", uid, triple);
		KConsole.lastString(uid); // ** NEW ** //
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String tripleUID = clmd.getVar("triple");
		if (tripleUID.equals("")) {
			KConsole.error("which triple?");
		} else {
			graph.triples().delete(tripleUID);
			KConsole.feedback("Triple '"+tripleUID+"' deleted");
			KConsole.metadata("Triple", tripleUID);
			KConsole.lastString(tripleUID); // ** NEW ** //
		}
		return true;
	}

	public static boolean doShow(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KTriple triple = KCFinder.findTriple(graph, clmd);
		KConsole.println("_.schema = " + triple.getGraph().getSchema().getName()); // ** NEW ** //
		KConsole.println("_.graph = " + triple.getGraph().getName()); // ** NEW ** //
		KConsole.println("_.split = " + triple.getSplit().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + triple.getUID()); // ** NEW ** //
		KConsole.println("_.head = " + triple.getHead()); // ** NEW ** //
		KConsole.println("_.rela = " + triple.getRela()); // ** NEW ** //
		KConsole.println("_.tail = " + triple.getTail()); // ** NEW ** //
		KConsole.println("_.pola = " + triple.getPola()); // ** NEW ** //
		KConsole.metadata("Triple", triple.getUID());
		KConsole.lastString(triple.getUID()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KTriple triple, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_SCHEMA__);
		String searchGraph = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_TABULAR__);
		String searchSplit = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_SPLIT__);
		String searchUID = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_UID__);
		String searchHead = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_HEAD__);
		String searchTail = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_TAIL__);
		String searchRela = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_RELA__);
		String searchPola = clmd.getParameter(KTriple.__INTERNAL_PROPERTY_POLA__);
		boolean match = false;
		if ( true
				&& ("["+triple.getGraph().getSchema()+"]").contains(searchSchema)
				&& ("["+triple.getGraph()+"]").contains(searchGraph)
				&& ("["+triple.getSplit()+"]").contains(searchSplit)
				&& ("["+triple.getUID()+"]").contains(searchUID)
				&& ("["+triple.getHead()+"]").contains(searchHead)
				&& ("["+triple.getTail()+"]").contains(searchTail)
				&& ("["+triple.getRela()+"]").contains(searchRela)
				&& ("["+triple.getPola()+"]").contains(searchPola)
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		for (String tripleUID : graph.triples().theList().keySet()) {
			KTriple triple = graph.triples().getTriple(tripleUID);
			if (Triple.matches(triple,clmd)) {
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
		KGraph graph = KCFinder.findGraph(schema,clmd);
		for (String tripleUID : graph.triples().theList().keySet()) {
			KTriple triple = graph.triples().getTriple(tripleUID);
			if (Triple.matches(triple,clmd)) {
				graph.triples().setCurrent(tripleUID);
				KConsole.feedback("Found: " + tripleUID);
				KConsole.lastFound(tripleUID); // ** NEW ** //
				return true;
			}
		}
		KConsole.feedback("Not found");
		KConsole.lastFound(""); // ** NEW ** //
		return true;
	}
	
	
	
	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String tripleUID : graph.triples().theList().keySet()) {
			KTriple triple = graph.triples().getTriple(tripleUID);
			if (Triple.matches(triple,clmd)) {
				graph.triples().setCurrent(tripleUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No triples found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		long count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		/*
		("["+triple.getHead().getType()+"]").contains(searchTripleHeadType)
		&&
		("["+triple.getTail().getType()+"]").contains(searchTripleTailType)
		&&
		("["+(triple.getPola()?"+":"-")+"]").contains(searchTriplePola)
		*/
		for (String tripleUID : graph.triples().theList().keySet()) {
			KTriple triple = graph.triples().getTriple(tripleUID);
			if (Triple.matches(triple,clmd)) {
				if (count==0) {
					String output = "";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_SCHEMA__)+"s", KTriple.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_GRAPH__)+"s", KTriple.__INTERNAL_PROPERTY_GRAPH__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_SPLIT__)+"s", KTriple.__INTERNAL_PROPERTY_SPLIT__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_UID__)+"s", KTriple.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_POLA__)+"s", KTriple.__INTERNAL_PROPERTY_POLA__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_HEAD__)+"s", KTriple.__INTERNAL_PROPERTY_HEAD__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_RELA__)+"s", KTriple.__INTERNAL_PROPERTY_RELA__);
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_TAIL__)+"s", KTriple.__INTERNAL_PROPERTY_TAIL__);
					KConsole.output (output);
				}
				String output = "";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_SCHEMA__)+"s", triple.getGraph().getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_TABULAR__)+"s", triple.getGraph().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_SPLIT__)+"s", triple.getSplit().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_UID__)+"s", triple.getUID());

				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_POLA__)+"s", triple.getPola());
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_HEAD__)+"s", triple.getHead());
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_RELA__)+"s", triple.getRela());
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize(KTriple.__INTERNAL_PROPERTY_TAIL__)+"s", triple.getTail());
				KConsole.output(output);
				count++;
			}
		}
		if (count==0)
			KConsole.feedback("No triples found");
		else
			KConsole.feedback(count+" found");
		return true;
	}
	
	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KTriple triple = KCFinder.findTriple(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = "";
		switch (property) {
		case "_.schema":	value = triple.getGraph().getSchema().getName();	break;
		case "_.graph": 	value = triple.getGraph().getName();				break;
		case "_.split": 	value = triple.getSplit().getName();				break;
		case "_.uid":		value = triple.getUID();							break;
		case "_.head":		value = triple.getHead().getName();					break;
		case "_.tail":		value = triple.getTail().getName();					break;
		case "_.headtype":	value = triple.getHead().getType();					break;
		case "_.tailtype":	value = triple.getTail().getType();					break;
		case "_.rela":		value = triple.getRela().getName();					break;
		case "_.pola":		value = (triple.getPola()?"+":"-");					break;
			default: throw new KException ("Invalid property '"+property+"'");
		}
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String tripleUID = KCFinder.which(clmd, "uid");
		graph.triples().setCurrent(tripleUID);
		KConsole.feedback("Triple '"+tripleUID+"' selected");
		KConsole.metadata("Triple", tripleUID);
		KConsole.lastString(tripleUID); // ** NEW ** //
		return true;
	}

	/*
	public static boolean doExec(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KTriple triple = KCFinder.findTriple(graph, clmd);
		KConsole.println("schema.name = " + triple.getGraph().getSchema().getName());
		KConsole.println("graph.name = " + triple.getGraph().getName());
		KConsole.println("split.name = " + triple.getSplit().getName());
		KConsole.println("triple.uid = " + triple.getUID());
		KConsole.println("triple.headtype = " + triple.getHead().getType());
		KConsole.println("triple.head = " + triple.getHead().getName());
		KConsole.println("triple.relation = " + triple.getRela().getName());
		KConsole.println("triple.tailtype = " + triple.getTail().getType());
		KConsole.println("triple.tail = " + triple.getTail().getName());
		KConsole.println("triple.polarity = " + (triple.getPola()?"+":"-"));
		KConsole.metadata("Triple", triple.getUID());
		return true;
	}
	*/
}

