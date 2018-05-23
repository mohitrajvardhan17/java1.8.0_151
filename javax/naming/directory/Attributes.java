package javax.naming.directory;

import java.io.Serializable;
import javax.naming.NamingEnumeration;

public abstract interface Attributes
  extends Cloneable, Serializable
{
  public abstract boolean isCaseIgnored();
  
  public abstract int size();
  
  public abstract Attribute get(String paramString);
  
  public abstract NamingEnumeration<? extends Attribute> getAll();
  
  public abstract NamingEnumeration<String> getIDs();
  
  public abstract Attribute put(String paramString, Object paramObject);
  
  public abstract Attribute put(Attribute paramAttribute);
  
  public abstract Attribute remove(String paramString);
  
  public abstract Object clone();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */