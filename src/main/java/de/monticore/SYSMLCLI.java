package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml._symboltable.HelperSysMLSymbolTableCreator;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.parser.SysMLParserMultipleFiles;
import de.monticore.lang.sysml.prettyprint.PrettyPrinter2;
import de.monticore.lang.sysml.sysml._ast.ASTSysMLNode;
import de.monticore.lang.sysml.sysml._od.SysML2OD;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLScopeDeSer;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SYSMLCLI {

  /* Part 1: Handling the arguments and options
  /*=================================================================*/

	/**
	 * Main method that is called from command line and runs the SysML tool.
	 *
	 * @param args The input parameters for configuring the SysML tool.
	 */
	public static void main(String[] args) {
		SYSMLCLI cli = new SYSMLCLI();

		cli.run(args);
	}

	/**
	 * Processes user input from command line and delegates to the corresponding
	 * tools.
	 *
	 * @param args The input parameters for configuring the SysML tool.
	 */
	public void run(String[] args) {
		Options options = initOptions();

		try {
			// create CLI parser and parse input options from command line
			CommandLineParser clipparser = new DefaultParser();
			CommandLine cmd = clipparser.parse(options, args);

			// help: when --help
			if (cmd.hasOption("h")) {
				printHelp(options);
				// do not continue, when help is printed
				return;
			}

			// if -i input is missing: also print help and stop
			if (!cmd.hasOption("i")) {
				printHelp(options);
				// do not continue, when help is printed
				return;
			}

			String dir = cmd.getOptionValue("i");
			// parse input file, which is now available
			// (only returns if successful)
			ASTUnit model = parseDirectory(cmd.getOptionValue("i").trim());
			runDefaultCocos(model);
			ModelPath mp = new ModelPath();

			// -option path
			if (cmd.hasOption("path")) {
				for (String p : cmd.getOptionValue("path").split(" ")) {
					mp.addEntry(Paths.get(p));
				}
			}
			ISysMLArtifactScope symbolTable = buildSymbolTable(dir, model, mp);

			// print (and optionally store) symbol table
			if (cmd.hasOption("s")) {
				//storing to default directory
				Path output = Paths.get("target");
				de.monticore.symboltable.serialization.JsonPrinter.disableIndentation();
				String s = cmd.getOptionValue("symboltable", StringUtils.EMPTY);
				SysMLScopeDeSer deser = new SysMLScopeDeSer();

				if (!s.isEmpty()) {
					String symbolFile = output.resolve(s.trim()).toAbsolutePath().toString();
					storeSymbols(symbolTable, symbolFile, deser);
				} else {
					storeSymbols(symbolTable, output, deser);
				}

				//print (formatted!) symboltable to console
				de.monticore.symboltable.serialization.JsonPrinter.enableIndentation();
				System.out.println(deser.serialize(symbolTable));
			}

			// -option pretty print
			if (cmd.hasOption("pp")) {
				String path = cmd.getOptionValue("pp", StringUtils.EMPTY).trim();
				prettyPrint(model, path);
			}

			// -option syntax objects
			if (cmd.hasOption("so")) {
				String path = cmd.getOptionValue("so", StringUtils.EMPTY).trim();
				sysML2od(model, getModelNameFromFile(cmd.getOptionValue("i")), path);
			}
		} catch (ParseException e) {
			// an unexpected error from the apache CLI parser:
			Log.error("0xA7199 Could not process CLI parameters: " + e.getMessage());
		}
	}
	/*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/

	/**
	 * Prints the contents of the SysML-AST to stdout or a specified file.
	 *
	 * @param model The SysML-AST to be pretty printed
	 * @param path  The target file name for printing the SysML artifact. If empty,
	 *              *               the content is printed to stdout instead
	 */
	private void prettyPrint(ASTUnit model, String path) {
		PrettyPrinter2 pp = new PrettyPrinter2();
		String s = pp.prettyPrint(model);
		print(s, path);
	}

	/**
	 * stores the symbol table of a passed ast in a file created in the passed output directory.
	 * The file path for the stored symbol table of an SysML "abc.BasicPhone.sysml" and the output
	 * path "target" will be: "target/abc/BasicPhone.sysmlsym"
	 *
	 */
	private void storeSymbols(ISysMLArtifactScope symbolTable, Path output, SysMLScopeDeSer deser) {
		Path f = output
			.resolve(Paths.get(Names.getPathFromPackage(symbolTable.getPackageName())))
			.resolve(symbolTable.getName() + ".sysmlsym");
		String serialized = deser.serialize(symbolTable);
		print(serialized, f.toString());
	}

	/**
	 * stores the symbol table of a passed ast in a file created in the passed output directory.
	 * The file path for the stored symbol table of an SysML "abc.BasicPhone.sysml" and the output
	 * path "target" will be: "target/abc/BasicPhone.sysmlsym"
	 *
	 */
	private void storeSymbols(ISysMLArtifactScope symbolTable, String output, SysMLScopeDeSer deser) {
		String serialized = deser.serialize(symbolTable);
		print(serialized, output);
	}

	/**
	 * Creates an object diagram for the SysML-AST to stdout or a specified file.
	 *
	 * @param model The SysML-AST for which the object diagram is created
	 * @param input Input file path to derive modelname from
	 * @param path  The target file name for printing the object diagram. If empty,
	 *              the content is printed to stdout instead
	 */
	private void sysML2od(ASTUnit model, String input, String path) {
		// initialize sysML2od printer
		IndentPrinter printer = new IndentPrinter();
		MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
		ReportingRepository repository = new ReportingRepository(identifierHelper);
		SysML2OD sysml2od = new SysML2OD(printer, repository);

		// print object diagram
		String od = sysml2od.printObjectDiagram((new File(getModelNameFromFile(input))).getName(), (ASTSysMLNode) model);
		print(od, path);
	}

	/**
	 * Prints the given content to a target file (if specified) or to stdout (if
	 * the file is Optional.empty()).
	 *
	 * @param content The String to be printed
	 * @param path    The target path to the file for printing the content. If empty,
	 *                the content is printed to stdout instead
	 */
	public void print(String content, String path) {
		// print to stdout or file
		if (path.isEmpty()) {
			System.out.println(content);
		} else {
			File f = new File(path.trim());
			// create directories (logs error otherwise)
			f.getAbsoluteFile().getParentFile().mkdirs();

			FileWriter writer;
			try {
				writer = new FileWriter(f);
				writer.write(content);
				writer.close();
			} catch (IOException e) {
				Log.error("0xA7198 Could not write to file " + f.getAbsolutePath());
			}
		}
	}

	/**
	 * Processes user input from command line and delegates to the corresponding
	 * tools.
	 *
	 * @param options The input parameters and options.
	 */
	public void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		formatter.printHelp("SysML", options);
	}

	public ISysMLArtifactScope buildSymbolTable(String dir, ASTUnit models, ModelPath mp) {
		Log.info("Creating Symbol Table.", SYSMLCLI.class.getName());
		mp.addEntry(Paths.get(dir));
		HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
		return helperSysMLSymbolTableCreator.createSymboltableSingleASTUnit(models, mp);
	}

	/**
	 * Parses the contents of a given file as SysML.
	 *
	 * @param dir The path to the SysML-file as String
	 */
	public ASTUnit parseDirectory(String dir) {
		SysMLParserMultipleFiles sysMLParserMultipleFiles = new SysMLParserMultipleFiles();
		File checkingIfDirOrFileExists = new File(dir);
		if (!checkingIfDirOrFileExists.exists()) {
			Log.error("The provided input path " + dir + " does not exists. Exiting.");
			return null;
		}
		ASTUnit model = null;
		try {
			model = sysMLParserMultipleFiles.parseSingleFile(dir);
		} catch (IOException e) {
			//e.printStackTrace();
			Log.error("Could not parse all provided models.");
		}
		return model;
	}

	/**
	 * Check for default CoCos
	 *
	 * @param unit Unit for CoCos to be checked on
	 */
	public void runDefaultCocos(ASTUnit unit) {
		SysMLCoCos cocos = new SysMLCoCos();
		cocos.getCheckerForAllCoCos().checkAll(unit);
	}

	/**
	 * Extracts the model name from a given file name. The model name corresponds
	 * to the unqualified file name without file extension.
	 *
	 * @param file The path to the input file
	 * @return The extracted model name
	 */
	public String getModelNameFromFile(String file) {
		String modelName = new File(file).getName();
		// cut file extension if present
		if (modelName.length() > 0) {
			int lastIndex = modelName.lastIndexOf(".");
			if (lastIndex != -1) {
				modelName = modelName.substring(0, lastIndex);
			}
		}
		return modelName;
	}
	/*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/

	/**
	 * Initializes the available CLI options for the S4M tool.
	 *
	 * @return The CLI options with arguments.
	 */

	protected Options initOptions() {


		Options options = new Options();

		// help dialog
		options.addOption(Option.builder("h")
			.longOpt("help")
			.desc("Prints this help dialog")
			.build());

		// parse input file
		options.addOption(Option.builder("i")
			.longOpt("input")
			.argName("file")
			.hasArg()
			.desc("Reads the source file (mandatory) and parses the contents as SysML")
			.build());

		// pretty print S4M
		options.addOption(Option.builder("pp")
			.longOpt("prettyprint")
			.argName("file")
			.optionalArg(true)
			.numberOfArgs(1)
			.desc("Prints the S4M-AST to stdout or the specified file (optional)")
			.build());

		// print object diagram
		options.addOption(Option.builder("so")
			.longOpt("syntaxobjects")
			.argName("file")
			.optionalArg(true)
			.numberOfArgs(1)
			.desc("Prints an object diagram of the S4M-AST to stdout or the specified file (optional)")
			.build());

		// set path for imported symbols
		options.addOption(Option.builder("path")
			.desc("Sets the artifact path for imported symbols")
			.argName("dirlist")
			.hasArg()
			.hasArgs()
			.valueSeparator(':')
			.build());

		options.addOption(Option.builder("s")
			.longOpt("symboltable")
			.desc("Serializes and prints the symbol table to stdout, if present, the specified output file")
			.optionalArg(true)
			.numberOfArgs(1)
			.argName("file")
			.build());
		return options;
	}
}