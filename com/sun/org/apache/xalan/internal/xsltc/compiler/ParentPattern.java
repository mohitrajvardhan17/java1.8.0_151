package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
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

final class ParentPattern
  extends RelativePathPattern
{
  private final Pattern _left;
  private final RelativePathPattern _right;
  
  public ParentPattern(Pattern paramPattern, RelativePathPattern paramRelativePathPattern)
  {
    (_left = paramPattern).setParent(this);
    (_right = paramRelativePathPattern).setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
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
    _left.typeCheck(paramSymbolTable);
    return _right.typeCheck(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable2("ppt", Util.getJCRefType("I"), null);
    ILOAD localILOAD = new ILOAD(localLocalVariableGen.getIndex());
    ISTORE localISTORE = new ISTORE(localLocalVariableGen.getIndex());
    if (_right.isWildcard())
    {
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
    }
    else if ((_right instanceof StepPattern))
    {
      localInstructionList.append(DUP);
      localLocalVariableGen.setStart(localInstructionList.append(localISTORE));
      _right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localLocalVariableGen.setEnd(localInstructionList.append(localILOAD));
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
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    Object localObject;
    if ((localSyntaxTreeNode == null) || ((localSyntaxTreeNode instanceof Instruction)) || ((localSyntaxTreeNode instanceof TopLevelElement)))
    {
      _left.translate(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      localInstructionList.append(DUP);
      localObject = localInstructionList.append(localISTORE);
      if (localLocalVariableGen.getStart() == null) {
        localLocalVariableGen.setStart((InstructionHandle)localObject);
      }
      _left.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localLocalVariableGen.setEnd(localInstructionList.append(localILOAD));
    }
    paramMethodGenerator.removeLocalVariable(localLocalVariableGen);
    if ((_right instanceof AncestorPattern))
    {
      localObject = (AncestorPattern)_right;
      _left.backPatchFalseList(((AncestorPattern)localObject).getLoopHandle());
    }
    _trueList.append(_right._trueList.append(_left._trueList));
    _falseList.append(_right._falseList.append(_left._falseList));
  }
  
  public String toString()
  {
    return "Parent(" + _left + ", " + _right + ')';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ParentPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */