package java.nio.channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

public abstract class MembershipKey
{
  protected MembershipKey() {}
  
  public abstract boolean isValid();
  
  public abstract void drop();
  
  public abstract MembershipKey block(InetAddress paramInetAddress)
    throws IOException;
  
  public abstract MembershipKey unblock(InetAddress paramInetAddress);
  
  public abstract MulticastChannel channel();
  
  public abstract InetAddress group();
  
  public abstract NetworkInterface networkInterface();
  
  public abstract InetAddress sourceAddress();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\MembershipKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */