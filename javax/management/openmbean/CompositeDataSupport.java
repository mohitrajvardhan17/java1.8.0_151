package javax.management.openmbean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class CompositeDataSupport
  implements CompositeData, Serializable
{
  static final long serialVersionUID = 8003518976613702244L;
  private final SortedMap<String, Object> contents;
  private final CompositeType compositeType;
  
  public CompositeDataSupport(CompositeType paramCompositeType, String[] paramArrayOfString, Object[] paramArrayOfObject)
    throws OpenDataException
  {
    this(makeMap(paramArrayOfString, paramArrayOfObject), paramCompositeType);
  }
  
  private static SortedMap<String, Object> makeMap(String[] paramArrayOfString, Object[] paramArrayOfObject)
    throws OpenDataException
  {
    if ((paramArrayOfString == null) || (paramArrayOfObject == null)) {
      throw new IllegalArgumentException("Null itemNames or itemValues");
    }
    if ((paramArrayOfString.length == 0) || (paramArrayOfObject.length == 0)) {
      throw new IllegalArgumentException("Empty itemNames or itemValues");
    }
    if (paramArrayOfString.length != paramArrayOfObject.length) {
      throw new IllegalArgumentException("Different lengths: itemNames[" + paramArrayOfString.length + "], itemValues[" + paramArrayOfObject.length + "]");
    }
    TreeMap localTreeMap = new TreeMap();
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = paramArrayOfString[i];
      if ((str == null) || (str.equals(""))) {
        throw new IllegalArgumentException("Null or empty item name");
      }
      if (localTreeMap.containsKey(str)) {
        throw new OpenDataException("Duplicate item name " + str);
      }
      localTreeMap.put(paramArrayOfString[i], paramArrayOfObject[i]);
    }
    return localTreeMap;
  }
  
  public CompositeDataSupport(CompositeType paramCompositeType, Map<String, ?> paramMap)
    throws OpenDataException
  {
    this(makeMap(paramMap), paramCompositeType);
  }
  
  private static SortedMap<String, Object> makeMap(Map<String, ?> paramMap)
  {
    if ((paramMap == null) || (paramMap.isEmpty())) {
      throw new IllegalArgumentException("Null or empty items map");
    }
    TreeMap localTreeMap = new TreeMap();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject == null) || (localObject.equals(""))) {
        throw new IllegalArgumentException("Null or empty item name");
      }
      if (!(localObject instanceof String)) {
        throw new ArrayStoreException("Item name is not string: " + localObject);
      }
      localTreeMap.put((String)localObject, paramMap.get(localObject));
    }
    return localTreeMap;
  }
  
  private CompositeDataSupport(SortedMap<String, Object> paramSortedMap, CompositeType paramCompositeType)
    throws OpenDataException
  {
    if (paramCompositeType == null) {
      throw new IllegalArgumentException("Argument compositeType cannot be null.");
    }
    Set localSet1 = paramCompositeType.keySet();
    Set localSet2 = paramSortedMap.keySet();
    Object localObject2;
    if (!localSet1.equals(localSet2))
    {
      localObject1 = new TreeSet(localSet1);
      ((Set)localObject1).removeAll(localSet2);
      localObject2 = new TreeSet(localSet2);
      ((Set)localObject2).removeAll(localSet1);
      if ((!((Set)localObject1).isEmpty()) || (!((Set)localObject2).isEmpty())) {
        throw new OpenDataException("Item names do not match CompositeType: names in items but not in CompositeType: " + localObject2 + "; names in CompositeType but not in items: " + localObject1);
      }
    }
    Object localObject1 = localSet1.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      Object localObject3 = paramSortedMap.get(localObject2);
      if (localObject3 != null)
      {
        OpenType localOpenType = paramCompositeType.getType((String)localObject2);
        if (!localOpenType.isValue(localObject3)) {
          throw new OpenDataException("Argument value of wrong type for item " + (String)localObject2 + ": value " + localObject3 + ", type " + localOpenType);
        }
      }
    }
    compositeType = paramCompositeType;
    contents = paramSortedMap;
  }
  
  public CompositeType getCompositeType()
  {
    return compositeType;
  }
  
  public Object get(String paramString)
  {
    if ((paramString == null) || (paramString.trim().equals(""))) {
      throw new IllegalArgumentException("Argument key cannot be a null or empty String.");
    }
    if (!contents.containsKey(paramString.trim())) {
      throw new InvalidKeyException("Argument key=\"" + paramString.trim() + "\" is not an existing item name for this CompositeData instance.");
    }
    return contents.get(paramString.trim());
  }
  
  public Object[] getAll(String[] paramArrayOfString)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      return new Object[0];
    }
    Object[] arrayOfObject = new Object[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++) {
      arrayOfObject[i] = get(paramArrayOfString[i]);
    }
    return arrayOfObject;
  }
  
  public boolean containsKey(String paramString)
  {
    if ((paramString == null) || (paramString.trim().equals(""))) {
      return false;
    }
    return contents.containsKey(paramString);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return contents.containsValue(paramObject);
  }
  
  public Collection<?> values()
  {
    return Collections.unmodifiableCollection(contents.values());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CompositeData)) {
      return false;
    }
    CompositeData localCompositeData = (CompositeData)paramObject;
    if (!getCompositeType().equals(localCompositeData.getCompositeType())) {
      return false;
    }
    if (contents.size() != localCompositeData.values().size()) {
      return false;
    }
    Iterator localIterator = contents.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject1 = localEntry.getValue();
      Object localObject2 = localCompositeData.get((String)localEntry.getKey());
      if (localObject1 != localObject2)
      {
        if (localObject1 == null) {
          return false;
        }
        boolean bool = localObject1.getClass().isArray() ? Arrays.deepEquals(new Object[] { localObject1 }, new Object[] { localObject2 }) : localObject1.equals(localObject2);
        if (!bool) {
          return false;
        }
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = compositeType.hashCode();
    Iterator localIterator = contents.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject instanceof Object[])) {
        i += Arrays.deepHashCode((Object[])localObject);
      } else if ((localObject instanceof byte[])) {
        i += Arrays.hashCode((byte[])localObject);
      } else if ((localObject instanceof short[])) {
        i += Arrays.hashCode((short[])localObject);
      } else if ((localObject instanceof int[])) {
        i += Arrays.hashCode((int[])localObject);
      } else if ((localObject instanceof long[])) {
        i += Arrays.hashCode((long[])localObject);
      } else if ((localObject instanceof char[])) {
        i += Arrays.hashCode((char[])localObject);
      } else if ((localObject instanceof float[])) {
        i += Arrays.hashCode((float[])localObject);
      } else if ((localObject instanceof double[])) {
        i += Arrays.hashCode((double[])localObject);
      } else if ((localObject instanceof boolean[])) {
        i += Arrays.hashCode((boolean[])localObject);
      } else if (localObject != null) {
        i += localObject.hashCode();
      }
    }
    return i;
  }
  
  public String toString()
  {
    return getClass().getName() + "(compositeType=" + compositeType.toString() + ",contents=" + contentString() + ")";
  }
  
  private String contentString()
  {
    StringBuilder localStringBuilder = new StringBuilder("{");
    String str1 = "";
    Iterator localIterator = contents.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localStringBuilder.append(str1).append((String)localEntry.getKey()).append("=");
      String str2 = Arrays.deepToString(new Object[] { localEntry.getValue() });
      localStringBuilder.append(str2.substring(1, str2.length() - 1));
      str1 = ", ";
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\CompositeDataSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */