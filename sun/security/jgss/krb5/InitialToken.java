package sun.security.jgss.krb5;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.security.auth.kerberos.DelegationPermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSToken;
import sun.security.krb5.Checksum;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbCred;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

abstract class InitialToken
  extends Krb5Token
{
  private static final int CHECKSUM_TYPE = 32771;
  private static final int CHECKSUM_LENGTH_SIZE = 4;
  private static final int CHECKSUM_BINDINGS_SIZE = 16;
  private static final int CHECKSUM_FLAGS_SIZE = 4;
  private static final int CHECKSUM_DELEG_OPT_SIZE = 2;
  private static final int CHECKSUM_DELEG_LGTH_SIZE = 2;
  private static final int CHECKSUM_DELEG_FLAG = 1;
  private static final int CHECKSUM_MUTUAL_FLAG = 2;
  private static final int CHECKSUM_REPLAY_FLAG = 4;
  private static final int CHECKSUM_SEQUENCE_FLAG = 8;
  private static final int CHECKSUM_CONF_FLAG = 16;
  private static final int CHECKSUM_INTEG_FLAG = 32;
  private final byte[] CHECKSUM_FIRST_BYTES = { 16, 0, 0, 0 };
  private static final int CHANNEL_BINDING_AF_INET = 2;
  private static final int CHANNEL_BINDING_AF_INET6 = 24;
  private static final int CHANNEL_BINDING_AF_NULL_ADDR = 255;
  private static final int Inet4_ADDRSZ = 4;
  private static final int Inet6_ADDRSZ = 16;
  
  InitialToken() {}
  
  private int getAddrType(InetAddress paramInetAddress)
  {
    int i = 255;
    if ((paramInetAddress instanceof Inet4Address)) {
      i = 2;
    } else if ((paramInetAddress instanceof Inet6Address)) {
      i = 24;
    }
    return i;
  }
  
  private byte[] getAddrBytes(InetAddress paramInetAddress)
    throws GSSException
  {
    int i = getAddrType(paramInetAddress);
    byte[] arrayOfByte = paramInetAddress.getAddress();
    if (arrayOfByte != null)
    {
      switch (i)
      {
      case 2: 
        if (arrayOfByte.length != 4) {
          throw new GSSException(11, -1, "Incorrect AF-INET address length in ChannelBinding.");
        }
        return arrayOfByte;
      case 24: 
        if (arrayOfByte.length != 16) {
          throw new GSSException(11, -1, "Incorrect AF-INET6 address length in ChannelBinding.");
        }
        return arrayOfByte;
      }
      throw new GSSException(11, -1, "Cannot handle non AF-INET addresses in ChannelBinding.");
    }
    return null;
  }
  
  private byte[] computeChannelBinding(ChannelBinding paramChannelBinding)
    throws GSSException
  {
    InetAddress localInetAddress1 = paramChannelBinding.getInitiatorAddress();
    InetAddress localInetAddress2 = paramChannelBinding.getAcceptorAddress();
    int i = 20;
    int j = getAddrType(localInetAddress1);
    int k = getAddrType(localInetAddress2);
    byte[] arrayOfByte1 = null;
    if (localInetAddress1 != null)
    {
      arrayOfByte1 = getAddrBytes(localInetAddress1);
      i += arrayOfByte1.length;
    }
    byte[] arrayOfByte2 = null;
    if (localInetAddress2 != null)
    {
      arrayOfByte2 = getAddrBytes(localInetAddress2);
      i += arrayOfByte2.length;
    }
    byte[] arrayOfByte3 = paramChannelBinding.getApplicationData();
    if (arrayOfByte3 != null) {
      i += arrayOfByte3.length;
    }
    byte[] arrayOfByte4 = new byte[i];
    int m = 0;
    writeLittleEndian(j, arrayOfByte4, m);
    m += 4;
    if (arrayOfByte1 != null)
    {
      writeLittleEndian(arrayOfByte1.length, arrayOfByte4, m);
      m += 4;
      System.arraycopy(arrayOfByte1, 0, arrayOfByte4, m, arrayOfByte1.length);
      m += arrayOfByte1.length;
    }
    else
    {
      m += 4;
    }
    writeLittleEndian(k, arrayOfByte4, m);
    m += 4;
    if (arrayOfByte2 != null)
    {
      writeLittleEndian(arrayOfByte2.length, arrayOfByte4, m);
      m += 4;
      System.arraycopy(arrayOfByte2, 0, arrayOfByte4, m, arrayOfByte2.length);
      m += arrayOfByte2.length;
    }
    else
    {
      m += 4;
    }
    if (arrayOfByte3 != null)
    {
      writeLittleEndian(arrayOfByte3.length, arrayOfByte4, m);
      m += 4;
      System.arraycopy(arrayOfByte3, 0, arrayOfByte4, m, arrayOfByte3.length);
      m += arrayOfByte3.length;
    }
    else
    {
      m += 4;
    }
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      return localMessageDigest.digest(arrayOfByte4);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new GSSException(11, -1, "Could not get MD5 Message Digest - " + localNoSuchAlgorithmException.getMessage());
    }
  }
  
  public abstract byte[] encode()
    throws IOException;
  
  protected class OverloadedChecksum
  {
    private byte[] checksumBytes = null;
    private Credentials delegCreds = null;
    private int flags = 0;
    
    public OverloadedChecksum(Krb5Context paramKrb5Context, Credentials paramCredentials1, Credentials paramCredentials2)
      throws KrbException, IOException, GSSException
    {
      byte[] arrayOfByte = null;
      int i = 0;
      int j = 24;
      if (!paramCredentials1.isForwardable())
      {
        paramKrb5Context.setCredDelegState(false);
        paramKrb5Context.setDelegPolicyState(false);
      }
      else if (paramKrb5Context.getCredDelegState())
      {
        if ((paramKrb5Context.getDelegPolicyState()) && (!paramCredentials2.checkDelegate())) {
          paramKrb5Context.setDelegPolicyState(false);
        }
      }
      else if (paramKrb5Context.getDelegPolicyState())
      {
        if (paramCredentials2.checkDelegate()) {
          paramKrb5Context.setCredDelegState(true);
        } else {
          paramKrb5Context.setDelegPolicyState(false);
        }
      }
      if (paramKrb5Context.getCredDelegState())
      {
        localObject1 = null;
        localObject2 = paramKrb5Context.getCipherHelper(paramCredentials2.getSessionKey());
        if (useNullKey((CipherHelper)localObject2)) {
          localObject1 = new KrbCred(paramCredentials1, paramCredentials2, EncryptionKey.NULL_KEY);
        } else {
          localObject1 = new KrbCred(paramCredentials1, paramCredentials2, paramCredentials2.getSessionKey());
        }
        arrayOfByte = ((KrbCred)localObject1).getMessage();
        j += 4 + arrayOfByte.length;
      }
      checksumBytes = new byte[j];
      checksumBytes[(i++)] = CHECKSUM_FIRST_BYTES[0];
      checksumBytes[(i++)] = CHECKSUM_FIRST_BYTES[1];
      checksumBytes[(i++)] = CHECKSUM_FIRST_BYTES[2];
      checksumBytes[(i++)] = CHECKSUM_FIRST_BYTES[3];
      Object localObject1 = paramKrb5Context.getChannelBinding();
      if (localObject1 != null)
      {
        localObject2 = InitialToken.this.computeChannelBinding(paramKrb5Context.getChannelBinding());
        System.arraycopy(localObject2, 0, checksumBytes, i, localObject2.length);
      }
      i += 16;
      if (paramKrb5Context.getCredDelegState()) {
        flags |= 0x1;
      }
      if (paramKrb5Context.getMutualAuthState()) {
        flags |= 0x2;
      }
      if (paramKrb5Context.getReplayDetState()) {
        flags |= 0x4;
      }
      if (paramKrb5Context.getSequenceDetState()) {
        flags |= 0x8;
      }
      if (paramKrb5Context.getIntegState()) {
        flags |= 0x20;
      }
      if (paramKrb5Context.getConfState()) {
        flags |= 0x10;
      }
      Object localObject2 = new byte[4];
      GSSToken.writeLittleEndian(flags, (byte[])localObject2);
      checksumBytes[(i++)] = localObject2[0];
      checksumBytes[(i++)] = localObject2[1];
      checksumBytes[(i++)] = localObject2[2];
      checksumBytes[(i++)] = localObject2[3];
      if (paramKrb5Context.getCredDelegState())
      {
        PrincipalName localPrincipalName = paramCredentials2.getServer();
        StringBuffer localStringBuffer = new StringBuffer("\"");
        localStringBuffer.append(localPrincipalName.getName()).append('"');
        String str = localPrincipalName.getRealmAsString();
        localStringBuffer.append(" \"krbtgt/").append(str).append('@');
        localStringBuffer.append(str).append('"');
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null)
        {
          DelegationPermission localDelegationPermission = new DelegationPermission(localStringBuffer.toString());
          localSecurityManager.checkPermission(localDelegationPermission);
        }
        checksumBytes[(i++)] = 1;
        checksumBytes[(i++)] = 0;
        if (arrayOfByte.length > 65535) {
          throw new GSSException(11, -1, "Incorrect message length");
        }
        GSSToken.writeLittleEndian(arrayOfByte.length, (byte[])localObject2);
        checksumBytes[(i++)] = localObject2[0];
        checksumBytes[(i++)] = localObject2[1];
        System.arraycopy(arrayOfByte, 0, checksumBytes, i, arrayOfByte.length);
      }
    }
    
    public OverloadedChecksum(Krb5Context paramKrb5Context, Checksum paramChecksum, EncryptionKey paramEncryptionKey1, EncryptionKey paramEncryptionKey2)
      throws GSSException, KrbException, IOException
    {
      int i = 0;
      if (paramChecksum == null)
      {
        localObject1 = new GSSException(11, -1, "No cksum in AP_REQ's authenticator");
        ((GSSException)localObject1).initCause(new KrbException(50));
        throw ((Throwable)localObject1);
      }
      checksumBytes = paramChecksum.getBytes();
      if ((checksumBytes[0] != CHECKSUM_FIRST_BYTES[0]) || (checksumBytes[1] != CHECKSUM_FIRST_BYTES[1]) || (checksumBytes[2] != CHECKSUM_FIRST_BYTES[2]) || (checksumBytes[3] != CHECKSUM_FIRST_BYTES[3])) {
        throw new GSSException(11, -1, "Incorrect checksum");
      }
      Object localObject1 = paramKrb5Context.getChannelBinding();
      byte[] arrayOfByte2;
      Object localObject2;
      if (localObject1 != null)
      {
        byte[] arrayOfByte1 = new byte[16];
        System.arraycopy(checksumBytes, 4, arrayOfByte1, 0, 16);
        arrayOfByte2 = new byte[16];
        if (!Arrays.equals(arrayOfByte2, arrayOfByte1))
        {
          localObject2 = InitialToken.this.computeChannelBinding((ChannelBinding)localObject1);
          if (!Arrays.equals((byte[])localObject2, arrayOfByte1)) {
            throw new GSSException(1, -1, "Bytes mismatch!");
          }
        }
        else
        {
          throw new GSSException(1, -1, "Token missing ChannelBinding!");
        }
      }
      flags = GSSToken.readLittleEndian(checksumBytes, 20, 4);
      if ((flags & 0x1) > 0)
      {
        int j = GSSToken.readLittleEndian(checksumBytes, 26, 2);
        arrayOfByte2 = new byte[j];
        System.arraycopy(checksumBytes, 28, arrayOfByte2, 0, j);
        try
        {
          localObject2 = new KrbCred(arrayOfByte2, paramEncryptionKey1);
        }
        catch (KrbException localKrbException)
        {
          if (paramEncryptionKey2 != null) {
            localObject2 = new KrbCred(arrayOfByte2, paramEncryptionKey2);
          } else {
            throw localKrbException;
          }
        }
        delegCreds = localObject2.getDelegatedCreds()[0];
      }
    }
    
    private boolean useNullKey(CipherHelper paramCipherHelper)
    {
      boolean bool = true;
      if ((paramCipherHelper.getProto() == 1) || (paramCipherHelper.isArcFour())) {
        bool = false;
      }
      return bool;
    }
    
    public Checksum getChecksum()
      throws KrbException
    {
      return new Checksum(checksumBytes, 32771);
    }
    
    public Credentials getDelegatedCreds()
    {
      return delegCreds;
    }
    
    public void setContextFlags(Krb5Context paramKrb5Context)
    {
      if ((flags & 0x1) > 0) {
        paramKrb5Context.setCredDelegState(true);
      }
      if ((flags & 0x2) == 0) {
        paramKrb5Context.setMutualAuthState(false);
      }
      if ((flags & 0x4) == 0) {
        paramKrb5Context.setReplayDetState(false);
      }
      if ((flags & 0x8) == 0) {
        paramKrb5Context.setSequenceDetState(false);
      }
      if ((flags & 0x10) == 0) {
        paramKrb5Context.setConfState(false);
      }
      if ((flags & 0x20) == 0) {
        paramKrb5Context.setIntegState(false);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\InitialToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */