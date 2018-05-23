package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class AlternativePattern
  extends Pattern
{
  private final Pattern _left;
  private final Pattern _right;
  
  public AlternativePattern(Pattern paramPattern1, Pattern paramPattern2)
  {
    _left = paramPattern1;
    _right = paramPattern2;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
    _right.setParser(paramParser);
  }
  
  public Pattern getLeft()
  {
    return _left;
  }
  
  public Pattern getRight()
  {
    return _right;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _left.typeCheck(paramSymbolTable);
    _right.typeCheck(paramSymbolTable);
    return null;
  }
  
  public double getPriority()
  {
    double d1 = _left.getPriority();
    double d2 = _right.getPriority();
    if (d1 < d2) {
      return d1;
    }
    return d2;
  }
  
  public String toString()
  {
    return "alternative(" + _left + ", " + _right + ')';
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _left.translate(paramClassGenerator, paramMethodGenerator);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    localInstructionList.append(paramMethodGenerator.loadContextNode());
    _right.translate(paramClassGenerator, paramMethodGenerator);
    _left._trueList.backPatch(localBranchHandle);
    _left._falseList.backPatch(localBranchHandle.getNext());
    _trueList.append(_right._trueList.add(localBranchHandle));
    _falseList.append(_right._falseList);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AlternativePattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */