package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.usages._ast.*;
import de.monticore.lang.sysml.basics.usages._visitor.UsagesHandler;
import de.monticore.lang.sysml.basics.usages._visitor.UsagesTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerUsages2 implements UsagesHandler {
	private IndentPrinter printer;
	private UsagesTraverser traverser;

	public PrettyPrinterHandlerUsages2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public UsagesTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(UsagesTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTTypePartStd node) {
		if (node.isPresentTypedByKeyword()) {
			getTraverser().handle(node.getTypedByKeyword());
			if (node.isEmptyFeatureTypings()) {
				printer.print("any ");
			} else {
				for (int i = 0; i < node.getFeatureTypingList().size(); i++) {
					getTraverser().handle(node.getFeatureTyping(i));
					if (i + 1 < node.getFeatureTypingList().size()) {
						printer.print(", ");
					} else {
						printer.print(" ");
					}
				}
			}
		} else {
			getTraverser().handle(node.getMultiplicityPart());
		}
	}

	@Override
	public void handle(ASTSubsettingPart node) {
		if (!node.isEmptySubsets()) {
			getTraverser().handle(node.getSubsets(0));
			for (ASTSubset s:
					 node.getSubsetList()) {
				printer.print(", ");
				getTraverser().handle(s);
			}
		}
		if(!node.isEmptyRedefiness()){
			getTraverser().handle(node.getRedefines(0));
			for (ASTRedefinition r:
				node.getRedefinitionList()) {
				printer.print(", ");
				getTraverser().handle(r);
			}
		}
	}

	@Override
	public void handle(ASTParameterTypePart node) {
		if (node.isPresentTypedByKeyword()) {
			getTraverser().handle(node.getTypedByKeyword());
			if (!node.isPresentFeatureTyping()) {
				printer.print("any ");
			} else {
				getTraverser().handle(node.getFeatureTyping());
			}
			if (node.isPresentMultiplicityPart()){
				getTraverser().handle(node.getMultiplicityPart());
			}
		} else {
			getTraverser().handle(node.getMultiplicityPart());
		}
	}
}
