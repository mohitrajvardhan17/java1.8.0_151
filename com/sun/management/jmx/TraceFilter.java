package com.sun.management.jmx;

import javax.management.Notification;
import javax.management.NotificationFilter;

@Deprecated
public class TraceFilter
  implements NotificationFilter
{
  protected int levels;
  protected int types;
  
  public TraceFilter(int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    levels = paramInt1;
    types = paramInt2;
  }
  
  public boolean isNotificationEnabled(Notification paramNotification)
  {
    return false;
  }
  
  public int getLevels()
  {
    return levels;
  }
  
  public int getTypes()
  {
    return types;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\jmx\TraceFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */