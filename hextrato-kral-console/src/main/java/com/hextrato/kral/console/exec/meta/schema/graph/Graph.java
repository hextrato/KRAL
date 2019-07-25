package com.hextrato.kral.console.exec.meta.schema.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.util.exception.KException;

public class Graph implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("graph"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "property", "foreach", "save", "hextract"}; }

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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KConsole.println("schema.name = " + graph.getSchema().getName());
		KConsole.println("graph.name = " + graph.getName());
		for (String property : graph.properties().keySet()) if (!property.endsWith("_")) {
			KConsole.println("property: "+property+" = " + graph.properties().get(property));
		}
		KConsole.metadata("Graph", graph.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		String searchGraphName = clmd.getVar("graph");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String graphName : schema.graphs().theList().keySet()) {
			KGraph graph = schema.graphs().getGraph(graphName);
			if (("["+graph.getName()+"]").contains(searchGraphName)) {
				schema.graphs().setCurrent(graphName);
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
		String searchGraphName = clmd.getVar("graph");
		for (String graphName : schema.graphs().theList().keySet()) {
			KGraph graph = schema.graphs().getGraph(graphName);
			if (("["+graph.getName()+"]").contains(searchGraphName)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+schema.graphs().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+schema.graphs().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+schema.graphs().getPropertySize("_name_")+"s", "_name_");
					for (String property : graph.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+schema.graphs().getPropertySize(property)+"s", property);
					}
					/*
					String output = String.format("%-20s %-20s", "schema","graph");
					for (String k : graph.properties().keySet().toArray(new String[0])) {
						output = output + String.format(" %-20s", k);
					}
					*/
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_schema_")+"s", graph.properties().get("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_uid_")+"s", graph.properties().get("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+schema.tabulars().getPropertySize("_name_")+"s", graph.properties().get("_name_"));
				for (String property : graph.properties().keySet()) if (!property.endsWith("_")) {
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
			case "hypergraph":
				graph.setHypergraph(value.toUpperCase().equals("TRUE"));
				break;
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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = graph.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		String graphName = KCFinder.which(clmd, "graph");
		schema.graphs().setCurrent(graphName);
		KConsole.feedback("Graph '"+graphName+"' selected");
		KConsole.metadata("Graph", graphName);
		return true;
	}

}

