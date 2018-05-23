package jdk.internal.util.xml.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import jdk.internal.util.xml.XMLStreamException;

public class XMLWriter
{
  private Writer _writer;
  private CharsetEncoder _encoder = null;
  
  public XMLWriter(OutputStream paramOutputStream, String paramString, Charset paramCharset)
    throws XMLStreamException
  {
    _encoder = paramCharset.newEncoder();
    try
    {
      _writer = getWriter(paramOutputStream, paramString, paramCharset);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new XMLStreamException(localUnsupportedEncodingException);
    }
  }
  
  public boolean canEncode(char paramChar)
  {
    if (_encoder == null) {
      return false;
    }
    return _encoder.canEncode(paramChar);
  }
  
  public void write(String paramString)
    throws XMLStreamException
  {
    try
    {
      _writer.write(paramString.toCharArray());
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException("I/O error", localIOException);
    }
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    try
    {
      _writer.write(paramString, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException("I/O error", localIOException);
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    try
    {
      _writer.write(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException("I/O error", localIOException);
    }
  }
  
  void write(int paramInt)
    throws XMLStreamException
  {
    try
    {
      _writer.write(paramInt);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException("I/O error", localIOException);
    }
  }
  
  void flush()
    throws XMLStreamException
  {
    try
    {
      _writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  void close()
    throws XMLStreamException
  {
    try
    {
      _writer.close();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  private void nl()
    throws XMLStreamException
  {
    String str = System.getProperty("line.separator");
    try
    {
      _writer.write(str);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException("I/O error", localIOException);
    }
  }
  
  private Writer getWriter(OutputStream paramOutputStream, String paramString, Charset paramCharset)
    throws XMLStreamException, UnsupportedEncodingException
  {
    if (paramCharset != null) {
      return new OutputStreamWriter(new BufferedOutputStream(paramOutputStream), paramCharset);
    }
    return new OutputStreamWriter(new BufferedOutputStream(paramOutputStream), paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\XMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */