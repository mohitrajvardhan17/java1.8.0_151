package sun.net.httpserver;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class HttpContextImpl
  extends HttpContext
{
  private String path;
  private String protocol;
  private HttpHandler handler;
  private Map<String, Object> attributes = new HashMap();
  private ServerImpl server;
  private LinkedList<Filter> sfilters = new LinkedList();
  private LinkedList<Filter> ufilters = new LinkedList();
  private Authenticator authenticator;
  private AuthFilter authfilter;
  
  HttpContextImpl(String paramString1, String paramString2, HttpHandler paramHttpHandler, ServerImpl paramServerImpl)
  {
    if ((paramString2 == null) || (paramString1 == null) || (paramString2.length() < 1) || (paramString2.charAt(0) != '/')) {
      throw new IllegalArgumentException("Illegal value for path or protocol");
    }
    protocol = paramString1.toLowerCase();
    path = paramString2;
    if ((!protocol.equals("http")) && (!protocol.equals("https"))) {
      throw new IllegalArgumentException("Illegal value for protocol");
    }
    handler = paramHttpHandler;
    server = paramServerImpl;
    authfilter = new AuthFilter(null);
    sfilters.add(authfilter);
  }
  
  public HttpHandler getHandler()
  {
    return handler;
  }
  
  public void setHandler(HttpHandler paramHttpHandler)
  {
    if (paramHttpHandler == null) {
      throw new NullPointerException("Null handler parameter");
    }
    if (handler != null) {
      throw new IllegalArgumentException("handler already set");
    }
    handler = paramHttpHandler;
  }
  
  public String getPath()
  {
    return path;
  }
  
  public HttpServer getServer()
  {
    return server.getWrapper();
  }
  
  ServerImpl getServerImpl()
  {
    return server;
  }
  
  public String getProtocol()
  {
    return protocol;
  }
  
  public Map<String, Object> getAttributes()
  {
    return attributes;
  }
  
  public List<Filter> getFilters()
  {
    return ufilters;
  }
  
  List<Filter> getSystemFilters()
  {
    return sfilters;
  }
  
  public Authenticator setAuthenticator(Authenticator paramAuthenticator)
  {
    Authenticator localAuthenticator = authenticator;
    authenticator = paramAuthenticator;
    authfilter.setAuthenticator(paramAuthenticator);
    return localAuthenticator;
  }
  
  public Authenticator getAuthenticator()
  {
    return authenticator;
  }
  
  Logger getLogger()
  {
    return server.getLogger();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\HttpContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */