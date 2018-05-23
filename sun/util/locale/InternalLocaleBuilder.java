package sun.util.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InternalLocaleBuilder
{
  private static final CaseInsensitiveChar PRIVATEUSE_KEY = new CaseInsensitiveChar("x", null);
  private String language = "";
  private String script = "";
  private String region = "";
  private String variant = "";
  private Map<CaseInsensitiveChar, String> extensions;
  private Set<CaseInsensitiveString> uattributes;
  private Map<CaseInsensitiveString, String> ukeywords;
  
  public InternalLocaleBuilder() {}
  
  public InternalLocaleBuilder setLanguage(String paramString)
    throws LocaleSyntaxException
  {
    if (LocaleUtils.isEmpty(paramString))
    {
      language = "";
    }
    else
    {
      if (!LanguageTag.isLanguage(paramString)) {
        throw new LocaleSyntaxException("Ill-formed language: " + paramString, 0);
      }
      language = paramString;
    }
    return this;
  }
  
  public InternalLocaleBuilder setScript(String paramString)
    throws LocaleSyntaxException
  {
    if (LocaleUtils.isEmpty(paramString))
    {
      script = "";
    }
    else
    {
      if (!LanguageTag.isScript(paramString)) {
        throw new LocaleSyntaxException("Ill-formed script: " + paramString, 0);
      }
      script = paramString;
    }
    return this;
  }
  
  public InternalLocaleBuilder setRegion(String paramString)
    throws LocaleSyntaxException
  {
    if (LocaleUtils.isEmpty(paramString))
    {
      region = "";
    }
    else
    {
      if (!LanguageTag.isRegion(paramString)) {
        throw new LocaleSyntaxException("Ill-formed region: " + paramString, 0);
      }
      region = paramString;
    }
    return this;
  }
  
  public InternalLocaleBuilder setVariant(String paramString)
    throws LocaleSyntaxException
  {
    if (LocaleUtils.isEmpty(paramString))
    {
      variant = "";
    }
    else
    {
      String str = paramString.replaceAll("-", "_");
      int i = checkVariants(str, "_");
      if (i != -1) {
        throw new LocaleSyntaxException("Ill-formed variant: " + paramString, i);
      }
      variant = str;
    }
    return this;
  }
  
  public InternalLocaleBuilder addUnicodeLocaleAttribute(String paramString)
    throws LocaleSyntaxException
  {
    if (!UnicodeLocaleExtension.isAttribute(paramString)) {
      throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + paramString);
    }
    if (uattributes == null) {
      uattributes = new HashSet(4);
    }
    uattributes.add(new CaseInsensitiveString(paramString));
    return this;
  }
  
  public InternalLocaleBuilder removeUnicodeLocaleAttribute(String paramString)
    throws LocaleSyntaxException
  {
    if ((paramString == null) || (!UnicodeLocaleExtension.isAttribute(paramString))) {
      throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + paramString);
    }
    if (uattributes != null) {
      uattributes.remove(new CaseInsensitiveString(paramString));
    }
    return this;
  }
  
  public InternalLocaleBuilder setUnicodeLocaleKeyword(String paramString1, String paramString2)
    throws LocaleSyntaxException
  {
    if (!UnicodeLocaleExtension.isKey(paramString1)) {
      throw new LocaleSyntaxException("Ill-formed Unicode locale keyword key: " + paramString1);
    }
    CaseInsensitiveString localCaseInsensitiveString = new CaseInsensitiveString(paramString1);
    if (paramString2 == null)
    {
      if (ukeywords != null) {
        ukeywords.remove(localCaseInsensitiveString);
      }
    }
    else
    {
      if (paramString2.length() != 0)
      {
        String str1 = paramString2.replaceAll("_", "-");
        StringTokenIterator localStringTokenIterator = new StringTokenIterator(str1, "-");
        while (!localStringTokenIterator.isDone())
        {
          String str2 = localStringTokenIterator.current();
          if (!UnicodeLocaleExtension.isTypeSubtag(str2)) {
            throw new LocaleSyntaxException("Ill-formed Unicode locale keyword type: " + paramString2, localStringTokenIterator.currentStart());
          }
          localStringTokenIterator.next();
        }
      }
      if (ukeywords == null) {
        ukeywords = new HashMap(4);
      }
      ukeywords.put(localCaseInsensitiveString, paramString2);
    }
    return this;
  }
  
  public InternalLocaleBuilder setExtension(char paramChar, String paramString)
    throws LocaleSyntaxException
  {
    boolean bool1 = LanguageTag.isPrivateusePrefixChar(paramChar);
    if ((!bool1) && (!LanguageTag.isExtensionSingletonChar(paramChar))) {
      throw new LocaleSyntaxException("Ill-formed extension key: " + paramChar);
    }
    boolean bool2 = LocaleUtils.isEmpty(paramString);
    CaseInsensitiveChar localCaseInsensitiveChar = new CaseInsensitiveChar(paramChar);
    if (bool2)
    {
      if (UnicodeLocaleExtension.isSingletonChar(localCaseInsensitiveChar.value()))
      {
        if (uattributes != null) {
          uattributes.clear();
        }
        if (ukeywords != null) {
          ukeywords.clear();
        }
      }
      else if ((extensions != null) && (extensions.containsKey(localCaseInsensitiveChar)))
      {
        extensions.remove(localCaseInsensitiveChar);
      }
    }
    else
    {
      String str1 = paramString.replaceAll("_", "-");
      StringTokenIterator localStringTokenIterator = new StringTokenIterator(str1, "-");
      while (!localStringTokenIterator.isDone())
      {
        String str2 = localStringTokenIterator.current();
        boolean bool3;
        if (bool1) {
          bool3 = LanguageTag.isPrivateuseSubtag(str2);
        } else {
          bool3 = LanguageTag.isExtensionSubtag(str2);
        }
        if (!bool3) {
          throw new LocaleSyntaxException("Ill-formed extension value: " + str2, localStringTokenIterator.currentStart());
        }
        localStringTokenIterator.next();
      }
      if (UnicodeLocaleExtension.isSingletonChar(localCaseInsensitiveChar.value()))
      {
        setUnicodeLocaleExtension(str1);
      }
      else
      {
        if (extensions == null) {
          extensions = new HashMap(4);
        }
        extensions.put(localCaseInsensitiveChar, str1);
      }
    }
    return this;
  }
  
  public InternalLocaleBuilder setExtensions(String paramString)
    throws LocaleSyntaxException
  {
    if (LocaleUtils.isEmpty(paramString))
    {
      clearExtensions();
      return this;
    }
    paramString = paramString.replaceAll("_", "-");
    StringTokenIterator localStringTokenIterator = new StringTokenIterator(paramString, "-");
    ArrayList localArrayList = null;
    String str1 = null;
    int i = 0;
    String str2;
    int j;
    Object localObject;
    while (!localStringTokenIterator.isDone())
    {
      str2 = localStringTokenIterator.current();
      if (!LanguageTag.isExtensionSingleton(str2)) {
        break;
      }
      j = localStringTokenIterator.currentStart();
      localObject = str2;
      StringBuilder localStringBuilder = new StringBuilder((String)localObject);
      localStringTokenIterator.next();
      while (!localStringTokenIterator.isDone())
      {
        str2 = localStringTokenIterator.current();
        if (!LanguageTag.isExtensionSubtag(str2)) {
          break;
        }
        localStringBuilder.append("-").append(str2);
        i = localStringTokenIterator.currentEnd();
        localStringTokenIterator.next();
      }
      if (i < j) {
        throw new LocaleSyntaxException("Incomplete extension '" + (String)localObject + "'", j);
      }
      if (localArrayList == null) {
        localArrayList = new ArrayList(4);
      }
      localArrayList.add(localStringBuilder.toString());
    }
    if (!localStringTokenIterator.isDone())
    {
      str2 = localStringTokenIterator.current();
      if (LanguageTag.isPrivateusePrefix(str2))
      {
        j = localStringTokenIterator.currentStart();
        localObject = new StringBuilder(str2);
        localStringTokenIterator.next();
        while (!localStringTokenIterator.isDone())
        {
          str2 = localStringTokenIterator.current();
          if (!LanguageTag.isPrivateuseSubtag(str2)) {
            break;
          }
          ((StringBuilder)localObject).append("-").append(str2);
          i = localStringTokenIterator.currentEnd();
          localStringTokenIterator.next();
        }
        if (i <= j) {
          throw new LocaleSyntaxException("Incomplete privateuse:" + paramString.substring(j), j);
        }
        str1 = ((StringBuilder)localObject).toString();
      }
    }
    if (!localStringTokenIterator.isDone()) {
      throw new LocaleSyntaxException("Ill-formed extension subtags:" + paramString.substring(localStringTokenIterator.currentStart()), localStringTokenIterator.currentStart());
    }
    return setExtensions(localArrayList, str1);
  }
  
  private InternalLocaleBuilder setExtensions(List<String> paramList, String paramString)
  {
    clearExtensions();
    if (!LocaleUtils.isEmpty(paramList))
    {
      HashSet localHashSet = new HashSet(paramList.size());
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        CaseInsensitiveChar localCaseInsensitiveChar = new CaseInsensitiveChar(str, null);
        if (!localHashSet.contains(localCaseInsensitiveChar)) {
          if (UnicodeLocaleExtension.isSingletonChar(localCaseInsensitiveChar.value()))
          {
            setUnicodeLocaleExtension(str.substring(2));
          }
          else
          {
            if (extensions == null) {
              extensions = new HashMap(4);
            }
            extensions.put(localCaseInsensitiveChar, str.substring(2));
          }
        }
        localHashSet.add(localCaseInsensitiveChar);
      }
    }
    if ((paramString != null) && (paramString.length() > 0))
    {
      if (extensions == null) {
        extensions = new HashMap(1);
      }
      extensions.put(new CaseInsensitiveChar(paramString, null), paramString.substring(2));
    }
    return this;
  }
  
  public InternalLocaleBuilder setLanguageTag(LanguageTag paramLanguageTag)
  {
    clear();
    if (!paramLanguageTag.getExtlangs().isEmpty())
    {
      language = ((String)paramLanguageTag.getExtlangs().get(0));
    }
    else
    {
      localObject = paramLanguageTag.getLanguage();
      if (!((String)localObject).equals("und")) {
        language = ((String)localObject);
      }
    }
    script = paramLanguageTag.getScript();
    region = paramLanguageTag.getRegion();
    Object localObject = paramLanguageTag.getVariants();
    if (!((List)localObject).isEmpty())
    {
      StringBuilder localStringBuilder = new StringBuilder((String)((List)localObject).get(0));
      int i = ((List)localObject).size();
      for (int j = 1; j < i; j++) {
        localStringBuilder.append("_").append((String)((List)localObject).get(j));
      }
      variant = localStringBuilder.toString();
    }
    setExtensions(paramLanguageTag.getExtensions(), paramLanguageTag.getPrivateuse());
    return this;
  }
  
  public InternalLocaleBuilder setLocale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions)
    throws LocaleSyntaxException
  {
    String str1 = paramBaseLocale.getLanguage();
    String str2 = paramBaseLocale.getScript();
    String str3 = paramBaseLocale.getRegion();
    String str4 = paramBaseLocale.getVariant();
    if ((str1.equals("ja")) && (str3.equals("JP")) && (str4.equals("JP")))
    {
      assert ("japanese".equals(paramLocaleExtensions.getUnicodeLocaleType("ca")));
      str4 = "";
    }
    else if ((str1.equals("th")) && (str3.equals("TH")) && (str4.equals("TH")))
    {
      assert ("thai".equals(paramLocaleExtensions.getUnicodeLocaleType("nu")));
      str4 = "";
    }
    else if ((str1.equals("no")) && (str3.equals("NO")) && (str4.equals("NY")))
    {
      str1 = "nn";
      str4 = "";
    }
    if ((str1.length() > 0) && (!LanguageTag.isLanguage(str1))) {
      throw new LocaleSyntaxException("Ill-formed language: " + str1);
    }
    if ((str2.length() > 0) && (!LanguageTag.isScript(str2))) {
      throw new LocaleSyntaxException("Ill-formed script: " + str2);
    }
    if ((str3.length() > 0) && (!LanguageTag.isRegion(str3))) {
      throw new LocaleSyntaxException("Ill-formed region: " + str3);
    }
    if (str4.length() > 0)
    {
      int i = checkVariants(str4, "_");
      if (i != -1) {
        throw new LocaleSyntaxException("Ill-formed variant: " + str4, i);
      }
    }
    language = str1;
    script = str2;
    region = str3;
    variant = str4;
    clearExtensions();
    Set localSet = paramLocaleExtensions == null ? null : paramLocaleExtensions.getKeys();
    if (localSet != null)
    {
      Iterator localIterator1 = localSet.iterator();
      while (localIterator1.hasNext())
      {
        Character localCharacter = (Character)localIterator1.next();
        Extension localExtension = paramLocaleExtensions.getExtension(localCharacter);
        if ((localExtension instanceof UnicodeLocaleExtension))
        {
          UnicodeLocaleExtension localUnicodeLocaleExtension = (UnicodeLocaleExtension)localExtension;
          Iterator localIterator2 = localUnicodeLocaleExtension.getUnicodeLocaleAttributes().iterator();
          String str5;
          while (localIterator2.hasNext())
          {
            str5 = (String)localIterator2.next();
            if (uattributes == null) {
              uattributes = new HashSet(4);
            }
            uattributes.add(new CaseInsensitiveString(str5));
          }
          localIterator2 = localUnicodeLocaleExtension.getUnicodeLocaleKeys().iterator();
          while (localIterator2.hasNext())
          {
            str5 = (String)localIterator2.next();
            if (ukeywords == null) {
              ukeywords = new HashMap(4);
            }
            ukeywords.put(new CaseInsensitiveString(str5), localUnicodeLocaleExtension.getUnicodeLocaleType(str5));
          }
        }
        else
        {
          if (extensions == null) {
            extensions = new HashMap(4);
          }
          extensions.put(new CaseInsensitiveChar(localCharacter.charValue()), localExtension.getValue());
        }
      }
    }
    return this;
  }
  
  public InternalLocaleBuilder clear()
  {
    language = "";
    script = "";
    region = "";
    variant = "";
    clearExtensions();
    return this;
  }
  
  public InternalLocaleBuilder clearExtensions()
  {
    if (extensions != null) {
      extensions.clear();
    }
    if (uattributes != null) {
      uattributes.clear();
    }
    if (ukeywords != null) {
      ukeywords.clear();
    }
    return this;
  }
  
  public BaseLocale getBaseLocale()
  {
    String str1 = language;
    String str2 = script;
    String str3 = region;
    String str4 = variant;
    if (extensions != null)
    {
      String str5 = (String)extensions.get(PRIVATEUSE_KEY);
      if (str5 != null)
      {
        StringTokenIterator localStringTokenIterator = new StringTokenIterator(str5, "-");
        int i = 0;
        int j = -1;
        while (!localStringTokenIterator.isDone())
        {
          if (i != 0)
          {
            j = localStringTokenIterator.currentStart();
            break;
          }
          if (LocaleUtils.caseIgnoreMatch(localStringTokenIterator.current(), "lvariant")) {
            i = 1;
          }
          localStringTokenIterator.next();
        }
        if (j != -1)
        {
          StringBuilder localStringBuilder = new StringBuilder(str4);
          if (localStringBuilder.length() != 0) {
            localStringBuilder.append("_");
          }
          localStringBuilder.append(str5.substring(j).replaceAll("-", "_"));
          str4 = localStringBuilder.toString();
        }
      }
    }
    return BaseLocale.getInstance(str1, str2, str3, str4);
  }
  
  public LocaleExtensions getLocaleExtensions()
  {
    if ((LocaleUtils.isEmpty(extensions)) && (LocaleUtils.isEmpty(uattributes)) && (LocaleUtils.isEmpty(ukeywords))) {
      return null;
    }
    LocaleExtensions localLocaleExtensions = new LocaleExtensions(extensions, uattributes, ukeywords);
    return localLocaleExtensions.isEmpty() ? null : localLocaleExtensions;
  }
  
  static String removePrivateuseVariant(String paramString)
  {
    StringTokenIterator localStringTokenIterator = new StringTokenIterator(paramString, "-");
    int i = -1;
    int j = 0;
    while (!localStringTokenIterator.isDone())
    {
      if (i != -1)
      {
        j = 1;
        break;
      }
      if (LocaleUtils.caseIgnoreMatch(localStringTokenIterator.current(), "lvariant")) {
        i = localStringTokenIterator.currentStart();
      }
      localStringTokenIterator.next();
    }
    if (j == 0) {
      return paramString;
    }
    assert ((i == 0) || (i > 1));
    return i == 0 ? null : paramString.substring(0, i - 1);
  }
  
  private int checkVariants(String paramString1, String paramString2)
  {
    StringTokenIterator localStringTokenIterator = new StringTokenIterator(paramString1, paramString2);
    while (!localStringTokenIterator.isDone())
    {
      String str = localStringTokenIterator.current();
      if (!LanguageTag.isVariant(str)) {
        return localStringTokenIterator.currentStart();
      }
      localStringTokenIterator.next();
    }
    return -1;
  }
  
  private void setUnicodeLocaleExtension(String paramString)
  {
    if (uattributes != null) {
      uattributes.clear();
    }
    if (ukeywords != null) {
      ukeywords.clear();
    }
    StringTokenIterator localStringTokenIterator = new StringTokenIterator(paramString, "-");
    while ((!localStringTokenIterator.isDone()) && (UnicodeLocaleExtension.isAttribute(localStringTokenIterator.current())))
    {
      if (uattributes == null) {
        uattributes = new HashSet(4);
      }
      uattributes.add(new CaseInsensitiveString(localStringTokenIterator.current()));
      localStringTokenIterator.next();
    }
    Object localObject = null;
    int i = -1;
    int j = -1;
    while (!localStringTokenIterator.isDone())
    {
      String str;
      if (localObject != null)
      {
        if (UnicodeLocaleExtension.isKey(localStringTokenIterator.current()))
        {
          assert ((i == -1) || (j != -1));
          str = i == -1 ? "" : paramString.substring(i, j);
          if (ukeywords == null) {
            ukeywords = new HashMap(4);
          }
          ukeywords.put(localObject, str);
          CaseInsensitiveString localCaseInsensitiveString = new CaseInsensitiveString(localStringTokenIterator.current());
          localObject = ukeywords.containsKey(localCaseInsensitiveString) ? null : localCaseInsensitiveString;
          i = j = -1;
        }
        else
        {
          if (i == -1) {
            i = localStringTokenIterator.currentStart();
          }
          j = localStringTokenIterator.currentEnd();
        }
      }
      else if (UnicodeLocaleExtension.isKey(localStringTokenIterator.current()))
      {
        localObject = new CaseInsensitiveString(localStringTokenIterator.current());
        if ((ukeywords != null) && (ukeywords.containsKey(localObject))) {
          localObject = null;
        }
      }
      if (!localStringTokenIterator.hasNext())
      {
        if (localObject == null) {
          break;
        }
        assert ((i == -1) || (j != -1));
        str = i == -1 ? "" : paramString.substring(i, j);
        if (ukeywords == null) {
          ukeywords = new HashMap(4);
        }
        ukeywords.put(localObject, str);
        break;
      }
      localStringTokenIterator.next();
    }
  }
  
  static final class CaseInsensitiveChar
  {
    private final char ch;
    private final char lowerCh;
    
    private CaseInsensitiveChar(String paramString)
    {
      this(paramString.charAt(0));
    }
    
    CaseInsensitiveChar(char paramChar)
    {
      ch = paramChar;
      lowerCh = LocaleUtils.toLower(ch);
    }
    
    public char value()
    {
      return ch;
    }
    
    public int hashCode()
    {
      return lowerCh;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof CaseInsensitiveChar)) {
        return false;
      }
      return lowerCh == lowerCh;
    }
  }
  
  static final class CaseInsensitiveString
  {
    private final String str;
    private final String lowerStr;
    
    CaseInsensitiveString(String paramString)
    {
      str = paramString;
      lowerStr = LocaleUtils.toLowerString(paramString);
    }
    
    public String value()
    {
      return str;
    }
    
    public int hashCode()
    {
      return lowerStr.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof CaseInsensitiveString)) {
        return false;
      }
      return lowerStr.equals(lowerStr);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\InternalLocaleBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */