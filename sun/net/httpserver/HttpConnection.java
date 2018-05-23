package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

class HttpConnection
{
  HttpContextImpl context;
  SSLEngine engine;
  SSLContext sslContext;
  SSLStreams sslStreams;
  InputStream i;
  InputStream raw;
  OutputStream rawout;
  SocketChannel chan;
  SelectionKey selectionKey;
  String protocol;
  long time;
  volatile long creationTime;
  volatile long rspStartedTime;
  int remaining;
  boolean closed = false;
  Logger logger;
  volatile State state;
  
  public String toString()
  {
    String str = null;
    if (chan != null) {
      str = chan.toString();
    }
    return str;
  }
  
  HttpConnection() {}
  
  void setChannel(SocketChannel paramSocketChannel)
  {
    chan = paramSocketChannel;
  }
  
  void setContext(HttpContextImpl paramHttpContextImpl)
  {
    context = paramHttpContextImpl;
  }
  
  State getState()
  {
    return state;
  }
  
  void setState(State paramState)
  {
    state = paramState;
  }
  
  void setParameters(InputStream paramInputStream1, OutputStream paramOutputStream, SocketChannel paramSocketChannel, SSLEngine paramSSLEngine, SSLStreams paramSSLStreams, SSLContext paramSSLContext, String paramString, HttpContextImpl paramHttpContextImpl, InputStream paramInputStream2)
  {
    context = paramHttpContextImpl;
    i = paramInputStream1;
    rawout = paramOutputStream;
    raw = paramInputStream2;
    protocol = paramString;
    engine = paramSSLEngine;
    chan = paramSocketChannel;
    sslContext = paramSSLContext;
    sslStreams = paramSSLStreams;
    logger = paramHttpContextImpl.getLogger();
  }
  
  SocketChannel getChannel()
  {
    return chan;
  }
  
  synchronized void close()
  {
    if (closed) {
      return;
    }
    closed = true;
    if ((logger != null) && (chan != null)) {
      logger.finest("Closing connection: " + chan.toString());
    }
    if (!chan.isOpen())
    {
      ServerImpl.dprint("Channel already closed");
      return;
    }
    try
    {
      if (raw != null) {
        raw.close();
      }
    }
    catch (IOException localIOException1)
    {
      ServerImpl.dprint(localIOException1);
    }
    try
    {
      if (rawout != null) {
        rawout.close();
      }
    }
    catch (IOException localIOException2)
    {
      ServerImpl.dprint(localIOException2);
    }
    try
    {
      if (sslStreams != null) {
        sslStreams.close();
      }
    }
    catch (IOException localIOException3)
    {
      ServerImpl.dprint(localIOException3);
    }
    try
    {
      chan.close();
    }
    catch (IOException localIOException4)
    {
      ServerImpl.dprint(localIOException4);
    }
  }
  
  void setRemaining(int paramInt)
  {
    remaining = paramInt;
  }
  
  int getRemaining()
  {
    return remaining;
  }
  
  SelectionKey getSelectionKey()
  {
    return selectionKey;
  }
  
  InputStream getInputStream()
  {
    return i;
  }
  
  OutputStream getRawOutputStream()
  {
    return rawout;
  }
  
  String getProtocol()
  {
    return protocol;
  }
  
  SSLEngine getSSLEngine()
  {
    return engine;
  }
  
  SSLContext getSSLContext()
  {
    return sslContext;
  }
  
  HttpContextImpl getHttpContext()
  {
    return context;
  }
  
  public static enum State
  {
    IDLE,  REQUEST,  RESPONSE;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\HttpConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */