package sun.util.locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Locale.FilteringMode;
import java.util.Locale.LanguageRange;
import java.util.Map;
import java.util.Set;

public final class LocaleMatcher
{
  public static List<Locale> filter(List<Locale.LanguageRange> paramList, Collection<Locale> paramCollection, Locale.FilteringMode paramFilteringMode)
  {
    if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
      return new ArrayList();
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = paramCollection.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Locale)((Iterator)localObject1).next();
      localArrayList.add(((Locale)localObject2).toLanguageTag());
    }
    localObject1 = filterTags(paramList, localArrayList, paramFilteringMode);
    Object localObject2 = new ArrayList(((List)localObject1).size());
    Iterator localIterator = ((List)localObject1).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      ((List)localObject2).add(Locale.forLanguageTag(str));
    }
    return (List<Locale>)localObject2;
  }
  
  public static List<String> filterTags(List<Locale.LanguageRange> paramList, Collection<String> paramCollection, Locale.FilteringMode paramFilteringMode)
  {
    if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
      return new ArrayList();
    }
    if (paramFilteringMode == Locale.FilteringMode.EXTENDED_FILTERING) {
      return filterExtended(paramList, paramCollection);
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Locale.LanguageRange localLanguageRange = (Locale.LanguageRange)localIterator.next();
      String str = localLanguageRange.getRange();
      if ((str.startsWith("*-")) || (str.indexOf("-*") != -1))
      {
        if (paramFilteringMode == Locale.FilteringMode.AUTOSELECT_FILTERING) {
          return filterExtended(paramList, paramCollection);
        }
        if (paramFilteringMode == Locale.FilteringMode.MAP_EXTENDED_RANGES)
        {
          if (str.charAt(0) == '*') {
            str = "*";
          } else {
            str = str.replaceAll("-[*]", "");
          }
          localArrayList.add(new Locale.LanguageRange(str, localLanguageRange.getWeight()));
        }
        else if (paramFilteringMode == Locale.FilteringMode.REJECT_EXTENDED_RANGES)
        {
          throw new IllegalArgumentException("An extended range \"" + str + "\" found in REJECT_EXTENDED_RANGES mode.");
        }
      }
      else
      {
        localArrayList.add(localLanguageRange);
      }
    }
    return filterBasic(localArrayList, paramCollection);
  }
  
  private static List<String> filterBasic(List<Locale.LanguageRange> paramList, Collection<String> paramCollection)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      Locale.LanguageRange localLanguageRange = (Locale.LanguageRange)localIterator1.next();
      String str1 = localLanguageRange.getRange();
      if (str1.equals("*")) {
        return new ArrayList(paramCollection);
      }
      Iterator localIterator2 = paramCollection.iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        str2 = str2.toLowerCase();
        if (str2.startsWith(str1))
        {
          int i = str1.length();
          if (((str2.length() == i) || (str2.charAt(i) == '-')) && (!localArrayList.contains(str2))) {
            localArrayList.add(str2);
          }
        }
      }
    }
    return localArrayList;
  }
  
  private static List<String> filterExtended(List<Locale.LanguageRange> paramList, Collection<String> paramCollection)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      Locale.LanguageRange localLanguageRange = (Locale.LanguageRange)localIterator1.next();
      String str1 = localLanguageRange.getRange();
      if (str1.equals("*")) {
        return new ArrayList(paramCollection);
      }
      String[] arrayOfString1 = str1.split("-");
      Iterator localIterator2 = paramCollection.iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        str2 = str2.toLowerCase();
        String[] arrayOfString2 = str2.split("-");
        if ((arrayOfString1[0].equals(arrayOfString2[0])) || (arrayOfString1[0].equals("*")))
        {
          int i = 1;
          int j = 1;
          while ((i < arrayOfString1.length) && (j < arrayOfString2.length)) {
            if (arrayOfString1[i].equals("*"))
            {
              i++;
            }
            else if (arrayOfString1[i].equals(arrayOfString2[j]))
            {
              i++;
              j++;
            }
            else
            {
              if ((arrayOfString2[j].length() == 1) && (!arrayOfString2[j].equals("*"))) {
                break;
              }
              j++;
            }
          }
          if ((arrayOfString1.length == i) && (!localArrayList.contains(str2))) {
            localArrayList.add(str2);
          }
        }
      }
    }
    return localArrayList;
  }
  
  public static Locale lookup(List<Locale.LanguageRange> paramList, Collection<Locale> paramCollection)
  {
    if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramCollection.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Locale localLocale = (Locale)((Iterator)localObject).next();
      localArrayList.add(localLocale.toLanguageTag());
    }
    localObject = lookupTag(paramList, localArrayList);
    if (localObject == null) {
      return null;
    }
    return Locale.forLanguageTag((String)localObject);
  }
  
  public static String lookupTag(List<Locale.LanguageRange> paramList, Collection<String> paramCollection)
  {
    if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
      return null;
    }
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      Locale.LanguageRange localLanguageRange = (Locale.LanguageRange)localIterator1.next();
      String str1 = localLanguageRange.getRange();
      if (!str1.equals("*"))
      {
        String str2 = str1.replaceAll("\\x2A", "\\\\p{Alnum}*");
        while (str2.length() > 0)
        {
          Iterator localIterator2 = paramCollection.iterator();
          while (localIterator2.hasNext())
          {
            String str3 = (String)localIterator2.next();
            str3 = str3.toLowerCase();
            if (str3.matches(str2)) {
              return str3;
            }
          }
          int i = str2.lastIndexOf('-');
          if (i >= 0)
          {
            str2 = str2.substring(0, i);
            if (str2.lastIndexOf('-') == str2.length() - 2) {
              str2 = str2.substring(0, str2.length() - 2);
            }
          }
          else
          {
            str2 = "";
          }
        }
      }
    }
    return null;
  }
  
  public static List<Locale.LanguageRange> parse(String paramString)
  {
    paramString = paramString.replaceAll(" ", "").toLowerCase();
    if (paramString.startsWith("accept-language:")) {
      paramString = paramString.substring(16);
    }
    String[] arrayOfString1 = paramString.split(",");
    ArrayList localArrayList1 = new ArrayList(arrayOfString1.length);
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    for (String str1 : arrayOfString1)
    {
      int m;
      String str2;
      double d;
      if ((m = str1.indexOf(";q=")) == -1)
      {
        str2 = str1;
        d = 1.0D;
      }
      else
      {
        str2 = str1.substring(0, m);
        m += 3;
        try
        {
          d = Double.parseDouble(str1.substring(m));
        }
        catch (Exception localException)
        {
          throw new IllegalArgumentException("weight=\"" + str1.substring(m) + "\" for language range \"" + str2 + "\"");
        }
        if ((d < 0.0D) || (d > 1.0D)) {
          throw new IllegalArgumentException("weight=" + d + " for language range \"" + str2 + "\". It must be between " + 0.0D + " and " + 1.0D + ".");
        }
      }
      if (!localArrayList2.contains(str2))
      {
        Locale.LanguageRange localLanguageRange = new Locale.LanguageRange(str2, d);
        m = i;
        for (int n = 0; n < i; n++) {
          if (((Locale.LanguageRange)localArrayList1.get(n)).getWeight() < d)
          {
            m = n;
            break;
          }
        }
        localArrayList1.add(m, localLanguageRange);
        i++;
        localArrayList2.add(str2);
        String str3;
        if (((str3 = getEquivalentForRegionAndVariant(str2)) != null) && (!localArrayList2.contains(str3)))
        {
          localArrayList1.add(m + 1, new Locale.LanguageRange(str3, d));
          i++;
          localArrayList2.add(str3);
        }
        String[] arrayOfString3;
        if ((arrayOfString3 = getEquivalentsForLanguage(str2)) != null) {
          for (String str4 : arrayOfString3)
          {
            if (!localArrayList2.contains(str4))
            {
              localArrayList1.add(m + 1, new Locale.LanguageRange(str4, d));
              i++;
              localArrayList2.add(str4);
            }
            str3 = getEquivalentForRegionAndVariant(str4);
            if ((str3 != null) && (!localArrayList2.contains(str3)))
            {
              localArrayList1.add(m + 1, new Locale.LanguageRange(str3, d));
              i++;
              localArrayList2.add(str3);
            }
          }
        }
      }
    }
    return localArrayList1;
  }
  
  private static String[] getEquivalentsForLanguage(String paramString)
  {
    int i;
    for (String str = paramString; str.length() > 0; str = str.substring(0, i))
    {
      Object localObject;
      if (LocaleEquivalentMaps.singleEquivMap.containsKey(str))
      {
        localObject = (String)LocaleEquivalentMaps.singleEquivMap.get(str);
        return new String[] { paramString.replaceFirst(str, (String)localObject) };
      }
      if (LocaleEquivalentMaps.multiEquivsMap.containsKey(str))
      {
        localObject = (String[])LocaleEquivalentMaps.multiEquivsMap.get(str);
        for (int j = 0; j < localObject.length; j++) {
          localObject[j] = paramString.replaceFirst(str, localObject[j]);
        }
        return (String[])localObject;
      }
      i = str.lastIndexOf('-');
      if (i == -1) {
        break;
      }
    }
    return null;
  }
  
  private static String getEquivalentForRegionAndVariant(String paramString)
  {
    int i = getExtentionKeyIndex(paramString);
    Iterator localIterator = LocaleEquivalentMaps.regionVariantEquivMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      int j;
      if ((j = paramString.indexOf(str)) != -1) {
        if ((i == Integer.MIN_VALUE) || (j <= i))
        {
          int k = j + str.length();
          if ((paramString.length() == k) || (paramString.charAt(k) == '-')) {
            return paramString.replaceFirst(str, (String)LocaleEquivalentMaps.regionVariantEquivMap.get(str));
          }
        }
      }
    }
    return null;
  }
  
  private static int getExtentionKeyIndex(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = Integer.MIN_VALUE;
    for (int j = 1; j < arrayOfChar.length; j++) {
      if (arrayOfChar[j] == '-')
      {
        if (j - i == 2) {
          return i;
        }
        i = j;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public static List<Locale.LanguageRange> mapEquivalents(List<Locale.LanguageRange> paramList, Map<String, List<String>> paramMap)
  {
    if (paramList.isEmpty()) {
      return new ArrayList();
    }
    if ((paramMap == null) || (paramMap.isEmpty())) {
      return new ArrayList(paramList);
    }
    HashMap localHashMap = new HashMap();
    Object localObject1 = paramMap.keySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      localHashMap.put(((String)localObject2).toLowerCase(), localObject2);
    }
    localObject1 = new ArrayList();
    Object localObject2 = paramList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Locale.LanguageRange localLanguageRange = (Locale.LanguageRange)((Iterator)localObject2).next();
      String str1 = localLanguageRange.getRange();
      String str2 = str1;
      int i = 0;
      while (str2.length() > 0)
      {
        if (localHashMap.containsKey(str2))
        {
          i = 1;
          List localList = (List)paramMap.get(localHashMap.get(str2));
          if (localList == null) {
            break;
          }
          int k = str2.length();
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            String str3 = (String)localIterator.next();
            ((List)localObject1).add(new Locale.LanguageRange(str3.toLowerCase() + str1.substring(k), localLanguageRange.getWeight()));
          }
          break;
        }
        int j = str2.lastIndexOf('-');
        if (j == -1) {
          break;
        }
        str2 = str2.substring(0, j);
      }
      if (i == 0) {
        ((List)localObject1).add(localLanguageRange);
      }
    }
    return (List<Locale.LanguageRange>)localObject1;
  }
  
  private LocaleMatcher() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\LocaleMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */