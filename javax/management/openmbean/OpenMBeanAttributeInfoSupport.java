package javax.management.openmbean;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.management.Descriptor;
import javax.management.DescriptorRead;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class OpenMBeanAttributeInfoSupport
  extends MBeanAttributeInfo
  implements OpenMBeanAttributeInfo
{
  static final long serialVersionUID = -4867215622149721849L;
  private OpenType<?> openType;
  private final Object defaultValue;
  private final Set<?> legalValues;
  private final Comparable<?> minValue;
  private final Comparable<?> maxValue;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  
  public OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, (Descriptor)null);
  }
  
  public OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor)
  {
    super(paramString1, paramOpenType == null ? null : paramOpenType.getClassName(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, ImmutableDescriptor.union(new Descriptor[] { paramDescriptor, paramOpenType == null ? null : paramOpenType.getDescriptor() }));
    openType = paramOpenType;
    paramDescriptor = getDescriptor();
    defaultValue = valueFrom(paramDescriptor, "defaultValue", paramOpenType);
    legalValues = valuesFrom(paramDescriptor, "legalValues", paramOpenType);
    minValue = comparableValueFrom(paramDescriptor, "minValue", paramOpenType);
    maxValue = comparableValueFrom(paramDescriptor, "maxValue", paramOpenType);
    try
    {
      check(this);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw new IllegalArgumentException(localOpenDataException.getMessage(), localOpenDataException);
    }
  }
  
  public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT)
    throws OpenDataException
  {
    this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, (Object[])null);
  }
  
  public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, T[] paramArrayOfT)
    throws OpenDataException
  {
    this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, paramArrayOfT, null, null);
  }
  
  public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
    throws OpenDataException
  {
    this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, null, paramComparable1, paramComparable2);
  }
  
  private <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
    throws OpenDataException
  {
    super(paramString1, paramOpenType == null ? null : paramOpenType.getClassName(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, makeDescriptor(paramOpenType, paramT, paramArrayOfT, paramComparable1, paramComparable2));
    openType = paramOpenType;
    Descriptor localDescriptor = getDescriptor();
    defaultValue = paramT;
    minValue = paramComparable1;
    maxValue = paramComparable2;
    legalValues = ((Set)localDescriptor.getFieldValue("legalValues"));
    check(this);
  }
  
  private Object readResolve()
  {
    if (getDescriptor().getFieldNames().length == 0)
    {
      OpenType localOpenType = (OpenType)cast(openType);
      Set localSet = (Set)cast(legalValues);
      Comparable localComparable1 = (Comparable)cast(minValue);
      Comparable localComparable2 = (Comparable)cast(maxValue);
      return new OpenMBeanAttributeInfoSupport(name, description, openType, isReadable(), isWritable(), isIs(), makeDescriptor(localOpenType, defaultValue, localSet, localComparable1, localComparable2));
    }
    return this;
  }
  
  static void check(OpenMBeanParameterInfo paramOpenMBeanParameterInfo)
    throws OpenDataException
  {
    OpenType localOpenType = paramOpenMBeanParameterInfo.getOpenType();
    if (localOpenType == null) {
      throw new IllegalArgumentException("OpenType cannot be null");
    }
    if ((paramOpenMBeanParameterInfo.getName() == null) || (paramOpenMBeanParameterInfo.getName().trim().equals(""))) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if ((paramOpenMBeanParameterInfo.getDescription() == null) || (paramOpenMBeanParameterInfo.getDescription().trim().equals(""))) {
      throw new IllegalArgumentException("Description cannot be null or empty");
    }
    Object localObject1;
    if (paramOpenMBeanParameterInfo.hasDefaultValue())
    {
      if ((localOpenType.isArray()) || ((localOpenType instanceof TabularType))) {
        throw new OpenDataException("Default value not supported for ArrayType and TabularType");
      }
      if (!localOpenType.isValue(paramOpenMBeanParameterInfo.getDefaultValue()))
      {
        localObject1 = "Argument defaultValue's class [\"" + paramOpenMBeanParameterInfo.getDefaultValue().getClass().getName() + "\"] does not match the one defined in openType[\"" + localOpenType.getClassName() + "\"]";
        throw new OpenDataException((String)localObject1);
      }
    }
    if ((paramOpenMBeanParameterInfo.hasLegalValues()) && ((paramOpenMBeanParameterInfo.hasMinValue()) || (paramOpenMBeanParameterInfo.hasMaxValue()))) {
      throw new OpenDataException("cannot have both legalValue and minValue or maxValue");
    }
    if ((paramOpenMBeanParameterInfo.hasMinValue()) && (!localOpenType.isValue(paramOpenMBeanParameterInfo.getMinValue())))
    {
      localObject1 = "Type of minValue [" + paramOpenMBeanParameterInfo.getMinValue().getClass().getName() + "] does not match OpenType [" + localOpenType.getClassName() + "]";
      throw new OpenDataException((String)localObject1);
    }
    if ((paramOpenMBeanParameterInfo.hasMaxValue()) && (!localOpenType.isValue(paramOpenMBeanParameterInfo.getMaxValue())))
    {
      localObject1 = "Type of maxValue [" + paramOpenMBeanParameterInfo.getMaxValue().getClass().getName() + "] does not match OpenType [" + localOpenType.getClassName() + "]";
      throw new OpenDataException((String)localObject1);
    }
    if (paramOpenMBeanParameterInfo.hasDefaultValue())
    {
      localObject1 = paramOpenMBeanParameterInfo.getDefaultValue();
      if ((paramOpenMBeanParameterInfo.hasLegalValues()) && (!paramOpenMBeanParameterInfo.getLegalValues().contains(localObject1))) {
        throw new OpenDataException("defaultValue is not contained in legalValues");
      }
      if ((paramOpenMBeanParameterInfo.hasMinValue()) && (compare(paramOpenMBeanParameterInfo.getMinValue(), localObject1) > 0)) {
        throw new OpenDataException("minValue cannot be greater than defaultValue");
      }
      if ((paramOpenMBeanParameterInfo.hasMaxValue()) && (compare(paramOpenMBeanParameterInfo.getMaxValue(), localObject1) < 0)) {
        throw new OpenDataException("maxValue cannot be less than defaultValue");
      }
    }
    if (paramOpenMBeanParameterInfo.hasLegalValues())
    {
      if (((localOpenType instanceof TabularType)) || (localOpenType.isArray())) {
        throw new OpenDataException("Legal values not supported for TabularType and arrays");
      }
      localObject1 = paramOpenMBeanParameterInfo.getLegalValues().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = ((Iterator)localObject1).next();
        if (!localOpenType.isValue(localObject2))
        {
          String str = "Element of legalValues [" + localObject2 + "] is not a valid value for the specified openType [" + localOpenType.toString() + "]";
          throw new OpenDataException(str);
        }
      }
    }
    if ((paramOpenMBeanParameterInfo.hasMinValue()) && (paramOpenMBeanParameterInfo.hasMaxValue()) && (compare(paramOpenMBeanParameterInfo.getMinValue(), paramOpenMBeanParameterInfo.getMaxValue()) > 0)) {
      throw new OpenDataException("minValue cannot be greater than maxValue");
    }
  }
  
  static int compare(Object paramObject1, Object paramObject2)
  {
    return ((Comparable)paramObject1).compareTo(paramObject2);
  }
  
  static <T> Descriptor makeDescriptor(OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
  {
    HashMap localHashMap = new HashMap();
    if (paramT != null) {
      localHashMap.put("defaultValue", paramT);
    }
    if (paramArrayOfT != null)
    {
      Object localObject = new HashSet();
      for (T ? : paramArrayOfT) {
        ((Set)localObject).add(?);
      }
      localObject = Collections.unmodifiableSet((Set)localObject);
      localHashMap.put("legalValues", localObject);
    }
    if (paramComparable1 != null) {
      localHashMap.put("minValue", paramComparable1);
    }
    if (paramComparable2 != null) {
      localHashMap.put("maxValue", paramComparable2);
    }
    if (localHashMap.isEmpty()) {
      return paramOpenType.getDescriptor();
    }
    localHashMap.put("openType", paramOpenType);
    return new ImmutableDescriptor(localHashMap);
  }
  
  static <T> Descriptor makeDescriptor(OpenType<T> paramOpenType, T paramT, Set<T> paramSet, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
  {
    Object[] arrayOfObject;
    if (paramSet == null)
    {
      arrayOfObject = null;
    }
    else
    {
      arrayOfObject = (Object[])cast(new Object[paramSet.size()]);
      paramSet.toArray(arrayOfObject);
    }
    return makeDescriptor(paramOpenType, paramT, arrayOfObject, paramComparable1, paramComparable2);
  }
  
  static <T> T valueFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType)
  {
    Object localObject = paramDescriptor.getFieldValue(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      return (T)convertFrom(localObject, paramOpenType);
    }
    catch (Exception localException)
    {
      String str = "Cannot convert descriptor field " + paramString + "  to " + paramOpenType.getTypeName();
      throw ((IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(str), localException));
    }
  }
  
  static <T> Set<T> valuesFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType)
  {
    Object localObject1 = paramDescriptor.getFieldValue(paramString);
    if (localObject1 == null) {
      return null;
    }
    Object localObject4;
    Object localObject2;
    if ((localObject1 instanceof Set))
    {
      localObject3 = (Set)localObject1;
      int i = 1;
      localObject4 = ((Set)localObject3).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        Object localObject5 = ((Iterator)localObject4).next();
        if (!paramOpenType.isValue(localObject5))
        {
          i = 0;
          break;
        }
      }
      if (i != 0) {
        return (Set)cast(localObject3);
      }
      localObject2 = localObject3;
    }
    else if ((localObject1 instanceof Object[]))
    {
      localObject2 = Arrays.asList((Object[])localObject1);
    }
    else
    {
      localObject3 = "Descriptor value for " + paramString + " must be a Set or an array: " + localObject1.getClass().getName();
      throw new IllegalArgumentException((String)localObject3);
    }
    Object localObject3 = new HashSet();
    Iterator localIterator = ((Collection)localObject2).iterator();
    while (localIterator.hasNext())
    {
      localObject4 = localIterator.next();
      ((Set)localObject3).add(convertFrom(localObject4, paramOpenType));
    }
    return (Set<T>)localObject3;
  }
  
  static <T> Comparable<?> comparableValueFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType)
  {
    Object localObject = valueFrom(paramDescriptor, paramString, paramOpenType);
    if ((localObject == null) || ((localObject instanceof Comparable))) {
      return (Comparable)localObject;
    }
    String str = "Descriptor field " + paramString + " with value " + localObject + " is not Comparable";
    throw new IllegalArgumentException(str);
  }
  
  private static <T> T convertFrom(Object paramObject, OpenType<T> paramOpenType)
  {
    if (paramOpenType.isValue(paramObject))
    {
      Object localObject = cast(paramObject);
      return (T)localObject;
    }
    return (T)convertFromStrings(paramObject, paramOpenType);
  }
  
  private static <T> T convertFromStrings(Object paramObject, OpenType<T> paramOpenType)
  {
    if ((paramOpenType instanceof ArrayType)) {
      return (T)convertFromStringArray(paramObject, paramOpenType);
    }
    if ((paramObject instanceof String)) {
      return (T)convertFromString((String)paramObject, paramOpenType);
    }
    String str = "Cannot convert value " + paramObject + " of type " + paramObject.getClass().getName() + " to type " + paramOpenType.getTypeName();
    throw new IllegalArgumentException(str);
  }
  
  private static <T> T convertFromString(String paramString, OpenType<T> paramOpenType)
  {
    Class localClass;
    try
    {
      String str1 = paramOpenType.safeGetClassName();
      ReflectUtil.checkPackageAccess(str1);
      localClass = (Class)cast(Class.forName(str1));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new NoClassDefFoundError(localClassNotFoundException.toString());
    }
    Method localMethod;
    try
    {
      localMethod = localClass.getMethod("valueOf", new Class[] { String.class });
      if ((!Modifier.isStatic(localMethod.getModifiers())) || (localMethod.getReturnType() != localClass)) {
        localMethod = null;
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      localMethod = null;
    }
    if (localMethod != null) {
      try
      {
        return (T)localClass.cast(MethodUtil.invoke(localMethod, null, new Object[] { paramString }));
      }
      catch (Exception localException1)
      {
        String str2 = "Could not convert \"" + paramString + "\" using method: " + localMethod;
        throw new IllegalArgumentException(str2, localException1);
      }
    }
    Constructor localConstructor;
    try
    {
      localConstructor = localClass.getConstructor(new Class[] { String.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException2)
    {
      localConstructor = null;
    }
    if (localConstructor != null) {
      try
      {
        return (T)localConstructor.newInstance(new Object[] { paramString });
      }
      catch (Exception localException2)
      {
        String str3 = "Could not convert \"" + paramString + "\" using constructor: " + localConstructor;
        throw new IllegalArgumentException(str3, localException2);
      }
    }
    throw new IllegalArgumentException("Don't know how to convert string to " + paramOpenType.getTypeName());
  }
  
  private static <T> T convertFromStringArray(Object paramObject, OpenType<T> paramOpenType)
  {
    ArrayType localArrayType = (ArrayType)paramOpenType;
    OpenType localOpenType = localArrayType.getElementOpenType();
    int i = localArrayType.getDimension();
    String str1 = "[";
    for (int j = 1; j < i; j++) {
      str1 = str1 + "[";
    }
    Class localClass1;
    Class localClass2;
    try
    {
      String str2 = localOpenType.safeGetClassName();
      ReflectUtil.checkPackageAccess(str2);
      localClass1 = Class.forName(str1 + "Ljava.lang.String;");
      localClass2 = Class.forName(str1 + "L" + str2 + ";");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new NoClassDefFoundError(localClassNotFoundException.toString());
    }
    Object localObject1;
    if (!localClass1.isInstance(paramObject))
    {
      localObject1 = "Value for " + i + "-dimensional array of " + localOpenType.getTypeName() + " must be same type or a String array with same dimensions";
      throw new IllegalArgumentException((String)localObject1);
    }
    if (i == 1) {
      localObject1 = localOpenType;
    } else {
      try
      {
        localObject1 = new ArrayType(i - 1, localOpenType);
      }
      catch (OpenDataException localOpenDataException)
      {
        throw new IllegalArgumentException(localOpenDataException.getMessage(), localOpenDataException);
      }
    }
    int k = Array.getLength(paramObject);
    Object[] arrayOfObject = (Object[])Array.newInstance(localClass2.getComponentType(), k);
    for (int m = 0; m < k; m++)
    {
      Object localObject2 = Array.get(paramObject, m);
      Object localObject3 = convertFromStrings(localObject2, (OpenType)localObject1);
      Array.set(arrayOfObject, m, localObject3);
    }
    return (T)cast(arrayOfObject);
  }
  
  static <T> T cast(Object paramObject)
  {
    return (T)paramObject;
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
    return isValue(this, paramObject);
  }
  
  static boolean isValue(OpenMBeanParameterInfo paramOpenMBeanParameterInfo, Object paramObject)
  {
    if ((paramOpenMBeanParameterInfo.hasDefaultValue()) && (paramObject == null)) {
      return true;
    }
    return (paramOpenMBeanParameterInfo.getOpenType().isValue(paramObject)) && ((!paramOpenMBeanParameterInfo.hasLegalValues()) || (paramOpenMBeanParameterInfo.getLegalValues().contains(paramObject))) && ((!paramOpenMBeanParameterInfo.hasMinValue()) || (paramOpenMBeanParameterInfo.getMinValue().compareTo(paramObject) <= 0)) && ((!paramOpenMBeanParameterInfo.hasMaxValue()) || (paramOpenMBeanParameterInfo.getMaxValue().compareTo(paramObject) >= 0));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof OpenMBeanAttributeInfo)) {
      return false;
    }
    OpenMBeanAttributeInfo localOpenMBeanAttributeInfo = (OpenMBeanAttributeInfo)paramObject;
    return (isReadable() == localOpenMBeanAttributeInfo.isReadable()) && (isWritable() == localOpenMBeanAttributeInfo.isWritable()) && (isIs() == localOpenMBeanAttributeInfo.isIs()) && (equal(this, localOpenMBeanAttributeInfo));
  }
  
  static boolean equal(OpenMBeanParameterInfo paramOpenMBeanParameterInfo1, OpenMBeanParameterInfo paramOpenMBeanParameterInfo2)
  {
    if ((paramOpenMBeanParameterInfo1 instanceof DescriptorRead))
    {
      if (!(paramOpenMBeanParameterInfo2 instanceof DescriptorRead)) {
        return false;
      }
      Descriptor localDescriptor1 = ((DescriptorRead)paramOpenMBeanParameterInfo1).getDescriptor();
      Descriptor localDescriptor2 = ((DescriptorRead)paramOpenMBeanParameterInfo2).getDescriptor();
      if (!localDescriptor1.equals(localDescriptor2)) {
        return false;
      }
    }
    else if ((paramOpenMBeanParameterInfo2 instanceof DescriptorRead))
    {
      return false;
    }
    return (paramOpenMBeanParameterInfo1.getName().equals(paramOpenMBeanParameterInfo2.getName())) && (paramOpenMBeanParameterInfo1.getOpenType().equals(paramOpenMBeanParameterInfo2.getOpenType())) && (paramOpenMBeanParameterInfo1.hasDefaultValue() ? paramOpenMBeanParameterInfo1.getDefaultValue().equals(paramOpenMBeanParameterInfo2.getDefaultValue()) : !paramOpenMBeanParameterInfo2.hasDefaultValue()) && (paramOpenMBeanParameterInfo1.hasMinValue() ? paramOpenMBeanParameterInfo1.getMinValue().equals(paramOpenMBeanParameterInfo2.getMinValue()) : !paramOpenMBeanParameterInfo2.hasMinValue()) && (paramOpenMBeanParameterInfo1.hasMaxValue() ? paramOpenMBeanParameterInfo1.getMaxValue().equals(paramOpenMBeanParameterInfo2.getMaxValue()) : !paramOpenMBeanParameterInfo2.hasMaxValue()) && (paramOpenMBeanParameterInfo1.hasLegalValues() ? paramOpenMBeanParameterInfo1.getLegalValues().equals(paramOpenMBeanParameterInfo2.getLegalValues()) : !paramOpenMBeanParameterInfo2.hasLegalValues());
  }
  
  public int hashCode()
  {
    if (myHashCode == null) {
      myHashCode = Integer.valueOf(hashCode(this));
    }
    return myHashCode.intValue();
  }
  
  static int hashCode(OpenMBeanParameterInfo paramOpenMBeanParameterInfo)
  {
    int i = 0;
    i += paramOpenMBeanParameterInfo.getName().hashCode();
    i += paramOpenMBeanParameterInfo.getOpenType().hashCode();
    if (paramOpenMBeanParameterInfo.hasDefaultValue()) {
      i += paramOpenMBeanParameterInfo.getDefaultValue().hashCode();
    }
    if (paramOpenMBeanParameterInfo.hasMinValue()) {
      i += paramOpenMBeanParameterInfo.getMinValue().hashCode();
    }
    if (paramOpenMBeanParameterInfo.hasMaxValue()) {
      i += paramOpenMBeanParameterInfo.getMaxValue().hashCode();
    }
    if (paramOpenMBeanParameterInfo.hasLegalValues()) {
      i += paramOpenMBeanParameterInfo.getLegalValues().hashCode();
    }
    if ((paramOpenMBeanParameterInfo instanceof DescriptorRead)) {
      i += ((DescriptorRead)paramOpenMBeanParameterInfo).getDescriptor().hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    if (myToString == null) {
      myToString = toString(this);
    }
    return myToString;
  }
  
  static String toString(OpenMBeanParameterInfo paramOpenMBeanParameterInfo)
  {
    Object localObject = (paramOpenMBeanParameterInfo instanceof DescriptorRead) ? ((DescriptorRead)paramOpenMBeanParameterInfo).getDescriptor() : null;
    return paramOpenMBeanParameterInfo.getClass().getName() + "(name=" + paramOpenMBeanParameterInfo.getName() + ",openType=" + paramOpenMBeanParameterInfo.getOpenType() + ",default=" + paramOpenMBeanParameterInfo.getDefaultValue() + ",minValue=" + paramOpenMBeanParameterInfo.getMinValue() + ",maxValue=" + paramOpenMBeanParameterInfo.getMaxValue() + ",legalValues=" + paramOpenMBeanParameterInfo.getLegalValues() + (localObject == null ? "" : new StringBuilder().append(",descriptor=").append(localObject).toString()) + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\OpenMBeanAttributeInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */