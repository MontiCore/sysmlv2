/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlconstraints._prettyprint;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLReference;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintReference;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementSatisfyReference;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementVerifyReference;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Iterator;

public class SysMLConstraintsPrettyPrinter
    extends SysMLConstraintsPrettyPrinterTOP {

  public SysMLConstraintsPrettyPrinter(IndentPrinter printer,
      boolean printComments) {
    super(printer, printComments);
  }

  public void handle(ASTConstraintUsage node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    node.getModifier().accept(getTraverser());

    node.getUserDefinedKeywordList().forEach(n -> n.accept(getTraverser()));

    if (node.isRequire()) {
      getPrinter().print("require ");
    } else if (node.isAssume()) {
      getPrinter().print("assume ");
    } else if (node.isAssert()) {
      getPrinter().print("assert ");
    }

    if (node.isNot()) { // opt: 0 req: 0
      getPrinter().print("not ");
    }

    getPrinter().print("constraint ");

    if (node.isPresentName()) {
      getPrinter().print(node.getName() + " ");
    }

    if (node.isPresentSysMLCardinality()) {
      node.getSysMLCardinality().accept(getTraverser());
    }

    node.getSpecializationList().forEach(n -> n.accept(getTraverser()));

    getPrinter().stripTrailing();
    getPrinter().print("(");

    Iterator<ASTSysMLParameter> iter_sysMLParameter = node.getSysMLParameterList().iterator();
    if (iter_sysMLParameter.hasNext()) {
      iter_sysMLParameter.next().accept(getTraverser());
      while (iter_sysMLParameter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_sysMLParameter.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    getPrinter().println("{ ");
    getPrinter().indent();

    node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));

    if (node.isPresentExpression()) {
      node.getExpression().accept(getTraverser());
    }

    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(ASTConstraintReference node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isRequire()) {
      getPrinter().print("require ");
    } else if (node.isAssume()) {
      getPrinter().print("assume ");
    } else if (node.isAssert()) {
      getPrinter().print("assert ");
    }

    if (node.isNot()) { // opt: 0 req: 0
      getPrinter().print("not ");
    }

    node.getUserDefinedKeywordList().forEach(n -> n.accept(getTraverser()));

    node.getMCQualifiedName().accept(getTraverser());

    if (node.isPresentSysMLCardinality()) {
      node.getSysMLCardinality().accept(getTraverser());
    }

    // We remove the specialization because it is already specified through the QN
    node.getSpecializationList()
        .stream()
        .filter(it ->
            !(it instanceof ASTSysMLReference &&
            it.sizeSuperTypes() == 1 &&
            it.getSuperTypes(0).printType().equals(node.getMCQualifiedName().getQName())))
        .forEach(n -> n.accept(getTraverser()));

    getPrinter().stripTrailing();
    getPrinter().print("(");

    Iterator<ASTSysMLParameter> iter_sysMLParameter = node.getSysMLParameterList().iterator();
    if (iter_sysMLParameter.hasNext()) {
      iter_sysMLParameter.next().accept(getTraverser());
      while (iter_sysMLParameter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_sysMLParameter.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    getPrinter().println("{ ");
    getPrinter().indent();

    node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));

    if (node.isPresentExpression()) {
      node.getExpression().accept(getTraverser());
    }

    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  public void handle(ASTRequirementUsage node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    node.getModifier().accept(getTraverser());

    node.getUserDefinedKeywordList().forEach(n -> n.accept(getTraverser()));

    if (node.isRequire()) {
      getPrinter().print("require ");
    } else if (node.isAssume()) {
      getPrinter().print("assume ");
    } else if (node.isAssert()) {
      getPrinter().print("assert ");
    }

    if (node.isNot()) { // opt: 0 req: 0
      getPrinter().print("not ");
    }

    if (node.isSatisfy()) {
      getPrinter().print("satisfy ");
    }

    if (node.isVerify()) {
      getPrinter().print("verify ");
    }

    getPrinter().print("requirement ");

    if (node.isPresentSysMLIdentifier()) {
      node.getSysMLIdentifier().accept(getTraverser());
    }

    if (node.isPresentName()) {
      getPrinter().print(node.getName() + " ");
    }

    if (node.isPresentSysMLCardinality()) {
      node.getSysMLCardinality().accept(getTraverser());
    }

    node.getSpecializationList().forEach(n -> n.accept(getTraverser()));

    if (node.isPresentDefaultValue()) {
      node.getDefaultValue().accept(getTraverser());
    }

    if (node.isPresentSubject()) {
      getPrinter().print("by ");
      node.getSubject().accept(getTraverser());
    }

    getPrinter().stripTrailing();
    getPrinter().print("(");

    Iterator<ASTSysMLParameter> iter_sysMLParameter = node.getSysMLParameterList().iterator();
    if (iter_sysMLParameter.hasNext()) {
      iter_sysMLParameter.next().accept(getTraverser());
      while (iter_sysMLParameter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_sysMLParameter.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    getPrinter().println("{ ");
    getPrinter().indent();

    node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));

    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(ASTRequirementSatisfyReference node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    node.getModifier().accept(getTraverser());

    node.getUserDefinedKeywordList().forEach(n -> n.accept(getTraverser()));

    if (node.isRequire()) {
      getPrinter().print("require ");
    } else if (node.isAssume()) {
      getPrinter().print("assume ");
    } else if (node.isAssert()) {
      getPrinter().print("assert ");
    }

    if (node.isNot()) { // opt: 0 req: 0
      getPrinter().print("not ");
    }

    getPrinter().print("satisfy ");

    node.getReq().accept(getTraverser());

    if (node.isPresentSysMLCardinality()) {
      node.getSysMLCardinality().accept(getTraverser());
    }

    // We remove the specialization because it is already specified through the QN
    node.getSpecializationList()
        .stream()
        .filter(it ->
            !(it instanceof ASTSysMLReference &&
                it.sizeSuperTypes() == 1 &&
                it.getSuperTypes(0).printType().equals(node.getReq().getQName())))
        .forEach(n -> n.accept(getTraverser()));

    if (node.isPresentSubject()) {
      getPrinter().print("by ");
      node.getSubject().accept(getTraverser());
    }

    getPrinter().stripTrailing();
    getPrinter().print("(");

    Iterator<ASTSysMLParameter> iter_sysMLParameter = node.getSysMLParameterList().iterator();
    if (iter_sysMLParameter.hasNext()) {
      iter_sysMLParameter.next().accept(getTraverser());
      while (iter_sysMLParameter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_sysMLParameter.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    getPrinter().println("{ ");
    getPrinter().indent();

    node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));

    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  public void handle(ASTRequirementVerifyReference node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isRequire()) {
      getPrinter().print("require ");
    } else if (node.isAssume()) {
      getPrinter().print("assume ");
    } else if (node.isAssert()) {
      getPrinter().print("assert ");
    }

    if (node.isNot()) { // opt: 0 req: 0
      getPrinter().print("not ");
    }

    node.getUserDefinedKeywordList().forEach(n -> n.accept(getTraverser()));

    node.getReq().accept(getTraverser());

    if (node.isPresentSysMLCardinality()) {
      node.getSysMLCardinality().accept(getTraverser());
    }

    // We remove the specialization because it is already specified through the QN
    node.getSpecializationList()
        .stream()
        .filter(it ->
            !(it instanceof ASTSysMLReference &&
                it.sizeSuperTypes() == 1 &&
                it.getSuperTypes(0).printType().equals(node.getReq().getQName())))
        .forEach(n -> n.accept(getTraverser()));

    getPrinter().stripTrailing();
    getPrinter().print("(");

    Iterator<ASTSysMLParameter> iter_sysMLParameter = node.getSysMLParameterList().iterator();
    if (iter_sysMLParameter.hasNext()) {
      iter_sysMLParameter.next().accept(getTraverser());
      while (iter_sysMLParameter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_sysMLParameter.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    getPrinter().println("{ ");
    getPrinter().indent();

    node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));

    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

}


