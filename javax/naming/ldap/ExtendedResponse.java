package javax.naming.ldap;

import java.io.Serializable;

public abstract interface ExtendedResponse
  extends Serializable
{
  public abstract String getID();
  
  public abstract byte[] getEncodedValue();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\ExtendedResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */