package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl
  implements WildcardType
{
  private final Type[] ub;
  private final Type[] lb;
  
  public WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
  {
    ub = paramArrayOfType1;
    lb = paramArrayOfType2;
  }
  
  public Type[] getUpperBounds()
  {
    return ub;
  }
  
  public Type[] getLowerBounds()
  {
    return lb;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(lb) ^ Arrays.hashCode(ub);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramObject;
      return (Arrays.equals(localWildcardType.getLowerBounds(), lb)) && (Arrays.equals(localWildcardType.getUpperBounds(), ub));
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */