package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import javax.naming.ldap.Control;

class SimpleClientId
  extends ClientId
{
  private final String username;
  private final Object passwd;
  private final int myHash;
  
  SimpleClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3, String paramString4, Object paramObject)
  {
    super(paramInt1, paramString1, paramInt2, paramString2, paramArrayOfControl, paramOutputStream, paramString3);
    username = paramString4;
    int i = 0;
    if (paramObject == null)
    {
      passwd = null;
    }
    else if ((paramObject instanceof byte[]))
    {
      passwd = ((byte[])paramObject).clone();
      i = Arrays.hashCode((byte[])paramObject);
    }
    else if ((paramObject instanceof char[]))
    {
      passwd = ((char[])paramObject).clone();
      i = Arrays.hashCode((char[])paramObject);
    }
    else
    {
      passwd = paramObject;
      i = paramObject.hashCode();
    }
    myHash = (super.hashCode() ^ (paramString4 != null ? paramString4.hashCode() : 0) ^ i);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof SimpleClientId))) {
      return false;
    }
    SimpleClientId localSimpleClientId = (SimpleClientId)paramObject;
    return (super.equals(paramObject)) && ((username == username) || ((username != null) && (username.equals(username)))) && ((passwd == passwd) || ((passwd != null) && (passwd != null) && ((((passwd instanceof String)) && (passwd.equals(passwd))) || (((passwd instanceof byte[])) && ((passwd instanceof byte[])) && (Arrays.equals((byte[])passwd, (byte[])passwd))) || (((passwd instanceof char[])) && ((passwd instanceof char[])) && (Arrays.equals((char[])passwd, (char[])passwd))))));
  }
  
  public int hashCode()
  {
    return myHash;
  }
  
  public String toString()
  {
    return super.toString() + ":" + username;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\SimpleClientId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */