package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.util.exception.KException;

public class Triple implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("triple"); }

	public String[] getValidTokenSet () {
		return new String[] {"list", "create", "delete", "select", "show", "foreach", "save"}; 
	}

	public boolean exec(KCMetadata clmd) throws KException {
		return (new TripleCreate()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String triple = KCFinder.which(clmd, "triple");
		String uid = graph.triples().create(triple);
		KConsole.feedback("Triple '"+uid+"' created");
		KConsole.metadata("Triple", uid, triple);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String tripleUID = clmd.getVar("triple");
		if (tripleUID.equals("")) {
			KConsole.error("which triple?");
		} else {
			graph.triples().delete(tripleUID);
			KConsole.feedback("Triple '"+tripleUID+"' deleted");
			KConsole.metadata("Triple", tripleUID);
		}
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String searchTripleUID = clmd.getVar("_uid_");
		String searchTripleSplit = clmd.getParameter("_split_");
		String searchTripleHead = clmd.getParameter("head");
		String searchTripleTail = clmd.getParameter("tail");
		String searchTripleHeadType = clmd.getParameter("headtype");
		String searchTripleTailType = clmd.getParameter("tailtype");
		String searchTripleRela = clmd.getParameter("relation");
		String searchTriplePola = clmd.getParameter("polarity");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String tripleUID : graph.triples().theList().keySet()) {
			KTriple triple = graph.triples().getTriple(tripleUID);
			if (
					("["+triple.getUID()+"]").contains(searchTripleUID)
					&&
					("["+triple.getSplit().getName()+"]").contains(searchTripleSplit)
					&&
					("["+triple.getHead().getName()+"]").contains(searchTripleHead)
					&&
					("["+triple.getTail().getName()+"]").contains(searchTripleTail)
					&&
					("["+triple.getRela().getName()+"]").contains(searchTripleRela)
					&&
					("["+triple.getHead().getType()+"]").contains(searchTripleHeadType)
					&&
					("["+triple.getTail().getType()+"]").contains(searchTripleTailType)
					&&
					("["+(triple.getPola()?"+":"-")+"]").contains(searchTriplePola)

			) {
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
		String searchTripleUID = clmd.getVar("uid");
		String searchTripleSplit = clmd.getParameter("split");
		String searchTripleHead = clmd.getParameter("head");
		String searchTripleTail = clmd.getParameter("tail");
		String searchTripleHeadType = clmd.getParameter("headtype");
		String searchTripleTailType = clmd.getParameter("tailtype");
		String searchTripleRela = clmd.getParameter("relation");
		String searchTriplePola = clmd.getParameter("polarity");
		for (String tripleUID : graph.triples().theList().keySet()) {
			KTriple triple = graph.triples().getTriple(tripleUID);
			if (
					("["+triple.getUID()+"]").contains(searchTripleUID)
					&&
					("["+triple.getSplit().getName()+"]").contains(searchTripleSplit)
					&&
					("["+triple.getHead().getName()+"]").contains(searchTripleHead)
					&&
					("["+triple.getTail().getName()+"]").contains(searchTripleTail)
					&&
					("["+triple.getRela().getName()+"]").contains(searchTripleRela)
					&&
					("["+triple.getHead().getType()+"]").contains(searchTripleHeadType)
					&&
					("["+triple.getTail().getType()+"]").contains(searchTripleTailType)
					&&
					("["+(triple.getPola()?"+":"-")+"]").contains(searchTriplePola)

			) {
				if (count==0) {
					String output = "";
					output = output + String.format("%-"+graph.triples().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_graph_")+"s", "_graph_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_split_")+"s", "_split_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_pola_")+"s", "_pola_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_head_")+"s", "_head_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_rela_")+"s", "_rela_");
					output = output + "\t";
					output = output + String.format("%-"+graph.triples().getPropertySize("_tail_")+"s", "_tail_");
					KConsole.output (output);
				}
				String output = "";
				output = output + String.format("%-"+graph.triples().getPropertySize("_schema_")+"s", triple.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_graph_")+"s", triple.getProperty("_graph_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_split_")+"s", triple.getProperty("_split_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_uid_")+"s", triple.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_pola_")+"s", triple.getProperty("_pola_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_head_")+"s", triple.getProperty("_head_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_rela_")+"s", triple.getProperty("_rela_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.triples().getPropertySize("_tail_")+"s", triple.getProperty("_tail_"));
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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KTriple triple = KCFinder.findTriple(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = "";
		switch (property) {
		case "schema":	value = triple.getGraph().getSchema().getName();	break;
		case "graph": 	value = triple.getGraph().getName();				break;
		case "split": 	value = triple.getSplit().getName();				break;
		case "uid":		value = triple.getUID();							break;
		case "head":	value = triple.getHead().getName();					break;
		case "tail":	value = triple.getTail().getName();					break;
		case "headtype":	value = triple.getHead().getType();					break;
		case "tailtype":	value = triple.getTail().getType();					break;
		case "relation":	value = triple.getRela().getName();					break;
		case "polarity":	value = (triple.getPola()?"+":"-");					break;
			default: throw new KException ("Invalid property '"+property+"'");
		}
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}
	
	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String tripleUID = KCFinder.which(clmd, "uid");
		graph.triples().setCurrent(tripleUID);
		KConsole.feedback("Triple '"+tripleUID+"' selected");
		KConsole.metadata("Triple", tripleUID);
		return true;
	}

	public static boolean doExec(KCMetadata clmd) throws KException {
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

}

