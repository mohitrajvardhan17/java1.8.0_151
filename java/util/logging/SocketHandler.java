package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class SocketHandler
  extends StreamHandler
{
  private Socket sock;
  private String host;
  private int port;
  
  private void configure()
  {
    LogManager localLogManager = LogManager.getLogManager();
    String str = getClass().getName();
    setLevel(localLogManager.getLevelProperty(str + ".level", Level.ALL));
    setFilter(localLogManager.getFilterProperty(str + ".filter", null));
    setFormatter(localLogManager.getFormatterProperty(str + ".formatter", new XMLFormatter()));
    try
    {
      setEncoding(localLogManager.getStringProperty(str + ".encoding", null));
    }
    catch (Exception localException1)
    {
      try
      {
        setEncoding(null);
      }
      catch (Exception localException2) {}
    }
    port = localLogManager.getIntProperty(str + ".port", 0);
    host = localLogManager.getStringProperty(str + ".host", null);
  }
  
  public SocketHandler()
    throws IOException
  {
    sealed = false;
    configure();
    try
    {
      connect();
    }
    catch (IOException localIOException)
    {
      System.err.println("SocketHandler: connect failed to " + host + ":" + port);
      throw localIOException;
    }
    sealed = true;
  }
  
  public SocketHandler(String paramString, int paramInt)
    throws IOException
  {
    sealed = false;
    configure();
    sealed = true;
    port = paramInt;
    host = paramString;
    connect();
  }
  
  private void connect()
    throws IOException
  {
    if (port == 0) {
      throw new IllegalArgumentException("Bad port: " + port);
    }
    if (host == null) {
      throw new IllegalArgumentException("Null host name: " + host);
    }
    sock = new Socket(host, port);
    OutputStream localOutputStream = sock.getOutputStream();
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localOutputStream);
    setOutputStream(localBufferedOutputStream);
  }
  
  public synchronized void close()
    throws SecurityException
  {
    super.close();
    if (sock != null) {
      try
      {
        sock.close();
      }
      catch (IOException localIOException) {}
    }
    sock = null;
  }
  
  public synchronized void publish(LogRecord paramLogRecord)
  {
    if (!isLoggable(paramLogRecord)) {
      return;
    }
    super.publish(paramLogRecord);
    flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\SocketHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */