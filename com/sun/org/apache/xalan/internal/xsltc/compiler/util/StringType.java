package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public class StringType
  extends Type
{
  protected StringType() {}
  
  public String toString()
  {
    return "string";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "Ljava/lang/String;";
  }
  
  public boolean isSimple()
  {
    return true;
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return com.sun.org.apache.bcel.internal.generic.Type.STRING;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.Boolean)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    }
    else if (paramType == Type.Real)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    }
    else if (paramType == Type.Reference)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    }
    else if (paramType != Type.ObjectString)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    FlowList localFlowList = translateToDesynthesized(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    localInstructionList.append(ICONST_1);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    localFlowList.backPatch(localInstructionList.append(ICONST_0));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "stringToReal", "(Ljava/lang/String;)D")));
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.lang.String", "length", "()I")));
    return new FlowList(localInstructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    if (paramClass.isAssignableFrom(String.class))
    {
      paramMethodGenerator.getInstructionList().append(NOP);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Object localObject;
    if (paramClass.getName().equals("java.lang.String"))
    {
      localInstructionList.append(DUP);
      localObject = localInstructionList.append(new IFNONNULL(null));
      localInstructionList.append(POP);
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
      ((BranchHandle)localObject).setTarget(localInstructionList.append(NOP));
    }
    else
    {
      localObject = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, (ErrorMsg)localObject);
    }
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
  }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public String getClassName()
  {
    return "java.lang.String";
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\StringType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */