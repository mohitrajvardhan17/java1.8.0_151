package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class IntType
  extends NumberType
{
  protected IntType() {}
  
  public String toString()
  {
    return "int";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "I";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return com.sun.org.apache.bcel.internal.generic.Type.INT;
  }
  
  public int distanceTo(Type paramType)
  {
    if (paramType == this) {
      return 0;
    }
    if (paramType == Type.Real) {
      return 1;
    }
    return Integer.MAX_VALUE;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.Real)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    }
    else if (paramType == Type.String)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    }
    else if (paramType == Type.Boolean)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    }
    else if (paramType == Type.Reference)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    paramMethodGenerator.getInstructionList().append(I2D);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("java.lang.Integer", "toString", "(I)Ljava/lang/String;")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    BranchHandle localBranchHandle1 = localInstructionList.append(new IFEQ(null));
    localInstructionList.append(ICONST_1);
    BranchHandle localBranchHandle2 = localInstructionList.append(new GOTO(null));
    localBranchHandle1.setTarget(localInstructionList.append(ICONST_0));
    localBranchHandle2.setTarget(localInstructionList.append(NOP));
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    return new FlowList(localInstructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.lang.Integer")));
    localInstructionList.append(DUP_X1);
    localInstructionList.append(SWAP);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("java.lang.Integer", "<init>", "(I)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (paramClass == Character.TYPE)
    {
      localInstructionList.append(I2C);
    }
    else if (paramClass == Byte.TYPE)
    {
      localInstructionList.append(I2B);
    }
    else if (paramClass == Short.TYPE)
    {
      localInstructionList.append(I2S);
    }
    else if (paramClass == Integer.TYPE)
    {
      localInstructionList.append(NOP);
    }
    else if (paramClass == Long.TYPE)
    {
      localInstructionList.append(I2L);
    }
    else if (paramClass == Float.TYPE)
    {
      localInstructionList.append(I2F);
    }
    else if (paramClass == Double.TYPE)
    {
      localInstructionList.append(I2D);
    }
    else if (paramClass.isAssignableFrom(Double.class))
    {
      localInstructionList.append(I2D);
      Type.Real.translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
  }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("java.lang.Integer")));
    int i = localConstantPoolGen.addMethodref("java.lang.Integer", "intValue", "()I");
    localInstructionList.append(new INVOKEVIRTUAL(i));
  }
  
  public Instruction ADD()
  {
    return InstructionConstants.IADD;
  }
  
  public Instruction SUB()
  {
    return InstructionConstants.ISUB;
  }
  
  public Instruction MUL()
  {
    return InstructionConstants.IMUL;
  }
  
  public Instruction DIV()
  {
    return InstructionConstants.IDIV;
  }
  
  public Instruction REM()
  {
    return InstructionConstants.IREM;
  }
  
  public Instruction NEG()
  {
    return InstructionConstants.INEG;
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new ILOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ISTORE(paramInt);
  }
  
  public BranchInstruction GT(boolean paramBoolean)
  {
    return paramBoolean ? new IFGT(null) : new IF_ICMPGT(null);
  }
  
  public BranchInstruction GE(boolean paramBoolean)
  {
    return paramBoolean ? new IFGE(null) : new IF_ICMPGE(null);
  }
  
  public BranchInstruction LT(boolean paramBoolean)
  {
    return paramBoolean ? new IFLT(null) : new IF_ICMPLT(null);
  }
  
  public BranchInstruction LE(boolean paramBoolean)
  {
    return paramBoolean ? new IFLE(null) : new IF_ICMPLE(null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\IntType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */