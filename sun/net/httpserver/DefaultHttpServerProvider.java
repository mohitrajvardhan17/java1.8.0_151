package sun.net.httpserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;

public class DefaultHttpServerProvider
  extends HttpServerProvider
{
  public DefaultHttpServerProvider() {}
  
  public HttpServer createHttpServer(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    return new HttpServerImpl(paramInetSocketAddress, paramInt);
  }
  
  public HttpsServer createHttpsServer(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    return new HttpsServerImpl(paramInetSocketAddress, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\DefaultHttpServerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */