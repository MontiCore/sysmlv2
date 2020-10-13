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

  public void setCoCoName(SysMLCoCoName coCoName) {
    this.coCoName = coCoName;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
