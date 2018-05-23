package sun.rmi.log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class ReliableLog
{
  public static final int PreferredMajorVersion = 0;
  public static final int PreferredMinorVersion = 2;
  private boolean Debug = false;
  private static String snapshotPrefix = "Snapshot.";
  private static String logfilePrefix = "Logfile.";
  private static String versionFile = "Version_Number";
  private static String newVersionFile = "New_Version_Number";
  private static int intBytes = 4;
  private static long diskPageSize = 512L;
  private File dir;
  private int version = 0;
  private String logName = null;
  private LogFile log = null;
  private long snapshotBytes = 0L;
  private long logBytes = 0L;
  private int logEntries = 0;
  private long lastSnapshot = 0L;
  private long lastLog = 0L;
  private LogHandler handler;
  private final byte[] intBuf = new byte[4];
  private int majorFormatVersion = 0;
  private int minorFormatVersion = 0;
  private static final Constructor<? extends LogFile> logClassConstructor = getLogClassConstructor();
  
  public ReliableLog(String paramString, LogHandler paramLogHandler, boolean paramBoolean)
    throws IOException
  {
    dir = new File(paramString);
    if (((!dir.exists()) || (!dir.isDirectory())) && (!dir.mkdir())) {
      throw new IOException("could not create directory for log: " + paramString);
    }
    handler = paramLogHandler;
    lastSnapshot = 0L;
    lastLog = 0L;
    getVersion();
    if (version == 0) {
      try
      {
        snapshot(paramLogHandler.initialSnapshot());
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      catch (Exception localException)
      {
        throw new IOException("initial snapshot failed with exception: " + localException);
      }
    }
  }
  
  public ReliableLog(String paramString, LogHandler paramLogHandler)
    throws IOException
  {
    this(paramString, paramLogHandler, false);
  }
  
  public synchronized Object recover()
    throws IOException
  {
    if (Debug) {
      System.err.println("log.debug: recover()");
    }
    if (version == 0) {
      return null;
    }
    String str = versionName(snapshotPrefix);
    File localFile = new File(str);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile));
    if (Debug) {
      System.err.println("log.debug: recovering from " + str);
    }
    try
    {
      Object localObject1;
      try
      {
        localObject1 = handler.recover(localBufferedInputStream);
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      return recoverUpdates(localObject1);
    }
    catch (Exception localException)
    {
      if (Debug) {
        System.err.println("log.debug: recovery failed: " + localException);
      }
      throw new IOException("log recover failed with exception: " + localException);
      snapshotBytes = localFile.length();
    }
    finally
    {
      localBufferedInputStream.close();
    }
  }
  
  public synchronized void update(Object paramObject)
    throws IOException
  {
    update(paramObject, true);
  }
  
  public synchronized void update(Object paramObject, boolean paramBoolean)
    throws IOException
  {
    if (log == null) {
      throw new IOException("log is inaccessible, it may have been corrupted or closed");
    }
    long l1 = log.getFilePointer();
    boolean bool = log.checkSpansBoundary(l1);
    writeInt(log, bool ? Integer.MIN_VALUE : 0);
    try
    {
      handler.writeUpdate(new LogOutputStream(log), paramObject);
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (Exception localException)
    {
      throw ((IOException)new IOException("write update failed").initCause(localException));
    }
    log.sync();
    long l2 = log.getFilePointer();
    int i = (int)(l2 - l1 - intBytes);
    log.seek(l1);
    if (bool)
    {
      writeInt(log, i | 0x80000000);
      log.sync();
      log.seek(l1);
      log.writeByte(i >> 24);
      log.sync();
    }
    else
    {
      writeInt(log, i);
      log.sync();
    }
    log.seek(l2);
    logBytes = l2;
    lastLog = System.currentTimeMillis();
    logEntries += 1;
  }
  
  private static Constructor<? extends LogFile> getLogClassConstructor()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.class"));
    if (str != null) {
      try
      {
        ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
        {
          public ClassLoader run()
          {
            return ClassLoader.getSystemClassLoader();
          }
        });
        Class localClass = localClassLoader.loadClass(str).asSubclass(LogFile.class);
        return localClass.getConstructor(new Class[] { String.class, String.class });
      }
      catch (Exception localException)
      {
        System.err.println("Exception occurred:");
        localException.printStackTrace();
      }
    }
    return null;
  }
  
  public synchronized void snapshot(Object paramObject)
    throws IOException
  {
    int i = version;
    incrVersion();
    String str = versionName(snapshotPrefix);
    File localFile = new File(str);
    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
    try
    {
      try
      {
        handler.snapshot(localFileOutputStream, paramObject);
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      openLogFile(true);
    }
    catch (Exception localException)
    {
      throw new IOException("snapshot failed", localException);
      lastSnapshot = System.currentTimeMillis();
    }
    finally
    {
      localFileOutputStream.close();
      snapshotBytes = localFile.length();
    }
    writeVersionFile(true);
    commitToNewVersion();
    deleteSnapshot(i);
    deleteLogFile(i);
  }
  
  /* Error */
  public synchronized void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 390	sun/rmi/log/ReliableLog:log	Lsun/rmi/log/ReliableLog$LogFile;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 390	sun/rmi/log/ReliableLog:log	Lsun/rmi/log/ReliableLog$LogFile;
    //   12: invokevirtual 463	sun/rmi/log/ReliableLog$LogFile:close	()V
    //   15: aload_0
    //   16: aconst_null
    //   17: putfield 390	sun/rmi/log/ReliableLog:log	Lsun/rmi/log/ReliableLog$LogFile;
    //   20: goto +11 -> 31
    //   23: astore_1
    //   24: aload_0
    //   25: aconst_null
    //   26: putfield 390	sun/rmi/log/ReliableLog:log	Lsun/rmi/log/ReliableLog$LogFile;
    //   29: aload_1
    //   30: athrow
    //   31: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	ReliableLog
    //   23	7	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	15	23	finally
  }
  
  public long snapshotSize()
  {
    return snapshotBytes;
  }
  
  public long logSize()
  {
    return logBytes;
  }
  
  private void writeInt(DataOutput paramDataOutput, int paramInt)
    throws IOException
  {
    intBuf[0] = ((byte)(paramInt >> 24));
    intBuf[1] = ((byte)(paramInt >> 16));
    intBuf[2] = ((byte)(paramInt >> 8));
    intBuf[3] = ((byte)paramInt);
    paramDataOutput.write(intBuf);
  }
  
  private String fName(String paramString)
  {
    return dir.getPath() + File.separator + paramString;
  }
  
  private String versionName(String paramString)
  {
    return versionName(paramString, 0);
  }
  
  private String versionName(String paramString, int paramInt)
  {
    paramInt = paramInt == 0 ? version : paramInt;
    return fName(paramString) + String.valueOf(paramInt);
  }
  
  private void incrVersion()
  {
    do
    {
      version += 1;
    } while (version == 0);
  }
  
  private void deleteFile(String paramString)
    throws IOException
  {
    File localFile = new File(paramString);
    if (!localFile.delete()) {
      throw new IOException("couldn't remove file: " + paramString);
    }
  }
  
  private void deleteNewVersionFile()
    throws IOException
  {
    deleteFile(fName(newVersionFile));
  }
  
  private void deleteSnapshot(int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return;
    }
    deleteFile(versionName(snapshotPrefix, paramInt));
  }
  
  private void deleteLogFile(int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return;
    }
    deleteFile(versionName(logfilePrefix, paramInt));
  }
  
  private void openLogFile(boolean paramBoolean)
    throws IOException
  {
    try
    {
      close();
    }
    catch (IOException localIOException) {}
    logName = versionName(logfilePrefix);
    try
    {
      log = (logClassConstructor == null ? new LogFile(logName, "rw") : (LogFile)logClassConstructor.newInstance(new Object[] { logName, "rw" }));
    }
    catch (Exception localException)
    {
      throw ((IOException)new IOException("unable to construct LogFile instance").initCause(localException));
    }
    if (paramBoolean) {
      initializeLogFile();
    }
  }
  
  private void initializeLogFile()
    throws IOException
  {
    log.setLength(0L);
    majorFormatVersion = 0;
    writeInt(log, 0);
    minorFormatVersion = 2;
    writeInt(log, 2);
    logBytes = (intBytes * 2);
    logEntries = 0;
  }
  
  private void writeVersionFile(boolean paramBoolean)
    throws IOException
  {
    String str;
    if (paramBoolean) {
      str = newVersionFile;
    } else {
      str = versionFile;
    }
    FileOutputStream localFileOutputStream = new FileOutputStream(fName(str));
    Object localObject1 = null;
    try
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(localFileOutputStream);
      Object localObject2 = null;
      try
      {
        writeInt(localDataOutputStream, version);
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally {}
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileOutputStream.close();
          }
          catch (Throwable localThrowable6)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable6);
          }
        } else {
          localFileOutputStream.close();
        }
      }
    }
  }
  
  private void createFirstVersion()
    throws IOException
  {
    version = 0;
    writeVersionFile(false);
  }
  
  private void commitToNewVersion()
    throws IOException
  {
    writeVersionFile(false);
    deleteNewVersionFile();
  }
  
  private int readVersion(String paramString)
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(paramString));
    Object localObject1 = null;
    try
    {
      int i = localDataInputStream.readInt();
      return i;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localDataInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localDataInputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localDataInputStream.close();
        }
      }
    }
  }
  
  private void getVersion()
    throws IOException
  {
    try
    {
      version = readVersion(fName(newVersionFile));
      commitToNewVersion();
    }
    catch (IOException localIOException1)
    {
      try
      {
        deleteNewVersionFile();
      }
      catch (IOException localIOException2) {}
      try
      {
        version = readVersion(fName(versionFile));
      }
      catch (IOException localIOException3)
      {
        createFirstVersion();
      }
    }
  }
  
  private Object recoverUpdates(Object paramObject)
    throws IOException
  {
    logBytes = 0L;
    logEntries = 0;
    if (version == 0) {
      return paramObject;
    }
    String str = versionName(logfilePrefix);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(str));
    DataInputStream localDataInputStream = new DataInputStream(localBufferedInputStream);
    if (Debug) {
      System.err.println("log.debug: reading updates from " + str);
    }
    try
    {
      majorFormatVersion = localDataInputStream.readInt();
      logBytes += intBytes;
      minorFormatVersion = localDataInputStream.readInt();
      logBytes += intBytes;
    }
    catch (EOFException localEOFException1)
    {
      openLogFile(true);
      localBufferedInputStream = null;
    }
    if (majorFormatVersion != 0)
    {
      if (Debug) {
        System.err.println("log.debug: major version mismatch: " + majorFormatVersion + "." + minorFormatVersion);
      }
      throw new IOException("Log file " + logName + " has a version " + majorFormatVersion + "." + minorFormatVersion + " format, and this implementation  understands only version " + 0 + "." + 2);
    }
    try
    {
      while (localBufferedInputStream != null)
      {
        int i = 0;
        try
        {
          i = localDataInputStream.readInt();
        }
        catch (EOFException localEOFException2)
        {
          if (Debug) {
            System.err.println("log.debug: log was sync'd cleanly");
          }
          break;
        }
        if (i <= 0)
        {
          if (!Debug) {
            break;
          }
          System.err.println("log.debug: last update incomplete, updateLen = 0x" + Integer.toHexString(i));
          break;
        }
        if (localBufferedInputStream.available() < i)
        {
          if (!Debug) {
            break;
          }
          System.err.println("log.debug: log was truncated");
          break;
        }
        if (Debug) {
          System.err.println("log.debug: rdUpdate size " + i);
        }
        try
        {
          paramObject = handler.readUpdate(new LogInputStream(localBufferedInputStream, i), paramObject);
        }
        catch (IOException localIOException)
        {
          throw localIOException;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          throw new IOException("read update failed with exception: " + localException);
        }
        logBytes += intBytes + i;
        logEntries += 1;
      }
    }
    finally
    {
      if (localBufferedInputStream != null) {
        localBufferedInputStream.close();
      }
    }
    if (Debug) {
      System.err.println("log.debug: recovered updates: " + logEntries);
    }
    openLogFile(false);
    if (log == null) {
      throw new IOException("rmid's log is inaccessible, it may have been corrupted or closed");
    }
    log.seek(logBytes);
    log.setLength(logBytes);
    return paramObject;
  }
  
  public static class LogFile
    extends RandomAccessFile
  {
    private final FileDescriptor fd = getFD();
    
    public LogFile(String paramString1, String paramString2)
      throws FileNotFoundException, IOException
    {
      super(paramString2);
    }
    
    protected void sync()
      throws IOException
    {
      fd.sync();
    }
    
    protected boolean checkSpansBoundary(long paramLong)
    {
      return paramLong % 512L > 508L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\log\ReliableLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */