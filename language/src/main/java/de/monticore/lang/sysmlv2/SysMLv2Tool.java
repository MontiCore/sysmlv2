package de.monticore.lang.sysmlv2;

import de.monticore.lang.sysmlv2._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;

import java.nio.file.Paths;

public class SysMLv2Tool extends SysMLv2ToolTOP {
  private long startTimeGlobal;
  @Override
  public void init() {
    var startTime = System.nanoTime();
    startTimeGlobal = startTime;
    super.init();
    var elapsed = System.nanoTime() - startTime;
    System.out.println("Mill init: " + elapsed/1000/1000);
  }

  @Override
  public ASTConstraintUsage parse(String model) {
    var startTime = System.nanoTime();
    var ast = super.parse(model);
    var elapsed = System.nanoTime() - startTime;
    System.out.println(Paths.get(model).getFileName() + ": " + elapsed/1000/1000);
    System.out.println("Total" + ": " + (System.nanoTime()/1000/1000 - startTimeGlobal/1000/1000));
    return ast;
  }
}
