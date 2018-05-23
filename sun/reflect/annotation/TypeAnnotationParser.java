package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.ConstantPool;

public final class TypeAnnotationParser
{
  private static final TypeAnnotation[] EMPTY_TYPE_ANNOTATION_ARRAY = new TypeAnnotation[0];
  private static final byte CLASS_TYPE_PARAMETER = 0;
  private static final byte METHOD_TYPE_PARAMETER = 1;
  private static final byte CLASS_EXTENDS = 16;
  private static final byte CLASS_TYPE_PARAMETER_BOUND = 17;
  private static final byte METHOD_TYPE_PARAMETER_BOUND = 18;
  private static final byte FIELD = 19;
  private static final byte METHOD_RETURN = 20;
  private static final byte METHOD_RECEIVER = 21;
  private static final byte METHOD_FORMAL_PARAMETER = 22;
  private static final byte THROWS = 23;
  private static final byte LOCAL_VARIABLE = 64;
  private static final byte RESOURCE_VARIABLE = 65;
  private static final byte EXCEPTION_PARAMETER = 66;
  private static final byte INSTANCEOF = 67;
  private static final byte NEW = 68;
  private static final byte CONSTRUCTOR_REFERENCE = 69;
  private static final byte METHOD_REFERENCE = 70;
  private static final byte CAST = 71;
  private static final byte CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
  private static final byte METHOD_INVOCATION_TYPE_ARGUMENT = 73;
  private static final byte CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
  private static final byte METHOD_REFERENCE_TYPE_ARGUMENT = 75;
  
  public TypeAnnotationParser() {}
  
  public static AnnotatedType buildAnnotatedType(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass, Type paramType, TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget)
  {
    TypeAnnotation[] arrayOfTypeAnnotation1 = parseTypeAnnotations(paramArrayOfByte, paramConstantPool, paramAnnotatedElement, paramClass);
    ArrayList localArrayList = new ArrayList(arrayOfTypeAnnotation1.length);
    for (TypeAnnotation localTypeAnnotation : arrayOfTypeAnnotation1)
    {
      TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = localTypeAnnotation.getTargetInfo();
      if (localTypeAnnotationTargetInfo.getTarget() == paramTypeAnnotationTarget) {
        localArrayList.add(localTypeAnnotation);
      }
    }
    ??? = (TypeAnnotation[])localArrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
    return AnnotatedTypeFactory.buildAnnotatedType(paramType, TypeAnnotation.LocationInfo.BASE_LOCATION, ???, ???, paramAnnotatedElement);
  }
  
  public static AnnotatedType[] buildAnnotatedTypes(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass, Type[] paramArrayOfType, TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget)
  {
    int i = paramArrayOfType.length;
    AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[i];
    Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
    ArrayList[] arrayOfArrayList = new ArrayList[i];
    TypeAnnotation[] arrayOfTypeAnnotation1 = parseTypeAnnotations(paramArrayOfByte, paramConstantPool, paramAnnotatedElement, paramClass);
    for (TypeAnnotation localTypeAnnotation : arrayOfTypeAnnotation1)
    {
      TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = localTypeAnnotation.getTargetInfo();
      if (localTypeAnnotationTargetInfo.getTarget() == paramTypeAnnotationTarget)
      {
        int n = localTypeAnnotationTargetInfo.getCount();
        if (arrayOfArrayList[n] == null)
        {
          localArrayList2 = new ArrayList(arrayOfTypeAnnotation1.length);
          arrayOfArrayList[n] = localArrayList2;
        }
        ArrayList localArrayList2 = arrayOfArrayList[n];
        localArrayList2.add(localTypeAnnotation);
      }
    }
    for (int j = 0; j < i; j++)
    {
      ArrayList localArrayList1 = arrayOfArrayList[j];
      TypeAnnotation[] arrayOfTypeAnnotation3;
      if (localArrayList1 != null) {
        arrayOfTypeAnnotation3 = (TypeAnnotation[])localArrayList1.toArray(new TypeAnnotation[localArrayList1.size()]);
      } else {
        arrayOfTypeAnnotation3 = EMPTY_TYPE_ANNOTATION_ARRAY;
      }
      arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[j], TypeAnnotation.LocationInfo.BASE_LOCATION, arrayOfTypeAnnotation3, arrayOfTypeAnnotation3, paramAnnotatedElement);
    }
    return arrayOfAnnotatedType;
  }
  
  public static AnnotatedType buildAnnotatedSuperclass(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    Type localType = paramClass.getGenericSuperclass();
    if (localType == null) {
      return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE;
    }
    return buildAnnotatedType(paramArrayOfByte, paramConstantPool, paramClass, paramClass, localType, TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
  }
  
  public static AnnotatedType[] buildAnnotatedInterfaces(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
  {
    if ((paramClass == Object.class) || (paramClass.isArray()) || (paramClass.isPrimitive()) || (paramClass == Void.TYPE)) {
      return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE_ARRAY;
    }
    return buildAnnotatedTypes(paramArrayOfByte, paramConstantPool, paramClass, paramClass, paramClass.getGenericInterfaces(), TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS);
  }
  
  public static <D extends GenericDeclaration> Annotation[] parseTypeVariableAnnotations(D paramD, int paramInt)
  {
    Object localObject;
    TypeAnnotation.TypeAnnotationTarget localTypeAnnotationTarget;
    if ((paramD instanceof Class))
    {
      localObject = (Class)paramD;
      localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER;
    }
    else if ((paramD instanceof Executable))
    {
      localObject = (Executable)paramD;
      localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER;
    }
    else
    {
      throw new AssertionError("Unknown GenericDeclaration " + paramD + "\nthis should not happen.");
    }
    List localList = TypeAnnotation.filter(parseAllTypeAnnotations((AnnotatedElement)localObject), localTypeAnnotationTarget);
    ArrayList localArrayList = new ArrayList(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      TypeAnnotation localTypeAnnotation = (TypeAnnotation)localIterator.next();
      if (localTypeAnnotation.getTargetInfo().getCount() == paramInt) {
        localArrayList.add(localTypeAnnotation.getAnnotation());
      }
    }
    return (Annotation[])localArrayList.toArray(new Annotation[0]);
  }
  
  public static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] paramArrayOfType, D paramD, int paramInt)
  {
    return parseAnnotatedBounds(paramArrayOfType, paramD, paramInt, TypeAnnotation.LocationInfo.BASE_LOCATION);
  }
  
  private static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] paramArrayOfType, D paramD, int paramInt, TypeAnnotation.LocationInfo paramLocationInfo)
  {
    List localList = fetchBounds(paramD);
    if (paramArrayOfType != null)
    {
      int i = 0;
      AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[paramArrayOfType.length];
      Object localObject;
      if (paramArrayOfType.length > 0)
      {
        Type localType = paramArrayOfType[0];
        if (!(localType instanceof Class))
        {
          i = 1;
        }
        else
        {
          localObject = (Class)localType;
          if (((Class)localObject).isInterface()) {
            i = 1;
          }
        }
      }
      for (int j = 0; j < paramArrayOfType.length; j++)
      {
        localObject = new ArrayList(localList.size());
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          TypeAnnotation localTypeAnnotation = (TypeAnnotation)localIterator.next();
          TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = localTypeAnnotation.getTargetInfo();
          if ((localTypeAnnotationTargetInfo.getSecondaryIndex() == j + i) && (localTypeAnnotationTargetInfo.getCount() == paramInt)) {
            ((List)localObject).add(localTypeAnnotation);
          }
        }
        arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[j], paramLocationInfo, (TypeAnnotation[])((List)localObject).toArray(EMPTY_TYPE_ANNOTATION_ARRAY), (TypeAnnotation[])localList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY), paramD);
      }
      return arrayOfAnnotatedType;
    }
    return new AnnotatedType[0];
  }
  
  private static <D extends GenericDeclaration> List<TypeAnnotation> fetchBounds(D paramD)
  {
    TypeAnnotation.TypeAnnotationTarget localTypeAnnotationTarget;
    Object localObject;
    if ((paramD instanceof Class))
    {
      localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND;
      localObject = (Class)paramD;
    }
    else
    {
      localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND;
      localObject = (Executable)paramD;
    }
    return TypeAnnotation.filter(parseAllTypeAnnotations((AnnotatedElement)localObject), localTypeAnnotationTarget);
  }
  
  static TypeAnnotation[] parseAllTypeAnnotations(AnnotatedElement paramAnnotatedElement)
  {
    JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
    Class localClass;
    byte[] arrayOfByte;
    if ((paramAnnotatedElement instanceof Class))
    {
      localClass = (Class)paramAnnotatedElement;
      arrayOfByte = localJavaLangAccess.getRawClassTypeAnnotations(localClass);
    }
    else if ((paramAnnotatedElement instanceof Executable))
    {
      localClass = ((Executable)paramAnnotatedElement).getDeclaringClass();
      arrayOfByte = localJavaLangAccess.getRawExecutableTypeAnnotations((Executable)paramAnnotatedElement);
    }
    else
    {
      return EMPTY_TYPE_ANNOTATION_ARRAY;
    }
    return parseTypeAnnotations(arrayOfByte, localJavaLangAccess.getConstantPool(localClass), paramAnnotatedElement, localClass);
  }
  
  private static TypeAnnotation[] parseTypeAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass)
  {
    if (paramArrayOfByte == null) {
      return EMPTY_TYPE_ANNOTATION_ARRAY;
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    int i = localByteBuffer.getShort() & 0xFFFF;
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      TypeAnnotation localTypeAnnotation = parseTypeAnnotation(localByteBuffer, paramConstantPool, paramAnnotatedElement, paramClass);
      if (localTypeAnnotation != null) {
        localArrayList.add(localTypeAnnotation);
      }
    }
    return (TypeAnnotation[])localArrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
  }
  
  static Map<Class<? extends Annotation>, Annotation> mapTypeAnnotations(TypeAnnotation[] paramArrayOfTypeAnnotation)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    for (TypeAnnotation localTypeAnnotation : paramArrayOfTypeAnnotation)
    {
      Annotation localAnnotation = localTypeAnnotation.getAnnotation();
      Class localClass = localAnnotation.annotationType();
      AnnotationType localAnnotationType = AnnotationType.getInstance(localClass);
      if ((localAnnotationType.retention() == RetentionPolicy.RUNTIME) && (localLinkedHashMap.put(localClass, localAnnotation) != null)) {
        throw new AnnotationFormatError("Duplicate annotation for class: " + localClass + ": " + localAnnotation);
      }
    }
    return localLinkedHashMap;
  }
  
  private static TypeAnnotation parseTypeAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass)
  {
    try
    {
      TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = parseTargetInfo(paramByteBuffer);
      TypeAnnotation.LocationInfo localLocationInfo = TypeAnnotation.LocationInfo.parseLocationInfo(paramByteBuffer);
      Annotation localAnnotation = AnnotationParser.parseAnnotation(paramByteBuffer, paramConstantPool, paramClass, false);
      if (localTypeAnnotationTargetInfo == null) {
        return null;
      }
      return new TypeAnnotation(localTypeAnnotationTargetInfo, localLocationInfo, localAnnotation, paramAnnotatedElement);
    }
    catch (IllegalArgumentException|BufferUnderflowException localIllegalArgumentException)
    {
      throw new AnnotationFormatError(localIllegalArgumentException);
    }
  }
  
  private static TypeAnnotation.TypeAnnotationTargetInfo parseTargetInfo(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.get() & 0xFF;
    int j;
    TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo;
    int k;
    int m;
    switch (i)
    {
    case 0: 
    case 1: 
      j = paramByteBuffer.get() & 0xFF;
      if (i == 0) {
        localTypeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER, j);
      } else {
        localTypeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER, j);
      }
      return localTypeAnnotationTargetInfo;
    case 16: 
      j = paramByteBuffer.getShort();
      if (j == -1) {
        return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
      }
      if (j >= 0)
      {
        localTypeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS, j);
        return localTypeAnnotationTargetInfo;
      }
      break;
    case 17: 
      return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND, paramByteBuffer);
    case 18: 
      return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND, paramByteBuffer);
    case 19: 
      return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.FIELD);
    case 20: 
      return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
    case 21: 
      return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
    case 22: 
      j = paramByteBuffer.get() & 0xFF;
      return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER, j);
    case 23: 
      return parseShortTarget(TypeAnnotation.TypeAnnotationTarget.THROWS, paramByteBuffer);
    case 64: 
    case 65: 
      j = paramByteBuffer.getShort();
      for (k = 0; k < j; k++)
      {
        m = paramByteBuffer.getShort();
        int n = paramByteBuffer.getShort();
        int i1 = paramByteBuffer.getShort();
      }
      return null;
    case 66: 
      k = paramByteBuffer.get();
      return null;
    case 67: 
    case 68: 
    case 69: 
    case 70: 
      k = paramByteBuffer.getShort();
      return null;
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
      k = paramByteBuffer.getShort();
      m = paramByteBuffer.get();
      return null;
    }
    throw new AnnotationFormatError("Could not parse bytes for type annotations");
  }
  
  private static TypeAnnotation.TypeAnnotationTargetInfo parseShortTarget(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    return new TypeAnnotation.TypeAnnotationTargetInfo(paramTypeAnnotationTarget, i);
  }
  
  private static TypeAnnotation.TypeAnnotationTargetInfo parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.get() & 0xFF;
    int j = paramByteBuffer.get() & 0xFF;
    return new TypeAnnotation.TypeAnnotationTargetInfo(paramTypeAnnotationTarget, i, j);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\TypeAnnotationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */