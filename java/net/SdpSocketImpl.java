package java.net;

import java.io.IOException;
import sun.net.sdp.SdpSupport;

class SdpSocketImpl
  extends PlainSocketImpl
{
  SdpSocketImpl() {}
  
  protected void create(boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {
      throw new UnsupportedOperationException("Must be a stream socket");
    }
    fd = SdpSupport.createSocket();
    if (socket != null) {
      socket.setCreated();
    }
    if (serverSocket != null) {
      serverSocket.setCreated();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SdpSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */