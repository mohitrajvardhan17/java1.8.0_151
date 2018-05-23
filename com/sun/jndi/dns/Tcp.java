package com.sun.jndi.dns;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

class Tcp
{
  private Socket sock;
  InputStream in;
  OutputStream out;
  
  Tcp(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    sock = new Socket(paramInetAddress, paramInt);
    sock.setTcpNoDelay(true);
    out = new BufferedOutputStream(sock.getOutputStream());
    in = new BufferedInputStream(sock.getInputStream());
  }
  
  void close()
    throws IOException
  {
    sock.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\Tcp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */