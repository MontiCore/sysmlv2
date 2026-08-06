import de.monticore.lang.kerml.KerMLTool;
import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import java.io.File;

public class KerMLTestRunner {

  public static void main(String[] args) {

    // 1. Initialize the tool
    KerMLTool tool = new KerMLTool();
    tool.init();

    // 2. Define exact paths based on the root project working directory
    String modelPath = "kerml/src/test/resources/KernelDataTypeLibrary/ScalarValues.kerml";
    String jsonOutputPath = "kerml/target/symbols/ScalarValues.json";

    // 3. Parse the model
    ASTKerMLModel ast = tool.parse(modelPath);

    if (ast != null) {

      // 4. Generate the symbol table
      IKerMLArtifactScope rootScope = tool.createSymbolTable(ast);

      // 5. Ensure the target directory exists and save the JSON
      new File("kerml/target/symbols").mkdirs();
      tool.storeSymbols(rootScope, jsonOutputPath);

      System.out.println("Symbols successfully exported to: " + new File(jsonOutputPath).getAbsolutePath());

    } else {
      System.err.println("Failed to parse the model at: " + modelPath);
    }
  }
}
