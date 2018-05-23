package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

public class ParameterizedTypeImpl
  implements ParameterizedType
{
  private final Type[] actualTypeArguments;
  private final Class<?> rawType;
  private final Type ownerType;
  
  private ParameterizedTypeImpl(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
  {
    actualTypeArguments = paramArrayOfType;
    rawType = paramClass;
    ownerType = (paramType != null ? paramType : paramClass.getDeclaringClass());
    validateConstructorArguments();
  }
  
  private void validateConstructorArguments()
  {
    TypeVariable[] arrayOfTypeVariable = rawType.getTypeParameters();
    if (arrayOfTypeVariable.length != actualTypeArguments.length) {
      throw new MalformedParameterizedTypeException();
    }
    for (int i = 0; i < actualTypeArguments.length; i++) {}
  }
  
  public static ParameterizedTypeImpl make(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
  {
    return new ParameterizedTypeImpl(paramClass, paramArrayOfType, paramType);
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
      return (Objects.equals(ownerType, localType1)) && (Objects.equals(rawType, localType2)) && (Arrays.equals(actualTypeArguments, localParameterizedType.getActualTypeArguments()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(actualTypeArguments) ^ Objects.hashCode(ownerType) ^ Objects.hashCode(rawType);
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
        localStringBuilder.append(localType.getTypeName());
        i = 0;
      }
      localStringBuilder.append(">");
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\reflectiveObjects\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */