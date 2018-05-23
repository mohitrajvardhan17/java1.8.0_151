package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ContainsCall
  extends FunctionCall
{
  private Expression _base = null;
  private Expression _token = null;
  
  public ContainsCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public boolean isBoolean()
  {
    return true;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (argumentCount() != 2) {
      throw new TypeCheckError("ILLEGAL_ARG_ERR", getName(), this);
    }
    _base = argument(0);
    Type localType1 = _base.typeCheck(paramSymbolTable);
    if (localType1 != Type.String) {
      _base = new CastExpr(_base, Type.String);
    }
    _token = argument(1);
    Type localType2 = _token.typeCheck(paramSymbolTable);
    if (localType2 != Type.String) {
      _token = new CastExpr(_token, Type.String);
    }
    return _type = Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    synthesize(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _base.translate(paramClassGenerator, paramMethodGenerator);
    _token.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.lang.String", "indexOf", "(Ljava/lang/String;)I")));
    _falseList.add(localInstructionList.append(new IFLT(null)));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ContainsCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */