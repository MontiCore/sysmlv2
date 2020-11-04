package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.bdd._ast.ASTAssociationEndMemberReferenceProperty;
import de.monticore.lang.sysml.bdd._ast.ASTBlockDeclaration;
import de.monticore.lang.sysml.bdd._ast.ASTReferencePropertyNonPortStructureUsageMember;
import de.monticore.lang.sysml.bdd._visitor.BDDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterBDD2 extends IndentPrinter implements BDDVisitor2 {
	@Override
	public void visit(ASTBlockDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("block " + node.getName());
	}

	@Override
	public void visit(ASTAssociationEndMemberReferenceProperty node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + "");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("end ");
		if (node.isRef()) {
			print("ref ");
		}
	}

	@Override
	public void visit(ASTReferencePropertyNonPortStructureUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("ref ");
	}
}
