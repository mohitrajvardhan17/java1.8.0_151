package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBCred
{
  public Ticket[] tickets = null;
  public EncryptedData encPart;
  private int pvno;
  private int msgType;
  
  public KRBCred(Ticket[] paramArrayOfTicket, EncryptedData paramEncryptedData)
    throws IOException
  {
    pvno = 5;
    msgType = 22;
    if (paramArrayOfTicket != null)
    {
      tickets = new Ticket[paramArrayOfTicket.length];
      for (int i = 0; i < paramArrayOfTicket.length; i++)
      {
        if (paramArrayOfTicket[i] == null) {
          throw new IOException("Cannot create a KRBCred");
        }
        tickets[i] = ((Ticket)paramArrayOfTicket[i].clone());
      }
    }
    encPart = paramEncryptedData;
  }
  
  public KRBCred(byte[] paramArrayOfByte)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public KRBCred(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    if (((paramDerValue.getTag() & 0x1F) != 22) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
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
      if (msgType != 22) {
        throw new KrbApErrException(40);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 2)
    {
      DerValue localDerValue3 = localDerValue2.getData().getDerValue();
      if (localDerValue3.getTag() != 48) {
        throw new Asn1Exception(906);
      }
      Vector localVector = new Vector();
      while (localDerValue3.getData().available() > 0) {
        localVector.addElement(new Ticket(localDerValue3.getData().getDerValue()));
      }
      if (localVector.size() > 0)
      {
        tickets = new Ticket[localVector.size()];
        localVector.copyInto(tickets);
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
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(msgType));
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    for (int i = 0; i < tickets.length; i++) {
      localDerOutputStream1.write(tickets[i].asn1Encode());
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), encPart.asn1Encode());
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream3);
    localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write(DerValue.createTag((byte)64, true, (byte)22), localDerOutputStream2);
    return localDerOutputStream3.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KRBCred.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */