package com.sun.org.apache.xml.internal.security.utils;

import java.io.OutputStream;

public class UnsyncByteArrayOutputStream
  extends OutputStream
{
  private static final int INITIAL_SIZE = 8192;
  private byte[] buf = new byte[' '];
  private int size = 8192;
  private int pos = 0;
  
  public UnsyncByteArrayOutputStream() {}
  
  public void write(byte[] paramArrayOfByte)
  {
    if (Integer.MAX_VALUE - pos < paramArrayOfByte.length) {
      throw new OutOfMemoryError();
    }
    int i = pos + paramArrayOfByte.length;
    if (i > size) {
      expandSize(i);
    }
    System.arraycopy(paramArrayOfByte, 0, buf, pos, paramArrayOfByte.length);
    pos = i;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (Integer.MAX_VALUE - pos < paramInt2) {
      throw new OutOfMemoryError();
    }
    int i = pos + paramInt2;
    if (i > size) {
      expandSize(i);
    }
    System.arraycopy(paramArrayOfByte, paramInt1, buf, pos, paramInt2);
    pos = i;
  }
  
  public void write(int paramInt)
  {
    if (Integer.MAX_VALUE - pos == 0) {
      throw new OutOfMemoryError();
    }
    int i = pos + 1;
    if (i > size) {
      expandSize(i);
    }
    buf[(pos++)] = ((byte)paramInt);
  }
  
  public byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[pos];
    System.arraycopy(buf, 0, arrayOfByte, 0, pos);
    return arrayOfByte;
  }
  
  public void reset()
  {
    pos = 0;
  }
  
  private void expandSize(int paramInt)
  {
    int i = size;
    while (paramInt > i)
    {
      i <<= 1;
      if (i < 0) {
        i = Integer.MAX_VALUE;
      }
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(buf, 0, arrayOfByte, 0, pos);
    buf = arrayOfByte;
    size = i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\UnsyncByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */