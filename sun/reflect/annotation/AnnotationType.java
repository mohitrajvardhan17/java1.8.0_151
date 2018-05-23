package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class AnnotationType
{
  private final Map<String, Class<?>> memberTypes;
  private final Map<String, Object> memberDefaults;
  private final Map<String, Method> members;
  private final RetentionPolicy retention;
  private final boolean inherited;
  
  public static AnnotationType getInstance(Class<? extends Annotation> paramClass)
  {
    JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
    AnnotationType localAnnotationType = localJavaLangAccess.getAnnotationType(paramClass);
    if (localAnnotationType == null)
    {
      localAnnotationType = new AnnotationType(paramClass);
      if (!localJavaLangAccess.casAnnotationType(paramClass, null, localAnnotationType))
      {
        localAnnotationType = localJavaLangAccess.getAnnotationType(paramClass);
        assert (localAnnotationType != null);
      }
    }
    return localAnnotationType;
  }
  
  private AnnotationType(final Class<? extends Annotation> paramClass)
  {
    if (!paramClass.isAnnotation()) {
      throw new IllegalArgumentException("Not an annotation type");
    }
    Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Method[] run()
      {
        return paramClass.getDeclaredMethods();
      }
    });
    memberTypes = new HashMap(arrayOfMethod.length + 1, 1.0F);
    memberDefaults = new HashMap(0);
    members = new HashMap(arrayOfMethod.length + 1, 1.0F);
    for (Object localObject2 : arrayOfMethod) {
      if ((Modifier.isPublic(((Method)localObject2).getModifiers())) && (Modifier.isAbstract(((Method)localObject2).getModifiers())) && (!((Method)localObject2).isSynthetic()))
      {
        if (((Method)localObject2).getParameterTypes().length != 0) {
          throw new IllegalArgumentException(localObject2 + " has params");
        }
        String str = ((Method)localObject2).getName();
        Class localClass = ((Method)localObject2).getReturnType();
        memberTypes.put(str, invocationHandlerReturnType(localClass));
        members.put(str, localObject2);
        Object localObject3 = ((Method)localObject2).getDefaultValue();
        if (localObject3 != null) {
          memberDefaults.put(str, localObject3);
        }
      }
    }
    if ((paramClass != Retention.class) && (paramClass != Inherited.class))
    {
      ??? = SharedSecrets.getJavaLangAccess();
      Map localMap = AnnotationParser.parseSelectAnnotations(((JavaLangAccess)???).getRawClassAnnotations(paramClass), ((JavaLangAccess)???).getConstantPool(paramClass), paramClass, new Class[] { Retention.class, Inherited.class });
      Retention localRetention = (Retention)localMap.get(Retention.class);
      retention = (localRetention == null ? RetentionPolicy.CLASS : localRetention.value());
      inherited = localMap.containsKey(Inherited.class);
    }
    else
    {
      retention = RetentionPolicy.RUNTIME;
      inherited = false;
    }
  }
  
  public static Class<?> invocationHandlerReturnType(Class<?> paramClass)
  {
    if (paramClass == Byte.TYPE) {
      return Byte.class;
    }
    if (paramClass == Character.TYPE) {
      return Character.class;
    }
    if (paramClass == Double.TYPE) {
      return Double.class;
    }
    if (paramClass == Float.TYPE) {
      return Float.class;
    }
    if (paramClass == Integer.TYPE) {
      return Integer.class;
    }
    if (paramClass == Long.TYPE) {
      return Long.class;
    }
    if (paramClass == Short.TYPE) {
      return Short.class;
    }
    if (paramClass == Boolean.TYPE) {
      return Boolean.class;
    }
    return paramClass;
  }
  
  public Map<String, Class<?>> memberTypes()
  {
    return memberTypes;
  }
  
  public Map<String, Method> members()
  {
    return members;
  }
  
  public Map<String, Object> memberDefaults()
  {
    return memberDefaults;
  }
  
  public RetentionPolicy retention()
  {
    return retention;
  }
  
  public boolean isInherited()
  {
    return inherited;
  }
  
  public String toString()
  {
    return "Annotation Type:\n   Member types: " + memberTypes + "\n   Member defaults: " + memberDefaults + "\n   Retention policy: " + retention + "\n   Inherited: " + inherited;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\AnnotationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */