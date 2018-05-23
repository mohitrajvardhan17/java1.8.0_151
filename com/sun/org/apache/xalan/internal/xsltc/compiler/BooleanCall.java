package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class BooleanCall
  extends FunctionCall
{
  private Expression _arg = null;
  
  public BooleanCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _arg.typeCheck(paramSymbolTable);
    return _type = Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _arg.translate(paramClassGenerator, paramMethodGenerator);
    Type localType = _arg.getType();
    if (!localType.identicalTo(Type.Boolean))
    {
      _arg.startIterator(paramClassGenerator, paramMethodGenerator);
      localType.translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\BooleanCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */