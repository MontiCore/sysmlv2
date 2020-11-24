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
		printer.println("");
		getTraverser().handle(node.getEmptyActionUsage());
		if (!node.isPresentPerformedActionUsage()) {
			printer.print(";");
		} else {
			getTraverser().handle(node.getPerformedActionUsage());
		}
		getTraverser().handle(node.getActivityBody());
	}

	@Override
	public void handle(ASTStateMember node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (!node.isIsComposite()) {
			printer.print("ref state ");
		} else {
			printer.print("state ");
		}
		getTraverser().handle(node.getStateUsage());
	}

	@Override
	public void handle(ASTEntryTransitionMember node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isPresentGuardedTargetSuccession()) {
			getTraverser().handle(node.getGuardedTargetSuccession());
		} else {
			printer.print("then ");
			getTraverser().handle(node.getTransitionSuccession());
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
					getTraverser().handle(node.getTypePart());
				}
				printer.print("as ");
			}
			getTraverser().handle(node.getSubset());
		} else {
			printer.print("state ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
		}
		if (node.isPresentActionParameterList()) {
			getTraverser().handle(node.getActionParameterList());
		}
		getTraverser().handle(node.getSubsettingPart());
		if (node.isPresentValuePart()) {
			getTraverser().handle(node.getValuePart());
		}
		getTraverser().handle(node.getStateBody());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberStateUsage node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isPresentIsComposite()) {
			printer.print("state ");
		} else {
			printer.print("ref state ");
		}
		getTraverser().handle(node.getStateUsage());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberExhibitStateUsage node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		printer.print("exhibit ");
		getTraverser().handle(node.getExhibitStateUsage());
	}
}
