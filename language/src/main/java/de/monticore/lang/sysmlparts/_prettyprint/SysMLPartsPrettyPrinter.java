package de.monticore.lang.sysmlparts._prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;

public class SysMLPartsPrettyPrinter extends SysMLPartsPrettyPrinterTOP {

  public SysMLPartsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTPartUsage node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    handleUsage("part",
        node.getName(),
        node.isPresentSysMLFeatureDirection() ? node.getSysMLFeatureDirection() : null,
        node.getSpecializationList(),
        node.getSysMLElementList());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTPortUsage node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    handleUsage("port",
        node.getName(),
        node.isPresentSysMLFeatureDirection() ? node.getSysMLFeatureDirection() : null,
        node.getSpecializationList(),
        null);

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTAttributeUsage node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentSysMLFeatureDirection()){
      getPrinter().print(node.getSysMLFeatureDirection().toString().toLowerCase() + " ");
    }

    getPrinter().print("attribute ");
    getPrinter().print(node.getName());
    if (node.getSpecializationList() != null && !node.getSpecializationList().isEmpty()){
      getPrinter().print(": ");
      if (((ASTSysMLTyping)node.getSpecializationList().get(0)).isConjugated()){
        getPrinter().print("~");
      }

      for (var i = 0; i < node.getSpecializationList().get(0).getSuperTypesList().size(); i++) {
        getPrinter().print(node.getSpecializationList().get(0).getSuperTypes(0).printType());
        if (i < node.getSpecializationList().get(0).getSuperTypesList().size() - 1){
          getPrinter().print(", ");
        }
      }
    }

    if (node.isPresentExpression()){
      getPrinter().print(" = ");
      node.getExpression().accept(getTraverser());
      getPrinter().stripTrailing();
    }

    getPrinter().stripTrailing();
    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }


  public void handleUsage(String usageType,
                          String name,
                          ASTSysMLFeatureDirection featureDirection,
                          List<ASTSpecialization> specializations,
                          List<ASTSysMLElement> sysMLElements
  ) {
    if (featureDirection != null){
      getPrinter().print(featureDirection.toString().toLowerCase() + " ");
    }

    getPrinter().print(usageType + " ");
    getPrinter().print(name);
    if (specializations != null && !specializations.isEmpty()){
      getPrinter().print(": ");
      if (((ASTSysMLTyping)specializations.get(0)).isConjugated()){
        getPrinter().print("~");
      }

      for (var i = 0; i < specializations.get(0).getSuperTypesList().size(); i++) {
        getPrinter().print(specializations.get(0).getSuperTypes(0).printType());
        if (i < specializations.get(0).getSuperTypesList().size() - 1){
          getPrinter().print(", ");
        }
      }
    }

    if (sysMLElements != null && !sysMLElements.isEmpty()){
      getPrinter().print("{ ");
      getPrinter().println();
      getPrinter().indent();
      for (var sysmlElement : sysMLElements) {
        sysmlElement.accept(getTraverser());
      }
      getPrinter().print("}");
      getPrinter().unindent();
      getPrinter().println();

    } else {
      getPrinter().stripTrailing();
      getPrinter().println(";");
    }
  }
}
