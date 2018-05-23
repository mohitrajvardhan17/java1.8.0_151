package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WSHttpHandler
  implements HttpHandler
{
  private static final String GET_METHOD = "GET";
  private static final String POST_METHOD = "POST";
  private static final String HEAD_METHOD = "HEAD";
  private static final String PUT_METHOD = "PUT";
  private static final String DELETE_METHOD = "DELETE";
  private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.http");
  private static final boolean fineTraceEnabled = LOGGER.isLoggable(Level.FINE);
  private final HttpAdapter adapter;
  private final Executor executor;
  
  public WSHttpHandler(@NotNull HttpAdapter paramHttpAdapter, @Nullable Executor paramExecutor)
  {
    assert (paramHttpAdapter != null);
    adapter = paramHttpAdapter;
    executor = paramExecutor;
  }
  
  public void handle(HttpExchange paramHttpExchange)
  {
    try
    {
      if (fineTraceEnabled) {
        LOGGER.log(Level.FINE, "Received HTTP request:{0}", paramHttpExchange.getRequestURI());
      }
      if (executor != null) {
        executor.execute(new HttpHandlerRunnable(paramHttpExchange));
      } else {
        handleExchange(paramHttpExchange);
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  private void handleExchange(HttpExchange paramHttpExchange)
    throws IOException
  {
    ServerConnectionImpl localServerConnectionImpl = new ServerConnectionImpl(adapter, paramHttpExchange);
    try
    {
      if (fineTraceEnabled) {
        LOGGER.log(Level.FINE, "Received HTTP request:{0}", paramHttpExchange.getRequestURI());
      }
      String str = paramHttpExchange.getRequestMethod();
      if ((str.equals("GET")) || (str.equals("POST")) || (str.equals("HEAD")) || (str.equals("PUT")) || (str.equals("DELETE"))) {
        adapter.handle(localServerConnectionImpl);
      } else if (LOGGER.isLoggable(Level.WARNING)) {
        LOGGER.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(str));
      }
    }
    finally
    {
      paramHttpExchange.close();
    }
  }
  
  class HttpHandlerRunnable
    implements Runnable
  {
    final HttpExchange msg;
    
    HttpHandlerRunnable(HttpExchange paramHttpExchange)
    {
      msg = paramHttpExchange;
    }
    
    public void run()
    {
      try
      {
        WSHttpHandler.this.handleExchange(msg);
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\WSHttpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */