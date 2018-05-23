package com.sun.security.sasl.gsskerb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;

final class GssKrb5Server
  extends GssKrb5Base
  implements SaslServer
{
  private static final String MY_CLASS_NAME = GssKrb5Server.class.getName();
  private int handshakeStage = 0;
  private String peer;
  private String me;
  private String authzid;
  private CallbackHandler cbh;
  private final String protocolSaved;
  
  GssKrb5Server(String paramString1, String paramString2, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    super(paramMap, MY_CLASS_NAME);
    cbh = paramCallbackHandler;
    String str;
    if (paramString2 == null)
    {
      protocolSaved = paramString1;
      str = null;
    }
    else
    {
      protocolSaved = null;
      str = paramString1 + "@" + paramString2;
    }
    logger.log(Level.FINE, "KRB5SRV01:Using service name: {0}", str);
    try
    {
      GSSManager localGSSManager = GSSManager.getInstance();
      GSSName localGSSName = str == null ? null : localGSSManager.createName(str, GSSName.NT_HOSTBASED_SERVICE, KRB5_OID);
      GSSCredential localGSSCredential = localGSSManager.createCredential(localGSSName, Integer.MAX_VALUE, KRB5_OID, 2);
      secCtx = localGSSManager.createContext(localGSSCredential);
      if ((allQop & 0x2) != 0) {
        secCtx.requestInteg(true);
      }
      if ((allQop & 0x4) != 0) {
        secCtx.requestConf(true);
      }
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Failure to initialize security context", localGSSException);
    }
    logger.log(Level.FINE, "KRB5SRV02:Initialization complete");
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (completed) {
      throw new SaslException("SASL authentication already complete");
    }
    if (logger.isLoggable(Level.FINER)) {
      traceOutput(MY_CLASS_NAME, "evaluateResponse", "KRB5SRV03:Response [raw]:", paramArrayOfByte);
    }
    switch (handshakeStage)
    {
    case 1: 
      return doHandshake1(paramArrayOfByte);
    case 2: 
      return doHandshake2(paramArrayOfByte);
    }
    try
    {
      byte[] arrayOfByte = secCtx.acceptSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "evaluateResponse", "KRB5SRV04:Challenge: [after acceptSecCtx]", arrayOfByte);
      }
      if (secCtx.isEstablished())
      {
        handshakeStage = 1;
        peer = secCtx.getSrcName().toString();
        me = secCtx.getTargName().toString();
        logger.log(Level.FINE, "KRB5SRV05:Peer name is : {0}, my name is : {1}", new Object[] { peer, me });
        if ((protocolSaved != null) && (!protocolSaved.equalsIgnoreCase(me.split("[/@]")[0]))) {
          throw new SaslException("GSS context targ name protocol error: " + me);
        }
        if (arrayOfByte == null) {
          return doHandshake1(EMPTY);
        }
      }
      return arrayOfByte;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("GSS initiate failed", localGSSException);
    }
  }
  
  private byte[] doHandshake1(byte[] paramArrayOfByte)
    throws SaslException
  {
    try
    {
      if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0)) {
        throw new SaslException("Handshake expecting no response data from server");
      }
      byte[] arrayOfByte1 = new byte[4];
      arrayOfByte1[0] = allQop;
      intToNetworkByteOrder(recvMaxBufSize, arrayOfByte1, 1, 3);
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "KRB5SRV06:Supported protections: {0}; recv max buf size: {1}", new Object[] { new Byte(allQop), new Integer(recvMaxBufSize) });
      }
      handshakeStage = 2;
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "doHandshake1", "KRB5SRV07:Challenge [raw]", arrayOfByte1);
      }
      byte[] arrayOfByte2 = secCtx.wrap(arrayOfByte1, 0, arrayOfByte1.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "doHandshake1", "KRB5SRV08:Challenge [after wrap]", arrayOfByte2);
      }
      return arrayOfByte2;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Problem wrapping handshake1", localGSSException);
    }
  }
  
  private byte[] doHandshake2(byte[] paramArrayOfByte)
    throws SaslException
  {
    try
    {
      byte[] arrayOfByte = secCtx.unwrap(paramArrayOfByte, 0, paramArrayOfByte.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "doHandshake2", "KRB5SRV09:Response [after unwrap]", arrayOfByte);
      }
      byte b = arrayOfByte[0];
      if ((b & allQop) == 0) {
        throw new SaslException("Client selected unsupported protection: " + b);
      }
      if ((b & 0x4) != 0)
      {
        privacy = true;
        integrity = true;
      }
      else if ((b & 0x2) != 0)
      {
        integrity = true;
      }
      int i = networkByteOrderToInt(arrayOfByte, 1, 3);
      sendMaxBufSize = (sendMaxBufSize == 0 ? i : Math.min(sendMaxBufSize, i));
      rawSendSize = secCtx.getWrapSizeLimit(0, privacy, sendMaxBufSize);
      if (logger.isLoggable(Level.FINE))
      {
        logger.log(Level.FINE, "KRB5SRV10:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[] { new Byte(b), Boolean.valueOf(privacy), Boolean.valueOf(integrity) });
        logger.log(Level.FINE, "KRB5SRV11:Client max recv size: {0}; server max send size: {1}; rawSendSize: {2}", new Object[] { new Integer(i), new Integer(sendMaxBufSize), new Integer(rawSendSize) });
      }
      if (arrayOfByte.length > 4) {
        try
        {
          authzid = new String(arrayOfByte, 4, arrayOfByte.length - 4, "UTF-8");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new SaslException("Cannot decode authzid", localUnsupportedEncodingException);
        }
      } else {
        authzid = peer;
      }
      logger.log(Level.FINE, "KRB5SRV12:Authzid: {0}", authzid);
      AuthorizeCallback localAuthorizeCallback = new AuthorizeCallback(peer, authzid);
      cbh.handle(new Callback[] { localAuthorizeCallback });
      if (localAuthorizeCallback.isAuthorized())
      {
        authzid = localAuthorizeCallback.getAuthorizedID();
        completed = true;
      }
      else
      {
        throw new SaslException(peer + " is not authorized to connect as " + authzid);
      }
      return null;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Final handshake step failed", localGSSException);
    }
    catch (IOException localIOException)
    {
      throw new SaslException("Problem with callback handler", localIOException);
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new SaslException("Problem with callback handler", localUnsupportedCallbackException);
    }
  }
  
  public String getAuthorizationID()
  {
    if (completed) {
      return authzid;
    }
    throw new IllegalStateException("Authentication incomplete");
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (!completed) {
      throw new IllegalStateException("Authentication incomplete");
    }
    Object localObject;
    switch (paramString)
    {
    case "javax.security.sasl.bound.server.name": 
      try
      {
        localObject = me.split("[/@]")[1];
      }
      catch (Exception localException)
      {
        localObject = null;
      }
    default: 
      localObject = super.getNegotiatedProperty(paramString);
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\gsskerb\GssKrb5Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */