package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;

@Exported
public abstract class PeerAddressChangeNotification
  implements Notification
{
  protected PeerAddressChangeNotification() {}
  
  public abstract SocketAddress address();
  
  public abstract Association association();
  
  public abstract AddressChangeEvent event();
  
  @Exported
  public static enum AddressChangeEvent
  {
    ADDR_AVAILABLE,  ADDR_UNREACHABLE,  ADDR_REMOVED,  ADDR_ADDED,  ADDR_MADE_PRIMARY,  ADDR_CONFIRMED;
    
    private AddressChangeEvent() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\PeerAddressChangeNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */