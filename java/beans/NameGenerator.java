package java.beans;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

class NameGenerator
{
  private Map<Object, String> valueToName = new IdentityHashMap();
  private Map<String, Integer> nameToCount = new HashMap();
  
  public NameGenerator() {}
  
  public void clear()
  {
    valueToName.clear();
    nameToCount.clear();
  }
  
  public static String unqualifiedClassName(Class paramClass)
  {
    if (paramClass.isArray()) {
      return unqualifiedClassName(paramClass.getComponentType()) + "Array";
    }
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  public static String capitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    return paramString.substring(0, 1).toUpperCase(Locale.ENGLISH) + paramString.substring(1);
  }
  
  public String instanceName(Object paramObject)
  {
    if (paramObject == null) {
      return "null";
    }
    if ((paramObject instanceof Class)) {
      return unqualifiedClassName((Class)paramObject);
    }
    String str1 = (String)valueToName.get(paramObject);
    if (str1 != null) {
      return str1;
    }
    Class localClass = paramObject.getClass();
    String str2 = unqualifiedClassName(localClass);
    Integer localInteger = (Integer)nameToCount.get(str2);
    int i = localInteger == null ? 0 : localInteger.intValue() + 1;
    nameToCount.put(str2, new Integer(i));
    str1 = str2 + i;
    valueToName.put(paramObject, str1);
    return str1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\NameGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */