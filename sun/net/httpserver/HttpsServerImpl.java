package sun.net.httpserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class HttpsServerImpl
  extends HttpsServer
{
  ServerImpl server;
  
  HttpsServerImpl()
    throws IOException
  {
    this(new InetSocketAddress(443), 0);
  }
  
  HttpsServerImpl(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    server = new ServerImpl(this, "https", paramInetSocketAddress, paramInt);
  }
  
  public void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator)
  {
    server.setHttpsConfigurator(paramHttpsConfigurator);
  }
  
  public HttpsConfigurator getHttpsConfigurator()
  {
    return server.getHttpsConfigurator();
  }
  
  public void bind(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    server.bind(paramInetSocketAddress, paramInt);
  }
  
  public void start()
  {
    server.start();
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    server.setExecutor(paramExecutor);
  }
  
  public Executor getExecutor()
  {
    return server.getExecutor();
  }
  
  public void stop(int paramInt)
  {
    server.stop(paramInt);
  }
  
  public HttpContextImpl createContext(String paramString, HttpHandler paramHttpHandler)
  {
    return server.createContext(paramString, paramHttpHandler);
  }
  
  public HttpContextImpl createContext(String paramString)
  {
    return server.createContext(paramString);
  }
  
  public void removeContext(String paramString)
    throws IllegalArgumentException
  {
    server.removeContext(paramString);
  }
  
  public void removeContext(HttpContext paramHttpContext)
    throws IllegalArgumentException
  {
    server.removeContext(paramHttpContext);
  }
  
  public InetSocketAddress getAddress()
  {
    return server.getAddress();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\HttpsServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */