package com.hextrato.kral.console.exec.meta.schema.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KRelation;
import com.hextrato.kral.core.util.exception.KException;

public class Relation implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("relation"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save", "property"}; }

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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String relationName = KCFinder.which(clmd, "relation");
		graph.relations().create(relationName);
		KConsole.feedback("Relation '"+relationName+"' created");
		KConsole.metadata("Relation", relationName);
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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KRelation relation = KCFinder.findRelation(graph, clmd);
		KConsole.println("schema.name = " + relation.getGraph().getSchema().getName());
		KConsole.println("graph.name = " + relation.getGraph().getName());
		KConsole.println("relation.name = " + relation.getName());
		KConsole.println("original.name = " + relation.getOriginalName());
		KConsole.println("split.name = " + relation.getSplit().getName());
		KConsole.println("used = " + relation.usedAs());
		for (String property : relation.properties().keySet()) if (!property.endsWith("_")) {
			KConsole.println("property: "+property+" = " + relation.properties().get(property));
		}
		KConsole.metadata("Relation", relation.getName());
		return true;
	}
	
	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String searchRelationName = clmd.getVar("_name_");
		String searchRelationSplit = clmd.getParameter("_split_");
		String searchRelationFunctional = clmd.getParameter("functional");
		String searchRelationIsolated = clmd.getParameter("isolated");
		String searchRelationHeadTypeNorm = clmd.getParameter("head_type_norm");
		String searchRelationTailTypeNorm = clmd.getParameter("tail_type_norm");
		String searchRelationOriginal = clmd.getParameter("original");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String relationName : graph.relations().theList().keySet()) {
			KRelation relation = graph.relations().getRelation(relationName);
			if (
					("["+relation.getName()+"]").contains(searchRelationName)
					&&
					("["+relation.getSplit().getName()+"]").contains(searchRelationSplit)
					&&
					("["+relation.getProperty("functional")+"]").contains(searchRelationFunctional)
					&&
					("["+relation.getProperty("isolated")+"]").contains(searchRelationIsolated)
					&&
					("["+relation.getProperty("head_type_norm")+"]").contains(searchRelationHeadTypeNorm)
					&&
					("["+relation.getProperty("tail_type_norm")+"]").contains(searchRelationTailTypeNorm)
					&&
					("["+relation.getOriginalName()+"]").contains(searchRelationOriginal) ) {
				graph.relations().setCurrent(relationName);
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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String searchRelationName = clmd.getVar("relation");
		String searchRelationSplit = clmd.getParameter("split");
		String searchRelationFunctional = clmd.getParameter("functional");
		String searchRelationIsolated = clmd.getParameter("isolated");
		String searchRelationHeadTypeNorm = clmd.getParameter("head_type_norm");
		String searchRelationTailTypeNorm = clmd.getParameter("tail_type_norm");
		String searchRelationOriginal = clmd.getParameter("original");
		for (String relationName : graph.relations().theList().keySet()) {
			KRelation relation = graph.relations().getRelation(relationName);
			if (
					("["+relation.getName()+"]").contains(searchRelationName)
					&&
					("["+relation.getSplit().getName()+"]").contains(searchRelationSplit)
					&&
					("["+relation.getProperty("functional")+"]").contains(searchRelationFunctional)
					&&
					("["+relation.getProperty("isolated")+"]").contains(searchRelationIsolated)
					&&
					("["+relation.getProperty("head_type_norm")+"]").contains(searchRelationHeadTypeNorm)
					&&
					("["+relation.getProperty("tail_type_norm")+"]").contains(searchRelationTailTypeNorm)
					&&
					("["+relation.getOriginalName()+"]").contains(searchRelationOriginal) ) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+graph.relations().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize("_graph_")+"s", "_graph_");
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize("_split_")+"s", "_split_");
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+graph.relations().getPropertySize("_original_")+"s", "_original_");
					for (String property : relation.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+graph.relations().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+graph.relations().getPropertySize("_schema_")+"s", relation.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize("_graph_")+"s", relation.getProperty("_graph_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize("_split_")+"s", relation.getProperty("_split_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize("_uid_")+"s", relation.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize("_name_")+"s", relation.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.relations().getPropertySize("_original_")+"s", relation.getProperty("_original_"));
				for (String property : relation.properties().keySet()) if (!property.endsWith("_")) {
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
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KRelation relation = KCFinder.findRelation(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = relation.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String relationName = KCFinder.which(clmd, "relation");
		graph.relations().setCurrent(relationName);
		KConsole.feedback("Relation '"+relationName+"' selected");
		KConsole.metadata("Relation", relationName);
		return true;
	}

}

