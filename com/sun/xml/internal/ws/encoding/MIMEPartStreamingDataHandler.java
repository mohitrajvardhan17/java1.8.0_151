package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MIMEPartStreamingDataHandler
  extends StreamingDataHandler
{
  private final StreamingDataSource ds = (StreamingDataSource)getDataSource();
  
  public MIMEPartStreamingDataHandler(MIMEPart paramMIMEPart)
  {
    super(new StreamingDataSource(paramMIMEPart));
  }
  
  public InputStream readOnce()
    throws IOException
  {
    return ds.readOnce();
  }
  
  public void moveTo(File paramFile)
    throws IOException
  {
    ds.moveTo(paramFile);
  }
  
  public void close()
    throws IOException
  {
    ds.close();
  }
  
  private static final class MyIOException
    extends IOException
  {
    private final Exception linkedException;
    
    MyIOException(Exception paramException)
    {
      linkedException = paramException;
    }
    
    public Throwable getCause()
    {
      return linkedException;
    }
  }
  
  private static final class StreamingDataSource
    implements DataSource
  {
    private final MIMEPart part;
    
    StreamingDataSource(MIMEPart paramMIMEPart)
    {
      part = paramMIMEPart;
    }
    
    public InputStream getInputStream()
      throws IOException
    {
      return part.read();
    }
    
    InputStream readOnce()
      throws IOException
    {
      try
      {
        return part.readOnce();
      }
      catch (Exception localException)
      {
        throw new MIMEPartStreamingDataHandler.MyIOException(localException);
      }
    }
    
    void moveTo(File paramFile)
      throws IOException
    {
      part.moveTo(paramFile);
    }
    
    public OutputStream getOutputStream()
      throws IOException
    {
      return null;
    }
    
    public String getContentType()
    {
      return part.getContentType();
    }
    
    public String getName()
    {
      return "";
    }
    
    public void close()
      throws IOException
    {
      part.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\MIMEPartStreamingDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */