package java.awt.datatransfer;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import sun.awt.AppContext;
import sun.awt.datatransfer.DataTransferer;

public final class SystemFlavorMap
  implements FlavorMap, FlavorTable
{
  private static String JavaMIME = "JAVA_DATAFLAVOR:";
  private static final Object FLAVOR_MAP_KEY = new Object();
  private static final String keyValueSeparators = "=: \t\r\n\f";
  private static final String strictKeyValueSeparators = "=:";
  private static final String whiteSpaceChars = " \t\r\n\f";
  private static final String[] UNICODE_TEXT_CLASSES = { "java.io.Reader", "java.lang.String", "java.nio.CharBuffer", "\"[C\"" };
  private static final String[] ENCODED_TEXT_CLASSES = { "java.io.InputStream", "java.nio.ByteBuffer", "\"[B\"" };
  private static final String TEXT_PLAIN_BASE_TYPE = "text/plain";
  private static final String HTML_TEXT_BASE_TYPE = "text/html";
  private final Map<String, LinkedHashSet<DataFlavor>> nativeToFlavor = new HashMap();
  private final Map<DataFlavor, LinkedHashSet<String>> flavorToNative = new HashMap();
  private Map<String, LinkedHashSet<String>> textTypeToNative = new HashMap();
  private boolean isMapInitialized = false;
  private final SoftCache<DataFlavor, String> nativesForFlavorCache = new SoftCache(null);
  private final SoftCache<String, DataFlavor> flavorsForNativeCache = new SoftCache(null);
  private Set<Object> disabledMappingGenerationKeys = new HashSet();
  private static final String[] htmlDocumntTypes = { "all", "selection", "fragment" };
  
  private Map<String, LinkedHashSet<DataFlavor>> getNativeToFlavor()
  {
    if (!isMapInitialized) {
      initSystemFlavorMap();
    }
    return nativeToFlavor;
  }
  
  private synchronized Map<DataFlavor, LinkedHashSet<String>> getFlavorToNative()
  {
    if (!isMapInitialized) {
      initSystemFlavorMap();
    }
    return flavorToNative;
  }
  
  private synchronized Map<String, LinkedHashSet<String>> getTextTypeToNative()
  {
    if (!isMapInitialized)
    {
      initSystemFlavorMap();
      textTypeToNative = Collections.unmodifiableMap(textTypeToNative);
    }
    return textTypeToNative;
  }
  
  public static FlavorMap getDefaultFlavorMap()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject = (FlavorMap)localAppContext.get(FLAVOR_MAP_KEY);
    if (localObject == null)
    {
      localObject = new SystemFlavorMap();
      localAppContext.put(FLAVOR_MAP_KEY, localObject);
    }
    return (FlavorMap)localObject;
  }
  
  private SystemFlavorMap() {}
  
  private void initSystemFlavorMap()
  {
    if (isMapInitialized) {
      return;
    }
    isMapInitialized = true;
    BufferedReader localBufferedReader1 = (BufferedReader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public BufferedReader run()
      {
        String str = System.getProperty("java.home") + File.separator + "lib" + File.separator + "flavormap.properties";
        try
        {
          return new BufferedReader(new InputStreamReader(new File(str).toURI().toURL().openStream(), "ISO-8859-1"));
        }
        catch (MalformedURLException localMalformedURLException)
        {
          System.err.println("MalformedURLException:" + localMalformedURLException + " while loading default flavormap.properties file:" + str);
        }
        catch (IOException localIOException)
        {
          System.err.println("IOException:" + localIOException + " while loading default flavormap.properties file:" + str);
        }
        return null;
      }
    });
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Toolkit.getProperty("AWT.DnD.flavorMapFileURL", null);
      }
    });
    if (localBufferedReader1 != null) {
      try
      {
        parseAndStoreReader(localBufferedReader1);
      }
      catch (IOException localIOException1)
      {
        System.err.println("IOException:" + localIOException1 + " while parsing default flavormap.properties file");
      }
    }
    BufferedReader localBufferedReader2 = null;
    if (str != null) {
      try
      {
        localBufferedReader2 = new BufferedReader(new InputStreamReader(new URL(str).openStream(), "ISO-8859-1"));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        System.err.println("MalformedURLException:" + localMalformedURLException + " while reading AWT.DnD.flavorMapFileURL:" + str);
      }
      catch (IOException localIOException2)
      {
        System.err.println("IOException:" + localIOException2 + " while reading AWT.DnD.flavorMapFileURL:" + str);
      }
      catch (SecurityException localSecurityException) {}
    }
    if (localBufferedReader2 != null) {
      try
      {
        parseAndStoreReader(localBufferedReader2);
      }
      catch (IOException localIOException3)
      {
        System.err.println("IOException:" + localIOException3 + " while parsing AWT.DnD.flavorMapFileURL");
      }
    }
  }
  
  private void parseAndStoreReader(BufferedReader paramBufferedReader)
    throws IOException
  {
    for (;;)
    {
      String str1 = paramBufferedReader.readLine();
      if (str1 == null) {
        return;
      }
      if (str1.length() > 0)
      {
        int i = str1.charAt(0);
        if ((i != 35) && (i != 33))
        {
          int m;
          while (continueLine(str1))
          {
            String str2 = paramBufferedReader.readLine();
            if (str2 == null) {
              str2 = "";
            }
            String str3 = str1.substring(0, str1.length() - 1);
            for (m = 0; (m < str2.length()) && (" \t\r\n\f".indexOf(str2.charAt(m)) != -1); m++) {}
            str2 = str2.substring(m, str2.length());
            str1 = str3 + str2;
          }
          int j = str1.length();
          for (int k = 0; (k < j) && (" \t\r\n\f".indexOf(str1.charAt(k)) != -1); k++) {}
          if (k != j)
          {
            for (m = k; m < j; m++)
            {
              n = str1.charAt(m);
              if (n == 92) {
                m++;
              } else {
                if ("=: \t\r\n\f".indexOf(n) != -1) {
                  break;
                }
              }
            }
            for (int n = m; (n < j) && (" \t\r\n\f".indexOf(str1.charAt(n)) != -1); n++) {}
            if ((n < j) && ("=:".indexOf(str1.charAt(n)) != -1)) {
              n++;
            }
            while ((n < j) && (" \t\r\n\f".indexOf(str1.charAt(n)) != -1)) {
              n++;
            }
            String str4 = str1.substring(k, m);
            String str5 = m < j ? str1.substring(n, j) : "";
            str4 = loadConvert(str4);
            str5 = loadConvert(str5);
            try
            {
              MimeType localMimeType = new MimeType(str5);
              if ("text".equals(localMimeType.getPrimaryType()))
              {
                String str6 = localMimeType.getParameter("charset");
                if (DataTransferer.doesSubtypeSupportCharset(localMimeType.getSubType(), str6))
                {
                  DataTransferer localDataTransferer = DataTransferer.getInstance();
                  if (localDataTransferer != null) {
                    localDataTransferer.registerTextFlavorProperties(str4, str6, localMimeType.getParameter("eoln"), localMimeType.getParameter("terminators"));
                  }
                }
                localMimeType.removeParameter("charset");
                localMimeType.removeParameter("class");
                localMimeType.removeParameter("eoln");
                localMimeType.removeParameter("terminators");
                str5 = localMimeType.toString();
              }
            }
            catch (MimeTypeParseException localMimeTypeParseException)
            {
              localMimeTypeParseException.printStackTrace();
            }
            continue;
            DataFlavor localDataFlavor1;
            try
            {
              localDataFlavor1 = new DataFlavor(str5);
            }
            catch (Exception localException1)
            {
              try
              {
                localDataFlavor1 = new DataFlavor(str5, null);
              }
              catch (Exception localException2)
              {
                localException2.printStackTrace();
              }
            }
            continue;
            LinkedHashSet localLinkedHashSet = new LinkedHashSet();
            localLinkedHashSet.add(localDataFlavor1);
            if ("text".equals(localDataFlavor1.getPrimaryType()))
            {
              localLinkedHashSet.addAll(convertMimeTypeToDataFlavors(str5));
              store(mimeType.getBaseType(), str4, getTextTypeToNative());
            }
            Iterator localIterator = localLinkedHashSet.iterator();
            while (localIterator.hasNext())
            {
              DataFlavor localDataFlavor2 = (DataFlavor)localIterator.next();
              store(localDataFlavor2, str4, getFlavorToNative());
              store(str4, localDataFlavor2, getNativeToFlavor());
            }
          }
        }
      }
    }
  }
  
  private boolean continueLine(String paramString)
  {
    int i = 0;
    int j = paramString.length() - 1;
    while ((j >= 0) && (paramString.charAt(j--) == '\\')) {
      i++;
    }
    return i % 2 == 1;
  }
  
  private String loadConvert(String paramString)
  {
    int j = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(j);
    int k = 0;
    while (k < j)
    {
      int i = paramString.charAt(k++);
      if (i == 92)
      {
        i = paramString.charAt(k++);
        if (i == 117)
        {
          int m = 0;
          for (int n = 0; n < 4; n++)
          {
            i = paramString.charAt(k++);
            switch (i)
            {
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
              m = (m << 4) + i - 48;
              break;
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: 
              m = (m << 4) + 10 + i - 97;
              break;
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: 
              m = (m << 4) + 10 + i - 65;
              break;
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 76: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            default: 
              throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            }
          }
          localStringBuilder.append((char)m);
        }
        else
        {
          if (i == 116) {
            i = 9;
          } else if (i == 114) {
            i = 13;
          } else if (i == 110) {
            i = 10;
          } else if (i == 102) {
            i = 12;
          }
          localStringBuilder.append(i);
        }
      }
      else
      {
        localStringBuilder.append(i);
      }
    }
    return localStringBuilder.toString();
  }
  
  private <H, L> void store(H paramH, L paramL, Map<H, LinkedHashSet<L>> paramMap)
  {
    LinkedHashSet localLinkedHashSet = (LinkedHashSet)paramMap.get(paramH);
    if (localLinkedHashSet == null)
    {
      localLinkedHashSet = new LinkedHashSet(1);
      paramMap.put(paramH, localLinkedHashSet);
    }
    if (!localLinkedHashSet.contains(paramL)) {
      localLinkedHashSet.add(paramL);
    }
  }
  
  private LinkedHashSet<DataFlavor> nativeToFlavorLookup(String paramString)
  {
    Object localObject1 = (LinkedHashSet)getNativeToFlavor().get(paramString);
    Object localObject2;
    Object localObject3;
    if ((paramString != null) && (!disabledMappingGenerationKeys.contains(paramString)))
    {
      localObject2 = DataTransferer.getInstance();
      if (localObject2 != null)
      {
        localObject3 = ((DataTransferer)localObject2).getPlatformMappingsForNative(paramString);
        if (!((LinkedHashSet)localObject3).isEmpty())
        {
          if (localObject1 != null) {
            ((LinkedHashSet)localObject3).addAll((Collection)localObject1);
          }
          localObject1 = localObject3;
        }
      }
    }
    if ((localObject1 == null) && (isJavaMIMEType(paramString)))
    {
      localObject2 = decodeJavaMIMEType(paramString);
      localObject3 = null;
      try
      {
        localObject3 = new DataFlavor((String)localObject2);
      }
      catch (Exception localException)
      {
        System.err.println("Exception \"" + localException.getClass().getName() + ": " + localException.getMessage() + "\"while constructing DataFlavor for: " + (String)localObject2);
      }
      if (localObject3 != null)
      {
        localObject1 = new LinkedHashSet(1);
        getNativeToFlavor().put(paramString, localObject1);
        ((LinkedHashSet)localObject1).add(localObject3);
        flavorsForNativeCache.remove(paramString);
        LinkedHashSet localLinkedHashSet = (LinkedHashSet)getFlavorToNative().get(localObject3);
        if (localLinkedHashSet == null)
        {
          localLinkedHashSet = new LinkedHashSet(1);
          getFlavorToNative().put(localObject3, localLinkedHashSet);
        }
        localLinkedHashSet.add(paramString);
        nativesForFlavorCache.remove(localObject3);
      }
    }
    return (LinkedHashSet<DataFlavor>)(localObject1 != null ? localObject1 : new LinkedHashSet(0));
  }
  
  private LinkedHashSet<String> flavorToNativeLookup(DataFlavor paramDataFlavor, boolean paramBoolean)
  {
    Object localObject1 = (LinkedHashSet)getFlavorToNative().get(paramDataFlavor);
    Object localObject2;
    LinkedHashSet localLinkedHashSet;
    if ((paramDataFlavor != null) && (!disabledMappingGenerationKeys.contains(paramDataFlavor)))
    {
      localObject2 = DataTransferer.getInstance();
      if (localObject2 != null)
      {
        localLinkedHashSet = ((DataTransferer)localObject2).getPlatformMappingsForFlavor(paramDataFlavor);
        if (!localLinkedHashSet.isEmpty())
        {
          if (localObject1 != null) {
            localLinkedHashSet.addAll((Collection)localObject1);
          }
          localObject1 = localLinkedHashSet;
        }
      }
    }
    if (localObject1 == null) {
      if (paramBoolean)
      {
        localObject2 = encodeDataFlavor(paramDataFlavor);
        localObject1 = new LinkedHashSet(1);
        getFlavorToNative().put(paramDataFlavor, localObject1);
        ((LinkedHashSet)localObject1).add(localObject2);
        localLinkedHashSet = (LinkedHashSet)getNativeToFlavor().get(localObject2);
        if (localLinkedHashSet == null)
        {
          localLinkedHashSet = new LinkedHashSet(1);
          getNativeToFlavor().put(localObject2, localLinkedHashSet);
        }
        localLinkedHashSet.add(paramDataFlavor);
        nativesForFlavorCache.remove(paramDataFlavor);
        flavorsForNativeCache.remove(localObject2);
      }
      else
      {
        localObject1 = new LinkedHashSet(0);
      }
    }
    return new LinkedHashSet((Collection)localObject1);
  }
  
  public synchronized List<String> getNativesForFlavor(DataFlavor paramDataFlavor)
  {
    LinkedHashSet localLinkedHashSet1 = nativesForFlavorCache.check(paramDataFlavor);
    if (localLinkedHashSet1 != null) {
      return new ArrayList(localLinkedHashSet1);
    }
    if (paramDataFlavor == null)
    {
      localLinkedHashSet1 = new LinkedHashSet(getNativeToFlavor().keySet());
    }
    else if (disabledMappingGenerationKeys.contains(paramDataFlavor))
    {
      localLinkedHashSet1 = flavorToNativeLookup(paramDataFlavor, false);
    }
    else if (DataTransferer.isFlavorCharsetTextType(paramDataFlavor))
    {
      localLinkedHashSet1 = new LinkedHashSet(0);
      if ("text".equals(paramDataFlavor.getPrimaryType()))
      {
        localLinkedHashSet2 = (LinkedHashSet)getTextTypeToNative().get(mimeType.getBaseType());
        if (localLinkedHashSet2 != null) {
          localLinkedHashSet1.addAll(localLinkedHashSet2);
        }
      }
      LinkedHashSet localLinkedHashSet2 = (LinkedHashSet)getTextTypeToNative().get("text/plain");
      if (localLinkedHashSet2 != null) {
        localLinkedHashSet1.addAll(localLinkedHashSet2);
      }
      if (localLinkedHashSet1.isEmpty()) {
        localLinkedHashSet1 = flavorToNativeLookup(paramDataFlavor, true);
      } else {
        localLinkedHashSet1.addAll(flavorToNativeLookup(paramDataFlavor, false));
      }
    }
    else if (DataTransferer.isFlavorNoncharsetTextType(paramDataFlavor))
    {
      localLinkedHashSet1 = (LinkedHashSet)getTextTypeToNative().get(mimeType.getBaseType());
      if ((localLinkedHashSet1 == null) || (localLinkedHashSet1.isEmpty())) {
        localLinkedHashSet1 = flavorToNativeLookup(paramDataFlavor, true);
      } else {
        localLinkedHashSet1.addAll(flavorToNativeLookup(paramDataFlavor, false));
      }
    }
    else
    {
      localLinkedHashSet1 = flavorToNativeLookup(paramDataFlavor, true);
    }
    nativesForFlavorCache.put(paramDataFlavor, localLinkedHashSet1);
    return new ArrayList(localLinkedHashSet1);
  }
  
  public synchronized List<DataFlavor> getFlavorsForNative(String paramString)
  {
    LinkedHashSet localLinkedHashSet = flavorsForNativeCache.check(paramString);
    if (localLinkedHashSet != null) {
      return new ArrayList(localLinkedHashSet);
    }
    localLinkedHashSet = new LinkedHashSet();
    Object localObject1;
    Object localObject2;
    if (paramString == null)
    {
      localObject1 = getNativesForFlavor(null).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        localLinkedHashSet.addAll(getFlavorsForNative((String)localObject2));
      }
    }
    else
    {
      localObject1 = nativeToFlavorLookup(paramString);
      if (disabledMappingGenerationKeys.contains(paramString)) {
        return new ArrayList((Collection)localObject1);
      }
      localObject2 = nativeToFlavorLookup(paramString);
      Iterator localIterator = ((LinkedHashSet)localObject2).iterator();
      while (localIterator.hasNext())
      {
        DataFlavor localDataFlavor = (DataFlavor)localIterator.next();
        localLinkedHashSet.add(localDataFlavor);
        if ("text".equals(localDataFlavor.getPrimaryType()))
        {
          String str = mimeType.getBaseType();
          localLinkedHashSet.addAll(convertMimeTypeToDataFlavors(str));
        }
      }
    }
    flavorsForNativeCache.put(paramString, localLinkedHashSet);
    return new ArrayList(localLinkedHashSet);
  }
  
  private static Set<DataFlavor> convertMimeTypeToDataFlavors(String paramString)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    String str1 = null;
    try
    {
      MimeType localMimeType = new MimeType(paramString);
      str1 = localMimeType.getSubType();
    }
    catch (MimeTypeParseException localMimeTypeParseException) {}
    if (DataTransferer.doesSubtypeSupportCharset(str1, null))
    {
      if ("text/plain".equals(paramString)) {
        localLinkedHashSet.add(DataFlavor.stringFlavor);
      }
      Object localObject2;
      Object localObject3;
      Object localObject4;
      Object localObject5;
      for (String str3 : UNICODE_TEXT_CLASSES)
      {
        String str5 = paramString + ";charset=Unicode;class=" + str3;
        localObject2 = handleHtmlMimeTypes(paramString, str5);
        localObject3 = ((LinkedHashSet)localObject2).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (String)((Iterator)localObject3).next();
          localObject5 = null;
          try
          {
            localObject5 = new DataFlavor((String)localObject4);
          }
          catch (ClassNotFoundException localClassNotFoundException2) {}
          localLinkedHashSet.add(localObject5);
        }
      }
      ??? = DataTransferer.standardEncodings().iterator();
      while (((Iterator)???).hasNext())
      {
        String str2 = (String)((Iterator)???).next();
        for (localObject2 : ENCODED_TEXT_CLASSES)
        {
          localObject3 = paramString + ";charset=" + str2 + ";class=" + (String)localObject2;
          localObject4 = handleHtmlMimeTypes(paramString, (String)localObject3);
          localObject5 = ((LinkedHashSet)localObject4).iterator();
          while (((Iterator)localObject5).hasNext())
          {
            String str6 = (String)((Iterator)localObject5).next();
            DataFlavor localDataFlavor2 = null;
            try
            {
              localDataFlavor2 = new DataFlavor(str6);
              if (localDataFlavor2.equals(DataFlavor.plainTextFlavor)) {
                localDataFlavor2 = DataFlavor.plainTextFlavor;
              }
            }
            catch (ClassNotFoundException localClassNotFoundException3) {}
            localLinkedHashSet.add(localDataFlavor2);
          }
        }
      }
      if ("text/plain".equals(paramString)) {
        localLinkedHashSet.add(DataFlavor.plainTextFlavor);
      }
    }
    else
    {
      for (String str4 : ENCODED_TEXT_CLASSES)
      {
        DataFlavor localDataFlavor1 = null;
        try
        {
          localDataFlavor1 = new DataFlavor(paramString + ";class=" + str4);
        }
        catch (ClassNotFoundException localClassNotFoundException1) {}
        localLinkedHashSet.add(localDataFlavor1);
      }
    }
    return localLinkedHashSet;
  }
  
  private static LinkedHashSet<String> handleHtmlMimeTypes(String paramString1, String paramString2)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    if ("text/html".equals(paramString1)) {
      for (String str : htmlDocumntTypes) {
        localLinkedHashSet.add(paramString2 + ";document=" + str);
      }
    } else {
      localLinkedHashSet.add(paramString2);
    }
    return localLinkedHashSet;
  }
  
  public synchronized Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] paramArrayOfDataFlavor)
  {
    if (paramArrayOfDataFlavor == null)
    {
      localObject = getFlavorsForNative(null);
      paramArrayOfDataFlavor = new DataFlavor[((List)localObject).size()];
      ((List)localObject).toArray(paramArrayOfDataFlavor);
    }
    Object localObject = new HashMap(paramArrayOfDataFlavor.length, 1.0F);
    for (DataFlavor localDataFlavor : paramArrayOfDataFlavor)
    {
      List localList = getNativesForFlavor(localDataFlavor);
      String str = localList.isEmpty() ? null : (String)localList.get(0);
      ((Map)localObject).put(localDataFlavor, str);
    }
    return (Map<DataFlavor, String>)localObject;
  }
  
  public synchronized Map<String, DataFlavor> getFlavorsForNatives(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null)
    {
      localObject = getNativesForFlavor(null);
      paramArrayOfString = new String[((List)localObject).size()];
      ((List)localObject).toArray(paramArrayOfString);
    }
    Object localObject = new HashMap(paramArrayOfString.length, 1.0F);
    for (String str : paramArrayOfString)
    {
      List localList = getFlavorsForNative(str);
      DataFlavor localDataFlavor = localList.isEmpty() ? null : (DataFlavor)localList.get(0);
      ((Map)localObject).put(str, localDataFlavor);
    }
    return (Map<String, DataFlavor>)localObject;
  }
  
  public synchronized void addUnencodedNativeForFlavor(DataFlavor paramDataFlavor, String paramString)
  {
    Objects.requireNonNull(paramString, "Null native not permitted");
    Objects.requireNonNull(paramDataFlavor, "Null flavor not permitted");
    LinkedHashSet localLinkedHashSet = (LinkedHashSet)getFlavorToNative().get(paramDataFlavor);
    if (localLinkedHashSet == null)
    {
      localLinkedHashSet = new LinkedHashSet(1);
      getFlavorToNative().put(paramDataFlavor, localLinkedHashSet);
    }
    localLinkedHashSet.add(paramString);
    nativesForFlavorCache.remove(paramDataFlavor);
  }
  
  public synchronized void setNativesForFlavor(DataFlavor paramDataFlavor, String[] paramArrayOfString)
  {
    Objects.requireNonNull(paramArrayOfString, "Null natives not permitted");
    Objects.requireNonNull(paramDataFlavor, "Null flavors not permitted");
    getFlavorToNative().remove(paramDataFlavor);
    for (String str : paramArrayOfString) {
      addUnencodedNativeForFlavor(paramDataFlavor, str);
    }
    disabledMappingGenerationKeys.add(paramDataFlavor);
    nativesForFlavorCache.remove(paramDataFlavor);
  }
  
  public synchronized void addFlavorForUnencodedNative(String paramString, DataFlavor paramDataFlavor)
  {
    Objects.requireNonNull(paramString, "Null native not permitted");
    Objects.requireNonNull(paramDataFlavor, "Null flavor not permitted");
    LinkedHashSet localLinkedHashSet = (LinkedHashSet)getNativeToFlavor().get(paramString);
    if (localLinkedHashSet == null)
    {
      localLinkedHashSet = new LinkedHashSet(1);
      getNativeToFlavor().put(paramString, localLinkedHashSet);
    }
    localLinkedHashSet.add(paramDataFlavor);
    flavorsForNativeCache.remove(paramString);
  }
  
  public synchronized void setFlavorsForNative(String paramString, DataFlavor[] paramArrayOfDataFlavor)
  {
    Objects.requireNonNull(paramString, "Null native not permitted");
    Objects.requireNonNull(paramArrayOfDataFlavor, "Null flavors not permitted");
    getNativeToFlavor().remove(paramString);
    for (DataFlavor localDataFlavor : paramArrayOfDataFlavor) {
      addFlavorForUnencodedNative(paramString, localDataFlavor);
    }
    disabledMappingGenerationKeys.add(paramString);
    flavorsForNativeCache.remove(paramString);
  }
  
  public static String encodeJavaMIMEType(String paramString)
  {
    return paramString != null ? JavaMIME + paramString : null;
  }
  
  public static String encodeDataFlavor(DataFlavor paramDataFlavor)
  {
    return paramDataFlavor != null ? encodeJavaMIMEType(paramDataFlavor.getMimeType()) : null;
  }
  
  public static boolean isJavaMIMEType(String paramString)
  {
    return (paramString != null) && (paramString.startsWith(JavaMIME, 0));
  }
  
  public static String decodeJavaMIMEType(String paramString)
  {
    return isJavaMIMEType(paramString) ? paramString.substring(JavaMIME.length(), paramString.length()).trim() : null;
  }
  
  public static DataFlavor decodeDataFlavor(String paramString)
    throws ClassNotFoundException
  {
    String str = decodeJavaMIMEType(paramString);
    return str != null ? new DataFlavor(str) : null;
  }
  
  private static final class SoftCache<K, V>
  {
    Map<K, SoftReference<LinkedHashSet<V>>> cache;
    
    private SoftCache() {}
    
    public void put(K paramK, LinkedHashSet<V> paramLinkedHashSet)
    {
      if (cache == null) {
        cache = new HashMap(1);
      }
      cache.put(paramK, new SoftReference(paramLinkedHashSet));
    }
    
    public void remove(K paramK)
    {
      if (cache == null) {
        return;
      }
      cache.remove(null);
      cache.remove(paramK);
    }
    
    public LinkedHashSet<V> check(K paramK)
    {
      if (cache == null) {
        return null;
      }
      SoftReference localSoftReference = (SoftReference)cache.get(paramK);
      if (localSoftReference != null) {
        return (LinkedHashSet)localSoftReference.get();
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\SystemFlavorMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */