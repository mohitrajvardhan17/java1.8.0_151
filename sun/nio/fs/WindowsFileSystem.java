package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.action.GetPropertyAction;

class WindowsFileSystem
  extends FileSystem
{
  private final WindowsFileSystemProvider provider;
  private final String defaultDirectory;
  private final String defaultRoot;
  private final boolean supportsLinks;
  private final boolean supportsStreamEnumeration;
  private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "basic", "dos", "acl", "owner", "user" })));
  private static final String GLOB_SYNTAX = "glob";
  private static final String REGEX_SYNTAX = "regex";
  
  WindowsFileSystem(WindowsFileSystemProvider paramWindowsFileSystemProvider, String paramString)
  {
    provider = paramWindowsFileSystemProvider;
    WindowsPathParser.Result localResult = WindowsPathParser.parse(paramString);
    if ((localResult.type() != WindowsPathType.ABSOLUTE) && (localResult.type() != WindowsPathType.UNC)) {
      throw new AssertionError("Default directory is not an absolute path");
    }
    defaultDirectory = localResult.path();
    defaultRoot = localResult.root();
    GetPropertyAction localGetPropertyAction = new GetPropertyAction("os.version");
    String str = (String)AccessController.doPrivileged(localGetPropertyAction);
    String[] arrayOfString = Util.split(str, '.');
    int i = Integer.parseInt(arrayOfString[0]);
    int j = Integer.parseInt(arrayOfString[1]);
    supportsLinks = (i >= 6);
    supportsStreamEnumeration = ((i >= 6) || ((i == 5) && (j >= 2)));
  }
  
  String defaultDirectory()
  {
    return defaultDirectory;
  }
  
  String defaultRoot()
  {
    return defaultRoot;
  }
  
  boolean supportsLinks()
  {
    return supportsLinks;
  }
  
  boolean supportsStreamEnumeration()
  {
    return supportsStreamEnumeration;
  }
  
  public FileSystemProvider provider()
  {
    return provider;
  }
  
  public String getSeparator()
  {
    return "\\";
  }
  
  public boolean isOpen()
  {
    return true;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public void close()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public Iterable<Path> getRootDirectories()
  {
    int i = 0;
    try
    {
      i = WindowsNativeDispatcher.GetLogicalDrives();
    }
    catch (WindowsException localWindowsException)
    {
      throw new AssertionError(localWindowsException.getMessage());
    }
    ArrayList localArrayList = new ArrayList();
    SecurityManager localSecurityManager = System.getSecurityManager();
    for (int j = 0; j <= 25; j++) {
      if ((i & 1 << j) != 0)
      {
        StringBuilder localStringBuilder = new StringBuilder(3);
        localStringBuilder.append((char)(65 + j));
        localStringBuilder.append(":\\");
        String str = localStringBuilder.toString();
        if (localSecurityManager != null) {
          try
          {
            localSecurityManager.checkRead(str);
          }
          catch (SecurityException localSecurityException)
          {
            continue;
          }
        }
        localArrayList.add(WindowsPath.createFromNormalizedPath(this, str));
      }
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public Iterable<FileStore> getFileStores()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
      }
      catch (SecurityException localSecurityException)
      {
        return Collections.emptyList();
      }
    }
    new Iterable()
    {
      public Iterator<FileStore> iterator()
      {
        return new WindowsFileSystem.FileStoreIterator(WindowsFileSystem.this);
      }
    };
  }
  
  public Set<String> supportedFileAttributeViews()
  {
    return supportedFileAttributeViews;
  }
  
  public final Path getPath(String paramString, String... paramVarArgs)
  {
    String str1;
    if (paramVarArgs.length == 0)
    {
      str1 = paramString;
    }
    else
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      for (String str2 : paramVarArgs) {
        if (str2.length() > 0)
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append('\\');
          }
          localStringBuilder.append(str2);
        }
      }
      str1 = localStringBuilder.toString();
    }
    return WindowsPath.parse(this, str1);
  }
  
  public UserPrincipalLookupService getUserPrincipalLookupService()
  {
    return LookupService.instance;
  }
  
  public PathMatcher getPathMatcher(String paramString)
  {
    int i = paramString.indexOf(':');
    if ((i <= 0) || (i == paramString.length())) {
      throw new IllegalArgumentException();
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    String str3;
    if (str1.equals("glob")) {
      str3 = Globs.toWindowsRegexPattern(str2);
    } else if (str1.equals("regex")) {
      str3 = str2;
    } else {
      throw new UnsupportedOperationException("Syntax '" + str1 + "' not recognized");
    }
    final Pattern localPattern = Pattern.compile(str3, 66);
    new PathMatcher()
    {
      public boolean matches(Path paramAnonymousPath)
      {
        return localPattern.matcher(paramAnonymousPath.toString()).matches();
      }
    };
  }
  
  public WatchService newWatchService()
    throws IOException
  {
    return new WindowsWatchService(this);
  }
  
  private class FileStoreIterator
    implements Iterator<FileStore>
  {
    private final Iterator<Path> roots = getRootDirectories().iterator();
    private FileStore next;
    
    FileStoreIterator() {}
    
    private FileStore readNext()
    {
      assert (Thread.holdsLock(this));
      for (;;)
      {
        if (!roots.hasNext()) {
          return null;
        }
        WindowsPath localWindowsPath = (WindowsPath)roots.next();
        try
        {
          localWindowsPath.checkRead();
        }
        catch (SecurityException localSecurityException) {}
        continue;
        try
        {
          WindowsFileStore localWindowsFileStore = WindowsFileStore.create(localWindowsPath.toString(), true);
          if (localWindowsFileStore != null) {
            return localWindowsFileStore;
          }
        }
        catch (IOException localIOException) {}
      }
    }
    
    public synchronized boolean hasNext()
    {
      if (next != null) {
        return true;
      }
      next = readNext();
      return next != null;
    }
    
    public synchronized FileStore next()
    {
      if (next == null) {
        next = readNext();
      }
      if (next == null) {
        throw new NoSuchElementException();
      }
      FileStore localFileStore = next;
      next = null;
      return localFileStore;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class LookupService
  {
    static final UserPrincipalLookupService instance = new UserPrincipalLookupService()
    {
      public UserPrincipal lookupPrincipalByName(String paramAnonymousString)
        throws IOException
      {
        return WindowsUserPrincipals.lookup(paramAnonymousString);
      }
      
      public GroupPrincipal lookupPrincipalByGroupName(String paramAnonymousString)
        throws IOException
      {
        UserPrincipal localUserPrincipal = WindowsUserPrincipals.lookup(paramAnonymousString);
        if (!(localUserPrincipal instanceof GroupPrincipal)) {
          throw new UserPrincipalNotFoundException(paramAnonymousString);
        }
        return (GroupPrincipal)localUserPrincipal;
      }
    };
    
    private LookupService() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */