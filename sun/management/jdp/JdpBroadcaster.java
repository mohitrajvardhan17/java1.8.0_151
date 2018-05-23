package sun.management.jdp;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;

public final class JdpBroadcaster
{
  private final InetAddress addr;
  private final int port;
  private final DatagramChannel channel;
  
  public JdpBroadcaster(InetAddress paramInetAddress1, InetAddress paramInetAddress2, int paramInt1, int paramInt2)
    throws IOException, JdpException
  {
    addr = paramInetAddress1;
    port = paramInt1;
    StandardProtocolFamily localStandardProtocolFamily = (paramInetAddress1 instanceof Inet6Address) ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
    channel = DatagramChannel.open(localStandardProtocolFamily);
    channel.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf(true));
    channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, Integer.valueOf(paramInt2));
    if (paramInetAddress2 != null)
    {
      NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress(paramInetAddress2);
      try
      {
        channel.bind(new InetSocketAddress(paramInetAddress2, 0));
      }
      catch (UnsupportedAddressTypeException localUnsupportedAddressTypeException)
      {
        throw new JdpException("Unable to bind to source address");
      }
      channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, localNetworkInterface);
    }
  }
  
  public JdpBroadcaster(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException, JdpException
  {
    this(paramInetAddress, null, paramInt1, paramInt2);
  }
  
  public void sendPacket(JdpPacket paramJdpPacket)
    throws IOException
  {
    byte[] arrayOfByte = paramJdpPacket.getPacketData();
    ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
    channel.send(localByteBuffer, new InetSocketAddress(addr, port));
  }
  
  public void shutdown()
    throws IOException
  {
    channel.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jdp\JdpBroadcaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */