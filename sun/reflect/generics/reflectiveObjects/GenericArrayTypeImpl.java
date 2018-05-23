package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

public class GenericArrayTypeImpl
  implements GenericArrayType
{
  private final Type genericComponentType;
  
  private GenericArrayTypeImpl(Type paramType)
  {
    genericComponentType = paramType;
  }
  
  public static GenericArrayTypeImpl make(Type paramType)
  {
    return new GenericArrayTypeImpl(paramType);
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
      return Objects.equals(genericComponentType, localGenericArrayType.getGenericComponentType());
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(genericComponentType);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\reflectiveObjects\GenericArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */