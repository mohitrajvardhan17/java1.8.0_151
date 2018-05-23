package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

abstract class Expression
  extends SyntaxTreeNode
{
  protected Type _type;
  protected FlowList _trueList = new FlowList();
  protected FlowList _falseList = new FlowList();
  
  Expression() {}
  
  public Type getType()
  {
    return _type;
  }
  
  public abstract String toString();
  
  public boolean hasPositionCall()
  {
    return false;
  }
  
  public boolean hasLastCall()
  {
    return false;
  }
  
  public Object evaluateAtCompileTime()
  {
    return null;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return typeCheckContents(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("NOT_IMPLEMENTED_ERR", getClass(), this);
    getParser().reportError(2, localErrorMsg);
  }
  
  public final InstructionList compile(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList2 = paramMethodGenerator.getInstructionList();
    InstructionList localInstructionList1;
    paramMethodGenerator.setInstructionList(localInstructionList1 = new InstructionList());
    translate(paramClassGenerator, paramMethodGenerator);
    paramMethodGenerator.setInstructionList(localInstructionList2);
    return localInstructionList1;
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translate(paramClassGenerator, paramMethodGenerator);
    if ((_type instanceof BooleanType)) {
      desynthesize(paramClassGenerator, paramMethodGenerator);
    }
  }
  
  public void startIterator(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (!(_type instanceof NodeSetType)) {
      return;
    }
    Expression localExpression = this;
    if ((localExpression instanceof CastExpr)) {
      localExpression = ((CastExpr)localExpression).getExpr();
    }
    if (!(localExpression instanceof VariableRefBase))
    {
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localInstructionList.append(paramMethodGenerator.setStartNode());
    }
  }
  
  public void synthesize(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _trueList.backPatch(localInstructionList.append(ICONST_1));
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO_W(null));
    _falseList.backPatch(localInstructionList.append(ICONST_0));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
  
  public void desynthesize(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _falseList.add(localInstructionList.append(new IFEQ(null)));
  }
  
  public FlowList getFalseList()
  {
    return _falseList;
  }
  
  public FlowList getTrueList()
  {
    return _trueList;
  }
  
  public void backPatchFalseList(InstructionHandle paramInstructionHandle)
  {
    _falseList.backPatch(paramInstructionHandle);
  }
  
  public void backPatchTrueList(InstructionHandle paramInstructionHandle)
  {
    _trueList.backPatch(paramInstructionHandle);
  }
  
  public MethodType lookupPrimop(SymbolTable paramSymbolTable, String paramString, MethodType paramMethodType)
  {
    Object localObject = null;
    Vector localVector = paramSymbolTable.lookupPrimop(paramString);
    if (localVector != null)
    {
      int i = localVector.size();
      int j = Integer.MAX_VALUE;
      for (int k = 0; k < i; k++)
      {
        MethodType localMethodType = (MethodType)localVector.elementAt(k);
        if (localMethodType.argsCount() == paramMethodType.argsCount())
        {
          if (localObject == null) {
            localObject = localMethodType;
          }
          int m = paramMethodType.distanceTo(localMethodType);
          if (m < j)
          {
            j = m;
            localObject = localMethodType;
          }
        }
      }
    }
    return (MethodType)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */