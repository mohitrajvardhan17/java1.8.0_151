package com.sun.security.ntlm;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public final class Client
  extends NTLM
{
  private final String hostname;
  private final String username;
  private String domain;
  private byte[] pw1;
  private byte[] pw2;
  
  public Client(String paramString1, String paramString2, String paramString3, String paramString4, char[] paramArrayOfChar)
    throws NTLMException
  {
    super(paramString1);
    if ((paramString3 == null) || (paramArrayOfChar == null)) {
      throw new NTLMException(6, "username/password cannot be null");
    }
    hostname = paramString2;
    username = paramString3;
    domain = (paramString4 == null ? "" : paramString4);
    pw1 = getP1(paramArrayOfChar);
    pw2 = getP2(paramArrayOfChar);
    debug("NTLM Client: (h,u,t,version(v)) = (%s,%s,%s,%s(%s))\n", new Object[] { paramString2, paramString3, paramString4, paramString1, v.toString() });
  }
  
  public byte[] type1()
  {
    NTLM.Writer localWriter = new NTLM.Writer(1, 32);
    int i = 33287;
    if (v != Version.NTLM) {
      i |= 0x80000;
    }
    localWriter.writeInt(12, i);
    debug("NTLM Client: Type 1 created\n", new Object[0]);
    debug(localWriter.getBytes());
    return localWriter.getBytes();
  }
  
  public byte[] type3(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NTLMException
  {
    if ((paramArrayOfByte1 == null) || ((v != Version.NTLM) && (paramArrayOfByte2 == null))) {
      throw new NTLMException(6, "type2 and nonce cannot be null");
    }
    debug("NTLM Client: Type 2 received\n", new Object[0]);
    debug(paramArrayOfByte1);
    NTLM.Reader localReader = new NTLM.Reader(paramArrayOfByte1);
    byte[] arrayOfByte1 = localReader.readBytes(24, 8);
    int i = localReader.readInt(20);
    boolean bool = (i & 0x1) == 1;
    int j = 0x88200 | i & 0x3;
    NTLM.Writer localWriter = new NTLM.Writer(3, 64);
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    localWriter.writeSecurityBuffer(28, domain, bool);
    localWriter.writeSecurityBuffer(36, username, bool);
    localWriter.writeSecurityBuffer(44, hostname, bool);
    byte[] arrayOfByte4;
    byte[] arrayOfByte5;
    if (v == Version.NTLM)
    {
      arrayOfByte4 = calcLMHash(pw1);
      arrayOfByte5 = calcNTHash(pw2);
      if (writeLM) {
        arrayOfByte2 = calcResponse(arrayOfByte4, arrayOfByte1);
      }
      if (writeNTLM) {
        arrayOfByte3 = calcResponse(arrayOfByte5, arrayOfByte1);
      }
    }
    else if (v == Version.NTLM2)
    {
      arrayOfByte4 = calcNTHash(pw2);
      arrayOfByte2 = ntlm2LM(paramArrayOfByte2);
      arrayOfByte3 = ntlm2NTLM(arrayOfByte4, paramArrayOfByte2, arrayOfByte1);
    }
    else
    {
      arrayOfByte4 = calcNTHash(pw2);
      if (writeLM) {
        arrayOfByte2 = calcV2(arrayOfByte4, username.toUpperCase(Locale.US) + domain, paramArrayOfByte2, arrayOfByte1);
      }
      if (writeNTLM)
      {
        arrayOfByte5 = (i & 0x800000) != 0 ? localReader.readSecurityBuffer(40) : new byte[0];
        byte[] arrayOfByte6 = new byte[32 + arrayOfByte5.length];
        System.arraycopy(new byte[] { 1, 1, 0, 0, 0, 0, 0, 0 }, 0, arrayOfByte6, 0, 8);
        byte[] arrayOfByte7 = BigInteger.valueOf(new Date().getTime()).add(new BigInteger("11644473600000")).multiply(BigInteger.valueOf(10000L)).toByteArray();
        for (int k = 0; k < arrayOfByte7.length; k++) {
          arrayOfByte6[(8 + arrayOfByte7.length - k - 1)] = arrayOfByte7[k];
        }
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte6, 16, 8);
        System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, arrayOfByte6, 24, 4);
        System.arraycopy(arrayOfByte5, 0, arrayOfByte6, 28, arrayOfByte5.length);
        System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, arrayOfByte6, 28 + arrayOfByte5.length, 4);
        arrayOfByte3 = calcV2(arrayOfByte4, username.toUpperCase(Locale.US) + domain, arrayOfByte6, arrayOfByte1);
      }
    }
    localWriter.writeSecurityBuffer(12, arrayOfByte2);
    localWriter.writeSecurityBuffer(20, arrayOfByte3);
    localWriter.writeSecurityBuffer(52, new byte[0]);
    localWriter.writeInt(60, j);
    debug("NTLM Client: Type 3 created\n", new Object[0]);
    debug(localWriter.getBytes());
    return localWriter.getBytes();
  }
  
  public String getDomain()
  {
    return domain;
  }
  
  public void dispose()
  {
    Arrays.fill(pw1, (byte)0);
    Arrays.fill(pw2, (byte)0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\ntlm\Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */