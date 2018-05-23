package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.util.HashSet;

class MembershipKeyImpl
  extends MembershipKey
{
  private final MulticastChannel ch;
  private final InetAddress group;
  private final NetworkInterface interf;
  private final InetAddress source;
  private volatile boolean valid = true;
  private Object stateLock = new Object();
  private HashSet<InetAddress> blockedSet;
  
  private MembershipKeyImpl(MulticastChannel paramMulticastChannel, InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
  {
    ch = paramMulticastChannel;
    group = paramInetAddress1;
    interf = paramNetworkInterface;
    source = paramInetAddress2;
  }
  
  public boolean isValid()
  {
    return valid;
  }
  
  void invalidate()
  {
    valid = false;
  }
  
  public void drop()
  {
    ((DatagramChannelImpl)ch).drop(this);
  }
  
  public MulticastChannel channel()
  {
    return ch;
  }
  
  public InetAddress group()
  {
    return group;
  }
  
  public NetworkInterface networkInterface()
  {
    return interf;
  }
  
  public InetAddress sourceAddress()
  {
    return source;
  }
  
  public MembershipKey block(InetAddress paramInetAddress)
    throws IOException
  {
    if (source != null) {
      throw new IllegalStateException("key is source-specific");
    }
    synchronized (stateLock)
    {
      if ((blockedSet != null) && (blockedSet.contains(paramInetAddress))) {
        return this;
      }
      ((DatagramChannelImpl)ch).block(this, paramInetAddress);
      if (blockedSet == null) {
        blockedSet = new HashSet();
      }
      blockedSet.add(paramInetAddress);
    }
    return this;
  }
  
  public MembershipKey unblock(InetAddress paramInetAddress)
  {
    synchronized (stateLock)
    {
      if ((blockedSet == null) || (!blockedSet.contains(paramInetAddress))) {
        throw new IllegalStateException("not blocked");
      }
      ((DatagramChannelImpl)ch).unblock(this, paramInetAddress);
      blockedSet.remove(paramInetAddress);
    }
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    localStringBuilder.append('<');
    localStringBuilder.append(group.getHostAddress());
    localStringBuilder.append(',');
    localStringBuilder.append(interf.getName());
    if (source != null)
    {
      localStringBuilder.append(',');
      localStringBuilder.append(source.getHostAddress());
    }
    localStringBuilder.append('>');
    return localStringBuilder.toString();
  }
  
  static class Type4
    extends MembershipKeyImpl
  {
    private final int groupAddress;
    private final int interfAddress;
    private final int sourceAddress;
    
    Type4(MulticastChannel paramMulticastChannel, InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2, int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInetAddress1, paramNetworkInterface, paramInetAddress2, null);
      groupAddress = paramInt1;
      interfAddress = paramInt2;
      sourceAddress = paramInt3;
    }
    
    int groupAddress()
    {
      return groupAddress;
    }
    
    int interfaceAddress()
    {
      return interfAddress;
    }
    
    int source()
    {
      return sourceAddress;
    }
  }
  
  static class Type6
    extends MembershipKeyImpl
  {
    private final byte[] groupAddress;
    private final int index;
    private final byte[] sourceAddress;
    
    Type6(MulticastChannel paramMulticastChannel, InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
    {
      super(paramInetAddress1, paramNetworkInterface, paramInetAddress2, null);
      groupAddress = paramArrayOfByte1;
      index = paramInt;
      sourceAddress = paramArrayOfByte2;
    }
    
    byte[] groupAddress()
    {
      return groupAddress;
    }
    
    int index()
    {
      return index;
    }
    
    byte[] source()
    {
      return sourceAddress;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\MembershipKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */