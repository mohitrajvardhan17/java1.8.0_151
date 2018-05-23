package sun.nio.fs;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.NotLinkException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class WindowsLinkSupport
{
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private WindowsLinkSupport() {}
  
  static String readLink(WindowsPath paramWindowsPath)
    throws IOException
  {
    long l = 0L;
    try
    {
      l = paramWindowsPath.openForReadAttributeAccess(false);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(paramWindowsPath);
    }
    try
    {
      String str = readLinkImpl(l);
      return str;
    }
    finally
    {
      WindowsNativeDispatcher.CloseHandle(l);
    }
  }
  
  static String getFinalPath(WindowsPath paramWindowsPath)
    throws IOException
  {
    long l = 0L;
    try
    {
      l = paramWindowsPath.openForReadAttributeAccess(true);
    }
    catch (WindowsException localWindowsException1)
    {
      localWindowsException1.rethrowAsIOException(paramWindowsPath);
    }
    try
    {
      String str = stripPrefix(WindowsNativeDispatcher.GetFinalPathNameByHandle(l));
      return str;
    }
    catch (WindowsException localWindowsException2)
    {
      if (localWindowsException2.lastError() != 124) {
        localWindowsException2.rethrowAsIOException(paramWindowsPath);
      }
    }
    finally
    {
      WindowsNativeDispatcher.CloseHandle(l);
    }
    return null;
  }
  
  static String getFinalPath(WindowsPath paramWindowsPath, boolean paramBoolean)
    throws IOException
  {
    WindowsFileSystem localWindowsFileSystem = paramWindowsPath.getFileSystem();
    try
    {
      if ((!paramBoolean) || (!localWindowsFileSystem.supportsLinks())) {
        return paramWindowsPath.getPathForWin32Calls();
      }
      if (!WindowsFileAttributes.get(paramWindowsPath, false).isSymbolicLink()) {
        return paramWindowsPath.getPathForWin32Calls();
      }
    }
    catch (WindowsException localWindowsException1)
    {
      localWindowsException1.rethrowAsIOException(paramWindowsPath);
    }
    String str = getFinalPath(paramWindowsPath);
    if (str != null) {
      return str;
    }
    WindowsPath localWindowsPath1 = paramWindowsPath;
    int i = 0;
    do
    {
      try
      {
        WindowsFileAttributes localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath1, false);
        if (!localWindowsFileAttributes.isSymbolicLink()) {
          return localWindowsPath1.getPathForWin32Calls();
        }
      }
      catch (WindowsException localWindowsException2)
      {
        localWindowsException2.rethrowAsIOException(localWindowsPath1);
      }
      WindowsPath localWindowsPath2 = WindowsPath.createFromNormalizedPath(localWindowsFileSystem, readLink(localWindowsPath1));
      WindowsPath localWindowsPath3 = localWindowsPath1.getParent();
      if (localWindowsPath3 == null)
      {
        WindowsPath localWindowsPath4 = localWindowsPath1;
        localWindowsPath1 = (WindowsPath)AccessController.doPrivileged(new PrivilegedAction()
        {
          public WindowsPath run()
          {
            return val$t.toAbsolutePath();
          }
        });
        localWindowsPath3 = localWindowsPath1.getParent();
      }
      localWindowsPath1 = localWindowsPath3.resolve(localWindowsPath2);
      i++;
    } while (i < 32);
    throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "Too many links");
  }
  
  static String getRealPath(WindowsPath paramWindowsPath, boolean paramBoolean)
    throws IOException
  {
    WindowsFileSystem localWindowsFileSystem = paramWindowsPath.getFileSystem();
    if ((paramBoolean) && (!localWindowsFileSystem.supportsLinks())) {
      paramBoolean = false;
    }
    String str1 = null;
    try
    {
      str1 = paramWindowsPath.toAbsolutePath().toString();
    }
    catch (IOError localIOError)
    {
      throw ((IOException)localIOError.getCause());
    }
    if (str1.indexOf('.') >= 0) {
      try
      {
        str1 = WindowsNativeDispatcher.GetFullPathName(str1);
      }
      catch (WindowsException localWindowsException1)
      {
        localWindowsException1.rethrowAsIOException(paramWindowsPath);
      }
    }
    StringBuilder localStringBuilder = new StringBuilder(str1.length());
    char c = str1.charAt(0);
    int i = str1.charAt(1);
    String str2;
    if (((c <= 'z') && (c >= 'a')) || ((c <= 'Z') && (c >= 'A') && (i == 58) && (str1.charAt(2) == '\\')))
    {
      localStringBuilder.append(Character.toUpperCase(c));
      localStringBuilder.append(":\\");
      str2 = 3;
    }
    else if ((c == '\\') && (i == 92))
    {
      int j = str1.length() - 1;
      int m = str1.indexOf('\\', 2);
      if ((m == -1) || (m == j)) {
        throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "UNC has invalid share");
      }
      m = str1.indexOf('\\', m + 1);
      if (m < 0)
      {
        m = j;
        localStringBuilder.append(str1).append("\\");
      }
      else
      {
        localStringBuilder.append(str1, 0, m + 1);
      }
      str2 = m + 1;
    }
    else
    {
      throw new AssertionError("path type not recognized");
    }
    if (str2 >= str1.length())
    {
      str3 = localStringBuilder.toString();
      try
      {
        WindowsNativeDispatcher.GetFileAttributes(str3);
      }
      catch (WindowsException localWindowsException2)
      {
        localWindowsException2.rethrowAsIOException(str1);
      }
      return str3;
    }
    int i1;
    int k;
    for (String str3 = str2; str3 < str1.length(); k = i1 + 1)
    {
      int n = str1.indexOf('\\', str3);
      i1 = n == -1 ? str1.length() : n;
      String str4 = localStringBuilder.toString() + str1.substring(str3, i1);
      try
      {
        WindowsNativeDispatcher.FirstFile localFirstFile = WindowsNativeDispatcher.FindFirstFile(WindowsPath.addPrefixIfNeeded(str4));
        WindowsNativeDispatcher.FindClose(localFirstFile.handle());
        if ((paramBoolean) && (WindowsFileAttributes.isReparsePoint(localFirstFile.attributes())))
        {
          String str5 = getFinalPath(paramWindowsPath);
          if (str5 == null)
          {
            WindowsPath localWindowsPath = resolveAllLinks(WindowsPath.createFromNormalizedPath(localWindowsFileSystem, str1));
            str5 = getRealPath(localWindowsPath, false);
          }
          return str5;
        }
        localStringBuilder.append(localFirstFile.name());
        if (n != -1) {
          localStringBuilder.append('\\');
        }
      }
      catch (WindowsException localWindowsException3)
      {
        localWindowsException3.rethrowAsIOException(str1);
      }
    }
    return localStringBuilder.toString();
  }
  
  private static String readLinkImpl(long paramLong)
    throws IOException
  {
    int i = 16384;
    NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(i);
    try
    {
      try
      {
        WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(paramLong, localNativeBuffer.address(), i);
      }
      catch (WindowsException localWindowsException)
      {
        if (localWindowsException.lastError() == 4390) {
          throw new NotLinkException(null, null, localWindowsException.errorString());
        }
        localWindowsException.rethrowAsIOException((String)null);
      }
      int j = (int)unsafe.getLong(localNativeBuffer.address() + 0L);
      if (j != -1610612724) {
        throw new NotLinkException(null, null, "Reparse point is not a symbolic link");
      }
      int k = unsafe.getShort(localNativeBuffer.address() + 8L);
      int m = unsafe.getShort(localNativeBuffer.address() + 10L);
      if (m % 2 != 0) {
        throw new FileSystemException(null, null, "Symbolic link corrupted");
      }
      char[] arrayOfChar = new char[m / 2];
      unsafe.copyMemory(null, localNativeBuffer.address() + 20L + k, arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, m);
      String str1 = stripPrefix(new String(arrayOfChar));
      if (str1.length() == 0) {
        throw new IOException("Symbolic link target is invalid");
      }
      String str2 = str1;
      return str2;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static WindowsPath resolveAllLinks(WindowsPath paramWindowsPath)
    throws IOException
  {
    assert (paramWindowsPath.isAbsolute());
    WindowsFileSystem localWindowsFileSystem = paramWindowsPath.getFileSystem();
    int i = 0;
    int j = 0;
    while (j < paramWindowsPath.getNameCount())
    {
      WindowsPath localWindowsPath1 = paramWindowsPath.getRoot().resolve(paramWindowsPath.subpath(0, j + 1));
      WindowsFileAttributes localWindowsFileAttributes = null;
      try
      {
        localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath1, false);
      }
      catch (WindowsException localWindowsException1)
      {
        localWindowsException1.rethrowAsIOException(localWindowsPath1);
      }
      if (localWindowsFileAttributes.isSymbolicLink())
      {
        i++;
        if (i > 32) {
          throw new IOException("Too many links");
        }
        WindowsPath localWindowsPath2 = WindowsPath.createFromNormalizedPath(localWindowsFileSystem, readLink(localWindowsPath1));
        WindowsPath localWindowsPath3 = null;
        int k = paramWindowsPath.getNameCount();
        if (j + 1 < k) {
          localWindowsPath3 = paramWindowsPath.subpath(j + 1, k);
        }
        paramWindowsPath = localWindowsPath1.getParent().resolve(localWindowsPath2);
        try
        {
          String str = WindowsNativeDispatcher.GetFullPathName(paramWindowsPath.toString());
          if (!str.equals(paramWindowsPath.toString())) {
            paramWindowsPath = WindowsPath.createFromNormalizedPath(localWindowsFileSystem, str);
          }
        }
        catch (WindowsException localWindowsException2)
        {
          localWindowsException2.rethrowAsIOException(paramWindowsPath);
        }
        if (localWindowsPath3 != null) {
          paramWindowsPath = paramWindowsPath.resolve(localWindowsPath3);
        }
        j = 0;
      }
      else
      {
        j++;
      }
    }
    return paramWindowsPath;
  }
  
  private static String stripPrefix(String paramString)
  {
    if (paramString.startsWith("\\\\?\\"))
    {
      if (paramString.startsWith("\\\\?\\UNC\\")) {
        paramString = "\\" + paramString.substring(7);
      } else {
        paramString = paramString.substring(4);
      }
      return paramString;
    }
    if (paramString.startsWith("\\??\\"))
    {
      if (paramString.startsWith("\\??\\UNC\\")) {
        paramString = "\\" + paramString.substring(7);
      } else {
        paramString = paramString.substring(4);
      }
      return paramString;
    }
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsLinkSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */