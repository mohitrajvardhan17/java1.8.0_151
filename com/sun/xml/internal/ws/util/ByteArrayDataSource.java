package com.sun.xml.internal.ws.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public final class ByteArrayDataSource
  implements DataSource
{
  private final String contentType;
  private final byte[] buf;
  private final int start;
  private final int len;
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, String paramString)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length, paramString);
  }
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, int paramInt, String paramString)
  {
    this(paramArrayOfByte, 0, paramInt, paramString);
  }
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
  {
    buf = paramArrayOfByte;
    start = paramInt1;
    len = paramInt2;
    contentType = paramString;
  }
  
  public String getContentType()
  {
    if (contentType == null) {
      return "application/octet-stream";
    }
    return contentType;
  }
  
  public InputStream getInputStream()
  {
    return new ByteArrayInputStream(buf, start, len);
  }
  
  public String getName()
  {
    return null;
  }
  
  public OutputStream getOutputStream()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\ByteArrayDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */