package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class ObjectType
  extends Type
{
  private String _javaClassName = "java.lang.Object";
  private Class _clazz = Object.class;
  
  protected ObjectType(String paramString)
  {
    _javaClassName = paramString;
    try
    {
      _clazz = ObjectFactory.findProviderClass(paramString, true);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      _clazz = null;
    }
  }
  
  protected ObjectType(Class paramClass)
  {
    _clazz = paramClass;
    _javaClassName = paramClass.getName();
  }
  
  public int hashCode()
  {
    return Object.class.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof ObjectType;
  }
  
  public String getJavaClassName()
  {
    return _javaClassName;
  }
  
  public Class getJavaClass()
  {
    return _clazz;
  }
  
  public String toString()
  {
    return _javaClassName;
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    StringBuffer localStringBuffer = new StringBuffer("L");
    localStringBuffer.append(_javaClassName.replace('.', '/')).append(';');
    return localStringBuffer.toString();
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return Util.getJCRefType(toSignature());
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
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(DUP);
    BranchHandle localBranchHandle1 = localInstructionList.append(new IFNULL(null));
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref(_javaClassName, "toString", "()Ljava/lang/String;")));
    BranchHandle localBranchHandle2 = localInstructionList.append(new GOTO(null));
    localBranchHandle1.setTarget(localInstructionList.append(POP));
    localInstructionList.append(new PUSH(localConstantPoolGen, ""));
    localBranchHandle2.setTarget(localInstructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    if (paramClass.isAssignableFrom(_clazz))
    {
      paramMethodGenerator.getInstructionList().append(NOP);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getClass().toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new ALOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ASTORE(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\ObjectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */