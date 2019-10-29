package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.util.exception.KException;

public class Entity implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("entity"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "save"}; }

	public boolean partial(KCMetadata clmd) { return !(clmd.getVar("entity").equals("")); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String entityName = KCFinder.which(clmd, "entity");
		if (!graph.entities().exists(entityName) && KConsole.isMetadataAutocreate())
			return (new EntityCreate()).exec(clmd);
		else
			return (new EntitySelect()).exec(clmd);
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String entityName = KCFinder.which(clmd, "entity");
		graph.entities().create(entityName);
		KConsole.feedback("Entity '"+entityName+"' created");
		KConsole.metadata("Entity", entityName);
		return true;
	}

	public static boolean doDelete(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String entityUID = clmd.getVar("entity");
		if (entityUID.equals("")) {
			KConsole.error("which entity?");
		} else {
			graph.entities().delete(entityUID,false);
			KConsole.feedback("Entity '"+entityUID+"' deleted");
			KConsole.metadata("Entity", entityUID);
		}
		return true;
	}

	public static boolean doDeleteCascade(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String entityUID = clmd.getVar("entity");
		if (entityUID.equals("")) {
			KConsole.error("which entity?");
		} else {
			graph.entities().delete(entityUID,true);
			KConsole.feedback("Entity '"+entityUID+"' deleted");
			KConsole.metadata("Entity", entityUID);
		}
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KEntity entity = KCFinder.findEntity(graph, clmd);
		KConsole.println("schema.name = " + entity.getGraph().getSchema().getName());
		KConsole.println("graph.name = " + entity.getGraph().getName());
		KConsole.println("split.name = " + entity.getSplit().getName());
		KConsole.println("entity.name = " + entity.getName());
		KConsole.println("entity.type = " + entity.getType());
		KConsole.println("entity.nick = " + entity.getNick());
		KConsole.println("split.name = " + entity.getSplit().getName());
		KConsole.println("used.head = " + entity.usedAsHead());
		KConsole.println("used.tail = " + entity.usedAsTail());
		KConsole.metadata("Entity", entity.getName());
		return true;
	}

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String searchEntityName = clmd.getVar("_name_");
		String searchEntityType = clmd.getParameter("_type_");
		String searchEntityNick = clmd.getParameter("_nick_");
		String searchEntitySplit = clmd.getParameter("_split_");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String entityName : graph.entities().theList().keySet()) {
			KEntity entity = graph.entities().getEntity(entityName);
			if (
					("["+entity.getName()+"]").contains(searchEntityName)
					&&
					("["+entity.getNick()+"]").contains(searchEntityNick)
					&&
					("["+entity.getType()+"]").contains(searchEntityType)
					&&
					("["+entity.getSplit().getName()+"]").contains(searchEntitySplit)
					) {
				graph.entities().setCurrent(entityName);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No entitys found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String searchEntityName = clmd.getVar("entity");
		String searchEntityType = clmd.getParameter("type");
		String searchEntitySplit = clmd.getParameter("split");
		for (String entityName : graph.entities().theList().keySet()) {
			KEntity entity = graph.entities().getEntity(entityName);
			if (
					("["+entity.getName()+"]").contains(searchEntityName)
					&&
					("["+entity.getType()+"]").contains(searchEntityType)
					&&
					("["+entity.getSplit().getName()+"]").contains(searchEntitySplit)
			) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+graph.entities().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize("_graph_")+"s", "_graph_");
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize("_split_")+"s", "_split_");
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize("_type_")+"s", "_type_");
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize("_nick_")+"s", "_nick_");
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+graph.entities().getPropertySize("_schema_")+"s", entity.getProperty("_schema_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize("_graph_")+"s", entity.getProperty("_graph_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize("_split_")+"s", entity.getProperty("_split_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize("_uid_")+"s", entity.getProperty("_uid_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize("_name_")+"s", entity.getProperty("_name_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize("_type_")+"s", entity.getProperty("_type_"));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize("_nick_")+"s", entity.getProperty("_nick_"));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No entitys found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KEntity entity = KCFinder.findEntity(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = entity.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String entityName = KCFinder.which(clmd, "entity");
		graph.entities().setCurrent(entityName);
		KConsole.feedback("Entity '"+entityName+"' selected");
		KConsole.metadata("Entity", entityName);
		return true;
	}

}

