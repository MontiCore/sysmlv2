standard library package AnalysisTooling {
    private import ScalarValues::*;

    metadata def ToolExecution {
      attribute toolName : String;
      attribute uri : String;
      }
    metadata def ToolVariable {
    attribute name : String;
    }
}
