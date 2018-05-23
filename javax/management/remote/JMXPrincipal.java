package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.security.Principal;

public class JMXPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -4184480100214577411L;
  private String name;
  
  public JMXPrincipal(String paramString)
  {
    validate(paramString);
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return "JMXPrincipal:  " + name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof JMXPrincipal)) {
      return false;
    }
    JMXPrincipal localJMXPrincipal = (JMXPrincipal)paramObject;
    return getName().equals(localJMXPrincipal.getName());
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str = (String)localGetField.get("name", null);
    try
    {
      validate(str);
      name = str;
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new InvalidObjectException(localNullPointerException.getMessage());
    }
  }
  
  private static void validate(String paramString)
    throws NullPointerException
  {
    if (paramString == null) {
      throw new NullPointerException("illegal null input");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */