package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

class ParameterizedTypeImpl
  implements ParameterizedType
{
  private Type[] actualTypeArguments;
  private Class<?> rawType;
  private Type ownerType;
  
  ParameterizedTypeImpl(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
  {
    actualTypeArguments = paramArrayOfType;
    rawType = paramClass;
    if (paramType != null) {
      ownerType = paramType;
    } else {
      ownerType = paramClass.getDeclaringClass();
    }
    validateConstructorArguments();
  }
  
  private void validateConstructorArguments()
  {
    TypeVariable[] arrayOfTypeVariable = rawType.getTypeParameters();
    if (arrayOfTypeVariable.length != actualTypeArguments.length) {
      throw new MalformedParameterizedTypeException();
    }
  }
  
  public Type[] getActualTypeArguments()
  {
    return (Type[])actualTypeArguments.clone();
  }
  
  public Class<?> getRawType()
  {
    return rawType;
  }
  
  public Type getOwnerType()
  {
    return ownerType;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ParameterizedType))
    {
      ParameterizedType localParameterizedType = (ParameterizedType)paramObject;
      if (this == localParameterizedType) {
        return true;
      }
      Type localType1 = localParameterizedType.getOwnerType();
      Type localType2 = localParameterizedType.getRawType();
      return (ownerType == null ? localType1 == null : ownerType.equals(localType1)) && (rawType == null ? localType2 == null : rawType.equals(localType2)) && (Arrays.equals(actualTypeArguments, localParameterizedType.getActualTypeArguments()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(actualTypeArguments) ^ (ownerType == null ? 0 : ownerType.hashCode()) ^ (rawType == null ? 0 : rawType.hashCode());
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (ownerType != null)
    {
      if ((ownerType instanceof Class)) {
        localStringBuilder.append(((Class)ownerType).getName());
      } else {
        localStringBuilder.append(ownerType.toString());
      }
      localStringBuilder.append(".");
      if ((ownerType instanceof ParameterizedTypeImpl)) {
        localStringBuilder.append(rawType.getName().replace(ownerType).rawType.getName() + "$", ""));
      } else {
        localStringBuilder.append(rawType.getName());
      }
    }
    else
    {
      localStringBuilder.append(rawType.getName());
    }
    if ((actualTypeArguments != null) && (actualTypeArguments.length > 0))
    {
      localStringBuilder.append("<");
      int i = 1;
      for (Type localType : actualTypeArguments)
      {
        if (i == 0) {
          localStringBuilder.append(", ");
        }
        if ((localType instanceof Class)) {
          localStringBuilder.append(((Class)localType).getName());
        } else {
          localStringBuilder.append(localType.toString());
        }
        i = 0;
      }
      localStringBuilder.append(">");
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */