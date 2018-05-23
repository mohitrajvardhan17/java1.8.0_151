package javax.management;

import java.util.Vector;

public class AttributeChangeNotificationFilter
  implements NotificationFilter
{
  private static final long serialVersionUID = -6347317584796410029L;
  private Vector<String> enabledAttributes = new Vector();
  
  public AttributeChangeNotificationFilter() {}
  
  public synchronized boolean isNotificationEnabled(Notification paramNotification)
  {
    String str1 = paramNotification.getType();
    if ((str1 == null) || (!str1.equals("jmx.attribute.change")) || (!(paramNotification instanceof AttributeChangeNotification))) {
      return false;
    }
    String str2 = ((AttributeChangeNotification)paramNotification).getAttributeName();
    return enabledAttributes.contains(str2);
  }
  
  public synchronized void enableAttribute(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("The name cannot be null.");
    }
    if (!enabledAttributes.contains(paramString)) {
      enabledAttributes.addElement(paramString);
    }
  }
  
  public synchronized void disableAttribute(String paramString)
  {
    enabledAttributes.removeElement(paramString);
  }
  
  public synchronized void disableAllAttributes()
  {
    enabledAttributes.removeAllElements();
  }
  
  public synchronized Vector<String> getEnabledAttributes()
  {
    return enabledAttributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\AttributeChangeNotificationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */