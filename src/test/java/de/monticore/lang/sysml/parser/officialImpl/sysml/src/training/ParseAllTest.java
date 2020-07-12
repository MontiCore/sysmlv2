package de.monticore.lang.sysml.parser.officialImpl.sysml.src.training;

import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._parser.SysMLParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ParseAllTest {
  public void parseSysML(String path){
    Log.enableFailQuick(false);
    SysMLParser parser = new SysMLParser();
    Path model = Paths.get(path);
    try {
      Optional<ASTUnit> sysmlPackage = parser.parse(model.toString());
      //assertFalse(parser.hasErrors());
      //assertTrue(sysmlPackage.isPresent());

    }catch( IOException e){
      //e.printStackTrace();
      //fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }

  private final String pathToDir= "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src/training/";

  @Ignore
  @Test
  public void parse01_Packages_Comment_ExampleTest(){
    this.parseSysML( pathToDir +  "01. Packages\\Comment Example.sysml");
  }

  @Test
  public void parse01_Packages_Package_ExampleTest(){
    this.parseSysML( pathToDir +  "01. Packages\\Package Example.sysml");
  }


  @Test
  public void parse02_Blocks_Blocks_ExampleTest(){
    this.parseSysML( pathToDir +  "02. Blocks\\Blocks Example.sysml");
  }


  @Test
  public void parse03_Generalization_Generalization_ExampleTest(){
    this.parseSysML( pathToDir +  "03. Generalization\\Generalization Example.sysml");
  }


  @Test
  public void parse04_Subsetting_Subsetting_ExampleTest(){
    this.parseSysML( pathToDir +  "04. Subsetting\\Subsetting Example.sysml");
  }

  @Ignore
  @Test
  public void parse05_Redefinition_Redefinition_ExampleTest(){
    this.parseSysML( pathToDir +  "05. Redefinition\\Redefinition Example.sysml");
  }

  @Ignore
  @Test
  public void parse06_Parts_Parts_Example_1Test(){
    this.parseSysML( pathToDir +  "06. Parts\\Parts Example-1.sysml");
  }

  @Ignore
  @Test
  public void parse06_Parts_Parts_Example_2Test(){
    this.parseSysML( pathToDir +  "06. Parts\\Parts Example-2.sysml");
  }

  @Ignore
  @Test
  public void parse07_Individuals_Individuals_and_Roles_1Test(){
    this.parseSysML( pathToDir +  "07. Individuals\\Individuals and Roles-1.sysml");
  }

  @Ignore
  @Test
  public void parse07_Individuals_Individuals_and_Snapshots_ExampleTest(){
    this.parseSysML( pathToDir +  "07. Individuals\\Individuals and Snapshots Example.sysml");
  }

  @Ignore
  @Test
  public void parse07_Individuals_Individuals_and_Time_SlicesTest(){
    this.parseSysML( pathToDir +  "07. Individuals\\Individuals and Time Slices.sysml");
  }

  @Ignore
  @Test
  public void parse08_Connectors_Connectors_ExampleTest(){
    this.parseSysML( pathToDir +  "08. Connectors\\Connectors Example.sysml");
  }

  @Ignore
  @Test
  public void parse09_Ports_Port_Conjugation_ExampleTest(){
    this.parseSysML( pathToDir +  "09. Ports\\Port Conjugation Example.sysml");
  }

  @Ignore
  @Test
  public void parse09_Ports_Port_ExampleTest(){
    this.parseSysML( pathToDir +  "09. Ports\\Port Example.sysml");
  }

  @Ignore
  @Test
  public void parse10_Interfaces_Interface_Decomposition_ExampleTest(){
    this.parseSysML( pathToDir +  "10. Interfaces\\Interface Decomposition Example.sysml");
  }

  @Ignore
  @Test
  public void parse10_Interfaces_Interface_ExampleTest(){
    this.parseSysML( pathToDir +  "10. Interfaces\\Interface Example.sysml");
  }

  @Ignore
  @Test
  public void parse11_Binding_Connectors_Binding_Connectors_Example_1Test(){
    this.parseSysML( pathToDir +  "11. Binding Connectors\\Binding Connectors Example-1.sysml");
  }

  @Ignore
  @Test
  public void parse11_Binding_Connectors_Binding_Connectors_Example_2Test(){
    this.parseSysML( pathToDir +  "11. Binding Connectors\\Binding Connectors Example-2.sysml");
  }

  @Ignore
  @Test
  public void parse12_Item_Flows_Streaming_ExampleTest(){
    this.parseSysML( pathToDir +  "12. Item Flows\\Streaming Example.sysml");
  }

  @Ignore
  @Test
  public void parse12_Item_Flows_Streaming_Interface_ExampleTest(){
    this.parseSysML( pathToDir +  "12. Item Flows\\Streaming Interface Example.sysml");
  }

  @Ignore
  @Test
  public void parse13_Activities_Activity_Example_1Test(){
    this.parseSysML( pathToDir +  "13. Activities\\Activity Example-1.sysml");
  }

  @Ignore
  @Test
  public void parse13_Activities_Activity_Example_2Test(){
    this.parseSysML( pathToDir +  "13. Activities\\Activity Example-2.sysml");
  }

  @Ignore
  @Test
  public void parse14_Actions_Action_DecompositionTest(){
    this.parseSysML( pathToDir +  "14. Actions\\Action Decomposition.sysml");
  }

  @Ignore
  @Test
  public void parse15_Succession_Conditional_Succession_ExampleTest(){
    this.parseSysML( pathToDir +  "15. Succession\\Conditional Succession Example.sysml");
  }

  @Ignore
  @Test
  public void parse15_Succession_Succession_Example_1Test(){
    this.parseSysML( pathToDir +  "15. Succession\\Succession Example-1.sysml");
  }

  @Ignore
  @Test
  public void parse15_Succession_Succession_Example_2Test(){
    this.parseSysML( pathToDir +  "15. Succession\\Succession Example-2.sysml");
  }

  @Ignore
  @Test
  public void parse16_Signaling_Signaling_ExampleTest(){
    this.parseSysML( pathToDir +  "16. Signaling\\Signaling Example.sysml");
  }

  @Ignore
  @Test
  public void parse17_Control_CameraTest(){
    this.parseSysML( pathToDir +  "17. Control\\Camera.sysml");
  }

  @Ignore
  @Test
  public void parse17_Control_Decision_ExampleTest(){
    this.parseSysML( pathToDir +  "17. Control\\Decision Example.sysml");
  }

  @Ignore
  @Test
  public void parse17_Control_Fork_Join_ExampleTest(){
    this.parseSysML( pathToDir +  "17. Control\\Fork Join Example.sysml");
  }

  @Ignore
  @Test
  public void parse17_Control_Merge_ExampleTest(){
    this.parseSysML( pathToDir +  "17. Control\\Merge Example.sysml");
  }

  @Ignore
  @Test
  public void parse18_Action_Allocation_Action_Allocation_ExampleTest(){
    this.parseSysML( pathToDir +  "18. Action Allocation\\Action Allocation Example.sysml");
  }

  @Ignore
  @Test
  public void parse19_State_Definitions_State_Definition_Example_1Test(){
    this.parseSysML( pathToDir +  "19. State Definitions\\State Definition Example-1.sysml");
  }

  @Ignore
  @Test
  public void parse19_State_Definitions_State_Definition_Example_2Test(){
    this.parseSysML( pathToDir +  "19. State Definitions\\State Definition Example-2.sysml");
  }

  @Ignore
  @Test
  public void parse20_States_State_ActionsTest(){
    this.parseSysML( pathToDir +  "20. States\\State Actions.sysml");
  }

  @Ignore
  @Test
  public void parse20_States_State_Decomposition_1Test(){
    this.parseSysML( pathToDir +  "20. States\\State Decomposition-1.sysml");
  }

  @Ignore
  @Test
  public void parse20_States_State_Decomposition_2Test(){
    this.parseSysML( pathToDir +  "20. States\\State Decomposition-2.sysml");
  }

  @Ignore
  @Test
  public void parse21_State_Allocation_State_Allocation_ExampleTest(){
    this.parseSysML( pathToDir +  "21. State Allocation\\State Allocation Example.sysml");
  }

  @Ignore
  @Test
  public void parse21_Transitions_Transition_ActionsTest(){
    this.parseSysML( pathToDir +  "21. Transitions\\Transition Actions.sysml");
  }

  @Ignore
  @Test
  public void parse23_Expressions_MassRollupTest(){
    this.parseSysML( pathToDir +  "23. Expressions\\MassRollup.sysml");
  }

  @Ignore
  @Test
  public void parse23_Expressions_VehicleMassesTest(){
    this.parseSysML( pathToDir +  "23. Expressions\\VehicleMasses.sysml");
  }

  @Ignore
  @Test
  public void parse24_Constraints_Constraint_Assertions_1Test(){
    this.parseSysML( pathToDir +  "24. Constraints\\Constraint Assertions-1.sysml");
  }

  @Ignore
  @Test
  public void parse24_Constraints_Constraint_Assertions_2Test(){
    this.parseSysML( pathToDir +  "24. Constraints\\Constraint Assertions-2.sysml");
  }

  @Ignore
  @Test
  public void parse24_Constraints_Constraints_Example_1Test(){
    this.parseSysML( pathToDir +  "24. Constraints\\Constraints Example-1.sysml");
  }

  @Ignore
  @Test
  public void parse24_Constraints_Constraints_Example_2Test(){
    this.parseSysML( pathToDir +  "24. Constraints\\Constraints Example-2.sysml");
  }

  @Ignore
  @Test
  public void parse24_Constraints_Derivation_ConstraintsTest(){
    this.parseSysML( pathToDir +  "24. Constraints\\Derivation Constraints.sysml");
  }

  @Ignore
  @Test
  public void parse25_Requirements_Requirement_DefinitionsTest(){
    this.parseSysML( pathToDir +  "25. Requirements\\Requirement Definitions.sysml");
  }

  @Ignore
  @Test
  public void parse25_Requirements_Requirement_GroupsTest(){
    this.parseSysML( pathToDir +  "25. Requirements\\Requirement Groups.sysml");
  }

  @Ignore
  @Test
  public void parse25_Requirements_Requirement_SatisfactionTest(){
    this.parseSysML( pathToDir +  "25. Requirements\\Requirement Satisfaction.sysml");
  }

  @Ignore
  @Test
  public void parse25_Requirements_Requirement_UsagesTest(){
    this.parseSysML( pathToDir +  "25. Requirements\\Requirement Usages.sysml");
  }

}
