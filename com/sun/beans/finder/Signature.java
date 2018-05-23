package com.sun.beans.finder;

final class Signature
{
  private final Class<?> type;
  private final String name;
  private final Class<?>[] args;
  private volatile int code;
  
  Signature(Class<?> paramClass, Class<?>[] paramArrayOfClass)
  {
    this(paramClass, null, paramArrayOfClass);
  }
  
  Signature(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
  {
    type = paramClass;
    name = paramString;
    args = paramArrayOfClass;
  }
  
  Class<?> getType()
  {
    return type;
  }
  
  String getName()
  {
    return name;
  }
  
  Class<?>[] getArgs()
  {
    return args;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Signature))
    {
      Signature localSignature = (Signature)paramObject;
      return (isEqual(type, type)) && (isEqual(name, name)) && (isEqual(args, args));
    }
    return false;
  }
  
  private static boolean isEqual(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
  
  private static boolean isEqual(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
  {
    if ((paramArrayOfClass1 == null) || (paramArrayOfClass2 == null)) {
      return paramArrayOfClass1 == paramArrayOfClass2;
    }
    if (paramArrayOfClass1.length != paramArrayOfClass2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfClass1.length; i++) {
      if (!isEqual(paramArrayOfClass1[i], paramArrayOfClass2[i])) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    if (code == 0)
    {
      int i = 17;
      i = addHashCode(i, type);
      i = addHashCode(i, name);
      if (args != null) {
        for (Class localClass : args) {
          i = addHashCode(i, localClass);
        }
      }
      code = i;
    }
    return code;
  }
  
  private static int addHashCode(int paramInt, Object paramObject)
  {
    paramInt *= 37;
    return paramObject != null ? paramInt + paramObject.hashCode() : paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\Signature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */