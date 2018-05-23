package java.util;

import java.util.function.Supplier;

public final class Objects
{
  private Objects()
  {
    throw new AssertionError("No java.util.Objects instances for you!");
  }
  
  public static boolean equals(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }
  
  public static boolean deepEquals(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return false;
    }
    return Arrays.deepEquals0(paramObject1, paramObject2);
  }
  
  public static int hashCode(Object paramObject)
  {
    return paramObject != null ? paramObject.hashCode() : 0;
  }
  
  public static int hash(Object... paramVarArgs)
  {
    return Arrays.hashCode(paramVarArgs);
  }
  
  public static String toString(Object paramObject)
  {
    return String.valueOf(paramObject);
  }
  
  public static String toString(Object paramObject, String paramString)
  {
    return paramObject != null ? paramObject.toString() : paramString;
  }
  
  public static <T> int compare(T paramT1, T paramT2, Comparator<? super T> paramComparator)
  {
    return paramT1 == paramT2 ? 0 : paramComparator.compare(paramT1, paramT2);
  }
  
  public static <T> T requireNonNull(T paramT)
  {
    if (paramT == null) {
      throw new NullPointerException();
    }
    return paramT;
  }
  
  public static <T> T requireNonNull(T paramT, String paramString)
  {
    if (paramT == null) {
      throw new NullPointerException(paramString);
    }
    return paramT;
  }
  
  public static boolean isNull(Object paramObject)
  {
    return paramObject == null;
  }
  
  public static boolean nonNull(Object paramObject)
  {
    return paramObject != null;
  }
  
  public static <T> T requireNonNull(T paramT, Supplier<String> paramSupplier)
  {
    if (paramT == null) {
      throw new NullPointerException((String)paramSupplier.get());
    }
    return paramT;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Objects.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */