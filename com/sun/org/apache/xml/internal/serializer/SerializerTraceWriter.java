package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

final class SerializerTraceWriter
  extends Writer
  implements WriterChain
{
  private final Writer m_writer;
  private final SerializerTrace m_tracer;
  private int buf_length;
  private byte[] buf;
  private int count;
  
  private void setBufferSize(int paramInt)
  {
    buf = new byte[paramInt + 3];
    buf_length = paramInt;
    count = 0;
  }
  
  public SerializerTraceWriter(Writer paramWriter, SerializerTrace paramSerializerTrace)
  {
    m_writer = paramWriter;
    m_tracer = paramSerializerTrace;
    setBufferSize(1024);
  }
  
  private void flushBuffer()
    throws IOException
  {
    if (count > 0)
    {
      char[] arrayOfChar = new char[count];
      for (int i = 0; i < count; i++) {
        arrayOfChar[i] = ((char)buf[i]);
      }
      if (m_tracer != null) {
        m_tracer.fireGenerateEvent(12, arrayOfChar, 0, arrayOfChar.length);
      }
      count = 0;
    }
  }
  
  public void flush()
    throws IOException
  {
    if (m_writer != null) {
      m_writer.flush();
    }
    flushBuffer();
  }
  
  public void close()
    throws IOException
  {
    if (m_writer != null) {
      m_writer.close();
    }
    flushBuffer();
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (m_writer != null) {
      m_writer.write(paramInt);
    }
    if (count >= buf_length) {
      flushBuffer();
    }
    if (paramInt < 128)
    {
      buf[(count++)] = ((byte)paramInt);
    }
    else if (paramInt < 2048)
    {
      buf[(count++)] = ((byte)(192 + (paramInt >> 6)));
      buf[(count++)] = ((byte)(128 + (paramInt & 0x3F)));
    }
    else
    {
      buf[(count++)] = ((byte)(224 + (paramInt >> 12)));
      buf[(count++)] = ((byte)(128 + (paramInt >> 6 & 0x3F)));
      buf[(count++)] = ((byte)(128 + (paramInt & 0x3F)));
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (m_writer != null) {
      m_writer.write(paramArrayOfChar, paramInt1, paramInt2);
    }
    int i = (paramInt2 << 1) + paramInt2;
    if (i >= buf_length)
    {
      flushBuffer();
      setBufferSize(2 * i);
    }
    if (i > buf_length - count) {
      flushBuffer();
    }
    int j = paramInt2 + paramInt1;
    for (int k = paramInt1; k < j; k++)
    {
      int m = paramArrayOfChar[k];
      if (m < 128)
      {
        buf[(count++)] = ((byte)m);
      }
      else if (m < 2048)
      {
        buf[(count++)] = ((byte)(192 + (m >> 6)));
        buf[(count++)] = ((byte)(128 + (m & 0x3F)));
      }
      else
      {
        buf[(count++)] = ((byte)(224 + (m >> 12)));
        buf[(count++)] = ((byte)(128 + (m >> 6 & 0x3F)));
        buf[(count++)] = ((byte)(128 + (m & 0x3F)));
      }
    }
  }
  
  public void write(String paramString)
    throws IOException
  {
    if (m_writer != null) {
      m_writer.write(paramString);
    }
    int i = paramString.length();
    int j = (i << 1) + i;
    if (j >= buf_length)
    {
      flushBuffer();
      setBufferSize(2 * j);
    }
    if (j > buf_length - count) {
      flushBuffer();
    }
    for (int k = 0; k < i; k++)
    {
      int m = paramString.charAt(k);
      if (m < 128)
      {
        buf[(count++)] = ((byte)m);
      }
      else if (m < 2048)
      {
        buf[(count++)] = ((byte)(192 + (m >> 6)));
        buf[(count++)] = ((byte)(128 + (m & 0x3F)));
      }
      else
      {
        buf[(count++)] = ((byte)(224 + (m >> 12)));
        buf[(count++)] = ((byte)(128 + (m >> 6 & 0x3F)));
        buf[(count++)] = ((byte)(128 + (m & 0x3F)));
      }
    }
  }
  
  public Writer getWriter()
  {
    return m_writer;
  }
  
  public OutputStream getOutputStream()
  {
    OutputStream localOutputStream = null;
    if ((m_writer instanceof WriterChain)) {
      localOutputStream = ((WriterChain)m_writer).getOutputStream();
    }
    return localOutputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\SerializerTraceWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */