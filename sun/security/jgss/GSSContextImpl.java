package sun.security.jgss;

import com.sun.security.jgss.ExtendedGSSContext;
import com.sun.security.jgss.InquireSecContextPermission;
import com.sun.security.jgss.InquireType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.ObjectIdentifier;

class GSSContextImpl
  implements ExtendedGSSContext
{
  private final GSSManagerImpl gssManager;
  private final boolean initiator;
  private static final int PRE_INIT = 1;
  private static final int IN_PROGRESS = 2;
  private static final int READY = 3;
  private static final int DELETED = 4;
  private int currentState = 1;
  private GSSContextSpi mechCtxt = null;
  private Oid mechOid = null;
  private ObjectIdentifier objId = null;
  private GSSCredentialImpl myCred = null;
  private GSSNameImpl srcName = null;
  private GSSNameImpl targName = null;
  private int reqLifetime = Integer.MAX_VALUE;
  private ChannelBinding channelBindings = null;
  private boolean reqConfState = true;
  private boolean reqIntegState = true;
  private boolean reqMutualAuthState = true;
  private boolean reqReplayDetState = true;
  private boolean reqSequenceDetState = true;
  private boolean reqCredDelegState = false;
  private boolean reqAnonState = false;
  private boolean reqDelegPolicyState = false;
  
  public GSSContextImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, Oid paramOid, GSSCredential paramGSSCredential, int paramInt)
    throws GSSException
  {
    if ((paramGSSName == null) || (!(paramGSSName instanceof GSSNameImpl))) {
      throw new GSSException(3);
    }
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    gssManager = paramGSSManagerImpl;
    myCred = ((GSSCredentialImpl)paramGSSCredential);
    reqLifetime = paramInt;
    targName = ((GSSNameImpl)paramGSSName);
    mechOid = paramOid;
    initiator = true;
  }
  
  public GSSContextImpl(GSSManagerImpl paramGSSManagerImpl, GSSCredential paramGSSCredential)
    throws GSSException
  {
    gssManager = paramGSSManagerImpl;
    myCred = ((GSSCredentialImpl)paramGSSCredential);
    initiator = false;
  }
  
  public GSSContextImpl(GSSManagerImpl paramGSSManagerImpl, byte[] paramArrayOfByte)
    throws GSSException
  {
    gssManager = paramGSSManagerImpl;
    mechCtxt = paramGSSManagerImpl.getMechanismContext(paramArrayOfByte);
    initiator = mechCtxt.isInitiator();
    mechOid = mechCtxt.getMech();
  }
  
  public byte[] initSecContext(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws GSSException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(600);
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2);
    int i = initSecContext(localByteArrayInputStream, localByteArrayOutputStream);
    return i == 0 ? null : localByteArrayOutputStream.toByteArray();
  }
  
  public int initSecContext(InputStream paramInputStream, OutputStream paramOutputStream)
    throws GSSException
  {
    if ((mechCtxt != null) && (currentState != 2)) {
      throw new GSSExceptionImpl(11, "Illegal call to initSecContext");
    }
    GSSHeader localGSSHeader = null;
    int i = -1;
    GSSCredentialSpi localGSSCredentialSpi = null;
    int j = 0;
    try
    {
      if (mechCtxt == null)
      {
        if (myCred != null) {
          try
          {
            localGSSCredentialSpi = myCred.getElement(mechOid, true);
          }
          catch (GSSException localGSSException)
          {
            if ((GSSUtil.isSpNegoMech(mechOid)) && (localGSSException.getMajor() == 13)) {
              localGSSCredentialSpi = myCred.getElement(myCred.getMechs()[0], true);
            } else {
              throw localGSSException;
            }
          }
        }
        localObject = targName.getElement(mechOid);
        mechCtxt = gssManager.getMechanismContext((GSSNameSpi)localObject, localGSSCredentialSpi, reqLifetime, mechOid);
        mechCtxt.requestConf(reqConfState);
        mechCtxt.requestInteg(reqIntegState);
        mechCtxt.requestCredDeleg(reqCredDelegState);
        mechCtxt.requestMutualAuth(reqMutualAuthState);
        mechCtxt.requestReplayDet(reqReplayDetState);
        mechCtxt.requestSequenceDet(reqSequenceDetState);
        mechCtxt.requestAnonymity(reqAnonState);
        mechCtxt.setChannelBinding(channelBindings);
        mechCtxt.requestDelegPolicy(reqDelegPolicyState);
        objId = new ObjectIdentifier(mechOid.toString());
        currentState = 2;
        j = 1;
      }
      else if ((!mechCtxt.getProvider().getName().equals("SunNativeGSS")) && (!GSSUtil.isSpNegoMech(mechOid)))
      {
        localGSSHeader = new GSSHeader(paramInputStream);
        if (!localGSSHeader.getOid().equals(objId)) {
          throw new GSSExceptionImpl(10, "Mechanism not equal to " + mechOid.toString() + " in initSecContext token");
        }
        i = localGSSHeader.getMechTokenLength();
      }
      Object localObject = mechCtxt.initSecContext(paramInputStream, i);
      int k = 0;
      if (localObject != null)
      {
        k = localObject.length;
        if ((!mechCtxt.getProvider().getName().equals("SunNativeGSS")) && ((j != 0) || (!GSSUtil.isSpNegoMech(mechOid))))
        {
          localGSSHeader = new GSSHeader(objId, localObject.length);
          k += localGSSHeader.encode(paramOutputStream);
        }
        paramOutputStream.write((byte[])localObject);
      }
      if (mechCtxt.isEstablished()) {
        currentState = 3;
      }
      return k;
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(10, localIOException.getMessage());
    }
  }
  
  public byte[] acceptSecContext(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws GSSException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(100);
    acceptSecContext(new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), localByteArrayOutputStream);
    byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
    return arrayOfByte.length == 0 ? null : arrayOfByte;
  }
  
  public void acceptSecContext(InputStream paramInputStream, OutputStream paramOutputStream)
    throws GSSException
  {
    if ((mechCtxt != null) && (currentState != 2)) {
      throw new GSSExceptionImpl(11, "Illegal call to acceptSecContext");
    }
    GSSHeader localGSSHeader = null;
    int i = -1;
    GSSCredentialSpi localGSSCredentialSpi = null;
    try
    {
      if (mechCtxt == null)
      {
        localGSSHeader = new GSSHeader(paramInputStream);
        i = localGSSHeader.getMechTokenLength();
        objId = localGSSHeader.getOid();
        mechOid = new Oid(objId.toString());
        if (myCred != null) {
          localGSSCredentialSpi = myCred.getElement(mechOid, false);
        }
        mechCtxt = gssManager.getMechanismContext(localGSSCredentialSpi, mechOid);
        mechCtxt.setChannelBinding(channelBindings);
        currentState = 2;
      }
      else if ((!mechCtxt.getProvider().getName().equals("SunNativeGSS")) && (!GSSUtil.isSpNegoMech(mechOid)))
      {
        localGSSHeader = new GSSHeader(paramInputStream);
        if (!localGSSHeader.getOid().equals(objId)) {
          throw new GSSExceptionImpl(10, "Mechanism not equal to " + mechOid.toString() + " in acceptSecContext token");
        }
        i = localGSSHeader.getMechTokenLength();
      }
      byte[] arrayOfByte = mechCtxt.acceptSecContext(paramInputStream, i);
      if (arrayOfByte != null)
      {
        int j = arrayOfByte.length;
        if ((!mechCtxt.getProvider().getName().equals("SunNativeGSS")) && (!GSSUtil.isSpNegoMech(mechOid)))
        {
          localGSSHeader = new GSSHeader(objId, arrayOfByte.length);
          j += localGSSHeader.encode(paramOutputStream);
        }
        paramOutputStream.write(arrayOfByte);
      }
      if (mechCtxt.isEstablished()) {
        currentState = 3;
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(10, localIOException.getMessage());
    }
  }
  
  public boolean isEstablished()
  {
    if (mechCtxt == null) {
      return false;
    }
    return currentState == 3;
  }
  
  public int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2)
    throws GSSException
  {
    if (mechCtxt != null) {
      return mechCtxt.getWrapSizeLimit(paramInt1, paramBoolean, paramInt2);
    }
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      return mechCtxt.wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    }
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      mechCtxt.wrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      return mechCtxt.unwrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    }
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      mechCtxt.unwrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
  }
  
  public byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      return mechCtxt.getMIC(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    }
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      mechCtxt.getMIC(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
  }
  
  public void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      mechCtxt.verifyMIC(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
  }
  
  public void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp)
    throws GSSException
  {
    if (mechCtxt != null) {
      mechCtxt.verifyMIC(paramInputStream1, paramInputStream2, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
  }
  
  public byte[] export()
    throws GSSException
  {
    byte[] arrayOfByte = null;
    if ((mechCtxt.isTransferable()) && (mechCtxt.getProvider().getName().equals("SunNativeGSS"))) {
      arrayOfByte = mechCtxt.export();
    }
    return arrayOfByte;
  }
  
  public void requestMutualAuth(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqMutualAuthState = paramBoolean;
    }
  }
  
  public void requestReplayDet(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqReplayDetState = paramBoolean;
    }
  }
  
  public void requestSequenceDet(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqSequenceDetState = paramBoolean;
    }
  }
  
  public void requestCredDeleg(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqCredDelegState = paramBoolean;
    }
  }
  
  public void requestAnonymity(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqAnonState = paramBoolean;
    }
  }
  
  public void requestConf(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqConfState = paramBoolean;
    }
  }
  
  public void requestInteg(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqIntegState = paramBoolean;
    }
  }
  
  public void requestLifetime(int paramInt)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqLifetime = paramInt;
    }
  }
  
  public void setChannelBinding(ChannelBinding paramChannelBinding)
    throws GSSException
  {
    if (mechCtxt == null) {
      channelBindings = paramChannelBinding;
    }
  }
  
  public boolean getCredDelegState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getCredDelegState();
    }
    return reqCredDelegState;
  }
  
  public boolean getMutualAuthState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getMutualAuthState();
    }
    return reqMutualAuthState;
  }
  
  public boolean getReplayDetState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getReplayDetState();
    }
    return reqReplayDetState;
  }
  
  public boolean getSequenceDetState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getSequenceDetState();
    }
    return reqSequenceDetState;
  }
  
  public boolean getAnonymityState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getAnonymityState();
    }
    return reqAnonState;
  }
  
  public boolean isTransferable()
    throws GSSException
  {
    if (mechCtxt != null) {
      return mechCtxt.isTransferable();
    }
    return false;
  }
  
  public boolean isProtReady()
  {
    if (mechCtxt != null) {
      return mechCtxt.isProtReady();
    }
    return false;
  }
  
  public boolean getConfState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getConfState();
    }
    return reqConfState;
  }
  
  public boolean getIntegState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getIntegState();
    }
    return reqIntegState;
  }
  
  public int getLifetime()
  {
    if (mechCtxt != null) {
      return mechCtxt.getLifetime();
    }
    return reqLifetime;
  }
  
  public GSSName getSrcName()
    throws GSSException
  {
    if (srcName == null) {
      srcName = GSSNameImpl.wrapElement(gssManager, mechCtxt.getSrcName());
    }
    return srcName;
  }
  
  public GSSName getTargName()
    throws GSSException
  {
    if (targName == null) {
      targName = GSSNameImpl.wrapElement(gssManager, mechCtxt.getTargName());
    }
    return targName;
  }
  
  public Oid getMech()
    throws GSSException
  {
    if (mechCtxt != null) {
      return mechCtxt.getMech();
    }
    return mechOid;
  }
  
  public GSSCredential getDelegCred()
    throws GSSException
  {
    if (mechCtxt == null) {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    GSSCredentialSpi localGSSCredentialSpi = mechCtxt.getDelegCred();
    return localGSSCredentialSpi == null ? null : new GSSCredentialImpl(gssManager, localGSSCredentialSpi);
  }
  
  public boolean isInitiator()
    throws GSSException
  {
    return initiator;
  }
  
  public void dispose()
    throws GSSException
  {
    currentState = 4;
    if (mechCtxt != null)
    {
      mechCtxt.dispose();
      mechCtxt = null;
    }
    myCred = null;
    srcName = null;
    targName = null;
  }
  
  public Object inquireSecContext(InquireType paramInquireType)
    throws GSSException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new InquireSecContextPermission(paramInquireType.toString()));
    }
    if (mechCtxt == null) {
      throw new GSSException(12);
    }
    return mechCtxt.inquireSecContext(paramInquireType);
  }
  
  public void requestDelegPolicy(boolean paramBoolean)
    throws GSSException
  {
    if ((mechCtxt == null) && (initiator)) {
      reqDelegPolicyState = paramBoolean;
    }
  }
  
  public boolean getDelegPolicyState()
  {
    if (mechCtxt != null) {
      return mechCtxt.getDelegPolicyState();
    }
    return reqDelegPolicyState;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\GSSContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */