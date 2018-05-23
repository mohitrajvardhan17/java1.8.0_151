package sun.nio.fs;

import com.sun.nio.file.ExtendedCopyOption;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

class WindowsFileCopy
{
  private WindowsFileCopy() {}
  
  static void copy(final WindowsPath paramWindowsPath1, final WindowsPath paramWindowsPath2, CopyOption... paramVarArgs)
    throws IOException
  {
    int i = 0;
    int j = 0;
    boolean bool = true;
    int k = 0;
    for (Object localObject2 : paramVarArgs) {
      if (localObject2 == StandardCopyOption.REPLACE_EXISTING)
      {
        i = 1;
      }
      else if (localObject2 == LinkOption.NOFOLLOW_LINKS)
      {
        bool = false;
      }
      else if (localObject2 == StandardCopyOption.COPY_ATTRIBUTES)
      {
        j = 1;
      }
      else if (localObject2 == ExtendedCopyOption.INTERRUPTIBLE)
      {
        k = 1;
      }
      else
      {
        if (localObject2 == null) {
          throw new NullPointerException();
        }
        throw new UnsupportedOperationException("Unsupported copy option");
      }
    }
    ??? = System.getSecurityManager();
    if (??? != null)
    {
      paramWindowsPath1.checkRead();
      paramWindowsPath2.checkWrite();
    }
    WindowsFileAttributes localWindowsFileAttributes1 = null;
    WindowsFileAttributes localWindowsFileAttributes2 = null;
    long l1 = 0L;
    try
    {
      l1 = paramWindowsPath1.openForReadAttributeAccess(bool);
    }
    catch (WindowsException localWindowsException1)
    {
      localWindowsException1.rethrowAsIOException(paramWindowsPath1);
    }
    try
    {
      try
      {
        localWindowsFileAttributes1 = WindowsFileAttributes.readAttributes(l1);
      }
      catch (WindowsException localWindowsException2)
      {
        localWindowsException2.rethrowAsIOException(paramWindowsPath1);
      }
      long l2 = 0L;
      try
      {
        l2 = paramWindowsPath2.openForReadAttributeAccess(false);
        try
        {
          localWindowsFileAttributes2 = WindowsFileAttributes.readAttributes(l2);
          if (WindowsFileAttributes.isSameFile(localWindowsFileAttributes1, localWindowsFileAttributes2)) {
            return;
          }
          if (i == 0) {
            throw new FileAlreadyExistsException(paramWindowsPath2.getPathForExceptionMessage());
          }
        }
        finally {}
      }
      catch (WindowsException localWindowsException3) {}
    }
    finally
    {
      WindowsNativeDispatcher.CloseHandle(l1);
    }
    if ((??? != null) && (localWindowsFileAttributes1.isSymbolicLink())) {
      ((SecurityManager)???).checkPermission(new LinkPermission("symbolic"));
    }
    String str1 = asWin32Path(paramWindowsPath1);
    final String str2 = asWin32Path(paramWindowsPath2);
    if (localWindowsFileAttributes2 != null) {
      try
      {
        if ((localWindowsFileAttributes2.isDirectory()) || (localWindowsFileAttributes2.isDirectoryLink())) {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        } else {
          WindowsNativeDispatcher.DeleteFile(str2);
        }
      }
      catch (WindowsException localWindowsException4)
      {
        if ((localWindowsFileAttributes2.isDirectory()) && ((localWindowsException4.lastError() == 145) || (localWindowsException4.lastError() == 183))) {
          throw new DirectoryNotEmptyException(paramWindowsPath2.getPathForExceptionMessage());
        }
        localWindowsException4.rethrowAsIOException(paramWindowsPath2);
      }
    }
    if ((!localWindowsFileAttributes1.isDirectory()) && (!localWindowsFileAttributes1.isDirectoryLink()))
    {
      final int i1 = (paramWindowsPath1.getFileSystem().supportsLinks()) && (!bool) ? 2048 : 0;
      if (k != 0)
      {
        Cancellable local1 = new Cancellable()
        {
          public int cancelValue()
          {
            return 1;
          }
          
          public void implRun()
            throws IOException
          {
            try
            {
              WindowsNativeDispatcher.CopyFileEx(val$sourcePath, str2, i1, addressToPollForCancel());
            }
            catch (WindowsException localWindowsException)
            {
              localWindowsException.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
            }
          }
        };
        try
        {
          Cancellable.runInterruptibly(local1);
        }
        catch (ExecutionException localExecutionException)
        {
          Throwable localThrowable = localExecutionException.getCause();
          if ((localThrowable instanceof IOException)) {
            throw ((IOException)localThrowable);
          }
          throw new IOException(localThrowable);
        }
      }
      else
      {
        try
        {
          WindowsNativeDispatcher.CopyFileEx(str1, str2, i1, 0L);
        }
        catch (WindowsException localWindowsException6)
        {
          localWindowsException6.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
        }
      }
      if (j != 0) {
        try
        {
          copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, bool);
        }
        catch (IOException localIOException1) {}
      }
      return;
    }
    try
    {
      if (localWindowsFileAttributes1.isDirectory())
      {
        WindowsNativeDispatcher.CreateDirectory(str2, 0L);
      }
      else
      {
        String str3 = WindowsLinkSupport.readLink(paramWindowsPath1);
        int i2 = 1;
        WindowsNativeDispatcher.CreateSymbolicLink(str2, WindowsPath.addPrefixIfNeeded(str3), i2);
      }
    }
    catch (WindowsException localWindowsException5)
    {
      localWindowsException5.rethrowAsIOException(paramWindowsPath2);
    }
    if (j != 0)
    {
      WindowsFileAttributeViews.Dos localDos = WindowsFileAttributeViews.createDosView(paramWindowsPath2, false);
      try
      {
        localDos.setAttributes(localWindowsFileAttributes1);
      }
      catch (IOException localIOException2)
      {
        if (localWindowsFileAttributes1.isDirectory()) {
          try
          {
            WindowsNativeDispatcher.RemoveDirectory(str2);
          }
          catch (WindowsException localWindowsException7) {}
        }
      }
      try
      {
        copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, bool);
      }
      catch (IOException localIOException3) {}
    }
  }
  
  static void move(WindowsPath paramWindowsPath1, WindowsPath paramWindowsPath2, CopyOption... paramVarArgs)
    throws IOException
  {
    int i = 0;
    int j = 0;
    for (Object localObject2 : paramVarArgs) {
      if (localObject2 == StandardCopyOption.ATOMIC_MOVE)
      {
        i = 1;
      }
      else if (localObject2 == StandardCopyOption.REPLACE_EXISTING)
      {
        j = 1;
      }
      else if (localObject2 != LinkOption.NOFOLLOW_LINKS)
      {
        if (localObject2 == null) {
          throw new NullPointerException();
        }
        throw new UnsupportedOperationException("Unsupported copy option");
      }
    }
    ??? = System.getSecurityManager();
    if (??? != null)
    {
      paramWindowsPath1.checkWrite();
      paramWindowsPath2.checkWrite();
    }
    String str1 = asWin32Path(paramWindowsPath1);
    String str2 = asWin32Path(paramWindowsPath2);
    if (i != 0)
    {
      try
      {
        WindowsNativeDispatcher.MoveFileEx(str1, str2, 1);
      }
      catch (WindowsException localWindowsException1)
      {
        if (localWindowsException1.lastError() == 17) {
          throw new AtomicMoveNotSupportedException(paramWindowsPath1.getPathForExceptionMessage(), paramWindowsPath2.getPathForExceptionMessage(), localWindowsException1.errorString());
        }
        localWindowsException1.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
      }
      return;
    }
    WindowsFileAttributes localWindowsFileAttributes1 = null;
    WindowsFileAttributes localWindowsFileAttributes2 = null;
    long l1 = 0L;
    try
    {
      l1 = paramWindowsPath1.openForReadAttributeAccess(false);
    }
    catch (WindowsException localWindowsException2)
    {
      localWindowsException2.rethrowAsIOException(paramWindowsPath1);
    }
    try
    {
      try
      {
        localWindowsFileAttributes1 = WindowsFileAttributes.readAttributes(l1);
      }
      catch (WindowsException localWindowsException3)
      {
        localWindowsException3.rethrowAsIOException(paramWindowsPath1);
      }
      long l2 = 0L;
      try
      {
        l2 = paramWindowsPath2.openForReadAttributeAccess(false);
        try
        {
          localWindowsFileAttributes2 = WindowsFileAttributes.readAttributes(l2);
          if (WindowsFileAttributes.isSameFile(localWindowsFileAttributes1, localWindowsFileAttributes2)) {
            return;
          }
          if (j == 0) {
            throw new FileAlreadyExistsException(paramWindowsPath2.getPathForExceptionMessage());
          }
        }
        finally {}
      }
      catch (WindowsException localWindowsException9) {}
    }
    finally
    {
      WindowsNativeDispatcher.CloseHandle(l1);
    }
    if (localWindowsFileAttributes2 != null) {
      try
      {
        if ((localWindowsFileAttributes2.isDirectory()) || (localWindowsFileAttributes2.isDirectoryLink())) {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        } else {
          WindowsNativeDispatcher.DeleteFile(str2);
        }
      }
      catch (WindowsException localWindowsException4)
      {
        if ((localWindowsFileAttributes2.isDirectory()) && ((localWindowsException4.lastError() == 145) || (localWindowsException4.lastError() == 183))) {
          throw new DirectoryNotEmptyException(paramWindowsPath2.getPathForExceptionMessage());
        }
        localWindowsException4.rethrowAsIOException(paramWindowsPath2);
      }
    }
    try
    {
      WindowsNativeDispatcher.MoveFileEx(str1, str2, 0);
      return;
    }
    catch (WindowsException localWindowsException5)
    {
      if (localWindowsException5.lastError() != 17) {
        localWindowsException5.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
      }
      if ((!localWindowsFileAttributes1.isDirectory()) && (!localWindowsFileAttributes1.isDirectoryLink()))
      {
        try
        {
          WindowsNativeDispatcher.MoveFileEx(str1, str2, 2);
        }
        catch (WindowsException localWindowsException6)
        {
          localWindowsException6.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
        }
        try
        {
          copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, false);
        }
        catch (IOException localIOException1) {}
        return;
      }
      assert ((localWindowsFileAttributes1.isDirectory()) || (localWindowsFileAttributes1.isDirectoryLink()));
      try
      {
        if (localWindowsFileAttributes1.isDirectory())
        {
          WindowsNativeDispatcher.CreateDirectory(str2, 0L);
        }
        else
        {
          String str3 = WindowsLinkSupport.readLink(paramWindowsPath1);
          WindowsNativeDispatcher.CreateSymbolicLink(str2, WindowsPath.addPrefixIfNeeded(str3), 1);
        }
      }
      catch (WindowsException localWindowsException7)
      {
        localWindowsException7.rethrowAsIOException(paramWindowsPath2);
      }
      WindowsFileAttributeViews.Dos localDos = WindowsFileAttributeViews.createDosView(paramWindowsPath2, false);
      try
      {
        localDos.setAttributes(localWindowsFileAttributes1);
      }
      catch (IOException localIOException2)
      {
        try
        {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        }
        catch (WindowsException localWindowsException10) {}
        throw localIOException2;
      }
      try
      {
        copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, false);
      }
      catch (IOException localIOException3) {}
      try
      {
        WindowsNativeDispatcher.RemoveDirectory(str1);
      }
      catch (WindowsException localWindowsException8)
      {
        try
        {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        }
        catch (WindowsException localWindowsException11) {}
        if ((localWindowsException8.lastError() == 145) || (localWindowsException8.lastError() == 183)) {
          throw new DirectoryNotEmptyException(paramWindowsPath2.getPathForExceptionMessage());
        }
        localWindowsException8.rethrowAsIOException(paramWindowsPath1);
      }
    }
  }
  
  private static String asWin32Path(WindowsPath paramWindowsPath)
    throws IOException
  {
    try
    {
      return paramWindowsPath.getPathForWin32Calls();
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(paramWindowsPath);
    }
    return null;
  }
  
  private static void copySecurityAttributes(WindowsPath paramWindowsPath1, WindowsPath paramWindowsPath2, boolean paramBoolean)
    throws IOException
  {
    String str = WindowsLinkSupport.getFinalPath(paramWindowsPath1, paramBoolean);
    WindowsSecurity.Privilege localPrivilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
    try
    {
      int i = 7;
      NativeBuffer localNativeBuffer = WindowsAclFileAttributeView.getFileSecurity(str, i);
      try
      {
        try
        {
          WindowsNativeDispatcher.SetFileSecurity(paramWindowsPath2.getPathForWin32Calls(), i, localNativeBuffer.address());
        }
        catch (WindowsException localWindowsException)
        {
          localWindowsException.rethrowAsIOException(paramWindowsPath2);
        }
      }
      finally
      {
        localNativeBuffer.release();
      }
    }
    finally
    {
      localPrivilege.drop();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsFileCopy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */