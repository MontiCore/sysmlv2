package de.monticore.lang.sysml.parser.officialImpl.sysml.src.training;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class PathsToFile {

  private final String pathToTraining = "src/test/resources/examples" +
          "/officialPilotImplementation/2020/03/sysml/src/training/";

  private final String [] trainingSubPaths = {
    //"01. Packages/Comment Example.sysml",
    "01. Packages/Package Example.sysml",
  };

  public List<String> getFullRelativePathToTrainingFiles() {
    List<String> fullRelPathList = new ArrayList<>();
    for (String subPath :this.trainingSubPaths) {
      String fullPath = this.pathToTraining.concat(subPath);
      fullRelPathList.add(fullPath);
    }
    return fullRelPathList;
  }
}
