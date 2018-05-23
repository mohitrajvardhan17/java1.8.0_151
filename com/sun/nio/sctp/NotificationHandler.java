package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract interface NotificationHandler<T>
{
  public abstract HandlerResult handleNotification(Notification paramNotification, T paramT);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\NotificationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */