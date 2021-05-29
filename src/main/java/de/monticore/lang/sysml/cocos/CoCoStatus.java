/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.cocos;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class CoCoStatus {
  private SysMLCoCoName coCoName;
  private String message;
  private boolean throwError = false;

  public CoCoStatus(SysMLCoCoName coCoName, String message) {
    this.coCoName = coCoName;
    this.message = message;
  }
  public CoCoStatus(SysMLCoCoName coCoName, String message, boolean throwError) {
    this.coCoName = coCoName;
    this.message = message;
    this.throwError = true;
  }

  public SysMLCoCoName getCoCoName() {
    return coCoName;
  }

  public String getMessage() {
    return message;
  }

  public boolean throwError() {
    return throwError;
  }
}
