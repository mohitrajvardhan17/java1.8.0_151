package javax.management;

public class MBeanServerNotification
  extends Notification
{
  private static final long serialVersionUID = 2876477500475969677L;
  public static final String REGISTRATION_NOTIFICATION = "JMX.mbean.registered";
  public static final String UNREGISTRATION_NOTIFICATION = "JMX.mbean.unregistered";
  private final ObjectName objectName;
  
  public MBeanServerNotification(String paramString, Object paramObject, long paramLong, ObjectName paramObjectName)
  {
    super(paramString, paramObject, paramLong);
    objectName = paramObjectName;
  }
  
  public ObjectName getMBeanName()
  {
    return objectName;
  }
  
  public String toString()
  {
    return super.toString() + "[mbeanName=" + objectName + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanServerNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */