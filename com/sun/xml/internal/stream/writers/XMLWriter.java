package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import java.io.IOException;
import java.io.Writer;

public class XMLWriter
  extends Writer
{
  private Writer writer;
  private int size;
  private XMLStringBuffer buffer = new XMLStringBuffer(12288);
  private static final int THRESHHOLD_LENGTH = 4096;
  private static final boolean DEBUG = false;
  
  public XMLWriter(Writer paramWriter)
  {
    this(paramWriter, 4096);
  }
  
  public XMLWriter(Writer paramWriter, int paramInt)
  {
    writer = paramWriter;
    size = paramInt;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    ensureOpen();
    buffer.append((char)paramInt);
    conditionalWrite();
  }
  
  public void write(char[] paramArrayOfChar)
    throws IOException
  {
    write(paramArrayOfChar, 0, paramArrayOfChar.length);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if (paramInt2 > size)
    {
      writeBufferedData();
      writer.write(paramArrayOfChar, paramInt1, paramInt2);
    }
    else
    {
      buffer.append(paramArrayOfChar, paramInt1, paramInt2);
      conditionalWrite();
    }
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    write(paramString.toCharArray(), paramInt1, paramInt2);
  }
  
  public void write(String paramString)
    throws IOException
  {
    if (paramString.length() > size)
    {
      writeBufferedData();
      writer.write(paramString);
    }
    else
    {
      buffer.append(paramString);
      conditionalWrite();
    }
  }
  
  public void close()
    throws IOException
  {
    if (writer == null) {
      return;
    }
    flush();
    writer.close();
    writer = null;
  }
  
  public void flush()
    throws IOException
  {
    ensureOpen();
    writeBufferedData();
    writer.flush();
  }
  
  public void reset()
  {
    writer = null;
    buffer.clear();
    size = 4096;
  }
  
  public void setWriter(Writer paramWriter)
  {
    writer = paramWriter;
    buffer.clear();
    size = 4096;
  }
  
  public void setWriter(Writer paramWriter, int paramInt)
  {
    writer = paramWriter;
    size = paramInt;
  }
  
  protected Writer getWriter()
  {
    return writer;
  }
  
  private void conditionalWrite()
    throws IOException
  {
    if (buffer.length > size) {
      writeBufferedData();
    }
  }
  
  private void writeBufferedData()
    throws IOException
  {
    writer.write(buffer.ch, buffer.offset, buffer.length);
    buffer.clear();
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (writer == null) {
      throw new IOException("Stream closed");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\writers\XMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */