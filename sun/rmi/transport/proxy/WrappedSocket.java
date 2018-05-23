package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;

class WrappedSocket
  extends Socket
{
  protected Socket socket;
  protected InputStream in = null;
  protected OutputStream out = null;
  
  public WrappedSocket(Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    super((SocketImpl)null);
    socket = paramSocket;
    in = paramInputStream;
    out = paramOutputStream;
  }
  
  public InetAddress getInetAddress()
  {
    return socket.getInetAddress();
  }
  
  public InetAddress getLocalAddress()
  {
    (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InetAddress run()
      {
        return socket.getLocalAddress();
      }
    });
  }
  
  public int getPort()
  {
    return socket.getPort();
  }
  
  public int getLocalPort()
  {
    return socket.getLocalPort();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (in == null) {
      in = socket.getInputStream();
    }
    return in;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (out == null) {
      out = socket.getOutputStream();
    }
    return out;
  }
  
  public void setTcpNoDelay(boolean paramBoolean)
    throws SocketException
  {
    socket.setTcpNoDelay(paramBoolean);
  }
  
  public boolean getTcpNoDelay()
    throws SocketException
  {
    return socket.getTcpNoDelay();
  }
  
  public void setSoLinger(boolean paramBoolean, int paramInt)
    throws SocketException
  {
    socket.setSoLinger(paramBoolean, paramInt);
  }
  
  public int getSoLinger()
    throws SocketException
  {
    return socket.getSoLinger();
  }
  
  public synchronized void setSoTimeout(int paramInt)
    throws SocketException
  {
    socket.setSoTimeout(paramInt);
  }
  
  public synchronized int getSoTimeout()
    throws SocketException
  {
    return socket.getSoTimeout();
  }
  
  public synchronized void close()
    throws IOException
  {
    socket.close();
  }
  
  public String toString()
  {
    return "Wrapped" + socket.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\WrappedSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */