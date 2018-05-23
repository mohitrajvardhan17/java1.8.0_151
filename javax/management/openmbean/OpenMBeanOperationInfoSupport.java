package javax.management.openmbean;

import java.util.Arrays;
import java.util.List;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

public class OpenMBeanOperationInfoSupport
  extends MBeanOperationInfo
  implements OpenMBeanOperationInfo
{
  static final long serialVersionUID = 4996859732565369366L;
  private OpenType<?> returnOpenType;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  
  public OpenMBeanOperationInfoSupport(String paramString1, String paramString2, OpenMBeanParameterInfo[] paramArrayOfOpenMBeanParameterInfo, OpenType<?> paramOpenType, int paramInt)
  {
    this(paramString1, paramString2, paramArrayOfOpenMBeanParameterInfo, paramOpenType, paramInt, (Descriptor)null);
  }
  
  public OpenMBeanOperationInfoSupport(String paramString1, String paramString2, OpenMBeanParameterInfo[] paramArrayOfOpenMBeanParameterInfo, OpenType<?> paramOpenType, int paramInt, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, arrayCopyCast(paramArrayOfOpenMBeanParameterInfo), paramOpenType == null ? null : paramOpenType.getClassName(), paramInt, ImmutableDescriptor.union(new Descriptor[] { paramDescriptor, paramOpenType == null ? null : paramOpenType.getDescriptor() }));
    if ((paramString1 == null) || (paramString1.trim().equals(""))) {
      throw new IllegalArgumentException("Argument name cannot be null or empty");
    }
    if ((paramString2 == null) || (paramString2.trim().equals(""))) {
      throw new IllegalArgumentException("Argument description cannot be null or empty");
    }
    if (paramOpenType == null) {
      throw new IllegalArgumentException("Argument returnOpenType cannot be null");
    }
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 0) && (paramInt != 3)) {
      throw new IllegalArgumentException("Argument impact can only be one of ACTION, ACTION_INFO, INFO, or UNKNOWN: " + paramInt);
    }
    returnOpenType = paramOpenType;
  }
  
  private static MBeanParameterInfo[] arrayCopyCast(OpenMBeanParameterInfo[] paramArrayOfOpenMBeanParameterInfo)
  {
    if (paramArrayOfOpenMBeanParameterInfo == null) {
      return null;
    }
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[paramArrayOfOpenMBeanParameterInfo.length];
    System.arraycopy(paramArrayOfOpenMBeanParameterInfo, 0, arrayOfMBeanParameterInfo, 0, paramArrayOfOpenMBeanParameterInfo.length);
    return arrayOfMBeanParameterInfo;
  }
  
  private static OpenMBeanParameterInfo[] arrayCopyCast(MBeanParameterInfo[] paramArrayOfMBeanParameterInfo)
  {
    if (paramArrayOfMBeanParameterInfo == null) {
      return null;
    }
    OpenMBeanParameterInfo[] arrayOfOpenMBeanParameterInfo = new OpenMBeanParameterInfo[paramArrayOfMBeanParameterInfo.length];
    System.arraycopy(paramArrayOfMBeanParameterInfo, 0, arrayOfOpenMBeanParameterInfo, 0, paramArrayOfMBeanParameterInfo.length);
    return arrayOfOpenMBeanParameterInfo;
  }
  
  public OpenType<?> getReturnOpenType()
  {
    return returnOpenType;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    OpenMBeanOperationInfo localOpenMBeanOperationInfo;
    try
    {
      localOpenMBeanOperationInfo = (OpenMBeanOperationInfo)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    if (!getName().equals(localOpenMBeanOperationInfo.getName())) {
      return false;
    }
    if (!Arrays.equals(getSignature(), localOpenMBeanOperationInfo.getSignature())) {
      return false;
    }
    if (!getReturnOpenType().equals(localOpenMBeanOperationInfo.getReturnOpenType())) {
      return false;
    }
    return getImpact() == localOpenMBeanOperationInfo.getImpact();
  }
  
  public int hashCode()
  {
    if (myHashCode == null)
    {
      int i = 0;
      i += getName().hashCode();
      i += Arrays.asList(getSignature()).hashCode();
      i += getReturnOpenType().hashCode();
      i += getImpact();
      myHashCode = Integer.valueOf(i);
    }
    return myHashCode.intValue();
  }
  
  public String toString()
  {
    if (myToString == null) {
      myToString = (getClass().getName() + "(name=" + getName() + ",signature=" + Arrays.asList(getSignature()).toString() + ",return=" + getReturnOpenType().toString() + ",impact=" + getImpact() + ",descriptor=" + getDescriptor() + ")");
    }
    return myToString;
  }
  
  private Object readResolve()
  {
    if (getDescriptor().getFieldNames().length == 0) {
      return new OpenMBeanOperationInfoSupport(name, description, arrayCopyCast(getSignature()), returnOpenType, getImpact());
    }
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\OpenMBeanOperationInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */