package javax.management.openmbean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

public class OpenMBeanInfoSupport
  extends MBeanInfo
  implements OpenMBeanInfo
{
  static final long serialVersionUID = 4349395935420511492L;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  
  public OpenMBeanInfoSupport(String paramString1, String paramString2, OpenMBeanAttributeInfo[] paramArrayOfOpenMBeanAttributeInfo, OpenMBeanConstructorInfo[] paramArrayOfOpenMBeanConstructorInfo, OpenMBeanOperationInfo[] paramArrayOfOpenMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo)
  {
    this(paramString1, paramString2, paramArrayOfOpenMBeanAttributeInfo, paramArrayOfOpenMBeanConstructorInfo, paramArrayOfOpenMBeanOperationInfo, paramArrayOfMBeanNotificationInfo, (Descriptor)null);
  }
  
  public OpenMBeanInfoSupport(String paramString1, String paramString2, OpenMBeanAttributeInfo[] paramArrayOfOpenMBeanAttributeInfo, OpenMBeanConstructorInfo[] paramArrayOfOpenMBeanConstructorInfo, OpenMBeanOperationInfo[] paramArrayOfOpenMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, attributeArray(paramArrayOfOpenMBeanAttributeInfo), constructorArray(paramArrayOfOpenMBeanConstructorInfo), operationArray(paramArrayOfOpenMBeanOperationInfo), paramArrayOfMBeanNotificationInfo == null ? null : (MBeanNotificationInfo[])paramArrayOfMBeanNotificationInfo.clone(), paramDescriptor);
  }
  
  private static MBeanAttributeInfo[] attributeArray(OpenMBeanAttributeInfo[] paramArrayOfOpenMBeanAttributeInfo)
  {
    if (paramArrayOfOpenMBeanAttributeInfo == null) {
      return null;
    }
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = new MBeanAttributeInfo[paramArrayOfOpenMBeanAttributeInfo.length];
    System.arraycopy(paramArrayOfOpenMBeanAttributeInfo, 0, arrayOfMBeanAttributeInfo, 0, paramArrayOfOpenMBeanAttributeInfo.length);
    return arrayOfMBeanAttributeInfo;
  }
  
  private static MBeanConstructorInfo[] constructorArray(OpenMBeanConstructorInfo[] paramArrayOfOpenMBeanConstructorInfo)
  {
    if (paramArrayOfOpenMBeanConstructorInfo == null) {
      return null;
    }
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = new MBeanConstructorInfo[paramArrayOfOpenMBeanConstructorInfo.length];
    System.arraycopy(paramArrayOfOpenMBeanConstructorInfo, 0, arrayOfMBeanConstructorInfo, 0, paramArrayOfOpenMBeanConstructorInfo.length);
    return arrayOfMBeanConstructorInfo;
  }
  
  private static MBeanOperationInfo[] operationArray(OpenMBeanOperationInfo[] paramArrayOfOpenMBeanOperationInfo)
  {
    if (paramArrayOfOpenMBeanOperationInfo == null) {
      return null;
    }
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = new MBeanOperationInfo[paramArrayOfOpenMBeanOperationInfo.length];
    System.arraycopy(paramArrayOfOpenMBeanOperationInfo, 0, arrayOfMBeanOperationInfo, 0, paramArrayOfOpenMBeanOperationInfo.length);
    return arrayOfMBeanOperationInfo;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    OpenMBeanInfo localOpenMBeanInfo;
    try
    {
      localOpenMBeanInfo = (OpenMBeanInfo)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    if (!Objects.equals(getClassName(), localOpenMBeanInfo.getClassName())) {
      return false;
    }
    if (!sameArrayContents(getAttributes(), localOpenMBeanInfo.getAttributes())) {
      return false;
    }
    if (!sameArrayContents(getConstructors(), localOpenMBeanInfo.getConstructors())) {
      return false;
    }
    if (!sameArrayContents(getOperations(), localOpenMBeanInfo.getOperations())) {
      return false;
    }
    return sameArrayContents(getNotifications(), localOpenMBeanInfo.getNotifications());
  }
  
  private static <T> boolean sameArrayContents(T[] paramArrayOfT1, T[] paramArrayOfT2)
  {
    return new HashSet(Arrays.asList(paramArrayOfT1)).equals(new HashSet(Arrays.asList(paramArrayOfT2)));
  }
  
  public int hashCode()
  {
    if (myHashCode == null)
    {
      int i = 0;
      if (getClassName() != null) {
        i += getClassName().hashCode();
      }
      i += arraySetHash(getAttributes());
      i += arraySetHash(getConstructors());
      i += arraySetHash(getOperations());
      i += arraySetHash(getNotifications());
      myHashCode = Integer.valueOf(i);
    }
    return myHashCode.intValue();
  }
  
  private static <T> int arraySetHash(T[] paramArrayOfT)
  {
    return new HashSet(Arrays.asList(paramArrayOfT)).hashCode();
  }
  
  public String toString()
  {
    if (myToString == null) {
      myToString = (getClass().getName() + "(mbean_class_name=" + getClassName() + ",attributes=" + Arrays.asList(getAttributes()).toString() + ",constructors=" + Arrays.asList(getConstructors()).toString() + ",operations=" + Arrays.asList(getOperations()).toString() + ",notifications=" + Arrays.asList(getNotifications()).toString() + ",descriptor=" + getDescriptor() + ")");
    }
    return myToString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\OpenMBeanInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */