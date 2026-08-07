import de.monticore.cd4codebasis._ast.ASTCDMethod
import de.monticore.cdbasis._ast.ASTCDCompilationUnit
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface
import de.monticore.generating.templateengine.GlobalExtensionManagement
import de.monticore.generating.templateengine.TemplateHookPoint
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument

/**
 * This script replaces generated methods for including imports in all scopes into
 * symbol resolution
 */

// glex, astGrammar, decoratedCD
GlobalExtensionManagement glex = args[0] as GlobalExtensionManagement
ASTCDCompilationUnit decoratedCD = args[2] as ASTCDCompilationUnit

List<ASTCDMethod> continueMethods = new ArrayList<>()
ASTCDInterface langScope

for (ASTCDInterface cl : decoratedCD.getCDDefinition().getCDInterfacesList()) {
  if (cl.getName().endsWith("Scope") && cl.getName().startsWith("ISysMLv2")
      && !cl.getName().endsWith("GlobalScope")
  ) {
    for (ASTCDMethod m : new ArrayList<ASTCDMethod>(cl.getCDMethodList())) {
      if (m.getMCReturnType().isPresentMCType() && m.getMCReturnType().getMCType() instanceof ASTMCGenericType) {
        ASTMCTypeArgument symbolType = ((ASTMCGenericType) m.getMCReturnType().getMCType()).getMCTypeArgumentList().get(0);
        List<String> nameList = ((ASTMCBasicTypeArgument) symbolType).getMCQualifiedType().getNameList();
        String last = nameList.get(nameList.size() - 1);
        String actualName = last.substring(0, last.length() - 6);

        if (m.getName() == "continue" + actualName + "WithEnclosingScope") {
          ASTCDMethod mNew
          if (cl.getName() == "ISysMLv2ArtifactScope") {
            // Copy and modify the continueWithEnclosing methods from ArtifactScope
            mNew = m.deepClone()
            continueMethods.add(mNew)
            glex.replaceTemplate("cd2java.EmptyBody", mNew, new TemplateHookPoint(
                "sysml.iscope.ContinueWithEnclosingScope4IScope",
                actualName,
                symbolType.printType())
            )
          } else if (cl.getName() == "ISysMLv2Scope") {
            // remove the generated methods as they are already copied from ArtifactScope
            cl.removeCDMember(m)
            langScope = cl
          }
        }

      }
    }
  }
}

if (langScope != null) {
  langScope.addAllCDMembers(continueMethods)
}
