package sun.rmi.transport.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Connection;
import sun.rmi.transport.proxy.RMISocketInfo;

public class TCPConnection
  implements Connection
{
  private Socket socket;
  private Channel channel;
  private InputStream in = null;
  private OutputStream out = null;
  private long expiration = Long.MAX_VALUE;
  private long lastuse = Long.MIN_VALUE;
  private long roundtrip = 5L;
  
  TCPConnection(TCPChannel paramTCPChannel, Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream)
  {
    socket = paramSocket;
    channel = paramTCPChannel;
    in = paramInputStream;
    out = paramOutputStream;
  }
  
  TCPConnection(TCPChannel paramTCPChannel, InputStream paramInputStream, OutputStream paramOutputStream)
  {
    this(paramTCPChannel, null, paramInputStream, paramOutputStream);
  }
  
  TCPConnection(TCPChannel paramTCPChannel, Socket paramSocket)
  {
    this(paramTCPChannel, paramSocket, null, null);
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (out == null) {
      out = new BufferedOutputStream(socket.getOutputStream());
    }
    return out;
  }
  
  public void releaseOutputStream()
    throws IOException
  {
    if (out != null) {
      out.flush();
    }
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (in == null) {
      in = new BufferedInputStream(socket.getInputStream());
    }
    return in;
  }
  
  public void releaseInputStream() {}
  
  public boolean isReusable()
  {
    if ((socket != null) && ((socket instanceof RMISocketInfo))) {
      return ((RMISocketInfo)socket).isReusable();
    }
    return true;
  }
  
  void setExpiration(long paramLong)
  {
    expiration = paramLong;
  }
  
  void setLastUseTime(long paramLong)
  {
    lastuse = paramLong;
  }
  
  boolean expired(long paramLong)
  {
    return expiration <= paramLong;
  }
  
  public boolean isDead()
  {
    long l = System.currentTimeMillis();
    if ((roundtrip > 0L) && (l < lastuse + roundtrip)) {
      return false;
    }
    InputStream localInputStream;
    OutputStream localOutputStream;
    try
    {
      localInputStream = getInputStream();
      localOutputStream = getOutputStream();
    }
    catch (IOException localIOException1)
    {
      return true;
    }
    int i = 0;
    try
    {
      localOutputStream.write(82);
      localOutputStream.flush();
      i = localInputStream.read();
    }
    catch (IOException localIOException2)
    {
      TCPTransport.tcpLog.log(Log.VERBOSE, "exception: ", localIOException2);
      TCPTransport.tcpLog.log(Log.BRIEF, "server ping failed");
      return true;
    }
    if (i == 83)
    {
      roundtrip = ((System.currentTimeMillis() - l) * 2L);
      return false;
    }
    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
      TCPTransport.tcpLog.log(Log.BRIEF, "server protocol error: ping response = " + i);
    }
    return true;
  }
  
  public void close()
    throws IOException
  {
    TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
    if (socket != null)
    {
      socket.close();
    }
    else
    {
      in.close();
      out.close();
    }
  }
  
  public Channel getChannel()
  {
    return channel;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\TCPConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */