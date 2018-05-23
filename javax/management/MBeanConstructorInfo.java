package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

public class MBeanConstructorInfo
  extends MBeanFeatureInfo
  implements Cloneable
{
  static final long serialVersionUID = 4433990064191844427L;
  static final MBeanConstructorInfo[] NO_CONSTRUCTORS = new MBeanConstructorInfo[0];
  private final transient boolean arrayGettersSafe;
  private final MBeanParameterInfo[] signature;
  
  public MBeanConstructorInfo(String paramString, Constructor<?> paramConstructor)
  {
    this(paramConstructor.getName(), paramString, constructorSignature(paramConstructor), Introspector.descriptorForElement(paramConstructor));
  }
  
  public MBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo)
  {
    this(paramString1, paramString2, paramArrayOfMBeanParameterInfo, null);
  }
  
  public MBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramDescriptor);
    if ((paramArrayOfMBeanParameterInfo == null) || (paramArrayOfMBeanParameterInfo.length == 0)) {
      paramArrayOfMBeanParameterInfo = MBeanParameterInfo.NO_PARAMS;
    } else {
      paramArrayOfMBeanParameterInfo = (MBeanParameterInfo[])paramArrayOfMBeanParameterInfo.clone();
    }
    signature = paramArrayOfMBeanParameterInfo;
    arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanConstructorInfo.class);
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
  
  public MBeanParameterInfo[] getSignature()
  {
    if (signature.length == 0) {
      return signature;
    }
    return (MBeanParameterInfo[])signature.clone();
  }
  
  private MBeanParameterInfo[] fastGetSignature()
  {
    if (arrayGettersSafe) {
      return signature;
    }
    return getSignature();
  }
  
  public String toString()
  {
    return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", signature=" + Arrays.asList(fastGetSignature()) + ", descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanConstructorInfo)) {
      return false;
    }
    MBeanConstructorInfo localMBeanConstructorInfo = (MBeanConstructorInfo)paramObject;
    return (Objects.equals(localMBeanConstructorInfo.getName(), getName())) && (Objects.equals(localMBeanConstructorInfo.getDescription(), getDescription())) && (Arrays.equals(localMBeanConstructorInfo.fastGetSignature(), fastGetSignature())) && (Objects.equals(localMBeanConstructorInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { getName() }) ^ Arrays.hashCode(fastGetSignature());
  }
  
  private static MBeanParameterInfo[] constructorSignature(Constructor<?> paramConstructor)
  {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    Annotation[][] arrayOfAnnotation = paramConstructor.getParameterAnnotations();
    return MBeanOperationInfo.parameters(arrayOfClass, arrayOfAnnotation);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanConstructorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */