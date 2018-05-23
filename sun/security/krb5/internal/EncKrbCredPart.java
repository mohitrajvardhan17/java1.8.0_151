package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKrbCredPart
{
  public KrbCredInfo[] ticketInfo = null;
  public KerberosTime timeStamp;
  private Integer nonce;
  private Integer usec;
  private HostAddress sAddress;
  private HostAddresses rAddress;
  
  public EncKrbCredPart(KrbCredInfo[] paramArrayOfKrbCredInfo, KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress, HostAddresses paramHostAddresses)
    throws IOException
  {
    if (paramArrayOfKrbCredInfo != null)
    {
      ticketInfo = new KrbCredInfo[paramArrayOfKrbCredInfo.length];
      for (int i = 0; i < paramArrayOfKrbCredInfo.length; i++)
      {
        if (paramArrayOfKrbCredInfo[i] == null) {
          throw new IOException("Cannot create a EncKrbCredPart");
        }
        ticketInfo[i] = ((KrbCredInfo)paramArrayOfKrbCredInfo[i].clone());
      }
    }
    timeStamp = paramKerberosTime;
    usec = paramInteger1;
    nonce = paramInteger2;
    sAddress = paramHostAddress;
    rAddress = paramHostAddresses;
  }
  
  public EncKrbCredPart(byte[] paramArrayOfByte)
    throws Asn1Exception, IOException, RealmException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public EncKrbCredPart(DerValue paramDerValue)
    throws Asn1Exception, IOException, RealmException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, IOException, RealmException
  {
    nonce = null;
    timeStamp = null;
    usec = null;
    sAddress = null;
    rAddress = null;
    if (((paramDerValue.getTag() & 0x1F) != 29) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if (localDerValue1.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 0)
    {
      DerValue[] arrayOfDerValue = localDerValue2.getData().getSequence(1);
      ticketInfo = new KrbCredInfo[arrayOfDerValue.length];
      for (int i = 0; i < arrayOfDerValue.length; i++) {
        ticketInfo[i] = new KrbCredInfo(arrayOfDerValue[i]);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    if ((localDerValue1.getData().available() > 0) && (((byte)localDerValue1.getData().peekByte() & 0x1F) == 1))
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      nonce = new Integer(localDerValue2.getData().getBigInteger().intValue());
    }
    if (localDerValue1.getData().available() > 0) {
      timeStamp = KerberosTime.parse(localDerValue1.getData(), (byte)2, true);
    }
    if ((localDerValue1.getData().available() > 0) && (((byte)localDerValue1.getData().peekByte() & 0x1F) == 3))
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      usec = new Integer(localDerValue2.getData().getBigInteger().intValue());
    }
    if (localDerValue1.getData().available() > 0) {
      sAddress = HostAddress.parse(localDerValue1.getData(), (byte)4, true);
    }
    if (localDerValue1.getData().available() > 0) {
      rAddress = HostAddresses.parse(localDerValue1.getData(), (byte)5, true);
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
    DerValue[] arrayOfDerValue = new DerValue[ticketInfo.length];
    for (int i = 0; i < ticketInfo.length; i++) {
      arrayOfDerValue[i] = new DerValue(ticketInfo[i].asn1Encode());
    }
    localDerOutputStream2.putSequence(arrayOfDerValue);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    if (nonce != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(BigInteger.valueOf(nonce.intValue()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    }
    if (timeStamp != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), timeStamp.asn1Encode());
    }
    if (usec != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(BigInteger.valueOf(usec.intValue()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
    }
    if (sAddress != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), sAddress.asn1Encode());
    }
    if (rAddress != null) {
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), rAddress.asn1Encode());
    }
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.write(DerValue.createTag((byte)64, true, (byte)29), localDerOutputStream2);
    return localDerOutputStream1.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\EncKrbCredPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */