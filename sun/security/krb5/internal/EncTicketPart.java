package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncTicketPart
{
  public TicketFlags flags;
  public EncryptionKey key;
  public PrincipalName cname;
  public TransitedEncoding transited;
  public KerberosTime authtime;
  public KerberosTime starttime;
  public KerberosTime endtime;
  public KerberosTime renewTill;
  public HostAddresses caddr;
  public AuthorizationData authorizationData;
  
  public EncTicketPart(TicketFlags paramTicketFlags, EncryptionKey paramEncryptionKey, PrincipalName paramPrincipalName, TransitedEncoding paramTransitedEncoding, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData)
  {
    flags = paramTicketFlags;
    key = paramEncryptionKey;
    cname = paramPrincipalName;
    transited = paramTransitedEncoding;
    authtime = paramKerberosTime1;
    starttime = paramKerberosTime2;
    endtime = paramKerberosTime3;
    renewTill = paramKerberosTime4;
    caddr = paramHostAddresses;
    authorizationData = paramAuthorizationData;
  }
  
  public EncTicketPart(byte[] paramArrayOfByte)
    throws Asn1Exception, KrbException, IOException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public EncTicketPart(DerValue paramDerValue)
    throws Asn1Exception, KrbException, IOException
  {
    init(paramDerValue);
  }
  
  private static String getHexBytes(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfByte[i] >> 4 & 0xF;
      int k = paramArrayOfByte[i] & 0xF;
      localStringBuffer.append(Integer.toHexString(j));
      localStringBuffer.append(Integer.toHexString(k));
      localStringBuffer.append(' ');
    }
    return localStringBuffer.toString();
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, IOException, RealmException
  {
    renewTill = null;
    caddr = null;
    authorizationData = null;
    if (((paramDerValue.getTag() & 0x1F) != 3) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue = paramDerValue.getData().getDerValue();
    if (localDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    flags = TicketFlags.parse(localDerValue.getData(), (byte)0, false);
    key = EncryptionKey.parse(localDerValue.getData(), (byte)1, false);
    Realm localRealm = Realm.parse(localDerValue.getData(), (byte)2, false);
    cname = PrincipalName.parse(localDerValue.getData(), (byte)3, false, localRealm);
    transited = TransitedEncoding.parse(localDerValue.getData(), (byte)4, false);
    authtime = KerberosTime.parse(localDerValue.getData(), (byte)5, false);
    starttime = KerberosTime.parse(localDerValue.getData(), (byte)6, true);
    endtime = KerberosTime.parse(localDerValue.getData(), (byte)7, false);
    if (localDerValue.getData().available() > 0) {
      renewTill = KerberosTime.parse(localDerValue.getData(), (byte)8, true);
    }
    if (localDerValue.getData().available() > 0) {
      caddr = HostAddresses.parse(localDerValue.getData(), (byte)9, true);
    }
    if (localDerValue.getData().available() > 0) {
      authorizationData = AuthorizationData.parse(localDerValue.getData(), (byte)10, true);
    }
    if (localDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), flags.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), key.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), cname.getRealm().asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), cname.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), transited.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), authtime.asn1Encode());
    if (starttime != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), starttime.asn1Encode());
    }
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), endtime.asn1Encode());
    if (renewTill != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), renewTill.asn1Encode());
    }
    if (caddr != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), caddr.asn1Encode());
    }
    if (authorizationData != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), authorizationData.asn1Encode());
    }
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.write(DerValue.createTag((byte)64, true, (byte)3), localDerOutputStream2);
    return localDerOutputStream1.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\EncTicketPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */