package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.ObjectVector;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.SAXSourceLocator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.axes.LocPathIterator;
import com.sun.org.apache.xpath.internal.axes.UnionPathIterator;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.org.apache.xpath.internal.operations.Div;
import com.sun.org.apache.xpath.internal.operations.Equals;
import com.sun.org.apache.xpath.internal.operations.Gt;
import com.sun.org.apache.xpath.internal.operations.Gte;
import com.sun.org.apache.xpath.internal.operations.Lt;
import com.sun.org.apache.xpath.internal.operations.Lte;
import com.sun.org.apache.xpath.internal.operations.Minus;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.sun.org.apache.xpath.internal.operations.Neg;
import com.sun.org.apache.xpath.internal.operations.NotEquals;
import com.sun.org.apache.xpath.internal.operations.Number;
import com.sun.org.apache.xpath.internal.operations.Operation;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.org.apache.xpath.internal.operations.Plus;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.patterns.FunctionPattern;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.patterns.UnionPattern;
import java.io.PrintStream;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public class Compiler
  extends OpMap
{
  private int locPathDepth = -1;
  private static final boolean DEBUG = false;
  private static long s_nextMethodId = 0L;
  private PrefixResolver m_currentPrefixResolver = null;
  ErrorListener m_errorHandler;
  SourceLocator m_locator;
  private FunctionTable m_functionTable;
  
  public Compiler(ErrorListener paramErrorListener, SourceLocator paramSourceLocator, FunctionTable paramFunctionTable)
  {
    m_errorHandler = paramErrorListener;
    m_locator = paramSourceLocator;
    m_functionTable = paramFunctionTable;
  }
  
  public Compiler()
  {
    m_errorHandler = null;
    m_locator = null;
  }
  
  public Expression compile(int paramInt)
    throws TransformerException
  {
    int i = getOp(paramInt);
    Expression localExpression = null;
    switch (i)
    {
    case 1: 
      localExpression = compile(paramInt + 2);
      break;
    case 2: 
      localExpression = or(paramInt);
      break;
    case 3: 
      localExpression = and(paramInt);
      break;
    case 4: 
      localExpression = notequals(paramInt);
      break;
    case 5: 
      localExpression = equals(paramInt);
      break;
    case 6: 
      localExpression = lte(paramInt);
      break;
    case 7: 
      localExpression = lt(paramInt);
      break;
    case 8: 
      localExpression = gte(paramInt);
      break;
    case 9: 
      localExpression = gt(paramInt);
      break;
    case 10: 
      localExpression = plus(paramInt);
      break;
    case 11: 
      localExpression = minus(paramInt);
      break;
    case 12: 
      localExpression = mult(paramInt);
      break;
    case 13: 
      localExpression = div(paramInt);
      break;
    case 14: 
      localExpression = mod(paramInt);
      break;
    case 16: 
      localExpression = neg(paramInt);
      break;
    case 17: 
      localExpression = string(paramInt);
      break;
    case 18: 
      localExpression = bool(paramInt);
      break;
    case 19: 
      localExpression = number(paramInt);
      break;
    case 20: 
      localExpression = union(paramInt);
      break;
    case 21: 
      localExpression = literal(paramInt);
      break;
    case 22: 
      localExpression = variable(paramInt);
      break;
    case 23: 
      localExpression = group(paramInt);
      break;
    case 27: 
      localExpression = numberlit(paramInt);
      break;
    case 26: 
      localExpression = arg(paramInt);
      break;
    case 24: 
      localExpression = compileExtension(paramInt);
      break;
    case 25: 
      localExpression = compileFunction(paramInt);
      break;
    case 28: 
      localExpression = locationPath(paramInt);
      break;
    case 29: 
      localExpression = null;
      break;
    case 30: 
      localExpression = matchPattern(paramInt + 2);
      break;
    case 31: 
      localExpression = locationPathPattern(paramInt);
      break;
    case 15: 
      error("ER_UNKNOWN_OPCODE", new Object[] { "quo" });
      break;
    default: 
      error("ER_UNKNOWN_OPCODE", new Object[] { Integer.toString(getOp(paramInt)) });
    }
    return localExpression;
  }
  
  private Expression compileOperation(Operation paramOperation, int paramInt)
    throws TransformerException
  {
    int i = getFirstChildPos(paramInt);
    int j = getNextOpPos(i);
    paramOperation.setLeftRight(compile(i), compile(j));
    return paramOperation;
  }
  
  private Expression compileUnary(UnaryOperation paramUnaryOperation, int paramInt)
    throws TransformerException
  {
    int i = getFirstChildPos(paramInt);
    paramUnaryOperation.setRight(compile(i));
    return paramUnaryOperation;
  }
  
  protected Expression or(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Or(), paramInt);
  }
  
  protected Expression and(int paramInt)
    throws TransformerException
  {
    return compileOperation(new And(), paramInt);
  }
  
  protected Expression notequals(int paramInt)
    throws TransformerException
  {
    return compileOperation(new NotEquals(), paramInt);
  }
  
  protected Expression equals(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Equals(), paramInt);
  }
  
  protected Expression lte(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Lte(), paramInt);
  }
  
  protected Expression lt(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Lt(), paramInt);
  }
  
  protected Expression gte(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Gte(), paramInt);
  }
  
  protected Expression gt(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Gt(), paramInt);
  }
  
  protected Expression plus(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Plus(), paramInt);
  }
  
  protected Expression minus(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Minus(), paramInt);
  }
  
  protected Expression mult(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Mult(), paramInt);
  }
  
  protected Expression div(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Div(), paramInt);
  }
  
  protected Expression mod(int paramInt)
    throws TransformerException
  {
    return compileOperation(new Mod(), paramInt);
  }
  
  protected Expression neg(int paramInt)
    throws TransformerException
  {
    return compileUnary(new Neg(), paramInt);
  }
  
  protected Expression string(int paramInt)
    throws TransformerException
  {
    return compileUnary(new com.sun.org.apache.xpath.internal.operations.String(), paramInt);
  }
  
  protected Expression bool(int paramInt)
    throws TransformerException
  {
    return compileUnary(new Bool(), paramInt);
  }
  
  protected Expression number(int paramInt)
    throws TransformerException
  {
    return compileUnary(new Number(), paramInt);
  }
  
  protected Expression literal(int paramInt)
  {
    paramInt = getFirstChildPos(paramInt);
    return (XString)getTokenQueue().elementAt(getOp(paramInt));
  }
  
  protected Expression numberlit(int paramInt)
  {
    paramInt = getFirstChildPos(paramInt);
    return (XNumber)getTokenQueue().elementAt(getOp(paramInt));
  }
  
  protected Expression variable(int paramInt)
    throws TransformerException
  {
    Variable localVariable = new Variable();
    paramInt = getFirstChildPos(paramInt);
    int i = getOp(paramInt);
    String str1 = -2 == i ? null : (String)getTokenQueue().elementAt(i);
    String str2 = (String)getTokenQueue().elementAt(getOp(paramInt + 1));
    QName localQName = new QName(str1, str2);
    localVariable.setQName(localQName);
    return localVariable;
  }
  
  protected Expression group(int paramInt)
    throws TransformerException
  {
    return compile(paramInt + 2);
  }
  
  protected Expression arg(int paramInt)
    throws TransformerException
  {
    return compile(paramInt + 2);
  }
  
  protected Expression union(int paramInt)
    throws TransformerException
  {
    locPathDepth += 1;
    try
    {
      LocPathIterator localLocPathIterator = UnionPathIterator.createUnionIterator(this, paramInt);
      return localLocPathIterator;
    }
    finally
    {
      locPathDepth -= 1;
    }
  }
  
  public int getLocationPathDepth()
  {
    return locPathDepth;
  }
  
  FunctionTable getFunctionTable()
  {
    return m_functionTable;
  }
  
  public Expression locationPath(int paramInt)
    throws TransformerException
  {
    locPathDepth += 1;
    try
    {
      DTMIterator localDTMIterator = WalkerFactory.newDTMIterator(this, paramInt, locPathDepth == 0);
      Expression localExpression = (Expression)localDTMIterator;
      return localExpression;
    }
    finally
    {
      locPathDepth -= 1;
    }
  }
  
  public Expression predicate(int paramInt)
    throws TransformerException
  {
    return compile(paramInt + 2);
  }
  
  protected Expression matchPattern(int paramInt)
    throws TransformerException
  {
    locPathDepth += 1;
    try
    {
      int i = paramInt;
      for (int j = 0; getOp(i) == 31; j++) {
        i = getNextOpPos(i);
      }
      if (j == 1)
      {
        localObject1 = compile(paramInt);
        return (Expression)localObject1;
      }
      Object localObject1 = new UnionPattern();
      StepPattern[] arrayOfStepPattern = new StepPattern[j];
      for (j = 0; getOp(paramInt) == 31; j++)
      {
        i = getNextOpPos(paramInt);
        arrayOfStepPattern[j] = ((StepPattern)compile(paramInt));
        paramInt = i;
      }
      ((UnionPattern)localObject1).setPatterns(arrayOfStepPattern);
      Object localObject2 = localObject1;
      return (Expression)localObject2;
    }
    finally
    {
      locPathDepth -= 1;
    }
  }
  
  public Expression locationPathPattern(int paramInt)
    throws TransformerException
  {
    paramInt = getFirstChildPos(paramInt);
    return stepPattern(paramInt, 0, null);
  }
  
  public int getWhatToShow(int paramInt)
  {
    int i = getOp(paramInt);
    int j = getOp(paramInt + 3);
    switch (j)
    {
    case 1030: 
      return 128;
    case 1031: 
      return 12;
    case 1032: 
      return 64;
    case 1033: 
      switch (i)
      {
      case 49: 
        return 4096;
      case 39: 
      case 51: 
        return 2;
      case 38: 
      case 42: 
      case 48: 
        return -1;
      }
      if (getOp(0) == 30) {
        return 64253;
      }
      return -3;
    case 35: 
      return 1280;
    case 1034: 
      return 65536;
    case 34: 
      switch (i)
      {
      case 49: 
        return 4096;
      case 39: 
      case 51: 
        return 2;
      case 52: 
      case 53: 
        return 1;
      }
      return 1;
    }
    return -1;
  }
  
  protected StepPattern stepPattern(int paramInt1, int paramInt2, StepPattern paramStepPattern)
    throws TransformerException
  {
    int i = paramInt1;
    int j = getOp(paramInt1);
    if (-1 == j) {
      return null;
    }
    int k = 1;
    int m = getNextOpPos(paramInt1);
    int n;
    Object localObject;
    switch (j)
    {
    case 25: 
      k = 0;
      n = getOp(paramInt1 + 1);
      localObject = new FunctionPattern(compileFunction(paramInt1), 10, 3);
      break;
    case 50: 
      k = 0;
      n = getArgLengthOfStep(paramInt1);
      paramInt1 = getFirstChildPosOfStep(paramInt1);
      localObject = new StepPattern(1280, 10, 3);
      break;
    case 51: 
      n = getArgLengthOfStep(paramInt1);
      paramInt1 = getFirstChildPosOfStep(paramInt1);
      localObject = new StepPattern(2, getStepNS(i), getStepLocalName(i), 10, 2);
      break;
    case 52: 
      n = getArgLengthOfStep(paramInt1);
      paramInt1 = getFirstChildPosOfStep(paramInt1);
      int i1 = getWhatToShow(i);
      if (1280 == i1) {
        k = 0;
      }
      localObject = new StepPattern(getWhatToShow(i), getStepNS(i), getStepLocalName(i), 0, 3);
      break;
    case 53: 
      n = getArgLengthOfStep(paramInt1);
      paramInt1 = getFirstChildPosOfStep(paramInt1);
      localObject = new StepPattern(getWhatToShow(i), getStepNS(i), getStepLocalName(i), 10, 3);
      break;
    default: 
      error("ER_UNKNOWN_MATCH_OPERATION", null);
      return null;
    }
    ((StepPattern)localObject).setPredicates(getCompiledPredicates(paramInt1 + n));
    if (null != paramStepPattern) {
      ((StepPattern)localObject).setRelativePathPattern(paramStepPattern);
    }
    StepPattern localStepPattern = stepPattern(m, paramInt2 + 1, (StepPattern)localObject);
    return null != localStepPattern ? localStepPattern : localObject;
  }
  
  public Expression[] getCompiledPredicates(int paramInt)
    throws TransformerException
  {
    int i = countPredicates(paramInt);
    if (i > 0)
    {
      Expression[] arrayOfExpression = new Expression[i];
      compilePredicates(paramInt, arrayOfExpression);
      return arrayOfExpression;
    }
    return null;
  }
  
  public int countPredicates(int paramInt)
    throws TransformerException
  {
    int i = 0;
    while (29 == getOp(paramInt))
    {
      i++;
      paramInt = getNextOpPos(paramInt);
    }
    return i;
  }
  
  private void compilePredicates(int paramInt, Expression[] paramArrayOfExpression)
    throws TransformerException
  {
    for (int i = 0; 29 == getOp(paramInt); i++)
    {
      paramArrayOfExpression[i] = predicate(paramInt);
      paramInt = getNextOpPos(paramInt);
    }
  }
  
  Expression compileFunction(int paramInt)
    throws TransformerException
  {
    int i = paramInt + getOp(paramInt + 1) - 1;
    paramInt = getFirstChildPos(paramInt);
    int j = getOp(paramInt);
    paramInt++;
    if (-1 != j)
    {
      Function localFunction = m_functionTable.getFunction(j);
      if ((localFunction instanceof FuncExtFunctionAvailable)) {
        ((FuncExtFunctionAvailable)localFunction).setFunctionTable(m_functionTable);
      }
      localFunction.postCompileStep(this);
      try
      {
        int k = 0;
        int m = paramInt;
        while (m < i)
        {
          localFunction.setArg(compile(m), k);
          m = getNextOpPos(m);
          k++;
        }
        localFunction.checkNumberArgs(k);
      }
      catch (WrongNumberArgsException localWrongNumberArgsException)
      {
        String str = m_functionTable.getFunctionName(j);
        m_errorHandler.fatalError(new TransformerException(XSLMessages.createXPATHMessage("ER_ONLY_ALLOWS", new Object[] { str, localWrongNumberArgsException.getMessage() }), m_locator));
      }
      return localFunction;
    }
    error("ER_FUNCTION_TOKEN_NOT_FOUND", null);
    return null;
  }
  
  private synchronized long getNextMethodId()
  {
    if (s_nextMethodId == Long.MAX_VALUE) {
      s_nextMethodId = 0L;
    }
    return s_nextMethodId++;
  }
  
  private Expression compileExtension(int paramInt)
    throws TransformerException
  {
    int i = paramInt + getOp(paramInt + 1) - 1;
    paramInt = getFirstChildPos(paramInt);
    String str1 = (String)getTokenQueue().elementAt(getOp(paramInt));
    paramInt++;
    String str2 = (String)getTokenQueue().elementAt(getOp(paramInt));
    paramInt++;
    FuncExtFunction localFuncExtFunction = new FuncExtFunction(str1, str2, String.valueOf(getNextMethodId()));
    try
    {
      for (int j = 0; paramInt < i; j++)
      {
        int k = getNextOpPos(paramInt);
        localFuncExtFunction.setArg(compile(paramInt), j);
        paramInt = k;
      }
    }
    catch (WrongNumberArgsException localWrongNumberArgsException) {}
    return localFuncExtFunction;
  }
  
  public void warn(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    if (null != m_errorHandler) {
      m_errorHandler.warning(new TransformerException(str, m_locator));
    } else {
      System.out.println(str + "; file " + m_locator.getSystemId() + "; line " + m_locator.getLineNumber() + "; column " + m_locator.getColumnNumber());
    }
  }
  
  public void assertion(boolean paramBoolean, String paramString)
  {
    if (!paramBoolean)
    {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    }
  }
  
  public void error(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    if (null != m_errorHandler) {
      m_errorHandler.fatalError(new TransformerException(str, m_locator));
    } else {
      throw new TransformerException(str, (SAXSourceLocator)m_locator);
    }
  }
  
  public PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }
  
  public void setNamespaceContext(PrefixResolver paramPrefixResolver)
  {
    m_currentPrefixResolver = paramPrefixResolver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\compiler\Compiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */