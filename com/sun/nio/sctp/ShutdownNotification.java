package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class ShutdownNotification
  implements Notification
{
  protected ShutdownNotification() {}
  
  public abstract Association association();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\ShutdownNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */