package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sun.reflect.ConstantPool;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class AnnotationParser
{
  private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
  private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
  
  public AnnotationParser() {}
  
  public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    if (paramArrayOfByte == null) {
      return Collections.emptyMap();
    }
    try
    {
      return parseAnnotations2(paramArrayOfByte, paramConstantPool, paramClass, null);
    }
    catch (BufferUnderflowException localBufferUnderflowException)
    {
      throw new AnnotationFormatError("Unexpected end of annotations.");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new AnnotationFormatError(localIllegalArgumentException);
    }
  }
  
  @SafeVarargs
  static Map<Class<? extends Annotation>, Annotation> parseSelectAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass, Class<? extends Annotation>... paramVarArgs)
  {
    if (paramArrayOfByte == null) {
      return Collections.emptyMap();
    }
    try
    {
      return parseAnnotations2(paramArrayOfByte, paramConstantPool, paramClass, paramVarArgs);
    }
    catch (BufferUnderflowException localBufferUnderflowException)
    {
      throw new AnnotationFormatError("Unexpected end of annotations.");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new AnnotationFormatError(localIllegalArgumentException);
    }
  }
  
  private static Map<Class<? extends Annotation>, Annotation> parseAnnotations2(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass, Class<? extends Annotation>[] paramArrayOfClass)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    int i = localByteBuffer.getShort() & 0xFFFF;
    for (int j = 0; j < i; j++)
    {
      Annotation localAnnotation = parseAnnotation2(localByteBuffer, paramConstantPool, paramClass, false, paramArrayOfClass);
      if (localAnnotation != null)
      {
        Class localClass = localAnnotation.annotationType();
        if ((AnnotationType.getInstance(localClass).retention() == RetentionPolicy.RUNTIME) && (localLinkedHashMap.put(localClass, localAnnotation) != null)) {
          throw new AnnotationFormatError("Duplicate annotation for class: " + localClass + ": " + localAnnotation);
        }
      }
    }
    return localLinkedHashMap;
  }
  
  public static Annotation[][] parseParameterAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    try
    {
      return parseParameterAnnotations2(paramArrayOfByte, paramConstantPool, paramClass);
    }
    catch (BufferUnderflowException localBufferUnderflowException)
    {
      throw new AnnotationFormatError("Unexpected end of parameter annotations.");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new AnnotationFormatError(localIllegalArgumentException);
    }
  }
  
  private static Annotation[][] parseParameterAnnotations2(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    int i = localByteBuffer.get() & 0xFF;
    Annotation[][] arrayOfAnnotation = new Annotation[i][];
    for (int j = 0; j < i; j++)
    {
      int k = localByteBuffer.getShort() & 0xFFFF;
      ArrayList localArrayList = new ArrayList(k);
      for (int m = 0; m < k; m++)
      {
        Annotation localAnnotation = parseAnnotation(localByteBuffer, paramConstantPool, paramClass, false);
        if (localAnnotation != null)
        {
          AnnotationType localAnnotationType = AnnotationType.getInstance(localAnnotation.annotationType());
          if (localAnnotationType.retention() == RetentionPolicy.RUNTIME) {
            localArrayList.add(localAnnotation);
          }
        }
      }
      arrayOfAnnotation[j] = ((Annotation[])localArrayList.toArray(EMPTY_ANNOTATIONS_ARRAY));
    }
    return arrayOfAnnotation;
  }
  
  static Annotation parseAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean)
  {
    return parseAnnotation2(paramByteBuffer, paramConstantPool, paramClass, paramBoolean, null);
  }
  
  private static Annotation parseAnnotation2(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean, Class<? extends Annotation>[] paramArrayOfClass)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    Class localClass1 = null;
    String str1 = "[unknown]";
    try
    {
      try
      {
        str1 = paramConstantPool.getUTF8At(i);
        localClass1 = parseSig(str1, paramClass);
      }
      catch (IllegalArgumentException localIllegalArgumentException1)
      {
        localClass1 = paramConstantPool.getClassAt(i);
      }
    }
    catch (NoClassDefFoundError localNoClassDefFoundError)
    {
      if (paramBoolean) {
        throw new TypeNotPresentException(str1, localNoClassDefFoundError);
      }
      skipAnnotation(paramByteBuffer, false);
      return null;
    }
    catch (TypeNotPresentException localTypeNotPresentException)
    {
      if (paramBoolean) {
        throw localTypeNotPresentException;
      }
      skipAnnotation(paramByteBuffer, false);
      return null;
    }
    if ((paramArrayOfClass != null) && (!contains(paramArrayOfClass, localClass1)))
    {
      skipAnnotation(paramByteBuffer, false);
      return null;
    }
    AnnotationType localAnnotationType = null;
    try
    {
      localAnnotationType = AnnotationType.getInstance(localClass1);
    }
    catch (IllegalArgumentException localIllegalArgumentException2)
    {
      skipAnnotation(paramByteBuffer, false);
      return null;
    }
    Map localMap = localAnnotationType.memberTypes();
    LinkedHashMap localLinkedHashMap = new LinkedHashMap(localAnnotationType.memberDefaults());
    int j = paramByteBuffer.getShort() & 0xFFFF;
    for (int k = 0; k < j; k++)
    {
      int m = paramByteBuffer.getShort() & 0xFFFF;
      String str2 = paramConstantPool.getUTF8At(m);
      Class localClass2 = (Class)localMap.get(str2);
      if (localClass2 == null)
      {
        skipMemberValue(paramByteBuffer);
      }
      else
      {
        Object localObject = parseMemberValue(localClass2, paramByteBuffer, paramConstantPool, paramClass);
        if ((localObject instanceof AnnotationTypeMismatchExceptionProxy)) {
          ((AnnotationTypeMismatchExceptionProxy)localObject).setMember((Method)localAnnotationType.members().get(str2));
        }
        localLinkedHashMap.put(str2, localObject);
      }
    }
    return annotationForMap(localClass1, localLinkedHashMap);
  }
  
  public static Annotation annotationForMap(Class<? extends Annotation> paramClass, final Map<String, Object> paramMap)
  {
    (Annotation)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Annotation run()
      {
        return (Annotation)Proxy.newProxyInstance(val$type.getClassLoader(), new Class[] { val$type }, new AnnotationInvocationHandler(val$type, paramMap));
      }
    });
  }
  
  public static Object parseMemberValue(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2)
  {
    Object localObject = null;
    int i = paramByteBuffer.get();
    switch (i)
    {
    case 101: 
      return parseEnumValue(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
    case 99: 
      localObject = parseClassValue(paramByteBuffer, paramConstantPool, paramClass2);
      break;
    case 64: 
      localObject = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass2, true);
      break;
    case 91: 
      return parseArray(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
    default: 
      localObject = parseConst(i, paramByteBuffer, paramConstantPool);
    }
    if ((!(localObject instanceof ExceptionProxy)) && (!paramClass1.isInstance(localObject))) {
      localObject = new AnnotationTypeMismatchExceptionProxy(localObject.getClass() + "[" + localObject + "]");
    }
    return localObject;
  }
  
  private static Object parseConst(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    switch (paramInt)
    {
    case 66: 
      return Byte.valueOf((byte)paramConstantPool.getIntAt(i));
    case 67: 
      return Character.valueOf((char)paramConstantPool.getIntAt(i));
    case 68: 
      return Double.valueOf(paramConstantPool.getDoubleAt(i));
    case 70: 
      return Float.valueOf(paramConstantPool.getFloatAt(i));
    case 73: 
      return Integer.valueOf(paramConstantPool.getIntAt(i));
    case 74: 
      return Long.valueOf(paramConstantPool.getLongAt(i));
    case 83: 
      return Short.valueOf((short)paramConstantPool.getIntAt(i));
    case 90: 
      return Boolean.valueOf(paramConstantPool.getIntAt(i) != 0);
    case 115: 
      return paramConstantPool.getUTF8At(i);
    }
    throw new AnnotationFormatError("Invalid member-value tag in annotation: " + paramInt);
  }
  
  private static Object parseClassValue(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    try
    {
      String str = paramConstantPool.getUTF8At(i);
      return parseSig(str, paramClass);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return paramConstantPool.getClassAt(i);
    }
    catch (NoClassDefFoundError localNoClassDefFoundError)
    {
      return new TypeNotPresentExceptionProxy("[unknown]", localNoClassDefFoundError);
    }
    catch (TypeNotPresentException localTypeNotPresentException)
    {
      return new TypeNotPresentExceptionProxy(localTypeNotPresentException.typeName(), localTypeNotPresentException.getCause());
    }
  }
  
  private static Class<?> parseSig(String paramString, Class<?> paramClass)
  {
    if (paramString.equals("V")) {
      return Void.TYPE;
    }
    SignatureParser localSignatureParser = SignatureParser.make();
    TypeSignature localTypeSignature = localSignatureParser.parseTypeSig(paramString);
    CoreReflectionFactory localCoreReflectionFactory = CoreReflectionFactory.make(paramClass, ClassScope.make(paramClass));
    Reifier localReifier = Reifier.make(localCoreReflectionFactory);
    localTypeSignature.accept(localReifier);
    Type localType = localReifier.getResult();
    return toClass(localType);
  }
  
  static Class<?> toClass(Type paramType)
  {
    if ((paramType instanceof GenericArrayType)) {
      return Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
    }
    return (Class)paramType;
  }
  
  private static Object parseEnumValue(Class<? extends Enum> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    String str1 = paramConstantPool.getUTF8At(i);
    int j = paramByteBuffer.getShort() & 0xFFFF;
    String str2 = paramConstantPool.getUTF8At(j);
    if (!str1.endsWith(";"))
    {
      if (!paramClass.getName().equals(str1)) {
        return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
      }
    }
    else if (paramClass != parseSig(str1, paramClass1)) {
      return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
    }
    try
    {
      return Enum.valueOf(paramClass, str2);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return new EnumConstantNotPresentExceptionProxy(paramClass, str2);
  }
  
  private static Object parseArray(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    Class localClass = paramClass1.getComponentType();
    if (localClass == Byte.TYPE) {
      return parseByteArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Character.TYPE) {
      return parseCharArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Double.TYPE) {
      return parseDoubleArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Float.TYPE) {
      return parseFloatArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Integer.TYPE) {
      return parseIntArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Long.TYPE) {
      return parseLongArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Short.TYPE) {
      return parseShortArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Boolean.TYPE) {
      return parseBooleanArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == String.class) {
      return parseStringArray(i, paramByteBuffer, paramConstantPool);
    }
    if (localClass == Class.class) {
      return parseClassArray(i, paramByteBuffer, paramConstantPool, paramClass2);
    }
    if (localClass.isEnum()) {
      return parseEnumArray(i, localClass, paramByteBuffer, paramConstantPool, paramClass2);
    }
    assert (localClass.isAnnotation());
    return parseAnnotationArray(i, localClass, paramByteBuffer, paramConstantPool, paramClass2);
  }
  
  private static Object parseByteArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    byte[] arrayOfByte = new byte[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 66)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfByte[k] = ((byte)paramConstantPool.getIntAt(m));
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfByte;
  }
  
  private static Object parseCharArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    char[] arrayOfChar = new char[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 67)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfChar[k] = ((char)paramConstantPool.getIntAt(m));
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfChar;
  }
  
  private static Object parseDoubleArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    double[] arrayOfDouble = new double[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 68)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfDouble[k] = paramConstantPool.getDoubleAt(m);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfDouble;
  }
  
  private static Object parseFloatArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    float[] arrayOfFloat = new float[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 70)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfFloat[k] = paramConstantPool.getFloatAt(m);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfFloat;
  }
  
  private static Object parseIntArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    int[] arrayOfInt = new int[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 73)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfInt[k] = paramConstantPool.getIntAt(m);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfInt;
  }
  
  private static Object parseLongArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    long[] arrayOfLong = new long[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 74)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfLong[k] = paramConstantPool.getLongAt(m);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfLong;
  }
  
  private static Object parseShortArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    short[] arrayOfShort = new short[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 83)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfShort[k] = ((short)paramConstantPool.getIntAt(m));
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfShort;
  }
  
  private static Object parseBooleanArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    boolean[] arrayOfBoolean = new boolean[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 90)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfBoolean[k] = (paramConstantPool.getIntAt(m) != 0 ? 1 : false);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfBoolean;
  }
  
  private static Object parseStringArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
  {
    String[] arrayOfString = new String[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 115)
      {
        int m = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfString[k] = paramConstantPool.getUTF8At(m);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfString;
  }
  
  private static Object parseClassArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    Class[] arrayOfClass = new Class[paramInt];
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 99)
      {
        arrayOfClass[k] = parseClassValue(paramByteBuffer, paramConstantPool, paramClass);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfClass;
  }
  
  private static Object parseEnumArray(int paramInt, Class<? extends Enum<?>> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
  {
    Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 101)
      {
        arrayOfObject[k] = parseEnumValue(paramClass, paramByteBuffer, paramConstantPool, paramClass1);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfObject;
  }
  
  private static Object parseAnnotationArray(int paramInt, Class<? extends Annotation> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
  {
    Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramInt; k++)
    {
      j = paramByteBuffer.get();
      if (j == 64)
      {
        arrayOfObject[k] = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass1, true);
      }
      else
      {
        skipMemberValue(j, paramByteBuffer);
        i = 1;
      }
    }
    return i != 0 ? exceptionProxy(j) : arrayOfObject;
  }
  
  private static ExceptionProxy exceptionProxy(int paramInt)
  {
    return new AnnotationTypeMismatchExceptionProxy("Array with component tag: " + paramInt);
  }
  
  private static void skipAnnotation(ByteBuffer paramByteBuffer, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramByteBuffer.getShort();
    }
    int i = paramByteBuffer.getShort() & 0xFFFF;
    for (int j = 0; j < i; j++)
    {
      paramByteBuffer.getShort();
      skipMemberValue(paramByteBuffer);
    }
  }
  
  private static void skipMemberValue(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.get();
    skipMemberValue(i, paramByteBuffer);
  }
  
  private static void skipMemberValue(int paramInt, ByteBuffer paramByteBuffer)
  {
    switch (paramInt)
    {
    case 101: 
      paramByteBuffer.getInt();
      break;
    case 64: 
      skipAnnotation(paramByteBuffer, true);
      break;
    case 91: 
      skipArray(paramByteBuffer);
      break;
    default: 
      paramByteBuffer.getShort();
    }
  }
  
  private static void skipArray(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    for (int j = 0; j < i; j++) {
      skipMemberValue(paramByteBuffer);
    }
  }
  
  private static boolean contains(Object[] paramArrayOfObject, Object paramObject)
  {
    for (Object localObject : paramArrayOfObject) {
      if (localObject == paramObject) {
        return true;
      }
    }
    return false;
  }
  
  public static Annotation[] toArray(Map<Class<? extends Annotation>, Annotation> paramMap)
  {
    return (Annotation[])paramMap.values().toArray(EMPTY_ANNOTATION_ARRAY);
  }
  
  static Annotation[] getEmptyAnnotationArray()
  {
    return EMPTY_ANNOTATION_ARRAY;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\AnnotationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */