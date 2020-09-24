package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._visitor.SysMLVisitor;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class PrettyPrinter implements SysMLVisitor {
  private String result = "";

  private int indention = 0;

  private String indent = "";

  /**
   * Prints the automata
   *
   * @param unit
   */
  public void print(ASTUnit unit) {
    handle(unit);
  }

  /**
   * Gets the printed result.
   *
   * @return the result of the pretty print.
   */
  public String getResult() {
    return this.result;
  }

  /* Helper methods */
  private void print(String s) {
    result += (indent + s);
    indent = "";
  }
  private void append(String s){
    result += result;
  }
  private void appendNewLine(){
    result += "\n";
  }

  private void println(String s) {
    result += (indent + s + "\n");
    indent = "";
    calcIndention();
  }

  private void calcIndention() {
    indent = "";
    for (int i = 0; i < indention; i++) {
      indent += "  ";
    }
  }

  private void indent() {
    indention++;
    calcIndention();
  }

  private void unindent() {
    indention--;
    calcIndention();
  }
}
