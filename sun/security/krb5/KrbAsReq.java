package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import sun.security.krb5.internal.ASReq;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.PAEncTSEnc;
import sun.security.krb5.internal.crypto.Nonce;

public class KrbAsReq
{
  private ASReq asReqMessg;
  private boolean DEBUG = Krb5.DEBUG;
  
  public KrbAsReq(EncryptionKey paramEncryptionKey, KDCOptions paramKDCOptions, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int[] paramArrayOfInt, HostAddresses paramHostAddresses)
    throws KrbException, IOException
  {
    if (paramKDCOptions == null) {
      paramKDCOptions = new KDCOptions();
    }
    if ((paramKDCOptions.get(2)) || (paramKDCOptions.get(4)) || (paramKDCOptions.get(28)) || (paramKDCOptions.get(30)) || (paramKDCOptions.get(31))) {
      throw new KrbException(101);
    }
    if ((!paramKDCOptions.get(6)) && (paramKerberosTime1 != null)) {
      paramKerberosTime1 = null;
    }
    if ((!paramKDCOptions.get(8)) && (paramKerberosTime3 != null)) {
      paramKerberosTime3 = null;
    }
    PAData[] arrayOfPAData = null;
    if (paramEncryptionKey != null)
    {
      localObject = new PAEncTSEnc();
      byte[] arrayOfByte = ((PAEncTSEnc)localObject).asn1Encode();
      EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey, arrayOfByte, 1);
      arrayOfPAData = new PAData[1];
      arrayOfPAData[0] = new PAData(2, localEncryptedData.asn1Encode());
    }
    if (paramPrincipalName1.getRealm() == null) {
      throw new RealmException(601, "default realm not specified ");
    }
    if (DEBUG) {
      System.out.println(">>> KrbAsReq creating message");
    }
    if ((paramHostAddresses == null) && (Config.getInstance().useAddresses())) {
      paramHostAddresses = HostAddresses.getLocalAddresses();
    }
    if (paramPrincipalName2 == null)
    {
      localObject = paramPrincipalName1.getRealmAsString();
      paramPrincipalName2 = PrincipalName.tgsService((String)localObject, (String)localObject);
    }
    if (paramKerberosTime2 == null) {
      paramKerberosTime2 = new KerberosTime(0L);
    }
    Object localObject = new KDCReqBody(paramKDCOptions, paramPrincipalName1, paramPrincipalName2, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, Nonce.value(), paramArrayOfInt, paramHostAddresses, null, null);
    asReqMessg = new ASReq(arrayOfPAData, (KDCReqBody)localObject);
  }
  
  byte[] encoding()
    throws IOException, Asn1Exception
  {
    return asReqMessg.asn1Encode();
  }
  
  ASReq getMessage()
  {
    return asReqMessg;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbAsReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */