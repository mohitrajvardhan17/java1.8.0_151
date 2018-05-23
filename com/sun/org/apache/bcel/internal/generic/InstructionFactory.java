package com.sun.org.apache.bcel.internal.generic;

import java.io.Serializable;

public class InstructionFactory
  implements InstructionConstants, Serializable
{
  protected ClassGen cg;
  protected ConstantPoolGen cp;
  private static MethodObject[] append_mos = { new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.STRING }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.OBJECT }, 1), null, null, new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.BOOLEAN }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.CHAR }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.FLOAT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.DOUBLE }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.LONG }, 1) };
  
  public InstructionFactory(ClassGen paramClassGen, ConstantPoolGen paramConstantPoolGen)
  {
    cg = paramClassGen;
    cp = paramConstantPoolGen;
  }
  
  public InstructionFactory(ClassGen paramClassGen)
  {
    this(paramClassGen, paramClassGen.getConstantPool());
  }
  
  public InstructionFactory(ConstantPoolGen paramConstantPoolGen)
  {
    this(null, paramConstantPoolGen);
  }
  
  public InvokeInstruction createInvoke(String paramString1, String paramString2, Type paramType, Type[] paramArrayOfType, short paramShort)
  {
    int j = 0;
    String str = Type.getMethodSignature(paramType, paramArrayOfType);
    for (int k = 0; k < paramArrayOfType.length; k++) {
      j += paramArrayOfType[k].getSize();
    }
    int i;
    if (paramShort == 185) {
      i = cp.addInterfaceMethodref(paramString1, paramString2, str);
    } else {
      i = cp.addMethodref(paramString1, paramString2, str);
    }
    switch (paramShort)
    {
    case 183: 
      return new INVOKESPECIAL(i);
    case 182: 
      return new INVOKEVIRTUAL(i);
    case 184: 
      return new INVOKESTATIC(i);
    case 185: 
      return new INVOKEINTERFACE(i, j + 1);
    }
    throw new RuntimeException("Oops: Unknown invoke kind:" + paramShort);
  }
  
  public InstructionList createPrintln(String paramString)
  {
    InstructionList localInstructionList = new InstructionList();
    int i = cp.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;");
    int j = cp.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
    localInstructionList.append(new GETSTATIC(i));
    localInstructionList.append(new PUSH(cp, paramString));
    localInstructionList.append(new INVOKEVIRTUAL(j));
    return localInstructionList;
  }
  
  public Instruction createConstant(Object paramObject)
  {
    PUSH localPUSH;
    if ((paramObject instanceof Number)) {
      localPUSH = new PUSH(cp, (Number)paramObject);
    } else if ((paramObject instanceof String)) {
      localPUSH = new PUSH(cp, (String)paramObject);
    } else if ((paramObject instanceof Boolean)) {
      localPUSH = new PUSH(cp, (Boolean)paramObject);
    } else if ((paramObject instanceof Character)) {
      localPUSH = new PUSH(cp, (Character)paramObject);
    } else {
      throw new ClassGenException("Illegal type: " + paramObject.getClass());
    }
    return localPUSH.getInstruction();
  }
  
  private InvokeInstruction createInvoke(MethodObject paramMethodObject, short paramShort)
  {
    return createInvoke(class_name, name, result_type, arg_types, paramShort);
  }
  
  private static final boolean isString(Type paramType)
  {
    return ((paramType instanceof ObjectType)) && (((ObjectType)paramType).getClassName().equals("java.lang.String"));
  }
  
  public Instruction createAppend(Type paramType)
  {
    int i = paramType.getType();
    if (isString(paramType)) {
      return createInvoke(append_mos[0], (short)182);
    }
    switch (i)
    {
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
      return createInvoke(append_mos[i], (short)182);
    case 13: 
    case 14: 
      return createInvoke(append_mos[1], (short)182);
    }
    throw new RuntimeException("Oops: No append for this type? " + paramType);
  }
  
  public FieldInstruction createFieldAccess(String paramString1, String paramString2, Type paramType, short paramShort)
  {
    String str = paramType.getSignature();
    int i = cp.addFieldref(paramString1, paramString2, str);
    switch (paramShort)
    {
    case 180: 
      return new GETFIELD(i);
    case 181: 
      return new PUTFIELD(i);
    case 178: 
      return new GETSTATIC(i);
    case 179: 
      return new PUTSTATIC(i);
    }
    throw new RuntimeException("Oops: Unknown getfield kind:" + paramShort);
  }
  
  public static Instruction createThis()
  {
    return new ALOAD(0);
  }
  
  public static ReturnInstruction createReturn(Type paramType)
  {
    switch (paramType.getType())
    {
    case 13: 
    case 14: 
      return ARETURN;
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    case 10: 
      return IRETURN;
    case 6: 
      return FRETURN;
    case 7: 
      return DRETURN;
    case 11: 
      return LRETURN;
    case 12: 
      return RETURN;
    }
    throw new RuntimeException("Invalid type: " + paramType);
  }
  
  private static final ArithmeticInstruction createBinaryIntOp(char paramChar, String paramString)
  {
    switch (paramChar)
    {
    case '-': 
      return ISUB;
    case '+': 
      return IADD;
    case '%': 
      return IREM;
    case '*': 
      return IMUL;
    case '/': 
      return IDIV;
    case '&': 
      return IAND;
    case '|': 
      return IOR;
    case '^': 
      return IXOR;
    case '<': 
      return ISHL;
    case '>': 
      return paramString.equals(">>>") ? IUSHR : ISHR;
    }
    throw new RuntimeException("Invalid operand " + paramString);
  }
  
  private static final ArithmeticInstruction createBinaryLongOp(char paramChar, String paramString)
  {
    switch (paramChar)
    {
    case '-': 
      return LSUB;
    case '+': 
      return LADD;
    case '%': 
      return LREM;
    case '*': 
      return LMUL;
    case '/': 
      return LDIV;
    case '&': 
      return LAND;
    case '|': 
      return LOR;
    case '^': 
      return LXOR;
    case '<': 
      return LSHL;
    case '>': 
      return paramString.equals(">>>") ? LUSHR : LSHR;
    }
    throw new RuntimeException("Invalid operand " + paramString);
  }
  
  private static final ArithmeticInstruction createBinaryFloatOp(char paramChar)
  {
    switch (paramChar)
    {
    case '-': 
      return FSUB;
    case '+': 
      return FADD;
    case '*': 
      return FMUL;
    case '/': 
      return FDIV;
    }
    throw new RuntimeException("Invalid operand " + paramChar);
  }
  
  private static final ArithmeticInstruction createBinaryDoubleOp(char paramChar)
  {
    switch (paramChar)
    {
    case '-': 
      return DSUB;
    case '+': 
      return DADD;
    case '*': 
      return DMUL;
    case '/': 
      return DDIV;
    }
    throw new RuntimeException("Invalid operand " + paramChar);
  }
  
  public static ArithmeticInstruction createBinaryOperation(String paramString, Type paramType)
  {
    char c = paramString.toCharArray()[0];
    switch (paramType.getType())
    {
    case 5: 
    case 8: 
    case 9: 
    case 10: 
      return createBinaryIntOp(c, paramString);
    case 11: 
      return createBinaryLongOp(c, paramString);
    case 6: 
      return createBinaryFloatOp(c);
    case 7: 
      return createBinaryDoubleOp(c);
    }
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static StackInstruction createPop(int paramInt)
  {
    return paramInt == 2 ? POP2 : POP;
  }
  
  public static StackInstruction createDup(int paramInt)
  {
    return paramInt == 2 ? DUP2 : DUP;
  }
  
  public static StackInstruction createDup_2(int paramInt)
  {
    return paramInt == 2 ? DUP2_X2 : DUP_X2;
  }
  
  public static StackInstruction createDup_1(int paramInt)
  {
    return paramInt == 2 ? DUP2_X1 : DUP_X1;
  }
  
  public static LocalVariableInstruction createStore(Type paramType, int paramInt)
  {
    switch (paramType.getType())
    {
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    case 10: 
      return new ISTORE(paramInt);
    case 6: 
      return new FSTORE(paramInt);
    case 7: 
      return new DSTORE(paramInt);
    case 11: 
      return new LSTORE(paramInt);
    case 13: 
    case 14: 
      return new ASTORE(paramInt);
    }
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static LocalVariableInstruction createLoad(Type paramType, int paramInt)
  {
    switch (paramType.getType())
    {
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    case 10: 
      return new ILOAD(paramInt);
    case 6: 
      return new FLOAD(paramInt);
    case 7: 
      return new DLOAD(paramInt);
    case 11: 
      return new LLOAD(paramInt);
    case 13: 
    case 14: 
      return new ALOAD(paramInt);
    }
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static ArrayInstruction createArrayLoad(Type paramType)
  {
    switch (paramType.getType())
    {
    case 4: 
    case 8: 
      return BALOAD;
    case 5: 
      return CALOAD;
    case 9: 
      return SALOAD;
    case 10: 
      return IALOAD;
    case 6: 
      return FALOAD;
    case 7: 
      return DALOAD;
    case 11: 
      return LALOAD;
    case 13: 
    case 14: 
      return AALOAD;
    }
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static ArrayInstruction createArrayStore(Type paramType)
  {
    switch (paramType.getType())
    {
    case 4: 
    case 8: 
      return BASTORE;
    case 5: 
      return CASTORE;
    case 9: 
      return SASTORE;
    case 10: 
      return IASTORE;
    case 6: 
      return FASTORE;
    case 7: 
      return DASTORE;
    case 11: 
      return LASTORE;
    case 13: 
    case 14: 
      return AASTORE;
    }
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public Instruction createCast(Type paramType1, Type paramType2)
  {
    if (((paramType1 instanceof BasicType)) && ((paramType2 instanceof BasicType)))
    {
      int i = paramType2.getType();
      int j = paramType1.getType();
      if ((i == 11) && ((j == 5) || (j == 8) || (j == 9))) {
        j = 10;
      }
      String[] arrayOfString = { "C", "F", "D", "B", "S", "I", "L" };
      String str = "com.sun.org.apache.bcel.internal.generic." + arrayOfString[(j - 5)] + "2" + arrayOfString[(i - 5)];
      Instruction localInstruction = null;
      try
      {
        localInstruction = (Instruction)Class.forName(str).newInstance();
      }
      catch (Exception localException)
      {
        throw new RuntimeException("Could not find instruction: " + str);
      }
      return localInstruction;
    }
    if (((paramType1 instanceof ReferenceType)) && ((paramType2 instanceof ReferenceType)))
    {
      if ((paramType2 instanceof ArrayType)) {
        return new CHECKCAST(cp.addArrayClass((ArrayType)paramType2));
      }
      return new CHECKCAST(cp.addClass(((ObjectType)paramType2).getClassName()));
    }
    throw new RuntimeException("Can not cast " + paramType1 + " to " + paramType2);
  }
  
  public GETFIELD createGetField(String paramString1, String paramString2, Type paramType)
  {
    return new GETFIELD(cp.addFieldref(paramString1, paramString2, paramType.getSignature()));
  }
  
  public GETSTATIC createGetStatic(String paramString1, String paramString2, Type paramType)
  {
    return new GETSTATIC(cp.addFieldref(paramString1, paramString2, paramType.getSignature()));
  }
  
  public PUTFIELD createPutField(String paramString1, String paramString2, Type paramType)
  {
    return new PUTFIELD(cp.addFieldref(paramString1, paramString2, paramType.getSignature()));
  }
  
  public PUTSTATIC createPutStatic(String paramString1, String paramString2, Type paramType)
  {
    return new PUTSTATIC(cp.addFieldref(paramString1, paramString2, paramType.getSignature()));
  }
  
  public CHECKCAST createCheckCast(ReferenceType paramReferenceType)
  {
    if ((paramReferenceType instanceof ArrayType)) {
      return new CHECKCAST(cp.addArrayClass((ArrayType)paramReferenceType));
    }
    return new CHECKCAST(cp.addClass((ObjectType)paramReferenceType));
  }
  
  public INSTANCEOF createInstanceOf(ReferenceType paramReferenceType)
  {
    if ((paramReferenceType instanceof ArrayType)) {
      return new INSTANCEOF(cp.addArrayClass((ArrayType)paramReferenceType));
    }
    return new INSTANCEOF(cp.addClass((ObjectType)paramReferenceType));
  }
  
  public NEW createNew(ObjectType paramObjectType)
  {
    return new NEW(cp.addClass(paramObjectType));
  }
  
  public NEW createNew(String paramString)
  {
    return createNew(new ObjectType(paramString));
  }
  
  public Instruction createNewArray(Type paramType, short paramShort)
  {
    if (paramShort == 1)
    {
      if ((paramType instanceof ObjectType)) {
        return new ANEWARRAY(cp.addClass((ObjectType)paramType));
      }
      if ((paramType instanceof ArrayType)) {
        return new ANEWARRAY(cp.addArrayClass((ArrayType)paramType));
      }
      return new NEWARRAY(((BasicType)paramType).getType());
    }
    ArrayType localArrayType;
    if ((paramType instanceof ArrayType)) {
      localArrayType = (ArrayType)paramType;
    } else {
      localArrayType = new ArrayType(paramType, paramShort);
    }
    return new MULTIANEWARRAY(cp.addArrayClass(localArrayType), paramShort);
  }
  
  public static Instruction createNull(Type paramType)
  {
    switch (paramType.getType())
    {
    case 13: 
    case 14: 
      return ACONST_NULL;
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    case 10: 
      return ICONST_0;
    case 6: 
      return FCONST_0;
    case 7: 
      return DCONST_0;
    case 11: 
      return LCONST_0;
    case 12: 
      return NOP;
    }
    throw new RuntimeException("Invalid type: " + paramType);
  }
  
  public static BranchInstruction createBranchInstruction(short paramShort, InstructionHandle paramInstructionHandle)
  {
    switch (paramShort)
    {
    case 153: 
      return new IFEQ(paramInstructionHandle);
    case 154: 
      return new IFNE(paramInstructionHandle);
    case 155: 
      return new IFLT(paramInstructionHandle);
    case 156: 
      return new IFGE(paramInstructionHandle);
    case 157: 
      return new IFGT(paramInstructionHandle);
    case 158: 
      return new IFLE(paramInstructionHandle);
    case 159: 
      return new IF_ICMPEQ(paramInstructionHandle);
    case 160: 
      return new IF_ICMPNE(paramInstructionHandle);
    case 161: 
      return new IF_ICMPLT(paramInstructionHandle);
    case 162: 
      return new IF_ICMPGE(paramInstructionHandle);
    case 163: 
      return new IF_ICMPGT(paramInstructionHandle);
    case 164: 
      return new IF_ICMPLE(paramInstructionHandle);
    case 165: 
      return new IF_ACMPEQ(paramInstructionHandle);
    case 166: 
      return new IF_ACMPNE(paramInstructionHandle);
    case 167: 
      return new GOTO(paramInstructionHandle);
    case 168: 
      return new JSR(paramInstructionHandle);
    case 198: 
      return new IFNULL(paramInstructionHandle);
    case 199: 
      return new IFNONNULL(paramInstructionHandle);
    case 200: 
      return new GOTO_W(paramInstructionHandle);
    case 201: 
      return new JSR_W(paramInstructionHandle);
    }
    throw new RuntimeException("Invalid opcode: " + paramShort);
  }
  
  public void setClassGen(ClassGen paramClassGen)
  {
    cg = paramClassGen;
  }
  
  public ClassGen getClassGen()
  {
    return cg;
  }
  
  public void setConstantPool(ConstantPoolGen paramConstantPoolGen)
  {
    cp = paramConstantPoolGen;
  }
  
  public ConstantPoolGen getConstantPool()
  {
    return cp;
  }
  
  private static class MethodObject
  {
    Type[] arg_types;
    Type result_type;
    String[] arg_names;
    String class_name;
    String name;
    int access;
    
    MethodObject(String paramString1, String paramString2, Type paramType, Type[] paramArrayOfType, int paramInt)
    {
      class_name = paramString1;
      name = paramString2;
      result_type = paramType;
      arg_types = paramArrayOfType;
      access = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */