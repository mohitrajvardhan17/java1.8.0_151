package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class NumberCall
  extends FunctionCall
{
  public NumberCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (argumentCount() > 0) {
      argument().typeCheck(paramSymbolTable);
    }
    return _type = Type.Real;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Type localType;
    if (argumentCount() == 0)
    {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localType = Type.Node;
    }
    else
    {
      Expression localExpression = argument();
      localExpression.translate(paramClassGenerator, paramMethodGenerator);
      localExpression.startIterator(paramClassGenerator, paramMethodGenerator);
      localType = localExpression.getType();
    }
    if (!localType.identicalTo(Type.Real)) {
      localType.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NumberCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */