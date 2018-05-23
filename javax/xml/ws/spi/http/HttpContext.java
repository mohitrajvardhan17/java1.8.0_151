package javax.xml.ws.spi.http;

import java.util.Set;

public abstract class HttpContext
{
  protected HttpHandler handler;
  
  public HttpContext() {}
  
  public void setHandler(HttpHandler paramHttpHandler)
  {
    handler = paramHttpHandler;
  }
  
  public abstract String getPath();
  
  public abstract Object getAttribute(String paramString);
  
  public abstract Set<String> getAttributeNames();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\spi\http\HttpContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */