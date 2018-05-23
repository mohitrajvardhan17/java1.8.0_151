package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class VoidType
  extends Type
{
  protected VoidType() {}
  
  public String toString()
  {
    return "void";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "V";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return null;
  }
  
  public Instruction POP()
  {
    return NOP;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.String)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new PUSH(paramClassGenerator.getConstantPool(), ""));
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    if (!paramClass.getName().equals("void"))
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\VoidType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */