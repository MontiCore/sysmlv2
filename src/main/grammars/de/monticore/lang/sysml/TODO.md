# TODO
- Check Expressions and Literals Compatibility.
- Check implementation of Expressions
- Implement 
    - Interface CONNECTORS (Implemented in Ports) &check;
    - Binding Connectors (Implemented in Ports) &check;
    - Successions (Implemented) &check;
    - Item Flows  (Implemented) &check;
    - ITEM FLOW MEMBERSHIPS  (Implemented) &check;
    - Expressions
    - Literals
- Structure all Files.
- Fix java.lang.ClassCastException: de.monticore.cd.cd4analysis._ast.ASTCDInterface cannot be cast to 
    de.monticore.cd.cd4analysis._ast.ASTCDClass
- Implement overall connecting NTs (check all interface, if they are properly implemented.).
   - Check if Cocos are necessary for some Connecting NTs


 - Debug
 
 - Maybe change isComposite:"action" to  isComposite:["action"]
 
 -------- 
 # Some renamed NTs
 
 (xtext->MC)
- Import -> ImportUnit
- All NTs which are now interface in MC must be implemented. For the standard implementation these NTs
  get suffix "std".
- 


# Maybe misstakes in Xtext

-> isPort ?= 'end' 'port'? <-  at InterfaceEndMember and
ConjugatedInterfaceEndMember
 
