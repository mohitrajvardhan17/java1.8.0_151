package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

public class HexOutputStream
  extends OutputStream
{
  private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  private StringWriter writer;
  
  public HexOutputStream(StringWriter paramStringWriter)
  {
    writer = paramStringWriter;
  }
  
  public synchronized void write(int paramInt)
    throws IOException
  {
    writer.write(hex[(paramInt >> 4 & 0xF)]);
    writer.write(hex[(paramInt >> 0 & 0xF)]);
  }
  
  public synchronized void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\HexOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */