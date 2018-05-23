package com.sun.security.ntlm;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.HexDumpEncoder;
import sun.security.provider.MD4;

class NTLM
{
  private final SecretKeyFactory fac;
  private final Cipher cipher;
  private final MessageDigest md4;
  private final Mac hmac;
  private final MessageDigest md5;
  private static final boolean DEBUG = System.getProperty("ntlm.debug") != null;
  final Version v;
  final boolean writeLM;
  final boolean writeNTLM;
  
  protected NTLM(String paramString)
    throws NTLMException
  {
    if (paramString == null) {
      paramString = "LMv2/NTLMv2";
    }
    switch (paramString)
    {
    case "LM": 
      v = Version.NTLM;
      writeLM = true;
      writeNTLM = false;
      break;
    case "NTLM": 
      v = Version.NTLM;
      writeLM = false;
      writeNTLM = true;
      break;
    case "LM/NTLM": 
      v = Version.NTLM;
      writeLM = (writeNTLM = 1);
      break;
    case "NTLM2": 
      v = Version.NTLM2;
      writeLM = (writeNTLM = 1);
      break;
    case "LMv2": 
      v = Version.NTLMv2;
      writeLM = true;
      writeNTLM = false;
      break;
    case "NTLMv2": 
      v = Version.NTLMv2;
      writeLM = false;
      writeNTLM = true;
      break;
    case "LMv2/NTLMv2": 
      v = Version.NTLMv2;
      writeLM = (writeNTLM = 1);
      break;
    default: 
      throw new NTLMException(5, "Unknown version " + paramString);
    }
    try
    {
      fac = SecretKeyFactory.getInstance("DES");
      cipher = Cipher.getInstance("DES/ECB/NoPadding");
      md4 = MD4.getInstance();
      hmac = Mac.getInstance("HmacMD5");
      md5 = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      throw new AssertionError();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError();
    }
  }
  
  public void debug(String paramString, Object... paramVarArgs)
  {
    if (DEBUG) {
      System.out.printf(paramString, paramVarArgs);
    }
  }
  
  public void debug(byte[] paramArrayOfByte)
  {
    if (DEBUG) {
      try
      {
        new HexDumpEncoder().encodeBuffer(paramArrayOfByte, System.out);
      }
      catch (IOException localIOException) {}
    }
  }
  
  byte[] makeDesKey(byte[] paramArrayOfByte, int paramInt)
  {
    int[] arrayOfInt = new int[paramArrayOfByte.length];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (paramArrayOfByte[i] < 0 ? paramArrayOfByte[i] + 256 : paramArrayOfByte[i]);
    }
    byte[] arrayOfByte = new byte[8];
    arrayOfByte[0] = ((byte)arrayOfInt[(paramInt + 0)]);
    arrayOfByte[1] = ((byte)(arrayOfInt[(paramInt + 0)] << 7 & 0xFF | arrayOfInt[(paramInt + 1)] >> 1));
    arrayOfByte[2] = ((byte)(arrayOfInt[(paramInt + 1)] << 6 & 0xFF | arrayOfInt[(paramInt + 2)] >> 2));
    arrayOfByte[3] = ((byte)(arrayOfInt[(paramInt + 2)] << 5 & 0xFF | arrayOfInt[(paramInt + 3)] >> 3));
    arrayOfByte[4] = ((byte)(arrayOfInt[(paramInt + 3)] << 4 & 0xFF | arrayOfInt[(paramInt + 4)] >> 4));
    arrayOfByte[5] = ((byte)(arrayOfInt[(paramInt + 4)] << 3 & 0xFF | arrayOfInt[(paramInt + 5)] >> 5));
    arrayOfByte[6] = ((byte)(arrayOfInt[(paramInt + 5)] << 2 & 0xFF | arrayOfInt[(paramInt + 6)] >> 6));
    arrayOfByte[7] = ((byte)(arrayOfInt[(paramInt + 6)] << 1 & 0xFF));
    return arrayOfByte;
  }
  
  byte[] calcLMHash(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = { 75, 71, 83, 33, 64, 35, 36, 37 };
    byte[] arrayOfByte2 = new byte[14];
    int i = paramArrayOfByte.length;
    if (i > 14) {
      i = 14;
    }
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte2, 0, i);
    try
    {
      DESKeySpec localDESKeySpec1 = new DESKeySpec(makeDesKey(arrayOfByte2, 0));
      DESKeySpec localDESKeySpec2 = new DESKeySpec(makeDesKey(arrayOfByte2, 7));
      SecretKey localSecretKey1 = fac.generateSecret(localDESKeySpec1);
      SecretKey localSecretKey2 = fac.generateSecret(localDESKeySpec2);
      cipher.init(1, localSecretKey1);
      byte[] arrayOfByte3 = cipher.doFinal(arrayOfByte1, 0, 8);
      cipher.init(1, localSecretKey2);
      byte[] arrayOfByte4 = cipher.doFinal(arrayOfByte1, 0, 8);
      byte[] arrayOfByte5 = new byte[21];
      System.arraycopy(arrayOfByte3, 0, arrayOfByte5, 0, 8);
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 8, 8);
      return arrayOfByte5;
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (BadPaddingException localBadPaddingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return null;
  }
  
  byte[] calcNTHash(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = md4.digest(paramArrayOfByte);
    byte[] arrayOfByte2 = new byte[21];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 16);
    return arrayOfByte2;
  }
  
  byte[] calcResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    try
    {
      assert (paramArrayOfByte1.length == 21);
      DESKeySpec localDESKeySpec1 = new DESKeySpec(makeDesKey(paramArrayOfByte1, 0));
      DESKeySpec localDESKeySpec2 = new DESKeySpec(makeDesKey(paramArrayOfByte1, 7));
      DESKeySpec localDESKeySpec3 = new DESKeySpec(makeDesKey(paramArrayOfByte1, 14));
      SecretKey localSecretKey1 = fac.generateSecret(localDESKeySpec1);
      SecretKey localSecretKey2 = fac.generateSecret(localDESKeySpec2);
      SecretKey localSecretKey3 = fac.generateSecret(localDESKeySpec3);
      cipher.init(1, localSecretKey1);
      byte[] arrayOfByte1 = cipher.doFinal(paramArrayOfByte2, 0, 8);
      cipher.init(1, localSecretKey2);
      byte[] arrayOfByte2 = cipher.doFinal(paramArrayOfByte2, 0, 8);
      cipher.init(1, localSecretKey3);
      byte[] arrayOfByte3 = cipher.doFinal(paramArrayOfByte2, 0, 8);
      byte[] arrayOfByte4 = new byte[24];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte4, 0, 8);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 8, 8);
      System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 16, 8);
      return arrayOfByte4;
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (BadPaddingException localBadPaddingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return null;
  }
  
  byte[] hmacMD5(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    try
    {
      SecretKeySpec localSecretKeySpec = new SecretKeySpec(Arrays.copyOf(paramArrayOfByte1, 16), "HmacMD5");
      hmac.init(localSecretKeySpec);
      return hmac.doFinal(paramArrayOfByte2);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return null;
  }
  
  byte[] calcV2(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    try
    {
      byte[] arrayOfByte1 = hmacMD5(paramArrayOfByte1, paramString.getBytes("UnicodeLittleUnmarked"));
      byte[] arrayOfByte2 = new byte[paramArrayOfByte2.length + 8];
      System.arraycopy(paramArrayOfByte3, 0, arrayOfByte2, 0, 8);
      System.arraycopy(paramArrayOfByte2, 0, arrayOfByte2, 8, paramArrayOfByte2.length);
      byte[] arrayOfByte3 = new byte[16 + paramArrayOfByte2.length];
      System.arraycopy(hmacMD5(arrayOfByte1, arrayOfByte2), 0, arrayOfByte3, 0, 16);
      System.arraycopy(paramArrayOfByte2, 0, arrayOfByte3, 16, paramArrayOfByte2.length);
      return arrayOfByte3;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return null;
  }
  
  static byte[] ntlm2LM(byte[] paramArrayOfByte)
  {
    return Arrays.copyOf(paramArrayOfByte, 24);
  }
  
  byte[] ntlm2NTLM(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    byte[] arrayOfByte1 = Arrays.copyOf(paramArrayOfByte3, 16);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, 8, 8);
    byte[] arrayOfByte2 = Arrays.copyOf(md5.digest(arrayOfByte1), 8);
    return calcResponse(paramArrayOfByte1, arrayOfByte2);
  }
  
  static byte[] getP1(char[] paramArrayOfChar)
  {
    try
    {
      return new String(paramArrayOfChar).toUpperCase(Locale.ENGLISH).getBytes("ISO8859_1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  static byte[] getP2(char[] paramArrayOfChar)
  {
    try
    {
      return new String(paramArrayOfChar).getBytes("UnicodeLittleUnmarked");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  static class Reader
  {
    private final byte[] internal;
    
    Reader(byte[] paramArrayOfByte)
    {
      internal = paramArrayOfByte;
    }
    
    int readInt(int paramInt)
      throws NTLMException
    {
      try
      {
        return (internal[paramInt] & 0xFF) + ((internal[(paramInt + 1)] & 0xFF) << 8) + ((internal[(paramInt + 2)] & 0xFF) << 16) + ((internal[(paramInt + 3)] & 0xFF) << 24);
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new NTLMException(1, "Input message incorrect size");
      }
    }
    
    int readShort(int paramInt)
      throws NTLMException
    {
      try
      {
        return (internal[paramInt] & 0xFF) + (internal[(paramInt + 1)] & 0xFF00);
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new NTLMException(1, "Input message incorrect size");
      }
    }
    
    byte[] readBytes(int paramInt1, int paramInt2)
      throws NTLMException
    {
      try
      {
        return Arrays.copyOfRange(internal, paramInt1, paramInt1 + paramInt2);
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new NTLMException(1, "Input message incorrect size");
      }
    }
    
    byte[] readSecurityBuffer(int paramInt)
      throws NTLMException
    {
      int i = readInt(paramInt + 4);
      if (i == 0) {
        return null;
      }
      try
      {
        return Arrays.copyOfRange(internal, i, i + readShort(paramInt));
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new NTLMException(1, "Input message incorrect size");
      }
    }
    
    String readSecurityBuffer(int paramInt, boolean paramBoolean)
      throws NTLMException
    {
      byte[] arrayOfByte = readSecurityBuffer(paramInt);
      try
      {
        return arrayOfByte == null ? null : new String(arrayOfByte, paramBoolean ? "UnicodeLittleUnmarked" : "ISO8859_1");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new NTLMException(1, "Invalid input encoding");
      }
    }
  }
  
  static class Writer
  {
    private byte[] internal;
    private int current;
    
    Writer(int paramInt1, int paramInt2)
    {
      assert (paramInt2 < 256);
      internal = new byte['Ā'];
      current = paramInt2;
      System.arraycopy(new byte[] { 78, 84, 76, 77, 83, 83, 80, 0, (byte)paramInt1 }, 0, internal, 0, 9);
    }
    
    void writeShort(int paramInt1, int paramInt2)
    {
      internal[paramInt1] = ((byte)paramInt2);
      internal[(paramInt1 + 1)] = ((byte)(paramInt2 >> 8));
    }
    
    void writeInt(int paramInt1, int paramInt2)
    {
      internal[paramInt1] = ((byte)paramInt2);
      internal[(paramInt1 + 1)] = ((byte)(paramInt2 >> 8));
      internal[(paramInt1 + 2)] = ((byte)(paramInt2 >> 16));
      internal[(paramInt1 + 3)] = ((byte)(paramInt2 >> 24));
    }
    
    void writeBytes(int paramInt, byte[] paramArrayOfByte)
    {
      System.arraycopy(paramArrayOfByte, 0, internal, paramInt, paramArrayOfByte.length);
    }
    
    void writeSecurityBuffer(int paramInt, byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte == null)
      {
        writeShort(paramInt + 4, current);
      }
      else
      {
        int i = paramArrayOfByte.length;
        if (current + i > internal.length) {
          internal = Arrays.copyOf(internal, current + i + 256);
        }
        writeShort(paramInt, i);
        writeShort(paramInt + 2, i);
        writeShort(paramInt + 4, current);
        System.arraycopy(paramArrayOfByte, 0, internal, current, i);
        current += i;
      }
    }
    
    void writeSecurityBuffer(int paramInt, String paramString, boolean paramBoolean)
    {
      try
      {
        writeSecurityBuffer(paramInt, paramString == null ? null : paramString.getBytes(paramBoolean ? "UnicodeLittleUnmarked" : "ISO8859_1"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
    }
    
    byte[] getBytes()
    {
      return Arrays.copyOf(internal, current);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\ntlm\NTLM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */