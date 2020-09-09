# TODO
- Check Expressions and Literals Compatibility.
- Check implementation of Expressions
- Implement 
    - Interface CONNECTORS (Implemented in Ports) &check;
    - Binding Connectors (Implemented in Ports) &check;
    - Successions (Implemented) &check;
    - Item Flows  (Implemented) &check;
    - ITEM FLOW MEMBERSHIPS  (Implemented) &check;
    - Expressions TODO 
    - Literals &check;
- Structure all Files.
- Fix java.lang.ClassCastException: de.monticore.cd.cd4analysis._ast.ASTCDInterface cannot be cast to 
    de.monticore.cd.cd4analysis._ast.ASTCDClass &check;
- Implement overall connecting NTs (check all interface, if they are properly implemented.). &check;
   - Check if Cocos are necessary for some Connecting NTs


 - Debug &check;
 
 - Maybe change isComposite:"action" to  isComposite:["action"] &check;
 
 -------- 
 # Some renamed NTs
 
 (xtext->MC)
- Import -> ImportUnit
- All NTs which are now interface in MC must be implemented. For the standard implementation these NTs
  get suffix "std".
-  ValueProperty -> ValueProperty + 'abstract' 'value'? (because just used as interface)


# Maybe misstakes in Xtext

-> isPort ?= 'end' 'port'? <-  at InterfaceEndMember and
ConjugatedInterfaceEndMember
 
 ----
 Check if the NTs need to be abstracted to an interface
 ```
 fragment PackagedDefinitionMember returns SysML::Membership :
 	( ownedMemberElement_comp = Package
 	| ownedMemberElement_comp = Block
 	| ownedMemberElement_comp = ValueType
 	| ownedMemberElement_comp = IndividualDefinition
 	| ownedMemberElement_comp = AssociationBlock
 	| ownedMemberElement_comp = InterfaceDefinition
 	| ownedMemberElement_comp = PortDefinition
 	| ownedMemberElement_comp = Activity
     | ownedMemberElement_comp = StateDefinition
     | ownedMemberElement_comp = ConstraintDefinition
     | ownedMemberElement_comp = RequirementDefinition
 	| ownedMemberElement_comp = Comment
 	| ( 'import' | 'alias' ) memberElement = [SysML::Element|QualifiedName] ( 'as' memberName = Name )? ';'
 	)
 ;
```
 
 Some are just used once in xtext. (Click Xtext find References)
 And because we merge them often with unit (to avoid duplicated code), we use them twice.
 
 ------

# Helpful methods
If searching for the implementation of an interface __x__ often it is helpful to search
for "__x =__" in the grammar directory. If an NT does only implement one interface it will always have
the form "MyNT implements __x =__".
