package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.UnsolicitedNotification;

final class UnsolicitedResponseImpl
  implements UnsolicitedNotification
{
  private String oid;
  private String[] referrals;
  private byte[] extensionValue;
  private NamingException exception;
  private Control[] controls;
  private static final long serialVersionUID = 5913778898401784775L;
  
  UnsolicitedResponseImpl(String paramString1, byte[] paramArrayOfByte, Vector<Vector<String>> paramVector, int paramInt, String paramString2, String paramString3, Control[] paramArrayOfControl)
  {
    oid = paramString1;
    extensionValue = paramArrayOfByte;
    if ((paramVector != null) && (paramVector.size() > 0))
    {
      int i = paramVector.size();
      referrals = new String[i];
      for (int j = 0; j < i; j++) {
        referrals[j] = ((String)((Vector)paramVector.elementAt(j)).elementAt(0));
      }
    }
    exception = LdapCtx.mapErrorCode(paramInt, paramString2);
    controls = paramArrayOfControl;
  }
  
  public String getID()
  {
    return oid;
  }
  
  public byte[] getEncodedValue()
  {
    return extensionValue;
  }
  
  public String[] getReferrals()
  {
    return referrals;
  }
  
  public NamingException getException()
  {
    return exception;
  }
  
  public Control[] getControls()
    throws NamingException
  {
    return controls;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\UnsolicitedResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */