package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

final class PropMap
  implements SortedMap<String, String>
{
  private final TreeMap<String, String> theMap = new TreeMap();
  private final List<Object> listenerList = new ArrayList(1);
  private static Map<String, String> defaultProps;
  
  void addListener(Object paramObject)
  {
    assert (Beans.isPropertyChangeListener(paramObject));
    listenerList.add(paramObject);
  }
  
  void removeListener(Object paramObject)
  {
    assert (Beans.isPropertyChangeListener(paramObject));
    listenerList.remove(paramObject);
  }
  
  public String put(String paramString1, String paramString2)
  {
    String str = (String)theMap.put(paramString1, paramString2);
    if ((paramString2 != str) && (!listenerList.isEmpty()))
    {
      assert (Beans.isBeansPresent());
      Object localObject1 = Beans.newPropertyChangeEvent(this, paramString1, str, paramString2);
      Iterator localIterator = listenerList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        Beans.invokePropertyChange(localObject2, localObject1);
      }
    }
    return str;
  }
  
  PropMap()
  {
    theMap.putAll(defaultProps);
  }
  
  SortedMap<String, String> prefixMap(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return this;
    }
    char c = (char)(paramString.charAt(i - 1) + '\001');
    String str = paramString.substring(0, i - 1) + c;
    return subMap(paramString, str);
  }
  
  String getProperty(String paramString)
  {
    return get(paramString);
  }
  
  String getProperty(String paramString1, String paramString2)
  {
    String str = getProperty(paramString1);
    if (str == null) {
      return paramString2;
    }
    return str;
  }
  
  String setProperty(String paramString1, String paramString2)
  {
    return put(paramString1, paramString2);
  }
  
  List<String> getProperties(String paramString)
  {
    Collection localCollection = prefixMap(paramString).values();
    ArrayList localArrayList = new ArrayList(localCollection.size());
    localArrayList.addAll(localCollection);
    while (localArrayList.remove(null)) {}
    return localArrayList;
  }
  
  private boolean toBoolean(String paramString)
  {
    return Boolean.valueOf(paramString).booleanValue();
  }
  
  boolean getBoolean(String paramString)
  {
    return toBoolean(getProperty(paramString));
  }
  
  boolean setBoolean(String paramString, boolean paramBoolean)
  {
    return toBoolean(setProperty(paramString, String.valueOf(paramBoolean)));
  }
  
  int toInteger(String paramString)
  {
    return toInteger(paramString, 0);
  }
  
  int toInteger(String paramString, int paramInt)
  {
    if (paramString == null) {
      return paramInt;
    }
    if ("true".equals(paramString)) {
      return 1;
    }
    if ("false".equals(paramString)) {
      return 0;
    }
    return Integer.parseInt(paramString);
  }
  
  int getInteger(String paramString, int paramInt)
  {
    return toInteger(getProperty(paramString), paramInt);
  }
  
  int getInteger(String paramString)
  {
    return toInteger(getProperty(paramString));
  }
  
  int setInteger(String paramString, int paramInt)
  {
    return toInteger(setProperty(paramString, String.valueOf(paramInt)));
  }
  
  long toLong(String paramString)
  {
    try
    {
      return paramString == null ? 0L : Long.parseLong(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IllegalArgumentException("Invalid value");
    }
  }
  
  long getLong(String paramString)
  {
    return toLong(getProperty(paramString));
  }
  
  long setLong(String paramString, long paramLong)
  {
    return toLong(setProperty(paramString, String.valueOf(paramLong)));
  }
  
  int getTime(String paramString)
  {
    String str = getProperty(paramString, "0");
    if ("now".equals(str)) {
      return (int)((System.currentTimeMillis() + 500L) / 1000L);
    }
    long l = toLong(str);
    if ((l < 10000000000L) && (!"0".equals(str))) {
      Utils.log.warning("Supplied modtime appears to be seconds rather than milliseconds: " + str);
    }
    return (int)((l + 500L) / 1000L);
  }
  
  void list(PrintStream paramPrintStream)
  {
    PrintWriter localPrintWriter = new PrintWriter(paramPrintStream);
    list(localPrintWriter);
    localPrintWriter.flush();
  }
  
  void list(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("#PACK200[");
    Set localSet = defaultProps.entrySet();
    Iterator localIterator = theMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (!localSet.contains(localEntry)) {
        paramPrintWriter.println("  " + (String)localEntry.getKey() + " = " + (String)localEntry.getValue());
      }
    }
    paramPrintWriter.println("#]");
  }
  
  public int size()
  {
    return theMap.size();
  }
  
  public boolean isEmpty()
  {
    return theMap.isEmpty();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return theMap.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return theMap.containsValue(paramObject);
  }
  
  public String get(Object paramObject)
  {
    return (String)theMap.get(paramObject);
  }
  
  public String remove(Object paramObject)
  {
    return (String)theMap.remove(paramObject);
  }
  
  public void putAll(Map<? extends String, ? extends String> paramMap)
  {
    theMap.putAll(paramMap);
  }
  
  public void clear()
  {
    theMap.clear();
  }
  
  public Set<String> keySet()
  {
    return theMap.keySet();
  }
  
  public Collection<String> values()
  {
    return theMap.values();
  }
  
  public Set<Map.Entry<String, String>> entrySet()
  {
    return theMap.entrySet();
  }
  
  public Comparator<? super String> comparator()
  {
    return theMap.comparator();
  }
  
  public SortedMap<String, String> subMap(String paramString1, String paramString2)
  {
    return theMap.subMap(paramString1, paramString2);
  }
  
  public SortedMap<String, String> headMap(String paramString)
  {
    return theMap.headMap(paramString);
  }
  
  public SortedMap<String, String> tailMap(String paramString)
  {
    return theMap.tailMap(paramString);
  }
  
  public String firstKey()
  {
    return (String)theMap.firstKey();
  }
  
  public String lastKey()
  {
    return (String)theMap.lastKey();
  }
  
  static
  {
    Properties localProperties = new Properties();
    localProperties.put("com.sun.java.util.jar.pack.disable.native", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.disable.native")));
    localProperties.put("com.sun.java.util.jar.pack.verbose", String.valueOf(Integer.getInteger("com.sun.java.util.jar.pack.verbose", 0)));
    localProperties.put("com.sun.java.util.jar.pack.default.timezone", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.default.timezone")));
    localProperties.put("pack.segment.limit", "-1");
    localProperties.put("pack.keep.file.order", "true");
    localProperties.put("pack.modification.time", "keep");
    localProperties.put("pack.deflate.hint", "keep");
    localProperties.put("pack.unknown.attribute", "pass");
    localProperties.put("com.sun.java.util.jar.pack.class.format.error", System.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass"));
    localProperties.put("pack.effort", "5");
    String str1 = "intrinsic.properties";
    Object localObject2;
    try
    {
      InputStream localInputStream = PackerImpl.class.getResourceAsStream(str1);
      localObject2 = null;
      try
      {
        if (localInputStream == null) {
          throw new RuntimeException(str1 + " cannot be loaded");
        }
        localProperties.load(localInputStream);
      }
      catch (Throwable localThrowable2)
      {
        localObject2 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStream != null) {
          if (localObject2 != null) {
            try
            {
              localInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject2).addSuppressed(localThrowable3);
            }
          } else {
            localInputStream.close();
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    Object localObject1 = localProperties.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      String str2 = (String)((Map.Entry)localObject2).getKey();
      String str3 = (String)((Map.Entry)localObject2).getValue();
      if (str2.startsWith("attribute.")) {
        ((Map.Entry)localObject2).setValue(Attribute.normalizeLayoutString(str3));
      }
    }
    localObject1 = new HashMap(localProperties);
    defaultProps = (Map)localObject1;
  }
  
  private static class Beans
  {
    private static final Class<?> propertyChangeListenerClass = getClass("java.beans.PropertyChangeListener");
    private static final Class<?> propertyChangeEventClass = getClass("java.beans.PropertyChangeEvent");
    private static final Method propertyChangeMethod = getMethod(propertyChangeListenerClass, "propertyChange", new Class[] { propertyChangeEventClass });
    private static final Constructor<?> propertyEventCtor = getConstructor(propertyChangeEventClass, new Class[] { Object.class, String.class, Object.class, Object.class });
    
    private Beans() {}
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, Beans.class.getClassLoader());
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return null;
    }
    
    private static Constructor<?> getConstructor(Class<?> paramClass, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass == null ? null : paramClass.getDeclaredConstructor(paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass == null ? null : paramClass.getMethod(paramString, paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    static boolean isBeansPresent()
    {
      return (propertyChangeListenerClass != null) && (propertyChangeEventClass != null);
    }
    
    static boolean isPropertyChangeListener(Object paramObject)
    {
      if (propertyChangeListenerClass == null) {
        return false;
      }
      return propertyChangeListenerClass.isInstance(paramObject);
    }
    
    static Object newPropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3)
    {
      try
      {
        return propertyEventCtor.newInstance(new Object[] { paramObject1, paramString, paramObject2, paramObject3 });
      }
      catch (InstantiationException|IllegalAccessException localInstantiationException)
      {
        throw new AssertionError(localInstantiationException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
    }
    
    static void invokePropertyChange(Object paramObject1, Object paramObject2)
    {
      try
      {
        propertyChangeMethod.invoke(paramObject1, new Object[] { paramObject2 });
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\PropMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */