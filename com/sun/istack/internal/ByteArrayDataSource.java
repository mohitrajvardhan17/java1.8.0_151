package com.sun.istack.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public final class ByteArrayDataSource
  implements DataSource
{
  private final String contentType;
  private final byte[] buf;
  private final int len;
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, String paramString)
  {
    this(paramArrayOfByte, paramArrayOfByte.length, paramString);
  }
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, int paramInt, String paramString)
  {
    buf = paramArrayOfByte;
    len = paramInt;
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
    return new ByteArrayInputStream(buf, 0, len);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\ByteArrayDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */