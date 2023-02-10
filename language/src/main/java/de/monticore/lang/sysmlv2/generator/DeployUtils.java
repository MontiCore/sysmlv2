/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class DeployUtils {

  protected final static int DEFAULT_MAX_CYCLES_COUNT = 20;
  protected int maxCyclesCount;

  protected final static int DEFAULT_CYCLE_TIME = 50; // in ms
  protected int cycleTime;

  protected final static Path DEFAULT_LOG_PATH = Paths.get("./Log.txt");
  protected Path logPath;


  public boolean parseArgs(String[] args) {
    if (Arrays.stream(args).anyMatch(s -> s.matches("(-h)|(--h)|(-help)|(--help)")) || args.length > 3) {
      showHelp();
      return false;
    }
    maxCyclesCount = DEFAULT_MAX_CYCLES_COUNT;
    cycleTime = DEFAULT_CYCLE_TIME;
    logPath = DEFAULT_LOG_PATH;
    if(args.length >= 1) {
      maxCyclesCount = Integer.parseInt(args[0]);
    }
    if(args.length >= 2) {
      cycleTime = Integer.parseInt(args[1]);
    }
    if(args.length >= 3) {
      logPath = Paths.get(args[2]);
    }
    return true;
  }

  protected static void showHelp() {
    String firstLine = "Usage options:";
    String whitespace = repeat(" ", firstLine.length());
    System.out.println(new StringBuilder(firstLine).append("\n")
      .append(whitespace).append("<jar>\n")
      .append(whitespace).append("<jar> <MAX_CYCLES_COUNT>\n")
      .append(whitespace).append("<jar> <MAX_CYCLES_COUNT> <CYCLE_TIME>\n")
      .append(whitespace).append("<jar> <MAX_CYCLES_COUNT> <CYCLE_TIME> <LOG_PATH>\n")
      .append("\n")
      .append("MAX_CYCLES_COUNT and CYCLE_TIME are positive integers, LOG_PATH is the path to a file or directory.\n")
      .append("CYCLE_TIME is given in milliseconds.\n")
      .append("\n")
      .append("Defaults:\n")
      .append(whitespace).append("MAX_CYCLES_COUNT = ").append(DEFAULT_MAX_CYCLES_COUNT).append("\n")
      .append(whitespace).append("CYCLE_TIME = ").append(DEFAULT_CYCLE_TIME).append("\n")
      .append(whitespace).append("LOG_PATH = ").append(DEFAULT_LOG_PATH));
  }

  private static String repeat(String s, int count) {
    return new String(new char[count]).replace("\0", s);
  }

  public int getCycleTime() {return this.cycleTime;}

  public int getMaxCyclesCount() {return this.maxCyclesCount;}

  public Path getLogPath() {return this.logPath;}
}
