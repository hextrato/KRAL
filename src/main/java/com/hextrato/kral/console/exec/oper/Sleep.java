package com.hextrato.kral.console.exec.oper;

import java.util.concurrent.TimeUnit;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Sleep implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("milliseconds"); }

	public boolean exec(KCMetadata clmd) throws KException {
		String millisecs = KCFinder.which(clmd, "milliseconds");
		try { TimeUnit.MILLISECONDS.sleep( Long.valueOf(millisecs)); } catch (InterruptedException e) {}
		return true;
	}
	
}
