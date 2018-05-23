package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

class FileTreeIterator
  implements Iterator<FileTreeWalker.Event>, Closeable
{
  private final FileTreeWalker walker;
  private FileTreeWalker.Event next;
  
  FileTreeIterator(Path paramPath, int paramInt, FileVisitOption... paramVarArgs)
    throws IOException
  {
    walker = new FileTreeWalker(Arrays.asList(paramVarArgs), paramInt);
    next = walker.walk(paramPath);
    assert ((next.type() == FileTreeWalker.EventType.ENTRY) || (next.type() == FileTreeWalker.EventType.START_DIRECTORY));
    IOException localIOException = next.ioeException();
    if (localIOException != null) {
      throw localIOException;
    }
  }
  
  private void fetchNextIfNeeded()
  {
    if (next == null) {
      for (FileTreeWalker.Event localEvent = walker.next(); localEvent != null; localEvent = walker.next())
      {
        IOException localIOException = localEvent.ioeException();
        if (localIOException != null) {
          throw new UncheckedIOException(localIOException);
        }
        if (localEvent.type() != FileTreeWalker.EventType.END_DIRECTORY)
        {
          next = localEvent;
          return;
        }
      }
    }
  }
  
  public boolean hasNext()
  {
    if (!walker.isOpen()) {
      throw new IllegalStateException();
    }
    fetchNextIfNeeded();
    return next != null;
  }
  
  public FileTreeWalker.Event next()
  {
    if (!walker.isOpen()) {
      throw new IllegalStateException();
    }
    fetchNextIfNeeded();
    if (next == null) {
      throw new NoSuchElementException();
    }
    FileTreeWalker.Event localEvent = next;
    next = null;
    return localEvent;
  }
  
  public void close()
  {
    walker.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\FileTreeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */