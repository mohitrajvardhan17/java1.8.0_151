package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoderStream
  extends FilterOutputStream
{
  private byte[] buffer;
  private int bufsize = 0;
  private boolean wrotePrefix = false;
  protected String name;
  protected int mode;
  
  public UUEncoderStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, "encoder.buf", 644);
  }
  
  public UUEncoderStream(OutputStream paramOutputStream, String paramString)
  {
    this(paramOutputStream, paramString, 644);
  }
  
  public UUEncoderStream(OutputStream paramOutputStream, String paramString, int paramInt)
  {
    super(paramOutputStream);
    name = paramString;
    mode = paramInt;
    buffer = new byte[45];
  }
  
  public void setNameMode(String paramString, int paramInt)
  {
    name = paramString;
    mode = paramInt;
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
    if (bufsize == 45)
    {
      writePrefix();
      encode();
      bufsize = 0;
    }
  }
  
  public void flush()
    throws IOException
  {
    if (bufsize > 0)
    {
      writePrefix();
      encode();
    }
    writeSuffix();
    out.flush();
  }
  
  public void close()
    throws IOException
  {
    flush();
    out.close();
  }
  
  private void writePrefix()
    throws IOException
  {
    if (!wrotePrefix)
    {
      PrintStream localPrintStream = new PrintStream(out);
      localPrintStream.println("begin " + mode + " " + name);
      localPrintStream.flush();
      wrotePrefix = true;
    }
  }
  
  private void writeSuffix()
    throws IOException
  {
    PrintStream localPrintStream = new PrintStream(out);
    localPrintStream.println(" \nend");
    localPrintStream.flush();
  }
  
  private void encode()
    throws IOException
  {
    int i3 = 0;
    out.write((bufsize & 0x3F) + 32);
    while (i3 < bufsize)
    {
      int i = buffer[(i3++)];
      int j;
      int k;
      if (i3 < bufsize)
      {
        j = buffer[(i3++)];
        if (i3 < bufsize) {
          k = buffer[(i3++)];
        } else {
          k = 1;
        }
      }
      else
      {
        j = 1;
        k = 1;
      }
      int m = i >>> 2 & 0x3F;
      int n = i << 4 & 0x30 | j >>> 4 & 0xF;
      int i1 = j << 2 & 0x3C | k >>> 6 & 0x3;
      int i2 = k & 0x3F;
      out.write(m + 32);
      out.write(n + 32);
      out.write(i1 + 32);
      out.write(i2 + 32);
    }
    out.write(10);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\UUEncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */