package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class AbsolutePathPattern
  extends LocationPathPattern
{
  private final RelativePathPattern _left;
  
  public AbsolutePathPattern(RelativePathPattern paramRelativePathPattern)
  {
    _left = paramRelativePathPattern;
    if (paramRelativePathPattern != null) {
      paramRelativePathPattern.setParent(this);
    }
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (_left != null) {
      _left.setParser(paramParser);
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return _left == null ? Type.Root : _left.typeCheck(paramSymbolTable);
  }
  
  public boolean isWildcard()
  {
    return false;
  }
  
  public StepPattern getKernelPattern()
  {
    return _left != null ? _left.getKernelPattern() : null;
  }
  
  public void reduceKernelPattern()
  {
    _left.reduceKernelPattern();
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_left != null) {
      if ((_left instanceof StepPattern))
      {
        LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable2("apptmp", Util.getJCRefType("I"), null);
        localInstructionList.append(DUP);
        localLocalVariableGen.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen.getIndex())));
        _left.translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localLocalVariableGen.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen.getIndex())));
        paramMethodGenerator.removeLocalVariable(localLocalVariableGen);
      }
      else
      {
        _left.translate(paramClassGenerator, paramMethodGenerator);
      }
    }
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
    InstructionHandle localInstructionHandle = localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(SWAP);
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
    if ((_left instanceof AncestorPattern))
    {
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
    }
    localInstructionList.append(new INVOKEINTERFACE(j, 2));
    localInstructionList.append(new PUSH(localConstantPoolGen, 9));
    BranchHandle localBranchHandle = localInstructionList.append(new IF_ICMPEQ(null));
    _falseList.add(localInstructionList.append(new GOTO_W(null)));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
    if (_left != null)
    {
      _left.backPatchTrueList(localInstructionHandle);
      if ((_left instanceof AncestorPattern))
      {
        AncestorPattern localAncestorPattern = (AncestorPattern)_left;
        _falseList.backPatch(localAncestorPattern.getLoopHandle());
      }
      _falseList.append(_left._falseList);
    }
  }
  
  public String toString()
  {
    return "absolutePathPattern(" + (_left != null ? _left.toString() : ")");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AbsolutePathPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */