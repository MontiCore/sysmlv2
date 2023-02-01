/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.log;

import java.time.format.DateTimeFormatter;

public class ConsoleLogger extends LogBehavior {

  public ConsoleLogger() {
    System.out.println("Beginning logging to console");
    timestamp = DateTimeFormatter.ofPattern("'['HH:mm:ss']'");
  }

  @Override
  public Runnable getRunnableForExit() {
    return () -> {};
  }

  @Override
  protected void outputMessage(String msg) {
    System.out.println(msg);
  }

  @Override
  protected void outputThrowable(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void emptyLine() {
    System.out.println();
  }
}
