package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

final class GenericArrayTypeImpl
  implements GenericArrayType
{
  private Type genericComponentType;
  
  GenericArrayTypeImpl(Type paramType)
  {
    assert (paramType != null);
    genericComponentType = paramType;
  }
  
  public Type getGenericComponentType()
  {
    return genericComponentType;
  }
  
  public String toString()
  {
    Type localType = getGenericComponentType();
    StringBuilder localStringBuilder = new StringBuilder();
    if ((localType instanceof Class)) {
      localStringBuilder.append(((Class)localType).getName());
    } else {
      localStringBuilder.append(localType.toString());
    }
    localStringBuilder.append("[]");
    return localStringBuilder.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof GenericArrayType))
    {
      GenericArrayType localGenericArrayType = (GenericArrayType)paramObject;
      Type localType = localGenericArrayType.getGenericComponentType();
      return genericComponentType.equals(localType);
    }
    return false;
  }
  
  public int hashCode()
  {
    return genericComponentType.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\GenericArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */