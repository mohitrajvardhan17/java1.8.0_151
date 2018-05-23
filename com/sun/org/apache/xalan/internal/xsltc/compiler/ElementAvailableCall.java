package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ElementAvailableCall
  extends FunctionCall
{
  public ElementAvailableCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if ((argument() instanceof LiteralExpr)) {
      return _type = Type.Boolean;
    }
    ErrorMsg localErrorMsg = new ErrorMsg("NEED_LITERAL_ERR", "element-available", this);
    throw new TypeCheckError(localErrorMsg);
  }
  
  public Object evaluateAtCompileTime()
  {
    return getResult() ? Boolean.TRUE : Boolean.FALSE;
  }
  
  public boolean getResult()
  {
    try
    {
      LiteralExpr localLiteralExpr = (LiteralExpr)argument();
      String str1 = localLiteralExpr.getValue();
      int i = str1.indexOf(':');
      String str2 = i > 0 ? str1.substring(i + 1) : str1;
      return getParser().elementSupported(localLiteralExpr.getNamespace(), str2);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    boolean bool = getResult();
    paramMethodGenerator.getInstructionList().append(new PUSH(localConstantPoolGen, bool));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ElementAvailableCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */