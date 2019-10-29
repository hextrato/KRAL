package com.hextrato.kral.core.schema.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hextrato.kral.core.data.abstracts.AMetaUIDObjectSet;
import com.hextrato.kral.core.data.type.TUid;
import com.hextrato.kral.core.util.exception.KException;

public class KTripleSet extends AMetaUIDObjectSet {
	
	private Map<String,String> _relaIndex = new HashMap<String,String>();
	private Map<String,String> _normMap = new HashMap<String,String>();
	
	public KTripleSet(KGraph graph) throws KException {
		if (graph == null) throw new KException("Invalid null graph");
		this._graph = graph;
		this.setMetaType("Triple");
		_normMap.put("head_name", "");
		_normMap.put("tail_name", "");
		_normMap.put("rela_name", "");
		_normMap.put("head_type", "");
		_normMap.put("tail_type", "");
		_normMap.put("norm_head_name", "");
		_normMap.put("norm_tail_name", "");
		_normMap.put("norm_rela_name", "");
		_normMap.put("norm_head_type", "");
		_normMap.put("norm_tail_type", "");
	}
	
	public Map<String,String> getNormalizeTripleConstituents(String head, String rela, String tail) throws KException {
		if (this._graph.isTyped()) {
			_normMap.replace("head_name", KEntity.extractNameFrom(head));
			_normMap.replace("head_type", KEntity.extractTypeFrom(head));
			_normMap.replace("tail_name", KEntity.extractNameFrom(tail));
			_normMap.replace("tail_type", KEntity.extractTypeFrom(tail));
		} else {
			_normMap.replace("head_name", head);
			_normMap.replace("head_type", "*");
			_normMap.replace("tail_name", tail);
			_normMap.replace("tail_type", "*");
		}
		_normMap.replace("rela_name", rela);
		// ...
		// normalized Constituents
		_normMap.replace("norm_head_name", _normMap.get("head_name"));
		_normMap.replace("norm_tail_name", _normMap.get("tail_name"));
		_normMap.replace("norm_rela_name", _normMap.get("rela_name"));
		_normMap.replace("norm_head_type", _normMap.get("head_type"));
		_normMap.replace("norm_tail_type", _normMap.get("tail_type"));
		
		// ...
		// not Typed Graph
		if (!this._graph.isTyped()) return this._normMap;
		// ...
		// Otherwise
		// ...
		// Typed Graph !!!
		// ...
		if ( _graph.relations().exists( _normMap.get("rela_name") ) && _graph.relations().getRelation( _normMap.get("rela_name") ).isHeadTypeNorm() ) {
			_normMap.replace("norm_rela_name", _normMap.get("norm_head_type") + "." + _normMap.get("norm_rela_name") );
		}
		if (( _graph.relations().exists( _normMap.get("rela_name") ) && _graph.relations().getRelation( _normMap.get("rela_name") ).isIsolated() ) ||
			( _graph.types().exists( _normMap.get("tail_type") ) && _graph.types().getType( _normMap.get("tail_type") ).isIsolated() ) ){
			// (???) _normMap.replace("norm_tail_name", "<" + _normMap.get("norm_rela_name") + ">" + _normMap.get("norm_tail_name") );
			_normMap.replace("norm_tail_type", "<" + _normMap.get("norm_rela_name") + ">" + _normMap.get("norm_tail_type") );
		}
		if ( _graph.relations().exists( _normMap.get("rela_name") ) && _graph.relations().getRelation( _normMap.get("rela_name") ).isTailTypeNorm() ) {
			// _normMap.replace("norm_rela_name", _normMap.get("norm_rela_name") + "." + _normMap.get("norm_tail_type") );
			_normMap.replace("norm_rela_name", _normMap.get("norm_rela_name") + "." + _normMap.get("tail_type") );
		}

		/*
		Hextrato.debug(">> getNormalizeTripleConstituents("+head+","+rela+","+tail+"):");
		Hextrato.debug("     head_type = "+_normMap.get("head_type"));
		Hextrato.debug("     head_name = "+_normMap.get("head_name"));
		Hextrato.debug("     tail_type = "+_normMap.get("tail_type"));
		Hextrato.debug("     tail_name = "+_normMap.get("tail_name"));
		Hextrato.debug("norm_head_type = "+_normMap.get("norm_head_type"));
		Hextrato.debug("norm_head_name = "+_normMap.get("norm_head_name"));
		Hextrato.debug("norm_tail_type = "+_normMap.get("norm_tail_type"));
		Hextrato.debug("norm_tail_name = "+_normMap.get("norm_tail_name"));
		Hextrato.debug("     rela_name = "+_normMap.get("rela_name"));
		Hextrato.debug("norm_rela_name = "+_normMap.get("norm_rela_name"));
		*/
		return _normMap;
	}
	
	private KGraph _graph = null;
	public KGraph getGraph() { return this._graph; }

	public String create (String triple) throws KException { 
		KGraph graph = getGraph();
		if (!triple.startsWith("(") || !triple.endsWith(")")) throw new KException("Invalid triple format (h,r,t)");
		String triplet = triple.substring(1, triple.length()-1);
		String[] triplets = triplet.split(",");
		if (triplets.length < 3 || triplets.length > 4) throw new KException("Incorrect number of triple arguments (h,r,t[,+/-]) in: "+triple);
		String head = triplets[0];
		String rela = triplets[1];
		String tail = triplets[2];
		boolean pola = true;
		if (triplets.length > 3) {
			String p = triplets[3];
			if (p.equals("+") || p.equals("-")) pola = p.equals("+");
			else throw new KException("Invalid polarity value [+/-]: "+p);
		}

		// ...
		// original tripleKey
		String tripleKey;
		tripleKey = head+","+rela+","+tail+","+(pola?"+":"-");
		if (this._relaIndex.containsKey(tripleKey)) 
			throw new KException("Triple already exists: "+triple);
		else
			this._relaIndex.put(tripleKey, "?");

		// ...
		// validate polarity
		if (triplets.length == 4) 
			switch(triplets[3]) {
			case "+": pola = true; break;
			case "-": pola = false; break;
				default:throw new KException("Incorrect polarity argument format [+/-]: "+triplets[3]);
			}

		// ...
		// autocreate constinuents
		Map<String,String> __norms = getNormalizeTripleConstituents(head,rela,tail);
		// head type
		if ( ! graph.types().exists( __norms.get("head_type") ) ) {
			if (graph.isAutocreate())
				graph.types().create( __norms.get("head_type") );
			else
				throw new KException("Graph "+graph.getName()+" is set autocreate OFF for head type " + __norms.get("head_type") );
		}
		
		if ( !__norms.get("norm_head_type").equals( __norms.get("head_type") ) ) {
			if ( ! graph.types().exists( __norms.get("norm_head_type") ) ) {
				graph.types().create( __norms.get("norm_head_type") );
			}
			KType orig = graph.types().getType( __norms.get("head_type") );
			KType norm = graph.types().getType( __norms.get("norm_head_type") );
			norm.setDisjoint( orig.isDisjoint() );
			norm.setIsolated( orig.isIsolated() );
			norm.setOriginalName( orig.getName() );
		}
		// tail type
		if ( ! graph.types().exists( __norms.get("tail_type") ) ) {
			if (graph.isAutocreate())
				graph.types().create( __norms.get("tail_type") );
			else
				throw new KException("Graph "+graph.getName()+" is set autocreate OFF for tail type " + __norms.get("tail_type") );
		}
		if ( !__norms.get("norm_tail_type").equals( __norms.get("tail_type") ) ) {
			if ( ! graph.types().exists( __norms.get("norm_tail_type") ) ) {
				graph.types().create( __norms.get("norm_tail_type") );
			}
			KType orig = graph.types().getType( __norms.get("tail_type") );
			KType norm = graph.types().getType( __norms.get("norm_tail_type") );
			norm.setDisjoint( orig.isDisjoint() );
			norm.setIsolated( orig.isIsolated() );
			norm.setOriginalName( orig.getName() );
		}
		if (graph.isTyped()) {
			// head
			if ( ! graph.entities().exists( __norms.get("norm_head_type") + ":" + __norms.get("norm_head_name") ) ) {
				graph.entities().create( __norms.get("norm_head_type") + ":" + __norms.get("norm_head_name") );
			}
			// tail
			if ( ! graph.entities().exists( __norms.get("norm_tail_type") + ":" + __norms.get("norm_tail_name") ) ) {
				graph.entities().create( __norms.get("norm_tail_type") + ":" + __norms.get("norm_tail_name") );
			}
		} else {
			if ( ! graph.entities().exists( __norms.get("norm_head_name") ) ) {
				graph.entities().create( __norms.get("norm_head_name") );
			}
			// tail
			if ( ! graph.entities().exists( __norms.get("norm_tail_name") ) ) {
				graph.entities().create( __norms.get("norm_tail_name") );
			}
		}

		// relation
		if ( ! graph.relations().exists( __norms.get("rela_name") ) ) {
			//if (graph.isAutocreate())
				graph.relations().create( __norms.get("rela_name") );
			//else
			//	throw new HXException("Graph "+graph.getName()+" is set autocreate OFF for relation " + __norms.get("rela_name") );
		}
		if ( !__norms.get("norm_rela_name").equals( __norms.get("rela_name") ) ) {
			if ( ! graph.relations().exists( __norms.get("norm_rela_name") ) ) {
				graph.relations().create( __norms.get("norm_rela_name") );
			}
			KRelation orig = graph.relations().getRelation( __norms.get("rela_name") );
			KRelation norm = graph.relations().getRelation( __norms.get("norm_rela_name") );
			norm.setFunctional( orig.isFunctional() );
			norm.setIsolated( orig.isIsolated() );
			norm.setHeadTypeNorm( orig.isHeadTypeNorm() );
			norm.setTailTypeNorm( orig.isTailTypeNorm() );
			norm.setOriginalName( orig.getName() );
		}

		// ...
		// create triple
		if (graph.isTyped()) {
			head = __norms.get("norm_head_type") + ":" + __norms.get("norm_head_name");
			rela = __norms.get("norm_rela_name");
			tail = __norms.get("norm_tail_type") + ":" + __norms.get("norm_tail_name");
		} else {
			head = __norms.get("norm_head_name");
			rela = __norms.get("norm_rela_name");
			tail = __norms.get("norm_tail_name");
		}
		// String uid = UUID.randomUUID().toString();
		String uid = TUid.random();
		// System.out.println(uid+","+head+","+rela+","+tail+","+pola);
		this.create(uid, new KTriple(graph,head,rela,tail,pola));
		
		// ...
		// updated triple key
		tripleKey = head+","+rela+","+tail+","+(pola?"+":"-");
		if (!this._relaIndex.containsKey(tripleKey)) 
			this._relaIndex.put(tripleKey, uid);
		else
			this._relaIndex.replace(tripleKey, uid);
		return uid;
	}
	
	public boolean containsTriple(String head, String rela, String tail, boolean pola) {
		String tripleKey = head+","+rela+","+tail+","+(pola?"+":"-");
		return this._relaIndex.containsKey(tripleKey);
	}
	
	public KTriple getTriple() {
		return getTriple(this.getCurrent());
	}
	public KTriple getTriple(String uid) {
		return (KTriple)this.get(uid);
	}

	// redefine delete in order to manage stats
	public void delete (String name) throws KException {
		if (this.theList().containsKey(name)) {
			KTriple triple = this.getTriple(name);
			triple.getHead().computeUsedAsHead(-1);
			triple.getTail().computeUsedAsTail(-1);
			triple.getRela().computeUsedAs(-1);
		}
		super.delete(name);
	}

	//
	// EXPORT
	//
	public void hextract (BufferedWriter bf) throws KException {
		String currSplit = "";
		for (String uid : this.theList().keySet()) {
			KTriple r = this.getTriple(uid); 
			if (!r.getSplit().getName().equals(currSplit)) {
				currSplit = r.getSplit().getName();
		        try {
		        	bf.write( String.format("split %s select", currSplit) );
		        	bf.newLine();
		        } catch (IOException e) {
		        	throw new KException(e.getMessage());
		        }
			}
			r.hextract(bf);
		}
	}
}
