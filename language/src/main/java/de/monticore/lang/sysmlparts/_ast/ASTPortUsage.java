package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTPortUsage extends ASTPortUsageTOP {

  /***
   * @return List of all PortDefs that are a super type of this PortUsage.
   */
  public List<ASTPortDef> getPortDefs() {
    return this.isEmptySpecializations() ? new ArrayList<>() :
        this.getSpecializationList().stream()
            .filter(s -> !s.isEmptySuperTypes())
            .flatMap(s -> s.getSuperTypesList().stream())
            .map(s -> SysMLv2Mill.globalScope().resolvePortDef(s.printType()))
            .filter(Optional::isPresent)
            .map(p -> p.get().getAstNode())
            .collect(Collectors.toList());
  }

  /***
   * @param conjugated Get only conjugated or unconjugated PortDefs.
   * @return List of all PortDefs that are a super type of this PortUsage.
   */
  public List<ASTPortDef> getPortDefs(boolean conjugated) {
    return this.isEmptySpecializations() ? new ArrayList<>() :
        this.getSpecializationList().stream()
            .filter(s -> !s.isEmptySuperTypes())
            .filter(s -> ((ASTSysMLTyping)s).isConjugated() == conjugated)
            .flatMap(s -> s.getSuperTypesList().stream())
            .map(s -> SysMLv2Mill.globalScope().resolvePortDef(s.printType()))
            .filter(Optional::isPresent)
            .map(p -> p.get().getAstNode())
            .collect(Collectors.toList());
  }

  private static boolean match(Collection<ASTSpecialization> s1, Collection<ASTSpecialization> s2){
    var t1 = new ArrayList<>(s1);
    var t2 = new ArrayList<>(s2);

    for (var t1_ : t1){
      var match = t2.stream().filter(t2_ -> t2_.deepEquals(t1_, false)).findFirst();
      if (match.isPresent()){
        t2.remove(match.get());
      } else {
        return false;
      }
    }
    return true;
  }

  private static boolean matchTypes(Collection<ASTSpecialization> s1, Collection<ASTSpecialization> s2){
    var t1 = new ArrayList<>(s1);
    var t2 = new ArrayList<>(s2);

    for (var t1_ : t1){
      var match = t2.stream()
          .filter(t2_ -> matchTypes(t1_, t2_))
          .filter(t2_ -> ((ASTSysMLTyping) t1_).isConjugated() == !((ASTSysMLTyping) t2_).isConjugated())
          .findFirst();

      if (match.isPresent()){
        t2.remove(match.get());
      } else {
        return false;
      }
    }
    return true;
  }

  private static boolean matchTypes(ASTSpecialization s1, ASTSpecialization s2){
    var t1 = s1.deepClone().getSuperTypesList().stream().map(ASTMCType::printType).collect(Collectors.toList());
    var t2 = s2.deepClone().getSuperTypesList().stream().map(ASTMCType::printType).collect(Collectors.toList());

    return t1.equals(t2);
  }

  public enum Direction {
    IN, OUT, INOUT;
  }

  /**
   * Estimates the direction of a port based on the directions of the underlying attributes.
   * @return {@link Direction#IN} if all attributes are in, {@link Direction#OUT} if all
   * attributes are out, {@link Direction#INOUT} otherwise.
   */
  public Direction estimatePortDirection() {
    var conjugatedPortDefs = this.getPortDefs(true);
    var unconjugatedPortDefs = this.getPortDefs(false);

    long in = Stream.concat(
        conjugatedPortDefs.stream()
            .flatMap(p -> p.getSysMLElementList().stream())
            .filter(a ->  a instanceof ASTAttributeUsage)
            .map(a -> (ASTAttributeUsage)a)
            .filter(a -> a.getSymbol().isOut()),
        unconjugatedPortDefs.stream()
            .flatMap(p -> p.getSysMLElementList().stream())
            .filter(a ->  a instanceof ASTAttributeUsage)
            .map(a -> (ASTAttributeUsage)a)
            .filter(a -> a.getSymbol().isIn())
        ).count();

    long out = Stream.concat(
        conjugatedPortDefs.stream()
            .flatMap(p -> p.getSysMLElementList().stream())
            .filter(a ->  a instanceof ASTAttributeUsage)
            .map(a -> (ASTAttributeUsage)a)
            .filter(a -> a.getSymbol().isIn()),
        unconjugatedPortDefs.stream()
            .flatMap(p -> p.getSysMLElementList().stream())
            .filter(a ->  a instanceof ASTAttributeUsage)
            .map(a -> (ASTAttributeUsage)a)
            .filter(a -> a.getSymbol().isOut())
    ).count();

    if (in > 0 && out == 0){
      return Direction.IN;
    } else if (out > 0 && in == 0){
      return Direction.OUT;
    } else {
      return Direction.INOUT;
    }
  }
}
