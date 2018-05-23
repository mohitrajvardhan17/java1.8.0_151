package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import javax.naming.ldap.Control;

class DigestClientId
  extends SimpleClientId
{
  private static final String[] SASL_PROPS = { "java.naming.security.sasl.authorizationId", "java.naming.security.sasl.realm", "javax.security.sasl.qop", "javax.security.sasl.strength", "javax.security.sasl.reuse", "javax.security.sasl.server.authentication", "javax.security.sasl.maxbuffer", "javax.security.sasl.policy.noplaintext", "javax.security.sasl.policy.noactive", "javax.security.sasl.policy.nodictionary", "javax.security.sasl.policy.noanonymous", "javax.security.sasl.policy.forward", "javax.security.sasl.policy.credentials" };
  private final String[] propvals;
  private final int myHash;
  
  DigestClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3, String paramString4, Object paramObject, Hashtable<?, ?> paramHashtable)
  {
    super(paramInt1, paramString1, paramInt2, paramString2, paramArrayOfControl, paramOutputStream, paramString3, paramString4, paramObject);
    if (paramHashtable == null)
    {
      propvals = null;
    }
    else
    {
      propvals = new String[SASL_PROPS.length];
      for (int i = 0; i < SASL_PROPS.length; i++) {
        propvals[i] = ((String)paramHashtable.get(SASL_PROPS[i]));
      }
    }
    myHash = (super.hashCode() ^ Arrays.hashCode(propvals));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof DigestClientId)) {
      return false;
    }
    DigestClientId localDigestClientId = (DigestClientId)paramObject;
    return (myHash == myHash) && (super.equals(paramObject)) && (Arrays.equals(propvals, propvals));
  }
  
  public int hashCode()
  {
    return myHash;
  }
  
  public String toString()
  {
    if (propvals != null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < propvals.length; i++)
      {
        localStringBuffer.append(':');
        if (propvals[i] != null) {
          localStringBuffer.append(propvals[i]);
        }
      }
      return super.toString() + localStringBuffer.toString();
    }
    return super.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\DigestClientId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */