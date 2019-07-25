package com.hextrato.kral.console.parser;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.util.exception.KException;
import com.hextrato.kral.console.KConsole;

public interface KCParser {

	// public static void out (String message) { if (message != null) HxConsole.out(message); }

	public default String[] getValidTokenSet () {
		return new String[] {}; 
	}
	
	public default void setContext(KCMetadata clmd) {
	}
	public default boolean exec(KCMetadata clmd) throws KException {
		return false; 
	}
	public default boolean partial(KCMetadata clmd) {
		return false; 
	}
	public default String getHelp() {
		return this.getClass().getName();
	};
	public default boolean help (KCMetadata clmd) throws KException {
		String token = clmd.nextToken();
		if (token != null) {
			KRAL.error("Unexpected help option: "+token);
			return false;
		} else {
			KRAL.print("help: "+getHelp());
			if (getValidTokenSet().length > 0) {
				KRAL.print("Options:"); 
				for (String validToken : getValidTokenSet()) {
					KRAL.print(validToken.toUpperCase());
				}
				KRAL.print("For more information on a specific command, type: <command> HELP");
			} else {
				// nothing!
			}
			return true;
		}
	}

	public static void tokenError (KCMetadata clmd, String token) throws KException {
		String errorMessage = "token/context ["+token+"] not recognized or expected.";
		// CLParser.err(errorMessage);
		if (!clmd.getParsed().equals("")) {
			// CLParserEngine.out("Try:"+(parsed.equals("")?"":"/")+parsed.toUpperCase().replace(".", "/")+"HELP");
			KRAL.print("Try: "+clmd.getParsed().toUpperCase().replace(".", "/")+"/HELP");
		}
		throw new KException(errorMessage);
	}

	public default boolean parse (KCMetadata clmd) throws KException {
		String token = clmd.nextToken();
		setContext(clmd);
		if (token == null) {
			if (exec(clmd)) {
				return true;
			} else { 
				KRAL.error("Missing expression");
			}
		} else {	
			if (token.equalsIgnoreCase("help")) {
				clmd.addParsed(token);
				return help(clmd);
			} else {
				// SCRIPT KEYWORDS
				for (String validToken : getValidTokenSet()) {
					if (validToken.startsWith("<") && validToken.endsWith(">")) { ; }
					else {
						boolean tokenMatching = false;
						tokenMatching |= validToken.equalsIgnoreCase(token);
						tokenMatching |= validToken.toLowerCase().endsWith("."+token.toLowerCase());
						if (tokenMatching) {
							// String validTokenClass = validToken.substring(0,1).toUpperCase()+validToken.substring(1).toLowerCase();
							clmd.addParsed(validToken);
							KCParser pe;
							try {
								// pe = (CLParser)(Class.forName("hextrato.console.exec."+clmd.getParsed()).newInstance());
								pe = KConsole.getParserExec(clmd.getParsed());
							} catch (Exception e) {
								KRAL.error(clmd.getParsed());
								KRAL.error(e.getMessage());
								KRAL.error(e.getStackTrace().toString());
								throw new KException(e.getMessage()); 
							}
							pe.parse(clmd);
							return true;
						}
						
					}
				}
					
					//
					// WITH workspace
					//
					/*
					if (clmd.getParsed().equals("") && validToken.equalsIgnoreCase(HXConsole.getWorkspace()+"."+token)) {
						// String validTokenClass = validToken.substring(0,1).toUpperCase()+validToken.substring(1).toLowerCase();
						clmd.addParsed(HXConsole.getWorkspace()+".");
						clmd.addParsed(token);
						CLParser pe;
						try {
							// pe = (CLParser)(Class.forName("hextrato.console.exec."+clmd.getParsed()).newInstance());
							pe = HXConsole.getParserExec(clmd.getParsed());
						} catch (Exception e) {
							HXConsole.error(e.getMessage());
							HXConsole.error(e.getStackTrace().toString());
							throw new HXException(e.getMessage()); 
						}
						pe.parse(clmd);
						return true;
					}
					else
					*/
					//
					// WithOUT workspace
					//
				// SCRIPT CONTEXT
				String context = clmd.getContext();
				if (context.length() > 0) {
					if (clmd.getVar(context).trim().equals("")) {
						clmd.setVar(context, token);
						this.parse(clmd);
						return true;
					}
				}
				if (!clmd.getParsed().equals("")) {
					// PARTIAL EXECUTION (to be continued from ROOT)
					KCParser pe;
					try {
						// pe = (CLParser)(Class.forName("hextrato.console.exec."+clmd.getParsed()).newInstance());
						pe = KConsole.getParserExec(clmd.getParsed());
					} catch (Exception e) {
						KConsole.error(e.getMessage());
						KConsole.error(e.getStackTrace().toString());
						throw new KException(e.getMessage()); 
					}
					if (pe.partial(clmd)) {
						clmd.setParsedRoot();
						clmd.prevToken();
						return (new KCParserRoot()).parse(clmd);
					}
				}
				// FINALLY
				tokenError(clmd,token);
				return false;
			}
		}
	
		return false;
	}	
	
}
