package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class APReq
{
  public int pvno;
  public int msgType;
  public APOptions apOptions;
  public Ticket ticket;
  public EncryptedData authenticator;
  
  public APReq(APOptions paramAPOptions, Ticket paramTicket, EncryptedData paramEncryptedData)
  {
    pvno = 5;
    msgType = 14;
    apOptions = paramAPOptions;
    ticket = paramTicket;
    authenticator = paramEncryptedData;
  }
  
  public APReq(byte[] paramArrayOfByte)
    throws Asn1Exception, IOException, KrbApErrException, RealmException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public APReq(DerValue paramDerValue)
    throws Asn1Exception, IOException, KrbApErrException, RealmException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, IOException, KrbApErrException, RealmException
  {
    if (((paramDerValue.getTag() & 0x1F) != 14) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
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
    pvno = localDerValue2.getData().getBigInteger().intValue();
    if (pvno != 5) {
      throw new KrbApErrException(39);
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) != 1) {
      throw new Asn1Exception(906);
    }
    msgType = localDerValue2.getData().getBigInteger().intValue();
    if (msgType != 14) {
      throw new KrbApErrException(40);
    }
    apOptions = APOptions.parse(localDerValue1.getData(), (byte)2, false);
    ticket = Ticket.parse(localDerValue1.getData(), (byte)3, false);
    authenticator = EncryptedData.parse(localDerValue1.getData(), (byte)4, false);
    if (localDerValue1.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(BigInteger.valueOf(pvno));
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(BigInteger.valueOf(msgType));
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), apOptions.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), ticket.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), authenticator.asn1Encode());
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write(DerValue.createTag((byte)64, true, (byte)14), localDerOutputStream2);
    return localDerOutputStream3.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\APReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */