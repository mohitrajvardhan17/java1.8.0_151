package sun.awt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import sun.font.CompositeFontDescriptor;
import sun.font.FontUtilities;
import sun.font.SunFontManager;
import sun.util.logging.PlatformLogger;

public abstract class FontConfiguration
{
  protected static String osVersion;
  protected static String osName;
  protected static String encoding;
  protected static Locale startupLocale = null;
  protected static Hashtable localeMap = null;
  private static FontConfiguration fontConfig;
  private static PlatformLogger logger;
  protected static boolean isProperties = true;
  protected SunFontManager fontManager;
  protected boolean preferLocaleFonts;
  protected boolean preferPropFonts;
  private File fontConfigFile;
  private boolean foundOsSpecificFile;
  private boolean inited;
  private String javaLib;
  private static short stringIDNum;
  private static short[] stringIDs;
  private static StringBuilder stringTable;
  public static boolean verbose;
  private short initELC = -1;
  private Locale initLocale;
  private String initEncoding;
  private String alphabeticSuffix;
  private short[][][] compFontNameIDs = new short[5][4][];
  private int[][][] compExclusions = new int[5][][];
  private int[] compCoreNum = new int[5];
  private Set<Short> coreFontNameIDs = new HashSet();
  private Set<Short> fallbackFontNameIDs = new HashSet();
  protected static final int NUM_FONTS = 5;
  protected static final int NUM_STYLES = 4;
  protected static final String[] fontNames = { "serif", "sansserif", "monospaced", "dialog", "dialoginput" };
  protected static final String[] publicFontNames = { "Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput" };
  protected static final String[] styleNames = { "plain", "bold", "italic", "bolditalic" };
  protected static String[] installedFallbackFontFiles = null;
  protected HashMap reorderMap = null;
  private Hashtable charsetRegistry = new Hashtable(5);
  private FontDescriptor[][][] fontDescriptors = new FontDescriptor[5][4][];
  HashMap<String, Boolean> existsMap;
  private int numCoreFonts = -1;
  private String[] componentFonts = null;
  HashMap<String, String> filenamesMap = new HashMap();
  HashSet<String> coreFontFileNames = new HashSet();
  private static final int HEAD_LENGTH = 20;
  private static final int INDEX_scriptIDs = 0;
  private static final int INDEX_scriptFonts = 1;
  private static final int INDEX_elcIDs = 2;
  private static final int INDEX_sequences = 3;
  private static final int INDEX_fontfileNameIDs = 4;
  private static final int INDEX_componentFontNameIDs = 5;
  private static final int INDEX_filenames = 6;
  private static final int INDEX_awtfontpaths = 7;
  private static final int INDEX_exclusions = 8;
  private static final int INDEX_proportionals = 9;
  private static final int INDEX_scriptFontsMotif = 10;
  private static final int INDEX_alphabeticSuffix = 11;
  private static final int INDEX_stringIDs = 12;
  private static final int INDEX_stringTable = 13;
  private static final int INDEX_TABLEEND = 14;
  private static final int INDEX_fallbackScripts = 15;
  private static final int INDEX_appendedfontpath = 16;
  private static final int INDEX_version = 17;
  private static short[] head;
  private static short[] table_scriptIDs;
  private static short[] table_scriptFonts;
  private static short[] table_elcIDs;
  private static short[] table_sequences;
  private static short[] table_fontfileNameIDs;
  private static short[] table_componentFontNameIDs;
  private static short[] table_filenames;
  protected static short[] table_awtfontpaths;
  private static short[] table_exclusions;
  private static short[] table_proportionals;
  private static short[] table_scriptFontsMotif;
  private static short[] table_alphabeticSuffix;
  private static short[] table_stringIDs;
  private static char[] table_stringTable;
  private HashMap<String, Short> reorderScripts;
  private static String[] stringCache;
  private static final int[] EMPTY_INT_ARRAY = new int[0];
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final short[] EMPTY_SHORT_ARRAY = new short[0];
  private static final String UNDEFINED_COMPONENT_FONT = "unknown";
  
  public FontConfiguration(SunFontManager paramSunFontManager)
  {
    if (FontUtilities.debugFonts()) {
      FontUtilities.getLogger().info("Creating standard Font Configuration");
    }
    if ((FontUtilities.debugFonts()) && (logger == null)) {
      logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
    }
    fontManager = paramSunFontManager;
    setOsNameAndVersion();
    setEncoding();
    findFontConfigFile();
  }
  
  public synchronized boolean init()
  {
    if (!inited)
    {
      preferLocaleFonts = false;
      preferPropFonts = false;
      setFontConfiguration();
      readFontConfigFile(fontConfigFile);
      initFontConfig();
      inited = true;
    }
    return true;
  }
  
  public FontConfiguration(SunFontManager paramSunFontManager, boolean paramBoolean1, boolean paramBoolean2)
  {
    fontManager = paramSunFontManager;
    if (FontUtilities.debugFonts()) {
      FontUtilities.getLogger().info("Creating alternate Font Configuration");
    }
    preferLocaleFonts = paramBoolean1;
    preferPropFonts = paramBoolean2;
    initFontConfig();
  }
  
  protected void setOsNameAndVersion()
  {
    osName = System.getProperty("os.name");
    osVersion = System.getProperty("os.version");
  }
  
  private void setEncoding()
  {
    encoding = Charset.defaultCharset().name();
    startupLocale = SunToolkit.getStartupLocale();
  }
  
  public boolean foundOsSpecificFile()
  {
    return foundOsSpecificFile;
  }
  
  public boolean fontFilesArePresent()
  {
    init();
    short s1 = compFontNameIDs[0][0][0];
    short s2 = getComponentFileID(s1);
    final String str = mapFileName(getComponentFileName(s2));
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          File localFile = new File(str);
          return Boolean.valueOf(localFile.exists());
        }
        catch (Exception localException) {}
        return Boolean.valueOf(false);
      }
    });
    return localBoolean.booleanValue();
  }
  
  private void findFontConfigFile()
  {
    foundOsSpecificFile = true;
    String str1 = System.getProperty("java.home");
    if (str1 == null) {
      throw new Error("java.home property not set");
    }
    javaLib = (str1 + File.separator + "lib");
    String str2 = System.getProperty("sun.awt.fontconfig");
    if (str2 != null) {
      fontConfigFile = new File(str2);
    } else {
      fontConfigFile = findFontConfigFile(javaLib);
    }
  }
  
  private void readFontConfigFile(File paramFile)
  {
    getInstalledFallbackFonts(javaLib);
    if (paramFile != null) {
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(paramFile.getPath());
        if (isProperties) {
          loadProperties(localFileInputStream);
        } else {
          loadBinary(localFileInputStream);
        }
        localFileInputStream.close();
        if (FontUtilities.debugFonts()) {
          logger.config("Read logical font configuration from " + paramFile);
        }
      }
      catch (IOException localIOException)
      {
        if (FontUtilities.debugFonts()) {
          logger.config("Failed to read logical font configuration from " + paramFile);
        }
      }
    }
    String str = getVersion();
    if ((!"1".equals(str)) && (FontUtilities.debugFonts())) {
      logger.config("Unsupported fontconfig version: " + str);
    }
  }
  
  protected void getInstalledFallbackFonts(String paramString)
  {
    String str = paramString + File.separator + "fonts" + File.separator + "fallback";
    File localFile = new File(str);
    if ((localFile.exists()) && (localFile.isDirectory()))
    {
      String[] arrayOfString1 = localFile.list(fontManager.getTrueTypeFilter());
      String[] arrayOfString2 = localFile.list(fontManager.getType1Filter());
      int i = arrayOfString1 == null ? 0 : arrayOfString1.length;
      int j = arrayOfString2 == null ? 0 : arrayOfString2.length;
      int k = i + j;
      if (i + j == 0) {
        return;
      }
      installedFallbackFontFiles = new String[k];
      for (int m = 0; m < i; m++) {
        installedFallbackFontFiles[m] = (localFile + File.separator + arrayOfString1[m]);
      }
      for (m = 0; m < j; m++) {
        installedFallbackFontFiles[(m + i)] = (localFile + File.separator + arrayOfString2[m]);
      }
      fontManager.registerFontsInDir(str);
    }
  }
  
  private File findImpl(String paramString)
  {
    File localFile = new File(paramString + ".properties");
    if (localFile.canRead())
    {
      isProperties = true;
      return localFile;
    }
    localFile = new File(paramString + ".bfc");
    if (localFile.canRead())
    {
      isProperties = false;
      return localFile;
    }
    return null;
  }
  
  private File findFontConfigFile(String paramString)
  {
    String str1 = paramString + File.separator + "fontconfig";
    String str2 = null;
    if ((osVersion != null) && (osName != null))
    {
      localFile = findImpl(str1 + "." + osName + "." + osVersion);
      if (localFile != null) {
        return localFile;
      }
      int i = osVersion.indexOf(".");
      if (i != -1)
      {
        str2 = osVersion.substring(0, osVersion.indexOf("."));
        localFile = findImpl(str1 + "." + osName + "." + str2);
        if (localFile != null) {
          return localFile;
        }
      }
    }
    if (osName != null)
    {
      localFile = findImpl(str1 + "." + osName);
      if (localFile != null) {
        return localFile;
      }
    }
    if (osVersion != null)
    {
      localFile = findImpl(str1 + "." + osVersion);
      if (localFile != null) {
        return localFile;
      }
      if (str2 != null)
      {
        localFile = findImpl(str1 + "." + str2);
        if (localFile != null) {
          return localFile;
        }
      }
    }
    foundOsSpecificFile = false;
    File localFile = findImpl(str1);
    if (localFile != null) {
      return localFile;
    }
    return null;
  }
  
  public static void loadBinary(InputStream paramInputStream)
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    head = readShortTable(localDataInputStream, 20);
    int[] arrayOfInt = new int[14];
    for (int i = 0; i < 14; i++) {
      arrayOfInt[i] = (head[(i + 1)] - head[i]);
    }
    table_scriptIDs = readShortTable(localDataInputStream, arrayOfInt[0]);
    table_scriptFonts = readShortTable(localDataInputStream, arrayOfInt[1]);
    table_elcIDs = readShortTable(localDataInputStream, arrayOfInt[2]);
    table_sequences = readShortTable(localDataInputStream, arrayOfInt[3]);
    table_fontfileNameIDs = readShortTable(localDataInputStream, arrayOfInt[4]);
    table_componentFontNameIDs = readShortTable(localDataInputStream, arrayOfInt[5]);
    table_filenames = readShortTable(localDataInputStream, arrayOfInt[6]);
    table_awtfontpaths = readShortTable(localDataInputStream, arrayOfInt[7]);
    table_exclusions = readShortTable(localDataInputStream, arrayOfInt[8]);
    table_proportionals = readShortTable(localDataInputStream, arrayOfInt[9]);
    table_scriptFontsMotif = readShortTable(localDataInputStream, arrayOfInt[10]);
    table_alphabeticSuffix = readShortTable(localDataInputStream, arrayOfInt[11]);
    table_stringIDs = readShortTable(localDataInputStream, arrayOfInt[12]);
    stringCache = new String[table_stringIDs.length + 1];
    i = arrayOfInt[13];
    byte[] arrayOfByte = new byte[i * 2];
    table_stringTable = new char[i];
    localDataInputStream.read(arrayOfByte);
    int j = 0;
    int k = 0;
    while (j < i) {
      table_stringTable[(j++)] = ((char)(arrayOfByte[(k++)] << 8 | arrayOfByte[(k++)] & 0xFF));
    }
    if (verbose) {
      dump();
    }
  }
  
  public static void saveBinary(OutputStream paramOutputStream)
    throws IOException
  {
    sanityCheck();
    DataOutputStream localDataOutputStream = new DataOutputStream(paramOutputStream);
    writeShortTable(localDataOutputStream, head);
    writeShortTable(localDataOutputStream, table_scriptIDs);
    writeShortTable(localDataOutputStream, table_scriptFonts);
    writeShortTable(localDataOutputStream, table_elcIDs);
    writeShortTable(localDataOutputStream, table_sequences);
    writeShortTable(localDataOutputStream, table_fontfileNameIDs);
    writeShortTable(localDataOutputStream, table_componentFontNameIDs);
    writeShortTable(localDataOutputStream, table_filenames);
    writeShortTable(localDataOutputStream, table_awtfontpaths);
    writeShortTable(localDataOutputStream, table_exclusions);
    writeShortTable(localDataOutputStream, table_proportionals);
    writeShortTable(localDataOutputStream, table_scriptFontsMotif);
    writeShortTable(localDataOutputStream, table_alphabeticSuffix);
    writeShortTable(localDataOutputStream, table_stringIDs);
    localDataOutputStream.writeChars(new String(table_stringTable));
    paramOutputStream.close();
    if (verbose) {
      dump();
    }
  }
  
  public static void loadProperties(InputStream paramInputStream)
    throws IOException
  {
    stringIDNum = 1;
    stringIDs = new short['Ϩ'];
    stringTable = new StringBuilder(4096);
    if ((verbose) && (logger == null)) {
      logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
    }
    new PropertiesHandler().load(paramInputStream);
    stringIDs = null;
    stringTable = null;
  }
  
  private void initFontConfig()
  {
    initLocale = startupLocale;
    initEncoding = encoding;
    if ((preferLocaleFonts) && (!willReorderForStartupLocale())) {
      preferLocaleFonts = false;
    }
    initELC = getInitELC();
    initAllComponentFonts();
  }
  
  private short getInitELC()
  {
    if (initELC != -1) {
      return initELC;
    }
    HashMap localHashMap = new HashMap();
    for (int i = 0; i < table_elcIDs.length; i++) {
      localHashMap.put(getString(table_elcIDs[i]), Integer.valueOf(i));
    }
    String str1 = initLocale.getLanguage();
    String str2 = initLocale.getCountry();
    String str3;
    if ((localHashMap.containsKey(str3 = initEncoding + "." + str1 + "." + str2)) || (localHashMap.containsKey(str3 = initEncoding + "." + str1)) || (localHashMap.containsKey(str3 = initEncoding))) {
      initELC = ((Integer)localHashMap.get(str3)).shortValue();
    } else {
      initELC = ((Integer)localHashMap.get("NULL.NULL.NULL")).shortValue();
    }
    for (int j = 0; j < table_alphabeticSuffix.length; j += 2) {
      if (initELC == table_alphabeticSuffix[j])
      {
        alphabeticSuffix = getString(table_alphabeticSuffix[(j + 1)]);
        return initELC;
      }
    }
    return initELC;
  }
  
  private void initAllComponentFonts()
  {
    short[] arrayOfShort1 = getFallbackScripts();
    for (int i = 0; i < 5; i++)
    {
      short[] arrayOfShort2 = getCoreScripts(i);
      compCoreNum[i] = arrayOfShort2.length;
      int[][] arrayOfInt = new int[arrayOfShort2.length][];
      for (int j = 0; j < arrayOfShort2.length; j++) {
        arrayOfInt[j] = getExclusionRanges(arrayOfShort2[j]);
      }
      compExclusions[i] = arrayOfInt;
      for (j = 0; j < 4; j++)
      {
        Object localObject = new short[arrayOfShort2.length + arrayOfShort1.length];
        for (int k = 0; k < arrayOfShort2.length; k++)
        {
          localObject[k] = getComponentFontID(arrayOfShort2[k], i, j);
          if ((preferLocaleFonts) && (localeMap != null) && (fontManager.usingAlternateFontforJALocales())) {
            localObject[k] = remapLocaleMap(i, j, arrayOfShort2[k], localObject[k]);
          }
          if (preferPropFonts) {
            localObject[k] = remapProportional(i, localObject[k]);
          }
          coreFontNameIDs.add(Short.valueOf(localObject[k]));
        }
        for (int m = 0; m < arrayOfShort1.length; m++)
        {
          short s = getComponentFontID(arrayOfShort1[m], i, j);
          if ((preferLocaleFonts) && (localeMap != null) && (fontManager.usingAlternateFontforJALocales())) {
            s = remapLocaleMap(i, j, arrayOfShort1[m], s);
          }
          if (preferPropFonts) {
            s = remapProportional(i, s);
          }
          if (!contains((short[])localObject, s, k))
          {
            fallbackFontNameIDs.add(Short.valueOf(s));
            localObject[(k++)] = s;
          }
        }
        if (k < localObject.length)
        {
          short[] arrayOfShort3 = new short[k];
          System.arraycopy(localObject, 0, arrayOfShort3, 0, k);
          localObject = arrayOfShort3;
        }
        compFontNameIDs[i][j] = localObject;
      }
    }
  }
  
  private short remapLocaleMap(int paramInt1, int paramInt2, short paramShort1, short paramShort2)
  {
    String str1 = getString(table_scriptIDs[paramShort1]);
    String str2 = (String)localeMap.get(str1);
    String str4;
    if (str2 == null)
    {
      String str3 = fontNames[paramInt1];
      str4 = styleNames[paramInt2];
      str2 = (String)localeMap.get(str3 + "." + str4 + "." + str1);
    }
    if (str2 == null) {
      return paramShort2;
    }
    for (int i = 0; i < table_componentFontNameIDs.length; i++)
    {
      str4 = getString(table_componentFontNameIDs[i]);
      if (str2.equalsIgnoreCase(str4))
      {
        paramShort2 = (short)i;
        break;
      }
    }
    return paramShort2;
  }
  
  public static boolean hasMonoToPropMap()
  {
    return (table_proportionals != null) && (table_proportionals.length != 0);
  }
  
  private short remapProportional(int paramInt, short paramShort)
  {
    if ((preferPropFonts) && (table_proportionals.length != 0) && (paramInt != 2) && (paramInt != 4)) {
      for (int i = 0; i < table_proportionals.length; i += 2) {
        if (table_proportionals[i] == paramShort) {
          return table_proportionals[(i + 1)];
        }
      }
    }
    return paramShort;
  }
  
  public static boolean isLogicalFontFamilyName(String paramString)
  {
    return isLogicalFontFamilyNameLC(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public static boolean isLogicalFontFamilyNameLC(String paramString)
  {
    for (int i = 0; i < fontNames.length; i++) {
      if (paramString.equals(fontNames[i])) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean isLogicalFontStyleName(String paramString)
  {
    for (int i = 0; i < styleNames.length; i++) {
      if (paramString.equals(styleNames[i])) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isLogicalFontFaceName(String paramString)
  {
    return isLogicalFontFaceNameLC(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public static boolean isLogicalFontFaceNameLC(String paramString)
  {
    int i = paramString.indexOf('.');
    if (i >= 0)
    {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      return (isLogicalFontFamilyName(str1)) && (isLogicalFontStyleName(str2));
    }
    return isLogicalFontFamilyName(paramString);
  }
  
  protected static int getFontIndex(String paramString)
  {
    return getArrayIndex(fontNames, paramString);
  }
  
  protected static int getStyleIndex(String paramString)
  {
    return getArrayIndex(styleNames, paramString);
  }
  
  private static int getArrayIndex(String[] paramArrayOfString, String paramString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramString.equals(paramArrayOfString[i])) {
        return i;
      }
    }
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return 0;
  }
  
  protected static int getStyleIndex(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 0;
    case 1: 
      return 1;
    case 2: 
      return 2;
    case 3: 
      return 3;
    }
    return 0;
  }
  
  protected static String getFontName(int paramInt)
  {
    return fontNames[paramInt];
  }
  
  protected static String getStyleName(int paramInt)
  {
    return styleNames[paramInt];
  }
  
  public static String getLogicalFontFaceName(String paramString, int paramInt)
  {
    assert (isLogicalFontFamilyName(paramString));
    return paramString.toLowerCase(Locale.ENGLISH) + "." + getStyleString(paramInt);
  }
  
  public static String getStyleString(int paramInt)
  {
    return getStyleName(getStyleIndex(paramInt));
  }
  
  public abstract String getFallbackFamilyName(String paramString1, String paramString2);
  
  protected String getCompatibilityFamilyName(String paramString)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    if (paramString.equals("timesroman")) {
      return "serif";
    }
    if (paramString.equals("helvetica")) {
      return "sansserif";
    }
    if (paramString.equals("courier")) {
      return "monospaced";
    }
    return null;
  }
  
  protected String mapFileName(String paramString)
  {
    return paramString;
  }
  
  protected abstract void initReorderMap();
  
  private void shuffle(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    if (paramInt2 >= paramInt1) {
      return;
    }
    String str = paramArrayOfString[paramInt1];
    for (int i = paramInt1; i > paramInt2; i--) {
      paramArrayOfString[i] = paramArrayOfString[(i - 1)];
    }
    paramArrayOfString[paramInt2] = str;
  }
  
  public static boolean willReorderForStartupLocale()
  {
    return getReorderSequence() != null;
  }
  
  private static Object getReorderSequence()
  {
    if (fontConfigreorderMap == null) {
      fontConfig.initReorderMap();
    }
    HashMap localHashMap = fontConfigreorderMap;
    String str1 = startupLocale.getLanguage();
    String str2 = startupLocale.getCountry();
    Object localObject = localHashMap.get(encoding + "." + str1 + "." + str2);
    if (localObject == null) {
      localObject = localHashMap.get(encoding + "." + str1);
    }
    if (localObject == null) {
      localObject = localHashMap.get(encoding);
    }
    return localObject;
  }
  
  private void reorderSequenceForLocale(String[] paramArrayOfString)
  {
    Object localObject = getReorderSequence();
    if ((localObject instanceof String))
    {
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (paramArrayOfString[i].equals(localObject))
        {
          shuffle(paramArrayOfString, i, 0);
          return;
        }
      }
    }
    else if ((localObject instanceof String[]))
    {
      String[] arrayOfString = (String[])localObject;
      for (int j = 0; j < arrayOfString.length; j++) {
        for (int k = 0; k < paramArrayOfString.length; k++) {
          if (paramArrayOfString[k].equals(arrayOfString[j])) {
            shuffle(paramArrayOfString, k, j);
          }
        }
      }
    }
  }
  
  private static Vector splitSequence(String paramString)
  {
    Vector localVector = new Vector();
    int j;
    for (int i = 0; (j = paramString.indexOf(',', i)) >= 0; i = j + 1) {
      localVector.add(paramString.substring(i, j));
    }
    if (paramString.length() > i) {
      localVector.add(paramString.substring(i, paramString.length()));
    }
    return localVector;
  }
  
  protected String[] split(String paramString)
  {
    Vector localVector = splitSequence(paramString);
    return (String[])localVector.toArray(new String[0]);
  }
  
  public FontDescriptor[] getFontDescriptors(String paramString, int paramInt)
  {
    assert (isLogicalFontFamilyName(paramString));
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    int i = getFontIndex(paramString);
    int j = getStyleIndex(paramInt);
    return getFontDescriptors(i, j);
  }
  
  private FontDescriptor[] getFontDescriptors(int paramInt1, int paramInt2)
  {
    FontDescriptor[] arrayOfFontDescriptor = fontDescriptors[paramInt1][paramInt2];
    if (arrayOfFontDescriptor == null)
    {
      arrayOfFontDescriptor = buildFontDescriptors(paramInt1, paramInt2);
      fontDescriptors[paramInt1][paramInt2] = arrayOfFontDescriptor;
    }
    return arrayOfFontDescriptor;
  }
  
  protected FontDescriptor[] buildFontDescriptors(int paramInt1, int paramInt2)
  {
    String str1 = fontNames[paramInt1];
    String str2 = styleNames[paramInt2];
    short[] arrayOfShort1 = getCoreScripts(paramInt1);
    short[] arrayOfShort2 = compFontNameIDs[paramInt1][paramInt2];
    String[] arrayOfString1 = new String[arrayOfShort1.length];
    String[] arrayOfString2 = new String[arrayOfShort1.length];
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      arrayOfString2[i] = getComponentFontName(arrayOfShort2[i]);
      arrayOfString1[i] = getScriptName(arrayOfShort1[i]);
      if ((alphabeticSuffix != null) && ("alphabetic".equals(arrayOfString1[i]))) {
        arrayOfString1[i] = (arrayOfString1[i] + "/" + alphabeticSuffix);
      }
    }
    int[][] arrayOfInt = compExclusions[paramInt1];
    FontDescriptor[] arrayOfFontDescriptor = new FontDescriptor[arrayOfString2.length];
    for (int j = 0; j < arrayOfString2.length; j++)
    {
      String str3 = makeAWTFontName(arrayOfString2[j], arrayOfString1[j]);
      String str4 = getEncoding(arrayOfString2[j], arrayOfString1[j]);
      if (str4 == null) {
        str4 = "default";
      }
      CharsetEncoder localCharsetEncoder = getFontCharsetEncoder(str4.trim(), str3);
      int[] arrayOfInt1 = arrayOfInt[j];
      arrayOfFontDescriptor[j] = new FontDescriptor(str3, localCharsetEncoder, arrayOfInt1);
    }
    return arrayOfFontDescriptor;
  }
  
  protected String makeAWTFontName(String paramString1, String paramString2)
  {
    return paramString1;
  }
  
  protected abstract String getEncoding(String paramString1, String paramString2);
  
  private CharsetEncoder getFontCharsetEncoder(final String paramString1, String paramString2)
  {
    Charset localCharset = null;
    if (paramString1.equals("default")) {
      localCharset = (Charset)charsetRegistry.get(paramString2);
    } else {
      localCharset = (Charset)charsetRegistry.get(paramString1);
    }
    if (localCharset != null) {
      return localCharset.newEncoder();
    }
    if ((!paramString1.startsWith("sun.awt.")) && (!paramString1.equals("default")))
    {
      localCharset = Charset.forName(paramString1);
    }
    else
    {
      Class localClass = (Class)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            return Class.forName(paramString1, true, ClassLoader.getSystemClassLoader());
          }
          catch (ClassNotFoundException localClassNotFoundException) {}
          return null;
        }
      });
      if (localClass != null) {
        try
        {
          localCharset = (Charset)localClass.newInstance();
        }
        catch (Exception localException) {}
      }
    }
    if (localCharset == null) {
      localCharset = getDefaultFontCharset(paramString2);
    }
    if (paramString1.equals("default")) {
      charsetRegistry.put(paramString2, localCharset);
    } else {
      charsetRegistry.put(paramString1, localCharset);
    }
    return localCharset.newEncoder();
  }
  
  protected abstract Charset getDefaultFontCharset(String paramString);
  
  public HashSet<String> getAWTFontPathSet()
  {
    return null;
  }
  
  public CompositeFontDescriptor[] get2DCompositeFontInfo()
  {
    CompositeFontDescriptor[] arrayOfCompositeFontDescriptor = new CompositeFontDescriptor[20];
    String str1 = fontManager.getDefaultFontFile();
    String str2 = fontManager.getDefaultFontFaceName();
    for (int i = 0; i < 5; i++)
    {
      String str3 = publicFontNames[i];
      int[][] arrayOfInt = compExclusions[i];
      int j = 0;
      for (int k = 0; k < arrayOfInt.length; k++) {
        j += arrayOfInt[k].length;
      }
      int[] arrayOfInt1 = new int[j];
      int[] arrayOfInt2 = new int[arrayOfInt.length];
      int m = 0;
      int n = 0;
      int i3;
      for (int i1 = 0; i1 < arrayOfInt.length; i1++)
      {
        int[] arrayOfInt3 = arrayOfInt[i1];
        i3 = 0;
        while (i3 < arrayOfInt3.length)
        {
          int i4 = arrayOfInt3[i3];
          arrayOfInt1[(m++)] = arrayOfInt3[(i3++)];
          arrayOfInt1[(m++)] = arrayOfInt3[(i3++)];
        }
        arrayOfInt2[i1] = m;
      }
      for (i1 = 0; i1 < 4; i1++)
      {
        int i2 = compFontNameIDs[i][i1].length;
        i3 = 0;
        if (installedFallbackFontFiles != null) {
          i2 += installedFallbackFontFiles.length;
        }
        String str4 = str3 + "." + styleNames[i1];
        Object localObject1 = new String[i2];
        Object localObject2 = new String[i2];
        short s1;
        for (String[] arrayOfString1 = 0; arrayOfString1 < compFontNameIDs[i][i1].length; arrayOfString1++)
        {
          s1 = compFontNameIDs[i][i1][arrayOfString1];
          short s2 = getComponentFileID(s1);
          localObject1[arrayOfString1] = getFaceNameFromComponentFontName(getComponentFontName(s1));
          localObject2[arrayOfString1] = mapFileName(getComponentFileName(s2));
          if ((localObject2[arrayOfString1] == null) || (needToSearchForFile(localObject2[arrayOfString1]))) {
            localObject2[arrayOfString1] = getFileNameFromComponentFontName(getComponentFontName(s1));
          }
          if ((i3 == 0) && (str1.equals(localObject2[arrayOfString1]))) {
            i3 = 1;
          }
        }
        int i5;
        String[] arrayOfString2;
        String[] arrayOfString4;
        if (i3 == 0)
        {
          s1 = 0;
          if (installedFallbackFontFiles != null) {
            i5 = installedFallbackFontFiles.length;
          }
          if (arrayOfString1 + i5 == i2)
          {
            arrayOfString2 = new String[i2 + 1];
            System.arraycopy(localObject1, 0, arrayOfString2, 0, arrayOfString1);
            localObject1 = arrayOfString2;
            arrayOfString4 = new String[i2 + 1];
            System.arraycopy(localObject2, 0, arrayOfString4, 0, arrayOfString1);
            localObject2 = arrayOfString4;
          }
          localObject1[arrayOfString1] = str2;
          localObject2[arrayOfString1] = str1;
          arrayOfString1++;
        }
        if (installedFallbackFontFiles != null) {
          for (i5 = 0; i5 < installedFallbackFontFiles.length; i5++)
          {
            localObject1[arrayOfString1] = null;
            localObject2[arrayOfString1] = installedFallbackFontFiles[i5];
            arrayOfString1++;
          }
        }
        if (arrayOfString1 < i2)
        {
          localObject3 = new String[arrayOfString1];
          System.arraycopy(localObject1, 0, localObject3, 0, arrayOfString1);
          localObject1 = localObject3;
          arrayOfString2 = new String[arrayOfString1];
          System.arraycopy(localObject2, 0, arrayOfString2, 0, arrayOfString1);
          localObject2 = arrayOfString2;
        }
        Object localObject3 = arrayOfInt2;
        if (arrayOfString1 != localObject3.length)
        {
          String[] arrayOfString3 = arrayOfInt2.length;
          localObject3 = new int[arrayOfString1];
          System.arraycopy(arrayOfInt2, 0, localObject3, 0, arrayOfString3);
          for (arrayOfString4 = arrayOfString3; arrayOfString4 < arrayOfString1; arrayOfString4++) {
            localObject3[arrayOfString4] = arrayOfInt1.length;
          }
        }
        arrayOfCompositeFontDescriptor[(i * 4 + i1)] = new CompositeFontDescriptor(str4, compCoreNum[i], (String[])localObject1, (String[])localObject2, arrayOfInt1, (int[])localObject3);
      }
    }
    return arrayOfCompositeFontDescriptor;
  }
  
  protected abstract String getFaceNameFromComponentFontName(String paramString);
  
  protected abstract String getFileNameFromComponentFontName(String paramString);
  
  public boolean needToSearchForFile(String paramString)
  {
    if (!FontUtilities.isLinux) {
      return false;
    }
    if (existsMap == null) {
      existsMap = new HashMap();
    }
    Boolean localBoolean = (Boolean)existsMap.get(paramString);
    if (localBoolean == null)
    {
      getNumberCoreFonts();
      if (!coreFontFileNames.contains(paramString))
      {
        localBoolean = Boolean.TRUE;
      }
      else
      {
        localBoolean = Boolean.valueOf(new File(paramString).exists());
        existsMap.put(paramString, localBoolean);
        if ((FontUtilities.debugFonts()) && (localBoolean == Boolean.FALSE)) {
          logger.warning("Couldn't locate font file " + paramString);
        }
      }
    }
    return localBoolean == Boolean.FALSE;
  }
  
  public int getNumberCoreFonts()
  {
    if (numCoreFonts == -1)
    {
      numCoreFonts = coreFontNameIDs.size();
      Short[] arrayOfShort1 = new Short[0];
      Short[] arrayOfShort2 = (Short[])coreFontNameIDs.toArray(arrayOfShort1);
      Short[] arrayOfShort3 = (Short[])fallbackFontNameIDs.toArray(arrayOfShort1);
      int i = 0;
      for (int j = 0; j < arrayOfShort3.length; j++) {
        if (coreFontNameIDs.contains(arrayOfShort3[j])) {
          arrayOfShort3[j] = null;
        } else {
          i++;
        }
      }
      componentFonts = new String[numCoreFonts + i];
      Object localObject = null;
      short s1;
      for (j = 0; j < arrayOfShort2.length; j++)
      {
        k = arrayOfShort2[j].shortValue();
        s1 = getComponentFileID(k);
        componentFonts[j] = getComponentFontName(k);
        String str = getComponentFileName(s1);
        if (str != null) {
          coreFontFileNames.add(str);
        }
        filenamesMap.put(componentFonts[j], mapFileName(str));
      }
      for (int k = 0; k < arrayOfShort3.length; k++) {
        if (arrayOfShort3[k] != null)
        {
          s1 = arrayOfShort3[k].shortValue();
          short s2 = getComponentFileID(s1);
          componentFonts[j] = getComponentFontName(s1);
          filenamesMap.put(componentFonts[j], mapFileName(getComponentFileName(s2)));
          j++;
        }
      }
    }
    return numCoreFonts;
  }
  
  public String[] getPlatformFontNames()
  {
    if (numCoreFonts == -1) {
      getNumberCoreFonts();
    }
    return componentFonts;
  }
  
  public String getFileNameFromPlatformName(String paramString)
  {
    return (String)filenamesMap.get(paramString);
  }
  
  public String getExtraFontPath()
  {
    return getString(head[16]);
  }
  
  public String getVersion()
  {
    return getString(head[17]);
  }
  
  protected static FontConfiguration getFontConfiguration()
  {
    return fontConfig;
  }
  
  protected void setFontConfiguration()
  {
    fontConfig = this;
  }
  
  private static void sanityCheck()
  {
    int i = 0;
    String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty("os.name");
      }
    });
    for (int j = 1; j < table_filenames.length; j++) {
      if (table_filenames[j] == -1) {
        if (str1.contains("Windows"))
        {
          System.err.println("\n Error: <filename." + getString(table_componentFontNameIDs[j]) + "> entry is missing!!!");
          i++;
        }
        else if ((verbose) && (!isEmpty(table_filenames)))
        {
          System.err.println("\n Note: 'filename' entry is undefined for \"" + getString(table_componentFontNameIDs[j]) + "\"");
        }
      }
    }
    for (j = 0; j < table_scriptIDs.length; j++)
    {
      int k = table_scriptFonts[j];
      if (k == 0)
      {
        System.out.println("\n Error: <allfonts." + getString(table_scriptIDs[j]) + "> entry is missing!!!");
        i++;
      }
      else if (k < 0)
      {
        k = (short)-k;
        for (int m = 0; m < 5; m++) {
          for (int n = 0; n < 4; n++)
          {
            int i1 = m * 4 + n;
            int i2 = table_scriptFonts[(k + i1)];
            if (i2 == 0)
            {
              System.err.println("\n Error: <" + getFontName(m) + "." + getStyleName(n) + "." + getString(table_scriptIDs[j]) + "> entry is missing!!!");
              i++;
            }
          }
        }
      }
    }
    if ("SunOS".equals(str1)) {
      for (j = 0; j < table_awtfontpaths.length; j++) {
        if (table_awtfontpaths[j] == 0)
        {
          String str2 = getString(table_scriptIDs[j]);
          if ((!str2.contains("lucida")) && (!str2.contains("dingbats")) && (!str2.contains("symbol")))
          {
            System.err.println("\nError: <awtfontpath." + str2 + "> entry is missing!!!");
            i++;
          }
        }
      }
    }
    if (i != 0)
    {
      System.err.println("!!THERE ARE " + i + " ERROR(S) IN THE FONTCONFIG FILE, PLEASE CHECK ITS CONTENT!!\n");
      System.exit(1);
    }
  }
  
  private static boolean isEmpty(short[] paramArrayOfShort)
  {
    for (int k : paramArrayOfShort) {
      if (k != -1) {
        return false;
      }
    }
    return true;
  }
  
  private static void dump()
  {
    System.out.println("\n----Head Table------------");
    for (int i = 0; i < 20; i++) {
      System.out.println("  " + i + " : " + head[i]);
    }
    System.out.println("\n----scriptIDs-------------");
    printTable(table_scriptIDs, 0);
    System.out.println("\n----scriptFonts----------------");
    int j;
    for (i = 0; i < table_scriptIDs.length; i++)
    {
      j = table_scriptFonts[i];
      if (j >= 0) {
        System.out.println("  allfonts." + getString(table_scriptIDs[i]) + "=" + getString(table_componentFontNameIDs[j]));
      }
    }
    for (i = 0; i < table_scriptIDs.length; i++)
    {
      j = table_scriptFonts[i];
      if (j < 0)
      {
        j = (short)-j;
        for (k = 0; k < 5; k++) {
          for (int m = 0; m < 4; m++)
          {
            int n = k * 4 + m;
            int i1 = table_scriptFonts[(j + n)];
            System.out.println("  " + getFontName(k) + "." + getStyleName(m) + "." + getString(table_scriptIDs[i]) + "=" + getString(table_componentFontNameIDs[i1]));
          }
        }
      }
    }
    System.out.println("\n----elcIDs----------------");
    printTable(table_elcIDs, 0);
    System.out.println("\n----sequences-------------");
    for (i = 0; i < table_elcIDs.length; i++)
    {
      System.out.println("  " + i + "/" + getString(table_elcIDs[i]));
      arrayOfShort = getShortArray(table_sequences[(i * 5 + 0)]);
      for (k = 0; k < arrayOfShort.length; k++) {
        System.out.println("     " + getString(table_scriptIDs[arrayOfShort[k]]));
      }
    }
    System.out.println("\n----fontfileNameIDs-------");
    printTable(table_fontfileNameIDs, 0);
    System.out.println("\n----componentFontNameIDs--");
    printTable(table_componentFontNameIDs, 1);
    System.out.println("\n----filenames-------------");
    for (i = 0; i < table_filenames.length; i++) {
      if (table_filenames[i] == -1) {
        System.out.println("  " + i + " : null");
      } else {
        System.out.println("  " + i + " : " + getString(table_fontfileNameIDs[table_filenames[i]]));
      }
    }
    System.out.println("\n----awtfontpaths---------");
    for (i = 0; i < table_awtfontpaths.length; i++) {
      System.out.println("  " + getString(table_scriptIDs[i]) + " : " + getString(table_awtfontpaths[i]));
    }
    System.out.println("\n----proportionals--------");
    for (i = 0; i < table_proportionals.length; i++) {
      System.out.println("  " + getString(table_componentFontNameIDs[table_proportionals[(i++)]]) + " -> " + getString(table_componentFontNameIDs[table_proportionals[i]]));
    }
    i = 0;
    System.out.println("\n----alphabeticSuffix----");
    while (i < table_alphabeticSuffix.length) {
      System.out.println("    " + getString(table_elcIDs[table_alphabeticSuffix[(i++)]]) + " -> " + getString(table_alphabeticSuffix[(i++)]));
    }
    System.out.println("\n----String Table---------");
    System.out.println("    stringID:    Num =" + table_stringIDs.length);
    System.out.println("    stringTable: Size=" + table_stringTable.length * 2);
    System.out.println("\n----fallbackScriptIDs---");
    short[] arrayOfShort = getShortArray(head[15]);
    for (int k = 0; k < arrayOfShort.length; k++) {
      System.out.println("  " + getString(table_scriptIDs[arrayOfShort[k]]));
    }
    System.out.println("\n----appendedfontpath-----");
    System.out.println("  " + getString(head[16]));
    System.out.println("\n----Version--------------");
    System.out.println("  " + getString(head[17]));
  }
  
  protected static short getComponentFontID(short paramShort, int paramInt1, int paramInt2)
  {
    short s = table_scriptFonts[paramShort];
    if (s >= 0) {
      return s;
    }
    return table_scriptFonts[(-s + paramInt1 * 4 + paramInt2)];
  }
  
  protected static short getComponentFontIDMotif(short paramShort, int paramInt1, int paramInt2)
  {
    if (table_scriptFontsMotif.length == 0) {
      return 0;
    }
    short s = table_scriptFontsMotif[paramShort];
    if (s >= 0) {
      return s;
    }
    return table_scriptFontsMotif[(-s + paramInt1 * 4 + paramInt2)];
  }
  
  private static int[] getExclusionRanges(short paramShort)
  {
    short s = table_exclusions[paramShort];
    if (s == 0) {
      return EMPTY_INT_ARRAY;
    }
    char[] arrayOfChar = getString(s).toCharArray();
    int[] arrayOfInt = new int[arrayOfChar.length / 2];
    int i = 0;
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[j] = ((arrayOfChar[(i++)] << '\020') + (arrayOfChar[(i++)] & 0xFFFF));
    }
    return arrayOfInt;
  }
  
  private static boolean contains(short[] paramArrayOfShort, short paramShort, int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      if (paramArrayOfShort[i] == paramShort) {
        return true;
      }
    }
    return false;
  }
  
  protected static String getComponentFontName(short paramShort)
  {
    if (paramShort < 0) {
      return null;
    }
    return getString(table_componentFontNameIDs[paramShort]);
  }
  
  private static String getComponentFileName(short paramShort)
  {
    if (paramShort < 0) {
      return null;
    }
    return getString(table_fontfileNameIDs[paramShort]);
  }
  
  private static short getComponentFileID(short paramShort)
  {
    return table_filenames[paramShort];
  }
  
  private static String getScriptName(short paramShort)
  {
    return getString(table_scriptIDs[paramShort]);
  }
  
  protected short[] getCoreScripts(int paramInt)
  {
    int i = getInitELC();
    short[] arrayOfShort = getShortArray(table_sequences[(i * 5 + paramInt)]);
    if (preferLocaleFonts)
    {
      if (reorderScripts == null) {
        reorderScripts = new HashMap();
      }
      String[] arrayOfString = new String[arrayOfShort.length];
      for (int j = 0; j < arrayOfString.length; j++)
      {
        arrayOfString[j] = getScriptName(arrayOfShort[j]);
        reorderScripts.put(arrayOfString[j], Short.valueOf(arrayOfShort[j]));
      }
      reorderSequenceForLocale(arrayOfString);
      for (j = 0; j < arrayOfString.length; j++) {
        arrayOfShort[j] = ((Short)reorderScripts.get(arrayOfString[j])).shortValue();
      }
    }
    return arrayOfShort;
  }
  
  private static short[] getFallbackScripts()
  {
    return getShortArray(head[15]);
  }
  
  private static void printTable(short[] paramArrayOfShort, int paramInt)
  {
    for (int i = paramInt; i < paramArrayOfShort.length; i++) {
      System.out.println("  " + i + " : " + getString(paramArrayOfShort[i]));
    }
  }
  
  private static short[] readShortTable(DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return EMPTY_SHORT_ARRAY;
    }
    short[] arrayOfShort = new short[paramInt];
    byte[] arrayOfByte = new byte[paramInt * 2];
    paramDataInputStream.read(arrayOfByte);
    int i = 0;
    int j = 0;
    while (i < paramInt) {
      arrayOfShort[(i++)] = ((short)(arrayOfByte[(j++)] << 8 | arrayOfByte[(j++)] & 0xFF));
    }
    return arrayOfShort;
  }
  
  private static void writeShortTable(DataOutputStream paramDataOutputStream, short[] paramArrayOfShort)
    throws IOException
  {
    for (int k : paramArrayOfShort) {
      paramDataOutputStream.writeShort(k);
    }
  }
  
  private static short[] toList(HashMap<String, Short> paramHashMap)
  {
    short[] arrayOfShort = new short[paramHashMap.size()];
    Arrays.fill(arrayOfShort, (short)-1);
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      arrayOfShort[((Short)localEntry.getValue()).shortValue()] = getStringID((String)localEntry.getKey());
    }
    return arrayOfShort;
  }
  
  protected static String getString(short paramShort)
  {
    if (paramShort == 0) {
      return null;
    }
    if (stringCache[paramShort] == null) {
      stringCache[paramShort] = new String(table_stringTable, table_stringIDs[paramShort], table_stringIDs[(paramShort + 1)] - table_stringIDs[paramShort]);
    }
    return stringCache[paramShort];
  }
  
  private static short[] getShortArray(short paramShort)
  {
    String str = getString(paramShort);
    char[] arrayOfChar = str.toCharArray();
    short[] arrayOfShort = new short[arrayOfChar.length];
    for (int i = 0; i < arrayOfChar.length; i++) {
      arrayOfShort[i] = ((short)(arrayOfChar[i] & 0xFFFF));
    }
    return arrayOfShort;
  }
  
  private static short getStringID(String paramString)
  {
    if (paramString == null) {
      return 0;
    }
    int i = (short)stringTable.length();
    stringTable.append(paramString);
    int j = (short)stringTable.length();
    stringIDs[stringIDNum] = i;
    stringIDs[(stringIDNum + 1)] = j;
    stringIDNum = (short)(stringIDNum + 1);
    if (stringIDNum + 1 >= stringIDs.length)
    {
      short[] arrayOfShort = new short[stringIDNum + 1000];
      System.arraycopy(stringIDs, 0, arrayOfShort, 0, stringIDNum);
      stringIDs = arrayOfShort;
    }
    return (short)(stringIDNum - 1);
  }
  
  private static short getShortArrayID(short[] paramArrayOfShort)
  {
    char[] arrayOfChar = new char[paramArrayOfShort.length];
    for (int i = 0; i < paramArrayOfShort.length; i++) {
      arrayOfChar[i] = ((char)paramArrayOfShort[i]);
    }
    String str = new String(arrayOfChar);
    return getStringID(str);
  }
  
  static class PropertiesHandler
  {
    private HashMap<String, Short> scriptIDs;
    private HashMap<String, Short> elcIDs;
    private HashMap<String, Short> componentFontNameIDs;
    private HashMap<String, Short> fontfileNameIDs;
    private HashMap<String, Integer> logicalFontIDs;
    private HashMap<String, Integer> fontStyleIDs;
    private HashMap<Short, Short> filenames;
    private HashMap<Short, short[]> sequences;
    private HashMap<Short, Short[]> scriptFonts;
    private HashMap<Short, Short> scriptAllfonts;
    private HashMap<Short, int[]> exclusions;
    private HashMap<Short, Short> awtfontpaths;
    private HashMap<Short, Short> proportionals;
    private HashMap<Short, Short> scriptAllfontsMotif;
    private HashMap<Short, Short[]> scriptFontsMotif;
    private HashMap<Short, Short> alphabeticSuffix;
    private short[] fallbackScriptIDs;
    private String version;
    private String appendedfontpath;
    
    PropertiesHandler() {}
    
    public void load(InputStream paramInputStream)
      throws IOException
    {
      initLogicalNameStyle();
      initHashMaps();
      FontProperties localFontProperties = new FontProperties();
      localFontProperties.load(paramInputStream);
      initBinaryTable();
    }
    
    private void initBinaryTable()
    {
      FontConfiguration.access$002(new short[20]);
      FontConfiguration.head[0] = 20;
      FontConfiguration.access$102(FontConfiguration.toList(scriptIDs));
      FontConfiguration.head[1] = ((short)(FontConfiguration.head[0] + FontConfiguration.table_scriptIDs.length));
      int i = FontConfiguration.table_scriptIDs.length + scriptFonts.size() * 20;
      FontConfiguration.access$302(new short[i]);
      Iterator localIterator = scriptAllfonts.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Map.Entry)localIterator.next();
        FontConfiguration.table_scriptFonts[((Short)localObject1.getKey()).intValue()] = ((Short)((Map.Entry)localObject1).getValue()).shortValue();
      }
      int j = FontConfiguration.table_scriptIDs.length;
      Object localObject1 = scriptFonts.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        FontConfiguration.table_scriptFonts[((Short)localObject2.getKey()).intValue()] = ((short)-j);
        Short[] arrayOfShort = (Short[])((Map.Entry)localObject2).getValue();
        for (int n = 0; n < 20; n++) {
          if (arrayOfShort[n] != null) {
            FontConfiguration.table_scriptFonts[(j++)] = arrayOfShort[n].shortValue();
          } else {
            FontConfiguration.table_scriptFonts[(j++)] = 0;
          }
        }
      }
      FontConfiguration.head[2] = ((short)(FontConfiguration.head[1] + FontConfiguration.table_scriptFonts.length));
      FontConfiguration.access$402(FontConfiguration.toList(elcIDs));
      FontConfiguration.head[3] = ((short)(FontConfiguration.head[2] + FontConfiguration.table_elcIDs.length));
      FontConfiguration.access$502(new short[elcIDs.size() * 5]);
      localObject1 = sequences.entrySet().iterator();
      Object localObject4;
      int i1;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        int m = ((Short)((Map.Entry)localObject2).getKey()).intValue();
        localObject4 = (short[])((Map.Entry)localObject2).getValue();
        if (localObject4.length == 1) {
          for (i1 = 0; i1 < 5; i1++) {
            FontConfiguration.table_sequences[(m * 5 + i1)] = localObject4[0];
          }
        } else {
          for (i1 = 0; i1 < 5; i1++) {
            FontConfiguration.table_sequences[(m * 5 + i1)] = localObject4[i1];
          }
        }
      }
      FontConfiguration.head[4] = ((short)(FontConfiguration.head[3] + FontConfiguration.table_sequences.length));
      FontConfiguration.access$602(FontConfiguration.toList(fontfileNameIDs));
      FontConfiguration.head[5] = ((short)(FontConfiguration.head[4] + FontConfiguration.table_fontfileNameIDs.length));
      FontConfiguration.access$702(FontConfiguration.toList(componentFontNameIDs));
      FontConfiguration.head[6] = ((short)(FontConfiguration.head[5] + FontConfiguration.table_componentFontNameIDs.length));
      FontConfiguration.access$802(new short[FontConfiguration.table_componentFontNameIDs.length]);
      Arrays.fill(FontConfiguration.table_filenames, (short)-1);
      localObject1 = filenames.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        FontConfiguration.table_filenames[((Short)localObject2.getKey()).shortValue()] = ((Short)((Map.Entry)localObject2).getValue()).shortValue();
      }
      FontConfiguration.head[7] = ((short)(FontConfiguration.head[6] + FontConfiguration.table_filenames.length));
      FontConfiguration.table_awtfontpaths = new short[FontConfiguration.table_scriptIDs.length];
      localObject1 = awtfontpaths.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        FontConfiguration.table_awtfontpaths[((Short)localObject2.getKey()).shortValue()] = ((Short)((Map.Entry)localObject2).getValue()).shortValue();
      }
      FontConfiguration.head[8] = ((short)(FontConfiguration.head[7] + FontConfiguration.table_awtfontpaths.length));
      FontConfiguration.access$902(new short[scriptIDs.size()]);
      localObject1 = exclusions.entrySet().iterator();
      Object localObject3;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localObject3 = (int[])((Map.Entry)localObject2).getValue();
        localObject4 = new char[localObject3.length * 2];
        i1 = 0;
        for (int i2 = 0; i2 < localObject3.length; i2++)
        {
          localObject4[(i1++)] = ((char)(localObject3[i2] >> 16));
          localObject4[(i1++)] = ((char)(localObject3[i2] & 0xFFFF));
        }
        FontConfiguration.table_exclusions[((Short)localObject2.getKey()).shortValue()] = FontConfiguration.getStringID(new String((char[])localObject4));
      }
      FontConfiguration.head[9] = ((short)(FontConfiguration.head[8] + FontConfiguration.table_exclusions.length));
      FontConfiguration.access$1102(new short[proportionals.size() * 2]);
      int k = 0;
      Object localObject2 = proportionals.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        FontConfiguration.table_proportionals[(k++)] = ((Short)((Map.Entry)localObject3).getKey()).shortValue();
        FontConfiguration.table_proportionals[(k++)] = ((Short)((Map.Entry)localObject3).getValue()).shortValue();
      }
      FontConfiguration.head[10] = ((short)(FontConfiguration.head[9] + FontConfiguration.table_proportionals.length));
      if ((scriptAllfontsMotif.size() != 0) || (scriptFontsMotif.size() != 0))
      {
        i = FontConfiguration.table_scriptIDs.length + scriptFontsMotif.size() * 20;
        FontConfiguration.access$1202(new short[i]);
        localObject2 = scriptAllfontsMotif.entrySet().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Map.Entry)((Iterator)localObject2).next();
          FontConfiguration.table_scriptFontsMotif[((Short)localObject3.getKey()).intValue()] = ((Short)((Map.Entry)localObject3).getValue()).shortValue();
        }
        j = FontConfiguration.table_scriptIDs.length;
        localObject2 = scriptFontsMotif.entrySet().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Map.Entry)((Iterator)localObject2).next();
          FontConfiguration.table_scriptFontsMotif[((Short)localObject3.getKey()).intValue()] = ((short)-j);
          localObject4 = (Short[])((Map.Entry)localObject3).getValue();
          for (i1 = 0; i1 < 20; i1++) {
            if (localObject4[i1] != null) {
              FontConfiguration.table_scriptFontsMotif[(j++)] = localObject4[i1].shortValue();
            } else {
              FontConfiguration.table_scriptFontsMotif[(j++)] = 0;
            }
          }
        }
      }
      else
      {
        FontConfiguration.access$1202(FontConfiguration.EMPTY_SHORT_ARRAY);
      }
      FontConfiguration.head[11] = ((short)(FontConfiguration.head[10] + FontConfiguration.table_scriptFontsMotif.length));
      FontConfiguration.access$1402(new short[alphabeticSuffix.size() * 2]);
      k = 0;
      localObject2 = alphabeticSuffix.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        FontConfiguration.table_alphabeticSuffix[(k++)] = ((Short)((Map.Entry)localObject3).getKey()).shortValue();
        FontConfiguration.table_alphabeticSuffix[(k++)] = ((Short)((Map.Entry)localObject3).getValue()).shortValue();
      }
      FontConfiguration.head[15] = FontConfiguration.getShortArrayID(fallbackScriptIDs);
      FontConfiguration.head[16] = FontConfiguration.getStringID(appendedfontpath);
      FontConfiguration.head[17] = FontConfiguration.getStringID(version);
      FontConfiguration.head[12] = ((short)(FontConfiguration.head[11] + FontConfiguration.table_alphabeticSuffix.length));
      FontConfiguration.access$1602(new short[FontConfiguration.stringIDNum + 1]);
      System.arraycopy(FontConfiguration.stringIDs, 0, FontConfiguration.table_stringIDs, 0, FontConfiguration.stringIDNum + 1);
      FontConfiguration.head[13] = ((short)(FontConfiguration.head[12] + FontConfiguration.stringIDNum + 1));
      FontConfiguration.access$1902(FontConfiguration.stringTable.toString().toCharArray());
      FontConfiguration.head[14] = ((short)(FontConfiguration.head[13] + FontConfiguration.stringTable.length()));
      FontConfiguration.access$2102(new String[FontConfiguration.table_stringIDs.length]);
    }
    
    private void initLogicalNameStyle()
    {
      logicalFontIDs = new HashMap();
      fontStyleIDs = new HashMap();
      logicalFontIDs.put("serif", Integer.valueOf(0));
      logicalFontIDs.put("sansserif", Integer.valueOf(1));
      logicalFontIDs.put("monospaced", Integer.valueOf(2));
      logicalFontIDs.put("dialog", Integer.valueOf(3));
      logicalFontIDs.put("dialoginput", Integer.valueOf(4));
      fontStyleIDs.put("plain", Integer.valueOf(0));
      fontStyleIDs.put("bold", Integer.valueOf(1));
      fontStyleIDs.put("italic", Integer.valueOf(2));
      fontStyleIDs.put("bolditalic", Integer.valueOf(3));
    }
    
    private void initHashMaps()
    {
      scriptIDs = new HashMap();
      elcIDs = new HashMap();
      componentFontNameIDs = new HashMap();
      componentFontNameIDs.put("", Short.valueOf((short)0));
      fontfileNameIDs = new HashMap();
      filenames = new HashMap();
      sequences = new HashMap();
      scriptFonts = new HashMap();
      scriptAllfonts = new HashMap();
      exclusions = new HashMap();
      awtfontpaths = new HashMap();
      proportionals = new HashMap();
      scriptFontsMotif = new HashMap();
      scriptAllfontsMotif = new HashMap();
      alphabeticSuffix = new HashMap();
      fallbackScriptIDs = FontConfiguration.EMPTY_SHORT_ARRAY;
    }
    
    private int[] parseExclusions(String paramString1, String paramString2)
    {
      if (paramString2 == null) {
        return FontConfiguration.EMPTY_INT_ARRAY;
      }
      int i = 1;
      for (int j = 0; (j = paramString2.indexOf(',', j)) != -1; j++) {
        i++;
      }
      int[] arrayOfInt = new int[i * 2];
      j = 0;
      int k = 0;
      int m = 0;
      while (m < i * 2)
      {
        int n = 0;
        int i1 = 0;
        try
        {
          k = paramString2.indexOf('-', j);
          String str1 = paramString2.substring(j, k);
          j = k + 1;
          k = paramString2.indexOf(',', j);
          if (k == -1) {
            k = paramString2.length();
          }
          String str2 = paramString2.substring(j, k);
          j = k + 1;
          int i2 = str1.length();
          int i3 = str2.length();
          if (((i2 != 4) && (i2 != 6)) || ((i3 != 4) && (i3 != 6))) {
            throw new Exception();
          }
          n = Integer.parseInt(str1, 16);
          i1 = Integer.parseInt(str2, 16);
          if (n > i1) {
            throw new Exception();
          }
        }
        catch (Exception localException)
        {
          if ((FontUtilities.debugFonts()) && (FontConfiguration.logger != null)) {
            FontConfiguration.logger.config("Failed parsing " + paramString1 + " property of font configuration.");
          }
          return FontConfiguration.EMPTY_INT_ARRAY;
        }
        arrayOfInt[(m++)] = n;
        arrayOfInt[(m++)] = i1;
      }
      return arrayOfInt;
    }
    
    private Short getID(HashMap<String, Short> paramHashMap, String paramString)
    {
      Short localShort = (Short)paramHashMap.get(paramString);
      if (localShort == null)
      {
        paramHashMap.put(paramString, Short.valueOf((short)paramHashMap.size()));
        return (Short)paramHashMap.get(paramString);
      }
      return localShort;
    }
    
    private void parseProperty(String paramString1, String paramString2)
    {
      if (paramString1.startsWith("filename."))
      {
        paramString1 = paramString1.substring(9);
        if (!"MingLiU_HKSCS".equals(paramString1)) {
          paramString1 = paramString1.replace('_', ' ');
        }
        Short localShort1 = getID(componentFontNameIDs, paramString1);
        Short localShort2 = getID(fontfileNameIDs, paramString2);
        filenames.put(localShort1, localShort2);
      }
      else if (paramString1.startsWith("exclusion."))
      {
        paramString1 = paramString1.substring(10);
        exclusions.put(getID(scriptIDs, paramString1), parseExclusions(paramString1, paramString2));
      }
      else
      {
        int i;
        int j;
        Object localObject;
        Short localShort3;
        if (paramString1.startsWith("sequence."))
        {
          paramString1 = paramString1.substring(9);
          i = 0;
          j = 0;
          String[] arrayOfString = (String[])FontConfiguration.splitSequence(paramString2).toArray(FontConfiguration.EMPTY_STRING_ARRAY);
          localObject = new short[arrayOfString.length];
          for (int m = 0; m < arrayOfString.length; m++)
          {
            if ("alphabetic/default".equals(arrayOfString[m]))
            {
              arrayOfString[m] = "alphabetic";
              i = 1;
            }
            else if ("alphabetic/1252".equals(arrayOfString[m]))
            {
              arrayOfString[m] = "alphabetic";
              j = 1;
            }
            localObject[m] = getID(scriptIDs, arrayOfString[m]).shortValue();
          }
          m = FontConfiguration.getShortArrayID((short[])localObject);
          localShort3 = null;
          int n = paramString1.indexOf('.');
          if (n == -1)
          {
            if ("fallback".equals(paramString1))
            {
              fallbackScriptIDs = ((short[])localObject);
              return;
            }
            if ("allfonts".equals(paramString1)) {
              localShort3 = getID(elcIDs, "NULL.NULL.NULL");
            } else if (FontConfiguration.logger != null) {
              FontConfiguration.logger.config("Error sequence def: <sequence." + paramString1 + ">");
            }
          }
          else
          {
            localShort3 = getID(elcIDs, paramString1.substring(n + 1));
            paramString1 = paramString1.substring(0, n);
          }
          short[] arrayOfShort1 = null;
          if ("allfonts".equals(paramString1))
          {
            arrayOfShort1 = new short[1];
            arrayOfShort1[0] = m;
          }
          else
          {
            arrayOfShort1 = (short[])sequences.get(localShort3);
            if (arrayOfShort1 == null) {
              arrayOfShort1 = new short[5];
            }
            Integer localInteger2 = (Integer)logicalFontIDs.get(paramString1);
            if (localInteger2 == null)
            {
              if (FontConfiguration.logger != null) {
                FontConfiguration.logger.config("Unrecognizable logicfont name " + paramString1);
              }
              return;
            }
            arrayOfShort1[localInteger2.intValue()] = m;
          }
          sequences.put(localShort3, arrayOfShort1);
          if (i != 0) {
            alphabeticSuffix.put(localShort3, Short.valueOf(FontConfiguration.getStringID("default")));
          } else if (j != 0) {
            alphabeticSuffix.put(localShort3, Short.valueOf(FontConfiguration.getStringID("1252")));
          }
        }
        else if (paramString1.startsWith("allfonts."))
        {
          paramString1 = paramString1.substring(9);
          if (paramString1.endsWith(".motif"))
          {
            paramString1 = paramString1.substring(0, paramString1.length() - 6);
            scriptAllfontsMotif.put(getID(scriptIDs, paramString1), getID(componentFontNameIDs, paramString2));
          }
          else
          {
            scriptAllfonts.put(getID(scriptIDs, paramString1), getID(componentFontNameIDs, paramString2));
          }
        }
        else if (paramString1.startsWith("awtfontpath."))
        {
          paramString1 = paramString1.substring(12);
          awtfontpaths.put(getID(scriptIDs, paramString1), Short.valueOf(FontConfiguration.getStringID(paramString2)));
        }
        else if ("version".equals(paramString1))
        {
          version = paramString2;
        }
        else if ("appendedfontpath".equals(paramString1))
        {
          appendedfontpath = paramString2;
        }
        else if (paramString1.startsWith("proportional."))
        {
          paramString1 = paramString1.substring(13).replace('_', ' ');
          proportionals.put(getID(componentFontNameIDs, paramString1), getID(componentFontNameIDs, paramString2));
        }
        else
        {
          int k = 0;
          i = paramString1.indexOf('.');
          if (i == -1)
          {
            if (FontConfiguration.logger != null) {
              FontConfiguration.logger.config("Failed parsing " + paramString1 + " property of font configuration.");
            }
            return;
          }
          j = paramString1.indexOf('.', i + 1);
          if (j == -1)
          {
            if (FontConfiguration.logger != null) {
              FontConfiguration.logger.config("Failed parsing " + paramString1 + " property of font configuration.");
            }
            return;
          }
          if (paramString1.endsWith(".motif"))
          {
            paramString1 = paramString1.substring(0, paramString1.length() - 6);
            k = 1;
          }
          localObject = (Integer)logicalFontIDs.get(paramString1.substring(0, i));
          Integer localInteger1 = (Integer)fontStyleIDs.get(paramString1.substring(i + 1, j));
          localShort3 = getID(scriptIDs, paramString1.substring(j + 1));
          if ((localObject == null) || (localInteger1 == null))
          {
            if (FontConfiguration.logger != null) {
              FontConfiguration.logger.config("unrecognizable logicfont name/style at " + paramString1);
            }
            return;
          }
          Short[] arrayOfShort;
          if (k != 0) {
            arrayOfShort = (Short[])scriptFontsMotif.get(localShort3);
          } else {
            arrayOfShort = (Short[])scriptFonts.get(localShort3);
          }
          if (arrayOfShort == null) {
            arrayOfShort = new Short[20];
          }
          arrayOfShort[(localObject.intValue() * 4 + localInteger1.intValue())] = getID(componentFontNameIDs, paramString2);
          if (k != 0) {
            scriptFontsMotif.put(localShort3, arrayOfShort);
          } else {
            scriptFonts.put(localShort3, arrayOfShort);
          }
        }
      }
    }
    
    class FontProperties
      extends Properties
    {
      FontProperties() {}
      
      public synchronized Object put(Object paramObject1, Object paramObject2)
      {
        FontConfiguration.PropertiesHandler.this.parseProperty((String)paramObject1, (String)paramObject2);
        return null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\FontConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */