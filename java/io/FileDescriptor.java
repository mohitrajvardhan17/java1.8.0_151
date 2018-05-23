package java.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

public final class FileDescriptor
{
  private int fd = -1;
  private long handle = -1L;
  private Closeable parent;
  private List<Closeable> otherParents;
  private boolean closed;
  public static final FileDescriptor in = standardStream(0);
  public static final FileDescriptor out = standardStream(1);
  public static final FileDescriptor err = standardStream(2);
  
  public FileDescriptor() {}
  
  public boolean valid()
  {
    return (handle != -1L) || (fd != -1);
  }
  
  public native void sync()
    throws SyncFailedException;
  
  private static native void initIDs();
  
  private static native long set(int paramInt);
  
  private static FileDescriptor standardStream(int paramInt)
  {
    FileDescriptor localFileDescriptor = new FileDescriptor();
    handle = set(paramInt);
    return localFileDescriptor;
  }
  
  synchronized void attach(Closeable paramCloseable)
  {
    if (parent == null)
    {
      parent = paramCloseable;
    }
    else if (otherParents == null)
    {
      otherParents = new ArrayList();
      otherParents.add(parent);
      otherParents.add(paramCloseable);
    }
    else
    {
      otherParents.add(paramCloseable);
    }
  }
  
  synchronized void closeAll(Closeable paramCloseable)
    throws IOException
  {
    if (!closed)
    {
      closed = true;
      Object localObject1 = null;
      try
      {
        Closeable localCloseable1 = paramCloseable;
        Object localObject2 = null;
        try
        {
          if (otherParents != null)
          {
            Iterator localIterator = otherParents.iterator();
            while (localIterator.hasNext())
            {
              Closeable localCloseable2 = (Closeable)localIterator.next();
              try
              {
                localCloseable2.close();
              }
              catch (IOException localIOException2)
              {
                if (localObject1 == null) {
                  localObject1 = localIOException2;
                } else {
                  ((IOException)localObject1).addSuppressed(localIOException2);
                }
              }
            }
          }
        }
        catch (Throwable localThrowable2)
        {
          localObject2 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localCloseable1 != null) {
            if (localObject2 != null) {
              try
              {
                localCloseable1.close();
              }
              catch (Throwable localThrowable3)
              {
                ((Throwable)localObject2).addSuppressed(localThrowable3);
              }
            } else {
              localCloseable1.close();
            }
          }
        }
      }
      catch (IOException localIOException1)
      {
        if (localObject1 != null) {
          localIOException1.addSuppressed((Throwable)localObject1);
        }
        localObject1 = localIOException1;
      }
      finally
      {
        if (localObject1 != null) {
          throw ((Throwable)localObject1);
        }
      }
    }
  }
  
  static
  {
    initIDs();
    SharedSecrets.setJavaIOFileDescriptorAccess(new JavaIOFileDescriptorAccess()
    {
      public void set(FileDescriptor paramAnonymousFileDescriptor, int paramAnonymousInt)
      {
        fd = paramAnonymousInt;
      }
      
      public int get(FileDescriptor paramAnonymousFileDescriptor)
      {
        return fd;
      }
      
      public void setHandle(FileDescriptor paramAnonymousFileDescriptor, long paramAnonymousLong)
      {
        handle = paramAnonymousLong;
      }
      
      public long getHandle(FileDescriptor paramAnonymousFileDescriptor)
      {
        return handle;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FileDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */