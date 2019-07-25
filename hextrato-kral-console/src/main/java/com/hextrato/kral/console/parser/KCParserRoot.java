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

					,"meta.schema.schema"
					,"meta.schema.split"
					,"meta.schema.tabular.tabular"
					,"meta.schema.tabular.attribute"
					,"meta.schema.tabular.record"
					
					,"meta.schema.neural.neural"
					,"meta.schema.neural.layer"
					,"meta.schema.hyper.space"
					,"meta.schema.hyper.vector"

					,"meta.schema.graph.graph"
					,"meta.schema.graph.type"
					,"meta.schema.graph.entity"
					,"meta.schema.graph.relation"
					,"meta.schema.graph.triple"

					,"meta.schema.ker.ker"
					,"meta.schema.ker.embed"

			}; 
	}

	public boolean exec(String parsed) throws KException {
		KConsole.error("nothing to do!");
		return true;
	}

}
