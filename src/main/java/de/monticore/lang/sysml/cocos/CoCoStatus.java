package de.monticore.lang.sysml.cocos;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class CoCoStatus {
  private SysMLCoCoName coCoName;
  private String message;

  public CoCoStatus(SysMLCoCoName coCoName, String message) {
    this.coCoName = coCoName;
    this.message = message;
  }

  public SysMLCoCoName getCoCoName() {
    return coCoName;
  }

  public String getMessage() {
    return message;
  }
}
