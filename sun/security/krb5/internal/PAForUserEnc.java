package sun.security.krb5.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAForUserEnc
{
  public final PrincipalName name;
  private final EncryptionKey key;
  public static final String AUTH_PACKAGE = "Kerberos";
  
  public PAForUserEnc(PrincipalName paramPrincipalName, EncryptionKey paramEncryptionKey)
  {
    name = paramPrincipalName;
    key = paramEncryptionKey;
  }
  
  public PAForUserEnc(DerValue paramDerValue, EncryptionKey paramEncryptionKey)
    throws Asn1Exception, KrbException, IOException
  {
    DerValue localDerValue = null;
    key = paramEncryptionKey;
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    PrincipalName localPrincipalName = null;
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      try
      {
        localPrincipalName = new PrincipalName(localDerValue.getData().getDerValue(), new Realm("PLACEHOLDER"));
      }
      catch (RealmException localRealmException1) {}
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 1) {
      try
      {
        Realm localRealm = new Realm(localDerValue.getData().getDerValue());
        name = new PrincipalName(localPrincipalName.getNameType(), localPrincipalName.getNameStrings(), localRealm);
      }
      catch (RealmException localRealmException2)
      {
        throw new IOException(localRealmException2);
      }
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) != 2) {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 3)
    {
      String str = new KerberosString(localDerValue.getData().getDerValue()).toString();
      if (!str.equalsIgnoreCase("Kerberos")) {
        throw new IOException("Incorrect auth-package");
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), name.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), name.getRealm().asn1Encode());
    try
    {
      Checksum localChecksum = new Checksum(65398, getS4UByteArray(), key, 17);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localChecksum.asn1Encode());
    }
    catch (KrbException localKrbException)
    {
      throw new IOException(localKrbException);
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putDerValue(new KerberosString("Kerberos").toDerValue());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public byte[] getS4UByteArray()
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localByteArrayOutputStream.write(new byte[4]);
      for (Object localObject2 : name.getNameStrings()) {
        localByteArrayOutputStream.write(((String)localObject2).getBytes("UTF-8"));
      }
      localByteArrayOutputStream.write(name.getRealm().toString().getBytes("UTF-8"));
      localByteArrayOutputStream.write("Kerberos".getBytes("UTF-8"));
      ??? = localByteArrayOutputStream.toByteArray();
      ??? = name.getNameType();
      ???[0] = ((byte)(??? & 0xFF));
      ???[1] = ((byte)(??? >> 8 & 0xFF));
      ???[2] = ((byte)(??? >> 16 & 0xFF));
      ???[3] = ((byte)(??? >> 24 & 0xFF));
      return (byte[])???;
    }
    catch (IOException localIOException)
    {
      throw new AssertionError("Cannot write ByteArrayOutputStream", localIOException);
    }
  }
  
  public String toString()
  {
    return "PA-FOR-USER: " + name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\PAForUserEnc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */