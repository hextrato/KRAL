package com.hextrato.kral.console.parser;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.KSplit;
import com.hextrato.kral.core.schema.graph.KEntity;
import com.hextrato.kral.core.schema.graph.KGraph;
import com.hextrato.kral.core.schema.graph.KRelation;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.schema.graph.KType;
import com.hextrato.kral.core.schema.hyper.KSpace;
import com.hextrato.kral.core.schema.hyper.KVector;
import com.hextrato.kral.core.schema.ker.KEmbed;
import com.hextrato.kral.core.schema.ker.KER;
import com.hextrato.kral.core.schema.neural.KLayer;
import com.hextrato.kral.core.schema.neural.KNeural;
import com.hextrato.kral.core.schema.tabular.KAttribute;
import com.hextrato.kral.core.schema.tabular.KRecord;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public interface KCFinder {

	public static String which (KCMetadata clmd, String what) throws KException {
		String name = clmd.find(what);
		if ( name == null || "".equals(name) ) {
			throw new KException("which "+what+" ?"); 
		}
		return name;
	}

	public static KSchema findSchema (KCMetadata clmd) throws KException {
		String name = clmd.find("schema"); if (name.equals("")) name = KRAL.schemata().getCurrent();
		if (name.equals("")) throw new KException("which schema ?"); 
		KSchema schema = null;
		if ( (schema = KRAL.schemata().getSchema(name) ) == null) {
			KRAL.schemata().itDoesNotExist(name);
			// throw new CLException("schema '' does NOT exists"); 
		}
		return schema;
	}

	public static KSplit findSplit (KSchema schema, KCMetadata clmd) throws KException {
		String name = clmd.find("split"); if (name.equals("")) name = schema.splits().getCurrent();
		if (name.equals("")) throw new KException("which split ?"); 
		KSplit split = null;
		if ( (split = schema.splits().getSplit(name) ) == null) {
			schema.splits().itDoesNotExist(name);
			// throw new CLException("which split ?"); 
		}
		return split;
	}

	public static KGraph findGraph (KSchema schema, KCMetadata clmd) throws KException {
		String name = clmd.find("graph"); if (name.equals("")) name = schema.graphs().getCurrent();
		if (name.equals("")) throw new KException("which graph ?"); 
		KGraph graph = null;
		if ( (graph = schema.graphs().getGraph(name) ) == null) {
			schema.graphs().itDoesNotExist(name);
			// throw new CLException("which graph ?"); 
		}
		return graph;
	}

	public static KType findType (KGraph graph, KCMetadata clmd) throws KException {
		String name = clmd.find("type"); if (name.equals("")) name = graph.types().getCurrent();
		if (name.equals("")) throw new KException("which type ?"); 
		KType type = null;
		if ( (type = graph.types().getType(name) ) == null) {
			graph.types().itDoesNotExist(name);
		}
		return type;
	}

	public static KEntity findEntity (KGraph graph, KCMetadata clmd) throws KException {
		String name = clmd.find("entity"); if (name.equals("")) name = graph.entities().getCurrent();
		if (name.equals("")) throw new KException("which entity ?"); 
		KEntity entity = null;
		if ( (entity = graph.entities().getEntity(name) ) == null) {
			graph.entities().itDoesNotExist(name);
		}
		return entity;
	}

	public static KRelation findRelation (KGraph graph, KCMetadata clmd) throws KException {
		String name = clmd.find("relation"); if (name.equals("")) name = graph.relations().getCurrent();
		if (name.equals("")) throw new KException("which relation ?"); 
		KRelation relation = null;
		if ( (relation = graph.relations().getRelation(name) ) == null) {
			graph.relations().itDoesNotExist(name);
		}
		return relation;
	}

	/*
	public static HextraHyper findHyper (HextraGraph graph, CLMetadata clmd) throws HXException {
		String name = clmd.find("hyper"); if (name.equals("")) name = graph.hypers().getCurrent();
		if (name.equals("")) throw new HXException("which hyper ?"); 
		HextraHyper hyper = null;
		if ( (hyper = graph.hypers().getHyper(name) ) == null) {
			graph.hypers().itDoesNotExist(name);
		}
		return hyper;
	}

	public static HextraRole findRole (HextraGraph graph, CLMetadata clmd) throws HXException {
		String name = clmd.find("role"); if (name.equals("")) name = graph.roles().getCurrent();
		if (name.equals("")) throw new HXException("which role ?"); 
		HextraRole role = null;
		if ( (role = graph.roles().getRole(name) ) == null) {
			graph.roles().itDoesNotExist(name);
		}
		return role;
	}
	*/
	
	public static KTabular findTabular (KSchema schema, KCMetadata clmd) throws KException {
		String name = clmd.find("tabular"); if (name.equals("")) name = schema.tabulars().getCurrent();
		if (name.equals("")) throw new KException("which tabular ?"); 
		KTabular tabular = null;
		if ( (tabular = schema.tabulars().getTabular(name) ) == null) {
			schema.tabulars().itDoesNotExist(name);
			//throw new CLException("which tabular ?"); 
		}
		return tabular;
	}
	
	public static KAttribute findAttribute (KTabular tabular, KCMetadata clmd) throws KException {
		String name = clmd.find("attribute"); if (name.equals("")) name = tabular.attributes().getCurrent();
		if (name.equals("")) throw new KException("which attribute ?"); 
		KAttribute attribute = null;
		if ( (attribute = tabular.attributes().getAttribute(name) ) == null) {
			tabular.attributes().itDoesNotExist(name);
			// throw new CLException("which relation ?"); 
		}
		return attribute;
	}

	public static KRecord findRecord (KTabular tabular, KCMetadata clmd) throws KException {
		String uid = clmd.find("uid"); if (uid.equals("")) uid = tabular.records().getCurrent();
		if (uid.equals("")) throw new KException("which record ?"); 
		KRecord record = null;
		if ( (record = tabular.records().getRecord(uid) ) == null) {
			tabular.records().itDoesNotExist(uid);
		}
		return record;
	}
	
	public static KTriple findTriple (KGraph graph, KCMetadata clmd) throws KException {
		String uid = clmd.find("uid"); if (uid.equals("")) uid = graph.triples().getCurrent();
		if (uid.equals("")) throw new KException("which record ?"); 
		KTriple triple = null;
		if ( (triple = graph.triples().getTriple(uid) ) == null) {
			graph.triples().itDoesNotExist(uid);
		}
		return triple;
	}

	public static KNeural findNeural (KSchema schema, KCMetadata clmd) throws KException {
		String name = clmd.find("neural"); if (name.equals("")) name = schema.neuronal().getCurrent();
		if (name.equals("")) throw new KException("which neural ?"); 
		KNeural neural = null;
		if ( (neural = schema.neuronal().getNeural(name) ) == null) {
			schema.neuronal().itDoesNotExist(name);
			// throw new CLException("neural '' does NOT exists"); 
		}
		return neural;
	}

	public static KLayer findLayer (KNeural neural, KCMetadata clmd) throws KException {
		String name = clmd.find("layer"); if (name.equals("")) name = neural.layers().getCurrent();
		if (name.equals("")) throw new KException("which layer ?"); 
		KLayer layer = null;
		if ( (layer = neural.layers().getLayer(name) ) == null) {
			neural.layers().itDoesNotExist(name);
			// throw new CLException("which layer ?"); 
		}
		return layer;
	}

	public static KSpace findSpace (KSchema schema, KCMetadata clmd) throws KException {
		String name = clmd.find("space"); if (name.equals("")) name = schema.hyperspace().getCurrent();
		if (name.equals("")) throw new KException("which space ?"); 
		KSpace space = null;
		if ( (space = schema.hyperspace().getSpace(name) ) == null) {
			schema.hyperspace().itDoesNotExist(name);
			// throw new CLException("which space ?"); 
		}
		return space;
	}

	public static KVector findVector (KSpace space, KCMetadata clmd) throws KException {
		String name = clmd.find("vector"); if (name.equals("")) name = space.vectors().getCurrent();
		if (name.equals("")) throw new KException("which vector ?"); 
		KVector vector = null;
		if ( (vector = space.vectors().getVector(name) ) == null) {
			space.vectors().itDoesNotExist(name);
			// throw new CLException("which vector ?"); 
		}
		return vector;
	}

	public static KER findKER (KSchema schema, KCMetadata clmd) throws KException {
		String name = clmd.find("ker"); if (name.equals("")) name = schema.kers().getCurrent();
		if (name.equals("")) throw new KException("which KER ?"); 
		KER embedder = null;
		if ( (embedder = schema.kers().getKER(name) ) == null) {
			schema.kers().itDoesNotExist(name);
			// throw new CLException("which embedder ?"); 
		}
		return embedder;
	}

	public static KEmbed findEmbed (KER ker, KCMetadata clmd) throws KException {
		String name = clmd.find("embed"); if (name.equals("")) name = ker.embeds().getCurrent();
		if (name.equals("")) throw new KException("which embed ?"); 
		KEmbed embed = null;
		if ( (embed = ker.embeds().getEmbed(name) ) == null) {
			ker.embeds().itDoesNotExist(name);
			// throw new CLException("which embedder ?"); 
		}
		return embed;
	}


	//
	// Ontology
	//
	/*
	public static HextraOntology findOntology (HextraSchema schema, CLMetadata clmd) throws HXException {
		String name = clmd.find("ontology"); if (name.equals("")) name = schema.ontologies().getCurrent();
		if (name.equals("")) throw new HXException("which ontology ?"); 
		HextraOntology ontology = null;
		if ( (ontology = schema.ontologies().getOntology(name) ) == null) {
			schema.ontologies().itDoesNotExist(name);
			//throw new CLException("which ontology ?"); 
		}
		return ontology;
	}
	*/
}
