package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class SimpleAttributeValue
  extends AttributeValue
{
  private String _value;
  
  public SimpleAttributeValue(String paramString)
  {
    _value = paramString;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return _type = Type.String;
  }
  
  public String toString()
  {
    return _value;
  }
  
  protected boolean contextDependent()
  {
    return false;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new PUSH(localConstantPoolGen, _value));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\SimpleAttributeValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */