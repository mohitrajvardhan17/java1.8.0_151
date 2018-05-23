package com.sun.xml.internal.bind.v2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.transform.stream.StreamSource;

public final class DataSourceSource
  extends StreamSource
{
  private final DataSource source;
  private final String charset;
  private Reader r;
  private InputStream is;
  
  public DataSourceSource(DataHandler paramDataHandler)
    throws MimeTypeParseException
  {
    this(paramDataHandler.getDataSource());
  }
  
  public DataSourceSource(DataSource paramDataSource)
    throws MimeTypeParseException
  {
    source = paramDataSource;
    String str = paramDataSource.getContentType();
    if (str == null)
    {
      charset = null;
    }
    else
    {
      MimeType localMimeType = new MimeType(str);
      charset = localMimeType.getParameter("charset");
    }
  }
  
  public void setReader(Reader paramReader)
  {
    throw new UnsupportedOperationException();
  }
  
  public void setInputStream(InputStream paramInputStream)
  {
    throw new UnsupportedOperationException();
  }
  
  public Reader getReader()
  {
    try
    {
      if (charset == null) {
        return null;
      }
      if (r == null) {
        r = new InputStreamReader(source.getInputStream(), charset);
      }
      return r;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public InputStream getInputStream()
  {
    try
    {
      if (charset != null) {
        return null;
      }
      if (is == null) {
        is = source.getInputStream();
      }
      return is;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public DataSource getDataSource()
  {
    return source;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\DataSourceSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */