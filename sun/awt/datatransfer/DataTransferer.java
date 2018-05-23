package sun.awt.datatransfer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class DataTransferer
{
  public static final DataFlavor plainTextStringFlavor;
  public static final DataFlavor javaTextEncodingFlavor;
  private static final Map textMIMESubtypeCharsetSupport;
  private static String defaultEncoding;
  private static final Set textNatives = Collections.synchronizedSet(new HashSet());
  private static final Map nativeCharsets = Collections.synchronizedMap(new HashMap());
  private static final Map nativeEOLNs = Collections.synchronizedMap(new HashMap());
  private static final Map nativeTerminators = Collections.synchronizedMap(new HashMap());
  private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
  private static DataTransferer transferer;
  private static final PlatformLogger dtLog = PlatformLogger.getLogger("sun.awt.datatransfer.DataTransfer");
  private static final String[] DEPLOYMENT_CACHE_PROPERTIES = { "deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir", "deployment.javapi.cachedir" };
  private static final ArrayList<File> deploymentCacheDirectoryList = new ArrayList();
  
  public DataTransferer() {}
  
  public static synchronized DataTransferer getInstance()
  {
    return ((ComponentFactory)Toolkit.getDefaultToolkit()).getDataTransferer();
  }
  
  public static String canonicalName(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      return Charset.forName(paramString).name();
    }
    catch (IllegalCharsetNameException localIllegalCharsetNameException)
    {
      return paramString;
    }
    catch (UnsupportedCharsetException localUnsupportedCharsetException) {}
    return paramString;
  }
  
  public static String getTextCharset(DataFlavor paramDataFlavor)
  {
    if (!isFlavorCharsetTextType(paramDataFlavor)) {
      return null;
    }
    String str = paramDataFlavor.getParameter("charset");
    return str != null ? str : getDefaultTextCharset();
  }
  
  public static String getDefaultTextCharset()
  {
    if (defaultEncoding != null) {
      return defaultEncoding;
    }
    return defaultEncoding = Charset.defaultCharset().name();
  }
  
  public static boolean doesSubtypeSupportCharset(DataFlavor paramDataFlavor)
  {
    if ((dtLog.isLoggable(PlatformLogger.Level.FINE)) && (!"text".equals(paramDataFlavor.getPrimaryType()))) {
      dtLog.fine("Assertion (\"text\".equals(flavor.getPrimaryType())) failed");
    }
    String str = paramDataFlavor.getSubType();
    if (str == null) {
      return false;
    }
    Object localObject = textMIMESubtypeCharsetSupport.get(str);
    if (localObject != null) {
      return localObject == Boolean.TRUE;
    }
    boolean bool = paramDataFlavor.getParameter("charset") != null;
    textMIMESubtypeCharsetSupport.put(str, bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  public static boolean doesSubtypeSupportCharset(String paramString1, String paramString2)
  {
    Object localObject = textMIMESubtypeCharsetSupport.get(paramString1);
    if (localObject != null) {
      return localObject == Boolean.TRUE;
    }
    boolean bool = paramString2 != null;
    textMIMESubtypeCharsetSupport.put(paramString1, bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  public static boolean isFlavorCharsetTextType(DataFlavor paramDataFlavor)
  {
    if (DataFlavor.stringFlavor.equals(paramDataFlavor)) {
      return true;
    }
    if ((!"text".equals(paramDataFlavor.getPrimaryType())) || (!doesSubtypeSupportCharset(paramDataFlavor))) {
      return false;
    }
    Class localClass = paramDataFlavor.getRepresentationClass();
    if ((paramDataFlavor.isRepresentationClassReader()) || (String.class.equals(localClass)) || (paramDataFlavor.isRepresentationClassCharBuffer()) || (char[].class.equals(localClass))) {
      return true;
    }
    if ((!paramDataFlavor.isRepresentationClassInputStream()) && (!paramDataFlavor.isRepresentationClassByteBuffer()) && (!byte[].class.equals(localClass))) {
      return false;
    }
    String str = paramDataFlavor.getParameter("charset");
    return str != null ? isEncodingSupported(str) : true;
  }
  
  public static boolean isFlavorNoncharsetTextType(DataFlavor paramDataFlavor)
  {
    if ((!"text".equals(paramDataFlavor.getPrimaryType())) || (doesSubtypeSupportCharset(paramDataFlavor))) {
      return false;
    }
    return (paramDataFlavor.isRepresentationClassInputStream()) || (paramDataFlavor.isRepresentationClassByteBuffer()) || (byte[].class.equals(paramDataFlavor.getRepresentationClass()));
  }
  
  public static boolean isEncodingSupported(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    try
    {
      return Charset.isSupported(paramString);
    }
    catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
    return false;
  }
  
  public static boolean isRemote(Class<?> paramClass)
  {
    return RMI.isRemote(paramClass);
  }
  
  public static Set<String> standardEncodings()
  {
    return StandardEncodingsHolder.standardEncodings;
  }
  
  public static FlavorTable adaptFlavorMap(FlavorMap paramFlavorMap)
  {
    if ((paramFlavorMap instanceof FlavorTable)) {
      return (FlavorTable)paramFlavorMap;
    }
    new FlavorTable()
    {
      public Map getNativesForFlavors(DataFlavor[] paramAnonymousArrayOfDataFlavor)
      {
        return val$map.getNativesForFlavors(paramAnonymousArrayOfDataFlavor);
      }
      
      public Map getFlavorsForNatives(String[] paramAnonymousArrayOfString)
      {
        return val$map.getFlavorsForNatives(paramAnonymousArrayOfString);
      }
      
      public List getNativesForFlavor(DataFlavor paramAnonymousDataFlavor)
      {
        Map localMap = getNativesForFlavors(new DataFlavor[] { paramAnonymousDataFlavor });
        String str = (String)localMap.get(paramAnonymousDataFlavor);
        if (str != null)
        {
          ArrayList localArrayList = new ArrayList(1);
          localArrayList.add(str);
          return localArrayList;
        }
        return Collections.EMPTY_LIST;
      }
      
      public List getFlavorsForNative(String paramAnonymousString)
      {
        Map localMap = getFlavorsForNatives(new String[] { paramAnonymousString });
        DataFlavor localDataFlavor = (DataFlavor)localMap.get(paramAnonymousString);
        if (localDataFlavor != null)
        {
          ArrayList localArrayList = new ArrayList(1);
          localArrayList.add(localDataFlavor);
          return localArrayList;
        }
        return Collections.EMPTY_LIST;
      }
    };
  }
  
  public abstract String getDefaultUnicodeEncoding();
  
  public void registerTextFlavorProperties(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Long localLong = getFormatForNativeAsLong(paramString1);
    textNatives.add(localLong);
    nativeCharsets.put(localLong, (paramString2 != null) && (paramString2.length() != 0) ? paramString2 : getDefaultTextCharset());
    if ((paramString3 != null) && (paramString3.length() != 0) && (!paramString3.equals("\n"))) {
      nativeEOLNs.put(localLong, paramString3);
    }
    if ((paramString4 != null) && (paramString4.length() != 0))
    {
      Integer localInteger = Integer.valueOf(paramString4);
      if (localInteger.intValue() > 0) {
        nativeTerminators.put(localLong, localInteger);
      }
    }
  }
  
  protected boolean isTextFormat(long paramLong)
  {
    return textNatives.contains(Long.valueOf(paramLong));
  }
  
  protected String getCharsetForTextFormat(Long paramLong)
  {
    return (String)nativeCharsets.get(paramLong);
  }
  
  public abstract boolean isLocaleDependentTextFormat(long paramLong);
  
  public abstract boolean isFileFormat(long paramLong);
  
  public abstract boolean isImageFormat(long paramLong);
  
  protected boolean isURIListFormat(long paramLong)
  {
    return false;
  }
  
  public SortedMap<Long, DataFlavor> getFormatsForTransferable(Transferable paramTransferable, FlavorTable paramFlavorTable)
  {
    DataFlavor[] arrayOfDataFlavor = paramTransferable.getTransferDataFlavors();
    if (arrayOfDataFlavor == null) {
      return new TreeMap();
    }
    return getFormatsForFlavors(arrayOfDataFlavor, paramFlavorTable);
  }
  
  public SortedMap getFormatsForFlavor(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable)
  {
    return getFormatsForFlavors(new DataFlavor[] { paramDataFlavor }, paramFlavorTable);
  }
  
  public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable)
  {
    HashMap localHashMap1 = new HashMap(paramArrayOfDataFlavor.length);
    HashMap localHashMap2 = new HashMap(paramArrayOfDataFlavor.length);
    HashMap localHashMap3 = new HashMap(paramArrayOfDataFlavor.length);
    HashMap localHashMap4 = new HashMap(paramArrayOfDataFlavor.length);
    int i = 0;
    for (int j = paramArrayOfDataFlavor.length - 1; j >= 0; j--)
    {
      localObject = paramArrayOfDataFlavor[j];
      if ((localObject != null) && ((((DataFlavor)localObject).isFlavorTextType()) || (((DataFlavor)localObject).isFlavorJavaFileListType()) || (DataFlavor.imageFlavor.equals((DataFlavor)localObject)) || (((DataFlavor)localObject).isRepresentationClassSerializable()) || (((DataFlavor)localObject).isRepresentationClassInputStream()) || (((DataFlavor)localObject).isRepresentationClassRemote())))
      {
        List localList = paramFlavorTable.getNativesForFlavor((DataFlavor)localObject);
        i += localList.size();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Long localLong = getFormatForNativeAsLong((String)localIterator.next());
          Integer localInteger = Integer.valueOf(i--);
          localHashMap1.put(localLong, localObject);
          localHashMap3.put(localLong, localInteger);
          if ((("text".equals(((DataFlavor)localObject).getPrimaryType())) && ("plain".equals(((DataFlavor)localObject).getSubType()))) || (((DataFlavor)localObject).equals(DataFlavor.stringFlavor)))
          {
            localHashMap2.put(localLong, localObject);
            localHashMap4.put(localLong, localInteger);
          }
        }
        i += localList.size();
      }
    }
    localHashMap1.putAll(localHashMap2);
    localHashMap3.putAll(localHashMap4);
    IndexOrderComparator localIndexOrderComparator = new IndexOrderComparator(localHashMap3, false);
    Object localObject = new TreeMap(localIndexOrderComparator);
    ((SortedMap)localObject).putAll(localHashMap1);
    return (SortedMap<Long, DataFlavor>)localObject;
  }
  
  public long[] getFormatsForTransferableAsArray(Transferable paramTransferable, FlavorTable paramFlavorTable)
  {
    return keysToLongArray(getFormatsForTransferable(paramTransferable, paramFlavorTable));
  }
  
  public long[] getFormatsForFlavorAsArray(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable)
  {
    return keysToLongArray(getFormatsForFlavor(paramDataFlavor, paramFlavorTable));
  }
  
  public long[] getFormatsForFlavorsAsArray(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable)
  {
    return keysToLongArray(getFormatsForFlavors(paramArrayOfDataFlavor, paramFlavorTable));
  }
  
  public Map getFlavorsForFormat(long paramLong, FlavorTable paramFlavorTable)
  {
    return getFlavorsForFormats(new long[] { paramLong }, paramFlavorTable);
  }
  
  public Map getFlavorsForFormats(long[] paramArrayOfLong, FlavorTable paramFlavorTable)
  {
    HashMap localHashMap = new HashMap(paramArrayOfLong.length);
    HashSet localHashSet1 = new HashSet(paramArrayOfLong.length);
    HashSet localHashSet2 = new HashSet(paramArrayOfLong.length);
    Object localObject1;
    Object localObject2;
    Object localObject3;
    for (int i = 0; i < paramArrayOfLong.length; i++)
    {
      long l = paramArrayOfLong[i];
      localObject1 = getNativeForFormat(l);
      localObject2 = paramFlavorTable.getFlavorsForNative((String)localObject1);
      localObject3 = ((List)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        DataFlavor localDataFlavor2 = (DataFlavor)((Iterator)localObject3).next();
        if ((localDataFlavor2.isFlavorTextType()) || (localDataFlavor2.isFlavorJavaFileListType()) || (DataFlavor.imageFlavor.equals(localDataFlavor2)) || (localDataFlavor2.isRepresentationClassSerializable()) || (localDataFlavor2.isRepresentationClassInputStream()) || (localDataFlavor2.isRepresentationClassRemote()))
        {
          Long localLong = Long.valueOf(l);
          Object localObject4 = createMapping(localLong, localDataFlavor2);
          localHashMap.put(localDataFlavor2, localLong);
          localHashSet1.add(localObject4);
          localHashSet2.add(localDataFlavor2);
        }
      }
    }
    Iterator localIterator = localHashSet2.iterator();
    while (localIterator.hasNext())
    {
      DataFlavor localDataFlavor1 = (DataFlavor)localIterator.next();
      List localList = paramFlavorTable.getNativesForFlavor(localDataFlavor1);
      localObject1 = localList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = getFormatForNativeAsLong((String)((Iterator)localObject1).next());
        localObject3 = createMapping(localObject2, localDataFlavor1);
        if (localHashSet1.contains(localObject3))
        {
          localHashMap.put(localDataFlavor1, localObject2);
          break;
        }
      }
    }
    return localHashMap;
  }
  
  public Set getFlavorsForFormatsAsSet(long[] paramArrayOfLong, FlavorTable paramFlavorTable)
  {
    HashSet localHashSet = new HashSet(paramArrayOfLong.length);
    for (int i = 0; i < paramArrayOfLong.length; i++)
    {
      String str = getNativeForFormat(paramArrayOfLong[i]);
      List localList = paramFlavorTable.getFlavorsForNative(str);
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        DataFlavor localDataFlavor = (DataFlavor)localIterator.next();
        if ((localDataFlavor.isFlavorTextType()) || (localDataFlavor.isFlavorJavaFileListType()) || (DataFlavor.imageFlavor.equals(localDataFlavor)) || (localDataFlavor.isRepresentationClassSerializable()) || (localDataFlavor.isRepresentationClassInputStream()) || (localDataFlavor.isRepresentationClassRemote())) {
          localHashSet.add(localDataFlavor);
        }
      }
    }
    return localHashSet;
  }
  
  public DataFlavor[] getFlavorsForFormatAsArray(long paramLong, FlavorTable paramFlavorTable)
  {
    return getFlavorsForFormatsAsArray(new long[] { paramLong }, paramFlavorTable);
  }
  
  public DataFlavor[] getFlavorsForFormatsAsArray(long[] paramArrayOfLong, FlavorTable paramFlavorTable)
  {
    return setToSortedDataFlavorArray(getFlavorsForFormatsAsSet(paramArrayOfLong, paramFlavorTable));
  }
  
  private static Object createMapping(Object paramObject1, Object paramObject2)
  {
    return Arrays.asList(new Object[] { paramObject1, paramObject2 });
  }
  
  protected abstract Long getFormatForNativeAsLong(String paramString);
  
  protected abstract String getNativeForFormat(long paramLong);
  
  private String getBestCharsetForTextFormat(Long paramLong, Transferable paramTransferable)
    throws IOException
  {
    String str = null;
    if ((paramTransferable != null) && (isLocaleDependentTextFormat(paramLong.longValue())) && (paramTransferable.isDataFlavorSupported(javaTextEncodingFlavor))) {
      try
      {
        str = new String((byte[])paramTransferable.getTransferData(javaTextEncodingFlavor), "UTF-8");
      }
      catch (UnsupportedFlavorException localUnsupportedFlavorException) {}
    } else {
      str = getCharsetForTextFormat(paramLong);
    }
    if (str == null) {
      str = getDefaultTextCharset();
    }
    return str;
  }
  
  private byte[] translateTransferableString(String paramString, long paramLong)
    throws IOException
  {
    Long localLong = Long.valueOf(paramLong);
    String str1 = getBestCharsetForTextFormat(localLong, null);
    String str2 = (String)nativeEOLNs.get(localLong);
    int j;
    if (str2 != null)
    {
      int i = paramString.length();
      localObject2 = new StringBuffer(i * 2);
      for (j = 0; j < i; j++) {
        if (paramString.startsWith(str2, j))
        {
          ((StringBuffer)localObject2).append(str2);
          j += str2.length() - 1;
        }
        else
        {
          char c = paramString.charAt(j);
          if (c == '\n') {
            ((StringBuffer)localObject2).append(str2);
          } else {
            ((StringBuffer)localObject2).append(c);
          }
        }
      }
      paramString = ((StringBuffer)localObject2).toString();
    }
    Object localObject1 = paramString.getBytes(str1);
    Object localObject2 = (Integer)nativeTerminators.get(localLong);
    if (localObject2 != null)
    {
      j = ((Integer)localObject2).intValue();
      byte[] arrayOfByte = new byte[localObject1.length + j];
      System.arraycopy(localObject1, 0, arrayOfByte, 0, localObject1.length);
      for (int k = localObject1.length; k < arrayOfByte.length; k++) {
        arrayOfByte[k] = 0;
      }
      localObject1 = arrayOfByte;
    }
    return (byte[])localObject1;
  }
  
  private String translateBytesToString(byte[] paramArrayOfByte, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    Long localLong = Long.valueOf(paramLong);
    String str1 = getBestCharsetForTextFormat(localLong, paramTransferable);
    String str2 = (String)nativeEOLNs.get(localLong);
    Integer localInteger = (Integer)nativeTerminators.get(localLong);
    int i;
    if (localInteger != null)
    {
      int j = localInteger.intValue();
      i = 0;
      while (i < paramArrayOfByte.length - j + 1)
      {
        for (int k = i; k < i + j; k++) {
          if (paramArrayOfByte[k] != 0) {
            break label106;
          }
        }
        break;
        label106:
        i += j;
      }
    }
    else
    {
      i = paramArrayOfByte.length;
    }
    String str3 = new String(paramArrayOfByte, 0, i, str1);
    if (str2 != null)
    {
      char[] arrayOfChar1 = str3.toCharArray();
      char[] arrayOfChar2 = str2.toCharArray();
      str3 = null;
      int m = 0;
      int i1 = 0;
      while (i1 < arrayOfChar1.length) {
        if (i1 + arrayOfChar2.length > arrayOfChar1.length)
        {
          arrayOfChar1[(m++)] = arrayOfChar1[(i1++)];
        }
        else
        {
          int n = 1;
          int i2 = 0;
          for (int i3 = i1; i2 < arrayOfChar2.length; i3++)
          {
            if (arrayOfChar2[i2] != arrayOfChar1[i3])
            {
              n = 0;
              break;
            }
            i2++;
          }
          if (n != 0)
          {
            arrayOfChar1[(m++)] = '\n';
            i1 += arrayOfChar2.length;
          }
          else
          {
            arrayOfChar1[(m++)] = arrayOfChar1[(i1++)];
          }
        }
      }
      str3 = new String(arrayOfChar1, 0, m);
    }
    return str3;
  }
  
  public byte[] translateTransferable(Transferable paramTransferable, DataFlavor paramDataFlavor, long paramLong)
    throws IOException
  {
    Object localObject1;
    int i;
    try
    {
      localObject1 = paramTransferable.getTransferData(paramDataFlavor);
      if (localObject1 == null) {
        return null;
      }
      if ((paramDataFlavor.equals(DataFlavor.plainTextFlavor)) && (!(localObject1 instanceof InputStream)))
      {
        localObject1 = paramTransferable.getTransferData(DataFlavor.stringFlavor);
        if (localObject1 == null) {
          return null;
        }
        i = 1;
      }
      else
      {
        i = 0;
      }
    }
    catch (UnsupportedFlavorException localUnsupportedFlavorException)
    {
      throw new IOException(localUnsupportedFlavorException.getMessage());
    }
    if ((i != 0) || ((String.class.equals(paramDataFlavor.getRepresentationClass())) && (isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))))
    {
      localObject2 = removeSuspectedData(paramDataFlavor, paramTransferable, (String)localObject1);
      return translateTransferableString((String)localObject2, paramLong);
    }
    Object localObject4;
    if (paramDataFlavor.isRepresentationClassReader())
    {
      if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
        throw new IOException("cannot transfer non-text data as Reader");
      }
      localObject2 = new StringBuffer();
      Reader localReader = (Reader)localObject1;
      localObject4 = null;
      try
      {
        int k;
        while ((k = localReader.read()) != -1) {
          ((StringBuffer)localObject2).append((char)k);
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject4 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localReader != null) {
          if (localObject4 != null) {
            try
            {
              localReader.close();
            }
            catch (Throwable localThrowable5)
            {
              ((Throwable)localObject4).addSuppressed(localThrowable5);
            }
          } else {
            localReader.close();
          }
        }
      }
      return translateTransferableString(((StringBuffer)localObject2).toString(), paramLong);
    }
    int j;
    if (paramDataFlavor.isRepresentationClassCharBuffer())
    {
      if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
        throw new IOException("cannot transfer non-text data as CharBuffer");
      }
      localObject2 = (CharBuffer)localObject1;
      j = ((CharBuffer)localObject2).remaining();
      localObject4 = new char[j];
      ((CharBuffer)localObject2).get((char[])localObject4, 0, j);
      return translateTransferableString(new String((char[])localObject4), paramLong);
    }
    if (char[].class.equals(paramDataFlavor.getRepresentationClass()))
    {
      if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
        throw new IOException("cannot transfer non-text data as char array");
      }
      return translateTransferableString(new String((char[])localObject1), paramLong);
    }
    Object localObject5;
    if (paramDataFlavor.isRepresentationClassByteBuffer())
    {
      localObject2 = (ByteBuffer)localObject1;
      j = ((ByteBuffer)localObject2).remaining();
      localObject4 = new byte[j];
      ((ByteBuffer)localObject2).get((byte[])localObject4, 0, j);
      if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
      {
        localObject5 = getTextCharset(paramDataFlavor);
        return translateTransferableString(new String((byte[])localObject4, (String)localObject5), paramLong);
      }
      return (byte[])localObject4;
    }
    Object localObject3;
    if (byte[].class.equals(paramDataFlavor.getRepresentationClass()))
    {
      localObject2 = (byte[])localObject1;
      if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
      {
        localObject3 = getTextCharset(paramDataFlavor);
        return translateTransferableString(new String((byte[])localObject2, (String)localObject3), paramLong);
      }
      return (byte[])localObject2;
    }
    if (DataFlavor.imageFlavor.equals(paramDataFlavor))
    {
      if (!isImageFormat(paramLong)) {
        throw new IOException("Data translation failed: not an image format");
      }
      localObject2 = (Image)localObject1;
      localObject3 = imageToPlatformBytes((Image)localObject2, paramLong);
      if (localObject3 == null) {
        throw new IOException("Data translation failed: cannot convert java image to native format");
      }
      return (byte[])localObject3;
    }
    Object localObject2 = null;
    Object localObject8;
    Object localObject9;
    if (isFileFormat(paramLong))
    {
      if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
        throw new IOException("data translation failed");
      }
      localObject3 = (List)localObject1;
      localObject4 = getUserProtectionDomain(paramTransferable);
      localObject5 = castToFiles((List)localObject3, (ProtectionDomain)localObject4);
      localObject8 = convertFileListToBytes((ArrayList)localObject5);
      localObject9 = null;
      try
      {
        localObject2 = ((ByteArrayOutputStream)localObject8).toByteArray();
      }
      catch (Throwable localThrowable9)
      {
        localObject9 = localThrowable9;
        throw localThrowable9;
      }
      finally
      {
        if (localObject8 != null) {
          if (localObject9 != null) {
            try
            {
              ((ByteArrayOutputStream)localObject8).close();
            }
            catch (Throwable localThrowable11)
            {
              ((Throwable)localObject9).addSuppressed(localThrowable11);
            }
          } else {
            ((ByteArrayOutputStream)localObject8).close();
          }
        }
      }
    }
    else
    {
      Object localObject6;
      Object localObject11;
      if (isURIListFormat(paramLong))
      {
        if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
          throw new IOException("data translation failed");
        }
        localObject3 = getNativeForFormat(paramLong);
        localObject4 = null;
        if (localObject3 != null) {
          try
          {
            localObject4 = new DataFlavor((String)localObject3).getParameter("charset");
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            throw new IOException(localClassNotFoundException);
          }
        }
        if (localObject4 == null) {
          localObject4 = "UTF-8";
        }
        localObject6 = (List)localObject1;
        localObject8 = getUserProtectionDomain(paramTransferable);
        localObject9 = castToFiles((List)localObject6, (ProtectionDomain)localObject8);
        ArrayList localArrayList = new ArrayList(((ArrayList)localObject9).size());
        localObject11 = ((ArrayList)localObject9).iterator();
        while (((Iterator)localObject11).hasNext())
        {
          localObject12 = (String)((Iterator)localObject11).next();
          localObject13 = new File((String)localObject12).toURI();
          try
          {
            localArrayList.add(new URI(((URI)localObject13).getScheme(), "", ((URI)localObject13).getPath(), ((URI)localObject13).getFragment()).toString());
          }
          catch (URISyntaxException localURISyntaxException)
          {
            throw new IOException(localURISyntaxException);
          }
        }
        localObject11 = "\r\n".getBytes((String)localObject4);
        Object localObject12 = new ByteArrayOutputStream();
        Object localObject13 = null;
        try
        {
          for (int i2 = 0; i2 < localArrayList.size(); i2++)
          {
            byte[] arrayOfByte2 = ((String)localArrayList.get(i2)).getBytes((String)localObject4);
            ((ByteArrayOutputStream)localObject12).write(arrayOfByte2, 0, arrayOfByte2.length);
            ((ByteArrayOutputStream)localObject12).write((byte[])localObject11, 0, localObject11.length);
          }
          localObject2 = ((ByteArrayOutputStream)localObject12).toByteArray();
        }
        catch (Throwable localThrowable13)
        {
          localObject13 = localThrowable13;
          throw localThrowable13;
        }
        finally
        {
          if (localObject12 != null) {
            if (localObject13 != null) {
              try
              {
                ((ByteArrayOutputStream)localObject12).close();
              }
              catch (Throwable localThrowable14)
              {
                ((Throwable)localObject13).addSuppressed(localThrowable14);
              }
            } else {
              ((ByteArrayOutputStream)localObject12).close();
            }
          }
        }
      }
      else if (paramDataFlavor.isRepresentationClassInputStream())
      {
        if (!(localObject1 instanceof InputStream)) {
          return new byte[0];
        }
        localObject3 = new ByteArrayOutputStream();
        localObject4 = null;
        try
        {
          localObject6 = (InputStream)localObject1;
          localObject8 = null;
          try
          {
            int m = 0;
            int n = ((InputStream)localObject6).available();
            localObject11 = new byte[n > 8192 ? n : ' '];
            do
            {
              int i1;
              if ((m = (i1 = ((InputStream)localObject6).read((byte[])localObject11, 0, localObject11.length)) == -1 ? 1 : 0) == 0) {
                ((ByteArrayOutputStream)localObject3).write((byte[])localObject11, 0, i1);
              }
            } while (m == 0);
          }
          catch (Throwable localThrowable7)
          {
            localObject8 = localThrowable7;
            throw localThrowable7;
          }
          finally
          {
            if (localObject6 != null) {
              if (localObject8 != null) {
                try
                {
                  ((InputStream)localObject6).close();
                }
                catch (Throwable localThrowable15)
                {
                  ((Throwable)localObject8).addSuppressed(localThrowable15);
                }
              } else {
                ((InputStream)localObject6).close();
              }
            }
          }
          if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
          {
            localObject6 = ((ByteArrayOutputStream)localObject3).toByteArray();
            localObject8 = getTextCharset(paramDataFlavor);
            byte[] arrayOfByte1 = translateTransferableString(new String((byte[])localObject6, (String)localObject8), paramLong);
            return arrayOfByte1;
          }
          localObject2 = ((ByteArrayOutputStream)localObject3).toByteArray();
        }
        catch (Throwable localThrowable4)
        {
          localObject4 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localObject3 != null) {
            if (localObject4 != null) {
              try
              {
                ((ByteArrayOutputStream)localObject3).close();
              }
              catch (Throwable localThrowable16)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable16);
              }
            } else {
              ((ByteArrayOutputStream)localObject3).close();
            }
          }
        }
      }
      else if (paramDataFlavor.isRepresentationClassRemote())
      {
        localObject3 = RMI.newMarshalledObject(localObject1);
        localObject2 = convertObjectToBytes(localObject3);
      }
      else if (paramDataFlavor.isRepresentationClassSerializable())
      {
        localObject2 = convertObjectToBytes(localObject1);
      }
      else
      {
        throw new IOException("data translation failed");
      }
    }
    return (byte[])localObject2;
  }
  
  /* Error */
  private static byte[] convertObjectToBytes(Object paramObject)
    throws IOException
  {
    // Byte code:
    //   0: new 468	java/io/ByteArrayOutputStream
    //   3: dup
    //   4: invokespecial 967	java/io/ByteArrayOutputStream:<init>	()V
    //   7: astore_1
    //   8: aconst_null
    //   9: astore_2
    //   10: new 474	java/io/ObjectOutputStream
    //   13: dup
    //   14: aload_1
    //   15: invokespecial 992	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   18: astore_3
    //   19: aconst_null
    //   20: astore 4
    //   22: aload_3
    //   23: aload_0
    //   24: invokevirtual 993	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   27: aload_1
    //   28: invokevirtual 970	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   31: astore 5
    //   33: aload_3
    //   34: ifnull +31 -> 65
    //   37: aload 4
    //   39: ifnull +22 -> 61
    //   42: aload_3
    //   43: invokevirtual 991	java/io/ObjectOutputStream:close	()V
    //   46: goto +19 -> 65
    //   49: astore 6
    //   51: aload 4
    //   53: aload 6
    //   55: invokevirtual 1032	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   58: goto +7 -> 65
    //   61: aload_3
    //   62: invokevirtual 991	java/io/ObjectOutputStream:close	()V
    //   65: aload_1
    //   66: ifnull +29 -> 95
    //   69: aload_2
    //   70: ifnull +21 -> 91
    //   73: aload_1
    //   74: invokevirtual 968	java/io/ByteArrayOutputStream:close	()V
    //   77: goto +18 -> 95
    //   80: astore 6
    //   82: aload_2
    //   83: aload 6
    //   85: invokevirtual 1032	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   88: goto +7 -> 95
    //   91: aload_1
    //   92: invokevirtual 968	java/io/ByteArrayOutputStream:close	()V
    //   95: aload 5
    //   97: areturn
    //   98: astore 5
    //   100: aload 5
    //   102: astore 4
    //   104: aload 5
    //   106: athrow
    //   107: astore 7
    //   109: aload_3
    //   110: ifnull +31 -> 141
    //   113: aload 4
    //   115: ifnull +22 -> 137
    //   118: aload_3
    //   119: invokevirtual 991	java/io/ObjectOutputStream:close	()V
    //   122: goto +19 -> 141
    //   125: astore 8
    //   127: aload 4
    //   129: aload 8
    //   131: invokevirtual 1032	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   134: goto +7 -> 141
    //   137: aload_3
    //   138: invokevirtual 991	java/io/ObjectOutputStream:close	()V
    //   141: aload 7
    //   143: athrow
    //   144: astore_3
    //   145: aload_3
    //   146: astore_2
    //   147: aload_3
    //   148: athrow
    //   149: astore 9
    //   151: aload_1
    //   152: ifnull +29 -> 181
    //   155: aload_2
    //   156: ifnull +21 -> 177
    //   159: aload_1
    //   160: invokevirtual 968	java/io/ByteArrayOutputStream:close	()V
    //   163: goto +18 -> 181
    //   166: astore 10
    //   168: aload_2
    //   169: aload 10
    //   171: invokevirtual 1032	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   174: goto +7 -> 181
    //   177: aload_1
    //   178: invokevirtual 968	java/io/ByteArrayOutputStream:close	()V
    //   181: aload 9
    //   183: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	184	0	paramObject	Object
    //   7	171	1	localByteArrayOutputStream	ByteArrayOutputStream
    //   9	160	2	localObject1	Object
    //   18	120	3	localObjectOutputStream	java.io.ObjectOutputStream
    //   144	4	3	localThrowable1	Throwable
    //   20	108	4	localObject2	Object
    //   31	65	5	arrayOfByte	byte[]
    //   98	7	5	localThrowable2	Throwable
    //   49	5	6	localThrowable3	Throwable
    //   80	4	6	localThrowable4	Throwable
    //   107	35	7	localObject3	Object
    //   125	5	8	localThrowable5	Throwable
    //   149	33	9	localObject4	Object
    //   166	4	10	localThrowable6	Throwable
    // Exception table:
    //   from	to	target	type
    //   42	46	49	java/lang/Throwable
    //   73	77	80	java/lang/Throwable
    //   22	33	98	java/lang/Throwable
    //   22	33	107	finally
    //   98	109	107	finally
    //   118	122	125	java/lang/Throwable
    //   10	65	144	java/lang/Throwable
    //   98	144	144	java/lang/Throwable
    //   10	65	149	finally
    //   98	151	149	finally
    //   159	163	166	java/lang/Throwable
  }
  
  protected abstract ByteArrayOutputStream convertFileListToBytes(ArrayList<String> paramArrayList)
    throws IOException;
  
  private String removeSuspectedData(DataFlavor paramDataFlavor, Transferable paramTransferable, final String paramString)
    throws IOException
  {
    if ((null == System.getSecurityManager()) || (!paramDataFlavor.isMimeTypeEqual("text/uri-list"))) {
      return paramString;
    }
    String str = "";
    final ProtectionDomain localProtectionDomain = getUserProtectionDomain(paramTransferable);
    try
    {
      str = (String)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
        {
          StringBuffer localStringBuffer = new StringBuffer(paramString.length());
          String[] arrayOfString1 = paramString.split("(\\s)+");
          for (String str : arrayOfString1)
          {
            File localFile = new File(str);
            if ((localFile.exists()) && (!DataTransferer.isFileInWebstartedCache(localFile)) && (!DataTransferer.this.isForbiddenToRead(localFile, localProtectionDomain)))
            {
              if (0 != localStringBuffer.length()) {
                localStringBuffer.append("\\r\\n");
              }
              localStringBuffer.append(str);
            }
          }
          return localStringBuffer.toString();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new IOException(localPrivilegedActionException.getMessage(), localPrivilegedActionException);
    }
    return str;
  }
  
  private static ProtectionDomain getUserProtectionDomain(Transferable paramTransferable)
  {
    return paramTransferable.getClass().getProtectionDomain();
  }
  
  private boolean isForbiddenToRead(File paramFile, ProtectionDomain paramProtectionDomain)
  {
    if (null == paramProtectionDomain) {
      return false;
    }
    try
    {
      FilePermission localFilePermission = new FilePermission(paramFile.getCanonicalPath(), "read, delete");
      if (paramProtectionDomain.implies(localFilePermission)) {
        return false;
      }
    }
    catch (IOException localIOException) {}
    return true;
  }
  
  private ArrayList<String> castToFiles(final List paramList, final ProtectionDomain paramProtectionDomain)
    throws IOException
  {
    final ArrayList localArrayList = new ArrayList();
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          Iterator localIterator = paramList.iterator();
          while (localIterator.hasNext())
          {
            Object localObject = localIterator.next();
            File localFile = DataTransferer.this.castToFile(localObject);
            if ((localFile != null) && ((null == System.getSecurityManager()) || ((!DataTransferer.isFileInWebstartedCache(localFile)) && (!DataTransferer.this.isForbiddenToRead(localFile, paramProtectionDomain))))) {
              localArrayList.add(localFile.getCanonicalPath());
            }
          }
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new IOException(localPrivilegedActionException.getMessage());
    }
    return localArrayList;
  }
  
  private File castToFile(Object paramObject)
    throws IOException
  {
    String str = null;
    if ((paramObject instanceof File)) {
      str = ((File)paramObject).getCanonicalPath();
    } else if ((paramObject instanceof String)) {
      str = (String)paramObject;
    } else {
      return null;
    }
    return new File(str);
  }
  
  private static boolean isFileInWebstartedCache(File paramFile)
  {
    if (deploymentCacheDirectoryList.isEmpty()) {
      for (String str1 : DEPLOYMENT_CACHE_PROPERTIES)
      {
        String str2 = System.getProperty(str1);
        if (str2 != null) {
          try
          {
            File localFile3 = new File(str2).getCanonicalFile();
            if (localFile3 != null) {
              deploymentCacheDirectoryList.add(localFile3);
            }
          }
          catch (IOException localIOException) {}
        }
      }
    }
    ??? = deploymentCacheDirectoryList.iterator();
    while (((Iterator)???).hasNext())
    {
      File localFile1 = (File)((Iterator)???).next();
      for (File localFile2 = paramFile; localFile2 != null; localFile2 = localFile2.getParentFile()) {
        if (localFile2.equals(localFile1)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Object translateBytes(byte[] paramArrayOfByte, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    Object localObject1 = null;
    Object localObject2;
    Object localObject3;
    if (isFileFormat(paramLong))
    {
      if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
        throw new IOException("data translation failed");
      }
      localObject2 = dragQueryFile(paramArrayOfByte);
      if (localObject2 == null) {
        return null;
      }
      localObject3 = new File[localObject2.length];
      for (int i = 0; i < localObject2.length; i++) {
        localObject3[i] = new File(localObject2[i]);
      }
      localObject1 = Arrays.asList((Object[])localObject3);
    }
    else
    {
      Object localObject4;
      if ((isURIListFormat(paramLong)) && (DataFlavor.javaFileListFlavor.equals(paramDataFlavor)))
      {
        localObject2 = new ByteArrayInputStream(paramArrayOfByte);
        localObject3 = null;
        try
        {
          URI[] arrayOfURI1 = dragQueryURIs((InputStream)localObject2, paramLong, paramTransferable);
          if (arrayOfURI1 == null)
          {
            localObject4 = null;
            return localObject4;
          }
          localObject4 = new ArrayList();
          for (URI localURI : arrayOfURI1) {
            try
            {
              ((List)localObject4).add(new File(localURI));
            }
            catch (IllegalArgumentException localIllegalArgumentException) {}
          }
          localObject1 = localObject4;
        }
        catch (Throwable localThrowable2)
        {
          localObject3 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localObject2 != null) {
            if (localObject3 != null) {
              try
              {
                ((ByteArrayInputStream)localObject2).close();
              }
              catch (Throwable localThrowable14)
              {
                ((Throwable)localObject3).addSuppressed(localThrowable14);
              }
            } else {
              ((ByteArrayInputStream)localObject2).close();
            }
          }
        }
      }
      else if ((String.class.equals(paramDataFlavor.getRepresentationClass())) && (isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
      {
        localObject1 = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable);
      }
      else if (paramDataFlavor.isRepresentationClassReader())
      {
        localObject2 = new ByteArrayInputStream(paramArrayOfByte);
        localObject3 = null;
        try
        {
          localObject1 = translateStream((InputStream)localObject2, paramDataFlavor, paramLong, paramTransferable);
        }
        catch (Throwable localThrowable4)
        {
          localObject3 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localObject2 != null) {
            if (localObject3 != null) {
              try
              {
                ((ByteArrayInputStream)localObject2).close();
              }
              catch (Throwable localThrowable15)
              {
                ((Throwable)localObject3).addSuppressed(localThrowable15);
              }
            } else {
              ((ByteArrayInputStream)localObject2).close();
            }
          }
        }
      }
      else if (paramDataFlavor.isRepresentationClassCharBuffer())
      {
        if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
          throw new IOException("cannot transfer non-text data as CharBuffer");
        }
        localObject2 = CharBuffer.wrap(translateBytesToString(paramArrayOfByte, paramLong, paramTransferable));
        localObject1 = constructFlavoredObject(localObject2, paramDataFlavor, CharBuffer.class);
      }
      else if (char[].class.equals(paramDataFlavor.getRepresentationClass()))
      {
        if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
          throw new IOException("cannot transfer non-text data as char array");
        }
        localObject1 = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).toCharArray();
      }
      else if (paramDataFlavor.isRepresentationClassByteBuffer())
      {
        if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
          paramArrayOfByte = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
        }
        localObject2 = ByteBuffer.wrap(paramArrayOfByte);
        localObject1 = constructFlavoredObject(localObject2, paramDataFlavor, ByteBuffer.class);
      }
      else if (byte[].class.equals(paramDataFlavor.getRepresentationClass()))
      {
        if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
          localObject1 = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
        } else {
          localObject1 = paramArrayOfByte;
        }
      }
      else if (paramDataFlavor.isRepresentationClassInputStream())
      {
        localObject2 = new ByteArrayInputStream(paramArrayOfByte);
        localObject3 = null;
        try
        {
          localObject1 = translateStream((InputStream)localObject2, paramDataFlavor, paramLong, paramTransferable);
        }
        catch (Throwable localThrowable6)
        {
          localObject3 = localThrowable6;
          throw localThrowable6;
        }
        finally
        {
          if (localObject2 != null) {
            if (localObject3 != null) {
              try
              {
                ((ByteArrayInputStream)localObject2).close();
              }
              catch (Throwable localThrowable16)
              {
                ((Throwable)localObject3).addSuppressed(localThrowable16);
              }
            } else {
              ((ByteArrayInputStream)localObject2).close();
            }
          }
        }
      }
      else if (paramDataFlavor.isRepresentationClassRemote())
      {
        try
        {
          localObject2 = new ByteArrayInputStream(paramArrayOfByte);
          localObject3 = null;
          try
          {
            ObjectInputStream localObjectInputStream = new ObjectInputStream((InputStream)localObject2);
            localObject4 = null;
            try
            {
              localObject1 = RMI.getMarshalledObject(localObjectInputStream.readObject());
            }
            catch (Throwable localThrowable13)
            {
              localObject4 = localThrowable13;
              throw localThrowable13;
            }
            finally
            {
              if (localObjectInputStream != null) {
                if (localObject4 != null) {
                  try
                  {
                    localObjectInputStream.close();
                  }
                  catch (Throwable localThrowable17)
                  {
                    ((Throwable)localObject4).addSuppressed(localThrowable17);
                  }
                } else {
                  localObjectInputStream.close();
                }
              }
            }
          }
          catch (Throwable localThrowable8)
          {
            localObject3 = localThrowable8;
            throw localThrowable8;
          }
          finally
          {
            if (localObject2 != null) {
              if (localObject3 != null) {
                try
                {
                  ((ByteArrayInputStream)localObject2).close();
                }
                catch (Throwable localThrowable18)
                {
                  ((Throwable)localObject3).addSuppressed(localThrowable18);
                }
              } else {
                ((ByteArrayInputStream)localObject2).close();
              }
            }
          }
        }
        catch (Exception localException)
        {
          throw new IOException(localException.getMessage());
        }
      }
      else if (paramDataFlavor.isRepresentationClassSerializable())
      {
        ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
        localObject3 = null;
        try
        {
          localObject1 = translateStream(localByteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
        }
        catch (Throwable localThrowable10)
        {
          localObject3 = localThrowable10;
          throw localThrowable10;
        }
        finally
        {
          if (localByteArrayInputStream != null) {
            if (localObject3 != null) {
              try
              {
                localByteArrayInputStream.close();
              }
              catch (Throwable localThrowable19)
              {
                ((Throwable)localObject3).addSuppressed(localThrowable19);
              }
            } else {
              localByteArrayInputStream.close();
            }
          }
        }
      }
      else if (DataFlavor.imageFlavor.equals(paramDataFlavor))
      {
        if (!isImageFormat(paramLong)) {
          throw new IOException("data translation failed");
        }
        localObject1 = platformImageBytesToImage(paramArrayOfByte, paramLong);
      }
    }
    if (localObject1 == null) {
      throw new IOException("data translation failed");
    }
    return localObject1;
  }
  
  public Object translateStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    Object localObject1 = null;
    Object localObject2;
    Object localObject3;
    if ((isURIListFormat(paramLong)) && (DataFlavor.javaFileListFlavor.equals(paramDataFlavor)))
    {
      localObject2 = dragQueryURIs(paramInputStream, paramLong, paramTransferable);
      if (localObject2 == null) {
        return null;
      }
      localObject3 = new ArrayList();
      for (URI localURI : localObject2) {
        try
        {
          ((ArrayList)localObject3).add(new File(localURI));
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
      }
      localObject1 = localObject3;
    }
    else
    {
      if ((String.class.equals(paramDataFlavor.getRepresentationClass())) && (isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
        return translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable);
      }
      if (DataFlavor.plainTextFlavor.equals(paramDataFlavor))
      {
        localObject1 = new StringReader(translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable));
      }
      else if (paramDataFlavor.isRepresentationClassInputStream())
      {
        localObject1 = translateStreamToInputStream(paramInputStream, paramDataFlavor, paramLong, paramTransferable);
      }
      else if (paramDataFlavor.isRepresentationClassReader())
      {
        if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
          throw new IOException("cannot transfer non-text data as Reader");
        }
        localObject2 = (InputStream)translateStreamToInputStream(paramInputStream, DataFlavor.plainTextFlavor, paramLong, paramTransferable);
        localObject3 = getTextCharset(DataFlavor.plainTextFlavor);
        ??? = new InputStreamReader((InputStream)localObject2, (String)localObject3);
        localObject1 = constructFlavoredObject(???, paramDataFlavor, Reader.class);
      }
      else if (byte[].class.equals(paramDataFlavor.getRepresentationClass()))
      {
        if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
          localObject1 = translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
        } else {
          localObject1 = inputStreamToByteArray(paramInputStream);
        }
      }
      else if (paramDataFlavor.isRepresentationClassRemote())
      {
        try
        {
          localObject2 = new ObjectInputStream(paramInputStream);
          localObject3 = null;
          try
          {
            localObject1 = RMI.getMarshalledObject(((ObjectInputStream)localObject2).readObject());
          }
          catch (Throwable localThrowable2)
          {
            localObject3 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localObject2 != null) {
              if (localObject3 != null) {
                try
                {
                  ((ObjectInputStream)localObject2).close();
                }
                catch (Throwable localThrowable5)
                {
                  ((Throwable)localObject3).addSuppressed(localThrowable5);
                }
              } else {
                ((ObjectInputStream)localObject2).close();
              }
            }
          }
        }
        catch (Exception localException1)
        {
          throw new IOException(localException1.getMessage());
        }
      }
      else if (paramDataFlavor.isRepresentationClassSerializable())
      {
        try
        {
          ObjectInputStream localObjectInputStream = new ObjectInputStream(paramInputStream);
          localObject3 = null;
          try
          {
            localObject1 = localObjectInputStream.readObject();
          }
          catch (Throwable localThrowable4)
          {
            localObject3 = localThrowable4;
            throw localThrowable4;
          }
          finally
          {
            if (localObjectInputStream != null) {
              if (localObject3 != null) {
                try
                {
                  localObjectInputStream.close();
                }
                catch (Throwable localThrowable6)
                {
                  ((Throwable)localObject3).addSuppressed(localThrowable6);
                }
              } else {
                localObjectInputStream.close();
              }
            }
          }
        }
        catch (Exception localException2)
        {
          throw new IOException(localException2.getMessage());
        }
      }
      else if (DataFlavor.imageFlavor.equals(paramDataFlavor))
      {
        if (!isImageFormat(paramLong)) {
          throw new IOException("data translation failed");
        }
        localObject1 = platformImageBytesToImage(inputStreamToByteArray(paramInputStream), paramLong);
      }
    }
    if (localObject1 == null) {
      throw new IOException("data translation failed");
    }
    return localObject1;
  }
  
  private Object translateStreamToInputStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
      paramInputStream = new ReencodingInputStream(paramInputStream, paramLong, getTextCharset(paramDataFlavor), paramTransferable);
    }
    return constructFlavoredObject(paramInputStream, paramDataFlavor, InputStream.class);
  }
  
  private Object constructFlavoredObject(Object paramObject, DataFlavor paramDataFlavor, Class paramClass)
    throws IOException
  {
    final Class localClass = paramDataFlavor.getRepresentationClass();
    if (paramClass.equals(localClass)) {
      return paramObject;
    }
    Constructor[] arrayOfConstructor = null;
    try
    {
      arrayOfConstructor = (Constructor[])AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return localClass.getConstructors();
        }
      });
    }
    catch (SecurityException localSecurityException)
    {
      throw new IOException(localSecurityException.getMessage());
    }
    Constructor localConstructor = null;
    for (int i = 0; i < arrayOfConstructor.length; i++) {
      if (Modifier.isPublic(arrayOfConstructor[i].getModifiers()))
      {
        Class[] arrayOfClass = arrayOfConstructor[i].getParameterTypes();
        if ((arrayOfClass != null) && (arrayOfClass.length == 1) && (paramClass.equals(arrayOfClass[0])))
        {
          localConstructor = arrayOfConstructor[i];
          break;
        }
      }
    }
    if (localConstructor == null) {
      throw new IOException("can't find <init>(L" + paramClass + ";)V for class: " + localClass.getName());
    }
    try
    {
      return localConstructor.newInstance(new Object[] { paramObject });
    }
    catch (Exception localException)
    {
      throw new IOException(localException.getMessage());
    }
  }
  
  protected abstract String[] dragQueryFile(byte[] paramArrayOfByte);
  
  protected URI[] dragQueryURIs(InputStream paramInputStream, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    throw new IOException(new UnsupportedOperationException("not implemented on this platform"));
  }
  
  protected abstract Image platformImageBytesToImage(byte[] paramArrayOfByte, long paramLong)
    throws IOException;
  
  protected Image standardImageBytesToImage(byte[] paramArrayOfByte, String paramString)
    throws IOException
  {
    Iterator localIterator = ImageIO.getImageReadersByMIMEType(paramString);
    if (!localIterator.hasNext()) {
      throw new IOException("No registered service provider can decode  an image from " + paramString);
    }
    Object localObject1 = null;
    while (localIterator.hasNext())
    {
      ImageReader localImageReader = (ImageReader)localIterator.next();
      try
      {
        ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
        Object localObject2 = null;
        try
        {
          ImageInputStream localImageInputStream = ImageIO.createImageInputStream(localByteArrayInputStream);
          try
          {
            ImageReadParam localImageReadParam = localImageReader.getDefaultReadParam();
            localImageReader.setInput(localImageInputStream, true, true);
            BufferedImage localBufferedImage1 = localImageReader.read(localImageReader.getMinIndex(), localImageReadParam);
            if (localBufferedImage1 != null)
            {
              BufferedImage localBufferedImage2 = localBufferedImage1;
              localImageInputStream.close();
              localImageReader.dispose();
              return localBufferedImage2;
            }
          }
          finally
          {
            localImageInputStream.close();
            localImageReader.dispose();
          }
        }
        catch (Throwable localThrowable2)
        {
          localObject2 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localByteArrayInputStream != null) {
            if (localObject2 != null) {
              try
              {
                localByteArrayInputStream.close();
              }
              catch (Throwable localThrowable4)
              {
                ((Throwable)localObject2).addSuppressed(localThrowable4);
              }
            } else {
              localByteArrayInputStream.close();
            }
          }
        }
      }
      catch (IOException localIOException)
      {
        localObject1 = localIOException;
      }
    }
    if (localObject1 == null) {
      localObject1 = new IOException("Registered service providers failed to decode an image from " + paramString);
    }
    throw ((Throwable)localObject1);
  }
  
  protected abstract byte[] imageToPlatformBytes(Image paramImage, long paramLong)
    throws IOException;
  
  protected byte[] imageToStandardBytes(Image paramImage, String paramString)
    throws IOException
  {
    Object localObject1 = null;
    Iterator localIterator = ImageIO.getImageWritersByMIMEType(paramString);
    if (!localIterator.hasNext()) {
      throw new IOException("No registered service provider can encode  an image to " + paramString);
    }
    if ((paramImage instanceof RenderedImage)) {
      try
      {
        return imageToStandardBytesImpl((RenderedImage)paramImage, paramString);
      }
      catch (IOException localIOException1)
      {
        localObject1 = localIOException1;
      }
    }
    int i = 0;
    int j = 0;
    if ((paramImage instanceof ToolkitImage))
    {
      localObject2 = ((ToolkitImage)paramImage).getImageRep();
      ((ImageRepresentation)localObject2).reconstruct(32);
      i = ((ImageRepresentation)localObject2).getWidth();
      j = ((ImageRepresentation)localObject2).getHeight();
    }
    else
    {
      i = paramImage.getWidth(null);
      j = paramImage.getHeight(null);
    }
    Object localObject2 = ColorModel.getRGBdefault();
    WritableRaster localWritableRaster = ((ColorModel)localObject2).createCompatibleWritableRaster(i, j);
    BufferedImage localBufferedImage = new BufferedImage((ColorModel)localObject2, localWritableRaster, ((ColorModel)localObject2).isAlphaPremultiplied(), null);
    Graphics localGraphics = localBufferedImage.getGraphics();
    try
    {
      localGraphics.drawImage(paramImage, 0, 0, i, j, null);
    }
    finally
    {
      localGraphics.dispose();
    }
    try
    {
      return imageToStandardBytesImpl(localBufferedImage, paramString);
    }
    catch (IOException localIOException2)
    {
      if (localObject1 != null) {
        throw ((Throwable)localObject1);
      }
      throw localIOException2;
    }
  }
  
  protected byte[] imageToStandardBytesImpl(RenderedImage paramRenderedImage, String paramString)
    throws IOException
  {
    Iterator localIterator = ImageIO.getImageWritersByMIMEType(paramString);
    ImageTypeSpecifier localImageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    Object localObject1 = null;
    while (localIterator.hasNext())
    {
      ImageWriter localImageWriter = (ImageWriter)localIterator.next();
      ImageWriterSpi localImageWriterSpi = localImageWriter.getOriginatingProvider();
      if (localImageWriterSpi.canEncodeImage(localImageTypeSpecifier))
      {
        try
        {
          ImageOutputStream localImageOutputStream = ImageIO.createImageOutputStream(localByteArrayOutputStream);
          try
          {
            localImageWriter.setOutput(localImageOutputStream);
            localImageWriter.write(paramRenderedImage);
            localImageOutputStream.flush();
          }
          finally
          {
            localImageOutputStream.close();
          }
        }
        catch (IOException localIOException)
        {
          localImageWriter.dispose();
          localByteArrayOutputStream.reset();
          localObject1 = localIOException;
        }
        continue;
        localImageWriter.dispose();
        localByteArrayOutputStream.close();
        return localByteArrayOutputStream.toByteArray();
      }
    }
    localByteArrayOutputStream.close();
    if (localObject1 == null) {
      localObject1 = new IOException("Registered service providers failed to encode " + paramRenderedImage + " to " + paramString);
    }
    throw ((Throwable)localObject1);
  }
  
  private Object concatData(Object paramObject1, Object paramObject2)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if ((paramObject1 instanceof byte[]))
    {
      byte[] arrayOfByte1 = (byte[])paramObject1;
      if ((paramObject2 instanceof byte[]))
      {
        byte[] arrayOfByte2 = (byte[])paramObject2;
        byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
        return arrayOfByte3;
      }
      localObject1 = new ByteArrayInputStream(arrayOfByte1);
      localObject2 = (InputStream)paramObject2;
    }
    else
    {
      localObject1 = (InputStream)paramObject1;
      if ((paramObject2 instanceof byte[])) {
        localObject2 = new ByteArrayInputStream((byte[])paramObject2);
      } else {
        localObject2 = (InputStream)paramObject2;
      }
    }
    return new SequenceInputStream((InputStream)localObject1, (InputStream)localObject2);
  }
  
  public byte[] convertData(Object paramObject, Transferable paramTransferable, final long paramLong, final Map paramMap, boolean paramBoolean)
    throws IOException
  {
    byte[] arrayOfByte = null;
    final Object localObject1;
    if (paramBoolean)
    {
      try
      {
        localObject1 = new Stack();
        Runnable local5 = new Runnable()
        {
          private boolean done = false;
          
          /* Error */
          public void run()
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 78	sun/awt/datatransfer/DataTransferer$5:done	Z
            //   4: ifeq +4 -> 8
            //   7: return
            //   8: aconst_null
            //   9: astore_1
            //   10: aload_0
            //   11: getfield 80	sun/awt/datatransfer/DataTransferer$5:val$formatMap	Ljava/util/Map;
            //   14: aload_0
            //   15: getfield 77	sun/awt/datatransfer/DataTransferer$5:val$format	J
            //   18: invokestatic 84	java/lang/Long:valueOf	(J)Ljava/lang/Long;
            //   21: invokeinterface 89 2 0
            //   26: checkcast 40	java/awt/datatransfer/DataFlavor
            //   29: astore_2
            //   30: aload_2
            //   31: ifnull +20 -> 51
            //   34: aload_0
            //   35: getfield 82	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
            //   38: aload_0
            //   39: getfield 79	sun/awt/datatransfer/DataTransferer$5:val$contents	Ljava/awt/datatransfer/Transferable;
            //   42: aload_2
            //   43: aload_0
            //   44: getfield 77	sun/awt/datatransfer/DataTransferer$5:val$format	J
            //   47: invokevirtual 88	sun/awt/datatransfer/DataTransferer:translateTransferable	(Ljava/awt/datatransfer/Transferable;Ljava/awt/datatransfer/DataFlavor;J)[B
            //   50: astore_1
            //   51: goto +10 -> 61
            //   54: astore_2
            //   55: aload_2
            //   56: invokevirtual 83	java/lang/Exception:printStackTrace	()V
            //   59: aconst_null
            //   60: astore_1
            //   61: aload_0
            //   62: getfield 82	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
            //   65: invokevirtual 87	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
            //   68: invokeinterface 91 1 0
            //   73: aload_0
            //   74: getfield 81	sun/awt/datatransfer/DataTransferer$5:val$stack	Ljava/util/Stack;
            //   77: aload_1
            //   78: invokevirtual 86	java/util/Stack:push	(Ljava/lang/Object;)Ljava/lang/Object;
            //   81: pop
            //   82: aload_0
            //   83: getfield 82	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
            //   86: invokevirtual 87	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
            //   89: invokeinterface 90 1 0
            //   94: aload_0
            //   95: getfield 82	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
            //   98: invokevirtual 87	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
            //   101: invokeinterface 92 1 0
            //   106: aload_0
            //   107: iconst_1
            //   108: putfield 78	sun/awt/datatransfer/DataTransferer$5:done	Z
            //   111: goto +23 -> 134
            //   114: astore_3
            //   115: aload_0
            //   116: getfield 82	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
            //   119: invokevirtual 87	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
            //   122: invokeinterface 92 1 0
            //   127: aload_0
            //   128: iconst_1
            //   129: putfield 78	sun/awt/datatransfer/DataTransferer$5:done	Z
            //   132: aload_3
            //   133: athrow
            //   134: return
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	135	0	this	5
            //   9	69	1	arrayOfByte	byte[]
            //   29	14	2	localDataFlavor	DataFlavor
            //   54	2	2	localException	Exception
            //   114	19	3	localObject	Object
            // Exception table:
            //   from	to	target	type
            //   10	51	54	java/lang/Exception
            //   61	94	114	finally
          }
        };
        AppContext localAppContext = SunToolkit.targetToAppContext(paramObject);
        getToolkitThreadBlockedHandler().lock();
        if (localAppContext != null) {
          localAppContext.put("DATA_CONVERTER_KEY", local5);
        }
        SunToolkit.executeOnEventHandlerThread(paramObject, local5);
        while (((Stack)localObject1).empty()) {
          getToolkitThreadBlockedHandler().enter();
        }
        if (localAppContext != null) {
          localAppContext.remove("DATA_CONVERTER_KEY");
        }
        arrayOfByte = (byte[])((Stack)localObject1).pop();
      }
      finally
      {
        getToolkitThreadBlockedHandler().unlock();
      }
    }
    else
    {
      localObject1 = (DataFlavor)paramMap.get(Long.valueOf(paramLong));
      if (localObject1 != null) {
        arrayOfByte = translateTransferable(paramTransferable, (DataFlavor)localObject1, paramLong);
      }
    }
    return arrayOfByte;
  }
  
  /* Error */
  public void processDataConversionRequests()
  {
    // Byte code:
    //   0: invokestatic 938	java/awt/EventQueue:isDispatchThread	()Z
    //   3: ifeq +69 -> 72
    //   6: invokestatic 1093	sun/awt/AppContext:getAppContext	()Lsun/awt/AppContext;
    //   9: astore_1
    //   10: aload_0
    //   11: invokevirtual 1117	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
    //   14: invokeinterface 1188 1 0
    //   19: aload_1
    //   20: ldc_w 421
    //   23: invokevirtual 1094	sun/awt/AppContext:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   26: checkcast 485	java/lang/Runnable
    //   29: astore_2
    //   30: aload_2
    //   31: ifnull +17 -> 48
    //   34: aload_2
    //   35: invokeinterface 1167 1 0
    //   40: aload_1
    //   41: ldc_w 421
    //   44: invokevirtual 1095	sun/awt/AppContext:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   47: pop
    //   48: aload_0
    //   49: invokevirtual 1117	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
    //   52: invokeinterface 1189 1 0
    //   57: goto +15 -> 72
    //   60: astore_3
    //   61: aload_0
    //   62: invokevirtual 1117	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
    //   65: invokeinterface 1189 1 0
    //   70: aload_3
    //   71: athrow
    //   72: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	DataTransferer
    //   9	32	1	localAppContext	AppContext
    //   29	6	2	localRunnable	Runnable
    //   60	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   19	48	60	finally
  }
  
  public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();
  
  public static long[] keysToLongArray(SortedMap paramSortedMap)
  {
    Set localSet = paramSortedMap.keySet();
    long[] arrayOfLong = new long[localSet.size()];
    int i = 0;
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      arrayOfLong[i] = ((Long)localIterator.next()).longValue();
      i++;
    }
    return arrayOfLong;
  }
  
  public static DataFlavor[] setToSortedDataFlavorArray(Set paramSet)
  {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[paramSet.size()];
    paramSet.toArray(arrayOfDataFlavor);
    DataFlavorComparator localDataFlavorComparator = new DataFlavorComparator(false);
    Arrays.sort(arrayOfDataFlavor, localDataFlavorComparator);
    return arrayOfDataFlavor;
  }
  
  protected static byte[] inputStreamToByteArray(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    Object localObject1 = null;
    try
    {
      int i = 0;
      byte[] arrayOfByte1 = new byte[' '];
      while ((i = paramInputStream.read(arrayOfByte1)) != -1) {
        localByteArrayOutputStream.write(arrayOfByte1, 0, i);
      }
      byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
      return arrayOfByte2;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localByteArrayOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localByteArrayOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localByteArrayOutputStream.close();
        }
      }
    }
  }
  
  public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(String paramString)
  {
    return new LinkedHashSet();
  }
  
  public LinkedHashSet<String> getPlatformMappingsForFlavor(DataFlavor paramDataFlavor)
  {
    return new LinkedHashSet();
  }
  
  static
  {
    DataFlavor localDataFlavor1 = null;
    try
    {
      localDataFlavor1 = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
    }
    catch (ClassNotFoundException localClassNotFoundException1) {}
    plainTextStringFlavor = localDataFlavor1;
    DataFlavor localDataFlavor2 = null;
    try
    {
      localDataFlavor2 = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
    }
    catch (ClassNotFoundException localClassNotFoundException2) {}
    javaTextEncodingFlavor = localDataFlavor2;
    HashMap localHashMap = new HashMap(17);
    localHashMap.put("sgml", Boolean.TRUE);
    localHashMap.put("xml", Boolean.TRUE);
    localHashMap.put("html", Boolean.TRUE);
    localHashMap.put("enriched", Boolean.TRUE);
    localHashMap.put("richtext", Boolean.TRUE);
    localHashMap.put("uri-list", Boolean.TRUE);
    localHashMap.put("directory", Boolean.TRUE);
    localHashMap.put("css", Boolean.TRUE);
    localHashMap.put("calendar", Boolean.TRUE);
    localHashMap.put("plain", Boolean.TRUE);
    localHashMap.put("rtf", Boolean.FALSE);
    localHashMap.put("tab-separated-values", Boolean.FALSE);
    localHashMap.put("t140", Boolean.FALSE);
    localHashMap.put("rfc822-headers", Boolean.FALSE);
    localHashMap.put("parityfec", Boolean.FALSE);
    textMIMESubtypeCharsetSupport = Collections.synchronizedMap(localHashMap);
  }
  
  public static class CharsetComparator
    extends DataTransferer.IndexedComparator
  {
    private static final Map charsets;
    private static String defaultEncoding;
    private static final Integer DEFAULT_CHARSET_INDEX = Integer.valueOf(2);
    private static final Integer OTHER_CHARSET_INDEX = Integer.valueOf(1);
    private static final Integer WORST_CHARSET_INDEX = Integer.valueOf(0);
    private static final Integer UNSUPPORTED_CHARSET_INDEX = Integer.valueOf(Integer.MIN_VALUE);
    private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";
    
    public CharsetComparator()
    {
      this(true);
    }
    
    public CharsetComparator(boolean paramBoolean)
    {
      super();
    }
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      String str1 = null;
      String str2 = null;
      if (order == true)
      {
        str1 = (String)paramObject1;
        str2 = (String)paramObject2;
      }
      else
      {
        str1 = (String)paramObject2;
        str2 = (String)paramObject1;
      }
      return compareCharsets(str1, str2);
    }
    
    protected int compareCharsets(String paramString1, String paramString2)
    {
      paramString1 = getEncoding(paramString1);
      paramString2 = getEncoding(paramString2);
      int i = compareIndices(charsets, paramString1, paramString2, OTHER_CHARSET_INDEX);
      if (i == 0) {
        return paramString2.compareTo(paramString1);
      }
      return i;
    }
    
    protected static String getEncoding(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      if (!DataTransferer.isEncodingSupported(paramString)) {
        return "UNSUPPORTED";
      }
      String str = DataTransferer.canonicalName(paramString);
      return charsets.containsKey(str) ? str : paramString;
    }
    
    static
    {
      HashMap localHashMap = new HashMap(8, 1.0F);
      localHashMap.put(DataTransferer.canonicalName("UTF-16LE"), Integer.valueOf(4));
      localHashMap.put(DataTransferer.canonicalName("UTF-16BE"), Integer.valueOf(5));
      localHashMap.put(DataTransferer.canonicalName("UTF-8"), Integer.valueOf(6));
      localHashMap.put(DataTransferer.canonicalName("UTF-16"), Integer.valueOf(7));
      localHashMap.put(DataTransferer.canonicalName("US-ASCII"), WORST_CHARSET_INDEX);
      String str = DataTransferer.canonicalName(DataTransferer.getDefaultTextCharset());
      if (localHashMap.get(defaultEncoding) == null) {
        localHashMap.put(defaultEncoding, DEFAULT_CHARSET_INDEX);
      }
      localHashMap.put("UNSUPPORTED", UNSUPPORTED_CHARSET_INDEX);
      charsets = Collections.unmodifiableMap(localHashMap);
    }
  }
  
  public static class DataFlavorComparator
    extends DataTransferer.IndexedComparator
  {
    private final DataTransferer.CharsetComparator charsetComparator;
    private static final Map exactTypes;
    private static final Map primaryTypes;
    private static final Map nonTextRepresentations;
    private static final Map textTypes;
    private static final Map decodedTextRepresentations;
    private static final Map encodedTextRepresentations;
    private static final Integer UNKNOWN_OBJECT_LOSES = Integer.valueOf(Integer.MIN_VALUE);
    private static final Integer UNKNOWN_OBJECT_WINS = Integer.valueOf(Integer.MAX_VALUE);
    private static final Long UNKNOWN_OBJECT_LOSES_L = Long.valueOf(Long.MIN_VALUE);
    private static final Long UNKNOWN_OBJECT_WINS_L = Long.valueOf(Long.MAX_VALUE);
    
    public DataFlavorComparator()
    {
      this(true);
    }
    
    public DataFlavorComparator(boolean paramBoolean)
    {
      super();
      charsetComparator = new DataTransferer.CharsetComparator(paramBoolean);
    }
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      DataFlavor localDataFlavor1 = null;
      DataFlavor localDataFlavor2 = null;
      if (order == true)
      {
        localDataFlavor1 = (DataFlavor)paramObject1;
        localDataFlavor2 = (DataFlavor)paramObject2;
      }
      else
      {
        localDataFlavor1 = (DataFlavor)paramObject2;
        localDataFlavor2 = (DataFlavor)paramObject1;
      }
      if (localDataFlavor1.equals(localDataFlavor2)) {
        return 0;
      }
      int i = 0;
      String str1 = localDataFlavor1.getPrimaryType();
      String str2 = localDataFlavor1.getSubType();
      String str3 = str1 + "/" + str2;
      Class localClass1 = localDataFlavor1.getRepresentationClass();
      String str4 = localDataFlavor2.getPrimaryType();
      String str5 = localDataFlavor2.getSubType();
      String str6 = str4 + "/" + str5;
      Class localClass2 = localDataFlavor2.getRepresentationClass();
      if ((localDataFlavor1.isFlavorTextType()) && (localDataFlavor2.isFlavorTextType()))
      {
        i = compareIndices(textTypes, str3, str6, UNKNOWN_OBJECT_LOSES);
        if (i != 0) {
          return i;
        }
        if (DataTransferer.doesSubtypeSupportCharset(localDataFlavor1))
        {
          i = compareIndices(decodedTextRepresentations, localClass1, localClass2, UNKNOWN_OBJECT_LOSES);
          if (i != 0) {
            return i;
          }
          i = charsetComparator.compareCharsets(DataTransferer.getTextCharset(localDataFlavor1), DataTransferer.getTextCharset(localDataFlavor2));
          if (i != 0) {
            return i;
          }
        }
        i = compareIndices(encodedTextRepresentations, localClass1, localClass2, UNKNOWN_OBJECT_LOSES);
        if (i != 0) {
          return i;
        }
      }
      else
      {
        if (localDataFlavor1.isFlavorTextType()) {
          return 1;
        }
        if (localDataFlavor2.isFlavorTextType()) {
          return -1;
        }
        i = compareIndices(primaryTypes, str1, str4, UNKNOWN_OBJECT_LOSES);
        if (i != 0) {
          return i;
        }
        i = compareIndices(exactTypes, str3, str6, UNKNOWN_OBJECT_WINS);
        if (i != 0) {
          return i;
        }
        i = compareIndices(nonTextRepresentations, localClass1, localClass2, UNKNOWN_OBJECT_LOSES);
        if (i != 0) {
          return i;
        }
      }
      return localDataFlavor1.getMimeType().compareTo(localDataFlavor2.getMimeType());
    }
    
    static
    {
      HashMap localHashMap = new HashMap(4, 1.0F);
      localHashMap.put("application/x-java-file-list", Integer.valueOf(0));
      localHashMap.put("application/x-java-serialized-object", Integer.valueOf(1));
      localHashMap.put("application/x-java-jvm-local-objectref", Integer.valueOf(2));
      localHashMap.put("application/x-java-remote-object", Integer.valueOf(3));
      exactTypes = Collections.unmodifiableMap(localHashMap);
      localHashMap = new HashMap(1, 1.0F);
      localHashMap.put("application", Integer.valueOf(0));
      primaryTypes = Collections.unmodifiableMap(localHashMap);
      localHashMap = new HashMap(3, 1.0F);
      localHashMap.put(InputStream.class, Integer.valueOf(0));
      localHashMap.put(Serializable.class, Integer.valueOf(1));
      Class localClass = DataTransferer.RMI.remoteClass();
      if (localClass != null) {
        localHashMap.put(localClass, Integer.valueOf(2));
      }
      nonTextRepresentations = Collections.unmodifiableMap(localHashMap);
      localHashMap = new HashMap(16, 1.0F);
      localHashMap.put("text/plain", Integer.valueOf(0));
      localHashMap.put("application/x-java-serialized-object", Integer.valueOf(1));
      localHashMap.put("text/calendar", Integer.valueOf(2));
      localHashMap.put("text/css", Integer.valueOf(3));
      localHashMap.put("text/directory", Integer.valueOf(4));
      localHashMap.put("text/parityfec", Integer.valueOf(5));
      localHashMap.put("text/rfc822-headers", Integer.valueOf(6));
      localHashMap.put("text/t140", Integer.valueOf(7));
      localHashMap.put("text/tab-separated-values", Integer.valueOf(8));
      localHashMap.put("text/uri-list", Integer.valueOf(9));
      localHashMap.put("text/richtext", Integer.valueOf(10));
      localHashMap.put("text/enriched", Integer.valueOf(11));
      localHashMap.put("text/rtf", Integer.valueOf(12));
      localHashMap.put("text/html", Integer.valueOf(13));
      localHashMap.put("text/xml", Integer.valueOf(14));
      localHashMap.put("text/sgml", Integer.valueOf(15));
      textTypes = Collections.unmodifiableMap(localHashMap);
      localHashMap = new HashMap(4, 1.0F);
      localHashMap.put(char[].class, Integer.valueOf(0));
      localHashMap.put(CharBuffer.class, Integer.valueOf(1));
      localHashMap.put(String.class, Integer.valueOf(2));
      localHashMap.put(Reader.class, Integer.valueOf(3));
      decodedTextRepresentations = Collections.unmodifiableMap(localHashMap);
      localHashMap = new HashMap(3, 1.0F);
      localHashMap.put(byte[].class, Integer.valueOf(0));
      localHashMap.put(ByteBuffer.class, Integer.valueOf(1));
      localHashMap.put(InputStream.class, Integer.valueOf(2));
      encodedTextRepresentations = Collections.unmodifiableMap(localHashMap);
    }
  }
  
  public static class IndexOrderComparator
    extends DataTransferer.IndexedComparator
  {
    private final Map indexMap;
    private static final Integer FALLBACK_INDEX = Integer.valueOf(Integer.MIN_VALUE);
    
    public IndexOrderComparator(Map paramMap)
    {
      super();
      indexMap = paramMap;
    }
    
    public IndexOrderComparator(Map paramMap, boolean paramBoolean)
    {
      super();
      indexMap = paramMap;
    }
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      if (!order) {
        return -compareIndices(indexMap, paramObject1, paramObject2, FALLBACK_INDEX);
      }
      return compareIndices(indexMap, paramObject1, paramObject2, FALLBACK_INDEX);
    }
  }
  
  public static abstract class IndexedComparator
    implements Comparator
  {
    public static final boolean SELECT_BEST = true;
    public static final boolean SELECT_WORST = false;
    protected final boolean order;
    
    public IndexedComparator()
    {
      this(true);
    }
    
    public IndexedComparator(boolean paramBoolean)
    {
      order = paramBoolean;
    }
    
    protected static int compareIndices(Map paramMap, Object paramObject1, Object paramObject2, Integer paramInteger)
    {
      Integer localInteger1 = (Integer)paramMap.get(paramObject1);
      Integer localInteger2 = (Integer)paramMap.get(paramObject2);
      if (localInteger1 == null) {
        localInteger1 = paramInteger;
      }
      if (localInteger2 == null) {
        localInteger2 = paramInteger;
      }
      return localInteger1.compareTo(localInteger2);
    }
    
    protected static int compareLongs(Map paramMap, Object paramObject1, Object paramObject2, Long paramLong)
    {
      Long localLong1 = (Long)paramMap.get(paramObject1);
      Long localLong2 = (Long)paramMap.get(paramObject2);
      if (localLong1 == null) {
        localLong1 = paramLong;
      }
      if (localLong2 == null) {
        localLong2 = paramLong;
      }
      return localLong1.compareTo(localLong2);
    }
  }
  
  private static class RMI
  {
    private static final Class<?> remoteClass = getClass("java.rmi.Remote");
    private static final Class<?> marshallObjectClass = getClass("java.rmi.MarshalledObject");
    private static final Constructor<?> marshallCtor = getConstructor(marshallObjectClass, new Class[] { Object.class });
    private static final Method marshallGet = getMethod(marshallObjectClass, "get", new Class[0]);
    
    private RMI() {}
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, null);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return null;
    }
    
    private static Constructor<?> getConstructor(Class<?> paramClass, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass == null ? null : paramClass.getDeclaredConstructor(paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass == null ? null : paramClass.getMethod(paramString, paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    static boolean isRemote(Class<?> paramClass)
    {
      return (remoteClass == null ? null : Boolean.valueOf(remoteClass.isAssignableFrom(paramClass))).booleanValue();
    }
    
    static Class<?> remoteClass()
    {
      return remoteClass;
    }
    
    static Object newMarshalledObject(Object paramObject)
      throws IOException
    {
      try
      {
        return marshallCtor.newInstance(new Object[] { paramObject });
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new AssertionError(localInstantiationException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof IOException)) {
          throw ((IOException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
    }
    
    static Object getMarshalledObject(Object paramObject)
      throws IOException, ClassNotFoundException
    {
      try
      {
        return marshallGet.invoke(paramObject, new Object[0]);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof IOException)) {
          throw ((IOException)localThrowable);
        }
        if ((localThrowable instanceof ClassNotFoundException)) {
          throw ((ClassNotFoundException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
    }
  }
  
  public class ReencodingInputStream
    extends InputStream
  {
    protected BufferedReader wrapped;
    protected final char[] in = new char[2];
    protected byte[] out;
    protected CharsetEncoder encoder;
    protected CharBuffer inBuf;
    protected ByteBuffer outBuf;
    protected char[] eoln;
    protected int numTerminators;
    protected boolean eos;
    protected int index;
    protected int limit;
    
    public ReencodingInputStream(InputStream paramInputStream, long paramLong, String paramString, Transferable paramTransferable)
      throws IOException
    {
      Long localLong = Long.valueOf(paramLong);
      String str1 = null;
      if ((isLocaleDependentTextFormat(paramLong)) && (paramTransferable != null) && (paramTransferable.isDataFlavorSupported(DataTransferer.javaTextEncodingFlavor))) {
        try
        {
          str1 = new String((byte[])paramTransferable.getTransferData(DataTransferer.javaTextEncodingFlavor), "UTF-8");
        }
        catch (UnsupportedFlavorException localUnsupportedFlavorException) {}
      } else {
        str1 = getCharsetForTextFormat(localLong);
      }
      if (str1 == null) {
        str1 = DataTransferer.getDefaultTextCharset();
      }
      wrapped = new BufferedReader(new InputStreamReader(paramInputStream, str1));
      if (paramString == null) {
        throw new NullPointerException("null target encoding");
      }
      try
      {
        encoder = Charset.forName(paramString).newEncoder();
        out = new byte[(int)(encoder.maxBytesPerChar() * 2.0F + 0.5D)];
        inBuf = CharBuffer.wrap(in);
        outBuf = ByteBuffer.wrap(out);
      }
      catch (IllegalCharsetNameException localIllegalCharsetNameException)
      {
        throw new IOException(localIllegalCharsetNameException.toString());
      }
      catch (UnsupportedCharsetException localUnsupportedCharsetException)
      {
        throw new IOException(localUnsupportedCharsetException.toString());
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        throw new IOException(localUnsupportedOperationException.toString());
      }
      String str2 = (String)DataTransferer.nativeEOLNs.get(localLong);
      if (str2 != null) {
        eoln = str2.toCharArray();
      }
      Integer localInteger = (Integer)DataTransferer.nativeTerminators.get(localLong);
      if (localInteger != null) {
        numTerminators = localInteger.intValue();
      }
    }
    
    private int readChar()
      throws IOException
    {
      int i = wrapped.read();
      if (i == -1)
      {
        eos = true;
        return -1;
      }
      if ((numTerminators > 0) && (i == 0))
      {
        eos = true;
        return -1;
      }
      if ((eoln != null) && (matchCharArray(eoln, i))) {
        i = 10;
      }
      return i;
    }
    
    public int read()
      throws IOException
    {
      if (eos) {
        return -1;
      }
      if (index >= limit)
      {
        int i = readChar();
        if (i == -1) {
          return -1;
        }
        in[0] = ((char)i);
        in[1] = '\000';
        inBuf.limit(1);
        if (Character.isHighSurrogate((char)i))
        {
          i = readChar();
          if (i != -1)
          {
            in[1] = ((char)i);
            inBuf.limit(2);
          }
        }
        inBuf.rewind();
        outBuf.limit(out.length).rewind();
        encoder.encode(inBuf, outBuf, false);
        outBuf.flip();
        limit = outBuf.limit();
        index = 0;
        return read();
      }
      return out[(index++)] & 0xFF;
    }
    
    public int available()
      throws IOException
    {
      return eos ? 0 : limit - index;
    }
    
    public void close()
      throws IOException
    {
      wrapped.close();
    }
    
    private boolean matchCharArray(char[] paramArrayOfChar, int paramInt)
      throws IOException
    {
      wrapped.mark(paramArrayOfChar.length);
      int i = 0;
      if ((char)paramInt == paramArrayOfChar[0]) {
        for (i = 1; i < paramArrayOfChar.length; i++)
        {
          paramInt = wrapped.read();
          if ((paramInt == -1) || ((char)paramInt != paramArrayOfChar[i])) {
            break;
          }
        }
      }
      if (i == paramArrayOfChar.length) {
        return true;
      }
      wrapped.reset();
      return false;
    }
  }
  
  private static class StandardEncodingsHolder
  {
    private static final SortedSet<String> standardEncodings = ;
    
    private StandardEncodingsHolder() {}
    
    private static SortedSet<String> load()
    {
      DataTransferer.CharsetComparator localCharsetComparator = new DataTransferer.CharsetComparator(false);
      TreeSet localTreeSet = new TreeSet(localCharsetComparator);
      localTreeSet.add("US-ASCII");
      localTreeSet.add("ISO-8859-1");
      localTreeSet.add("UTF-8");
      localTreeSet.add("UTF-16BE");
      localTreeSet.add("UTF-16LE");
      localTreeSet.add("UTF-16");
      localTreeSet.add(DataTransferer.getDefaultTextCharset());
      return Collections.unmodifiableSortedSet(localTreeSet);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\datatransfer\DataTransferer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */