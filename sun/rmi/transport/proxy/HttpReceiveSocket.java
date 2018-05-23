package sun.rmi.transport.proxy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HttpReceiveSocket
  extends WrappedSocket
  implements RMISocketInfo
{
  private boolean headerSent = false;
  
  public HttpReceiveSocket(Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    super(paramSocket, paramInputStream, paramOutputStream);
    in = new HttpInputStream(paramInputStream != null ? paramInputStream : paramSocket.getInputStream());
    out = (paramOutputStream != null ? paramOutputStream : paramSocket.getOutputStream());
  }
  
  public boolean isReusable()
  {
    return false;
  }
  
  public InetAddress getInetAddress()
  {
    return null;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (!headerSent)
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(out);
      localDataOutputStream.writeBytes("HTTP/1.0 200 OK\r\n");
      localDataOutputStream.flush();
      headerSent = true;
      out = new HttpOutputStream(out);
    }
    return out;
  }
  
  public synchronized void close()
    throws IOException
  {
    getOutputStream().close();
    socket.close();
  }
  
  public String toString()
  {
    return "HttpReceive" + socket.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\HttpReceiveSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */