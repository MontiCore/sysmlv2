package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.smd._ast.*;
import de.monticore.lang.sysml.smd._visitor.SMDHandler;
import de.monticore.lang.sysml.smd._visitor.SMDTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerSMD2 implements SMDHandler {

	private IndentPrinter printer;
	private SMDTraverser traverser;

	public PrettyPrinterHandlerSMD2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SMDTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SMDTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTStateActionUsage node) {
		if (node.isPresentEmptyActionUsage()) {
			node.getEmptyActionUsage().accept(getTraverser());
		}
		if (!node.isPresentPerformedActionUsage()) {
			printer.print(";");
		} else {
			node.getPerformedActionUsage().accept(getTraverser());
		}
		if (node.isPresentActivityBody()) {
			node.getActivityBody().accept(getTraverser());
		}
	}

	@Override
	public void handle(ASTStateMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (!node.isIsComposite()) {
			printer.print("ref state ");
		} else {
			printer.print("state ");
		}
		node.getStateUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTEntryTransitionMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isPresentGuardedTargetSuccession()) {
			node.getGuardedTargetSuccession().accept(getTraverser());
		} else {
			printer.print("then ");
			node.getTransitionSuccession().accept(getTraverser());
		}
		printer.print(";");
	}

	@Override
	public void handle(ASTExhibitStateUsage node) {
		printer.println("");
		if (node.isPresentSubset()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
				if (node.isPresentTypePart()) {
					node.getTypePart().accept(getTraverser());
				}
				printer.print("as ");
			}
			node.getSubset().accept(getTraverser());
		} else {
			printer.print("state ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
		}
		if (node.isPresentActionParameterList()) {
			node.getActionParameterList().accept(getTraverser());
		}
		node.getSubsettingPart().accept(getTraverser());
		if (node.isPresentValuePart()) {
			node.getValuePart().accept(getTraverser());
		}
		node.getStateBody().accept(getTraverser());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberStateUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isPresentIsComposite()) {
			printer.print("state ");
		} else {
			printer.print("ref state ");
		}
		node.getStateUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberExhibitStateUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		printer.print("exhibit ");
		node.getExhibitStateUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTStateBodyPart node) {
		if (node.isPresentEntryActionMember()) {
			printer.println("");
			node.getEntryActionMember().accept(getTraverser());
			for (ASTEntryTransitionMember t :
				node.getEntryTransitionMemberList()) {
				t.accept(getTraverser());
			}
		}
		if (node.isPresentDoActionMember()) {
			printer.println("");
			node.getDoActionMember().accept(getTraverser());
		}
		if (node.isPresentExitActionMember()) {
			printer.println("");
			node.getExitActionMember().accept(getTraverser());
		}
		for (ASTStateBodyItem sbi :
			node.getStateBodyItemList()) {
			printer.println("");
			sbi.accept(getTraverser());
		}
	}
}
