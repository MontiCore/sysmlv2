package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.sysmlad._ast.*;
import de.monticore.lang.sysml.sysmlad._visitor.SysMLADVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterAD2 implements SysMLADVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterAD2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTActivityDeclaration node) {
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("activity " + node.getSysMLName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void visit(ASTActivityBodyStd node) {
		if (node.getActivityBodyItemList().size()==0) {
			printer.print(";");
		} else {
			printer.println("{");
			printer.indent();
		}
	}

	@Override
	public void endVisit(ASTActivityBodyStd node) {
		if (node.getActivityBodyItemList().size()>0) {
			printer.unindent();
			printer.println("");
			printer.println("}");
		}
	}

	@Override
	public void visit(ASTTargetSuccessionMember node) {
		printer.println("");
	}

	@Override
	public void endVisit(ASTTargetSuccessionMember node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTGuardedSuccessionMember node) {
		printer.println("");
	}

	@Override
	public void endVisit(ASTGuardedSuccessionMember node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTAssociationEndMemberActionUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("action ");
		} else {
			printer.print("ref action ");
		}
		printer.print("end ");
	}

	@Override
	public void visit(ASTActionDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("action");
	}

	@Override
	public void visit(ASTActionUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("action ");
	}

	@Override
	public void visit(ASTActionUsageNodeDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isIsComposite()) {
			printer.print("action ");
		} else {
			printer.print("ref action ");
		}
	}

	@Override
	public void visit(ASTPerformActionNodeDeclaration node) {
		printer.println("");
		if (node.isIsComposite()) {
			printer.print("perform ");
		}
		if (node.isPresentSysMLNameAndTypePart()) {
			printer.print(node.getSysMLNameAndTypePart().getName() + " as ");
		}
	}

	@Override
	public void visit(ASTControlNode node) {
	  if (node.isMerge()) {
	    printer.print("merge ");
	  } else if (node.isDecide()) {
	    printer.print("decide ");
	  } else if (node.isJoin()) {
	    printer.println("");
	    printer.print("join ");
    } else if (node.isFork()) {
      printer.print("fork ");
    }
		
	}

	@Override
	public void endVisit(ASTControlNode node) {
		printer.print(";");
	}

}
