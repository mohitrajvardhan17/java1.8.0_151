package sun.security.jgss.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import com.sun.security.jgss.InquireType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.Provider;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSCredentialImpl;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSNameImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class SpNegoContext
  implements GSSContextSpi
{
  private static final int STATE_NEW = 1;
  private static final int STATE_IN_PROCESS = 2;
  private static final int STATE_DONE = 3;
  private static final int STATE_DELETED = 4;
  private int state = 1;
  private boolean credDelegState = false;
  private boolean mutualAuthState = true;
  private boolean replayDetState = true;
  private boolean sequenceDetState = true;
  private boolean confState = true;
  private boolean integState = true;
  private boolean delegPolicyState = false;
  private GSSNameSpi peerName = null;
  private GSSNameSpi myName = null;
  private SpNegoCredElement myCred = null;
  private GSSContext mechContext = null;
  private byte[] DER_mechTypes = null;
  private int lifetime;
  private ChannelBinding channelBinding;
  private boolean initiator;
  private Oid internal_mech = null;
  private final SpNegoMechFactory factory;
  static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.spnego.debug"))).booleanValue();
  
  public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt)
    throws GSSException
  {
    if (paramGSSNameSpi == null) {
      throw new IllegalArgumentException("Cannot have null peer name");
    }
    if ((paramGSSCredentialSpi != null) && (!(paramGSSCredentialSpi instanceof SpNegoCredElement))) {
      throw new IllegalArgumentException("Wrong cred element type");
    }
    peerName = paramGSSNameSpi;
    myCred = ((SpNegoCredElement)paramGSSCredentialSpi);
    lifetime = paramInt;
    initiator = true;
    factory = paramSpNegoMechFactory;
  }
  
  public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSCredentialSpi paramGSSCredentialSpi)
    throws GSSException
  {
    if ((paramGSSCredentialSpi != null) && (!(paramGSSCredentialSpi instanceof SpNegoCredElement))) {
      throw new IllegalArgumentException("Wrong cred element type");
    }
    myCred = ((SpNegoCredElement)paramGSSCredentialSpi);
    initiator = false;
    factory = paramSpNegoMechFactory;
  }
  
  public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, byte[] paramArrayOfByte)
    throws GSSException
  {
    throw new GSSException(16, -1, "GSS Import Context not available");
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
  
  public final void requestDelegPolicy(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      delegPolicyState = paramBoolean;
    }
  }
  
  public final boolean getIntegState()
  {
    return integState;
  }
  
  public final boolean getDelegPolicyState()
  {
    if ((isInitiator()) && (mechContext != null) && ((mechContext instanceof ExtendedGSSContext)) && ((state == 2) || (state == 3))) {
      return ((ExtendedGSSContext)mechContext).getDelegPolicyState();
    }
    return delegPolicyState;
  }
  
  public final void requestCredDeleg(boolean paramBoolean)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      credDelegState = paramBoolean;
    }
  }
  
  public final boolean getCredDelegState()
  {
    if ((isInitiator()) && (mechContext != null) && ((state == 2) || (state == 3))) {
      return mechContext.getCredDelegState();
    }
    return credDelegState;
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
  
  public final Oid getMech()
  {
    if (isEstablished()) {
      return getNegotiatedMech();
    }
    return SpNegoMechFactory.GSS_SPNEGO_MECH_OID;
  }
  
  public final Oid getNegotiatedMech()
  {
    return internal_mech;
  }
  
  public final Provider getProvider()
  {
    return SpNegoMechFactory.PROVIDER;
  }
  
  public final void dispose()
    throws GSSException
  {
    mechContext = null;
    state = 4;
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
    Object localObject1 = null;
    NegTokenInit localNegTokenInit = null;
    byte[] arrayOfByte1 = null;
    int i = 11;
    if (DEBUG) {
      System.out.println("Entered SpNego.initSecContext with state=" + printState(state));
    }
    if (!isInitiator()) {
      throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
    }
    try
    {
      Object localObject2;
      if (state == 1)
      {
        state = 2;
        i = 13;
        localObject2 = getAvailableMechs();
        DER_mechTypes = getEncodedMechs((Oid[])localObject2);
        internal_mech = localObject2[0];
        arrayOfByte1 = GSS_initSecContext(null);
        i = 10;
        localNegTokenInit = new NegTokenInit(DER_mechTypes, getContextFlags(), arrayOfByte1, null);
        if (DEBUG) {
          System.out.println("SpNegoContext.initSecContext: sending token of type = " + SpNegoToken.getTokenName(localNegTokenInit.getType()));
        }
        localObject1 = localNegTokenInit.getEncoded();
      }
      else if (state == 2)
      {
        i = 11;
        if (paramInputStream == null) {
          throw new GSSException(i, -1, "No token received from peer!");
        }
        i = 10;
        localObject2 = new byte[paramInputStream.available()];
        SpNegoToken.readFully(paramInputStream, (byte[])localObject2);
        if (DEBUG) {
          System.out.println("SpNegoContext.initSecContext: process received token = " + SpNegoToken.getHexBytes((byte[])localObject2));
        }
        localObject3 = new NegTokenTarg((byte[])localObject2);
        if (DEBUG) {
          System.out.println("SpNegoContext.initSecContext: received token of type = " + SpNegoToken.getTokenName(((NegTokenTarg)localObject3).getType()));
        }
        internal_mech = ((NegTokenTarg)localObject3).getSupportedMech();
        if (internal_mech == null) {
          throw new GSSException(i, -1, "supported mechanism from server is null");
        }
        SpNegoToken.NegoResult localNegoResult = null;
        int j = ((NegTokenTarg)localObject3).getNegotiatedResult();
        switch (j)
        {
        case 0: 
          localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
          state = 3;
          break;
        case 1: 
          localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
          state = 2;
          break;
        case 2: 
          localNegoResult = SpNegoToken.NegoResult.REJECT;
          state = 4;
          break;
        default: 
          state = 3;
        }
        i = 2;
        if (localNegoResult == SpNegoToken.NegoResult.REJECT) {
          throw new GSSException(i, -1, internal_mech.toString());
        }
        i = 10;
        if ((localNegoResult == SpNegoToken.NegoResult.ACCEPT_COMPLETE) || (localNegoResult == SpNegoToken.NegoResult.ACCEPT_INCOMPLETE))
        {
          byte[] arrayOfByte2 = ((NegTokenTarg)localObject3).getResponseToken();
          if (arrayOfByte2 == null)
          {
            if (!isMechContextEstablished()) {
              throw new GSSException(i, -1, "mechanism token from server is null");
            }
          }
          else {
            arrayOfByte1 = GSS_initSecContext(arrayOfByte2);
          }
          if (!GSSUtil.useMSInterop())
          {
            byte[] arrayOfByte3 = ((NegTokenTarg)localObject3).getMechListMIC();
            if (!verifyMechListMIC(DER_mechTypes, arrayOfByte3)) {
              throw new GSSException(i, -1, "verification of MIC on MechList Failed!");
            }
          }
          if (isMechContextEstablished())
          {
            state = 3;
            localObject1 = arrayOfByte1;
            if (DEBUG) {
              System.out.println("SPNEGO Negotiated Mechanism = " + internal_mech + " " + GSSUtil.getMechStr(internal_mech));
            }
          }
          else
          {
            localNegTokenInit = new NegTokenInit(null, null, arrayOfByte1, null);
            if (DEBUG) {
              System.out.println("SpNegoContext.initSecContext: continue sending token of type = " + SpNegoToken.getTokenName(localNegTokenInit.getType()));
            }
            localObject1 = localNegTokenInit.getEncoded();
          }
        }
      }
      else if (DEBUG)
      {
        System.out.println(state);
      }
      if ((DEBUG) && (localObject1 != null)) {
        System.out.println("SNegoContext.initSecContext: sending token = " + SpNegoToken.getHexBytes((byte[])localObject1));
      }
    }
    catch (GSSException localGSSException)
    {
      localObject3 = new GSSException(i, -1, localGSSException.getMessage());
      ((GSSException)localObject3).initCause(localGSSException);
      throw ((Throwable)localObject3);
    }
    catch (IOException localIOException)
    {
      Object localObject3 = new GSSException(11, -1, localIOException.getMessage());
      ((GSSException)localObject3).initCause(localIOException);
      throw ((Throwable)localObject3);
    }
    return (byte[])localObject1;
  }
  
  public final byte[] acceptSecContext(InputStream paramInputStream, int paramInt)
    throws GSSException
  {
    byte[] arrayOfByte1 = null;
    boolean bool = true;
    if (DEBUG) {
      System.out.println("Entered SpNegoContext.acceptSecContext with state=" + printState(state));
    }
    if (isInitiator()) {
      throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
    }
    try
    {
      byte[] arrayOfByte2;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      SpNegoToken.NegoResult localNegoResult;
      if (state == 1)
      {
        state = 2;
        arrayOfByte2 = new byte[paramInputStream.available()];
        SpNegoToken.readFully(paramInputStream, arrayOfByte2);
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(arrayOfByte2));
        }
        localObject1 = new NegTokenInit(arrayOfByte2);
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(((NegTokenInit)localObject1).getType()));
        }
        localObject2 = ((NegTokenInit)localObject1).getMechTypeList();
        DER_mechTypes = ((NegTokenInit)localObject1).getMechTypes();
        if (DER_mechTypes == null) {
          bool = false;
        }
        localObject3 = getAvailableMechs();
        localObject4 = negotiate_mech_type((Oid[])localObject3, (Oid[])localObject2);
        if (localObject4 == null) {
          bool = false;
        }
        internal_mech = ((Oid)localObject4);
        byte[] arrayOfByte3;
        if ((localObject2[0].equals(localObject4)) || ((GSSUtil.isKerberosMech(localObject2[0])) && (GSSUtil.isKerberosMech((Oid)localObject4))))
        {
          if ((DEBUG) && (!((Oid)localObject4).equals(localObject2[0]))) {
            System.out.println("SpNegoContext.acceptSecContext: negotiated mech adjusted to " + localObject2[0]);
          }
          localObject5 = ((NegTokenInit)localObject1).getMechToken();
          if (localObject5 == null) {
            throw new GSSException(11, -1, "mechToken is missing");
          }
          arrayOfByte3 = GSS_acceptSecContext((byte[])localObject5);
          localObject4 = localObject2[0];
        }
        else
        {
          arrayOfByte3 = null;
        }
        if ((!GSSUtil.useMSInterop()) && (bool)) {
          bool = verifyMechListMIC(DER_mechTypes, ((NegTokenInit)localObject1).getMechListMIC());
        }
        if (bool)
        {
          if (isMechContextEstablished())
          {
            localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
            state = 3;
            setContextFlags();
            if (DEBUG) {
              System.out.println("SPNEGO Negotiated Mechanism = " + internal_mech + " " + GSSUtil.getMechStr(internal_mech));
            }
          }
          else
          {
            localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
            state = 2;
          }
        }
        else
        {
          localNegoResult = SpNegoToken.NegoResult.REJECT;
          state = 3;
        }
        if (DEBUG)
        {
          System.out.println("SpNegoContext.acceptSecContext: mechanism wanted = " + localObject4);
          System.out.println("SpNegoContext.acceptSecContext: negotiated result = " + localNegoResult);
        }
        Object localObject5 = new NegTokenTarg(localNegoResult.ordinal(), (Oid)localObject4, arrayOfByte3, null);
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(((NegTokenTarg)localObject5).getType()));
        }
        arrayOfByte1 = ((NegTokenTarg)localObject5).getEncoded();
      }
      else if (state == 2)
      {
        arrayOfByte2 = new byte[paramInputStream.available()];
        SpNegoToken.readFully(paramInputStream, arrayOfByte2);
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(arrayOfByte2));
        }
        localObject1 = new NegTokenTarg(arrayOfByte2);
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(((NegTokenTarg)localObject1).getType()));
        }
        localObject2 = ((NegTokenTarg)localObject1).getResponseToken();
        localObject3 = GSS_acceptSecContext((byte[])localObject2);
        if (localObject3 == null) {
          bool = false;
        }
        if (bool)
        {
          if (isMechContextEstablished())
          {
            localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
            state = 3;
          }
          else
          {
            localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
            state = 2;
          }
        }
        else
        {
          localNegoResult = SpNegoToken.NegoResult.REJECT;
          state = 3;
        }
        localObject4 = new NegTokenTarg(localNegoResult.ordinal(), null, (byte[])localObject3, null);
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(((NegTokenTarg)localObject4).getType()));
        }
        arrayOfByte1 = ((NegTokenTarg)localObject4).getEncoded();
      }
      else if (DEBUG)
      {
        System.out.println("AcceptSecContext: state = " + state);
      }
      if (DEBUG) {
        System.out.println("SpNegoContext.acceptSecContext: sending token = " + SpNegoToken.getHexBytes(arrayOfByte1));
      }
    }
    catch (IOException localIOException)
    {
      Object localObject1 = new GSSException(11, -1, localIOException.getMessage());
      ((GSSException)localObject1).initCause(localIOException);
      throw ((Throwable)localObject1);
    }
    if (state == 3) {
      setContextFlags();
    }
    return arrayOfByte1;
  }
  
  private Oid[] getAvailableMechs()
  {
    if (myCred != null)
    {
      Oid[] arrayOfOid = new Oid[1];
      arrayOfOid[0] = myCred.getInternalMech();
      return arrayOfOid;
    }
    return factory.availableMechs;
  }
  
  private byte[] getEncodedMechs(Oid[] paramArrayOfOid)
    throws IOException, GSSException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    for (int i = 0; i < paramArrayOfOid.length; i++)
    {
      arrayOfByte = paramArrayOfOid[i].getDER();
      localDerOutputStream1.write(arrayOfByte);
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    byte[] arrayOfByte = localDerOutputStream2.toByteArray();
    return arrayOfByte;
  }
  
  private BitArray getContextFlags()
  {
    BitArray localBitArray = new BitArray(7);
    if (getCredDelegState()) {
      localBitArray.set(0, true);
    }
    if (getMutualAuthState()) {
      localBitArray.set(1, true);
    }
    if (getReplayDetState()) {
      localBitArray.set(2, true);
    }
    if (getSequenceDetState()) {
      localBitArray.set(3, true);
    }
    if (getConfState()) {
      localBitArray.set(5, true);
    }
    if (getIntegState()) {
      localBitArray.set(6, true);
    }
    return localBitArray;
  }
  
  private void setContextFlags()
  {
    if (mechContext != null)
    {
      if (mechContext.getCredDelegState()) {
        credDelegState = true;
      }
      if (!mechContext.getMutualAuthState()) {
        mutualAuthState = false;
      }
      if (!mechContext.getReplayDetState()) {
        replayDetState = false;
      }
      if (!mechContext.getSequenceDetState()) {
        sequenceDetState = false;
      }
      if (!mechContext.getIntegState()) {
        integState = false;
      }
      if (!mechContext.getConfState()) {
        confState = false;
      }
    }
  }
  
  private boolean verifyMechListMIC(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws GSSException
  {
    if (paramArrayOfByte2 == null)
    {
      if (DEBUG) {
        System.out.println("SpNegoContext: no MIC token validation");
      }
      return true;
    }
    if (!mechContext.getIntegState())
    {
      if (DEBUG) {
        System.out.println("SpNegoContext: no MIC token validation - mechanism does not support integrity");
      }
      return true;
    }
    boolean bool = false;
    try
    {
      MessageProp localMessageProp = new MessageProp(0, true);
      verifyMIC(paramArrayOfByte2, 0, paramArrayOfByte2.length, paramArrayOfByte1, 0, paramArrayOfByte1.length, localMessageProp);
      bool = true;
    }
    catch (GSSException localGSSException)
    {
      bool = false;
      if (DEBUG) {
        System.out.println("SpNegoContext: MIC validation failed! " + localGSSException.getMessage());
      }
    }
    return bool;
  }
  
  private byte[] GSS_initSecContext(byte[] paramArrayOfByte)
    throws GSSException
  {
    byte[] arrayOfByte = null;
    if (mechContext == null)
    {
      localObject = factory.manager.createName(peerName.toString(), peerName.getStringNameType(), internal_mech);
      GSSCredentialImpl localGSSCredentialImpl = null;
      if (myCred != null) {
        localGSSCredentialImpl = new GSSCredentialImpl(factory.manager, myCred.getInternalCred());
      }
      mechContext = factory.manager.createContext((GSSName)localObject, internal_mech, localGSSCredentialImpl, 0);
      mechContext.requestConf(confState);
      mechContext.requestInteg(integState);
      mechContext.requestCredDeleg(credDelegState);
      mechContext.requestMutualAuth(mutualAuthState);
      mechContext.requestReplayDet(replayDetState);
      mechContext.requestSequenceDet(sequenceDetState);
      if ((mechContext instanceof ExtendedGSSContext)) {
        ((ExtendedGSSContext)mechContext).requestDelegPolicy(delegPolicyState);
      }
    }
    if (paramArrayOfByte != null) {
      arrayOfByte = paramArrayOfByte;
    } else {
      arrayOfByte = new byte[0];
    }
    Object localObject = mechContext.initSecContext(arrayOfByte, 0, arrayOfByte.length);
    return (byte[])localObject;
  }
  
  private byte[] GSS_acceptSecContext(byte[] paramArrayOfByte)
    throws GSSException
  {
    if (mechContext == null)
    {
      localObject = null;
      if (myCred != null) {
        localObject = new GSSCredentialImpl(factory.manager, myCred.getInternalCred());
      }
      mechContext = factory.manager.createContext((GSSCredential)localObject);
    }
    Object localObject = mechContext.acceptSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
    return (byte[])localObject;
  }
  
  private static Oid negotiate_mech_type(Oid[] paramArrayOfOid1, Oid[] paramArrayOfOid2)
  {
    for (int i = 0; i < paramArrayOfOid1.length; i++) {
      for (int j = 0; j < paramArrayOfOid2.length; j++) {
        if (paramArrayOfOid2[j].equals(paramArrayOfOid1[i]))
        {
          if (DEBUG) {
            System.out.println("SpNegoContext: negotiated mechanism = " + paramArrayOfOid2[j]);
          }
          return paramArrayOfOid2[j];
        }
      }
    }
    return null;
  }
  
  public final boolean isEstablished()
  {
    return state == 3;
  }
  
  public final boolean isMechContextEstablished()
  {
    if (mechContext != null) {
      return mechContext.isEstablished();
    }
    if (DEBUG) {
      System.out.println("The underlying mechanism context has not been initialized");
    }
    return false;
  }
  
  public final byte[] export()
    throws GSSException
  {
    throw new GSSException(16, -1, "GSS Export Context not available");
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
  
  public final void requestAnonymity(boolean paramBoolean)
    throws GSSException
  {}
  
  public final boolean getAnonymityState()
  {
    return false;
  }
  
  public void requestLifetime(int paramInt)
    throws GSSException
  {
    if ((state == 1) && (isInitiator())) {
      lifetime = paramInt;
    }
  }
  
  public final int getLifetime()
  {
    if (mechContext != null) {
      return mechContext.getLifetime();
    }
    return Integer.MAX_VALUE;
  }
  
  public final boolean isTransferable()
    throws GSSException
  {
    return false;
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
  
  public final GSSNameSpi getTargName()
    throws GSSException
  {
    if (mechContext != null)
    {
      GSSNameImpl localGSSNameImpl = (GSSNameImpl)mechContext.getTargName();
      peerName = localGSSNameImpl.getElement(internal_mech);
      return peerName;
    }
    if (DEBUG) {
      System.out.println("The underlying mechanism context has not been initialized");
    }
    return null;
  }
  
  public final GSSNameSpi getSrcName()
    throws GSSException
  {
    if (mechContext != null)
    {
      GSSNameImpl localGSSNameImpl = (GSSNameImpl)mechContext.getSrcName();
      myName = localGSSNameImpl.getElement(internal_mech);
      return myName;
    }
    if (DEBUG) {
      System.out.println("The underlying mechanism context has not been initialized");
    }
    return null;
  }
  
  public final GSSCredentialSpi getDelegCred()
    throws GSSException
  {
    if ((state != 2) && (state != 3)) {
      throw new GSSException(12);
    }
    if (mechContext != null)
    {
      GSSCredentialImpl localGSSCredentialImpl = (GSSCredentialImpl)mechContext.getDelegCred();
      if (localGSSCredentialImpl == null) {
        return null;
      }
      boolean bool = false;
      if (localGSSCredentialImpl.getUsage() == 1) {
        bool = true;
      }
      GSSCredentialSpi localGSSCredentialSpi = localGSSCredentialImpl.getElement(internal_mech, bool);
      SpNegoCredElement localSpNegoCredElement = new SpNegoCredElement(localGSSCredentialSpi);
      return localSpNegoCredElement.getInternalCred();
    }
    throw new GSSException(12, -1, "getDelegCred called in invalid state!");
  }
  
  public final int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2)
    throws GSSException
  {
    if (mechContext != null) {
      return mechContext.getWrapSizeLimit(paramInt1, paramBoolean, paramInt2);
    }
    throw new GSSException(12, -1, "getWrapSizeLimit called in invalid state!");
  }
  
  public final byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      return mechContext.wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    }
    throw new GSSException(12, -1, "Wrap called in invalid state!");
  }
  
  public final void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      mechContext.wrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "Wrap called in invalid state!");
    }
  }
  
  public final byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      return mechContext.unwrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    }
    throw new GSSException(12, -1, "UnWrap called in invalid state!");
  }
  
  public final void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      mechContext.unwrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "UnWrap called in invalid state!");
    }
  }
  
  public final byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      return mechContext.getMIC(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    }
    throw new GSSException(12, -1, "getMIC called in invalid state!");
  }
  
  public final void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      mechContext.getMIC(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "getMIC called in invalid state!");
    }
  }
  
  public final void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      mechContext.verifyMIC(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "verifyMIC called in invalid state!");
    }
  }
  
  public final void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechContext != null) {
      mechContext.verifyMIC(paramInputStream1, paramInputStream2, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "verifyMIC called in invalid state!");
    }
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
  
  public Object inquireSecContext(InquireType paramInquireType)
    throws GSSException
  {
    if (mechContext == null) {
      throw new GSSException(12, -1, "Underlying mech not established.");
    }
    if ((mechContext instanceof ExtendedGSSContext)) {
      return ((ExtendedGSSContext)mechContext).inquireSecContext(paramInquireType);
    }
    throw new GSSException(2, -1, "inquireSecContext not supported by underlying mech.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\spnego\SpNegoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */