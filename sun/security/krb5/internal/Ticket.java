package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Ticket
  implements Cloneable
{
  public int tkt_vno;
  public PrincipalName sname;
  public EncryptedData encPart;
  
  private Ticket() {}
  
  public Object clone()
  {
    Ticket localTicket = new Ticket();
    sname = ((PrincipalName)sname.clone());
    encPart = ((EncryptedData)encPart.clone());
    tkt_vno = tkt_vno;
    return localTicket;
  }
  
  public Ticket(PrincipalName paramPrincipalName, EncryptedData paramEncryptedData)
  {
    tkt_vno = 5;
    sname = paramPrincipalName;
    encPart = paramEncryptedData;
  }
  
  public Ticket(byte[] paramArrayOfByte)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public Ticket(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    if (((paramDerValue.getTag() & 0x1F) != 1) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
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
    tkt_vno = localDerValue2.getData().getBigInteger().intValue();
    if (tkt_vno != 5) {
      throw new KrbApErrException(39);
    }
    Realm localRealm = Realm.parse(localDerValue1.getData(), (byte)1, false);
    sname = PrincipalName.parse(localDerValue1.getData(), (byte)2, false, localRealm);
    encPart = EncryptedData.parse(localDerValue1.getData(), (byte)3, false);
    if (localDerValue1.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[4];
    localDerOutputStream2.putInteger(BigInteger.valueOf(tkt_vno));
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), sname.getRealm().asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), sname.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), encPart.asn1Encode());
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write(DerValue.createTag((byte)64, true, (byte)1), localDerOutputStream2);
    return localDerOutputStream3.toByteArray();
  }
  
  public static Ticket parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException, RealmException, KrbApErrException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new Ticket(localDerValue2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\Ticket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */