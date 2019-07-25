package com.hextrato.kral.console.exec.oper;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class RepeatDo implements KCParser {

	public void setContext (KCMetadata clmd) { clmd.setContext(""); }

	public boolean exec(KCMetadata clmd) throws KException {
		String times = clmd.getVar("times");
		String variable = clmd.getVar("var").trim();
		int repeatTimes = 0;
		try { repeatTimes = Integer.valueOf(times); }
		catch (Exception e) {
			throw new KException("Invalid number of times to repeat");
		}
		String blok = clmd.getBlok();
		if (blok.equals("")) throw new KException("Undefined repeat blok");
		for (int iteraction = 1; iteraction <= repeatTimes; iteraction++) {
			if (!variable.isEmpty()) KConsole.vars().set(variable, Integer.toString(iteraction));
			//System.out.println("["+blok+"]");
			//System.exit(0);
			if (!KConsole.runLine(blok)) return true;
		}
		return true;
	}
}

