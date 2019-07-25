package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Gosub implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext("subroutine"); }

	public boolean exec(KCMetadata clmd) throws KException {
		String subroutine = KCFinder.which(clmd, "subroutine");
		Run.goSub(subroutine);
		return true;
	}
	
}
