package schrott._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * The generated Artifact Scope default implementations for resolution(getRemainingNameForResolveDown) use the
 * assumption that models have only one top-level component.
 * Thus, each artifact scope is assumed to have a name (the name of the top-level component).
 * We implement the Artifact Scope by hand to avoid setting the artifact scope name when there is none.
 */
public class SysML4VerificationArtifactScope extends SysML4VerificationArtifactScopeTOP {

  /**
   * Diese Methode berechnet den "restlichen" Namen eines gesuchten Symbolnamens beim Resolve-Down, also dem Resolven
   * des Namens im Scope oder (rekursiv) in seinen Subscopes.
   * Beispiel: im Scope namens "A" wird bei Eingabe "A.B.c" nach "B.c" in den Subscopes weitergesucht.
   *
   * Die generierte Default-Impl. geht dabei davon aus, dass das Scope immer einen Namen hat. Das ist in der SysML 2.0
   * nicht der Fall. Das ArtifactScope hat eigentlich nie einen Namen, da Namespaces explizit (durch das Keyword
   * `package`) angegeben werden. Weiterhin wird das Paket eines ArtifactScopes benutzt, um den Symbolnamen zu kürzen.
   * Diese Impl. scheint sich an Paketstrukturen zu orientieren, die man zB. aus Java kennt: Das Artifact liegt
   * entsprechend seiner Paketangabe (in Java die erste Zeile) in einer Ordnerstruktur. In SysML sind Pakete (i.e.,
   * jedes einzelne Paket) jeweils Scopes, die IN einem Artifact explizit (siehe oben) angebeben werden. Demzufolge hat
   * ein Artifact kein Paket. Das Artifact ist für das Resolving vollkommen transparent.
   */
  @Override
  public List<String> getRemainingNameForResolveDown(String symbolName) {
    return List.of(symbolName);
  }

  @Override
  public boolean isPresentName() {
    return false;
  }

  @Override
  public String getName() {
    Log.error("0xA7003x96605 get for Name can't return a value. Attribute is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
}
