/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.parser.official.sysml.src.training;

import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Test;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class TrainingParseAllTest {

  public void parseSysML(String path) {
    SysMLParserForTesting parser = new SysMLParserForTesting();
    parser.parseSysML(path);
  }

  private final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src"
      + "/training/";

  @Test
  public void parse_01_Packages_Comment_ExampleTest() {
    this.parseSysML(pathToDir + "/01. Packages/Comment Example.sysml");
  }

  @Test
  public void parse_01_Packages_Package_ExampleTest() {
    this.parseSysML(pathToDir + "/01. Packages/Package Example.sysml");
  }

  @Test
  public void parse_02_Blocks_Blocks_ExampleTest() {
    this.parseSysML(pathToDir + "/02. Blocks/Blocks Example.sysml");
  }

  @Test
  public void parse_03_Generalization_Generalization_ExampleTest() {
    this.parseSysML(pathToDir + "/03. Generalization/Generalization Example.sysml");
  }

  @Test
  public void parse_04_Subsetting_Subsetting_ExampleTest() {
    this.parseSysML(pathToDir + "/04. Subsetting/Subsetting Example.sysml");
  }

  @Test
  public void parse_05_Redefinition_Redefinition_ExampleTest() {
    this.parseSysML(pathToDir + "/05. Redefinition/Redefinition Example.sysml");
  }

  @Test
  public void parse_06_Parts_Parts_Example_1Test() {
    this.parseSysML(pathToDir + "/06. Parts/Parts Example-1.sysml");
  }

  @Test
  public void parse_06_Parts_Parts_Example_2Test() {
    this.parseSysML(pathToDir + "/06. Parts/Parts Example-2.sysml");
  }

  @Test
  public void parse_07_Individuals_Individuals_and_Roles_1Test() {
    this.parseSysML(pathToDir + "/07. Individuals/Individuals and Roles-1.sysml");
  }

  @Test
  public void parse_07_Individuals_Individuals_and_Snapshots_ExampleTest() {
    this.parseSysML(pathToDir + "/07. Individuals/Individuals and Snapshots Example.sysml");
  }

  @Test
  public void parse_07_Individuals_Individuals_and_Time_SlicesTest() {
    this.parseSysML(pathToDir + "/07. Individuals/Individuals and Time Slices.sysml");
  }

  @Test
  public void parse_08_Connectors_Connectors_ExampleTest() {
    this.parseSysML(pathToDir + "/08. Connectors/Connectors Example.sysml");
  }

  @Test
  public void parse_09_Ports_Port_Conjugation_ExampleTest() {
    this.parseSysML(pathToDir + "/09. Ports/Port Conjugation Example.sysml");
  }

  @Test
  public void parse_09_Ports_Port_ExampleTest() {
    this.parseSysML(pathToDir + "/09. Ports/Port Example.sysml");
  }

  @Test
  public void parse_10_Interfaces_Interface_Decomposition_ExampleTest() {
    this.parseSysML(pathToDir + "/10. Interfaces/Interface Decomposition Example.sysml");
  }

  @Test
  public void parse_10_Interfaces_Interface_ExampleTest() {
    this.parseSysML(pathToDir + "/10. Interfaces/Interface Example.sysml");
  }

  @Test
  public void parse_11_Binding_Connectors_Binding_Connectors_Example_1Test() {
    this.parseSysML(pathToDir + "/11. Binding Connectors/Binding Connectors Example-1.sysml");
  }

  @Test
  public void parse_11_Binding_Connectors_Binding_Connectors_Example_2Test() {
    this.parseSysML(pathToDir + "/11. Binding Connectors/Binding Connectors Example-2.sysml");
  }

  @Test
  public void parse_12_Item_Flows_Streaming_ExampleTest() {
    this.parseSysML(pathToDir + "/12. Item Flows/Streaming Example.sysml");
  }

  @Test
  public void parse_12_Item_Flows_Streaming_Interface_ExampleTest() {
    this.parseSysML(pathToDir + "/12. Item Flows/Streaming Interface Example.sysml");
  }

  @Test
  public void parse_13_Activities_Activity_Example_1Test() {
    this.parseSysML(pathToDir + "/13. Activities/Activity Example-1.sysml");
  }

  @Test
  public void parse_13_Activities_Activity_Example_2Test() {
    this.parseSysML(pathToDir + "/13. Activities/Activity Example-2.sysml");
  }

  @Test
  public void parse_14_Actions_Action_DecompositionTest() {
    this.parseSysML(pathToDir + "/14. Actions/Action Decomposition.sysml");
  }

  @Test
  public void parse_15_Succession_Conditional_Succession_ExampleTest() {
    this.parseSysML(pathToDir + "/15. Succession/Conditional Succession Example.sysml");
  }

  @Test
  public void parse_15_Succession_Succession_Example_1Test() {
    this.parseSysML(pathToDir + "/15. Succession/Succession Example-1.sysml");
  }

  @Test
  public void parse_15_Succession_Succession_Example_2Test() {
    this.parseSysML(pathToDir + "/15. Succession/Succession Example-2.sysml");
  }

  @Test
  public void parse_16_Signaling_Signaling_ExampleTest() {
    this.parseSysML(pathToDir + "/16. Signaling/Signaling Example.sysml");
  }

  @Test
  public void parse_17_Control_CameraTest() {
    this.parseSysML(pathToDir + "/17. Control/Camera.sysml");
  }

  @Test
  public void parse_17_Control_Decision_ExampleTest() {
    this.parseSysML(pathToDir + "/17. Control/Decision Example.sysml");
  }

  @Test
  public void parse_17_Control_Fork_Join_ExampleTest() {
    this.parseSysML(pathToDir + "/17. Control/Fork Join Example.sysml");
  }

  @Test
  public void parse_17_Control_Merge_ExampleTest() {
    this.parseSysML(pathToDir + "/17. Control/Merge Example.sysml");
  }

  @Test
  public void parse_18_Action_Allocation_Action_Allocation_ExampleTest() {
    this.parseSysML(pathToDir + "/18. Action Allocation/Action Allocation Example.sysml");
  }

  @Test
  public void parse_19_State_Definitions_State_Definition_Example_1Test() {
    this.parseSysML(pathToDir + "/19. State Definitions/State Definition Example-1.sysml");
  }

  @Test
  public void parse_19_State_Definitions_State_Definition_Example_2Test() {
    this.parseSysML(pathToDir + "/19. State Definitions/State Definition Example-2.sysml");
  }

  @Test
  public void parse_20_States_State_ActionsTest() {
    this.parseSysML(pathToDir + "/20. States/State Actions.sysml");
  }

  @Test
  public void parse_20_States_State_Decomposition_1Test() {
    this.parseSysML(pathToDir + "/20. States/State Decomposition-1.sysml");
  }

  @Test
  public void parse_20_States_State_Decomposition_2Test() {
    this.parseSysML(pathToDir + "/20. States/State Decomposition-2.sysml");
  }

  @Test
  public void parse_21_State_Allocation_State_Allocation_ExampleTest() {
    this.parseSysML(pathToDir + "/21. State Allocation/State Allocation Example.sysml");
  }

  @Test
  public void parse_21_Transitions_Transition_ActionsTest() {
    this.parseSysML(pathToDir + "/21. Transitions/Transition Actions.sysml");
  }

  @Test
  public void parse_23_Expressions_MassRollupTest() {
    this.parseSysML(pathToDir + "/23. Expressions/MassRollup.sysml");
  }

  @Test
  public void parse_23_Expressions_VehicleMassesTest() {
    this.parseSysML(pathToDir + "/23. Expressions/VehicleMasses.sysml");
  }

  @Test
  public void parse_24_Constraints_Constraint_Assertions_1Test() {
    this.parseSysML(pathToDir + "/24. Constraints/Constraint Assertions-1.sysml");
  }

  @Test
  public void parse_24_Constraints_Constraint_Assertions_2Test() {
    this.parseSysML(pathToDir + "/24. Constraints/Constraint Assertions-2.sysml");
  }

  @Test
  public void parse_24_Constraints_Constraints_Example_1Test() {
    this.parseSysML(pathToDir + "/24. Constraints/Constraints Example-1.sysml");
  }

  @Test
  public void parse_24_Constraints_Constraints_Example_2Test() {
    this.parseSysML(pathToDir + "/24. Constraints/Constraints Example-2.sysml");
  }

  @Test
  public void parse_24_Constraints_Derivation_ConstraintsTest() {
    this.parseSysML(pathToDir + "/24. Constraints/Derivation Constraints.sysml");
  }

  @Test
  public void parse_25_Requirements_Requirement_DefinitionsTest() {
    this.parseSysML(pathToDir + "/25. Requirements/Requirement Definitions.sysml");
  }

  @Test
  public void parse_25_Requirements_Requirement_GroupsTest() {
    this.parseSysML(pathToDir + "/25. Requirements/Requirement Groups.sysml");
  }

  @Test
  public void parse_25_Requirements_Requirement_SatisfactionTest() {
    this.parseSysML(pathToDir + "/25. Requirements/Requirement Satisfaction.sysml");
  }

  @Test
  public void parse_25_Requirements_Requirement_UsagesTest() {
    this.parseSysML(pathToDir + "/25. Requirements/Requirement Usages.sysml");
  }

}
