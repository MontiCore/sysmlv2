package symboltable;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml.KerMLTool;
import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kermlelements._ast.ASTDatatype;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsArtifactScope;
import de.monticore.lang.kermlparts.symboltable.adapters.Datatype2TypeSymbolAdapter;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.FileWriter;

public class KerMLTestRunner {

  public static class DatatypeExtractor implements KerMLElementsVisitor2 {
    private IBasicSymbolsArtifactScope exportScope;

    public DatatypeExtractor(IBasicSymbolsArtifactScope exportScope) {
      this.exportScope = exportScope;
    }

    @Override
    public void visit(ASTDatatype node) {
      if (node.isPresentSymbol()) {
        exportScope.add(new Datatype2TypeSymbolAdapter(node.getSymbol()));
      }
    }
  }

  public static void main(String[] args) {
    Log.init();
    Log.enableFailQuick(false);

    try {
      KerMLTool tool = new KerMLTool();
      tool.init();
      BasicSymbolsMill.init();

      String modelPath = "kerml/src/test/resources/KernelDataTypeLibrary/ScalarValues.kerml";
      String jsonOutputPath = "kerml/src/test/resources/symbols/ScalarValues.json";

      ASTKerMLModel ast = tool.parse(modelPath);

      if (ast != null) {
        tool.createSymbolTable(ast);

        IBasicSymbolsArtifactScope exportScope = BasicSymbolsMill.artifactScope();
        exportScope.setName("");
        exportScope.setPackageName("ScalarValues");

        var traverser = KerMLMill.traverser();
        traverser.add4KerMLElements(new DatatypeExtractor(exportScope));
        ast.accept(traverser);

        BasicSymbolsSymbols2Json symbols2Json = new BasicSymbolsSymbols2Json();
        String jsonString = symbols2Json.serialize(exportScope);

        File outFile = new File(jsonOutputPath);
        outFile.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(outFile);
        writer.write(jsonString);
        writer.close();

        System.out.println("\nJSON-File was generated");
        System.out.println(outFile.getAbsolutePath());
      } else {
        System.err.println("\nModel couldnt be parsed. Check if Model exists.");
      }

    } catch (Exception e) {
      System.err.println("\nError occured!");
      e.printStackTrace();
    }
  }
}
