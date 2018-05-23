package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
import java.util.Objects;

public class MBeanNotificationInfo
  extends MBeanFeatureInfo
  implements Cloneable
{
  static final long serialVersionUID = -3888371564530107064L;
  private static final String[] NO_TYPES = new String[0];
  static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
  private String[] types;
  private final transient boolean arrayGettersSafe;
  
  public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    this(paramArrayOfString, paramString1, paramString2, null);
  }
  
  public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramDescriptor);
    types = ((paramArrayOfString != null) && (paramArrayOfString.length > 0) ? (String[])paramArrayOfString.clone() : NO_TYPES);
    arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanNotificationInfo.class);
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public String[] getNotifTypes()
  {
    if (types.length == 0) {
      return NO_TYPES;
    }
    return (String[])types.clone();
  }
  
  private String[] fastGetNotifTypes()
  {
    if (arrayGettersSafe) {
      return types;
    }
    return getNotifTypes();
  }
  
  public String toString()
  {
    return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", notifTypes=" + Arrays.asList(fastGetNotifTypes()) + ", descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanNotificationInfo)) {
      return false;
    }
    MBeanNotificationInfo localMBeanNotificationInfo = (MBeanNotificationInfo)paramObject;
    return (Objects.equals(localMBeanNotificationInfo.getName(), getName())) && (Objects.equals(localMBeanNotificationInfo.getDescription(), getDescription())) && (Objects.equals(localMBeanNotificationInfo.getDescriptor(), getDescriptor())) && (Arrays.equals(localMBeanNotificationInfo.fastGetNotifTypes(), fastGetNotifTypes()));
  }
  
  public int hashCode()
  {
    int i = getName().hashCode();
    for (int j = 0; j < types.length; j++) {
      i ^= types[j].hashCode();
    }
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String[] arrayOfString = (String[])localGetField.get("types", null);
    types = ((arrayOfString != null) && (arrayOfString.length != 0) ? (String[])arrayOfString.clone() : NO_TYPES);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */