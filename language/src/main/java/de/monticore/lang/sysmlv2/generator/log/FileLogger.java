/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

/**
 * A concrete implementation of logging behavior which
 * enables writing to a log file without re-routing standard system output
 */
public class FileLogger extends LogBehavior {

  protected final FileWriter fileWriter;
  protected final PrintWriter writer;

  public FileLogger(File file, boolean append) {
    file = file.isAbsolute() ? file : file.getAbsoluteFile();
    if (file.isDirectory()) {
      file = file.toPath().resolve("Log" + System.nanoTime() + ".txt").toFile();
    }
    assertWritableFileExists(file, append);
    try {
      fileWriter = new FileWriter(file, append);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      writer = new PrintWriter(bufferedWriter);
      System.out.println("Beginning logging to file " + file);
      timestamp = DateTimeFormatter.ofPattern("'['yyyy/MM/dd HH:mm:ss']'");
      comment("Beginning of file-based logging");
    } catch (Exception e) {
      Log.initConsoleLog();
      throw new LogException("File-based logger could not be initialized " +
        "due to an unforeseen I/O exception. " +
        "Catching this exception causes use of the console logger.");
    }
  }

  /**
   * Checks whether the given file exists (if it doesn't, tries to create it),
   * then checks if the given file can be written to.
   * Fails if the file is already present and would be overwritten.
   *
   * @param file   the file to which the log should be written
   * @param append append to existing file - does not require the file to exist yet
   * @throws LogException if: file cannot be created OR file exists and would be overwritten OR system lacks writing permission for file
   */
  protected static void assertWritableFileExists(File file, boolean append) {
    if (file.exists()) {
      if (!append) {
        throw new LogException(String.format("File '%s' to which " +
          "the log was supposed to be written " +
          "exists already and it would be overwritten.", file));
      } else if (!file.canWrite()) {
        throw new LogException(String.format("File '%s' to which " +
          "the log was supposed to be written " +
          "exists already but it cannot be written to.", file));
      }
    } else {
      new File(file.toString().substring(0, file.toString().length() - file.getName().length())).mkdirs();
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new LogException(String.format("File '%s' to which " +
          "the log was supposed to be written " +
          "could not be created.", file));
      }
      if (!file.canWrite()) {
        throw new LogException(String.format("File '%s' to which " +
          "the log was supposed to be written " +
          "was created but it cannot be written to.", file));
      }
    }
  }

  @Override
  public Runnable getRunnableForExit() {
    return () -> {
      try {
        writer.close();
        fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }

  @Override
  protected void outputMessage(String msg) {
    writer.println(msg);
  }

  @Override
  protected void outputThrowable(Throwable t) {
    t.printStackTrace(writer);
  }

  @Override
  public void error(String msg) {
    String formattedMessage = formatMessage(msg, logName_error);
    writer.println(formattedMessage);
    System.err.println(formattedMessage);
    failOnError();
  }

  @Override
  public void error(String msg, String tag) {
    String formattedMessage = formatMessage(msg, tag, logName_error);
    writer.println(formattedMessage);
    System.err.println(formattedMessage);
    failOnError();
  }

  @Override
  public void error(String msg, Throwable t) {
    String formattedMessage = formatMessage(msg, logName_error);
    writer.println(formattedMessage);
    System.err.println(formattedMessage);
    t.printStackTrace(writer);
    t.printStackTrace(System.err);
    failOnError();
  }

  @Override
  public void error(String msg, String tag, Throwable t) {
    String formattedMessage = formatMessage(msg, tag, logName_error);
    writer.println(formattedMessage);
    System.err.println(formattedMessage);
    t.printStackTrace(writer);
    t.printStackTrace(System.err);
    failOnError();
  }

  @Override
  public void emptyLine() {
    writer.println();
  }
}
