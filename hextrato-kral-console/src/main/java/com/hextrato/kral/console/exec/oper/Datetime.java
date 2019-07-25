package com.hextrato.kral.console.exec.oper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Datetime implements KCParser {

	public boolean exec(KCMetadata clmd) throws KException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		return true;
	}
	
}
