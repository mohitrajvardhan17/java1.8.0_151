package javax.management.openmbean;

import java.util.Set;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanParameterInfo;

public class OpenMBeanParameterInfoSupport
  extends MBeanParameterInfo
  implements OpenMBeanParameterInfo
{
  static final long serialVersionUID = -7235016873758443122L;
  private OpenType<?> openType;
  private Object defaultValue = null;
  private Set<?> legalValues = null;
  private Comparable<?> minValue = null;
  private Comparable<?> maxValue = null;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  
  public OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType)
  {
    this(paramString1, paramString2, paramOpenType, (Descriptor)null);
  }
  
  public OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, Descriptor paramDescriptor)
  {
    super(paramString1, paramOpenType == null ? null : paramOpenType.getClassName(), paramString2, ImmutableDescriptor.union(new Descriptor[] { paramDescriptor, paramOpenType == null ? null : paramOpenType.getDescriptor() }));
    openType = paramOpenType;
    paramDescriptor = getDescriptor();
    defaultValue = OpenMBeanAttributeInfoSupport.valueFrom(paramDescriptor, "defaultValue", paramOpenType);
    legalValues = OpenMBeanAttributeInfoSupport.valuesFrom(paramDescriptor, "legalValues", paramOpenType);
    minValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(paramDescriptor, "minValue", paramOpenType);
    maxValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(paramDescriptor, "maxValue", paramOpenType);
    try
    {
      OpenMBeanAttributeInfoSupport.check(this);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw new IllegalArgumentException(localOpenDataException.getMessage(), localOpenDataException);
    }
  }
  
  public <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT)
    throws OpenDataException
  {
    this(paramString1, paramString2, paramOpenType, paramT, (Object[])null);
  }
  
  public <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT)
    throws OpenDataException
  {
    this(paramString1, paramString2, paramOpenType, paramT, paramArrayOfT, null, null);
  }
  
  public <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
    throws OpenDataException
  {
    this(paramString1, paramString2, paramOpenType, paramT, null, paramComparable1, paramComparable2);
  }
  
  private <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
    throws OpenDataException
  {
    super(paramString1, paramOpenType == null ? null : paramOpenType.getClassName(), paramString2, OpenMBeanAttributeInfoSupport.makeDescriptor(paramOpenType, paramT, paramArrayOfT, paramComparable1, paramComparable2));
    openType = paramOpenType;
    Descriptor localDescriptor = getDescriptor();
    defaultValue = paramT;
    minValue = paramComparable1;
    maxValue = paramComparable2;
    legalValues = ((Set)localDescriptor.getFieldValue("legalValues"));
    OpenMBeanAttributeInfoSupport.check(this);
  }
  
  private Object readResolve()
  {
    if (getDescriptor().getFieldNames().length == 0)
    {
      OpenType localOpenType = (OpenType)OpenMBeanAttributeInfoSupport.cast(openType);
      Set localSet = (Set)OpenMBeanAttributeInfoSupport.cast(legalValues);
      Comparable localComparable1 = (Comparable)OpenMBeanAttributeInfoSupport.cast(minValue);
      Comparable localComparable2 = (Comparable)OpenMBeanAttributeInfoSupport.cast(maxValue);
      return new OpenMBeanParameterInfoSupport(name, description, openType, OpenMBeanAttributeInfoSupport.makeDescriptor(localOpenType, defaultValue, localSet, localComparable1, localComparable2));
    }
    return this;
  }
  
  public OpenType<?> getOpenType()
  {
    return openType;
  }
  
  public Object getDefaultValue()
  {
    return defaultValue;
  }
  
  public Set<?> getLegalValues()
  {
    return legalValues;
  }
  
  public Comparable<?> getMinValue()
  {
    return minValue;
  }
  
  public Comparable<?> getMaxValue()
  {
    return maxValue;
  }
  
  public boolean hasDefaultValue()
  {
    return defaultValue != null;
  }
  
  public boolean hasLegalValues()
  {
    return legalValues != null;
  }
  
  public boolean hasMinValue()
  {
    return minValue != null;
  }
  
  public boolean hasMaxValue()
  {
    return maxValue != null;
  }
  
  public boolean isValue(Object paramObject)
  {
    return OpenMBeanAttributeInfoSupport.isValue(this, paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof OpenMBeanParameterInfo)) {
      return false;
    }
    OpenMBeanParameterInfo localOpenMBeanParameterInfo = (OpenMBeanParameterInfo)paramObject;
    return OpenMBeanAttributeInfoSupport.equal(this, localOpenMBeanParameterInfo);
  }
  
  public int hashCode()
  {
    if (myHashCode == null) {
      myHashCode = Integer.valueOf(OpenMBeanAttributeInfoSupport.hashCode(this));
    }
    return myHashCode.intValue();
  }
  
  public String toString()
  {
    if (myToString == null) {
      myToString = OpenMBeanAttributeInfoSupport.toString(this);
    }
    return myToString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\OpenMBeanParameterInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */