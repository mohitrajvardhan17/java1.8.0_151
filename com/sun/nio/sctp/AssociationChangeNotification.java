package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class AssociationChangeNotification
  implements Notification
{
  protected AssociationChangeNotification() {}
  
  public abstract Association association();
  
  public abstract AssocChangeEvent event();
  
  @Exported
  public static enum AssocChangeEvent
  {
    COMM_UP,  COMM_LOST,  RESTART,  SHUTDOWN,  CANT_START;
    
    private AssocChangeEvent() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\AssociationChangeNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */