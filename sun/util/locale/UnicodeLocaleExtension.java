package sun.util.locale;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class UnicodeLocaleExtension
  extends Extension
{
  public static final char SINGLETON = 'u';
  private final Set<String> attributes;
  private final Map<String, String> keywords;
  public static final UnicodeLocaleExtension CA_JAPANESE = new UnicodeLocaleExtension("ca", "japanese");
  public static final UnicodeLocaleExtension NU_THAI = new UnicodeLocaleExtension("nu", "thai");
  
  private UnicodeLocaleExtension(String paramString1, String paramString2)
  {
    super('u', paramString1 + "-" + paramString2);
    attributes = Collections.emptySet();
    keywords = Collections.singletonMap(paramString1, paramString2);
  }
  
  UnicodeLocaleExtension(SortedSet<String> paramSortedSet, SortedMap<String, String> paramSortedMap)
  {
    super('u');
    if (paramSortedSet != null) {
      attributes = paramSortedSet;
    } else {
      attributes = Collections.emptySet();
    }
    if (paramSortedMap != null) {
      keywords = paramSortedMap;
    } else {
      keywords = Collections.emptyMap();
    }
    if ((!attributes.isEmpty()) || (!keywords.isEmpty()))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = attributes.iterator();
      Object localObject;
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        localStringBuilder.append("-").append((String)localObject);
      }
      localIterator = keywords.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (Map.Entry)localIterator.next();
        String str1 = (String)((Map.Entry)localObject).getKey();
        String str2 = (String)((Map.Entry)localObject).getValue();
        localStringBuilder.append("-").append(str1);
        if (str2.length() > 0) {
          localStringBuilder.append("-").append(str2);
        }
      }
      setValue(localStringBuilder.substring(1));
    }
  }
  
  public Set<String> getUnicodeLocaleAttributes()
  {
    if (attributes == Collections.EMPTY_SET) {
      return attributes;
    }
    return Collections.unmodifiableSet(attributes);
  }
  
  public Set<String> getUnicodeLocaleKeys()
  {
    if (keywords == Collections.EMPTY_MAP) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(keywords.keySet());
  }
  
  public String getUnicodeLocaleType(String paramString)
  {
    return (String)keywords.get(paramString);
  }
  
  public static boolean isSingletonChar(char paramChar)
  {
    return 'u' == LocaleUtils.toLower(paramChar);
  }
  
  public static boolean isAttribute(String paramString)
  {
    int i = paramString.length();
    return (i >= 3) && (i <= 8) && (LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static boolean isKey(String paramString)
  {
    return (paramString.length() == 2) && (LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static boolean isTypeSubtag(String paramString)
  {
    int i = paramString.length();
    return (i >= 3) && (i <= 8) && (LocaleUtils.isAlphaNumericString(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\UnicodeLocaleExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */