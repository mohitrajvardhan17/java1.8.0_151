package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation.TypeAnnotationTarget;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.repository.ConstructorRepository;

public abstract class Executable
  extends AccessibleObject
  implements Member, GenericDeclaration
{
  private volatile transient boolean hasRealParameterData;
  private volatile transient Parameter[] parameters;
  private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  Executable() {}
  
  abstract byte[] getAnnotationBytes();
  
  abstract Executable getRoot();
  
  abstract boolean hasGenericInformation();
  
  abstract ConstructorRepository getGenericInfo();
  
  boolean equalParamTypes(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
  {
    if (paramArrayOfClass1.length == paramArrayOfClass2.length)
    {
      for (int i = 0; i < paramArrayOfClass1.length; i++) {
        if (paramArrayOfClass1[i] != paramArrayOfClass2[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  Annotation[][] parseParameterAnnotations(byte[] paramArrayOfByte)
  {
    return AnnotationParser.parseParameterAnnotations(paramArrayOfByte, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
  }
  
  void separateWithCommas(Class<?>[] paramArrayOfClass, StringBuilder paramStringBuilder)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      paramStringBuilder.append(paramArrayOfClass[i].getTypeName());
      if (i < paramArrayOfClass.length - 1) {
        paramStringBuilder.append(",");
      }
    }
  }
  
  void printModifiersIfNonzero(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean)
  {
    int i = getModifiers() & paramInt;
    if ((i != 0) && (!paramBoolean))
    {
      paramStringBuilder.append(Modifier.toString(i)).append(' ');
    }
    else
    {
      int j = i & 0x7;
      if (j != 0) {
        paramStringBuilder.append(Modifier.toString(j)).append(' ');
      }
      if (paramBoolean) {
        paramStringBuilder.append("default ");
      }
      i &= 0xFFFFFFF8;
      if (i != 0) {
        paramStringBuilder.append(Modifier.toString(i)).append(' ');
      }
    }
  }
  
  String sharedToString(int paramInt, boolean paramBoolean, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      printModifiersIfNonzero(localStringBuilder, paramInt, paramBoolean);
      specificToStringHeader(localStringBuilder);
      localStringBuilder.append('(');
      separateWithCommas(paramArrayOfClass1, localStringBuilder);
      localStringBuilder.append(')');
      if (paramArrayOfClass2.length > 0)
      {
        localStringBuilder.append(" throws ");
        separateWithCommas(paramArrayOfClass2, localStringBuilder);
      }
      return localStringBuilder.toString();
    }
    catch (Exception localException)
    {
      return "<" + localException + ">";
    }
  }
  
  abstract void specificToStringHeader(StringBuilder paramStringBuilder);
  
  String sharedToGenericString(int paramInt, boolean paramBoolean)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      printModifiersIfNonzero(localStringBuilder, paramInt, paramBoolean);
      TypeVariable[] arrayOfTypeVariable1 = getTypeParameters();
      if (arrayOfTypeVariable1.length > 0)
      {
        int i = 1;
        localStringBuilder.append('<');
        for (TypeVariable localTypeVariable : arrayOfTypeVariable1)
        {
          if (i == 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(localTypeVariable.toString());
          i = 0;
        }
        localStringBuilder.append("> ");
      }
      specificToGenericStringHeader(localStringBuilder);
      localStringBuilder.append('(');
      Type[] arrayOfType1 = getGenericParameterTypes();
      for (int j = 0; j < arrayOfType1.length; j++)
      {
        String str = arrayOfType1[j].getTypeName();
        if ((isVarArgs()) && (j == arrayOfType1.length - 1)) {
          str = str.replaceFirst("\\[\\]$", "...");
        }
        localStringBuilder.append(str);
        if (j < arrayOfType1.length - 1) {
          localStringBuilder.append(',');
        }
      }
      localStringBuilder.append(')');
      Type[] arrayOfType2 = getGenericExceptionTypes();
      if (arrayOfType2.length > 0)
      {
        localStringBuilder.append(" throws ");
        for (int m = 0; m < arrayOfType2.length; m++)
        {
          localStringBuilder.append((arrayOfType2[m] instanceof Class) ? ((Class)arrayOfType2[m]).getName() : arrayOfType2[m].toString());
          if (m < arrayOfType2.length - 1) {
            localStringBuilder.append(',');
          }
        }
      }
      return localStringBuilder.toString();
    }
    catch (Exception localException)
    {
      return "<" + localException + ">";
    }
  }
  
  abstract void specificToGenericStringHeader(StringBuilder paramStringBuilder);
  
  public abstract Class<?> getDeclaringClass();
  
  public abstract String getName();
  
  public abstract int getModifiers();
  
  public abstract TypeVariable<?>[] getTypeParameters();
  
  public abstract Class<?>[] getParameterTypes();
  
  public int getParameterCount()
  {
    throw new AbstractMethodError();
  }
  
  public Type[] getGenericParameterTypes()
  {
    if (hasGenericInformation()) {
      return getGenericInfo().getParameterTypes();
    }
    return getParameterTypes();
  }
  
  Type[] getAllGenericParameterTypes()
  {
    boolean bool1 = hasGenericInformation();
    if (!bool1) {
      return getParameterTypes();
    }
    boolean bool2 = hasRealParameterData();
    Type[] arrayOfType1 = getGenericParameterTypes();
    Class[] arrayOfClass = getParameterTypes();
    Type[] arrayOfType2 = new Type[arrayOfClass.length];
    Parameter[] arrayOfParameter = getParameters();
    int i = 0;
    if (bool2) {
      for (int j = 0; j < arrayOfType2.length; j++)
      {
        Parameter localParameter = arrayOfParameter[j];
        if ((localParameter.isSynthetic()) || (localParameter.isImplicit()))
        {
          arrayOfType2[j] = arrayOfClass[j];
        }
        else
        {
          arrayOfType2[j] = arrayOfType1[i];
          i++;
        }
      }
    } else {
      return arrayOfType1.length == arrayOfClass.length ? arrayOfType1 : arrayOfClass;
    }
    return arrayOfType2;
  }
  
  public Parameter[] getParameters()
  {
    return (Parameter[])privateGetParameters().clone();
  }
  
  private Parameter[] synthesizeAllParams()
  {
    int i = getParameterCount();
    Parameter[] arrayOfParameter = new Parameter[i];
    for (int j = 0; j < i; j++) {
      arrayOfParameter[j] = new Parameter("arg" + j, 0, this, j);
    }
    return arrayOfParameter;
  }
  
  private void verifyParameters(Parameter[] paramArrayOfParameter)
  {
    if (getParameterTypes().length != paramArrayOfParameter.length) {
      throw new MalformedParametersException("Wrong number of parameters in MethodParameters attribute");
    }
    for (Parameter localParameter : paramArrayOfParameter)
    {
      String str = localParameter.getRealName();
      int k = localParameter.getModifiers();
      if ((str != null) && ((str.isEmpty()) || (str.indexOf('.') != -1) || (str.indexOf(';') != -1) || (str.indexOf('[') != -1) || (str.indexOf('/') != -1))) {
        throw new MalformedParametersException("Invalid parameter name \"" + str + "\"");
      }
      if (k != (k & 0x9010)) {
        throw new MalformedParametersException("Invalid parameter modifiers");
      }
    }
  }
  
  private Parameter[] privateGetParameters()
  {
    Parameter[] arrayOfParameter = parameters;
    if (arrayOfParameter == null)
    {
      try
      {
        arrayOfParameter = getParameters0();
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new MalformedParametersException("Invalid constant pool index");
      }
      if (arrayOfParameter == null)
      {
        hasRealParameterData = false;
        arrayOfParameter = synthesizeAllParams();
      }
      else
      {
        hasRealParameterData = true;
        verifyParameters(arrayOfParameter);
      }
      parameters = arrayOfParameter;
    }
    return arrayOfParameter;
  }
  
  boolean hasRealParameterData()
  {
    if (parameters == null) {
      privateGetParameters();
    }
    return hasRealParameterData;
  }
  
  private native Parameter[] getParameters0();
  
  native byte[] getTypeAnnotationBytes0();
  
  byte[] getTypeAnnotationBytes()
  {
    return getTypeAnnotationBytes0();
  }
  
  public abstract Class<?>[] getExceptionTypes();
  
  public Type[] getGenericExceptionTypes()
  {
    Type[] arrayOfType;
    if ((hasGenericInformation()) && ((arrayOfType = getGenericInfo().getExceptionTypes()).length > 0)) {
      return arrayOfType;
    }
    return getExceptionTypes();
  }
  
  public abstract String toGenericString();
  
  public boolean isVarArgs()
  {
    return (getModifiers() & 0x80) != 0;
  }
  
  public boolean isSynthetic()
  {
    return Modifier.isSynthetic(getModifiers());
  }
  
  public abstract Annotation[][] getParameterAnnotations();
  
  Annotation[][] sharedGetParameterAnnotations(Class<?>[] paramArrayOfClass, byte[] paramArrayOfByte)
  {
    int i = paramArrayOfClass.length;
    if (paramArrayOfByte == null) {
      return new Annotation[i][0];
    }
    Annotation[][] arrayOfAnnotation = parseParameterAnnotations(paramArrayOfByte);
    if (arrayOfAnnotation.length != i) {
      handleParameterNumberMismatch(arrayOfAnnotation.length, i);
    }
    return arrayOfAnnotation;
  }
  
  abstract void handleParameterNumberMismatch(int paramInt1, int paramInt2);
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return (Annotation)paramClass.cast(declaredAnnotations().get(paramClass));
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return AnnotationParser.toArray(declaredAnnotations());
  }
  
  private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
  {
    if (declaredAnnotations == null)
    {
      Executable localExecutable = getRoot();
      if (localExecutable != null) {
        declaredAnnotations = localExecutable.declaredAnnotations();
      } else {
        declaredAnnotations = AnnotationParser.parseAnnotations(getAnnotationBytes(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
      }
    }
    return declaredAnnotations;
  }
  
  public abstract AnnotatedType getAnnotatedReturnType();
  
  AnnotatedType getAnnotatedReturnType0(Type paramType)
  {
    return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), paramType, TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
  }
  
  public AnnotatedType getAnnotatedReceiverType()
  {
    if (Modifier.isStatic(getModifiers())) {
      return null;
    }
    return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getDeclaringClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
  }
  
  public AnnotatedType[] getAnnotatedParameterTypes()
  {
    return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getAllGenericParameterTypes(), TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER);
  }
  
  public AnnotatedType[] getAnnotatedExceptionTypes()
  {
    return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getGenericExceptionTypes(), TypeAnnotation.TypeAnnotationTarget.THROWS);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\Executable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */