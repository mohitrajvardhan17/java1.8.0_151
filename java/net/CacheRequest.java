package java.net;

import java.io.IOException;
import java.io.OutputStream;

public abstract class CacheRequest
{
  public CacheRequest() {}
  
  public abstract OutputStream getBody()
    throws IOException;
  
  public abstract void abort();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\CacheRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */