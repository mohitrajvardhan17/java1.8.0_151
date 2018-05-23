package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import sun.nio.fs.BasicFileAttributesHolder;

class FileTreeWalker
  implements Closeable
{
  private final boolean followLinks;
  private final LinkOption[] linkOptions;
  private final int maxDepth;
  private final ArrayDeque<DirectoryNode> stack = new ArrayDeque();
  private boolean closed;
  
  FileTreeWalker(Collection<FileVisitOption> paramCollection, int paramInt)
  {
    boolean bool = false;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      FileVisitOption localFileVisitOption = (FileVisitOption)localIterator.next();
      switch (localFileVisitOption)
      {
      case FOLLOW_LINKS: 
        bool = true;
        break;
      default: 
        throw new AssertionError("Should not get here");
      }
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("'maxDepth' is negative");
    }
    followLinks = bool;
    linkOptions = new LinkOption[] { bool ? new LinkOption[0] : LinkOption.NOFOLLOW_LINKS };
    maxDepth = paramInt;
  }
  
  private BasicFileAttributes getAttributes(Path paramPath, boolean paramBoolean)
    throws IOException
  {
    BasicFileAttributes localBasicFileAttributes;
    if ((paramBoolean) && ((paramPath instanceof BasicFileAttributesHolder)) && (System.getSecurityManager() == null))
    {
      localBasicFileAttributes = ((BasicFileAttributesHolder)paramPath).get();
      if ((localBasicFileAttributes != null) && ((!followLinks) || (!localBasicFileAttributes.isSymbolicLink()))) {
        return localBasicFileAttributes;
      }
    }
    try
    {
      localBasicFileAttributes = Files.readAttributes(paramPath, BasicFileAttributes.class, linkOptions);
    }
    catch (IOException localIOException)
    {
      if (!followLinks) {
        throw localIOException;
      }
      localBasicFileAttributes = Files.readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
    }
    return localBasicFileAttributes;
  }
  
  private boolean wouldLoop(Path paramPath, Object paramObject)
  {
    Iterator localIterator = stack.iterator();
    while (localIterator.hasNext())
    {
      DirectoryNode localDirectoryNode = (DirectoryNode)localIterator.next();
      Object localObject = localDirectoryNode.key();
      if ((paramObject != null) && (localObject != null))
      {
        if (paramObject.equals(localObject)) {
          return true;
        }
      }
      else {
        try
        {
          if (Files.isSameFile(paramPath, localDirectoryNode.directory())) {
            return true;
          }
        }
        catch (IOException|SecurityException localIOException) {}
      }
    }
    return false;
  }
  
  private Event visit(Path paramPath, boolean paramBoolean1, boolean paramBoolean2)
  {
    BasicFileAttributes localBasicFileAttributes;
    try
    {
      localBasicFileAttributes = getAttributes(paramPath, paramBoolean2);
    }
    catch (IOException localIOException1)
    {
      return new Event(EventType.ENTRY, paramPath, localIOException1);
    }
    catch (SecurityException localSecurityException1)
    {
      if (paramBoolean1) {
        return null;
      }
      throw localSecurityException1;
    }
    int i = stack.size();
    if ((i >= maxDepth) || (!localBasicFileAttributes.isDirectory())) {
      return new Event(EventType.ENTRY, paramPath, localBasicFileAttributes);
    }
    if ((followLinks) && (wouldLoop(paramPath, localBasicFileAttributes.fileKey()))) {
      return new Event(EventType.ENTRY, paramPath, new FileSystemLoopException(paramPath.toString()));
    }
    DirectoryStream localDirectoryStream = null;
    try
    {
      localDirectoryStream = Files.newDirectoryStream(paramPath);
    }
    catch (IOException localIOException2)
    {
      return new Event(EventType.ENTRY, paramPath, localIOException2);
    }
    catch (SecurityException localSecurityException2)
    {
      if (paramBoolean1) {
        return null;
      }
      throw localSecurityException2;
    }
    stack.push(new DirectoryNode(paramPath, localBasicFileAttributes.fileKey(), localDirectoryStream));
    return new Event(EventType.START_DIRECTORY, paramPath, localBasicFileAttributes);
  }
  
  Event walk(Path paramPath)
  {
    if (closed) {
      throw new IllegalStateException("Closed");
    }
    Event localEvent = visit(paramPath, false, false);
    assert (localEvent != null);
    return localEvent;
  }
  
  Event next()
  {
    DirectoryNode localDirectoryNode = (DirectoryNode)stack.peek();
    if (localDirectoryNode == null) {
      return null;
    }
    Event localEvent;
    do
    {
      Path localPath = null;
      Object localObject = null;
      if (!localDirectoryNode.skipped())
      {
        Iterator localIterator = localDirectoryNode.iterator();
        try
        {
          if (localIterator.hasNext()) {
            localPath = (Path)localIterator.next();
          }
        }
        catch (DirectoryIteratorException localDirectoryIteratorException)
        {
          localObject = localDirectoryIteratorException.getCause();
        }
      }
      if (localPath == null)
      {
        try
        {
          localDirectoryNode.stream().close();
        }
        catch (IOException localIOException)
        {
          if (localObject != null) {
            localObject = localIOException;
          } else {
            ((IOException)localObject).addSuppressed(localIOException);
          }
        }
        stack.pop();
        return new Event(EventType.END_DIRECTORY, localDirectoryNode.directory(), (IOException)localObject);
      }
      localEvent = visit(localPath, true, true);
    } while (localEvent == null);
    return localEvent;
  }
  
  void pop()
  {
    if (!stack.isEmpty())
    {
      DirectoryNode localDirectoryNode = (DirectoryNode)stack.pop();
      try
      {
        localDirectoryNode.stream().close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  void skipRemainingSiblings()
  {
    if (!stack.isEmpty()) {
      ((DirectoryNode)stack.peek()).skip();
    }
  }
  
  boolean isOpen()
  {
    return !closed;
  }
  
  public void close()
  {
    if (!closed)
    {
      while (!stack.isEmpty()) {
        pop();
      }
      closed = true;
    }
  }
  
  private static class DirectoryNode
  {
    private final Path dir;
    private final Object key;
    private final DirectoryStream<Path> stream;
    private final Iterator<Path> iterator;
    private boolean skipped;
    
    DirectoryNode(Path paramPath, Object paramObject, DirectoryStream<Path> paramDirectoryStream)
    {
      dir = paramPath;
      key = paramObject;
      stream = paramDirectoryStream;
      iterator = paramDirectoryStream.iterator();
    }
    
    Path directory()
    {
      return dir;
    }
    
    Object key()
    {
      return key;
    }
    
    DirectoryStream<Path> stream()
    {
      return stream;
    }
    
    Iterator<Path> iterator()
    {
      return iterator;
    }
    
    void skip()
    {
      skipped = true;
    }
    
    boolean skipped()
    {
      return skipped;
    }
  }
  
  static class Event
  {
    private final FileTreeWalker.EventType type;
    private final Path file;
    private final BasicFileAttributes attrs;
    private final IOException ioe;
    
    private Event(FileTreeWalker.EventType paramEventType, Path paramPath, BasicFileAttributes paramBasicFileAttributes, IOException paramIOException)
    {
      type = paramEventType;
      file = paramPath;
      attrs = paramBasicFileAttributes;
      ioe = paramIOException;
    }
    
    Event(FileTreeWalker.EventType paramEventType, Path paramPath, BasicFileAttributes paramBasicFileAttributes)
    {
      this(paramEventType, paramPath, paramBasicFileAttributes, null);
    }
    
    Event(FileTreeWalker.EventType paramEventType, Path paramPath, IOException paramIOException)
    {
      this(paramEventType, paramPath, null, paramIOException);
    }
    
    FileTreeWalker.EventType type()
    {
      return type;
    }
    
    Path file()
    {
      return file;
    }
    
    BasicFileAttributes attributes()
    {
      return attrs;
    }
    
    IOException ioeException()
    {
      return ioe;
    }
  }
  
  static enum EventType
  {
    START_DIRECTORY,  END_DIRECTORY,  ENTRY;
    
    private EventType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\FileTreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */