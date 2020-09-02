package de.monticore.lang.sysml.parser.official.sysml.src.examples;

import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ExampleParseAllTest {

  private final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src"
      + "/examples/";

  public void parseSysML(String path) {
    SysMLParserForTesting parser = new SysMLParserForTesting();
    parser.parseSysML(path);
  }

  @Test
  public void parse_Action_Sequencing_Example_ActionInterfaceProtocolTest() {
    this.parseSysML(pathToDir + "/Action Sequencing Example/ActionInterfaceProtocol.sysml");
  }

  @Test
  public void parse_Action_Sequencing_Example_ActionSequencingTest() {
    this.parseSysML(pathToDir + "/Action Sequencing Example/ActionSequencing.sysml");
  }

  @Test
  public void parse_Arrowhead_Framework_Example_AHFCoreLibTest() {
    this.parseSysML(pathToDir + "/Arrowhead Framework Example/AHFCoreLib.sysml");
  }

  @Test
  public void parse_Arrowhead_Framework_Example_AHFNorwayAppSystemTest() {
    this.parseSysML(pathToDir + "/Arrowhead Framework Example/AHFNorwayAppSystem.sysml");
  }

  @Test
  public void parse_Arrowhead_Framework_Example_AHFProfileLibTest() {
    this.parseSysML(pathToDir + "/Arrowhead Framework Example/AHFProfileLib.sysml");
  }

  @Test
  public void parse_Camera_Example_CameraTest() {
    this.parseSysML(pathToDir + "/Camera Example/Camera.sysml");
  }

  @Test
  public void parse_Camera_Example_CameraWithOperationsTest() {
    this.parseSysML(pathToDir + "/Camera Example/CameraWithOperations.sysml");
  }

  @Test
  public void parse_Camera_Example_PictureTakingTest() {
    this.parseSysML(pathToDir + "/Camera Example/PictureTaking.sysml");
  }

  @Test
  public void parse_Comment_Examples_CommentsTest() {
    this.parseSysML(pathToDir + "/Comment Examples/Comments.sysml");
  }

  @Test
  public void parse_Flashlight_Example_Flashlight_ExampleTest() {
    this.parseSysML(pathToDir + "/Flashlight Example/Flashlight Example.sysml");
  }

  @Test
  public void parse_Mass_Roll_up_Example_MassConstraintExampleTest() {
    this.parseSysML(pathToDir + "/Mass Roll-up Example/MassConstraintExample.sysml");
  }

  @Test
  public void parse_Mass_Roll_up_Example_MassRollupTest() {
    this.parseSysML(pathToDir + "/Mass Roll-up Example/MassRollup.sysml");
  }

  @Test
  public void parse_Mass_Roll_up_Example_SimpleVehicleMassTest() {
    this.parseSysML(pathToDir + "/Mass Roll-up Example/SimpleVehicleMass.sysml");
  }

  @Test
  public void parse_Mass_Roll_up_Example_VehiclesTest() {
    this.parseSysML(pathToDir + "/Mass Roll-up Example/Vehicles.sysml");
  }

  @Test
  public void parse_Packet_Example_PacketsTest() {
    this.parseSysML(pathToDir + "/Packet Example/Packets.sysml");
  }

  @Test
  public void parse_Packet_Example_PacketUsageTest() {
    this.parseSysML(pathToDir + "/Packet Example/PacketUsage.sysml");
  }

  @Test
  public void parse_Requirements_Examples_HSUVRequirementsTest() {
    this.parseSysML(pathToDir + "/Requirements Examples/HSUVRequirements.sysml");
  }

  @Test
  public void parse_Requirements_Examples_MIDRequirementsTest() {
    this.parseSysML(pathToDir + "/Requirements Examples/MIDRequirements.sysml");
  }

  @Test
  public void parse_Room_Model_RoomModelTest() {
    this.parseSysML(pathToDir + "/Room Model/RoomModel.sysml");
  }

  @Test
  public void parse_Simple_Tests_ActionTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/ActionTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_AnalysisExampleTest() {
    this.parseSysML(pathToDir + "/Simple Tests/AnalysisExample.sysml");
  }

  @Test
  public void parse_Simple_Tests_BlockTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/BlockTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_ConjugationTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/ConjugationTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_ConstraintTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/ConstraintTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_ControlNodeTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/ControlNodeTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_DecisionTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/DecisionTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_SmallTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/SmallTest.sysml");
  }

  @Test
  public void parse_Simple_Tests_StateTestTest() {
    this.parseSysML(pathToDir + "/Simple Tests/StateTest.sysml");
  }

  @Test
  public void parse_v1_Spec_Examples_841_Wheel_Hub_Assembly_Wheel_Package___UpdatedTest() {
    this.parseSysML(pathToDir + "/v1 Spec Examples/8.4.1 Wheel Hub Assembly/Wheel Package - Updated.sysml");
  }

  @Test
  public void parse_v1_Spec_Examples_841_Wheel_Hub_Assembly_Wheel_PackageTest() {
    this.parseSysML(pathToDir + "/v1 Spec Examples/8.4.1 Wheel Hub Assembly/Wheel Package.sysml");
  }

  @Test
  public void parse_v1_Spec_Examples_845_Constraining_Decomposition_Vehicle_Decomposition___UpdatedTest() {
    this.parseSysML(pathToDir + "/v1 Spec Examples/8.4.5 Constraining Decomposition/Vehicle Decomposition - Updated"
        + ".sysml");
  }

  @Test
  public void parse_v1_Spec_Examples_845_Constraining_Decomposition_Vehicle_DecompositionTest() {
    this.parseSysML(pathToDir + "/v1 Spec Examples/8.4.5 Constraining Decomposition/Vehicle Decomposition.sysml");
  }

  @Test
  public void parse_v1_Spec_Examples_D478_Dynamics_HSUVDynamicsTest() {
    this.parseSysML(pathToDir + "/v1 Spec Examples/D.4.7.8 Dynamics/HSUVDynamics.sysml");
  }

  @Test
  public void parse_Vehicle_Example_INCOSE_IW_2020_DemoTest() {
    this.parseSysML(pathToDir + "/Vehicle Example/INCOSE IW 2020 Demo.sysml");
  }

  @Test
  public void parse_Vehicle_Example_sfriedenthal_VehicleModel_1Test() {
    this.parseSysML(pathToDir + "/Vehicle Example/sfriedenthal_VehicleModel_1.sysml");
  }

  @Test
  public void parse_Vehicle_Example_VehicleDefinitionsTest() {
    this.parseSysML(pathToDir + "/Vehicle Example/VehicleDefinitions.sysml");
  }

  @Test
  public void parse_Vehicle_Example_VehicleIndividualsTest() {
    this.parseSysML(pathToDir + "/Vehicle Example/VehicleIndividuals.sysml");
  }

  @Test
  public void parse_Vehicle_Example_VehicleModel_evs_updateTest() {
    this.parseSysML(pathToDir + "/Vehicle Example/VehicleModel_evs_update.sysml");
  }

  @Test
  public void parse_Vehicle_Example_VehicleUsagesTest() {
    this.parseSysML(pathToDir + "/Vehicle Example/VehicleUsages.sysml");
  }
}
