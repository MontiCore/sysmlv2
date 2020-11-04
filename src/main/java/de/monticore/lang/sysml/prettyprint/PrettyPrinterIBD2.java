package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ibd._ast.ASTAssociationEndMemberPartProperty;
import de.monticore.lang.sysml.ibd._ast.ASTPartDeclaration;
import de.monticore.lang.sysml.ibd._ast.ASTPartProperty;
import de.monticore.lang.sysml.ibd._ast.ASTPartPropertyUsageMember;
import de.monticore.lang.sysml.ibd._visitor.IBDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterIBD2 extends IndentPrinter implements IBDVisitor2 {
	@Override
	public void visit(ASTPartDeclaration node) {
		println("");
		if (node.isAbstract())
		{print("abstract ");}
		print("part "+node.getSysMLNameAndTypePart().getName());
	}

	@Override
	public void visit(ASTPartPropertyUsageMember node) {
		println("");
		if (node.isAbstract()){
			print("abstract ");
		}
	}

	@Override
	public void visit(ASTAssociationEndMemberPartProperty node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString()+" ");
		if (node.isAbstract()){
			print("abstract ");
		}
		print("end");
	}

	@Override
	public void visit(ASTPartProperty node) {
		println("");
		print("part ");
	}
}
