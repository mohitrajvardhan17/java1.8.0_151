package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBPriv
{
  public int pvno;
  public int msgType;
  public EncryptedData encPart;
  
  public KRBPriv(EncryptedData paramEncryptedData)
  {
    pvno = 5;
    msgType = 21;
    encPart = paramEncryptedData;
  }
  
  public KRBPriv(byte[] paramArrayOfByte)
    throws Asn1Exception, KrbApErrException, IOException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public KRBPriv(DerValue paramDerValue)
    throws Asn1Exception, KrbApErrException, IOException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, KrbApErrException, IOException
  {
    if (((paramDerValue.getTag() & 0x1F) != 21) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if (localDerValue1.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 0)
    {
      pvno = localDerValue2.getData().getBigInteger().intValue();
      if (pvno != 5) {
        throw new KrbApErrException(39);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 1)
    {
      msgType = localDerValue2.getData().getBigInteger().intValue();
      if (msgType != 21) {
        throw new KrbApErrException(40);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    encPart = EncryptedData.parse(localDerValue1.getData(), (byte)3, false);
    if (localDerValue1.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(pvno));
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(msgType));
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream1);
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), encPart.asn1Encode());
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write(DerValue.createTag((byte)64, true, (byte)21), localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KRBPriv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */