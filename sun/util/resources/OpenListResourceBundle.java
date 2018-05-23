package sun.util.resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.ResourceBundleEnumeration;

public abstract class OpenListResourceBundle
  extends ResourceBundle
{
  private volatile Map<String, Object> lookup = null;
  private volatile Set<String> keyset;
  
  protected OpenListResourceBundle() {}
  
  protected Object handleGetObject(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    loadLookupTablesIfNecessary();
    return lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys()
  {
    ResourceBundle localResourceBundle = parent;
    return new ResourceBundleEnumeration(handleKeySet(), localResourceBundle != null ? localResourceBundle.getKeys() : null);
  }
  
  protected Set<String> handleKeySet()
  {
    loadLookupTablesIfNecessary();
    return lookup.keySet();
  }
  
  public Set<String> keySet()
  {
    if (keyset != null) {
      return keyset;
    }
    Set localSet = createSet();
    localSet.addAll(handleKeySet());
    if (parent != null) {
      localSet.addAll(parent.keySet());
    }
    synchronized (this)
    {
      if (keyset == null) {
        keyset = localSet;
      }
    }
    return keyset;
  }
  
  protected abstract Object[][] getContents();
  
  void loadLookupTablesIfNecessary()
  {
    if (lookup == null) {
      loadLookup();
    }
  }
  
  private void loadLookup()
  {
    Object[][] arrayOfObject = getContents();
    Map localMap = createMap(arrayOfObject.length);
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      String str = (String)arrayOfObject[i][0];
      Object localObject1 = arrayOfObject[i][1];
      if ((str == null) || (localObject1 == null)) {
        throw new NullPointerException();
      }
      localMap.put(str, localObject1);
    }
    synchronized (this)
    {
      if (lookup == null) {
        lookup = localMap;
      }
    }
  }
  
  protected <K, V> Map<K, V> createMap(int paramInt)
  {
    return new HashMap(paramInt);
  }
  
  protected <E> Set<E> createSet()
  {
    return new HashSet();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\resources\OpenListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */