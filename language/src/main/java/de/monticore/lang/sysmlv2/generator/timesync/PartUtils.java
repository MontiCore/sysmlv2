package de.monticore.lang.sysmlv2.generator.timesync;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;

import java.util.*;
import java.util.stream.Collectors;

public class PartUtils {

  private List<ASTPartUsage> createPartUsageList(ASTSysMLElement element) {
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(element instanceof ASTPartDef)
      elementList = ((ASTPartDef) element).getSysMLElementList();
    if(element instanceof ASTPartUsage)
      elementList = ((ASTPartUsage) element).getSysMLElementList();
    if(element instanceof ASTAttributeDef)
      elementList = ((ASTAttributeDef) element).getSysMLElementList();
    List<ASTPartUsage> attributeUsageList;
    attributeUsageList = elementList.stream().filter(
        t -> t instanceof ASTPartUsage).map(t -> (ASTPartUsage) t).collect(Collectors.toList());
    return attributeUsageList;
  }

  public List<ASTPartUsage> setPortLists(ASTSysMLElement astSysMLElement) {
    List<ASTPartUsage> portUsageList = createPartUsageList(astSysMLElement);  //create Port usage list
    List<ASTPartUsage> supertypePortUsageList = new ArrayList<>();
    //create astcdattributes for transitive attributes
    if(astSysMLElement instanceof ASTPartDef) {
      supertypePortUsageList = ((ASTPartDef) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createPartUsageList(t).stream()).collect(Collectors.toList());
    }
    if(astSysMLElement instanceof ASTPartUsage) {
      supertypePortUsageList = ((ASTPartUsage) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createPartUsageList(t).stream()).collect(Collectors.toList());
    }
    portUsageList.addAll(supertypePortUsageList);
    //divide into the different directions
    return portUsageList;
  }

}
