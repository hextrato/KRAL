package com.hextrato.kral.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.hextrato.kral.console.parser.KCMetadata;
import com.hextrato.kral.console.parser.KCParser;
import com.hextrato.kral.console.parser.KCParserRoot;
import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.util.exception.KException;

public class KConsole extends KRAL {


	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// Constant values
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	public static final String HEXTRATO_KRAL_CONSOLE_VERSION = "1.9.12";
	public static final boolean KRAL_CONSOLE_ON_VALIDATION = false;
	public static final String ROOT_PARSER_PACKAGE = "com.hextrato.kral.console.exec";
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// parse executors
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	private static Map<String,KCParser> _parserExecutorSet = new HashMap<String,KCParser>();
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void resetParser() {
		_parserExecutorSet.clear();
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	@SuppressWarnings("deprecation")
	public static KCParser getParserExec(String className) throws KException {
		String parseFullName = ROOT_PARSER_PACKAGE + ".";
		parseFullName = parseFullName + className;
		if (_parserExecutorSet.containsKey(parseFullName))
			return _parserExecutorSet.get(parseFullName);
		else {
			KCParser pe;
			try {

				String _firstPath = parseFullName.substring(0, parseFullName.lastIndexOf('.'));
				String _prevsPath = _firstPath.substring(0, _firstPath.lastIndexOf('.'));
				String _thirdPath = _prevsPath.substring(0, _prevsPath.lastIndexOf('.'));
				String _lastToken = parseFullName.substring(parseFullName.lastIndexOf('.')+1);
				String _prevToken = _firstPath.substring(_firstPath.lastIndexOf('.')+1);
				_prevToken = _prevToken.substring(0,1).toUpperCase() + _prevToken.substring(1).toLowerCase();
				String _thirdToken = _prevsPath.substring(_prevsPath.lastIndexOf('.')+1);
				_thirdToken = _thirdToken.substring(0,1).toUpperCase() + _thirdToken.substring(1).toLowerCase();
				String _className = _lastToken.substring(0,1).toUpperCase() + _lastToken.substring(1).toLowerCase();
				try {
					pe = (KCParser)(Class.forName(_firstPath+"."+_className).newInstance());
				} catch (Exception e1) {
					try {
						pe = (KCParser)(Class.forName(_firstPath+"."+_className).newInstance());
					} catch (Exception e2) {
						try {
							pe = (KCParser)(Class.forName(_prevsPath+"."+_prevToken+_className).newInstance());
						} catch (Exception e3) {
							try {
								pe = (KCParser)(Class.forName(_thirdPath+"."+_thirdToken+_prevToken+_className).newInstance());
							} catch (Exception e4) {
								pe = (KCParser)(Class.forName(_firstPath+"."+_prevToken+_className).newInstance());
							}
						}
					}
				}
				_parserExecutorSet.put(parseFullName, pe);
			} catch (Exception e) {
				KConsole.error(e.getMessage());
				KConsole.error(e.getStackTrace().toString());
				throw new KException(e.getMessage()); 
			}
			return pe;
		}
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// INIT / RESET
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void reset() throws KException {
		init();
		resetParser();
		schemata().create("kral");
		schemata().getSchema("kral").splits().create("default");
		welcome();
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// CONSOLE
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	private static void welcome() throws KException {
		if (config().get("console.echo").equals("ON")) {
			//println(" _   _ ____ _  _ _____ ____  _  _____ ___    _");
			//println("| |_| |  __\\ \\/ /_   _|    |/ \\|_   _/ _ \\ _| |_");
			//println("|  _  | |-_ |  |  | | |   \\/ _ \\ | |  (_) |_   _|");
			//println("|_| |_|____/_/\\_\\ |_| |_|\\/_/ \\_\\|_| \\___/  |_|");
			//println(" _  _  ____  _  _"); 
			println(" _   _ ____ _  _ _____ ____  _  _____ ___    ");
			println("| |_| |  __\\ \\/ /_   _|    |/ \\|_   _/ _ \\ ");
			println("|  _  | |-_ |  |  | | |   \\/ _ \\ | |  (_) |");
			println("|_| |_|____/_/\\_\\ |_| |_|\\/_/ \\_\\|_| \\___/ ");
			println(" _  _  ____  _  _"); 
			println("| |/ /|    |/ \\| |  ");
			println("|   | |   \\/ _ \\ |__ ");
			println("|_|\\_\\|_|\\/_/ \\_\\___|");
			println("");
			println("KRAL Console"+" "+HEXTRATO_KRAL_CONSOLE_VERSION);
			println("Copyright (C) www.hextrato.com");
			println((new java.util.Date()).toString());
			println("");
			println("Welcome!");
			//println("h+> @tmp/auprc.kral");
/*			
			DVector x = new DVector(new double[] {
					0.35601712879069963,-0.7474235124399087,0.20576239773401034,-0.475634000611115,-0.5883123944747833,-0.4542390153095121,-0.9474699755910452,-0.2084880372239445,0.49952323869976634,0.3717773336687203,-0.1004165631297262,-0.589499191415433,0.40867404095066356,-0.2776418411242713,-0.4058376098334733,-0.12650839350671453,0.7099789424402956,-0.752024510057459,0.6359851384982589,-0.25627392938456406,0.49217443567006147,-0.10398695974120634,0.6087801011717144,0.36276401179769546,-0.8048486446479716,0.376352921238706,0.26216470146878273,0.7889850850089302,0.00526023737447225,0.2731898545296423,-0.11503025423804428,0.7202242345813276,0.35601712879069963,-0.7474235124399087,0.20576239773401034,-0.475634000611115,-0.5883123944747833,-0.4542390153095121,-0.9474699755910452,-0.2084880372239445,0.49952323869976634,0.3717773336687203,-0.1004165631297262,-0.589499191415433,0.40867404095066356,-0.2776418411242713,-0.4058376098334733,-0.12650839350671453,0.7099789424402956,-0.752024510057459,0.6359851384982589,-0.25627392938456406,0.49217443567006147,-0.10398695974120634,0.6087801011717144,0.36276401179769546,-0.8048486446479716,0.376352921238706,0.26216470146878273,0.7889850850089302,0.00526023737447225,0.2731898545296423,-0.11503025423804428,0.7202242345813276
					});
			DVector y = new DVector(new double[] {
					0.8361591025425081,-0.8549166059526081,-0.133806213951235,-0.47087677744760964,-0.5521940461358613,-0.3378743971332053,-0.32023275987661515,-0.20874288804304347,0.6227259262949225,0.04119090985754814,0.1364993573501412,-0.5412649504253898,0.6100963388487053,-0.3338764214310881,-0.5523663051849151,0.4158721211922879,0.6508487905379953,-0.5756918688902097,0.2560887732489286,-0.27769222881276234,0.22668943303784667,-0.16142614376958525,0.6451785246495159,0.2527448834492733,-0.8862430580317459,0.3444154159786066,0.42347816303159813,0.2857783539761243,0.3685546898571342,0.6396048574260569,-0.19651273266429115,0.9478370654631343,0.8361591025425081,-0.8549166059526081,-0.133806213951235,-0.47087677744760964,-0.5521940461358613,-0.3378743971332053,-0.32023275987661515,-0.20874288804304347,0.6227259262949225,0.04119090985754814,0.1364993573501412,-0.5412649504253898,0.6100963388487053,-0.3338764214310881,-0.5523663051849151,0.4158721211922879,0.6508487905379953,-0.5756918688902097,0.2560887732489286,-0.27769222881276234,0.22668943303784667,-0.16142614376958525,0.6451785246495159,0.2527448834492733,-0.8862430580317459,0.3444154159786066,0.42347816303159813,0.2857783539761243,0.3685546898571342,0.6396048574260569,-0.19651273266429115,0.9478370654631343
					});
			DVector z = new DVector(new double[] {
					0.07149333355730367,-0.7615985300965574,-0.6474334552062282,-0.660771738049719,0.7468195685255555,0.6943117625618663,-0.8579518437970587,-0.06194161621366799,0.32995490370737734,0.2824509197612087,-0.5693268357892038,-0.17417996199472302,-0.7983270102565266,0.13898297625820977,-0.0444658080606721,0.4568067335736226,0.08353558012990604,0.2137916113859497,0.8395096153672398,0.23192483277343587,0.5334651412309573,0.12373309436918158,-0.7773472644034205,-0.5432110966973485,0.04721393874194277,-0.2410452412837858,0.5532534640905963,-0.03355152165451899,-0.1990615501020087,0.21268537768993045,-0.6990382343051699,-0.6460440234614545,0.07149333355730367,-0.7615985300965574,-0.6474334552062282,-0.660771738049719,0.7468195685255555,0.6943117625618663,-0.8579518437970587,-0.06194161621366799,0.32995490370737734,0.2824509197612087,-0.5693268357892038,-0.17417996199472302,-0.7983270102565266,0.13898297625820977,-0.0444658080606721,0.4568067335736226,0.08353558012990604,0.2137916113859497,0.8395096153672398,0.23192483277343587,0.5334651412309573,0.12373309436918158,-0.7773472644034205,-0.5432110966973485,0.04721393874194277,-0.2410452412837858,0.5532534640905963,-0.03355152165451899,-0.1990615501020087,0.21268537768993045,-0.6990382343051699,-0.6460440234614545
					});
			DVector w = new DVector(new double[] {
					-0.7157112475018456,-0.861058456139004,-0.415134447144285,-0.3016158952364893,0.033958988315791375,0.20066802292138686,-0.1698787180403342,0.31278254805642747,0.49402516879613195,-0.5625713438736488,0.17371088966762438,-0.18684182602088084,0.1277418199283429,-0.10560458487861203,-0.1455832649324519,0.5893703859294616,0.52953529452847,0.927616032215177,0.41466606921608345,0.14270822499896496,-0.6185201326391815,0.7884491067283644,-0.6583660212224692,-0.27808430384122723,0.9478357127806709,-0.8567324074008811,-0.27815705199897356,-0.437364281062963,-0.18699081060765338,-0.05451164881432076,-0.16160122404814586,-0.6928032392493756,-0.7157112475018456,-0.861058456139004,-0.415134447144285,-0.3016158952364893,0.033958988315791375,0.20066802292138686,-0.1698787180403342,0.31278254805642747,0.49402516879613195,-0.5625713438736488,0.17371088966762438,-0.18684182602088084,0.1277418199283429,-0.10560458487861203,-0.1455832649324519,0.5893703859294616,0.52953529452847,0.927616032215177,0.41466606921608345,0.14270822499896496,-0.6185201326391815,0.7884491067283644,-0.6583660212224692,-0.27808430384122723,0.9478357127806709,-0.8567324074008811,-0.27815705199897356,-0.437364281062963,-0.18699081060765338,-0.05451164881432076,-0.16160122404814586,-0.6928032392493756
					});
			println("x = "+x.magnitude());
			println("y = "+y.magnitude());
			println("z = "+z.magnitude());
			println("w = "+w.magnitude());
			println("x,y = "+x.arc(y));
			println("z,w = "+w.arc(z));
			/*
			if (KRAL_CONSOLE_ON_VALIDATION) {
				println("");
				println("Samples:");
				println("h+> @sample/neural/learn_xor.h+");
				println("h+> @sample/ker/infohealth.demograph/_main_.h+");
				println("h+> @sample/ker/infohealth.pregnancy/_main_.h+");
				println("h+> @sample/ker/infohealth.bpa/_main_.h+");
				println("h+> @sample/ker/uci.mushroom/_main_.h+");
				println("h+> ");
				//println("h+> @sample/test_graph.h+");
				//println("h+> @sample/test_graph0.h+");
				//println("h+> ");
				println("h+> @sample/ker/uci.mushroom/001LIN-full.h+");
				println("h+> @sample/ker/uci.mushroom/001LIN-split.h+");
				println("h+> @sample/ker/uci.mushroom/ker_test_neural.h+");
				println("h+>");
				println("h+> @sample/ker/uci.mushroom.noclass/001LIN-full.h+");
				println("h+> @sample/ker/uci.mushroom.noclass/001LIN-split.h+");
				println("h+> @sample/ker/uci.mushroom.noclass/ker_test_neural.h+");
				println("h+>");
				println("h+> @sample/ker/infohealth.demographic/001LIN-full.h+");
				println("h+> @sample/ker/infohealth.demographic/001LIN-split.h+");
				println("h+> @sample/ker/infohealth.demographic/ker_test_neural.h+");
				println("h+>");
				println("h+> @sample/ker/infohealth.demographic.noSocialGroup/001LIN-full.h+");
				println("h+> @sample/ker/infohealth.demographic.noSocialGroup/001LIN-split.h+");
				println("h+> @sample/ker/infohealth.demographic.noSocialGroup/ker_test_neural.h+");
				println("h+>");
				println("h+> @sample/ker/infohealth.pregnancy/001LIN-full.h+");
				println("h+> @sample/ker/infohealth.pregnancy/001LIN-split.h+");
				println("h+> @sample/ker/infohealth.pregnancy/ker_test_neural.h+");
				println("h+> ");
				println("h+> @sample/ker/infohealth.pregnancy.noclass/001LIN-full.h+");
				println("h+> @sample/ker/infohealth.pregnancy.noclass/001LIN-split.h+");
				println("h+> @sample/ker/infohealth.pregnancy.noclass/ker_test_neural.h+");
				println("h+> ");
				println("h+> @sample/ker/infohealth.bpa/001LIN-full.h+");
				println("h+> @sample/ker/infohealth.bpa/001LIN-split.h+");
				println("h+> @sample/ker/infohealth.bpa/ker_test_neural.h+");
				println("h+>");
			}
			*/
			/*
			KContinuous c = new KContinuous();
			c.setMin(0);
			c.setMax(1);
			println(c.getVector(0.5, 2).toString());
			println(c.getVector(0.5, 2, Math.sqrt(2)).toString());
			println(c.getVector(0.5, 3).toString());
			println(c.getVector(0.5, 4).toString());
			println(c.getVector(0.5, 5).toString());
			println(c.getVector(0.9, 5).toString());
			
			println(Double.toString(c.getValue( new DVector(new double[] { 0,1 } ) ) ) );
			println(Double.toString(c.getValue( new DVector(new double[] { 1,1 } ) ) ) );
			println(Double.toString(c.getValue( new DVector(new double[] { 1,0 } ) ) ) );
			println(Double.toString(c.getValue( new DVector(new double[] { 0.9,-0.1 } ) ) ) );
			println(Double.toString(c.getValue( new DVector(new double[] { 0.8,-0.2 } ) ) ) );
			println(Double.toString(c.getValue( new DVector(new double[] { 0.7,-0.3 } ) ) ) );
			println(Double.toString(c.getValue( new DVector(new double[] { 0.52,0.68,0.9,0.7,0.14,0.94,0.1,0.32} ) , 5) ) );
			*/
		}
	}

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// iteractive console
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void prompt () throws KException {
		// String prompt = "h+";
		// String prompt = "kral";
		String prompt = "";
		String schema = KConsole.schemata().getCurrentName();
		if (!schema.equals("")) prompt = KConsole.schemata().getCurrentName(); 
		System.out.print("\n"+prompt+"> ");
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static boolean iteract () throws Exception {
		prompt();
		sc = new Scanner(System.in);
		String cmdlin = sc.nextLine();
		if (!cmdlin.trim().isEmpty())
			runLine(cmdlin);
		return true;
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static boolean runLine (String cmdlinInput) throws KException {
		String cmdlin = cmdlinInput;
		if (!cmdlin.trim().equals("") && !cmdlin.trim().startsWith("#") && !cmdlin.trim().startsWith("//") && !cmdlin.trim().startsWith(">>")) {
			if (cmdlin.trim().startsWith("@")) {
				String script = cmdlin.trim().substring(1).trim();
				cmdlin = "run ? script="+script;
			}
			String secondPart = "";
			if (cmdlin.indexOf(";;")>0 && !cmdlin.contains("{")) {
				secondPart = cmdlin.substring(cmdlin.indexOf(";;")+2).trim();
				cmdlin = cmdlin.substring(0, cmdlin.indexOf(";;")).trim();
			}
			KCMetadata clmd = new KCMetadata(cmdlin);
			try {
				(new KCParserRoot()).parse(clmd);
			} catch (KException e) {
				KConsole.error(e);
				return false;
			}
			if (!secondPart.isEmpty()) return runLine(secondPart);
		}
		return true;
	}


	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// MAIN
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void main (String args[]) throws Exception {
		reset();
		if (args.length > 0)
			// if (!(args[0].trim().equals("")))
			for (String cmdLine : args) {
				// runLine(args[0].trim());
				runLine(cmdLine.trim());
			}
		while (iteract()) {};
	}
	

	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =  
	// ?
	// UNDER REVIEW 
	// ?
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	
	//
	// Input / Output
	//
	private static Scanner sc;
	
	public static void feedback (String message) throws KException {
		if (message != null) { 
			last().set("_last.feedback",message);
			if (KConsole.config().get("console.feedback").equals("ON")) KConsole.println("\n"+message);
		}
	}
	public static void echo (String message) throws KException {
		if (message != null) { 
			last().set("_last.echo",message);
			if (KConsole.config().get("console.echo").equals("ON") || message.trim().startsWith(">>")) {
				// HXConsole.prompt();
				if (message.trim().startsWith(">>"))
					KConsole.println(message.trim().substring(2).trim());
				else
					KConsole.println(message);
			}
		}
	}
	public static void error (String message) throws KException {
		if (message != null) { 
			last().set("_last.error",message);
			KConsole.println("\n"+"h+ERR: "+message);
		}
	}
	
	private static String _trace = "";
	public static String trace() { return _trace; }
	public static void error (KException e) throws KException {
		KConsole.error(e.getMessage());
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer );
		e.printStackTrace( printWriter );
		printWriter.flush();
		_trace = writer.toString();
		// HxConsole.println();
	}
	public static void output (String message) throws KException {
		last().set("_last.output",message);
		KConsole.println(message);
	}
	public static void println (String message) { // println ONLY
		System.out.println(message);
	}
	

	//
	// workspace
	//
	/*
	private static String _workspace = "";
	public static void setWorkspace(String workspace) {
		_workspace = workspace;
	}
	public static String getWorkspace() {
		return _workspace;
	}
	*/
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	/*
		runLine("@examples/wine/learn_red.hextra");
		runLine("@examples/mushroom/mushroom_graph_sample.hextra");
		runLine("@examples/embedding/test-1.hextra");
		runLine("var a declare vector");
		runLine("var a set [-1,-2]");
		runLine("var a show");
		runLine("var b declare vector");
		runLine("var b set [0.5,0.3]");
		runLine("var b show");
		*/
		
		/*
		 * Continuous TEST
		 *
		HXContinuous axis = new HXContinuous(3);
		axis.setBoundaries(0, 50);
		System.out.println( axis.calcValue( (new HXVector(3)).setValues(new double[] {1,0,0} ) ));
		System.out.println( axis.calcValue( (new HXVector(3)).setValues(new double[] {0,1,0} ) ));
		System.out.println( axis.calcValue( (new HXVector(3)).setValues(new double[] {0,0,1} ) ));
		System.out.println( axis.calcValue( (new HXVector(3)).setValues(new double[] {-1,0,0} ) ));
		System.out.println( axis.calcValue( (new HXVector(3)).setValues(new double[] {0,-1,0} ) ));
		System.out.println( axis.calcValue( (new HXVector(3)).setValues(new double[] {0,0,-1} ) ));
		System.out.println("===");
		HXVector test = new HXVector(3).setValues(new double[] { 0.5, 0.5, 0.1 });
		System.out.println( axis.calcValue( test ));
		System.out.println( axis.calcAccuracy( test ) );
		System.out.println("---");
		for (double i=0; i<=5; i += 0.25) {
			HXVector v = axis.calcVector(i);
			System.out.println( Double.toString(i) + " => " + v.toString() + " => " + axis.calcValue(v) );
		}
		System.out.println("***");
		System.out.println( axis.calcVector(0.9) );
		System.out.println( axis.calcVector(1.1) );
		System.exit(0);
	*/ 
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	
	/*
	 * COMMAND LINE ARGUMENTS
	 * 
		for (int i=0; i < args.length; i++) {
			System.out.println("args["+i+"] = "+args[i]);
			if (args[i].equals("--run")) par_datatype  = args[i+1];  
			if (args[i].equals("-i")) par_idatafile = args[i+1];  
			if (args[i].equals("-g")) par_groups = Integer.valueOf(args[i+1]);  
			if (args[i].equals("-d")) par_docspf = Integer.valueOf(args[i+1]);  
		}
	}
	*/

}


