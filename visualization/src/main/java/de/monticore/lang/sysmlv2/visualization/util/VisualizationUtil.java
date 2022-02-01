package de.monticore.lang.sysmlv2.visualization.util;

public class VisualizationUtil {
  public final static String STYLE_HELP = "Pick one or multiple <STYLE>s. <STYLE> is case insensitive. Multiple --style options are allowed.\n"
      + "   The possible style names are:\n"
      + "   DEFAULT\t\tStandard B&W format\n"
      + "   STDCOLOR\t\tStandard format with colors\n"
      + "   PLANTUML\t\tPlantUML format\n"
      + "   PUMLCODE\t\tShow PlantUML code mainly for debugging\n"
      + "   TB\t\t\tTop-to-Bottom orientation\n"
      + "   LR\t\t\tLeft-to-Right orientation\n"
      + "   POLYLINE\t\tPolyline style\n"
      + "   ORTHOLINE\t\tOrthogonal line style\n"
      + "   SHOWINHERITED\t\tShow inherited members\n"
      + "   SHOWLIB\t\tShow elements of the standard libraries\n"
      + "   COMPMOST\t\tShow as many memberships in a compartment as possible\n"
      + "   COMPTREE\t\tShow tree structures in compartments\n\n";

  public final static String VIEW_HELP = "Pick a <VIEW>. <VIEW> is case insensitive and possible candidates are:\n"
      + "   DEFAULT\t\tAutomatically choose an appropriate view from the given model element names\n"
      + "   TREE\t\t\tShow a tree-structural view, like a Block Definition Diagram (BDD)\n"
      + "   INTERCONNECTION\tShow an interconnection view, like an Internal Block Diagram (IBD)\n"
      + "   STATE\t\tShow state machines\n"
      + "   ACTION\t\tShow actions like an activity diagram\n"
      + "   SEQUENCE\t\tShow events and messages in a sequence diagram\n"
      + "   MIXED\t\tShow multiple views\n\n";
}
