package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.APReq;
import sun.security.krb5.internal.Authenticator;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.EncTicketPart;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.ReplayCache;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.util.DerValue;

public class KrbApReq
{
  private byte[] obuf;
  private KerberosTime ctime;
  private int cusec;
  private Authenticator authenticator;
  private Credentials creds;
  private APReq apReqMessg;
  private static ReplayCache rcache = ;
  private static boolean DEBUG = Krb5.DEBUG;
  private static final char[] hexConst = "0123456789ABCDEF".toCharArray();
  
  public KrbApReq(Credentials paramCredentials, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Checksum paramChecksum)
    throws Asn1Exception, KrbCryptoException, KrbException, IOException
  {
    APOptions localAPOptions = paramBoolean1 ? new APOptions(2) : new APOptions();
    if (DEBUG) {
      System.out.println(">>> KrbApReq: APOptions are " + localAPOptions);
    }
    EncryptionKey localEncryptionKey = paramBoolean2 ? new EncryptionKey(paramCredentials.getSessionKey()) : null;
    LocalSeqNumber localLocalSeqNumber = new LocalSeqNumber();
    init(localAPOptions, paramCredentials, paramChecksum, localEncryptionKey, localLocalSeqNumber, null, 11);
  }
  
  public KrbApReq(byte[] paramArrayOfByte, Krb5AcceptCredential paramKrb5AcceptCredential, InetAddress paramInetAddress)
    throws KrbException, IOException
  {
    obuf = paramArrayOfByte;
    if (apReqMessg == null) {
      decode();
    }
    authenticate(paramKrb5AcceptCredential, paramInetAddress);
  }
  
  KrbApReq(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData)
    throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
  {
    init(paramAPOptions, paramTicket, paramEncryptionKey1, paramPrincipalName, paramChecksum, paramKerberosTime, paramEncryptionKey2, paramSeqNumber, paramAuthorizationData, 7);
  }
  
  private void init(APOptions paramAPOptions, Credentials paramCredentials, Checksum paramChecksum, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt)
    throws KrbException, IOException
  {
    ctime = KerberosTime.now();
    init(paramAPOptions, ticket, key, client, paramChecksum, ctime, paramEncryptionKey, paramSeqNumber, paramAuthorizationData, paramInt);
  }
  
  private void init(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt)
    throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
  {
    createMessage(paramAPOptions, paramTicket, paramEncryptionKey1, paramPrincipalName, paramChecksum, paramKerberosTime, paramEncryptionKey2, paramSeqNumber, paramAuthorizationData, paramInt);
    obuf = apReqMessg.asn1Encode();
  }
  
  void decode()
    throws KrbException, IOException
  {
    DerValue localDerValue = new DerValue(obuf);
    decode(localDerValue);
  }
  
  void decode(DerValue paramDerValue)
    throws KrbException, IOException
  {
    apReqMessg = null;
    try
    {
      apReqMessg = new APReq(paramDerValue);
    }
    catch (Asn1Exception localAsn1Exception)
    {
      apReqMessg = null;
      KRBError localKRBError = new KRBError(paramDerValue);
      String str1 = localKRBError.getErrorString();
      String str2;
      if (str1.charAt(str1.length() - 1) == 0) {
        str2 = str1.substring(0, str1.length() - 1);
      } else {
        str2 = str1;
      }
      KrbException localKrbException = new KrbException(localKRBError.getErrorCode(), str2);
      localKrbException.initCause(localAsn1Exception);
      throw localKrbException;
    }
  }
  
