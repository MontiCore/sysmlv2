package de.monticore.lang.sysml.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlexpressionsbasis._ast.ASTExpressionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._ast.*;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerExpressions2 implements SysMLExpressionsHandler {
	private IndentPrinter printer;
	private SysMLExpressionsTraverser traverser;

	public PrettyPrinterHandlerExpressions2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLExpressionsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLExpressionsTraverser realThis) {
		this.traverser = realThis;
	}

//	@Override
//	public void handle(ASTSysMLConditionalExpression node) {
//		node.getCondition().accept(getTraverser());
//		printer.print("?");
//		for (ASTExpression e :
//			node.getDoOnTrueList()) {
//			e.accept(getTraverser());
//		}
//		printer.print(":");
//		for (ASTExpression e :
//			node.getDoOnFalseList()) {
//			e.accept(getTraverser());
//		}
//	}

	@Override
	public void handle(ASTNullCoalescingExpression node) {
		node.getIfNotNull().accept(getTraverser());
		printer.print("?? ");
		node.getIfNullReturn().accept(getTraverser());
	}

	@Override
	public void handle(ASTOrExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("| ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void handle(ASTXorExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("^ ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void handle(ASTAndExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("& ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void handle(ASTClassificationExpression node) {
		node.getExpression(0).accept(getTraverser());
		printer.print("instanceof ");
		node.getExpression(1).accept(getTraverser());
	}

	@Override
	public void handle(ASTMultiplicativeExpression node) {
		node.getLeft().accept(getTraverser());
		if (node.isTimes()) {
			printer.print("* ");
		}
		if (node.isDivide()) {
			printer.print("/ ");
		}
		if (node.isExp()) {
			printer.print("** ");
		}
		node.getRight().accept(getTraverser());
	}

	@Override
	public void handle(ASTUnitsExpression node) {
		node.getExpression(0).accept(getTraverser());
		printer.print("@ [");
		node.getExpression(1).accept(getTraverser());
		printer.print("]");
	}

	@Override
	public void handle(ASTUnaryExpression node) {
		if (node.isPresentPlus()) {
			printer.print("+ ");
		}
		if (node.isPresentMinus()) {
			printer.print("- ");
		}
		if (node.isPresentNot()) {
			printer.print("! ");
		}
		if (node.isPresentTilde()) {
			printer.print("~ ");
		}
		node.getExpression().accept(getTraverser());
	}

	@Override
	public void handle(ASTSequenceAccessExpression node) {
		node.getExpression(0).accept(getTraverser());
		printer.print("[");
		for (int i = 1; i < node.getExpressionList().size(); i++) {
			node.getExpression(i).accept(getTraverser());
		}
		printer.print("]");
	}

	@Override
	public void handle(ASTPrimaryExpression node) {
		node.getExpression().accept(getTraverser());
		for (ASTPrimaryExpressionPart p :
			node.getPrimaryExpressionPartList()) {
			p.accept(getTraverser());
		}
	}

	@Override
	public void handle(ASTPrimaryExpressionPart node) {
		printer.print("-> " + node.getSysMLName().getNameForPrettyPrinting() + " ");
		for (ASTBodyMember b :
			node.getBodyMemberList()) {
			b.accept(getTraverser());
		}
	}

	@Override
	public void handle(ASTBodyExpression node) {
		if (!node.isEmptyBodyParameterMembers()) {
			for (ASTBodyParameterMember b :
				node.getBodyParameterMemberList()) {
				b.accept(getTraverser());
			}
			printer.print("(");
			for (ASTExpressionMember e :
				node.getExpressionMemberList()) {
				e.accept(getTraverser());
			}
			printer.print(") ");
		} else {
			for (ASTExpressionTyping et :
				node.getExpressionTypingList()) {
				et.accept(getTraverser());
			}
		}
	}

	@Override
	public void handle(ASTInvocationExpression node) {
		node.getFeatureTyping().accept(getTraverser());
		printer.print("(");
		node.getTuple().accept(getTraverser());
		printer.print(") ");
	}

	@Override
	public void handle(ASTPositionalTuple node) {
		for (int i = 0; i < node.getExpressionMemberList().size(); i++) {
			node.getExpressionMember(i).accept(getTraverser());
			if (i + 1 < node.getExpressionMemberList().size()) {
				printer.print(", ");
			}
		}
	}

	@Override
	public void handle(ASTNamedTuple node) {
		for (int i = 0; i < node.getNamedExpressionMemberList().size(); i++) {
			node.getNamedExpressionMember(i).accept(getTraverser());
			if (i + 1 < node.getNamedExpressionMemberList().size()) {
				printer.print(", ");
			}
		}
	}

	@Override
	public void handle(ASTSequenceConstructionExpression node) {
		if (!node.isPresentExpression()) {
			printer.print("{}");
		} else {
			printer.println("{");
			printer.indent();
			if (node.isPresentFromToDigitsDotDot()) {
				printer.print(node.getFromToDigitsDotDot() + " ");
				node.getExpression().accept(getTraverser());
			} else {
				node.getExpression().accept(getTraverser());
				if (node.isPresentSequenceElementList()) {
					printer.print(", ");
					node.getSequenceElementList().accept(getTraverser());
				}
			}
			printer.println("");
			printer.unindent();
			printer.println("}");
		}
	}

	@Override
	public void handle(ASTSequenceElementList node) {
		node.getExpression().accept(getTraverser());
		if (node.isPresentSequenceElementList()) {
			printer.print(", ");
			node.getSequenceElementList().accept(getTraverser());
		}
	}

	@Override
	public void handle(ASTSysMLQueryPathExpression node) {
		node.getQueryHeadExpression().accept(getTraverser());
		if (node.isPresentFirstBodyMember()) {
			printer.print("[");
			node.getFirstBodyMember().accept(getTraverser());
			printer.print("]");
		}
		if (!node.isEmptySysMLQueryPathExpressionParts()) {
			for (ASTSysMLQueryPathExpressionPart q :
				node.getSysMLQueryPathExpressionPartList()) {
				q.accept(getTraverser());
			}
		}
	}

	@Override
	public void handle(ASTSysMLQueryPathExpressionPart node) {
		printer.print("/ ");
		node.getQueryNameExpression().accept(getTraverser());
		if (node.isPresentBodyMember()) {
			printer.print("[");
			node.getBodyMember().accept(getTraverser());
			printer.print("] ");
		}
	}
}
