package java.text;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract interface AttributedCharacterIterator
  extends CharacterIterator
{
  public abstract int getRunStart();
  
  public abstract int getRunStart(Attribute paramAttribute);
  
  public abstract int getRunStart(Set<? extends Attribute> paramSet);
  
  public abstract int getRunLimit();
  
  public abstract int getRunLimit(Attribute paramAttribute);
  
  public abstract int getRunLimit(Set<? extends Attribute> paramSet);
  
  public abstract Map<Attribute, Object> getAttributes();
  
  public abstract Object getAttribute(Attribute paramAttribute);
  
  public abstract Set<Attribute> getAllAttributeKeys();
  
  public static class Attribute
    implements Serializable
  {
    private String name;
    private static final Map<String, Attribute> instanceMap = new HashMap(7);
    public static final Attribute LANGUAGE = new Attribute("language");
    public static final Attribute READING = new Attribute("reading");
    public static final Attribute INPUT_METHOD_SEGMENT = new Attribute("input_method_segment");
    private static final long serialVersionUID = -9142742483513960612L;
    
    protected Attribute(String paramString)
    {
      name = paramString;
      if (getClass() == Attribute.class) {
        instanceMap.put(paramString, this);
      }
    }
    
    public final boolean equals(Object paramObject)
    {
      return super.equals(paramObject);
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
    
    public String toString()
    {
      return getClass().getName() + "(" + name + ")";
    }
    
    protected String getName()
    {
      return name;
    }
    
    protected Object readResolve()
      throws InvalidObjectException
    {
      if (getClass() != Attribute.class) {
        throw new InvalidObjectException("subclass didn't correctly implement readResolve");
      }
      Attribute localAttribute = (Attribute)instanceMap.get(getName());
      if (localAttribute != null) {
        return localAttribute;
      }
      throw new InvalidObjectException("unknown attribute name");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\AttributedCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */