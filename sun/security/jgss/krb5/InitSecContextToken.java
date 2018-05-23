package sun.security.jgss.krb5;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import sun.security.krb5.Checksum;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.KerberosTime;
import sun.security.util.DerValue;

class InitSecContextToken
  extends InitialToken
{
  private KrbApReq apReq = null;
  
  InitSecContextToken(Krb5Context paramKrb5Context, Credentials paramCredentials1, Credentials paramCredentials2)
    throws KrbException, IOException, GSSException
  {
    boolean bool1 = paramKrb5Context.getMutualAuthState();
    boolean bool2 = true;
    boolean bool3 = true;
    InitialToken.OverloadedChecksum localOverloadedChecksum = new InitialToken.OverloadedChecksum(this, paramKrb5Context, paramCredentials1, paramCredentials2);
    Checksum localChecksum = localOverloadedChecksum.getChecksum();
    paramKrb5Context.setTktFlags(paramCredentials2.getFlags());
    paramKrb5Context.setAuthTime(new KerberosTime(paramCredentials2.getAuthTime()).toString());
    apReq = new KrbApReq(paramCredentials2, bool1, bool2, bool3, localChecksum);
    paramKrb5Context.resetMySequenceNumber(apReq.getSeqNumber().intValue());
    EncryptionKey localEncryptionKey = apReq.getSubKey();
    if (localEncryptionKey != null) {
      paramKrb5Context.setKey(1, localEncryptionKey);
    } else {
      paramKrb5Context.setKey(0, paramCredentials2.getSessionKey());
    }
    if (!bool1) {
      paramKrb5Context.resetPeerSequenceNumber(0);
    }
  }
  
  InitSecContextToken(Krb5Context paramKrb5Context, Krb5AcceptCredential paramKrb5AcceptCredential, InputStream paramInputStream)
    throws IOException, GSSException, KrbException
  {
    int i = paramInputStream.read() << 8 | paramInputStream.read();
    if (i != 256) {
      throw new GSSException(10, -1, "AP_REQ token id does not match!");
    }
    byte[] arrayOfByte = new DerValue(paramInputStream).toByteArray();
    InetAddress localInetAddress = null;
    if (paramKrb5Context.getChannelBinding() != null) {
      localInetAddress = paramKrb5Context.getChannelBinding().getInitiatorAddress();
    }
    apReq = new KrbApReq(arrayOfByte, paramKrb5AcceptCredential, localInetAddress);
    EncryptionKey localEncryptionKey1 = apReq.getCreds().getSessionKey();
    EncryptionKey localEncryptionKey2 = apReq.getSubKey();
    if (localEncryptionKey2 != null) {
      paramKrb5Context.setKey(1, localEncryptionKey2);
    } else {
      paramKrb5Context.setKey(0, localEncryptionKey1);
    }
    InitialToken.OverloadedChecksum localOverloadedChecksum = new InitialToken.OverloadedChecksum(this, paramKrb5Context, apReq.getChecksum(), localEncryptionKey1, localEncryptionKey2);
    localOverloadedChecksum.setContextFlags(paramKrb5Context);
    Credentials localCredentials = localOverloadedChecksum.getDelegatedCreds();
    if (localCredentials != null)
    {
      localObject = Krb5InitCredential.getInstance((Krb5NameElement)paramKrb5Context.getSrcName(), localCredentials);
      paramKrb5Context.setDelegCred((Krb5CredElement)localObject);
    }
    Object localObject = apReq.getSeqNumber();
    int j = localObject != null ? ((Integer)localObject).intValue() : 0;
    paramKrb5Context.resetPeerSequenceNumber(j);
    if (!paramKrb5Context.getMutualAuthState()) {
      paramKrb5Context.resetMySequenceNumber(j);
    }
    paramKrb5Context.setAuthTime(new KerberosTime(apReq.getCreds().getAuthTime()).toString());
    paramKrb5Context.setTktFlags(apReq.getCreds().getFlags());
    AuthorizationData localAuthorizationData = apReq.getCreds().getAuthzData();
    if (localAuthorizationData == null)
    {
      paramKrb5Context.setAuthzData(null);
    }
    else
    {
      com.sun.security.jgss.AuthorizationDataEntry[] arrayOfAuthorizationDataEntry = new com.sun.security.jgss.AuthorizationDataEntry[localAuthorizationData.count()];
      for (int k = 0; k < localAuthorizationData.count(); k++) {
        arrayOfAuthorizationDataEntry[k] = new com.sun.security.jgss.AuthorizationDataEntry(itemadType, itemadData);
      }
      paramKrb5Context.setAuthzData(arrayOfAuthorizationDataEntry);
    }
  }
  
  public final KrbApReq getKrbApReq()
  {
    return apReq;
  }
  
  public final byte[] encode()
    throws IOException
  {
    byte[] arrayOfByte1 = apReq.getMessage();
    byte[] arrayOfByte2 = new byte[2 + arrayOfByte1.length];
    writeInt(256, arrayOfByte2, 0);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 2, arrayOfByte1.length);
    return arrayOfByte2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\InitSecContextToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */