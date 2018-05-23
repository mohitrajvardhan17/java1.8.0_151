package sun.net;

import java.net.URL;

public class ProgressSource
{
  private URL url;
  private String method;
  private String contentType;
  private long progress = 0L;
  private long lastProgress = 0L;
  private long expected = -1L;
  private State state;
  private boolean connected = false;
  private int threshold = 8192;
  private ProgressMonitor progressMonitor;
  
  public ProgressSource(URL paramURL, String paramString)
  {
    this(paramURL, paramString, -1L);
  }
  
  public ProgressSource(URL paramURL, String paramString, long paramLong)
  {
    url = paramURL;
    method = paramString;
    contentType = "content/unknown";
    progress = 0L;
    lastProgress = 0L;
    expected = paramLong;
    state = State.NEW;
    progressMonitor = ProgressMonitor.getDefault();
    threshold = progressMonitor.getProgressUpdateThreshold();
  }
  
  public boolean connected()
  {
    if (!connected)
    {
      connected = true;
      state = State.CONNECTED;
      return false;
    }
    return true;
  }
  
  public void close()
  {
    state = State.DELETE;
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
  
  public void setContentType(String paramString)
  {
    contentType = paramString;
  }
  
  public long getProgress()
  {
    return progress;
  }
  
  public long getExpected()
  {
    return expected;
  }
  
  public State getState()
  {
    return state;
  }
  
  public void beginTracking()
  {
    progressMonitor.registerSource(this);
  }
  
  public void finishTracking()
  {
    progressMonitor.unregisterSource(this);
  }
  
  public void updateProgress(long paramLong1, long paramLong2)
  {
    lastProgress = progress;
    progress = paramLong1;
    expected = paramLong2;
    if (!connected()) {
      state = State.CONNECTED;
    } else {
      state = State.UPDATE;
    }
    if (lastProgress / threshold != progress / threshold) {
      progressMonitor.updateProgress(this);
    }
    if ((expected != -1L) && (progress >= expected) && (progress != 0L)) {
      close();
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  
  public String toString()
  {
    return getClass().getName() + "[url=" + url + ", method=" + method + ", state=" + state + ", content-type=" + contentType + ", progress=" + progress + ", expected=" + expected + "]";
  }
  
  public static enum State
  {
    NEW,  CONNECTED,  UPDATE,  DELETE;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ProgressSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */