package de.monticore.lang.sysmlv2._symboltable;

import java.util.List;

public interface ISysMLv2ArtifactScope extends ISysMLv2ArtifactScopeTOP {
  @Override
  default public List<String> getRemainingNameForResolveDown (String symbolName) {
    // AS is transparent
    return List.of(symbolName);
  }
}
