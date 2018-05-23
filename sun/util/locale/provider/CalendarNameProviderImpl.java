package sun.util.locale.provider;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.spi.CalendarNameProvider;

public class CalendarNameProviderImpl
  extends CalendarNameProvider
  implements AvailableLanguageTags
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  private static int[] REST_OF_STYLES = { 32769, 2, 32770, 4, 32772 };
  
  public CalendarNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    type = paramType;
    langtags = paramSet;
  }
  
  public String getDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
  {
    return getDisplayNameImpl(paramString, paramInt1, paramInt2, paramInt3, paramLocale, false);
  }
  
  public String getJavaTimeDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
  {
    return getDisplayNameImpl(paramString, paramInt1, paramInt2, paramInt3, paramLocale, true);
  }
  
  public String getDisplayNameImpl(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale, boolean paramBoolean)
  {
    String str1 = null;
    String str2 = getResourceKey(paramString, paramInt1, paramInt3, paramBoolean);
    if (str2 != null)
    {
      LocaleResources localLocaleResources = LocaleProviderAdapter.forType(type).getLocaleResources(paramLocale);
      String[] arrayOfString = paramBoolean ? localLocaleResources.getJavaTimeNames(str2) : localLocaleResources.getCalendarNames(str2);
      if ((arrayOfString != null) && (arrayOfString.length > 0))
      {
        if ((paramInt1 == 7) || (paramInt1 == 1)) {
          paramInt2--;
        }
        if ((paramInt2 < 0) || (paramInt2 >= arrayOfString.length)) {
          return null;
        }
        str1 = arrayOfString[paramInt2];
        if ((str1.length() == 0) && ((paramInt3 == 32769) || (paramInt3 == 32770) || (paramInt3 == 32772))) {
          str1 = getDisplayName(paramString, paramInt1, paramInt2, getBaseStyle(paramInt3), paramLocale);
        }
      }
    }
    return str1;
  }
  
  public Map<String, Integer> getDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
  {
    Map localMap;
    if (paramInt2 == 0)
    {
      localMap = getDisplayNamesImpl(paramString, paramInt1, 1, paramLocale, false);
      for (int k : REST_OF_STYLES) {
        localMap.putAll(getDisplayNamesImpl(paramString, paramInt1, k, paramLocale, false));
      }
    }
    else
    {
      localMap = getDisplayNamesImpl(paramString, paramInt1, paramInt2, paramLocale, false);
    }
    return localMap.isEmpty() ? null : localMap;
  }
  
  public Map<String, Integer> getJavaTimeDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
  {
    Map localMap = getDisplayNamesImpl(paramString, paramInt1, paramInt2, paramLocale, true);
    return localMap.isEmpty() ? null : localMap;
  }
  
  private Map<String, Integer> getDisplayNamesImpl(String paramString, int paramInt1, int paramInt2, Locale paramLocale, boolean paramBoolean)
  {
    String str1 = getResourceKey(paramString, paramInt1, paramInt2, paramBoolean);
    TreeMap localTreeMap = new TreeMap(LengthBasedComparator.INSTANCE);
    if (str1 != null)
    {
      LocaleResources localLocaleResources = LocaleProviderAdapter.forType(type).getLocaleResources(paramLocale);
      String[] arrayOfString = paramBoolean ? localLocaleResources.getJavaTimeNames(str1) : localLocaleResources.getCalendarNames(str1);
      if ((arrayOfString != null) && (!hasDuplicates(arrayOfString))) {
        if (paramInt1 == 1)
        {
          if (arrayOfString.length > 0) {
            localTreeMap.put(arrayOfString[0], Integer.valueOf(1));
          }
        }
        else
        {
          int i = paramInt1 == 7 ? 1 : 0;
          for (int j = 0; j < arrayOfString.length; j++)
          {
            String str2 = arrayOfString[j];
            if (str2.length() != 0) {
              localTreeMap.put(str2, Integer.valueOf(i + j));
            }
          }
        }
      }
    }
    return localTreeMap;
  }
  
  private int getBaseStyle(int paramInt)
  {
    return paramInt & 0xFFFF7FFF;
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.toLocaleArray(langtags);
  }
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    if (Locale.ROOT.equals(paramLocale)) {
      return true;
    }
    String str1 = null;
    if (paramLocale.hasExtensions())
    {
      str1 = paramLocale.getUnicodeLocaleType("ca");
      paramLocale = paramLocale.stripExtensions();
    }
    if (str1 != null) {
      switch (str1)
      {
      case "buddhist": 
      case "gregory": 
      case "islamic": 
      case "japanese": 
      case "roc": 
        break;
      default: 
        return false;
      }
    }
    if (langtags.contains(paramLocale.toLanguageTag())) {
      return true;
    }
    if (type == LocaleProviderAdapter.Type.JRE)
    {
      ??? = paramLocale.toString().replace('_', '-');
      return langtags.contains(???);
    }
    return false;
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
  
  private boolean hasDuplicates(String[] paramArrayOfString)
  {
    int i = paramArrayOfString.length;
    for (int j = 0; j < i - 1; j++)
    {
      String str = paramArrayOfString[j];
      if (str != null) {
        for (int k = j + 1; k < i; k++) {
          if (str.equals(paramArrayOfString[k])) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  private String getResourceKey(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = getBaseStyle(paramInt2);
    int j = paramInt2 != i ? 1 : 0;
    if ("gregory".equals(paramString)) {
      paramString = null;
    }
    int k = i == 4 ? 1 : 0;
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramBoolean) {
      localStringBuilder.append("java.time.");
    }
    switch (paramInt1)
    {
    case 0: 
      if (paramString != null) {
        localStringBuilder.append(paramString).append('.');
      }
      if (k != 0)
      {
        localStringBuilder.append("narrow.");
      }
      else if (type == LocaleProviderAdapter.Type.JRE)
      {
        if ((paramBoolean) && (i == 2)) {
          localStringBuilder.append("long.");
        }
        if (i == 1) {
          localStringBuilder.append("short.");
        }
      }
      else if (i == 2)
      {
        localStringBuilder.append("long.");
      }
      localStringBuilder.append("Eras");
      break;
    case 1: 
      if (k == 0) {
        localStringBuilder.append(paramString).append(".FirstYear");
      }
      break;
    case 2: 
      if ("islamic".equals(paramString)) {
        localStringBuilder.append(paramString).append('.');
      }
      if (j != 0) {
        localStringBuilder.append("standalone.");
      }
      localStringBuilder.append("Month").append(toStyleName(i));
      break;
    case 7: 
      if ((j != 0) && (k != 0)) {
        localStringBuilder.append("standalone.");
      }
      localStringBuilder.append("Day").append(toStyleName(i));
      break;
    case 9: 
      if (k != 0) {
        localStringBuilder.append("narrow.");
      }
      localStringBuilder.append("AmPmMarkers");
    }
    return localStringBuilder.length() > 0 ? localStringBuilder.toString() : null;
  }
  
  private String toStyleName(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "Abbreviations";
    case 4: 
      return "Narrows";
    }
    return "Names";
  }
  
  private static class LengthBasedComparator
    implements Comparator<String>
  {
    private static final LengthBasedComparator INSTANCE = new LengthBasedComparator();
    
    private LengthBasedComparator() {}
    
    public int compare(String paramString1, String paramString2)
    {
      int i = paramString2.length() - paramString1.length();
      return i == 0 ? paramString1.compareTo(paramString2) : i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\CalendarNameProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */