package java.io;

import java.security.AccessController;
import java.util.Locale;
import sun.security.action.GetPropertyAction;

class WinNTFileSystem
  extends FileSystem
{
  private final char slash = ((String)AccessController.doPrivileged(new GetPropertyAction("file.separator"))).charAt(0);
  private final char altSlash = slash == '\\' ? '/' : '\\';
  private final char semicolon = ((String)AccessController.doPrivileged(new GetPropertyAction("path.separator"))).charAt(0);
  private static String[] driveDirCache = new String[26];
  private ExpiringCache cache = new ExpiringCache();
  private ExpiringCache prefixCache = new ExpiringCache();
  
  public WinNTFileSystem() {}
  
  private boolean isSlash(char paramChar)
  {
    return (paramChar == '\\') || (paramChar == '/');
  }
  
  private boolean isLetter(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
  }
  
  private String slashify(String paramString)
  {
    if ((paramString.length() > 0) && (paramString.charAt(0) != slash)) {
      return slash + paramString;
    }
    return paramString;
  }
  
  public char getSeparator()
  {
    return slash;
  }
  
  public char getPathSeparator()
  {
    return semicolon;
  }
  
  public String normalize(String paramString)
  {
    int i = paramString.length();
    int j = slash;
    int k = altSlash;
    int m = 0;
    for (int n = 0; n < i; n++)
    {
      int i1 = paramString.charAt(n);
      if (i1 == k) {
        return normalize(paramString, i, m == j ? n - 1 : n);
      }
      if ((i1 == j) && (m == j) && (n > 1)) {
        return normalize(paramString, i, n - 1);
      }
      if ((i1 == 58) && (n > 1)) {
        return normalize(paramString, i, 0);
      }
      m = i1;
    }
    if (m == j) {
      return normalize(paramString, i, i - 1);
    }
    return paramString;
  }
  
  private String normalize(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0) {
      return paramString;
    }
    if (paramInt2 < 3) {
      paramInt2 = 0;
    }
    char c1 = slash;
    StringBuffer localStringBuffer = new StringBuffer(paramInt1);
    int i;
    if (paramInt2 == 0)
    {
      i = normalizePrefix(paramString, paramInt1, localStringBuffer);
    }
    else
    {
      i = paramInt2;
      localStringBuffer.append(paramString.substring(0, paramInt2));
    }
    while (i < paramInt1)
    {
      char c2 = paramString.charAt(i++);
      if (isSlash(c2))
      {
        while ((i < paramInt1) && (isSlash(paramString.charAt(i)))) {
          i++;
        }
        if (i == paramInt1)
        {
          int j = localStringBuffer.length();
          if ((j == 2) && (localStringBuffer.charAt(1) == ':'))
          {
            localStringBuffer.append(c1);
            break;
          }
          if (j == 0)
          {
            localStringBuffer.append(c1);
            break;
          }
          if ((j != 1) || (!isSlash(localStringBuffer.charAt(0)))) {
            break;
          }
          localStringBuffer.append(c1);
          break;
        }
        localStringBuffer.append(c1);
      }
      else
      {
        localStringBuffer.append(c2);
      }
    }
    String str = localStringBuffer.toString();
    return str;
  }
  
  private int normalizePrefix(String paramString, int paramInt, StringBuffer paramStringBuffer)
  {
    for (int i = 0; (i < paramInt) && (isSlash(paramString.charAt(i))); i++) {}
    char c;
    if ((paramInt - i >= 2) && (isLetter(c = paramString.charAt(i))) && (paramString.charAt(i + 1) == ':'))
    {
      paramStringBuffer.append(c);
      paramStringBuffer.append(':');
      i += 2;
    }
    else
    {
      i = 0;
      if ((paramInt >= 2) && (isSlash(paramString.charAt(0))) && (isSlash(paramString.charAt(1))))
      {
        i = 1;
        paramStringBuffer.append(slash);
      }
    }
    return i;
  }
  
  public int prefixLength(String paramString)
  {
    int i = slash;
    int j = paramString.length();
    if (j == 0) {
      return 0;
    }
    char c = paramString.charAt(0);
    int k = j > 1 ? paramString.charAt(1) : 0;
    if (c == i)
    {
      if (k == i) {
        return 2;
      }
      return 1;
    }
    if ((isLetter(c)) && (k == 58))
    {
      if ((j > 2) && (paramString.charAt(2) == i)) {
        return 3;
      }
      return 2;
    }
    return 0;
  }
  
  public String resolve(String paramString1, String paramString2)
  {
    int i = paramString1.length();
    if (i == 0) {
      return paramString2;
    }
    int j = paramString2.length();
    if (j == 0) {
      return paramString1;
    }
    String str = paramString2;
    int k = 0;
    int m = i;
    if ((j > 1) && (str.charAt(0) == slash))
    {
      if (str.charAt(1) == slash) {
        k = 2;
      } else {
        k = 1;
      }
      if (j == k)
      {
        if (paramString1.charAt(i - 1) == slash) {
          return paramString1.substring(0, i - 1);
        }
        return paramString1;
      }
    }
    if (paramString1.charAt(i - 1) == slash) {
      m--;
    }
    int n = m + j - k;
    char[] arrayOfChar = null;
    if (paramString2.charAt(k) == slash)
    {
      arrayOfChar = new char[n];
      paramString1.getChars(0, m, arrayOfChar, 0);
      paramString2.getChars(k, j, arrayOfChar, m);
    }
    else
    {
      arrayOfChar = new char[n + 1];
      paramString1.getChars(0, m, arrayOfChar, 0);
      arrayOfChar[m] = slash;
      paramString2.getChars(k, j, arrayOfChar, m + 1);
    }
    return new String(arrayOfChar);
  }
  
  public String getDefaultParent()
  {
    return "" + slash;
  }
  
  public String fromURIPath(String paramString)
  {
    String str = paramString;
    if ((str.length() > 2) && (str.charAt(2) == ':'))
    {
      str = str.substring(1);
      if ((str.length() > 3) && (str.endsWith("/"))) {
        str = str.substring(0, str.length() - 1);
      }
    }
    else if ((str.length() > 1) && (str.endsWith("/")))
    {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }
  
  public boolean isAbsolute(File paramFile)
  {
    int i = paramFile.getPrefixLength();
    return ((i == 2) && (paramFile.getPath().charAt(0) == slash)) || (i == 3);
  }
  
  public String resolve(File paramFile)
  {
    String str1 = paramFile.getPath();
    int i = paramFile.getPrefixLength();
    if ((i == 2) && (str1.charAt(0) == slash)) {
      return str1;
    }
    if (i == 3) {
      return str1;
    }
    if (i == 0) {
      return getUserPath() + slashify(str1);
    }
    String str2;
    String str3;
    if (i == 1)
    {
      str2 = getUserPath();
      str3 = getDrive(str2);
      if (str3 != null) {
        return str3 + str1;
      }
      return str2 + str1;
    }
    if (i == 2)
    {
      str2 = getUserPath();
      str3 = getDrive(str2);
      if ((str3 != null) && (str1.startsWith(str3))) {
        return str2 + slashify(str1.substring(2));
      }
      char c = str1.charAt(0);
      String str4 = getDriveDirectory(c);
      if (str4 != null)
      {
        String str5 = c + ':' + str4 + slashify(str1.substring(2));
        SecurityManager localSecurityManager = System.getSecurityManager();
        try
        {
          if (localSecurityManager != null) {
            localSecurityManager.checkRead(str5);
          }
        }
        catch (SecurityException localSecurityException)
        {
          throw new SecurityException("Cannot resolve path " + str1);
        }
        return str5;
      }
      return c + ":" + slashify(str1.substring(2));
    }
    throw new InternalError("Unresolvable path: " + str1);
  }
  
  private String getUserPath()
  {
    return normalize(System.getProperty("user.dir"));
  }
  
  private String getDrive(String paramString)
  {
    int i = prefixLength(paramString);
    return i == 3 ? paramString.substring(0, 2) : null;
  }
  
  private static int driveIndex(char paramChar)
  {
    if ((paramChar >= 'a') && (paramChar <= 'z')) {
      return paramChar - 'a';
    }
    if ((paramChar >= 'A') && (paramChar <= 'Z')) {
      return paramChar - 'A';
    }
    return -1;
  }
  
  private native String getDriveDirectory(int paramInt);
  
  private String getDriveDirectory(char paramChar)
  {
    int i = driveIndex(paramChar);
    if (i < 0) {
      return null;
    }
    String str = driveDirCache[i];
    if (str != null) {
      return str;
    }
    str = getDriveDirectory(i + 1);
    driveDirCache[i] = str;
    return str;
  }
  
  public String canonicalize(String paramString)
    throws IOException
  {
    int i = paramString.length();
    int j;
    if ((i == 2) && (isLetter(paramString.charAt(0))) && (paramString.charAt(1) == ':'))
    {
      j = paramString.charAt(0);
      if ((j >= 65) && (j <= 90)) {
        return paramString;
      }
      return "" + (char)(j - 32) + ':';
    }
    if ((i == 3) && (isLetter(paramString.charAt(0))) && (paramString.charAt(1) == ':') && (paramString.charAt(2) == '\\'))
    {
      j = paramString.charAt(0);
      if ((j >= 65) && (j <= 90)) {
        return paramString;
      }
      return "" + (char)(j - 32) + ':' + '\\';
    }
    if (!useCanonCaches) {
      return canonicalize0(paramString);
    }
    String str1 = cache.get(paramString);
    if (str1 == null)
    {
      String str2 = null;
      String str3 = null;
      Object localObject;
      if (useCanonPrefixCache)
      {
        str2 = parentOrNull(paramString);
        if (str2 != null)
        {
          str3 = prefixCache.get(str2);
          if (str3 != null)
          {
            localObject = paramString.substring(1 + str2.length());
            str1 = canonicalizeWithPrefix(str3, (String)localObject);
            cache.put(str2 + File.separatorChar + (String)localObject, str1);
          }
        }
      }
      if (str1 == null)
      {
        str1 = canonicalize0(paramString);
        cache.put(paramString, str1);
        if ((useCanonPrefixCache) && (str2 != null))
        {
          str3 = parentOrNull(str1);
          if (str3 != null)
          {
            localObject = new File(str1);
            if ((((File)localObject).exists()) && (!((File)localObject).isDirectory())) {
              prefixCache.put(str2, str3);
            }
          }
        }
      }
    }
    return str1;
  }
  
  private native String canonicalize0(String paramString)
    throws IOException;
  
  private String canonicalizeWithPrefix(String paramString1, String paramString2)
    throws IOException
  {
    return canonicalizeWithPrefix0(paramString1, paramString1 + File.separatorChar + paramString2);
  }
  
  private native String canonicalizeWithPrefix0(String paramString1, String paramString2)
    throws IOException;
  
  private static String parentOrNull(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = File.separatorChar;
    int j = 47;
    int k = paramString.length() - 1;
    int m = k;
    int n = 0;
    int i1 = 0;
    while (m > 0)
    {
      int i2 = paramString.charAt(m);
      if (i2 == 46)
      {
        n++;
        if (n >= 2) {
          return null;
        }
        if (i1 == 0) {
          return null;
        }
      }
      else
      {
        if (i2 == i)
        {
          if ((n == 1) && (i1 == 0)) {
            return null;
          }
          if ((m == 0) || (m >= k - 1) || (paramString.charAt(m - 1) == i) || (paramString.charAt(m - 1) == j)) {
            return null;
          }
          return paramString.substring(0, m);
        }
        if (i2 == j) {
          return null;
        }
        if ((i2 == 42) || (i2 == 63)) {
          return null;
        }
        i1++;
        n = 0;
      }
      m--;
    }
    return null;
  }
  
  public native int getBooleanAttributes(File paramFile);
  
  public native boolean checkAccess(File paramFile, int paramInt);
  
  public native long getLastModifiedTime(File paramFile);
  
  public native long getLength(File paramFile);
  
  public native boolean setPermission(File paramFile, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  public native boolean createFileExclusively(String paramString)
    throws IOException;
  
  public native String[] list(File paramFile);
  
  public native boolean createDirectory(File paramFile);
  
  public native boolean setLastModifiedTime(File paramFile, long paramLong);
  
  public native boolean setReadOnly(File paramFile);
  
  public boolean delete(File paramFile)
  {
    cache.clear();
    prefixCache.clear();
    return delete0(paramFile);
  }
  
  private native boolean delete0(File paramFile);
  
  public boolean rename(File paramFile1, File paramFile2)
  {
    cache.clear();
    prefixCache.clear();
    return rename0(paramFile1, paramFile2);
  }
  
  private native boolean rename0(File paramFile1, File paramFile2);
  
  public File[] listRoots()
  {
    int i = listRoots0();
    int j = 0;
    for (int k = 0; k < 26; k++) {
      if ((i >> k & 0x1) != 0) {
        if (!access((char)(65 + k) + ":" + slash)) {
          i &= (1 << k ^ 0xFFFFFFFF);
        } else {
          j++;
        }
      }
    }
    File[] arrayOfFile = new File[j];
    int m = 0;
    char c = slash;
    for (int n = 0; n < 26; n++) {
      if ((i >> n & 0x1) != 0) {
        arrayOfFile[(m++)] = new File((char)(65 + n) + ":" + c);
      }
    }
    return arrayOfFile;
  }
  
  private static native int listRoots0();
  
  private boolean access(String paramString)
  {
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkRead(paramString);
      }
      return true;
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
  
  public long getSpace(File paramFile, int paramInt)
  {
    if (paramFile.exists()) {
      return getSpace0(paramFile, paramInt);
    }
    return 0L;
  }
  
  private native long getSpace0(File paramFile, int paramInt);
  
  public int compare(File paramFile1, File paramFile2)
  {
    return paramFile1.getPath().compareToIgnoreCase(paramFile2.getPath());
  }
  
  public int hashCode(File paramFile)
  {
    return paramFile.getPath().toLowerCase(Locale.ENGLISH).hashCode() ^ 0x12D591;
  }
  
  private static native void initIDs();
  
  static
  {
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\WinNTFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */