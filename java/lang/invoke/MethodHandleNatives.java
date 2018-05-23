package java.lang.invoke;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;

class MethodHandleNatives
{
  static final boolean COUNT_GWT;
  
  private MethodHandleNatives() {}
  
  static native void init(MemberName paramMemberName, Object paramObject);
  
  static native void expand(MemberName paramMemberName);
  
  static native MemberName resolve(MemberName paramMemberName, Class<?> paramClass)
    throws LinkageError, ClassNotFoundException;
  
  static native int getMembers(Class<?> paramClass1, String paramString1, String paramString2, int paramInt1, Class<?> paramClass2, int paramInt2, MemberName[] paramArrayOfMemberName);
  
  static native long objectFieldOffset(MemberName paramMemberName);
  
  static native long staticFieldOffset(MemberName paramMemberName);
  
  static native Object staticFieldBase(MemberName paramMemberName);
  
  static native Object getMemberVMInfo(MemberName paramMemberName);
  
  static native int getConstant(int paramInt);
  
  static native void setCallSiteTargetNormal(CallSite paramCallSite, MethodHandle paramMethodHandle);
  
  static native void setCallSiteTargetVolatile(CallSite paramCallSite, MethodHandle paramMethodHandle);
  
  private static native void registerNatives();
  
  static boolean refKindIsValid(int paramInt)
  {
    return (paramInt > 0) && (paramInt < 10);
  }
  
  static boolean refKindIsField(byte paramByte)
  {
    assert (refKindIsValid(paramByte));
    return paramByte <= 4;
  }
  
  static boolean refKindIsGetter(byte paramByte)
  {
    assert (refKindIsValid(paramByte));
    return paramByte <= 2;
  }
  
  static boolean refKindIsSetter(byte paramByte)
  {
    return (refKindIsField(paramByte)) && (!refKindIsGetter(paramByte));
  }
  
  static boolean refKindIsMethod(byte paramByte)
  {
    return (!refKindIsField(paramByte)) && (paramByte != 8);
  }
  
  static boolean refKindIsConstructor(byte paramByte)
  {
    return paramByte == 8;
  }
  
  static boolean refKindHasReceiver(byte paramByte)
  {
    assert (refKindIsValid(paramByte));
    return (paramByte & 0x1) != 0;
  }
  
  static boolean refKindIsStatic(byte paramByte)
  {
    return (!refKindHasReceiver(paramByte)) && (paramByte != 8);
  }
  
  static boolean refKindDoesDispatch(byte paramByte)
  {
    assert (refKindIsValid(paramByte));
    return (paramByte == 5) || (paramByte == 9);
  }
  
  static String refKindName(byte paramByte)
  {
    assert (refKindIsValid(paramByte));
    switch (paramByte)
    {
    case 1: 
      return "getField";
    case 2: 
      return "getStatic";
    case 3: 
      return "putField";
    case 4: 
      return "putStatic";
    case 5: 
      return "invokeVirtual";
    case 6: 
      return "invokeStatic";
    case 7: 
      return "invokeSpecial";
    case 8: 
      return "newInvokeSpecial";
    case 9: 
      return "invokeInterface";
    }
    return "REF_???";
  }
  
  private static native int getNamedCon(int paramInt, Object[] paramArrayOfObject);
  
  static boolean verifyConstants()
  {
    Object[] arrayOfObject = { null };
    int i = 0;
    for (;;)
    {
      arrayOfObject[0] = null;
      int j = getNamedCon(i, arrayOfObject);
      if (arrayOfObject[0] == null) {
        break;
      }
      String str1 = (String)arrayOfObject[0];
      try
      {
        Field localField = Constants.class.getDeclaredField(str1);
        int k = localField.getInt(null);
        if (k != j)
        {
          String str3 = str1 + ": JVM has " + j + " while Java has " + k;
          if (str1.equals("CONV_OP_LIMIT")) {
            System.err.println("warning: " + str3);
          } else {
            throw new InternalError(str3);
          }
        }
      }
      catch (NoSuchFieldException|IllegalAccessException localNoSuchFieldException)
      {
        String str2 = str1 + ": JVM has " + j + " which Java does not define";
        i++;
      }
    }
    return true;
  }
  
  static MemberName linkCallSite(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object[] paramArrayOfObject)
  {
    MethodHandle localMethodHandle = (MethodHandle)paramObject2;
    Class localClass = (Class)paramObject1;
    String str = paramObject3.toString().intern();
    MethodType localMethodType = (MethodType)paramObject4;
    if (!MethodHandleStatics.TRACE_METHOD_LINKAGE) {
      return linkCallSiteImpl(localClass, localMethodHandle, str, localMethodType, paramObject5, paramArrayOfObject);
    }
    return linkCallSiteTracing(localClass, localMethodHandle, str, localMethodType, paramObject5, paramArrayOfObject);
  }
  
  static MemberName linkCallSiteImpl(Class<?> paramClass, MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, Object[] paramArrayOfObject)
  {
    CallSite localCallSite = CallSite.makeSite(paramMethodHandle, paramString, paramMethodType, paramObject, paramClass);
    if ((localCallSite instanceof ConstantCallSite))
    {
      paramArrayOfObject[0] = localCallSite.dynamicInvoker();
      return Invokers.linkToTargetMethod(paramMethodType);
    }
    paramArrayOfObject[0] = localCallSite;
    return Invokers.linkToCallSiteMethod(paramMethodType);
  }
  
  static MemberName linkCallSiteTracing(Class<?> paramClass, MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, Object[] paramArrayOfObject)
  {
    Object localObject1 = paramMethodHandle.internalMemberName();
    if (localObject1 == null) {
      localObject1 = paramMethodHandle;
    }
    Object localObject2 = (paramObject instanceof Object[]) ? Arrays.asList((Object[])paramObject) : paramObject;
    System.out.println("linkCallSite " + paramClass.getName() + " " + localObject1 + " " + paramString + paramMethodType + "/" + localObject2);
    try
    {
      MemberName localMemberName = linkCallSiteImpl(paramClass, paramMethodHandle, paramString, paramMethodType, paramObject, paramArrayOfObject);
      System.out.println("linkCallSite => " + localMemberName + " + " + paramArrayOfObject[0]);
      return localMemberName;
    }
    catch (Throwable localThrowable)
    {
      System.out.println("linkCallSite => throw " + localThrowable);
      throw localThrowable;
    }
  }
  
  static MethodType findMethodHandleType(Class<?> paramClass, Class<?>[] paramArrayOfClass)
  {
    return MethodType.makeImpl(paramClass, paramArrayOfClass, true);
  }
  
  static MemberName linkMethod(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject, Object[] paramArrayOfObject)
  {
    if (!MethodHandleStatics.TRACE_METHOD_LINKAGE) {
      return linkMethodImpl(paramClass1, paramInt, paramClass2, paramString, paramObject, paramArrayOfObject);
    }
    return linkMethodTracing(paramClass1, paramInt, paramClass2, paramString, paramObject, paramArrayOfObject);
  }
  
  static MemberName linkMethodImpl(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject, Object[] paramArrayOfObject)
  {
    try
    {
      if ((paramClass2 == MethodHandle.class) && (paramInt == 5)) {
        return Invokers.methodHandleInvokeLinkerMethod(paramString, fixMethodType(paramClass1, paramObject), paramArrayOfObject);
      }
    }
    catch (Throwable localThrowable)
    {
      if ((localThrowable instanceof LinkageError)) {
        throw ((LinkageError)localThrowable);
      }
      throw new LinkageError(localThrowable.getMessage(), localThrowable);
    }
    throw new LinkageError("no such method " + paramClass2.getName() + "." + paramString + paramObject);
  }
  
  private static MethodType fixMethodType(Class<?> paramClass, Object paramObject)
  {
    if ((paramObject instanceof MethodType)) {
      return (MethodType)paramObject;
    }
    return MethodType.fromMethodDescriptorString((String)paramObject, paramClass.getClassLoader());
  }
  
  static MemberName linkMethodTracing(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject, Object[] paramArrayOfObject)
  {
    System.out.println("linkMethod " + paramClass2.getName() + "." + paramString + paramObject + "/" + Integer.toHexString(paramInt));
    try
    {
      MemberName localMemberName = linkMethodImpl(paramClass1, paramInt, paramClass2, paramString, paramObject, paramArrayOfObject);
      System.out.println("linkMethod => " + localMemberName + " + " + paramArrayOfObject[0]);
      return localMemberName;
    }
    catch (Throwable localThrowable)
    {
      System.out.println("linkMethod => throw " + localThrowable);
      throw localThrowable;
    }
  }
  
  static MethodHandle linkMethodHandleConstant(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject)
  {
    try
    {
      MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP.in(paramClass1);
      assert (refKindIsValid(paramInt));
      return localLookup.linkMethodHandleConstant((byte)paramInt, paramClass2, paramString, paramObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localObject = localIllegalAccessException.getCause();
      if ((localObject instanceof AbstractMethodError)) {
        throw ((AbstractMethodError)localObject);
      }
      IllegalAccessError localIllegalAccessError = new IllegalAccessError(localIllegalAccessException.getMessage());
      throw initCauseFrom(localIllegalAccessError, localIllegalAccessException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localObject = new NoSuchMethodError(localNoSuchMethodException.getMessage());
      throw initCauseFrom((Error)localObject, localNoSuchMethodException);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      localObject = new NoSuchFieldError(localNoSuchFieldException.getMessage());
      throw initCauseFrom((Error)localObject, localNoSuchFieldException);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      Object localObject = new IncompatibleClassChangeError();
      throw initCauseFrom((Error)localObject, localReflectiveOperationException);
    }
  }
  
  private static Error initCauseFrom(Error paramError, Exception paramException)
  {
    Throwable localThrowable = paramException.getCause();
    if (paramError.getClass().isInstance(localThrowable)) {
      return (Error)localThrowable;
    }
    paramError.initCause(localThrowable == null ? paramException : localThrowable);
    return paramError;
  }
  
  static boolean isCallerSensitive(MemberName paramMemberName)
  {
    if (!paramMemberName.isInvocable()) {
      return false;
    }
    return (paramMemberName.isCallerSensitive()) || (canBeCalledVirtual(paramMemberName));
  }
  
  static boolean canBeCalledVirtual(MemberName paramMemberName)
  {
    assert (paramMemberName.isInvocable());
    Class localClass = paramMemberName.getDeclaringClass();
    switch (paramMemberName.getName())
    {
    case "checkMemberAccess": 
      return canBeCalledVirtual(paramMemberName, SecurityManager.class);
    case "getContextClassLoader": 
      return canBeCalledVirtual(paramMemberName, Thread.class);
    }
    return false;
  }
  
  static boolean canBeCalledVirtual(MemberName paramMemberName, Class<?> paramClass)
  {
    Class localClass = paramMemberName.getDeclaringClass();
    if (localClass == paramClass) {
      return true;
    }
    if ((paramMemberName.isStatic()) || (paramMemberName.isPrivate())) {
      return false;
    }
    return (paramClass.isAssignableFrom(localClass)) || (localClass.isInterface());
  }
  
  static
  {
    registerNatives();
    COUNT_GWT = getConstant(4) != 0;
    MethodHandleImpl.initStatics();
    for (byte b = 1; b < 10; b = (byte)(b + 1)) {
      if (!$assertionsDisabled) {
        if (refKindHasReceiver(b) != ((1 << b & 0x2AA) != 0)) {
          throw new AssertionError(b);
        }
      }
    }
    assert (verifyConstants());
  }
  
  static class Constants
  {
    static final int GC_COUNT_GWT = 4;
    static final int GC_LAMBDA_SUPPORT = 5;
    static final int MN_IS_METHOD = 65536;
    static final int MN_IS_CONSTRUCTOR = 131072;
    static final int MN_IS_FIELD = 262144;
    static final int MN_IS_TYPE = 524288;
    static final int MN_CALLER_SENSITIVE = 1048576;
    static final int MN_REFERENCE_KIND_SHIFT = 24;
    static final int MN_REFERENCE_KIND_MASK = 15;
    static final int MN_SEARCH_SUPERCLASSES = 1048576;
    static final int MN_SEARCH_INTERFACES = 2097152;
    static final int T_BOOLEAN = 4;
    static final int T_CHAR = 5;
    static final int T_FLOAT = 6;
    static final int T_DOUBLE = 7;
    static final int T_BYTE = 8;
    static final int T_SHORT = 9;
    static final int T_INT = 10;
    static final int T_LONG = 11;
    static final int T_OBJECT = 12;
    static final int T_VOID = 14;
    static final int T_ILLEGAL = 99;
    static final byte CONSTANT_Utf8 = 1;
    static final byte CONSTANT_Integer = 3;
    static final byte CONSTANT_Float = 4;
    static final byte CONSTANT_Long = 5;
    static final byte CONSTANT_Double = 6;
    static final byte CONSTANT_Class = 7;
    static final byte CONSTANT_String = 8;
    static final byte CONSTANT_Fieldref = 9;
    static final byte CONSTANT_Methodref = 10;
    static final byte CONSTANT_InterfaceMethodref = 11;
    static final byte CONSTANT_NameAndType = 12;
    static final byte CONSTANT_MethodHandle = 15;
    static final byte CONSTANT_MethodType = 16;
    static final byte CONSTANT_InvokeDynamic = 18;
    static final byte CONSTANT_LIMIT = 19;
    static final char ACC_PUBLIC = '\001';
    static final char ACC_PRIVATE = '\002';
    static final char ACC_PROTECTED = '\004';
    static final char ACC_STATIC = '\b';
    static final char ACC_FINAL = '\020';
    static final char ACC_SYNCHRONIZED = ' ';
    static final char ACC_VOLATILE = '@';
    static final char ACC_TRANSIENT = '';
    static final char ACC_NATIVE = 'Ā';
    static final char ACC_INTERFACE = 'Ȁ';
    static final char ACC_ABSTRACT = 'Ѐ';
    static final char ACC_STRICT = 'ࠀ';
    static final char ACC_SYNTHETIC = 'က';
    static final char ACC_ANNOTATION = ' ';
    static final char ACC_ENUM = '䀀';
    static final char ACC_SUPER = ' ';
    static final char ACC_BRIDGE = '@';
    static final char ACC_VARARGS = '';
    static final byte REF_NONE = 0;
    static final byte REF_getField = 1;
    static final byte REF_getStatic = 2;
    static final byte REF_putField = 3;
    static final byte REF_putStatic = 4;
    static final byte REF_invokeVirtual = 5;
    static final byte REF_invokeStatic = 6;
    static final byte REF_invokeSpecial = 7;
    static final byte REF_newInvokeSpecial = 8;
    static final byte REF_invokeInterface = 9;
    static final byte REF_LIMIT = 10;
    
    Constants() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\MethodHandleNatives.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */