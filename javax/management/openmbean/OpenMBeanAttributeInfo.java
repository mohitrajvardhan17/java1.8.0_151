package javax.management.openmbean;

public abstract interface OpenMBeanAttributeInfo
  extends OpenMBeanParameterInfo
{
  public abstract boolean isReadable();
  
  public abstract boolean isWritable();
  
  public abstract boolean isIs();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\OpenMBeanAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */