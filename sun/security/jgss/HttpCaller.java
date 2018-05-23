package sun.security.jgss;

import sun.net.www.protocol.http.HttpCallerInfo;

public class HttpCaller
  extends GSSCaller
{
  private final HttpCallerInfo hci;
  
  public HttpCaller(HttpCallerInfo paramHttpCallerInfo)
  {
    super("HTTP_CLIENT");
    hci = paramHttpCallerInfo;
  }
  
  public HttpCallerInfo info()
  {
    return hci;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\HttpCaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */