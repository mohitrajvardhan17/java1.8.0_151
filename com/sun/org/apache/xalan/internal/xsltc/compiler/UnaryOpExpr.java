package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class UnaryOpExpr
  extends Expression
{
  private Expression _left;
  
  public UnaryOpExpr(Expression paramExpression)
  {
    (_left = paramExpression).setParent(this);
  }
  
  public boolean hasPositionCall()
  {
    return _left.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return _left.hasLastCall();
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = _left.typeCheck(paramSymbolTable);
    MethodType localMethodType = lookupPrimop(paramSymbolTable, "u-", new MethodType(Type.Void, localType1));
    if (localMethodType != null)
    {
      Type localType2 = (Type)localMethodType.argsType().elementAt(0);
      if (!localType2.identicalTo(localType1)) {
        _left = new CastExpr(_left, localType2);
      }
      return _type = localMethodType.resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public String toString()
  {
    return "u-(" + _left + ')';
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _left.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(_type.NEG());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnaryOpExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */