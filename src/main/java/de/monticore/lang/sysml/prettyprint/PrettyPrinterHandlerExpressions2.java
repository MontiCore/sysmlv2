package de.monticore.lang.sysml.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysml.basics.interfaces.namesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlexpressionsbasis._ast.ASTExpressionMember;
import de.monticore.lang.sysml.basics.sysmldefault.expressions._ast.*;
import de.monticore.lang.sysml.basics.sysmldefault.expressions._visitor.ExpressionsHandler;
import de.monticore.lang.sysml.basics.sysmldefault.expressions._visitor.ExpressionsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerExpressions2 implements ExpressionsHandler {
	private IndentPrinter printer;
	private ExpressionsTraverser traverser;

	public PrettyPrinterHandlerExpressions2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public ExpressionsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(ExpressionsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTSysMLConditionalExpression node) {
		getTraverser().handle(node.getCondition());
		printer.print("?");
		for (ASTExpression e :
			node.getDoOnTrueList()) {
			getTraverser().handle(e);
		}
		printer.print(":");
		for (ASTExpression e :
			node.getDoOnFalseList()) {
			getTraverser().handle(e);
		}
	}

	@Override
	public void handle(ASTNullCoalescingExpression node) {
		getTraverser().handle(node.getLeft());
		printer.print("?? ");
		getTraverser().handle(node.getRight());
	}

	@Override
	public void handle(ASTOrExpression node) {
		getTraverser().handle(node.getLeft());
		printer.print("| ");
		getTraverser().handle(node.getRight());
	}

	@Override
	public void handle(ASTXorExpression node) {
		getTraverser().handle(node.getLeft());
		printer.print("^ ");
		getTraverser().handle(node.getRight());
	}

	@Override
	public void handle(ASTAndExpression node) {
		getTraverser().handle(node.getLeft());
		printer.print("& ");
		getTraverser().handle(node.getRight());
	}

	@Override
	public void handle(ASTClassificationExpression node) {
		getTraverser().handle(node.getExpression(0));
		printer.print("instanceof ");
		getTraverser().handle(node.getExpression(1));
	}

	@Override
	public void handle(ASTMultiplicativeExpression node) {
		getTraverser().handle(node.getLeft());
		if (node.isTimes()) {
			printer.print("* ");
		}
		if (node.isDivide()) {
			printer.print("/ ");
		}
		if (node.isExp()) {
			printer.print("** ");
		}
		getTraverser().handle(node.getRight());
	}

	@Override
	public void handle(ASTUnitsExpression node) {
		getTraverser().handle(node.getExpression(0));
		printer.print("@ [");
		getTraverser().handle(node.getExpression(1));
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
		getTraverser().handle(node.getExpression());
	}

	@Override
	public void handle(ASTSequenceAccessExpression node) {
		getTraverser().handle(node.getExpression(0));
		printer.print("[");
		for (int i = 1; i < node.getExpressionList().size(); i++) {
			getTraverser().handle(node.getExpression(i));
		}
		printer.print("]");
	}

	@Override
	public void handle(ASTPrimaryExpression node) {
		getTraverser().handle(node.getExpression());
		for (ASTSysMLName n :
			node.getSysMLNameList()) {
			printer.print("-> " + n.getNameForPrettyPrinting() + " ");
			//TODO Expression ("->" SysMLName BodyMember+)+
		}
	}

	@Override
	public void handle(ASTBodyExpression node) {
		if (!node.isEmptyBodyParameterMembers()) {
			for (ASTBodyParameterMember b :
				node.getBodyParameterMemberList()) {
				getTraverser().handle(b);
			}
			printer.print("(");
			for (ASTExpressionMember e :
				node.getExpressionMemberList()) {
				getTraverser().handle(e);
			}
			printer.print(")");
		} else {
			for (ASTExpressionTyping et :
				node.getExpressionTypingList()) {
				getTraverser().handle(et);
			}
		}
	}

	@Override
	public void handle(ASTInvocationExpression node) {
		getTraverser().handle(node.getFeatureTyping());
		printer.print("(");
		getTraverser().handle(node.getTuple());
		printer.print(")");
	}

	@Override
	public void handle(ASTPositionalTuple node) {
		for (int i = 0; i < node.getExpressionMemberList().size(); i++) {
			getTraverser().handle(node.getExpressionMember(i));
			if (i + 1 < node.getExpressionMemberList().size()) {
				printer.print("; ");
			}
		}
	}

	@Override
	public void handle(ASTNamedTuple node) {
		for (int i = 0; i < node.getNamedExpressionMemberList().size(); i++) {
			getTraverser().handle(node.getNamedExpressionMember(i));
			if (i + 1 < node.getNamedExpressionMemberList().size()) {
				printer.print("; ");
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
				getTraverser().handle(node.getExpression());
			} else {
				getTraverser().handle(node.getExpression());
				if (node.isPresentSequenceElementList()) {
					printer.print(", ");
					getTraverser().handle(node.getSequenceElementList());
				}
			}
			printer.println("");
			printer.unindent();
			printer.println("}");
		}
	}

	@Override
	public void handle(ASTSequenceElementList node) {
		getTraverser().handle(node.getExpression());
		if (node.isPresentSequenceElementList()) {
			printer.print(", ");
			getTraverser().handle(node.getSequenceElementList());
		}
	}

	@Override
	public void handle(ASTSysMLQueryPathExpression node) {
		getTraverser().handle(node.getQueryHeadExpression());
		if (node.isEmptyBodyMembers()){
			printer.print("[");
			for (ASTBodyMember b:
					 node.getBodyMemberList()) {
				getTraverser().handle(b);
			}
			printer.print("]");
		}
		//TODO: QueryHeadExpression
		//    	( "[" BodyMember+ "]" )?
		//    	( "/"  QueryNameExpression+	( "[" BodyMember+ "]" )?)*;
	}
}
