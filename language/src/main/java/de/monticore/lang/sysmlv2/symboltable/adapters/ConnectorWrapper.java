package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.ast.Comment;
import de.monticore.cardinality._symboltable.ICardinalityScope;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.lang.componentconnector._symboltable.IComponentConnectorScope;
import de.monticore.lang.componentconnector._visitor.ComponentConnectorTraverser;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolTOP;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adapter von ASTConnectionUsage auf ASTConnector. TODO Gehört vermutlich eigentlich nicht in dieses Package.
 */
public class ConnectorWrapper implements ASTConnector {

  protected ASTMCQualifiedName source;
  protected ASTMCQualifiedName target;

  public ConnectorWrapper(ASTConnectionUsage adaptee) {
    source = adaptee.getSrc();
    target = adaptee.getTgt();
  }

  protected ConnectorWrapper(ASTMCQualifiedName source, ASTMCQualifiedName target, String name) {
    this.source = MCBasicTypesMill.mCQualifiedNameBuilder()
        .addAllParts(source.getPartsList())
        .addParts(name)
        .build();
    this.target = MCBasicTypesMill.mCQualifiedNameBuilder()
        .addAllParts(target.getPartsList())
        .addParts(name)
        .build();
  }

  public static List<ConnectorWrapper> build(ASTConnectionUsage connection) {
    // Qualified -> "part.port"
    if(connection.getSrc().isQualified()) {
      // Name of part usage  is the one before last
      var partUsageName = connection.getSrc().getParts(connection.getSrc().sizeParts()-2);
      var partUsage = connection.getEnclosingScope().resolvePartUsage(partUsageName);
      if(!partUsage.isPresent()) {
        Log.warn("0x10009 Could not resolve part usage \"" + partUsageName + "\"",
            connection.get_SourcePositionStart(),
            connection.get_SourcePositionEnd());
        return Collections.emptyList();
      }
      else {
        if(!partUsage.get().getPartDef().isPresent()) {
          Log.warn("0x10010 Could not resolve part definition of \"" + partUsageName + "\"",
              connection.get_SourcePositionStart(),
              connection.get_SourcePositionEnd());
          return Collections.emptyList();
        }
        else {
          // Name of port is the last one
          var portName = connection.getSrc().getBaseName();
          var srcPort = partUsage.get().getPartDef().get().getSpannedScope().resolvePortUsage(portName);
          if(!srcPort.isPresent()) {
            Log.warn("0x10012 Could not resolve port \"" + portName
                    + "\" in part definition of \"" + partUsageName + "\"",
                connection.get_SourcePositionStart(),
                connection.get_SourcePositionEnd());
            return Collections.emptyList();
          }
          else {
            // All attributes across all super types
            var types = new ArrayList<>(srcPort.get().getTypesList());
            types.addAll(srcPort.get().getConjugatedTypesList());

            var attributes = types.stream()
                .filter(SymTypeExpression::hasTypeInfo)
                .map(SymTypeExpression::getTypeInfo)
                .map(TypeSymbolTOP::getSpannedScope)
                .flatMap(scope -> ((ISysMLv2Scope)scope).getLocalAttributeUsageSymbols().stream())
                .collect(Collectors.toList());

            // Now we simply append the attribute names to the name of the src
            return attributes.stream()
                .map(a -> new ConnectorWrapper(connection.getSrc(), connection.getTgt(), a.getName()))
                .collect(Collectors.toList());
          }
        }
      }
    }
    else {
      // Not qualified -> "port"
      var srcName = connection.getSrc().getQName();
      var srcPort = connection.getEnclosingScope().resolvePortUsage(srcName);

      // All attributes across all super types
      var types = new ArrayList<>(srcPort.get().getTypesList());
      types.addAll(srcPort.get().getConjugatedTypesList());

      var attributes = types.stream()
          .filter(SymTypeExpression::hasTypeInfo)
          .map(SymTypeExpression::getTypeInfo)
          .map(TypeSymbolTOP::getSpannedScope)
          .flatMap(scope -> ((ISysMLv2Scope)scope).getLocalAttributeUsageSymbols().stream())
          .collect(Collectors.toList());

      // Now we simply append the attribute names to the name of the src
      return attributes.stream()
          .map(a -> new ConnectorWrapper(connection.getSrc(), connection.getTgt(), a.getName()))
          .collect(Collectors.toList());
    }
  }

  @Override public ASTMCQualifiedName getSource() {
    return source;
  }

  @Override public void setSource(ASTMCQualifiedName source) {
    this.source = source;
  }

  @Override public ASTMCQualifiedName getTarget() {
    return target;
  }

  @Override public void setTarget(ASTMCQualifiedName target) {
    this.target = target;
  }


  /* #################### BOILERPLATE ######################## */

  @Override public void accept(ComponentConnectorTraverser visitor) {

  }

  @Override public boolean deepEquals(Object o) {
    return false;
  }

  @Override public boolean deepEquals(Object o, boolean forceSameOrder) {
    return false;
  }

  @Override public boolean deepEqualsWithComments(Object o) {
    return false;
  }

  @Override public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    return false;
  }

  @Override public boolean equalAttributes(Object o) {
    return false;
  }

  @Override public boolean equalsWithComments(Object o) {
    return false;
  }

  @Override public ASTConnector deepClone() {
    return null;
  }

  @Override public SourcePosition get_SourcePositionEnd() {
    return null;
  }

  @Override public void set_SourcePositionEnd(SourcePosition end) {

  }

  @Override public void set_SourcePositionEndAbsent() {

  }

  @Override public boolean isPresent_SourcePositionEnd() {
    return false;
  }

  @Override public SourcePosition get_SourcePositionStart() {
    return null;
  }

  @Override public void set_SourcePositionStart(SourcePosition start) {

  }

  @Override public void set_SourcePositionStartAbsent() {

  }

  @Override public boolean isPresent_SourcePositionStart() {
    return false;
  }

  @Override public void clear_PreComments() {

  }

  @Override public boolean add_PreComment(Comment precomment) {
    return false;
  }

  @Override public boolean addAll_PreComments(Collection<Comment> precomments) {
    return false;
  }

  @Override public boolean contains_PreComment(Object element) {
    return false;
  }

  @Override public boolean containsAll_PreComments(Collection<?> element) {
    return false;
  }

  @Override public boolean isEmpty_PreComments() {
    return false;
  }

  @Override public Iterator<Comment> iterator_PreComments() {
    return null;
  }

  @Override public boolean remove_PreComment(Object element) {
    return false;
  }

  @Override public boolean removeAll_PreComments(Collection<?> element) {
    return false;
  }

  @Override public boolean retainAll_PreComments(Collection<?> element) {
    return false;
  }

  @Override public int size_PreComments() {
    return 0;
  }

  @Override public Comment[] toArray_PreComments(Comment[] array) {
    return new Comment[0];
  }

  @Override public boolean removeIf_PreComment(Predicate<? super Comment> filter) {
    return false;
  }

  @Override public Spliterator<Comment> spliterator_PreComments() {
    return null;
  }

  @Override public Stream<Comment> stream_PreComments() {
    return null;
  }

  @Override public Stream<Comment> parallelStream_PreComments() {
    return null;
  }

  @Override public void forEach_PreComments(Consumer<? super Comment> action) {

  }

  @Override public void add_PreComment(int index, Comment precomment) {

  }

  @Override public boolean addAll_PreComments(int index, Collection<Comment> precomments) {
    return false;
  }

  @Override public Comment get_PreComment(int index) {
    return null;
  }

  @Override public int indexOf_PreComment(Object element) {
    return 0;
  }

  @Override public int lastIndexOf_PreComment(Object element) {
    return 0;
  }

  @Override public boolean equals_PreComments(Object element) {
    return false;
  }

  @Override public int hashCode_PreComments() {
    return 0;
  }

  @Override public ListIterator<Comment> listIterator_PreComments() {
    return null;
  }

  @Override public Comment remove_PreComment(int index) {
    return null;
  }

  @Override public List<Comment> subList_PreComments(int start, int end) {
    return null;
  }

  @Override public void replaceAll_PreComments(UnaryOperator<Comment> operator) {

  }

  @Override public void sort_PreComments(Comparator<? super Comment> comparator) {

  }

  @Override public void set_PreCommentList(List<Comment> preComments) {

  }

  @Override public List<Comment> get_PreCommentList() {
    return null;
  }

  @Override public ListIterator<Comment> listIterator_PreComments(int index) {
    return null;
  }

  @Override public Comment set_PreComment(int index, Comment precomment) {
    return null;
  }

  @Override public Object[] toArray_PreComments() {
    return new Object[0];
  }

  @Override public void clear_PostComments() {

  }

  @Override public boolean add_PostComment(Comment postcomment) {
    return false;
  }

  @Override public boolean addAll_PostComments(Collection<Comment> postcomments) {
    return false;
  }

  @Override public boolean contains_PostComment(Object element) {
    return false;
  }

  @Override public boolean containsAll_PostComments(Collection<?> element) {
    return false;
  }

  @Override public boolean isEmpty_PostComments() {
    return false;
  }

  @Override public Iterator<Comment> iterator_PostComments() {
    return null;
  }

  @Override public boolean remove_PostComment(Object element) {
    return false;
  }

  @Override public boolean removeAll_PostComments(Collection<?> element) {
    return false;
  }

  @Override public boolean retainAll_PostComments(Collection<?> element) {
    return false;
  }

  @Override public int size_PostComments() {
    return 0;
  }

  @Override public Comment[] toArray_PostComments(Comment[] array) {
    return new Comment[0];
  }

  @Override public boolean removeIf_PostComment(Predicate<? super Comment> filter) {
    return false;
  }

  @Override public Spliterator<Comment> spliterator_PostComments() {
    return null;
  }

  @Override public Stream<Comment> stream_PostComments() {
    return null;
  }

  @Override public Stream<Comment> parallelStream_PostComments() {
    return null;
  }

  @Override public void forEach_PostComments(Consumer<? super Comment> action) {

  }

  @Override public void add_PostComment(int index, Comment postcomment) {

  }

  @Override public boolean addAll_PostComments(int index, Collection<Comment> postcomments) {
    return false;
  }

  @Override public Comment get_PostComment(int index) {
    return null;
  }

  @Override public int indexOf_PostComment(Object element) {
    return 0;
  }

  @Override public int lastIndexOf_PostComment(Object element) {
    return 0;
  }

  @Override public boolean equals_PostComments(Object element) {
    return false;
  }

  @Override public int hashCode_PostComments() {
    return 0;
  }

  @Override public ListIterator<Comment> listIterator_PostComments() {
    return null;
  }

  @Override public Comment remove_PostComment(int index) {
    return null;
  }

  @Override public List<Comment> subList_PostComments(int start, int end) {
    return null;
  }

  @Override public void replaceAll_PostComments(UnaryOperator<Comment> operator) {

  }

  @Override public void sort_PostComments(Comparator<? super Comment> comparator) {

  }

  @Override public void set_PostCommentList(List<Comment> postComments) {

  }

  @Override public List<Comment> get_PostCommentList() {
    return null;
  }

  @Override public ListIterator<Comment> listIterator_PostComments(int index) {
    return null;
  }

  @Override public Comment set_PostComment(int index, Comment postcomment) {
    return null;
  }

  @Override public Object[] toArray_PostComments() {
    return new Object[0];
  }

  @Override public IComponentConnectorScope getEnclosingScope() {
    return null;
  }

  @Override public void setEnclosingScope(IComponentConnectorScope enclosingScope) {

  }

  @Override public void setEnclosingScope(IMCBasicTypesScope enclosingScope) {

  }

  @Override public void setEnclosingScope(IExpressionsBasisScope enclosingScope) {

  }

  @Override public void setEnclosingScope(ICompSymbolsScope enclosingScope) {

  }

  @Override public void setEnclosingScope(ICardinalityScope enclosingScope) {

  }

  @Override public void setEnclosingScope(IMCBasicsScope enclosingScope) {

  }

  @Override public void setEnclosingScope(IMCLiteralsBasisScope enclosingScope) {

  }

  @Override public void setEnclosingScope(IBasicSymbolsScope enclosingScope) {

  }

  @Override public void setEnclosingScope(IMCCommonLiteralsScope enclosingScope) {

  }
}
