package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCReqBody
{
  public KDCOptions kdcOptions;
  public PrincipalName cname;
  public PrincipalName sname;
  public KerberosTime from;
  public KerberosTime till;
  public KerberosTime rtime;
  public HostAddresses addresses;
  private int nonce;
  private int[] eType = null;
  private EncryptedData encAuthorizationData;
  private Ticket[] additionalTickets;
  
  public KDCReqBody(KDCOptions paramKDCOptions, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int paramInt, int[] paramArrayOfInt, HostAddresses paramHostAddresses, EncryptedData paramEncryptedData, Ticket[] paramArrayOfTicket)
    throws IOException
  {
    kdcOptions = paramKDCOptions;
    cname = paramPrincipalName1;
    sname = paramPrincipalName2;
    from = paramKerberosTime1;
    till = paramKerberosTime2;
    rtime = paramKerberosTime3;
    nonce = paramInt;
    if (paramArrayOfInt != null) {
      eType = ((int[])paramArrayOfInt.clone());
    }
    addresses = paramHostAddresses;
    encAuthorizationData = paramEncryptedData;
    if (paramArrayOfTicket != null)
    {
      additionalTickets = new Ticket[paramArrayOfTicket.length];
      for (int i = 0; i < paramArrayOfTicket.length; i++)
      {
        if (paramArrayOfTicket[i] == null) {
          throw new IOException("Cannot create a KDCReqBody");
        }
        additionalTickets[i] = ((Ticket)paramArrayOfTicket[i].clone());
      }
    }
  }
  
  public KDCReqBody(DerValue paramDerValue, int paramInt)
    throws Asn1Exception, RealmException, KrbException, IOException
  {
    addresses = null;
    encAuthorizationData = null;
    additionalTickets = null;
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    kdcOptions = KDCOptions.parse(paramDerValue.getData(), (byte)0, false);
    cname = PrincipalName.parse(paramDerValue.getData(), (byte)1, true, new Realm("PLACEHOLDER"));
    if ((paramInt != 10) && (cname != null)) {
      throw new Asn1Exception(906);
    }
    Realm localRealm = Realm.parse(paramDerValue.getData(), (byte)2, false);
    if (cname != null) {
      cname = new PrincipalName(cname.getNameType(), cname.getNameStrings(), localRealm);
    }
    sname = PrincipalName.parse(paramDerValue.getData(), (byte)3, true, localRealm);
    from = KerberosTime.parse(paramDerValue.getData(), (byte)4, true);
    till = KerberosTime.parse(paramDerValue.getData(), (byte)5, false);
    rtime = KerberosTime.parse(paramDerValue.getData(), (byte)6, true);
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if ((localDerValue1.getTag() & 0x1F) == 7) {
      nonce = localDerValue1.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue1 = paramDerValue.getData().getDerValue();
    Vector localVector1 = new Vector();
    DerValue localDerValue2;
    if ((localDerValue1.getTag() & 0x1F) == 8)
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      if (localDerValue2.getTag() == 48)
      {
        while (localDerValue2.getData().available() > 0) {
          localVector1.addElement(Integer.valueOf(localDerValue2.getData().getBigInteger().intValue()));
        }
        eType = new int[localVector1.size()];
        for (int i = 0; i < localVector1.size(); i++) {
          eType[i] = ((Integer)localVector1.elementAt(i)).intValue();
        }
      }
      else
      {
        throw new Asn1Exception(906);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0) {
      addresses = HostAddresses.parse(paramDerValue.getData(), (byte)9, true);
    }
    if (paramDerValue.getData().available() > 0) {
      encAuthorizationData = EncryptedData.parse(paramDerValue.getData(), (byte)10, true);
    }
    if (paramDerValue.getData().available() > 0)
    {
      Vector localVector2 = new Vector();
      localDerValue1 = paramDerValue.getData().getDerValue();
      if ((localDerValue1.getTag() & 0x1F) == 11)
      {
        localDerValue2 = localDerValue1.getData().getDerValue();
        if (localDerValue2.getTag() == 48) {
          while (localDerValue2.getData().available() > 0) {
            localVector2.addElement(new Ticket(localDerValue2.getData().getDerValue()));
          }
        }
        throw new Asn1Exception(906);
        if (localVector2.size() > 0)
        {
          additionalTickets = new Ticket[localVector2.size()];
          localVector2.copyInto(additionalTickets);
        }
      }
      else
      {
        throw new Asn1Exception(906);
      }
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode(int paramInt)
    throws Asn1Exception, IOException
  {
    Vector localVector = new Vector();
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), kdcOptions.asn1Encode()));
    if ((paramInt == 10) && (cname != null)) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), cname.asn1Encode()));
    }
    if (sname != null)
    {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), sname.getRealm().asn1Encode()));
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), sname.asn1Encode()));
    }
    else if (cname != null)
    {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), cname.getRealm().asn1Encode()));
    }
    if (from != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), from.asn1Encode()));
    }
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), till.asn1Encode()));
    if (rtime != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), rtime.asn1Encode()));
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(nonce));
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), localDerOutputStream1.toByteArray()));
    localDerOutputStream1 = new DerOutputStream();
    for (int i = 0; i < eType.length; i++) {
      localDerOutputStream1.putInteger(BigInteger.valueOf(eType[i]));
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), localDerOutputStream2.toByteArray()));
    if (addresses != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), addresses.asn1Encode()));
    }
    if (encAuthorizationData != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), encAuthorizationData.asn1Encode()));
    }
    if ((additionalTickets != null) && (additionalTickets.length > 0))
    {
      localDerOutputStream1 = new DerOutputStream();
      for (int j = 0; j < additionalTickets.length; j++) {
        localDerOutputStream1.write(additionalTickets[j].asn1Encode());
      }
      localObject = new DerOutputStream();
      ((DerOutputStream)localObject).write((byte)48, localDerOutputStream1);
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)11), ((DerOutputStream)localObject).toByteArray()));
    }
    Object localObject = new DerValue[localVector.size()];
    localVector.copyInto((Object[])localObject);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putSequence((DerValue[])localObject);
    return localDerOutputStream1.toByteArray();
  }
  
  public int getNonce()
  {
    return nonce;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KDCReqBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */