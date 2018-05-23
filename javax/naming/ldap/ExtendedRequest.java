package javax.naming.ldap;

import java.io.Serializable;
import javax.naming.NamingException;

public abstract interface ExtendedRequest
  extends Serializable
{
  public abstract String getID();
  
  public abstract byte[] getEncodedValue();
  
  public abstract ExtendedResponse createExtendedResponse(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\ExtendedRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */