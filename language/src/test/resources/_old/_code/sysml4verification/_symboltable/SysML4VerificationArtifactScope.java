package de.monticore.lang.sysml4verification._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Dopplung des {@link de.monticore.lang.sysmlv2._symboltable.SysMLv2ArtifactScope}.
 * Anscheinend nötig, da die generierten Scopes nicht die Implementation der Obersprache (SysMLv2) nutzen.
 */
public class SysML4VerificationArtifactScope extends SysML4VerificationArtifactScopeTOP {

  /**
   * When resolving down, an ArtifactScope is completely transparent. It does not alter the remaining name.
   * Namespaces in SysMLv2 are modeled explicitly within the model file. Neither does an artifact's name affect symbol
   * resolution, nor does the artifact have an implicit package.
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
