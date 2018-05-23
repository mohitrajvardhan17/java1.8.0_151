package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBSafeBody
{
  public byte[] userData = null;
  public KerberosTime timestamp;
  public Integer usec;
  public Integer seqNumber;
  public HostAddress sAddress;
  public HostAddress rAddress;
  
  public KRBSafeBody(byte[] paramArrayOfByte, KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
  {
    if (paramArrayOfByte != null) {
      userData = ((byte[])paramArrayOfByte.clone());
    }
    timestamp = paramKerberosTime;
    usec = paramInteger1;
    seqNumber = paramInteger2;
    sAddress = paramHostAddress1;
    rAddress = paramHostAddress2;
  }
  
  public KRBSafeBody(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      userData = localDerValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    }
    timestamp = KerberosTime.parse(paramDerValue.getData(), (byte)1, true);
    if ((paramDerValue.getData().peekByte() & 0x1F) == 2)
    {
      localDerValue = paramDerValue.getData().getDerValue();
      usec = new Integer(localDerValue.getData().getBigInteger().intValue());
    }
    if ((paramDerValue.getData().peekByte() & 0x1F) == 3)
    {
      localDerValue = paramDerValue.getData().getDerValue();
      seqNumber = new Integer(localDerValue.getData().getBigInteger().intValue());
    }
    sAddress = HostAddress.parse(paramDerValue.getData(), (byte)4, false);
    if (paramDerValue.getData().available() > 0) {
      rAddress = HostAddress.parse(paramDerValue.getData(), (byte)5, true);
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOctetString(userData);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    if (timestamp != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), timestamp.asn1Encode());
    }
    if (usec != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(BigInteger.valueOf(usec.intValue()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    }
    if (seqNumber != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(BigInteger.valueOf(seqNumber.longValue()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
    }
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), sAddress.asn1Encode());
    if (rAddress != null) {
      localDerOutputStream2 = new DerOutputStream();
    }
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public static KRBSafeBody parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new KRBSafeBody(localDerValue2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KRBSafeBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */