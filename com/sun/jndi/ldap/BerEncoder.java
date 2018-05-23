package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerEncoder
  extends Ber
{
  private int curSeqIndex;
  private int[] seqOffset;
  private static final int INITIAL_SEQUENCES = 16;
  private static final int DEFAULT_BUFSIZE = 1024;
  private static final int BUF_GROWTH_FACTOR = 8;
  
  public BerEncoder()
  {
    this(1024);
  }
  
  public BerEncoder(int paramInt)
  {
    buf = new byte[paramInt];
    bufsize = paramInt;
    offset = 0;
    seqOffset = new int[16];
    curSeqIndex = 0;
  }
  
  public void reset()
  {
    while (offset > 0) {
      buf[(--offset)] = 0;
    }
    while (curSeqIndex > 0) {
      seqOffset[(--curSeqIndex)] = 0;
    }
  }
  
  public int getDataLen()
  {
    return offset;
  }
  
  public byte[] getBuf()
  {
    if (curSeqIndex != 0) {
      throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs.");
    }
    return buf;
  }
  
  public byte[] getTrimmedBuf()
  {
    int i = getDataLen();
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(getBuf(), 0, arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public void beginSeq(int paramInt)
  {
    if (curSeqIndex >= seqOffset.length)
    {
      int[] arrayOfInt = new int[seqOffset.length * 2];
      for (int i = 0; i < seqOffset.length; i++) {
        arrayOfInt[i] = seqOffset[i];
      }
      seqOffset = arrayOfInt;
    }
    encodeByte(paramInt);
    seqOffset[curSeqIndex] = offset;
    ensureFreeBytes(3);
    offset += 3;
    curSeqIndex += 1;
  }
  
  public void endSeq()
    throws Ber.EncodeException
  {
    curSeqIndex -= 1;
    if (curSeqIndex < 0) {
      throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs.");
    }
    int i = seqOffset[curSeqIndex] + 3;
    int j = offset - i;
    if (j <= 127)
    {
      shiftSeqData(i, j, -2);
      buf[seqOffset[curSeqIndex]] = ((byte)j);
    }
    else if (j <= 255)
    {
      shiftSeqData(i, j, -1);
      buf[seqOffset[curSeqIndex]] = -127;
      buf[(seqOffset[curSeqIndex] + 1)] = ((byte)j);
    }
    else if (j <= 65535)
    {
      buf[seqOffset[curSeqIndex]] = -126;
      buf[(seqOffset[curSeqIndex] + 1)] = ((byte)(j >> 8));
      buf[(seqOffset[curSeqIndex] + 2)] = ((byte)j);
    }
    else if (j <= 16777215)
    {
      shiftSeqData(i, j, 1);
      buf[seqOffset[curSeqIndex]] = -125;
      buf[(seqOffset[curSeqIndex] + 1)] = ((byte)(j >> 16));
      buf[(seqOffset[curSeqIndex] + 2)] = ((byte)(j >> 8));
      buf[(seqOffset[curSeqIndex] + 3)] = ((byte)j);
    }
    else
    {
      throw new Ber.EncodeException("SEQUENCE too long");
    }
  }
  
  private void shiftSeqData(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 > 0) {
      ensureFreeBytes(paramInt3);
    }
    System.arraycopy(buf, paramInt1, buf, paramInt1 + paramInt3, paramInt2);
    offset += paramInt3;
  }
  
  public void encodeByte(int paramInt)
  {
    ensureFreeBytes(1);
    buf[(offset++)] = ((byte)paramInt);
  }
  
  public void encodeInt(int paramInt)
  {
    encodeInt(paramInt, 2);
  }
  
  public void encodeInt(int paramInt1, int paramInt2)
  {
    int i = -8388608;
    int j = 4;
    while ((((paramInt1 & i) == 0) || ((paramInt1 & i) == i)) && (j > 1))
    {
      j--;
      paramInt1 <<= 8;
    }
    encodeInt(paramInt1, paramInt2, j);
  }
  
  private void encodeInt(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 > 4) {
      throw new IllegalArgumentException("BER encode error: INTEGER too long.");
    }
    ensureFreeBytes(2 + paramInt3);
    buf[(offset++)] = ((byte)paramInt2);
    buf[(offset++)] = ((byte)paramInt3);
    int i = -16777216;
    while (paramInt3-- > 0)
    {
      buf[(offset++)] = ((byte)((paramInt1 & i) >> 24));
      paramInt1 <<= 8;
    }
  }
  
  public void encodeBoolean(boolean paramBoolean)
  {
    encodeBoolean(paramBoolean, 1);
  }
  
  public void encodeBoolean(boolean paramBoolean, int paramInt)
  {
    ensureFreeBytes(3);
    buf[(offset++)] = ((byte)paramInt);
    buf[(offset++)] = 1;
    buf[(offset++)] = (paramBoolean ? -1 : 0);
  }
  
  public void encodeString(String paramString, boolean paramBoolean)
    throws Ber.EncodeException
  {
    encodeString(paramString, 4, paramBoolean);
  }
  
  public void encodeString(String paramString, int paramInt, boolean paramBoolean)
    throws Ber.EncodeException
  {
    encodeByte(paramInt);
    int i = 0;
    byte[] arrayOfByte = null;
    int j;
    if (paramString == null) {
      j = 0;
    } else if (paramBoolean) {
      try
      {
        arrayOfByte = paramString.getBytes("UTF8");
        j = arrayOfByte.length;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException1)
      {
        throw new Ber.EncodeException("UTF8 not available on platform");
      }
    } else {
      try
      {
        arrayOfByte = paramString.getBytes("8859_1");
        j = arrayOfByte.length;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException2)
      {
        throw new Ber.EncodeException("8859_1 not available on platform");
      }
    }
    encodeLength(j);
    ensureFreeBytes(j);
    while (i < j) {
      buf[(offset++)] = arrayOfByte[(i++)];
    }
  }
  
  public void encodeOctetString(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws Ber.EncodeException
  {
    encodeByte(paramInt1);
    encodeLength(paramInt3);
    if (paramInt3 > 0)
    {
      ensureFreeBytes(paramInt3);
      System.arraycopy(paramArrayOfByte, paramInt2, buf, offset, paramInt3);
      offset += paramInt3;
    }
  }
  
  public void encodeOctetString(byte[] paramArrayOfByte, int paramInt)
    throws Ber.EncodeException
  {
    encodeOctetString(paramArrayOfByte, paramInt, 0, paramArrayOfByte.length);
  }
  
  private void encodeLength(int paramInt)
    throws Ber.EncodeException
  {
    ensureFreeBytes(4);
    if (paramInt < 128)
    {
      buf[(offset++)] = ((byte)paramInt);
    }
    else if (paramInt <= 255)
    {
      buf[(offset++)] = -127;
      buf[(offset++)] = ((byte)paramInt);
    }
    else if (paramInt <= 65535)
    {
      buf[(offset++)] = -126;
      buf[(offset++)] = ((byte)(paramInt >> 8));
      buf[(offset++)] = ((byte)(paramInt & 0xFF));
    }
    else if (paramInt <= 16777215)
    {
      buf[(offset++)] = -125;
      buf[(offset++)] = ((byte)(paramInt >> 16));
      buf[(offset++)] = ((byte)(paramInt >> 8));
      buf[(offset++)] = ((byte)(paramInt & 0xFF));
    }
    else
    {
      throw new Ber.EncodeException("string too long");
    }
  }
  
  public void encodeStringArray(String[] paramArrayOfString, boolean paramBoolean)
    throws Ber.EncodeException
  {
    if (paramArrayOfString == null) {
      return;
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      encodeString(paramArrayOfString[i], paramBoolean);
    }
  }
  
  private void ensureFreeBytes(int paramInt)
  {
    if (bufsize - offset < paramInt)
    {
      int i = bufsize * 8;
      if (i - offset < paramInt) {
        i += paramInt;
      }
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(buf, 0, arrayOfByte, 0, offset);
      buf = arrayOfByte;
      bufsize = i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\BerEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */