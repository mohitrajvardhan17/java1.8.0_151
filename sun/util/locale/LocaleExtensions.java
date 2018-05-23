package sun.util.locale;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class LocaleExtensions
{
  private final Map<Character, Extension> extensionMap;
  private final String id;
  public static final LocaleExtensions CALENDAR_JAPANESE = new LocaleExtensions("u-ca-japanese", Character.valueOf('u'), UnicodeLocaleExtension.CA_JAPANESE);
  public static final LocaleExtensions NUMBER_THAI = new LocaleExtensions("u-nu-thai", Character.valueOf('u'), UnicodeLocaleExtension.NU_THAI);
  
  private LocaleExtensions(String paramString, Character paramCharacter, Extension paramExtension)
  {
    id = paramString;
    extensionMap = Collections.singletonMap(paramCharacter, paramExtension);
  }
  
  LocaleExtensions(Map<InternalLocaleBuilder.CaseInsensitiveChar, String> paramMap, Set<InternalLocaleBuilder.CaseInsensitiveString> paramSet, Map<InternalLocaleBuilder.CaseInsensitiveString, String> paramMap1)
  {
    int i = !LocaleUtils.isEmpty(paramMap) ? 1 : 0;
    int j = !LocaleUtils.isEmpty(paramSet) ? 1 : 0;
    int k = !LocaleUtils.isEmpty(paramMap1) ? 1 : 0;
    if ((i == 0) && (j == 0) && (k == 0))
    {
      id = "";
      extensionMap = Collections.emptyMap();
      return;
    }
    TreeMap localTreeMap = new TreeMap();
    Object localObject1;
    Object localObject2;
    Object localObject4;
    if (i != 0)
    {
      localObject1 = paramMap.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        char c = LocaleUtils.toLower(((InternalLocaleBuilder.CaseInsensitiveChar)((Map.Entry)localObject2).getKey()).value());
        localObject4 = (String)((Map.Entry)localObject2).getValue();
        if (LanguageTag.isPrivateusePrefixChar(c))
        {
          localObject4 = InternalLocaleBuilder.removePrivateuseVariant((String)localObject4);
          if (localObject4 == null) {}
        }
        else
        {
          localTreeMap.put(Character.valueOf(c), new Extension(c, LocaleUtils.toLowerString((String)localObject4)));
        }
      }
    }
    if ((j != 0) || (k != 0))
    {
      localObject1 = null;
      localObject2 = null;
      if (j != 0)
      {
        localObject1 = new TreeSet();
        localObject3 = paramSet.iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (InternalLocaleBuilder.CaseInsensitiveString)((Iterator)localObject3).next();
          ((SortedSet)localObject1).add(LocaleUtils.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)localObject4).value()));
        }
      }
      if (k != 0)
      {
        localObject2 = new TreeMap();
        localObject3 = paramMap1.entrySet().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (Map.Entry)((Iterator)localObject3).next();
          String str1 = LocaleUtils.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)((Map.Entry)localObject4).getKey()).value());
          String str2 = LocaleUtils.toLowerString((String)((Map.Entry)localObject4).getValue());
          ((SortedMap)localObject2).put(str1, str2);
        }
      }
      Object localObject3 = new UnicodeLocaleExtension((SortedSet)localObject1, (SortedMap)localObject2);
      localTreeMap.put(Character.valueOf('u'), localObject3);
    }
    if (localTreeMap.isEmpty())
    {
      id = "";
      extensionMap = Collections.emptyMap();
    }
    else
    {
      id = toID(localTreeMap);
      extensionMap = localTreeMap;
    }
  }
  
  public Set<Character> getKeys()
  {
    if (extensionMap.isEmpty()) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(extensionMap.keySet());
  }
  
  public Extension getExtension(Character paramCharacter)
  {
    return (Extension)extensionMap.get(Character.valueOf(LocaleUtils.toLower(paramCharacter.charValue())));
  }
  
  public String getExtensionValue(Character paramCharacter)
  {
    Extension localExtension = (Extension)extensionMap.get(Character.valueOf(LocaleUtils.toLower(paramCharacter.charValue())));
    if (localExtension == null) {
      return null;
    }
    return localExtension.getValue();
  }
  
  public Set<String> getUnicodeLocaleAttributes()
  {
    Extension localExtension = (Extension)extensionMap.get(Character.valueOf('u'));
    if (localExtension == null) {
      return Collections.emptySet();
    }
    assert ((localExtension instanceof UnicodeLocaleExtension));
    return ((UnicodeLocaleExtension)localExtension).getUnicodeLocaleAttributes();
  }
  
  public Set<String> getUnicodeLocaleKeys()
  {
    Extension localExtension = (Extension)extensionMap.get(Character.valueOf('u'));
    if (localExtension == null) {
      return Collections.emptySet();
    }
    assert ((localExtension instanceof UnicodeLocaleExtension));
    return ((UnicodeLocaleExtension)localExtension).getUnicodeLocaleKeys();
  }
  
  public String getUnicodeLocaleType(String paramString)
  {
    Extension localExtension = (Extension)extensionMap.get(Character.valueOf('u'));
    if (localExtension == null) {
      return null;
    }
    assert ((localExtension instanceof UnicodeLocaleExtension));
    return ((UnicodeLocaleExtension)localExtension).getUnicodeLocaleType(LocaleUtils.toLowerString(paramString));
  }
  
  public boolean isEmpty()
  {
    return extensionMap.isEmpty();
  }
  
  public static boolean isValidKey(char paramChar)
  {
    return (LanguageTag.isExtensionSingletonChar(paramChar)) || (LanguageTag.isPrivateusePrefixChar(paramChar));
  }
  
  public static boolean isValidUnicodeLocaleKey(String paramString)
  {
    return UnicodeLocaleExtension.isKey(paramString);
  }
  
  private static String toID(SortedMap<Character, Extension> paramSortedMap)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject = null;
    Iterator localIterator = paramSortedMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      char c = ((Character)localEntry.getKey()).charValue();
      Extension localExtension = (Extension)localEntry.getValue();
      if (LanguageTag.isPrivateusePrefixChar(c))
      {
        localObject = localExtension;
      }
      else
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append("-");
        }
        localStringBuilder.append(localExtension);
      }
    }
    if (localObject != null)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("-");
      }
      localStringBuilder.append(localObject);
    }
    return localStringBuilder.toString();
  }
  
  public String toString()
  {
    return id;
  }
  
  public String getID()
  {
    return id;
  }
  
  public int hashCode()
  {
    return id.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof LocaleExtensions)) {
      return false;
    }
    return id.equals(id);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\LocaleExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */