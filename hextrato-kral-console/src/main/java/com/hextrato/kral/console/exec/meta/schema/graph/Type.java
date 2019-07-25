package com.hextrato.kral.console.exec.meta.schema.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KType;
import com.hextrato.kral.core.util.exception.KException;

public class Type implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("type"); }

	public String[] getValidTokenSet () {
		return new String[] {"create", "delete", "list", "select", "desc", "foreach", "property", "save"}; 
	}

	public boolean partial(KCMetadata clmd) {
		return !(clmd.getVar("type").equals(""));
	}

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeName = KCFinder.which(clmd, "type");
		if (!graph.types().exists(typeName) && KConsole.isMetadataAutocreate())
			return (new TypeCreate()).exec(clmd);
		else
			return (new TypeSelect()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeName = KCFinder.which(clmd, "type");
		graph.types().create(typeName);
		KConsole.feedback("Type '"+typeName+"' created");
		KConsole.metadata("Type", typeName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeUID = clmd.getVar("type");
		if (typeUID.equals("")) {
			KConsole.error("which type?");
		} else {
			graph.types().delete(typeUID,false);
			KConsole.feedback("Type '"+typeUID+"' deleted");
			KConsole.metadata("Type", typeUID);
		}
		return true;
	}

	public static boolean doDeleteCascade(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeUID = clmd.getVar("type");
		if (typeUID.equals("")) {
			KConsole.error("which type?");
		} else {
			graph.types().delete(typeUID,true);
			KConsole.feedback("Type '"+typeUID+"' deleted");
			KConsole.metadata("Type", typeUID);
		}
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KType type = KCFinder.findType(graph, clmd);
		KConsole.println("schema.name = " + type.getGraph().getSchema().getName());
		KConsole.println("graph.name = " + type.getGraph().getName());
		KConsole.println("type.name = " + type.getName());
		KConsole.println("original.name = " + type.getOriginalName());
		KConsole.println("used = " + type.usedAs());
		for (String property : type.properties().keySet()) if (!property.endsWith("_")) {
			KConsole.println("property: "+property+" = " + type.properties().get(property));
		}
		KConsole.metadata("Type", type.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String searchTypeName = clmd.getVar("type");
		String searchTypeDisjoint = clmd.getParameter("disjoint");
		String searchTypeIsolated = clmd.getParameter("isolated");
		String searchTypeOriginal = clmd.getParameter("original");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String typeName : graph.types().theList().keySet()) {
			KType type = graph.types().getType(typeName);
			if (	("["+type.getName()+"]").contains(searchTypeName) 
					&&
					("["+type.getProperty("disjoint")+"]").contains(searchTypeDisjoint)	
					&&
					("["+type.getProperty("isolated")+"]").contains(searchTypeIsolated)	
					&&
					("["+type.getOriginalName()+"]").contains(searchTypeOriginal) ) 
			{
				graph.types().setCurrent(typeName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No types found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String searchTypeName = clmd.getVar("type");
		String searchTypeSplit = clmd.getVar("split");
		String searchTypeDisjoint = clmd.getParameter("disjoint");
		String searchTypeIsolated = clmd.getParameter("isolated");
		String searchTypeOriginal = clmd.getParameter("original");
		for (String typeName : graph.types().theList().keySet()) {
			KType type = graph.types().getType(typeName);
			if (	("["+type.getName()+"]").contains(searchTypeName) 
					&&
					("["+type.getSplit().getName()+"]").contains(searchTypeSplit)
					&&
					("["+type.getProperty("disjoint")+"]").contains(searchTypeDisjoint)	
					&&
					("["+type.getProperty("isolated")+"]").contains(searchTypeIsolated)
					&&
					("["+type.getOriginalName()+"]").contains(searchTypeOriginal) ) 
			{
				if (!found) {
					String output = "";
					output = output + String.format("%-"+graph.types().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize("_graph_")+"s", "_graph_");
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize("_split_")+"s", "_split_");
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize("_original_")+"s", "_original_");
					for (String property : type.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+graph.types().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+graph.types().getPropertySize("_schema_")+"s", type.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize("_graph_")+"s", type.getProperty("_graph_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize("_split_")+"s", type.getProperty("_split_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize("_uid_")+"s", type.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize("_name_")+"s", type.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize("_original_")+"s", type.getProperty("_original_"));
				for (String property : type.properties().keySet()) if (!property.endsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize(property)+"s", type.getProperty(property));
				}
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No types found");
		return true;
	}

	public static boolean doProperty(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KType type = KCFinder.findType(graph, clmd);
		String property = KCFinder.which(clmd, "property");
		String value = KCFinder.which(clmd, "value");
		if (!property.endsWith("_") && !property.startsWith("_")) {
			switch(property) {
			case "disjoint":
				type.setDisjoint(value.toUpperCase().equals("TRUE"));
				break;
			case "isolated":
				type.setIsolated(value.toUpperCase().equals("TRUE"));
				break;
			default:
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
		KType type = KCFinder.findType(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = type.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeName = KCFinder.which(clmd, "type");
		graph.types().setCurrent(typeName);
		KConsole.feedback("Type '"+typeName+"' selected");
		KConsole.metadata("Type", typeName);
		return true;
	}

}

