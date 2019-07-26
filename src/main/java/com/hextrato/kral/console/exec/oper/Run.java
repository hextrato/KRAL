package com.hextrato.kral.console.exec.oper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hextrato.kral.console.KConsole;
import com.hextrato.kral.console.parser.KCFinder;
import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.core.util.exception.KException;

public class Run implements KCParser {

	public static Map<String,Integer> gosubs = null; 
	private static String[] lines = null;
	
	public void setContext (KCMetadata clmd) { clmd.setContext("script"); }

    public String[] readLines(String pathname, String filename) throws IOException {
    	
        FileReader fileReader = new FileReader(pathname + File.separator + filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
        	if (!line.trim().startsWith("//")) {
        		lines.add(line.trim());
        	}
        }
        bufferedReader.close();
        
        return lines.toArray(new String[lines.size()]);
    }
    
    public static void goSub(String sub) throws KException {
		// if (cmdlin.trim().toLowerCase().startsWith("gosub")) {
		// String sub = cmdlin.trim().substring(6).trim();
		if (gosubs == null)
			throw new KException("No subroutines defines");
		Integer line = gosubs.get(sub);
		if (line == null)
			throw new KException("Invalid subroutine: "+sub);
		else 
			runLines(line.intValue());
	}
	
    public static void runLines(int lineNumber) throws KException {
		while (lineNumber < lines.length) {
			String cmdlin = lines[lineNumber];
			KConsole.echo(cmdlin);
			if (!cmdlin.trim().isEmpty()) {
				if (cmdlin.trim().startsWith(":")) {
					// nothing
				} else {
					// return
					if (cmdlin.trim().toLowerCase().equals("return")) return;
					// others
					if (!KConsole.runLine (cmdlin)) {	
						KConsole.println("h+ERR: line "+(lineNumber+1));
						return;
					}
				}
				// if (HXConsole.hasError()) return true;
			}
			lineNumber++;
		}
    }
    
	public boolean exec(KCMetadata clmd) throws KException {

		String prevWorkDir = KConsole.config().get("hextrato.current.dir");
		// HXConsole.feedback("Current at: "+prevWorkDir);

		String script = KCFinder.which(clmd, "script");
		// HXConsole.feedback("Script: "+script);

		// load script
		
		String partialPath = "";
		try { partialPath = Paths.get(script).getParent().toString(); } catch (NullPointerException e) {}
		// HXConsole.feedback("Partial Path: "+partialPath);

		String nextWorkDir = prevWorkDir; 
		if (partialPath.startsWith("/") || partialPath.startsWith("\\") || (partialPath.length()>1 && partialPath.charAt(1) == ':') ) {
			nextWorkDir = partialPath;
		} else {
			if (!nextWorkDir.isEmpty() && !partialPath.isEmpty()) nextWorkDir = nextWorkDir + File.separator;
			nextWorkDir = nextWorkDir + partialPath;
		}

		// HXConsole.feedback("Next Work Path: "+nextWorkDir);

		File file = new File(Paths.get(nextWorkDir).toString());
		String scriptPath = "";
		try { scriptPath = file.getCanonicalPath().toString(); } catch (IOException e) {}

		String scriptFile = Paths.get(script).getFileName().toString();

		KConsole.feedback("Running: "+scriptFile+" @ "+scriptPath);

		String[] previousLines = lines;
		try {
			lines = readLines(scriptPath,scriptFile);
		} catch (IOException e) {
			//throw new HXException("error reading script: "+scriptFile+" at "+scriptPath);
			KConsole.error("error reading script: "+scriptFile+" at "+scriptPath);
			System.exit(1);
			//return false;
		}

		Map<String,Integer> previousGosubs = gosubs;
		
		gosubs = new HashMap<String,Integer>();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].trim().startsWith(":")) {
				gosubs.put(lines[i].trim().substring(1),i);
			}
		}
				
		KConsole.config().set("hextrato.current.dir",scriptPath);
		
		// HXConsole.feedback("Running at: "+ HXConsole.config().getProperty("hextrato.current.dir") );

		runLines(0);
		
		gosubs = previousGosubs;
		lines = previousLines;
		
		KConsole.config().set("hextrato.current.dir",prevWorkDir);
		
		// HXConsole.feedback("Back to : "+ HXConsole.config().getProperty("hextrato.current.dir") );
		
		return true;
	}
	
}

