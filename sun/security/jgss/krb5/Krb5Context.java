package sun.security.jgss.krb5;

import com.sun.security.jgss.AuthorizationDataEntry;
import com.sun.security.jgss.InquireType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Key;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.misc.HexDumpEncoder;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.TokenTracker;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Ticket;

class Krb5Context
  implements GSSContextSpi
{
  private static final int STATE_NEW = 1;
  private static final int STATE_IN_PROCESS = 2;
  private static final int STATE_DONE = 3;
  private static final int STATE_DELETED = 4;
  private int state = 1;
  public static final int SESSION_KEY = 0;
  public static final int INITIATOR_SUBKEY = 1;
  public static final int ACCEPTOR_SUBKEY = 2;
  private boolean credDelegState = false;
  private boolean mutualAuthState = true;
  private boolean replayDetState = true;
  private boolean sequenceDetState = true;
  private boolean confState = true;
  private boolean integState = true;
  private boolean delegPolicyState = false;
  private boolean isConstrainedDelegationTried = false;
  private int mySeqNumber;
  private int peerSeqNumber;
  private int keySrc;
  private TokenTracker peerTokenTracker;
  private CipherHelper cipherHelper = null;
  private Object mySeqNumberLock = new Object();
  private Object peerSeqNumberLock = new Object();
  private EncryptionKey key;
  private Krb5NameElement myName;
  private Krb5NameElement peerName;
  private int lifetime;
  private boolean initiator;
  private ChannelBinding channelBinding;
  private Krb5CredElement myCred;
  private Krb5CredElement delegatedCred;
  private Credentials serviceCreds;
  private KrbApReq apReq;
  Ticket serviceTicket;
  private final GSSCaller caller;
  private static final boolean DEBUG = Krb5Util.DEBUG;
  private boolean[] tktFlags;
  private String authTime;
  private AuthorizationDataEntry[] authzData;
  
  Krb5Context(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, Krb5CredElement paramKrb5CredElement, int paramInt)
    throws GSSException
  {
    if (paramKrb5NameElement == null) {
      throw new IllegalArgumentException("Cannot have null peer name");
    }
    caller = paramGSSCaller;
    peerName = paramKrb5NameElement;
    myCred = paramKrb5CredElement;
    lifetime = paramInt;
    initiator = true;
  }
  
  Krb5Context(GSSCaller paramGSSCaller, Krb5CredElement paramKrb5CredElement)
    throws GSSException
  {
    caller = paramGSSCaller;
    myCred = paramKrb5CredElement;
    initiator = false;
  }
  
  public Krb5Context(GSSCaller paramGSSCaller, byte[] paramArrayOfByte)
    throws GSSException
  {
    throw new GSSException(16, -1, "GSS Import Context not available");
  }
  
  public final boolean isTransferable()
    throws GSSException
  {
    return false;
  }
  
  public final int getLifetime()
  {
    return Integer.MAX_VALUE;
  }
  
  public void requestLifetime(int paramInt)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      lifetime = paramInt;
    }
  }
  
  public final void requestConf(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      confState = paramBoolean;
    }
  }
  
  public final boolean getConfState()
  {
    return confState;
  }
  
  public final void requestInteg(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      integState = paramBoolean;
    }
  }
  
  public final boolean getIntegState()
  {
    return integState;
  }
  
  public final void requestCredDeleg(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator()) && ((myCred == null) || (!(myCred instanceof Krb5ProxyCredential)))) {
      credDelegState = paramBoolean;
    }
  }
  
  public final boolean getCredDelegState()
  {
    if (isInitiator()) {
      return credDelegState;
    }
    tryConstrainedDelegation();
    return delegatedCred != null;
  }
  
  public final void requestMutualAuth(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      mutualAuthState = paramBoolean;
    }
  }
  
  public final boolean getMutualAuthState()
  {
    return mutualAuthState;
  }
  
  public final void requestReplayDet(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      replayDetState = paramBoolean;
    }
  }
  
  public final boolean getReplayDetState()
  {
    return (replayDetState) || (sequenceDetState);
  }
  
  public final void requestSequenceDet(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      sequenceDetState = paramBoolean;
    }
  }
  
  public final boolean getSequenceDetState()
  {
    return (sequenceDetState) || (replayDetState);
  }
  
  public final void requestDelegPolicy(boolean paramBoolean)
  {
    if ((state == 1) && (isInitiator())) {
      delegPolicyState = paramBoolean;
    }
  }
  
  public final boolean getDelegPolicyState()
  {
    return delegPolicyState;
  }
  
  public final void requestAnonymity(boolean paramBoolean)
    throws GSSException
  {}
  
  public final boolean getAnonymityState()
  {
    return false;
  }
  
  final CipherHelper getCipherHelper(EncryptionKey paramEncryptionKey)
    throws GSSException
  {
    EncryptionKey localEncryptionKey = null;
    if (cipherHelper == null)
    {
      localEncryptionKey = getKey() == null ? paramEncryptionKey : getKey();
      cipherHelper = new CipherHelper(localEncryptionKey);
    }
    return cipherHelper;
  }
  
  final int incrementMySequenceNumber()
  {
    int i;
    synchronized (mySeqNumberLock)
    {
      i = mySeqNumber;
      mySeqNumber = (i + 1);
    }
    return i;
  }
  
  final void resetMySequenceNumber(int paramInt)
  {
    if (DEBUG) {
      System.out.println("Krb5Context setting mySeqNumber to: " + paramInt);
    }
    synchronized (mySeqNumberLock)
    {
      mySeqNumber = paramInt;
    }
  }
  
  final void resetPeerSequenceNumber(int paramInt)
  {
    if (DEBUG) {
      System.out.println("Krb5Context setting peerSeqNumber to: " + paramInt);
    }
    synchronized (peerSeqNumberLock)
    {
      peerSeqNumber = paramInt;
      peerTokenTracker = new TokenTracker(peerSeqNumber);
    }
  }
  
  final void setKey(int paramInt, EncryptionKey paramEncryptionKey)
    throws GSSException
  {
    key = paramEncryptionKey;
    keySrc = paramInt;
    cipherHelper = new CipherHelper(paramEncryptionKey);
  }
  
  public final int getKeySrc()
  {
    return keySrc;
  }
  
  private final EncryptionKey getKey()
  {
    return key;
  }
  
  final void setDelegCred(Krb5CredElement paramKrb5CredElement)
  {
    delegatedCred = paramKrb5CredElement;
  }
  
  final void setCredDelegState(boolean paramBoolean)
  {
    credDelegState = paramBoolean;
  }
  
  final void setMutualAuthState(boolean paramBoolean)
  {
    mutualAuthState = paramBoolean;
  }
  
  final void setReplayDetState(boolean paramBoolean)
  {
    replayDetState = paramBoolean;
  }
  
  final void setSequenceDetState(boolean paramBoolean)
  {
    sequenceDetState = paramBoolean;
  }
  
  final void setConfState(boolean paramBoolean)
  {
    confState = paramBoolean;
  }
  
  final void setIntegState(boolean paramBoolean)
  {
    integState = paramBoolean;
  }
  
  final void setDelegPolicyState(boolean paramBoolean)
  {
    delegPolicyState = paramBoolean;
  }
  
  public final void setChannelBinding(ChannelBinding paramChannelBinding)
    throws GSSException
  {
    channelBinding = paramChannelBinding;
  }
  
  final ChannelBinding getChannelBinding()
  {
    return channelBinding;
  }
  
  public final Oid getMech()
  {
    return Krb5MechFactory.GSS_KRB5_MECH_OID;
  }
  
  public final GSSNameSpi getSrcName()
    throws GSSException
  {
    return isInitiator() ? myName : peerName;
  }
  
  public final GSSNameSpi getTargName()
    throws GSSException
  {
    return !isInitiator() ? myName : peerName;
  }
  
  public final GSSCredentialSpi getDelegCred()
    throws GSSException
  {
    if ((state != 2) && (state != 3)) {
      throw new GSSException(12);
    }
    if (isInitiator()) {
      throw new GSSException(13);
    }
    tryConstrainedDelegation();
    if (delegatedCred == null) {
      throw new GSSException(13);
    }
    return delegatedCred;
  }
  
  private void tryConstrainedDelegation()
  {
    if ((state != 2) && (state != 3)) {
      return;
    }
    if (!isConstrainedDelegationTried)
    {
      if (delegatedCred == null)
      {
        if (DEBUG) {
          System.out.println(">>> Constrained deleg from " + caller);
        }
        try
        {
          delegatedCred = new Krb5ProxyCredential(Krb5InitCredential.getInstance(GSSCaller.CALLER_ACCEPT, myName, lifetime), peerName, serviceTicket);
        }
        catch (GSSException localGSSException) {}
      }
      isConstrainedDelegationTried = true;
    }
  }
  
  public final boolean isInitiator()
  {
    return initiator;
  }
  
  public final boolean isProtReady()
  {
    return state == 3;
  }
  
  public final byte[] initSecContext(InputStream paramInputStream, int paramInt)
    throws GSSException
  {
    byte[] arrayOfByte = null;
    InitSecContextToken localInitSecContextToken = null;
    int i = 11;
    if (DEBUG) {
      System.out.println("Entered Krb5Context.initSecContext with state=" + printState(state));
    }
    if (!isInitiator()) {
      throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
    }
    try
    {
      if (state == 1)
      {
        state = 2;
        i = 13;
        if (myCred == null) {
          myCred = Krb5InitCredential.getInstance(caller, myName, 0);
        } else if (!myCred.isInitiatorCredential()) {
          throw new GSSException(i, -1, "No TGT available");
        }
        myName = ((Krb5NameElement)myCred.getName());
        Credentials localCredentials;
        if ((myCred instanceof Krb5InitCredential))
        {
          localObject1 = null;
          localCredentials = ((Krb5InitCredential)myCred).getKrb5Credentials();
        }
        else
        {
          localObject1 = (Krb5ProxyCredential)myCred;
          localCredentials = self.getKrb5Credentials();
        }
        checkPermission(peerName.getKrb5PrincipalName().getName(), "initiate");
        final AccessControlContext localAccessControlContext = AccessController.getContext();
        final Object localObject2;
        if (GSSUtil.useSubjectCredsOnly(caller))
        {
          localObject2 = null;
          try
          {
            localObject2 = (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public KerberosTicket run()
                throws Exception
              {
                return Krb5Util.getTicket(GSSCaller.CALLER_UNKNOWN, localObject1 == null ? myName.getKrb5PrincipalName().getName() : localObject1.getName().getKrb5PrincipalName().getName(), peerName.getKrb5PrincipalName().getName(), localAccessControlContext);
              }
            });
          }
          catch (PrivilegedActionException localPrivilegedActionException)
          {
            if (DEBUG) {
              System.out.println("Attempt to obtain service ticket from the subject failed!");
            }
          }
          if (localObject2 != null)
          {
            if (DEBUG) {
              System.out.println("Found service ticket in the subject" + localObject2);
            }
            serviceCreds = Krb5Util.ticketToCreds((KerberosTicket)localObject2);
          }
        }
        if (serviceCreds == null)
        {
          if (DEBUG) {
            System.out.println("Service ticket not found in the subject");
          }
          if (localObject1 == null) {
            serviceCreds = Credentials.acquireServiceCreds(peerName.getKrb5PrincipalName().getName(), localCredentials);
          } else {
            serviceCreds = Credentials.acquireS4U2proxyCreds(peerName.getKrb5PrincipalName().getName(), tkt, ((Krb5ProxyCredential)localObject1).getName().getKrb5PrincipalName(), localCredentials);
          }
          if (GSSUtil.useSubjectCredsOnly(caller))
          {
            localObject2 = (Subject)AccessController.doPrivileged(new PrivilegedAction()
            {
              public Subject run()
              {
                return Subject.getSubject(localAccessControlContext);
              }
            });
            if ((localObject2 != null) && (!((Subject)localObject2).isReadOnly()))
            {
              final KerberosTicket localKerberosTicket = Krb5Util.credsToTicket(serviceCreds);
              AccessController.doPrivileged(new PrivilegedAction()
              {
                public Void run()
                {
                  localObject2.getPrivateCredentials().add(localKerberosTicket);
                  return null;
                }
              });
            }
            else if (DEBUG)
            {
              System.out.println("Subject is readOnly;Kerberos Service ticket not stored");
            }
          }
        }
        i = 11;
        localInitSecContextToken = new InitSecContextToken(this, localCredentials, serviceCreds);
        apReq = ((InitSecContextToken)localInitSecContextToken).getKrbApReq();
        arrayOfByte = localInitSecContextToken.encode();
        myCred = null;
        if (!getMutualAuthState()) {
          state = 3;
        }
        if (DEBUG) {
          System.out.println("Created InitSecContextToken:\n" + new HexDumpEncoder().encodeBuffer(arrayOfByte));
        }
      }
      else if (state == 2)
      {
        new AcceptSecContextToken(this, serviceCreds, apReq, paramInputStream);
        serviceCreds = null;
        apReq = null;
        state = 3;
      }
      else if (DEBUG)
      {
        System.out.println(state);
      }
    }
    catch (KrbException localKrbException)
    {
      if (DEBUG) {
        localKrbException.printStackTrace();
      }
      localObject1 = new GSSException(i, -1, localKrbException.getMessage());
      ((GSSException)localObject1).initCause(localKrbException);
      throw ((Throwable)localObject1);
    }
    catch (IOException localIOException)
    {
      final Object localObject1 = new GSSException(i, -1, localIOException.getMessage());
      ((GSSException)localObject1).initCause(localIOException);
      throw ((Throwable)localObject1);
    }
    return arrayOfByte;
  }
  
  public final boolean isEstablished()
  {
    return state == 3;
  }
  
  public final byte[] acceptSecContext(InputStream paramInputStream, int paramInt)
    throws GSSException
  {
    byte[] arrayOfByte = null;
    if (DEBUG) {
      System.out.println("Entered Krb5Context.acceptSecContext with state=" + printState(state));
    }
    if (isInitiator()) {
      throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
    }
    try
    {
      if (state == 1)
      {
        state = 2;
        if (myCred == null) {
          myCred = Krb5AcceptCredential.getInstance(caller, myName);
        } else if (!myCred.isAcceptorCredential()) {
          throw new GSSException(13, -1, "No Secret Key available");
        }
        myName = ((Krb5NameElement)myCred.getName());
        if (myName != null) {
          Krb5MechFactory.checkAcceptCredPermission(myName, myName);
        }
        InitSecContextToken localInitSecContextToken = new InitSecContextToken(this, (Krb5AcceptCredential)myCred, paramInputStream);
        localObject = localInitSecContextToken.getKrbApReq().getClient();
        peerName = Krb5NameElement.getInstance((PrincipalName)localObject);
        if (myName == null)
        {
          myName = Krb5NameElement.getInstance(localInitSecContextToken.getKrbApReq().getCreds().getServer());
          Krb5MechFactory.checkAcceptCredPermission(myName, myName);
        }
        if (getMutualAuthState()) {
          arrayOfByte = new AcceptSecContextToken(this, localInitSecContextToken.getKrbApReq()).encode();
        }
        serviceTicket = localInitSecContextToken.getKrbApReq().getCreds().getTicket();
        myCred = null;
        state = 3;
      }
      else if (DEBUG)
      {
        System.out.println(state);
      }
    }
    catch (KrbException localKrbException)
    {
      localObject = new GSSException(11, -1, localKrbException.getMessage());
      ((GSSException)localObject).initCause(localKrbException);
      throw ((Throwable)localObject);
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        localIOException.printStackTrace();
      }
      Object localObject = new GSSException(11, -1, localIOException.getMessage());
      ((GSSException)localObject).initCause(localIOException);
      throw ((Throwable)localObject);
    }
    return arrayOfByte;
  }
  
  public final int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2)
    throws GSSException
  {
    int i = 0;
    if (cipherHelper.getProto() == 0) {
      i = WrapToken.getSizeLimit(paramInt1, paramBoolean, paramInt2, getCipherHelper(null));
    } else if (cipherHelper.getProto() == 1) {
      i = WrapToken_v2.getSizeLimit(paramInt1, paramBoolean, paramInt2, getCipherHelper(null));
    }
    return i;
  }
  
  public final byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (DEBUG) {
      System.out.println("Krb5Context.wrap: data=[" + getHexBytes(paramArrayOfByte, paramInt1, paramInt2) + "]");
    }
    if (state != 3) {
      throw new GSSException(12, -1, "Wrap called in invalid state!");
    }
    byte[] arrayOfByte = null;
    try
    {
      Object localObject;
      if (cipherHelper.getProto() == 0)
      {
        localObject = new WrapToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = ((WrapToken)localObject).encode();
      }
      else if (cipherHelper.getProto() == 1)
      {
        localObject = new WrapToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = ((WrapToken_v2)localObject).encode();
      }
      if (DEBUG) {
        System.out.println("Krb5Context.wrap: token=[" + getHexBytes(arrayOfByte, 0, arrayOfByte.length) + "]");
      }
      return arrayOfByte;
    }
    catch (IOException localIOException)
    {
      arrayOfByte = null;
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
  }
  
  public final int wrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    if (state != 3) {
      throw new GSSException(12, -1, "Wrap called in invalid state!");
    }
    int i = 0;
    try
    {
      Object localObject;
      if (cipherHelper.getProto() == 0)
      {
        localObject = new WrapToken(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = ((WrapToken)localObject).encode(paramArrayOfByte2, paramInt3);
      }
      else if (cipherHelper.getProto() == 1)
      {
        localObject = new WrapToken_v2(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = ((WrapToken_v2)localObject).encode(paramArrayOfByte2, paramInt3);
      }
      if (DEBUG) {
        System.out.println("Krb5Context.wrap: token=[" + getHexBytes(paramArrayOfByte2, paramInt3, i) + "]");
      }
      return i;
    }
    catch (IOException localIOException)
    {
      i = 0;
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
  }
  
  public final void wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (state != 3) {
      throw new GSSException(12, -1, "Wrap called in invalid state!");
    }
    byte[] arrayOfByte = null;
    try
    {
      Object localObject;
      if (cipherHelper.getProto() == 0)
      {
        localObject = new WrapToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        ((WrapToken)localObject).encode(paramOutputStream);
        if (DEBUG) {
          arrayOfByte = ((WrapToken)localObject).encode();
        }
      }
      else if (cipherHelper.getProto() == 1)
      {
        localObject = new WrapToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        ((WrapToken_v2)localObject).encode(paramOutputStream);
        if (DEBUG) {
          arrayOfByte = ((WrapToken_v2)localObject).encode();
        }
      }
    }
    catch (IOException localIOException)
    {
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
    if (DEBUG) {
      System.out.println("Krb5Context.wrap: token=[" + getHexBytes(arrayOfByte, 0, arrayOfByte.length) + "]");
    }
  }
  
  public final void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = new byte[paramInputStream.available()];
      paramInputStream.read(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
    wrap(arrayOfByte, 0, arrayOfByte.length, paramOutputStream, paramMessageProp);
  }
  
  public final byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (DEBUG) {
      System.out.println("Krb5Context.unwrap: token=[" + getHexBytes(paramArrayOfByte, paramInt1, paramInt2) + "]");
    }
    if (state != 3) {
      throw new GSSException(12, -1, " Unwrap called in invalid state!");
    }
    byte[] arrayOfByte = null;
    Object localObject;
    if (cipherHelper.getProto() == 0)
    {
      localObject = new WrapToken(this, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
      arrayOfByte = ((WrapToken)localObject).getData();
      setSequencingAndReplayProps((MessageToken)localObject, paramMessageProp);
    }
    else if (cipherHelper.getProto() == 1)
    {
      localObject = new WrapToken_v2(this, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
      arrayOfByte = ((WrapToken_v2)localObject).getData();
      setSequencingAndReplayProps((MessageToken_v2)localObject, paramMessageProp);
    }
    if (DEBUG) {
      System.out.println("Krb5Context.unwrap: data=[" + getHexBytes(arrayOfByte, 0, arrayOfByte.length) + "]");
    }
    return arrayOfByte;
  }
  
  public final int unwrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    if (state != 3) {
      throw new GSSException(12, -1, "Unwrap called in invalid state!");
    }
    Object localObject;
    if (cipherHelper.getProto() == 0)
    {
      localObject = new WrapToken(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      paramInt2 = ((WrapToken)localObject).getData(paramArrayOfByte2, paramInt3);
      setSequencingAndReplayProps((MessageToken)localObject, paramMessageProp);
    }
    else if (cipherHelper.getProto() == 1)
    {
      localObject = new WrapToken_v2(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      paramInt2 = ((WrapToken_v2)localObject).getData(paramArrayOfByte2, paramInt3);
      setSequencingAndReplayProps((MessageToken_v2)localObject, paramMessageProp);
    }
    return paramInt2;
  }
  
  public final int unwrap(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt, MessageProp paramMessageProp)
    throws GSSException
  {
    if (state != 3) {
      throw new GSSException(12, -1, "Unwrap called in invalid state!");
    }
    int i = 0;
    Object localObject;
    if (cipherHelper.getProto() == 0)
    {
      localObject = new WrapToken(this, paramInputStream, paramMessageProp);
      i = ((WrapToken)localObject).getData(paramArrayOfByte, paramInt);
      setSequencingAndReplayProps((MessageToken)localObject, paramMessageProp);
    }
    else if (cipherHelper.getProto() == 1)
    {
      localObject = new WrapToken_v2(this, paramInputStream, paramMessageProp);
      i = ((WrapToken_v2)localObject).getData(paramArrayOfByte, paramInt);
      setSequencingAndReplayProps((MessageToken_v2)localObject, paramMessageProp);
    }
    return i;
  }
  
  public final void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (state != 3) {
      throw new GSSException(12, -1, "Unwrap called in invalid state!");
    }
    byte[] arrayOfByte = null;
    Object localObject;
    if (cipherHelper.getProto() == 0)
    {
      localObject = new WrapToken(this, paramInputStream, paramMessageProp);
      arrayOfByte = ((WrapToken)localObject).getData();
      setSequencingAndReplayProps((MessageToken)localObject, paramMessageProp);
    }
    else if (cipherHelper.getProto() == 1)
    {
      localObject = new WrapToken_v2(this, paramInputStream, paramMessageProp);
      arrayOfByte = ((WrapToken_v2)localObject).getData();
      setSequencingAndReplayProps((MessageToken_v2)localObject, paramMessageProp);
    }
    try
    {
      paramOutputStream.write(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
  }
  
  public final byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte = null;
    try
    {
      Object localObject;
      if (cipherHelper.getProto() == 0)
      {
        localObject = new MicToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = ((MicToken)localObject).encode();
      }
      else if (cipherHelper.getProto() == 1)
      {
        localObject = new MicToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = ((MicToken_v2)localObject).encode();
      }
      return arrayOfByte;
    }
    catch (IOException localIOException)
    {
      arrayOfByte = null;
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
  }
  
  private int getMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    int i = 0;
    try
    {
      Object localObject;
      if (cipherHelper.getProto() == 0)
      {
        localObject = new MicToken(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = ((MicToken)localObject).encode(paramArrayOfByte2, paramInt3);
      }
      else if (cipherHelper.getProto() == 1)
      {
        localObject = new MicToken_v2(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = ((MicToken_v2)localObject).encode(paramArrayOfByte2, paramInt3);
      }
      return i;
    }
    catch (IOException localIOException)
    {
      i = 0;
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
  }
  
  private void getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    try
    {
      Object localObject;
      if (cipherHelper.getProto() == 0)
      {
        localObject = new MicToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        ((MicToken)localObject).encode(paramOutputStream);
      }
      else if (cipherHelper.getProto() == 1)
      {
        localObject = new MicToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        ((MicToken_v2)localObject).encode(paramOutputStream);
      }
    }
    catch (IOException localIOException)
    {
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
  }
  
  public final void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = new byte[paramInputStream.available()];
      paramInputStream.read(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
    getMIC(arrayOfByte, 0, arrayOfByte.length, paramOutputStream, paramMessageProp);
  }
  
  public final void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp)
    throws GSSException
  {
    Object localObject;
    if (cipherHelper.getProto() == 0)
    {
      localObject = new MicToken(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      ((MicToken)localObject).verify(paramArrayOfByte2, paramInt3, paramInt4);
      setSequencingAndReplayProps((MessageToken)localObject, paramMessageProp);
    }
    else if (cipherHelper.getProto() == 1)
    {
      localObject = new MicToken_v2(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      ((MicToken_v2)localObject).verify(paramArrayOfByte2, paramInt3, paramInt4);
      setSequencingAndReplayProps((MessageToken_v2)localObject, paramMessageProp);
    }
  }
  
  private void verifyMIC(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    Object localObject;
    if (cipherHelper.getProto() == 0)
    {
      localObject = new MicToken(this, paramInputStream, paramMessageProp);
      ((MicToken)localObject).verify(paramArrayOfByte, paramInt1, paramInt2);
      setSequencingAndReplayProps((MessageToken)localObject, paramMessageProp);
    }
    else if (cipherHelper.getProto() == 1)
    {
      localObject = new MicToken_v2(this, paramInputStream, paramMessageProp);
      ((MicToken_v2)localObject).verify(paramArrayOfByte, paramInt1, paramInt2);
      setSequencingAndReplayProps((MessageToken_v2)localObject, paramMessageProp);
    }
  }
  
  public final void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = new byte[paramInputStream2.available()];
      paramInputStream2.read(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      GSSException localGSSException = new GSSException(11, -1, localIOException.getMessage());
      localGSSException.initCause(localIOException);
      throw localGSSException;
    }
    verifyMIC(paramInputStream1, arrayOfByte, 0, arrayOfByte.length, paramMessageProp);
  }
  
  public final byte[] export()
    throws GSSException
  {
    throw new GSSException(16, -1, "GSS Export Context not available");
  }
  
  public final void dispose()
    throws GSSException
  {
    state = 4;
    delegatedCred = null;
  }
  
  public final Provider getProvider()
  {
    return Krb5MechFactory.PROVIDER;
  }
  
  private void setSequencingAndReplayProps(MessageToken paramMessageToken, MessageProp paramMessageProp)
  {
    if ((replayDetState) || (sequenceDetState))
    {
      int i = paramMessageToken.getSequenceNumber();
      peerTokenTracker.getProps(i, paramMessageProp);
    }
  }
  
  private void setSequencingAndReplayProps(MessageToken_v2 paramMessageToken_v2, MessageProp paramMessageProp)
  {
    if ((replayDetState) || (sequenceDetState))
    {
      int i = paramMessageToken_v2.getSequenceNumber();
      peerTokenTracker.getProps(i, paramMessageProp);
    }
  }
  
  private void checkPermission(String paramString1, String paramString2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      ServicePermission localServicePermission = new ServicePermission(paramString1, paramString2);
      localSecurityManager.checkPermission(localServicePermission);
    }
  }
  
  private static String getHexBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramArrayOfByte[i] >> 4 & 0xF;
      int k = paramArrayOfByte[i] & 0xF;
      localStringBuffer.append(Integer.toHexString(j));
      localStringBuffer.append(Integer.toHexString(k));
      localStringBuffer.append(' ');
    }
    return localStringBuffer.toString();
  }
  
  private static String printState(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "STATE_NEW";
    case 2: 
      return "STATE_IN_PROCESS";
    case 3: 
      return "STATE_DONE";
    case 4: 
      return "STATE_DELETED";
    }
    return "Unknown state " + paramInt;
  }
  
  GSSCaller getCaller()
  {
    return caller;
  }
  
  public Object inquireSecContext(InquireType paramInquireType)
    throws GSSException
  {
    if (!isEstablished()) {
      throw new GSSException(12, -1, "Security context not established.");
    }
    switch (paramInquireType)
    {
    case KRB5_GET_SESSION_KEY: 
      return new KerberosSessionKey(key);
    case KRB5_GET_TKT_FLAGS: 
      return tktFlags.clone();
    case KRB5_GET_AUTHZ_DATA: 
      if (isInitiator()) {
        throw new GSSException(16, -1, "AuthzData not available on initiator side.");
      }
      return authzData == null ? null : authzData.clone();
    case KRB5_GET_AUTHTIME: 
      return authTime;
    }
    throw new GSSException(16, -1, "Inquire type not supported.");
  }
  
  public void setTktFlags(boolean[] paramArrayOfBoolean)
  {
    tktFlags = paramArrayOfBoolean;
  }
  
  public void setAuthTime(String paramString)
  {
    authTime = paramString;
  }
  
  public void setAuthzData(AuthorizationDataEntry[] paramArrayOfAuthorizationDataEntry)
  {
    authzData = paramArrayOfAuthorizationDataEntry;
  }
  
  static class KerberosSessionKey
    implements Key
  {
    private static final long serialVersionUID = 699307378954123869L;
    private final EncryptionKey key;
    
    KerberosSessionKey(EncryptionKey paramEncryptionKey)
    {
      key = paramEncryptionKey;
    }
    
    public String getAlgorithm()
    {
      return Integer.toString(key.getEType());
    }
    
    public String getFormat()
    {
      return "RAW";
    }
    
    public byte[] getEncoded()
    {
      return (byte[])key.getBytes().clone();
    }
    
    public String toString()
    {
      return "Kerberos session key: etype: " + key.getEType() + "\n" + new HexDumpEncoder().encodeBuffer(key.getBytes());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\Krb5Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */