package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public final class AnnotationSupport
{
  private static final JavaLangAccess LANG_ACCESS = ;
  
  public AnnotationSupport() {}
  
  public static <A extends Annotation> A[] getDirectlyAndIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    Annotation localAnnotation = (Annotation)paramMap.get(paramClass);
    if (localAnnotation != null) {
      localArrayList.add(localAnnotation);
    }
    Annotation[] arrayOfAnnotation1 = getIndirectlyPresent(paramMap, paramClass);
    if ((arrayOfAnnotation1 != null) && (arrayOfAnnotation1.length != 0))
    {
      int i = (localAnnotation == null) || (containerBeforeContainee(paramMap, paramClass)) ? 1 : 0;
      localArrayList.addAll(i != 0 ? 0 : 1, Arrays.asList(arrayOfAnnotation1));
    }
    Annotation[] arrayOfAnnotation2 = (Annotation[])Array.newInstance(paramClass, localArrayList.size());
    return (Annotation[])localArrayList.toArray(arrayOfAnnotation2);
  }
  
  private static <A extends Annotation> A[] getIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass)
  {
    Repeatable localRepeatable = (Repeatable)paramClass.getDeclaredAnnotation(Repeatable.class);
    if (localRepeatable == null) {
      return null;
    }
    Class localClass = localRepeatable.value();
    Annotation localAnnotation = (Annotation)paramMap.get(localClass);
    if (localAnnotation == null) {
      return null;
    }
    Annotation[] arrayOfAnnotation = getValueArray(localAnnotation);
    checkTypes(arrayOfAnnotation, localAnnotation, paramClass);
    return arrayOfAnnotation;
  }
  
  private static <A extends Annotation> boolean containerBeforeContainee(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass)
  {
    Class localClass1 = ((Repeatable)paramClass.getDeclaredAnnotation(Repeatable.class)).value();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Class localClass2 = (Class)localIterator.next();
      if (localClass2 == localClass1) {
        return true;
      }
      if (localClass2 == paramClass) {
        return false;
      }
    }
    return false;
  }
  
  public static <A extends Annotation> A[] getAssociatedAnnotations(Map<Class<? extends Annotation>, Annotation> paramMap, Class<?> paramClass, Class<A> paramClass1)
  {
    Objects.requireNonNull(paramClass);
    Annotation[] arrayOfAnnotation = getDirectlyAndIndirectlyPresent(paramMap, paramClass1);
    if (AnnotationType.getInstance(paramClass1).isInherited()) {
      for (Class localClass = paramClass.getSuperclass(); (arrayOfAnnotation.length == 0) && (localClass != null); localClass = localClass.getSuperclass()) {
        arrayOfAnnotation = getDirectlyAndIndirectlyPresent(LANG_ACCESS.getDeclaredAnnotationMap(localClass), paramClass1);
      }
    }
    return arrayOfAnnotation;
  }
  
  private static <A extends Annotation> A[] getValueArray(Annotation paramAnnotation)
  {
    try
    {
      Class localClass = paramAnnotation.annotationType();
      AnnotationType localAnnotationType = AnnotationType.getInstance(localClass);
      if (localAnnotationType == null) {
        throw invalidContainerException(paramAnnotation, null);
      }
      Method localMethod = (Method)localAnnotationType.members().get("value");
      if (localMethod == null) {
        throw invalidContainerException(paramAnnotation, null);
      }
      localMethod.setAccessible(true);
      Annotation[] arrayOfAnnotation = (Annotation[])localMethod.invoke(paramAnnotation, new Object[0]);
      return arrayOfAnnotation;
    }
    catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|ClassCastException localIllegalAccessException)
    {
      throw invalidContainerException(paramAnnotation, localIllegalAccessException);
    }
  }
  
  private static AnnotationFormatError invalidContainerException(Annotation paramAnnotation, Throwable paramThrowable)
  {
    return new AnnotationFormatError(paramAnnotation + " is an invalid container for repeating annotations", paramThrowable);
  }
  
  private static <A extends Annotation> void checkTypes(A[] paramArrayOfA, Annotation paramAnnotation, Class<A> paramClass)
  {
    for (A ? : paramArrayOfA) {
      if (!paramClass.isInstance(?)) {
        throw new AnnotationFormatError(String.format("%s is an invalid container for repeating annotations of type: %s", new Object[] { paramAnnotation, paramClass }));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\AnnotationSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */