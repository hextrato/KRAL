package com.hextrato.kral.console.exec.meta.ker;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.ker.KEmbed;
import com.hextrato.kral.core.util.exception.KException;
import com.hextrato.kral.core.schema.ker.KER;

public class Embed implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("embed"); }

	public String[] getValidTokenSet () { return new String[] {"config", "create", "foreach", "list", "select", "desc", "save"}; }

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
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String embedName = KCFinder.which(clmd, "embed");
		String embedType = KCFinder.which(clmd, "type");
		ker.embeds().create(embedName,embedType);
		KConsole.feedback("Embed '"+embedName+"' created");
		KConsole.metadata("Embed", embedName);
		return true;
	}

	public static boolean doDesc(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		KEmbed embed = KCFinder.findEmbed(ker, clmd);
		KConsole.println("embed.name = " + embed.getName());
		KConsole.println("embed.type = " + embed.getType());
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

	public static boolean doForeach(KCMetadata clmd) throws KException {
		boolean found = false;
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String searchEmbedName = clmd.getVar("embed");
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined foreach blok");
		for (String embedName : ker.embeds().theList().keySet()) {
			KEmbed e = ker.embeds().getEmbed(embedName);
			if (("["+e.getName()+"]").contains(searchEmbedName)) {
				ker.embeds().setCurrent(embedName);
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
		String searchEmbedName = clmd.getParameter("name");
		String searchEmbedType = clmd.getParameter("type");
		for (String embedName : ker.embeds().theList().keySet()) {
			KEmbed embed = ker.embeds().getEmbed(embedName);
			if (("["+embed.getName()+"]").contains(searchEmbedName)
				&&
				("["+embed.getType()+"]").contains(searchEmbedType)
				) {
				if (!found) {
					String output = "";
					output = output + String.format("%-"+ker.embeds().getPropertySize("_schema_")+"s", "_schema_");
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize("_ker_")+"s", "_ker_");
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize("_uid_")+"s", "_uid_");
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize("_name_")+"s", "_name_");
					output = output + "\t";
					output = output + String.format("%-"+ker.embeds().getPropertySize("_type_")+"s", "_type_");
					for (String property : embed.properties().keySet()) if (!property.endsWith("_")) {
						output = output + "\t";
						output = output + String.format("%-"+ker.embeds().getPropertySize(property)+"s", property);
					}
					KConsole.output(output);
				}
				String output = "";
				output = output + String.format("%-"+ker.embeds().getPropertySize("_schema_")+"s", ker.getSchema().getName());
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize("_ker_")+"s", ker.getName());
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize("_uid_")+"s", embed.getUID());
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize("_name_")+"s", embed.getName());
				output = output + "\t";
				output = output + String.format("%-"+ker.embeds().getPropertySize("_type_")+"s", embed.getType());
				for (String property : embed.properties().keySet()) if (!property.endsWith("_")) {
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
		return true;
	}

	public static boolean doSelect(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KER ker = KCFinder.findKER(schema, clmd);
		String embedName = KCFinder.which(clmd, "embed");
		ker.embeds().setCurrent(embedName);
		KConsole.feedback("Embed '"+embedName+"' selected");
		KConsole.metadata("Embed", embedName);
		return true;
	}	
}

