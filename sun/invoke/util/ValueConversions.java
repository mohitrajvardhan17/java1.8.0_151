package sun.invoke.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.EnumMap;

public class ValueConversions
{
  private static final Class<?> THIS_CLASS;
  private static final MethodHandles.Lookup IMPL_LOOKUP;
  private static final WrapperCache[] UNBOX_CONVERSIONS;
  private static final Integer ZERO_INT;
  private static final Integer ONE_INT;
  private static final WrapperCache[] BOX_CONVERSIONS;
  private static final WrapperCache[] CONSTANT_FUNCTIONS;
  private static final MethodHandle CAST_REFERENCE;
  private static final MethodHandle IGNORE;
  private static final MethodHandle EMPTY;
  private static final WrapperCache[] CONVERT_PRIMITIVE_FUNCTIONS = newWrapperCaches(Wrapper.values().length);
  
  public ValueConversions() {}
  
  private static WrapperCache[] newWrapperCaches(int paramInt)
  {
    WrapperCache[] arrayOfWrapperCache = new WrapperCache[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfWrapperCache[i] = new WrapperCache(null);
    }
    return arrayOfWrapperCache;
  }
  
  static int unboxInteger(Integer paramInteger)
  {
    return paramInteger.intValue();
  }
  
  static int unboxInteger(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Integer)) {
      return ((Integer)paramObject).intValue();
    }
    return primitiveConversion(Wrapper.INT, paramObject, paramBoolean).intValue();
  }
  
  static byte unboxByte(Byte paramByte)
  {
    return paramByte.byteValue();
  }
  
  static byte unboxByte(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Byte)) {
      return ((Byte)paramObject).byteValue();
    }
    return primitiveConversion(Wrapper.BYTE, paramObject, paramBoolean).byteValue();
  }
  
  static short unboxShort(Short paramShort)
  {
    return paramShort.shortValue();
  }
  
  static short unboxShort(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Short)) {
      return ((Short)paramObject).shortValue();
    }
    return primitiveConversion(Wrapper.SHORT, paramObject, paramBoolean).shortValue();
  }
  
  static boolean unboxBoolean(Boolean paramBoolean)
  {
    return paramBoolean.booleanValue();
  }
  
  static boolean unboxBoolean(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Boolean)) {
      return ((Boolean)paramObject).booleanValue();
    }
    return (primitiveConversion(Wrapper.BOOLEAN, paramObject, paramBoolean).intValue() & 0x1) != 0;
  }
  
  static char unboxCharacter(Character paramCharacter)
  {
    return paramCharacter.charValue();
  }
  
  static char unboxCharacter(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Character)) {
      return ((Character)paramObject).charValue();
    }
    return (char)primitiveConversion(Wrapper.CHAR, paramObject, paramBoolean).intValue();
  }
  
  static long unboxLong(Long paramLong)
  {
    return paramLong.longValue();
  }
  
  static long unboxLong(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Long)) {
      return ((Long)paramObject).longValue();
    }
    return primitiveConversion(Wrapper.LONG, paramObject, paramBoolean).longValue();
  }
  
  static float unboxFloat(Float paramFloat)
  {
    return paramFloat.floatValue();
  }
  
  static float unboxFloat(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Float)) {
      return ((Float)paramObject).floatValue();
    }
    return primitiveConversion(Wrapper.FLOAT, paramObject, paramBoolean).floatValue();
  }
  
  static double unboxDouble(Double paramDouble)
  {
    return paramDouble.doubleValue();
  }
  
  static double unboxDouble(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof Double)) {
      return ((Double)paramObject).doubleValue();
    }
    return primitiveConversion(Wrapper.DOUBLE, paramObject, paramBoolean).doubleValue();
  }
  
  private static MethodType unboxType(Wrapper paramWrapper, int paramInt)
  {
    if (paramInt == 0) {
      return MethodType.methodType(paramWrapper.primitiveType(), paramWrapper.wrapperType());
    }
    return MethodType.methodType(paramWrapper.primitiveType(), Object.class, new Class[] { Boolean.TYPE });
  }
  
  private static MethodHandle unbox(Wrapper paramWrapper, int paramInt)
  {
    WrapperCache localWrapperCache = UNBOX_CONVERSIONS[paramInt];
    MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    switch (paramWrapper)
    {
    case OBJECT: 
    case VOID: 
      throw new IllegalArgumentException("unbox " + paramWrapper);
    }
    String str = "unbox" + paramWrapper.wrapperSimpleName();
    MethodType localMethodType = unboxType(paramWrapper, paramInt);
    try
    {
      localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      localMethodHandle = null;
    }
    if (localMethodHandle != null)
    {
      if (paramInt > 0)
      {
        boolean bool = paramInt != 2;
        localMethodHandle = MethodHandles.insertArguments(localMethodHandle, 1, new Object[] { Boolean.valueOf(bool) });
      }
      if (paramInt == 1) {
        localMethodHandle = localMethodHandle.asType(unboxType(paramWrapper, 0));
      }
      return localWrapperCache.put(paramWrapper, localMethodHandle);
    }
    throw new IllegalArgumentException("cannot find unbox adapter for " + paramWrapper + (paramInt == 3 ? " (cast)" : paramInt <= 1 ? " (exact)" : ""));
  }
  
  public static MethodHandle unboxExact(Wrapper paramWrapper)
  {
    return unbox(paramWrapper, 0);
  }
  
  public static MethodHandle unboxExact(Wrapper paramWrapper, boolean paramBoolean)
  {
    return unbox(paramWrapper, paramBoolean ? 0 : 1);
  }
  
  public static MethodHandle unboxWiden(Wrapper paramWrapper)
  {
    return unbox(paramWrapper, 2);
  }
  
  public static MethodHandle unboxCast(Wrapper paramWrapper)
  {
    return unbox(paramWrapper, 3);
  }
  
  public static Number primitiveConversion(Wrapper paramWrapper, Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null)
    {
      if (!paramBoolean) {
        return null;
      }
      return ZERO_INT;
    }
    Object localObject;
    if ((paramObject instanceof Number)) {
      localObject = (Number)paramObject;
    } else if ((paramObject instanceof Boolean)) {
      localObject = ((Boolean)paramObject).booleanValue() ? ONE_INT : ZERO_INT;
    } else if ((paramObject instanceof Character)) {
      localObject = Integer.valueOf(((Character)paramObject).charValue());
    } else {
      localObject = (Number)paramObject;
    }
    Wrapper localWrapper = Wrapper.findWrapperType(paramObject.getClass());
    if ((localWrapper == null) || ((!paramBoolean) && (!paramWrapper.isConvertibleFrom(localWrapper)))) {
      return (Number)paramWrapper.wrapperType().cast(paramObject);
    }
    return (Number)localObject;
  }
  
  public static int widenSubword(Object paramObject)
  {
    if ((paramObject instanceof Integer)) {
      return ((Integer)paramObject).intValue();
    }
    if ((paramObject instanceof Boolean)) {
      return fromBoolean(((Boolean)paramObject).booleanValue());
    }
    if ((paramObject instanceof Character)) {
      return ((Character)paramObject).charValue();
    }
    if ((paramObject instanceof Short)) {
      return ((Short)paramObject).shortValue();
    }
    if ((paramObject instanceof Byte)) {
      return ((Byte)paramObject).byteValue();
    }
    return ((Integer)paramObject).intValue();
  }
  
  static Integer boxInteger(int paramInt)
  {
    return Integer.valueOf(paramInt);
  }
  
  static Byte boxByte(byte paramByte)
  {
    return Byte.valueOf(paramByte);
  }
  
  static Short boxShort(short paramShort)
  {
    return Short.valueOf(paramShort);
  }
  
  static Boolean boxBoolean(boolean paramBoolean)
  {
    return Boolean.valueOf(paramBoolean);
  }
  
  static Character boxCharacter(char paramChar)
  {
    return Character.valueOf(paramChar);
  }
  
  static Long boxLong(long paramLong)
  {
    return Long.valueOf(paramLong);
  }
  
  static Float boxFloat(float paramFloat)
  {
    return Float.valueOf(paramFloat);
  }
  
  static Double boxDouble(double paramDouble)
  {
    return Double.valueOf(paramDouble);
  }
  
  private static MethodType boxType(Wrapper paramWrapper)
  {
    Class localClass = paramWrapper.wrapperType();
    return MethodType.methodType(localClass, paramWrapper.primitiveType());
  }
  
  public static MethodHandle boxExact(Wrapper paramWrapper)
  {
    WrapperCache localWrapperCache = BOX_CONVERSIONS[0];
    MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    String str = "box" + paramWrapper.wrapperSimpleName();
    MethodType localMethodType = boxType(paramWrapper);
    try
    {
      localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      localMethodHandle = null;
    }
    if (localMethodHandle != null) {
      return localWrapperCache.put(paramWrapper, localMethodHandle);
    }
    throw new IllegalArgumentException("cannot find box adapter for " + paramWrapper);
  }
  
  static void ignore(Object paramObject) {}
  
  static void empty() {}
  
  static Object zeroObject()
  {
    return null;
  }
  
  static int zeroInteger()
  {
    return 0;
  }
  
  static long zeroLong()
  {
    return 0L;
  }
  
  static float zeroFloat()
  {
    return 0.0F;
  }
  
  static double zeroDouble()
  {
    return 0.0D;
  }
  
  public static MethodHandle zeroConstantFunction(Wrapper paramWrapper)
  {
    WrapperCache localWrapperCache = CONSTANT_FUNCTIONS[0];
    MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    MethodType localMethodType = MethodType.methodType(paramWrapper.primitiveType());
    switch (paramWrapper)
    {
    case VOID: 
      localMethodHandle = EMPTY;
      break;
    case OBJECT: 
    case INT: 
    case LONG: 
    case FLOAT: 
    case DOUBLE: 
      try
      {
        localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, "zero" + paramWrapper.wrapperSimpleName(), localMethodType);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        localMethodHandle = null;
      }
    }
    if (localMethodHandle != null) {
      return localWrapperCache.put(paramWrapper, localMethodHandle);
    }
    if ((paramWrapper.isSubwordOrInt()) && (paramWrapper != Wrapper.INT))
    {
      localMethodHandle = MethodHandles.explicitCastArguments(zeroConstantFunction(Wrapper.INT), localMethodType);
      return localWrapperCache.put(paramWrapper, localMethodHandle);
    }
    throw new IllegalArgumentException("cannot find zero constant for " + paramWrapper);
  }
  
  public static MethodHandle ignore()
  {
    return IGNORE;
  }
  
  public static MethodHandle cast()
  {
    return CAST_REFERENCE;
  }
  
  static float doubleToFloat(double paramDouble)
  {
    return (float)paramDouble;
  }
  
  static long doubleToLong(double paramDouble)
  {
    return paramDouble;
  }
  
  static int doubleToInt(double paramDouble)
  {
    return (int)paramDouble;
  }
  
  static short doubleToShort(double paramDouble)
  {
    return (short)(int)paramDouble;
  }
  
  static char doubleToChar(double paramDouble)
  {
    return (char)(int)paramDouble;
  }
  
  static byte doubleToByte(double paramDouble)
  {
    return (byte)(int)paramDouble;
  }
  
  static boolean doubleToBoolean(double paramDouble)
  {
    return toBoolean((byte)(int)paramDouble);
  }
  
  static double floatToDouble(float paramFloat)
  {
    return paramFloat;
  }
  
  static long floatToLong(float paramFloat)
  {
    return paramFloat;
  }
  
  static int floatToInt(float paramFloat)
  {
    return (int)paramFloat;
  }
  
  static short floatToShort(float paramFloat)
  {
    return (short)(int)paramFloat;
  }
  
  static char floatToChar(float paramFloat)
  {
    return (char)(int)paramFloat;
  }
  
  static byte floatToByte(float paramFloat)
  {
    return (byte)(int)paramFloat;
  }
  
  static boolean floatToBoolean(float paramFloat)
  {
    return toBoolean((byte)(int)paramFloat);
  }
  
  static double longToDouble(long paramLong)
  {
    return paramLong;
  }
  
  static float longToFloat(long paramLong)
  {
    return (float)paramLong;
  }
  
  static int longToInt(long paramLong)
  {
    return (int)paramLong;
  }
  
  static short longToShort(long paramLong)
  {
    return (short)(int)paramLong;
  }
  
  static char longToChar(long paramLong)
  {
    return (char)(int)paramLong;
  }
  
  static byte longToByte(long paramLong)
  {
    return (byte)(int)paramLong;
  }
  
  static boolean longToBoolean(long paramLong)
  {
    return toBoolean((byte)(int)paramLong);
  }
  
  static double intToDouble(int paramInt)
  {
    return paramInt;
  }
  
  static float intToFloat(int paramInt)
  {
    return paramInt;
  }
  
  static long intToLong(int paramInt)
  {
    return paramInt;
  }
  
  static short intToShort(int paramInt)
  {
    return (short)paramInt;
  }
  
  static char intToChar(int paramInt)
  {
    return (char)paramInt;
  }
  
  static byte intToByte(int paramInt)
  {
    return (byte)paramInt;
  }
  
  static boolean intToBoolean(int paramInt)
  {
    return toBoolean((byte)paramInt);
  }
  
  static double shortToDouble(short paramShort)
  {
    return paramShort;
  }
  
  static float shortToFloat(short paramShort)
  {
    return paramShort;
  }
  
  static long shortToLong(short paramShort)
  {
    return paramShort;
  }
  
  static int shortToInt(short paramShort)
  {
    return paramShort;
  }
  
  static char shortToChar(short paramShort)
  {
    return (char)paramShort;
  }
  
  static byte shortToByte(short paramShort)
  {
    return (byte)paramShort;
  }
  
  static boolean shortToBoolean(short paramShort)
  {
    return toBoolean((byte)paramShort);
  }
  
  static double charToDouble(char paramChar)
  {
    return paramChar;
  }
  
  static float charToFloat(char paramChar)
  {
    return paramChar;
  }
  
  static long charToLong(char paramChar)
  {
    return paramChar;
  }
  
  static int charToInt(char paramChar)
  {
    return paramChar;
  }
  
  static short charToShort(char paramChar)
  {
    return (short)paramChar;
  }
  
  static byte charToByte(char paramChar)
  {
    return (byte)paramChar;
  }
  
  static boolean charToBoolean(char paramChar)
  {
    return toBoolean((byte)paramChar);
  }
  
  static double byteToDouble(byte paramByte)
  {
    return paramByte;
  }
  
  static float byteToFloat(byte paramByte)
  {
    return paramByte;
  }
  
  static long byteToLong(byte paramByte)
  {
    return paramByte;
  }
  
  static int byteToInt(byte paramByte)
  {
    return paramByte;
  }
  
  static short byteToShort(byte paramByte)
  {
    return (short)paramByte;
  }
  
  static char byteToChar(byte paramByte)
  {
    return (char)paramByte;
  }
  
  static boolean byteToBoolean(byte paramByte)
  {
    return toBoolean(paramByte);
  }
  
  static double booleanToDouble(boolean paramBoolean)
  {
    return fromBoolean(paramBoolean);
  }
  
  static float booleanToFloat(boolean paramBoolean)
  {
    return fromBoolean(paramBoolean);
  }
  
  static long booleanToLong(boolean paramBoolean)
  {
    return fromBoolean(paramBoolean);
  }
  
  static int booleanToInt(boolean paramBoolean)
  {
    return fromBoolean(paramBoolean);
  }
  
  static short booleanToShort(boolean paramBoolean)
  {
    return (short)fromBoolean(paramBoolean);
  }
  
  static char booleanToChar(boolean paramBoolean)
  {
    return (char)fromBoolean(paramBoolean);
  }
  
  static byte booleanToByte(boolean paramBoolean)
  {
    return fromBoolean(paramBoolean);
  }
  
  static boolean toBoolean(byte paramByte)
  {
    return (paramByte & 0x1) != 0;
  }
  
  static byte fromBoolean(boolean paramBoolean)
  {
    return paramBoolean ? 1 : 0;
  }
  
  public static MethodHandle convertPrimitive(Wrapper paramWrapper1, Wrapper paramWrapper2)
  {
    WrapperCache localWrapperCache = CONVERT_PRIMITIVE_FUNCTIONS[paramWrapper1.ordinal()];
    MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper2);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    Class localClass1 = paramWrapper1.primitiveType();
    Class localClass2 = paramWrapper2.primitiveType();
    MethodType localMethodType = MethodType.methodType(localClass2, localClass1);
    if (paramWrapper1 == paramWrapper2)
    {
      localMethodHandle = MethodHandles.identity(localClass1);
    }
    else
    {
      assert ((localClass1.isPrimitive()) && (localClass2.isPrimitive()));
      try
      {
        localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, localClass1.getSimpleName() + "To" + capitalize(localClass2.getSimpleName()), localMethodType);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        localMethodHandle = null;
      }
    }
    if (localMethodHandle != null)
    {
      assert (localMethodHandle.type() == localMethodType) : localMethodHandle;
      return localWrapperCache.put(paramWrapper2, localMethodHandle);
    }
    throw new IllegalArgumentException("cannot find primitive conversion function for " + localClass1.getSimpleName() + " -> " + localClass2.getSimpleName());
  }
  
  public static MethodHandle convertPrimitive(Class<?> paramClass1, Class<?> paramClass2)
  {
    return convertPrimitive(Wrapper.forPrimitiveType(paramClass1), Wrapper.forPrimitiveType(paramClass2));
  }
  
  private static String capitalize(String paramString)
  {
    return Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1);
  }
  
  private static InternalError newInternalError(String paramString, Throwable paramThrowable)
  {
    return new InternalError(paramString, paramThrowable);
  }
  
  private static InternalError newInternalError(Throwable paramThrowable)
  {
    return new InternalError(paramThrowable);
  }
  
  static
  {
    THIS_CLASS = ValueConversions.class;
    IMPL_LOOKUP = MethodHandles.lookup();
    UNBOX_CONVERSIONS = newWrapperCaches(4);
    ZERO_INT = Integer.valueOf(0);
    ONE_INT = Integer.valueOf(1);
    BOX_CONVERSIONS = newWrapperCaches(1);
    CONSTANT_FUNCTIONS = newWrapperCaches(2);
    try
    {
      MethodType localMethodType1 = MethodType.genericMethodType(1);
      MethodType localMethodType2 = localMethodType1.changeReturnType(Void.TYPE);
      CAST_REFERENCE = IMPL_LOOKUP.findVirtual(Class.class, "cast", localMethodType1);
      IGNORE = IMPL_LOOKUP.findStatic(THIS_CLASS, "ignore", localMethodType2);
      EMPTY = IMPL_LOOKUP.findStatic(THIS_CLASS, "empty", localMethodType2.dropParameterTypes(0, 1));
    }
    catch (NoSuchMethodException|IllegalAccessException localNoSuchMethodException)
    {
      throw newInternalError("uncaught exception", localNoSuchMethodException);
    }
  }
  
  private static class WrapperCache
  {
    private final EnumMap<Wrapper, MethodHandle> map = new EnumMap(Wrapper.class);
    
    private WrapperCache() {}
    
    public MethodHandle get(Wrapper paramWrapper)
    {
      return (MethodHandle)map.get(paramWrapper);
    }
    
    public synchronized MethodHandle put(Wrapper paramWrapper, MethodHandle paramMethodHandle)
    {
      MethodHandle localMethodHandle = (MethodHandle)map.putIfAbsent(paramWrapper, paramMethodHandle);
      if (localMethodHandle != null) {
        return localMethodHandle;
      }
      return paramMethodHandle;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\invoke\util\ValueConversions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */