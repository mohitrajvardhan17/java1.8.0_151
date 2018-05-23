package sun.reflect;

import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

class AccessorGenerator
  implements ClassFileConstants
{
  static final Unsafe unsafe = ;
  protected static final short S0 = 0;
  protected static final short S1 = 1;
  protected static final short S2 = 2;
  protected static final short S3 = 3;
  protected static final short S4 = 4;
  protected static final short S5 = 5;
  protected static final short S6 = 6;
  protected ClassFileAssembler asm;
  protected int modifiers;
  protected short thisClass;
  protected short superClass;
  protected short targetClass;
  protected short throwableClass;
  protected short classCastClass;
  protected short nullPointerClass;
  protected short illegalArgumentClass;
  protected short invocationTargetClass;
  protected short initIdx;
  protected short initNameAndTypeIdx;
  protected short initStringNameAndTypeIdx;
  protected short nullPointerCtorIdx;
  protected short illegalArgumentCtorIdx;
  protected short illegalArgumentStringCtorIdx;
  protected short invocationTargetCtorIdx;
  protected short superCtorIdx;
  protected short objectClass;
  protected short toStringIdx;
  protected short codeIdx;
  protected short exceptionsIdx;
  protected short booleanIdx;
  protected short booleanCtorIdx;
  protected short booleanUnboxIdx;
  protected short byteIdx;
  protected short byteCtorIdx;
  protected short byteUnboxIdx;
  protected short characterIdx;
  protected short characterCtorIdx;
  protected short characterUnboxIdx;
  protected short doubleIdx;
  protected short doubleCtorIdx;
  protected short doubleUnboxIdx;
  protected short floatIdx;
  protected short floatCtorIdx;
  protected short floatUnboxIdx;
  protected short integerIdx;
  protected short integerCtorIdx;
  protected short integerUnboxIdx;
  protected short longIdx;
  protected short longCtorIdx;
  protected short longUnboxIdx;
  protected short shortIdx;
  protected short shortCtorIdx;
  protected short shortUnboxIdx;
  protected final short NUM_COMMON_CPOOL_ENTRIES = 30;
  protected final short NUM_BOXING_CPOOL_ENTRIES = 72;
  protected static final Class<?>[] primitiveTypes = { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
  private ClassFileAssembler illegalArgumentCodeBuffer;
  
  AccessorGenerator() {}
  
  protected void emitCommonConstantPoolEntries()
  {
    asm.emitConstantPoolUTF8("java/lang/Throwable");
    asm.emitConstantPoolClass(asm.cpi());
    throwableClass = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/ClassCastException");
    asm.emitConstantPoolClass(asm.cpi());
    classCastClass = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/NullPointerException");
    asm.emitConstantPoolClass(asm.cpi());
    nullPointerClass = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/IllegalArgumentException");
    asm.emitConstantPoolClass(asm.cpi());
    illegalArgumentClass = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/reflect/InvocationTargetException");
    asm.emitConstantPoolClass(asm.cpi());
    invocationTargetClass = asm.cpi();
    asm.emitConstantPoolUTF8("<init>");
    initIdx = asm.cpi();
    asm.emitConstantPoolUTF8("()V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    initNameAndTypeIdx = asm.cpi();
    asm.emitConstantPoolMethodref(nullPointerClass, initNameAndTypeIdx);
    nullPointerCtorIdx = asm.cpi();
    asm.emitConstantPoolMethodref(illegalArgumentClass, initNameAndTypeIdx);
    illegalArgumentCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(Ljava/lang/String;)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    initStringNameAndTypeIdx = asm.cpi();
    asm.emitConstantPoolMethodref(illegalArgumentClass, initStringNameAndTypeIdx);
    illegalArgumentStringCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(Ljava/lang/Throwable;)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(invocationTargetClass, asm.cpi());
    invocationTargetCtorIdx = asm.cpi();
    asm.emitConstantPoolMethodref(superClass, initNameAndTypeIdx);
    superCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Object");
    asm.emitConstantPoolClass(asm.cpi());
    objectClass = asm.cpi();
    asm.emitConstantPoolUTF8("toString");
    asm.emitConstantPoolUTF8("()Ljava/lang/String;");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(objectClass, asm.cpi());
    toStringIdx = asm.cpi();
    asm.emitConstantPoolUTF8("Code");
    codeIdx = asm.cpi();
    asm.emitConstantPoolUTF8("Exceptions");
    exceptionsIdx = asm.cpi();
  }
  
  protected void emitBoxingContantPoolEntries()
  {
    asm.emitConstantPoolUTF8("java/lang/Boolean");
    asm.emitConstantPoolClass(asm.cpi());
    booleanIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(Z)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    booleanCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("booleanValue");
    asm.emitConstantPoolUTF8("()Z");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    booleanUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Byte");
    asm.emitConstantPoolClass(asm.cpi());
    byteIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(B)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    byteCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("byteValue");
    asm.emitConstantPoolUTF8("()B");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    byteUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Character");
    asm.emitConstantPoolClass(asm.cpi());
    characterIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(C)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    characterCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("charValue");
    asm.emitConstantPoolUTF8("()C");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    characterUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Double");
    asm.emitConstantPoolClass(asm.cpi());
    doubleIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(D)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    doubleCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("doubleValue");
    asm.emitConstantPoolUTF8("()D");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    doubleUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Float");
    asm.emitConstantPoolClass(asm.cpi());
    floatIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(F)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    floatCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("floatValue");
    asm.emitConstantPoolUTF8("()F");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    floatUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Integer");
    asm.emitConstantPoolClass(asm.cpi());
    integerIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(I)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    integerCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("intValue");
    asm.emitConstantPoolUTF8("()I");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    integerUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Long");
    asm.emitConstantPoolClass(asm.cpi());
    longIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(J)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    longCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("longValue");
    asm.emitConstantPoolUTF8("()J");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    longUnboxIdx = asm.cpi();
    asm.emitConstantPoolUTF8("java/lang/Short");
    asm.emitConstantPoolClass(asm.cpi());
    shortIdx = asm.cpi();
    asm.emitConstantPoolUTF8("(S)V");
    asm.emitConstantPoolNameAndType(initIdx, asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)2), asm.cpi());
    shortCtorIdx = asm.cpi();
    asm.emitConstantPoolUTF8("shortValue");
    asm.emitConstantPoolUTF8("()S");
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    asm.emitConstantPoolMethodref(sub(asm.cpi(), (short)6), asm.cpi());
    shortUnboxIdx = asm.cpi();
  }
  
  protected static short add(short paramShort1, short paramShort2)
  {
    return (short)(paramShort1 + paramShort2);
  }
  
  protected static short sub(short paramShort1, short paramShort2)
  {
    return (short)(paramShort1 - paramShort2);
  }
  
  protected boolean isStatic()
  {
    return Modifier.isStatic(modifiers);
  }
  
  protected boolean isPrivate()
  {
    return Modifier.isPrivate(modifiers);
  }
  
  protected static String getClassName(Class<?> paramClass, boolean paramBoolean)
  {
    if (paramClass.isPrimitive())
    {
      if (paramClass == Boolean.TYPE) {
        return "Z";
      }
      if (paramClass == Byte.TYPE) {
        return "B";
      }
      if (paramClass == Character.TYPE) {
        return "C";
      }
      if (paramClass == Double.TYPE) {
        return "D";
      }
      if (paramClass == Float.TYPE) {
        return "F";
      }
      if (paramClass == Integer.TYPE) {
        return "I";
      }
      if (paramClass == Long.TYPE) {
        return "J";
      }
      if (paramClass == Short.TYPE) {
        return "S";
      }
      if (paramClass == Void.TYPE) {
        return "V";
      }
      throw new InternalError("Should have found primitive type");
    }
    if (paramClass.isArray()) {
      return "[" + getClassName(paramClass.getComponentType(), true);
    }
    if (paramBoolean) {
      return internalize("L" + paramClass.getName() + ";");
    }
    return internalize(paramClass.getName());
  }
  
  private static String internalize(String paramString)
  {
    return paramString.replace('.', '/');
  }
  
  protected void emitConstructor()
  {
    ClassFileAssembler localClassFileAssembler = new ClassFileAssembler();
    localClassFileAssembler.setMaxLocals(1);
    localClassFileAssembler.opc_aload_0();
    localClassFileAssembler.opc_invokespecial(superCtorIdx, 0, 0);
    localClassFileAssembler.opc_return();
    emitMethod(initIdx, localClassFileAssembler.getMaxLocals(), localClassFileAssembler, null, null);
  }
  
  protected void emitMethod(short paramShort, int paramInt, ClassFileAssembler paramClassFileAssembler1, ClassFileAssembler paramClassFileAssembler2, short[] paramArrayOfShort)
  {
    int i = paramClassFileAssembler1.getLength();
    int j = 0;
    if (paramClassFileAssembler2 != null)
    {
      j = paramClassFileAssembler2.getLength();
      if (j % 8 != 0) {
        throw new IllegalArgumentException("Illegal exception table");
      }
    }
    int k = 12 + i + j;
    j /= 8;
    asm.emitShort((short)1);
    asm.emitShort(paramShort);
    asm.emitShort(add(paramShort, (short)1));
    if (paramArrayOfShort == null) {
      asm.emitShort((short)1);
    } else {
      asm.emitShort((short)2);
    }
    asm.emitShort(codeIdx);
    asm.emitInt(k);
    asm.emitShort(paramClassFileAssembler1.getMaxStack());
    asm.emitShort((short)Math.max(paramInt, paramClassFileAssembler1.getMaxLocals()));
    asm.emitInt(i);
    asm.append(paramClassFileAssembler1);
    asm.emitShort((short)j);
    if (paramClassFileAssembler2 != null) {
      asm.append(paramClassFileAssembler2);
    }
    asm.emitShort((short)0);
    if (paramArrayOfShort != null)
    {
      asm.emitShort(exceptionsIdx);
      asm.emitInt(2 + 2 * paramArrayOfShort.length);
      asm.emitShort((short)paramArrayOfShort.length);
      for (int m = 0; m < paramArrayOfShort.length; m++) {
        asm.emitShort(paramArrayOfShort[m]);
      }
    }
  }
  
  protected short indexForPrimitiveType(Class<?> paramClass)
  {
    if (paramClass == Boolean.TYPE) {
      return booleanIdx;
    }
    if (paramClass == Byte.TYPE) {
      return byteIdx;
    }
    if (paramClass == Character.TYPE) {
      return characterIdx;
    }
    if (paramClass == Double.TYPE) {
      return doubleIdx;
    }
    if (paramClass == Float.TYPE) {
      return floatIdx;
    }
    if (paramClass == Integer.TYPE) {
      return integerIdx;
    }
    if (paramClass == Long.TYPE) {
      return longIdx;
    }
    if (paramClass == Short.TYPE) {
      return shortIdx;
    }
    throw new InternalError("Should have found primitive type");
  }
  
  protected short ctorIndexForPrimitiveType(Class<?> paramClass)
  {
    if (paramClass == Boolean.TYPE) {
      return booleanCtorIdx;
    }
    if (paramClass == Byte.TYPE) {
      return byteCtorIdx;
    }
    if (paramClass == Character.TYPE) {
      return characterCtorIdx;
    }
    if (paramClass == Double.TYPE) {
      return doubleCtorIdx;
    }
    if (paramClass == Float.TYPE) {
      return floatCtorIdx;
    }
    if (paramClass == Integer.TYPE) {
      return integerCtorIdx;
    }
    if (paramClass == Long.TYPE) {
      return longCtorIdx;
    }
    if (paramClass == Short.TYPE) {
      return shortCtorIdx;
    }
    throw new InternalError("Should have found primitive type");
  }
  
  protected static boolean canWidenTo(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (!paramClass1.isPrimitive()) {
      return false;
    }
    if (paramClass1 == Boolean.TYPE)
    {
      if (paramClass2 == Boolean.TYPE) {
        return true;
      }
    }
    else if (paramClass1 == Byte.TYPE)
    {
      if ((paramClass2 == Byte.TYPE) || (paramClass2 == Short.TYPE) || (paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE)) {
        return true;
      }
    }
    else if (paramClass1 == Short.TYPE)
    {
      if ((paramClass2 == Short.TYPE) || (paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE)) {
        return true;
      }
    }
    else if (paramClass1 == Character.TYPE)
    {
      if ((paramClass2 == Character.TYPE) || (paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE)) {
        return true;
      }
    }
    else if (paramClass1 == Integer.TYPE)
    {
      if ((paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE)) {
        return true;
      }
    }
    else if (paramClass1 == Long.TYPE)
    {
      if ((paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE)) {
        return true;
      }
    }
    else if (paramClass1 == Float.TYPE)
    {
      if ((paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE)) {
        return true;
      }
    }
    else if ((paramClass1 == Double.TYPE) && (paramClass2 == Double.TYPE)) {
      return true;
    }
    return false;
  }
  
  protected static void emitWideningBytecodeForPrimitiveConversion(ClassFileAssembler paramClassFileAssembler, Class<?> paramClass1, Class<?> paramClass2)
  {
    if ((paramClass1 == Byte.TYPE) || (paramClass1 == Short.TYPE) || (paramClass1 == Character.TYPE) || (paramClass1 == Integer.TYPE))
    {
      if (paramClass2 == Long.TYPE) {
        paramClassFileAssembler.opc_i2l();
      } else if (paramClass2 == Float.TYPE) {
        paramClassFileAssembler.opc_i2f();
      } else if (paramClass2 == Double.TYPE) {
        paramClassFileAssembler.opc_i2d();
      }
    }
    else if (paramClass1 == Long.TYPE)
    {
      if (paramClass2 == Float.TYPE) {
        paramClassFileAssembler.opc_l2f();
      } else if (paramClass2 == Double.TYPE) {
        paramClassFileAssembler.opc_l2d();
      }
    }
    else if ((paramClass1 == Float.TYPE) && (paramClass2 == Double.TYPE)) {
      paramClassFileAssembler.opc_f2d();
    }
  }
  
  protected short unboxingMethodForPrimitiveType(Class<?> paramClass)
  {
    if (paramClass == Boolean.TYPE) {
      return booleanUnboxIdx;
    }
    if (paramClass == Byte.TYPE) {
      return byteUnboxIdx;
    }
    if (paramClass == Character.TYPE) {
      return characterUnboxIdx;
    }
    if (paramClass == Short.TYPE) {
      return shortUnboxIdx;
    }
    if (paramClass == Integer.TYPE) {
      return integerUnboxIdx;
    }
    if (paramClass == Long.TYPE) {
      return longUnboxIdx;
    }
    if (paramClass == Float.TYPE) {
      return floatUnboxIdx;
    }
    if (paramClass == Double.TYPE) {
      return doubleUnboxIdx;
    }
    throw new InternalError("Illegal primitive type " + paramClass.getName());
  }
  
  protected static boolean isPrimitive(Class<?> paramClass)
  {
    return (paramClass.isPrimitive()) && (paramClass != Void.TYPE);
  }
  
  protected int typeSizeInStackSlots(Class<?> paramClass)
  {
    if (paramClass == Void.TYPE) {
      return 0;
    }
    if ((paramClass == Long.TYPE) || (paramClass == Double.TYPE)) {
      return 2;
    }
    return 1;
  }
  
  protected ClassFileAssembler illegalArgumentCodeBuffer()
  {
    if (illegalArgumentCodeBuffer == null)
    {
      illegalArgumentCodeBuffer = new ClassFileAssembler();
      illegalArgumentCodeBuffer.opc_new(illegalArgumentClass);
      illegalArgumentCodeBuffer.opc_dup();
      illegalArgumentCodeBuffer.opc_invokespecial(illegalArgumentCtorIdx, 0, 0);
      illegalArgumentCodeBuffer.opc_athrow();
    }
    return illegalArgumentCodeBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\AccessorGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */