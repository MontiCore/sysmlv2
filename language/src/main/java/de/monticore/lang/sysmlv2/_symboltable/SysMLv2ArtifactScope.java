package de.monticore.lang.sysmlv2._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ArtifactScopes in SysMLv2 neither have a name, nor a package and are completely "transparent" to the resolving
 * process.
 */
public class SysMLv2ArtifactScope extends SysMLv2ArtifactScopeTOP {

  /**
   * When resolving down, an ArtifactScope is completely transparent. It does not alter the remaining name.
   * Namespaces in SysMLv2 are modeled explicitly within the model file. Neither does an artifact's name affect symbol
   * resolution, not does the artifact have an implicit package.
   */
  @Override
  public List<String> getRemainingNameForResolveDown(String symbolName) {
    List<String> res = new ArrayList<String>();
    res.add(symbolName);
    return res;
  }

  /**
   * An artifact scope does not have a name.
   */
  @Override
  public boolean isPresentName() {
    return false;
  }

  /**
   * Since an artifact scope does not have a name, getting it is impossible.
   */
  @Override
  public String getName() {
    Log.error("ArtifactScopes in SysMLv2 do not have names. \"getName\" was called erroneously.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

}
