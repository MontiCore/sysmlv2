package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.ast.Comment;
import de.monticore.cardinality._symboltable.ICardinalityScope;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.lang.componentconnector._ast.ASTStateSpace;
import de.monticore.lang.componentconnector._symboltable.IComponentConnectorScope;
import de.monticore.lang.componentconnector._visitor.ComponentConnectorTraverser;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.symbols.basicsymbols._ast.ASTVariable;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope;
import de.se_rwth.commons.SourcePosition;

import java.util.Collection;
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

public class StateSpaceWrapper implements ASTStateSpace {
  private final StateUsageSymbol adaptee;
  // In aut check exhibit
  StateSpaceWrapper(StateUsageSymbol adaptee) {
    this.adaptee = adaptee;
  }

  public StateUsageSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public List<ASTMCQualifiedName> getStatesList() {
    return getAdaptee()
        .getSpannedScope()
        .getLocalStateUsageSymbols()
        .stream()
        .map(state ->
            new ASTMCQualifiedNameBuilder()
                .setPartsList(List.of(state.getFullName().split("\\."))).build())
        .collect(Collectors.toList());
  }


  @Override
  public List<String> getVariablesList() {
    // TODO fqn name might be necessary.
    return ((ISysMLv2Scope)getAdaptee().getSpannedScope()).getLocalAttributeUsageSymbols().stream().map(
        AttributeUsageSymbol::getName).collect(Collectors.toList());
  }

  @Override
  public List<Optional<VariableSymbol>> getVariablesSymbolList() {
    return getVariablesList().stream().map(variable ->
            getAdaptee().getSpannedScope().resolveVariableLocally(variable))
        .collect(Collectors.toList());
  }

  /* #################### BOILERPLATE ######################## */

  @Override
  public void accept(ComponentConnectorTraverser visitor) {

  }

  @Override
  public boolean containsVariablesSymbol(Object element) {
    return false;
  }

  @Override
  public boolean containsAllVariablesSymbol(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean isEmptyVariablesSymbol() {
    return false;
  }

  @Override
  public Iterator<Optional<VariableSymbol>> iteratorVariablesSymbol() {
    return null;
  }

  @Override
  public int sizeVariablesSymbol() {
    return 0;
  }

  @Override
  public Optional<VariableSymbol>[] toArrayVariablesSymbol(Optional<VariableSymbol>[] array) {
    return new Optional[0];
  }

  @Override
  public Object[] toArrayVariablesSymbol() {
    return new Object[0];
  }

  @Override
  public Spliterator<Optional<VariableSymbol>> spliteratorVariablesSymbol() {
    return null;
  }

  @Override
  public Stream<Optional<VariableSymbol>> streamVariablesSymbol() {
    return null;
  }

  @Override
  public Stream<Optional<VariableSymbol>> parallelStreamVariablesSymbol() {
    return null;
  }

  @Override
  public Optional<VariableSymbol> getVariablesSymbol(int index) {
    return Optional.empty();
  }

  @Override
  public int indexOfVariablesSymbol(Object element) {
    return 0;
  }

  @Override
  public int lastIndexOfVariablesSymbol(Object element) {
    return 0;
  }

  @Override
  public boolean equalsVariablesSymbol(Object o) {
    return false;
  }

  @Override
  public int hashCodeVariablesSymbol() {
    return 0;
  }

  @Override
  public ListIterator<Optional<VariableSymbol>> listIteratorVariablesSymbol() {
    return null;
  }

  @Override
  public ListIterator<Optional<VariableSymbol>> listIteratorVariablesSymbol(int index) {
    return null;
  }

  @Override
  public List<Optional<VariableSymbol>> subListVariablesSymbol(int start, int end) {
    return null;
  }

  @Override
  public boolean containsVariablesDefinition(Object element) {
    return false;
  }

  @Override
  public boolean containsAllVariablesDefinition(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean isEmptyVariablesDefinition() {
    return false;
  }

  @Override
  public Iterator<Optional<ASTVariable>> iteratorVariablesDefinition() {
    return null;
  }

  @Override
  public int sizeVariablesDefinition() {
    return 0;
  }

  @Override
  public Optional<ASTVariable>[] toArrayVariablesDefinition(Optional<ASTVariable>[] array) {
    return new Optional[0];
  }

  @Override
  public Object[] toArrayVariablesDefinition() {
    return new Object[0];
  }

  @Override
  public Spliterator<Optional<ASTVariable>> spliteratorVariablesDefinition() {
    return null;
  }

  @Override
  public Stream<Optional<ASTVariable>> streamVariablesDefinition() {
    return null;
  }

  @Override
  public Stream<Optional<ASTVariable>> parallelStreamVariablesDefinition() {
    return null;
  }

  @Override
  public Optional<ASTVariable> getVariablesDefinition(int index) {
    return Optional.empty();
  }

  @Override
  public int indexOfVariablesDefinition(Object element) {
    return 0;
  }

  @Override
  public int lastIndexOfVariablesDefinition(Object element) {
    return 0;
  }

  @Override
  public boolean equalsVariablesDefinition(Object o) {
    return false;
  }

  @Override
  public int hashCodeVariablesDefinition() {
    return 0;
  }

  @Override
  public ListIterator<Optional<ASTVariable>> listIteratorVariablesDefinition() {
    return null;
  }

  @Override
  public ListIterator<Optional<ASTVariable>> listIteratorVariablesDefinition(int index) {
    return null;
  }

  @Override
  public List<Optional<ASTVariable>> subListVariablesDefinition(int start, int end) {
    return null;
  }

  @Override
  public List<Optional<ASTVariable>> getVariablesDefinitionList() {
    return null;
  }

  @Override
  public boolean deepEquals(Object o) {
    return false;
  }

  @Override
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    return false;
  }

  @Override
  public boolean deepEqualsWithComments(Object o) {
    return false;
  }

  @Override
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    return false;
  }

  @Override
  public boolean equalAttributes(Object o) {
    return false;
  }

  @Override
  public boolean equalsWithComments(Object o) {
    return false;
  }

  @Override
  public ASTStateSpace deepClone() {
    return null;
  }

  @Override
  public SourcePosition get_SourcePositionEnd() {
    return null;
  }

  @Override
  public void set_SourcePositionEnd(SourcePosition end) {

  }

  @Override
  public void set_SourcePositionEndAbsent() {

  }

  @Override
  public boolean isPresent_SourcePositionEnd() {
    return false;
  }

  @Override
  public SourcePosition get_SourcePositionStart() {
    return null;
  }

  @Override
  public void set_SourcePositionStart(SourcePosition start) {

  }

  @Override
  public void set_SourcePositionStartAbsent() {

  }

  @Override
  public boolean isPresent_SourcePositionStart() {
    return false;
  }

  @Override
  public void clear_PreComments() {

  }

  @Override
  public boolean add_PreComment(Comment precomment) {
    return false;
  }

  @Override
  public boolean addAll_PreComments(Collection<Comment> precomments) {
    return false;
  }

  @Override
  public boolean contains_PreComment(Object element) {
    return false;
  }

  @Override
  public boolean containsAll_PreComments(Collection<?> element) {
    return false;
  }

  @Override
  public boolean isEmpty_PreComments() {
    return false;
  }

  @Override
  public Iterator<Comment> iterator_PreComments() {
    return null;
  }

  @Override
  public boolean remove_PreComment(Object element) {
    return false;
  }

  @Override
  public boolean removeAll_PreComments(Collection<?> element) {
    return false;
  }

  @Override
  public boolean retainAll_PreComments(Collection<?> element) {
    return false;
  }

  @Override
  public int size_PreComments() {
    return 0;
  }

  @Override
  public Comment[] toArray_PreComments(Comment[] array) {
    return new Comment[0];
  }

  @Override
  public boolean removeIf_PreComment(Predicate<? super Comment> filter) {
    return false;
  }

  @Override
  public Spliterator<Comment> spliterator_PreComments() {
    return null;
  }

  @Override
  public Stream<Comment> stream_PreComments() {
    return null;
  }

  @Override
  public Stream<Comment> parallelStream_PreComments() {
    return null;
  }

  @Override
  public void forEach_PreComments(Consumer<? super Comment> action) {

  }

  @Override
  public void add_PreComment(int index, Comment precomment) {

  }

  @Override
  public boolean addAll_PreComments(int index, Collection<Comment> precomments) {
    return false;
  }

  @Override
  public Comment get_PreComment(int index) {
    return null;
  }

  @Override
  public int indexOf_PreComment(Object element) {
    return 0;
  }

  @Override
  public int lastIndexOf_PreComment(Object element) {
    return 0;
  }

  @Override
  public boolean equals_PreComments(Object element) {
    return false;
  }

  @Override
  public int hashCode_PreComments() {
    return 0;
  }

  @Override
  public ListIterator<Comment> listIterator_PreComments() {
    return null;
  }

  @Override
  public Comment remove_PreComment(int index) {
    return null;
  }

  @Override
  public List<Comment> subList_PreComments(int start, int end) {
    return null;
  }

  @Override
  public void replaceAll_PreComments(UnaryOperator<Comment> operator) {

  }

  @Override
  public void sort_PreComments(Comparator<? super Comment> comparator) {

  }

  @Override
  public void set_PreCommentList(List<Comment> preComments) {

  }

  @Override
  public List<Comment> get_PreCommentList() {
    return null;
  }

  @Override
  public ListIterator<Comment> listIterator_PreComments(int index) {
    return null;
  }

  @Override
  public Comment set_PreComment(int index, Comment precomment) {
    return null;
  }

  @Override
  public Object[] toArray_PreComments() {
    return new Object[0];
  }

  @Override
  public void clear_PostComments() {

  }

  @Override
  public boolean add_PostComment(Comment postcomment) {
    return false;
  }

  @Override
  public boolean addAll_PostComments(Collection<Comment> postcomments) {
    return false;
  }

  @Override
  public boolean contains_PostComment(Object element) {
    return false;
  }

  @Override
  public boolean containsAll_PostComments(Collection<?> element) {
    return false;
  }

  @Override
  public boolean isEmpty_PostComments() {
    return false;
  }

  @Override
  public Iterator<Comment> iterator_PostComments() {
    return null;
  }

  @Override
  public boolean remove_PostComment(Object element) {
    return false;
  }

  @Override
  public boolean removeAll_PostComments(Collection<?> element) {
    return false;
  }

  @Override
  public boolean retainAll_PostComments(Collection<?> element) {
    return false;
  }

  @Override
  public int size_PostComments() {
    return 0;
  }

  @Override
  public Comment[] toArray_PostComments(Comment[] array) {
    return new Comment[0];
  }

  @Override
  public boolean removeIf_PostComment(Predicate<? super Comment> filter) {
    return false;
  }

  @Override
  public Spliterator<Comment> spliterator_PostComments() {
    return null;
  }

  @Override
  public Stream<Comment> stream_PostComments() {
    return null;
  }

  @Override
  public Stream<Comment> parallelStream_PostComments() {
    return null;
  }

  @Override
  public void forEach_PostComments(Consumer<? super Comment> action) {

  }

  @Override
  public void add_PostComment(int index, Comment postcomment) {

  }

  @Override
  public boolean addAll_PostComments(int index, Collection<Comment> postcomments) {
    return false;
  }

  @Override
  public Comment get_PostComment(int index) {
    return null;
  }

  @Override
  public int indexOf_PostComment(Object element) {
    return 0;
  }

  @Override
  public int lastIndexOf_PostComment(Object element) {
    return 0;
  }

  @Override
  public boolean equals_PostComments(Object element) {
    return false;
  }

  @Override
  public int hashCode_PostComments() {
    return 0;
  }

  @Override
  public ListIterator<Comment> listIterator_PostComments() {
    return null;
  }

  @Override
  public Comment remove_PostComment(int index) {
    return null;
  }

  @Override
  public List<Comment> subList_PostComments(int start, int end) {
    return null;
  }

  @Override
  public void replaceAll_PostComments(UnaryOperator<Comment> operator) {

  }

  @Override
  public void sort_PostComments(Comparator<? super Comment> comparator) {

  }

  @Override
  public void set_PostCommentList(List<Comment> postComments) {

  }

  @Override
  public List<Comment> get_PostCommentList() {
    return null;
  }

  @Override
  public ListIterator<Comment> listIterator_PostComments(int index) {
    return null;
  }

  @Override
  public Comment set_PostComment(int index, Comment postcomment) {
    return null;
  }

  @Override
  public Object[] toArray_PostComments() {
    return new Object[0];
  }

  @Override
  public boolean containsStates(Object element) {
    return false;
  }

  @Override
  public boolean containsAllStates(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean isEmptyStates() {
    return false;
  }

  @Override
  public Iterator<ASTMCQualifiedName> iteratorStates() {
    return null;
  }

  @Override
  public int sizeStates() {
    return 0;
  }

  @Override
  public ASTMCQualifiedName[] toArrayStates(ASTMCQualifiedName[] array) {
    return new ASTMCQualifiedName[0];
  }

  @Override
  public Object[] toArrayStates() {
    return new Object[0];
  }

  @Override
  public Spliterator<ASTMCQualifiedName> spliteratorStates() {
    return null;
  }

  @Override
  public Stream<ASTMCQualifiedName> streamStates() {
    return null;
  }

  @Override
  public Stream<ASTMCQualifiedName> parallelStreamStates() {
    return null;
  }

  @Override
  public ASTMCQualifiedName getStates(int index) {
    return null;
  }

  @Override
  public int indexOfStates(Object element) {
    return 0;
  }

  @Override
  public int lastIndexOfStates(Object element) {
    return 0;
  }

  @Override
  public boolean equalsStates(Object o) {
    return false;
  }

  @Override
  public int hashCodeStates() {
    return 0;
  }

  @Override
  public ListIterator<ASTMCQualifiedName> listIteratorStates() {
    return null;
  }

  @Override
  public ListIterator<ASTMCQualifiedName> listIteratorStates(int index) {
    return null;
  }

  @Override
  public List<ASTMCQualifiedName> subListStates(int start, int end) {
    return null;
  }

  @Override
  public void clearStates() {

  }

  @Override
  public boolean addStates(ASTMCQualifiedName element) {
    return false;
  }

  @Override
  public boolean addAllStates(Collection<? extends ASTMCQualifiedName> collection) {
    return false;
  }

  @Override
  public boolean removeStates(Object element) {
    return false;
  }

  @Override
  public boolean removeAllStates(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean retainAllStates(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean removeIfStates(Predicate<? super ASTMCQualifiedName> filter) {
    return false;
  }

  @Override
  public void forEachStates(Consumer<? super ASTMCQualifiedName> action) {

  }

  @Override
  public void addStates(int index, ASTMCQualifiedName element) {

  }

  @Override
  public boolean addAllStates(int index, Collection<? extends ASTMCQualifiedName> collection) {
    return false;
  }

  @Override
  public ASTMCQualifiedName removeStates(int index) {
    return null;
  }

  @Override
  public ASTMCQualifiedName setStates(int index, ASTMCQualifiedName element) {
    return null;
  }

  @Override
  public void replaceAllStates(UnaryOperator<ASTMCQualifiedName> operator) {

  }

  @Override
  public void sortStates(Comparator<? super ASTMCQualifiedName> comparator) {

  }

  @Override
  public void setStatesList(List<ASTMCQualifiedName> states) {

  }

  @Override
  public boolean containsVariables(Object element) {
    return false;
  }

  @Override
  public boolean containsAllVariables(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean isEmptyVariables() {
    return false;
  }

  @Override
  public Iterator<String> iteratorVariables() {
    return null;
  }

  @Override
  public int sizeVariables() {
    return 0;
  }

  @Override
  public String[] toArrayVariables(String[] array) {
    return new String[0];
  }

  @Override
  public Object[] toArrayVariables() {
    return new Object[0];
  }

  @Override
  public Spliterator<String> spliteratorVariables() {
    return null;
  }

  @Override
  public Stream<String> streamVariables() {
    return null;
  }

  @Override
  public Stream<String> parallelStreamVariables() {
    return null;
  }

  @Override
  public String getVariables(int index) {
    return null;
  }

  @Override
  public int indexOfVariables(Object element) {
    return 0;
  }

  @Override
  public int lastIndexOfVariables(Object element) {
    return 0;
  }

  @Override
  public boolean equalsVariables(Object o) {
    return false;
  }

  @Override
  public int hashCodeVariables() {
    return 0;
  }

  @Override
  public ListIterator<String> listIteratorVariables() {
    return null;
  }

  @Override
  public ListIterator<String> listIteratorVariables(int index) {
    return null;
  }

  @Override
  public List<String> subListVariables(int start, int end) {
    return null;
  }

  @Override
  public void clearVariables() {

  }

  @Override
  public boolean addVariables(String element) {
    return false;
  }

  @Override
  public boolean addAllVariables(Collection<? extends String> collection) {
    return false;
  }

  @Override
  public boolean removeVariables(Object element) {
    return false;
  }

  @Override
  public boolean removeAllVariables(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean retainAllVariables(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean removeIfVariables(Predicate<? super String> filter) {
    return false;
  }

  @Override
  public void forEachVariables(Consumer<? super String> action) {

  }

  @Override
  public void addVariables(int index, String element) {

  }

  @Override
  public boolean addAllVariables(int index, Collection<? extends String> collection) {
    return false;
  }

  @Override
  public String removeVariables(int index) {
    return null;
  }

  @Override
  public String setVariables(int index, String element) {
    return null;
  }

  @Override
  public void replaceAllVariables(UnaryOperator<String> operator) {

  }

  @Override
  public void sortVariables(Comparator<? super String> comparator) {

  }

  @Override
  public void setVariablesList(List<String> variables) {

  }

  @Override
  public IComponentConnectorScope getEnclosingScope() {
    return null;
  }

  @Override
  public void setEnclosingScope(IComponentConnectorScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(IMCBasicTypesScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(IExpressionsBasisScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(ICompSymbolsScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(ICardinalityScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(IMCBasicsScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(IMCLiteralsBasisScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(IBasicSymbolsScope enclosingScope) {

  }

  @Override
  public void setEnclosingScope(IMCCommonLiteralsScope enclosingScope) {

  }
}