  private void authenticate(Krb5AcceptCredential paramKrb5AcceptCredential, InetAddress paramInetAddress)
    throws KrbException, IOException
  {
    int i = apReqMessg.ticket.encPart.getEType();
    Integer localInteger = apReqMessg.ticket.encPart.getKeyVersionNumber();
    EncryptionKey[] arrayOfEncryptionKey = paramKrb5AcceptCredential.getKrb5EncryptionKeys(apReqMessg.ticket.sname);
    EncryptionKey localEncryptionKey = EncryptionKey.findKey(i, localInteger, arrayOfEncryptionKey);
    if (localEncryptionKey == null) {
      throw new KrbException(400, "Cannot find key of appropriate type to decrypt AP REP - " + EType.toString(i));
    }
    byte[] arrayOfByte1 = apReqMessg.ticket.encPart.decrypt(localEncryptionKey, 2);
    byte[] arrayOfByte2 = apReqMessg.ticket.encPart.reset(arrayOfByte1);
    EncTicketPart localEncTicketPart = new EncTicketPart(arrayOfByte2);
    checkPermittedEType(key.getEType());
    byte[] arrayOfByte3 = apReqMessg.authenticator.decrypt(key, 11);
    byte[] arrayOfByte4 = apReqMessg.authenticator.reset(arrayOfByte3);
    authenticator = new Authenticator(arrayOfByte4);
    ctime = authenticator.ctime;
    cusec = authenticator.cusec;
    authenticator.ctime = authenticator.ctime.withMicroSeconds(authenticator.cusec);
    if (!authenticator.cname.equals(cname)) {
      throw new KrbApErrException(36);
    }
    if (!authenticator.ctime.inClockSkew()) {
      throw new KrbApErrException(37);
    }
    byte[] arrayOfByte5;
    try
    {
      arrayOfByte5 = MessageDigest.getInstance("MD5").digest(apReqMessg.authenticator.cipher);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError("Impossible");
    }
    char[] arrayOfChar = new char[arrayOfByte5.length * 2];
    for (int j = 0; j < arrayOfByte5.length; j++)
    {
      arrayOfChar[(2 * j)] = hexConst[((arrayOfByte5[j] & 0xFF) >> 4)];
      arrayOfChar[(2 * j + 1)] = hexConst[(arrayOfByte5[j] & 0xF)];
    }
    AuthTimeWithHash localAuthTimeWithHash = new AuthTimeWithHash(authenticator.cname.toString(), apReqMessg.ticket.sname.toString(), authenticator.ctime.getSeconds(), authenticator.cusec, new String(arrayOfChar));
    rcache.checkAndStore(KerberosTime.now(), localAuthTimeWithHash);
    if (paramInetAddress != null)
    {
      localObject = new HostAddress(paramInetAddress);
      if ((caddr != null) && (!caddr.inList((HostAddress)localObject)))
      {
        if (DEBUG) {
          System.out.println(">>> KrbApReq: initiator is " + ((HostAddress)localObject).getInetAddress() + ", but caddr is " + Arrays.toString(caddr.getInetAddresses()));
        }
        throw new KrbApErrException(38);
      }
    }
    Object localObject = KerberosTime.now();
    if (((starttime != null) && (starttime.greaterThanWRTClockSkew((KerberosTime)localObject))) || (flags.get(7))) {
      throw new KrbApErrException(33);
    }
    if ((endtime != null) && (((KerberosTime)localObject).greaterThanWRTClockSkew(endtime))) {
      throw new KrbApErrException(32);
    }
    creds = new Credentials(apReqMessg.ticket, authenticator.cname, apReqMessg.ticket.sname, key, flags, authtime, starttime, endtime, renewTill, caddr, authorizationData);
    if (DEBUG) {
      System.out.println(">>> KrbApReq: authenticate succeed.");
    }
  }
  
  public Credentials getCreds()
  {
    return creds;
  }
  
  KerberosTime getCtime()
  {
    if (ctime != null) {
      return ctime;
    }
    return authenticator.ctime;
  }
  
  int cusec()
  {
    return cusec;
  }
  
  APOptions getAPOptions()
    throws KrbException, IOException
  {
    if (apReqMessg == null) {
      decode();
    }
    if (apReqMessg != null) {
      return apReqMessg.apOptions;
    }
    return null;
  }
  
  public boolean getMutualAuthRequired()
    throws KrbException, IOException
  {
    if (apReqMessg == null) {
      decode();
    }
    if (apReqMessg != null) {
      return apReqMessg.apOptions.get(2);
    }
    return false;
  }
  
  boolean useSessionKey()
    throws KrbException, IOException
  {
    if (apReqMessg == null) {
      decode();
    }
    if (apReqMessg != null) {
      return apReqMessg.apOptions.get(1);
    }
    return false;
  }
  
  public EncryptionKey getSubKey()
  {
    return authenticator.getSubKey();
  }
  
  public Integer getSeqNumber()
  {
    return authenticator.getSeqNumber();
  }
  
  public Checksum getChecksum()
  {
    return authenticator.getChecksum();
  }
  
  public byte[] getMessage()
  {
    return obuf;
  }
  
  public PrincipalName getClient()
  {
    return creds.getClient();
  }
  
  private void createMessage(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt)
    throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
  {
    Integer localInteger = null;
    if (paramSeqNumber != null) {
      localInteger = new Integer(paramSeqNumber.current());
    }
    authenticator = new Authenticator(paramPrincipalName, paramChecksum, paramKerberosTime.getMicroSeconds(), paramKerberosTime, paramEncryptionKey2, localInteger, paramAuthorizationData);
    byte[] arrayOfByte = authenticator.asn1Encode();
    EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, paramInt);
    apReqMessg = new APReq(paramAPOptions, paramTicket, localEncryptedData);
  }
  
  private static void checkPermittedEType(int paramInt)
    throws KrbException
  {
    int[] arrayOfInt = EType.getDefaults("permitted_enctypes");
    if (!EType.isSupported(paramInt, arrayOfInt)) {
      throw new KrbException(EType.toString(paramInt) + " encryption type not in permitted_enctypes list");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbApReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */