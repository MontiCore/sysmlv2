package de.monticore.lang.sysmlv2.symboltable;

public class NamesUtil {
  public static <T> String getRelativeFromFqn(String relative, String fqn) {
    if (relative == null || fqn == null || relative.length() > fqn.length()) {
      return "";
    }
    if (relative.isEmpty()) {
      return "";
    }

    // 1. Collections.indexOfSubList finds where the relative path starts inside the fqn
    int startIndex = fqn.indexOf(relative);

    // 2. If 'relative' is not found inside 'fqn', return an empty list
    if (startIndex == -1) {
      return "";
    }

    // 3. Extract the slice using .subList() and wrap it in a new ArrayList
    return fqn.substring(startIndex);
  }
}
