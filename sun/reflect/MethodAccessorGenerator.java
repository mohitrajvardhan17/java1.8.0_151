package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;

class MethodAccessorGenerator
  extends AccessorGenerator
{
  private static final short NUM_BASE_CPOOL_ENTRIES = 12;
  private static final short NUM_METHODS = 2;
  private static final short NUM_SERIALIZATION_CPOOL_ENTRIES = 2;
  private static volatile int methodSymnum = 0;
  private static volatile int constructorSymnum = 0;
  private static volatile int serializationConstructorSymnum = 0;
  private Class<?> declaringClass;
  private Class<?>[] parameterTypes;
  private Class<?> returnType;
  private boolean isConstructor;
  private boolean forSerialization;
  private short targetMethodRef;
  private short invokeIdx;
  private short invokeDescriptorIdx;
  private short nonPrimitiveParametersBaseIdx;
  
  MethodAccessorGenerator() {}
  
  public MethodAccessor generateMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt)
  {
    return (MethodAccessor)generate(paramClass1, paramString, paramArrayOfClass1, paramClass2, paramArrayOfClass2, paramInt, false, false, null);
  }
  
  public ConstructorAccessor generateConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt)
  {
    return (ConstructorAccessor)generate(paramClass, "<init>", paramArrayOfClass1, Void.TYPE, paramArrayOfClass2, paramInt, true, false, null);
  }
  
  public SerializationConstructorAccessorImpl generateSerializationConstructor(Class<?> paramClass1, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt, Class<?> paramClass2)
  {
    return (SerializationConstructorAccessorImpl)generate(paramClass1, "<init>", paramArrayOfClass1, Void.TYPE, paramArrayOfClass2, paramInt, true, true, paramClass2);
  }
  
  private MagicAccessorImpl generate(final Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt, boolean paramBoolean1, boolean paramBoolean2, Class<?> paramClass3)
  {
    ByteVector localByteVector = ByteVectorFactory.create();
    asm = new ClassFileAssembler(localByteVector);
    declaringClass = paramClass1;
    parameterTypes = paramArrayOfClass1;
    returnType = paramClass2;
    modifiers = paramInt;
    isConstructor = paramBoolean1;
    forSerialization = paramBoolean2;
    asm.emitMagicAndVersion();
    short s1 = 42;
    boolean bool = usesPrimitiveTypes();
    if (bool) {
      s1 = (short)(s1 + 72);
    }
    if (paramBoolean2) {
      s1 = (short)(s1 + 2);
    }
    s1 = (short)(s1 + (short)(2 * numNonPrimitiveParameterTypes()));
    asm.emitShort(add(s1, (short)1));
    final String str = generateName(paramBoolean1, paramBoolean2);
    asm.emitConstantPoolUTF8(str);
    asm.emitConstantPoolClass(asm.cpi());
    thisClass = asm.cpi();
    if (paramBoolean1)
    {
      if (paramBoolean2) {
        asm.emitConstantPoolUTF8("sun/reflect/SerializationConstructorAccessorImpl");
      } else {
        asm.emitConstantPoolUTF8("sun/reflect/ConstructorAccessorImpl");
      }
    }
    else {
      asm.emitConstantPoolUTF8("sun/reflect/MethodAccessorImpl");
    }
    asm.emitConstantPoolClass(asm.cpi());
    superClass = asm.cpi();
    asm.emitConstantPoolUTF8(getClassName(paramClass1, false));
    asm.emitConstantPoolClass(asm.cpi());
    targetClass = asm.cpi();
    short s2 = 0;
    if (paramBoolean2)
    {
      asm.emitConstantPoolUTF8(getClassName(paramClass3, false));
      asm.emitConstantPoolClass(asm.cpi());
      s2 = asm.cpi();
    }
    asm.emitConstantPoolUTF8(paramString);
    asm.emitConstantPoolUTF8(buildInternalSignature());
    asm.emitConstantPoolNameAndType(sub(asm.cpi(), (short)1), asm.cpi());
    if (isInterface()) {
      asm.emitConstantPoolInterfaceMethodref(targetClass, asm.cpi());
    } else if (paramBoolean2) {
      asm.emitConstantPoolMethodref(s2, asm.cpi());
    } else {
      asm.emitConstantPoolMethodref(targetClass, asm.cpi());
    }
    targetMethodRef = asm.cpi();
    if (paramBoolean1) {
      asm.emitConstantPoolUTF8("newInstance");
    } else {
      asm.emitConstantPoolUTF8("invoke");
    }
    invokeIdx = asm.cpi();
    if (paramBoolean1) {
      asm.emitConstantPoolUTF8("([Ljava/lang/Object;)Ljava/lang/Object;");
    } else {
      asm.emitConstantPoolUTF8("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
    }
    invokeDescriptorIdx = asm.cpi();
    nonPrimitiveParametersBaseIdx = add(asm.cpi(), (short)2);
    for (int i = 0; i < paramArrayOfClass1.length; i++)
    {
      Class<?> localClass = paramArrayOfClass1[i];
      if (!isPrimitive(localClass))
      {
        asm.emitConstantPoolUTF8(getClassName(localClass, false));
        asm.emitConstantPoolClass(asm.cpi());
      }
    }
    emitCommonConstantPoolEntries();
    if (bool) {
      emitBoxingContantPoolEntries();
    }
    if (asm.cpi() != s1) {
      throw new InternalError("Adjust this code (cpi = " + asm.cpi() + ", numCPEntries = " + s1 + ")");
    }
    asm.emitShort((short)1);
    asm.emitShort(thisClass);
    asm.emitShort(superClass);
    asm.emitShort((short)0);
    asm.emitShort((short)0);
    asm.emitShort((short)2);
    emitConstructor();
    emitInvoke();
    asm.emitShort((short)0);
    localByteVector.trim();
    final byte[] arrayOfByte = localByteVector.getData();
    (MagicAccessorImpl)AccessController.doPrivileged(new PrivilegedAction()
    {
      public MagicAccessorImpl run()
      {
        try
        {
          return (MagicAccessorImpl)ClassDefiner.defineClass(str, arrayOfByte, 0, arrayOfByte.length, paramClass1.getClassLoader()).newInstance();
        }
        catch (InstantiationException|IllegalAccessException localInstantiationException)
        {
          throw new InternalError(localInstantiationException);
        }
      }
    });
  }
  
  private void emitInvoke()
  {
    if (parameterTypes.length > 65535) {
      throw new InternalError("Can't handle more than 65535 parameters");
    }
    ClassFileAssembler localClassFileAssembler = new ClassFileAssembler();
    if (isConstructor) {
      localClassFileAssembler.setMaxLocals(2);
    } else {
      localClassFileAssembler.setMaxLocals(3);
    }
    short s1 = 0;
    if (isConstructor)
    {
      localClassFileAssembler.opc_new(targetClass);
      localClassFileAssembler.opc_dup();
    }
    else
    {
      if (isPrimitive(returnType))
      {
        localClassFileAssembler.opc_new(indexForPrimitiveType(returnType));
        localClassFileAssembler.opc_dup();
      }
      if (!isStatic())
      {
        localClassFileAssembler.opc_aload_1();
        localLabel1 = new Label();
        localClassFileAssembler.opc_ifnonnull(localLabel1);
        localClassFileAssembler.opc_new(nullPointerClass);
        localClassFileAssembler.opc_dup();
        localClassFileAssembler.opc_invokespecial(nullPointerCtorIdx, 0, 0);
        localClassFileAssembler.opc_athrow();
        localLabel1.bind();
        s1 = localClassFileAssembler.getLength();
        localClassFileAssembler.opc_aload_1();
        localClassFileAssembler.opc_checkcast(targetClass);
      }
    }
    Label localLabel1 = new Label();
    if (parameterTypes.length == 0)
    {
      if (isConstructor) {
        localClassFileAssembler.opc_aload_1();
      } else {
        localClassFileAssembler.opc_aload_2();
      }
      localClassFileAssembler.opc_ifnull(localLabel1);
    }
    if (isConstructor) {
      localClassFileAssembler.opc_aload_1();
    } else {
      localClassFileAssembler.opc_aload_2();
    }
    localClassFileAssembler.opc_arraylength();
    localClassFileAssembler.opc_sipush((short)parameterTypes.length);
    localClassFileAssembler.opc_if_icmpeq(localLabel1);
    localClassFileAssembler.opc_new(illegalArgumentClass);
    localClassFileAssembler.opc_dup();
    localClassFileAssembler.opc_invokespecial(illegalArgumentCtorIdx, 0, 0);
    localClassFileAssembler.opc_athrow();
    localLabel1.bind();
    short s2 = nonPrimitiveParametersBaseIdx;
    Label localLabel2 = null;
    byte b = 1;
    for (int i = 0; i < parameterTypes.length; i++)
    {
      Class localClass = parameterTypes[i];
      b = (byte)(b + (byte)typeSizeInStackSlots(localClass));
      if (localLabel2 != null)
      {
        localLabel2.bind();
        localLabel2 = null;
      }
      if (isConstructor) {
        localClassFileAssembler.opc_aload_1();
      } else {
        localClassFileAssembler.opc_aload_2();
      }
      localClassFileAssembler.opc_sipush((short)i);
      localClassFileAssembler.opc_aaload();
      if (isPrimitive(localClass))
      {
        if (isConstructor) {
          localClassFileAssembler.opc_astore_2();
        } else {
          localClassFileAssembler.opc_astore_3();
        }
        Label localLabel3 = null;
        localLabel2 = new Label();
        for (j = 0; j < primitiveTypes.length; j++)
        {
          localObject = primitiveTypes[j];
          if (canWidenTo((Class)localObject, localClass))
          {
            if (localLabel3 != null) {
              localLabel3.bind();
            }
            if (isConstructor) {
              localClassFileAssembler.opc_aload_2();
            } else {
              localClassFileAssembler.opc_aload_3();
            }
            localClassFileAssembler.opc_instanceof(indexForPrimitiveType((Class)localObject));
            localLabel3 = new Label();
            localClassFileAssembler.opc_ifeq(localLabel3);
            if (isConstructor) {
              localClassFileAssembler.opc_aload_2();
            } else {
              localClassFileAssembler.opc_aload_3();
            }
            localClassFileAssembler.opc_checkcast(indexForPrimitiveType((Class)localObject));
            localClassFileAssembler.opc_invokevirtual(unboxingMethodForPrimitiveType((Class)localObject), 0, typeSizeInStackSlots((Class)localObject));
            emitWideningBytecodeForPrimitiveConversion(localClassFileAssembler, (Class)localObject, localClass);
            localClassFileAssembler.opc_goto(localLabel2);
          }
        }
        if (localLabel3 == null) {
          throw new InternalError("Must have found at least identity conversion");
        }
        localLabel3.bind();
        localClassFileAssembler.opc_new(illegalArgumentClass);
        localClassFileAssembler.opc_dup();
        localClassFileAssembler.opc_invokespecial(illegalArgumentCtorIdx, 0, 0);
        localClassFileAssembler.opc_athrow();
      }
      else
      {
        localClassFileAssembler.opc_checkcast(s2);
        s2 = add(s2, (short)2);
      }
    }
    if (localLabel2 != null) {
      localLabel2.bind();
    }
    i = localClassFileAssembler.getLength();
    if (isConstructor) {
      localClassFileAssembler.opc_invokespecial(targetMethodRef, b, 0);
    } else if (isStatic()) {
      localClassFileAssembler.opc_invokestatic(targetMethodRef, b, typeSizeInStackSlots(returnType));
    } else if (isInterface())
    {
      if (isPrivate()) {
        localClassFileAssembler.opc_invokespecial(targetMethodRef, b, 0);
      } else {
        localClassFileAssembler.opc_invokeinterface(targetMethodRef, b, b, typeSizeInStackSlots(returnType));
      }
    }
    else {
      localClassFileAssembler.opc_invokevirtual(targetMethodRef, b, typeSizeInStackSlots(returnType));
    }
    short s3 = localClassFileAssembler.getLength();
    if (!isConstructor) {
      if (isPrimitive(returnType)) {
        localClassFileAssembler.opc_invokespecial(ctorIndexForPrimitiveType(returnType), typeSizeInStackSlots(returnType), 0);
      } else if (returnType == Void.TYPE) {
        localClassFileAssembler.opc_aconst_null();
      }
    }
    localClassFileAssembler.opc_areturn();
    short s4 = localClassFileAssembler.getLength();
    localClassFileAssembler.setStack(1);
    localClassFileAssembler.opc_invokespecial(toStringIdx, 0, 1);
    localClassFileAssembler.opc_new(illegalArgumentClass);
    localClassFileAssembler.opc_dup_x1();
    localClassFileAssembler.opc_swap();
    localClassFileAssembler.opc_invokespecial(illegalArgumentStringCtorIdx, 1, 0);
    localClassFileAssembler.opc_athrow();
    int j = localClassFileAssembler.getLength();
    localClassFileAssembler.setStack(1);
    localClassFileAssembler.opc_new(invocationTargetClass);
    localClassFileAssembler.opc_dup_x1();
    localClassFileAssembler.opc_swap();
    localClassFileAssembler.opc_invokespecial(invocationTargetCtorIdx, 1, 0);
    localClassFileAssembler.opc_athrow();
    Object localObject = new ClassFileAssembler();
    ((ClassFileAssembler)localObject).emitShort(s1);
    ((ClassFileAssembler)localObject).emitShort(i);
    ((ClassFileAssembler)localObject).emitShort(s4);
    ((ClassFileAssembler)localObject).emitShort(classCastClass);
    ((ClassFileAssembler)localObject).emitShort(s1);
    ((ClassFileAssembler)localObject).emitShort(i);
    ((ClassFileAssembler)localObject).emitShort(s4);
    ((ClassFileAssembler)localObject).emitShort(nullPointerClass);
    ((ClassFileAssembler)localObject).emitShort(i);
    ((ClassFileAssembler)localObject).emitShort(s3);
    ((ClassFileAssembler)localObject).emitShort(j);
    ((ClassFileAssembler)localObject).emitShort(throwableClass);
    emitMethod(invokeIdx, localClassFileAssembler.getMaxLocals(), localClassFileAssembler, (ClassFileAssembler)localObject, new short[] { invocationTargetClass });
  }
  
  private boolean usesPrimitiveTypes()
  {
    if (returnType.isPrimitive()) {
      return true;
    }
    for (int i = 0; i < parameterTypes.length; i++) {
      if (parameterTypes[i].isPrimitive()) {
        return true;
      }
    }
    return false;
  }
  
  private int numNonPrimitiveParameterTypes()
  {
    int i = 0;
    for (int j = 0; j < parameterTypes.length; j++) {
      if (!parameterTypes[j].isPrimitive()) {
        i++;
      }
    }
    return i;
  }
  
  private boolean isInterface()
  {
    return declaringClass.isInterface();
  }
  
  private String buildInternalSignature()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    for (int i = 0; i < parameterTypes.length; i++) {
      localStringBuffer.append(getClassName(parameterTypes[i], true));
    }
    localStringBuffer.append(")");
    localStringBuffer.append(getClassName(returnType, true));
    return localStringBuffer.toString();
  }
  
  private static synchronized String generateName(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      if (paramBoolean2)
      {
        i = ++serializationConstructorSymnum;
        return "sun/reflect/GeneratedSerializationConstructorAccessor" + i;
      }
      i = ++constructorSymnum;
      return "sun/reflect/GeneratedConstructorAccessor" + i;
    }
    int i = ++methodSymnum;
    return "sun/reflect/GeneratedMethodAccessor" + i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\MethodAccessorGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */