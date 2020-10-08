package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.data.abstracts.AMetaUIDObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.ker.KEmbed;
import com.hextrato.kral.core.util.exception.KException;
import com.hextrato.kral.core.schema.ker.KER;

public class Embed implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("embed"); }

	public String[] getValidTokenSet () { return new String[] {"config", "create", "foreach", "list", "select", "desc", "count", "find", "save"}; }

	// public boolean partial(CLMetadata clmd) { return !(clmd.getVar("embedding").equals("")); }

	/*
	public boolean exec(CLMetadata clmd) throws HXException {
		HextraSchema schema = CLFinder.findSchema(clmd);
		String embedderName = CLFinder.which(clmd, "embedding");
		if (!schema.embedders().exists(embedderName) && HXConsole.isMetadataAutocreate())
			return (new EmbedderCreate()).exec(clmd);
		else
			return (new EmbedderSelect()).exec(clmd);
	}
	*/

	public static boolean doConfig(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		KEmbed embed = KCFinder.findEmbed(ker, clmd);
		String hyperparam = KCFinder.which(clmd, "hyperparam").toLowerCase();
		String hypervalue = KCFinder.which(clmd, "hypervalue").toLowerCase();
		switch (hyperparam) {
		
		case "evs":
			embed.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).setValues(hypervalue);
			break;

		case "rvs":
			embed.representation().getSLR(KEmbed.RELATION_VECTOR_SLR).setValues(hypervalue);
			break;

		case "rvsi":
			embed.representation().getSLR(KEmbed.RELATION_VECTOR_SLR_INVERSE).setValues(hypervalue);
			break;

		case "rpm":
			embed.representation().getSLR(KEmbed.RELATION_MATRIX_SLR).setValues(hypervalue);
			break;

		case "rpmi":
			embed.representation().getSLR(KEmbed.RELATION_MATRIX_SLR_INVERSE).setValues(hypervalue);
			break;

		default:
			throw new KException ("Invalid hyperparam '"+hyperparam+"'");
		}
		return true;
	}

	public static boolean doCreate(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String embedName = KCFinder.which(clmd, "embed");
		String embedType = KCFinder.which(clmd, "type");
		ker.embeds().create(embedName,embedType);
		KConsole.feedback("Embed '"+embedName+"' created");
		KConsole.metadata("Embed", embedName);
		KConsole.lastString(embedName); // ** NEW ** //
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		KEmbed embed = KCFinder.findEmbed(ker, clmd);
		KConsole.println("_.schema = " + embed.getKER().getSchema().getName());
		KConsole.println("_.ker = " + embed.getKER().getName());
		KConsole.println("_.name = " + embed.getName());
		KConsole.println("_.type = " + embed.getType());
		switch (embed.getType()) {
		case "ENTITY":
			KConsole.println("embed."+KEmbed.ENTITY_VECTOR_SLR+" = " + embed.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0));
			break;
		case "RELATION":
			KConsole.println("embed."+KEmbed.RELATION_VECTOR_SLR+" = " + embed.representation().getSLR(KEmbed.RELATION_VECTOR_SLR).getRow(0));
			if (embed.isProjectionMatrixActive()) 
				KConsole.println("embed."+KEmbed.RELATION_MATRIX_SLR+" = [" + embed.representation().getSLR(KEmbed.RELATION_MATRIX_SLR).getRow(0)+"...");
			if (embed.isInverseRelationActive()) {
				KConsole.println("embed."+KEmbed.RELATION_VECTOR_SLR_INVERSE+" = " + embed.representation().getSLR(KEmbed.RELATION_VECTOR_SLR_INVERSE).getRow(0));
				if (embed.isProjectionMatrixActive()) 
					KConsole.println("embed."+KEmbed.RELATION_MATRIX_SLR_INVERSE+" = [" + embed.representation().getSLR(KEmbed.RELATION_MATRIX_SLR_INVERSE).getRow(0)+"...");
			}
			break;
		}
		KConsole.metadata("Embed", embed.getName());
		return true;
	}

	private static boolean matches(KEmbed embed, KCMetadata clmd) throws KException {
		String searchSchema = clmd.getParameter(KEmbed.__INTERNAL_PROPERTY_SCHEMA__);
		String searchKER = clmd.getParameter(KEmbed.__INTERNAL_PROPERTY_GRAPH__);
		String searchUID = clmd.getParameter(KEmbed.__INTERNAL_PROPERTY_UID__);
		String searchName = clmd.getParameter(KEmbed.__INTERNAL_PROPERTY_NAME__);
		boolean match = false;
		if ( true
				&& ("["+embed.getKER().getSchema()+"]").contains(searchSchema)
				&& ("["+embed.getKER()+"]").contains(searchKER)
				&& ("["+embed.getUID()+"]").contains(searchUID)
				&& ("["+embed.getName()+"]").contains(searchName)
				) {
			match = true;
		}
		return match;
	}

	public static boolean doCount(KCMetadata clmd) throws KException {
		KConsole.lastInteger(0); // ** NEW ** //
		int count = 0;
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema,clmd);
		for (String embedUID : ker.embeds().theList().keySet()) {
			KEmbed embed = ker.embeds().getEmbed(embedUID);
			if (Embed.matches(embed,clmd)) {
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
		KER ker = KCFinder.findKER(schema,clmd);
		for (String embedUID : ker.embeds().theList().keySet()) {
			KEmbed embed = ker.embeds().getEmbed(embedUID);
			if (Embed.matches(embed,clmd)) {
				ker.embeds().setCurrent(embedUID);
				KConsole.feedback("Found: " + embedUID);
				KConsole.lastFound(embedUID); // ** NEW ** //
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
		KER ker = KCFinder.findKER(schema,clmd);
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String embedUID : ker.embeds().theList().keySet()) {
			KEmbed embed = ker.embeds().getEmbed(embedUID);
			if (Embed.matches(embed,clmd)) {
				ker.embeds().setCurrent(embedUID);
				KConsole.runLine(blok);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No embeds found");
		return true;
	}

	public static boolean doList(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		for (String embedUID : ker.embeds().theList().keySet()) {
			KEmbed embed = ker.embeds().getEmbed(embedUID);
			if (Embed.matches(embed,clmd)) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__);
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_KER__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_KER__);
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_UID__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_UID__);
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_NAME__);
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__)+"s", AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__);
					for (String property : embed.properties().keySet()) if (!property.startsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+ker.embeds().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__)+"s", embed.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_SCHEMA__));
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_KER__)+"s", embed.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_KER__));
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_UID__)+"s", embed.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_UID__));
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__)+"s", embed.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_NAME__));
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__)+"s", embed.getProperty(AMetaUIDObject.__INTERNAL_PROPERTY_TYPE__));
				for (String property : embed.properties().keySet()) if (!property.startsWith("_")) {
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize(property)+"s", embed.getProperty(property));
				}
				KConsole.output(output);
				found = true;
			}
		}
		if (!found)
			KConsole.feedback("No embeds found");
		return true;
	}

	public static boolean doSaveVar(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		KEmbed embed = KCFinder.findEmbed(ker, clmd);
		String property = clmd.getVar("property");
		String var = clmd.getVar("var");
		String value = "";
		if (embed.getType().equals("ENTITY") && property.toLowerCase().equals("evs")) 
			value = embed.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).toString();
		else
			value = embed.getProperty(property);
		KConsole.vars().set(var,value);
		KConsole.feedback("Variable '"+var+"' set");
		KConsole.metadata("Variable", var, value);
		KConsole.lastString(value); // ** NEW ** //
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KConsole.lastString(""); // ** NEW ** //
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String embedName = KCFinder.which(clmd, "embed");
		ker.embeds().setCurrent(embedName);
		KConsole.feedback("Embed '"+embedName+"' selected");
		KConsole.metadata("Embed", embedName);
		KConsole.lastString(embedName); // ** NEW ** //
		return true;
	}	
}

