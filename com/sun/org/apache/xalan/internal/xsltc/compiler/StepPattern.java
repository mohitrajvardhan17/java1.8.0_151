package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

class StepPattern
  extends RelativePathPattern
{
  private static final int NO_CONTEXT = 0;
  private static final int SIMPLE_CONTEXT = 1;
  private static final int GENERAL_CONTEXT = 2;
  protected final int _axis;
  protected final int _nodeType;
  protected Vector _predicates;
  private Step _step = null;
  private boolean _isEpsilon = false;
  private int _contextCase;
  private double _priority = Double.MAX_VALUE;
  
  public StepPattern(int paramInt1, int paramInt2, Vector paramVector)
  {
    _axis = paramInt1;
    _nodeType = paramInt2;
    _predicates = paramVector;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (_predicates != null)
    {
      int i = _predicates.size();
      for (int j = 0; j < i; j++)
      {
        Predicate localPredicate = (Predicate)_predicates.elementAt(j);
        localPredicate.setParser(paramParser);
        localPredicate.setParent(this);
      }
    }
  }
  
  public int getNodeType()
  {
    return _nodeType;
  }
  
  public void setPriority(double paramDouble)
  {
    _priority = paramDouble;
  }
  
  public StepPattern getKernelPattern()
  {
    return this;
  }
  
  public boolean isWildcard()
  {
    return (_isEpsilon) && (!hasPredicates());
  }
  
  public StepPattern setPredicates(Vector paramVector)
  {
    _predicates = paramVector;
    return this;
  }
  
  protected boolean hasPredicates()
  {
    return (_predicates != null) && (_predicates.size() > 0);
  }
  
  public double getDefaultPriority()
  {
    if (_priority != Double.MAX_VALUE) {
      return _priority;
    }
    if (hasPredicates()) {
      return 0.5D;
    }
    switch (_nodeType)
    {
    case -1: 
      return -0.5D;
    case 0: 
      return 0.0D;
    }
    return _nodeType >= 14 ? 0.0D : -0.5D;
  }
  
  public int getAxis()
  {
    return _axis;
  }
  
  public void reduceKernelPattern()
  {
    _isEpsilon = true;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("stepPattern(\"");
    localStringBuffer.append(Axis.getNames(_axis)).append("\", ").append(_isEpsilon ? "epsilon{" + Integer.toString(_nodeType) + "}" : Integer.toString(_nodeType));
    if (_predicates != null) {
      localStringBuffer.append(", ").append(_predicates.toString());
    }
    return ')';
  }
  
  private int analyzeCases()
  {
    int i = 1;
    int j = _predicates.size();
    for (int k = 0; (k < j) && (i != 0); k++)
    {
      Predicate localPredicate = (Predicate)_predicates.elementAt(k);
      if ((localPredicate.isNthPositionFilter()) || (localPredicate.hasPositionCall()) || (localPredicate.hasLastCall())) {
        i = 0;
      }
    }
    if (i != 0) {
      return 0;
    }
    if (j == 1) {
      return 1;
    }
    return 2;
  }
  
  private String getNextFieldName()
  {
    return "__step_pattern_iter_" + getXSLTC().nextStepPatternSerial();
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (hasPredicates())
    {
      int i = _predicates.size();
      Predicate localPredicate;
      for (int j = 0; j < i; j++)
      {
        localPredicate = (Predicate)_predicates.elementAt(j);
        localPredicate.typeCheck(paramSymbolTable);
      }
      _contextCase = analyzeCases();
      Step localStep = null;
      if (_contextCase == 1)
      {
        localPredicate = (Predicate)_predicates.elementAt(0);
        if (localPredicate.isNthPositionFilter())
        {
          _contextCase = 2;
          localStep = new Step(_axis, _nodeType, _predicates);
        }
        else
        {
          localStep = new Step(_axis, _nodeType, null);
        }
      }
      else if (_contextCase == 2)
      {
        int k = _predicates.size();
        for (int m = 0; m < k; m++) {
          ((Predicate)_predicates.elementAt(m)).dontOptimize();
        }
        localStep = new Step(_axis, _nodeType, _predicates);
      }
      if (localStep != null)
      {
        localStep.setParser(getParser());
        localStep.typeCheck(paramSymbolTable);
        _step = localStep;
      }
    }
    return _axis == 3 ? Type.Element : Type.Attribute;
  }
  
  private void translateKernel(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i;
    BranchHandle localBranchHandle;
    if (_nodeType == 1)
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "isElement", "(I)Z");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      localBranchHandle = localInstructionList.append(new IFNE(null));
      _falseList.add(localInstructionList.append(new GOTO_W(null)));
      localBranchHandle.setTarget(localInstructionList.append(NOP));
    }
    else if (_nodeType == 2)
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "isAttribute", "(I)Z");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      localBranchHandle = localInstructionList.append(new IFNE(null));
      _falseList.add(localInstructionList.append(new GOTO_W(null)));
      localBranchHandle.setTarget(localInstructionList.append(NOP));
    }
    else
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      localInstructionList.append(new PUSH(localConstantPoolGen, _nodeType));
      localBranchHandle = localInstructionList.append(new IF_ICMPEQ(null));
      _falseList.add(localInstructionList.append(new GOTO_W(null)));
      localBranchHandle.setTarget(localInstructionList.append(NOP));
    }
  }
  
  private void translateNoContext(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(SWAP);
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    if (!_isEpsilon)
    {
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      translateKernel(paramClassGenerator, paramMethodGenerator);
    }
    int i = _predicates.size();
    for (int j = 0; j < i; j++)
    {
      localObject = (Predicate)_predicates.elementAt(j);
      Expression localExpression = ((Predicate)localObject).getExpr();
      localExpression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      _trueList.append(_trueList);
      _falseList.append(_falseList);
    }
    InstructionHandle localInstructionHandle = localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchTrueList(localInstructionHandle);
    Object localObject = localInstructionList.append(new GOTO(null));
    localInstructionHandle = localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchFalseList(localInstructionHandle);
    _falseList.add(localInstructionList.append(new GOTO(null)));
    ((BranchHandle)localObject).setTarget(localInstructionList.append(NOP));
  }
  
  private void translateSimpleContext(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
    localLocalVariableGen1.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen1.getIndex())));
    if (!_isEpsilon)
    {
      localInstructionList.append(new ILOAD(localLocalVariableGen1.getIndex()));
      translateKernel(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator", "<init>", "(ILcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
    _step.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator")));
    localInstructionList.append(DUP);
    localInstructionList.append(new ILOAD(localLocalVariableGen1.getIndex()));
    localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new INVOKESPECIAL(i));
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new ILOAD(localLocalVariableGen1.getIndex()));
    i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
    localInstructionList.append(paramMethodGenerator.setStartNode());
    localInstructionList.append(paramMethodGenerator.storeIterator());
    localLocalVariableGen1.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen1.getIndex())));
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    Predicate localPredicate = (Predicate)_predicates.elementAt(0);
    Expression localExpression = localPredicate.getExpr();
    localExpression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    InstructionHandle localInstructionHandle = localInstructionList.append(paramMethodGenerator.storeIterator());
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    localExpression.backPatchTrueList(localInstructionHandle);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    localInstructionHandle = localInstructionList.append(paramMethodGenerator.storeIterator());
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    localExpression.backPatchFalseList(localInstructionHandle);
    _falseList.add(localInstructionList.append(new GOTO(null)));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
  
  private void translateGeneralContext(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = 0;
    BranchHandle localBranchHandle1 = null;
    String str = getNextFieldName();
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
    localLocalVariableGen2.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen2.getIndex())));
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    if (!paramClassGenerator.isExternal())
    {
      localObject = new Field(2, localConstantPoolGen.addUtf8(str), localConstantPoolGen.addUtf8("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, localConstantPoolGen.getConstantPool());
      paramClassGenerator.addField((Field)localObject);
      i = localConstantPoolGen.addFieldref(paramClassGenerator.getClassName(), str, "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new GETFIELD(i));
      localInstructionList.append(DUP);
      localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
      localBranchHandle1 = localInstructionList.append(new IFNONNULL(null));
      localInstructionList.append(paramClassGenerator.loadTranslet());
    }
    _step.translate(paramClassGenerator, paramMethodGenerator);
    Object localObject = localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex()));
    if (!paramClassGenerator.isExternal())
    {
      localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex()));
      localInstructionList.append(new PUTFIELD(i));
      localBranchHandle1.setTarget(localInstructionList.append(NOP));
    }
    else
    {
      localLocalVariableGen1.setStart((InstructionHandle)localObject);
    }
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new ILOAD(localLocalVariableGen2.getIndex()));
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    localInstructionList.append(new INVOKEINTERFACE(j, 2));
    localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex()));
    localInstructionList.append(SWAP);
    localInstructionList.append(paramMethodGenerator.setStartNode());
    LocalVariableGen localLocalVariableGen3 = paramMethodGenerator.addLocalVariable("step_pattern_tmp3", Util.getJCRefType("I"), null, null);
    BranchHandle localBranchHandle2 = localInstructionList.append(new GOTO(null));
    InstructionHandle localInstructionHandle2 = localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex()));
    localLocalVariableGen3.setStart(localInstructionHandle2);
    InstructionHandle localInstructionHandle1 = localInstructionList.append(paramMethodGenerator.nextNode());
    localInstructionList.append(DUP);
    localInstructionList.append(new ISTORE(localLocalVariableGen3.getIndex()));
    _falseList.add(localInstructionList.append(new IFLT(null)));
    localInstructionList.append(new ILOAD(localLocalVariableGen3.getIndex()));
    localInstructionList.append(new ILOAD(localLocalVariableGen2.getIndex()));
    localLocalVariableGen1.setEnd(localInstructionList.append(new IF_ICMPLT(localInstructionHandle2)));
    localLocalVariableGen3.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen3.getIndex())));
    localLocalVariableGen2.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen2.getIndex())));
    _falseList.add(localInstructionList.append(new IF_ICMPNE(null)));
    localBranchHandle2.setTarget(localInstructionHandle1);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (hasPredicates()) {
      switch (_contextCase)
      {
      case 0: 
        translateNoContext(paramClassGenerator, paramMethodGenerator);
        break;
      case 1: 
        translateSimpleContext(paramClassGenerator, paramMethodGenerator);
        break;
      default: 
        translateGeneralContext(paramClassGenerator, paramMethodGenerator);
        break;
      }
    } else if (isWildcard()) {
      localInstructionList.append(POP);
    } else {
      translateKernel(paramClassGenerator, paramMethodGenerator);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StepPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */