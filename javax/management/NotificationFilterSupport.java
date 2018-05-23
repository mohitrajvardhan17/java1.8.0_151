package javax.management;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class NotificationFilterSupport
  implements NotificationFilter
{
  private static final long serialVersionUID = 6579080007561786969L;
  private List<String> enabledTypes = new Vector();
  
  public NotificationFilterSupport() {}
  
  public synchronized boolean isNotificationEnabled(Notification paramNotification)
  {
    String str1 = paramNotification.getType();
    if (str1 == null) {
      return false;
    }
    try
    {
      Iterator localIterator = enabledTypes.iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str1.startsWith(str2)) {
          return true;
        }
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      return false;
    }
    return false;
  }
  
  public synchronized void enableType(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("The prefix cannot be null.");
    }
    if (!enabledTypes.contains(paramString)) {
      enabledTypes.add(paramString);
    }
  }
  
  public synchronized void disableType(String paramString)
  {
    enabledTypes.remove(paramString);
  }
  
  public synchronized void disableAllTypes()
  {
    enabledTypes.clear();
  }
  
  public synchronized Vector<String> getEnabledTypes()
  {
    return (Vector)enabledTypes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotificationFilterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */