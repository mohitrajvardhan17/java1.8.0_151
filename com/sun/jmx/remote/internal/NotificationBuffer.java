package com.sun.jmx.remote.internal;

import javax.management.remote.NotificationResult;

public abstract interface NotificationBuffer
{
  public abstract NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
    throws InterruptedException;
  
  public abstract void dispose();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\NotificationBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */