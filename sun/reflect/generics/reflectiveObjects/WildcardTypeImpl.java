package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class WildcardTypeImpl
  extends LazyReflectiveObjectGenerator
  implements WildcardType
{
  private Type[] upperBounds;
  private Type[] lowerBounds;
  private FieldTypeSignature[] upperBoundASTs;
  private FieldTypeSignature[] lowerBoundASTs;
  
  private WildcardTypeImpl(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2, GenericsFactory paramGenericsFactory)
  {
    super(paramGenericsFactory);
    upperBoundASTs = paramArrayOfFieldTypeSignature1;
    lowerBoundASTs = paramArrayOfFieldTypeSignature2;
  }
  
  public static WildcardTypeImpl make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2, GenericsFactory paramGenericsFactory)
  {
    return new WildcardTypeImpl(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2, paramGenericsFactory);
  }
  
  private FieldTypeSignature[] getUpperBoundASTs()
  {
    assert (upperBounds == null);
    return upperBoundASTs;
  }
  
  private FieldTypeSignature[] getLowerBoundASTs()
  {
    assert (lowerBounds == null);
    return lowerBoundASTs;
  }
  
  public Type[] getUpperBounds()
  {
    if (upperBounds == null)
    {
      FieldTypeSignature[] arrayOfFieldTypeSignature = getUpperBoundASTs();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (int i = 0; i < arrayOfFieldTypeSignature.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfFieldTypeSignature[i].accept(localReifier);
        arrayOfType[i] = localReifier.getResult();
      }
      upperBounds = arrayOfType;
    }
    return (Type[])upperBounds.clone();
  }
  
  public Type[] getLowerBounds()
  {
    if (lowerBounds == null)
    {
      FieldTypeSignature[] arrayOfFieldTypeSignature = getLowerBoundASTs();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (int i = 0; i < arrayOfFieldTypeSignature.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfFieldTypeSignature[i].accept(localReifier);
        arrayOfType[i] = localReifier.getResult();
      }
      lowerBounds = arrayOfType;
    }
    return (Type[])lowerBounds.clone();
  }
  
  public String toString()
  {
    Type[] arrayOfType1 = getLowerBounds();
    Object localObject1 = arrayOfType1;
    StringBuilder localStringBuilder = new StringBuilder();
    if (arrayOfType1.length > 0)
    {
      localStringBuilder.append("? super ");
    }
    else
    {
      Type[] arrayOfType2 = getUpperBounds();
      if ((arrayOfType2.length > 0) && (!arrayOfType2[0].equals(Object.class)))
      {
        localObject1 = arrayOfType2;
        localStringBuilder.append("? extends ");
      }
      else
      {
        return "?";
      }
    }
    assert (localObject1.length > 0);
    int i = 1;
    for (Object localObject3 : localObject1)
    {
      if (i == 0) {
        localStringBuilder.append(" & ");
      }
      i = 0;
      localStringBuilder.append(((Type)localObject3).getTypeName());
    }
    return localStringBuilder.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramObject;
      return (Arrays.equals(getLowerBounds(), localWildcardType.getLowerBounds())) && (Arrays.equals(getUpperBounds(), localWildcardType.getUpperBounds()));
    }
    return false;
  }
  
  public int hashCode()
  {
    Type[] arrayOfType1 = getLowerBounds();
    Type[] arrayOfType2 = getUpperBounds();
    return Arrays.hashCode(arrayOfType1) ^ Arrays.hashCode(arrayOfType2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\reflectiveObjects\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */