package sun.nio.fs;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.NoSuchElementException;

class WindowsDirectoryStream
  implements DirectoryStream<Path>
{
  private final WindowsPath dir;
  private final DirectoryStream.Filter<? super Path> filter;
  private final long handle;
  private final String firstName;
  private final NativeBuffer findDataBuffer;
  private final Object closeLock = new Object();
  private boolean isOpen = true;
  private Iterator<Path> iterator;
  
  WindowsDirectoryStream(WindowsPath paramWindowsPath, DirectoryStream.Filter<? super Path> paramFilter)
    throws IOException
  {
    dir = paramWindowsPath;
    filter = paramFilter;
    try
    {
      String str = paramWindowsPath.getPathForWin32Calls();
      int i = str.charAt(str.length() - 1);
      if ((i == 58) || (i == 92)) {
        str = str + "*";
      } else {
        str = str + "\\*";
      }
      WindowsNativeDispatcher.FirstFile localFirstFile = WindowsNativeDispatcher.FindFirstFile(str);
      handle = localFirstFile.handle();
      firstName = localFirstFile.name();
      findDataBuffer = WindowsFileAttributes.getBufferForFindData();
    }
    catch (WindowsException localWindowsException)
    {
      if (localWindowsException.lastError() == 267) {
        throw new NotDirectoryException(paramWindowsPath.getPathForExceptionMessage());
      }
      localWindowsException.rethrowAsIOException(paramWindowsPath);
      throw new AssertionError();
    }
  }
  
  public void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (!isOpen) {
        return;
      }
      isOpen = false;
    }
    findDataBuffer.release();
    try
    {
      WindowsNativeDispatcher.FindClose(handle);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(dir);
    }
  }
  
  public Iterator<Path> iterator()
  {
    if (!isOpen) {
      throw new IllegalStateException("Directory stream is closed");
    }
    synchronized (this)
    {
      if (iterator != null) {
        throw new IllegalStateException("Iterator already obtained");
      }
      iterator = new WindowsDirectoryIterator(firstName);
      return iterator;
    }
  }
  
  private class WindowsDirectoryIterator
    implements Iterator<Path>
  {
    private boolean atEof = false;
    private String first;
    private Path nextEntry;
    private String prefix;
    
    WindowsDirectoryIterator(String paramString)
    {
      first = paramString;
      if (dir.needsSlashWhenResolving()) {
        prefix = (dir.toString() + "\\");
      } else {
        prefix = dir.toString();
      }
    }
    
    private boolean isSelfOrParent(String paramString)
    {
      return (paramString.equals(".")) || (paramString.equals(".."));
    }
    
    private Path acceptEntry(String paramString, BasicFileAttributes paramBasicFileAttributes)
    {
      WindowsPath localWindowsPath = WindowsPath.createFromNormalizedPath(dir.getFileSystem(), prefix + paramString, paramBasicFileAttributes);
      try
      {
        if (filter.accept(localWindowsPath)) {
          return localWindowsPath;
        }
      }
      catch (IOException localIOException)
      {
        throw new DirectoryIteratorException(localIOException);
      }
      return null;
    }
    
    private Path readNextEntry()
    {
      if (first != null)
      {
        nextEntry = (isSelfOrParent(first) ? null : acceptEntry(first, null));
        first = null;
        if (nextEntry != null) {
          return nextEntry;
        }
      }
      for (;;)
      {
        String str = null;
        WindowsFileAttributes localWindowsFileAttributes;
        synchronized (closeLock)
        {
          try
          {
            if (isOpen) {
              str = WindowsNativeDispatcher.FindNextFile(handle, findDataBuffer.address());
            }
          }
          catch (WindowsException localWindowsException)
          {
            IOException localIOException = localWindowsException.asIOException(dir);
            throw new DirectoryIteratorException(localIOException);
          }
          if (str == null)
          {
            atEof = true;
            return null;
          }
          if (isSelfOrParent(str)) {
            continue;
          }
          localWindowsFileAttributes = WindowsFileAttributes.fromFindData(findDataBuffer.address());
        }
        ??? = acceptEntry(str, localWindowsFileAttributes);
        if (??? != null) {
          return (Path)???;
        }
      }
    }
    
    public synchronized boolean hasNext()
    {
      if ((nextEntry == null) && (!atEof)) {
        nextEntry = readNextEntry();
      }
      return nextEntry != null;
    }
    
    public synchronized Path next()
    {
      Path localPath = null;
      if ((nextEntry == null) && (!atEof))
      {
        localPath = readNextEntry();
      }
      else
      {
        localPath = nextEntry;
        nextEntry = null;
      }
      if (localPath == null) {
        throw new NoSuchElementException();
      }
      return localPath;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsDirectoryStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */