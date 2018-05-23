package sun.invoke.util;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Arrays;

public enum Wrapper
{
  private final Class<?> wrapperType;
  private final Class<?> primitiveType;
  private final char basicTypeChar;
  private final Object zero;
  private final Object emptyArray;
  private final int format;
  private final String wrapperSimpleName;
  private final String primitiveSimpleName;
  private static final Wrapper[] FROM_PRIM;
  private static final Wrapper[] FROM_WRAP;
  private static final Wrapper[] FROM_CHAR;
  
  private Wrapper(Class<?> paramClass1, Class<?> paramClass2, char paramChar, Object paramObject1, Object paramObject2, int paramInt)
  {
    wrapperType = paramClass1;
    primitiveType = paramClass2;
    basicTypeChar = paramChar;
    zero = paramObject1;
    emptyArray = paramObject2;
    format = paramInt;
    wrapperSimpleName = paramClass1.getSimpleName();
    primitiveSimpleName = paramClass2.getSimpleName();
  }
  
  public String detailString()
  {
    return wrapperSimpleName + Arrays.asList(new Object[] { wrapperType, primitiveType, Character.valueOf(basicTypeChar), zero, "0x" + Integer.toHexString(format) });
  }
  
  public int bitWidth()
  {
    return format >> 2 & 0x3FF;
  }
  
  public int stackSlots()
  {
    return format >> 0 & 0x3;
  }
  
  public boolean isSingleWord()
  {
    return (format & 0x1) != 0;
  }
  
  public boolean isDoubleWord()
  {
    return (format & 0x2) != 0;
  }
  
  public boolean isNumeric()
  {
    return (format & 0xFFFFFFFC) != 0;
  }
  
  public boolean isIntegral()
  {
    return (isNumeric()) && (format < 4225);
  }
  
  public boolean isSubwordOrInt()
  {
    return (isIntegral()) && (isSingleWord());
  }
  
  public boolean isSigned()
  {
    return format < 0;
  }
  
  public boolean isUnsigned()
  {
    return (format >= 5) && (format < 4225);
  }
  
  public boolean isFloating()
  {
    return format >= 4225;
  }
  
  public boolean isOther()
  {
    return (format & 0xFFFFFFFC) == 0;
  }
  
  public boolean isConvertibleFrom(Wrapper paramWrapper)
  {
    if (this == paramWrapper) {
      return true;
    }
    if (compareTo(paramWrapper) < 0) {
      return false;
    }
    int i = (format & format & 0xF000) != 0 ? 1 : 0;
    if (i == 0)
    {
      if (isOther()) {
        return true;
      }
      return format == 65;
    }
    assert ((isFloating()) || (isSigned()));
    assert ((paramWrapper.isFloating()) || (paramWrapper.isSigned()));
    return true;
  }
  
  private static boolean checkConvertibleFrom()
  {
    for (Wrapper localWrapper1 : )
    {
      assert (localWrapper1.isConvertibleFrom(localWrapper1));
      assert (VOID.isConvertibleFrom(localWrapper1));
      if (localWrapper1 != VOID)
      {
        assert (OBJECT.isConvertibleFrom(localWrapper1));
        assert (!localWrapper1.isConvertibleFrom(VOID));
      }
      if (localWrapper1 != CHAR)
      {
        assert (!CHAR.isConvertibleFrom(localWrapper1));
        if ((!localWrapper1.isConvertibleFrom(INT)) && (!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(CHAR))) {
          throw new AssertionError();
        }
      }
      if (localWrapper1 != BOOLEAN)
      {
        assert (!BOOLEAN.isConvertibleFrom(localWrapper1));
        if ((localWrapper1 != VOID) && (localWrapper1 != OBJECT) && (!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(BOOLEAN))) {
          throw new AssertionError();
        }
      }
      Wrapper localWrapper2;
      if (localWrapper1.isSigned()) {
        for (localWrapper2 : values()) {
          if (localWrapper1 != localWrapper2) {
            if (localWrapper2.isFloating())
            {
              if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) {
                throw new AssertionError();
              }
            }
            else if (localWrapper2.isSigned()) {
              if (localWrapper1.compareTo(localWrapper2) < 0)
              {
                if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) {
                  throw new AssertionError();
                }
              }
              else {
                assert (localWrapper1.isConvertibleFrom(localWrapper2));
              }
            }
          }
        }
      }
      if (localWrapper1.isFloating()) {
        for (localWrapper2 : values()) {
          if (localWrapper1 != localWrapper2) {
            if (localWrapper2.isSigned())
            {
              if ((!$assertionsDisabled) && (!localWrapper1.isConvertibleFrom(localWrapper2))) {
                throw new AssertionError();
              }
            }
            else if (localWrapper2.isFloating()) {
              if (localWrapper1.compareTo(localWrapper2) < 0)
              {
                if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) {
                  throw new AssertionError();
                }
              }
              else {
                assert (localWrapper1.isConvertibleFrom(localWrapper2));
              }
            }
          }
        }
      }
    }
    return true;
  }
  
  public Object zero()
  {
    return zero;
  }
  
  public <T> T zero(Class<T> paramClass)
  {
    return (T)convert(zero, paramClass);
  }
  
  public static Wrapper forPrimitiveType(Class<?> paramClass)
  {
    Wrapper localWrapper = findPrimitiveType(paramClass);
    if (localWrapper != null) {
      return localWrapper;
    }
    if (paramClass.isPrimitive()) {
      throw new InternalError();
    }
    throw newIllegalArgumentException("not primitive: " + paramClass);
  }
  
  static Wrapper findPrimitiveType(Class<?> paramClass)
  {
    Wrapper localWrapper = FROM_PRIM[hashPrim(paramClass)];
    if ((localWrapper != null) && (primitiveType == paramClass)) {
      return localWrapper;
    }
    return null;
  }
  
  public static Wrapper forWrapperType(Class<?> paramClass)
  {
    Wrapper localWrapper1 = findWrapperType(paramClass);
    if (localWrapper1 != null) {
      return localWrapper1;
    }
    for (Wrapper localWrapper2 : values()) {
      if (wrapperType == paramClass) {
        throw new InternalError();
      }
    }
    throw newIllegalArgumentException("not wrapper: " + paramClass);
  }
  
  static Wrapper findWrapperType(Class<?> paramClass)
  {
    Wrapper localWrapper = FROM_WRAP[hashWrap(paramClass)];
    if ((localWrapper != null) && (wrapperType == paramClass)) {
      return localWrapper;
    }
    return null;
  }
  
  public static Wrapper forBasicType(char paramChar)
  {
    Wrapper localWrapper1 = FROM_CHAR[hashChar(paramChar)];
    if ((localWrapper1 != null) && (basicTypeChar == paramChar)) {
      return localWrapper1;
    }
    for (Wrapper localWrapper2 : values()) {
      if (basicTypeChar == paramChar) {
        throw new InternalError();
      }
    }
    throw newIllegalArgumentException("not basic type char: " + paramChar);
  }
  
  public static Wrapper forBasicType(Class<?> paramClass)
  {
    if (paramClass.isPrimitive()) {
      return forPrimitiveType(paramClass);
    }
    return OBJECT;
  }
  
  private static int hashPrim(Class<?> paramClass)
  {
    String str = paramClass.getName();
    if (str.length() < 3) {
      return 0;
    }
    return (str.charAt(0) + str.charAt(2)) % 16;
  }
  
  private static int hashWrap(Class<?> paramClass)
  {
    String str = paramClass.getName();
    assert (10 == "java.lang.".length());
    if (str.length() < 13) {
      return 0;
    }
    return ('\003' * str.charAt(11) + str.charAt(12)) % 16;
  }
  
  private static int hashChar(char paramChar)
  {
    return (paramChar + (paramChar >> '\001')) % 16;
  }
  
  public Class<?> primitiveType()
  {
    return primitiveType;
  }
  
  public Class<?> wrapperType()
  {
    return wrapperType;
  }
  
  public <T> Class<T> wrapperType(Class<T> paramClass)
  {
    if (paramClass == wrapperType) {
      return paramClass;
    }
    if ((paramClass == primitiveType) || (wrapperType == Object.class) || (paramClass.isInterface())) {
      return forceType(wrapperType, paramClass);
    }
    throw newClassCastException(paramClass, primitiveType);
  }
  
  private static ClassCastException newClassCastException(Class<?> paramClass1, Class<?> paramClass2)
  {
    return new ClassCastException(paramClass1 + " is not compatible with " + paramClass2);
  }
  
  public static <T> Class<T> asWrapperType(Class<T> paramClass)
  {
    if (paramClass.isPrimitive()) {
      return forPrimitiveType(paramClass).wrapperType(paramClass);
    }
    return paramClass;
  }
  
  public static <T> Class<T> asPrimitiveType(Class<T> paramClass)
  {
    Wrapper localWrapper = findWrapperType(paramClass);
    if (localWrapper != null) {
      return forceType(localWrapper.primitiveType(), paramClass);
    }
    return paramClass;
  }
  
  public static boolean isWrapperType(Class<?> paramClass)
  {
    return findWrapperType(paramClass) != null;
  }
  
  public static boolean isPrimitiveType(Class<?> paramClass)
  {
    return paramClass.isPrimitive();
  }
  
  public static char basicTypeChar(Class<?> paramClass)
  {
    if (!paramClass.isPrimitive()) {
      return 'L';
    }
    return forPrimitiveType(paramClass).basicTypeChar();
  }
  
  public char basicTypeChar()
  {
    return basicTypeChar;
  }
  
  public String wrapperSimpleName()
  {
    return wrapperSimpleName;
  }
  
  public String primitiveSimpleName()
  {
    return primitiveSimpleName;
  }
  
  public <T> T cast(Object paramObject, Class<T> paramClass)
  {
    return (T)convert(paramObject, paramClass, true);
  }
  
  public <T> T convert(Object paramObject, Class<T> paramClass)
  {
    return (T)convert(paramObject, paramClass, false);
  }
  
  private <T> T convert(Object paramObject, Class<T> paramClass, boolean paramBoolean)
  {
    if (this == OBJECT)
    {
      assert (!paramClass.isPrimitive());
      if (!paramClass.isInterface()) {
        paramClass.cast(paramObject);
      }
      localObject1 = paramObject;
      return (T)localObject1;
    }
    Object localObject1 = wrapperType(paramClass);
    if (((Class)localObject1).isInstance(paramObject)) {
      return (T)((Class)localObject1).cast(paramObject);
    }
    if (!paramBoolean)
    {
      localObject2 = paramObject.getClass();
      Wrapper localWrapper = findWrapperType((Class)localObject2);
      if ((localWrapper == null) || (!isConvertibleFrom(localWrapper))) {
        throw newClassCastException((Class)localObject1, (Class)localObject2);
      }
    }
    else if (paramObject == null)
    {
      localObject2 = zero;
      return (T)localObject2;
    }
    Object localObject2 = wrap(paramObject);
    if (!$assertionsDisabled) {
      if ((localObject2 == null ? Void.class : localObject2.getClass()) != localObject1) {
        throw new AssertionError();
      }
    }
    return (T)localObject2;
  }
  
  static <T> Class<T> forceType(Class<?> paramClass, Class<T> paramClass1)
  {
    int i = (paramClass == paramClass1) || ((paramClass.isPrimitive()) && (forPrimitiveType(paramClass) == findWrapperType(paramClass1))) || ((paramClass1.isPrimitive()) && (forPrimitiveType(paramClass1) == findWrapperType(paramClass))) || ((paramClass == Object.class) && (!paramClass1.isPrimitive())) ? 1 : 0;
    if (i == 0) {
      System.out.println(paramClass + " <= " + paramClass1);
    }
    assert ((paramClass == paramClass1) || ((paramClass.isPrimitive()) && (forPrimitiveType(paramClass) == findWrapperType(paramClass1))) || ((paramClass1.isPrimitive()) && (forPrimitiveType(paramClass1) == findWrapperType(paramClass))) || ((paramClass == Object.class) && (!paramClass1.isPrimitive())));
    Class<?> localClass = paramClass;
    return localClass;
  }
  
  public Object wrap(Object paramObject)
  {
    switch (basicTypeChar)
    {
    case 'L': 
      return paramObject;
    case 'V': 
      return null;
    }
    Number localNumber = numberValue(paramObject);
    switch (basicTypeChar)
    {
    case 'I': 
      return Integer.valueOf(localNumber.intValue());
    case 'J': 
      return Long.valueOf(localNumber.longValue());
    case 'F': 
      return Float.valueOf(localNumber.floatValue());
    case 'D': 
      return Double.valueOf(localNumber.doubleValue());
    case 'S': 
      return Short.valueOf((short)localNumber.intValue());
    case 'B': 
      return Byte.valueOf((byte)localNumber.intValue());
    case 'C': 
      return Character.valueOf((char)localNumber.intValue());
    case 'Z': 
      return Boolean.valueOf(boolValue(localNumber.byteValue()));
    }
    throw new InternalError("bad wrapper");
  }
  
  public Object wrap(int paramInt)
  {
    if (basicTypeChar == 'L') {
      return Integer.valueOf(paramInt);
    }
    switch (basicTypeChar)
    {
    case 'L': 
      throw newIllegalArgumentException("cannot wrap to object type");
    case 'V': 
      return null;
    case 'I': 
      return Integer.valueOf(paramInt);
    case 'J': 
      return Long.valueOf(paramInt);
    case 'F': 
      return Float.valueOf(paramInt);
    case 'D': 
      return Double.valueOf(paramInt);
    case 'S': 
      return Short.valueOf((short)paramInt);
    case 'B': 
      return Byte.valueOf((byte)paramInt);
    case 'C': 
      return Character.valueOf((char)paramInt);
    case 'Z': 
      return Boolean.valueOf(boolValue((byte)paramInt));
    }
    throw new InternalError("bad wrapper");
  }
  
  private static Number numberValue(Object paramObject)
  {
    if ((paramObject instanceof Number)) {
      return (Number)paramObject;
    }
    if ((paramObject instanceof Character)) {
      return Integer.valueOf(((Character)paramObject).charValue());
    }
    if ((paramObject instanceof Boolean)) {
      return Integer.valueOf(((Boolean)paramObject).booleanValue() ? 1 : 0);
    }
    return (Number)paramObject;
  }
  
  private static boolean boolValue(byte paramByte)
  {
    paramByte = (byte)(paramByte & 0x1);
    return paramByte != 0;
  }
  
  private static RuntimeException newIllegalArgumentException(String paramString, Object paramObject)
  {
    return newIllegalArgumentException(paramString + paramObject);
  }
  
  private static RuntimeException newIllegalArgumentException(String paramString)
  {
    return new IllegalArgumentException(paramString);
  }
  
  public Object makeArray(int paramInt)
  {
    return Array.newInstance(primitiveType, paramInt);
  }
  
  public Class<?> arrayType()
  {
    return emptyArray.getClass();
  }
  
  public void copyArrayUnboxing(Object[] paramArrayOfObject, int paramInt1, Object paramObject, int paramInt2, int paramInt3)
  {
    if (paramObject.getClass() != arrayType()) {
      arrayType().cast(paramObject);
    }
    for (int i = 0; i < paramInt3; i++)
    {
      Object localObject = paramArrayOfObject[(i + paramInt1)];
      localObject = convert(localObject, primitiveType);
      Array.set(paramObject, i + paramInt2, localObject);
    }
  }
  
  public void copyArrayBoxing(Object paramObject, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3)
  {
    if (paramObject.getClass() != arrayType()) {
      arrayType().cast(paramObject);
    }
    for (int i = 0; i < paramInt3; i++)
    {
      Object localObject = Array.get(paramObject, i + paramInt1);
      assert (localObject.getClass() == wrapperType);
      paramArrayOfObject[(i + paramInt2)] = localObject;
    }
  }
  
  static
  {
    BOOLEAN = new Wrapper("BOOLEAN", 0, Boolean.class, Boolean.TYPE, 'Z', Boolean.valueOf(false), new boolean[0], Format.unsigned(1));
    BYTE = new Wrapper("BYTE", 1, Byte.class, Byte.TYPE, 'B', Byte.valueOf((byte)0), new byte[0], Format.signed(8));
    SHORT = new Wrapper("SHORT", 2, Short.class, Short.TYPE, 'S', Short.valueOf((short)0), new short[0], Format.signed(16));
    CHAR = new Wrapper("CHAR", 3, Character.class, Character.TYPE, 'C', Character.valueOf('\000'), new char[0], Format.unsigned(16));
    INT = new Wrapper("INT", 4, Integer.class, Integer.TYPE, 'I', Integer.valueOf(0), new int[0], Format.signed(32));
    LONG = new Wrapper("LONG", 5, Long.class, Long.TYPE, 'J', Long.valueOf(0L), new long[0], Format.signed(64));
    FLOAT = new Wrapper("FLOAT", 6, Float.class, Float.TYPE, 'F', Float.valueOf(0.0F), new float[0], Format.floating(32));
    DOUBLE = new Wrapper("DOUBLE", 7, Double.class, Double.TYPE, 'D', Double.valueOf(0.0D), new double[0], Format.floating(64));
    OBJECT = new Wrapper("OBJECT", 8, Object.class, Object.class, 'L', null, new Object[0], Format.other(1));
    VOID = new Wrapper("VOID", 9, Void.class, Void.TYPE, 'V', null, null, Format.other(0));
    $VALUES = new Wrapper[] { BOOLEAN, BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE, OBJECT, VOID };
    assert (checkConvertibleFrom());
    FROM_PRIM = new Wrapper[16];
    FROM_WRAP = new Wrapper[16];
    FROM_CHAR = new Wrapper[16];
    for (Wrapper localWrapper : values())
    {
      int k = hashPrim(primitiveType);
      int m = hashWrap(wrapperType);
      int n = hashChar(basicTypeChar);
      assert (FROM_PRIM[k] == null);
      assert (FROM_WRAP[m] == null);
      assert (FROM_CHAR[n] == null);
      FROM_PRIM[k] = localWrapper;
      FROM_WRAP[m] = localWrapper;
      FROM_CHAR[n] = localWrapper;
    }
  }
  
  private static abstract class Format
  {
    static final int SLOT_SHIFT = 0;
    static final int SIZE_SHIFT = 2;
    static final int KIND_SHIFT = 12;
    static final int SIGNED = -4096;
    static final int UNSIGNED = 0;
    static final int FLOATING = 4096;
    static final int SLOT_MASK = 3;
    static final int SIZE_MASK = 1023;
    static final int INT = -3967;
    static final int SHORT = -4031;
    static final int BOOLEAN = 5;
    static final int CHAR = 65;
    static final int FLOAT = 4225;
    static final int VOID = 0;
    static final int NUM_MASK = -4;
    
    private Format() {}
    
    static int format(int paramInt1, int paramInt2, int paramInt3)
    {
      assert (paramInt1 >> 12 << 12 == paramInt1);
      assert ((paramInt2 & paramInt2 - 1) == 0);
      assert (paramInt1 == 61440 ? paramInt2 > 0 : paramInt1 == 0 ? paramInt2 > 0 : (paramInt1 == 4096) && ((paramInt2 == 32) || (paramInt2 == 64)));
      assert (paramInt3 == 2 ? paramInt2 != 64 : (paramInt3 == 1) && (paramInt2 <= 32));
      return paramInt1 | paramInt2 << 2 | paramInt3 << 0;
    }
    
    static int signed(int paramInt)
    {
      return format(61440, paramInt, paramInt > 32 ? 2 : 1);
    }
    
    static int unsigned(int paramInt)
    {
      return format(0, paramInt, paramInt > 32 ? 2 : 1);
    }
    
    static int floating(int paramInt)
    {
      return format(4096, paramInt, paramInt > 32 ? 2 : 1);
    }
    
    static int other(int paramInt)
    {
      return paramInt << 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\invoke\util\Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */