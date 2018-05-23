package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class NotCall
  extends FunctionCall
{
  public NotCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    argument().translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(ICONST_1);
    localInstructionList.append(IXOR);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Expression localExpression = argument();
    localExpression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    _trueList = _falseList;
    _falseList = _trueList;
    _falseList.add(localBranchHandle);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NotCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */