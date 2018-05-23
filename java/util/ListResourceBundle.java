package java.util;

import sun.util.ResourceBundleEnumeration;

public abstract class ListResourceBundle
  extends ResourceBundle
{
  private Map<String, Object> lookup = null;
  
  public ListResourceBundle() {}
  
  public final Object handleGetObject(String paramString)
  {
    if (lookup == null) {
      loadLookup();
    }
    if (paramString == null) {
      throw new NullPointerException();
    }
    return lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys()
  {
    if (lookup == null) {
      loadLookup();
    }
    ResourceBundle localResourceBundle = parent;
    return new ResourceBundleEnumeration(lookup.keySet(), localResourceBundle != null ? localResourceBundle.getKeys() : null);
  }
  
  protected Set<String> handleKeySet()
  {
    if (lookup == null) {
      loadLookup();
    }
    return lookup.keySet();
  }
  
  protected abstract Object[][] getContents();
  
  private synchronized void loadLookup()
  {
    if (lookup != null) {
      return;
    }
    Object[][] arrayOfObject = getContents();
    HashMap localHashMap = new HashMap(arrayOfObject.length);
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      String str = (String)arrayOfObject[i][0];
      Object localObject = arrayOfObject[i][1];
      if ((str == null) || (localObject == null)) {
        throw new NullPointerException();
      }
      localHashMap.put(str, localObject);
    }
    lookup = localHashMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */