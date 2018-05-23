package sun.nio.fs;

import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.IOError;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class WindowsPath
  extends AbstractPath
{
  private static final int MAX_PATH = 247;
  private static final int MAX_LONG_PATH = 32000;
  private final WindowsFileSystem fs;
  private final WindowsPathType type;
  private final String root;
  private final String path;
  private volatile WeakReference<String> pathForWin32Calls;
  private volatile Integer[] offsets;
  private int hash;
  
  private WindowsPath(WindowsFileSystem paramWindowsFileSystem, WindowsPathType paramWindowsPathType, String paramString1, String paramString2)
  {
    fs = paramWindowsFileSystem;
    type = paramWindowsPathType;
    root = paramString1;
    path = paramString2;
  }
  
  static WindowsPath parse(WindowsFileSystem paramWindowsFileSystem, String paramString)
  {
    WindowsPathParser.Result localResult = WindowsPathParser.parse(paramString);
    return new WindowsPath(paramWindowsFileSystem, localResult.type(), localResult.root(), localResult.path());
  }
  
  static WindowsPath createFromNormalizedPath(WindowsFileSystem paramWindowsFileSystem, String paramString, BasicFileAttributes paramBasicFileAttributes)
  {
    try
    {
      WindowsPathParser.Result localResult = WindowsPathParser.parseNormalizedPath(paramString);
      if (paramBasicFileAttributes == null) {
        return new WindowsPath(paramWindowsFileSystem, localResult.type(), localResult.root(), localResult.path());
      }
      return new WindowsPathWithAttributes(paramWindowsFileSystem, localResult.type(), localResult.root(), localResult.path(), paramBasicFileAttributes);
    }
    catch (InvalidPathException localInvalidPathException)
    {
      throw new AssertionError(localInvalidPathException.getMessage());
    }
  }
  
  static WindowsPath createFromNormalizedPath(WindowsFileSystem paramWindowsFileSystem, String paramString)
  {
    return createFromNormalizedPath(paramWindowsFileSystem, paramString, null);
  }
  
  String getPathForExceptionMessage()
  {
    return path;
  }
  
  String getPathForPermissionCheck()
  {
    return path;
  }
  
  String getPathForWin32Calls()
    throws WindowsException
  {
    if ((isAbsolute()) && (path.length() <= 247)) {
      return path;
    }
    WeakReference localWeakReference = pathForWin32Calls;
    String str = localWeakReference != null ? (String)localWeakReference.get() : null;
    if (str != null) {
      return str;
    }
    str = getAbsolutePath();
    if (str.length() > 247)
    {
      if (str.length() > 32000) {
        throw new WindowsException("Cannot access file with path exceeding 32000 characters");
      }
      str = addPrefixIfNeeded(WindowsNativeDispatcher.GetFullPathName(str));
    }
    if (type != WindowsPathType.DRIVE_RELATIVE) {
      synchronized (path)
      {
        pathForWin32Calls = new WeakReference(str);
      }
    }
    return str;
  }
  
  private String getAbsolutePath()
    throws WindowsException
  {
    if (isAbsolute()) {
      return path;
    }
    String str1;
    Object localObject;
    if (type == WindowsPathType.RELATIVE)
    {
      str1 = getFileSystem().defaultDirectory();
      if (isEmpty()) {
        return str1;
      }
      if (str1.endsWith("\\")) {
        return str1 + path;
      }
      localObject = new StringBuilder(str1.length() + path.length() + 1);
      return str1 + '\\' + path;
    }
    if (type == WindowsPathType.DIRECTORY_RELATIVE)
    {
      str1 = getFileSystem().defaultRoot();
      return str1 + path.substring(1);
    }
    if (isSameDrive(root, getFileSystem().defaultRoot()))
    {
      str1 = path.substring(root.length());
      localObject = getFileSystem().defaultDirectory();
      String str3;
      if (((String)localObject).endsWith("\\")) {
        str3 = (String)localObject + str1;
      } else {
        str3 = (String)localObject + "\\" + str1;
      }
      return str3;
    }
    try
    {
      int i = WindowsNativeDispatcher.GetDriveType(root + "\\");
      if ((i == 0) || (i == 1)) {
        throw new WindowsException("");
      }
      str1 = WindowsNativeDispatcher.GetFullPathName(root + ".");
    }
    catch (WindowsException localWindowsException)
    {
      throw new WindowsException("Unable to get working directory of drive '" + Character.toUpperCase(root.charAt(0)) + "'");
    }
    String str2 = str1;
    if (str1.endsWith("\\")) {
      str2 = str2 + path.substring(root.length());
    } else if (path.length() > root.length()) {
      str2 = str2 + "\\" + path.substring(root.length());
    }
    return str2;
  }
  
  private static boolean isSameDrive(String paramString1, String paramString2)
  {
    return Character.toUpperCase(paramString1.charAt(0)) == Character.toUpperCase(paramString2.charAt(0));
  }
  
  static String addPrefixIfNeeded(String paramString)
  {
    if (paramString.length() > 247) {
      if (paramString.startsWith("\\\\")) {
        paramString = "\\\\?\\UNC" + paramString.substring(1, paramString.length());
      } else {
        paramString = "\\\\?\\" + paramString;
      }
    }
    return paramString;
  }
  
  public WindowsFileSystem getFileSystem()
  {
    return fs;
  }
  
  private boolean isEmpty()
  {
    return path.length() == 0;
  }
  
  private WindowsPath emptyPath()
  {
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", "");
  }
  
  public Path getFileName()
  {
    int i = path.length();
    if (i == 0) {
      return this;
    }
    if (root.length() == i) {
      return null;
    }
    int j = path.lastIndexOf('\\');
    if (j < root.length()) {
      j = root.length();
    } else {
      j++;
    }
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", path.substring(j));
  }
  
  public WindowsPath getParent()
  {
    if (root.length() == path.length()) {
      return null;
    }
    int i = path.lastIndexOf('\\');
    if (i < root.length()) {
      return getRoot();
    }
    return new WindowsPath(getFileSystem(), type, root, path.substring(0, i));
  }
  
  public WindowsPath getRoot()
  {
    if (root.length() == 0) {
      return null;
    }
    return new WindowsPath(getFileSystem(), type, root, root);
  }
  
  WindowsPathType type()
  {
    return type;
  }
  
  boolean isUnc()
  {
    return type == WindowsPathType.UNC;
  }
  
  boolean needsSlashWhenResolving()
  {
    if (path.endsWith("\\")) {
      return false;
    }
    return path.length() > root.length();
  }
  
  public boolean isAbsolute()
  {
    return (type == WindowsPathType.ABSOLUTE) || (type == WindowsPathType.UNC);
  }
  
  static WindowsPath toWindowsPath(Path paramPath)
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    if (!(paramPath instanceof WindowsPath)) {
      throw new ProviderMismatchException();
    }
    return (WindowsPath)paramPath;
  }
  
  public WindowsPath relativize(Path paramPath)
  {
    WindowsPath localWindowsPath = toWindowsPath(paramPath);
    if (equals(localWindowsPath)) {
      return emptyPath();
    }
    if (type != type) {
      throw new IllegalArgumentException("'other' is different type of Path");
    }
    if (!root.equalsIgnoreCase(root)) {
      throw new IllegalArgumentException("'other' has different root");
    }
    int i = getNameCount();
    int j = localWindowsPath.getNameCount();
    int k = i > j ? j : i;
    for (int m = 0; (m < k) && (getName(m).equals(localWindowsPath.getName(m))); m++) {}
    StringBuilder localStringBuilder = new StringBuilder();
    for (int n = m; n < i; n++) {
      localStringBuilder.append("..\\");
    }
    for (n = m; n < j; n++)
    {
      localStringBuilder.append(localWindowsPath.getName(n).toString());
      localStringBuilder.append("\\");
    }
    localStringBuilder.setLength(localStringBuilder.length() - 1);
    return createFromNormalizedPath(getFileSystem(), localStringBuilder.toString());
  }
  
  public Path normalize()
  {
    int i = getNameCount();
    if ((i == 0) || (isEmpty())) {
      return this;
    }
    boolean[] arrayOfBoolean = new boolean[i];
    int j = i;
    int k;
    do
    {
      k = j;
      int m = -1;
      for (n = 0; n < i; n++) {
        if (arrayOfBoolean[n] == 0)
        {
          String str = elementAsString(n);
          if (str.length() > 2)
          {
            m = n;
          }
          else if (str.length() == 1)
          {
            if (str.charAt(0) == '.')
            {
              arrayOfBoolean[n] = true;
              j--;
            }
            else
            {
              m = n;
            }
          }
          else if ((str.charAt(0) != '.') || (str.charAt(1) != '.'))
          {
            m = n;
          }
          else if (m >= 0)
          {
            arrayOfBoolean[m] = true;
            arrayOfBoolean[n] = true;
            j -= 2;
            m = -1;
          }
          else if ((isAbsolute()) || (type == WindowsPathType.DIRECTORY_RELATIVE))
          {
            int i1 = 0;
            for (int i2 = 0; i2 < n; i2++) {
              if (arrayOfBoolean[i2] == 0)
              {
                i1 = 1;
                break;
              }
            }
            if (i1 == 0)
            {
              arrayOfBoolean[n] = true;
              j--;
            }
          }
        }
      }
    } while (k > j);
    if (j == i) {
      return this;
    }
    if (j == 0) {
      return root.length() == 0 ? emptyPath() : getRoot();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (root != null) {
      localStringBuilder.append(root);
    }
    for (int n = 0; n < i; n++) {
      if (arrayOfBoolean[n] == 0)
      {
        localStringBuilder.append(getName(n));
        localStringBuilder.append("\\");
      }
    }
    localStringBuilder.setLength(localStringBuilder.length() - 1);
    return createFromNormalizedPath(getFileSystem(), localStringBuilder.toString());
  }
  
  public WindowsPath resolve(Path paramPath)
  {
    WindowsPath localWindowsPath = toWindowsPath(paramPath);
    if (localWindowsPath.isEmpty()) {
      return this;
    }
    if (localWindowsPath.isAbsolute()) {
      return localWindowsPath;
    }
    String str1;
    switch (type)
    {
    case RELATIVE: 
      if ((path.endsWith("\\")) || (root.length() == path.length())) {
        str1 = path + path;
      } else {
        str1 = path + "\\" + path;
      }
      return new WindowsPath(getFileSystem(), type, root, str1);
    case DIRECTORY_RELATIVE: 
      if (root.endsWith("\\")) {
        str1 = root + path.substring(1);
      } else {
        str1 = root + path;
      }
      return createFromNormalizedPath(getFileSystem(), str1);
    case DRIVE_RELATIVE: 
      if (!root.endsWith("\\")) {
        return localWindowsPath;
      }
      str1 = root.substring(0, root.length() - 1);
      if (!str1.equalsIgnoreCase(root)) {
        return localWindowsPath;
      }
      String str2 = path.substring(root.length());
      String str3;
      if (path.endsWith("\\")) {
        str3 = path + str2;
      } else {
        str3 = path + "\\" + str2;
      }
      return createFromNormalizedPath(getFileSystem(), str3);
    }
    throw new AssertionError();
  }
  
  private void initOffsets()
  {
    if (offsets == null)
    {
      ArrayList localArrayList = new ArrayList();
      if (isEmpty())
      {
        localArrayList.add(Integer.valueOf(0));
      }
      else
      {
        int i = root.length();
        int j = root.length();
        while (j < path.length()) {
          if (path.charAt(j) != '\\')
          {
            j++;
          }
          else
          {
            localArrayList.add(Integer.valueOf(i));
            j++;
            i = j;
          }
        }
        if (i != j) {
          localArrayList.add(Integer.valueOf(i));
        }
      }
      synchronized (this)
      {
        if (offsets == null) {
          offsets = ((Integer[])localArrayList.toArray(new Integer[localArrayList.size()]));
        }
      }
    }
  }
  
  public int getNameCount()
  {
    initOffsets();
    return offsets.length;
  }
  
  private String elementAsString(int paramInt)
  {
    initOffsets();
    if (paramInt == offsets.length - 1) {
      return path.substring(offsets[paramInt].intValue());
    }
    return path.substring(offsets[paramInt].intValue(), offsets[(paramInt + 1)].intValue() - 1);
  }
  
  public WindowsPath getName(int paramInt)
  {
    initOffsets();
    if ((paramInt < 0) || (paramInt >= offsets.length)) {
      throw new IllegalArgumentException();
    }
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", elementAsString(paramInt));
  }
  
  public WindowsPath subpath(int paramInt1, int paramInt2)
  {
    initOffsets();
    if (paramInt1 < 0) {
      throw new IllegalArgumentException();
    }
    if (paramInt1 >= offsets.length) {
      throw new IllegalArgumentException();
    }
    if (paramInt2 > offsets.length) {
      throw new IllegalArgumentException();
    }
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    Integer[] arrayOfInteger = new Integer[paramInt2 - paramInt1];
    for (int i = paramInt1; i < paramInt2; i++)
    {
      arrayOfInteger[(i - paramInt1)] = Integer.valueOf(localStringBuilder.length());
      localStringBuilder.append(elementAsString(i));
      if (i != paramInt2 - 1) {
        localStringBuilder.append("\\");
      }
    }
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", localStringBuilder.toString());
  }
  
  public boolean startsWith(Path paramPath)
  {
    if (!(Objects.requireNonNull(paramPath) instanceof WindowsPath)) {
      return false;
    }
    WindowsPath localWindowsPath = (WindowsPath)paramPath;
    if (!root.equalsIgnoreCase(root)) {
      return false;
    }
    if (localWindowsPath.isEmpty()) {
      return isEmpty();
    }
    int i = getNameCount();
    int j = localWindowsPath.getNameCount();
    if (j <= i)
    {
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        String str1 = elementAsString(j);
        String str2 = localWindowsPath.elementAsString(j);
        if (!str1.equalsIgnoreCase(str2)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public boolean endsWith(Path paramPath)
  {
    if (!(Objects.requireNonNull(paramPath) instanceof WindowsPath)) {
      return false;
    }
    WindowsPath localWindowsPath = (WindowsPath)paramPath;
    if (path.length() > path.length()) {
      return false;
    }
    if (localWindowsPath.isEmpty()) {
      return isEmpty();
    }
    int i = getNameCount();
    int j = localWindowsPath.getNameCount();
    if (j > i) {
      return false;
    }
    if (root.length() > 0)
    {
      if (j < i) {
        return false;
      }
      if (!root.equalsIgnoreCase(root)) {
        return false;
      }
    }
    int k = i - j;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      String str1 = elementAsString(k + j);
      String str2 = localWindowsPath.elementAsString(j);
      if (!str1.equalsIgnoreCase(str2)) {
        return false;
      }
    }
    return true;
  }
  
  public int compareTo(Path paramPath)
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    String str1 = path;
    String str2 = path;
    int i = str1.length();
    int j = str2.length();
    int k = Math.min(i, j);
    for (int m = 0; m < k; m++)
    {
      char c1 = str1.charAt(m);
      char c2 = str2.charAt(m);
      if (c1 != c2)
      {
        c1 = Character.toUpperCase(c1);
        c2 = Character.toUpperCase(c2);
        if (c1 != c2) {
          return c1 - c2;
        }
      }
    }
    return i - j;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof WindowsPath))) {
      return compareTo((Path)paramObject) == 0;
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = hash;
    if (i == 0)
    {
      for (int j = 0; j < path.length(); j++) {
        i = 31 * i + Character.toUpperCase(path.charAt(j));
      }
      hash = i;
    }
    return i;
  }
  
  public String toString()
  {
    return path;
  }
  
  long openForReadAttributeAccess(boolean paramBoolean)
    throws WindowsException
  {
    int i = 33554432;
    if ((!paramBoolean) && (getFileSystem().supportsLinks())) {
      i |= 0x200000;
    }
    return WindowsNativeDispatcher.CreateFile(getPathForWin32Calls(), 128, 7, 0L, 3, i);
  }
  
  void checkRead()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(getPathForPermissionCheck());
    }
  }
  
  void checkWrite()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(getPathForPermissionCheck());
    }
  }
  
  void checkDelete()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkDelete(getPathForPermissionCheck());
    }
  }
  
  public URI toUri()
  {
    return WindowsUriSupport.toUri(this);
  }
  
  public WindowsPath toAbsolutePath()
  {
    if (isAbsolute()) {
      return this;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertyAccess("user.dir");
    }
    try
    {
      return createFromNormalizedPath(getFileSystem(), getAbsolutePath());
    }
    catch (WindowsException localWindowsException)
    {
      throw new IOError(new IOException(localWindowsException.getMessage()));
    }
  }
  
  public WindowsPath toRealPath(LinkOption... paramVarArgs)
    throws IOException
  {
    checkRead();
    String str = WindowsLinkSupport.getRealPath(this, Util.followLinks(paramVarArgs));
    return createFromNormalizedPath(getFileSystem(), str);
  }
  
  public WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs)
    throws IOException
  {
    if (paramWatchService == null) {
      throw new NullPointerException();
    }
    if (!(paramWatchService instanceof WindowsWatchService)) {
      throw new ProviderMismatchException();
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      int i = 0;
      int j = paramVarArgs.length;
      if (j > 0)
      {
        paramVarArgs = (WatchEvent.Modifier[])Arrays.copyOf(paramVarArgs, j);
        int k = 0;
        while (k < j) {
          if (paramVarArgs[(k++)] == ExtendedWatchEventModifier.FILE_TREE) {
            i = 1;
          }
        }
      }
      String str = getPathForPermissionCheck();
      localSecurityManager.checkRead(str);
      if (i != 0) {
        localSecurityManager.checkRead(str + "\\-");
      }
    }
    return ((WindowsWatchService)paramWatchService).register(this, paramArrayOfKind, paramVarArgs);
  }
  
  private static class WindowsPathWithAttributes
    extends WindowsPath
    implements BasicFileAttributesHolder
  {
    final WeakReference<BasicFileAttributes> ref;
    
    WindowsPathWithAttributes(WindowsFileSystem paramWindowsFileSystem, WindowsPathType paramWindowsPathType, String paramString1, String paramString2, BasicFileAttributes paramBasicFileAttributes)
    {
      super(paramWindowsPathType, paramString1, paramString2, null);
      ref = new WeakReference(paramBasicFileAttributes);
    }
    
    public BasicFileAttributes get()
    {
      return (BasicFileAttributes)ref.get();
    }
    
    public void invalidate()
    {
      ref.clear();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */