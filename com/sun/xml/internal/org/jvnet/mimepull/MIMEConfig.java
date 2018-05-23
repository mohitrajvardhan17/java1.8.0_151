package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEConfig
{
  private static final int DEFAULT_CHUNK_SIZE = 8192;
  private static final long DEFAULT_MEMORY_THRESHOLD = 1048576L;
  private static final String DEFAULT_FILE_PREFIX = "MIME";
  private static final Logger LOGGER = Logger.getLogger(MIMEConfig.class.getName());
  boolean parseEagerly;
  int chunkSize;
  long memoryThreshold;
  File tempDir;
  String prefix;
  String suffix;
  
  private MIMEConfig(boolean paramBoolean, int paramInt, long paramLong, String paramString1, String paramString2, String paramString3)
  {
    parseEagerly = paramBoolean;
    chunkSize = paramInt;
    memoryThreshold = paramLong;
    prefix = paramString2;
    suffix = paramString3;
    setDir(paramString1);
  }
  
  public MIMEConfig()
  {
    this(false, 8192, 1048576L, null, "MIME", null);
  }
  
  boolean isParseEagerly()
  {
    return parseEagerly;
  }
  
  public void setParseEagerly(boolean paramBoolean)
  {
    parseEagerly = paramBoolean;
  }
  
  int getChunkSize()
  {
    return chunkSize;
  }
  
  void setChunkSize(int paramInt)
  {
    chunkSize = paramInt;
  }
  
  long getMemoryThreshold()
  {
    return memoryThreshold;
  }
  
  public void setMemoryThreshold(long paramLong)
  {
    memoryThreshold = paramLong;
  }
  
  boolean isOnlyMemory()
  {
    return memoryThreshold == -1L;
  }
  
  File getTempDir()
  {
    return tempDir;
  }
  
  String getTempFilePrefix()
  {
    return prefix;
  }
  
  String getTempFileSuffix()
  {
    return suffix;
  }
  
  public final void setDir(String paramString)
  {
    if ((tempDir == null) && (paramString != null) && (!paramString.equals(""))) {
      tempDir = new File(paramString);
    }
  }
  
  public void validate()
  {
    if (!isOnlyMemory()) {
      try
      {
        File localFile = tempDir == null ? File.createTempFile(prefix, suffix) : File.createTempFile(prefix, suffix, tempDir);
        boolean bool = localFile.delete();
        if ((!bool) && (LOGGER.isLoggable(Level.INFO))) {
          LOGGER.log(Level.INFO, "File {0} was not deleted", localFile.getAbsolutePath());
        }
      }
      catch (Exception localException)
      {
        memoryThreshold = -1L;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */