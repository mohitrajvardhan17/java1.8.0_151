package java.lang;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

final class ProcessEnvironment
  extends HashMap<String, String>
{
  private static final long serialVersionUID = -8017839552603542824L;
  static final int MIN_NAME_LENGTH = 1;
  private static final NameComparator nameComparator = new NameComparator(null);
  private static final EntryComparator entryComparator = new EntryComparator(null);
  private static final ProcessEnvironment theEnvironment = new ProcessEnvironment();
  private static final Map<String, String> theUnmodifiableEnvironment = Collections.unmodifiableMap(theEnvironment);
  private static final Map<String, String> theCaseInsensitiveEnvironment;
  
  private static String validateName(String paramString)
  {
    if ((paramString.indexOf('=', 1) != -1) || (paramString.indexOf(0) != -1)) {
      throw new IllegalArgumentException("Invalid environment variable name: \"" + paramString + "\"");
    }
    return paramString;
  }
  
  private static String validateValue(String paramString)
  {
    if (paramString.indexOf(0) != -1) {
      throw new IllegalArgumentException("Invalid environment variable value: \"" + paramString + "\"");
    }
    return paramString;
  }
  
  private static String nonNullString(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    return (String)paramObject;
  }
  
  public String put(String paramString1, String paramString2)
  {
    return (String)super.put(validateName(paramString1), validateValue(paramString2));
  }
  
  public String get(Object paramObject)
  {
    return (String)super.get(nonNullString(paramObject));
  }
  
  public boolean containsKey(Object paramObject)
  {
    return super.containsKey(nonNullString(paramObject));
  }
  
  public boolean containsValue(Object paramObject)
  {
    return super.containsValue(nonNullString(paramObject));
  }
  
  public String remove(Object paramObject)
  {
    return (String)super.remove(nonNullString(paramObject));
  }
  
  public Set<String> keySet()
  {
    return new CheckedKeySet(super.keySet());
  }
  
  public Collection<String> values()
  {
    return new CheckedValues(super.values());
  }
  
  public Set<Map.Entry<String, String>> entrySet()
  {
    return new CheckedEntrySet(super.entrySet());
  }
  
  private ProcessEnvironment() {}
  
  private ProcessEnvironment(int paramInt)
  {
    super(paramInt);
  }
  
  static String getenv(String paramString)
  {
    return (String)theCaseInsensitiveEnvironment.get(paramString);
  }
  
  static Map<String, String> getenv()
  {
    return theUnmodifiableEnvironment;
  }
  
  static Map<String, String> environment()
  {
    return (Map)theEnvironment.clone();
  }
  
  static Map<String, String> emptyEnvironment(int paramInt)
  {
    return new ProcessEnvironment(paramInt);
  }
  
  private static native String environmentBlock();
  
  String toEnvironmentBlock()
  {
    ArrayList localArrayList = new ArrayList(entrySet());
    Collections.sort(localArrayList, entryComparator);
    StringBuilder localStringBuilder = new StringBuilder(size() * 30);
    int i = -1;
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      String str2 = (String)localEntry.getValue();
      if ((i < 0) && ((i = nameComparator.compare(str1, "SystemRoot")) > 0)) {
        addToEnvIfSet(localStringBuilder, "SystemRoot");
      }
      addToEnv(localStringBuilder, str1, str2);
    }
    if (i < 0) {
      addToEnvIfSet(localStringBuilder, "SystemRoot");
    }
    if (localStringBuilder.length() == 0) {
      localStringBuilder.append('\000');
    }
    localStringBuilder.append('\000');
    return localStringBuilder.toString();
  }
  
  private static void addToEnvIfSet(StringBuilder paramStringBuilder, String paramString)
  {
    String str = getenv(paramString);
    if (str != null) {
      addToEnv(paramStringBuilder, paramString, str);
    }
  }
  
  private static void addToEnv(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    paramStringBuilder.append(paramString1).append('=').append(paramString2).append('\000');
  }
  
  static String toEnvironmentBlock(Map<String, String> paramMap)
  {
    return paramMap == null ? null : ((ProcessEnvironment)paramMap).toEnvironmentBlock();
  }
  
  static
  {
    String str = environmentBlock();
    int j;
    int k;
    for (int i = 0; ((j = str.indexOf(0, i)) != -1) && ((k = str.indexOf('=', i + 1)) != -1); i = j + 1) {
      if (k < j) {
        theEnvironment.put(str.substring(i, k), str.substring(k + 1, j));
      }
    }
    theCaseInsensitiveEnvironment = new TreeMap(nameComparator);
    theCaseInsensitiveEnvironment.putAll(theEnvironment);
  }
  
  private static class CheckedEntry
    implements Map.Entry<String, String>
  {
    private final Map.Entry<String, String> e;
    
    public CheckedEntry(Map.Entry<String, String> paramEntry)
    {
      e = paramEntry;
    }
    
    public String getKey()
    {
      return (String)e.getKey();
    }
    
    public String getValue()
    {
      return (String)e.getValue();
    }
    
    public String setValue(String paramString)
    {
      return (String)e.setValue(ProcessEnvironment.validateValue(paramString));
    }
    
    public String toString()
    {
      return getKey() + "=" + getValue();
    }
    
    public boolean equals(Object paramObject)
    {
      return e.equals(paramObject);
    }
    
    public int hashCode()
    {
      return e.hashCode();
    }
  }
  
  private static class CheckedEntrySet
    extends AbstractSet<Map.Entry<String, String>>
  {
    private final Set<Map.Entry<String, String>> s;
    
    public CheckedEntrySet(Set<Map.Entry<String, String>> paramSet)
    {
      s = paramSet;
    }
    
    public int size()
    {
      return s.size();
    }
    
    public boolean isEmpty()
    {
      return s.isEmpty();
    }
    
    public void clear()
    {
      s.clear();
    }
    
    public Iterator<Map.Entry<String, String>> iterator()
    {
      new Iterator()
      {
        Iterator<Map.Entry<String, String>> i = s.iterator();
        
        public boolean hasNext()
        {
          return i.hasNext();
        }
        
        public Map.Entry<String, String> next()
        {
          return new ProcessEnvironment.CheckedEntry((Map.Entry)i.next());
        }
        
        public void remove()
        {
          i.remove();
        }
      };
    }
    
    private static Map.Entry<String, String> checkedEntry(Object paramObject)
    {
      Map.Entry localEntry = (Map.Entry)paramObject;
      ProcessEnvironment.nonNullString(localEntry.getKey());
      ProcessEnvironment.nonNullString(localEntry.getValue());
      return localEntry;
    }
    
    public boolean contains(Object paramObject)
    {
      return s.contains(checkedEntry(paramObject));
    }
    
    public boolean remove(Object paramObject)
    {
      return s.remove(checkedEntry(paramObject));
    }
  }
  
  private static class CheckedKeySet
    extends AbstractSet<String>
  {
    private final Set<String> s;
    
    public CheckedKeySet(Set<String> paramSet)
    {
      s = paramSet;
    }
    
    public int size()
    {
      return s.size();
    }
    
    public boolean isEmpty()
    {
      return s.isEmpty();
    }
    
    public void clear()
    {
      s.clear();
    }
    
    public Iterator<String> iterator()
    {
      return s.iterator();
    }
    
    public boolean contains(Object paramObject)
    {
      return s.contains(ProcessEnvironment.nonNullString(paramObject));
    }
    
    public boolean remove(Object paramObject)
    {
      return s.remove(ProcessEnvironment.nonNullString(paramObject));
    }
  }
  
  private static class CheckedValues
    extends AbstractCollection<String>
  {
    private final Collection<String> c;
    
    public CheckedValues(Collection<String> paramCollection)
    {
      c = paramCollection;
    }
    
    public int size()
    {
      return c.size();
    }
    
    public boolean isEmpty()
    {
      return c.isEmpty();
    }
    
    public void clear()
    {
      c.clear();
    }
    
    public Iterator<String> iterator()
    {
      return c.iterator();
    }
    
    public boolean contains(Object paramObject)
    {
      return c.contains(ProcessEnvironment.nonNullString(paramObject));
    }
    
    public boolean remove(Object paramObject)
    {
      return c.remove(ProcessEnvironment.nonNullString(paramObject));
    }
  }
  
  private static final class EntryComparator
    implements Comparator<Map.Entry<String, String>>
  {
    private EntryComparator() {}
    
    public int compare(Map.Entry<String, String> paramEntry1, Map.Entry<String, String> paramEntry2)
    {
      return ProcessEnvironment.nameComparator.compare((String)paramEntry1.getKey(), (String)paramEntry2.getKey());
    }
  }
  
  private static final class NameComparator
    implements Comparator<String>
  {
    private NameComparator() {}
    
    public int compare(String paramString1, String paramString2)
    {
      int i = paramString1.length();
      int j = paramString2.length();
      int k = Math.min(i, j);
      for (int m = 0; m < k; m++)
      {
        char c1 = paramString1.charAt(m);
        char c2 = paramString2.charAt(m);
        if (c1 != c2)
        {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2) {
            return c1 - c2;
          }
        }
      }
      return i - j;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ProcessEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */