package java.nio.file.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class FileSystemProvider
{
  private static final Object lock = new Object();
  private static volatile List<FileSystemProvider> installedProviders;
  private static boolean loadingProviders = false;
  
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("fileSystemProvider"));
    }
    return null;
  }
  
  private FileSystemProvider(Void paramVoid) {}
  
  protected FileSystemProvider()
  {
    this(checkPermission());
  }
  
  private static List<FileSystemProvider> loadInstalledProviders()
  {
    ArrayList localArrayList = new ArrayList();
    ServiceLoader localServiceLoader = ServiceLoader.load(FileSystemProvider.class, ClassLoader.getSystemClassLoader());
    Iterator localIterator1 = localServiceLoader.iterator();
    while (localIterator1.hasNext())
    {
      FileSystemProvider localFileSystemProvider1 = (FileSystemProvider)localIterator1.next();
      String str = localFileSystemProvider1.getScheme();
      if (!str.equalsIgnoreCase("file"))
      {
        int i = 0;
        Iterator localIterator2 = localArrayList.iterator();
        while (localIterator2.hasNext())
        {
          FileSystemProvider localFileSystemProvider2 = (FileSystemProvider)localIterator2.next();
          if (localFileSystemProvider2.getScheme().equalsIgnoreCase(str))
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          localArrayList.add(localFileSystemProvider1);
        }
      }
    }
    return localArrayList;
  }
  
  public static List<FileSystemProvider> installedProviders()
  {
    if (installedProviders == null)
    {
      FileSystemProvider localFileSystemProvider = FileSystems.getDefault().provider();
      synchronized (lock)
      {
        if (installedProviders == null)
        {
          if (loadingProviders) {
            throw new Error("Circular loading of installed providers detected");
          }
          loadingProviders = true;
          List localList = (List)AccessController.doPrivileged(new PrivilegedAction()
          {
            public List<FileSystemProvider> run()
            {
              return FileSystemProvider.access$000();
            }
          });
          localList.add(0, localFileSystemProvider);
          installedProviders = Collections.unmodifiableList(localList);
        }
      }
    }
    return installedProviders;
  }
  
  public abstract String getScheme();
  
  public abstract FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap)
    throws IOException;
  
  public abstract FileSystem getFileSystem(URI paramURI);
  
  public abstract Path getPath(URI paramURI);
  
  public FileSystem newFileSystem(Path paramPath, Map<String, ?> paramMap)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public InputStream newInputStream(Path paramPath, OpenOption... paramVarArgs)
    throws IOException
  {
    if (paramVarArgs.length > 0) {
      for (OpenOption localOpenOption : paramVarArgs) {
        if ((localOpenOption == StandardOpenOption.APPEND) || (localOpenOption == StandardOpenOption.WRITE)) {
          throw new UnsupportedOperationException("'" + localOpenOption + "' not allowed");
        }
      }
    }
    return Channels.newInputStream(Files.newByteChannel(paramPath, paramVarArgs));
  }
  
  public OutputStream newOutputStream(Path paramPath, OpenOption... paramVarArgs)
    throws IOException
  {
    int i = paramVarArgs.length;
    HashSet localHashSet = new HashSet(i + 3);
    if (i == 0)
    {
      localHashSet.add(StandardOpenOption.CREATE);
      localHashSet.add(StandardOpenOption.TRUNCATE_EXISTING);
    }
    else
    {
      for (OpenOption localOpenOption : paramVarArgs)
      {
        if (localOpenOption == StandardOpenOption.READ) {
          throw new IllegalArgumentException("READ not allowed");
        }
        localHashSet.add(localOpenOption);
      }
    }
    localHashSet.add(StandardOpenOption.WRITE);
    return Channels.newOutputStream(newByteChannel(paramPath, localHashSet, new FileAttribute[0]));
  }
  
  public FileChannel newFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public AsynchronousFileChannel newAsynchronousFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService paramExecutorService, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs)
    throws IOException;
  
  public abstract DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter)
    throws IOException;
  
  public abstract void createDirectory(Path paramPath, FileAttribute<?>... paramVarArgs)
    throws IOException;
  
  public void createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public void createLink(Path paramPath1, Path paramPath2)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract void delete(Path paramPath)
    throws IOException;
  
  public boolean deleteIfExists(Path paramPath)
    throws IOException
  {
    try
    {
      delete(paramPath);
      return true;
    }
    catch (NoSuchFileException localNoSuchFileException) {}
    return false;
  }
  
  public Path readSymbolicLink(Path paramPath)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract void copy(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
    throws IOException;
  
  public abstract void move(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
    throws IOException;
  
  public abstract boolean isSameFile(Path paramPath1, Path paramPath2)
    throws IOException;
  
  public abstract boolean isHidden(Path paramPath)
    throws IOException;
  
  public abstract FileStore getFileStore(Path paramPath)
    throws IOException;
  
  public abstract void checkAccess(Path paramPath, AccessMode... paramVarArgs)
    throws IOException;
  
  public abstract <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption... paramVarArgs);
  
  public abstract <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption... paramVarArgs)
    throws IOException;
  
  public abstract Map<String, Object> readAttributes(Path paramPath, String paramString, LinkOption... paramVarArgs)
    throws IOException;
  
  public abstract void setAttribute(Path paramPath, String paramString, Object paramObject, LinkOption... paramVarArgs)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\spi\FileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */