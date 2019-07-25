package com.hextrato.kral.console.exec.meta.schema.tabular;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.schema.tabular.KRecord;
import com.hextrato.kral.core.schema.tabular.KTabular;
import com.hextrato.kral.core.util.exception.KException;

public class RecordShow implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public boolean exec(KCMetadata clmd) throws KException {
		KSchema schema = KCFinder.findSchema(clmd);
		KTabular tabular = KCFinder.findTabular(schema, clmd);
		KRecord record = KCFinder.findRecord(tabular, clmd);
		KConsole.println("schema.name = " + record.getTabular().getSchema().getName());
		KConsole.println("tabular.name = " + record.getTabular().getName());
		KConsole.println("split.name = " + record.getSplit().getName());
		KConsole.println("record.uid = " + record.getUID());
		KConsole.metadata("Record", record.getUID());
		for (String attribute : record.values().keySet()) {
			KConsole.println(attribute+" = " + record.getAttributeValue(attribute));
		}
		return true;
	}
}

