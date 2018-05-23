package sun.net.www.http;

class KeepAliveEntry
{
  HttpClient hc;
  long idleStartTime;
  
  KeepAliveEntry(HttpClient paramHttpClient, long paramLong)
  {
    hc = paramHttpClient;
    idleStartTime = paramLong;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\KeepAliveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */