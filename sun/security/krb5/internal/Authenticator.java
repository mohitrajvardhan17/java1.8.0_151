package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Authenticator
{
  public int authenticator_vno;
  public PrincipalName cname;
  Checksum cksum;
  public int cusec;
  public KerberosTime ctime;
  EncryptionKey subKey;
  Integer seqNumber;
  public AuthorizationData authorizationData;
  
  public Authenticator(PrincipalName paramPrincipalName, Checksum paramChecksum, int paramInt, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey, Integer paramInteger, AuthorizationData paramAuthorizationData)
  {
    authenticator_vno = 5;
    cname = paramPrincipalName;
    cksum = paramChecksum;
    cusec = paramInt;
    ctime = paramKerberosTime;
    subKey = paramEncryptionKey;
    seqNumber = paramInteger;
    authorizationData = paramAuthorizationData;
  }
  
  public Authenticator(byte[] paramArrayOfByte)
    throws Asn1Exception, IOException, KrbApErrException, RealmException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public Authenticator(DerValue paramDerValue)
    throws Asn1Exception, IOException, KrbApErrException, RealmException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, IOException, KrbApErrException, RealmException
  {
    if (((paramDerValue.getTag() & 0x1F) != 2) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if (localDerValue1.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) != 0) {
      throw new Asn1Exception(906);
    }
    authenticator_vno = localDerValue2.getData().getBigInteger().intValue();
    if (authenticator_vno != 5) {
      throw new KrbApErrException(39);
    }
    Realm localRealm = Realm.parse(localDerValue1.getData(), (byte)1, false);
    cname = PrincipalName.parse(localDerValue1.getData(), (byte)2, false, localRealm);
    cksum = Checksum.parse(localDerValue1.getData(), (byte)3, true);
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 4) {
      cusec = localDerValue2.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    ctime = KerberosTime.parse(localDerValue1.getData(), (byte)5, false);
    if (localDerValue1.getData().available() > 0)
    {
      subKey = EncryptionKey.parse(localDerValue1.getData(), (byte)6, true);
    }
    else
    {
      subKey = null;
      seqNumber = null;
      authorizationData = null;
    }
    if (localDerValue1.getData().available() > 0)
    {
      if ((localDerValue1.getData().peekByte() & 0x1F) == 7)
      {
        localDerValue2 = localDerValue1.getData().getDerValue();
        if ((localDerValue2.getTag() & 0x1F) == 7) {
          seqNumber = new Integer(localDerValue2.getData().getBigInteger().intValue());
        }
      }
    }
    else
    {
      seqNumber = null;
      authorizationData = null;
    }
    if (localDerValue1.getData().available() > 0) {
      authorizationData = AuthorizationData.parse(localDerValue1.getData(), (byte)8, true);
    } else {
      authorizationData = null;
    }
    if (localDerValue1.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    Vector localVector = new Vector();
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(authenticator_vno));
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1.toByteArray()));
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), cname.getRealm().asn1Encode()));
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), cname.asn1Encode()));
    if (cksum != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), cksum.asn1Encode()));
    }
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(cusec));
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), localDerOutputStream1.toByteArray()));
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), ctime.asn1Encode()));
    if (subKey != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), subKey.asn1Encode()));
    }
    if (seqNumber != null)
    {
      localDerOutputStream1 = new DerOutputStream();
      localDerOutputStream1.putInteger(BigInteger.valueOf(seqNumber.longValue()));
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), localDerOutputStream1.toByteArray()));
    }
    if (authorizationData != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), authorizationData.asn1Encode()));
    }
    DerValue[] arrayOfDerValue = new DerValue[localVector.size()];
    localVector.copyInto(arrayOfDerValue);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putSequence(arrayOfDerValue);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write(DerValue.createTag((byte)64, true, (byte)2), localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public final Checksum getChecksum()
  {
    return cksum;
  }
  
  public final Integer getSeqNumber()
  {
    return seqNumber;
  }
  
  public final EncryptionKey getSubKey()
  {
    return subKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */