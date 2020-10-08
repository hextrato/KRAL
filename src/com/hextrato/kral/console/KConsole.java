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
import com.hextrato.kral.core.util.exception.KException;

public class KConsole extends KRAL {


	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        
	// Constant values
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	public static final String HEXTRATO_KRAL_CONSOLE_VERSION = "3.0.1";
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
		
			// runLine("@tmp/REVIEW.kral");
			//println("h+> @tmp/auprc.kral");
			//println("h+> @tmp/matrix.kral");

			/*		
			DVector a = new DVector(new double[] {
					0.2356	,0.2380	,0.2361	,0.2353	,0.2121	,0.2306

					});
			DVector b = new DVector(new double[] {
					0.3793	,0.1145	,0.3255	,0.1595	,0.1578	,0.1397

					});
			println(""+a.PearsonsCorrelation(b));

			DVector a = new DVector(new double[] {
					1,1,1,1,1,1,1,1,1,1
					});
			DVector b = new DVector(new double[] {
					-1,-1,0,1,1,1,1,1,1,0
					});
			DVector x = new DVector(new double[] {
					-0.030623985803383744,0.025425564762439627,0.42312843358991203,0.16932773562937325,-0.657347973973557,0.2855459790728329,-0.054057864123314384,0.6920760968479229,0.2650335852197282,0.4166721349674723,0.18070053282210058,0.883077906855119,-0.22553825591433943,-0.5230292322893535,-0.24689872634134719,-0.48745029905359816,0.4321546592538962,0.6748261101564544,-0.8470562507110672,-0.9424246875729554,0.7201426142348653,-0.15023835845828518,-0.12878049692130708,0.3859410394853472,0.18272713270947138,0.11793700889550655,0.95,-0.7464192962150612,-0.0694771867548337,0.20800739764408566,0.19973573357241595,0.7329061160991448,0.15974481042347108,-0.6074032716668121,-0.16670935100293993,0.5174012016374258,-0.21267483906512746,0.949699288950238,0.9437984213220533,-0.6525187744391274,-0.029748699473914918,-0.13891021655644992,0.20824331900667625,0.141858936958238,0.41045578299551405,-0.7686327407900142,0.6465760681861181,-0.7096431686043514,0.12642644412546686,-0.38914454439582263,0.08925035771201317,-0.7872425008709607,0.024028755719009146,0.23836771877954738,0.17112540865841663,-0.21658856843428478,-0.13477178327154377,0.011170806625222756,0.865322505692657,0.05612914117924132,-0.5995176920032348,-0.5017237328837709,0.7782408272510071,0.6149688909331688
					});
			DVector y = new DVector(new double[] {
					0.5417710643386542,0.13934657976801915,-0.253031947472197,0.4859467268845004,0.22520883505949107,0.9224129848176573,0.6466712276563251,0.38661858867383125,-0.3710281420163116,0.4790200387489298,-0.5200406026323245,0.555445651787365,-0.8484495874227501,-0.95,-0.6775999045757429,0.21660653975250996,-0.23448486034320853,-0.0021720619745175763,-0.3630733957112226,-0.95,-0.0298250337420868,-0.014603528569809813,-0.002038228092866044,-0.5593081722198212,0.7020808713105833,0.95,0.95,-0.1550078459842591,0.4290890580184149,-0.5592451461814069,-0.3708133015464377,-0.9037269869801395,-0.95,-0.28563661811163416,0.44150017585488716,-0.45362373423800534,-0.3925601942747555,0.07237472398939511,-0.6119166327912882,-0.5463689969511896,0.056272732028294914,-0.6110366958340989,0.41204129700418846,-0.48861113773318154,-0.3647329036253167,0.3994161699137033,-0.1948605285963924,-0.28518438400525803,-0.1525749940847458,0.10304563777264245,-0.11367334436062822,-0.5985838943136693,-0.34093917834408805,-0.025078801676623644,-0.25991926131964976,0.41856441838996516,0.44228612676689955,-0.47960627484221807,-0.22891872809688285,-0.17980383544484324,-0.05249146035824249,0.1563104233350051,-0.9346842102719036,0.24104960483644503
					});
			DVector z = new DVector(new double[] {
					0.03312208346622876,0.09366563886014038,-0.059852795555635885,0.3915088657147376,-0.017743483490685514,0.586613132773705,-0.2957350347627254,0.21047556214105664,-0.14790290573727632,-0.20762515308401622,-0.3186286704732172,0.3487554228890845,0.056223717946320195,0.0761582924646738,-0.46165331872660154,-0.05984915766490585,0.35398355452872404,0.1819166654756275,0.6010036410910813,-0.95,0.669146214477813,-0.27264641876706347,-0.7627972373488803,0.07226902131953948,0.19738691601433672,-0.35718326008232093,0.7646875142746046,-0.8551407333474254,0.7632059994299504,0.29020019075378695,-0.28976938005919567,0.95,0.95,-0.405774173380078,0.8043119040727905,-0.10273962283705497,-0.95,0.021141630632361613,0.1050903268188399,-0.95,0.435267636605807,0.5595466254910842,-0.7220953394922767,-0.11275975185024312,-0.517484451101643,-0.43522138903974905,-0.36292358936636476,-0.5936767348337492,0.7337461017909415,0.2872249943128222,0.5803382303953294,-0.3433223219276534,-0.035807870458421695,-0.24765864643954993,-0.95,-0.051679764980705374,-0.607953425422802,-0.043645301715243605,0.6187772758014609,-0.4800301255297412,-0.22400355623100227,-0.08735571174456527,0.5496950036201353,-0.21914070571503552
					});
			DVector w = new DVector(new double[] {
					-0.4326496209259687,0.3180315592985599,0.11032618778794885,-0.95,-0.23620995623807828,-0.42657916416990654,0.4839750130370816,0.34742783629565377,0.43596677976706727,0.7864603982247172,-0.47264253453720223,0.8766218412108193,-0.052903929506189495,0.07652745042559078,0.24835975883675138,-0.3704528641354169,0.37814878646347555,0.08017736738566263,0.039892782105442295,-0.95,-0.27176373598275644,0.5737078399991181,0.06157710217264655,-0.2267575263291439,-0.7758373963673543,0.2835611963873433,0.47166812639854666,0.12325013456490454,0.10892015595323397,0.705550747213379,-0.4119618824208598,0.95,0.95,0.025386963879687512,-0.7388626947772826,0.15386357119923857,-0.23293164286289558,-0.07534337954874636,-0.2341825491287578,-0.8340868920970527,-0.545731558369216,-0.2127359876754404,0.13510611691790664,0.14021833540797593,0.2430190525618708,-0.5188816402500972,-0.06581454001549669,0.4214377953226421,0.7779366553866668,-0.06584539492264349,-0.7261042932962788,-0.2968867709811253,0.7064333992492333,0.13222277599119403,0.25009278345141844,-0.1992820417132518,-0.8630696159688998,0.6304329279256131,-0.436754394230192,-0.4426554849203973,-0.95,-0.3405371593161993,-0.3659256871125843,-0.48220102116615227
					});
			DVector v = new DVector(new double[] {
					0.31600777183139844,-0.1926000336323949,0.03353919555808146,-0.7031014553531308,-0.31695317027993314,-0.6326641518187516,0.2319030727867847,0.9255080514686947,-0.5195462689450905,-0.4465380461666213,0.5001432446129443,0.11791278073427891,-0.46483769980549644,0.1131159035164606,0.8724432682175735,-0.11881935761139249,0.05582077378911014,0.30338542813530667,-0.36673917396565425,-0.8710890101577241,0.31905338064281996,0.4709708404827165,-0.13655988932527177,-0.7844276114380454,-0.6170678057477275,-0.6099307162987047,0.1588829397604757,-0.6041065905290386,0.6594887002569473,0.6451819738924619,-0.0748956489659386,0.95,0.95,0.058841457762142745,-0.17881124824700398,-0.22403884085207448,0.026526664183395325,-0.6831814083498153,0.32203363772132,-0.95,-0.12327148394318005,0.0443144001321384,0.5314946010717255,0.7383384790643448,-0.3112865393962196,-0.6668121088849405,0.2895552235257962,-0.40124049113664995,0.7588551192000357,0.39504759406991213,0.12492484644123815,-0.17757391427026956,-0.34918652623427926,-0.4511661484929643,0.537842391529692,0.5664461980138802,0.27512841147949585,0.1390565022945525,-0.08018694366466982,0.9230438781129784,-0.13161131385654914,-0.4526693444633755,-0.12381468443547711,0.4525991664309214
					});
			println("x = "+x.magnitude());
			println("y = "+y.magnitude());
			println("z = "+z.magnitude());
			println("w = "+w.magnitude());
			println("v = "+v.magnitude());
			println("x,z = "+x.arc(z));
			println("x,w = "+x.arc(w));
			println("x,v = "+x.arc(v));
			println("y,z = "+y.arc(z));
			println("y,w = "+y.arc(w));
			println("y,v = "+y.arc(v));
			println("a,b = "+a.arc(b));
			println("a,b = "+a.cosine(b));
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
	// Console PROMPT
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =        

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -         
	public static void prompt () throws KException {
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
		if (args.length > 0) {
			for (String cmdLine : args) {
				runLine(cmdLine.trim());
			}
		}
		while (iteract()) {};
	}
	
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
	
}


