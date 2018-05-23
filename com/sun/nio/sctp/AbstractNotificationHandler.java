package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class AbstractNotificationHandler<T>
  implements NotificationHandler<T>
{
  protected AbstractNotificationHandler() {}
  
  public HandlerResult handleNotification(Notification paramNotification, T paramT)
  {
    return HandlerResult.CONTINUE;
  }
  
  public HandlerResult handleNotification(AssociationChangeNotification paramAssociationChangeNotification, T paramT)
  {
    return HandlerResult.CONTINUE;
  }
  
  public HandlerResult handleNotification(PeerAddressChangeNotification paramPeerAddressChangeNotification, T paramT)
  {
    return HandlerResult.CONTINUE;
  }
  
  public HandlerResult handleNotification(SendFailedNotification paramSendFailedNotification, T paramT)
  {
    return HandlerResult.CONTINUE;
  }
  
  public HandlerResult handleNotification(ShutdownNotification paramShutdownNotification, T paramT)
  {
    return HandlerResult.CONTINUE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\AbstractNotificationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */