package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;

public class MBeanServerNotificationFilter
  extends NotificationFilterSupport
{
  private static final long oldSerialVersionUID = 6001782699077323605L;
  private static final long newSerialVersionUID = 2605900539589789736L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("mySelectObjNameList", Vector.class), new ObjectStreamField("myDeselectObjNameList", Vector.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("selectedNames", List.class), new ObjectStreamField("deselectedNames", List.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private List<ObjectName> selectedNames = new Vector();
  private List<ObjectName> deselectedNames = null;
  
  public MBeanServerNotificationFilter()
  {
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
    enableType("JMX.mbean.registered");
    enableType("JMX.mbean.unregistered");
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
  }
  
  public synchronized void disableAllObjectNames()
  {
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
    selectedNames = new Vector();
    deselectedNames = null;
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
  }
  
  public synchronized void disableObjectName(ObjectName paramObjectName)
    throws IllegalArgumentException
  {
    if (paramObjectName == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableObjectName", paramObjectName);
    if ((selectedNames != null) && (selectedNames.size() != 0)) {
      selectedNames.remove(paramObjectName);
    }
    if ((deselectedNames != null) && (!deselectedNames.contains(paramObjectName))) {
      deselectedNames.add(paramObjectName);
    }
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableObjectName");
  }
  
  public synchronized void enableAllObjectNames()
  {
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
    selectedNames = null;
    deselectedNames = new Vector();
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
  }
  
  public synchronized void enableObjectName(ObjectName paramObjectName)
    throws IllegalArgumentException
  {
    if (paramObjectName == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableObjectName", paramObjectName);
    if ((deselectedNames != null) && (deselectedNames.size() != 0)) {
      deselectedNames.remove(paramObjectName);
    }
    if ((selectedNames != null) && (!selectedNames.contains(paramObjectName))) {
      selectedNames.add(paramObjectName);
    }
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableObjectName");
  }
  
  public synchronized Vector<ObjectName> getEnabledObjectNames()
  {
    if (selectedNames != null) {
      return new Vector(selectedNames);
    }
    return null;
  }
  
  public synchronized Vector<ObjectName> getDisabledObjectNames()
  {
    if (deselectedNames != null) {
      return new Vector(deselectedNames);
    }
    return null;
  }
  
  public synchronized boolean isNotificationEnabled(Notification paramNotification)
    throws IllegalArgumentException
  {
    if (paramNotification == null)
    {
      str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", paramNotification);
    String str = paramNotification.getType();
    Vector localVector = getEnabledTypes();
    if (!localVector.contains(str))
    {
      JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "Type not selected, exiting");
      return false;
    }
    MBeanServerNotification localMBeanServerNotification = (MBeanServerNotification)paramNotification;
    ObjectName localObjectName = localMBeanServerNotification.getMBeanName();
    boolean bool = false;
    if (selectedNames != null)
    {
      if (selectedNames.size() == 0)
      {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "No ObjectNames selected, exiting");
        return false;
      }
      bool = selectedNames.contains(localObjectName);
      if (!bool)
      {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not in selected list, exiting");
        return false;
      }
    }
    if (!bool)
    {
      if (deselectedNames == null)
      {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not selected, and all names deselected, exiting");
        return false;
      }
      if (deselectedNames.contains(localObjectName))
      {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName explicitly not selected, exiting");
        return false;
      }
    }
    JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName selected, exiting");
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      selectedNames = ((List)Util.cast(localGetField.get("mySelectObjNameList", null)));
      if (localGetField.defaulted("mySelectObjNameList")) {
        throw new NullPointerException("mySelectObjNameList");
      }
      deselectedNames = ((List)Util.cast(localGetField.get("myDeselectObjNameList", null)));
      if (localGetField.defaulted("myDeselectObjNameList")) {
        throw new NullPointerException("myDeselectObjNameList");
      }
    }
    else
    {
      paramObjectInputStream.defaultReadObject();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("mySelectObjNameList", selectedNames);
      localPutField.put("myDeselectObjNameList", deselectedNames);
      paramObjectOutputStream.writeFields();
    }
    else
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  static
  {
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      compat = (str != null) && (str.equals("1.0"));
    }
    catch (Exception localException) {}
    if (compat)
    {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 6001782699077323605L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 2605900539589789736L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\MBeanServerNotificationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */