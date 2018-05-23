package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WeakDataFile
  extends WeakReference<DataFile>
{
  private static final Logger LOGGER = Logger.getLogger(WeakDataFile.class.getName());
  private static ReferenceQueue<DataFile> refQueue = new ReferenceQueue();
  private static List<WeakDataFile> refList = new ArrayList();
  private final File file;
  private final RandomAccessFile raf;
  private static boolean hasCleanUpExecutor = false;
  
  WeakDataFile(DataFile paramDataFile, File paramFile)
  {
    super(paramDataFile, refQueue);
    refList.add(this);
    file = paramFile;
    try
    {
      raf = new RandomAccessFile(paramFile, "rw");
    }
    catch (IOException localIOException)
    {
      throw new MIMEParsingException(localIOException);
    }
    if (!hasCleanUpExecutor) {
      drainRefQueueBounded();
    }
  }
  
  synchronized void read(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      raf.seek(paramLong);
      raf.readFully(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new MIMEParsingException(localIOException);
    }
  }
  
  synchronized long writeTo(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      raf.seek(paramLong);
      raf.write(paramArrayOfByte, paramInt1, paramInt2);
      return raf.getFilePointer();
    }
    catch (IOException localIOException)
    {
      throw new MIMEParsingException(localIOException);
    }
  }
  
  void close()
  {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Deleting file = {0}", file.getName());
    }
    refList.remove(this);
    try
    {
      raf.close();
      boolean bool = file.delete();
      if ((!bool) && (LOGGER.isLoggable(Level.INFO))) {
        LOGGER.log(Level.INFO, "File {0} was not deleted", file.getAbsolutePath());
      }
    }
    catch (IOException localIOException)
    {
      throw new MIMEParsingException(localIOException);
    }
  }
  
  void renameTo(File paramFile)
  {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Moving file={0} to={1}", new Object[] { file, paramFile });
    }
    refList.remove(this);
    try
    {
      raf.close();
      boolean bool = file.renameTo(paramFile);
      if ((!bool) && (LOGGER.isLoggable(Level.INFO))) {
        LOGGER.log(Level.INFO, "File {0} was not moved to {1}", new Object[] { file.getAbsolutePath(), paramFile.getAbsolutePath() });
      }
    }
    catch (IOException localIOException)
    {
      throw new MIMEParsingException(localIOException);
    }
  }
  
  static void drainRefQueueBounded()
  {
    WeakDataFile localWeakDataFile;
    while ((localWeakDataFile = (WeakDataFile)refQueue.poll()) != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", file);
      }
      localWeakDataFile.close();
    }
  }
  
  static
  {
    CleanUpExecutorFactory localCleanUpExecutorFactory = CleanUpExecutorFactory.newInstance();
    if (localCleanUpExecutorFactory != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Initializing clean up executor for MIMEPULL: {0}", localCleanUpExecutorFactory.getClass().getName());
      }
      Executor localExecutor = localCleanUpExecutorFactory.getExecutor();
      localExecutor.execute(new Runnable()
      {
        public void run()
        {
          try
          {
            for (;;)
            {
              WeakDataFile localWeakDataFile = (WeakDataFile)WeakDataFile.refQueue.remove();
              if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
                WeakDataFile.LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", file);
              }
              localWeakDataFile.close();
            }
          }
          catch (InterruptedException localInterruptedException) {}
        }
      });
      hasCleanUpExecutor = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\WeakDataFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */