package com.hextrato.kral.console.exec.meta.graph;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.abstracts.AMetaUIDObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.util.exception.KException;

public class Entity implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("entity"); }

	public String[] getValidTokenSet () { return new String[] {"create", "delete", "list", "select", "desc", "foreach", "count", "find", "save"}; }

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
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		String entityName = KCFinder.which(clmd, "entity");
		graph.entities().create(entityName);
		KConsole.feedback("Entity '"+entityName+"' created");
		KConsole.metadata("Entity", entityName);
		KConsole.lastString(entityName); // ** NEW ** //
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
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KEntity entity = KCFinder.findEntity(graph, clmd);
		KConsole.println("_.schema = " + entity.getGraph().getSchema().getName());
		KConsole.println("_.graph = " + entity.getGraph().getName());
		KConsole.println("_.split = " + entity.getSplit().getName());
		KConsole.println("_.uid = " + entity.getUID());
		KConsole.println("_.name = " + entity.getName());
		KConsole.println("_.type = " + entity.getType());
		KConsole.println("_.nick = " + entity.getNick());
		KConsole.println("?.used.as.head = " + entity.usedAsHead());
		KConsole.println("?.used.as.tail = " + entity.usedAsTail());
		KConsole.metadata("Entity", entity.getName());
		KConsole.lastString(entity.getName()); // ** NEW ** //
		return true;
	}

	private static boolean matches(KEntity entity, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KEntity.__INTERNAL_PROPERTY_SCHEMA__);
		String searchGraph = clmd.getParameter(KEntity.__INTERNAL_PROPERTY_GRAPH__);
		String searchSplit = clmd.getParameter(KEntity.__INTERNAL_PROPERTY_SPLIT__);
		String searchUID = clmd.getParameter(KEntity.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KEntity.__INTERNAL_PROPERTY_NAME__);
		String searchNick = clmd.getParameter(KEntity.__INTERNAL_PROPERTY_NICK__);
		boolean match = false;
		if ( true
				&& ("["+entity.getGraph().getSchema()+"]").contains(searchSchema)
				&& ("["+entity.getGraph()+"]").contains(searchGraph)
				&& ("["+entity.getSplit()+"]").contains(searchSplit)
				&& ("["+entity.getUID()+"]").contains(searchUID)
				&& ("["+entity.getName()+"]").contains(searchName)
				&& ("["+entity.getNick()+"]").contains(searchNick)
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
		for (String entityUID : graph.entities().theList().keySet()) {
			KEntity entity = graph.entities().getEntity(entityUID);
			if (Entity.matches(entity,clmd)) {
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
		for (String entityUID : graph.entities().theList().keySet()) {
			KEntity entity = graph.entities().getEntity(entityUID);
			if (Entity.matches(entity,clmd)) {
				graph.entities().setCurrent(entityUID);
				KConsole.feedback("Found: " + entityUID);
				KConsole.lastFound(entityUID); // ** NEW ** //
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
		String searchEntityName = clmd.getVar(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__);
		String searchEntityType = clmd.getParameter(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__);
		String searchEntityNick = clmd.getParameter(AMetaUIDObject.__INTERNAL_PROPERTY_NICK__);
		String searchEntitySplit = clmd.getParameter(AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__);
		*/
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String entityUID : graph.entities().theList().keySet()) {
			KEntity entity = graph.entities().getEntity(entityUID);
			if (Entity.matches(entity,clmd)) {
				graph.entities().setCurrent(entityUID);
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
		/*
		String searchEntityName = clmd.getVar("entity");
		String searchEntityType = clmd.getParameter("type");
		String searchEntitySplit = clmd.getParameter("split");
		*/
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema,clmd);
		for (String entityUID : graph.entities().theList().keySet()) {
			KEntity entity = graph.entities().getEntity(entityUID);
			if (Entity.matches(entity,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_GRAPH__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_GRAPH__);
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__);
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_UID__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__);
					output = output + "\t";
					output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NICK__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_NICK__);
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_GRAPH__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_GRAPH__));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_SPLIT__));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_UID__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_UID__));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__));
				output = output + "\t";
				output = output + String.format("%-"+graph.entities().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NICK__)+"s", entity.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_NICK__));
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No entitys found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KGraph graph = KCFinder.findGraph(schema, clmd);
		KEntity entity = KCFinder.findEntity(graph, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = entity.getProperty(property);
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
		String entityName = KCFinder.which(clmd, "entity");
		graph.entities().setCurrent(entityName);
		KConsole.feedback("Entity '"+entityName+"' selected");
		KConsole.metadata("Entity", entityName);
		KConsole.lastString(entityName); // ** NEW ** //
		return true;
	}

}

