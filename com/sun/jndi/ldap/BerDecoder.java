package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerDecoder
  extends Ber
{
  private int origOffset;
  
  public BerDecoder(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    buf = paramArrayOfByte;
    bufsize = paramInt2;
    origOffset = paramInt1;
    reset();
  }
  
  public void reset()
  {
    offset = origOffset;
  }
  
  public int getParsePosition()
  {
    return offset;
  }
  
  public int parseLength()
    throws Ber.DecodeException
  {
    int i = parseByte();
    if ((i & 0x80) == 128)
    {
      i &= 0x7F;
      if (i == 0) {
        throw new Ber.DecodeException("Indefinite length not supported");
      }
      if (i > 4) {
        throw new Ber.DecodeException("encoding too long");
      }
      if (bufsize - offset < i) {
        throw new Ber.DecodeException("Insufficient data");
      }
      int j = 0;
      for (int k = 0; k < i; k++) {
        j = (j << 8) + (buf[(offset++)] & 0xFF);
      }
      if (j < 0) {
        throw new Ber.DecodeException("Invalid length bytes");
      }
      return j;
    }
    return i;
  }
  
  public int parseSeq(int[] paramArrayOfInt)
    throws Ber.DecodeException
  {
    int i = parseByte();
    int j = parseLength();
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = j;
    }
    return i;
  }
  
  void seek(int paramInt)
    throws Ber.DecodeException
  {
    if ((offset + paramInt > bufsize) || (offset + paramInt < 0)) {
      throw new Ber.DecodeException("array index out of bounds");
    }
    offset += paramInt;
  }
  
  public int parseByte()
    throws Ber.DecodeException
  {
    if (bufsize - offset < 1) {
      throw new Ber.DecodeException("Insufficient data");
    }
    return buf[(offset++)] & 0xFF;
  }
  
  public int peekByte()
    throws Ber.DecodeException
  {
    if (bufsize - offset < 1) {
      throw new Ber.DecodeException("Insufficient data");
    }
    return buf[offset] & 0xFF;
  }
  
  public boolean parseBoolean()
    throws Ber.DecodeException
  {
    return parseIntWithTag(1) != 0;
  }
  
  public int parseEnumeration()
    throws Ber.DecodeException
  {
    return parseIntWithTag(10);
  }
  
  public int parseInt()
    throws Ber.DecodeException
  {
    return parseIntWithTag(2);
  }
  
  private int parseIntWithTag(int paramInt)
    throws Ber.DecodeException
  {
    if (parseByte() != paramInt) {
      throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(buf[(offset - 1)] & 0xFF) + " (expected tag " + Integer.toString(paramInt) + ")");
    }
    int i = parseLength();
    if (i > 4) {
      throw new Ber.DecodeException("INTEGER too long");
    }
    if (i > bufsize - offset) {
      throw new Ber.DecodeException("Insufficient data");
    }
    int j = buf[(offset++)];
    int k = 0;
    k = j & 0x7F;
    for (int m = 1; m < i; m++)
    {
      k <<= 8;
      k |= buf[(offset++)] & 0xFF;
    }
    if ((j & 0x80) == 128) {
      k = -k;
    }
    return k;
  }
  
  public String parseString(boolean paramBoolean)
    throws Ber.DecodeException
  {
    return parseStringWithTag(4, paramBoolean, null);
  }
  
  public String parseStringWithTag(int paramInt, boolean paramBoolean, int[] paramArrayOfInt)
    throws Ber.DecodeException
  {
    int j = offset;
    int i;
    if ((i = parseByte()) != paramInt) {
      throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString((byte)i) + " (expected tag " + paramInt + ")");
    }
    int k = parseLength();
    if (k > bufsize - offset) {
      throw new Ber.DecodeException("Insufficient data");
    }
    String str;
    if (k == 0)
    {
      str = "";
    }
    else
    {
      byte[] arrayOfByte = new byte[k];
      System.arraycopy(buf, offset, arrayOfByte, 0, k);
      if (paramBoolean) {
        try
        {
          str = new String(arrayOfByte, "UTF8");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException1)
        {
          throw new Ber.DecodeException("UTF8 not available on platform");
        }
      } else {
        try
        {
          str = new String(arrayOfByte, "8859_1");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException2)
        {
          throw new Ber.DecodeException("8859_1 not available on platform");
        }
      }
      offset += k;
    }
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = (offset - j);
    }
    return str;
  }
  
  public byte[] parseOctetString(int paramInt, int[] paramArrayOfInt)
    throws Ber.DecodeException
  {
    int i = offset;
    int j;
    if ((j = parseByte()) != paramInt) {
      throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(j) + " (expected tag " + Integer.toString(paramInt) + ")");
    }
    int k = parseLength();
    if (k > bufsize - offset) {
      throw new Ber.DecodeException("Insufficient data");
    }
    byte[] arrayOfByte = new byte[k];
    if (k > 0)
    {
      System.arraycopy(buf, offset, arrayOfByte, 0, k);
      offset += k;
    }
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = (offset - i);
    }
    return arrayOfByte;
  }
  
  public int bytesLeft()
  {
    return bufsize - offset;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\BerDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */