/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.types;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class SysMLBasisTypesFullPrettyPrinter extends MCSimpleGenericTypesFullPrettyPrinter {

  protected SysMLv2Traverser traverser;

  public SysMLBasisTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    traverser = SysMLv2Mill.traverser();

    SysMLBasisTypesPrettyPrinter sysmlTypes = new SysMLBasisTypesPrettyPrinter(printer);
    traverser.setSysMLBasisHandler(sysmlTypes);
    traverser.add4SysMLBasis(sysmlTypes);

    // ############################### BOILER PLATE ###############################
    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.add4MCCollectionTypes(collectionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
    // #############################################################################
  }

  public void setTraverser(SysMLv2Traverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public SysMLv2Traverser getTraverser() {
    return traverser;
  }

}
