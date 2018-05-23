package java.lang;

import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import sun.text.Normalizer;

final class ConditionalSpecialCasing
{
  static final int FINAL_CASED = 1;
  static final int AFTER_SOFT_DOTTED = 2;
  static final int MORE_ABOVE = 3;
  static final int AFTER_I = 4;
  static final int NOT_BEFORE_DOT = 5;
  static final int COMBINING_CLASS_ABOVE = 230;
  static Entry[] entry = { new Entry(931, new char[] { 'ς' }, new char[] { 'Σ' }, null, 1), new Entry(304, new char[] { 'i', '̇' }, new char[] { 'İ' }, null, 0), new Entry(775, new char[] { '̇' }, new char[0], "lt", 2), new Entry(73, new char[] { 'i', '̇' }, new char[] { 'I' }, "lt", 3), new Entry(74, new char[] { 'j', '̇' }, new char[] { 'J' }, "lt", 3), new Entry(302, new char[] { 'į', '̇' }, new char[] { 'Į' }, "lt", 3), new Entry(204, new char[] { 'i', '̇', '̀' }, new char[] { 'Ì' }, "lt", 0), new Entry(205, new char[] { 'i', '̇', '́' }, new char[] { 'Í' }, "lt", 0), new Entry(296, new char[] { 'i', '̇', '̃' }, new char[] { 'Ĩ' }, "lt", 0), new Entry(304, new char[] { 'i' }, new char[] { 'İ' }, "tr", 0), new Entry(304, new char[] { 'i' }, new char[] { 'İ' }, "az", 0), new Entry(775, new char[0], new char[] { '̇' }, "tr", 4), new Entry(775, new char[0], new char[] { '̇' }, "az", 4), new Entry(73, new char[] { 'ı' }, new char[] { 'I' }, "tr", 5), new Entry(73, new char[] { 'ı' }, new char[] { 'I' }, "az", 5), new Entry(105, new char[] { 'i' }, new char[] { 'İ' }, "tr", 0), new Entry(105, new char[] { 'i' }, new char[] { 'İ' }, "az", 0) };
  static Hashtable<Integer, HashSet<Entry>> entryTable = new Hashtable();
  
  ConditionalSpecialCasing() {}
  
  static int toLowerCaseEx(String paramString, int paramInt, Locale paramLocale)
  {
    char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, true);
    if (arrayOfChar != null)
    {
      if (arrayOfChar.length == 1) {
        return arrayOfChar[0];
      }
      return -1;
    }
    return Character.toLowerCase(paramString.codePointAt(paramInt));
  }
  
  static int toUpperCaseEx(String paramString, int paramInt, Locale paramLocale)
  {
    char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, false);
    if (arrayOfChar != null)
    {
      if (arrayOfChar.length == 1) {
        return arrayOfChar[0];
      }
      return -1;
    }
    return Character.toUpperCaseEx(paramString.codePointAt(paramInt));
  }
  
  static char[] toLowerCaseCharArray(String paramString, int paramInt, Locale paramLocale)
  {
    return lookUpTable(paramString, paramInt, paramLocale, true);
  }
  
  static char[] toUpperCaseCharArray(String paramString, int paramInt, Locale paramLocale)
  {
    char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, false);
    if (arrayOfChar != null) {
      return arrayOfChar;
    }
    return Character.toUpperCaseCharArray(paramString.codePointAt(paramInt));
  }
  
  private static char[] lookUpTable(String paramString, int paramInt, Locale paramLocale, boolean paramBoolean)
  {
    HashSet localHashSet = (HashSet)entryTable.get(new Integer(paramString.codePointAt(paramInt)));
    char[] arrayOfChar = null;
    if (localHashSet != null)
    {
      Iterator localIterator = localHashSet.iterator();
      String str1 = paramLocale.getLanguage();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        String str2 = localEntry.getLanguage();
        if (((str2 == null) || (str2.equals(str1))) && (isConditionMet(paramString, paramInt, paramLocale, localEntry.getCondition())))
        {
          arrayOfChar = paramBoolean ? localEntry.getLowerCase() : localEntry.getUpperCase();
          if (str2 != null) {
            break;
          }
        }
      }
    }
    return arrayOfChar;
  }
  
  private static boolean isConditionMet(String paramString, int paramInt1, Locale paramLocale, int paramInt2)
  {
    switch (paramInt2)
    {
    case 1: 
      return isFinalCased(paramString, paramInt1, paramLocale);
    case 2: 
      return isAfterSoftDotted(paramString, paramInt1);
    case 3: 
      return isMoreAbove(paramString, paramInt1);
    case 4: 
      return isAfterI(paramString, paramInt1);
    case 5: 
      return !isBeforeDot(paramString, paramInt1);
    }
    return true;
  }
  
  private static boolean isFinalCased(String paramString, int paramInt, Locale paramLocale)
  {
    BreakIterator localBreakIterator = BreakIterator.getWordInstance(paramLocale);
    localBreakIterator.setText(paramString);
    int j = paramInt;
    while ((j >= 0) && (!localBreakIterator.isBoundary(j)))
    {
      int i = paramString.codePointBefore(j);
      if (isCased(i))
      {
        int k = paramString.length();
        j = paramInt + Character.charCount(paramString.codePointAt(paramInt));
        while ((j < k) && (!localBreakIterator.isBoundary(j)))
        {
          i = paramString.codePointAt(j);
          if (isCased(i)) {
            return false;
          }
          j += Character.charCount(i);
        }
        return true;
      }
      j -= Character.charCount(i);
    }
    return false;
  }
  
  private static boolean isAfterI(String paramString, int paramInt)
  {
    int k = paramInt;
    while (k > 0)
    {
      int i = paramString.codePointBefore(k);
      if (i == 73) {
        return true;
      }
      int j = Normalizer.getCombiningClass(i);
      if ((j == 0) || (j == 230)) {
        return false;
      }
      k -= Character.charCount(i);
    }
    return false;
  }
  
  private static boolean isAfterSoftDotted(String paramString, int paramInt)
  {
    int k = paramInt;
    while (k > 0)
    {
      int i = paramString.codePointBefore(k);
      if (isSoftDotted(i)) {
        return true;
      }
      int j = Normalizer.getCombiningClass(i);
      if ((j == 0) || (j == 230)) {
        return false;
      }
      k -= Character.charCount(i);
    }
    return false;
  }
  
  private static boolean isMoreAbove(String paramString, int paramInt)
  {
    int k = paramString.length();
    int m = paramInt + Character.charCount(paramString.codePointAt(paramInt));
    while (m < k)
    {
      int i = paramString.codePointAt(m);
      int j = Normalizer.getCombiningClass(i);
      if (j == 230) {
        return true;
      }
      if (j == 0) {
        return false;
      }
      m += Character.charCount(i);
    }
    return false;
  }
  
  private static boolean isBeforeDot(String paramString, int paramInt)
  {
    int k = paramString.length();
    int m = paramInt + Character.charCount(paramString.codePointAt(paramInt));
    while (m < k)
    {
      int i = paramString.codePointAt(m);
      if (i == 775) {
        return true;
      }
      int j = Normalizer.getCombiningClass(i);
      if ((j == 0) || (j == 230)) {
        return false;
      }
      m += Character.charCount(i);
    }
    return false;
  }
  
  private static boolean isCased(int paramInt)
  {
    int i = Character.getType(paramInt);
    if ((i == 2) || (i == 1) || (i == 3)) {
      return true;
    }
    if ((paramInt >= 688) && (paramInt <= 696)) {
      return true;
    }
    if ((paramInt >= 704) && (paramInt <= 705)) {
      return true;
    }
    if ((paramInt >= 736) && (paramInt <= 740)) {
      return true;
    }
    if (paramInt == 837) {
      return true;
    }
    if (paramInt == 890) {
      return true;
    }
    if ((paramInt >= 7468) && (paramInt <= 7521)) {
      return true;
    }
    if ((paramInt >= 8544) && (paramInt <= 8575)) {
      return true;
    }
    return (paramInt >= 9398) && (paramInt <= 9449);
  }
  
  private static boolean isSoftDotted(int paramInt)
  {
    switch (paramInt)
    {
    case 105: 
    case 106: 
    case 303: 
    case 616: 
    case 1110: 
    case 1112: 
    case 7522: 
    case 7725: 
    case 7883: 
    case 8305: 
      return true;
    }
    return false;
  }
  
  static
  {
    for (int i = 0; i < entry.length; i++)
    {
      Entry localEntry = entry[i];
      Integer localInteger = new Integer(localEntry.getCodePoint());
      HashSet localHashSet = (HashSet)entryTable.get(localInteger);
      if (localHashSet == null) {
        localHashSet = new HashSet();
      }
      localHashSet.add(localEntry);
      entryTable.put(localInteger, localHashSet);
    }
  }
  
  static class Entry
  {
    int ch;
    char[] lower;
    char[] upper;
    String lang;
    int condition;
    
    Entry(int paramInt1, char[] paramArrayOfChar1, char[] paramArrayOfChar2, String paramString, int paramInt2)
    {
      ch = paramInt1;
      lower = paramArrayOfChar1;
      upper = paramArrayOfChar2;
      lang = paramString;
      condition = paramInt2;
    }
    
    int getCodePoint()
    {
      return ch;
    }
    
    char[] getLowerCase()
    {
      return lower;
    }
    
    char[] getUpperCase()
    {
      return upper;
    }
    
    String getLanguage()
    {
      return lang;
    }
    
    int getCondition()
    {
      return condition;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ConditionalSpecialCasing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */