/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class LogBehavior {
  protected final static String
    logName_info = "INFO",
    logName_debug = "DEBUG",
    logName_trace = "TRACE",
    logName_warn = "WARN",
    logName_error = "ERROR",
    logName_comment = "COMMENT";
  protected static String commentStart = "<!--- ", commentEnd = " --->";
  protected static boolean debugEnabled = false, traceEnabled = false;

  protected static boolean failOnWarn = false, failOnError = true;
  protected DateTimeFormatter timestamp = DateTimeFormatter.ofPattern("'['yyyy/MM/dd HH:mm:ss']'");

  /**
   * Returns a Runnable which is supposed to be executed when
   * the system exits or the instance is discarded/replaced.
   * <p>
   * If the concrete logger instance uses any resources which
   * need to be recovered/released once the logger is done,
   * they should be released via this runnable.
   *
   * @return the runnable to be run when the logger instance is discarded
   */
  public abstract Runnable getRunnableForExit();

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public void setDebugEnabled(boolean debugEnabled) {
    LogBehavior.debugEnabled = debugEnabled;
  }

  public boolean isTraceEnabled() {
    return traceEnabled;
  }

  public void setTraceEnabled(boolean traceEnabled) {
    LogBehavior.traceEnabled = traceEnabled;
  }

  public boolean isFailOnWarn() {
    return failOnWarn;
  }

  public void setFailOnWarn(boolean failOnWarn) {
    LogBehavior.failOnWarn = failOnWarn;
  }

  public boolean isFailOnError() {
    return failOnError;
  }

  public void setFailOnError(boolean failOnError) {
    LogBehavior.failOnError = failOnError;
  }

  protected void failOnWarn() {
    if (isFailOnWarn()) {
      System.exit(-1);
    }
  }

  protected void failOnError() {
    if (isFailOnError()) {
      System.exit(-1);
    }
  }

  protected abstract void outputMessage(String msg);

  protected abstract void outputThrowable(Throwable t);

  public void info(String msg) {
    outputMessage(formatMessage(msg, logName_info));
  }

  public void info(String msg, String tag) {
    outputMessage(formatMessage(msg, tag, logName_info));
  }

  public void debug(String msg) {
    outputMessage(formatMessage(msg, logName_debug));
  }

  public void debug(String msg, String tag) {
    outputMessage(formatMessage(msg, tag, logName_debug));
  }

  public void debug(String msg, Throwable t) {
    outputMessage(formatMessage(msg, logName_debug));
    outputThrowable(t);
  }

  public void debug(String msg, String tag, Throwable t) {
    outputMessage(formatMessage(msg, tag, logName_debug));
    outputThrowable(t);
  }

  public void trace(String msg) {
    outputMessage(formatMessage(msg, logName_trace));
  }

  public void trace(String msg, String tag) {
    outputMessage(formatMessage(msg, tag, logName_trace));
  }

  public void trace(String msg, Throwable t) {
    outputMessage(formatMessage(msg, logName_trace));
    outputThrowable(t);
  }

  public void trace(String msg, String tag, Throwable t) {
    outputMessage(formatMessage(msg, tag, logName_trace));
    outputThrowable(t);
  }

  public void warn(String msg) {
    outputMessage(formatMessage(msg, logName_warn));
    failOnWarn();
  }

  public void warn(String msg, String tag) {
    outputMessage(formatMessage(msg, tag, logName_warn));
    failOnWarn();
  }

  public void warn(String msg, Throwable t) {
    outputMessage(formatMessage(msg, logName_warn));
    outputThrowable(t);
    failOnWarn();
  }

  public void warn(String msg, String tag, Throwable t) {
    outputMessage(formatMessage(msg, tag, logName_warn));
    outputThrowable(t);
    failOnWarn();
  }

  public void error(String msg) {
    outputMessage(formatMessage(msg, logName_error));
    failOnError();
  }

  public void error(String msg, String tag) {
    outputMessage(formatMessage(msg, tag, logName_error));
    failOnError();
  }

  public void error(String msg, Throwable t) {
    outputMessage(formatMessage(msg, logName_error));
    outputThrowable(t);
    failOnError();
  }

  public void error(String msg, String tag, Throwable t) {
    outputMessage(formatMessage(msg, tag, logName_error));
    outputThrowable(t);
    failOnError();
  }

  public void log(String msg, String logName) {
    outputMessage(formatMessage(msg, logName));
  }

  public abstract void emptyLine();

  public void setCommentStart(String commentStart) {
    LogBehavior.commentStart = commentStart;
  }

  public void setCommentEnd(String commentEnd) {
    LogBehavior.commentEnd = commentEnd;
  }

  // Notice: This method is mainly intended to be used for file logging where certain
  // output is not supposed to be visible in the processed file, e.g. markdown with comments
  public void comment(String msg) {
    log(commentStart + msg + commentEnd, logName_comment);
  }

  /**
   * A method used to uniformly format each logged message.
   * Adds the current time and day to the logged message by default.
   * This method should be overridden in order to achieve a fitting
   * logger output for the chosen output channel and purpose.
   *
   * @param msg     the actual message which is to be logged
   * @param logName the name of the log or severity level
   * @return a formatted log message, ready for printing.
   */
  protected String formatMessage(String msg, String logName) {
    return String.format("%s[%s] %s", timestamp.format(LocalDateTime.now()), logName, msg);
  }

  /**
   * A method used to uniformly format each logged message.
   * Adds the current time and day to the logged message by default.
   * This method should be overridden in order to achieve a fitting
   * logger output for the chosen output channel and purpose.
   *
   * @param msg     the actual message which is to be logged
   * @param tag     an additional tag to be logged alongside the message
   * @param logName the name of the log or severity level
   * @return a formatted log message, ready for printing.
   */
  protected String formatMessage(String msg, String tag, String logName) {
    return String.format("%s[%s][%s] %s", timestamp.format(LocalDateTime.now()), logName, tag, msg);
  }
}
