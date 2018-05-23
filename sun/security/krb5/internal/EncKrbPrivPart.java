package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKrbPrivPart
{
  public byte[] userData = null;
  public KerberosTime timestamp;
  public Integer usec;
  public Integer seqNumber;
  public HostAddress sAddress;
  public HostAddress rAddress;
  
  public EncKrbPrivPart(byte[] paramArrayOfByte, KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
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
  
  public EncKrbPrivPart(byte[] paramArrayOfByte)
    throws Asn1Exception, IOException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public EncKrbPrivPart(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    if (((paramDerValue.getTag() & 0x1F) != 28) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if (localDerValue1.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 0) {
      userData = localDerValue2.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    }
    timestamp = KerberosTime.parse(localDerValue1.getData(), (byte)1, true);
    if ((localDerValue1.getData().peekByte() & 0x1F) == 2)
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      usec = new Integer(localDerValue2.getData().getBigInteger().intValue());
    }
    else
    {
      usec = null;
    }
    if ((localDerValue1.getData().peekByte() & 0x1F) == 3)
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      seqNumber = new Integer(localDerValue2.getData().getBigInteger().intValue());
    }
    else
    {
      seqNumber = null;
    }
    sAddress = HostAddress.parse(localDerValue1.getData(), (byte)4, false);
    if (localDerValue1.getData().available() > 0) {
      rAddress = HostAddress.parse(localDerValue1.getData(), (byte)5, true);
    }
    if (localDerValue1.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.putOctetString(userData);
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
    if (timestamp != null) {
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), timestamp.asn1Encode());
    }
    if (usec != null)
    {
      localDerOutputStream1 = new DerOutputStream();
      localDerOutputStream1.putInteger(BigInteger.valueOf(usec.intValue()));
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream1);
    }
    if (seqNumber != null)
    {
      localDerOutputStream1 = new DerOutputStream();
      localDerOutputStream1.putInteger(BigInteger.valueOf(seqNumber.longValue()));
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream1);
    }
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), sAddress.asn1Encode());
    if (rAddress != null) {
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), rAddress.asn1Encode());
    }
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write(DerValue.createTag((byte)64, true, (byte)28), localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\EncKrbPrivPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */