package sun.security.jgss.krb5;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Provider;
import java.util.Locale;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;

public class Krb5NameElement
  implements GSSNameSpi
{
  private PrincipalName krb5PrincipalName;
  private String gssNameStr = null;
  private Oid gssNameType = null;
  private static String CHAR_ENCODING = "UTF-8";
  
  private Krb5NameElement(PrincipalName paramPrincipalName, String paramString, Oid paramOid)
  {
    krb5PrincipalName = paramPrincipalName;
    gssNameStr = paramString;
    gssNameType = paramOid;
  }
  
  static Krb5NameElement getInstance(String paramString, Oid paramOid)
    throws GSSException
  {
    if (paramOid == null) {
      paramOid = Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL;
    } else if ((!paramOid.equals(GSSName.NT_USER_NAME)) && (!paramOid.equals(GSSName.NT_HOSTBASED_SERVICE)) && (!paramOid.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL)) && (!paramOid.equals(GSSName.NT_EXPORT_NAME))) {
      throw new GSSException(4, -1, paramOid.toString() + " is an unsupported nametype");
    }
    PrincipalName localPrincipalName;
    try
    {
      if ((paramOid.equals(GSSName.NT_EXPORT_NAME)) || (paramOid.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL)))
      {
        localPrincipalName = new PrincipalName(paramString, 1);
      }
      else
      {
        String[] arrayOfString = getComponents(paramString);
        if (paramOid.equals(GSSName.NT_USER_NAME))
        {
          localPrincipalName = new PrincipalName(paramString, 1);
        }
        else
        {
          String str1 = null;
          String str2 = arrayOfString[0];
          if (arrayOfString.length >= 2) {
            str1 = arrayOfString[1];
          }
          String str3 = getHostBasedInstance(str2, str1);
          localPrincipalName = new PrincipalName(str3, 3);
        }
      }
    }
    catch (KrbException localKrbException)
    {
      throw new GSSException(3, -1, localKrbException.getMessage());
    }
    if ((localPrincipalName.isRealmDeduced()) && (!Realm.AUTODEDUCEREALM))
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        try
        {
          localSecurityManager.checkPermission(new ServicePermission("@" + localPrincipalName.getRealmAsString(), "-"));
        }
        catch (SecurityException localSecurityException)
        {
          throw new GSSException(11);
        }
      }
    }
    return new Krb5NameElement(localPrincipalName, paramString, paramOid);
  }
  
  static Krb5NameElement getInstance(PrincipalName paramPrincipalName)
  {
    return new Krb5NameElement(paramPrincipalName, paramPrincipalName.getName(), Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
  }
  
  private static String[] getComponents(String paramString)
    throws GSSException
  {
    int i = paramString.lastIndexOf('@', paramString.length());
    if ((i > 0) && (paramString.charAt(i - 1) == '\\') && ((i - 2 < 0) || (paramString.charAt(i - 2) != '\\'))) {
      i = -1;
    }
    String[] arrayOfString;
    if (i > 0)
    {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      arrayOfString = new String[] { str1, str2 };
    }
    else
    {
      arrayOfString = new String[] { paramString };
    }
    return arrayOfString;
  }
  
  private static String getHostBasedInstance(String paramString1, String paramString2)
    throws GSSException
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString1);
    try
    {
      if (paramString2 == null) {
        paramString2 = InetAddress.getLocalHost().getHostName();
      }
    }
    catch (UnknownHostException localUnknownHostException) {}
    paramString2 = paramString2.toLowerCase(Locale.ENGLISH);
    localStringBuffer = localStringBuffer.append('/').append(paramString2);
    return localStringBuffer.toString();
  }
  
  public final PrincipalName getKrb5PrincipalName()
  {
    return krb5PrincipalName;
  }
  
  public boolean equals(GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    if (paramGSSNameSpi == this) {
      return true;
    }
    if ((paramGSSNameSpi instanceof Krb5NameElement))
    {
      Krb5NameElement localKrb5NameElement = (Krb5NameElement)paramGSSNameSpi;
      return krb5PrincipalName.getName().equals(krb5PrincipalName.getName());
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    try
    {
      if ((paramObject instanceof Krb5NameElement)) {
        return equals((Krb5NameElement)paramObject);
      }
    }
    catch (GSSException localGSSException) {}
    return false;
  }
  
  public int hashCode()
  {
    return 629 + krb5PrincipalName.getName().hashCode();
  }
  
  public byte[] export()
    throws GSSException
  {
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = krb5PrincipalName.getName().getBytes(CHAR_ENCODING);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return arrayOfByte;
  }
  
  public Oid getMechanism()
  {
    return Krb5MechFactory.GSS_KRB5_MECH_OID;
  }
  
  public String toString()
  {
    return gssNameStr;
  }
  
  public Oid getGSSNameType()
  {
    return gssNameType;
  }
  
  public Oid getStringNameType()
  {
    return gssNameType;
  }
  
  public boolean isAnonymousName()
  {
    return gssNameType.equals(GSSName.NT_ANONYMOUS);
  }
  
  public Provider getProvider()
  {
    return Krb5MechFactory.PROVIDER;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\Krb5NameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */