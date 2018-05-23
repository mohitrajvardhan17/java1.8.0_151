package javax.management.openmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TabularDataSupport
  implements TabularData, Map<Object, Object>, Cloneable, Serializable
{
  static final long serialVersionUID = 5720150593236309827L;
  private Map<Object, CompositeData> dataMap;
  private final TabularType tabularType;
  private transient String[] indexNamesArray;
  
  public TabularDataSupport(TabularType paramTabularType)
  {
    this(paramTabularType, 16, 0.75F);
  }
  
  public TabularDataSupport(TabularType paramTabularType, int paramInt, float paramFloat)
  {
    if (paramTabularType == null) {
      throw new IllegalArgumentException("Argument tabularType cannot be null.");
    }
    tabularType = paramTabularType;
    List localList = paramTabularType.getIndexNames();
    indexNamesArray = ((String[])localList.toArray(new String[localList.size()]));
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jmx.tabular.data.hash.map"));
    boolean bool = "true".equalsIgnoreCase(str);
    dataMap = (bool ? new HashMap(paramInt, paramFloat) : new LinkedHashMap(paramInt, paramFloat));
  }
  
  public TabularType getTabularType()
  {
    return tabularType;
  }
  
  public Object[] calculateIndex(CompositeData paramCompositeData)
  {
    checkValueType(paramCompositeData);
    return internalCalculateIndex(paramCompositeData).toArray();
  }
  
  public boolean containsKey(Object paramObject)
  {
    Object[] arrayOfObject;
    try
    {
      arrayOfObject = (Object[])paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    return containsKey(arrayOfObject);
  }
  
  public boolean containsKey(Object[] paramArrayOfObject)
  {
    return paramArrayOfObject == null ? false : dataMap.containsKey(Arrays.asList(paramArrayOfObject));
  }
  
  public boolean containsValue(CompositeData paramCompositeData)
  {
    return dataMap.containsValue(paramCompositeData);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return dataMap.containsValue(paramObject);
  }
  
  public Object get(Object paramObject)
  {
    return get((Object[])paramObject);
  }
  
  public CompositeData get(Object[] paramArrayOfObject)
  {
    checkKeyType(paramArrayOfObject);
    return (CompositeData)dataMap.get(Arrays.asList(paramArrayOfObject));
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    internalPut((CompositeData)paramObject2);
    return paramObject2;
  }
  
  public void put(CompositeData paramCompositeData)
  {
    internalPut(paramCompositeData);
  }
  
  private CompositeData internalPut(CompositeData paramCompositeData)
  {
    List localList = checkValueAndIndex(paramCompositeData);
    return (CompositeData)dataMap.put(localList, paramCompositeData);
  }
  
  public Object remove(Object paramObject)
  {
    return remove((Object[])paramObject);
  }
  
  public CompositeData remove(Object[] paramArrayOfObject)
  {
    checkKeyType(paramArrayOfObject);
    return (CompositeData)dataMap.remove(Arrays.asList(paramArrayOfObject));
  }
  
  public void putAll(Map<?, ?> paramMap)
  {
    if ((paramMap == null) || (paramMap.size() == 0)) {
      return;
    }
    CompositeData[] arrayOfCompositeData;
    try
    {
      arrayOfCompositeData = (CompositeData[])paramMap.values().toArray(new CompositeData[paramMap.size()]);
    }
    catch (ArrayStoreException localArrayStoreException)
    {
      throw new ClassCastException("Map argument t contains values which are not instances of <tt>CompositeData</tt>");
    }
    putAll(arrayOfCompositeData);
  }
  
  public void putAll(CompositeData[] paramArrayOfCompositeData)
  {
    if ((paramArrayOfCompositeData == null) || (paramArrayOfCompositeData.length == 0)) {
      return;
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfCompositeData.length + 1);
    for (int i = 0; i < paramArrayOfCompositeData.length; i++)
    {
      List localList = checkValueAndIndex(paramArrayOfCompositeData[i]);
      if (localArrayList.contains(localList)) {
        throw new KeyAlreadyExistsException("Argument elements values[" + i + "] and values[" + localArrayList.indexOf(localList) + "] have the same indexes, calculated according to this TabularData instance's tabularType.");
      }
      localArrayList.add(localList);
    }
    for (i = 0; i < paramArrayOfCompositeData.length; i++) {
      dataMap.put(localArrayList.get(i), paramArrayOfCompositeData[i]);
    }
  }
  
  public void clear()
  {
    dataMap.clear();
  }
  
  public int size()
  {
    return dataMap.size();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public Set<Object> keySet()
  {
    return dataMap.keySet();
  }
  
  public Collection<Object> values()
  {
    return (Collection)Util.cast(dataMap.values());
  }
  
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    return (Set)Util.cast(dataMap.entrySet());
  }
  
  public Object clone()
  {
    try
    {
      TabularDataSupport localTabularDataSupport = (TabularDataSupport)super.clone();
      dataMap = new HashMap(dataMap);
      return localTabularDataSupport;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    TabularData localTabularData;
    try
    {
      localTabularData = (TabularData)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    if (!getTabularType().equals(localTabularData.getTabularType())) {
      return false;
    }
    if (size() != localTabularData.size()) {
      return false;
    }
    Iterator localIterator = dataMap.values().iterator();
    while (localIterator.hasNext())
    {
      CompositeData localCompositeData = (CompositeData)localIterator.next();
      if (!localTabularData.containsValue(localCompositeData)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    i += tabularType.hashCode();
    Iterator localIterator = values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      i += localObject.hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    return getClass().getName() + "(tabularType=" + tabularType.toString() + ",contents=" + dataMap.toString() + ")";
  }
  
  private List<?> internalCalculateIndex(CompositeData paramCompositeData)
  {
    return Collections.unmodifiableList(Arrays.asList(paramCompositeData.getAll(indexNamesArray)));
  }
  
  private void checkKeyType(Object[] paramArrayOfObject)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) {
      throw new NullPointerException("Argument key cannot be null or empty.");
    }
    if (paramArrayOfObject.length != indexNamesArray.length) {
      throw new InvalidKeyException("Argument key's length=" + paramArrayOfObject.length + " is different from the number of item values, which is " + indexNamesArray.length + ", specified for the indexing rows in this TabularData instance.");
    }
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      OpenType localOpenType = tabularType.getRowType().getType(indexNamesArray[i]);
      if ((paramArrayOfObject[i] != null) && (!localOpenType.isValue(paramArrayOfObject[i]))) {
        throw new InvalidKeyException("Argument element key[" + i + "] is not a value for the open type expected for this element of the index, whose name is \"" + indexNamesArray[i] + "\" and whose open type is " + localOpenType);
      }
    }
  }
  
  private void checkValueType(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      throw new NullPointerException("Argument value cannot be null.");
    }
    if (!tabularType.getRowType().isValue(paramCompositeData)) {
      throw new InvalidOpenTypeException("Argument value's composite type [" + paramCompositeData.getCompositeType() + "] is not assignable to this TabularData instance's row type [" + tabularType.getRowType() + "].");
    }
  }
  
  private List<?> checkValueAndIndex(CompositeData paramCompositeData)
  {
    checkValueType(paramCompositeData);
    List localList = internalCalculateIndex(paramCompositeData);
    if (dataMap.containsKey(localList)) {
      throw new KeyAlreadyExistsException("Argument value's index, calculated according to this TabularData instance's tabularType, already refers to a value in this table.");
    }
    return localList;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    List localList = tabularType.getIndexNames();
    indexNamesArray = ((String[])localList.toArray(new String[localList.size()]));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\TabularDataSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */