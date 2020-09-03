package de.monticore.lang.sysml.printast;

import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTUnit;
import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Test;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class PrintAstOfTrainingExamplesTest {

  public Optional<ASTUnit> parseSysML(String path) {
    SysMLParserForTesting parser = new SysMLParserForTesting();
    return parser.parseSysML(path);
  }

  private final String pathToDir =
      "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src" + "/training/";

  public String printAST(Object o, Integer intendationSteps) {
    intendationSteps++;

    StringBuilder intendationHelper = new StringBuilder();
    for(int i =0; i< intendationSteps;i++){
      intendationHelper.append(" ");
    }
    String intendation = intendationHelper.toString();
    StringBuilder printedAST = new StringBuilder();
    printedAST.append(intendation).append(o.getClass().getSimpleName());
    printedAST.append(intendation).append("\n").append(intendation).append("{\n");


    if(o instanceof Optional){
      if(!((Optional) o).isPresent()){
        printedAST.append("\n").append(intendation).append(" is Empty } \n");
        return printedAST.toString();
      }
    }
    if(o instanceof ArrayList){
      for (Object inArrList : (ArrayList) o) {
        printedAST.append(printAST(inArrList, intendationSteps));
      }
      printedAST.append("\n").append(intendation).append(" } \n");
      return printedAST.toString();
    }
    Set<Method> getters = ReflectionUtils.getAllMethods(o.getClass(), ReflectionUtils.withModifier(Modifier.PUBLIC),
        ReflectionUtils.withPrefix("get"));
    List<Method> allGetters = new ArrayList<Method>();
    allGetters.addAll(getters);

    // System.out.println(intendation+"Class " + o.getClass().getName() + " has these getters: " + printAllMethods
    // (allGetters));
    for (Method m : allGetters) {
      if (checkIfMethodGivesConcreteSyntaxValues(m.getName())) {
        try {
          // System.out.println(" Invoking method: " + m.getName() + " of class " + o.getClass().getName());
          Object gotObj = m.invoke(o);
          if (gotObj instanceof String) {
            String castedGotObj = (String) gotObj;
            //printedAST.append(intendation+ " " +m.getName() + " returns " + castedGotObj);
            //printedAST.append(intendation+ " " + castedGotObj);
            printedAST.append(intendation).append(" ").append("\"").append(castedGotObj).append("\"\n");
          }else if (isWrapperType(gotObj.getClass())){
            String castedGotObj = gotObj.toString();
            printedAST.append(intendation).append(" ").append("\"").append(castedGotObj).append("\"\n");
          }else if(gotObj instanceof List){
            List listGotObject = (List) gotObj;
            List flattenedList = flattenArrayList(listGotObject);
            for (Object inList: flattenedList) {
              //Print substring:
              String subString = printAST(gotObj,intendationSteps);
              printedAST.append(subString);
            }
          }else{
            //Print substring:
            String subString = printAST(gotObj,intendationSteps);
            printedAST.append(subString);
          }
        }
        catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        catch (InvocationTargetException e) {
          // e.printStackTrace();
        }
        catch (IllegalArgumentException e){
          // Ignore
          // e.printStackTrace();
        }
        catch (IllegalStateException e){
          // Ignore (Some string is not present)
          //  e.printStackTrace();
        }
      }
    }
    printedAST.append("\n").append(intendation).append(" } \n");
    return printedAST.toString();
  }

  List flattenArrayList(List list){
    List res = new ArrayList();
    for (Object inList: list     ) {
      if(inList instanceof List){
        res.add(flattenArrayList((List)inList));
      }else {
        res.add(inList);
      }
    }
    return res;
  }

  String printAllMethods(List<Method> methods){
    StringBuilder res = new StringBuilder();
    for (Method m : methods) {
      res.append(m.getName());
      res.append(" ||| ");
    }
    return res.toString();
  }


  public boolean checkIfMethodGivesConcreteSyntaxValues(String methodName) {
    String[] shouldNotEqual = { "getEnclosingScope", "get_PreCommentList", "get_SourcePositionStart",
        "get_SourcePositionEnd", "get_PostComment", "get_PreComment", "get_PostCommentList", "get_Children", "getDeclaringClass"
    , "getEnclosingClass"};
    for (String notWanted : shouldNotEqual) {
      if (methodName.equals(notWanted)) {
        return false;
      }
    }
    return true;
  }

  @Test //TODO delete if not used.
  public void parse_01_Packages_Comment_ExampleTest() {
    Optional<ASTUnit> ast = this.parseSysML(pathToDir + "/01. Packages/Comment Example.sysml");
    if(!ast.isPresent()){
      System.out.println("AST is not present!!");
      return;
    }
     /*String printedAST = printAST(ast.get(), 0);
    System.out.println("----------------------------------------------------------------------");
    System.out.println("----------------------------------------------------------------------");
    System.out.println("----------------------------------------------------------------------");
    printedAST = printedAST.replace("\n\n", "\n");
    System.out.println("AST of "+ pathToDir + "/01. Packages/Comment Example.sysml"
        +" (Values can be seen with quotations: \" <value> \")" +" \n"
        +" This printer is not perfect yet, because some classes cannot be displayed (like ImportUnit, see the "
        + "ignored exceptions ) and the"
        + "order of the AST-children is sometimes backwards. But it gives an Impression of the AST."
        + "\n If you see somthing in the output it is included in the AST \n"
        + printedAST);*/
  }

  private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

  public static boolean isWrapperType(Class<?> clazz)
  {
    return WRAPPER_TYPES.contains(clazz);
  }

  private static Set<Class<?>> getWrapperTypes()
  {
    Set<Class<?>> ret = new HashSet<>();
    ret.add(Boolean.class);
    ret.add(Character.class);
    ret.add(Byte.class);
    ret.add(Short.class);
    ret.add(Integer.class);
    ret.add(Long.class);
    ret.add(Float.class);
    ret.add(Double.class);
    ret.add(Void.class);
    return ret;
  }

}
