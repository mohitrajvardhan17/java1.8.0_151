package com.sun.xml.internal.org.jvnet.staxex;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Base64EncoderStream
  extends FilterOutputStream
{
  private byte[] buffer = new byte[3];
  private int bufsize = 0;
  private XMLStreamWriter outWriter;
  private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  
  public Base64EncoderStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public Base64EncoderStream(XMLStreamWriter paramXMLStreamWriter, OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    outWriter = paramXMLStreamWriter;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    buffer[(bufsize++)] = ((byte)paramInt);
    if (bufsize == 3)
    {
      encode();
      bufsize = 0;
    }
  }
  
  public void flush()
    throws IOException
  {
    if (bufsize > 0)
    {
      encode();
      bufsize = 0;
    }
    out.flush();
    try
    {
      outWriter.flush();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, localXMLStreamException);
      throw new IOException(localXMLStreamException);
    }
  }
  
  public void close()
    throws IOException
  {
    flush();
    out.close();
  }
  
  private void encode()
    throws IOException
  {
    char[] arrayOfChar = new char[4];
    int i;
    int j;
    int k;
    if (bufsize == 1)
    {
      i = buffer[0];
      j = 0;
      k = 0;
      arrayOfChar[0] = pem_array[(i >>> 2 & 0x3F)];
      arrayOfChar[1] = pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))];
      arrayOfChar[2] = '=';
      arrayOfChar[3] = '=';
    }
    else if (bufsize == 2)
    {
      i = buffer[0];
      j = buffer[1];
      k = 0;
      arrayOfChar[0] = pem_array[(i >>> 2 & 0x3F)];
      arrayOfChar[1] = pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))];
      arrayOfChar[2] = pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))];
      arrayOfChar[3] = '=';
    }
    else
    {
      i = buffer[0];
      j = buffer[1];
      k = buffer[2];
      arrayOfChar[0] = pem_array[(i >>> 2 & 0x3F)];
      arrayOfChar[1] = pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))];
      arrayOfChar[2] = pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))];
      arrayOfChar[3] = pem_array[(k & 0x3F)];
    }
    try
    {
      outWriter.writeCharacters(arrayOfChar, 0, 4);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, localXMLStreamException);
      throw new IOException(localXMLStreamException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\Base64EncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */