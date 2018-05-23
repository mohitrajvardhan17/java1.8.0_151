package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;

final class Predicate
  extends Expression
  implements Closure
{
  private Expression _exp = null;
  private boolean _canOptimize = true;
  private boolean _nthPositionFilter = false;
  private boolean _nthDescendant = false;
  int _ptype = -1;
  private String _className = null;
  private ArrayList _closureVars = null;
  private Closure _parentClosure = null;
  private Expression _value = null;
  private Step _step = null;
  
  public Predicate(Expression paramExpression)
  {
    _exp = paramExpression;
    _exp.setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _exp.setParser(paramParser);
  }
  
  public boolean isNthPositionFilter()
  {
    return _nthPositionFilter;
  }
  
  public boolean isNthDescendant()
  {
    return _nthDescendant;
  }
  
  public void dontOptimize()
  {
    _canOptimize = false;
  }
  
  public boolean hasPositionCall()
  {
    return _exp.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return _exp.hasLastCall();
  }
  
  public boolean inInnerClass()
  {
    return _className != null;
  }
  
  public Closure getParentClosure()
  {
    if (_parentClosure == null)
    {
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      do
      {
        if ((localSyntaxTreeNode instanceof Closure))
        {
          _parentClosure = ((Closure)localSyntaxTreeNode);
          break;
        }
        if ((localSyntaxTreeNode instanceof TopLevelElement)) {
          break;
        }
        localSyntaxTreeNode = localSyntaxTreeNode.getParent();
      } while (localSyntaxTreeNode != null);
    }
    return _parentClosure;
  }
  
  public String getInnerClassName()
  {
    return _className;
  }
  
  public void addVariable(VariableRefBase paramVariableRefBase)
  {
    if (_closureVars == null) {
      _closureVars = new ArrayList();
    }
    if (!_closureVars.contains(paramVariableRefBase))
    {
      _closureVars.add(paramVariableRefBase);
      Closure localClosure = getParentClosure();
      if (localClosure != null) {
        localClosure.addVariable(paramVariableRefBase);
      }
    }
  }
  
  public int getPosType()
  {
    if (_ptype == -1)
    {
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      if ((localSyntaxTreeNode instanceof StepPattern))
      {
        _ptype = ((StepPattern)localSyntaxTreeNode).getNodeType();
      }
      else
      {
        Object localObject1;
        Object localObject2;
        if ((localSyntaxTreeNode instanceof AbsoluteLocationPath))
        {
          localObject1 = (AbsoluteLocationPath)localSyntaxTreeNode;
          localObject2 = ((AbsoluteLocationPath)localObject1).getPath();
          if ((localObject2 instanceof Step)) {
            _ptype = ((Step)localObject2).getNodeType();
          }
        }
        else if ((localSyntaxTreeNode instanceof VariableRefBase))
        {
          localObject1 = (VariableRefBase)localSyntaxTreeNode;
          localObject2 = ((VariableRefBase)localObject1).getVariable();
          Expression localExpression = ((VariableBase)localObject2).getExpression();
          if ((localExpression instanceof Step)) {
            _ptype = ((Step)localExpression).getNodeType();
          }
        }
        else if ((localSyntaxTreeNode instanceof Step))
        {
          _ptype = ((Step)localSyntaxTreeNode).getNodeType();
        }
      }
    }
    return _ptype;
  }
  
  public boolean parentIsPattern()
  {
    return getParent() instanceof Pattern;
  }
  
  public Expression getExpr()
  {
    return _exp;
  }
  
  public String toString()
  {
    return "pred(" + _exp + ')';
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = _exp.typeCheck(paramSymbolTable);
    if ((localType instanceof ReferenceType)) {
      _exp = new CastExpr(_exp, localType = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Real);
    }
    if ((localType instanceof ResultTreeType))
    {
      _exp = new CastExpr(_exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean);
      _exp = new CastExpr(_exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Real);
      localType = _exp.typeCheck(paramSymbolTable);
    }
    if ((localType instanceof NumberType))
    {
      if (!(localType instanceof IntType)) {
        _exp = new CastExpr(_exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int);
      }
      if (_canOptimize)
      {
        _nthPositionFilter = ((!_exp.hasLastCall()) && (!_exp.hasPositionCall()));
        if (_nthPositionFilter)
        {
          localObject = getParent();
          _nthDescendant = (((localObject instanceof Step)) && ((((SyntaxTreeNode)localObject).getParent() instanceof AbsoluteLocationPath)));
          return _type = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.NodeSet;
        }
      }
      _nthPositionFilter = (_nthDescendant = 0);
      Object localObject = getParser().getQNameIgnoreDefaultNs("position");
      PositionCall localPositionCall = new PositionCall((QName)localObject);
      localPositionCall.setParser(getParser());
      localPositionCall.setParent(this);
      _exp = new EqualityExpr(0, localPositionCall, _exp);
      if (_exp.typeCheck(paramSymbolTable) != com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean) {
        _exp = new CastExpr(_exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean);
      }
      return _type = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean;
    }
    if (!(localType instanceof BooleanType)) {
      _exp = new CastExpr(_exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean);
    }
    return _type = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean;
  }
  
  private void compileFilter(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _className = getXSLTC().getHelperClassName();
    FilterGenerator localFilterGenerator = new FilterGenerator(_className, "java.lang.Object", toString(), 33, new String[] { "com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter" }, paramClassGenerator.getStylesheet());
    ConstantPoolGen localConstantPoolGen = localFilterGenerator.getConstantPool();
    int i = _closureVars == null ? 0 : _closureVars.size();
    for (int j = 0; j < i; j++)
    {
      localObject = ((VariableRefBase)_closureVars.get(j)).getVariable();
      localFilterGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(((VariableBase)localObject).getEscapedName()), localConstantPoolGen.addUtf8(((VariableBase)localObject).getType().toSignature()), null, localConstantPoolGen.getConstantPool()));
    }
    InstructionList localInstructionList = new InstructionList();
    TestGenerator localTestGenerator = new TestGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;") }, new String[] { "node", "position", "last", "current", "translet", "iterator" }, "test", _className, localInstructionList, localConstantPoolGen);
    LocalVariableGen localLocalVariableGen = localTestGenerator.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
    Object localObject = paramClassGenerator.getClassName();
    localInstructionList.append(localFilterGenerator.loadTranslet());
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass((String)localObject)));
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref((String)localObject, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
    localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
    localTestGenerator.setDomIndex(localLocalVariableGen.getIndex());
    _exp.translate(localFilterGenerator, localTestGenerator);
    localInstructionList.append(IRETURN);
    localFilterGenerator.addEmptyConstructor(1);
    localFilterGenerator.addMethod(localTestGenerator);
    getXSLTC().dumpClass(localFilterGenerator.getJavaClass());
  }
  
  public boolean isBooleanTest()
  {
    return _exp instanceof BooleanExpr;
  }
  
  public boolean isNodeValueTest()
  {
    if (!_canOptimize) {
      return false;
    }
    return (getStep() != null) && (getCompareValue() != null);
  }
  
  public Step getStep()
  {
    if (_step != null) {
      return _step;
    }
    if (_exp == null) {
      return null;
    }
    if ((_exp instanceof EqualityExpr))
    {
      EqualityExpr localEqualityExpr = (EqualityExpr)_exp;
      Expression localExpression1 = localEqualityExpr.getLeft();
      Expression localExpression2 = localEqualityExpr.getRight();
      if ((localExpression1 instanceof CastExpr)) {
        localExpression1 = ((CastExpr)localExpression1).getExpr();
      }
      if ((localExpression1 instanceof Step)) {
        _step = ((Step)localExpression1);
      }
      if ((localExpression2 instanceof CastExpr)) {
        localExpression2 = ((CastExpr)localExpression2).getExpr();
      }
      if ((localExpression2 instanceof Step)) {
        _step = ((Step)localExpression2);
      }
    }
    return _step;
  }
  
  public Expression getCompareValue()
  {
    if (_value != null) {
      return _value;
    }
    if (_exp == null) {
      return null;
    }
    if ((_exp instanceof EqualityExpr))
    {
      EqualityExpr localEqualityExpr = (EqualityExpr)_exp;
      Expression localExpression1 = localEqualityExpr.getLeft();
      Expression localExpression2 = localEqualityExpr.getRight();
      if ((localExpression1 instanceof LiteralExpr))
      {
        _value = localExpression1;
        return _value;
      }
      if (((localExpression1 instanceof VariableRefBase)) && (localExpression1.getType() == com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String))
      {
        _value = localExpression1;
        return _value;
      }
      if ((localExpression2 instanceof LiteralExpr))
      {
        _value = localExpression2;
        return _value;
      }
      if (((localExpression2 instanceof VariableRefBase)) && (localExpression2.getType() == com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String))
      {
        _value = localExpression2;
        return _value;
      }
    }
    return null;
  }
  
  public void translateFilter(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    compileFilter(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new NEW(localConstantPoolGen.addClass(_className)));
    localInstructionList.append(DUP);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref(_className, "<init>", "()V")));
    int i = _closureVars == null ? 0 : _closureVars.size();
    for (int j = 0; j < i; j++)
    {
      VariableRefBase localVariableRefBase = (VariableRefBase)_closureVars.get(j);
      VariableBase localVariableBase = localVariableRefBase.getVariable();
      com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = localVariableBase.getType();
      localInstructionList.append(DUP);
      for (Closure localClosure = _parentClosure; (localClosure != null) && (!localClosure.inInnerClass()); localClosure = localClosure.getParentClosure()) {}
      if (localClosure != null)
      {
        localInstructionList.append(ALOAD_0);
        localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref(localClosure.getInnerClassName(), localVariableBase.getEscapedName(), localType.toSignature())));
      }
      else
      {
        localInstructionList.append(localVariableBase.loadInstruction());
      }
      localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref(_className, localVariableBase.getEscapedName(), localType.toSignature())));
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((_nthPositionFilter) || (_nthDescendant))
    {
      _exp.translate(paramClassGenerator, paramMethodGenerator);
    }
    else if ((isNodeValueTest()) && ((getParent() instanceof Step)))
    {
      _value.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("java.lang.String")));
      localInstructionList.append(new PUSH(localConstantPoolGen, ((EqualityExpr)_exp).getOp()));
    }
    else
    {
      translateFilter(paramClassGenerator, paramMethodGenerator);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Predicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */