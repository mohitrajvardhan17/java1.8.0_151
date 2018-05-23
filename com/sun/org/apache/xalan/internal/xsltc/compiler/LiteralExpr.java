package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class LiteralExpr
  extends Expression
{
  private final String _value;
  private final String _namespace;
  
  public LiteralExpr(String paramString)
  {
    _value = paramString;
    _namespace = null;
  }
  
  public LiteralExpr(String paramString1, String paramString2)
  {
    _value = paramString1;
    _namespace = (paramString2.equals("") ? null : paramString2);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return _type = Type.String;
  }
  
  public String toString()
  {
    return "literal-expr(" + _value + ')';
  }
  
  protected boolean contextDependent()
  {
    return false;
  }
  
  protected String getValue()
  {
    return _value;
  }
  
  protected String getNamespace()
  {
    return _namespace;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new PUSH(localConstantPoolGen, _value));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LiteralExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */