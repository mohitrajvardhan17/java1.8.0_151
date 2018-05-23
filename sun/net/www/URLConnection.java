package sun.net.www;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class URLConnection
  extends java.net.URLConnection
{
  private String contentType;
  private int contentLength = -1;
  protected MessageHeader properties = new MessageHeader();
  private static HashMap<String, Void> proxiedHosts = new HashMap();
  
  public URLConnection(URL paramURL)
  {
    super(paramURL);
  }
  
  public MessageHeader getProperties()
  {
    return properties;
  }
  
  public void setProperties(MessageHeader paramMessageHeader)
  {
    properties = paramMessageHeader;
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    if (connected) {
      throw new IllegalAccessError("Already connected");
    }
    if (paramString1 == null) {
      throw new NullPointerException("key cannot be null");
    }
    properties.set(paramString1, paramString2);
  }
  
  public void addRequestProperty(String paramString1, String paramString2)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (paramString1 == null) {
      throw new NullPointerException("key is null");
    }
  }
  
  public String getRequestProperty(String paramString)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    return null;
  }
  
  public Map<String, List<String>> getRequestProperties()
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    return Collections.emptyMap();
  }
  
  public String getHeaderField(String paramString)
  {
    try
    {
      getInputStream();
    }
    catch (Exception localException)
    {
      return null;
    }
    return properties == null ? null : properties.findValue(paramString);
  }
  
  public String getHeaderFieldKey(int paramInt)
  {
    try
    {
      getInputStream();
    }
    catch (Exception localException)
    {
      return null;
    }
    MessageHeader localMessageHeader = properties;
    return localMessageHeader == null ? null : localMessageHeader.getKey(paramInt);
  }
  
  public String getHeaderField(int paramInt)
  {
    try
    {
      getInputStream();
    }
    catch (Exception localException)
    {
      return null;
    }
    MessageHeader localMessageHeader = properties;
    return localMessageHeader == null ? null : localMessageHeader.getValue(paramInt);
  }
  
  public String getContentType()
  {
    if (contentType == null) {
      contentType = getHeaderField("content-type");
    }
    if (contentType == null)
    {
      String str1 = null;
      try
      {
        str1 = guessContentTypeFromStream(getInputStream());
      }
      catch (IOException localIOException) {}
      String str2 = properties.findValue("content-encoding");
      if (str1 == null)
      {
        str1 = properties.findValue("content-type");
        if (str1 == null) {
          if (url.getFile().endsWith("/")) {
            str1 = "text/html";
          } else {
            str1 = guessContentTypeFromName(url.getFile());
          }
        }
      }
      if ((str1 == null) || ((str2 != null) && (!str2.equalsIgnoreCase("7bit")) && (!str2.equalsIgnoreCase("8bit")) && (!str2.equalsIgnoreCase("binary")))) {
        str1 = "content/unknown";
      }
      setContentType(str1);
    }
    return contentType;
  }
  
  public void setContentType(String paramString)
  {
    contentType = paramString;
    properties.set("content-type", paramString);
  }
  
  public int getContentLength()
  {
    try
    {
      getInputStream();
    }
    catch (Exception localException1)
    {
      return -1;
    }
    int i = contentLength;
    if (i < 0) {
      try
      {
        i = Integer.parseInt(properties.findValue("content-length"));
        setContentLength(i);
      }
      catch (Exception localException2) {}
    }
    return i;
  }
  
  protected void setContentLength(int paramInt)
  {
    contentLength = paramInt;
    properties.set("content-length", String.valueOf(paramInt));
  }
  
  public boolean canCache()
  {
    return url.getFile().indexOf('?') < 0;
  }
  
  public void close()
  {
    url = null;
  }
  
  public static synchronized void setProxiedHost(String paramString)
  {
    proxiedHosts.put(paramString.toLowerCase(), null);
  }
  
  public static synchronized boolean isProxiedHost(String paramString)
  {
    return proxiedHosts.containsKey(paramString.toLowerCase());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\URLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */