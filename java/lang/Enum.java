package java.lang;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

public abstract class Enum<E extends Enum<E>>
  implements Comparable<E>, Serializable
{
  private final String name;
  private final int ordinal;
  
  public final String name()
  {
    return name;
  }
  
  public final int ordinal()
  {
    return ordinal;
  }
  
  protected Enum(String paramString, int paramInt)
  {
    name = paramString;
    ordinal = paramInt;
  }
  
  public String toString()
  {
    return name;
  }
  
  public final boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  protected final Object clone()
    throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException();
  }
  
  public final int compareTo(E paramE)
  {
    E ? = paramE;
    Enum localEnum = this;
    if ((localEnum.getClass() != ?.getClass()) && (localEnum.getDeclaringClass() != ?.getDeclaringClass())) {
      throw new ClassCastException();
    }
    return ordinal - ordinal;
  }
  
  public final Class<E> getDeclaringClass()
  {
    Class localClass1 = getClass();
    Class localClass2 = localClass1.getSuperclass();
    return localClass2 == Enum.class ? localClass1 : localClass2;
  }
  
  public static <T extends Enum<T>> T valueOf(Class<T> paramClass, String paramString)
  {
    Enum localEnum = (Enum)paramClass.enumConstantDirectory().get(paramString);
    if (localEnum != null) {
      return localEnum;
    }
    if (paramString == null) {
      throw new NullPointerException("Name is null");
    }
    throw new IllegalArgumentException("No enum constant " + paramClass.getCanonicalName() + "." + paramString);
  }
  
  protected final void finalize() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    throw new InvalidObjectException("can't deserialize enum");
  }
  
  private void readObjectNoData()
    throws ObjectStreamException
  {
    throw new InvalidObjectException("can't deserialize enum");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Enum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */