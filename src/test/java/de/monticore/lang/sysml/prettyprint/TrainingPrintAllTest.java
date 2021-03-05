package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.parser.SysMLParserMultipleFiles;
import de.monticore.lang.sysml.prettyprint.PrettyPrinter2;
import de.monticore.lang.sysml.sysml._parser.SysMLParser;
import de.se_rwth.commons.logging.Log;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class TrainingPrintAllTest {

	private boolean ppSysML(String path) {
		SysMLParserMultipleFiles parser = new SysMLParserMultipleFiles();
		try {
			ASTUnit unitexp = parser.parseSingleFile(path);
			PrettyPrinter2 pp = new PrettyPrinter2();
			String s = pp.prettyPrint(unitexp);
			SysMLParser parser2 = new SysMLParser();
			Optional<ASTUnit> unitact = parser2.parse_String(s);
			if (!unitact.isPresent()) {
				Log.error("Error: The model path " + path + " had an empty model.");
				throw new IOException("Error: The model path " + path + " had an empty model.");
			}
			return unitexp.deepEquals(unitact.get());
		} catch (Exception e) {
			return false;
		}
	}

	public void print(String content, String path) {
		// print to stdout or file
		if (path.isEmpty()) {
			System.out.println(content);
		} else {
			File f = new File(path.trim());
			// create directories (logs error otherwise)
			f.getAbsoluteFile().getParentFile().mkdirs();

			FileWriter writer;
			try {
				writer = new FileWriter(f);
				writer.write(content);
				writer.close();
			} catch (IOException e) {
				Log.error("0xA7198 Could not write to file " + f.getAbsolutePath());
			}
		}
	}

	private final String pathToDir = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src"
		+ "/training/";

	@Test
	@Ignore // TODO fix me
	public void prettyprint_01_Packages_Comment_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/01. Packages/Comment Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_01_Packages_Package_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/01. Packages/Package Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_02_Blocks_Blocks_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/02. Blocks/Blocks Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_03_Generalization_Generalization_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/03. Generalization/Generalization Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_04_Subsetting_Subsetting_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/04. Subsetting/Subsetting Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_05_Redefinition_Redefinition_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/05. Redefinition/Redefinition Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_06_Parts_Parts_Example_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/06. Parts/Parts Example-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_06_Parts_Parts_Example_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/06. Parts/Parts Example-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_07_Individuals_Individuals_and_Roles_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/07. Individuals/Individuals and Roles-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_07_Individuals_Individuals_and_Snapshots_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/07. Individuals/Individuals and Snapshots Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_07_Individuals_Individuals_and_Time_SlicesTest() {
		assertTrue(this.ppSysML(pathToDir + "/07. Individuals/Individuals and Time Slices.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_08_Connectors_Connectors_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/08. Connectors/Connectors Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_09_Ports_Port_Conjugation_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/09. Ports/Port Conjugation Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_09_Ports_Port_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/09. Ports/Port Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_10_Interfaces_Interface_Decomposition_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/10. Interfaces/Interface Decomposition Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_10_Interfaces_Interface_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/10. Interfaces/Interface Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_11_Binding_Connectors_Binding_Connectors_Example_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/11. Binding Connectors/Binding Connectors Example-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_11_Binding_Connectors_Binding_Connectors_Example_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/11. Binding Connectors/Binding Connectors Example-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_12_Item_Flows_Streaming_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/12. Item Flows/Streaming Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_12_Item_Flows_Streaming_Interface_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/12. Item Flows/Streaming Interface Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_13_Activities_Activity_Example_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/13. Activities/Activity Example-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_13_Activities_Activity_Example_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/13. Activities/Activity Example-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_14_Actions_Action_DecompositionTest() {
		assertTrue(this.ppSysML(pathToDir + "/14. Actions/Action Decomposition.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_15_Succession_Conditional_Succession_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/15. Succession/Conditional Succession Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_15_Succession_Succession_Example_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/15. Succession/Succession Example-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_15_Succession_Succession_Example_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/15. Succession/Succession Example-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_16_Signaling_Signaling_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/16. Signaling/Signaling Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_17_Control_CameraTest() {
		assertTrue(this.ppSysML(pathToDir + "/17. Control/Camera.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_17_Control_Decision_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/17. Control/Decision Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_17_Control_Fork_Join_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/17. Control/Fork Join Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_17_Control_Merge_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/17. Control/Merge Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_18_Action_Allocation_Action_Allocation_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/18. Action Allocation/Action Allocation Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_19_State_Definitions_State_Definition_Example_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/19. State Definitions/State Definition Example-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_19_State_Definitions_State_Definition_Example_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/19. State Definitions/State Definition Example-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_20_States_State_ActionsTest() {
		assertTrue(this.ppSysML(pathToDir + "/20. States/State Actions.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_20_States_State_Decomposition_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/20. States/State Decomposition-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_20_States_State_Decomposition_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/20. States/State Decomposition-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_21_State_Allocation_State_Allocation_ExampleTest() {
		assertTrue(this.ppSysML(pathToDir + "/21. State Allocation/State Allocation Example.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_21_Transitions_Transition_ActionsTest() {
		assertTrue(this.ppSysML(pathToDir + "/21. Transitions/Transition Actions.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_23_Expressions_MassRollupTest() {
		assertTrue(this.ppSysML(pathToDir + "/23. Expressions/MassRollup.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_23_Expressions_VehicleMassesTest() {
		assertTrue(this.ppSysML(pathToDir + "/23. Expressions/VehicleMasses.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_24_Constraints_Constraint_Assertions_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/24. Constraints/Constraint Assertions-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_24_Constraints_Constraint_Assertions_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/24. Constraints/Constraint Assertions-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_24_Constraints_Constraints_Example_1Test() {
		assertTrue(this.ppSysML(pathToDir + "/24. Constraints/Constraints Example-1.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_24_Constraints_Constraints_Example_2Test() {
		assertTrue(this.ppSysML(pathToDir + "/24. Constraints/Constraints Example-2.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_24_Constraints_Derivation_ConstraintsTest() {
		assertTrue(this.ppSysML(pathToDir + "/24. Constraints/Derivation Constraints.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_25_Requirements_Requirement_DefinitionsTest() {
		assertTrue(this.ppSysML(pathToDir + "/25. Requirements/Requirement Definitions.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_25_Requirements_Requirement_GroupsTest() {
		assertTrue(this.ppSysML(pathToDir + "/25. Requirements/Requirement Groups.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_25_Requirements_Requirement_SatisfactionTest() {
		assertTrue(this.ppSysML(pathToDir + "/25. Requirements/Requirement Satisfaction.sysml"));
	}

	@Test
	@Ignore // TODO fix me
	public void prettyprint_25_Requirements_Requirement_UsagesTest() {
		assertTrue(this.ppSysML(pathToDir + "/25. Requirements/Requirement Usages.sysml"));
	}
}