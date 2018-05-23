package com.sun.xml.internal.messaging.saaj.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ByteInputStream
  extends ByteArrayInputStream
{
  private static final byte[] EMPTY_ARRAY = new byte[0];
  
  public ByteInputStream()
  {
    this(EMPTY_ARRAY, 0);
  }
  
  public ByteInputStream(byte[] paramArrayOfByte, int paramInt)
  {
    super(paramArrayOfByte, 0, paramInt);
  }
  
  public ByteInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] getBytes()
  {
    return buf;
  }
  
  public int getCount()
  {
    return count;
  }
  
  public void close()
    throws IOException
  {
    reset();
  }
  
  public void setBuf(byte[] paramArrayOfByte)
  {
    buf = paramArrayOfByte;
    pos = 0;
    count = paramArrayOfByte.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\ByteInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */