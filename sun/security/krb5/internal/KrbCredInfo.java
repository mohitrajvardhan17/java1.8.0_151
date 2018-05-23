package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KrbCredInfo
{
  public EncryptionKey key;
  public PrincipalName pname;
  public TicketFlags flags;
  public KerberosTime authtime;
  public KerberosTime starttime;
  public KerberosTime endtime;
  public KerberosTime renewTill;
  public PrincipalName sname;
  public HostAddresses caddr;
  
  private KrbCredInfo() {}
  
  public KrbCredInfo(EncryptionKey paramEncryptionKey, PrincipalName paramPrincipalName1, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, PrincipalName paramPrincipalName2, HostAddresses paramHostAddresses)
  {
    key = paramEncryptionKey;
    pname = paramPrincipalName1;
    flags = paramTicketFlags;
    authtime = paramKerberosTime1;
    starttime = paramKerberosTime2;
    endtime = paramKerberosTime3;
    renewTill = paramKerberosTime4;
    sname = paramPrincipalName2;
    caddr = paramHostAddresses;
  }
  
  public KrbCredInfo(DerValue paramDerValue)
    throws Asn1Exception, IOException, RealmException
  {
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    pname = null;
    flags = null;
    authtime = null;
    starttime = null;
    endtime = null;
    renewTill = null;
    sname = null;
    caddr = null;
    key = EncryptionKey.parse(paramDerValue.getData(), (byte)0, false);
    Realm localRealm1 = null;
    Realm localRealm2 = null;
    if (paramDerValue.getData().available() > 0) {
      localRealm1 = Realm.parse(paramDerValue.getData(), (byte)1, true);
    }
    if (paramDerValue.getData().available() > 0) {
      pname = PrincipalName.parse(paramDerValue.getData(), (byte)2, true, localRealm1);
    }
    if (paramDerValue.getData().available() > 0) {
      flags = TicketFlags.parse(paramDerValue.getData(), (byte)3, true);
    }
    if (paramDerValue.getData().available() > 0) {
      authtime = KerberosTime.parse(paramDerValue.getData(), (byte)4, true);
    }
    if (paramDerValue.getData().available() > 0) {
      starttime = KerberosTime.parse(paramDerValue.getData(), (byte)5, true);
    }
    if (paramDerValue.getData().available() > 0) {
      endtime = KerberosTime.parse(paramDerValue.getData(), (byte)6, true);
    }
    if (paramDerValue.getData().available() > 0) {
      renewTill = KerberosTime.parse(paramDerValue.getData(), (byte)7, true);
    }
    if (paramDerValue.getData().available() > 0) {
      localRealm2 = Realm.parse(paramDerValue.getData(), (byte)8, true);
    }
    if (paramDerValue.getData().available() > 0) {
      sname = PrincipalName.parse(paramDerValue.getData(), (byte)9, true, localRealm2);
    }
    if (paramDerValue.getData().available() > 0) {
      caddr = HostAddresses.parse(paramDerValue.getData(), (byte)10, true);
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    Vector localVector = new Vector();
    localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), key.asn1Encode()));
    if (pname != null)
    {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), pname.getRealm().asn1Encode()));
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), pname.asn1Encode()));
    }
    if (flags != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), flags.asn1Encode()));
    }
    if (authtime != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), authtime.asn1Encode()));
    }
    if (starttime != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), starttime.asn1Encode()));
    }
    if (endtime != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), endtime.asn1Encode()));
    }
    if (renewTill != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), renewTill.asn1Encode()));
    }
    if (sname != null)
    {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), sname.getRealm().asn1Encode()));
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), sname.asn1Encode()));
    }
    if (caddr != null) {
      localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), caddr.asn1Encode()));
    }
    DerValue[] arrayOfDerValue = new DerValue[localVector.size()];
    localVector.copyInto(arrayOfDerValue);
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putSequence(arrayOfDerValue);
    return localDerOutputStream.toByteArray();
  }
  
  public Object clone()
  {
    KrbCredInfo localKrbCredInfo = new KrbCredInfo();
    key = ((EncryptionKey)key.clone());
    if (pname != null) {
      pname = ((PrincipalName)pname.clone());
    }
    if (flags != null) {
      flags = ((TicketFlags)flags.clone());
    }
    authtime = authtime;
    starttime = starttime;
    endtime = endtime;
    renewTill = renewTill;
    if (sname != null) {
      sname = ((PrincipalName)sname.clone());
    }
    if (caddr != null) {
      caddr = ((HostAddresses)caddr.clone());
    }
    return localKrbCredInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KrbCredInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */