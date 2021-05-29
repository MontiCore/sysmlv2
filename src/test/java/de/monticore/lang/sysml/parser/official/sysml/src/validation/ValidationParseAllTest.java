/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.parser.official.sysml.src.validation;

import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Test;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ValidationParseAllTest {
  private final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src"
      + "/validation/";

  public void parseSysML(String path) {
    SysMLParserForTesting parser = new SysMLParserForTesting();
    parser.parseSysML(path);
  }

  @Test
  public void parse_01_Parts_Tree_1a_Parts_TreeTest() {
    this.parseSysML(pathToDir + "/01-Parts Tree/1a-Parts Tree.sysml");
  }

  @Test
  public void parse_01_Parts_Tree_1c_Parts_Tree_RedefinitionTest() {
    this.parseSysML(pathToDir + "/01-Parts Tree/1c-Parts Tree Redefinition.sysml");
  }

  @Test
  public void parse_02_Parts_Interconnection_2_Parts_InterconnectionTest() {
    this.parseSysML(pathToDir + "/02-Parts Interconnection/2-Parts Interconnection.sysml");
  }

  @Test
  public void parse_03_Function_based_Behavior_3a_Function_based_Behavior_1Test() {
    this.parseSysML(pathToDir + "/03-Function-based Behavior/3a-Function-based Behavior-1.sysml");
  }

  @Test
  public void parse_03_Function_based_Behavior_3a_Function_based_Behavior_2Test() {
    this.parseSysML(pathToDir + "/03-Function-based Behavior/3a-Function-based Behavior-2.sysml");
  }

  @Test
  public void parse_03_Function_based_Behavior_3a_Function_based_BehaviorTest() {
    this.parseSysML(pathToDir + "/03-Function-based Behavior/3a-Function-based Behavior.sysml");
  }

  @Test
  public void parse_04_Functional_Allocation_4a1_Functional_AllocationTest() {
    this.parseSysML(pathToDir + "/04-Functional Allocation/4a1-Functional Allocation.sysml");
  }

  @Test
  public void parse_05_State_based_Behavior_5_State_based_Behavior_1Test() {
    this.parseSysML(pathToDir + "/05-State-based Behavior/5-State-based Behavior-1.sysml");
  }

  @Test
  public void parse_05_State_based_Behavior_5_State_based_BehaviorTest() {
    this.parseSysML(pathToDir + "/05-State-based Behavior/5-State-based Behavior.sysml");
  }

  @Test
  public void parse_06_Individual_and_Snapshots_6_Individual_and_SnapshotsTest() {
    this.parseSysML(pathToDir + "/06-Individual and Snapshots/6-Individual and Snapshots.sysml");
  }

  @Test
  public void parse_08_Requirements_8_RequirementsTest() {
    this.parseSysML(pathToDir + "/08-Requirements/8-Requirements.sysml");
  }

  @Test
  public void parse_13_Model_Containment_13a_Model_ContainmentTest() {
    this.parseSysML(pathToDir + "/13-Model Containment/13a-Model Containment.sysml");
  }

  @Test
  public void parse_13_Model_Containment_13b_Safety_and_Security_Features_Element_GroupTest() {
    this.parseSysML(pathToDir + "/13-Model Containment/13b-Safety and Security Features Element Group.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_01_ConstantsTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_01-Constants.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_02_Basic_Properties_with_Default_ValuesTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_02-Basic Properties with Default Values.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_03_Value_ExpressionTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_03-Value Expression.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_05_Unification_of_Expression_and_Constraint_DefinitionTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_05-Unification of Expression and Constraint "
        + "Definition.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_08_Range_RestrictionTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_08-Range Restriction.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_10_Primitive_Data_TypesTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_10-Primitive Data Types.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_11_Variable_Length_Collection_TypesTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_11-Variable Length Collection Types.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_12_Compound_Value_TypeTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_12-Compound Value Type.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_13_Discretely_Sampled_Function_ValueTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_13-Discretely Sampled Function Value.sysml");
  }

  @Test
  public void parse_15_Properties_Values_Expressions_15_19_Materials_with_PropertiesTest() {
    this.parseSysML(pathToDir + "/15-Properties-Values-Expressions/15_19-Materials with Properties.sysml");
  }
}
