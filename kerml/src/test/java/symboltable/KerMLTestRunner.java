package symboltable;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml.KerMLTool;
import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kermlelements._ast.ASTDatatype;
import de.monticore.lang.kermlelements._ast.ASTKerMLSpecialization;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.lang.kermlparts.symboltable.adapters.Datatype2TypeSymbolAdapter;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsArtifactScope;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class KerMLTestRunner {

  public static class DatatypeExtractor implements KerMLElementsVisitor2 {
    private final IOOSymbolsArtifactScope exportScope;
    private final Map<ASTDatatype, Datatype2TypeSymbolAdapter> exportedTypes =
        new LinkedHashMap<>();
    private final Map<String, Datatype2TypeSymbolAdapter> exportedTypesByName =
        new LinkedHashMap<>();

    public DatatypeExtractor(IOOSymbolsArtifactScope exportScope) {
      this.exportScope = exportScope;
    }

    @Override
    public void visit(ASTDatatype node) {
      if (node.isPresentSymbol()) {
        var type = new Datatype2TypeSymbolAdapter(node.getSymbol());
        type.setPackageName(exportScope.getPackageName());
        exportScope.add(type);
        exportedTypes.put(node, type);
        exportedTypesByName.put(type.getName(), type);
      }
    }

    /**
     * Connects specializations after all datatypes have been collected. Only
     * datatypes exported from this model are connected; imported KerML base
     * types remain outside this self-contained ScalarValues symbol table.
     */
    public void completeSuperTypes() {
      exportedTypes.forEach((node, type) -> node.getKerMLRelationClauseList().stream()
          .filter(ASTKerMLSpecialization.class::isInstance)
          .map(ASTKerMLSpecialization.class::cast)
          .flatMap(specialization -> specialization.getSpecializedList().stream())
          .map(superTypeName -> exportedTypesByName.get(superTypeName.getBaseName()))
          .filter(java.util.Objects::nonNull)
          .map(SymTypeExpressionFactory::createTypeObject)
          .forEach(type::addSuperTypes));
    }
  }

  public static void main(String[] args) {
    Log.init();
    Log.enableFailQuick(false);

    try {
      KerMLTool tool = new KerMLTool();
      tool.init();
      OOSymbolsMill.init();

      String modelPath = "kerml/src/test/resources/KernelDataTypeLibrary/ScalarValues.kerml";
      String OutputPath = "language/src/main/resources/ScalarValues.kermlsym";

      ASTKerMLModel ast = tool.parse(modelPath);

      if (ast != null) {
        tool.createSymbolTable(ast);

        IOOSymbolsArtifactScope exportScope = OOSymbolsMill.artifactScope();

        exportScope.setName("");
        exportScope.setPackageName("ScalarValues");

        var traverser = KerMLMill.traverser();
        var datatypeExtractor = new DatatypeExtractor(exportScope);
        traverser.add4KerMLElements(datatypeExtractor);
        ast.accept(traverser);
        datatypeExtractor.completeSuperTypes();

        OOSymbolsSymbols2Json symbols2Json = new OOSymbolsSymbols2Json();
        String jsonString = symbols2Json.serialize(exportScope);

        File outFile = new File(OutputPath);
        outFile.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(outFile);
        writer.write(jsonString);
        writer.close();

        System.out.println("\nSymboltable was generated");
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
