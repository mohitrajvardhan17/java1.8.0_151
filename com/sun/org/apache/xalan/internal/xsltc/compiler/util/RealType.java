package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class RealType
  extends NumberType
{
  protected RealType() {}
  
  public String toString()
  {
    return "real";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "D";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return com.sun.org.apache.bcel.internal.generic.Type.DOUBLE;
  }
  
  public int distanceTo(Type paramType)
  {
    if (paramType == this) {
      return 0;
    }
    if (paramType == Type.Int) {
      return 1;
    }
    return Integer.MAX_VALUE;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.String)
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
    else if (paramType == Type.Int)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (IntType)paramType);
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
    localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "realToString", "(D)Ljava/lang/String;")));
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
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, IntType paramIntType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "realToInt", "(D)I")));
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    FlowList localFlowList = new FlowList();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(DUP2);
    LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable("real_to_boolean_tmp", com.sun.org.apache.bcel.internal.generic.Type.DOUBLE, null, null);
    localLocalVariableGen.setStart(localInstructionList.append(new DSTORE(localLocalVariableGen.getIndex())));
    localInstructionList.append(DCONST_0);
    localInstructionList.append(DCMPG);
    localFlowList.add(localInstructionList.append(new IFEQ(null)));
    localInstructionList.append(new DLOAD(localLocalVariableGen.getIndex()));
    localLocalVariableGen.setEnd(localInstructionList.append(new DLOAD(localLocalVariableGen.getIndex())));
    localInstructionList.append(DCMPG);
    localFlowList.add(localInstructionList.append(new IFNE(null)));
    return localFlowList;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.lang.Double")));
    localInstructionList.append(DUP_X2);
    localInstructionList.append(DUP_X2);
    localInstructionList.append(POP);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("java.lang.Double", "<init>", "(D)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (paramClass == Character.TYPE)
    {
      localInstructionList.append(D2I);
      localInstructionList.append(I2C);
    }
    else if (paramClass == Byte.TYPE)
    {
      localInstructionList.append(D2I);
      localInstructionList.append(I2B);
    }
    else if (paramClass == Short.TYPE)
    {
      localInstructionList.append(D2I);
      localInstructionList.append(I2S);
    }
    else if (paramClass == Integer.TYPE)
    {
      localInstructionList.append(D2I);
    }
    else if (paramClass == Long.TYPE)
    {
      localInstructionList.append(D2L);
    }
    else if (paramClass == Float.TYPE)
    {
      localInstructionList.append(D2F);
    }
    else if (paramClass == Double.TYPE)
    {
      localInstructionList.append(NOP);
    }
    else if (paramClass.isAssignableFrom(Double.class))
    {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((paramClass == Character.TYPE) || (paramClass == Byte.TYPE) || (paramClass == Short.TYPE) || (paramClass == Integer.TYPE))
    {
      localInstructionList.append(I2D);
    }
    else if (paramClass == Long.TYPE)
    {
      localInstructionList.append(L2D);
    }
    else if (paramClass == Float.TYPE)
    {
      localInstructionList.append(F2D);
    }
    else if (paramClass == Double.TYPE)
    {
      localInstructionList.append(NOP);
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
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("java.lang.Double")));
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.lang.Double", "doubleValue", "()D")));
  }
  
  public Instruction ADD()
  {
    return InstructionConstants.DADD;
  }
  
  public Instruction SUB()
  {
    return InstructionConstants.DSUB;
  }
  
  public Instruction MUL()
  {
    return InstructionConstants.DMUL;
  }
  
  public Instruction DIV()
  {
    return InstructionConstants.DDIV;
  }
  
  public Instruction REM()
  {
    return InstructionConstants.DREM;
  }
  
  public Instruction NEG()
  {
    return InstructionConstants.DNEG;
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new DLOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new DSTORE(paramInt);
  }
  
  public Instruction POP()
  {
    return POP2;
  }
  
  public Instruction CMP(boolean paramBoolean)
  {
    return paramBoolean ? InstructionConstants.DCMPG : InstructionConstants.DCMPL;
  }
  
  public Instruction DUP()
  {
    return DUP2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\RealType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */