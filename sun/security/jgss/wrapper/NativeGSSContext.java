package sun.security.jgss.wrapper;

import com.sun.security.jgss.InquireType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import javax.security.auth.kerberos.DelegationPermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSHeader;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.NegTokenInit;
import sun.security.jgss.spnego.NegTokenTarg;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

class NativeGSSContext
  implements GSSContextSpi
{
  private static final int GSS_C_DELEG_FLAG = 1;
  private static final int GSS_C_MUTUAL_FLAG = 2;
  private static final int GSS_C_REPLAY_FLAG = 4;
  private static final int GSS_C_SEQUENCE_FLAG = 8;
  private static final int GSS_C_CONF_FLAG = 16;
  private static final int GSS_C_INTEG_FLAG = 32;
  private static final int GSS_C_ANON_FLAG = 64;
  private static final int GSS_C_PROT_READY_FLAG = 128;
  private static final int GSS_C_TRANS_FLAG = 256;
  private static final int NUM_OF_INQUIRE_VALUES = 6;
  private long pContext = 0L;
  private GSSNameElement srcName;
  private GSSNameElement targetName;
  private GSSCredElement cred;
  private boolean isInitiator;
  private boolean isEstablished;
  private Oid actualMech;
  private ChannelBinding cb;
  private GSSCredElement delegatedCred;
  private int flags;
  private int lifetime = 0;
  private final GSSLibStub cStub;
  private boolean skipDelegPermCheck;
  private boolean skipServicePermCheck;
  
  private static Oid getMechFromSpNegoToken(byte[] paramArrayOfByte, boolean paramBoolean)
    throws GSSException
  {
    Oid localOid = null;
    Object localObject;
    if (paramBoolean)
    {
      localObject = null;
      try
      {
        localObject = new GSSHeader(new ByteArrayInputStream(paramArrayOfByte));
      }
      catch (IOException localIOException)
      {
        throw new GSSExceptionImpl(11, localIOException);
      }
      int i = ((GSSHeader)localObject).getMechTokenLength();
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(paramArrayOfByte, paramArrayOfByte.length - i, arrayOfByte, 0, arrayOfByte.length);
      NegTokenInit localNegTokenInit = new NegTokenInit(arrayOfByte);
      if (localNegTokenInit.getMechToken() != null)
      {
        Oid[] arrayOfOid = localNegTokenInit.getMechTypeList();
        localOid = arrayOfOid[0];
      }
    }
    else
    {
      localObject = new NegTokenTarg(paramArrayOfByte);
      localOid = ((NegTokenTarg)localObject).getSupportedMech();
    }
    return localOid;
  }
  
  private void doServicePermCheck()
    throws GSSException
  {
    if (System.getSecurityManager() != null)
    {
      String str = isInitiator ? "initiate" : "accept";
      if ((GSSUtil.isSpNegoMech(cStub.getMech())) && (isInitiator) && (!isEstablished)) {
        if (srcName == null)
        {
          localObject = new GSSCredElement(null, lifetime, 1, GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID));
          ((GSSCredElement)localObject).dispose();
        }
        else
        {
          localObject = Krb5Util.getTGSName(srcName);
          Krb5Util.checkServicePermission((String)localObject, str);
        }
      }
      Object localObject = targetName.getKrbName();
      Krb5Util.checkServicePermission((String)localObject, str);
      skipServicePermCheck = true;
    }
  }
  
  private void doDelegPermCheck()
    throws GSSException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      String str1 = targetName.getKrbName();
      String str2 = Krb5Util.getTGSName(targetName);
      StringBuffer localStringBuffer = new StringBuffer("\"");
      localStringBuffer.append(str1).append("\" \"");
      localStringBuffer.append(str2).append('"');
      String str3 = localStringBuffer.toString();
      SunNativeProvider.debug("Checking DelegationPermission (" + str3 + ")");
      DelegationPermission localDelegationPermission = new DelegationPermission(str3);
      localSecurityManager.checkPermission(localDelegationPermission);
      skipDelegPermCheck = true;
    }
  }
  
  private byte[] retrieveToken(InputStream paramInputStream, int paramInt)
    throws GSSException
  {
    try
    {
      byte[] arrayOfByte1 = null;
      Object localObject;
      if (paramInt != -1)
      {
        SunNativeProvider.debug("Precomputed mechToken length: " + paramInt);
        localObject = new GSSHeader(new ObjectIdentifier(cStub.getMech().toString()), paramInt);
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(600);
        byte[] arrayOfByte2 = new byte[paramInt];
        int i = paramInputStream.read(arrayOfByte2);
        assert (paramInt == i);
        ((GSSHeader)localObject).encode(localByteArrayOutputStream);
        localByteArrayOutputStream.write(arrayOfByte2);
        arrayOfByte1 = localByteArrayOutputStream.toByteArray();
      }
      else
      {
        assert (paramInt == -1);
        localObject = new DerValue(paramInputStream);
        arrayOfByte1 = ((DerValue)localObject).toByteArray();
      }
      SunNativeProvider.debug("Complete Token length: " + arrayOfByte1.length);
      return arrayOfByte1;
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
  }
  
  NativeGSSContext(GSSNameElement paramGSSNameElement, GSSCredElement paramGSSCredElement, int paramInt, GSSLibStub paramGSSLibStub)
    throws GSSException
  {
    if (paramGSSNameElement == null) {
      throw new GSSException(11, 1, "null peer");
    }
    cStub = paramGSSLibStub;
    cred = paramGSSCredElement;
    targetName = paramGSSNameElement;
    isInitiator = true;
    lifetime = paramInt;
    if (GSSUtil.isKerberosMech(cStub.getMech()))
    {
      doServicePermCheck();
      if (cred == null) {
        cred = new GSSCredElement(null, lifetime, 1, cStub);
      }
      srcName = cred.getName();
    }
  }
  
  NativeGSSContext(GSSCredElement paramGSSCredElement, GSSLibStub paramGSSLibStub)
    throws GSSException
  {
    cStub = paramGSSLibStub;
    cred = paramGSSCredElement;
    if (cred != null) {
      targetName = cred.getName();
    }
    isInitiator = false;
    if ((GSSUtil.isKerberosMech(cStub.getMech())) && (targetName != null)) {
      doServicePermCheck();
    }
  }
  
  NativeGSSContext(long paramLong, GSSLibStub paramGSSLibStub)
    throws GSSException
  {
    assert (pContext != 0L);
    pContext = paramLong;
    cStub = paramGSSLibStub;
    long[] arrayOfLong = cStub.inquireContext(pContext);
    if (arrayOfLong.length != 6) {
      throw new RuntimeException("Bug w/ GSSLibStub.inquireContext()");
    }
    srcName = new GSSNameElement(arrayOfLong[0], cStub);
    targetName = new GSSNameElement(arrayOfLong[1], cStub);
    isInitiator = (arrayOfLong[2] != 0L);
    isEstablished = (arrayOfLong[3] != 0L);
    flags = ((int)arrayOfLong[4]);
    lifetime = ((int)arrayOfLong[5]);
    Oid localOid = cStub.getMech();
    if ((GSSUtil.isSpNegoMech(localOid)) || (GSSUtil.isKerberosMech(localOid))) {
      doServicePermCheck();
    }
  }
  
  public Provider getProvider()
  {
    return SunNativeProvider.INSTANCE;
  }
  
  public byte[] initSecContext(InputStream paramInputStream, int paramInt)
    throws GSSException
  {
    byte[] arrayOfByte1 = null;
    if ((!isEstablished) && (isInitiator))
    {
      byte[] arrayOfByte2 = null;
      if (pContext != 0L)
      {
        arrayOfByte2 = retrieveToken(paramInputStream, paramInt);
        SunNativeProvider.debug("initSecContext=> inToken len=" + arrayOfByte2.length);
      }
      if (!getCredDelegState()) {
        skipDelegPermCheck = true;
      }
      if ((GSSUtil.isKerberosMech(cStub.getMech())) && (!skipDelegPermCheck)) {
        doDelegPermCheck();
      }
      long l = cred == null ? 0L : cred.pCred;
      arrayOfByte1 = cStub.initContext(l, targetName.pName, cb, arrayOfByte2, this);
      SunNativeProvider.debug("initSecContext=> outToken len=" + (arrayOfByte1 == null ? 0 : arrayOfByte1.length));
      if ((GSSUtil.isSpNegoMech(cStub.getMech())) && (arrayOfByte1 != null))
      {
        actualMech = getMechFromSpNegoToken(arrayOfByte1, true);
        if (GSSUtil.isKerberosMech(actualMech))
        {
          if (!skipServicePermCheck) {
            doServicePermCheck();
          }
          if (!skipDelegPermCheck) {
            doDelegPermCheck();
          }
        }
      }
      if (isEstablished)
      {
        if (srcName == null) {
          srcName = new GSSNameElement(cStub.getContextName(pContext, true), cStub);
        }
        if (cred == null) {
          cred = new GSSCredElement(srcName, lifetime, 1, cStub);
        }
      }
    }
    return arrayOfByte1;
  }
  
  public byte[] acceptSecContext(InputStream paramInputStream, int paramInt)
    throws GSSException
  {
    byte[] arrayOfByte1 = null;
    if ((!isEstablished) && (!isInitiator))
    {
      byte[] arrayOfByte2 = retrieveToken(paramInputStream, paramInt);
      SunNativeProvider.debug("acceptSecContext=> inToken len=" + arrayOfByte2.length);
      long l = cred == null ? 0L : cred.pCred;
      arrayOfByte1 = cStub.acceptContext(l, cb, arrayOfByte2, this);
      SunNativeProvider.debug("acceptSecContext=> outToken len=" + (arrayOfByte1 == null ? 0 : arrayOfByte1.length));
      if (targetName == null)
      {
        targetName = new GSSNameElement(cStub.getContextName(pContext, false), cStub);
        if (cred != null) {
          cred.dispose();
        }
        cred = new GSSCredElement(targetName, lifetime, 2, cStub);
      }
      if ((GSSUtil.isSpNegoMech(cStub.getMech())) && (arrayOfByte1 != null) && (!skipServicePermCheck) && (GSSUtil.isKerberosMech(getMechFromSpNegoToken(arrayOfByte1, false)))) {
        doServicePermCheck();
      }
    }
    return arrayOfByte1;
  }
  
  public boolean isEstablished()
  {
    return isEstablished;
  }
  
  public void dispose()
    throws GSSException
  {
    srcName = null;
    targetName = null;
    cred = null;
    delegatedCred = null;
    if (pContext != 0L)
    {
      pContext = cStub.deleteContext(pContext);
      pContext = 0L;
    }
  }
  
  public int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2)
    throws GSSException
  {
    return cStub.wrapSizeLimit(pContext, paramBoolean ? 1 : 0, paramInt1, paramInt2);
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte = paramArrayOfByte;
    if ((paramInt1 != 0) || (paramInt2 != paramArrayOfByte.length))
    {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    }
    return cStub.wrap(pContext, arrayOfByte, paramMessageProp);
  }
  
  public void wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    try
    {
      byte[] arrayOfByte = wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
      paramOutputStream.write(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
  }
  
  public int wrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte = wrap(paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte2, paramInt3, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  public void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    try
    {
      byte[] arrayOfByte1 = new byte[paramInputStream.available()];
      int i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = wrap(arrayOfByte1, 0, i, paramMessageProp);
      paramOutputStream.write(arrayOfByte2);
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    if ((paramInt1 != 0) || (paramInt2 != paramArrayOfByte.length))
    {
      byte[] arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
      return cStub.unwrap(pContext, arrayOfByte, paramMessageProp);
    }
    return cStub.unwrap(pContext, paramArrayOfByte, paramMessageProp);
  }
  
  public int unwrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte1 = null;
    if ((paramInt1 != 0) || (paramInt2 != paramArrayOfByte1.length))
    {
      byte[] arrayOfByte2 = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte1, paramInt1, arrayOfByte2, 0, paramInt2);
      arrayOfByte1 = cStub.unwrap(pContext, arrayOfByte2, paramMessageProp);
    }
    else
    {
      arrayOfByte1 = cStub.unwrap(pContext, paramArrayOfByte1, paramMessageProp);
    }
    System.arraycopy(arrayOfByte1, 0, paramArrayOfByte2, paramInt3, arrayOfByte1.length);
    return arrayOfByte1.length;
  }
  
  public void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    try
    {
      byte[] arrayOfByte1 = new byte[paramInputStream.available()];
      int i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = unwrap(arrayOfByte1, 0, i, paramMessageProp);
      paramOutputStream.write(arrayOfByte2);
      paramOutputStream.flush();
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
  }
  
  public int unwrap(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte1 = null;
    int i = 0;
    try
    {
      arrayOfByte1 = new byte[paramInputStream.available()];
      i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = unwrap(arrayOfByte1, 0, i, paramMessageProp);
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
    byte[] arrayOfByte3 = unwrap(arrayOfByte1, 0, i, paramMessageProp);
    System.arraycopy(arrayOfByte3, 0, paramArrayOfByte, paramInt, arrayOfByte3.length);
    return arrayOfByte3.length;
  }
  
  public byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    int i = paramMessageProp == null ? 0 : paramMessageProp.getQOP();
    byte[] arrayOfByte = paramArrayOfByte;
    if ((paramInt1 != 0) || (paramInt2 != paramArrayOfByte.length))
    {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    }
    return cStub.getMic(pContext, i, arrayOfByte);
  }
  
  public void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    try
    {
      int i = 0;
      byte[] arrayOfByte1 = new byte[paramInputStream.available()];
      i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = getMIC(arrayOfByte1, 0, i, paramMessageProp);
      if ((arrayOfByte2 != null) && (arrayOfByte2.length != 0)) {
        paramOutputStream.write(arrayOfByte2);
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
  }
  
  public void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp)
    throws GSSException
  {
    byte[] arrayOfByte1 = paramArrayOfByte1;
    byte[] arrayOfByte2 = paramArrayOfByte2;
    if ((paramInt1 != 0) || (paramInt2 != paramArrayOfByte1.length))
    {
      arrayOfByte1 = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte1, paramInt1, arrayOfByte1, 0, paramInt2);
    }
    if ((paramInt3 != 0) || (paramInt4 != paramArrayOfByte2.length))
    {
      arrayOfByte2 = new byte[paramInt4];
      System.arraycopy(paramArrayOfByte2, paramInt3, arrayOfByte2, 0, paramInt4);
    }
    cStub.verifyMic(pContext, arrayOfByte1, arrayOfByte2, paramMessageProp);
  }
  
  public void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp)
    throws GSSException
  {
    try
    {
      byte[] arrayOfByte1 = new byte[paramInputStream2.available()];
      int i = paramInputStream2.read(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[paramInputStream1.available()];
      int j = paramInputStream1.read(arrayOfByte2);
      verifyMIC(arrayOfByte2, 0, j, arrayOfByte1, 0, i, paramMessageProp);
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(11, localIOException);
    }
  }
  
  public byte[] export()
    throws GSSException
  {
    byte[] arrayOfByte = cStub.exportContext(pContext);
    pContext = 0L;
    return arrayOfByte;
  }
  
  private void changeFlags(int paramInt, boolean paramBoolean)
  {
    if ((isInitiator) && (pContext == 0L)) {
      if (paramBoolean) {
        flags |= paramInt;
      } else {
        flags &= (paramInt ^ 0xFFFFFFFF);
      }
    }
  }
  
  public void requestMutualAuth(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(2, paramBoolean);
  }
  
  public void requestReplayDet(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(4, paramBoolean);
  }
  
  public void requestSequenceDet(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(8, paramBoolean);
  }
  
  public void requestCredDeleg(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(1, paramBoolean);
  }
  
  public void requestAnonymity(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(64, paramBoolean);
  }
  
  public void requestConf(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(16, paramBoolean);
  }
  
  public void requestInteg(boolean paramBoolean)
    throws GSSException
  {
    changeFlags(32, paramBoolean);
  }
  
  public void requestDelegPolicy(boolean paramBoolean)
    throws GSSException
  {}
  
  public void requestLifetime(int paramInt)
    throws GSSException
  {
    if ((isInitiator) && (pContext == 0L)) {
      lifetime = paramInt;
    }
  }
  
  public void setChannelBinding(ChannelBinding paramChannelBinding)
    throws GSSException
  {
    if (pContext == 0L) {
      cb = paramChannelBinding;
    }
  }
  
  private boolean checkFlags(int paramInt)
  {
    return (flags & paramInt) != 0;
  }
  
  public boolean getCredDelegState()
  {
    return checkFlags(1);
  }
  
  public boolean getMutualAuthState()
  {
    return checkFlags(2);
  }
  
  public boolean getReplayDetState()
  {
    return checkFlags(4);
  }
  
  public boolean getSequenceDetState()
  {
    return checkFlags(8);
  }
  
  public boolean getAnonymityState()
  {
    return checkFlags(64);
  }
  
  public boolean isTransferable()
    throws GSSException
  {
    return checkFlags(256);
  }
  
  public boolean isProtReady()
  {
    return checkFlags(128);
  }
  
  public boolean getConfState()
  {
    return checkFlags(16);
  }
  
  public boolean getIntegState()
  {
    return checkFlags(32);
  }
  
  public boolean getDelegPolicyState()
  {
    return false;
  }
  
  public int getLifetime()
  {
    return cStub.getContextTime(pContext);
  }
  
  public GSSNameSpi getSrcName()
    throws GSSException
  {
    return srcName;
  }
  
  public GSSNameSpi getTargName()
    throws GSSException
  {
    return targetName;
  }
  
  public Oid getMech()
    throws GSSException
  {
    if ((isEstablished) && (actualMech != null)) {
      return actualMech;
    }
    return cStub.getMech();
  }
  
  public GSSCredentialSpi getDelegCred()
    throws GSSException
  {
    return delegatedCred;
  }
  
  public boolean isInitiator()
  {
    return isInitiator;
  }
  
  protected void finalize()
    throws Throwable
  {
    dispose();
  }
  
  public Object inquireSecContext(InquireType paramInquireType)
    throws GSSException
  {
    throw new GSSException(16, -1, "Inquire type not supported.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\wrapper\NativeGSSContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */