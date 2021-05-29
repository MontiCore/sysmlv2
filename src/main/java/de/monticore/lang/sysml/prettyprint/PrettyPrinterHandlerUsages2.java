/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlusages._ast.*;
import de.monticore.lang.sysml.common.sysmlusages._visitor.SysMLUsagesHandler;
import de.monticore.lang.sysml.common.sysmlusages._visitor.SysMLUsagesTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerUsages2 implements SysMLUsagesHandler {
	private IndentPrinter printer;
	private SysMLUsagesTraverser traverser;

	public PrettyPrinterHandlerUsages2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLUsagesTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLUsagesTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTTypePartStd node) {
		if (node.isPresentTypedByKeyword()) {
			node.getTypedByKeyword().accept(getTraverser());
			if (node.isEmptyFeatureTypings()) {
				printer.print("any ");
			} else {
				for (int i = 0; i < node.getFeatureTypingList().size(); i++) {
					node.getFeatureTyping(i).accept(getTraverser());
					if (i + 1 < node.getFeatureTypingList().size()) {
						printer.print(", ");
					} else {
						printer.print(" ");
					}
				}
				if(node.isPresentMultiplicityPart()) {
					node.getMultiplicityPart().accept(getTraverser());
				}
			}
		} else {
			node.getMultiplicityPart().accept(getTraverser());
		}
	}

	@Override
	public void handle(ASTSubsettingPart node) {
		if (node.getSubsetsList().size()>0) {
			node.getSubsets(0).accept(getTraverser());
			for (ASTSubset s:
					 node.getSubsetList()) {
				printer.print(", ");
				s.accept(getTraverser());
			}
		}
		if(node.getRedefinesList().size()>0){
			node.getRedefines(0).accept(getTraverser());
			for (ASTRedefinition r:
				node.getRedefinitionList()) {
				printer.print(", ");
				r.accept(getTraverser());
			}
		}
	}

	@Override
	public void handle(ASTParameterTypePart node) {
		if (node.isPresentTypedByKeyword()) {
			node.getTypedByKeyword().accept(getTraverser());
			if (!node.isPresentFeatureTyping()) {
				printer.print("any ");
			} else {
				node.getFeatureTyping().accept(getTraverser());
			}
			if (node.isPresentMultiplicityPart()){
				node.getMultiplicityPart().accept(getTraverser());
			}
		} else {
			node.getMultiplicityPart().accept(getTraverser());
		}
	}
}
