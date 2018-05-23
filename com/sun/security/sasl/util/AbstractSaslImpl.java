package com.sun.security.sasl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;
import sun.misc.HexDumpEncoder;

public abstract class AbstractSaslImpl
{
  protected boolean completed = false;
  protected boolean privacy = false;
  protected boolean integrity = false;
  protected byte[] qop;
  protected byte allQop;
  protected byte[] strength;
  protected int sendMaxBufSize = 0;
  protected int recvMaxBufSize = 65536;
  protected int rawSendSize;
  protected String myClassName;
  private static final String SASL_LOGGER_NAME = "javax.security.sasl";
  protected static final String MAX_SEND_BUF = "javax.security.sasl.sendmaxbuffer";
  protected static final Logger logger = Logger.getLogger("javax.security.sasl");
  protected static final byte NO_PROTECTION = 1;
  protected static final byte INTEGRITY_ONLY_PROTECTION = 2;
  protected static final byte PRIVACY_PROTECTION = 4;
  protected static final byte LOW_STRENGTH = 1;
  protected static final byte MEDIUM_STRENGTH = 2;
  protected static final byte HIGH_STRENGTH = 4;
  private static final byte[] DEFAULT_QOP = { 1 };
  private static final String[] QOP_TOKENS = { "auth-conf", "auth-int", "auth" };
  private static final byte[] QOP_MASKS = { 4, 2, 1 };
  private static final byte[] DEFAULT_STRENGTH = { 4, 2, 1 };
  private static final String[] STRENGTH_TOKENS = { "low", "medium", "high" };
  private static final byte[] STRENGTH_MASKS = { 1, 2, 4 };
  
  protected AbstractSaslImpl(Map<String, ?> paramMap, String paramString)
    throws SaslException
  {
    myClassName = paramString;
    if (paramMap != null)
    {
      qop = parseQop(str = (String)paramMap.get("javax.security.sasl.qop"));
      logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL01:Preferred qop property: {0}", str);
      allQop = combineMasks(qop);
      StringBuffer localStringBuffer;
      int i;
      if (logger.isLoggable(Level.FINE))
      {
        logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL02:Preferred qop mask: {0}", new Byte(allQop));
        if (qop.length > 0)
        {
          localStringBuffer = new StringBuffer();
          for (i = 0; i < qop.length; i++)
          {
            localStringBuffer.append(Byte.toString(qop[i]));
            localStringBuffer.append(' ');
          }
          logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL03:Preferred qops : {0}", localStringBuffer.toString());
        }
      }
      strength = parseStrength(str = (String)paramMap.get("javax.security.sasl.strength"));
      logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL04:Preferred strength property: {0}", str);
      if ((logger.isLoggable(Level.FINE)) && (strength.length > 0))
      {
        localStringBuffer = new StringBuffer();
        for (i = 0; i < strength.length; i++)
        {
          localStringBuffer.append(Byte.toString(strength[i]));
          localStringBuffer.append(' ');
        }
        logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL05:Cipher strengths: {0}", localStringBuffer.toString());
      }
      String str = (String)paramMap.get("javax.security.sasl.maxbuffer");
      if (str != null) {
        try
        {
          logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL06:Max receive buffer size: {0}", str);
          recvMaxBufSize = Integer.parseInt(str);
        }
        catch (NumberFormatException localNumberFormatException1)
        {
          throw new SaslException("Property must be string representation of integer: javax.security.sasl.maxbuffer");
        }
      }
      str = (String)paramMap.get("javax.security.sasl.sendmaxbuffer");
      if (str != null) {
        try
        {
          logger.logp(Level.FINE, myClassName, "constructor", "SASLIMPL07:Max send buffer size: {0}", str);
          sendMaxBufSize = Integer.parseInt(str);
        }
        catch (NumberFormatException localNumberFormatException2)
        {
          throw new SaslException("Property must be string representation of integer: javax.security.sasl.sendmaxbuffer");
        }
      }
    }
    else
    {
      qop = DEFAULT_QOP;
      allQop = 1;
      strength = STRENGTH_MASKS;
    }
  }
  
  public boolean isComplete()
  {
    return completed;
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (!completed) {
      throw new IllegalStateException("SASL authentication not completed");
    }
    switch (paramString)
    {
    case "javax.security.sasl.qop": 
      if (privacy) {
        return "auth-conf";
      }
      if (integrity) {
        return "auth-int";
      }
      return "auth";
    case "javax.security.sasl.maxbuffer": 
      return Integer.toString(recvMaxBufSize);
    case "javax.security.sasl.rawsendsize": 
      return Integer.toString(rawSendSize);
    case "javax.security.sasl.sendmaxbuffer": 
      return Integer.toString(sendMaxBufSize);
    }
    return null;
  }
  
  protected static final byte combineMasks(byte[] paramArrayOfByte)
  {
    byte b = 0;
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      b = (byte)(b | paramArrayOfByte[i]);
    }
    return b;
  }
  
  protected static final byte findPreferredMask(byte paramByte, byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      if ((paramArrayOfByte[i] & paramByte) != 0) {
        return paramArrayOfByte[i];
      }
    }
    return 0;
  }
  
  private static final byte[] parseQop(String paramString)
    throws SaslException
  {
    return parseQop(paramString, null, false);
  }
  
  protected static final byte[] parseQop(String paramString, String[] paramArrayOfString, boolean paramBoolean)
    throws SaslException
  {
    if (paramString == null) {
      return DEFAULT_QOP;
    }
    return parseProp("javax.security.sasl.qop", paramString, QOP_TOKENS, QOP_MASKS, paramArrayOfString, paramBoolean);
  }
  
  private static final byte[] parseStrength(String paramString)
    throws SaslException
  {
    if (paramString == null) {
      return DEFAULT_STRENGTH;
    }
    return parseProp("javax.security.sasl.strength", paramString, STRENGTH_TOKENS, STRENGTH_MASKS, null, false);
  }
  
  private static final byte[] parseProp(String paramString1, String paramString2, String[] paramArrayOfString1, byte[] paramArrayOfByte, String[] paramArrayOfString2, boolean paramBoolean)
    throws SaslException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, ", \t\n");
    byte[] arrayOfByte = new byte[paramArrayOfString1.length];
    int i = 0;
    while ((localStringTokenizer.hasMoreTokens()) && (i < arrayOfByte.length))
    {
      String str = localStringTokenizer.nextToken();
      int j = 0;
      for (k = 0; (j == 0) && (k < paramArrayOfString1.length); k++) {
        if (str.equalsIgnoreCase(paramArrayOfString1[k]))
        {
          j = 1;
          arrayOfByte[(i++)] = paramArrayOfByte[k];
          if (paramArrayOfString2 != null) {
            paramArrayOfString2[k] = str;
          }
        }
      }
      if ((j == 0) && (!paramBoolean)) {
        throw new SaslException("Invalid token in " + paramString1 + ": " + paramString2);
      }
    }
    for (int k = i; k < arrayOfByte.length; k++) {
      arrayOfByte[k] = 0;
    }
    return arrayOfByte;
  }
  
  protected static final void traceOutput(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte)
  {
    traceOutput(paramString1, paramString2, paramString3, paramArrayOfByte, 0, paramArrayOfByte == null ? 0 : paramArrayOfByte.length);
  }
  
  protected static final void traceOutput(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      int i = paramInt2;
      Level localLevel;
      if (!logger.isLoggable(Level.FINEST))
      {
        paramInt2 = Math.min(16, paramInt2);
        localLevel = Level.FINER;
      }
      else
      {
        localLevel = Level.FINEST;
      }
      String str;
      if (paramArrayOfByte != null)
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramInt2);
        new HexDumpEncoder().encodeBuffer(new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), localByteArrayOutputStream);
        str = localByteArrayOutputStream.toString();
      }
      else
      {
        str = "NULL";
      }
      logger.logp(localLevel, paramString1, paramString2, "{0} ( {1} ): {2}", new Object[] { paramString3, new Integer(i), str });
    }
    catch (Exception localException)
    {
      logger.logp(Level.WARNING, paramString1, paramString2, "SASLIMPL09:Error generating trace output: {0}", localException);
    }
  }
  
  protected static final int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    int i = 0;
    for (int j = 0; j < paramInt2; j++)
    {
      i <<= 8;
      i |= paramArrayOfByte[(paramInt1 + j)] & 0xFF;
    }
    return i;
  }
  
  protected static final void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if (paramInt3 > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    for (int i = paramInt3 - 1; i >= 0; i--)
    {
      paramArrayOfByte[(paramInt2 + i)] = ((byte)(paramInt1 & 0xFF));
      paramInt1 >>>= 8;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\util\AbstractSaslImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */