package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.ast.Comment;
import de.monticore.cardinality._symboltable.ICardinalityScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.lang.componentconnector._ast.ASTRequirement;
import de.monticore.lang.componentconnector._symboltable.IComponentConnectorScope;
import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.lang.componentconnector._visitor.ComponentConnectorTraverser;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.compsymbols._ast.ASTComponentType;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

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
import java.util.stream.Stream;

public class RequirementWrapper implements ASTRequirement {

  private String name;
  private String subject;
  private ASTExpression constraint;
  private ISysMLv2Scope enclosingScope;
  private Optional<RequirementSymbol> symbol;

  public RequirementWrapper(
      String name,
      String subject,
      ASTExpression constraint,
      ISysMLv2Scope enclosingScope)
  {
    this.name = name;
    this.subject = subject;
    this.constraint = constraint;
    this.enclosingScope = enclosingScope;
    this.symbol = Optional.empty();
  }

  //@Override
  public ComponentTypeSymbol getSubjectSymbol() {
    var subject =  enclosingScope.resolveComponentType(getSubject());
    if(subject.isEmpty()) {
      Log.error("0xF0C03 Internal error, subject could not be resolved");
    }
    return subject.get();
  }

  @Override
  public String getName() {
    return name;
  }

  //@Override
  public String getSubject() {
    return subject;
  }

  //@Override
  public void setSubject(String subject) {
    this.subject = subject;
  }

  //@Override
  public boolean isPresentSubjectSymbol() {
    return enclosingScope.resolveComponentType(getSubject()).isPresent();
  }

  //@Override
  public boolean isPresentSubjectDefinition() {
    return isPresentSubjectSymbol() && getSubjectSymbol().isPresentAstNode();
  }

  //@Override
  public ASTComponentType getSubjectDefinition() {
    if(!isPresentSubjectSymbol()) {
      Log.error("0xF0C04 Internal error, subject symbol was not present");
    }
    var symbol = getSubjectSymbol();
    if(!symbol.isPresentAstNode()) {
      Log.error("0xF0C05 Internal error, subject AST was not present");
    }
    return symbol.getAstNode();
  }

  //@Override
  public ASTExpression getConstraint() {
    return this.constraint;
  }

  //@Override
  public void setConstraint(ASTExpression constraint) {
    this.constraint = constraint;
  }

  @Override
  public RequirementSymbol getSymbol() {
    return this.symbol.get();
  }

  @Override
  public boolean isPresentSymbol() {
    return this.symbol.isPresent();
  }

  @Override
  public IComponentConnectorScope getEnclosingScope() {
    return enclosingScope;
  }

  @Override
  public void setEnclosingScope(IComponentConnectorScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(IMCBasicTypesScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(IExpressionsBasisScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(ICompSymbolsScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(ICardinalityScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(IMCBasicsScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(IMCLiteralsBasisScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(IBasicSymbolsScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  @Override
  public void setEnclosingScope(IMCCommonLiteralsScope enclosingScope) {
    if(!(enclosingScope instanceof ISysMLv2Scope)) {
      Log.error("OxF0C06 Expected a SysMLv2Scope");
    }
    this.enclosingScope = (ISysMLv2Scope) enclosingScope;
  }

  // ######### BOILERPLATE ##############

  @Override
  public void accept(ComponentConnectorTraverser visitor) {

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
  public ASTRequirement deepClone() {
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
  public boolean addAll_PreComments(int index,
                                    Collection<Comment> precomments) {
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
  public boolean addAll_PostComments(int index,
                                     Collection<Comment> postcomments) {
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

}
