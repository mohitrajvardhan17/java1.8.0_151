package sun.net;

import java.net.URL;
import java.util.EventObject;

public class ProgressEvent
  extends EventObject
{
  private URL url;
  private String contentType;
  private String method;
  private long progress;
  private long expected;
  private ProgressSource.State state;
  
  public ProgressEvent(ProgressSource paramProgressSource, URL paramURL, String paramString1, String paramString2, ProgressSource.State paramState, long paramLong1, long paramLong2)
  {
    super(paramProgressSource);
    url = paramURL;
    method = paramString1;
    contentType = paramString2;
    progress = paramLong1;
    expected = paramLong2;
    state = paramState;
  }
  
  public URL getURL()
  {
    return url;
  }
  
  public String getMethod()
  {
    return method;
  }
  
  public String getContentType()
  {
    return contentType;
  }
  
  public long getProgress()
  {
    return progress;
  }
  
  public long getExpected()
  {
    return expected;
  }
  
  public ProgressSource.State getState()
  {
    return state;
  }
  
  public String toString()
  {
    return getClass().getName() + "[url=" + url + ", method=" + method + ", state=" + state + ", content-type=" + contentType + ", progress=" + progress + ", expected=" + expected + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ProgressEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */