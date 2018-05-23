package com.sun.xml.internal.ws.developer;

import java.net.URL;
import javax.activation.DataSource;

public abstract class StreamingDataHandler
  extends com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler
{
  private String hrefCid;
  
  public StreamingDataHandler(Object paramObject, String paramString)
  {
    super(paramObject, paramString);
  }
  
  public StreamingDataHandler(URL paramURL)
  {
    super(paramURL);
  }
  
  public StreamingDataHandler(DataSource paramDataSource)
  {
    super(paramDataSource);
  }
  
  public String getHrefCid()
  {
    return hrefCid;
  }
  
  public void setHrefCid(String paramString)
  {
    hrefCid = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\StreamingDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */