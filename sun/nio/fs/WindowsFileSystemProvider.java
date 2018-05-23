package sun.nio.fs;

import java.io.FilePermission;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.Permission;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import sun.misc.Unsafe;
import sun.nio.ch.ThreadPool;

public class WindowsFileSystemProvider
  extends AbstractFileSystemProvider
{
  private static final Unsafe unsafe = ;
  private static final String USER_DIR = "user.dir";
  private final WindowsFileSystem theFileSystem = new WindowsFileSystem(this, System.getProperty("user.dir"));
  
  public WindowsFileSystemProvider() {}
  
  public String getScheme()
  {
    return "file";
  }
  
  private void checkUri(URI paramURI)
  {
    if (!paramURI.getScheme().equalsIgnoreCase(getScheme())) {
      throw new IllegalArgumentException("URI does not match this provider");
    }
    if (paramURI.getAuthority() != null) {
      throw new IllegalArgumentException("Authority component present");
    }
    if (paramURI.getPath() == null) {
      throw new IllegalArgumentException("Path component is undefined");
    }
    if (!paramURI.getPath().equals("/")) {
      throw new IllegalArgumentException("Path component should be '/'");
    }
    if (paramURI.getQuery() != null) {
      throw new IllegalArgumentException("Query component present");
    }
    if (paramURI.getFragment() != null) {
      throw new IllegalArgumentException("Fragment component present");
    }
  }
  
  public FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap)
    throws IOException
  {
    checkUri(paramURI);
    throw new FileSystemAlreadyExistsException();
  }
  
  public final FileSystem getFileSystem(URI paramURI)
  {
    checkUri(paramURI);
    return theFileSystem;
  }
  
  public Path getPath(URI paramURI)
  {
    return WindowsUriSupport.fromUri(theFileSystem, paramURI);
  }
  
  public FileChannel newFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    if (!(paramPath instanceof WindowsPath)) {
      throw new ProviderMismatchException();
    }
    WindowsPath localWindowsPath = (WindowsPath)paramPath;
    WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try
    {
      FileChannel localFileChannel1 = WindowsChannelFactory.newFileChannel(localWindowsPath.getPathForWin32Calls(), localWindowsPath.getPathForPermissionCheck(), paramSet, localWindowsSecurityDescriptor.address());
      return localFileChannel1;
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(localWindowsPath);
      FileChannel localFileChannel2 = null;
      return localFileChannel2;
    }
    finally
    {
      if (localWindowsSecurityDescriptor != null) {
        localWindowsSecurityDescriptor.release();
      }
    }
  }
  
  public AsynchronousFileChannel newAsynchronousFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService paramExecutorService, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    if (!(paramPath instanceof WindowsPath)) {
      throw new ProviderMismatchException();
    }
    WindowsPath localWindowsPath = (WindowsPath)paramPath;
    ThreadPool localThreadPool = paramExecutorService == null ? null : ThreadPool.wrap(paramExecutorService, 0);
    WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try
    {
      AsynchronousFileChannel localAsynchronousFileChannel1 = WindowsChannelFactory.newAsynchronousFileChannel(localWindowsPath.getPathForWin32Calls(), localWindowsPath.getPathForPermissionCheck(), paramSet, localWindowsSecurityDescriptor.address(), localThreadPool);
      return localAsynchronousFileChannel1;
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(localWindowsPath);
      AsynchronousFileChannel localAsynchronousFileChannel2 = null;
      return localAsynchronousFileChannel2;
    }
    finally
    {
      if (localWindowsSecurityDescriptor != null) {
        localWindowsSecurityDescriptor.release();
      }
    }
  }
  
  public <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption... paramVarArgs)
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    if (paramClass == null) {
      throw new NullPointerException();
    }
    boolean bool = Util.followLinks(paramVarArgs);
    if (paramClass == BasicFileAttributeView.class) {
      return WindowsFileAttributeViews.createBasicView(localWindowsPath, bool);
    }
    if (paramClass == DosFileAttributeView.class) {
      return WindowsFileAttributeViews.createDosView(localWindowsPath, bool);
    }
    if (paramClass == AclFileAttributeView.class) {
      return new WindowsAclFileAttributeView(localWindowsPath, bool);
    }
    if (paramClass == FileOwnerAttributeView.class) {
      return new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(localWindowsPath, bool));
    }
    if (paramClass == UserDefinedFileAttributeView.class) {
      return new WindowsUserDefinedFileAttributeView(localWindowsPath, bool);
    }
    return (FileAttributeView)null;
  }
  
  public <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption... paramVarArgs)
    throws IOException
  {
    Class localClass;
    if (paramClass == BasicFileAttributes.class)
    {
      localClass = BasicFileAttributeView.class;
    }
    else if (paramClass == DosFileAttributes.class)
    {
      localClass = DosFileAttributeView.class;
    }
    else
    {
      if (paramClass == null) {
        throw new NullPointerException();
      }
      throw new UnsupportedOperationException();
    }
    return ((BasicFileAttributeView)getFileAttributeView(paramPath, localClass, paramVarArgs)).readAttributes();
  }
  
  public DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption... paramVarArgs)
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    boolean bool = Util.followLinks(paramVarArgs);
    if (paramString.equals("basic")) {
      return WindowsFileAttributeViews.createBasicView(localWindowsPath, bool);
    }
    if (paramString.equals("dos")) {
      return WindowsFileAttributeViews.createDosView(localWindowsPath, bool);
    }
    if (paramString.equals("acl")) {
      return new WindowsAclFileAttributeView(localWindowsPath, bool);
    }
    if (paramString.equals("owner")) {
      return new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(localWindowsPath, bool));
    }
    if (paramString.equals("user")) {
      return new WindowsUserDefinedFileAttributeView(localWindowsPath, bool);
    }
    return null;
  }
  
  public SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try
    {
      FileChannel localFileChannel = WindowsChannelFactory.newFileChannel(localWindowsPath.getPathForWin32Calls(), localWindowsPath.getPathForPermissionCheck(), paramSet, localWindowsSecurityDescriptor.address());
      return localFileChannel;
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(localWindowsPath);
      SeekableByteChannel localSeekableByteChannel = null;
      return localSeekableByteChannel;
    }
    finally
    {
      localWindowsSecurityDescriptor.release();
    }
  }
  
  boolean implDelete(Path paramPath, boolean paramBoolean)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    localWindowsPath.checkDelete();
    WindowsFileAttributes localWindowsFileAttributes = null;
    try
    {
      localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath, false);
      if ((localWindowsFileAttributes.isDirectory()) || (localWindowsFileAttributes.isDirectoryLink())) {
        WindowsNativeDispatcher.RemoveDirectory(localWindowsPath.getPathForWin32Calls());
      } else {
        WindowsNativeDispatcher.DeleteFile(localWindowsPath.getPathForWin32Calls());
      }
      return true;
    }
    catch (WindowsException localWindowsException)
    {
      if ((!paramBoolean) && ((localWindowsException.lastError() == 2) || (localWindowsException.lastError() == 3))) {
        return false;
      }
      if ((localWindowsFileAttributes != null) && (localWindowsFileAttributes.isDirectory()) && ((localWindowsException.lastError() == 145) || (localWindowsException.lastError() == 183))) {
        throw new DirectoryNotEmptyException(localWindowsPath.getPathForExceptionMessage());
      }
      localWindowsException.rethrowAsIOException(localWindowsPath);
    }
    return false;
  }
  
  public void copy(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
    throws IOException
  {
    WindowsFileCopy.copy(WindowsPath.toWindowsPath(paramPath1), WindowsPath.toWindowsPath(paramPath2), paramVarArgs);
  }
  
  public void move(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
    throws IOException
  {
    WindowsFileCopy.move(WindowsPath.toWindowsPath(paramPath1), WindowsPath.toWindowsPath(paramPath2), paramVarArgs);
  }
  
  private static boolean hasDesiredAccess(WindowsPath paramWindowsPath, int paramInt)
    throws IOException
  {
    boolean bool = false;
    String str = WindowsLinkSupport.getFinalPath(paramWindowsPath, true);
    NativeBuffer localNativeBuffer = WindowsAclFileAttributeView.getFileSecurity(str, 7);
    try
    {
      bool = WindowsSecurity.checkAccessMask(localNativeBuffer.address(), paramInt, 1179785, 1179926, 1179808, 2032127);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(paramWindowsPath);
    }
    finally
    {
      localNativeBuffer.release();
    }
    return bool;
  }
  
  private void checkReadAccess(WindowsPath paramWindowsPath)
    throws IOException
  {
    try
    {
      Set localSet = Collections.emptySet();
      FileChannel localFileChannel = WindowsChannelFactory.newFileChannel(paramWindowsPath.getPathForWin32Calls(), paramWindowsPath.getPathForPermissionCheck(), localSet, 0L);
      localFileChannel.close();
    }
    catch (WindowsException localWindowsException)
    {
      try
      {
        new WindowsDirectoryStream(paramWindowsPath, null).close();
      }
      catch (IOException localIOException)
      {
        localWindowsException.rethrowAsIOException(paramWindowsPath);
      }
    }
  }
  
  public void checkAccess(Path paramPath, AccessMode... paramVarArgs)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    int i = 0;
    int j = 0;
    int k = 0;
    for (AccessMode localAccessMode : paramVarArgs) {
      switch (localAccessMode)
      {
      case READ: 
        i = 1;
        break;
      case WRITE: 
        j = 1;
        break;
      case EXECUTE: 
        k = 1;
        break;
      default: 
        throw new AssertionError("Should not get here");
      }
    }
    if ((j == 0) && (k == 0))
    {
      checkReadAccess(localWindowsPath);
      return;
    }
    int m = 0;
    if (i != 0)
    {
      localWindowsPath.checkRead();
      m |= 0x1;
    }
    if (j != 0)
    {
      localWindowsPath.checkWrite();
      m |= 0x2;
    }
    Object localObject;
    if (k != 0)
    {
      localObject = System.getSecurityManager();
      if (localObject != null) {
        ((SecurityManager)localObject).checkExec(localWindowsPath.getPathForPermissionCheck());
      }
      m |= 0x20;
    }
    if (!hasDesiredAccess(localWindowsPath, m)) {
      throw new AccessDeniedException(localWindowsPath.getPathForExceptionMessage(), null, "Permissions does not allow requested access");
    }
    if (j != 0)
    {
      try
      {
        localObject = WindowsFileAttributes.get(localWindowsPath, true);
        if ((!((WindowsFileAttributes)localObject).isDirectory()) && (((WindowsFileAttributes)localObject).isReadOnly())) {
          throw new AccessDeniedException(localWindowsPath.getPathForExceptionMessage(), null, "DOS readonly attribute is set");
        }
      }
      catch (WindowsException localWindowsException)
      {
        localWindowsException.rethrowAsIOException(localWindowsPath);
      }
      if (WindowsFileStore.create(localWindowsPath).isReadOnly()) {
        throw new AccessDeniedException(localWindowsPath.getPathForExceptionMessage(), null, "Read-only file system");
      }
    }
  }
  
  /* Error */
  public boolean isSameFile(Path paramPath1, Path paramPath2)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 539	sun/nio/fs/WindowsPath:toWindowsPath	(Ljava/nio/file/Path;)Lsun/nio/fs/WindowsPath;
    //   4: astore_3
    //   5: aload_3
    //   6: aload_2
    //   7: invokevirtual 529	sun/nio/fs/WindowsPath:equals	(Ljava/lang/Object;)Z
    //   10: ifeq +5 -> 15
    //   13: iconst_1
    //   14: ireturn
    //   15: aload_2
    //   16: ifnonnull +11 -> 27
    //   19: new 245	java/lang/NullPointerException
    //   22: dup
    //   23: invokespecial 457	java/lang/NullPointerException:<init>	()V
    //   26: athrow
    //   27: aload_2
    //   28: instanceof 290
    //   31: ifne +5 -> 36
    //   34: iconst_0
    //   35: ireturn
    //   36: aload_2
    //   37: checkcast 290	sun/nio/fs/WindowsPath
    //   40: astore 4
    //   42: aload_3
    //   43: invokevirtual 526	sun/nio/fs/WindowsPath:checkRead	()V
    //   46: aload 4
    //   48: invokevirtual 526	sun/nio/fs/WindowsPath:checkRead	()V
    //   51: lconst_0
    //   52: lstore 5
    //   54: aload_3
    //   55: iconst_1
    //   56: invokevirtual 528	sun/nio/fs/WindowsPath:openForReadAttributeAccess	(Z)J
    //   59: lstore 5
    //   61: goto +11 -> 72
    //   64: astore 7
    //   66: aload 7
    //   68: aload_3
    //   69: invokevirtual 495	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
    //   72: aconst_null
    //   73: astore 7
    //   75: lload 5
    //   77: invokestatic 503	sun/nio/fs/WindowsFileAttributes:readAttributes	(J)Lsun/nio/fs/WindowsFileAttributes;
    //   80: astore 7
    //   82: goto +11 -> 93
    //   85: astore 8
    //   87: aload 8
    //   89: aload_3
    //   90: invokevirtual 495	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
    //   93: lconst_0
    //   94: lstore 8
    //   96: aload 4
    //   98: iconst_1
    //   99: invokevirtual 528	sun/nio/fs/WindowsPath:openForReadAttributeAccess	(Z)J
    //   102: lstore 8
    //   104: goto +12 -> 116
    //   107: astore 10
    //   109: aload 10
    //   111: aload 4
    //   113: invokevirtual 495	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
    //   116: aconst_null
    //   117: astore 10
    //   119: lload 8
    //   121: invokestatic 503	sun/nio/fs/WindowsFileAttributes:readAttributes	(J)Lsun/nio/fs/WindowsFileAttributes;
    //   124: astore 10
    //   126: goto +12 -> 138
    //   129: astore 11
    //   131: aload 11
    //   133: aload 4
    //   135: invokevirtual 495	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
    //   138: aload 7
    //   140: aload 10
    //   142: invokestatic 504	sun/nio/fs/WindowsFileAttributes:isSameFile	(Lsun/nio/fs/WindowsFileAttributes;Lsun/nio/fs/WindowsFileAttributes;)Z
    //   145: istore 11
    //   147: lload 8
    //   149: invokestatic 519	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
    //   152: lload 5
    //   154: invokestatic 519	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
    //   157: iload 11
    //   159: ireturn
    //   160: astore 12
    //   162: lload 8
    //   164: invokestatic 519	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
    //   167: aload 12
    //   169: athrow
    //   170: astore 13
    //   172: lload 5
    //   174: invokestatic 519	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
    //   177: aload 13
    //   179: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	this	WindowsFileSystemProvider
    //   0	180	1	paramPath1	Path
    //   0	180	2	paramPath2	Path
    //   4	86	3	localWindowsPath1	WindowsPath
    //   40	94	4	localWindowsPath2	WindowsPath
    //   52	121	5	l1	long
    //   64	3	7	localWindowsException1	WindowsException
    //   73	66	7	localWindowsFileAttributes1	WindowsFileAttributes
    //   85	3	8	localWindowsException2	WindowsException
    //   94	69	8	l2	long
    //   107	3	10	localWindowsException3	WindowsException
    //   117	24	10	localWindowsFileAttributes2	WindowsFileAttributes
    //   129	3	11	localWindowsException4	WindowsException
    //   145	13	11	bool	boolean
    //   160	8	12	localObject1	Object
    //   170	8	13	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   54	61	64	sun/nio/fs/WindowsException
    //   75	82	85	sun/nio/fs/WindowsException
    //   96	104	107	sun/nio/fs/WindowsException
    //   119	126	129	sun/nio/fs/WindowsException
    //   116	147	160	finally
    //   160	162	160	finally
    //   72	152	170	finally
    //   160	172	170	finally
  }
  
  public boolean isHidden(Path paramPath)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    localWindowsPath.checkRead();
    WindowsFileAttributes localWindowsFileAttributes = null;
    try
    {
      localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath, true);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(localWindowsPath);
    }
    if (localWindowsFileAttributes.isDirectory()) {
      return false;
    }
    return localWindowsFileAttributes.isHidden();
  }
  
  public FileStore getFileStore(Path paramPath)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
      localWindowsPath.checkRead();
    }
    return WindowsFileStore.create(localWindowsPath);
  }
  
  public void createDirectory(Path paramPath, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    localWindowsPath.checkWrite();
    WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try
    {
      WindowsNativeDispatcher.CreateDirectory(localWindowsPath.getPathForWin32Calls(), localWindowsSecurityDescriptor.address());
    }
    catch (WindowsException localWindowsException1)
    {
      if (localWindowsException1.lastError() == 5) {
        try
        {
          if (WindowsFileAttributes.get(localWindowsPath, false).isDirectory()) {
            throw new FileAlreadyExistsException(localWindowsPath.toString());
          }
        }
        catch (WindowsException localWindowsException2) {}
      }
      localWindowsException1.rethrowAsIOException(localWindowsPath);
    }
    finally
    {
      localWindowsSecurityDescriptor.release();
    }
  }
  
  public DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    localWindowsPath.checkRead();
    if (paramFilter == null) {
      throw new NullPointerException();
    }
    return new WindowsDirectoryStream(localWindowsPath, paramFilter);
  }
  
  public void createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    WindowsPath localWindowsPath1 = WindowsPath.toWindowsPath(paramPath1);
    WindowsPath localWindowsPath2 = WindowsPath.toWindowsPath(paramPath2);
    if (!localWindowsPath1.getFileSystem().supportsLinks()) {
      throw new UnsupportedOperationException("Symbolic links not supported on this operating system");
    }
    if (paramVarArgs.length > 0)
    {
      WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
      throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new LinkPermission("symbolic"));
      localWindowsPath1.checkWrite();
    }
    if (localWindowsPath2.type() == WindowsPathType.DRIVE_RELATIVE) {
      throw new IOException("Cannot create symbolic link to working directory relative target");
    }
    WindowsPath localWindowsPath3;
    if (localWindowsPath2.type() == WindowsPathType.RELATIVE)
    {
      WindowsPath localWindowsPath4 = localWindowsPath1.getParent();
      localWindowsPath3 = localWindowsPath4 == null ? localWindowsPath2 : localWindowsPath4.resolve(localWindowsPath2);
    }
    else
    {
      localWindowsPath3 = localWindowsPath1.resolve(localWindowsPath2);
    }
    int i = 0;
    try
    {
      WindowsFileAttributes localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath3, false);
      if ((localWindowsFileAttributes.isDirectory()) || (localWindowsFileAttributes.isDirectoryLink())) {
        i |= 0x1;
      }
    }
    catch (WindowsException localWindowsException1) {}
    try
    {
      WindowsNativeDispatcher.CreateSymbolicLink(localWindowsPath1.getPathForWin32Calls(), WindowsPath.addPrefixIfNeeded(localWindowsPath2.toString()), i);
    }
    catch (WindowsException localWindowsException2)
    {
      if (localWindowsException2.lastError() == 4392) {
        localWindowsException2.rethrowAsIOException(localWindowsPath1, localWindowsPath2);
      } else {
        localWindowsException2.rethrowAsIOException(localWindowsPath1);
      }
    }
  }
  
  public void createLink(Path paramPath1, Path paramPath2)
    throws IOException
  {
    WindowsPath localWindowsPath1 = WindowsPath.toWindowsPath(paramPath1);
    WindowsPath localWindowsPath2 = WindowsPath.toWindowsPath(paramPath2);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new LinkPermission("hard"));
      localWindowsPath1.checkWrite();
      localWindowsPath2.checkWrite();
    }
    try
    {
      WindowsNativeDispatcher.CreateHardLink(localWindowsPath1.getPathForWin32Calls(), localWindowsPath2.getPathForWin32Calls());
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(localWindowsPath1, localWindowsPath2);
    }
  }
  
  public Path readSymbolicLink(Path paramPath)
    throws IOException
  {
    WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
    WindowsFileSystem localWindowsFileSystem = localWindowsPath.getFileSystem();
    if (!localWindowsFileSystem.supportsLinks()) {
      throw new UnsupportedOperationException("symbolic links not supported");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject = new FilePermission(localWindowsPath.getPathForPermissionCheck(), "readlink");
      localSecurityManager.checkPermission((Permission)localObject);
    }
    Object localObject = WindowsLinkSupport.readLink(localWindowsPath);
    return WindowsPath.createFromNormalizedPath(localWindowsFileSystem, (String)localObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsFileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */