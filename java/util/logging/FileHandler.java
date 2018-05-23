package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

public class FileHandler
  extends StreamHandler
{
  private MeteredStream meter;
  private boolean append;
  private int limit;
  private int count;
  private String pattern;
  private String lockFileName;
  private FileChannel lockFileChannel;
  private File[] files;
  private static final int MAX_LOCKS = 100;
  private static final Set<String> locks = new HashSet();
  
  private void open(File paramFile, boolean paramBoolean)
    throws IOException
  {
    int i = 0;
    if (paramBoolean) {
      i = (int)paramFile.length();
    }
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile.toString(), paramBoolean);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
    meter = new MeteredStream(localBufferedOutputStream, i);
    setOutputStream(meter);
  }
  
  private void configure()
  {
    LogManager localLogManager = LogManager.getLogManager();
    String str = getClass().getName();
    pattern = localLogManager.getStringProperty(str + ".pattern", "%h/java%u.log");
    limit = localLogManager.getIntProperty(str + ".limit", 0);
    if (limit < 0) {
      limit = 0;
    }
    count = localLogManager.getIntProperty(str + ".count", 1);
    if (count <= 0) {
      count = 1;
    }
    append = localLogManager.getBooleanProperty(str + ".append", false);
    setLevel(localLogManager.getLevelProperty(str + ".level", Level.ALL));
    setFilter(localLogManager.getFilterProperty(str + ".filter", null));
    setFormatter(localLogManager.getFormatterProperty(str + ".formatter", new XMLFormatter()));
    try
    {
      setEncoding(localLogManager.getStringProperty(str + ".encoding", null));
    }
    catch (Exception localException1)
    {
      try
      {
        setEncoding(null);
      }
      catch (Exception localException2) {}
    }
  }
  
  public FileHandler()
    throws IOException, SecurityException
  {
    checkPermission();
    configure();
    openFiles();
  }
  
  public FileHandler(String paramString)
    throws IOException, SecurityException
  {
    if (paramString.length() < 1) {
      throw new IllegalArgumentException();
    }
    checkPermission();
    configure();
    pattern = paramString;
    limit = 0;
    count = 1;
    openFiles();
  }
  
  public FileHandler(String paramString, boolean paramBoolean)
    throws IOException, SecurityException
  {
    if (paramString.length() < 1) {
      throw new IllegalArgumentException();
    }
    checkPermission();
    configure();
    pattern = paramString;
    limit = 0;
    count = 1;
    append = paramBoolean;
    openFiles();
  }
  
  public FileHandler(String paramString, int paramInt1, int paramInt2)
    throws IOException, SecurityException
  {
    if ((paramInt1 < 0) || (paramInt2 < 1) || (paramString.length() < 1)) {
      throw new IllegalArgumentException();
    }
    checkPermission();
    configure();
    pattern = paramString;
    limit = paramInt1;
    count = paramInt2;
    openFiles();
  }
  
  public FileHandler(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, SecurityException
  {
    if ((paramInt1 < 0) || (paramInt2 < 1) || (paramString.length() < 1)) {
      throw new IllegalArgumentException();
    }
    checkPermission();
    configure();
    pattern = paramString;
    limit = paramInt1;
    count = paramInt2;
    append = paramBoolean;
    openFiles();
  }
  
  private boolean isParentWritable(Path paramPath)
  {
    Path localPath = paramPath.getParent();
    if (localPath == null) {
      localPath = paramPath.toAbsolutePath().getParent();
    }
    return (localPath != null) && (Files.isWritable(localPath));
  }
  
  private void openFiles()
    throws IOException
  {
    LogManager localLogManager = LogManager.getLogManager();
    localLogManager.checkPermission();
    if (count < 1) {
      throw new IllegalArgumentException("file count = " + count);
    }
    if (limit < 0) {
      limit = 0;
    }
    InitializationErrorManager localInitializationErrorManager = new InitializationErrorManager(null);
    setErrorManager(localInitializationErrorManager);
    int i = -1;
    for (;;)
    {
      i++;
      if (i > 100) {
        throw new IOException("Couldn't get lock for " + pattern);
      }
      lockFileName = (generate(pattern, 0, i).toString() + ".lck");
      synchronized (locks)
      {
        if (!locks.contains(lockFileName))
        {
          Path localPath = Paths.get(lockFileName, new String[0]);
          FileChannel localFileChannel = null;
          int k = -1;
          int m = 0;
          while ((localFileChannel == null) && (k++ < 1)) {
            try
            {
              localFileChannel = FileChannel.open(localPath, new OpenOption[] { StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE });
              m = 1;
            }
            catch (FileAlreadyExistsException localFileAlreadyExistsException)
            {
              if ((Files.isRegularFile(localPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) && (isParentWritable(localPath))) {
                try
                {
                  localFileChannel = FileChannel.open(localPath, new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.APPEND });
                }
                catch (NoSuchFileException localNoSuchFileException)
                {
                  continue;
                }
                catch (IOException localIOException1)
                {
                  break;
                }
              } else {
                break;
              }
            }
          }
          if (localFileChannel != null)
          {
            lockFileChannel = localFileChannel;
            int n;
            try
            {
              n = lockFileChannel.tryLock() != null ? 1 : 0;
            }
            catch (IOException localIOException2)
            {
              n = m;
            }
            catch (OverlappingFileLockException localOverlappingFileLockException)
            {
              n = 0;
            }
            if (n != 0)
            {
              locks.add(lockFileName);
              break;
            }
            lockFileChannel.close();
          }
        }
      }
    }
    files = new File[count];
    for (int j = 0; j < count; j++) {
      files[j] = generate(pattern, j, i);
    }
    if (append) {
      open(files[0], true);
    } else {
      rotate();
    }
    Exception localException = lastException;
    if (localException != null)
    {
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      if ((localException instanceof SecurityException)) {
        throw ((SecurityException)localException);
      }
      throw new IOException("Exception: " + localException);
    }
    setErrorManager(new ErrorManager());
  }
  
  private File generate(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    File localFile = null;
    String str1 = "";
    int i = 0;
    int j = 0;
    int k = 0;
    while (i < paramString.length())
    {
      char c = paramString.charAt(i);
      i++;
      int m = 0;
      if (i < paramString.length()) {
        m = Character.toLowerCase(paramString.charAt(i));
      }
      if (c == '/')
      {
        if (localFile == null) {
          localFile = new File(str1);
        } else {
          localFile = new File(localFile, str1);
        }
        str1 = "";
      }
      else
      {
        if (c == '%')
        {
          if (m == 116)
          {
            String str2 = System.getProperty("java.io.tmpdir");
            if (str2 == null) {
              str2 = System.getProperty("user.home");
            }
            localFile = new File(str2);
            i++;
            str1 = "";
            continue;
          }
          if (m == 104)
          {
            localFile = new File(System.getProperty("user.home"));
            if (isSetUID()) {
              throw new IOException("can't use %h in set UID program");
            }
            i++;
            str1 = "";
            continue;
          }
          if (m == 103)
          {
            str1 = str1 + paramInt1;
            j = 1;
            i++;
            continue;
          }
          if (m == 117)
          {
            str1 = str1 + paramInt2;
            k = 1;
            i++;
            continue;
          }
          if (m == 37)
          {
            str1 = str1 + "%";
            i++;
            continue;
          }
        }
        str1 = str1 + c;
      }
    }
    if ((count > 1) && (j == 0)) {
      str1 = str1 + "." + paramInt1;
    }
    if ((paramInt2 > 0) && (k == 0)) {
      str1 = str1 + "." + paramInt2;
    }
    if (str1.length() > 0) {
      if (localFile == null) {
        localFile = new File(str1);
      } else {
        localFile = new File(localFile, str1);
      }
    }
    return localFile;
  }
  
  private synchronized void rotate()
  {
    Level localLevel = getLevel();
    setLevel(Level.OFF);
    super.close();
    for (int i = count - 2; i >= 0; i--)
    {
      File localFile1 = files[i];
      File localFile2 = files[(i + 1)];
      if (localFile1.exists())
      {
        if (localFile2.exists()) {
          localFile2.delete();
        }
        localFile1.renameTo(localFile2);
      }
    }
    try
    {
      open(files[0], false);
    }
    catch (IOException localIOException)
    {
      reportError(null, localIOException, 4);
    }
    setLevel(localLevel);
  }
  
  public synchronized void publish(LogRecord paramLogRecord)
  {
    if (!isLoggable(paramLogRecord)) {
      return;
    }
    super.publish(paramLogRecord);
    flush();
    if ((limit > 0) && (meter.written >= limit)) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          FileHandler.this.rotate();
          return null;
        }
      });
    }
  }
  
  public synchronized void close()
    throws SecurityException
  {
    super.close();
    if (lockFileName == null) {
      return;
    }
    try
    {
      lockFileChannel.close();
    }
    catch (Exception localException) {}
    synchronized (locks)
    {
      locks.remove(lockFileName);
    }
    new File(lockFileName).delete();
    lockFileName = null;
    lockFileChannel = null;
  }
  
  private static native boolean isSetUID();
  
  private static class InitializationErrorManager
    extends ErrorManager
  {
    Exception lastException;
    
    private InitializationErrorManager() {}
    
    public void error(String paramString, Exception paramException, int paramInt)
    {
      lastException = paramException;
    }
  }
  
  private class MeteredStream
    extends OutputStream
  {
    final OutputStream out;
    int written;
    
    MeteredStream(OutputStream paramOutputStream, int paramInt)
    {
      out = paramOutputStream;
      written = paramInt;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      out.write(paramInt);
      written += 1;
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      out.write(paramArrayOfByte);
      written += paramArrayOfByte.length;
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      out.write(paramArrayOfByte, paramInt1, paramInt2);
      written += paramInt2;
    }
    
    public void flush()
      throws IOException
    {
      out.flush();
    }
    
    public void close()
      throws IOException
    {
      out.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\FileHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */