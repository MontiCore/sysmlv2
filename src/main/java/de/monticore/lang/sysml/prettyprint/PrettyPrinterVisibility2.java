package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmlvisibility._ast.ASTPackageElementVisibilityIndicatorStd;
import de.monticore.lang.sysml.basics.sysmlvisibility._ast.ASTVISIBILITYLONG;
import de.monticore.lang.sysml.basics.sysmlvisibility._ast.ASTVISIBILITYSHORT;
import de.monticore.lang.sysml.basics.sysmlvisibility._ast.ASTVisibilityIndicatorStd;
import de.monticore.lang.sysml.basics.sysmlvisibility._visitor.SysMLVisibilityVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;


public class PrettyPrinterVisibility2 implements SysMLVisibilityVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterVisibility2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTVISIBILITYSHORT node) {
		switch (node.getIntValue()) {
			case 4:
				printer.print("public ");
				break;
			case 2:
				printer.print("private ");
				break;
			default:
				Log.error("Error 0xA7197: Invalid Visibility");
		}
	}

	@Override
	public void visit(ASTVISIBILITYLONG node) {
		switch (node.getIntValue()) {
			case 4:
				printer.print("public ");
				break;
			case 2:
				printer.print("private ");
				break;
			case 3:
				printer.print("protected ");
				break;
			case 1:
				printer.print("package ");
				break;
			default:
				Log.error("Error 0xA7196: Invalid Visibility");
		}
	}

	@Override
	public void visit(ASTPackageElementVisibilityIndicatorStd node) {
		this.visit(node.getVis());
	}

	@Override
	public void visit(ASTVisibilityIndicatorStd node) {
		this.visit(node.getVis());
	}
}
