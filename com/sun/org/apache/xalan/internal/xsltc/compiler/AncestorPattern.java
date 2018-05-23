package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class AncestorPattern
  extends RelativePathPattern
{
  private final Pattern _left;
  private final RelativePathPattern _right;
  private InstructionHandle _loop;
  
  public AncestorPattern(RelativePathPattern paramRelativePathPattern)
  {
    this(null, paramRelativePathPattern);
  }
  
  public AncestorPattern(Pattern paramPattern, RelativePathPattern paramRelativePathPattern)
  {
    _left = paramPattern;
    (_right = paramRelativePathPattern).setParent(this);
    if (paramPattern != null) {
      paramPattern.setParent(this);
    }
  }
  
  public InstructionHandle getLoopHandle()
  {
    return _loop;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (_left != null) {
      _left.setParser(paramParser);
    }
    _right.setParser(paramParser);
  }
  
  public boolean isWildcard()
  {
    return false;
  }
  
  public StepPattern getKernelPattern()
  {
    return _right.getKernelPattern();
  }
  
  public void reduceKernelPattern()
  {
    _right.reduceKernelPattern();
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_left != null) {
      _left.typeCheck(paramSymbolTable);
    }
    return _right.typeCheck(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable2("app", Util.getJCRefType("I"), localInstructionList.getEnd());
    ILOAD localILOAD = new ILOAD(localLocalVariableGen.getIndex());
    ISTORE localISTORE = new ISTORE(localLocalVariableGen.getIndex());
    if ((_right instanceof StepPattern))
    {
      localInstructionList.append(DUP);
      localInstructionList.append(localISTORE);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(localILOAD);
    }
    else
    {
      _right.translate(paramClassGenerator, paramMethodGenerator);
      if ((_right instanceof AncestorPattern))
      {
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(SWAP);
      }
    }
    if (_left != null)
    {
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
      InstructionHandle localInstructionHandle = localInstructionList.append(new INVOKEINTERFACE(i, 2));
      localInstructionList.append(DUP);
      localInstructionList.append(localISTORE);
      _falseList.add(localInstructionList.append(new IFLT(null)));
      localInstructionList.append(localILOAD);
      _left.translate(paramClassGenerator, paramMethodGenerator);
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      if ((localSyntaxTreeNode != null) && (!(localSyntaxTreeNode instanceof Instruction)) && (!(localSyntaxTreeNode instanceof TopLevelElement))) {
        localInstructionList.append(localILOAD);
      }
      BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
      _loop = localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(localILOAD);
      localLocalVariableGen.setEnd(_loop);
      localInstructionList.append(new GOTO(localInstructionHandle));
      localBranchHandle.setTarget(localInstructionList.append(NOP));
      _left.backPatchFalseList(_loop);
      _trueList.append(_left._trueList);
    }
    else
    {
      localInstructionList.append(POP2);
    }
    if ((_right instanceof AncestorPattern))
    {
      AncestorPattern localAncestorPattern = (AncestorPattern)_right;
      _falseList.backPatch(localAncestorPattern.getLoopHandle());
    }
    _trueList.append(_right._trueList);
    _falseList.append(_right._falseList);
  }
  
  public String toString()
  {
    return "AncestorPattern(" + _left + ", " + _right + ')';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AncestorPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */