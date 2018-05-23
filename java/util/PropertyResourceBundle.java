package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import sun.util.ResourceBundleEnumeration;

public class PropertyResourceBundle
  extends ResourceBundle
{
  private Map<String, Object> lookup;
  
  public PropertyResourceBundle(InputStream paramInputStream)
    throws IOException
  {
    Properties localProperties = new Properties();
    localProperties.load(paramInputStream);
    lookup = new HashMap(localProperties);
  }
  
  public PropertyResourceBundle(Reader paramReader)
    throws IOException
  {
    Properties localProperties = new Properties();
    localProperties.load(paramReader);
    lookup = new HashMap(localProperties);
  }
  
  public Object handleGetObject(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    return lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys()
  {
    ResourceBundle localResourceBundle = parent;
    return new ResourceBundleEnumeration(lookup.keySet(), localResourceBundle != null ? localResourceBundle.getKeys() : null);
  }
  
  protected Set<String> handleKeySet()
  {
    return lookup.keySet();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\PropertyResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */