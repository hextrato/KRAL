package com.hextrato.kral.console.parser;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.core.util.exception.KException;

public class KCParserRoot implements KCParser {

	public String[] getValidTokenSet () {
		return 
			new String[] {
	 				//  "graph","type","entity","relation","triple"
					//, "role","hyper","tuple"
					//, "embedder","embedding"
					//, "ontology"
					
					// NEW info.hextrato.console.exec
					
					 "oper.exit"
					,"oper.quit"
					,"oper.reset"
					,"oper.info"
					,"oper.trace"
					,"oper.sleep"
					,"oper.gosub"
					,"oper.run"
					,"oper.last"
					,"oper.repeat"
					,"oper.when"
					,"oper.datetime"
					
					,"cons.parameter"
					,"cons.var"

					,"meta.schema"
					,"meta.split"
					,"meta.tabular.tabular"
					,"meta.tabular.attribute"
					,"meta.tabular.record"
					
					,"meta.neural.neural"
					,"meta.neural.layer"
					,"meta.hyper.space"
					,"meta.hyper.vector"

					,"meta.graph.graph"
					,"meta.graph.type"
					,"meta.graph.entity"
					,"meta.graph.relation"
					,"meta.graph.triple"

					,"meta.ker.ker"
					,"meta.ker.embed"

					,"meta.nlp.corpus"
					,"meta.nlp.document"

			}; 
	}

	public boolean exec(String parsed) throws KException {
		KConsole.error("nothing to do!");
		return true;
	}

}
