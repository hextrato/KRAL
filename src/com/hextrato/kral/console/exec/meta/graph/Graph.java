package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.util.exception.KException;

public class Graph implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("graph"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "count", "find", "select", "desc", "property", "foreach", "save", "hextract"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("graph").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String graphName = KCFinder.which(clmd, "graph");
		if (!schema.graphs().exists(graphName) && KConsole.isMetadataAutocreate())
			return (new GraphCreate()).exec(clmd);
		else
			return (new GraphSelect()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String graphName = KCFinder.which(clmd, "graph");
		schema.graphs().create(graphName);
		KConsole.feedback("Graph '"+graphName+"' created");
		KConsole.metadata("Graph", graphName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String graphName = KCFinder.which(clmd, "graph");
		schema.graphs().delete(graphName);
		KConsole.feedback("Graph '"+graphName+"' deleted");
		KConsole.metadata("Graph", graphName);
		return true;
	}
	
	public static boolean doDesc(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KConsole.println("_.schema = " + graph.getSchema().getName()); // ** NEW ** //
		KConsole.println("_.uid = " + graph.getUID()); // ** NEW ** //
		KConsole.println("_.name = " + graph.getName()); // ** NEW ** //
		for (String property : graph.properties().keySet()) if (!property.startsWith("_")) {
			KConsole.println("property."+property+" = " + graph.properties().get(property));
		}
		KConsole.metadata("Graph", graph.getName());
		KConsole.lastString(graph.getName()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KGraph graph, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KGraph.__INTERNAL_PROPERTY_SCHEMA__);
		String searchUID = clmd.getParameter(KGraph.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KGraph.__INTERNAL_PROPERTY_NAME__);
		boolean match = false;
		if ( true
				&& ("["+graph.getSchema()+"]").contains(searchSchema)
				&& ("["+graph.getUID()+"]").contains(searchUID)
				&& ("["+graph.getName()+"]").contains(searchName)
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String graphName : schema.graphs().theList().keySet()) {
			KGraph graph = schema.graphs().getGraph(graphName);
			if (Graph.matches(graph,clmd)) {
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
		for (String graphUID : schema.graphs().theList().keySet()) {
			KGraph graph = schema.graphs().getGraph(graphUID);
			if (Graph.matches(graph,clmd)) {
				schema.graphs().setCurrent(graphUID);
				KConsole.feedback("Found: " + graphUID);
				KConsole.lastFound(graphUID); // ** NEW ** //
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
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String graphUID : schema.graphs().theList().keySet()) {
			KGraph graph = schema.graphs().getGraph(graphUID);
			if (Graph.matches(graph,clmd)) {
				schema.graphs().setCurrent(graphUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No graphs found");
		return true;
	}
	
	public static boolean doHextract(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String fileName = KCFinder.which(clmd, "file");
		graph.hextract(fileName);
		KConsole.feedback("Graph '"+graph.getName()+"' hextracted");
		KConsole.metadata("Graph", graph.getName(), fileName);
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		for (String graphUID : schema.graphs().theList().keySet()) {
			KGraph graph = schema.graphs().getGraph(graphUID);
			if (Graph.matches(graph,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.graphs().getPropertySize(KGraph.__INTERNAL_PROPERTY_SCHEMA__)+"s", KGraph.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+schema.graphs().getPropertySize(KGraph.__INTERNAL_PROPERTY_UID__)+"s", KGraph.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+schema.graphs().getPropertySize(KGraph.__INTERNAL_PROPERTY_NAME__)+"s", KGraph.__INTERNAL_PROPERTY_NAME__);
					for (String property : graph.properties().keySet()) if (!property.startsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+schema.graphs().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.graphs().getPropertySize(KGraph.__INTERNAL_PROPERTY_SCHEMA__)+"s", graph.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+schema.graphs().getPropertySize(KGraph.__INTERNAL_PROPERTY_UID__)+"s", graph.getUID());
				output = output + "\t";
				output = output + String.format("%-"+schema.graphs().getPropertySize(KGraph.__INTERNAL_PROPERTY_NAME__)+"s", graph.getName());
				for (String property : graph.properties().keySet()) if (!property.startsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+schema.tabulars().getPropertySize(property)+"s", graph.properties().get(property));
				}
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No graphs found");
		return true;
	}

	public static boolean doProperty(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String property = KCFinder.which(clmd, "property");
		String value = KCFinder.which(clmd, "value");
		if (!property.endsWith("_") && !property.startsWith("_")) {
			switch(property) {
			case "typed":
				graph.setTyped(value.toUpperCase().equals("TRUE"));
				break;
			case "autocreate":
				graph.setAutocreate(value.toUpperCase().equals("TRUE"));
				break;
//			case "hypergraph":
//				graph.setHypergraph(value.toUpperCase().equals("TRUE"));
//				break;
			default:
				// graph.setProperty(property, value);
				throw new KException("Invalid property ["+property+"]");
			}
		} else {
			throw new KException("Invalid property ["+property+"]");
		}
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = graph.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		String graphName = KCFinder.which(clmd, "graph");
		schema.graphs().setCurrent(graphName);
		KConsole.feedback("Graph '"+graphName+"' selected");
		KConsole.metadata("Graph", graphName);
		KConsole.lastString(graphName); // ** NEW ** //
		return true;
	}

}

