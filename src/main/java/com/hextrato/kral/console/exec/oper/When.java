package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class When implements KCParser {

	public void setContext (KCMetadata clmd) { 
		String comp = "";
		try { comp = KCFinder.which(clmd, "var1"); } catch (KException e) {} finally {}
		if (comp.equals(""))
			clmd.setContext("var1"); 
		else {
			comp = "";
			try { comp = KCFinder.which(clmd, "oper"); } catch (KException e) {} finally {}
			if (comp.equals(""))
				clmd.setContext("oper"); 
			else
				clmd.setContext("var2"); 
		}
	}
	
	public String[] getValidTokenSet () {
		return new String[] {"do"}; 
	}
}

