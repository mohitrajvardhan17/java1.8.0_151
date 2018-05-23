package sun.security.krb5.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCRep
{
  public PrincipalName cname;
  public Ticket ticket;
  public EncryptedData encPart;
  public EncKDCRepPart encKDCRepPart;
  private int pvno;
  private int msgType;
  public PAData[] pAData = null;
  private boolean DEBUG = Krb5.DEBUG;
  
  public KDCRep(PAData[] paramArrayOfPAData, PrincipalName paramPrincipalName, Ticket paramTicket, EncryptedData paramEncryptedData, int paramInt)
    throws IOException
  {
    pvno = 5;
    msgType = paramInt;
    if (paramArrayOfPAData != null)
    {
      pAData = new PAData[paramArrayOfPAData.length];
      for (int i = 0; i < paramArrayOfPAData.length; i++)
      {
        if (paramArrayOfPAData[i] == null) {
          throw new IOException("Cannot create a KDCRep");
        }
        pAData[i] = ((PAData)paramArrayOfPAData[i].clone());
      }
    }
    cname = paramPrincipalName;
    ticket = paramTicket;
    encPart = paramEncryptedData;
  }
  
  public KDCRep() {}
  
  public KDCRep(byte[] paramArrayOfByte, int paramInt)
    throws Asn1Exception, KrbApErrException, RealmException, IOException
  {
    init(new DerValue(paramArrayOfByte), paramInt);
  }
  
  public KDCRep(DerValue paramDerValue, int paramInt)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(paramDerValue, paramInt);
  }
  
  protected void init(DerValue paramDerValue, int paramInt)
    throws Asn1Exception, RealmException, IOException, KrbApErrException
  {
    if ((paramDerValue.getTag() & 0x1F) != paramInt)
    {
      if (DEBUG) {
        System.out.println(">>> KDCRep: init() encoding tag is " + paramDerValue.getTag() + " req type is " + paramInt);
      }
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
      if (msgType != paramInt) {
        throw new KrbApErrException(40);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    if ((localDerValue1.getData().peekByte() & 0x1F) == 2)
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      localObject = localDerValue2.getData().getSequence(1);
      pAData = new PAData[localObject.length];
      for (int i = 0; i < localObject.length; i++) {
        pAData[i] = new PAData(localObject[i]);
      }
    }
    else
    {
      pAData = null;
    }
    Object localObject = Realm.parse(localDerValue1.getData(), (byte)3, false);
    cname = PrincipalName.parse(localDerValue1.getData(), (byte)4, false, (Realm)localObject);
    ticket = Ticket.parse(localDerValue1.getData(), (byte)5, false);
    encPart = EncryptedData.parse(localDerValue1.getData(), (byte)6, false);
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
    if ((pAData != null) && (pAData.length > 0))
    {
      DerOutputStream localDerOutputStream3 = new DerOutputStream();
      for (int i = 0; i < pAData.length; i++) {
        localDerOutputStream3.write(pAData[i].asn1Encode());
      }
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.write((byte)48, localDerOutputStream3);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    }
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), cname.getRealm().asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), cname.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), ticket.asn1Encode());
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), encPart.asn1Encode());
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KDCRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */