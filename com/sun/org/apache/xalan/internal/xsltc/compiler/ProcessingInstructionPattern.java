package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ProcessingInstructionPattern
  extends StepPattern
{
  private String _name = null;
  private boolean _typeChecked = false;
  
  public ProcessingInstructionPattern(String paramString)
  {
    super(3, 7, null);
    _name = paramString;
  }
  
  public double getDefaultPriority()
  {
    return _name != null ? 0.0D : -0.5D;
  }
  
  public String toString()
  {
    if (_predicates == null) {
      return "processing-instruction(" + _name + ")";
    }
    return "processing-instruction(" + _name + ")" + _predicates;
  }
  
  public void reduceKernelPattern()
  {
    _typeChecked = true;
  }
  
  public boolean isWildcard()
  {
    return false;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (hasPredicates())
    {
      int i = _predicates.size();
      for (int j = 0; j < i; j++)
      {
        Predicate localPredicate = (Predicate)_predicates.elementAt(j);
        localPredicate.typeCheck(paramSymbolTable);
      }
    }
    return Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeName", "(I)Ljava/lang/String;");
    int j = localConstantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(SWAP);
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    int k;
    if (!_typeChecked)
    {
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      k = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      localInstructionList.append(new INVOKEINTERFACE(k, 2));
      localInstructionList.append(new PUSH(localConstantPoolGen, 7));
      _falseList.add(localInstructionList.append(new IF_ICMPEQ(null)));
    }
    localInstructionList.append(new PUSH(localConstantPoolGen, _name));
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
    localInstructionList.append(new INVOKEVIRTUAL(j));
    _falseList.add(localInstructionList.append(new IFEQ(null)));
    if (hasPredicates())
    {
      k = _predicates.size();
      for (int m = 0; m < k; m++)
      {
        Predicate localPredicate = (Predicate)_predicates.elementAt(m);
        Expression localExpression = localPredicate.getExpr();
        localExpression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
        _trueList.append(_trueList);
        _falseList.append(_falseList);
      }
    }
    InstructionHandle localInstructionHandle = localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchTrueList(localInstructionHandle);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    localInstructionHandle = localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    backPatchFalseList(localInstructionHandle);
    _falseList.add(localInstructionList.append(new GOTO(null)));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ProcessingInstructionPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */