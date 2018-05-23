package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.spi.LocaleNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.locale.BaseLocale;
import sun.util.locale.InternalLocaleBuilder;
import sun.util.locale.LanguageTag;
import sun.util.locale.LocaleExtensions;
import sun.util.locale.LocaleMatcher;
import sun.util.locale.LocaleObjectCache;
import sun.util.locale.LocaleSyntaxException;
import sun.util.locale.LocaleUtils;
import sun.util.locale.ParseStatus;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.LocaleServiceProviderPool.LocalizedObjectGetter;

public final class Locale
  implements Cloneable, Serializable
{
  private static final Cache LOCALECACHE = new Cache(null);
  public static final Locale ENGLISH = createConstant("en", "");
  public static final Locale FRENCH = createConstant("fr", "");
  public static final Locale GERMAN = createConstant("de", "");
  public static final Locale ITALIAN = createConstant("it", "");
  public static final Locale JAPANESE = createConstant("ja", "");
  public static final Locale KOREAN = createConstant("ko", "");
  public static final Locale CHINESE = createConstant("zh", "");
  public static final Locale SIMPLIFIED_CHINESE = createConstant("zh", "CN");
  public static final Locale TRADITIONAL_CHINESE = createConstant("zh", "TW");
  public static final Locale FRANCE = createConstant("fr", "FR");
  public static final Locale GERMANY = createConstant("de", "DE");
  public static final Locale ITALY = createConstant("it", "IT");
  public static final Locale JAPAN = createConstant("ja", "JP");
  public static final Locale KOREA = createConstant("ko", "KR");
  public static final Locale CHINA = SIMPLIFIED_CHINESE;
  public static final Locale PRC = SIMPLIFIED_CHINESE;
  public static final Locale TAIWAN = TRADITIONAL_CHINESE;
  public static final Locale UK = createConstant("en", "GB");
  public static final Locale US = createConstant("en", "US");
  public static final Locale CANADA = createConstant("en", "CA");
  public static final Locale CANADA_FRENCH = createConstant("fr", "CA");
  public static final Locale ROOT = createConstant("", "");
  public static final char PRIVATE_USE_EXTENSION = 'x';
  public static final char UNICODE_LOCALE_EXTENSION = 'u';
  static final long serialVersionUID = 9149081749638150636L;
  private static final int DISPLAY_LANGUAGE = 0;
  private static final int DISPLAY_COUNTRY = 1;
  private static final int DISPLAY_VARIANT = 2;
  private static final int DISPLAY_SCRIPT = 3;
  private transient BaseLocale baseLocale;
  private transient LocaleExtensions localeExtensions;
  private volatile transient int hashCodeValue = 0;
  private static volatile Locale defaultLocale = initDefault();
  private static volatile Locale defaultDisplayLocale = null;
  private static volatile Locale defaultFormatLocale = null;
  private volatile transient String languageTag;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("language", String.class), new ObjectStreamField("country", String.class), new ObjectStreamField("variant", String.class), new ObjectStreamField("hashcode", Integer.TYPE), new ObjectStreamField("script", String.class), new ObjectStreamField("extensions", String.class) };
  private static volatile String[] isoLanguages = null;
  private static volatile String[] isoCountries = null;
  
  private Locale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions)
  {
    baseLocale = paramBaseLocale;
    localeExtensions = paramLocaleExtensions;
  }
  
  public Locale(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null)) {
      throw new NullPointerException();
    }
    baseLocale = BaseLocale.getInstance(convertOldISOCodes(paramString1), "", paramString2, paramString3);
    localeExtensions = getCompatibilityExtensions(paramString1, "", paramString2, paramString3);
  }
  
  public Locale(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, "");
  }
  
  public Locale(String paramString)
  {
    this(paramString, "", "");
  }
  
  private static Locale createConstant(String paramString1, String paramString2)
  {
    BaseLocale localBaseLocale = BaseLocale.createInstance(paramString1, paramString2);
    return getInstance(localBaseLocale, null);
  }
  
  static Locale getInstance(String paramString1, String paramString2, String paramString3)
  {
    return getInstance(paramString1, "", paramString2, paramString3, null);
  }
  
  static Locale getInstance(String paramString1, String paramString2, String paramString3, String paramString4, LocaleExtensions paramLocaleExtensions)
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null) || (paramString4 == null)) {
      throw new NullPointerException();
    }
    if (paramLocaleExtensions == null) {
      paramLocaleExtensions = getCompatibilityExtensions(paramString1, paramString2, paramString3, paramString4);
    }
    BaseLocale localBaseLocale = BaseLocale.getInstance(paramString1, paramString2, paramString3, paramString4);
    return getInstance(localBaseLocale, paramLocaleExtensions);
  }
  
  static Locale getInstance(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions)
  {
    LocaleKey localLocaleKey = new LocaleKey(paramBaseLocale, paramLocaleExtensions, null);
    return (Locale)LOCALECACHE.get(localLocaleKey);
  }
  
  public static Locale getDefault()
  {
    return defaultLocale;
  }
  
  public static Locale getDefault(Category paramCategory)
  {
    switch (paramCategory)
    {
    case DISPLAY: 
      if (defaultDisplayLocale == null) {
        synchronized (Locale.class)
        {
          if (defaultDisplayLocale == null) {
            defaultDisplayLocale = initDefault(paramCategory);
          }
        }
      }
      return defaultDisplayLocale;
    case FORMAT: 
      if (defaultFormatLocale == null) {
        synchronized (Locale.class)
        {
          if (defaultFormatLocale == null) {
            defaultFormatLocale = initDefault(paramCategory);
          }
        }
      }
      return defaultFormatLocale;
    }
    if (!$assertionsDisabled) {
      throw new AssertionError("Unknown Category");
    }
    return getDefault();
  }
  
  private static Locale initDefault()
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.language", "en"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.region"));
    String str4;
    String str5;
    String str3;
    if (str2 != null)
    {
      int i = str2.indexOf('_');
      if (i >= 0)
      {
        str4 = str2.substring(0, i);
        str5 = str2.substring(i + 1);
      }
      else
      {
        str4 = str2;
        str5 = "";
      }
      str3 = "";
    }
    else
    {
      str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.script", ""));
      str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.country", ""));
      str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.variant", ""));
    }
    return getInstance(str1, str3, str4, str5, null);
  }
  
  private static Locale initDefault(Category paramCategory)
  {
    return getInstance((String)AccessController.doPrivileged(new GetPropertyAction(languageKey, defaultLocale.getLanguage())), (String)AccessController.doPrivileged(new GetPropertyAction(scriptKey, defaultLocale.getScript())), (String)AccessController.doPrivileged(new GetPropertyAction(countryKey, defaultLocale.getCountry())), (String)AccessController.doPrivileged(new GetPropertyAction(variantKey, defaultLocale.getVariant())), null);
  }
  
  public static synchronized void setDefault(Locale paramLocale)
  {
    setDefault(Category.DISPLAY, paramLocale);
    setDefault(Category.FORMAT, paramLocale);
    defaultLocale = paramLocale;
  }
  
  public static synchronized void setDefault(Category paramCategory, Locale paramLocale)
  {
    if (paramCategory == null) {
      throw new NullPointerException("Category cannot be NULL");
    }
    if (paramLocale == null) {
      throw new NullPointerException("Can't set default locale to NULL");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new PropertyPermission("user.language", "write"));
    }
    switch (paramCategory)
    {
    case DISPLAY: 
      defaultDisplayLocale = paramLocale;
      break;
    case FORMAT: 
      defaultFormatLocale = paramLocale;
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError("Unknown Category");
      }
      break;
    }
  }
  
  public static Locale[] getAvailableLocales()
  {
    return LocaleServiceProviderPool.getAllAvailableLocales();
  }
  
  public static String[] getISOCountries()
  {
    if (isoCountries == null) {
      isoCountries = getISO2Table("ADANDAEAREAFAFGAGATGAIAIAALALBAMARMANANTAOAGOAQATAARARGASASMATAUTAUAUSAWABWAXALAAZAZEBABIHBBBRBBDBGDBEBELBFBFABGBGRBHBHRBIBDIBJBENBLBLMBMBMUBNBRNBOBOLBQBESBRBRABSBHSBTBTNBVBVTBWBWABYBLRBZBLZCACANCCCCKCDCODCFCAFCGCOGCHCHECICIVCKCOKCLCHLCMCMRCNCHNCOCOLCRCRICUCUBCVCPVCWCUWCXCXRCYCYPCZCZEDEDEUDJDJIDKDNKDMDMADODOMDZDZAECECUEEESTEGEGYEHESHERERIESESPETETHFIFINFJFJIFKFLKFMFSMFOFROFRFRAGAGABGBGBRGDGRDGEGEOGFGUFGGGGYGHGHAGIGIBGLGRLGMGMBGNGINGPGLPGQGNQGRGRCGSSGSGTGTMGUGUMGWGNBGYGUYHKHKGHMHMDHNHNDHRHRVHTHTIHUHUNIDIDNIEIRLILISRIMIMNININDIOIOTIQIRQIRIRNISISLITITAJEJEYJMJAMJOJORJPJPNKEKENKGKGZKHKHMKIKIRKMCOMKNKNAKPPRKKRKORKWKWTKYCYMKZKAZLALAOLBLBNLCLCALILIELKLKALRLBRLSLSOLTLTULULUXLVLVALYLBYMAMARMCMCOMDMDAMEMNEMFMAFMGMDGMHMHLMKMKDMLMLIMMMMRMNMNGMOMACMPMNPMQMTQMRMRTMSMSRMTMLTMUMUSMVMDVMWMWIMXMEXMYMYSMZMOZNANAMNCNCLNENERNFNFKNGNGANINICNLNLDNONORNPNPLNRNRUNUNIUNZNZLOMOMNPAPANPEPERPFPYFPGPNGPHPHLPKPAKPLPOLPMSPMPNPCNPRPRIPSPSEPTPRTPWPLWPYPRYQAQATREREUROROURSSRBRURUSRWRWASASAUSBSLBSCSYCSDSDNSESWESGSGPSHSHNSISVNSJSJMSKSVKSLSLESMSMRSNSENSOSOMSRSURSSSSDSTSTPSVSLVSXSXMSYSYRSZSWZTCTCATDTCDTFATFTGTGOTHTHATJTJKTKTKLTLTLSTMTKMTNTUNTOTONTRTURTTTTOTVTUVTWTWNTZTZAUAUKRUGUGAUMUMIUSUSAUYURYUZUZBVAVATVCVCTVEVENVGVGBVIVIRVNVNMVUVUTWFWLFWSWSMYEYEMYTMYTZAZAFZMZMBZWZWE");
    }
    String[] arrayOfString = new String[isoCountries.length];
    System.arraycopy(isoCountries, 0, arrayOfString, 0, isoCountries.length);
    return arrayOfString;
  }
  
  public static String[] getISOLanguages()
  {
    if (isoLanguages == null) {
      isoLanguages = getISO2Table("aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjiyidjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmomolmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul");
    }
    String[] arrayOfString = new String[isoLanguages.length];
    System.arraycopy(isoLanguages, 0, arrayOfString, 0, isoLanguages.length);
    return arrayOfString;
  }
  
  private static String[] getISO2Table(String paramString)
  {
    int i = paramString.length() / 5;
    String[] arrayOfString = new String[i];
    int j = 0;
    for (int k = 0; j < i; k += 5)
    {
      arrayOfString[j] = paramString.substring(k, k + 2);
      j++;
    }
    return arrayOfString;
  }
  
  public String getLanguage()
  {
    return baseLocale.getLanguage();
  }
  
  public String getScript()
  {
    return baseLocale.getScript();
  }
  
  public String getCountry()
  {
    return baseLocale.getRegion();
  }
  
  public String getVariant()
  {
    return baseLocale.getVariant();
  }
  
  public boolean hasExtensions()
  {
    return localeExtensions != null;
  }
  
  public Locale stripExtensions()
  {
    return hasExtensions() ? getInstance(baseLocale, null) : this;
  }
  
  public String getExtension(char paramChar)
  {
    if (!LocaleExtensions.isValidKey(paramChar)) {
      throw new IllegalArgumentException("Ill-formed extension key: " + paramChar);
    }
    return hasExtensions() ? localeExtensions.getExtensionValue(Character.valueOf(paramChar)) : null;
  }
  
  public Set<Character> getExtensionKeys()
  {
    if (!hasExtensions()) {
      return Collections.emptySet();
    }
    return localeExtensions.getKeys();
  }
  
  public Set<String> getUnicodeLocaleAttributes()
  {
    if (!hasExtensions()) {
      return Collections.emptySet();
    }
    return localeExtensions.getUnicodeLocaleAttributes();
  }
  
  public String getUnicodeLocaleType(String paramString)
  {
    if (!isUnicodeExtensionKey(paramString)) {
      throw new IllegalArgumentException("Ill-formed Unicode locale key: " + paramString);
    }
    return hasExtensions() ? localeExtensions.getUnicodeLocaleType(paramString) : null;
  }
  
  public Set<String> getUnicodeLocaleKeys()
  {
    if (localeExtensions == null) {
      return Collections.emptySet();
    }
    return localeExtensions.getUnicodeLocaleKeys();
  }
  
  BaseLocale getBaseLocale()
  {
    return baseLocale;
  }
  
  LocaleExtensions getLocaleExtensions()
  {
    return localeExtensions;
  }
  
  public final String toString()
  {
    int i = baseLocale.getLanguage().length() != 0 ? 1 : 0;
    int j = baseLocale.getScript().length() != 0 ? 1 : 0;
    int k = baseLocale.getRegion().length() != 0 ? 1 : 0;
    int m = baseLocale.getVariant().length() != 0 ? 1 : 0;
    int n = (localeExtensions != null) && (localeExtensions.getID().length() != 0) ? 1 : 0;
    StringBuilder localStringBuilder = new StringBuilder(baseLocale.getLanguage());
    if ((k != 0) || ((i != 0) && ((m != 0) || (j != 0) || (n != 0)))) {
      localStringBuilder.append('_').append(baseLocale.getRegion());
    }
    if ((m != 0) && ((i != 0) || (k != 0))) {
      localStringBuilder.append('_').append(baseLocale.getVariant());
    }
    if ((j != 0) && ((i != 0) || (k != 0))) {
      localStringBuilder.append("_#").append(baseLocale.getScript());
    }
    if ((n != 0) && ((i != 0) || (k != 0)))
    {
      localStringBuilder.append('_');
      if (j == 0) {
        localStringBuilder.append('#');
      }
      localStringBuilder.append(localeExtensions.getID());
    }
    return localStringBuilder.toString();
  }
  
  public String toLanguageTag()
  {
    if (languageTag != null) {
      return languageTag;
    }
    LanguageTag localLanguageTag = LanguageTag.parseLocale(baseLocale, localeExtensions);
    StringBuilder localStringBuilder = new StringBuilder();
    String str1 = localLanguageTag.getLanguage();
    if (str1.length() > 0) {
      localStringBuilder.append(LanguageTag.canonicalizeLanguage(str1));
    }
    str1 = localLanguageTag.getScript();
    if (str1.length() > 0)
    {
      localStringBuilder.append("-");
      localStringBuilder.append(LanguageTag.canonicalizeScript(str1));
    }
    str1 = localLanguageTag.getRegion();
    if (str1.length() > 0)
    {
      localStringBuilder.append("-");
      localStringBuilder.append(LanguageTag.canonicalizeRegion(str1));
    }
    List localList = localLanguageTag.getVariants();
    Object localObject1 = localList.iterator();
    String str2;
    while (((Iterator)localObject1).hasNext())
    {
      str2 = (String)((Iterator)localObject1).next();
      localStringBuilder.append("-");
      localStringBuilder.append(str2);
    }
    localList = localLanguageTag.getExtensions();
    localObject1 = localList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      str2 = (String)((Iterator)localObject1).next();
      localStringBuilder.append("-");
      localStringBuilder.append(LanguageTag.canonicalizeExtension(str2));
    }
    str1 = localLanguageTag.getPrivateuse();
    if (str1.length() > 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("-");
      }
      localStringBuilder.append("x").append("-");
      localStringBuilder.append(str1);
    }
    localObject1 = localStringBuilder.toString();
    synchronized (this)
    {
      if (languageTag == null) {
        languageTag = ((String)localObject1);
      }
    }
    return languageTag;
  }
  
  public static Locale forLanguageTag(String paramString)
  {
    LanguageTag localLanguageTag = LanguageTag.parse(paramString, null);
    InternalLocaleBuilder localInternalLocaleBuilder = new InternalLocaleBuilder();
    localInternalLocaleBuilder.setLanguageTag(localLanguageTag);
    BaseLocale localBaseLocale = localInternalLocaleBuilder.getBaseLocale();
    LocaleExtensions localLocaleExtensions = localInternalLocaleBuilder.getLocaleExtensions();
    if ((localLocaleExtensions == null) && (localBaseLocale.getVariant().length() > 0)) {
      localLocaleExtensions = getCompatibilityExtensions(localBaseLocale.getLanguage(), localBaseLocale.getScript(), localBaseLocale.getRegion(), localBaseLocale.getVariant());
    }
    return getInstance(localBaseLocale, localLocaleExtensions);
  }
  
  public String getISO3Language()
    throws MissingResourceException
  {
    String str1 = baseLocale.getLanguage();
    if (str1.length() == 3) {
      return str1;
    }
    String str2 = getISO3Code(str1, "aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjiyidjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmomolmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul");
    if (str2 == null) {
      throw new MissingResourceException("Couldn't find 3-letter language code for " + str1, "FormatData_" + toString(), "ShortLanguage");
    }
    return str2;
  }
  
  public String getISO3Country()
    throws MissingResourceException
  {
    String str = getISO3Code(baseLocale.getRegion(), "ADANDAEAREAFAFGAGATGAIAIAALALBAMARMANANTAOAGOAQATAARARGASASMATAUTAUAUSAWABWAXALAAZAZEBABIHBBBRBBDBGDBEBELBFBFABGBGRBHBHRBIBDIBJBENBLBLMBMBMUBNBRNBOBOLBQBESBRBRABSBHSBTBTNBVBVTBWBWABYBLRBZBLZCACANCCCCKCDCODCFCAFCGCOGCHCHECICIVCKCOKCLCHLCMCMRCNCHNCOCOLCRCRICUCUBCVCPVCWCUWCXCXRCYCYPCZCZEDEDEUDJDJIDKDNKDMDMADODOMDZDZAECECUEEESTEGEGYEHESHERERIESESPETETHFIFINFJFJIFKFLKFMFSMFOFROFRFRAGAGABGBGBRGDGRDGEGEOGFGUFGGGGYGHGHAGIGIBGLGRLGMGMBGNGINGPGLPGQGNQGRGRCGSSGSGTGTMGUGUMGWGNBGYGUYHKHKGHMHMDHNHNDHRHRVHTHTIHUHUNIDIDNIEIRLILISRIMIMNININDIOIOTIQIRQIRIRNISISLITITAJEJEYJMJAMJOJORJPJPNKEKENKGKGZKHKHMKIKIRKMCOMKNKNAKPPRKKRKORKWKWTKYCYMKZKAZLALAOLBLBNLCLCALILIELKLKALRLBRLSLSOLTLTULULUXLVLVALYLBYMAMARMCMCOMDMDAMEMNEMFMAFMGMDGMHMHLMKMKDMLMLIMMMMRMNMNGMOMACMPMNPMQMTQMRMRTMSMSRMTMLTMUMUSMVMDVMWMWIMXMEXMYMYSMZMOZNANAMNCNCLNENERNFNFKNGNGANINICNLNLDNONORNPNPLNRNRUNUNIUNZNZLOMOMNPAPANPEPERPFPYFPGPNGPHPHLPKPAKPLPOLPMSPMPNPCNPRPRIPSPSEPTPRTPWPLWPYPRYQAQATREREUROROURSSRBRURUSRWRWASASAUSBSLBSCSYCSDSDNSESWESGSGPSHSHNSISVNSJSJMSKSVKSLSLESMSMRSNSENSOSOMSRSURSSSSDSTSTPSVSLVSXSXMSYSYRSZSWZTCTCATDTCDTFATFTGTGOTHTHATJTJKTKTKLTLTLSTMTKMTNTUNTOTONTRTURTTTTOTVTUVTWTWNTZTZAUAUKRUGUGAUMUMIUSUSAUYURYUZUZBVAVATVCVCTVEVENVGVGBVIVIRVNVNMVUVUTWFWLFWSWSMYEYEMYTMYTZAZAFZMZMBZWZWE");
    if (str == null) {
      throw new MissingResourceException("Couldn't find 3-letter country code for " + baseLocale.getRegion(), "FormatData_" + toString(), "ShortCountry");
    }
    return str;
  }
  
  private static String getISO3Code(String paramString1, String paramString2)
  {
    int i = paramString1.length();
    if (i == 0) {
      return "";
    }
    int j = paramString2.length();
    int k = j;
    if (i == 2)
    {
      int m = paramString1.charAt(0);
      int n = paramString1.charAt(1);
      for (k = 0; (k < j) && ((paramString2.charAt(k) != m) || (paramString2.charAt(k + 1) != n)); k += 5) {}
    }
    return k < j ? paramString2.substring(k + 2, k + 5) : null;
  }
  
  public final String getDisplayLanguage()
  {
    return getDisplayLanguage(getDefault(Category.DISPLAY));
  }
  
  public String getDisplayLanguage(Locale paramLocale)
  {
    return getDisplayString(baseLocale.getLanguage(), paramLocale, 0);
  }
  
  public String getDisplayScript()
  {
    return getDisplayScript(getDefault(Category.DISPLAY));
  }
  
  public String getDisplayScript(Locale paramLocale)
  {
    return getDisplayString(baseLocale.getScript(), paramLocale, 3);
  }
  
  public final String getDisplayCountry()
  {
    return getDisplayCountry(getDefault(Category.DISPLAY));
  }
  
  public String getDisplayCountry(Locale paramLocale)
  {
    return getDisplayString(baseLocale.getRegion(), paramLocale, 1);
  }
  
  private String getDisplayString(String paramString, Locale paramLocale, int paramInt)
  {
    if (paramString.length() == 0) {
      return "";
    }
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(LocaleNameProvider.class);
    String str1 = paramInt == 2 ? "%%" + paramString : paramString;
    String str2 = (String)localLocaleServiceProviderPool.getLocalizedObject(LocaleNameGetter.INSTANCE, paramLocale, str1, new Object[] { Integer.valueOf(paramInt), paramString });
    if (str2 != null) {
      return str2;
    }
    return paramString;
  }
  
  public final String getDisplayVariant()
  {
    return getDisplayVariant(getDefault(Category.DISPLAY));
  }
  
  public String getDisplayVariant(Locale paramLocale)
  {
    if (baseLocale.getVariant().length() == 0) {
      return "";
    }
    LocaleResources localLocaleResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
    String[] arrayOfString = getDisplayVariantArray(paramLocale);
    return formatList(arrayOfString, localLocaleResources.getLocaleName("ListPattern"), localLocaleResources.getLocaleName("ListCompositionPattern"));
  }
  
  public final String getDisplayName()
  {
    return getDisplayName(getDefault(Category.DISPLAY));
  }
  
  public String getDisplayName(Locale paramLocale)
  {
    LocaleResources localLocaleResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
    String str1 = getDisplayLanguage(paramLocale);
    String str2 = getDisplayScript(paramLocale);
    String str3 = getDisplayCountry(paramLocale);
    String[] arrayOfString1 = getDisplayVariantArray(paramLocale);
    String str4 = localLocaleResources.getLocaleName("DisplayNamePattern");
    String str5 = localLocaleResources.getLocaleName("ListPattern");
    String str6 = localLocaleResources.getLocaleName("ListCompositionPattern");
    String str7 = null;
    String[] arrayOfString2 = null;
    if ((str1.length() == 0) && (str2.length() == 0) && (str3.length() == 0))
    {
      if (arrayOfString1.length == 0) {
        return "";
      }
      return formatList(arrayOfString1, str5, str6);
    }
    ArrayList localArrayList = new ArrayList(4);
    if (str1.length() != 0) {
      localArrayList.add(str1);
    }
    if (str2.length() != 0) {
      localArrayList.add(str2);
    }
    if (str3.length() != 0) {
      localArrayList.add(str3);
    }
    if (arrayOfString1.length != 0) {
      localArrayList.addAll(Arrays.asList(arrayOfString1));
    }
    str7 = (String)localArrayList.get(0);
    int i = localArrayList.size();
    arrayOfString2 = i > 1 ? (String[])localArrayList.subList(1, i).toArray(new String[i - 1]) : new String[0];
    Object[] arrayOfObject = { new Integer(arrayOfString2.length != 0 ? 2 : 1), str7, arrayOfString2.length != 0 ? formatList(arrayOfString2, str5, str6) : null };
    if (str4 != null) {
      return new MessageFormat(str4).format(arrayOfObject);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append((String)arrayOfObject[1]);
    if (arrayOfObject.length > 2)
    {
      localStringBuilder.append(" (");
      localStringBuilder.append((String)arrayOfObject[2]);
      localStringBuilder.append(')');
    }
    return localStringBuilder.toString();
  }
  
  public Object clone()
  {
    try
    {
      Locale localLocale = (Locale)super.clone();
      return localLocale;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public int hashCode()
  {
    int i = hashCodeValue;
    if (i == 0)
    {
      i = baseLocale.hashCode();
      if (localeExtensions != null) {
        i ^= localeExtensions.hashCode();
      }
      hashCodeValue = i;
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Locale)) {
      return false;
    }
    BaseLocale localBaseLocale = baseLocale;
    if (!baseLocale.equals(localBaseLocale)) {
      return false;
    }
    if (localeExtensions == null) {
      return localeExtensions == null;
    }
    return localeExtensions.equals(localeExtensions);
  }
  
  private String[] getDisplayVariantArray(Locale paramLocale)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(baseLocale.getVariant(), "_");
    String[] arrayOfString = new String[localStringTokenizer.countTokens()];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = getDisplayString(localStringTokenizer.nextToken(), paramLocale, 2);
    }
    return arrayOfString;
  }
  
  private static String formatList(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject = new StringBuilder();
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        if (i > 0) {
          ((StringBuilder)localObject).append(',');
        }
        ((StringBuilder)localObject).append(paramArrayOfString[i]);
      }
      return ((StringBuilder)localObject).toString();
    }
    if (paramArrayOfString.length > 3)
    {
      localObject = new MessageFormat(paramString2);
      paramArrayOfString = composeList((MessageFormat)localObject, paramArrayOfString);
    }
    Object localObject = new Object[paramArrayOfString.length + 1];
    System.arraycopy(paramArrayOfString, 0, localObject, 1, paramArrayOfString.length);
    localObject[0] = new Integer(paramArrayOfString.length);
    MessageFormat localMessageFormat = new MessageFormat(paramString1);
    return localMessageFormat.format(localObject);
  }
  
  private static String[] composeList(MessageFormat paramMessageFormat, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length <= 3) {
      return paramArrayOfString;
    }
    String[] arrayOfString1 = { paramArrayOfString[0], paramArrayOfString[1] };
    String str = paramMessageFormat.format(arrayOfString1);
    String[] arrayOfString2 = new String[paramArrayOfString.length - 1];
    System.arraycopy(paramArrayOfString, 2, arrayOfString2, 1, arrayOfString2.length - 1);
    arrayOfString2[0] = str;
    return composeList(paramMessageFormat, arrayOfString2);
  }
  
  private static boolean isUnicodeExtensionKey(String paramString)
  {
    return (paramString.length() == 2) && (LocaleUtils.isAlphaNumericString(paramString));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("language", baseLocale.getLanguage());
    localPutField.put("script", baseLocale.getScript());
    localPutField.put("country", baseLocale.getRegion());
    localPutField.put("variant", baseLocale.getVariant());
    localPutField.put("extensions", localeExtensions == null ? "" : localeExtensions.getID());
    localPutField.put("hashcode", -1);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str1 = (String)localGetField.get("language", "");
    String str2 = (String)localGetField.get("script", "");
    String str3 = (String)localGetField.get("country", "");
    String str4 = (String)localGetField.get("variant", "");
    String str5 = (String)localGetField.get("extensions", "");
    baseLocale = BaseLocale.getInstance(convertOldISOCodes(str1), str2, str3, str4);
    if (str5.length() > 0) {
      try
      {
        InternalLocaleBuilder localInternalLocaleBuilder = new InternalLocaleBuilder();
        localInternalLocaleBuilder.setExtensions(str5);
        localeExtensions = localInternalLocaleBuilder.getLocaleExtensions();
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage());
      }
    } else {
      localeExtensions = null;
    }
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    return getInstance(baseLocale.getLanguage(), baseLocale.getScript(), baseLocale.getRegion(), baseLocale.getVariant(), localeExtensions);
  }
  
  private static String convertOldISOCodes(String paramString)
  {
    paramString = LocaleUtils.toLowerString(paramString).intern();
    if (paramString == "he") {
      return "iw";
    }
    if (paramString == "yi") {
      return "ji";
    }
    if (paramString == "id") {
      return "in";
    }
    return paramString;
  }
  
  private static LocaleExtensions getCompatibilityExtensions(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    LocaleExtensions localLocaleExtensions = null;
    if ((LocaleUtils.caseIgnoreMatch(paramString1, "ja")) && (paramString2.length() == 0) && (LocaleUtils.caseIgnoreMatch(paramString3, "jp")) && ("JP".equals(paramString4))) {
      localLocaleExtensions = LocaleExtensions.CALENDAR_JAPANESE;
    } else if ((LocaleUtils.caseIgnoreMatch(paramString1, "th")) && (paramString2.length() == 0) && (LocaleUtils.caseIgnoreMatch(paramString3, "th")) && ("TH".equals(paramString4))) {
      localLocaleExtensions = LocaleExtensions.NUMBER_THAI;
    }
    return localLocaleExtensions;
  }
  
  public static List<Locale> filter(List<LanguageRange> paramList, Collection<Locale> paramCollection, FilteringMode paramFilteringMode)
  {
    return LocaleMatcher.filter(paramList, paramCollection, paramFilteringMode);
  }
  
  public static List<Locale> filter(List<LanguageRange> paramList, Collection<Locale> paramCollection)
  {
    return filter(paramList, paramCollection, FilteringMode.AUTOSELECT_FILTERING);
  }
  
  public static List<String> filterTags(List<LanguageRange> paramList, Collection<String> paramCollection, FilteringMode paramFilteringMode)
  {
    return LocaleMatcher.filterTags(paramList, paramCollection, paramFilteringMode);
  }
  
  public static List<String> filterTags(List<LanguageRange> paramList, Collection<String> paramCollection)
  {
    return filterTags(paramList, paramCollection, FilteringMode.AUTOSELECT_FILTERING);
  }
  
  public static Locale lookup(List<LanguageRange> paramList, Collection<Locale> paramCollection)
  {
    return LocaleMatcher.lookup(paramList, paramCollection);
  }
  
  public static String lookupTag(List<LanguageRange> paramList, Collection<String> paramCollection)
  {
    return LocaleMatcher.lookupTag(paramList, paramCollection);
  }
  
  public static final class Builder
  {
    private final InternalLocaleBuilder localeBuilder = new InternalLocaleBuilder();
    
    public Builder() {}
    
    public Builder setLocale(Locale paramLocale)
    {
      try
      {
        localeBuilder.setLocale(baseLocale, localeExtensions);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder setLanguageTag(String paramString)
    {
      ParseStatus localParseStatus = new ParseStatus();
      LanguageTag localLanguageTag = LanguageTag.parse(paramString, localParseStatus);
      if (localParseStatus.isError()) {
        throw new IllformedLocaleException(localParseStatus.getErrorMessage(), localParseStatus.getErrorIndex());
      }
      localeBuilder.setLanguageTag(localLanguageTag);
      return this;
    }
    
    public Builder setLanguage(String paramString)
    {
      try
      {
        localeBuilder.setLanguage(paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder setScript(String paramString)
    {
      try
      {
        localeBuilder.setScript(paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder setRegion(String paramString)
    {
      try
      {
        localeBuilder.setRegion(paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder setVariant(String paramString)
    {
      try
      {
        localeBuilder.setVariant(paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder setExtension(char paramChar, String paramString)
    {
      try
      {
        localeBuilder.setExtension(paramChar, paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder setUnicodeLocaleKeyword(String paramString1, String paramString2)
    {
      try
      {
        localeBuilder.setUnicodeLocaleKeyword(paramString1, paramString2);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder addUnicodeLocaleAttribute(String paramString)
    {
      try
      {
        localeBuilder.addUnicodeLocaleAttribute(paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder removeUnicodeLocaleAttribute(String paramString)
    {
      try
      {
        localeBuilder.removeUnicodeLocaleAttribute(paramString);
      }
      catch (LocaleSyntaxException localLocaleSyntaxException)
      {
        throw new IllformedLocaleException(localLocaleSyntaxException.getMessage(), localLocaleSyntaxException.getErrorIndex());
      }
      return this;
    }
    
    public Builder clear()
    {
      localeBuilder.clear();
      return this;
    }
    
    public Builder clearExtensions()
    {
      localeBuilder.clearExtensions();
      return this;
    }
    
    public Locale build()
    {
      BaseLocale localBaseLocale = localeBuilder.getBaseLocale();
      LocaleExtensions localLocaleExtensions = localeBuilder.getLocaleExtensions();
      if ((localLocaleExtensions == null) && (localBaseLocale.getVariant().length() > 0)) {
        localLocaleExtensions = Locale.getCompatibilityExtensions(localBaseLocale.getLanguage(), localBaseLocale.getScript(), localBaseLocale.getRegion(), localBaseLocale.getVariant());
      }
      return Locale.getInstance(localBaseLocale, localLocaleExtensions);
    }
  }
  
  private static class Cache
    extends LocaleObjectCache<Locale.LocaleKey, Locale>
  {
    private Cache() {}
    
    protected Locale createObject(Locale.LocaleKey paramLocaleKey)
    {
      return new Locale(Locale.LocaleKey.access$200(paramLocaleKey), Locale.LocaleKey.access$300(paramLocaleKey), null);
    }
  }
  
  public static enum Category
  {
    DISPLAY("user.language.display", "user.script.display", "user.country.display", "user.variant.display"),  FORMAT("user.language.format", "user.script.format", "user.country.format", "user.variant.format");
    
    final String languageKey;
    final String scriptKey;
    final String countryKey;
    final String variantKey;
    
    private Category(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      languageKey = paramString1;
      scriptKey = paramString2;
      countryKey = paramString3;
      variantKey = paramString4;
    }
  }
  
  public static enum FilteringMode
  {
    AUTOSELECT_FILTERING,  EXTENDED_FILTERING,  IGNORE_EXTENDED_RANGES,  MAP_EXTENDED_RANGES,  REJECT_EXTENDED_RANGES;
    
    private FilteringMode() {}
  }
  
  public static final class LanguageRange
  {
    public static final double MAX_WEIGHT = 1.0D;
    public static final double MIN_WEIGHT = 0.0D;
    private final String range;
    private final double weight;
    private volatile int hash = 0;
    
    public LanguageRange(String paramString)
    {
      this(paramString, 1.0D);
    }
    
    public LanguageRange(String paramString, double paramDouble)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
        throw new IllegalArgumentException("weight=" + paramDouble);
      }
      paramString = paramString.toLowerCase();
      int i = 0;
      String[] arrayOfString = paramString.split("-");
      if ((isSubtagIllFormed(arrayOfString[0], true)) || (paramString.endsWith("-"))) {
        i = 1;
      } else {
        for (int j = 1; j < arrayOfString.length; j++) {
          if (isSubtagIllFormed(arrayOfString[j], false))
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0) {
        throw new IllegalArgumentException("range=" + paramString);
      }
      range = paramString;
      weight = paramDouble;
    }
    
    private static boolean isSubtagIllFormed(String paramString, boolean paramBoolean)
    {
      if ((paramString.equals("")) || (paramString.length() > 8)) {
        return true;
      }
      if (paramString.equals("*")) {
        return false;
      }
      char[] arrayOfChar1 = paramString.toCharArray();
      int k;
      if (paramBoolean) {
        for (k : arrayOfChar1) {
          if ((k < 97) || (k > 122)) {
            return true;
          }
        }
      } else {
        for (k : arrayOfChar1) {
          if ((k < 48) || ((k > 57) && (k < 97)) || (k > 122)) {
            return true;
          }
        }
      }
      return false;
    }
    
    public String getRange()
    {
      return range;
    }
    
    public double getWeight()
    {
      return weight;
    }
    
    public static List<LanguageRange> parse(String paramString)
    {
      return LocaleMatcher.parse(paramString);
    }
    
    public static List<LanguageRange> parse(String paramString, Map<String, List<String>> paramMap)
    {
      return mapEquivalents(parse(paramString), paramMap);
    }
    
    public static List<LanguageRange> mapEquivalents(List<LanguageRange> paramList, Map<String, List<String>> paramMap)
    {
      return LocaleMatcher.mapEquivalents(paramList, paramMap);
    }
    
    public int hashCode()
    {
      if (hash == 0)
      {
        int i = 17;
        i = 37 * i + range.hashCode();
        long l = Double.doubleToLongBits(weight);
        i = 37 * i + (int)(l ^ l >>> 32);
        hash = i;
      }
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof LanguageRange)) {
        return false;
      }
      LanguageRange localLanguageRange = (LanguageRange)paramObject;
      return (hash == hash) && (range.equals(range)) && (weight == weight);
    }
  }
  
  private static final class LocaleKey
  {
    private final BaseLocale base;
    private final LocaleExtensions exts;
    private final int hash;
    
    private LocaleKey(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions)
    {
      base = paramBaseLocale;
      exts = paramLocaleExtensions;
      int i = base.hashCode();
      if (exts != null) {
        i ^= exts.hashCode();
      }
      hash = i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof LocaleKey)) {
        return false;
      }
      LocaleKey localLocaleKey = (LocaleKey)paramObject;
      if ((hash != hash) || (!base.equals(base))) {
        return false;
      }
      if (exts == null) {
        return exts == null;
      }
      return exts.equals(exts);
    }
    
    public int hashCode()
    {
      return hash;
    }
  }
  
  private static class LocaleNameGetter
    implements LocaleServiceProviderPool.LocalizedObjectGetter<LocaleNameProvider, String>
  {
    private static final LocaleNameGetter INSTANCE = new LocaleNameGetter();
    
    private LocaleNameGetter() {}
    
    public String getObject(LocaleNameProvider paramLocaleNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
    {
      assert (paramVarArgs.length == 2);
      int i = ((Integer)paramVarArgs[0]).intValue();
      String str = (String)paramVarArgs[1];
      switch (i)
      {
      case 0: 
        return paramLocaleNameProvider.getDisplayLanguage(str, paramLocale);
      case 1: 
        return paramLocaleNameProvider.getDisplayCountry(str, paramLocale);
      case 2: 
        return paramLocaleNameProvider.getDisplayVariant(str, paramLocale);
      case 3: 
        return paramLocaleNameProvider.getDisplayScript(str, paramLocale);
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Locale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */