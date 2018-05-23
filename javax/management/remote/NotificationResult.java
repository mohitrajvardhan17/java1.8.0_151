package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class NotificationResult
  implements Serializable
{
  private static final long serialVersionUID = 1191800228721395279L;
  private long earliestSequenceNumber;
  private long nextSequenceNumber;
  private TargetedNotification[] targetedNotifications;
  
  public NotificationResult(long paramLong1, long paramLong2, TargetedNotification[] paramArrayOfTargetedNotification)
  {
    validate(paramArrayOfTargetedNotification, paramLong1, paramLong2);
    earliestSequenceNumber = paramLong1;
    nextSequenceNumber = paramLong2;
    targetedNotifications = (paramArrayOfTargetedNotification.length == 0 ? paramArrayOfTargetedNotification : (TargetedNotification[])paramArrayOfTargetedNotification.clone());
  }
  
  public long getEarliestSequenceNumber()
  {
    return earliestSequenceNumber;
  }
  
  public long getNextSequenceNumber()
  {
    return nextSequenceNumber;
  }
  
  public TargetedNotification[] getTargetedNotifications()
  {
    return targetedNotifications.length == 0 ? targetedNotifications : (TargetedNotification[])targetedNotifications.clone();
  }
  
  public String toString()
  {
    return "NotificationResult: earliest=" + getEarliestSequenceNumber() + "; next=" + getNextSequenceNumber() + "; nnotifs=" + getTargetedNotifications().length;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      validate(targetedNotifications, earliestSequenceNumber, nextSequenceNumber);
      targetedNotifications = (targetedNotifications.length == 0 ? targetedNotifications : (TargetedNotification[])targetedNotifications.clone());
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new InvalidObjectException(localIllegalArgumentException.getMessage());
    }
  }
  
  private static void validate(TargetedNotification[] paramArrayOfTargetedNotification, long paramLong1, long paramLong2)
    throws IllegalArgumentException
  {
    if (paramArrayOfTargetedNotification == null) {
      throw new IllegalArgumentException("Notifications null");
    }
    if ((paramLong1 < 0L) || (paramLong2 < 0L)) {
      throw new IllegalArgumentException("Bad sequence numbers");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\NotificationResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */