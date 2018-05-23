package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.util.DerValue;

public final class KerberosPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -7374788026156829911L;
  public static final int KRB_NT_UNKNOWN = 0;
  public static final int KRB_NT_PRINCIPAL = 1;
  public static final int KRB_NT_SRV_INST = 2;
  public static final int KRB_NT_SRV_HST = 3;
  public static final int KRB_NT_SRV_XHST = 4;
  public static final int KRB_NT_UID = 5;
  private transient String fullName;
  private transient String realm;
  private transient int nameType;
  
  public KerberosPrincipal(String paramString)
  {
    this(paramString, 1);
  }
  
  public KerberosPrincipal(String paramString, int paramInt)
  {
    PrincipalName localPrincipalName = null;
    try
    {
      localPrincipalName = new PrincipalName(paramString, paramInt);
    }
    catch (KrbException localKrbException)
    {
      throw new IllegalArgumentException(localKrbException.getMessage());
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
          throw new SecurityException("Cannot read realm info");
        }
      }
    }
    nameType = paramInt;
    fullName = localPrincipalName.toString();
    realm = localPrincipalName.getRealmString();
  }
  
  public String getRealm()
  {
    return realm;
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof KerberosPrincipal)) {
      return false;
    }
    String str1 = getName();
    String str2 = ((KerberosPrincipal)paramObject).getName();
    return str1.equals(str2);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    try
    {
      PrincipalName localPrincipalName = new PrincipalName(fullName, nameType);
      paramObjectOutputStream.writeObject(localPrincipalName.asn1Encode());
      paramObjectOutputStream.writeObject(localPrincipalName.getRealm().asn1Encode());
    }
    catch (Exception localException)
    {
      throw new IOException(localException);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    byte[] arrayOfByte1 = (byte[])paramObjectInputStream.readObject();
    byte[] arrayOfByte2 = (byte[])paramObjectInputStream.readObject();
    try
    {
      Realm localRealm = new Realm(new DerValue(arrayOfByte2));
      PrincipalName localPrincipalName = new PrincipalName(new DerValue(arrayOfByte1), localRealm);
      realm = localRealm.toString();
      fullName = localPrincipalName.toString();
      nameType = localPrincipalName.getNameType();
    }
    catch (Exception localException)
    {
      throw new IOException(localException);
    }
  }
  
  public String getName()
  {
    return fullName;
  }
  
  public int getNameType()
  {
    return nameType;
  }
  
  public String toString()
  {
    return getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\KerberosPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */