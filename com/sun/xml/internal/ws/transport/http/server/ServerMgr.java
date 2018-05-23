package com.sun.xml.internal.ws.transport.http.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

final class ServerMgr
{
  private static final ServerMgr serverMgr = new ServerMgr();
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
  private final Map<InetSocketAddress, ServerState> servers = new HashMap();
  
  private ServerMgr() {}
  
  static ServerMgr getInstance()
  {
    return serverMgr;
  }
  
  HttpContext createContext(String paramString)
  {
    try
    {
      URL localURL = new URL(paramString);
      int i = localURL.getPort();
      if (i == -1) {
        i = localURL.getDefaultPort();
      }
      InetSocketAddress localInetSocketAddress = new InetSocketAddress(localURL.getHost(), i);
      ServerState localServerState;
      synchronized (servers)
      {
        localServerState = (ServerState)servers.get(localInetSocketAddress);
        if (localServerState == null)
        {
          logger.fine("Creating new HTTP Server at " + localInetSocketAddress);
          localHttpServer = HttpServer.create(localInetSocketAddress, 0);
          localHttpServer.setExecutor(Executors.newCachedThreadPool());
          String str = localURL.toURI().getPath();
          logger.fine("Creating HTTP Context at = " + str);
          HttpContext localHttpContext = localHttpServer.createContext(str);
          localHttpServer.start();
          localInetSocketAddress = localHttpServer.getAddress();
          logger.fine("HTTP server started = " + localInetSocketAddress);
          localServerState = new ServerState(localHttpServer, str);
          servers.put(localInetSocketAddress, localServerState);
          return localHttpContext;
        }
      }
      HttpServer localHttpServer = localServerState.getServer();
      if (localServerState.getPaths().contains(localURL.getPath()))
      {
        ??? = "Context with URL path " + localURL.getPath() + " already exists on the server " + localHttpServer.getAddress();
        logger.fine((String)???);
        throw new IllegalArgumentException((String)???);
      }
      logger.fine("Creating HTTP Context at = " + localURL.getPath());
      ??? = localHttpServer.createContext(localURL.getPath());
      localServerState.oneMoreContext(localURL.getPath());
      return (HttpContext)???;
    }
    catch (Exception localException)
    {
      throw new ServerRtException("server.rt.err", new Object[] { localException });
    }
  }
  
  void removeContext(HttpContext paramHttpContext)
  {
    InetSocketAddress localInetSocketAddress = paramHttpContext.getServer().getAddress();
    synchronized (servers)
    {
      ServerState localServerState = (ServerState)servers.get(localInetSocketAddress);
      int i = localServerState.noOfContexts();
      if (i < 2)
      {
        ((ExecutorService)localServerState.getServer().getExecutor()).shutdown();
        localServerState.getServer().stop(0);
        servers.remove(localInetSocketAddress);
      }
      else
      {
        localServerState.getServer().removeContext(paramHttpContext);
        localServerState.oneLessContext(paramHttpContext.getPath());
      }
    }
  }
  
  private static final class ServerState
  {
    private final HttpServer server;
    private int instances;
    private Set<String> paths = new HashSet();
    
    ServerState(HttpServer paramHttpServer, String paramString)
    {
      server = paramHttpServer;
      instances = 1;
      paths.add(paramString);
    }
    
    public HttpServer getServer()
    {
      return server;
    }
    
    public void oneMoreContext(String paramString)
    {
      instances += 1;
      paths.add(paramString);
    }
    
    public void oneLessContext(String paramString)
    {
      instances -= 1;
      paths.remove(paramString);
    }
    
    public int noOfContexts()
    {
      return instances;
    }
    
    public Set<String> getPaths()
    {
      return paths;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerMgr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */