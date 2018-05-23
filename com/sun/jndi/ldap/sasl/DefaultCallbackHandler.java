package com.sun.jndi.ldap.sasl;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

final class DefaultCallbackHandler
  implements CallbackHandler
{
  private char[] passwd;
  private String authenticationID;
  private String authRealm;
  
  DefaultCallbackHandler(String paramString1, Object paramObject, String paramString2)
    throws IOException
  {
    authenticationID = paramString1;
    authRealm = paramString2;
    if ((paramObject instanceof String))
    {
      passwd = ((String)paramObject).toCharArray();
    }
    else if ((paramObject instanceof char[]))
    {
      passwd = ((char[])((char[])paramObject).clone());
    }
    else if (paramObject != null)
    {
      String str = new String((byte[])paramObject, "UTF8");
      passwd = str.toCharArray();
    }
  }
  
  public void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException
  {
    for (int i = 0; i < paramArrayOfCallback.length; i++) {
      if ((paramArrayOfCallback[i] instanceof NameCallback))
      {
        ((NameCallback)paramArrayOfCallback[i]).setName(authenticationID);
      }
      else if ((paramArrayOfCallback[i] instanceof PasswordCallback))
      {
        ((PasswordCallback)paramArrayOfCallback[i]).setPassword(passwd);
      }
      else
      {
        Object localObject;
        if ((paramArrayOfCallback[i] instanceof RealmChoiceCallback))
        {
          localObject = ((RealmChoiceCallback)paramArrayOfCallback[i]).getChoices();
          int j = 0;
          if ((authRealm != null) && (authRealm.length() > 0))
          {
            j = -1;
            for (int k = 0; k < localObject.length; k++) {
              if (localObject[k].equals(authRealm)) {
                j = k;
              }
            }
            if (j == -1)
            {
              StringBuffer localStringBuffer = new StringBuffer();
              for (int m = 0; m < localObject.length; m++) {
                localStringBuffer.append(localObject[m] + ",");
              }
              throw new IOException("Cannot match 'java.naming.security.sasl.realm' property value, '" + authRealm + "' with choices " + localStringBuffer + "in RealmChoiceCallback");
            }
          }
          ((RealmChoiceCallback)paramArrayOfCallback[i]).setSelectedIndex(j);
        }
        else if ((paramArrayOfCallback[i] instanceof RealmCallback))
        {
          localObject = (RealmCallback)paramArrayOfCallback[i];
          if (authRealm != null)
          {
            ((RealmCallback)localObject).setText(authRealm);
          }
          else
          {
            String str = ((RealmCallback)localObject).getDefaultText();
            if (str != null) {
              ((RealmCallback)localObject).setText(str);
            } else {
              ((RealmCallback)localObject).setText("");
            }
          }
        }
        else
        {
          throw new UnsupportedCallbackException(paramArrayOfCallback[i]);
        }
      }
    }
  }
  
  void clearPassword()
  {
    if (passwd != null)
    {
      for (int i = 0; i < passwd.length; i++) {
        passwd[i] = '\000';
      }
      passwd = null;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    clearPassword();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\sasl\DefaultCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */