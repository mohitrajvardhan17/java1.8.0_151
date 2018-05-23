package com.sun.security.sasl.digest;

import com.sun.security.sasl.util.AbstractSaslImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;

abstract class DigestMD5Base
  extends AbstractSaslImpl
{
  private static final String DI_CLASS_NAME = DigestIntegrity.class.getName();
  private static final String DP_CLASS_NAME = DigestPrivacy.class.getName();
  protected static final int MAX_CHALLENGE_LENGTH = 2048;
  protected static final int MAX_RESPONSE_LENGTH = 4096;
  protected static final int DEFAULT_MAXBUF = 65536;
  protected static final int DES3 = 0;
  protected static final int RC4 = 1;
  protected static final int DES = 2;
  protected static final int RC4_56 = 3;
  protected static final int RC4_40 = 4;
  protected static final String[] CIPHER_TOKENS = { "3des", "rc4", "des", "rc4-56", "rc4-40" };
  private static final String[] JCE_CIPHER_NAME = { "DESede/CBC/NoPadding", "RC4", "DES/CBC/NoPadding" };
  protected static final byte DES_3_STRENGTH = 4;
  protected static final byte RC4_STRENGTH = 4;
  protected static final byte DES_STRENGTH = 2;
  protected static final byte RC4_56_STRENGTH = 2;
  protected static final byte RC4_40_STRENGTH = 1;
  protected static final byte UNSET = 0;
  protected static final byte[] CIPHER_MASKS = { 4, 4, 2, 2, 1 };
  private static final String SECURITY_LAYER_MARKER = ":00000000000000000000000000000000";
  protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  protected int step;
  protected CallbackHandler cbh;
  protected SecurityCtx secCtx;
  protected byte[] H_A1;
  protected byte[] nonce;
  protected String negotiatedStrength;
  protected String negotiatedCipher;
  protected String negotiatedQop;
  protected String negotiatedRealm;
  protected boolean useUTF8 = false;
  protected String encoding = "8859_1";
  protected String digestUri;
  protected String authzid;
  private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static final int RAW_NONCE_SIZE = 30;
  private static final int ENCODED_NONCE_SIZE = 40;
  private static final BigInteger MASK = new BigInteger("7f", 16);
  
  protected DigestMD5Base(Map<String, ?> paramMap, String paramString1, int paramInt, String paramString2, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    super(paramMap, paramString1);
    step = paramInt;
    digestUri = paramString2;
    cbh = paramCallbackHandler;
  }
  
  public String getMechanismName()
  {
    return "DIGEST-MD5";
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (!completed) {
      throw new IllegalStateException("DIGEST-MD5 authentication not completed");
    }
    if (secCtx == null) {
      throw new IllegalStateException("Neither integrity nor privacy was negotiated");
    }
    return secCtx.unwrap(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (!completed) {
      throw new IllegalStateException("DIGEST-MD5 authentication not completed");
    }
    if (secCtx == null) {
      throw new IllegalStateException("Neither integrity nor privacy was negotiated");
    }
    return secCtx.wrap(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void dispose()
    throws SaslException
  {
    if (secCtx != null) {
      secCtx = null;
    }
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (completed)
    {
      if (paramString.equals("javax.security.sasl.strength")) {
        return negotiatedStrength;
      }
      if (paramString.equals("javax.security.sasl.bound.server.name")) {
        return digestUri.substring(digestUri.indexOf('/') + 1);
      }
      return super.getNegotiatedProperty(paramString);
    }
    throw new IllegalStateException("DIGEST-MD5 authentication not completed");
  }
  
  protected static final byte[] generateNonce()
  {
    Random localRandom = new Random();
    byte[] arrayOfByte1 = new byte[30];
    localRandom.nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[40];
    int m = 0;
    for (int n = 0; n < arrayOfByte1.length; n += 3)
    {
      int i = arrayOfByte1[n];
      int j = arrayOfByte1[(n + 1)];
      int k = arrayOfByte1[(n + 2)];
      arrayOfByte2[(m++)] = ((byte)pem_array[(i >>> 2 & 0x3F)]);
      arrayOfByte2[(m++)] = ((byte)pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
      arrayOfByte2[(m++)] = ((byte)pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
      arrayOfByte2[(m++)] = ((byte)pem_array[(k & 0x3F)]);
    }
    return arrayOfByte2;
  }
  
  protected static void writeQuotedStringValue(ByteArrayOutputStream paramByteArrayOutputStream, byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    for (int k = 0; k < i; k++)
    {
      int j = paramArrayOfByte[k];
      if (needEscape((char)j)) {
        paramByteArrayOutputStream.write(92);
      }
      paramByteArrayOutputStream.write(j);
    }
  }
  
  private static boolean needEscape(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      if (needEscape(paramString.charAt(j))) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean needEscape(char paramChar)
  {
    return (paramChar == '"') || (paramChar == '\\') || (paramChar == '') || ((paramChar >= 0) && (paramChar <= '\037') && (paramChar != '\r') && (paramChar != '\t') && (paramChar != '\n'));
  }
  
  protected static String quotedStringValue(String paramString)
  {
    if (needEscape(paramString))
    {
      int i = paramString.length();
      char[] arrayOfChar = new char[i + i];
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        char c = paramString.charAt(k);
        if (needEscape(c)) {
          arrayOfChar[(j++)] = '\\';
        }
        arrayOfChar[(j++)] = c;
      }
      return new String(arrayOfChar, 0, j);
    }
    return paramString;
  }
  
  protected byte[] binaryToHex(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      if ((paramArrayOfByte[i] & 0xFF) < 16) {
        localStringBuffer.append("0" + Integer.toHexString(paramArrayOfByte[i] & 0xFF));
      } else {
        localStringBuffer.append(Integer.toHexString(paramArrayOfByte[i] & 0xFF));
      }
    }
    return localStringBuffer.toString().getBytes(encoding);
  }
  
  protected byte[] stringToByte_8859_1(String paramString)
    throws SaslException
  {
    char[] arrayOfChar = paramString.toCharArray();
    try
    {
      if (useUTF8) {
        for (int i = 0; i < arrayOfChar.length; i++) {
          if (arrayOfChar[i] > 'ÿ') {
            return paramString.getBytes("UTF8");
          }
        }
      }
      return paramString.getBytes("8859_1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new SaslException("cannot encode string in UTF8 or 8859-1 (Latin-1)", localUnsupportedEncodingException);
    }
  }
  
  protected static byte[] getPlatformCiphers()
  {
    byte[] arrayOfByte = new byte[CIPHER_TOKENS.length];
    for (int i = 0; i < JCE_CIPHER_NAME.length; i++) {
      try
      {
        Cipher.getInstance(JCE_CIPHER_NAME[i]);
        logger.log(Level.FINE, "DIGEST01:Platform supports {0}", JCE_CIPHER_NAME[i]);
        int tmp44_43 = i;
        arrayOfByte[tmp44_43] = ((byte)(arrayOfByte[tmp44_43] | CIPHER_MASKS[i]));
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}catch (NoSuchPaddingException localNoSuchPaddingException) {}
    }
    if (arrayOfByte[1] != 0)
    {
      int tmp76_75 = 3;
      arrayOfByte[tmp76_75] = ((byte)(arrayOfByte[tmp76_75] | CIPHER_MASKS[3]));
      int tmp88_87 = 4;
      arrayOfByte[tmp88_87] = ((byte)(arrayOfByte[tmp88_87] | CIPHER_MASKS[4]));
    }
    return arrayOfByte;
  }
  
  protected byte[] generateResponseValue(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, char[] paramArrayOfChar, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, byte[] paramArrayOfByte3)
    throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException
  {
    MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
    ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
    localByteArrayOutputStream1.write((paramString1 + ":" + paramString2).getBytes(encoding));
    if ((paramString3.equals("auth-conf")) || (paramString3.equals("auth-int")))
    {
      logger.log(Level.FINE, "DIGEST04:QOP: {0}", paramString3);
      localByteArrayOutputStream1.write(":00000000000000000000000000000000".getBytes(encoding));
    }
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST05:A2: {0}", localByteArrayOutputStream1.toString());
    }
    localMessageDigest.update(localByteArrayOutputStream1.toByteArray());
    byte[] arrayOfByte3 = localMessageDigest.digest();
    byte[] arrayOfByte2 = binaryToHex(arrayOfByte3);
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST06:HEX(H(A2)): {0}", new String(arrayOfByte2));
    }
    ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
    localByteArrayOutputStream2.write(stringToByte_8859_1(paramString4));
    localByteArrayOutputStream2.write(58);
    localByteArrayOutputStream2.write(stringToByte_8859_1(paramString5));
    localByteArrayOutputStream2.write(58);
    localByteArrayOutputStream2.write(stringToByte_8859_1(new String(paramArrayOfChar)));
    localMessageDigest.update(localByteArrayOutputStream2.toByteArray());
    arrayOfByte3 = localMessageDigest.digest();
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST07:H({0}) = {1}", new Object[] { localByteArrayOutputStream2.toString(), new String(binaryToHex(arrayOfByte3)) });
    }
    ByteArrayOutputStream localByteArrayOutputStream3 = new ByteArrayOutputStream();
    localByteArrayOutputStream3.write(arrayOfByte3);
    localByteArrayOutputStream3.write(58);
    localByteArrayOutputStream3.write(paramArrayOfByte1);
    localByteArrayOutputStream3.write(58);
    localByteArrayOutputStream3.write(paramArrayOfByte2);
    if (paramArrayOfByte3 != null)
    {
      localByteArrayOutputStream3.write(58);
      localByteArrayOutputStream3.write(paramArrayOfByte3);
    }
    localMessageDigest.update(localByteArrayOutputStream3.toByteArray());
    arrayOfByte3 = localMessageDigest.digest();
    H_A1 = arrayOfByte3;
    byte[] arrayOfByte1 = binaryToHex(arrayOfByte3);
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST08:H(A1) = {0}", new String(arrayOfByte1));
    }
    ByteArrayOutputStream localByteArrayOutputStream4 = new ByteArrayOutputStream();
    localByteArrayOutputStream4.write(arrayOfByte1);
    localByteArrayOutputStream4.write(58);
    localByteArrayOutputStream4.write(paramArrayOfByte1);
    localByteArrayOutputStream4.write(58);
    localByteArrayOutputStream4.write(nonceCountToHex(paramInt).getBytes(encoding));
    localByteArrayOutputStream4.write(58);
    localByteArrayOutputStream4.write(paramArrayOfByte2);
    localByteArrayOutputStream4.write(58);
    localByteArrayOutputStream4.write(paramString3.getBytes(encoding));
    localByteArrayOutputStream4.write(58);
    localByteArrayOutputStream4.write(arrayOfByte2);
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST09:KD: {0}", localByteArrayOutputStream4.toString());
    }
    localMessageDigest.update(localByteArrayOutputStream4.toByteArray());
    arrayOfByte3 = localMessageDigest.digest();
    byte[] arrayOfByte4 = binaryToHex(arrayOfByte3);
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST10:response-value: {0}", new String(arrayOfByte4));
    }
    return arrayOfByte4;
  }
  
  protected static String nonceCountToHex(int paramInt)
  {
    String str = Integer.toHexString(paramInt);
    StringBuffer localStringBuffer = new StringBuffer();
    if (str.length() < 8) {
      for (int i = 0; i < 8 - str.length(); i++) {
        localStringBuffer.append("0");
      }
    }
    return localStringBuffer.toString() + str;
  }
  
  protected static byte[][] parseDirectives(byte[] paramArrayOfByte, String[] paramArrayOfString, List<byte[]> paramList, int paramInt)
    throws SaslException
  {
    byte[][] arrayOfByte = new byte[paramArrayOfString.length][];
    ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream(10);
    ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream(10);
    int i = 1;
    int j = 0;
    int k = 0;
    int m = skipLws(paramArrayOfByte, 0);
    while (m < paramArrayOfByte.length)
    {
      byte b = paramArrayOfByte[m];
      if (i != 0)
      {
        if (b == 44)
        {
          if (localByteArrayOutputStream1.size() != 0) {
            throw new SaslException("Directive key contains a ',':" + localByteArrayOutputStream1);
          }
          m = skipLws(paramArrayOfByte, m + 1);
        }
        else if (b == 61)
        {
          if (localByteArrayOutputStream1.size() == 0) {
            throw new SaslException("Empty directive key");
          }
          i = 0;
          m = skipLws(paramArrayOfByte, m + 1);
          if (m < paramArrayOfByte.length)
          {
            if (paramArrayOfByte[m] == 34)
            {
              j = 1;
              m++;
            }
          }
          else {
            throw new SaslException("Valueless directive found: " + localByteArrayOutputStream1.toString());
          }
        }
        else if (isLws(b))
        {
          m = skipLws(paramArrayOfByte, m + 1);
          if (m < paramArrayOfByte.length)
          {
            if (paramArrayOfByte[m] != 61) {
              throw new SaslException("'=' expected after key: " + localByteArrayOutputStream1.toString());
            }
          }
          else {
            throw new SaslException("'=' expected after key: " + localByteArrayOutputStream1.toString());
          }
        }
        else
        {
          localByteArrayOutputStream1.write(b);
          m++;
        }
      }
      else if (j != 0)
      {
        if (b == 92)
        {
          m++;
          if (m < paramArrayOfByte.length)
          {
            localByteArrayOutputStream2.write(paramArrayOfByte[m]);
            m++;
          }
          else
          {
            throw new SaslException("Unmatched quote found for directive: " + localByteArrayOutputStream1.toString() + " with value: " + localByteArrayOutputStream2.toString());
          }
        }
        else if (b == 34)
        {
          m++;
          j = 0;
          k = 1;
        }
        else
        {
          localByteArrayOutputStream2.write(b);
          m++;
        }
      }
      else if ((isLws(b)) || (b == 44))
      {
        extractDirective(localByteArrayOutputStream1.toString(), localByteArrayOutputStream2.toByteArray(), paramArrayOfString, arrayOfByte, paramList, paramInt);
        localByteArrayOutputStream1.reset();
        localByteArrayOutputStream2.reset();
        i = 1;
        j = k = 0;
        m = skipLws(paramArrayOfByte, m + 1);
      }
      else
      {
        if (k != 0) {
          throw new SaslException("Expecting comma or linear whitespace after quoted string: \"" + localByteArrayOutputStream2.toString() + "\"");
        }
        localByteArrayOutputStream2.write(b);
        m++;
      }
    }
    if (j != 0) {
      throw new SaslException("Unmatched quote found for directive: " + localByteArrayOutputStream1.toString() + " with value: " + localByteArrayOutputStream2.toString());
    }
    if (localByteArrayOutputStream1.size() > 0) {
      extractDirective(localByteArrayOutputStream1.toString(), localByteArrayOutputStream2.toByteArray(), paramArrayOfString, arrayOfByte, paramList, paramInt);
    }
    return arrayOfByte;
  }
  
  private static boolean isLws(byte paramByte)
  {
    switch (paramByte)
    {
    case 9: 
    case 10: 
    case 13: 
    case 32: 
      return true;
    }
    return false;
  }
  
  private static int skipLws(byte[] paramArrayOfByte, int paramInt)
  {
    for (int i = paramInt; i < paramArrayOfByte.length; i++) {
      if (!isLws(paramArrayOfByte[i])) {
        return i;
      }
    }
    return i;
  }
  
  private static void extractDirective(String paramString, byte[] paramArrayOfByte, String[] paramArrayOfString, byte[][] paramArrayOfByte1, List<byte[]> paramList, int paramInt)
    throws SaslException
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramString.equalsIgnoreCase(paramArrayOfString[i]))
      {
        if (paramArrayOfByte1[i] == null)
        {
          paramArrayOfByte1[i] = paramArrayOfByte;
          if (!logger.isLoggable(Level.FINE)) {
            break;
          }
          logger.log(Level.FINE, "DIGEST11:Directive {0} = {1}", new Object[] { paramArrayOfString[i], new String(paramArrayOfByte1[i]) });
          break;
        }
        if ((paramList != null) && (i == paramInt))
        {
          if (paramList.isEmpty()) {
            paramList.add(paramArrayOfByte1[i]);
          }
          paramList.add(paramArrayOfByte);
          break;
        }
        throw new SaslException("DIGEST-MD5: peer sent more than one " + paramString + " directive: " + new String(paramArrayOfByte));
      }
    }
  }
  
  private static void setParityBit(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i] & 0xFE;
      j |= Integer.bitCount(j) & 0x1 ^ 0x1;
      paramArrayOfByte[i] = ((byte)j);
    }
  }
  
  private static byte[] addDesParity(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 != 7) {
      throw new IllegalArgumentException("Invalid length of DES Key Value:" + paramInt2);
    }
    byte[] arrayOfByte1 = new byte[7];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte1, 0, paramInt2);
    byte[] arrayOfByte2 = new byte[8];
    BigInteger localBigInteger = new BigInteger(arrayOfByte1);
    for (int i = arrayOfByte2.length - 1; i >= 0; i--)
    {
      arrayOfByte2[i] = localBigInteger.and(MASK).toByteArray()[0];
      int tmp96_94 = i;
      byte[] tmp96_92 = arrayOfByte2;
      tmp96_92[tmp96_94] = ((byte)(tmp96_92[tmp96_94] << 1));
      localBigInteger = localBigInteger.shiftRight(7);
    }
    setParityBit(arrayOfByte2);
    return arrayOfByte2;
  }
  
  private static SecretKey makeDesKeys(byte[] paramArrayOfByte, String paramString)
    throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException
  {
    byte[] arrayOfByte1 = addDesParity(paramArrayOfByte, 0, 7);
    Object localObject = null;
    SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance(paramString);
    switch (paramString)
    {
    case "des": 
      localObject = new DESKeySpec(arrayOfByte1, 0);
      if (logger.isLoggable(Level.FINEST))
      {
        traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST42:DES key input: ", paramArrayOfByte);
        traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST43:DES key parity-adjusted: ", arrayOfByte1);
        traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST44:DES key material: ", ((DESKeySpec)localObject).getKey());
        logger.log(Level.FINEST, "DIGEST45: is parity-adjusted? {0}", Boolean.valueOf(DESKeySpec.isParityAdjusted(arrayOfByte1, 0)));
      }
      break;
    case "desede": 
      byte[] arrayOfByte2 = addDesParity(paramArrayOfByte, 7, 7);
      byte[] arrayOfByte3 = new byte[arrayOfByte1.length * 2 + arrayOfByte2.length];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, arrayOfByte1.length + arrayOfByte2.length, arrayOfByte1.length);
      localObject = new DESedeKeySpec(arrayOfByte3, 0);
      if (logger.isLoggable(Level.FINEST))
      {
        traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST46:3DES key input: ", paramArrayOfByte);
        traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST47:3DES key ede: ", arrayOfByte3);
        traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST48:3DES key material: ", ((DESedeKeySpec)localObject).getKey());
        logger.log(Level.FINEST, "DIGEST49: is parity-adjusted? ", Boolean.valueOf(DESedeKeySpec.isParityAdjusted(arrayOfByte3, 0)));
      }
      break;
    default: 
      throw new IllegalArgumentException("Invalid DES strength:" + paramString);
    }
    return localSecretKeyFactory.generateSecret((KeySpec)localObject);
  }
  
  class DigestIntegrity
    implements SecurityCtx
  {
    private static final String CLIENT_INT_MAGIC = "Digest session key to client-to-server signing key magic constant";
    private static final String SVR_INT_MAGIC = "Digest session key to server-to-client signing key magic constant";
    protected byte[] myKi;
    protected byte[] peerKi;
    protected int mySeqNum = 0;
    protected int peerSeqNum = 0;
    protected final byte[] messageType = new byte[2];
    protected final byte[] sequenceNum = new byte[4];
    
    DigestIntegrity(boolean paramBoolean)
      throws SaslException
    {
      try
      {
        generateIntegrityKeyPair(paramBoolean);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new SaslException("DIGEST-MD5: Error encoding strings into UTF-8", localUnsupportedEncodingException);
      }
      catch (IOException localIOException)
      {
        throw new SaslException("DIGEST-MD5: Error accessing buffers required to create integrity key pairs", localIOException);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new SaslException("DIGEST-MD5: Unsupported digest algorithm used to create integrity key pairs", localNoSuchAlgorithmException);
      }
      DigestMD5Base.intToNetworkByteOrder(1, messageType, 0, 2);
    }
    
    private void generateIntegrityKeyPair(boolean paramBoolean)
      throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException
    {
      byte[] arrayOfByte1 = "Digest session key to client-to-server signing key magic constant".getBytes(encoding);
      byte[] arrayOfByte2 = "Digest session key to server-to-client signing key magic constant".getBytes(encoding);
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      byte[] arrayOfByte3 = new byte[H_A1.length + arrayOfByte1.length];
      System.arraycopy(H_A1, 0, arrayOfByte3, 0, H_A1.length);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, H_A1.length, arrayOfByte1.length);
      localMessageDigest.update(arrayOfByte3);
      byte[] arrayOfByte4 = localMessageDigest.digest();
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, H_A1.length, arrayOfByte2.length);
      localMessageDigest.update(arrayOfByte3);
      byte[] arrayOfByte5 = localMessageDigest.digest();
      if (DigestMD5Base.logger.isLoggable(Level.FINER))
      {
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST12:Kic: ", arrayOfByte4);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST13:Kis: ", arrayOfByte5);
      }
      if (paramBoolean)
      {
        myKi = arrayOfByte4;
        peerKi = arrayOfByte5;
      }
      else
      {
        myKi = arrayOfByte5;
        peerKi = arrayOfByte4;
      }
    }
    
    public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SaslException
    {
      if (paramInt2 == 0) {
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      byte[] arrayOfByte1 = new byte[paramInt2 + 10 + 2 + 4];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte1, 0, paramInt2);
      incrementSeqNum();
      byte[] arrayOfByte2 = getHMAC(myKi, sequenceNum, paramArrayOfByte, paramInt1, paramInt2);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST))
      {
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST14:outgoing: ", paramArrayOfByte, paramInt1, paramInt2);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST15:seqNum: ", sequenceNum);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST16:MAC: ", arrayOfByte2);
      }
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, paramInt2, 10);
      System.arraycopy(messageType, 0, arrayOfByte1, paramInt2 + 10, 2);
      System.arraycopy(sequenceNum, 0, arrayOfByte1, paramInt2 + 12, 4);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST17:wrapped: ", arrayOfByte1);
      }
      return arrayOfByte1;
    }
    
    public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SaslException
    {
      if (paramInt2 == 0) {
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      byte[] arrayOfByte1 = new byte[10];
      byte[] arrayOfByte2 = new byte[paramInt2 - 16];
      byte[] arrayOfByte3 = new byte[2];
      byte[] arrayOfByte4 = new byte[4];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte2, 0, arrayOfByte2.length);
      System.arraycopy(paramArrayOfByte, paramInt1 + arrayOfByte2.length, arrayOfByte1, 0, 10);
      System.arraycopy(paramArrayOfByte, paramInt1 + arrayOfByte2.length + 10, arrayOfByte3, 0, 2);
      System.arraycopy(paramArrayOfByte, paramInt1 + arrayOfByte2.length + 12, arrayOfByte4, 0, 4);
      byte[] arrayOfByte5 = getHMAC(peerKi, arrayOfByte4, arrayOfByte2, 0, arrayOfByte2.length);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST))
      {
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST18:incoming: ", arrayOfByte2);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST19:MAC: ", arrayOfByte1);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST20:messageType: ", arrayOfByte3);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST21:sequenceNum: ", arrayOfByte4);
        DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST22:expectedMAC: ", arrayOfByte5);
      }
      if (!Arrays.equals(arrayOfByte1, arrayOfByte5))
      {
        DigestMD5Base.logger.log(Level.INFO, "DIGEST23:Unmatched MACs");
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      if (peerSeqNum != DigestMD5Base.networkByteOrderToInt(arrayOfByte4, 0, 4)) {
        throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte4, 0, 4) + " Expected: " + peerSeqNum);
      }
      if (!Arrays.equals(messageType, arrayOfByte3)) {
        throw new SaslException("DIGEST-MD5: invalid message type: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte3, 0, 2));
      }
      peerSeqNum += 1;
      return arrayOfByte2;
    }
    
    protected byte[] getHMAC(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2)
      throws SaslException
    {
      byte[] arrayOfByte1 = new byte[4 + paramInt2];
      System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, 0, 4);
      System.arraycopy(paramArrayOfByte3, paramInt1, arrayOfByte1, 4, paramInt2);
      try
      {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "HmacMD5");
        Mac localMac = Mac.getInstance("HmacMD5");
        localMac.init(localSecretKeySpec);
        localMac.update(arrayOfByte1);
        byte[] arrayOfByte2 = localMac.doFinal();
        byte[] arrayOfByte3 = new byte[10];
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 0, 10);
        return arrayOfByte3;
      }
      catch (InvalidKeyException localInvalidKeyException)
      {
        throw new SaslException("DIGEST-MD5: Invalid bytes used for key of HMAC-MD5 hash.", localInvalidKeyException);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new SaslException("DIGEST-MD5: Error creating instance of MD5 digest algorithm", localNoSuchAlgorithmException);
      }
    }
    
    protected void incrementSeqNum()
    {
      DigestMD5Base.intToNetworkByteOrder(mySeqNum++, sequenceNum, 0, 4);
    }
  }
  
  final class DigestPrivacy
    extends DigestMD5Base.DigestIntegrity
    implements SecurityCtx
  {
    private static final String CLIENT_CONF_MAGIC = "Digest H(A1) to client-to-server sealing key magic constant";
    private static final String SVR_CONF_MAGIC = "Digest H(A1) to server-to-client sealing key magic constant";
    private Cipher encCipher;
    private Cipher decCipher;
    
    DigestPrivacy(boolean paramBoolean)
      throws SaslException
    {
      super(paramBoolean);
      try
      {
        generatePrivacyKeyPair(paramBoolean);
      }
      catch (SaslException localSaslException)
      {
        throw localSaslException;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new SaslException("DIGEST-MD5: Error encoding string value into UTF-8", localUnsupportedEncodingException);
      }
      catch (IOException localIOException)
      {
        throw new SaslException("DIGEST-MD5: Error accessing buffers required to generate cipher keys", localIOException);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new SaslException("DIGEST-MD5: Error creating instance of required cipher or digest", localNoSuchAlgorithmException);
      }
    }
    
    private void generatePrivacyKeyPair(boolean paramBoolean)
      throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, SaslException
    {
      byte[] arrayOfByte1 = "Digest H(A1) to client-to-server sealing key magic constant".getBytes(encoding);
      byte[] arrayOfByte2 = "Digest H(A1) to server-to-client sealing key magic constant".getBytes(encoding);
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      int i;
      if (negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[4])) {
        i = 5;
      } else if (negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[3])) {
        i = 7;
      } else {
        i = 16;
      }
      byte[] arrayOfByte3 = new byte[i + arrayOfByte1.length];
      System.arraycopy(H_A1, 0, arrayOfByte3, 0, i);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i, arrayOfByte1.length);
      localMessageDigest.update(arrayOfByte3);
      byte[] arrayOfByte4 = localMessageDigest.digest();
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, i, arrayOfByte2.length);
      localMessageDigest.update(arrayOfByte3);
      byte[] arrayOfByte5 = localMessageDigest.digest();
      if (DigestMD5Base.logger.isLoggable(Level.FINER))
      {
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST24:Kcc: ", arrayOfByte4);
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST25:Kcs: ", arrayOfByte5);
      }
      byte[] arrayOfByte6;
      byte[] arrayOfByte7;
      if (paramBoolean)
      {
        arrayOfByte6 = arrayOfByte4;
        arrayOfByte7 = arrayOfByte5;
      }
      else
      {
        arrayOfByte6 = arrayOfByte5;
        arrayOfByte7 = arrayOfByte4;
      }
      try
      {
        Object localObject1;
        Object localObject2;
        if (negotiatedCipher.indexOf(DigestMD5Base.CIPHER_TOKENS[1]) > -1)
        {
          encCipher = Cipher.getInstance("RC4");
          decCipher = Cipher.getInstance("RC4");
          localObject1 = new SecretKeySpec(arrayOfByte6, "RC4");
          localObject2 = new SecretKeySpec(arrayOfByte7, "RC4");
          encCipher.init(1, (Key)localObject1);
          decCipher.init(2, (Key)localObject2);
        }
        else if ((negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2])) || (negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[0])))
        {
          String str1;
          String str2;
          if (negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2]))
          {
            str1 = "DES/CBC/NoPadding";
            str2 = "des";
          }
          else
          {
            str1 = "DESede/CBC/NoPadding";
            str2 = "desede";
          }
          encCipher = Cipher.getInstance(str1);
          decCipher = Cipher.getInstance(str1);
          localObject1 = DigestMD5Base.makeDesKeys(arrayOfByte6, str2);
          localObject2 = DigestMD5Base.makeDesKeys(arrayOfByte7, str2);
          IvParameterSpec localIvParameterSpec1 = new IvParameterSpec(arrayOfByte6, 8, 8);
          IvParameterSpec localIvParameterSpec2 = new IvParameterSpec(arrayOfByte7, 8, 8);
          encCipher.init(1, (Key)localObject1, localIvParameterSpec1);
          decCipher.init(2, (Key)localObject2, localIvParameterSpec2);
          if (DigestMD5Base.logger.isLoggable(Level.FINER))
          {
            DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST26:" + negotiatedCipher + " IVcc: ", localIvParameterSpec1.getIV());
            DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST27:" + negotiatedCipher + " IVcs: ", localIvParameterSpec2.getIV());
            DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST28:" + negotiatedCipher + " encryption key: ", ((SecretKey)localObject1).getEncoded());
            DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST29:" + negotiatedCipher + " decryption key: ", ((SecretKey)localObject2).getEncoded());
          }
        }
      }
      catch (InvalidKeySpecException localInvalidKeySpecException)
      {
        throw new SaslException("DIGEST-MD5: Unsupported key specification used.", localInvalidKeySpecException);
      }
      catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
      {
        throw new SaslException("DIGEST-MD5: Invalid cipher algorithem parameter used to create cipher instance", localInvalidAlgorithmParameterException);
      }
      catch (NoSuchPaddingException localNoSuchPaddingException)
      {
        throw new SaslException("DIGEST-MD5: Unsupported padding used for chosen cipher", localNoSuchPaddingException);
      }
      catch (InvalidKeyException localInvalidKeyException)
      {
        throw new SaslException("DIGEST-MD5: Invalid data used to initialize keys", localInvalidKeyException);
      }
    }
    
    public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SaslException
    {
      if (paramInt2 == 0) {
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      incrementSeqNum();
      byte[] arrayOfByte1 = getHMAC(myKi, sequenceNum, paramArrayOfByte, paramInt1, paramInt2);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST))
      {
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST30:Outgoing: ", paramArrayOfByte, paramInt1, paramInt2);
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "seqNum: ", sequenceNum);
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "MAC: ", arrayOfByte1);
      }
      int i = encCipher.getBlockSize();
      byte[] arrayOfByte2;
      if (i > 1)
      {
        int j = i - (paramInt2 + 10) % i;
        arrayOfByte2 = new byte[j];
        for (int k = 0; k < j; k++) {
          arrayOfByte2[k] = ((byte)j);
        }
      }
      else
      {
        arrayOfByte2 = DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      byte[] arrayOfByte3 = new byte[paramInt2 + arrayOfByte2.length + 10];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte3, 0, paramInt2);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, paramInt2, arrayOfByte2.length);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, paramInt2 + arrayOfByte2.length, 10);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST31:{msg, pad, KicMAC}: ", arrayOfByte3);
      }
      byte[] arrayOfByte4;
      try
      {
        arrayOfByte4 = encCipher.update(arrayOfByte3);
        if (arrayOfByte4 == null) {
          throw new IllegalBlockSizeException("" + arrayOfByte3.length);
        }
      }
      catch (IllegalBlockSizeException localIllegalBlockSizeException)
      {
        throw new SaslException("DIGEST-MD5: Invalid block size for cipher", localIllegalBlockSizeException);
      }
      byte[] arrayOfByte5 = new byte[arrayOfByte4.length + 2 + 4];
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte4.length);
      System.arraycopy(messageType, 0, arrayOfByte5, arrayOfByte4.length, 2);
      System.arraycopy(sequenceNum, 0, arrayOfByte5, arrayOfByte4.length + 2, 4);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST32:Wrapped: ", arrayOfByte5);
      }
      return arrayOfByte5;
    }
    
    public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SaslException
    {
      if (paramInt2 == 0) {
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      byte[] arrayOfByte1 = new byte[paramInt2 - 6];
      byte[] arrayOfByte2 = new byte[2];
      byte[] arrayOfByte3 = new byte[4];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte1, 0, arrayOfByte1.length);
      System.arraycopy(paramArrayOfByte, paramInt1 + arrayOfByte1.length, arrayOfByte2, 0, 2);
      System.arraycopy(paramArrayOfByte, paramInt1 + arrayOfByte1.length + 2, arrayOfByte3, 0, 4);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST))
      {
        DigestMD5Base.logger.log(Level.FINEST, "DIGEST33:Expecting sequence num: {0}", new Integer(peerSeqNum));
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST34:incoming: ", arrayOfByte1);
      }
      byte[] arrayOfByte4;
      try
      {
        arrayOfByte4 = decCipher.update(arrayOfByte1);
        if (arrayOfByte4 == null) {
          throw new IllegalBlockSizeException("" + arrayOfByte1.length);
        }
      }
      catch (IllegalBlockSizeException localIllegalBlockSizeException)
      {
        throw new SaslException("DIGEST-MD5: Illegal block sizes used with chosen cipher", localIllegalBlockSizeException);
      }
      byte[] arrayOfByte5 = new byte[arrayOfByte4.length - 10];
      byte[] arrayOfByte6 = new byte[10];
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte5.length);
      System.arraycopy(arrayOfByte4, arrayOfByte5.length, arrayOfByte6, 0, 10);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST))
      {
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST35:Unwrapped (w/padding): ", arrayOfByte5);
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST36:MAC: ", arrayOfByte6);
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST37:messageType: ", arrayOfByte2);
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST38:sequenceNum: ", arrayOfByte3);
      }
      int i = arrayOfByte5.length;
      int j = decCipher.getBlockSize();
      if (j > 1)
      {
        i -= arrayOfByte5[(arrayOfByte5.length - 1)];
        if (i < 0)
        {
          if (DigestMD5Base.logger.isLoggable(Level.INFO)) {
            DigestMD5Base.logger.log(Level.INFO, "DIGEST39:Incorrect padding: {0}", new Byte(arrayOfByte5[(arrayOfByte5.length - 1)]));
          }
          return DigestMD5Base.EMPTY_BYTE_ARRAY;
        }
      }
      byte[] arrayOfByte7 = getHMAC(peerKi, arrayOfByte3, arrayOfByte5, 0, i);
      if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST40:KisMAC: ", arrayOfByte7);
      }
      if (!Arrays.equals(arrayOfByte6, arrayOfByte7))
      {
        DigestMD5Base.logger.log(Level.INFO, "DIGEST41:Unmatched MACs");
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      }
      if (peerSeqNum != DigestMD5Base.networkByteOrderToInt(arrayOfByte3, 0, 4)) {
        throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte3, 0, 4) + " Expected: " + peerSeqNum);
      }
      if (!Arrays.equals(messageType, arrayOfByte2)) {
        throw new SaslException("DIGEST-MD5: invalid message type: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte2, 0, 2));
      }
      peerSeqNum += 1;
      if (i == arrayOfByte5.length) {
        return arrayOfByte5;
      }
      byte[] arrayOfByte8 = new byte[i];
      System.arraycopy(arrayOfByte5, 0, arrayOfByte8, 0, i);
      return arrayOfByte8;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\digest\DigestMD5Base.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */