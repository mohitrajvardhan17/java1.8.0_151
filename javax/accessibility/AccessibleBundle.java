package javax.accessibility;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class AccessibleBundle
{
  private static Hashtable table = new Hashtable();
  private final String defaultResourceBundleName = "com.sun.accessibility.internal.resources.accessibility";
  protected String key = null;
  
  public AccessibleBundle() {}
  
  protected String toDisplayString(String paramString, Locale paramLocale)
  {
    loadResourceBundle(paramString, paramLocale);
    Object localObject = table.get(paramLocale);
    if ((localObject != null) && ((localObject instanceof Hashtable)))
    {
      Hashtable localHashtable = (Hashtable)localObject;
      localObject = localHashtable.get(key);
      if ((localObject != null) && ((localObject instanceof String))) {
        return (String)localObject;
      }
    }
    return key;
  }
  
  public String toDisplayString(Locale paramLocale)
  {
    return toDisplayString("com.sun.accessibility.internal.resources.accessibility", paramLocale);
  }
  
  public String toDisplayString()
  {
    return toDisplayString(Locale.getDefault());
  }
  
  public String toString()
  {
    return toDisplayString();
  }
  
  private void loadResourceBundle(String paramString, Locale paramLocale)
  {
    if (!table.contains(paramLocale)) {
      try
      {
        Hashtable localHashtable = new Hashtable();
        ResourceBundle localResourceBundle = ResourceBundle.getBundle(paramString, paramLocale);
        Enumeration localEnumeration = localResourceBundle.getKeys();
        while (localEnumeration.hasMoreElements())
        {
          String str = (String)localEnumeration.nextElement();
          localHashtable.put(str, localResourceBundle.getObject(str));
        }
        table.put(paramLocale, localHashtable);
      }
      catch (MissingResourceException localMissingResourceException)
      {
        System.err.println("loadResourceBundle: " + localMissingResourceException);
        return;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */