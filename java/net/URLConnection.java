package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import sun.net.www.MessageHeader;
import sun.net.www.MimeTable;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;

public abstract class URLConnection
{
  protected URL url;
  protected boolean doInput = true;
  protected boolean doOutput = false;
  private static boolean defaultAllowUserInteraction = false;
  protected boolean allowUserInteraction = defaultAllowUserInteraction;
  private static boolean defaultUseCaches = true;
  protected boolean useCaches = defaultUseCaches;
  protected long ifModifiedSince = 0L;
  protected boolean connected = false;
  private int connectTimeout;
  private int readTimeout;
  private MessageHeader requests;
  private static FileNameMap fileNameMap;
  private static boolean fileNameMapLoaded = false;
  static ContentHandlerFactory factory;
  private static Hashtable<String, ContentHandler> handlers = new Hashtable();
  private static final String contentClassPrefix = "sun.net.www.content";
  private static final String contentPathProp = "java.content.handler.pkgs";
  
  public static synchronized FileNameMap getFileNameMap()
  {
    if ((fileNameMap == null) && (!fileNameMapLoaded))
    {
      fileNameMap = MimeTable.loadTable();
      fileNameMapLoaded = true;
    }
    new FileNameMap()
    {
      private FileNameMap map = URLConnection.fileNameMap;
      
      public String getContentTypeFor(String paramAnonymousString)
      {
        return map.getContentTypeFor(paramAnonymousString);
      }
    };
  }
  
  public static void setFileNameMap(FileNameMap paramFileNameMap)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    fileNameMap = paramFileNameMap;
  }
  
  public abstract void connect()
    throws IOException;
  
  public void setConnectTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("timeout can not be negative");
    }
    connectTimeout = paramInt;
  }
  
  public int getConnectTimeout()
  {
    return connectTimeout;
  }
  
  public void setReadTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("timeout can not be negative");
    }
    readTimeout = paramInt;
  }
  
  public int getReadTimeout()
  {
    return readTimeout;
  }
  
  protected URLConnection(URL paramURL)
  {
    url = paramURL;
  }
  
  public URL getURL()
  {
    return url;
  }
  
  public int getContentLength()
  {
    long l = getContentLengthLong();
    if (l > 2147483647L) {
      return -1;
    }
    return (int)l;
  }
  
  public long getContentLengthLong()
  {
    return getHeaderFieldLong("content-length", -1L);
  }
  
  public String getContentType()
  {
    return getHeaderField("content-type");
  }
  
  public String getContentEncoding()
  {
    return getHeaderField("content-encoding");
  }
  
  public long getExpiration()
  {
    return getHeaderFieldDate("expires", 0L);
  }
  
  public long getDate()
  {
    return getHeaderFieldDate("date", 0L);
  }
  
  public long getLastModified()
  {
    return getHeaderFieldDate("last-modified", 0L);
  }
  
  public String getHeaderField(String paramString)
  {
    return null;
  }
  
  public Map<String, List<String>> getHeaderFields()
  {
    return Collections.emptyMap();
  }
  
  public int getHeaderFieldInt(String paramString, int paramInt)
  {
    String str = getHeaderField(paramString);
    try
    {
      return Integer.parseInt(str);
    }
    catch (Exception localException) {}
    return paramInt;
  }
  
  public long getHeaderFieldLong(String paramString, long paramLong)
  {
    String str = getHeaderField(paramString);
    try
    {
      return Long.parseLong(str);
    }
    catch (Exception localException) {}
    return paramLong;
  }
  
  public long getHeaderFieldDate(String paramString, long paramLong)
  {
    String str = getHeaderField(paramString);
    try
    {
      return Date.parse(str);
    }
    catch (Exception localException) {}
    return paramLong;
  }
  
  public String getHeaderFieldKey(int paramInt)
  {
    return null;
  }
  
  public String getHeaderField(int paramInt)
  {
    return null;
  }
  
  public Object getContent()
    throws IOException
  {
    getInputStream();
    return getContentHandler().getContent(this);
  }
  
  public Object getContent(Class[] paramArrayOfClass)
    throws IOException
  {
    getInputStream();
    return getContentHandler().getContent(this, paramArrayOfClass);
  }
  
  public Permission getPermission()
    throws IOException
  {
    return SecurityConstants.ALL_PERMISSION;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    throw new UnknownServiceException("protocol doesn't support input");
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    throw new UnknownServiceException("protocol doesn't support output");
  }
  
  public String toString()
  {
    return getClass().getName() + ":" + url;
  }
  
  public void setDoInput(boolean paramBoolean)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    doInput = paramBoolean;
  }
  
  public boolean getDoInput()
  {
    return doInput;
  }
  
  public void setDoOutput(boolean paramBoolean)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    doOutput = paramBoolean;
  }
  
  public boolean getDoOutput()
  {
    return doOutput;
  }
  
  public void setAllowUserInteraction(boolean paramBoolean)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    allowUserInteraction = paramBoolean;
  }
  
  public boolean getAllowUserInteraction()
  {
    return allowUserInteraction;
  }
  
  public static void setDefaultAllowUserInteraction(boolean paramBoolean)
  {
    defaultAllowUserInteraction = paramBoolean;
  }
  
  public static boolean getDefaultAllowUserInteraction()
  {
    return defaultAllowUserInteraction;
  }
  
  public void setUseCaches(boolean paramBoolean)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    useCaches = paramBoolean;
  }
  
  public boolean getUseCaches()
  {
    return useCaches;
  }
  
  public void setIfModifiedSince(long paramLong)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    ifModifiedSince = paramLong;
  }
  
  public long getIfModifiedSince()
  {
    return ifModifiedSince;
  }
  
  public boolean getDefaultUseCaches()
  {
    return defaultUseCaches;
  }
  
  public void setDefaultUseCaches(boolean paramBoolean)
  {
    defaultUseCaches = paramBoolean;
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (paramString1 == null) {
      throw new NullPointerException("key is null");
    }
    if (requests == null) {
      requests = new MessageHeader();
    }
    requests.set(paramString1, paramString2);
  }
  
  public void addRequestProperty(String paramString1, String paramString2)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (paramString1 == null) {
      throw new NullPointerException("key is null");
    }
    if (requests == null) {
      requests = new MessageHeader();
    }
    requests.add(paramString1, paramString2);
  }
  
  public String getRequestProperty(String paramString)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (requests == null) {
      return null;
    }
    return requests.findValue(paramString);
  }
  
  public Map<String, List<String>> getRequestProperties()
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (requests == null) {
      return Collections.emptyMap();
    }
    return requests.getHeaders(null);
  }
  
  @Deprecated
  public static void setDefaultRequestProperty(String paramString1, String paramString2) {}
  
  @Deprecated
  public static String getDefaultRequestProperty(String paramString)
  {
    return null;
  }
  
  public static synchronized void setContentHandlerFactory(ContentHandlerFactory paramContentHandlerFactory)
  {
    if (factory != null) {
      throw new Error("factory already defined");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    factory = paramContentHandlerFactory;
  }
  
  synchronized ContentHandler getContentHandler()
    throws UnknownServiceException
  {
    String str = stripOffParameters(getContentType());
    ContentHandler localContentHandler = null;
    if (str == null) {
      throw new UnknownServiceException("no content-type");
    }
    try
    {
      localContentHandler = (ContentHandler)handlers.get(str);
      if (localContentHandler != null) {
        return localContentHandler;
      }
    }
    catch (Exception localException1) {}
    if (factory != null) {
      localContentHandler = factory.createContentHandler(str);
    }
    if (localContentHandler == null)
    {
      try
      {
        localContentHandler = lookupContentHandlerClassFor(str);
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
        localContentHandler = UnknownContentHandler.INSTANCE;
      }
      handlers.put(str, localContentHandler);
    }
    return localContentHandler;
  }
  
  private String stripOffParameters(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.indexOf(';');
    if (i > 0) {
      return paramString.substring(0, i);
    }
    return paramString;
  }
  
  private ContentHandler lookupContentHandlerClassFor(String paramString)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    String str1 = typeToPackageName(paramString);
    String str2 = getContentHandlerPkgPrefixes();
    StringTokenizer localStringTokenizer = new StringTokenizer(str2, "|");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str3 = localStringTokenizer.nextToken().trim();
      try
      {
        String str4 = str3 + "." + str1;
        Class localClass = null;
        try
        {
          localClass = Class.forName(str4);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
          if (localClassLoader != null) {
            localClass = localClassLoader.loadClass(str4);
          }
        }
        if (localClass != null)
        {
          ContentHandler localContentHandler = (ContentHandler)localClass.newInstance();
          return localContentHandler;
        }
      }
      catch (Exception localException) {}
    }
    return UnknownContentHandler.INSTANCE;
  }
  
  private String typeToPackageName(String paramString)
  {
    paramString = paramString.toLowerCase();
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    paramString.getChars(0, i, arrayOfChar, 0);
    for (int j = 0; j < i; j++)
    {
      int k = arrayOfChar[j];
      if (k == 47) {
        arrayOfChar[j] = '.';
      } else if (((65 > k) || (k > 90)) && ((97 > k) || (k > 122)) && ((48 > k) || (k > 57))) {
        arrayOfChar[j] = '_';
      }
    }
    return new String(arrayOfChar);
  }
  
  private String getContentHandlerPkgPrefixes()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.content.handler.pkgs", ""));
    if (str != "") {
      str = str + "|";
    }
    return str + "sun.net.www.content";
  }
  
  public static String guessContentTypeFromName(String paramString)
  {
    return getFileNameMap().getContentTypeFor(paramString);
  }
  
  public static String guessContentTypeFromStream(InputStream paramInputStream)
    throws IOException
  {
    if (!paramInputStream.markSupported()) {
      return null;
    }
    paramInputStream.mark(16);
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    int k = paramInputStream.read();
    int m = paramInputStream.read();
    int n = paramInputStream.read();
    int i1 = paramInputStream.read();
    int i2 = paramInputStream.read();
    int i3 = paramInputStream.read();
    int i4 = paramInputStream.read();
    int i5 = paramInputStream.read();
    int i6 = paramInputStream.read();
    int i7 = paramInputStream.read();
    int i8 = paramInputStream.read();
    int i9 = paramInputStream.read();
    int i10 = paramInputStream.read();
    int i11 = paramInputStream.read();
    paramInputStream.reset();
    if ((i == 202) && (j == 254) && (k == 186) && (m == 190)) {
      return "application/java-vm";
    }
    if ((i == 172) && (j == 237)) {
      return "application/x-java-serialized-object";
    }
    if (i == 60)
    {
      if ((j == 33) || ((j == 104) && (((k == 116) && (m == 109) && (n == 108)) || ((k == 101) && (m == 97) && (n == 100)))) || ((j == 98) && (k == 111) && (m == 100) && (n == 121)) || ((j == 72) && (((k == 84) && (m == 77) && (n == 76)) || ((k == 69) && (m == 65) && (n == 68)))) || ((j == 66) && (k == 79) && (m == 68) && (n == 89))) {
        return "text/html";
      }
      if ((j == 63) && (k == 120) && (m == 109) && (n == 108) && (i1 == 32)) {
        return "application/xml";
      }
    }
    if ((i == 239) && (j == 187) && (k == 191) && (m == 60) && (n == 63) && (i1 == 120)) {
      return "application/xml";
    }
    if ((i == 254) && (j == 255) && (k == 0) && (m == 60) && (n == 0) && (i1 == 63) && (i2 == 0) && (i3 == 120)) {
      return "application/xml";
    }
    if ((i == 255) && (j == 254) && (k == 60) && (m == 0) && (n == 63) && (i1 == 0) && (i2 == 120) && (i3 == 0)) {
      return "application/xml";
    }
    if ((i == 0) && (j == 0) && (k == 254) && (m == 255) && (n == 0) && (i1 == 0) && (i2 == 0) && (i3 == 60) && (i4 == 0) && (i5 == 0) && (i6 == 0) && (i7 == 63) && (i8 == 0) && (i9 == 0) && (i10 == 0) && (i11 == 120)) {
      return "application/xml";
    }
    if ((i == 255) && (j == 254) && (k == 0) && (m == 0) && (n == 60) && (i1 == 0) && (i2 == 0) && (i3 == 0) && (i4 == 63) && (i5 == 0) && (i6 == 0) && (i7 == 0) && (i8 == 120) && (i9 == 0) && (i10 == 0) && (i11 == 0)) {
      return "application/xml";
    }
    if ((i == 71) && (j == 73) && (k == 70) && (m == 56)) {
      return "image/gif";
    }
    if ((i == 35) && (j == 100) && (k == 101) && (m == 102)) {
      return "image/x-bitmap";
    }
    if ((i == 33) && (j == 32) && (k == 88) && (m == 80) && (n == 77) && (i1 == 50)) {
      return "image/x-pixmap";
    }
    if ((i == 137) && (j == 80) && (k == 78) && (m == 71) && (n == 13) && (i1 == 10) && (i2 == 26) && (i3 == 10)) {
      return "image/png";
    }
    if ((i == 255) && (j == 216) && (k == 255))
    {
      if ((m == 224) || (m == 238)) {
        return "image/jpeg";
      }
      if ((m == 225) && (i2 == 69) && (i3 == 120) && (i4 == 105) && (i5 == 102) && (i6 == 0)) {
        return "image/jpeg";
      }
    }
    if ((i == 208) && (j == 207) && (k == 17) && (m == 224) && (n == 161) && (i1 == 177) && (i2 == 26) && (i3 == 225) && (checkfpx(paramInputStream))) {
      return "image/vnd.fpx";
    }
    if ((i == 46) && (j == 115) && (k == 110) && (m == 100)) {
      return "audio/basic";
    }
    if ((i == 100) && (j == 110) && (k == 115) && (m == 46)) {
      return "audio/basic";
    }
    if ((i == 82) && (j == 73) && (k == 70) && (m == 70)) {
      return "audio/x-wav";
    }
    return null;
  }
  
  private static boolean checkfpx(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream.mark(256);
    long l1 = 28L;
    long l2;
    if ((l2 = skipForward(paramInputStream, l1)) < l1)
    {
      paramInputStream.reset();
      return false;
    }
    int[] arrayOfInt = new int[16];
    if (readBytes(arrayOfInt, 2, paramInputStream) < 0)
    {
      paramInputStream.reset();
      return false;
    }
    int i = arrayOfInt[0];
    l2 += 2L;
    if (readBytes(arrayOfInt, 2, paramInputStream) < 0)
    {
      paramInputStream.reset();
      return false;
    }
    int j;
    if (i == 254)
    {
      j = arrayOfInt[0];
      j += (arrayOfInt[1] << 8);
    }
    else
    {
      j = arrayOfInt[0] << 8;
      j += arrayOfInt[1];
    }
    l2 += 2L;
    l1 = 48L - l2;
    long l3 = 0L;
    if ((l3 = skipForward(paramInputStream, l1)) < l1)
    {
      paramInputStream.reset();
      return false;
    }
    l2 += l3;
    if (readBytes(arrayOfInt, 4, paramInputStream) < 0)
    {
      paramInputStream.reset();
      return false;
    }
    int k;
    if (i == 254)
    {
      k = arrayOfInt[0];
      k += (arrayOfInt[1] << 8);
      k += (arrayOfInt[2] << 16);
      k += (arrayOfInt[3] << 24);
    }
    else
    {
      k = arrayOfInt[0] << 24;
      k += (arrayOfInt[1] << 16);
      k += (arrayOfInt[2] << 8);
      k += arrayOfInt[3];
    }
    l2 += 4L;
    paramInputStream.reset();
    l1 = 512L + (1 << j) * k + 80L;
    if (l1 < 0L) {
      return false;
    }
    paramInputStream.mark((int)l1 + 48);
    if (skipForward(paramInputStream, l1) < l1)
    {
      paramInputStream.reset();
      return false;
    }
    if (readBytes(arrayOfInt, 16, paramInputStream) < 0)
    {
      paramInputStream.reset();
      return false;
    }
    if ((i == 254) && (arrayOfInt[0] == 0) && (arrayOfInt[2] == 97) && (arrayOfInt[3] == 86) && (arrayOfInt[4] == 84) && (arrayOfInt[5] == 193) && (arrayOfInt[6] == 206) && (arrayOfInt[7] == 17) && (arrayOfInt[8] == 133) && (arrayOfInt[9] == 83) && (arrayOfInt[10] == 0) && (arrayOfInt[11] == 170) && (arrayOfInt[12] == 0) && (arrayOfInt[13] == 161) && (arrayOfInt[14] == 249) && (arrayOfInt[15] == 91))
    {
      paramInputStream.reset();
      return true;
    }
    if ((arrayOfInt[3] == 0) && (arrayOfInt[1] == 97) && (arrayOfInt[0] == 86) && (arrayOfInt[5] == 84) && (arrayOfInt[4] == 193) && (arrayOfInt[7] == 206) && (arrayOfInt[6] == 17) && (arrayOfInt[8] == 133) && (arrayOfInt[9] == 83) && (arrayOfInt[10] == 0) && (arrayOfInt[11] == 170) && (arrayOfInt[12] == 0) && (arrayOfInt[13] == 161) && (arrayOfInt[14] == 249) && (arrayOfInt[15] == 91))
    {
      paramInputStream.reset();
      return true;
    }
    paramInputStream.reset();
    return false;
  }
  
  private static int readBytes(int[] paramArrayOfInt, int paramInt, InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    if (paramInputStream.read(arrayOfByte, 0, paramInt) < paramInt) {
      return -1;
    }
    for (int i = 0; i < paramInt; i++) {
      arrayOfByte[i] &= 0xFF;
    }
    return 0;
  }
  
  private static long skipForward(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    long l1 = 0L;
    for (long l2 = 0L; l2 != paramLong; l2 += l1)
    {
      l1 = paramInputStream.skip(paramLong - l2);
      if (l1 <= 0L)
      {
        if (paramInputStream.read() == -1) {
          return l2;
        }
        l2 += 1L;
      }
    }
    return l2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */