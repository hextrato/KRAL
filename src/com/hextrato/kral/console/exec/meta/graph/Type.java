package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KType;
import com.hextrato.kral.core.schema.hyper.KVector;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Type implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("type"); }

	public String[] getValidTokenSet () {
		return new String[] {"create", "delete", "list", "select", "desc", "foreach", "count", "find", "property", "save", "continuous"}; 
	}

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("type").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeName = KCFinder.which(clmd, "type");
		if (!graph.types().exists(typeName) && KConsole.isMetadataAutocreate())
			return (new TypeCreate()).exec(clmd);
		else
			return (new TypeSelect()).exec(clmd);
	}

	public static boolean doContinuousMin(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KType type = KCFinder.findType(graph, clmd);
		String value = clmd.getVar("value");
		type.setMin(Double.valueOf(value));
		KConsole.feedback("Type '"+type.getName()+"' min");
		KConsole.metadata("Type", type.getName());
		return true;
	}

	public static boolean doContinuousMax(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KType type = KCFinder.findType(graph, clmd);
		String value = clmd.getVar("value");
		type.setMax(Double.valueOf(value));
		KConsole.feedback("Type '"+type.getName()+"' max");
		KConsole.metadata("Type", type.getName());
		return true;
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String typeName = KCFinder.which(clmd, "type");
		graph.types().create(typeName);
		KConsole.feedback("Type '"+typeName+"' created");
		KConsole.metadata("Type", typeName);
		KConsole.lastString(typeName); // ** NEW ** //
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
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KType type = KCFinder.findType(graph, clmd);
		KConsole.println("_.schema = " + type.getGraph().getSchema().getName());
		KConsole.println("_.graph = " + type.getGraph().getName());
		KConsole.println("_.uid = " + type.getUID());
		KConsole.println("_.name = " + type.getName());
		KConsole.println("_.original = " + type.getOriginalName());
		KConsole.println("_.used = " + type.usedAs());
		for (String property : type.properties().keySet()) if (!property.startsWith("_")) {
			KConsole.println("property."+property+" = " + type.properties().get(property));
		}
		KConsole.metadata("Type", type.getName());
		KConsole.lastString(type.getName()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KType type, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KType.__INTERNAL_PROPERTY_SCHEMA__);
		String searchGraph = clmd.getParameter(KType.__INTERNAL_PROPERTY_GRAPH__);
		String searchSplit = clmd.getParameter(KType.__INTERNAL_PROPERTY_SPLIT__);
		String searchUID = clmd.getParameter(KType.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KType.__INTERNAL_PROPERTY_NAME__);
		String searchOriginal = clmd.getParameter(KType.__INTERNAL_PROPERTY_ORIGINAL__);
		boolean match = false;
		if ( true
				&& ("["+type.getGraph().getSchema()+"]").contains(searchSchema)
				&& ("["+type.getGraph()+"]").contains(searchGraph)
				&& ("["+type.getSplit()+"]").contains(searchSplit)
				&& ("["+type.getUID()+"]").contains(searchUID)
				&& ("["+type.getName()+"]").contains(searchName)
				&& ("["+type.getOriginalName()+"]").contains(searchOriginal)
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
		for (String typeUID : graph.types().theList().keySet()) {
			KType type = graph.types().getType(typeUID);
			if (Type.matches(type,clmd)) {
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
		for (String typeUID : graph.types().theList().keySet()) {
			KType type = graph.types().getType(typeUID);
			if (Type.matches(type,clmd)) {
				graph.types().setCurrent(typeUID);
				KConsole.feedback("Found: " + typeUID);
				KConsole.lastFound(typeUID); // ** NEW ** //
				return true;
			}
		}
		KConsole.feedback("Not found");
		KConsole.lastFound(""); // ** NEW ** //
		return true;
	}
	

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		/*
		String searchTypeName = clmd.getVar("type");
		String searchTypeDisjoint = clmd.getParameter("disjoint");
		String searchTypeIsolated = clmd.getParameter("isolated");
		String searchTypeOriginal = clmd.getParameter("original");
		*/
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String typeUID : graph.types().theList().keySet()) {
			KType type = graph.types().getType(typeUID);
			if (Type.matches(type,clmd)) {
				graph.types().setCurrent(typeUID);
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
		/*
		String searchTypeName = clmd.getVar("type");
		String searchTypeSplit = clmd.getVar("split");
		String searchTypeDisjoint = clmd.getParameter("disjoint");
		String searchTypeIsolated = clmd.getParameter("isolated");
		String searchTypeOriginal = clmd.getParameter("original");
		*/
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		for (String typeUID : graph.types().theList().keySet()) {
			KType type = graph.types().getType(typeUID);
			if (Type.matches(type,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+graph.types().getPropertySize(KVector.__INTERNAL_PROPERTY_SCHEMA__)+"s", KType.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize(KVector.__INTERNAL_PROPERTY_GRAPH__)+"s", KType.__INTERNAL_PROPERTY_GRAPH__);
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize(KVector.__INTERNAL_PROPERTY_SPLIT__)+"s", KType.__INTERNAL_PROPERTY_SPLIT__);
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize(KVector.__INTERNAL_PROPERTY_UID__)+"s", KType.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize(KVector.__INTERNAL_PROPERTY_NAME__)+"s", KType.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+graph.types().getPropertySize(KVector.__INTERNAL_PROPERTY_ORIGINAL__)+"s", KType.__INTERNAL_PROPERTY_ORIGINAL__);
					for (String property : type.properties().keySet()) if (!property.startsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+graph.types().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+graph.types().getPropertySize(KTabular.__INTERNAL_PROPERTY_SCHEMA__)+"s", type.getGraph().getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize(KTabular.__INTERNAL_PROPERTY_GRAPH__)+"s", type.getGraph().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize(KTabular.__INTERNAL_PROPERTY_SPLIT__)+"s", type.getSplit().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize(KTabular.__INTERNAL_PROPERTY_UID__)+"s", type.getUID());
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize(KTabular.__INTERNAL_PROPERTY_NAME__)+"s", type.getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.types().getPropertySize(KTabular.__INTERNAL_PROPERTY_ORIGINAL__)+"s", type.getOriginalName());
				for (String property : type.properties().keySet()) if (!property.startsWith("_")) {
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
			case "continuous":
				type.setContinuous(value.toUpperCase().equals("TRUE"));
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
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KType type = KCFinder.findType(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = type.getProperty(property);
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
		String typeName = KCFinder.which(clmd, "type");
		graph.types().setCurrent(typeName);
		KConsole.feedback("Type '"+typeName+"' selected");
		KConsole.metadata("Type", typeName);
		KConsole.lastString(typeName); // ** NEW ** //
		return true;
	}

}

