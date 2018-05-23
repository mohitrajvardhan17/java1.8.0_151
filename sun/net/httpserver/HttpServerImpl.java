package sun.net.httpserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class HttpServerImpl
  extends HttpServer
{
  ServerImpl server;
  
  HttpServerImpl()
    throws IOException
  {
    this(new InetSocketAddress(80), 0);
  }
  
  HttpServerImpl(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    server = new ServerImpl(this, "http", paramInetSocketAddress, paramInt);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\HttpServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */