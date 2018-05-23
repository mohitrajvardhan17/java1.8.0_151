package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.APRep;
import sun.security.krb5.internal.EncAPRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.SeqNumber;
import sun.security.util.DerValue;

public class KrbApRep
{
  private byte[] obuf;
  private byte[] ibuf;
  private EncAPRepPart encPart;
  private APRep apRepMessg;
  
  public KrbApRep(KrbApReq paramKrbApReq, boolean paramBoolean, EncryptionKey paramEncryptionKey)
    throws KrbException, IOException
  {
    LocalSeqNumber localLocalSeqNumber = new LocalSeqNumber();
    init(paramKrbApReq, paramEncryptionKey, localLocalSeqNumber);
  }
  
  public KrbApRep(byte[] paramArrayOfByte, Credentials paramCredentials, KrbApReq paramKrbApReq)
    throws KrbException, IOException
  {
    this(paramArrayOfByte, paramCredentials);
    authenticate(paramKrbApReq);
  }
  
  private void init(KrbApReq paramKrbApReq, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber)
    throws KrbException, IOException
  {
    createMessage(getCredskey, paramKrbApReq.getCtime(), paramKrbApReq.cusec(), paramEncryptionKey, paramSeqNumber);
    obuf = apRepMessg.asn1Encode();
  }
  
  private KrbApRep(byte[] paramArrayOfByte, Credentials paramCredentials)
    throws KrbException, IOException
  {
    this(new DerValue(paramArrayOfByte), paramCredentials);
  }
  
  private KrbApRep(DerValue paramDerValue, Credentials paramCredentials)
    throws KrbException, IOException
  {
    APRep localAPRep = null;
    try
    {
      localAPRep = new APRep(paramDerValue);
    }
    catch (Asn1Exception localAsn1Exception)
    {
      localAPRep = null;
      localObject = new KRBError(paramDerValue);
      String str1 = ((KRBError)localObject).getErrorString();
      String str2;
      if (str1.charAt(str1.length() - 1) == 0) {
        str2 = str1.substring(0, str1.length() - 1);
      } else {
        str2 = str1;
      }
      KrbException localKrbException = new KrbException(((KRBError)localObject).getErrorCode(), str2);
      localKrbException.initCause(localAsn1Exception);
      throw localKrbException;
    }
    byte[] arrayOfByte = encPart.decrypt(key, 12);
    Object localObject = encPart.reset(arrayOfByte);
    paramDerValue = new DerValue((byte[])localObject);
    encPart = new EncAPRepPart(paramDerValue);
  }
  
  private void authenticate(KrbApReq paramKrbApReq)
    throws KrbException, IOException
  {
    if ((encPart.ctime.getSeconds() != paramKrbApReq.getCtime().getSeconds()) || (encPart.cusec != paramKrbApReq.getCtime().getMicroSeconds())) {
      throw new KrbApErrException(46);
    }
  }
  
  public EncryptionKey getSubKey()
  {
    return encPart.getSubKey();
  }
  
  public Integer getSeqNumber()
  {
    return encPart.getSeqNumber();
  }
  
  public byte[] getMessage()
  {
    return obuf;
  }
  
  private void createMessage(EncryptionKey paramEncryptionKey1, KerberosTime paramKerberosTime, int paramInt, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber)
    throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
  {
    Integer localInteger = null;
    if (paramSeqNumber != null) {
      localInteger = new Integer(paramSeqNumber.current());
    }
    encPart = new EncAPRepPart(paramKerberosTime, paramInt, paramEncryptionKey2, localInteger);
    byte[] arrayOfByte = encPart.asn1Encode();
    EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, 12);
    apRepMessg = new APRep(localEncryptedData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbApRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */