package javax.management;

import java.io.Serializable;

public class Attribute
  implements Serializable
{
  private static final long serialVersionUID = 2484220110589082382L;
  private String name;
  private Object value = null;
  
  public Attribute(String paramString, Object paramObject)
  {
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null "));
    }
    name = paramString;
    value = paramObject;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Attribute)) {
      return false;
    }
    Attribute localAttribute = (Attribute)paramObject;
    if (value == null)
    {
      if (localAttribute.getValue() == null) {
        return name.equals(localAttribute.getName());
      }
      return false;
    }
    return (name.equals(localAttribute.getName())) && (value.equals(localAttribute.getValue()));
  }
  
  public int hashCode()
  {
    return name.hashCode() ^ (value == null ? 0 : value.hashCode());
  }
  
  public String toString()
  {
    return getName() + " = " + getValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */