package sun.util.locale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanguageTag
{
  public static final String SEP = "-";
  public static final String PRIVATEUSE = "x";
  public static final String UNDETERMINED = "und";
  public static final String PRIVUSE_VARIANT_PREFIX = "lvariant";
  private String language = "";
  private String script = "";
  private String region = "";
  private String privateuse = "";
  private List<String> extlangs = Collections.emptyList();
  private List<String> variants = Collections.emptyList();
  private List<String> extensions = Collections.emptyList();
  private static final Map<String, String[]> GRANDFATHERED = new HashMap();
  
  private LanguageTag() {}
  
  public static LanguageTag parse(String paramString, ParseStatus paramParseStatus)
  {
    if (paramParseStatus == null) {
      paramParseStatus = new ParseStatus();
    } else {
      paramParseStatus.reset();
    }
    String[] arrayOfString = (String[])GRANDFATHERED.get(LocaleUtils.toLowerString(paramString));
    StringTokenIterator localStringTokenIterator;
    if (arrayOfString != null) {
      localStringTokenIterator = new StringTokenIterator(arrayOfString[1], "-");
    } else {
      localStringTokenIterator = new StringTokenIterator(paramString, "-");
    }
    LanguageTag localLanguageTag = new LanguageTag();
    if (localLanguageTag.parseLanguage(localStringTokenIterator, paramParseStatus))
    {
      localLanguageTag.parseExtlangs(localStringTokenIterator, paramParseStatus);
      localLanguageTag.parseScript(localStringTokenIterator, paramParseStatus);
      localLanguageTag.parseRegion(localStringTokenIterator, paramParseStatus);
      localLanguageTag.parseVariants(localStringTokenIterator, paramParseStatus);
      localLanguageTag.parseExtensions(localStringTokenIterator, paramParseStatus);
    }
    localLanguageTag.parsePrivateuse(localStringTokenIterator, paramParseStatus);
    if ((!localStringTokenIterator.isDone()) && (!paramParseStatus.isError()))
    {
      String str = localStringTokenIterator.current();
      errorIndex = localStringTokenIterator.currentStart();
      if (str.length() == 0) {
        errorMsg = "Empty subtag";
      } else {
        errorMsg = ("Invalid subtag: " + str);
      }
    }
    return localLanguageTag;
  }
  
  private boolean parseLanguage(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isLanguage(str))
    {
      bool = true;
      language = str;
      parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    }
    return bool;
  }
  
  private boolean parseExtlangs(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    boolean bool = false;
    while (!paramStringTokenIterator.isDone())
    {
      String str = paramStringTokenIterator.current();
      if (!isExtlang(str)) {
        break;
      }
      bool = true;
      if (extlangs.isEmpty()) {
        extlangs = new ArrayList(3);
      }
      extlangs.add(str);
      parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
      if (extlangs.size() == 3) {
        break;
      }
    }
    return bool;
  }
  
  private boolean parseScript(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isScript(str))
    {
      bool = true;
      script = str;
      parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    }
    return bool;
  }
  
  private boolean parseRegion(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isRegion(str))
    {
      bool = true;
      region = str;
      parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    }
    return bool;
  }
  
  private boolean parseVariants(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    boolean bool = false;
    while (!paramStringTokenIterator.isDone())
    {
      String str = paramStringTokenIterator.current();
      if (!isVariant(str)) {
        break;
      }
      bool = true;
      if (variants.isEmpty()) {
        variants = new ArrayList(3);
      }
      variants.add(str);
      parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    }
    return bool;
  }
  
  private boolean parseExtensions(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    for (boolean bool = false; !paramStringTokenIterator.isDone(); bool = true)
    {
      String str1 = paramStringTokenIterator.current();
      if (!isExtensionSingleton(str1)) {
        break;
      }
      int i = paramStringTokenIterator.currentStart();
      String str2 = str1;
      StringBuilder localStringBuilder = new StringBuilder(str2);
      paramStringTokenIterator.next();
      while (!paramStringTokenIterator.isDone())
      {
        str1 = paramStringTokenIterator.current();
        if (!isExtensionSubtag(str1)) {
          break;
        }
        localStringBuilder.append("-").append(str1);
        parseLength = paramStringTokenIterator.currentEnd();
        paramStringTokenIterator.next();
      }
      if (parseLength <= i)
      {
        errorIndex = i;
        errorMsg = ("Incomplete extension '" + str2 + "'");
        break;
      }
      if (extensions.isEmpty()) {
        extensions = new ArrayList(4);
      }
      extensions.add(localStringBuilder.toString());
    }
    return bool;
  }
  
  private boolean parsePrivateuse(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
  {
    if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
      return false;
    }
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isPrivateusePrefix(str))
    {
      int i = paramStringTokenIterator.currentStart();
      StringBuilder localStringBuilder = new StringBuilder(str);
      paramStringTokenIterator.next();
      while (!paramStringTokenIterator.isDone())
      {
        str = paramStringTokenIterator.current();
        if (!isPrivateuseSubtag(str)) {
          break;
        }
        localStringBuilder.append("-").append(str);
        parseLength = paramStringTokenIterator.currentEnd();
        paramStringTokenIterator.next();
      }
      if (parseLength <= i)
      {
        errorIndex = i;
        errorMsg = "Incomplete privateuse";
      }
      else
      {
        privateuse = localStringBuilder.toString();
        bool = true;
      }
    }
    return bool;
  }
  
  public static LanguageTag parseLocale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions)
  {
    LanguageTag localLanguageTag = new LanguageTag();
    String str1 = paramBaseLocale.getLanguage();
    String str2 = paramBaseLocale.getScript();
    String str3 = paramBaseLocale.getRegion();
    String str4 = paramBaseLocale.getVariant();
    int i = 0;
    String str5 = null;
    if (isLanguage(str1))
    {
      if (str1.equals("iw")) {
        str1 = "he";
      } else if (str1.equals("ji")) {
        str1 = "yi";
      } else if (str1.equals("in")) {
        str1 = "id";
      }
      language = str1;
    }
    if (isScript(str2))
    {
      script = canonicalizeScript(str2);
      i = 1;
    }
    if (isRegion(str3))
    {
      region = canonicalizeRegion(str3);
      i = 1;
    }
    if ((language.equals("no")) && (region.equals("NO")) && (str4.equals("NY")))
    {
      language = "nn";
      str4 = "";
    }
    Object localObject2;
    Object localObject3;
    if (str4.length() > 0)
    {
      localArrayList = null;
      localObject1 = new StringTokenIterator(str4, "_");
      while (!((StringTokenIterator)localObject1).isDone())
      {
        localObject2 = ((StringTokenIterator)localObject1).current();
        if (!isVariant((String)localObject2)) {
          break;
        }
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localObject2);
        ((StringTokenIterator)localObject1).next();
      }
      if (localArrayList != null)
      {
        variants = localArrayList;
        i = 1;
      }
      if (!((StringTokenIterator)localObject1).isDone())
      {
        localObject2 = new StringBuilder();
        while (!((StringTokenIterator)localObject1).isDone())
        {
          localObject3 = ((StringTokenIterator)localObject1).current();
          if (!isPrivateuseSubtag((String)localObject3)) {
            break;
          }
          if (((StringBuilder)localObject2).length() > 0) {
            ((StringBuilder)localObject2).append("-");
          }
          ((StringBuilder)localObject2).append((String)localObject3);
          ((StringTokenIterator)localObject1).next();
        }
        if (((StringBuilder)localObject2).length() > 0) {
          str5 = ((StringBuilder)localObject2).toString();
        }
      }
    }
    ArrayList localArrayList = null;
    Object localObject1 = null;
    if (paramLocaleExtensions != null)
    {
      localObject2 = paramLocaleExtensions.getKeys();
      localObject3 = ((Set)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        Character localCharacter = (Character)((Iterator)localObject3).next();
        Extension localExtension = paramLocaleExtensions.getExtension(localCharacter);
        if (isPrivateusePrefixChar(localCharacter.charValue()))
        {
          localObject1 = localExtension.getValue();
        }
        else
        {
          if (localArrayList == null) {
            localArrayList = new ArrayList();
          }
          localArrayList.add(localCharacter.toString() + "-" + localExtension.getValue());
        }
      }
    }
    if (localArrayList != null)
    {
      extensions = localArrayList;
      i = 1;
    }
    if (str5 != null) {
      if (localObject1 == null) {
        localObject1 = "lvariant-" + str5;
      } else {
        localObject1 = (String)localObject1 + "-" + "lvariant" + "-" + str5.replace("_", "-");
      }
    }
    if (localObject1 != null) {
      privateuse = ((String)localObject1);
    }
    if ((language.length() == 0) && ((i != 0) || (localObject1 == null))) {
      language = "und";
    }
    return localLanguageTag;
  }
  
  public String getLanguage()
  {
    return language;
  }
  
  public List<String> getExtlangs()
  {
    if (extlangs.isEmpty()) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(extlangs);
  }
  
  public String getScript()
  {
    return script;
  }
  
  public String getRegion()
  {
    return region;
  }
  
  public List<String> getVariants()
  {
    if (variants.isEmpty()) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(variants);
  }
  
  public List<String> getExtensions()
  {
    if (extensions.isEmpty()) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(extensions);
  }
  
  public String getPrivateuse()
  {
    return privateuse;
  }
  
  public static boolean isLanguage(String paramString)
  {
    int i = paramString.length();
    return (i >= 2) && (i <= 8) && (LocaleUtils.isAlphaString(paramString));
  }
  
  public static boolean isExtlang(String paramString)
  {
    return (paramString.length() == 3) && (LocaleUtils.isAlphaString(paramString));
  }
  
  public static boolean isScript(String paramString)
  {
    return (paramString.length() == 4) && (LocaleUtils.isAlphaString(paramString));
  }
  
  public static boolean isRegion(String paramString)
  {
    return ((paramString.length() == 2) && (LocaleUtils.isAlphaString(paramString))) || ((paramString.length() == 3) && (LocaleUtils.isNumericString(paramString)));
  }
  
  public static boolean isVariant(String paramString)
  {
    int i = paramString.length();
    if ((i >= 5) && (i <= 8)) {
      return LocaleUtils.isAlphaNumericString(paramString);
    }
    if (i == 4) {
      return (LocaleUtils.isNumeric(paramString.charAt(0))) && (LocaleUtils.isAlphaNumeric(paramString.charAt(1))) && (LocaleUtils.isAlphaNumeric(paramString.charAt(2))) && (LocaleUtils.isAlphaNumeric(paramString.charAt(3)));
    }
    return false;
  }
  
  public static boolean isExtensionSingleton(String paramString)
  {
    return (paramString.length() == 1) && (LocaleUtils.isAlphaString(paramString)) && (!LocaleUtils.caseIgnoreMatch("x", paramString));
  }
  
  public static boolean isExtensionSingletonChar(char paramChar)
  {
    return isExtensionSingleton(String.valueOf(paramChar));
  }
  
  public static boolean isExtensionSubtag(String paramString)
  {
    int i = paramString.length();
    return (i >= 2) && (i <= 8) && (LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static boolean isPrivateusePrefix(String paramString)
  {
    return (paramString.length() == 1) && (LocaleUtils.caseIgnoreMatch("x", paramString));
  }
  
  public static boolean isPrivateusePrefixChar(char paramChar)
  {
    return LocaleUtils.caseIgnoreMatch("x", String.valueOf(paramChar));
  }
  
  public static boolean isPrivateuseSubtag(String paramString)
  {
    int i = paramString.length();
    return (i >= 1) && (i <= 8) && (LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static String canonicalizeLanguage(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizeExtlang(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizeScript(String paramString)
  {
    return LocaleUtils.toTitleString(paramString);
  }
  
  public static String canonicalizeRegion(String paramString)
  {
    return LocaleUtils.toUpperString(paramString);
  }
  
  public static String canonicalizeVariant(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizeExtension(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizeExtensionSingleton(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizeExtensionSubtag(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizePrivateuse(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public static String canonicalizePrivateuseSubtag(String paramString)
  {
    return LocaleUtils.toLowerString(paramString);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (language.length() > 0)
    {
      localStringBuilder.append(language);
      Iterator localIterator = extlangs.iterator();
      String str;
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        localStringBuilder.append("-").append(str);
      }
      if (script.length() > 0) {
        localStringBuilder.append("-").append(script);
      }
      if (region.length() > 0) {
        localStringBuilder.append("-").append(region);
      }
      localIterator = variants.iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        localStringBuilder.append("-").append(str);
      }
      localIterator = extensions.iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        localStringBuilder.append("-").append(str);
      }
    }
    if (privateuse.length() > 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("-");
      }
      localStringBuilder.append(privateuse);
    }
    return localStringBuilder.toString();
  }
  
  static
  {
    String[][] arrayOfString1 = { { "art-lojban", "jbo" }, { "cel-gaulish", "xtg-x-cel-gaulish" }, { "en-GB-oed", "en-GB-x-oed" }, { "i-ami", "ami" }, { "i-bnn", "bnn" }, { "i-default", "en-x-i-default" }, { "i-enochian", "und-x-i-enochian" }, { "i-hak", "hak" }, { "i-klingon", "tlh" }, { "i-lux", "lb" }, { "i-mingo", "see-x-i-mingo" }, { "i-navajo", "nv" }, { "i-pwn", "pwn" }, { "i-tao", "tao" }, { "i-tay", "tay" }, { "i-tsu", "tsu" }, { "no-bok", "nb" }, { "no-nyn", "nn" }, { "sgn-BE-FR", "sfb" }, { "sgn-BE-NL", "vgt" }, { "sgn-CH-DE", "sgg" }, { "zh-guoyu", "cmn" }, { "zh-hakka", "hak" }, { "zh-min", "nan-x-zh-min" }, { "zh-min-nan", "nan" }, { "zh-xiang", "hsn" } };
    for (String[] arrayOfString : arrayOfString1) {
      GRANDFATHERED.put(LocaleUtils.toLowerString(arrayOfString[0]), arrayOfString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\LanguageTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */