package com.sun.beans;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl
  implements WildcardType
{
  private final Type[] upperBounds;
  private final Type[] lowerBounds;
  
  WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
  {
    upperBounds = paramArrayOfType1;
    lowerBounds = paramArrayOfType2;
  }
  
  public Type[] getUpperBounds()
  {
    return (Type[])upperBounds.clone();
  }
  
  public Type[] getLowerBounds()
  {
    return (Type[])lowerBounds.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramObject;
      return (Arrays.equals(upperBounds, localWildcardType.getUpperBounds())) && (Arrays.equals(lowerBounds, localWildcardType.getLowerBounds()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds);
  }
  
  public String toString()
  {
    Type[] arrayOfType;
    StringBuilder localStringBuilder;
    if (lowerBounds.length == 0)
    {
      if ((upperBounds.length == 0) || (Object.class == upperBounds[0])) {
        return "?";
      }
      arrayOfType = upperBounds;
      localStringBuilder = new StringBuilder("? extends ");
    }
    else
    {
      arrayOfType = lowerBounds;
      localStringBuilder = new StringBuilder("? super ");
    }
    for (int i = 0; i < arrayOfType.length; i++)
    {
      if (i > 0) {
        localStringBuilder.append(" & ");
      }
      localStringBuilder.append((arrayOfType[i] instanceof Class) ? ((Class)arrayOfType[i]).getName() : arrayOfType[i].toString());
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */