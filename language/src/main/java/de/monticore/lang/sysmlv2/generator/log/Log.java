/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.log;

import java.io.File;
import java.nio.file.Path;

/**
 * Facade class for different logging behaviors.
 */
public abstract class Log {

  protected static Thread currentShutdownHook = new Thread(() -> {});
  static LogBehavior behavior = getDefaultBehavior();
  static long errorCount = 0;

  public static void initConsoleLog() {
    init(new ConsoleLogger());
  }

  public static void initFileLog(Path path) {
    initFileLog(path, false);
  }

  public static void initFileLog(Path path, boolean append) {
    initFileLog(path.toFile(), append);
  }

  public static void initFileLog(File file) {
    initFileLog(file, false);
  }

  public static void initFileLog(File file, boolean append) {
    init(new FileLogger(file, append));
  }

  public static void init() {
    init(getDefaultBehavior());
  }

  public static void init(LogBehavior log) {
    setBehavior(log);
    errorCount = 0;
  }

  protected static LogBehavior getDefaultBehavior() {
    return new ConsoleLogger();
  }

  /**
   * Resets the Log to the state it was in at system startup.
   */
  public static void reset() {
    init(getDefaultBehavior());
  }

  /**
   * Replaces the current behavior with a new behavior.
   * Does not reset error count or log fail behavior.
   *
   * @param newBehavior the new concrete logger behavior
   */
  public static void setBehavior(LogBehavior newBehavior) {
    Runtime.getRuntime().removeShutdownHook(currentShutdownHook);
    behavior.getRunnableForExit().run();
    behavior = newBehavior;
    currentShutdownHook = new Thread(newBehavior.getRunnableForExit());
    Runtime.getRuntime().addShutdownHook(currentShutdownHook);
  }

  public static boolean isDebugEnabled() {
    return behavior.isDebugEnabled();
  }

  public static void setDebugEnabled(boolean debugEnabled) {
    behavior.setDebugEnabled(debugEnabled);
  }

  public static boolean isTraceEnabled() {
    return behavior.isTraceEnabled();
  }

  public static void setTraceEnabled(boolean traceEnabled) {
    behavior.setTraceEnabled(traceEnabled);
  }

  public static boolean isFailOnWarn() {
    return behavior.isFailOnWarn();
  }

  public static void setFailOnWarn(boolean failOnWarn) {
    behavior.setFailOnWarn(failOnWarn);
  }

  public static boolean isFailOnError() {
    return behavior.isFailOnError();
  }

  public static void setFailOnError(boolean failOnError) {
    behavior.setFailOnError(failOnError);
  }

  public static void info(String msg) {
    behavior.info(msg);
  }

  public static void info(String msg, String tag) {
    behavior.info(msg, tag);
  }

  public static void debug(String msg) {
    if (isDebugEnabled()) {
      behavior.debug(msg);
    }
  }

  public static void debug(String msg, String tag) {
    if (isDebugEnabled()) {
      behavior.debug(msg, tag);
    }
  }

  public static void debug(String msg, Throwable t) {
    if (isDebugEnabled()) {
      behavior.debug(msg, t);
    }
  }

  public static void debug(String msg, String tag, Throwable t) {
    if (isDebugEnabled()) {
      behavior.debug(msg, tag, t);
    }
  }

  public static void trace(String msg) {
    if (isTraceEnabled()) {
      behavior.trace(msg);
    }
  }

  public static void trace(String msg, String tag) {
    if (isTraceEnabled()) {
      behavior.trace(msg, tag);
    }
  }

  public static void trace(String msg, Throwable t) {
    if (isTraceEnabled()) {
      behavior.trace(msg, t);
    }
  }

  public static void trace(String msg, String tag, Throwable t) {
    if (isTraceEnabled()) {
      behavior.trace(msg, tag, t);
    }
  }

  public static void warn(String msg) {
    behavior.warn(msg);
  }

  public static void warn(String msg, String tag) {
    behavior.warn(msg, tag);
  }

  public static void warn(String msg, Throwable t) {
    behavior.warn(msg, t);
  }

  public static void warn(String msg, String tag, Throwable t) {
    behavior.warn(msg, tag, t);
  }

  public static void error(String msg) {
    errorCount++;
    behavior.error(msg);
  }

  public static void error(String msg, String tag) {
    errorCount++;
    behavior.error(msg, tag);
  }

  public static void error(String msg, Throwable t) {
    errorCount++;
    behavior.error(msg, t);
  }

  public static void error(String msg, String tag, Throwable t) {
    errorCount++;
    behavior.error(msg, tag, t);
  }

  public static void log(String msg, String logName) {
    behavior.log(msg, logName);
  }

  public static void comment(String msg) {
    behavior.comment(msg);
  }

  public static void emptyLine() {
    behavior.emptyLine();
  }

  public static long getErrorCount() {
    return errorCount;
  }

  public void setCommentStart(String commentStart) {
    behavior.setCommentStart(commentStart);
  }

  public void setCommentEnd(String commentEnd) {
    behavior.setCommentEnd(commentEnd);
  }
}
