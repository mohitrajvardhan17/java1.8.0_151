package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class StringCall
  extends FunctionCall
{
  public StringCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    int i = argumentCount();
    if (i > 1)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
      throw new TypeCheckError(localErrorMsg);
    }
    if (i > 0) {
      argument().typeCheck(paramSymbolTable);
    }
    return _type = Type.String;
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
    if (!localType.identicalTo(Type.String)) {
      localType.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StringCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */