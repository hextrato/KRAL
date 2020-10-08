package com.hextrato.kral.console.exec.meta.graph;


import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.abstracts.AMetaUIDObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KRelation;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class Relation implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("relation"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "count", "find", "save", "property"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("relation").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String relationName = KCFinder.which(clmd, "relation");
		if (!graph.relations().exists(relationName) && KConsole.isMetadataAutocreate())
			return (new RelationCreate()).exec(clmd);
		else
			return (new RelationSelect()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String relationName = KCFinder.which(clmd, "relation");
		graph.relations().create(relationName);
		KConsole.feedback("Relation '"+relationName+"' created");
		KConsole.metadata("Relation", relationName);
		KConsole.lastString(relationName); // ** NEW ** //
		return true;
	}
	
	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String relationUID = clmd.getVar("relation");
		if (relationUID.equals("")) {
			KConsole.error("which relation?");
		} else {
			graph.relations().delete(relationUID,false);
			KConsole.feedback("Relation '"+relationUID+"' deleted");
			KConsole.metadata("Relation", relationUID);
		}
		return true;
	}
	
	public static boolean doDeleteCascade(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String relationUID = clmd.getVar("relation");
		if (relationUID.equals("")) {
			KConsole.error("which relation?");
		} else {
			graph.relations().delete(relationUID,true);
			KConsole.feedback("Relation '"+relationUID+"' deleted");
			KConsole.metadata("Relation", relationUID);
		}
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KRelation relation = KCFinder.findRelation(graph, clmd);
		KConsole.println("_.schema = " + relation.getGraph().getSchema().getName());
		KConsole.println("_.graph = " + relation.getGraph().getName());
		KConsole.println("_.split = " + relation.getSplit().getName());
		KConsole.println("_.uid = " + relation.getUID());
		KConsole.println("_.name = " + relation.getName());
		KConsole.println("_.original = " + relation.getOriginalName());
		KConsole.println("?.used = " + relation.usedAs());
		for (String property : relation.properties().keySet()) if (!property.startsWith("_")) {
			KConsole.println("property."+property+" = " + relation.properties().get(property));
		}
		KConsole.metadata("Relation", relation.getName());
		KConsole.lastString(relation.getName()); // ** NEW ** //
		return true;
	}
	
	private static boolean matches(KRelation relation, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KRelation.__INTERNAL_PROPERTY_SCHEMA__);
		String searchGraph = clmd.getParameter(KRelation.__INTERNAL_PROPERTY_GRAPH__);
		String searchSplit = clmd.getParameter(KRelation.__INTERNAL_PROPERTY_SPLIT__);
		String searchUID = clmd.getParameter(KRelation.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KRelation.__INTERNAL_PROPERTY_NAME__);
		boolean match = false;
		if ( true
				&& ("["+relation.getGraph().getSchema()+"]").contains(searchSchema)
				&& ("["+relation.getGraph()+"]").contains(searchGraph)
				&& ("["+relation.getSplit()+"]").contains(searchSplit)
				&& ("["+relation.getUID()+"]").contains(searchUID)
				&& ("["+relation.getName()+"]").contains(searchName)
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
		for (String relationUID : graph.relations().theList().keySet()) {
			KRelation relation = graph.relations().getRelation(relationUID);
			if (Relation.matches(relation,clmd)) {
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
		for (String relationUID : graph.relations().theList().keySet()) {
			KRelation relation = graph.relations().getRelation(relationUID);
			if (Relation.matches(relation,clmd)) {
				graph.relations().setCurrent(relationUID);
				KConsole.feedback("Found: " + relationUID);
				KConsole.lastFound(relationUID); // ** NEW ** //
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
		String searchRelationName = clmd.getVar("_name_");
		String searchRelationSplit = clmd.getParameter("_split_");
		String searchRelationFunctional = clmd.getParameter("functional");
		String searchRelationIsolated = clmd.getParameter("isolated");
		String searchRelationHeadTypeNorm = clmd.getParameter("head_type_norm");
		String searchRelationTailTypeNorm = clmd.getParameter("tail_type_norm");
		String searchRelationOriginal = clmd.getParameter("original");
		*/
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String relationUID : graph.relations().theList().keySet()) {
			KRelation relation = graph.relations().getRelation(relationUID);
			if (Relation.matches(relation,clmd)) {
				graph.relations().setCurrent(relationUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No relations found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		/*
		String searchRelationName = clmd.getVar("relation");
		String searchRelationSplit = clmd.getParameter("split");
		String searchRelationFunctional = clmd.getParameter("functional");
		String searchRelationIsolated = clmd.getParameter("isolated");
		String searchRelationHeadTypeNorm = clmd.getParameter("head_type_norm");
		String searchRelationTailTypeNorm = clmd.getParameter("tail_type_norm");
		String searchRelationOriginal = clmd.getParameter("original");
		*/
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		for (String relationUID : graph.relations().theList().keySet()) {
			KRelation relation = graph.relations().getRelation(relationUID);
			if (Relation.matches(relation,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+graph.relations().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_GRAPH__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_GRAPH__);
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__);
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_UID__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_ORIGINAL__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_ORIGINAL__);
					for (String property : relation.properties().keySet()) if (!property.startsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+graph.relations().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+graph.relations().getPropertySize(KTabular.__INTERNAL_PROPERTY_SCHEMA__)+"s", relation.getGraph().getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize(KTabular.__INTERNAL_PROPERTY_GRAPH__)+"s", relation.getGraph().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize(KTabular.__INTERNAL_PROPERTY_SPLIT__)+"s", relation.getSplit().getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize(KTabular.__INTERNAL_PROPERTY_UID__)+"s", relation.getUID());
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize(KTabular.__INTERNAL_PROPERTY_NAME__)+"s", relation.getName());
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize(KTabular.__INTERNAL_PROPERTY_ORIGINAL__)+"s", relation.getOriginalName());
				for (String property : relation.properties().keySet()) if (!property.startsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize(property)+"s", relation.getProperty(property));
				}
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No relations found");
		return true;
	}

	public static boolean doProperty(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KRelation relation = KCFinder.findRelation(graph, clmd);
		String property = KCFinder.which(clmd, "property");
		String value = KCFinder.which(clmd, "value");
		if (!property.endsWith("_") && !property.startsWith("_")) {
			switch(property) {
			case "functional":
				relation.setFunctional(value.toUpperCase().equals("TRUE"));
				break;
			case "isolated":
				relation.setIsolated(value.toUpperCase().equals("TRUE"));
				break;
			case "head_type_norm":
				relation.setHeadTypeNorm(value.toUpperCase().equals("TRUE"));
				break;
			case "tail_type_norm":
				relation.setTailTypeNorm(value.toUpperCase().equals("TRUE"));
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
		KRelation relation = KCFinder.findRelation(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = relation.getProperty(property);
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
		String relationName = KCFinder.which(clmd, "relation");
		graph.relations().setCurrent(relationName);
		KConsole.feedback("Relation '"+relationName+"' selected");
		KConsole.metadata("Relation", relationName);
		KConsole.lastString(relationName); // ** NEW ** //
		return true;
	}

}

