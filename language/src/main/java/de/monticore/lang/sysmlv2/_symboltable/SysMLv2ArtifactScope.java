package de.monticore.lang.sysmlv2._symboltable;

import java.util.UUID;

public class SysMLv2ArtifactScope extends SysMLv2ArtifactScopeTOP {

  @Override
  public String getName()  {
    if (!super.isPresentName()) {
      // An artifact always has a name. If the generated code could not determine a name,
      // that means there wasnt a single top level symbol. In such cases, we set a random
      // name to avoid crashing the symbol table / resolution. But it basically obstructs
      // any symbols from being referencable from the outside.
      setName("AnonymousArtifact_" + UUID.randomUUID());
    }
    return super.getName();
  }

}
