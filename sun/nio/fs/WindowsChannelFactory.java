package sun.nio.fs;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.FileChannelImpl;
import sun.nio.ch.ThreadPool;
import sun.nio.ch.WindowsAsynchronousFileChannelImpl;

class WindowsChannelFactory
{
  private static final JavaIOFileDescriptorAccess fdAccess = ;
  static final OpenOption OPEN_REPARSE_POINT = new OpenOption() {};
  
  private WindowsChannelFactory() {}
  
  static FileChannel newFileChannel(String paramString1, String paramString2, Set<? extends OpenOption> paramSet, long paramLong)
    throws WindowsException
  {
    Flags localFlags = Flags.toFlags(paramSet);
    if ((!read) && (!write)) {
      if (append) {
        write = true;
      } else {
        read = true;
      }
    }
    if ((read) && (append)) {
      throw new IllegalArgumentException("READ + APPEND not allowed");
    }
    if ((append) && (truncateExisting)) {
      throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
    }
    FileDescriptor localFileDescriptor = open(paramString1, paramString2, localFlags, paramLong);
    return FileChannelImpl.open(localFileDescriptor, paramString1, read, write, append, null);
  }
  
  static AsynchronousFileChannel newAsynchronousFileChannel(String paramString1, String paramString2, Set<? extends OpenOption> paramSet, long paramLong, ThreadPool paramThreadPool)
    throws IOException
  {
    Flags localFlags = Flags.toFlags(paramSet);
    overlapped = true;
    if ((!read) && (!write)) {
      read = true;
    }
    if (append) {
      throw new UnsupportedOperationException("APPEND not allowed");
    }
    FileDescriptor localFileDescriptor;
    try
    {
      localFileDescriptor = open(paramString1, paramString2, localFlags, paramLong);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(paramString1);
      return null;
    }
    try
    {
      return WindowsAsynchronousFileChannelImpl.open(localFileDescriptor, read, write, paramThreadPool);
    }
    catch (IOException localIOException)
    {
      long l = fdAccess.getHandle(localFileDescriptor);
      WindowsNativeDispatcher.CloseHandle(l);
      throw localIOException;
    }
  }
  
  private static FileDescriptor open(String paramString1, String paramString2, Flags paramFlags, long paramLong)
    throws WindowsException
  {
    int i = 0;
    int j = 0;
    if (read) {
      j |= 0x80000000;
    }
    if (write) {
      j |= 0x40000000;
    }
    int k = 0;
    if (shareRead) {
      k |= 0x1;
    }
    if (shareWrite) {
      k |= 0x2;
    }
    if (shareDelete) {
      k |= 0x4;
    }
    int m = 128;
    int n = 3;
    if (write) {
      if (createNew)
      {
        n = 1;
        m |= 0x200000;
      }
      else
      {
        if (create) {
          n = 4;
        }
        if (truncateExisting) {
          if (n == 4) {
            i = 1;
          } else {
            n = 5;
          }
        }
      }
    }
    if ((dsync) || (sync)) {
      m |= 0x80000000;
    }
    if (overlapped) {
      m |= 0x40000000;
    }
    if (deleteOnClose) {
      m |= 0x4000000;
    }
    int i1 = 1;
    if ((n != 1) && ((noFollowLinks) || (openReparsePoint) || (deleteOnClose)))
    {
      if ((noFollowLinks) || (deleteOnClose)) {
        i1 = 0;
      }
      m |= 0x200000;
    }
    if (paramString2 != null)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        if (read) {
          localSecurityManager.checkRead(paramString2);
        }
        if (write) {
          localSecurityManager.checkWrite(paramString2);
        }
        if (deleteOnClose) {
          localSecurityManager.checkDelete(paramString2);
        }
      }
    }
    long l = WindowsNativeDispatcher.CreateFile(paramString1, j, k, paramLong, n, m);
    if (i1 == 0) {
      try
      {
        if (WindowsFileAttributes.readAttributes(l).isSymbolicLink()) {
          throw new WindowsException("File is symbolic link");
        }
      }
      catch (WindowsException localWindowsException1)
      {
        WindowsNativeDispatcher.CloseHandle(l);
        throw localWindowsException1;
      }
    }
    if (i != 0) {
      try
      {
        WindowsNativeDispatcher.SetEndOfFile(l);
      }
      catch (WindowsException localWindowsException2)
      {
        WindowsNativeDispatcher.CloseHandle(l);
        throw localWindowsException2;
      }
    }
    if ((n == 1) && (sparse)) {
      try
      {
        WindowsNativeDispatcher.DeviceIoControlSetSparse(l);
      }
      catch (WindowsException localWindowsException3) {}
    }
    FileDescriptor localFileDescriptor = new FileDescriptor();
    fdAccess.setHandle(localFileDescriptor, l);
    return localFileDescriptor;
  }
  
  private static class Flags
  {
    boolean read;
    boolean write;
    boolean append;
    boolean truncateExisting;
    boolean create;
    boolean createNew;
    boolean deleteOnClose;
    boolean sparse;
    boolean overlapped;
    boolean sync;
    boolean dsync;
    boolean shareRead = true;
    boolean shareWrite = true;
    boolean shareDelete = true;
    boolean noFollowLinks;
    boolean openReparsePoint;
    
    private Flags() {}
    
    static Flags toFlags(Set<? extends OpenOption> paramSet)
    {
      Flags localFlags = new Flags();
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        OpenOption localOpenOption = (OpenOption)localIterator.next();
        if ((localOpenOption instanceof StandardOpenOption))
        {
          switch (WindowsChannelFactory.2.$SwitchMap$java$nio$file$StandardOpenOption[((StandardOpenOption)localOpenOption).ordinal()])
          {
          case 1: 
            read = true;
            break;
          case 2: 
            write = true;
            break;
          case 3: 
            append = true;
            break;
          case 4: 
            truncateExisting = true;
            break;
          case 5: 
            create = true;
            break;
          case 6: 
            createNew = true;
            break;
          case 7: 
            deleteOnClose = true;
            break;
          case 8: 
            sparse = true;
            break;
          case 9: 
            sync = true;
            break;
          case 10: 
            dsync = true;
            break;
          default: 
            throw new UnsupportedOperationException();
          }
        }
        else if ((localOpenOption instanceof ExtendedOpenOption))
        {
          switch (WindowsChannelFactory.2.$SwitchMap$com$sun$nio$file$ExtendedOpenOption[((ExtendedOpenOption)localOpenOption).ordinal()])
          {
          case 1: 
            shareRead = false;
            break;
          case 2: 
            shareWrite = false;
            break;
          case 3: 
            shareDelete = false;
            break;
          default: 
            throw new UnsupportedOperationException();
          }
        }
        else if (localOpenOption == LinkOption.NOFOLLOW_LINKS)
        {
          noFollowLinks = true;
        }
        else if (localOpenOption == WindowsChannelFactory.OPEN_REPARSE_POINT)
        {
          openReparsePoint = true;
        }
        else
        {
          if (localOpenOption == null) {
            throw new NullPointerException();
          }
          throw new UnsupportedOperationException();
        }
      }
      return localFlags;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsChannelFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */