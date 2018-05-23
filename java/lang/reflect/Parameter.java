package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;

public final class Parameter
  implements AnnotatedElement
{
  private final String name;
  private final int modifiers;
  private final Executable executable;
  private final int index;
  private volatile transient Type parameterTypeCache = null;
  private volatile transient Class<?> parameterClassCache = null;
  private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  Parameter(String paramString, int paramInt1, Executable paramExecutable, int paramInt2)
  {
    name = paramString;
    modifiers = paramInt1;
    executable = paramExecutable;
    index = paramInt2;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Parameter))
    {
      Parameter localParameter = (Parameter)paramObject;
      return (executable.equals(executable)) && (index == index);
    }
    return false;
  }
  
  public int hashCode()
  {
    return executable.hashCode() ^ index;
  }
  
  public boolean isNamePresent()
  {
    return (executable.hasRealParameterData()) && (name != null);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Type localType = getParameterizedType();
    String str = localType.getTypeName();
    localStringBuilder.append(Modifier.toString(getModifiers()));
    if (0 != modifiers) {
      localStringBuilder.append(' ');
    }
    if (isVarArgs()) {
      localStringBuilder.append(str.replaceFirst("\\[\\]$", "..."));
    } else {
      localStringBuilder.append(str);
    }
    localStringBuilder.append(' ');
    localStringBuilder.append(getName());
    return localStringBuilder.toString();
  }
  
  public Executable getDeclaringExecutable()
  {
    return executable;
  }
  
  public int getModifiers()
  {
    return modifiers;
  }
  
  public String getName()
  {
    if ((name == null) || (name.equals(""))) {
      return "arg" + index;
    }
    return name;
  }
  
  String getRealName()
  {
    return name;
  }
  
  public Type getParameterizedType()
  {
    Type localType = parameterTypeCache;
    if (null == localType)
    {
      localType = executable.getAllGenericParameterTypes()[index];
      parameterTypeCache = localType;
    }
    return localType;
  }
  
  public Class<?> getType()
  {
    Class localClass = parameterClassCache;
    if (null == localClass)
    {
      localClass = executable.getParameterTypes()[index];
      parameterClassCache = localClass;
    }
    return localClass;
  }
  
  public AnnotatedType getAnnotatedType()
  {
    return executable.getAnnotatedParameterTypes()[index];
  }
  
  public boolean isImplicit()
  {
    return Modifier.isMandated(getModifiers());
  }
  
  public boolean isSynthetic()
  {
    return Modifier.isSynthetic(getModifiers());
  }
  
  public boolean isVarArgs()
  {
    return (executable.isVarArgs()) && (index == executable.getParameterCount() - 1);
  }
  
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
    return executable.getParameterAnnotations()[index];
  }
  
  public <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass)
  {
    return getAnnotation(paramClass);
  }
  
  public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass)
  {
    return getAnnotationsByType(paramClass);
  }
  
  public Annotation[] getAnnotations()
  {
    return getDeclaredAnnotations();
  }
  
  private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
  {
    if (null == declaredAnnotations)
    {
      declaredAnnotations = new HashMap();
      Annotation[] arrayOfAnnotation = getDeclaredAnnotations();
      for (int i = 0; i < arrayOfAnnotation.length; i++) {
        declaredAnnotations.put(arrayOfAnnotation[i].annotationType(), arrayOfAnnotation[i]);
      }
    }
    return declaredAnnotations;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */