package com.sun.xml.internal.org.jvnet.staxex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class ByteArrayOutputStreamEx
  extends ByteArrayOutputStream
{
  public ByteArrayOutputStreamEx() {}
  
  public ByteArrayOutputStreamEx(int paramInt)
  {
    super(paramInt);
  }
  
  public void set(Base64Data paramBase64Data, String paramString)
  {
    paramBase64Data.set(buf, count, paramString);
  }
  
  public byte[] getBuffer()
  {
    return buf;
  }
  
  public void readFrom(InputStream paramInputStream)
    throws IOException
  {
    for (;;)
    {
      if (count == buf.length)
      {
        byte[] arrayOfByte = new byte[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfByte, 0, buf.length);
        buf = arrayOfByte;
      }
      int i = paramInputStream.read(buf, count, buf.length - count);
      if (i < 0) {
        return;
      }
      count += i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\ByteArrayOutputStreamEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */