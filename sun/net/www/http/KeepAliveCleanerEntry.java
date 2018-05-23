package sun.net.www.http;

class KeepAliveCleanerEntry
{
  KeepAliveStream kas;
  HttpClient hc;
  
  public KeepAliveCleanerEntry(KeepAliveStream paramKeepAliveStream, HttpClient paramHttpClient)
  {
    kas = paramKeepAliveStream;
    hc = paramHttpClient;
  }
  
  protected KeepAliveStream getKeepAliveStream()
  {
    return kas;
  }
  
  protected HttpClient getHttpClient()
  {
    return hc;
  }
  
  protected void setQueuedForCleanup()
  {
    kas.queuedForCleanup = true;
  }
  
  protected boolean getQueuedForCleanup()
  {
    return kas.queuedForCleanup;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\KeepAliveCleanerEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */