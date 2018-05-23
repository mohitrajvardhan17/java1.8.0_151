package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.management.Notification;

public class TargetedNotification
  implements Serializable
{
  private static final long serialVersionUID = 7676132089779300926L;
  private Notification notif;
  private Integer id;
  
  public TargetedNotification(Notification paramNotification, Integer paramInteger)
  {
    validate(paramNotification, paramInteger);
    notif = paramNotification;
    id = paramInteger;
  }
  
  public Notification getNotification()
  {
    return notif;
  }
  
  public Integer getListenerID()
  {
    return id;
  }
  
  public String toString()
  {
    return "{" + notif + ", " + id + "}";
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      validate(notif, id);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new InvalidObjectException(localIllegalArgumentException.getMessage());
    }
  }
  
  private static void validate(Notification paramNotification, Integer paramInteger)
    throws IllegalArgumentException
  {
    if (paramNotification == null) {
      throw new IllegalArgumentException("Invalid notification: null");
    }
    if (paramInteger == null) {
      throw new IllegalArgumentException("Invalid listener ID: null");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\TargetedNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */