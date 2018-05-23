package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public abstract interface OpenMBeanConstructorInfo
{
  public abstract String getDescription();
  
  public abstract String getName();
  
  public abstract MBeanParameterInfo[] getSignature();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\OpenMBeanConstructorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */