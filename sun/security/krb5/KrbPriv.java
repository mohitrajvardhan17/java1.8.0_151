package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncKrbPrivPart;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBPriv;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;
import sun.security.util.DerValue;

class KrbPriv
  extends KrbAppMessage
{
  private byte[] obuf;
  private byte[] userData;
  
  private KrbPriv(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
    throws KrbException, IOException
  {
    EncryptionKey localEncryptionKey = null;
    if (paramEncryptionKey != null) {
      localEncryptionKey = paramEncryptionKey;
    } else {
      localEncryptionKey = key;
    }
    obuf = mk_priv(paramArrayOfByte, localEncryptionKey, paramKerberosTime, paramSeqNumber, paramHostAddress1, paramHostAddress2);
  }
  
  private KrbPriv(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2)
    throws KrbException, IOException
  {
    KRBPriv localKRBPriv = new KRBPriv(paramArrayOfByte);
    EncryptionKey localEncryptionKey = null;
    if (paramEncryptionKey != null) {
      localEncryptionKey = paramEncryptionKey;
    } else {
      localEncryptionKey = key;
    }
    userData = rd_priv(localKRBPriv, localEncryptionKey, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, client);
  }
  
  public byte[] getMessage()
    throws KrbException
  {
    return obuf;
  }
  
  public byte[] getData()
  {
    return userData;
  }
  
  private byte[] mk_priv(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
    throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
  {
    Integer localInteger1 = null;
    Integer localInteger2 = null;
    if (paramKerberosTime != null) {
      localInteger1 = new Integer(paramKerberosTime.getMicroSeconds());
    }
    if (paramSeqNumber != null)
    {
      localInteger2 = new Integer(paramSeqNumber.current());
      paramSeqNumber.step();
    }
    EncKrbPrivPart localEncKrbPrivPart = new EncKrbPrivPart(paramArrayOfByte, paramKerberosTime, localInteger1, localInteger2, paramHostAddress1, paramHostAddress2);
    byte[] arrayOfByte = localEncKrbPrivPart.asn1Encode();
    EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey, arrayOfByte, 13);
    KRBPriv localKRBPriv = new KRBPriv(localEncryptedData);
    arrayOfByte = localKRBPriv.asn1Encode();
    return localKRBPriv.asn1Encode();
  }
  
  private byte[] rd_priv(KRBPriv paramKRBPriv, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2, PrincipalName paramPrincipalName)
    throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException
  {
    byte[] arrayOfByte1 = encPart.decrypt(paramEncryptionKey, 13);
    byte[] arrayOfByte2 = encPart.reset(arrayOfByte1);
    DerValue localDerValue = new DerValue(arrayOfByte2);
    EncKrbPrivPart localEncKrbPrivPart = new EncKrbPrivPart(localDerValue);
    check(timestamp, usec, seqNumber, sAddress, rAddress, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramPrincipalName);
    return userData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbPriv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */