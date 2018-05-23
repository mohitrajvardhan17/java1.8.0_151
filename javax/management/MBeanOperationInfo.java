package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class MBeanOperationInfo
  extends MBeanFeatureInfo
  implements Cloneable
{
  static final long serialVersionUID = -6178860474881375330L;
  static final MBeanOperationInfo[] NO_OPERATIONS = new MBeanOperationInfo[0];
  public static final int INFO = 0;
  public static final int ACTION = 1;
  public static final int ACTION_INFO = 2;
  public static final int UNKNOWN = 3;
  private final String type;
  private final MBeanParameterInfo[] signature;
  private final int impact;
  private final transient boolean arrayGettersSafe;
  
  public MBeanOperationInfo(String paramString, Method paramMethod)
  {
    this(paramMethod.getName(), paramString, methodSignature(paramMethod), paramMethod.getReturnType().getName(), 3, Introspector.descriptorForElement(paramMethod));
  }
  
  public MBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt)
  {
    this(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt, (Descriptor)null);
  }
  
  public MBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramDescriptor);
    if ((paramArrayOfMBeanParameterInfo == null) || (paramArrayOfMBeanParameterInfo.length == 0)) {
      paramArrayOfMBeanParameterInfo = MBeanParameterInfo.NO_PARAMS;
    } else {
      paramArrayOfMBeanParameterInfo = (MBeanParameterInfo[])paramArrayOfMBeanParameterInfo.clone();
    }
    signature = paramArrayOfMBeanParameterInfo;
    type = paramString3;
    impact = paramInt;
    arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanOperationInfo.class);
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
  
  public String getReturnType()
  {
    return type;
  }
  
  public MBeanParameterInfo[] getSignature()
  {
    if (signature == null) {
      return MBeanParameterInfo.NO_PARAMS;
    }
    if (signature.length == 0) {
      return signature;
    }
    return (MBeanParameterInfo[])signature.clone();
  }
  
  private MBeanParameterInfo[] fastGetSignature()
  {
    if (arrayGettersSafe)
    {
      if (signature == null) {
        return MBeanParameterInfo.NO_PARAMS;
      }
      return signature;
    }
    return getSignature();
  }
  
  public int getImpact()
  {
    return impact;
  }
  
  public String toString()
  {
    String str;
    switch (getImpact())
    {
    case 1: 
      str = "action";
      break;
    case 2: 
      str = "action/info";
      break;
    case 0: 
      str = "info";
      break;
    case 3: 
      str = "unknown";
      break;
    default: 
      str = "(" + getImpact() + ")";
    }
    return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", returnType=" + getReturnType() + ", signature=" + Arrays.asList(fastGetSignature()) + ", impact=" + str + ", descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanOperationInfo)) {
      return false;
    }
    MBeanOperationInfo localMBeanOperationInfo = (MBeanOperationInfo)paramObject;
    return (Objects.equals(localMBeanOperationInfo.getName(), getName())) && (Objects.equals(localMBeanOperationInfo.getReturnType(), getReturnType())) && (Objects.equals(localMBeanOperationInfo.getDescription(), getDescription())) && (localMBeanOperationInfo.getImpact() == getImpact()) && (Arrays.equals(localMBeanOperationInfo.fastGetSignature(), fastGetSignature())) && (Objects.equals(localMBeanOperationInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { getName(), getReturnType() });
  }
  
  private static MBeanParameterInfo[] methodSignature(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Annotation[][] arrayOfAnnotation = paramMethod.getParameterAnnotations();
    return parameters(arrayOfClass, arrayOfAnnotation);
  }
  
  static MBeanParameterInfo[] parameters(Class<?>[] paramArrayOfClass, Annotation[][] paramArrayOfAnnotation)
  {
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[paramArrayOfClass.length];
    assert (paramArrayOfClass.length == paramArrayOfAnnotation.length);
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      Descriptor localDescriptor = Introspector.descriptorForAnnotations(paramArrayOfAnnotation[i]);
      String str = "p" + (i + 1);
      arrayOfMBeanParameterInfo[i] = new MBeanParameterInfo(str, paramArrayOfClass[i].getName(), "", localDescriptor);
    }
    return arrayOfMBeanParameterInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanOperationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */