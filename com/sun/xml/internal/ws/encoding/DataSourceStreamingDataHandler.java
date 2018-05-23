package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class DataSourceStreamingDataHandler
  extends StreamingDataHandler
{
  public DataSourceStreamingDataHandler(DataSource paramDataSource)
  {
    super(paramDataSource);
  }
  
  public InputStream readOnce()
    throws IOException
  {
    return getInputStream();
  }
  
  public void moveTo(File paramFile)
    throws IOException
  {
    InputStream localInputStream = getInputStream();
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    try
    {
      byte[] arrayOfByte = new byte[' '];
      int i;
      while ((i = localInputStream.read(arrayOfByte)) != -1) {
        localFileOutputStream.write(arrayOfByte, 0, i);
      }
      localInputStream.close();
    }
    finally
    {
      if (localFileOutputStream != null) {
        localFileOutputStream.close();
      }
    }
  }
  
  public void close()
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\DataSourceStreamingDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */