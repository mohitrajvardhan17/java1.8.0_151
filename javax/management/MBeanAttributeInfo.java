package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Objects;

public class MBeanAttributeInfo
  extends MBeanFeatureInfo
  implements Cloneable
{
  private static final long serialVersionUID;
  static final MBeanAttributeInfo[] NO_ATTRIBUTES = new MBeanAttributeInfo[0];
  private final String attributeType;
  private final boolean isWrite;
  private final boolean isRead;
  private final boolean is;
  
  public MBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3, (Descriptor)null);
  }
  
  public MBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor)
  {
    super(paramString1, paramString3, paramDescriptor);
    attributeType = paramString2;
    isRead = paramBoolean1;
    isWrite = paramBoolean2;
    if ((paramBoolean3) && (!paramBoolean1)) {
      throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-readable attribute");
    }
    if ((paramBoolean3) && (!paramString2.equals("java.lang.Boolean")) && (!paramString2.equals("boolean"))) {
      throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-boolean attribute");
    }
    is = paramBoolean3;
  }
  
  public MBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    this(paramString1, attributeType(paramMethod1, paramMethod2), paramString2, paramMethod1 != null, paramMethod2 != null, isIs(paramMethod1), ImmutableDescriptor.union(new Descriptor[] { Introspector.descriptorForElement(paramMethod1), Introspector.descriptorForElement(paramMethod2) }));
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
  
  public String getType()
  {
    return attributeType;
  }
  
  public boolean isReadable()
  {
    return isRead;
  }
  
  public boolean isWritable()
  {
    return isWrite;
  }
  
  public boolean isIs()
  {
    return is;
  }
  
  public String toString()
  {
    String str;
    if (isReadable())
    {
      if (isWritable()) {
        str = "read/write";
      } else {
        str = "read-only";
      }
    }
    else if (isWritable()) {
      str = "write-only";
    } else {
      str = "no-access";
    }
    return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", type=" + getType() + ", " + str + ", " + (isIs() ? "isIs, " : "") + "descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanAttributeInfo)) {
      return false;
    }
    MBeanAttributeInfo localMBeanAttributeInfo = (MBeanAttributeInfo)paramObject;
    return (Objects.equals(localMBeanAttributeInfo.getName(), getName())) && (Objects.equals(localMBeanAttributeInfo.getType(), getType())) && (Objects.equals(localMBeanAttributeInfo.getDescription(), getDescription())) && (Objects.equals(localMBeanAttributeInfo.getDescriptor(), getDescriptor())) && (localMBeanAttributeInfo.isReadable() == isReadable()) && (localMBeanAttributeInfo.isWritable() == isWritable()) && (localMBeanAttributeInfo.isIs() == isIs());
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { getName(), getType() });
  }
  
  private static boolean isIs(Method paramMethod)
  {
    return (paramMethod != null) && (paramMethod.getName().startsWith("is")) && ((paramMethod.getReturnType().equals(Boolean.TYPE)) || (paramMethod.getReturnType().equals(Boolean.class)));
  }
  
  private static String attributeType(Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    Class localClass = null;
    if (paramMethod1 != null)
    {
      if (paramMethod1.getParameterTypes().length != 0) {
        throw new IntrospectionException("bad getter arg count");
      }
      localClass = paramMethod1.getReturnType();
      if (localClass == Void.TYPE) {
        throw new IntrospectionException("getter " + paramMethod1.getName() + " returns void");
      }
    }
    if (paramMethod2 != null)
    {
      Class[] arrayOfClass = paramMethod2.getParameterTypes();
      if (arrayOfClass.length != 1) {
        throw new IntrospectionException("bad setter arg count");
      }
      if (localClass == null) {
        localClass = arrayOfClass[0];
      } else if (localClass != arrayOfClass[0]) {
        throw new IntrospectionException("type mismatch between getter and setter");
      }
    }
    if (localClass == null) {
      throw new IntrospectionException("getter and setter cannot both be null");
    }
    return localClass.getName();
  }
  
  static
  {
    long l = 8644704819898565848L;
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      if ("1.0".equals(str)) {
        l = 7043855487133450673L;
      }
    }
    catch (Exception localException) {}
    serialVersionUID = l;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */